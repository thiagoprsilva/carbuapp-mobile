package br.com.carbuapp.auth.domain.model

data class User(
    val id: Int,
    val nome: String,
    val email: String,
    val role: String,
    val oficinaId: Int?
) {
    val isAdmin: Boolean get() = role == "ADMIN" || role == "SUPERADMIN"
    val isSuperAdmin: Boolean get() = role == "SUPERADMIN"
}
