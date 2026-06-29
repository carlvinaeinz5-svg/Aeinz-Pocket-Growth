package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object GeminiHelper {
    suspend fun getFinancialTip(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        // Check if API key is empty, or is default placeholder from env
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("PLACEHOLDER")) {
            return@withContext getOfflineTip(prompt)
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.7f),
            systemInstruction = Content(parts = listOf(Part(text = "You are Aeinz AI, a friendly financial literacy mentor helping low and moderate income earners in Uganda. Keep answers under 120 words. Focus on practical saving hacks, mobile money discipline (MTN/Airtel), automated goals, and minimal fees.")))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: getOfflineTip(prompt)
        } catch (e: Exception) {
            getOfflineTip(prompt)
        }
    }

    private fun getOfflineTip(prompt: String): String {
        // Fallback robust tips for Ugandan moderate earners
        val tips = listOf(
            "Track small expenses. Saving 1,000 UGX daily adds up to 365,000 UGX in a year! Use the Aeinz Automated Goal tracker to keep yourself disciplined.",
            "Before spending, sleep on it for 24 hours. This reduces impulsive mobile money transactions on Airtel Money or MTN, helping you save on withdrawal fees.",
            "Always utilize the Aeinz Transact Card for online transactions to avoid high cash handling and withdrawal fees. Plan your withdrawals in bulk to beat the 5% charge.",
            "Ugandan market tips: buy groceries in bulk from local hubs rather than piece-by-piece to minimize transport and minor inflated pricing.",
            "Split your income immediately. Put 20% directly into your Aeinz Savings Goals before spending on rent, airtime, or leisure.",
            "Emergency funds are your shield. Try to save at least 3 months of basic living costs on Aeinz Pocket Growth to avoid borrowing during emergencies.",
            "Financial literacy hack: avoid taking short-term loans with daily/weekly interest. Save first with Aeinz Pocket Growth, then spend with your own hard-earned money."
        )
        return tips.random()
    }
}
