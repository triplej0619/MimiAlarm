package com.mimi.mimialarm.android.presentation.view

import android.databinding.DataBindingUtil
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.ActivityComponent
import com.mimi.mimialarm.android.presentation.DaggerActivityComponent
import com.mimi.mimialarm.android.presentation.MimiAlarmApplication
import com.mimi.mimialarm.android.presentation.ViewModelModule
import com.mimi.mimialarm.android.utils.ContextUtils
import com.mimi.mimialarm.core.presentation.viewmodel.SettingsViewModel
import com.mimi.mimialarm.databinding.FragmentSettingsBinding
import com.squareup.otto.Bus
import javax.inject.Inject
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.mimi.mimialarm.core.infrastructure.ShareToFriendsEvent
import com.mimi.mimialarm.core.infrastructure.StartRewardedAdEvent
import com.squareup.otto.Subscribe
import android.content.Intent
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.infrastructure.ChagneThemeEvent
import com.mimi.mimialarm.core.utils.Command


/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class SettingsFragment : android.support.v4.app.Fragment() {

    var rewardedVideoAd: RewardedVideoAd? = null
    var binding: FragmentSettingsBinding? = null
    var nextThemeIndex: Int = 0
    var themeChangeCompletedCallback: Command? = null
    @Inject lateinit var viewModel: SettingsViewModel
    @Inject lateinit var bus: Bus
    @Inject lateinit var dataManager: ApplicationDataManager

    val rewardedVideoAdListener: RewardedVideoAdListener = object : RewardedVideoAdListener {
        override fun onRewardedVideoAdClosed() {
            LogUtils.printDebugLog(SettingsFragment::class.java, "onRewardedVideoAdClosed()")
            loadAdVideo()
        }

        override fun onRewardedVideoAdLeftApplication() {
            LogUtils.printDebugLog(SettingsFragment::class.java, "onRewardedVideoAdLeftApplication()")
        }

        override fun onRewardedVideoAdLoaded() {
            LogUtils.printDebugLog(SettingsFragment::class.java, "onRewardedVideoAdLoaded()")
        }

        override fun onRewardedVideoAdOpened() {
            LogUtils.printDebugLog(SettingsFragment::class.java, "onRewardedVideoAdOpened()")
        }

        override fun onRewarded(p0: RewardItem?) {
            LogUtils.printDebugLog(SettingsFragment::class.java, "onRewarded()")
            themeChangeCompletedCallback?.execute(Unit)
            themeChangeCompletedCallback = null
            dataManager.setCurrentTheme(nextThemeIndex)
            bus.post(ChagneThemeEvent(nextThemeIndex))
        }

        override fun onRewardedVideoStarted() {
            LogUtils.printDebugLog(SettingsFragment::class.java, "onRewardedVideoStarted()")
        }

        override fun onRewardedVideoAdFailedToLoad(p0: Int) {
            LogUtils.printDebugLog(SettingsFragment::class.java, "onRewardedVideoAdFailedToLoad()")
            loadAdVideo()
            //Toast.makeText(this@SettingsFragment.context, R.string.settings_fail_to_load_ad, Toast.LENGTH_SHORT).show()
        }
    }

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((activity.application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        buildComponent().inject(this)
        binding?.settingsViewModel = viewModel

        bus.register(this)

        init()

        return binding?.root
    }

    fun init() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        rewardedVideoAd?.rewardedVideoAdListener = rewardedVideoAdListener
        loadAdVideo()

        viewModel.version.set(ContextUtils.getVersion(activity))
    }

    fun loadAdVideo() {
        rewardedVideoAd?.loadAd(getString(R.string.test_ad_rewarded_video), AdRequest.Builder().build())
    }

    override fun onDestroy() {
        super.onDestroy()
        rewardedVideoAd?.destroy(this@SettingsFragment.context)
        bus.unregister(this)
        viewModel.release()
    }

    override fun onResume() {
        super.onResume()
        rewardedVideoAd?.resume(this@SettingsFragment.context)
    }

    override fun onPause() {
        super.onPause()
        rewardedVideoAd?.pause(this@SettingsFragment.context)
    }

    @Subscribe
    fun answerStartRewardedAdEvent(event: StartRewardedAdEvent) {
        if(rewardedVideoAd?.isLoaded ?: false) {
            nextThemeIndex = event.themeNo
            themeChangeCompletedCallback = event.onComplete
            rewardedVideoAd?.show()
        } else {
            Toast.makeText(this@SettingsFragment.context, R.string.settings_ad_video_not_load_yet, Toast.LENGTH_SHORT).show()
        }
    }

    @Subscribe
    fun answerShareToFriends(event: ShareToFriendsEvent) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_see_alarm_app) + " http://play.google.com/store/apps/details?id=" + context.packageName)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, getString(R.string.settings_share_app)))
    }
}