package pt.ipt.dam.pocketknowledge.model

// Classe que representa os dados de um utilizador autenticado
data class userData(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val created_at: String
)
