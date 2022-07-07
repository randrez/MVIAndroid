package com.scgts.sctrace.app

import com.scgts.sctrace.DistributeRepository
import com.scgts.sctrace.DistributeRepositoryImpl
import com.scgts.sctrace.UpdateStatus
import com.scgts.sctrace.base.auth.AuthManager
import com.scgts.sctrace.base.model.AuthState
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.network.NetworkChangeListener
import com.scgts.sctrace.queue.QueueRepository
import com.scgts.sctrace.root.RootMvi
import com.scgts.sctrace.root.RootViewModel
import com.scgts.sctrace.tasks.ProjectsRepository
import com.scgts.sctrace.tasks.TasksRepository
import util.TopToastManager
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class RootViewModelTest {
    private lateinit var model: RootViewModel
    private lateinit var intents: PublishSubject<RootMvi.Intent>
    private lateinit var navigator: AppNavigator
    private lateinit var authManager: AuthManager
    private lateinit var networkChangeListener: NetworkChangeListener
    private lateinit var tasksRepository: TasksRepository
    private lateinit var queueRepository: QueueRepository
    private lateinit var projectsRepository: ProjectsRepository
    private lateinit var distributeRepository: DistributeRepository
    private lateinit var topToastManager: TopToastManager
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setup() {
        intents = PublishSubject.create<RootMvi.Intent>()
        navigator = mockk(relaxed = true)
        authManager = mockk()
        networkChangeListener = mockk(relaxed = true)
        tasksRepository = mockk(relaxed = true)
        projectsRepository = mockk(relaxed = true)
        distributeRepository = mockk(relaxed = true)
        queueRepository = mockk(relaxed = true)
        topToastManager = mockk(relaxed = true)
        testScheduler = TestScheduler()
    }

    @Test
    fun doesDistributeEmitTwice() {
        //setup
        model = RootViewModel(navigator, authManager, networkChangeListener, tasksRepository,
            queueRepository, projectsRepository, distributeRepository, topToastManager)

        val testObs = Observable.just(AuthState.LOGGED_IN)

        every { authManager.authStateObs() } returns testObs
        every { distributeRepository.setStatus(any()) } returns Unit
        every { distributeRepository.getStatus() } returns Observable.just(UpdateStatus.NO_UPDATE)

        //execute

        val testObserver = model.bind(intents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testScheduler.triggerActions()
        distributeRepository.setStatus(UpdateStatus.UPDATE)


        //verify
        verify { distributeRepository.getStatus() }
        verify { authManager.authStateObs() }
        verify { navigator.clearStack() }
    }

    @Test
    fun doesDistributeEmitTwiceNegative() {
        //setup
        model = RootViewModel(navigator, authManager, networkChangeListener, tasksRepository,
            queueRepository, projectsRepository, distributeRepository, topToastManager)

        val testObs = Observable.just(AuthState.LOGGED_IN)

        every { authManager.authStateObs() } returns testObs
        every { distributeRepository.setStatus(any()) } returns Unit
        every { distributeRepository.getStatus() } returns Observable.just(UpdateStatus.UPDATE)
        every { networkChangeListener.isConnected() } returns true

        //execute

        val testObserver = model.bind(intents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testScheduler.triggerActions()
        //distributeRepository.setStatus(UpdateStatus.UPDATE)


        //verify
        verify { distributeRepository.getStatus() }
        verify(exactly = 1) { authManager.authStateObs() }
        verify { navigator.clearStack() wasNot Called }
    }

    @Test
    fun doesDistributeEmitTwiceUpdate() {
        //setup
        val tester2 = TestScheduler()
        val testDistRepo = DistributeRepositoryImpl(tester2)
        model = RootViewModel(navigator, authManager, networkChangeListener, tasksRepository,
            queueRepository, projectsRepository, testDistRepo, topToastManager)
        val testObs = Observable.just(AuthState.LOGGED_IN)
        every { authManager.authStateObs() } returns testObs
        every { distributeRepository.setStatus(any()) } returns Unit
        every { networkChangeListener.isConnected() } returns true

        //execute

        val testObserver = model.bind(intents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()
        testScheduler.triggerActions()

        testDistRepo.setStatus(UpdateStatus.NO_UPDATE)
        tester2.triggerActions()
        tester2.advanceTimeBy(4, TimeUnit.SECONDS)
        testDistRepo.setStatus(UpdateStatus.NO_UPDATE)
        tester2.triggerActions()
        tester2.advanceTimeBy(4, TimeUnit.SECONDS)
        //verify

        verify(exactly = 3) { authManager.authStateObs() }
        verify { navigator.clearStack() }
    }
}