package org.jpaextension.manager

import org.jpaextension.ReflectionUtil
import org.jpaextension.filter.{FilterFactory}
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

