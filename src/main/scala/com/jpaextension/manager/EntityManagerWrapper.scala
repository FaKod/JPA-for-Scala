package com.jpaextension.manager

import com.jpaextension.ReflectionUtil
import com.jpaextension.filter.{FilterFactory, QueryId}
import javax.persistence.{EntityTransaction, EntityManager, Query}
import FilterFactory._
import java.util.regex.Pattern
import com.jpaextension.exception.JPAExtensionException

/**
 * User: FaKod
 * Date: 02.05.2010
 * Time: 11:44:04
 */

/**
 * static Entity Manager access Methods
 */
trait EntityManagerWrapper {
  val em: EntityManager

  import ReflectionUtil._

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
  def newFilterInstance[T](queryId: QueryId, entity: Class[_]) =
    FilterFactory.newFilterInstance[T](queryId, entity)

  /**
   * creates query using filter object
   */
  def createFilterQuery(filter: AnyRef) = {

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
        val sId = temp.substring(1, temp.length() - 1)

        val snippet = getSnippetInstance(sId)

        environment_matcher.appendReplacement(sb, snippet)
      }

      val buffer = environment_matcher.appendTail(sb)
      buffer.toString()
    }

    
    val entityName = getEntityName(filter)

    var jPAQuery: Query = null

    val alias = query.alias
    val where = replaceSnippets(query.jpql)

    var qStr: StringBuilder = new StringBuilder(alias)

    // @TODO add fetch statements
    // queryString.append(query.fetch)

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

      jPAQuery = createQuery(queryString)

      query.filterClass.binding.keySet.foreach {
        key =>
          val value = query.filterClass.binding.get(key).get
          jPAQuery.setParameter(key, getFilterPropertyField(filter, value))
      }

    } else {
      val queryString = " from " + entityName + " " + qStr + " " + orderBy;

      jPAQuery = createQuery(queryString)
    }

    jPAQuery
  }

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

