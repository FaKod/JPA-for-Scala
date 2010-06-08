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

import org.jpaextension.exception.JPAExtensionException
import org.jpaextension.filter.{FilterFactory, QueryId}
import FilterFactory._
import java.util.regex.Pattern
import org.jpaextension.ReflectionUtil
import reflect.{ClassManifest}

/**
 * query helper to ease handling of queries through the use of
 * closures or the use of filter objects
 *
 * @author Christopher Schmidt
 */
trait QueryHelper {
  self: UsesEntityManager =>

  /**
   * finds an entity with id, does a refresh and executes f() with it
   * @param id ref to id instance (primary key)
   * @return A return value of the applied function
   */
  protected def findAndApply[T: ClassManifest, A](id: AnyRef)(f: T => A) = {
    val m = implicitly[ClassManifest[T]]

    find[T](id) match {
      case None => throw new JPAExtensionException("Entity: " + m.erasure.getName + " not found with parameter: " + id)
      case Some(u) => {
        refresh(u.asInstanceOf[AnyRef])
        f(u)
      }
    }
  }


  /**
   * creates a simple query for one entity and query parameter list
   * @param ( ( T ) =>A) function to be applied
   * @return A return value of the applied function
   */
  protected def oneResultQueryAndApply[A, T: ClassManifest](body: (T) => A): DoWithQuery[A, T] =
    new DoWithQuery[A, T](body)

  /**
   * creates a simple query without closure
   * @return A return value of the applied function
   */
  protected def oneResultQuery[A, T: ClassManifest]: DoWithQuery[A, T] =
    new DoWithQuery[A, T]({x: T => x.asInstanceOf[A]})

  /**
   * executes query and applies body() to each element
   * @param ( ( T ) =>Unit) function to be applies on each element
   * @return Unit
   */
  protected def forQueryResults[T: ClassManifest](body: (T) => Unit): DoWithForQuery[T] =
    new DoWithForQuery[T](body)


  /**
   * see DoWithQueryBase Class 
   */
  protected class DoWithQueryBase {

    /**
     * filling parameter and calling fillParamAndExec on query
     */
    //@TODO rewrite this to use EntityManagerWrapper stuff
    protected def fillParam[T](q: QueryWrapper[T], param: AnyRef*) = {
      var i: Int = 1
      param.foreach {
        x =>
          q.setParameter(i, x)
          i += 1
      }
      q
    }

    /**
     * filling parameter and calling fillParamAndExec on query
     */
    //@TODO rewrite this to use EntityManagerWrapper stuff
    protected def fillParam[T](q: QueryWrapper[T], param: Map[String, AnyRef]) = {
      param.foreach(p => q.setParameter(p._1, p._2))
      q
    }

  }

  /**
   * class for one result queries
   */
  protected class DoWithQuery[A, T: ClassManifest](body: (T) => A) extends DoWithQueryBase {

    /**
     * doing Query
     * @param query JPQL query text
     * @param param list of query parameter
     */
    def withQuery(query: String, param: AnyRef*): A = {
      val q = fillParam[T](createQuery[T](query), param: _*)

      q.findOne match {
        case Some(x) => return body(x)
        case None => throw new JPAExtensionException("Query: " + query + " has no results")
      }
    }

    /**
     * doing Query from JPAExtensions QueryId
     * @param query Query ID (from JPAExtension.xml)
     * @param param list of query parameter
     */
    def withQuery(query: QueryId, param: AnyRef*): A =
      withQuery(getJPQL(query), param: _*)

    /**
     * doing Query from JPAExtensions QueryId
     * @param query Query ID (from JPAExtension.xml)
     * @param param Map of query parameter
     */
    def withQuery(query: QueryId, param: Map[String, AnyRef]): A = {
      val q = fillParam[T](createQuery[T](getJPQL(query)), param)

      q.findOne match {
        case Some(x) => return body(x)
        case None => throw new JPAExtensionException("Query: " + query + " has no results")
      }
    }

    /**
     * doing native Query
     * @param query JPQL query text
     * @param param list of query parameter
     */
    def withNativeQuery(query: String, param: AnyRef*): A = {
      val q = fillParam[T](createNativeQuery[T](query), param: _*)

      q.findOne match {
        case Some(x) => body(x)
        case None => null.asInstanceOf[A]
      }
    }

    /**
     * doing native Query
     * @param query JPQL query text
     * @param param Map of query parameter
     */
    def withNativeQuery(query: String, param: Map[String, AnyRef]): A = {
      val q = fillParam[T](createNativeQuery[T](query), param)

      q.findOne match {
        case Some(x) => body(x)
        case None => null.asInstanceOf[A]
      }
    }

    /**
     * doing native Query from JPAExtensions QueryId
     * @param query Query ID (from JPAExtension.xml)
     * @param param list of query parameter
     */
    def withNativeQuery(query: QueryId, param: AnyRef*): A =
      withNativeQuery(getJPQL(query), param: _*)

    /**
     * doing native Query from JPAExtensions QueryId
     * @param query Query ID (from JPAExtension.xml)
     * @param param Map of query parameter
     */
    def withNativeQuery(query: QueryId, param: Map[String, AnyRef]): A = {
      withNativeQuery(getJPQL(query), param)
    }

