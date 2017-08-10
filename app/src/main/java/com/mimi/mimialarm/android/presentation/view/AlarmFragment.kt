package com.mimi.mimialarm.android.presentation.view

import android.app.NotificationManager
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mimi.mimialarm.R
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
import android.widget.Toast
import com.mimi.data.DBManager
import com.mimi.mimialarm.android.infrastructure.ChangePageEvent
import com.mimi.mimialarm.android.utils.LogUtil
import com.mimi.mimialarm.core.infrastructure.*
import com.mimi.mimialarm.core.utils.TimeCalculator
import java.util.*


/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class AlarmFragment : LifecycleFragment() {

    val MINUTE: Int = 60

    private var listAdapter: AlarmListAdapter? = null
    lateinit var binding: FragmentAlarmBinding
    @Inject lateinit var viewModel: AlarmViewModel
    @Inject lateinit var bus: Bus
    @Inject lateinit var dbManager: DBManager
    @Inject lateinit var alarmManager: AlarmManager

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
                listAdapter?.notifyDataSetChanged()
            }

        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)
        buildComponent().inject(this)
        binding.alarmViewModel = viewModel

        bus.register(this)
        initListView()

        return binding.root
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
        binding.list.layoutManager = LinearLayoutManager(activity)
        listAdapter = AlarmListAdapter(viewModel.alarmList, R.layout.list_item_alarm, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
                viewModel.clickListItem(pos)
            }
        }, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
                viewModel.startDeleteModeCommand.execute(Unit)
            }
        })

        binding.list.adapter = listAdapter
    }

    private inner class AlarmListAdapter(items: List<AlarmListItemViewModel>?, layoutId: Int, itemClickEvent: IListItemClick, longClick: IListItemClick)
        : CustomRecyclerViewAdapter<AlarmListItemViewModel>(items, layoutId, itemClickEvent, longClick) {

        override fun setViewModel(holder: CustomRecyclerViewHolder, item: AlarmListItemViewModel) {
            if(holder.binding is ListItemAlarmBinding) {
                holder.binding.alarmListItemViewModel = item
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelDeleteModeCommand.execute(Unit)
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
        LogUtil.printDebugLog(this@AlarmFragment.javaClass, "answerAddAlarmEvent()")
        viewModel.loadAlarmList()
        listAdapter?.addItem(listAdapter!!.itemCount - 1)
        binding.list.smoothScrollToPosition(listAdapter!!.itemCount - 1)

        Toast.makeText(context, getAlarmScheduleString(event.seconds), Toast.LENGTH_SHORT).show()
    }

    fun getAlarmScheduleString(second: Long) : String{
        var text: String = ""
        val hour = TimeCalculator.getHourFromSeconds(second)
        val minute = TimeCalculator.getMinuteFromSeconds(second)
        val day = TimeCalculator.getDayFromSeconds(second)

        if(hour > 0 || minute > 0 || day > 0) {
            if (day > 0) {
                text = day.toString() + getString(R.string.day) + " "
            }
            if (hour > 0) {
                text += hour.toString() + getString(R.string.hour) + " "
            }
            if (minute > 0) {
                text += minute.toString() + getString(R.string.minute) + " "
            }
            text += getString(R.string.msg_add_alarm_over_1min)
        } else {
            text = String.format("1%s %s", getString(R.string.minute), getString(R.string.msg_add_alarm_under_1min))
        }
        LogUtil.printDebugLog(this@AlarmFragment.javaClass, "getAlarmScheduleString() $text")
        return text
    }

    @Subscribe
    fun answerUpdateAlarmEvent(event: UpdateAlarmEvent) {
        LogUtil.printDebugLog(this@AlarmFragment.javaClass, "answerUpdateAlarmEvent()")
        Toast.makeText(context, getAlarmScheduleString(event.seconds), Toast.LENGTH_SHORT).show()
    }

    @Subscribe
    fun answerChangePageEvent(event: ChangePageEvent) {
        if(event.page == 0) {
            viewModel.cancelDeleteModeCommand.execute(Unit)
        }
    }

    @Subscribe
    fun answerActivateAlarmEvent(event: ActivateAlarmEvent) {
        LogUtil.printDebugLog(this@AlarmFragment.javaClass, "answerActivateAlarmEvent()")
        viewModel.showActivatedAlarmList.set(true)
    }

    @Subscribe
    fun answerDeleteAllAlarmEvent(event: DeleteAllAlarmEvent) {
        LogUtil.printDebugLog(this@AlarmFragment.javaClass, "answerDeleteAllAlarmEvent()")
        val nm: NotificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    @Subscribe
    fun answerDeleteAlarmEvent(event: DeleteAlarmEvent) {
        LogUtil.printDebugLog(this@AlarmFragment.javaClass, "answerDeleteAlarmEvent()")
        val nm: NotificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(event.id)
    }

    @Subscribe
    fun answerRefreshAlarmListEvent(event: RefreshAlarmListEvent) {
        LogUtil.printDebugLog(this@AlarmFragment.javaClass, "answerDeleteAlarmEvent()")
        activity.runOnUiThread {
            viewModel.loadAlarmList()
        }
    }

}