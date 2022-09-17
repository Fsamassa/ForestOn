package com.example.foreston.utils

object UtilsAuth {
    enum class ProveedorLogin {
        BASICO,
        GOOGLE,
        FACEBOOK
    }
    private val dominiosValidos: List<String> = listOf(
        "@yahoo.com",
        "@yahoo.com.ar",
        "@gmail.com",
        "@hotmail.com",
        "@live.com",
        "@live.com.ar",
        "@frba.utn.edu.ar",
        "@tfbnw.net",
        "@forestonapp.app.com")

    fun tieneDominioValido(email: String): Boolean{
        if (email.indexOfAny(dominiosValidos) != -1){
            return true
        }
        return false
    }
}