    /**
     * doing Query from Filter Object
     * @param filter instance of filter object
     */
    def withQuery(filter: AnyRef): A = {
      val q = createFilterQuery(filter)

      q.findOne match {
        case Some(x) => return body(x)
        case None => throw new JPAExtensionException("Filter with Query: " + getQueryId(filter) + " has no results")
      }
    }
  }


  /**
   * see forResults (for more than one result)
   */
  protected class DoWithForQuery[T: ClassManifest](body: (T) => Unit) extends DoWithQueryBase {

    /**
     * doing Query
     * @param query JPQL query text
     * @param param list of query parameter (nu
     */
    def withQuery(query: String, param: AnyRef*): Unit = {
      val q = fillParam[T](createQuery[T](query), param: _*)
      q.getResultList.foreach(x => body(x))
    }

    /**
     * doing Query
     * @param query JPQL query text
     * @param param MAP of query parameter
     */
    def withQuery(query: String, param: Map[String, AnyRef]): Unit = {
      val q = fillParam[T](createQuery[T](query), param)
      q.getResultList.foreach(x => body(x))
    }

    /**
     * doing Query from JPAExtensions QueryId
     * @param query Query ID (from JPAExtension.xml)
     * @param param list of query parameter
     */
    def withQuery(query: QueryId, param: AnyRef*): Unit =
      withQuery(getJPQL(query), param: _*)

    /**
     * doing Query from JPAExtensions QueryId
     * @param query Query ID (from JPAExtension.xml)
     * @param param Map of query parameter
     */
    def withQuery(query: QueryId, param: Map[String, AnyRef]): Unit = {
      val q = fillParam[T](createQuery[T](getJPQL(query)), param)
      q.getResultList.foreach(x => body(x))
    }

    /**
     * doing Query from Filter Object
     * @param filter instance of filter object
     */
    def withQuery(filter: AnyRef): Unit = {
      val q = createFilterQuery[T](filter)
      val res = q.getResultList
      res.foreach {
        x => body(x)
      }
    }
  }


  // FILTER STUFF


  /**
   * creates new filter instance
   * @param query Query ID (from JPAExtension.xml)
   * @param entity Class the filter should return
   */
  def newFilterInstance[T](queryId: QueryId, entity: Class[_] = null) =
    FilterFactory.newFilterInstance[T](queryId, entity)

  /**
   * creates query using filter object
   * @param filter instance of filter object
   */
  def createFilterQuery[T: ClassManifest](filter: AnyRef) = {
    val m = implicitly[ClassManifest[T]]

    /**
     * query instance from filters Query ID
     */
    val query = getQueryInstance(getQueryId(filter))

    /**
     * replace all hql snippets with syntax |snippetId|
     */
    def replaceSnippets(jpql: String): String = {

      val environment_pattern = Pattern.compile("\\|[^\\|]*\\|", Pattern.UNICODE_CASE)
      val environment_matcher = environment_pattern.matcher(jpql)

      val sb = new StringBuffer

      while (environment_matcher.find) {
        val temp = environment_matcher.group
        val sId = temp.substring(1, temp.length - 1)

        val snippet = getSnippetInstance(sId)

        environment_matcher.appendReplacement(sb, snippet)
      }

      val buffer = environment_matcher.appendTail(sb)
      buffer.toString
    }

    /**
     * creating join fetch statements
     */
    import ReflectionUtil._
    def getFetch: String = {

      var alias = query.alias
      if (alias == null || alias.length() == 0) {
        alias = ""
      } else {
        alias = alias + "."
      }
      val builder = new StringBuilder

      query.fetch.foreach {
        p =>
          val (property: String, jointype: String) = p
          builder.append(' ').append(jointype).append(" join fetch ").append(alias).append(property)
      }
      return builder.toString
    }


    val entityName = getEntityName(filter) match {
      case null => m.erasure.getSimpleName
      case x: String => x
    }

    var jPAQuery: QueryWrapper[T] = null

    val alias = query.alias
    val where = replaceSnippets(query.jpql)

    var qStr: StringBuilder = new StringBuilder(alias)

    val fetch = getFetch
    if (fetch != null && fetch.length() > 0)
      qStr.append(fetch)

    var ext = query.ext
    if (ext != null && ext.length() > 0)
      qStr.append(", ").append(ext)

    var select: String = ""
    if (alias != null && alias.length() > 0)
      select = "select " + alias + " "

    var orderBy = query.orderby
    if (orderBy != null && orderBy.length() > 0)
      orderBy = " order by " + orderBy

    if (where != null && where.length() > 0) {
      val queryString = select + " from " + entityName + " " + qStr + " " + " where " + where + orderBy

      jPAQuery = createQuery[T](queryString)

      query.filterClass.binding.keySet.foreach {
        key =>
          val value = query.filterClass.binding.get(key).get
          jPAQuery.setParameter(key, getFilterPropertyField(filter, value))
      }

    } else {
      val queryString = " from " + entityName + " " + qStr + " " + orderBy;

      jPAQuery = createQuery[T](queryString)
    }

    jPAQuery
  }

  /**
   * returns a List of query annotations
   * @param filter instance of filter object
   */
  def getFilterAnnotations(filter: AnyRef) = {
    val query = getQueryInstance(getQueryId(filter))
    query.annotation
  }
}