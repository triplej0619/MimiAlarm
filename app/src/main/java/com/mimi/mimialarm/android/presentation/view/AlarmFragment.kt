package com.mimi.mimialarm.android.presentation.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.*
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmListItemViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.AlarmViewModel
import com.mimi.mimialarm.databinding.FragmentAlarmBinding
import com.mimi.mimialarm.databinding.ListItemAlarmBinding
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class AlarmFragment : Fragment() {

    var binding: FragmentAlarmBinding? = null
    @Inject
    lateinit var viewModel: AlarmViewModel
    lateinit var activityComponent: ActivityComponent

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)
        initListView()

        activityComponent = DaggerActivityComponent.builder().build()
        activityComponent.inject(this)

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }

    fun initListView() {
        binding?.list?.layoutManager = LinearLayoutManager(activity)
        binding?.list?.adapter = AlarmListAdapter(viewModel.alarmList, R.layout.list_item_alarm, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
                viewModel.clickListItem(pos)
            }
        })
    }

    protected inner class AlarmListAdapter(items: List<AlarmListItemViewModel>?, layoutId: Int, itemClickEvent: IListItemClick)
        : CustomRecyclerViewAdapter<AlarmListItemViewModel>(items, layoutId, itemClickEvent) {

        override fun setViewModel(holder: CustomRecyclerViewHolder, item: AlarmListItemViewModel) {
            if(holder.binding is ListItemAlarmBinding) {
                holder.binding.viewModel = item
            }
        }
    }
}