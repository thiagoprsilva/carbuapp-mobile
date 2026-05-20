package br.com.carbuapp.core.util

import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

fun parseHttpError(e: Exception): String {
    // 1. Erros HTTP com body da API
    if (e is HttpException) {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            JSONObject(errorBody ?: "").getString("message")
        } catch (ex: Exception) {
            "Erro ${e.code()}"
        }
    }

    // 2. Percorre a cadeia de causas para identificar o tipo real
    var cause: Throwable? = e
    while (cause != null) {
        when (cause) {
            is UnknownHostException   -> return "Sem conexão com o servidor. Verifique sua internet."
            is SocketTimeoutException -> return "Tempo limite de conexão esgotado. Tente novamente."
            is SSLException           -> return "Erro de certificado SSL. Verifique a data/hora do dispositivo."
            else -> { /* continua percorrendo */ }
        }
        cause = cause.cause
    }

    // 3. Fallback por texto (compatibilidade)
    val msg = e.message ?: ""
    return when {
        msg.contains("Unable to resolve host", ignoreCase = true) -> "Sem conexão com o servidor."
        msg.contains("timeout", ignoreCase = true)                -> "Tempo limite de conexão esgotado."
        msg.contains("failed to connect", ignoreCase = true)      -> "Não foi possível conectar ao servidor."
        msg.contains("SSL", ignoreCase = true)                    -> "Erro de certificado SSL."
        msg.isNotBlank()                                          -> msg
        else                                                      -> "Erro desconhecido."
    }
}
