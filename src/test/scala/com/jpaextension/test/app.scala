package com.jpaextension.test

import data.SomeJavaTestFilter
import java.math.BigInteger
import com.pf4mip.persistence.popo.ObjectItem
import com.jpaextension.manager.UsesEntityManager
import com.jpaextension.filter.QueryId

/**
 * User: FaKod
 * Date: 16.01.2010
 * Time: 14:03:00
 */

object app extends Application with UsesEntityManager {
  val MyFilter: SomeJavaTestFilter = newFilterInstance(QueryId("MyOIQuery2"), classOf[ObjectItem])

  MyFilter.creatorId = BigInteger.valueOf(815)
  MyFilter.nameTxt = "Test1"

  val result = createFilterQuery(MyFilter).getResultList
}