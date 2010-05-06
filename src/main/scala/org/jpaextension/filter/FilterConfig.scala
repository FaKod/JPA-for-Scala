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

package org.jpaextension.filter

import xml.XML
import collection.mutable.HashMap
import collection.mutable.Map
import org.jpaextension.exception.FilterException
import java.net.URL


/**
 * class to store query instances and containing classes
 */
class Query(idc: String, orderbyc: String, extc: String, aliasc: String, jpqlc: String,
            annotationc: List[String], fetchc: Map[String, String], filterClassc: FilterClass) {
  def id = idc
  def orderby = orderbyc
  def ext = extc
  def alias = aliasc
  def jpql = jpqlc
  def annotation = annotationc
  def fetch = fetchc
  def filterClass = filterClassc
}



/**
 * filter class and bindings
 */
class FilterClass(clazzc: Class[_], bindingc: Map[String, String]) {
  def clazz = clazzc
  def binding = bindingc
}



/**
 * filter configuration class reads configuration from settings.XML
 */
class FilterConfig(url:URL) {

  /**
   * holding read snippets
   */
  private val snippetMap = new HashMap[String, String]

  /**
   * holding read queries
   */
  private val queryMap = new HashMap[String, Query]
  
  init

  private def init = {
    val settings = XML.load(url)

    /**
     *  create snippet map
     */
    val snippetTag = ( settings \ "jpqlsnippets" ) \ "snippet"
    val snippetList = for(snippet <- snippetTag) yield (snippet \ "@id" text, snippet \ "@jpql" text)
    snippetList.foreach (x => snippetMap.put(x._1, x._2))

    /**
     * create query map
     */
    val queriesTag = ( settings \ "filter" ) \ "query"

    for(queryTag <- queriesTag) {
      // maps for Query class
      val bindingMap = new HashMap[String, String]
      val fetchMap = new HashMap[String, String]

      val filterClassTag = queryTag \ "filterClass"

      // create binding map
      val bindingList = for(binding <- filterClassTag \ "binding") yield (binding \ "@var" text, binding \ "@attribute" text)
      bindingList.foreach(x => bindingMap.put(x._1, x._2))

      val filterClassName = filterClassTag \ "@class" text
      val filterClass =
        if(filterClassName.length > 0)
          new FilterClass(Class.forName(filterClassName), bindingMap)
        else
          null

      // create fetch map
      var fetchList = for(fetchTag <- queryTag \ "fetch") yield (fetchTag \ "@property" text, fetchTag \ "@joinType" text)
      fetchList.foreach(x => fetchMap.put(x._1, x._2))

      // create annotation list
      val annotationList = (queryTag \ "@annotation" text).split("[,; ]").toList

      // create query map
      var query = new Query(
        queryTag \ "@id" text,
        queryTag \ "@orderby" text,
        queryTag \ "@ext" text,
        queryTag \ "@alias" text,
        queryTag \ "@jpql" text,
        annotationList,
        fetchMap,
        filterClass
        )
      queryMap.put(queryTag \ "@id" text, query)
    }
  }

  /**
   * return the snippet map read from XML
   */
  def getSnippetMap = snippetMap

  /**
   * returns the query map read from XML
   */
  def getQueryMap = queryMap

  /**
   * return specific query
   */
  def getQueryInstance(queryId:String) = queryMap.get(queryId) match {
    case Some(x) => x
    case None => throw new FilterException("Query ID: " + queryId + " not found")
  }

  /**
   * returns specific snippet
   */
  def getSnippetInstance(snippetId:String) = snippetMap.get(snippetId) match {
    case Some(x) => x
    case None => throw new FilterException("HQLSnippet ID: " + snippetId + " not found")
  }
}
