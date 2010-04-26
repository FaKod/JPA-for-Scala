package com.jpaextension.test

import data._
import com.jpaextension.filter.FilterFactory._
import com.jpaextension.filter.{QueryId, FilterConfig}
import org.specs.{Specification, SpecificationWithJUnit}

/**
 * User: FaKod
 * Date: 09.01.2010
 * Time: 14:18:15
 */

object FilterTest extends Specification {
  
  "A Filter" should {

    "fill Maps" in {
      val conf = new FilterConfig("JPAExtension.xml")
      println("SnippetMap: " + conf.getSnippetMap)
      println("QueryMap: " + conf.getQueryMap)
    }

    "enhance the Filter objects" in {

      val f1: Test1 = newFilterInstance(QueryId("query1"), null)
      val f2: Test2 = newFilterInstance(QueryId("query2"), null)
      val f3: Test3 = newFilterInstance(QueryId("query3"), null)
      val f4: Test1 = newFilterInstance(QueryId("query4"), null)
      val f5: Test2 = newFilterInstance(QueryId("query5"), null)

      val f1s = getQueryId(f1)
      val f2s = getQueryId(f2)
      val f3s = getQueryId(f3)
      val f4s = getQueryId(f4)
      val f5s = getQueryId(f5)

      (f1s + f2s + f3s + f4s + f5s) must_== ("query1query2query3query4query5")
    }
  }
}