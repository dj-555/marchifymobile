package com.example.marchify.data.repository

import android.net.Uri
import com.example.marchify.api.RetrofitClient
import com.example.marchify.api.models.BatchProductRequest
import com.example.marchify.api.models.Produit
import com.example.marchify.api.models.UniteMesure
import com.example.marchify.utils.PrefsManager
import com.example.marchify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Repository for product operations
 */
class ProductRepository(private val prefsManager: PrefsManager) {

    private val apiService = RetrofitClient.getApiService(prefsManager)


    /**
     * Get all products
     */
    fun getAllProducts(): Flow<Resource<List<Produit>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getProduits()

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load products"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get product by ID
     */
    fun getProductById(id: String): Flow<Resource<Produit>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getProduitById(id)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Product not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Get products by shop ID
     */
    fun getProductsByShopId(shopId: String): Flow<Resource<List<Produit>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getProduitsByShopId(shopId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load products"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }


    /**
     * Get pinned top-rated products
     */
    fun getPinnedTopRatedProducts(): Flow<Resource<List<Produit>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getPinnedTopRatedProduits()

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load featured products"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
    /**
     * Get featured products (alias for getPinnedTopRatedProducts)
     */
    fun getFeaturedProducts(): Flow<Resource<List<Produit>>> {
        return getPinnedTopRatedProducts()
    }

    /**
     * Get products by boutique ID (alias for getProductsByShopId)
     */
    fun getProductsByBoutique(boutiqueId: String): Flow<Resource<List<Produit>>> {
        return getProductsByShopId(boutiqueId)
    }


    /**
     * Get products by IDs (batch)
     */
    fun getProductsByIds(ids: List<String>): Flow<Resource<List<Produit>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getProduitsByIds(BatchProductRequest(ids))

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to load products"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Create product with image upload
     */
    fun createProduct(
        nom: String,
        description: String,
        prix: Double,
        stock: Int,
        categorie: String,
        unite: UniteMesure,
        shopId: String,
        imageUri: Uri?
    ): Flow<Resource<Produit>> = flow {
        emit(Resource.Loading())

        try {
            // Prepare request body parts
            val nomPart = nom.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val prixPart = prix.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoriePart = categorie.toRequestBody("text/plain".toMediaTypeOrNull())
            val shopIdPart = shopId.toRequestBody("text/plain".toMediaTypeOrNull())

            // Prepare image parts (if image provided)
            val imageParts = if (imageUri != null) {
                // TODO: Convert Uri to File
                // For now, pass null
                null
            } else {
                null
            }

            val response = apiService.createProduit(
                nomPart, descriptionPart, prixPart, stockPart, categoriePart, shopIdPart, imageParts
            )

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to create product"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Update product with optional image upload
     */

    fun updateProduct(
        productId: String,
        nom: String,
        description: String,
        prix: Double,
        stock: Int,
        categorie: String,
        unite: UniteMesure,
        imageUri: Uri?
    ): Flow<Resource<Produit>> = flow {
        emit(Resource.Loading())

        try {
            // Prepare request body parts
            val nomPart = nom.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val prixPart = prix.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoriePart = categorie.toRequestBody("text/plain".toMediaTypeOrNull())

            // Prepare image parts (if new image provided)
            val imageParts = if (imageUri != null) {
                // TODO: Convert Uri to File
                // For now, pass null
                null
            } else {
                null
            }

            val response = apiService.updateProduit(
                id = productId,
                nom = nomPart,
                description = descriptionPart,
                prix = prixPart,
                stock = stockPart,
                categorie = categoriePart,
                imageFile = imageParts
            )

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to update product"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    /**
     * Delete product
     */
    fun deleteProduct(id: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.deleteProduit(id)

            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Failed to delete product"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}
