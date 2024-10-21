package com.uni.proyecto.event.presenter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uni.proyecto.event.R
import com.uni.proyecto.event.domain.model.TiposEvento
import com.uni.proyecto.event.presenter.view.HomeFragment

class AdapterHome(
    private val homeFragment: HomeFragment,
    private var typeEventosList: MutableList<TiposEvento>,
    private val requireContext: Context
): RecyclerView.Adapter<AdapterHome.ViewHolder>() {

    fun updateData(eventosList: MutableList<TiposEvento>) {
        this.typeEventosList = eventosList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_evento, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return typeEventosList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evento = typeEventosList[position]

        holder.miniDescription.text = evento.miniDescripcion

        Picasso.get()
            .load(evento.image)
            .placeholder(R.drawable.cargando) // Imagen de placeholder mientras carga
            .error(R.drawable.imagen_error) // Imagen de error si falla la carga
            .into(holder.image)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image)
        val miniDescription = itemView.findViewById<TextView>(R.id.text_description)
    }
}
