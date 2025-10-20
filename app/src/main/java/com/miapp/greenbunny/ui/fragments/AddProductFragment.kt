package com.miapp.greenbunny.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.databinding.FragmentAddProductBinding
import com.miapp.greenbunny.model.CreateProductRequest
import com.miapp.greenbunny.model.ProductImage
import com.miapp.greenbunny.ui.adapter.ImagePreviewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var imagePreviewAdapter: ImagePreviewAdapter

    private val pickImages =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                selectedImageUris.clear()
                selectedImageUris.addAll(uris)
                imagePreviewAdapter.notifyDataSetChanged()
                binding.rvImagePreview.visibility = View.VISIBLE
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.btnSelectImage.setOnClickListener {
            pickImages.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            submit()
        }
    }

    private fun setupRecyclerView() {
        imagePreviewAdapter = ImagePreviewAdapter(selectedImageUris)
        binding.rvImagePreview.adapter = imagePreviewAdapter
    }

    private fun submit() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val description = binding.etDescription.text?.toString()?.trim()
        val price = binding.etPrice.text?.toString()?.trim()?.toIntOrNull()

        if (name.isBlank()) {
            Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progress.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Subir imágenes una por una
                val uploadedImages = mutableListOf<ProductImage>()
                if (selectedImageUris.isNotEmpty()) {
                    Log.d("AddProductFragment", "Subiendo ${selectedImageUris.size} imágenes...")
                    val uploadService = RetrofitClient.createUploadService(requireContext())

                    val uploadTasks = selectedImageUris.map { uri ->
                        async(Dispatchers.IO) { uploadImage(uri, uploadService) }
                    }

                    val results = uploadTasks.awaitAll()
                    results.filterNotNull().forEach { img ->
                        // Agregar URL completa
                        uploadedImages.add(img.copy(url = "https://x8ki-letl-twmt.n7.xano.io${img.path}"))
                    }

                    Log.d("AddProductFragment", "Subida completada: ${uploadedImages.size} imágenes.")
                }

                // Crear producto
                val service = RetrofitClient.createProductService(requireContext())
                val productRequest = CreateProductRequest(
                    name = name,
                    description = description,
                    price = price,
                    images = if (uploadedImages.isNotEmpty()) uploadedImages else null
                )

                Log.d("AddProductFragment", "Creando producto: $productRequest")
                withContext(Dispatchers.IO) { service.createProduct(productRequest) }

                Toast.makeText(requireContext(), "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                clearForm()
            } catch (e: Exception) {
                Log.e("AddProductFragment", "Error al crear producto", e)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progress.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
            }
        }
    }

    private suspend fun uploadImage(uri: Uri, service: com.miapp.greenbunny.api.UploadService): ProductImage? =
        withContext(Dispatchers.IO) {
            try {
                val contentResolver = requireContext().contentResolver
                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw IOException("No se pudo abrir el archivo: $uri")
                val requestBody = bytes.toRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("content", "image.jpg", requestBody)

                val imageList = service.uploadImage(part)
                val productImage = imageList.firstOrNull()
                if (productImage != null) Log.d("AddProductFragment", "Imagen subida: ${productImage.path}")
                productImage
            } catch (e: Exception) {
                Log.e("AddProductFragment", "Fallo al subir imagen $uri", e)
                null
            }
        }

    private fun clearForm() {
        binding.etName.text?.clear()
        binding.etDescription.text?.clear()
        binding.etPrice.text?.clear()
        selectedImageUris.clear()
        imagePreviewAdapter.notifyDataSetChanged()
        binding.rvImagePreview.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
