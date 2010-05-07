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
 * User: FaKod
 * Date: 07.05.2010
 * Time: 11:20:07
 */

trait SimpleEntityManagerFactory {

  object MyEntityManagerFactory extends LocalEntityManagerFactory(getPersistenceUnitName, true)

  protected def getPersistenceUnitName : String

  def openEM = MyEntityManagerFactory.openEM

  def closeEM(em: EntityManager) = MyEntityManagerFactory.closeEM(em)

  def getUnitName = MyEntityManagerFactory.getUnitName
}