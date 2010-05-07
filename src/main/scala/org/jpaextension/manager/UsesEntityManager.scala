/*
 * Copyright Christopher Schmidt
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

package org.jpaextension.manager


/**
 * closures for Transaction management
 */
trait UsesEntityManager extends EntityManagerWrapper {

  /**
   * creates transaction and executes a commit
   */
  def withTrxAndCommit[T](f: => T): T = {

    // no nested transactions
    var trxStartedHere = !em.getTransaction.isActive
    if (trxStartedHere)
      em.getTransaction.begin

    try {
      val ret = f
      if (trxStartedHere)
        em.getTransaction.commit
      ret
    }
    catch {
      case t: Throwable => {
        try {
          em.getTransaction.rollback
        }
        catch {
          case _ =>
        }
        throw t
      }
    }
  }

  /**
   * creates transaction and executes a rollback
   */
  def withTrxAndRollback[T](f: => T): T = {

    // no nested transactions
    var trxStartedHere = !em.getTransaction.isActive
    if (trxStartedHere)
      em.getTransaction.begin

    val ret =
    try {
      f
    }
    finally {
      try {
        if (trxStartedHere)
          em.getTransaction.rollback
      }
      catch {
        case _ =>
      }
    }
    ret
  }

  /**
   * simply executes f
   */
  def withNoTrx[T](f: => T): T = f
}