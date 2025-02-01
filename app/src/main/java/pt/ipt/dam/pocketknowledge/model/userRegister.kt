package pt.ipt.dam.pocketknowledge.model

// Classe que representa os dados de um utilizador (registo)
data class userRegister(
    val username: String,
    val password: String,
    val email: String
)
