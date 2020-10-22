package com.instagramclone

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.instagramclone.fragment.HomeFragment
import com.instagramclone.fragment.NotificationFragment
import com.instagramclone.fragment.ProfileFragment
import com.instagramclone.fragment.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var selectedFragment: Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        moveToFragment(HomeFragment())


    }

    private val onNavigationItemSelectedListener =

        BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> selectedFragment = HomeFragment()
                R.id.nav_search -> selectedFragment = SearchFragment()
                R.id.nav_add_post -> {
                }
                R.id.nav_notification -> selectedFragment = NotificationFragment()
                R.id.nav_profile -> selectedFragment = ProfileFragment()
            }

            moveToFragment(selectedFragment!!)

            true
        }

    private fun moveToFragment(fragment: Fragment) {
        Log.d(TAG, "moveToFragment: ")
        var fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragrment_container, fragment)
        fragmentTrans.commit()
    }

}