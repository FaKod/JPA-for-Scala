package com.jpaextension.manager

import com.jpaextension.ReflectionUtil
import com.jpaextension.filter.{FilterFactory, QueryId}
import javax.persistence.{EntityTransaction, EntityManager, Query}

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
    val queryId = FilterFactory.getQueryId(filter)
    val entityName = FilterFactory.getEntityName(filter)

    val query = FilterFactory.getQueryInstance(queryId)

    //@TODO generate HQL query with all possibilities
    val queryText = "select " +
            query.alias +
            " from " +
            entityName + " " +
            query.alias + " " +
            " where (" +
            query.jpql +
            ")"

    val jPAQuery = createQuery(queryText)

    query.filterClass.binding.keySet.foreach {
      key =>
        val value = query.filterClass.binding.get(key).get
        jPAQuery.setParameter(key, getFilterPropertyField(filter, value))
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

