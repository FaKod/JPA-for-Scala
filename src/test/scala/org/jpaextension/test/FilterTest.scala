package org.jpaextension.test

import org.jpaextension.filter.FilterFactory._
import org.jpaextension.filter.{QueryId, FilterConfig}
import org.specs.{SpecificationWithJUnit}
import org.jpaextension.manager.{UsesEntityManager, QueryHelper}
import org.jpaextension.test.data.{Filter3, Filter2, Filter1}

/**
 * User: FaKod
 * Date: 09.01.2010
 * Time: 14:18:15
 */

class FilterTest extends SpecificationWithJUnit with UsesEntityManager with QueryHelper {
  "A Filter" should {

    "fill Maps" in {
      val url = classOf[FilterTest].getClassLoader.getResource("META-INF/JPAExtension.xml")
      val conf = new FilterConfig(url)
      println("SnippetMap: " + conf.getSnippetMap)
      conf.getSnippetMap.size must_== 3

      println("QueryMap: " + conf.getQueryMap)
      conf.getQueryMap.size must_== 9
    }

    "enhance the Filter objects" in {

      val f1: Filter1 = newFilterInstance(QueryId("query1"), null)
      val f2: Filter2 = newFilterInstance(QueryId("query2"), null)
      val f3: Filter3 = newFilterInstance(QueryId("query3"), null)
      val f4: Filter1 = newFilterInstance(QueryId("query4"), null)
      val f5: Filter2 = newFilterInstance(QueryId("query5"), null)

      val f1s = getQueryId(f1)
      val f2s = getQueryId(f2)
      val f3s = getQueryId(f3)
      val f4s = getQueryId(f4)
      val f5s = getQueryId(f5)

      (f1s + f2s + f3s + f4s + f5s) must_== ("query1query2query3query4query5")
    }

    "return annotations" in {
      val f: Filter1 = newFilterInstance(QueryId("query1"), null)

      val annoList = getFilterAnnotations(f)

      annoList.size must_== 5
    }
  }
}