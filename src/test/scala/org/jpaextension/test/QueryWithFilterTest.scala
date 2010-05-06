package org.jpaextension.test

import org.jpaextension.manager.{UsesEntityManager, QueryHelper}
import org.specs.SpecificationWithJUnit
import org.jpaextension.filter.QueryId
import org.pf4mip.persistence.popo.ObjectItem
import java.math.BigInteger

/**
 * User: FaKod
 * Date: 01.05.2010
 * Time: 15:16:41
 */

class NameFilter {
  var name: String = _
}

class QueryWithFilterTest extends SpecificationWithJUnit with UsesEntityManager with QueryHelper {
  "A Query with Filter" should {

    "create Object Items" in {
      withTrxAndCommit {
        createQuery("Delete from ObjectItem") executeUpdate

        for (i <- 1 to 20) {
          val item = new ObjectItem()
          item.setNameTxt("Test:" + i)
          item.setObjItemCatCode("NKN")
          item.setUpdateSeqNr(i)
          item.setCreatorId(BigInteger.valueOf(i))
          persist(item)
        }
      }
    }

    "execute a Query and apply f on results" in {

      val filter: NameFilter = newFilterInstance(QueryId("FindObjectItemFromNameWithFilter"), classOf[ObjectItem])
      filter.name = "%Test%"
      
      val filter2: NameFilter = newFilterInstance(QueryId("FindObjectItemFromNameWithFilter"), classOf[ObjectItem])
      filter2.name = "%Test:%"

      var i:Int = 0
      forQueryResults {
        oi: ObjectItem =>
          i = i + 1
      } withQuery (filter)
      i must_== 20

      i = 0
      forQueryResults {
        oi: ObjectItem =>
          i = i + 1
      } withQuery (filter2)
      i must_== 20
    }
  }
}