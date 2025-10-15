package com.miapp.greenbunny.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.databinding.FragmentAddProductBinding
import com.miapp.greenbunny.model.CreateProductRequest
import com.miapp.greenbunny.model.ProductImage
import com.miapp.greenbunny.ui.adapter.ImagePreviewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val selectedImages = mutableListOf<Uri>()
    private lateinit var previewAdapter: ImagePreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btnSelectImage.setOnClickListener { selectImages() }

        binding.btnSubmit.setOnClickListener {
            createProduct()
        }
    }

    private fun setupRecyclerView() {
        previewAdapter = ImagePreviewAdapter(selectedImages)
        binding.rvImagePreview.apply {
            adapter = previewAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            visibility = View.GONE
        }
    }

    private fun selectImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Selecciona imágenes"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            data?.let {
                selectedImages.clear()
                // Varias imágenes seleccionadas
                if (it.clipData != null) {
                    for (i in 0 until it.clipData!!.itemCount) {
                        selectedImages.add(it.clipData!!.getItemAt(i).uri)
                    }
                } else if (it.data != null) {
                    selectedImages.add(it.data!!)
                }

                binding.rvImagePreview.visibility = if (selectedImages.isNotEmpty()) View.VISIBLE else View.GONE
                previewAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun createProduct() {
        val name = binding.etName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val price = binding.etPrice.text.toString().trim().toIntOrNull() ?: 0

        if (name.isEmpty()) return

        binding.progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // 1. Subir todas las imágenes
                val uploadService = RetrofitClient.createUploadService(requireContext())
                val uploadedImages = mutableListOf<ProductImage>()
                for (uri in selectedImages) {
                    val file = File(uri.path ?: continue)
                    val requestBody = file.asRequestBody("image/*".toMediaType())
                    val part = MultipartBody.Part.createFormData("content", file.name, requestBody)
                    val uploaded = withContext(Dispatchers.IO) { uploadService.uploadImage(part) }
                    uploadedImages.addAll(uploaded)
                }

                // 2. Crear el producto
                val productRequest = CreateProductRequest(
                    name = name,
                    description = description,
                    price = price,
                    images = uploadedImages
                )
                val productService = RetrofitClient.createProductService(requireContext())
                val response = withContext(Dispatchers.IO) { productService.createProduct(productRequest) }

                binding.progress.visibility = View.GONE
                // Mostrar feedback / cerrar fragmento
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                e.printStackTrace()
                // Mostrar error
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
