package br.com.carbuapp.core.util

import org.json.JSONObject
import retrofit2.HttpException

fun parseHttpError(e: Exception): String {
    return if (e is HttpException) {
        try {
            val errorBody = e.response()?.errorBody()?.string()
            JSONObject(errorBody ?: "").getString("message")
        } catch (ex: Exception) {
            "Erro ${e.code()}"
        }
    } else if (e.message?.contains("Unable to resolve host") == true ||
        e.message?.contains("timeout") == true ||
        e.message?.contains("failed to connect") == true) {
        "Sem conexão com a internet."
    } else {
        e.message ?: "Erro desconhecido."
    }
}
