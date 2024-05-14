package ua.khai.slesarev.bookfinder.ui.home_screen

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.khai.slesarev.bookfinder.R
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.databinding.ActivityHomeBinding
import ua.khai.slesarev.bookfinder.ui.sign_in_screen.SingInActivity
import ua.khai.slesarev.bookfinder.ui.util.END_SESSION_REQUEST_CODE
import ua.khai.slesarev.bookfinder.ui.util.launchAndCollectIn

class HomeActivity : AppCompatActivity() {

    private lateinit var signOutBtn: Button
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var searchBar: SearchBar
    private lateinit var closeBtn: ImageButton
    private val viewModel: HomeActivityViewModel by viewModels()
    private lateinit var intent: Intent
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(MY_TAG, "HomeActivity.onCreate(): Started!")
        setContentView(R.layout.activity_home)
        intent = Intent(this@HomeActivity, SingInActivity::class.java)

        val homeProgressBar = binding.homeProgrBar
        val dialog = Dialog(this)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        closeBtn = dialog.findViewById(R.id.backButton)
        signOutBtn = dialog.findViewById(R.id.signOutBtn)
        userNameText = dialog.findViewById(R.id.userNameText)
        userEmailText = dialog.findViewById(R.id.userEmailText)
        profileImage = dialog.findViewById(R.id.profile_image)

        searchBar = findViewById(R.id.search_bar)
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setupWithSearchBar(searchBar)

        viewModel.getCurrentUser()

        viewModel.getCurrentUserSuccessFlow.launchAndCollectIn(this){
            val User = it.first()
            userNameText.text = User.username
            userEmailText.text = User.email
            renderProfile(Uri.parse(User.imageUri), profileImage)
            renderSearchBarImage(Uri.parse(User.imageUri), searchBar)
        }

        viewModel.revokeTokenCompletedFlow.launchAndCollectIn(this) {
            viewModel.webLogoutComplete()
        }

        viewModel.logoutCompletedFlow.launchAndCollectIn(this) {
            dialog.dismiss()
            startActivity(intent)
            finish()
        }

        closeBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                dialog.dismiss()
            }
        })

        signOutBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                viewModel.signOut()
            }
        })

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)

        val homeScreenHostFrag = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        val navController = homeScreenHostFrag.findNavController()

        bottomNavigationView.setupWithNavController(navController)


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

    private fun renderProfile(resource: Uri, imageView: ImageView) {
        Glide.with(this)
            .load(resource)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error_image)
            .centerCrop()
            .circleCrop()
            .sizeMultiplier(0.50f)
            .into(imageView)
    }

    private fun renderSearchBarImage(resource: Uri, searchTopBar: SearchBar){
        Glide.with(this)
            .load(resource)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error_image)
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
                    resource?.let { renderProfileImage(it, searchTopBar) }
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
    }
}