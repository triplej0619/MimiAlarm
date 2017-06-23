package com.mimi.mimialarm.android.presentation.view

import android.app.Activity
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.mimi.mimialarm.R
import com.mimi.mimialarm.android.infrastructure.AddTimerEvent
import com.mimi.mimialarm.android.presentation.*
import com.mimi.mimialarm.core.presentation.viewmodel.TimerListItemViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.TimerViewModel
import com.mimi.mimialarm.databinding.FragmentTimerBinding
import com.mimi.mimialarm.databinding.ListItemTimerBinding
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class TimerFragment : LifecycleFragment() {

    private var listAdapter: TimerListAdapter? = null
    var binding: FragmentTimerBinding? = null
    @Inject
    lateinit var viewModel: TimerViewModel
    @Inject
    lateinit var bus: Bus

    fun buildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
                .applicationComponent((activity.application as MimiAlarmApplication).component)
                .viewModelModule(ViewModelModule())
                .build()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.timerListLive.observe(this, object : Observer<ArrayList<TimerListItemViewModel>> {
            override fun onChanged(t: ArrayList<TimerListItemViewModel>?) {
                listAdapter?.clear()
            }

        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)
        buildComponent().inject(this)
        binding?.timerViewModel = viewModel

        bus.register(this)
        initListView()

        return binding?.root
    }

    fun initListView() {
        binding?.list?.layoutManager = LinearLayoutManager(activity)
        listAdapter = TimerListAdapter(viewModel.timerList, R.layout.list_item_timer, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
//                viewModel.clickListItem(pos)
            }
        }, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
//                viewModel.startDeleteModeCommand.execute(Unit)
            }
        })

        binding?.list?.adapter = listAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bus.unregister(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTimerList()
    }

    protected inner class TimerListAdapter(items: List<TimerListItemViewModel>?, layoutId: Int, itemClickEvent: IListItemClick, longClick: IListItemClick)
        : CustomRecyclerViewAdapter<TimerListItemViewModel>(items, layoutId, itemClickEvent, longClick) {

        override fun setViewModel(holder: CustomRecyclerViewHolder, item: TimerListItemViewModel) {
            if(holder.binding is ListItemTimerBinding) {
                holder.binding.timerListItemViewModel = item
            }
        }
    }

    @Subscribe
    fun answerAddTimerEvent(event: AddTimerEvent) {
        binding?.list?.smoothScrollToPosition(listAdapter!!.itemCount - 1)
        keyboardHide()
    }

    fun keyboardHide() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding?.list?.windowToken, 0)
//        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}
