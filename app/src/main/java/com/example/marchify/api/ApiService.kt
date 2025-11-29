package com.example.marchify.api

import com.example.marchify.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface
 * Defines all MarchiFy backend endpoints
 */
interface ApiService {

    // ==================== USER & AUTH ROUTES ====================

    @POST("users")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users/logout")
    suspend fun logout(): Response<Unit>

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("users/{userId}/vendeur")
    suspend fun getVendeurByUserId(@Path("userId") userId: String): Response<Vendeur>

    @GET("users/{userId}/livreur")
    suspend fun getLivreurByUserId(@Path("userId") userId: String): Response<Livreur>


    // ==================== BOUTIQUE ROUTES ====================

    @POST("boutiques")
    suspend fun createBoutique(@Body request: BoutiqueRequest): Response<Boutique>

    @GET("boutiques")
    suspend fun getBoutiques(): Response<List<Boutique>>

    @GET("boutiques/{id}")
    suspend fun getBoutiqueById(@Path("id") id: String): Response<Boutique>

    @PUT("boutiques/{id}")
    suspend fun updateBoutique(
        @Path("id") id: String,
        @Body request: BoutiqueRequest
    ): Response<Boutique>

    @GET("boutiques/vendeur/{vendeurId}")
    suspend fun getBoutiquesByVendeurId(@Path("vendeurId") vendeurId: String): Response<List<Boutique>>


    // ==================== PRODUIT ROUTES ====================

    @Multipart
    @POST("produits")
    suspend fun createProduit(
        @Part("nom") nom: RequestBody,
        @Part("description") description: RequestBody,
        @Part("prix") prix: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("categorie") categorie: RequestBody,
        @Part("shopId") shopId: RequestBody,
        @Part imageFile: List<MultipartBody.Part>?
    ): Response<Produit>

    @GET("produits")
    suspend fun getProduits(): Response<List<Produit>>

    @GET("produits/{id}")
    suspend fun getProduitById(@Path("id") id: String): Response<Produit>

    @GET("produits/shop/{shopId}")
    suspend fun getProduitsByShopId(@Path("shopId") shopId: String): Response<List<Produit>>

    @GET("produits/pinned/top-rated")
    suspend fun getPinnedTopRatedProduits(): Response<List<Produit>>

    @Multipart
    @PUT("produits/{id}")
    suspend fun updateProduit(
        @Path("id") id: String,
        @Part("nom") nom: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("prix") prix: RequestBody?,
        @Part("stock") stock: RequestBody?,
        @Part("categorie") categorie: RequestBody?,
        @Part imageFile: List<MultipartBody.Part>?
    ): Response<Produit>

    @DELETE("produits/{id}")
    suspend fun deleteProduit(@Path("id") id: String): Response<Unit>

    @POST("produits/batch")
    suspend fun getProduitsByIds(@Body request: BatchProductRequest): Response<List<Produit>>


    // ==================== CART ROUTES ====================

    @POST("cart/add")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<AddToCartResponse>

    @GET("cart/{clientId}")
    suspend fun getCart(@Path("clientId") clientId: String): Response<Cart>

    @PUT("cart/update")
    suspend fun updateCartQuantities(@Body request: UpdateCartRequest): Response<Cart>

    @GET("cart/recalc/{clientId}")
    suspend fun recalcCartTotal(@Path("clientId") clientId: String): Response<Cart>

    @POST("cart/remove")
    suspend fun removeFromCart(@Body request: RemoveFromCartRequest): Response<Cart>

    @DELETE("cart/clear/{clientId}")
    suspend fun clearCart(@Path("clientId") clientId: String): Response<Unit>

    @POST("cart/confirm")
    suspend fun confirmOrder(@Body request: ConfirmOrderRequest): Response<ConfirmOrderResponse>


    // ==================== COMMANDE ROUTES ====================

    @GET("commandes/vendeur/{vendeurId}")
    suspend fun getCommandesVendeur(@Path("vendeurId") vendeurId: String): Response<List<Commande>>

    @GET("commandes/boutique/{boutiqueId}")
    suspend fun getCommandesBoutique(@Path("boutiqueId") boutiqueId: String): Response<List<Commande>>

    @GET("commandes/commandesList/{clientId}")
    suspend fun getCommandesByAcheteur(@Path("clientId") clientId: String): Response<List<Commande>>

    @GET("commandes/{commandeId}")
    suspend fun getDetailCommande(@Path("commandeId") commandeId: String): Response<Commande>

    @PATCH("commandes/accepter/{commandeId}")
    suspend fun accepterCommande(@Path("commandeId") commandeId: String): Response<Commande>

    @PATCH("commandes/preparer/{commandeId}")
    suspend fun preparerCommande(@Path("commandeId") commandeId: String): Response<Commande>

    @PATCH("commandes/expedier/{commandeId}")
    suspend fun expedierCommande(@Path("commandeId") commandeId: String): Response<Commande>

    @PATCH("commandes/livrer/{commandeId}")
    suspend fun livrerCommande(@Path("commandeId") commandeId: String): Response<Commande>

    @PATCH("commandes/annuler/{commandeId}")
    suspend fun annulerCommande(@Path("commandeId") commandeId: String): Response<Commande>

    @PATCH("commandes/status/{commandeId}")
    suspend fun updateCommandeStatus(
        @Path("commandeId") commandeId: String,
        @Body request: UpdateStatusRequest
    ): Response<Commande>

