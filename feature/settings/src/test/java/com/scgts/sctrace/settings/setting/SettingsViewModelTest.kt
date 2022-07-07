package com.scgts.sctrace.settings.setting

import com.scgts.sctrace.base.model.User
import com.scgts.sctrace.feature.settings.ui.SettingsMvi.Intent
import com.scgts.sctrace.feature.settings.ui.SettingsMvi.ViewState
import com.scgts.sctrace.feature.settings.ui.SettingsViewModel
import com.scgts.sctrace.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private var settingsIntents = PublishSubject.create<Intent>()
    private val mockUserRepository: UserRepository = mockk()
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUpViewModel() {
        viewModel = SettingsViewModel(
            mockk(relaxed = true),
            mockk(relaxed = true),
            userRepository = mockUserRepository
        )

        testScheduler = TestScheduler()
        every { mockUserRepository.getUser() } returns Single.just(getUserTest()).toObservable()
    }

    @Test
    fun setUser() {
        val expectedViewState = ViewState(
            name = getUserTest().name.toString(),
            email = getUserTest().email.toString()
        )

        val testObserver = viewModel.bind(settingsIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        testScheduler.triggerActions()

        testObserver.assertValueCount(2)

        assertEquals(expectedViewState.name, testObserver.values().last().name)
        assertEquals(expectedViewState.email, testObserver.values().last().email)
    }

    @Test
    fun setUserFail() {
        val expectedViewState = ViewState(
            name = "",
            email = ""
        )

        val testObserver = viewModel.bind(settingsIntents.hide())
            .subscribeOn(testScheduler)
            .observeOn(testScheduler)
            .test()

        testObserver.assertValueCount(0)

        testScheduler.triggerActions()

        testObserver.assertValueCount(2)

        assertFalse(expectedViewState.name == testObserver.values().last().name)
        assertFalse(expectedViewState.email == testObserver.values().last().email)
    }

    private fun getUserTest(): User {
        return User(
            id = "id",
            name = "name",
            email = "email"
        )
    }
}