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

package monix.cats.tests

import cats._
import cats.data.{Xor, XorT}
import cats.laws.discipline.BimonadTests
import cats.laws.discipline.CartesianTests.Isomorphisms
import cats.laws.{BimonadLaws, MonadErrorLaws}
import monix.cats.Evaluable
import org.scalacheck.Arbitrary

import scala.language.higherKinds

trait EvaluableTests[F[_]] extends DeferrableTests[F] with BimonadTests[F]  {
  def laws: MonadErrorLaws[F, Throwable] with BimonadLaws[F]

  def evaluable[A: Arbitrary: Eq, B: Arbitrary: Eq, C: Arbitrary: Eq](implicit
    ArbFA: Arbitrary[F[A]],
    ArbFB: Arbitrary[F[B]],
    ArbFC: Arbitrary[F[C]],
    ArbFAtoB: Arbitrary[F[A => B]],
    ArbFBtoC: Arbitrary[F[B => C]],
    ArbE: Arbitrary[Throwable],
    ArbFFA: Arbitrary[F[F[A]]],
    EqFA: Eq[F[A]],
    EqFB: Eq[F[B]],
    EqFC: Eq[F[C]],
    EqE: Eq[Throwable],
    EqFXorEU: Eq[F[Throwable Xor Unit]],
    EqFXorEA: Eq[F[Throwable Xor A]],
    EqXorTFEA: Eq[XorT[F, Throwable, A]],
    EqFABC: Eq[F[(A, B, C)]],
    EqFFA: cats.Eq[F[F[A]]],
    EqFFFA: Eq[F[F[F[A]]]],
    iso: Isomorphisms[F]
  ): RuleSet = {
    new RuleSet {
      val name = "evaluable"
      val bases = Nil
      val parents = Seq(deferrable[A,B,C], bimonad[A,B,C])
      val props = Seq.empty
    }
  }
}

object EvaluableTests {
  type Laws[F[_]] = DeferrableTests.Laws[F] with BimonadLaws[F]

  def apply[F[_] : Evaluable]: EvaluableTests[F] = {
    val ev = implicitly[Evaluable[F]]

    new EvaluableTests[F] {
      def laws: Laws[F] =
        new MonadErrorLaws[F, Throwable] with BimonadLaws[F] {
          implicit override def F: Evaluable[F] = ev
        }
    }
  }
}