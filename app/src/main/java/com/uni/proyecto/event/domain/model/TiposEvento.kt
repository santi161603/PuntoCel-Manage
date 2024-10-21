package com.uni.proyecto.event.domain.model

data class TiposEvento(
    var nombre: String? = null,
    val descripcion: String? = null,
    val serviciosIncluidos: List<String?> = emptyList(),
    val costo_minimo: String? = null,
    val image: String? = null,
    val miniDescripcion: String? = null
){
    constructor() : this(null,null, emptyList(),null,null,null)
}
