package com.example.turbo_transport

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class ListDeliveries : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    private lateinit var topAppBar1 : MaterialToolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listdeliveries)
        initializeViews()
        db = Firebase.firestore
        auth = Firebase.auth

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tabs)


        setupViewPager()
        setupTabLayout()
        bottomMenu()
        topBar()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
    private fun showLocation() {
        val intent = Intent(this,MapsActivity::class.java)
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
                2 -> "Failed Delivery"
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }.attach()
    }

    inner class TabsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DeliveriesFragment()
                1 -> DoneDeliveriesFragment()
                2 -> FailedDeliveriesFragment()
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }
    }

    private fun topBar(){
        topAppBar1.setNavigationOnClickListener {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        topAppBar1.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.user -> {
                    goToDriverProfile()
                    // Handle edit text press
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

        private fun goToDriverProfile() {
            val intent = Intent(this, DriverInformationActivity::class.java)
            startActivity(intent)

        }

        private fun bottomMenu() {
            val bottomNavigation = findViewById<NavigationBarView>(R.id.bottom_navigation)
            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.item_1 -> {
                        true
                    }

                    R.id.item_2 -> {
                        showLocation()
                        true
                    }

                    R.id.item_3 -> {
                        //start addPackageActivity:
                        val intent = Intent(this, AddPackageActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    else -> false
                }
            }

            bottomNavigation.setOnItemReselectedListener { item ->
                when (item.itemId) {
                    R.id.item_1 -> {
                        true
                    }

                    R.id.item_2 -> {
                        showLocation()
                        true

                    }

                    R.id.item_3 -> {
                        val intent = Intent(this, AddPackageActivity::class.java)
                        startActivity(intent)
                        true
                    }

                }
            }
        }

        private fun initializeViews() {
            topAppBar1 = findViewById(R.id.topAppBar)
        }

}