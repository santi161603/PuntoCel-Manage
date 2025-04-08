package com.uni.proyecto.event.presenter.adapters.onClick

import com.uni.proyecto.event.domain.model.TiposEvento

interface OnClickProducts {
    fun onItemClick(typeEvent: TiposEvento)
}