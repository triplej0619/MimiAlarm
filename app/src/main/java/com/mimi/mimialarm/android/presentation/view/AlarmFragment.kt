package com.mimi.mimialarm.android.presentation.view

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mimi.mimialarm.R
import com.mimi.mimialarm.core.infrastructure.AddAlarmEvent
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
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.UpdateAlarmEvent
import java.util.*


/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class AlarmFragment : LifecycleFragment() {

    val MINUTE: Int = 60

    private var listAdapter: AlarmListAdapter? = null
    var binding: FragmentAlarmBinding? = null
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
                listAdapter?.clear()
            }

        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)
        buildComponent().inject(this)
        binding?.alarmViewModel = viewModel

        bus.register(this)
        viewModel.init()
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

//        event.id?.let { startAlarm(event.id!!) }
        // TODO 문구 리소스 화
        var minute: Long = (event.seconds / MINUTE) % 60
        val hour: Long = (event.seconds / MINUTE) / 60
        if(event.seconds % MINUTE > 0) minute += 1
        Toast.makeText(context, String.format("%s시간 %s분 후 알람이 울립니다.", hour, minute), Toast.LENGTH_SHORT).show()
    }

    @Subscribe
    fun answerUpdateAlarmEvent(event: UpdateAlarmEvent) {
        // TODO 문구 리소스 화
        var minute: Long = (event.seconds / MINUTE) % 60
        val hour: Long = (event.seconds / MINUTE) / 60
        if(event.seconds % MINUTE > 0) minute += 1
        Toast.makeText(context, String.format("%s시간 %s분 후 알람이 울립니다.", hour, minute), Toast.LENGTH_SHORT).show()
    }

//    @Subscribe
//    fun answerChangeAlarmStatusEvent(event: ChangeAlarmStatusEvent) {
//        if(event.activation) {
//            startAlarm(event.id)
//        } else {
//            stopAlarm(event.id)
//        }
//    }

//    fun startAlarm(id: Int) {
//        val alarm: MyAlarm = dbManager.findAlarmWithId(id) ?: return
//        val time: Long = TimeCalculator.getMilliSecondsForScheduling(alarm)
//        alarmManager.startAlarm(id, time)

//         TODO 문구 리소스 화
//        var minute: Long = (time / MINUTE) % 60
//        val hour: Long = (time / MINUTE) / 60
//        if(time % MINUTE > 0) minute += 1
//        Toast.makeText(context, String.format("%s시간 %s분 후 알람이 울립니다.", hour, minute), Toast.LENGTH_SHORT).show()
//    }

//    fun stopAlarm(id: Int) {
//        alarmManager.cancelAlarm(id)
//    }
}