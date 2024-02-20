package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.HomeItemBinding


class HomeGroupAdapter : RecyclerView.Adapter<HomeGroupAdapter.GroupHolder>() {
    private val itemsCount = 10

    class GroupHolder(item : View) : RecyclerView.ViewHolder(item) {
        val binding = HomeItemBinding.bind(item)
        val booksListRecycler: RecyclerView = binding.booksListRecycler
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return GroupHolder(view)
    }

    override fun getItemCount(): Int = itemsCount

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        holder.booksListRecycler.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.booksListRecycler.adapter = BookListAdapter()
    }
}