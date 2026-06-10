package br.com.carbuapp.usuarios.domain

data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val role: String,       // "ADMIN" | "MECANICO" | "SUPERADMIN"
    val ativo: Boolean,
    val oficinaId: Int?
) {
    val roleLabel: String get() = when (role) {
        "SUPERADMIN" -> "Super Admin"
        "ADMIN"      -> "Administrador"
        else         -> "Mecânico"
    }
}

data class UsuarioCreateInput(
    val nome: String,
    val email: String,
    val senha: String,
    val role: String
)

data class UsuarioUpdateInput(
    val nome: String,
    val email: String,
    val role: String,
    val ativo: Boolean
)
