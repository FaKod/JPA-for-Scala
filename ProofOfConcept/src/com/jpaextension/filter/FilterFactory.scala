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

package com.jpaextension.filter

import javassist._
import collection.mutable.HashMap
import com.jpaextension.ReflectionUtil


/**
 * enhances filter object instances with some attributes needed for query
 */
object FilterEnhancer {
  
  val FIELD_NAME_QUERYID = "m_targetFilterIdEnhanced"
  val FIELD_NAME_ENTITY = "m_entityNameEnhanced"

  private val enhanced = HashMap[String, Class[_]]()
  private val pool = ClassPool.getDefault()
  private val target = pool.get("java.lang.String")

  def enhanceObject(filterClassName: String): AnyRef = {

    val cname = filterClassName + "Enhanced"

    enhanced.get(cname) match {
      case Some(x) => x.newInstance.asInstanceOf[AnyRef]
      case None => {
        val baseClass = pool.get(filterClassName)
        val filterClassToEnhance = pool.makeClass(cname, baseClass)
        // creating field
        var field = CtField.make("public String " + FIELD_NAME_QUERYID + " = null;", filterClassToEnhance);
        filterClassToEnhance.addField(field)
        field = CtField.make("public String " + FIELD_NAME_ENTITY + " = null;", filterClassToEnhance);
        filterClassToEnhance.addField(field)
        // store class
        enhanced += ((cname, filterClassToEnhance.toClass))
        enhanceObject(filterClassName)
      }
    }
  }
}


/**
 * Query ID mapper
 */
object QueryId {
  def apply(qid:String) = new QueryId(qid)
}


/**
 * holds query ID
 */
class QueryId(qid:String) {
  def queryId = qid
}


/**
 * Factory for Filter instances
 */
object FilterFactory {
  
  import FilterEnhancer._
  import ReflectionUtil._

  private var path:String = null

  /**
   * lazy init to support change of pah
   */
  lazy private val configuration = {
    path match {
      case null => new FilterConfig("./JPAExtension.xml")
      case _ => new FilterConfig(path)
    }
  }

  /**
   * setting path
   */
  def setPath(p:String) = path=p

  /**
   *  returns new filter object instance
   */
  def newFilterInstance[T](qId: QueryId, entity:Class[_]): T = {
    val query = configuration.getQueryInstance(qId.queryId)
    val filter = enhanceObject(query.filterClass.clazz.getName)
    setQueryId(filter, qId.queryId)
    setEntityName(filter, entity)
    filter.asInstanceOf[T]
  }

  /**
   * return jpql string
   */
  def getJPQL(qId: QueryId):String = {
    val query = configuration.getQueryInstance(qId.queryId)
    query.jpql
  }

  /**
   * get query id from filter instance
   */
  def getQueryId(o:AnyRef):String =
    getField(o, FIELD_NAME_QUERYID)

  /**
   * set query id in filter instance
   */
  private def setQueryId(o:AnyRef, id:String) =
    setField(o, id, FIELD_NAME_QUERYID)

  /**
   * get entitiy name from filter instance
   */
  def getEntityName(o:AnyRef):String =
    getField(o, FIELD_NAME_ENTITY)

  /**
   * set entity name in filter from class object
   */
  private def setEntityName(o:AnyRef, entity:Class[_]) =
    setField(o, if(entity!=null)entity.getSimpleName else null, FIELD_NAME_ENTITY)

  /**
   * return XML unmarshalled Query object
   */
  def getQueryInstance(queryId:String) = configuration.getQueryInstance(queryId)
}
