/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ring.coroutines.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ring.coroutines.fakes.MainNetworkFake
import com.ring.coroutines.fakes.TitleDaoFake
import com.ring.coroutines.main.utils.MainCoroutineScopeRule
import com.ring.coroutines.main.utils.getValueForTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    // Rule : JUnit에서 테스트를 실행하기 전과 후에 코드를 실행하는 방법
    @get:Rule
    // TestCoroutineDispatcher를 사용하도록 Dispatchers.Main을 구성한다. 단위 테스트에서 Dispatcher.Main 사용이 가능해짐
    val coroutineScope = MainCoroutineScopeRule()
    @get:Rule
    // 각 작업을 동기적으로 실행하도록 LiveData를 구성하는 JUnit 규칙
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var subject: MainViewModel
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        subject = MainViewModel(
            TitleRepository(
                MainNetworkFake("OK"),
                TitleDaoFake("initial")
            )
        )
    }

    @Test
    fun whenMainClicked_updatesTaps() {
        subject.onMainViewClicked()
        assertEquals("0 taps", subject.taps.getValueForTest())
        testScope.advanceTimeBy(1000)
        assertEquals("1 taps", subject.taps.getValueForTest())
    }
}