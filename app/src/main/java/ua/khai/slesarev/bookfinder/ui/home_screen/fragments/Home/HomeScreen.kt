package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.HomeGroupAdapter
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
/*TODO: Реализуй механизм пагинации. То есть подгрузки новых книг
    с Books API таким способом что бы подгружать список кусочками*/
/*TODO: Реализуй способ подгрузки списка книг для случая первого запуска приложения
*  и для случия повторной отрисовки фрагмента*/
/*TODO: Реализуй перезагрузку списка книг с учётом наличия или отсутствия новых книг в  списках*/
class HomeScreen : Fragment() {

    private val viewModel: HomeFragmentViewModel by viewModels()
    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var homeGroupRecycler: RecyclerView
    private lateinit var homeGroupAdapter: HomeGroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(MY_TAG, "HomeScreen.onViewCreated: Started!")
        homeGroupRecycler = view.findViewById(R.id.homeGroupList)
        homeGroupAdapter = HomeGroupAdapter(viewModel = viewModel, lifecycleOwner = this)
        shimmerView = view.findViewById(R.id.loadingBookCover)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        shimmerView.startShimmer()
        homeGroupRecycler.isVisible = false

        Log.i(MY_TAG, "Before: viewModel.initContent()")
        try {
            viewModel.initContent()
        } catch (e: Exception) {
            Log.e(MY_TAG, "HomeScreen: ${e.message.toString()}")
        }

        homeGroupRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        homeGroupRecycler.adapter = homeGroupAdapter


        lifecycleScope.launchWhenCreated {
            viewModel.FictionAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.source.refresh is LoadState.NotLoading }
                .collect {
                    homeGroupRecycler.isVisible = true
                    shimmerView.stopShimmer()
                }
        }
    }

    private fun updateRender() {
        homeGroupRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        homeGroupRecycler.adapter = homeGroupAdapter
    }
}