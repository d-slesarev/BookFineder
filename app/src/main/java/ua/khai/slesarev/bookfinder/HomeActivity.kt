package ua.khai.slesarev.bookfinder

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import ua.khai.slesarev.bookfinder.dialogs.FullscreenDialog

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)

        val homeScreenHostFrag = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        val navController = homeScreenHostFrag.findNavController()

        bottomNavigationView.setupWithNavController(navController)

        val searchBar = findViewById<SearchBar>(R.id.search_bar)
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setupWithSearchBar(searchBar)

        searchBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {

                    val fragmentManager = supportFragmentManager
                    val newFragment = FullscreenDialog()

                    val transaction = fragmentManager.beginTransaction()
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    transaction
                        .add(android.R.id.content, newFragment)
                        .addToBackStack(null)
                        .commit()

                    true
                }

                else ->{
                    false
                }
            }
        }
    }
}