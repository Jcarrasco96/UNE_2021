package com.jcarrasco96.une2021.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jcarrasco96.une2021.R
import com.jcarrasco96.une2021.database.Registry
import com.jcarrasco96.une2021.databinding.ItemRegistryBinding
import com.jcarrasco96.une2021.interfaces.IRegistryAdapterIterface
import com.jcarrasco96.une2021.utils.Utils
import java.text.DecimalFormat

class RegistryAdapter(private val registryAdapterInterface: IRegistryAdapterIterface) :
    GenericAdapter<Registry, RegistryAdapter.AdapterHolder>() {

    private val nf = DecimalFormat("#,##0.00")

    class AdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRegistryBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        this.context = parent.context

        return AdapterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_registry, parent, false)
        )
    }

    override fun bind(holder: AdapterHolder, data: Registry) {
        val importe =
            nf.format(Utils.importe(data.lectura - data.ultima_lectura))

        when (data.oficial) {
            false -> {
                holder.binding.btnMarkAsOfficial.setImageResource(R.drawable.ic_star_off)
                holder.binding.btnMarkAsOfficial.setColorFilter(
                    ContextCompat.getColor(context, R.color.blue_darken_3), PorterDuff.Mode.SRC_IN
                )
            }
            true -> {
                holder.binding.btnMarkAsOfficial.setImageResource(R.drawable.ic_star_on)
                holder.binding.btnMarkAsOfficial.setColorFilter(
                    ContextCompat.getColor(context, R.color.yellow_darken_4), PorterDuff.Mode.SRC_IN
                )
            }
        }
        holder.binding.itemRead.text =
            context.getString(R.string.registry_interval, data.ultima_lectura, data.lectura)
        holder.binding.itemConsumo.text =
            context.getString(R.string.consumo_interval, data.lectura - data.ultima_lectura)
        holder.binding.itemImporte.text = context.getString(R.string.importe_interval, importe)
        holder.binding.itemDate.text = data.fecha

        holder.binding.btnDelete.setOnClickListener {
            registryAdapterInterface.buttonPressedDelete(data)
        }
        holder.binding.btnMarkAsOfficial.setOnClickListener {
            registryAdapterInterface.buttonPressedOficial(data)
        }
    }

}