package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.models.local.books.MysteryBook
import ua.khai.slesarev.bookfinder.data.models.local.books.ThrillersBook

class MysteryListAdapter :
    PagingDataAdapter<MysteryBook, MysteryListAdapter.MysteryBookHolder>(MysteryBookComparator) {
    class MysteryBookHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover)
        fun bind(item: MysteryBook)  {
            val secureUrl = if (item.coverUrl.startsWith("http://") == true) {
                item.coverUrl.replaceFirst("http://", "https://")
            } else {
                item.coverUrl
            }
            Glide.with(itemView.context)
                .load(secureUrl)
                .fitCenter()
                .transform(RoundedCorners(18))
                .placeholder(R.drawable.unnamed210)
                .error(R.drawable.frankenshtein)
                .into(bookCover)
            bookTitle.text = item.title
            bookAuthor.text = item.authors
        }
    }

    override fun onBindViewHolder(holder: MysteryBookHolder, position: Int) {
        val book = getItem(position)
        if (book != null) {
            holder.bind(book)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MysteryBookHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return MysteryBookHolder(view)
    }

    object MysteryBookComparator : DiffUtil.ItemCallback<MysteryBook>() {
        override fun areItemsTheSame(oldItem: MysteryBook, newItem: MysteryBook): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MysteryBook, newItem: MysteryBook): Boolean {
            return newItem == oldItem
        }
    }
}