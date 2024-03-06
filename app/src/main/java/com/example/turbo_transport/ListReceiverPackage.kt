package com.example.turbo_transport

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging

class ListReceiverPackage : AppCompatActivity() {
    lateinit var receiverRecylerView: RecyclerView
    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var bottomBar: NavigationBarView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_receiver_package)
        viewPager = findViewById(R.id.receiverView_Pager)
        tabLayout = findViewById(R.id.receiverTabs)

        db = Firebase.firestore
        auth = Firebase.auth



        setupViewPager()
        setupTabLayout()
        setTokenDb()
        initializeViews()
        showMenu()
        bottomMenu()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
    private fun setupViewPager() {
        val adapter = TabsPagerAdapter(this)
        viewPager.adapter = adapter
    }
    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Active"
                1 -> "Done"
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }.attach()
    }
    inner class TabsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ReceiverDeliveriesFragment()
                1 -> ReceiverDoneDeliveriesFragment()
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }
    }

    private fun showMenu(){
        topAppBar.setNavigationOnClickListener {
            finish()
        }
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.user -> {
                    goToReceiverProfile()
                    true
                }
                R.id.help -> {
                    showFAQDialog()
                    true
                }

                else -> false
            }
        }
    }
    private fun showFAQDialog() {
        val dialog = AlertDialog.Builder(this)
            .setView(R.layout.faq_layout)
            .create()

        dialog.show()
    }
    private fun goToReceiverProfile() {
        val intent = Intent(this,ReceiverInforamtionActivity::class.java)
        startActivity(intent)
    }
    private fun bottomMenu(){

        bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {

                    true
                }

                R.id.item_2 -> {

                    true
                }

                R.id.item_3 -> {

                    true
                }
                else -> false
            }
        }

        bottomBar.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {

                }
                R.id.item_2 -> {


                }
                R.id.item_3 -> {

                    true
                }

            }
        }
    }

    fun setTokenDb() {
        val user = auth.currentUser
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getToken failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Log.d("!!!", "this user token:$token")
                if (user != null) {
                    db.collection("users").document(user.uid)
                        .set(
                            mapOf("notificationToken" to token),
                            SetOptions.merge()
                        )
                }
            })
    }

    private fun initializeViews() {
        topAppBar = findViewById(R.id.topAppBar)
        appBarLayout = findViewById(R.id.appBarLayout)
        bottomBar = findViewById(R.id.bottom_navigation)
    }

}