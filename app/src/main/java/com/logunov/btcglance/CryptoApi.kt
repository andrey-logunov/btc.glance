package com.logunov.btcglance

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@Serializable
data class BitcoinResponse(
    val bitcoin: Map<String, Double>
)

interface CryptoApi {
    @GET("simple/price")
    suspend fun getBitcoinPrice(
        @Query("ids") ids: String = "bitcoin",
        @Query("vs_currencies") vsCurrencies: String = "usd",
        @Query("x_cg_demo_api_key") apiKey: String
    ): BitcoinResponse
}

object CryptoApiClient {
    private const val DEMO_API_KEY = "CG-DEMO_API_KEY"
    private val jsonWithUnknownKeys = Json { ignoreUnknownKeys = true }

    // Keep your existing interceptor with User-Agent (it's still useful)
    private val userAgentInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
            .header("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(userAgentInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .client(okHttpClient)
        .addConverterFactory(jsonWithUnknownKeys.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: CryptoApi = retrofit.create(CryptoApi::class.java)

    // Optional helper function
    suspend fun fetchBitcoinPrice(): Double? {
        return try {
            val response = api.getBitcoinPrice(apiKey = DEMO_API_KEY)
            response.bitcoin["usd"]
        } catch (e: Exception) {
            null
        }
    }
}