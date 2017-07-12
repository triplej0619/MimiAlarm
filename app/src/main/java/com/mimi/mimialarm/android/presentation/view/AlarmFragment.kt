package com.mimi.mimialarm.android.presentation.view

import android.app.AlarmManager
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.infrastructure.AddAlarmEvent
import com.mimi.mimialarm.android.infrastructure.BackPressedEvent
import com.mimi.mimialarm.android.presentation.*
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmListItemViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmViewModel
import com.mimi.mimialarm.core.utils.Enums
import com.mimi.mimialarm.databinding.FragmentAlarmBinding
import com.mimi.mimialarm.databinding.ListItemAlarmBinding
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import javax.inject.Inject
import android.os.Build
import android.app.PendingIntent
import com.mimi.data.DBManager
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.infrastructure.service.AlarmOnBroadcastReceiver
import java.util.*


/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class AlarmFragment : LifecycleFragment() {

    private var listAdapter: AlarmListAdapter? = null
    var binding: FragmentAlarmBinding? = null
    @Inject
    lateinit var viewModel: AlarmViewModel
    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var dbManager: DBManager

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((activity.application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.alarmListLive.observe(this, object : Observer<ArrayList<AlarmListItemViewModel>> {
            override fun onChanged(t: ArrayList<AlarmListItemViewModel>?) {
                listAdapter?.clear()
            }

        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)
        buildComponent().inject(this)
        binding?.alarmViewModel = viewModel

        bus.register(this)
        initListView()

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        bus.unregister(this)
        viewModel.release()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAlarmList()
    }

    fun initListView() {
        binding?.list?.layoutManager = LinearLayoutManager(activity)
        listAdapter = AlarmListAdapter(viewModel.alarmList, R.layout.list_item_alarm, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
                viewModel.clickListItem(pos)
            }
        }, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
                viewModel.startDeleteModeCommand.execute(Unit)
            }
        })

        binding?.list?.adapter = listAdapter
//        binding?.list?.setOnLongClickListener(object : View.OnLongClickListener {
//            override fun onLongClick(v: View?): Boolean {
//                viewModel.startDeleteModeCommand.execute(Unit)
//                return true
//            }
//        })
    }

    private inner class AlarmListAdapter(items: List<AlarmListItemViewModel>?, layoutId: Int, itemClickEvent: IListItemClick, longClick: IListItemClick)
        : CustomRecyclerViewAdapter<AlarmListItemViewModel>(items, layoutId, itemClickEvent, longClick) {

        override fun setViewModel(holder: CustomRecyclerViewHolder, item: AlarmListItemViewModel) {
            if(holder.binding is ListItemAlarmBinding) {
                holder.binding.alarmListItemViewModel = item
            }
        }
    }

    @Subscribe
    fun answerBackPressed(event: BackPressedEvent) {
        if(event.tabType == Enums.MAIN_TAB.MAIN_ALARM) {
            if (viewModel.deleteMode.get()) {
                viewModel.cancelDeleteModeCommand.execute(Unit)
            } else {
                event.callback.execute(Unit)
            }
        }
    }

    @Subscribe
    fun answerAddAlarmEvent(event: AddAlarmEvent) {
        viewModel.loadAlarmList()
        listAdapter?.addItem(listAdapter!!.itemCount - 1)
        binding?.list?.smoothScrollToPosition(listAdapter!!.itemCount - 1)

        event.id?.let { startAlarm(event.id!!) }
    }

    fun startAlarm(id: Int) {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmOnBroadcastReceiver::class.java)
        alarmIntent.putExtra(AlarmOnBroadcastReceiver.KEY_ALARM_ID, id)
        val pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, 0)

        val alarm: MyAlarm = dbManager.findAlarmWithId(id) ?: return
        val time: Long = alarm.completedAt?.time?.minus(Date().time) ?: 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent)
        }
    }
}