    // Statistics routes
    @GET("commandes/stats/vendeur/{vendeurId}/months")
    suspend fun getStatsByMonth(@Path("vendeurId") vendeurId: String): Response<MonthlyStats>

    @GET("commandes/stats/vendeur/{vendeurId}/status")
    suspend fun getStatsByStatusForMonth(@Path("vendeurId") vendeurId: String): Response<StatusStats>

    @GET("commandes/stats/vendeur/{vendeurId}/month/{month}/year/{year}")
    suspend fun getStatsByMonthAndYear(
        @Path("vendeurId") vendeurId: String,
        @Path("month") month: Int,
        @Path("year") year: Int
    ): Response<MonthlyStats>


    // ==================== BON DE LIVRAISON ROUTES (Updated) ====================

    @GET("bons/getAllBons")
    suspend fun getAllBonsDeLivraison(): Response<List<BonDeLivraison>>

    @GET("bons/unassigned")
    suspend fun getUnassignedBons(): Response<List<BonDeLivraison>>

    @GET("bons/livreur/{livreurId}")
    suspend fun getBonsDeLivraisonByLivreur(@Path("livreurId") livreurId: String): Response<List<BonDeLivraison>>

    @GET("bons/commande/{commandeId}")
    suspend fun getBonByCommandeId(@Path("commandeId") commandeId: String): Response<BonDeLivraison>

    @GET("bons/{bonId}")
    suspend fun getBonById(@Path("bonId") bonId: String): Response<BonDeLivraison>

    @PATCH("bons/{bonId}/assign-livreur")
    suspend fun assignLivreurToBon(
        @Path("bonId") bonId: String,
        @Body request: AssignLivreurRequest
    ): Response<BonDeLivraison>

    /**
     * Mark order as picked up by livreur
     * Changes status: pending_pickup -> picked_up
     */
    @PATCH("bons/{bonId}/pickup")
    suspend fun pickupCommande(@Path("bonId") bonId: String): Response<BonDeLivraison>

    /**
     * Mark order as delivered
     * Changes status: in_transit -> delivered
     */
    @PATCH("bons/{bonId}/deliver")
    suspend fun deliverCommande(@Path("bonId") bonId: String): Response<BonDeLivraison>

    /**
     * Mark delivery as failed with reason
     * Changes status: * -> failed
     */
    @PATCH("bons/{bonId}/fail")
    suspend fun failDelivery(
        @Path("bonId") bonId: String,
        @Body request: FailDeliveryRequest
    ): Response<BonDeLivraison>

    /**
     * Update delivery status manually (admin/vendeur)
     * @param request contains new DeliveryStatus
     */
    @PATCH("bons/{bonId}/status")
    suspend fun updateBonDeliverStatus(
        @Path("bonId") bonId: String,
        @Body request: UpdateDeliveryStatusRequest
    ): Response<BonDeLivraison>


    // ==================== LIVREUR/MISSION ROUTES ====================

    /**
     * Get available missions (unassigned bons)
     */
    @GET("livreur/missions")
    suspend fun getMissionsDisponibles(): Response<List<Mission>>

    /**
     * Get mission details by ID
     */
    @GET("livreur/missions/{id}")
    suspend fun getMissionById(@Path("id") id: String): Response<Mission>

    /**
     * Accept a mission (assigns livreur to bon)
     * @param livreurId - ID of the livreur accepting
     * @param bonId - ID of the bon de livraison
     */
    @PATCH("livreur/missions/accepter/{livreurId}/{bonId}")
    suspend fun accepterMission(
        @Path("livreurId") livreurId: String,
        @Path("bonId") bonId: String
    ): Response<AcceptMissionResponse>

    /**
     * Refuse a mission
     * @param commandeId - ID of the commande to refuse
     */
    @PATCH("livreur/missions/refuser/{commandeId}")
    suspend fun refuserMission(@Path("commandeId") commandeId: String): Response<Unit>


    // ==================== NOTIFICATION ROUTES ====================

    @GET("notifications/{userId}")
    suspend fun getNotifications(
        @Path("userId") userId: String,
        @Query("limit") limit: Int? = 20,
        @Query("unreadOnly") unreadOnly: Boolean? = false
    ): Response<List<Notification>>

    @GET("notifications/{userId}/unread/count")
    suspend fun getUnreadNotificationCount(@Path("userId") userId: String): Response<UnreadCountResponse>

    @PATCH("notifications/{userId}/{notificationId}/read")
    suspend fun markNotificationAsRead(
        @Path("userId") userId: String,
        @Path("notificationId") notificationId: String
    ): Response<Notification>

    @PATCH("notifications/{userId}/read-all")
    suspend fun markAllNotificationsAsRead(@Path("userId") userId: String): Response<Unit>

    @DELETE("notifications/{userId}/{notificationId}")
    suspend fun deleteNotificationById(
        @Path("userId") userId: String,
        @Path("notificationId") notificationId: String
    ): Response<Unit>


    // ==================== REVIEW ROUTES ====================

    @POST("reviews")
    suspend fun addReview(@Body request: ReviewRequest): Response<Review>

    @GET("reviews/product/{produitId}")
    suspend fun getProductReviews(@Path("produitId") produitId: String): Response<List<Review>>

    @GET("reviews/boutique/{boutiqueId}")
    suspend fun getBoutiqueReviews(@Path("boutiqueId") boutiqueId: String): Response<List<Review>>

    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: String): Response<Unit>
}
