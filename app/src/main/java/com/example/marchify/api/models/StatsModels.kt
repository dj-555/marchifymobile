package com.example.marchify.api.models

data class MonthlyStats(
    val totalRevenue: Double,
    val totalOrders: Int,
    val month: Int,
    val year: Int
)

data class StatusStats(
    val pending: Int = 0,
    val processing: Int = 0,
    val ready: Int = 0,
    val shipped: Int = 0,
    val delivered: Int = 0,
    val cancelled: Int = 0,
    val returned: Int = 0
)
