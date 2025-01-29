package pt.ipt.dam.pocketknowledge.model

import com.google.gson.annotations.SerializedName

// Classe que representa os dados de um flashcard
data class flashcards(
    @SerializedName("id") val id: Int,
    @SerializedName("question") val question: String,
    @SerializedName("answer") val answer: String,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("user_id") val user_id: Int
)
