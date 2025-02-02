package pt.ipt.dam.pocketknowledge.retrofit

import pt.ipt.dam.pocketknowledge.retrofit.service.api_service
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {
    private val retrofit = Retrofit.Builder()
        // Localhost do computador
        .baseUrl("http://143.47.51.101:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun apiService() = retrofit.create(api_service::class.java)
}