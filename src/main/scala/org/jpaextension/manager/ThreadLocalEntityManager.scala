/*
 * Copyright 2008 Christopher Schmidt
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
 * An example would be:
 *
 * <pre>
 * object MyEM extends LocalEMF("test") with ThreadLocalEntityManager
 *
 * ...
 * MyEM.createNamedQuery(...)
 *
 * MyEM.cleanup()
 * ...
 *
 * </pre>
 *
 * Best practice for this code is to ensure that when the
 * thread exits it calls the cleanup method so that the EM is properly closed.
 */



trait ThreadLocalEntityManager extends EntityManagerWrapper with EntityManagerFactory {

  EMCache.initCache(openEM)

  def em = EMCache.get

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

private[manager] class ScalaThreadLocal[T](getInitialValue: =>(T)) extends ThreadLocal[T] {
  override protected def initialValue = getInitialValue
}

private[manager] object EMCache {
  var cache:ScalaThreadLocal[EntityManager] = _

  def initCache(f: => EntityManager) = if(cache==null) cache = new ScalaThreadLocal[EntityManager](f)

  def get = cache.get
  def remove = cache.remove
}

