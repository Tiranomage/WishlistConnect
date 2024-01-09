package com.example.wishlistconnect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class GiftAdapter(private val context: Context, private var gifts: List<Gift>) : BaseAdapter() {

    override fun getCount(): Int {
        return gifts.size
    }

    override fun getItem(position: Int): Any {
        return gifts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_item_gift, null)

            holder = ViewHolder()
            holder.giftNameTextView = view.findViewById(R.id.giftNameTextView)
            holder.giftDescriptionTextView = view.findViewById(R.id.giftDescriptionTextView)
            holder.giftPriceTextView = view.findViewById(R.id.giftPriceTextView)

            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val gift = getItem(position) as Gift

        holder.giftNameTextView.text = gift.name
        holder.giftDescriptionTextView.text = gift.description
        holder.giftPriceTextView.text = "$${gift.price}"

        return view!!
    }

    // ViewHolder pattern to improve performance by avoiding findViewById on each getView call
    private class ViewHolder {
        lateinit var giftNameTextView: TextView
        lateinit var giftDescriptionTextView: TextView
        lateinit var giftPriceTextView: TextView
    }

    // Function to update the gifts data in the adapter
    fun updateGifts(newGifts: List<Gift>) {
        gifts = newGifts
        notifyDataSetChanged()
    }
}
