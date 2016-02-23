/*
 * Copyright (c) 2014-2016 by its authors. Some rights reserved.
 * See the project homepage at: https://monix.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package monix.streams.internal.operators2

import monix.streams.Observable
import monix.streams.observers.Subscriber

import scala.concurrent.duration.FiniteDuration

private[streams] final class DelaySubscriptionByTimespanObservable[A]
  (source: Observable[A], timespan: FiniteDuration)
  extends Observable[A] {

  def unsafeSubscribeFn(subscriber: Subscriber[A]): Unit = {
    subscriber.scheduler.scheduleOnce(timespan.length, timespan.unit,
      new Runnable {
        def run(): Unit =
          source.unsafeSubscribeFn(subscriber)
      })
  }
}