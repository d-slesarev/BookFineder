package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.facebook.shimmer.ShimmerFrameLayout
import ua.khai.slesarev.bookfinder.R
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.DataSource
import ua.khai.slesarev.bookfinder.data.models.local.books.RecentBook
import ua.khai.slesarev.bookfinder.data.util.MY_TAG

class RecentListAdapter :
    PagingDataAdapter<RecentBook, RecyclerView.ViewHolder>(RecentBookComparator) {
    class RecentBookHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        private val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        private val bookCover: ImageView = itemView.findViewById(R.id.bookCover)

        private val presentBookCover: LinearLayout = itemView.findViewById(R.id.presentBookCover)
        private val loadingBookCover: ShimmerFrameLayout = itemView.findViewById(R.id.loadingBookCover)
        fun bindBook(item: RecentBook)  {
            presentBookCover.visibility = View.GONE
            loadingBookCover.visibility = View.VISIBLE
            val secureUrl = if (item.coverUrl.startsWith("http://") == true) {
                item.coverUrl.replaceFirst("http://", "https://")
            } else {
                item.coverUrl
            }
            Glide.with(itemView.context)
                .load(secureUrl)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        presentBookCover.visibility = View.VISIBLE
                        loadingBookCover.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        presentBookCover.visibility = View.VISIBLE
                        loadingBookCover.visibility = View.GONE
                        return false
                    }
                })
                .fitCenter()
                .transform(RoundedCorners(18))
                .into(bookCover)
            bookTitle.text = item.title
            bookAuthor.text = item.authors
        }

        fun bindPlaceholder(){
            presentBookCover.visibility = View.GONE
            loadingBookCover.visibility = View.VISIBLE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val book = getItem(position)
        if (book != null) {
            (holder as RecentBookHolder).bindBook(book)
        } else {
            Log.e(MY_TAG, "RecentListAdapter.onBindViewHolder(): NULL!!!")
            (holder as RecentBookHolder).bindPlaceholder()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return RecentBookHolder(view)
    }

    object RecentBookComparator : DiffUtil.ItemCallback<RecentBook>() {
        override fun areItemsTheSame(oldItem: RecentBook, newItem: RecentBook): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecentBook, newItem: RecentBook): Boolean {
            return newItem == oldItem
        }
    }

}