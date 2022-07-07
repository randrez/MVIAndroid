package com.scgts.sctrace.di

import com.scgts.sctrace.DistributeRepository
import com.scgts.sctrace.DistributeRepositoryImpl
import com.scgts.sctrace.NetworkReceiver
import com.scgts.sctrace.ScTraceDistributeListener
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.root.RootViewModel
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { NetworkReceiver() } bind NetworkChangeListener::class

    viewModel { RootViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    single<DistributeRepository> { DistributeRepositoryImpl(Schedulers.io()) }

    single<ScTraceDistributeListener> { ScTraceDistributeListener(get()) }
}
