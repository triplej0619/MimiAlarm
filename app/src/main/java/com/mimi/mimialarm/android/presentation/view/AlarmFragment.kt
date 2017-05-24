package com.mimi.mimialarm.android.presentation.view

import android.databinding.DataBindingUtil
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.presentation.CustomRecyclerViewAdapter
import com.mimi.mimialarm.android.presentation.CustomRecyclerViewHolder
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
    var viewModel: AlarmViewModel? = AlarmViewModel()

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)
        initListView()
        return binding?.root
    }

    fun initListView() {
        binding?.list?.layoutManager = LinearLayoutManager(activity)
        binding?.list?.adapter = AlarmListAdapter(viewModel?.alarmList, R.layout.list_item_alarm)

    }

    protected inner class AlarmListAdapter(items: List<AlarmListItemViewModel>?, layoutId: Int) : CustomRecyclerViewAdapter<AlarmListItemViewModel>(items, layoutId) {

        override fun setViewModel(holder: CustomRecyclerViewHolder, item: AlarmListItemViewModel) {
            if(holder.binding is ListItemAlarmBinding) {
                holder.binding.viewModel = item
            }
        }
    }
}