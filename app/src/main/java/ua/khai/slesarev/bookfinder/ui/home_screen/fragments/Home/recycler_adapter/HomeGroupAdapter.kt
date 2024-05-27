package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import ua.khai.slesarev.bookfinder.R
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ua.khai.slesarev.bookfinder.data.repository.book.BookType
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.HomeFragmentViewModel


class HomeGroupAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: HomeFragmentViewModel
) : RecyclerView.Adapter<HomeGroupAdapter.GroupHolder>() {
    class GroupHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val groupTitle: Button = itemView.findViewById(R.id.lookAllBtn)
        val booksListRecycler: RecyclerView = itemView.findViewById(R.id.booksListRecycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return GroupHolder(view)
    }

    override fun getItemCount(): Int = viewModel.configList.size

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        when(position){
            BookType.RECENT.ordinal -> {
                Log.i(MY_TAG, "onBindViewHolder: RECENT")
                try {
                    holder.groupTitle.text = viewModel.shelfTitles[position]
                    holder.booksListRecycler.layoutManager = LinearLayoutManager(holder.booksListRecycler.context, LinearLayoutManager.HORIZONTAL, false)
                    holder.booksListRecycler.adapter = viewModel.RecentAdapter

                    lifecycleOwner.lifecycleScope.launch {
                        viewModel.RecentFlow.collectLatest { pagingData ->
                            viewModel.RecentAdapter.submitData(pagingData)
                        }
                    }
                } catch (e:Exception){
                    Log.e(MY_TAG, "onBindViewHolder()-RECENT: ${e.message}")
                }

            }
            BookType.ADVENTURES.ordinal -> {
                Log.i(MY_TAG, "onBindViewHolder: ADVENTURES")
                try {
                    holder.groupTitle.text = viewModel.shelfTitles[position]
                    holder.booksListRecycler.layoutManager = LinearLayoutManager(holder.booksListRecycler.context, LinearLayoutManager.HORIZONTAL, false)
                    holder.booksListRecycler.adapter = viewModel.AdventuresAdapter

                    lifecycleOwner.lifecycleScope.launch {
                        viewModel.AdventuresFlow.collectLatest { pagingData ->
                            viewModel.AdventuresAdapter.submitData(pagingData)
                        }
                    }
                } catch (e:Exception){
                    Log.e(MY_TAG, "onBindViewHolder()-ADVENTURES: ${e.message}")
                }
            }
            BookType.THRILLERS.ordinal -> {
                Log.i(MY_TAG, "onBindViewHolder: THRILLERS")
                try {
                    holder.groupTitle.text = viewModel.shelfTitles[position]
                    holder.booksListRecycler.layoutManager = LinearLayoutManager(holder.booksListRecycler.context, LinearLayoutManager.HORIZONTAL, false)
                    holder.booksListRecycler.adapter = viewModel.ThrillersAdapter

                    lifecycleOwner.lifecycleScope.launch {
                        viewModel.ThrillersFlow.collectLatest { pagingData ->
                            viewModel.ThrillersAdapter.submitData(pagingData)
                        }
                    }
                } catch (e:Exception){
                    Log.e(MY_TAG, "onBindViewHolder()-THRILLERS: ${e.message}")
                }
            }
            BookType.MYSTERY.ordinal -> {
                Log.i(MY_TAG, "onBindViewHolder: MYSTERY")
                try {
                    holder.groupTitle.text = viewModel.shelfTitles[position]
                    holder.booksListRecycler.layoutManager = LinearLayoutManager(holder.booksListRecycler.context, LinearLayoutManager.HORIZONTAL, false)
                    holder.booksListRecycler.adapter = viewModel.MysteryAdapter

                    lifecycleOwner.lifecycleScope.launch {
                        viewModel.MysteryFlow.collectLatest { pagingData ->
                            viewModel.MysteryAdapter.submitData(pagingData)
                        }
                    }
                } catch (e:Exception){
                    Log.e(MY_TAG, "onBindViewHolder()-MYSTERY: ${e.message}")
                }
            }
            BookType.FANTASY.ordinal -> {
                Log.i(MY_TAG, "onBindViewHolder: FANTASY")
                try {
                    holder.groupTitle.text = viewModel.shelfTitles[position]
                    holder.booksListRecycler.layoutManager = LinearLayoutManager(holder.booksListRecycler.context, LinearLayoutManager.HORIZONTAL, false)
                    holder.booksListRecycler.adapter = viewModel.FantasyAdapter

                    lifecycleOwner.lifecycleScope.launch {
                        viewModel.FantasyFlow.collectLatest { pagingData ->
                            viewModel.FantasyAdapter.submitData(pagingData)
                        }
                    }
                } catch (e:Exception){
                    Log.e(MY_TAG, "onBindViewHolder()-FANTASY: ${e.message}")
                }
            }
            BookType.ROMANCE.ordinal -> {
                Log.i(MY_TAG, "onBindViewHolder: ROMANCE")
                try {
                    holder.groupTitle.text = viewModel.shelfTitles[position]
                    holder.booksListRecycler.layoutManager = LinearLayoutManager(holder.booksListRecycler.context, LinearLayoutManager.HORIZONTAL, false)
                    holder.booksListRecycler.adapter = viewModel.RomanceAdapter

                    lifecycleOwner.lifecycleScope.launch {
                        viewModel.RomanceFlow.collectLatest { pagingData ->
                            viewModel.RomanceAdapter.submitData(pagingData)
                        }
                    }
                } catch (e:Exception){
                    Log.e(MY_TAG, "onBindViewHolder()-ROMANCE: ${e.message}")
                }
            }
            BookType.SELF_DEVELOPMENT.ordinal -> {
                Log.i(MY_TAG, "onBindViewHolder: SELF_DEVELOPMENT")
                try {
                    holder.groupTitle.text = viewModel.shelfTitles[position]
                    holder.booksListRecycler.layoutManager = LinearLayoutManager(holder.booksListRecycler.context, LinearLayoutManager.HORIZONTAL, false)
                    holder.booksListRecycler.adapter = viewModel.SelfDevelopmentAdapter

                    lifecycleOwner.lifecycleScope.launch {
                        viewModel.SelfDevelopmentFlow.collectLatest { pagingData ->
                            viewModel.SelfDevelopmentAdapter.submitData(pagingData)
                        }
                    }
                } catch (e:Exception){
                    Log.e(MY_TAG, "onBindViewHolder()-SELF_DEVELOPMENT: ${e.message}")
                }
            }
            BookType.FICTION.ordinal -> {
                Log.i(MY_TAG, "onBindViewHolder: FICTION")
                try {
                    holder.groupTitle.text = viewModel.shelfTitles[position]
                    holder.booksListRecycler.layoutManager = LinearLayoutManager(holder.booksListRecycler.context, LinearLayoutManager.HORIZONTAL, false)
                    holder.booksListRecycler.adapter = viewModel.FictionAdapter

                    lifecycleOwner.lifecycleScope.launch {
                        viewModel.FictionFlow.collectLatest { pagingData ->
                            viewModel.FictionAdapter.submitData(pagingData)
                        }
                    }
                } catch (e:Exception){
                    Log.e(MY_TAG, "onBindViewHolder()-FICTION: ${e.message}")
                }
            }
        }
    }
}
































