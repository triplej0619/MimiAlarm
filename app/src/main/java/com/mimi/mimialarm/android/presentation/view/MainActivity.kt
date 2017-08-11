package com.mimi.mimialarm.android.presentation.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.mimi.mimialarm.BuildConfig
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.infrastructure.BackPressedEvent
import com.mimi.mimialarm.android.infrastructure.ChangePageEvent
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.ContextUtil
import com.mimi.mimialarm.android.utils.LogUtil
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.infrastructure.ChagneThemeEvent
import com.mimi.mimialarm.core.utils.Command
import com.mimi.mimialarm.core.utils.Enums
import com.mimi.mimialarm.databinding.ActivityMainBinding
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject lateinit var bus: Bus
    @Inject lateinit var dataManager: ApplicationDataManager

    var viewPagerAdapter: CustomViewPagerAdapter? = null
    var binding: ActivityMainBinding? = null
    var adBannerId: ObservableField<String> = ObservableField("")

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        buildComponent().inject(this)
        setTheme(ContextUtil.getThemeId(dataManager.getCurrentTheme()))
        setAdInfo()

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        init()
    }

    fun setAdInfo() {
        if(BuildConfig.DEBUG) {
            adBannerId.set(getString(R.string.test_ad_banner))
        } else {
            adBannerId.set(getString(R.string.ad_main_bottom_banner))
        }
        LogUtil.printDebugLog(MainActivity::class.java, "loadAdVideo() : $adBannerId")
    }

    fun init() {
        bus.register(this)

        MobileAds.initialize(applicationContext, getString(R.string.adsmob_id))

        val adRequestBuild = AdRequest.Builder()
        if(BuildConfig.DEBUG) {
            adRequestBuild.addTestDevice("A9BE77B32D16C6F3BD1C2609FA36BE9C")
        }
        binding?.adView?.loadAd(adRequestBuild.build())

        setupViewPager()
    }

    override fun onDestroy() {
        super.onDestroy()
        bus.unregister(this)
    }

    fun setupViewPager() {
        viewPagerAdapter = CustomViewPagerAdapter(supportFragmentManager);

        binding?.viewpager?.adapter = viewPagerAdapter
        binding?.viewpager?.offscreenPageLimit = 2

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

        binding?.viewpager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                bus.post(ChangePageEvent(position))
            }

        })
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        if(binding!!.viewpager.currentItem < 2) {
            bus.post(BackPressedEvent(Enums.MAIN_TAB.valueOf(binding!!.viewpager.currentItem),
                object : Command {
                override fun execute(arg: Any) {
                    this@MainActivity.finish()
                }
            }))
        } else {
            super.onBackPressed()
        }
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

    @Subscribe
    fun answerChangeThemeEvent(event: ChagneThemeEvent) {
        recreate()
    }
}
