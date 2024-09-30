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
import com.ring.coroutines.fakes.MainNetworkCompletableFake
import com.ring.coroutines.fakes.MainNetworkFake
import com.ring.coroutines.fakes.TitleDaoFake
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

// 코루틴에서 적용할 수 있는 test를 작성해야한다.
// 함수가 끝나기 전에 코루틴이 완료되어야한다. -> 코루틴 완료를 기다려야한다.
// `kotlinx-coroutine-test`에 `runTest`이라는 함수가 있다.
// 이 함수는 쉽게 생각하면 suspend 함수와 코루틴을 일반적인 함수로 call한다.
class TitleRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun whenRefreshTitleSuccess_insertsRows() = runTest {
        val titleDao = TitleDaoFake("title")
        val subject = TitleRepository(
            MainNetworkFake("OK"),
            titleDao
        )

        subject.refreshTitle()
        assertEquals(titleDao.nextInsertedOrNull(), "OK")
    }

    // 타임아웃 테스트
    @Test(expected = TitleRefreshError::class)
    fun whenRefreshTitleTimeout_throws() = runTest {
        val network = MainNetworkCompletableFake()
        val subject = TitleRepository(
            network,
            TitleDaoFake("title")
        )

        launch {
            subject.refreshTitle()
        }
        advanceTimeBy(5_000)
    }
}