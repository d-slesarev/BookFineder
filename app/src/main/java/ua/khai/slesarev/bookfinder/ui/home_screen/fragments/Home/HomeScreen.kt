package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.FragHomeBinding
import ua.khai.slesarev.bookfinder.databinding.FragSingInBinding
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.HomeGroupAdapter
import ua.khai.slesarev.bookfinder.ui.util.resourse_util.getBookResourses

class HomeScreen : Fragment() {
    private val booksMap = getBookResourses()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeGroupList: RecyclerView = view.findViewById(R.id.homeGroupList)
        val shimmerView: ShimmerFrameLayout = view.findViewById(R.id.shimmer_view_container)

        try {
            homeGroupList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            homeGroupList.adapter = HomeGroupAdapter(booksMap)
        } catch (e: Exception){
            Log.d("MY_TAG", "HomeScreen: ${e.message.toString()}")
        }
    }

}