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

package com.ring.coroutines.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ring.coroutines.main.MainNetwork
import com.ring.coroutines.main.Title
import com.ring.coroutines.main.TitleDao
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fake [TitleDao] for use in tests.
 */
class TitleDaoFake(initialTitle: String) : TitleDao {
    /**
     * A channel is a Coroutines based implementation of a blocking queue.
     *
     * We're using it here as a buffer of inserted elements.
     *
     * This uses a channel instead of a list to allow multiple threads to call insertTitle and
     * synchronize the results with the test thread.
     */
    private val insertedForNext = Channel<Title>(capacity = Channel.BUFFERED)

    override suspend fun insertTitle(title: Title) {
        insertedForNext.trySend(title)
        _titleLiveData.value = title
    }

    private val _titleLiveData = MutableLiveData<Title?>(Title(initialTitle))

    override val titleLiveData: LiveData<Title?>
        get() = _titleLiveData

    /**
     * Assertion that the next element inserted has a title of expected
     *
     * If the element was previously inserted and is currently the most recent element
     * this assertion will also match. This allows tests to avoid synchronizing calls to insert
     * with calls to assertNextInsert.
     *
     * If multiple items were inserted, this will always match the first item that was not
     * previously matched.
     *
     * @param expected the value to match
     * @param timeout duration to wait (this is provided for instrumentation tests that may run on
     *                multiple threads)
     * @param unit timeunit
     * @return the next value that was inserted into this dao, or null if none found
     */
    fun nextInsertedOrNull(timeout: Long = 2_000): String? {
        var result: String? = null
        runBlocking {
            // wait for the next insertion to complete
            try {
                withTimeout(timeout) {
                    result = insertedForNext.receive().title
                }
            } catch (ex: TimeoutCancellationException) {
                // ignore
            }
        }
        return result
    }
}

/**
 * Testing Fake implementation of MainNetwork
 */
class MainNetworkFake(var result: String) : MainNetwork {
    override suspend fun fetchNextTitle(): String = result
}

/**
 * Testing Fake for MainNetwork that lets you complete or error all current requests
 */
class MainNetworkCompletableFake() : MainNetwork {
    // CompletableDeferred : public function을 이용해 완료하거나 취소할 수 있는 Deferred
    private var completable = CompletableDeferred<String>()

    // await : 결과가 설정될 때까지 대기
    override suspend fun fetchNextTitle(): String = completable.await()

    fun sendCompletionToAllCurrentRequests(result: String) {
        // complete : 수동 완료
        completable.complete(result)
        completable = CompletableDeferred()
    }

    fun sendErrorToCurrentRequests(throwable: Throwable) {
        // 예외와 함께 완료
        completable.completeExceptionally(throwable)
        completable = CompletableDeferred()
    }

}

/**
 * This class only exists to make the starter code compile. Remove after refactoring retrofit to use
 * suspend functions.
 */
class FakeCallForRetrofit<T> : Call<T> {
    override fun enqueue(callback: Callback<T>) {
        // nothing
    }

    override fun isExecuted() = false

    override fun clone(): Call<T> {
        return this
    }

    override fun isCanceled() = true

    override fun cancel() {
        // nothing
    }

    override fun execute(): Response<T> {
        TODO("Not implemented")
    }

    override fun request(): Request {
        TODO("Not implemented")
    }

    override fun timeout(): Timeout {
        TODO("Not yet implemented")
    }

}