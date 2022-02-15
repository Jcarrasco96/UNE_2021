package com.jcarrasco96.une2021.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.jcarrasco96.une2021.R
import com.jcarrasco96.une2021.database.Contador
import com.jcarrasco96.une2021.databinding.ItemContadorBinding
import com.jcarrasco96.une2021.interfaces.IContadorAdapterInterface

class ContadorAdapter(private val contadorAdapterInterface: IContadorAdapterInterface) :
    GenericAdapter<Contador, ContadorAdapter.AdapterHolder>() {

    inner class AdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemContadorBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        this.context = parent.context

        return AdapterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_contador, parent, false)
        )
    }

    override fun bind(holder: AdapterHolder, data: Contador) {
        holder.binding.itemRead.text = data.nombre
        holder.binding.itemConsumo.text = data.id.toString()

        holder.binding.btnDelete.setOnClickListener {
            contadorAdapterInterface.buttonPressedDelete(data)
        }
        holder.binding.root.setOnClickListener {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("contador", data.id.toString())
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "Copaido!", Toast.LENGTH_SHORT).show()
        }
    }

}