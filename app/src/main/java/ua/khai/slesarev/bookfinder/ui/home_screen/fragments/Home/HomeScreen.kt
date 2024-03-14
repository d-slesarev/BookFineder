package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.model.BookItem
import ua.khai.slesarev.bookfinder.databinding.FragHomeBinding
import ua.khai.slesarev.bookfinder.databinding.FragSingInBinding
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.HomeGroupAdapter
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.fragments.SingIn.SignInViewModel
import ua.khai.slesarev.bookfinder.ui.util.StateHomeList
import ua.khai.slesarev.bookfinder.ui.util.UiState
import ua.khai.slesarev.bookfinder.ui.util.resourse_util.getBookResourses

class HomeScreen : Fragment() {

    private val booksMap = getBookResourses()
    private val viewModel: HomeScreenViewModel by viewModels()

    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var homeGroupList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeGroupList = view.findViewById(R.id.homeGroupList)
        shimmerView = view.findViewById(R.id.shimmer_view_container)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        shimmerView.startShimmer()
        swipeRefreshLayout.visibility = View.GONE

        viewModel.uiState.observe(requireActivity()) { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        }

    }

    private fun render(uiState: StateHomeList<List<BookItem>>) {
        when (uiState) {
            is StateHomeList.Loading -> {}

            is StateHomeList.Success -> {
                updateRender(uiState.response)
            }
            is StateHomeList.Error -> {}
        }
    }

    private fun updateRender(response: List<BookItem>) {
        shimmerView.stopShimmer()
        swipeRefreshLayout.visibility = View.VISIBLE

        try {
            homeGroupList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            homeGroupList.adapter = HomeGroupAdapter(booksMap)
        } catch (e: Exception){
            Log.d("MY_TAG", "HomeScreen: ${e.message.toString()}")
        }
    }

}