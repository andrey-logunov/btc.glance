package com.logunov.btcglance

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET

@Serializable
data class BitcoinResponse(
    val bitcoin: Map<String, Double>
)

interface CryptoApi {
    @GET("/simple/price?ids=bitcoin&vs_currencies=usd")
    suspend fun getBitcoinPrice(): BitcoinResponse
}

object CryptoApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: CryptoApi = retrofit.create(CryptoApi::class.java)
}