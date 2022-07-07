package com.scgts.sctrace.feature.landing.filter_and_sort

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.base.model.FilterAndSortCategory.None
import com.scgts.sctrace.feature.landing.composable.filter_and_sort.FilterAndSortScreen
import com.scgts.sctrace.feature.landing.composable.filter_and_sort.TabletFilterAndSortScreen
import com.scgts.sctrace.feature.landing.filter_and_sort.FilterAndSortMvi.Intent
import com.scgts.sctrace.feature.landing.filter_and_sort.FilterAndSortMvi.Intent.*
import com.scgts.sctrace.feature.landing.filter_and_sort.FilterAndSortMvi.ViewState
import com.scgts.sctrace.framework.view.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import theme.SCGTSTheme
import util.isTablet
import util.sendErrorToDtrace

class FilterAndSortFragment : BaseFragment<FilterAndSortViewModel>() {
    private val intents = PublishSubject.create<Intent>()

    override val viewModel: FilterAndSortViewModel by viewModel()

    private val _viewState = MutableLiveData<ViewState>()
    private val viewState: LiveData<ViewState>
        get() = _viewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(intents.hide())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render) { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
            .autoDisposeOnDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SCGTSTheme {
                    if (isTablet()) TabletFilterAndSortScreen(
                        viewState = viewState,
                        onCloseClicked = { intents.onNext(CloseClicked) },
                        onClearClicked = { category -> intents.onNext(ClearClicked(category)) },
                        onCategoryClicked = { category -> intents.onNext(CategorySelected(category)) },
                        onOptionClicked = { selectedCategory, option ->
                            intents.onNext(OptionClicked(selectedCategory, option))
                        },
                        onShowTasksClicked = { intents.onNext(ShowTasksClicked) },
                    )
                    else FilterAndSortScreen(
                        viewState = viewState,
                        onBackClicked = { intents.onNext(BackClicked) },
                        onCloseClicked = { intents.onNext(CloseClicked) },
                        onClearClicked = { category -> intents.onNext(ClearClicked(category)) },
                        onCategoryClicked = { category -> intents.onNext(CategorySelected(category)) },
                        onOptionClicked = { selectedCategory, option ->
                            intents.onNext(OptionClicked(selectedCategory, option))
                        },
                        onShowTasksClicked = { intents.onNext(ShowTasksClicked) },
                    )
                }
            }
        }
    }

    private fun render(viewState: ViewState) {
        _viewState.postValue(viewState)
        if (isTablet() && viewState.selectedCategory is None) {
            intents.onNext(CategorySelected(viewState.categories.first()))
        }
    }
}