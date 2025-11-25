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
import coil.load
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.databinding.FragmentAddProductBinding
import com.miapp.greenbunny.model.CreateProductRequest
import com.miapp.greenbunny.model.Product
import com.miapp.greenbunny.model.ProductImage
import com.miapp.greenbunny.model.UpdateProductRequest
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

    private val newImageUris = mutableListOf<Uri>()
    private lateinit var newImagesAdapter: ImagePreviewAdapter

    // Modo edición
    private var productToEdit: Product? = null
    private val existingImages = mutableListOf<ProductImage>()
    private val existingImageUris = mutableListOf<Uri>()
    private lateinit var existingImagesAdapter: ImagePreviewAdapter

    private val pickImages =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                newImageUris.clear()
                newImageUris.addAll(uris)
                newImagesAdapter.notifyDataSetChanged()
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

        // Leer producto para editar (si viene en argumentos)
        productToEdit = arguments?.getSerializable("PRODUCT_TO_EDIT") as? Product
        productToEdit?.let { setupEditMode(it) }
    }

    private fun setupRecyclerView() {
        newImagesAdapter = ImagePreviewAdapter(newImageUris) { idx ->
            newImageUris.removeAt(idx)
            newImagesAdapter.notifyDataSetChanged()
            if (newImageUris.isEmpty()) binding.rvImagePreview.visibility = View.GONE
        }
        binding.rvImagePreview.adapter = newImagesAdapter

        existingImagesAdapter = ImagePreviewAdapter(existingImageUris) { idx ->
            existingImageUris.removeAt(idx)
            existingImages.removeAt(idx)
            existingImagesAdapter.notifyDataSetChanged()
            val hasExisting = existingImageUris.isNotEmpty()
            binding.rvExistingPreview.visibility = if (hasExisting) View.VISIBLE else View.GONE
            binding.tvExistingLabel.visibility = if (hasExisting) View.VISIBLE else View.GONE
        }
        binding.rvExistingPreview.adapter = existingImagesAdapter
    }

    private fun submit() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val description = binding.etDescription.text?.toString()?.trim()
        val price = binding.etPrice.text?.toString()?.trim()?.toIntOrNull()
        val stock = binding.etStock.text?.toString()?.trim()?.toIntOrNull() ?: 0  // <-- stock leído

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
                if (newImageUris.isNotEmpty()) {
                    Log.d("AddProductFragment", "Subiendo ${newImageUris.size} imágenes...")
                    val uploadService = RetrofitClient.createUploadService(requireContext())

                    val uploadTasks = newImageUris.map { uri ->
                        async(Dispatchers.IO) { uploadImage(uri, uploadService) }
                    }

                    val results = uploadTasks.awaitAll()
                    results.filterNotNull().forEach { img ->
                        uploadedImages.add(img.copy(url = "https://x8ki-letl-twmt.n7.xano.io${img.path}"))
                    }

                    Log.d("AddProductFragment", "Subida completada: ${uploadedImages.size} imágenes.")
                }
                val service = RetrofitClient.createProductService(requireContext())

                if (productToEdit == null) {
                    // CREAR producto
                    val productRequest = CreateProductRequest(
                        name = name,
                        description = description,
                        price = price,
                        stock = stock,
                        images = if (uploadedImages.isNotEmpty()) uploadedImages else null
                    )
                    Log.d("AddProductFragment", "Creando producto: $productRequest")
                    withContext(Dispatchers.IO) { service.createProduct(productRequest) }
                    Toast.makeText(requireContext(), "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    // EDITAR producto
                    val imagesForUpdate = mutableListOf<ProductImage>()
                    imagesForUpdate.addAll(existingImages) // preservamos las que quedaron
                    imagesForUpdate.addAll(uploadedImages) // agregamos nuevas

                    val updateRequest = UpdateProductRequest(
                        name = name,
                        description = description,
                        price = price,
                        stock = stock,
                        images = imagesForUpdate.takeIf { it.isNotEmpty() }
                    )
                    Log.d("AddProductFragment", "Actualizando producto ${productToEdit?.id}: $updateRequest")
                    withContext(Dispatchers.IO) { service.updateProduct(productToEdit!!.id, updateRequest) }
                    Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AddProductFragment", "Error al crear producto", e)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progress.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
            }
        }
    }

    private fun setupEditMode(product: Product) {
        // Cambiar texto del botón
        binding.btnSubmit.text = "Actualizar Producto"

        // Prefill campos
        binding.etName.setText(product.name)
        binding.etDescription.setText(product.description ?: "")
        binding.etPrice.setText(product.price?.toString() ?: "")
        binding.etStock.setText(product.stock.toString())

        // Cargar imágenes existentes
        val imgs = product.images ?: emptyList()
        existingImages.clear()
        existingImages.addAll(imgs)
        existingImageUris.clear()
        imgs.forEach { img ->
            val uri = Uri.parse(img.url ?: ("https://x8ki-letl-twmt.n7.xano.io" + img.path))
            existingImageUris.add(uri)
        }
        if (existingImageUris.isNotEmpty()) {
            binding.tvExistingLabel.visibility = View.VISIBLE
            binding.rvExistingPreview.visibility = View.VISIBLE
            existingImagesAdapter.notifyDataSetChanged()
        }
    }

    private suspend fun uploadImage(uri: Uri, service: com.miapp.greenbunny.api.UploadService): ProductImage? =
        withContext(Dispatchers.IO) {
            try {
                val contentResolver = requireContext().contentResolver
                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw IOException("No se pudo abrir el archivo: $uri")
                val requestBody = bytes.toRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

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
        binding.etStock.text?.clear()      // <-- limpiar stock
        newImageUris.clear()
        newImagesAdapter.notifyDataSetChanged()
        binding.rvImagePreview.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
