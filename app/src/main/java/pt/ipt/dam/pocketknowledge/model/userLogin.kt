package pt.ipt.dam.pocketknowledge.model

// Classe que representa os dados de um utilizador ainda não autenticado
data class userLogin(
    val email: String,
    val password: String
)
