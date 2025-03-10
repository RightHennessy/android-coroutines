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

package com.ring.coroutines.util

import java.util.concurrent.Executors

/**
 * An executor service that can run [Runnable]s off the main thread.
 */

// Java의 Executors 클래스를 사용하여 만든 스레드 풀이다.
// 아래 코드는 최대 2개의 스레드를 사용하는 고정 크기 스레드 풀을 생성한다.
val BACKGROUND = Executors.newFixedThreadPool(2)
