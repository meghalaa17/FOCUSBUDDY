package com.example.a211393_nelson_lab01.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// ================================================================
// WHAT IS RETROFIT?
// Retrofit turns an HTTP REST API into a Kotlin interface.
// Instead of writing low-level networking code (open socket,
// send headers, parse response), you just define a function
// and Retrofit handles everything else.
//
// We're using ZenQuotes API (free, no API key needed):
// https://zenquotes.io/api/random
// Returns JSON like: [{"q": "Quote text", "a": "Author"}]
// ================================================================

// ------- 1. DATA MODEL -------
// This matches the JSON structure the API returns.
// Gson (the JSON parser) maps JSON keys to these field names.
// "q" in JSON → q in Kotlin, "a" in JSON → a in Kotlin
data class QuoteResponse(
    val q: String,  // the quote text
    val a: String   // the author name
)


// ------- 2. API INTERFACE -------
// @GET("/api/random") means: make an HTTP GET request to that path.
// Retrofit reads this interface and creates a real implementation
// at runtime — you never write the network code yourself.
interface QuoteApiService {

    @GET("api/random")
    // suspend = runs on background thread (same reason as Room DAOs)
    suspend fun getRandomQuote(): List<QuoteResponse>
    // Returns a List because the API wraps the quote in an array: [{ ... }]
    // We'll use [0] to get the first (and only) item.
}


// ------- 3. SINGLETON RETROFIT INSTANCE -------
// Same singleton pattern as the database — one Retrofit client
// shared across the whole app.
object QuoteApi {
    private const val BASE_URL = "https://zenquotes.io/"

    val service: QuoteApiService by lazy {
        // lazy = only created when first accessed, not at app start
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // GsonConverterFactory automatically converts the JSON
            // response body into your QuoteResponse data class
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApiService::class.java)
        // .create() takes your interface and generates the real
        // implementation that makes the actual HTTP call
    }
}