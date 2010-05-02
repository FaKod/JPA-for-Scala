package com.jpaextension.manager

import com.jpaextension.exception.JPAExtensionException
import com.jpaextension.filter.{FilterFactory, QueryId}
import collection.mutable.Buffer
import javax.persistence.Query
import collection.JavaConversions
import FilterFactory._

/**
 * User: FaKod
 * Date: 02.05.2010
 * Time: 11:45:47
 */

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
    protected def fillParamAndExec[T](q: Query, param: AnyRef*): Buffer[T] = {
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
      withQuery(getJPQL(query), param: _*)

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
      withNativeQuery(getJPQL(query), param: _*)

    /**
     * doing Query from Filter Object
     */
    import JavaConversions._
    def withQuery(filter: AnyRef): A = {
      val q = createFilterQuery(filter)
      val res = q.getResultList.asInstanceOf[java.util.List[T]]
      if (res.isEmpty)
        throw new JPAExtensionException("Filter with Query: " + getQueryId(filter) + " has no results")
      body(res.head)
    }
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
      withQuery(getJPQL(query), param: _*)

    /**
     * doing Query from Filter Object
     */
    import JavaConversions._
    def withQuery(filter: AnyRef): Unit = {
      val q = createFilterQuery(filter)
      val res = q.getResultList.asInstanceOf[java.util.List[T]]
      res.foreach {
        x => body(x)
      }
    }
  }
} 