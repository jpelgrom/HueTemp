package nl.jpelgrom.huetemp.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import nl.jpelgrom.huetemp.data.DiscoveredBridge

class DiscoveredBridgesDiffCallback(
    private val oldList: List<DiscoveredBridge>,
    private val newList: List<DiscoveredBridge>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].ip == newList[newItemPosition].ip
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}