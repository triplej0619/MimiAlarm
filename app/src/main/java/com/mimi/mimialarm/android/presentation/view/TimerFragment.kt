package com.mimi.mimialarm.android.presentation.view

import android.app.Activity
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.mimi.mimialarm.R
import com.mimi.mimialarm.core.infrastructure.AddTimerEvent
import com.mimi.mimialarm.android.infrastructure.BackPressedEvent
import com.mimi.mimialarm.android.infrastructure.ChangePageEvent
import com.mimi.mimialarm.android.presentation.*
import com.mimi.mimialarm.android.utils.LogUtils
import com.mimi.mimialarm.core.presentation.viewmodel.TimerListItemViewModel
import com.mimi.mimialarm.core.presentation.viewmodel.TimerViewModel
import com.mimi.mimialarm.core.utils.Enums
import com.mimi.mimialarm.databinding.FragmentTimerBinding
import com.mimi.mimialarm.databinding.ListItemTimerBinding
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import io.reactivex.Observable
import io.reactivex.functions.Function3
import javax.inject.Inject

/**
 * Created by MihyeLee on 2017. 5. 22..
 */
class TimerFragment : LifecycleFragment() {

    private var listAdapter: TimerListAdapter? = null
    lateinit var binding: FragmentTimerBinding
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
        binding.timerViewModel = viewModel

        bus.register(this)
        initListView()
        initEditView()

        return binding.root
    }

    fun initListView() {
        binding.list.layoutManager = LinearLayoutManager(activity)
        listAdapter = TimerListAdapter(viewModel.timerList, R.layout.list_item_timer, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
                viewModel.clickListItem(pos)
            }
        }, object : IListItemClick {
            override fun clickEvent(v: View, pos: Int) {
                viewModel.startDeleteModeCommand.execute(Unit)
            }
        })

        binding.list.adapter = listAdapter

        viewModel.loadTimerList()
    }

    fun initEditView() {
        binding.hour.setSelectAllOnFocus(true)
        binding.minute.setSelectAllOnFocus(true)
        binding.second.setSelectAllOnFocus(true)
        binding.hour.setOnClickListener{ binding.hour.selectAll() }
        binding.minute.setOnClickListener{ binding.minute.selectAll() }
        binding.second.setOnClickListener{ binding.second.selectAll() }

        Observable.combineLatest(RxTextView.textChanges(binding.hour as TextView),
                RxTextView.textChanges(binding.minute as TextView),
                RxTextView.textChanges(binding.second as TextView),
                Function3<CharSequence, CharSequence, CharSequence, Boolean> { t1, t2, t3 ->
                    var isEnabled: Boolean = false
                    if(!t1.isNullOrEmpty() && !t2.isNullOrEmpty() && !t3.isNullOrEmpty()) {
                        if(Integer.parseInt(t1.toString()) > 0 ||
                                Integer.parseInt(t2.toString()) > 0 ||
                                Integer.parseInt(t3.toString()) > 0) {
                            isEnabled = true
                        }
                    }
                    binding.addTimer.isEnabled = isEnabled
                    false
                }
            ).subscribe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bus.unregister(this)
        viewModel.release()
    }

    override fun onResume() {
        super.onResume()
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
        binding.list.smoothScrollToPosition(listAdapter!!.itemCount - 1)
        keyboardHide()
    }

    fun keyboardHide() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.list.windowToken, 0)
    }

    @Subscribe
    fun answerBackPressed(event: BackPressedEvent) {
        LogUtils.printDebugLog(this@TimerFragment.javaClass, "answerBackPressed()")
        if(event.tabType == Enums.MAIN_TAB.MAIN_TIMER) {
            if (viewModel.deleteMode.get()) {
                viewModel.cancelDeleteModeCommand.execute(Unit)
            } else {
                event.callback.execute(Unit)
            }
        }
    }

    @Subscribe

    fun answerChangePageEvent(event: ChangePageEvent) {
        if(event.page == 1) {
            viewModel.cancelDeleteModeCommand.execute(Unit)
        }
    }
}
