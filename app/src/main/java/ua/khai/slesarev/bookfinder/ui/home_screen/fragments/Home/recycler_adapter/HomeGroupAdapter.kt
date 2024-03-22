package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.model.BookItem
import ua.khai.slesarev.bookfinder.databinding.BookItemBinding
import ua.khai.slesarev.bookfinder.databinding.HomeItemBinding


class HomeGroupAdapter(private val data: Map<String, List<BookItem>>) : RecyclerView.Adapter<HomeGroupAdapter.GroupHolder>() {
    class GroupHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val booksListRecycler: RecyclerView = itemView.findViewById(R.id.booksListRecycler)
        private val groupTitle: Button = itemView.findViewById(R.id.lookAllBtn)

        fun bind(title: String, items: List<BookItem>) {
            groupTitle.text = title

            booksListRecycler.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            booksListRecycler.adapter = BookListAdapter(items)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return GroupHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        val entry = data.entries.toList()[position]
        holder.bind(entry.key, entry.value)
    }
}