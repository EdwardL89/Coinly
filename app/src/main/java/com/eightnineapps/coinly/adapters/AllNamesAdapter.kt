package com.eightnineapps.coinly.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import kotlinx.android.synthetic.main.user_list_view_layout.view.*

/**
 * An adapter class to populate the recycler view
 */
class AllNamesAdapter(_items: List<String>, _context: Context): RecyclerView.Adapter<AllNamesAdapter.ViewHolder>() {

    private var list = _items
    private var context = _context

    /**
     * Explicitly defines the UI elements belonging to a single list element in the recycler view
     */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val singleUserName: TextView = view.user_name_text_view
    }

    /**
     * Inflates each row of the recycler view with the proper layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.user_list_view_layout, parent, false))
    }

    /**
     * Returns the number of items in the recycler view
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Defines what each UI element (defined in the ViewHolder class above) maps to
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.singleUserName.text = list[position]
    }
}