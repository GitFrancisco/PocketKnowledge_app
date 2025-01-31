package pt.ipt.dam.pocketknowledge.model

data class addFlashcard(
    val question: String,
    val answer: String,
    val user_id: Int
)
