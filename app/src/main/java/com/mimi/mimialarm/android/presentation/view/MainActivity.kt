package com.mimi.mimialarm.android.presentation.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import com.mimi.mimialarm.R
import com.mimi.mimialarm.databinding.ActivityMainBinding
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper




class MainActivity : AppCompatActivity() {

    var viewPagerAdapter: CustomViewPagerAdapter? = null
    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        init()
    }

    fun init() {
        setupViewPager()
    }

    fun setupViewPager() {
        viewPagerAdapter = CustomViewPagerAdapter(supportFragmentManager);

        binding?.viewpager?.adapter = viewPagerAdapter
        binding!!.viewpager.offscreenPageLimit = 2

        viewPagerAdapter?.addItem(AlarmFragment())
        viewPagerAdapter?.addItem(TimerFragment())
        viewPagerAdapter?.addItem(SettingsFragment())

        binding?.tabs?.setupWithViewPager(binding?.viewpager)
        for (i in 0..binding!!.tabs.tabCount - 1) {
            binding?.tabs?.getTabAt(i)?.setCustomView(R.layout.custom_img_tab)
            var iconIndex = R.drawable.icn_alarm
            when(i) {
                1 -> iconIndex = R.drawable.icn_timer
                2 -> iconIndex = R.drawable.icn_menu
            }
            binding?.tabs?.getTabAt(i)?.customView?.findViewById(R.id.tabIcon)?.setBackgroundResource(iconIndex)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    class CustomViewPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {

        var fragmentList: MutableList<Fragment> = java.util.ArrayList()

        fun addItem(fragment: Fragment) {
            fragmentList.add(fragment)
            notifyDataSetChanged()
        }

        fun clear() {
            fragmentList.clear()
            notifyDataSetChanged()
        }

        override fun getItem(position: Int): Fragment? {
            when(position) {
                0 -> return AlarmFragment()
                1 -> return TimerFragment()
                2 -> return SettingsFragment()
            }
            return Fragment()
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
    }
}
