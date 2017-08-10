package com.mimi.mimialarm.android.presentation.view

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.mimi.data.DBManager
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.*
import com.mimi.mimialarm.android.utils.ContextUtil
import com.mimi.mimialarm.android.utils.LogUtil
import com.mimi.mimialarm.core.infrastructure.ApplicationDataManager
import com.mimi.mimialarm.core.presentation.viewmodel.ActivatedAlarmListViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmListItemViewModel
import com.mimi.mimialarm.databinding.ActivityActivatedAlarmListBinding
import com.mimi.mimialarm.databinding.ListItemActivatedAlarmBinding
import com.squareup.otto.Bus
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.ArrayList
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by MihyeLee on 2017. 7. 26..
 */
class ActivatedAlarmListActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val registry = LifecycleRegistry(this)
    private lateinit var listAdapter: ActivatedAlarmListAdapter

    lateinit var binding: ActivityActivatedAlarmListBinding
    var realm: Realm by Delegates.notNull<Realm>()
    @Inject lateinit var dbManager: DBManager
    @Inject lateinit var dataManager: ApplicationDataManager
    @Inject lateinit var bus: Bus
    @Inject lateinit var viewModel: ActivatedAlarmListViewModel

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun getLifecycle(): LifecycleRegistry {
        return registry
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        buildComponent().inject(this)
        setTheme(ContextUtil.getThemeId(dataManager.getCurrentTheme()))

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_activated_alarm_list)
        binding.activatedAlarmListViewModel = viewModel

        init()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAlarmList()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        val ab = supportActionBar
        if (ab != null) {
            ab.setDisplayShowTitleEnabled(true)
            ab.setDisplayShowHomeEnabled(true)
            ab.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val viewResourceId = item.itemId
        if (viewResourceId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun init() {
        LogUtil.printDebugLog(this@ActivatedAlarmListActivity.javaClass, "init()")
        realm = Realm.getDefaultInstance()
        binding.list.layoutManager = LinearLayoutManager(this)
        listAdapter = ActivatedAlarmListAdapter(viewModel.alarmList, R.layout.list_item_activated_alarm)
//        listAdapter = ActivatedAlarmListAdapter(RealmDataUtils.findObjects<MyAlarm>(realm), R.layout.list_item_activated_alarm)
        binding.list.adapter = listAdapter

        viewModel.alarmListLive.observe(this, Observer<ArrayList<AlarmListItemViewModel>> { listAdapter.notifyDataSetChanged() })
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
        viewModel.release()
    }

        private inner class ActivatedAlarmListAdapter(items: List<AlarmListItemViewModel>?, layoutId: Int)
        : CustomRecyclerViewAdapter<AlarmListItemViewModel>(items, layoutId) {

        override fun setViewModel(holder: CustomRecyclerViewHolder, item: AlarmListItemViewModel) {
            if(holder.binding is ListItemActivatedAlarmBinding) {
                holder.binding.alarmListItemViewModel = item
            }
        }
    }

//    private inner class ActivatedAlarmListAdapter(data: OrderedRealmCollection<MyAlarm>, val layoutId: Int)
//        : RealmRecyclerViewAdapter<MyAlarm, CustomRecyclerViewHolder>(data, true) {
//
//        init {
//            viewModel.alarmCount.set(data.size)
//            setHasStableIds(true)
//        }
//
//        override fun onBindViewHolder(holder: CustomRecyclerViewHolder?, position: Int) {
//            val item = data!![position]
//            setViewModel(holder!!, DataMapper.alarmToListItemViewModel(item, bus))
//        }
//
//        fun setViewModel(holder: CustomRecyclerViewHolder, item: AlarmListItemViewModel) {
//            if(holder.binding is ListItemActivatedAlarmBinding) {
//                holder.binding.alarmListItemViewModel = item
//            }
//        }
//
//        override fun getItemCount(): Int {
//            val count = super.getItemCount()
//            viewModel.alarmCount.set(count)
//            return count
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomRecyclerViewHolder {
//            val itemView = LayoutInflater.from(parent!!.context).inflate(layoutId, parent, false)
//
//            return CustomRecyclerViewHolder(itemView, null, null)
//        }
//    }
}