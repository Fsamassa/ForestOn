package com.example.foreston.recyclerParcelas

data class Parcela(
    val nombre_parcela:String ?= null,
    val cant_arboles:String ?= null,
    val diametro_arboles:String ?= null,
    val altura_prom:String ?= null,
    val tipo:String ?= null,
    val edad:String ?= null,
    val direccion:String ?= null,
    val tipo_industria:String ?= null
)
