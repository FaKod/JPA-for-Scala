/*
 * Copyright 2010 Christopher Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.jpaextension.manager

import javax.persistence.EntityManager

/**
 * This trait can be mixed in so that an object may provide
 * access to EntityManager instances on a thread-local basis.
 *
 * @author Christopher Schmidt
 */
trait ThreadLocalEntityManager extends EntityManagerWrapper with EntityManagerFactory {
  /**
   * special thread local implementation
   */
  class ScalaThreadLocal[T](getInitialValue: => (T)) extends ThreadLocal[T] {
    override protected def initialValue = getInitialValue
  }

  /**
   * thread local cache used by ThreadLocalEntityManager
   */
  object EMCache {
    private val cache: ScalaThreadLocal[EntityManager] = new ScalaThreadLocal[EntityManager](openEM)

    /**
     * retrieve EntityManager
     */
    def get = cache.get

    /**
     * remove EntityManager
     */
    def remove = cache.remove
  }

  /**
   * retrieve EntityManager
   * @return EntityManager
   */
  def em = EMCache.get

  /**
   * retrieve EntityManagerFactory
   * @return EntityManagerFactory
   */
  def factory = this

  /**
   * Cleans up the current thread's EntityManager by closing it and
   * removing the em from the thread-local storage.
   */
  def cleanup(): Unit = {
    try {
      closeEM(em)
    } finally {
      EMCache.remove
    }
  }
}



