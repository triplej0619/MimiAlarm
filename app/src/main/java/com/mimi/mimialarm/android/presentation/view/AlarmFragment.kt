package com.mimi.mimialarm.android.presentation.view

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.infrastructure.AddAlarmEvent
import com.mimi.mimialarm.android.presentation.*
import com.mimi.mimialarm.android.utils.ActivityRequestCode
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmListItemViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmViewModel
import com.mimi.mimialarm.databinding.FragmentAlarmBinding
import com.mimi.mimialarm.databinding.ListItemAlarmBinding
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class AlarmFragment : Fragment() {

    private var listAdapter: AlarmListAdapter? = null
    var binding: FragmentAlarmBinding? = null
    @Inject
    lateinit var viewModel: AlarmViewModel
    @Inject
    lateinit var bus: Bus

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder().applicationComponent((activity.application as MimiAlarmApplication).component).viewModelModule(ViewModelModule()).build()
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when(requestCode) {
//            ActivityRequestCode.ALARM_DETAIL.code -> {
//                if(resultCode == Activity.RESULT_OK) {
//                    viewModel.loadAlarmList()
//                    listAdapter?.addItems()
//                    binding?.list?.smoothScrollToPosition(100)
//                }
//            }
//        }
//    }

    fun initListView() {
        binding?.list?.layoutManager = LinearLayoutManager(activity)
        listAdapter = AlarmListAdapter(viewModel.alarmList, R.layout.list_item_alarm, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
                viewModel.clickListItem(pos)
            }
        })
        binding?.list?.adapter = listAdapter
    }

    protected inner class AlarmListAdapter(items: List<AlarmListItemViewModel>?, layoutId: Int, itemClickEvent: IListItemClick)
        : CustomRecyclerViewAdapter<AlarmListItemViewModel>(items, layoutId, itemClickEvent) {

        override fun setViewModel(holder: CustomRecyclerViewHolder, item: AlarmListItemViewModel) {
            if(holder.binding is ListItemAlarmBinding) {
                holder.binding.alarmListItemViewModel = item
            }
        }
    }

    @Subscribe
    fun answerAddAlarmEvent(event: AddAlarmEvent) {
        viewModel.loadAlarmList()
        listAdapter?.addItem(listAdapter!!.itemCount - 1)
        binding?.list?.smoothScrollToPosition(listAdapter!!.itemCount - 1)
    }
}