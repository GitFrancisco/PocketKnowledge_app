package pt.ipt.dam.pocketknowledge.retrofit.service

import pt.ipt.dam.pocketknowledge.model.authResponse
import pt.ipt.dam.pocketknowledge.model.flashcards
import pt.ipt.dam.pocketknowledge.model.userData
import pt.ipt.dam.pocketknowledge.model.userLogin
import pt.ipt.dam.pocketknowledge.model.userRegister
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface api_service {
    // ### USERS ###
    // Autenticar o utilizador
    @POST("api/users/login")
    fun login(@Body userLogin: userLogin): Call<authResponse>

    // Registar um novo utilizador
    @POST("api/users/register")
    fun register(@Body userRegister: userRegister): Call<Void>

    // Obter os dados do utilizador autenticado
    @GET("api/users/me")
    fun getUserData(@Header("Authorization") token: String): Call<userData>

    // ### FLASHCARDS ###
    @GET("api/flashcards/list")
    fun getFlashcards(): Call<List<flashcards>>

    // ### TEMAS ###
}