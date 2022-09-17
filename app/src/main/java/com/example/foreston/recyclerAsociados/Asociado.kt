package com.example.foreston.recyclerAsociados

data class Asociado(
    val uid: String ?= null,
    val nombre:String ?= null,
    val apellido:String ?= null,
    val direccion:String ?= null,
    val imagen_foto_url:String ?= null,
    val campos:Int ?= null,
    val email:String ?= null,
    val telefono:String ?= null)
