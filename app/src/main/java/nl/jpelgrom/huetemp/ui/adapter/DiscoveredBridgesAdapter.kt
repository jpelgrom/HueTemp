package nl.jpelgrom.huetemp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import nl.jpelgrom.huetemp.R
import nl.jpelgrom.huetemp.data.DiscoveredBridge
import nl.jpelgrom.huetemp.data.HueBridgeType
import nl.jpelgrom.huetemp.databinding.ListDiscoveredbridgeBinding

class DiscoveredBridgesAdapter(
    private val items: List<DiscoveredBridge>,
    private val listener: (DiscoveredBridge) -> Unit
) : RecyclerView.Adapter<DiscoveredBridgesAdapter.ViewHolder>() {

    private val bridges = mutableListOf<DiscoveredBridge>()

    init {
        bridges.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListDiscoveredbridgeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = bridges.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(bridges[position])

    fun swap(bridges: List<DiscoveredBridge>) {
        val diffCallback = DiscoveredBridgesDiffCallback(this.bridges, bridges)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.bridges.clear()
        this.bridges.addAll(bridges)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private val binding: ListDiscoveredbridgeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiscoveredBridge) {
            with(binding) {
                if (item.type == HueBridgeType.IP) name.setText(R.string.discovery_manualip) else name.text =
                    item.name
                icon.setImageResource(
                    when (item.type) {
                        HueBridgeType.V1 -> R.drawable.ic_hue_bridge1
                        HueBridgeType.V2 -> R.drawable.ic_hue_bridge2
                        else -> R.drawable.ic_space_bar
                    }
                )

                root.setOnClickListener { listener(item) }
            }
        }
    }
}