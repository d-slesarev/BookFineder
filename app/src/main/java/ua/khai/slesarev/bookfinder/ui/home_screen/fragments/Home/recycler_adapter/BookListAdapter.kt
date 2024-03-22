package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.suspendCancellableCoroutine
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.model.BookItem
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.databinding.BookItemBinding
import ua.khai.slesarev.bookfinder.databinding.HomeItemBinding

class BookListAdapter(private val items: List<BookItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_LOADING = 1
    }

    /*
    *  TODO: Продумай, как разумно организовать вывод длинных названий книг
    *   и стоит ли выводить автора книги... возможно лучше оставить только цену и название.
    */
    class BookHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover)
        fun bind(item: BookItem)  {
            val secureUrl = if (item.volumeInfo?.imageLinks?.thumbnail?.startsWith("http://") == true) {
                item.volumeInfo?.imageLinks?.thumbnail.replaceFirst("http://", "https://")
            } else {
                item.volumeInfo?.imageLinks?.thumbnail
            }
                Glide.with(itemView.context)
                    .load(secureUrl)
                    .fitCenter()
                    .transform(RoundedCorners(18))
                    .placeholder(R.drawable.unnamed210)
                    .error(R.drawable.frankenshtein)
                    .into(bookCover)
            bookTitle.text = "${item.volumeInfo?.title}"
            bookAuthor.text = "${item.volumeInfo?.authors?.toString()}"
        }
    }

    class LoadingItemHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) TYPE_LOADING // Если элемент последний, возвращаем TYPE_FOOTER
        else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
            BookHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loading_item, parent, false)
            LoadingItemHolder(view)
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is BookHolder){
            holder.bind(items[position])
        }
    }
}