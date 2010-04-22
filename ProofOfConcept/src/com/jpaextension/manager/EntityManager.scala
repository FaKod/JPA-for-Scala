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

package com.jpaextension.manager

import com.tagger.util.ScalaThreadLocal
import javax.persistence.{Query, EntityTransaction, EntityManager, Persistence}
import com.jpaextension.exception.JPAExtensionException
import collection.mutable.Buffer
import collection.JavaConversions
import com.jpaextension.filter.{FilterFactory, QueryId}


/**
 * Thread Local singleton wrapper for Entity Manager
 */
object GetEntityManager {
  private val s = new ScalaThreadLocal[EntityManager]()
  private val emf = Persistence.createEntityManagerFactory("tagger")

  def apply() = s get match {
    case Some(x) => x
    case None => s set Option(emf.createEntityManager); s.get.get
  }
}


/**
 * static Entity Manager access Methods
 */
trait EntityManagerWrapper {
  val em: EntityManager

  /**
   * persist wrapper
   */
  def persist(o: Object): Unit = em.persist(o)

  /**
   * transaction wrapper
   */
  def getTransaction(): EntityTransaction = em.getTransaction

  /**
   * create Query wrapper
   */
  def createQuery(s: String): Query = em.createQuery(s)

  /**
   * creates new filter instance
   */
  def newFilterInstance[T](queryId: String, entity: Class[_]) = null

  /**
   * creates query using filter object
   */
  def createFilterQuery(filter: AnyRef) = null

  /**
   * finds entity of class tClass by primary id o
   */
  def find[T](tClass: Class[T], o: Object): T = em.find[T](tClass, o)

  /**
   * merges the given entity into persistence context
   */
  def merge[T](entity: T): T = em.merge(entity)

  /**
   * refreshes the given entity
   */
  def refresh(entity: AnyRef) = em.refresh(entity)

  /**
   * removes the entity from DB
   */
  def remove(entity: AnyRef): Unit = em.remove(entity)

  /**
   * creating native query s with result class aClass
   */
  def createNativeQuery(s: String, aClass: Class[_]): Query = em.createNativeQuery(s, aClass)

  /**
   * creating native query s
   */
  def createNativeQuery(s: String): Query = em.createNativeQuery(s)
}


/**
 * closures for Transaction management
 */
trait UsesEntityManager extends EntityManagerWrapper {
  val em = GetEntityManager()

  /**
   * creates transaction and executes a commit
   */
  def withTrxAndCommit[T](f: => T): T = {

    // no nested transactions
    var trxStartedHere = !em.getTransaction.isActive
    if(trxStartedHere)
      em.getTransaction.begin

    try {
      val ret = f
      if(trxStartedHere)
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
    if(trxStartedHere)
      em.getTransaction.begin

    val ret =
    try {
      f
    }
    finally {
      try {
        if(trxStartedHere)
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


/**
 * query helper to ease handling of queries
 */
trait QueryHelper {
  self: UsesEntityManager =>

  /**
   * finds an entity with id, does a refresh and executes f() with it
   */
  protected def findAndApply[T, A](c: Class[T], id: AnyRef)(f: T => A): A = {
    find[T](c, id) match {
      case null => throw new JPAExtensionException("Entity: " + c + " not found with parameter: " + id)
      case u => {
        refresh(u.asInstanceOf[AnyRef])
        f(u)
      }
    }
  }

  /**
   * finds and entity with id and returns it
   */
  protected def findSimple[T](c: Class[T], id: AnyRef): T =
    findAndApply(c, id) {x => x}

  /**
   * creates a simple query for one entity and query parameter list
   */
  protected def oneResultQueryAndApply[A, T](body: (T) => A): DoWithQuery[A, T] =
    new DoWithQuery[A, T](body)

  /**
   * creates a simple query without closure
   */
  protected def oneResultQuery[A, T]: DoWithQuery[A, T] =
    new DoWithQuery[A, T]({x: T => x.asInstanceOf[A]})

  /**
   * executes query and applies body() to each element
   */
  protected def forQueryResults[T](body: (T) => Unit): DoWithForQuery[T] =
    new DoWithForQuery[T](body)

  
  /**
   * see oneResultQuery
   */
  protected class DoWithQueryBase {

    /**
     * filling parameter and calling fillParamAndExec on query
     */
    import JavaConversions._
    protected def fillParamAndExec[T](q: Query, param: AnyRef*):Buffer[T] = {
      var i: Int = 1
      param.foreach {
        x =>
          q.setParameter(i, x)
          i += 1
      }
      q.getResultList.asInstanceOf[java.util.List[T]]
    }

  }

  protected class DoWithQuery[A, T](body: (T) => A) extends DoWithQueryBase {

    /**
     * doing Query
     */
    def withQuery(query: String, param: AnyRef*): A = {
      val q = createQuery(query)

      val res = fillParamAndExec[T](q, param: _*)
      if (res.isEmpty)
        throw new JPAExtensionException("Query: " + query + " has no results")
      body(res.head)
    }

    /**
     * doing Query from JPAExtensions QueryId
     */
    def withQuery(query: QueryId, param: AnyRef*): A =
      withQuery(FilterFactory.getJPQL(query), param: _*)

    /**
     * doing native Query
     */
    def withNativeQuery(query: String, param: AnyRef*): A = {
      val q = createNativeQuery(query)

      val res = fillParamAndExec[T](q, param: _*)
      if (!res.isEmpty)
        body(res.head)
      else
        null.asInstanceOf[A]
    }

    /**
     * doing native Query from JPAExtensions QueryId
     */
    def withNativeQuery(query: QueryId, param: AnyRef*): A =
      withNativeQuery(FilterFactory.getJPQL(query), param: _*)
  }


  /**
   * see forResults
   */
  protected class DoWithForQuery[T](body: (T) => Unit) extends DoWithQueryBase {

    /**
     * doing Query
     */
    def withQuery(query: String, param: AnyRef*): Unit = {
      val q = createQuery(query)

      val res = fillParamAndExec[T](q, param: _*)
      res.foreach {
        x => body(x)
      }
    }

    /**
     * doing Query from JPAExtensions QueryId
     */
    def withQuery(query: QueryId, param: AnyRef*): Unit =
      withQuery(FilterFactory.getJPQL(query), param: _*)
  }
}