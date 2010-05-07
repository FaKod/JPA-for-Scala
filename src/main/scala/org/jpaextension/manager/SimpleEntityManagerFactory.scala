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
 * Mixin for creating a Entity Manager Factory
 * Class has to implement getPersistenceUnitName to provide
 * the persistence unit name
 * @author Christopher Schmidt
 */
trait SimpleEntityManagerFactory {

  /**
   * create a entity manager factory with persistence
   * unit name getPersistenceUnitName
   */
  object MyEntityManagerFactory extends LocalEntityManagerFactory(getPersistenceUnitName, true)

  /**
   * to be implemented to provide the persistence unit name
   * @return String persistence unit name
   */
  protected def getPersistenceUnitName : String

  /**
   * opens a new instance of Entity Manager
   * @return EnityManager new instance
   */
  def openEM = MyEntityManagerFactory.openEM

  /**
   * closes the given EnityManager
   * @return Unit
   */
  def closeEM(em: EntityManager):Unit = MyEntityManagerFactory.closeEM(em)

  /**
   * retrieve the persistence unit name
   * @return String persistence unit name
   */
  def getUnitName:String = MyEntityManagerFactory.getUnitName
}