package ua.khai.slesarev.bookfinder.ui.home_screen

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.databinding.ActivityHomeBinding
import ua.khai.slesarev.bookfinder.databinding.FragSingInBinding
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.HomeActivityViewModel
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.SingInActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var signOutBtn: Button
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private val viewModel: HomeActivityViewModel by viewModels()
    private lateinit var intent: Intent
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        intent =Intent(this@HomeActivity, SingInActivity::class.java)
        val homeProgressBar = binding.homeProgrBar

        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val closeBtn = dialog.findViewById<ImageButton>(R.id.backButton)
        signOutBtn = dialog.findViewById(R.id.signOutBtn)
        userNameText = dialog.findViewById(R.id.userNameText)
        userEmailText = dialog.findViewById(R.id.userEmailText)

        lifecycleScope.launch {
            viewModel.updateUI()
            userNameText.text = viewModel.userName
            userEmailText.text = viewModel.userEmail
        }

        closeBtn.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View) {
                dialog.dismiss()
            }
        })

        signOutBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                viewModel.singOut()
                dialog.dismiss()
                //homeProgressBar.visibility = View.VISIBLE
                startActivity(intent)
                //homeProgressBar.visibility = View.GONE
                finish()
            }
        })

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)

        val homeScreenHostFrag = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        val navController = homeScreenHostFrag.findNavController()

        bottomNavigationView.setupWithNavController(navController)

        val searchBar = findViewById<SearchBar>(R.id.search_bar)
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setupWithSearchBar(searchBar)

        Glide.with(this)
            .load(R.drawable.ic_big_avatar)
            .centerCrop()
            .circleCrop()
            .sizeMultiplier(0.50f) //optional
            .addListener(object : RequestListener<Drawable> {

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let { renderProfileImage(it, searchBar) }
                    return true
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

            }).submit()

        searchBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {

                    dialog.show()

                    true
                }

                else ->{
                    false
                }
            }
        }

    }

    private fun renderProfileImage(resource:Drawable, searchTopBar: SearchBar) {
        lifecycleScope.launch(Dispatchers.Main){ //Running on Main/UI thread
            searchTopBar.menu.findItem(R.id.action_search).icon = resource
        }
    }
}