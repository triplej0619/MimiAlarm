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
import com.mimi.data.RealmDataUtils
import com.mimi.data.model.MyAlarm
import com.mimi.mimialarm.android.infrastructure.ChangePageEvent
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.infrastructure.AlarmManager
import com.mimi.mimialarm.core.infrastructure.UpdateAlarmEvent
import com.mimi.mimialarm.core.model.DataMapper
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import java.util.*
import kotlin.properties.Delegates


/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class AlarmFragment : LifecycleFragment() {

    val MINUTE: Int = 60

    private var listAdapter: AlarmListAdapter? = null
    var binding: FragmentAlarmBinding? = null
    var realm: Realm by Delegates.notNull<Realm>()
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
//        viewModel.alarmListLive.observe(this, object : Observer<ArrayList<AlarmListItemViewModel>> {
//            override fun onChanged(t: ArrayList<AlarmListItemViewModel>?) {
//                listAdapter?.clear()
//            }
//
//        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)
        buildComponent().inject(this)
        binding?.alarmViewModel = viewModel

        realm = Realm.getDefaultInstance()
        bus.register(this)
        initListView()

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        bus.unregister(this)
        viewModel.release()
        realm.close()
    }

    override fun onResume() {
        super.onResume()
//        viewModel.loadAlarmList()
    }

    fun initListView() {
        binding?.list?.layoutManager = LinearLayoutManager(activity)
        listAdapter = AlarmListAdapter(RealmDataUtils.findObjects<MyAlarm>(realm)
                                        , R.layout.list_item_alarm
                                        , object : IListItemClick {
                                            override fun clickEvent(v: View, pos: Int) {
                                                viewModel.clickListItem(pos)
                                            }
                                        }, object : IListItemClick {
                                            override fun clickEvent(v: View, pos: Int) {
                                                viewModel.startDeleteModeCommand.execute(Unit)
                                            }
                                        })
//        listAdapter = AlarmListAdapter(viewModel.alarmList, R.layout.list_item_alarm, object : IListItemClick {
//            override fun clickEvent(v: View, pos: Int) {
//                viewModel.clickListItem(pos)
//            }
//        }, object : IListItemClick {
//            override fun clickEvent(v: View, pos: Int) {
//                viewModel.startDeleteModeCommand.execute(Unit)
//            }
//        })

        binding?.list?.adapter = listAdapter
    }

//    private inner class AlarmListAdapter(items: List<AlarmListItemViewModel>?, layoutId: Int, itemClickEvent: IListItemClick, longClick: IListItemClick)
//        : CustomRecyclerViewAdapter<AlarmListItemViewModel>(items, layoutId, itemClickEvent, longClick) {
//
//        override fun setViewModel(holder: CustomRecyclerViewHolder, item: AlarmListItemViewModel) {
//            if(holder.binding is ListItemAlarmBinding) {
//                holder.binding.alarmListItemViewModel = item
//            }
//        }
//    }

    private inner class AlarmListAdapter(data: OrderedRealmCollection<MyAlarm>, val layoutId: Int, val itemClick: IListItemClick, val longClick: IListItemClick)
        : RealmRecyclerViewAdapter<MyAlarm, CustomRecyclerViewHolder>(data, true) {

//        var longClick: IListItemClick by Delegates.notNull<IListItemClick>()
//        var itemClick: IListItemClick by Delegates.notNull<IListItemClick>()

//        constructor(data: OrderedRealmCollection<MyAlarm>, layoutId: Int, itemClick: IListItemClick, longClick: IListItemClick) : this(data, layoutId) {
//            this.itemClick = itemClick
//            this.longClick = longClick
//            setHasStableIds(true)
//        }

        init {
            setHasStableIds(true)
        }

        override fun onBindViewHolder(holder: CustomRecyclerViewHolder?, position: Int) {
            val item = data!![position]
            setViewModel(holder!!, DataMapper.alarmToListItemViewModel(item, bus))
        }

        fun setViewModel(holder: CustomRecyclerViewHolder, item: AlarmListItemViewModel) {
                        if(holder.binding is ListItemAlarmBinding) {
                holder.binding.alarmListItemViewModel = item
            }
        }

        override fun getItemCount(): Int {
            val count = super.getItemCount()
            viewModel.alarmCount.set(count)
            return count
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomRecyclerViewHolder {
            val itemView = LayoutInflater.from(parent!!.context).inflate(layoutId, parent, false)

            return CustomRecyclerViewHolder(itemView, itemClick, longClick)
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
        LogUtils.printDebugLog(this@AlarmFragment.javaClass, "answerAddAlarmEvent()")
//        viewModel.loadAlarmList()
//        listAdapter?.addItem(listAdapter!!.itemCount - 1)
        binding?.list?.smoothScrollToPosition(listAdapter!!.itemCount - 1)

//        event.id?.let { startAlarm(event.id!!) }
        val minute: Long = (event.seconds / MINUTE) % 60
        val hour: Long = (event.seconds / MINUTE) / 60
        Toast.makeText(context, getAlarmScheduleString(hour, minute), Toast.LENGTH_SHORT).show()
    }

    fun getAlarmScheduleString(hour: Long, minute: Long) : String{
        var text: String = ""

        if(hour > 0 || minute > 0) {
            if (hour > 0) {
                text = hour.toString() + getString(R.string.hour)
            }
            if (minute > 0) {
                text += " " + minute.toString() + getString(R.string.minute)
            }
            text += " " + getString(R.string.msg_add_alarm_over_1min)
        } else {
            text = String.format("1%s %s", getString(R.string.minute), getString(R.string.msg_add_alarm_under_1min))
        }
        return text
    }

    @Subscribe
    fun answerUpdateAlarmEvent(event: UpdateAlarmEvent) {
        val minute: Long = (event.seconds / MINUTE) % 60
        val hour: Long = (event.seconds / MINUTE) / 60
        Toast.makeText(context, getAlarmScheduleString(hour, minute), Toast.LENGTH_SHORT).show()
    }

    @Subscribe
    fun answerChangePageEvent(event: ChangePageEvent) {
        if(event.page == 0) {
            viewModel.cancelDeleteModeCommand.execute(Unit)
        }
    }
}