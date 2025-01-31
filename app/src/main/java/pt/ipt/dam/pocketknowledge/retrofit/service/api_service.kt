package pt.ipt.dam.pocketknowledge.retrofit.service

import pt.ipt.dam.pocketknowledge.model.addFlashcard
import pt.ipt.dam.pocketknowledge.model.authResponse
import pt.ipt.dam.pocketknowledge.model.createFlashcardResponse
import pt.ipt.dam.pocketknowledge.model.flashcards
import pt.ipt.dam.pocketknowledge.model.mapFlashcardToTheme
import pt.ipt.dam.pocketknowledge.model.themes
import pt.ipt.dam.pocketknowledge.model.userData
import pt.ipt.dam.pocketknowledge.model.userLogin
import pt.ipt.dam.pocketknowledge.model.userRegister
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

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
    // Obter todos os flashcards
    @GET("api/flashcards/list")
    fun getFlashcards(): Call<List<flashcards>>

    // Obter um flashcard por ID
    @GET("api/flashcards/find/{id}")
    fun getFlashcardById(@Path("id") id: Int): Call<flashcards>

    // Criar um novo flashcard
    @POST("api/flashcards/create")
    fun createFlashcard(@Body addFlashcard: addFlashcard): Call<createFlashcardResponse>

    // Apagar um flashcard
    @DELETE("api/flashcards/delete/{id}")
    fun deleteFlashcard(@Path("id") id: Int): Call<Void>

    // ### TEMAS ###
    // Obter todos os temas
    @GET("api/themes/list")
    fun getThemes(): Call<List<themes>>

    // Obter os flashcards de um tema
    @GET("api/themes/{id}/flashcards")
    fun getFlashcardsByTheme(@Path("id") id: Int): Call<List<flashcards>>

    // Fazer o mapeamento de um flashcard para um tema
    @POST("api/themes/flashcard-theme/create")
    fun mapFlashcardToTheme(@Body mapFlashcardToTheme: mapFlashcardToTheme ): Call<Void>
}