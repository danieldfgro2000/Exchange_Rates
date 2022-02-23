package dfg.exchangerates.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dfg.exchangerates.data.model.ExchangeRates
import dfg.exchangerates.databinding.ViewItemBinding
import org.w3c.dom.Text

class ExchangeRatesAdapter : RecyclerView.Adapter<ExchangeRatesAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(binding: ViewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val from: TextView = binding.from
        val to: TextView = binding.to
        val rate: TextView = binding.rate
    }

    private val callback = object : DiffUtil.ItemCallback<ExchangeRates.Rate>() {
        override fun areItemsTheSame(oldItem: ExchangeRates.Rate, newItem: ExchangeRates.Rate): Boolean {
            return oldItem.rate == newItem.rate
        }

        override fun areContentsTheSame(oldItem: ExchangeRates.Rate, newItem: ExchangeRates.Rate): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.from.text = item.from
        holder.to.text = item.to
        holder.rate.text = item.rate
    }

    override fun getItemCount(): Int = differ.currentList.size
}