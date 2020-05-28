package com.eightnineapps.coinly.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.Prize
import kotlinx.android.synthetic.main.prize_list_view_layout.view.*

class PrizesRecyclerViewAdapter(_items: List<Prize>, _context: Context): RecyclerView.Adapter<PrizesRecyclerViewAdapter.ViewHolder>() {

    private var prizeList = _items
    var context = _context

    class ViewHolder(_view: View, _items: List<Prize>): RecyclerView.ViewHolder(_view), View.OnClickListener {

        private var context: Context = _view.context
        private var viewHolderPrizeList = _items

        val singlePrizePictureImageView: ImageView = _view.prize_picture

        init {
            _view.isClickable = true
            _view.setOnClickListener(this)
        }

        /**
         * Start a shared element transition to an AlertDialog where the user can confirm actions
         * they want to take on a prize
         */
        override fun onClick(v: View?) {
            Toast.makeText(context, "Checking out Prize", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Inflates each row of the recycler view with the proper layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater
            .from(context)
            .inflate(R.layout.prize_list_view_layout, parent, false), prizeList)
    }

    /**
     * Returns the number of prizes
     */
    override fun getItemCount(): Int {
        return prizeList.size
    }

    /**
     * Loads the prize picture uri into the ImageView
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPrize = prizeList[position]
        Glide.with(context).load(currentPrize.uri).into(holder.singlePrizePictureImageView)
    }
}