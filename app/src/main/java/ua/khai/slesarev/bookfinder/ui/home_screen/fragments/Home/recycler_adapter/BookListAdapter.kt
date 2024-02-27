package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.BookItemBinding
import ua.khai.slesarev.bookfinder.databinding.HomeItemBinding

class BookListAdapter(private val items: List<String>) : RecyclerView.Adapter<BookListAdapter.BookHolder>() {
    class BookHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover)
        fun bind(text: String)  {
            Glide.with(itemView.context)
                .load(R.drawable.unnamed)
                .apply(
                    RequestOptions()
                    .override(325, 500)
                    .transform(RoundedCorners(18))
                )
                .into(bookCover)
            bookTitle.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        holder.bind(items[position])
    }
}