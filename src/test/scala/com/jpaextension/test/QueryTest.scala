package com.jpaextension.test

import org.specs.SpecificationWithJUnit
import com.jpaextension.manager.{QueryHelper, UsesEntityManager}
import com.pf4mip.persistence.popo.ObjectItem
import java.math.BigInteger
import com.jpaextension.filter.QueryId

/**
 * User: FaKod
 * Date: 30.04.2010
 * Time: 20:36:06
 */

class QueryTest extends SpecificationWithJUnit with UsesEntityManager with QueryHelper {
  "A Query" should {

    setSequential()

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

    "find an entity" in {
      for (i <- 1 to 20) {
        val item = findSimple(classOf[ObjectItem], BigInteger.valueOf(i))
        println("Loading Object Item: " + i)
        item.getId must_== i
      }
    }

    "find an entity and apply" in {
      for (t <- 1 to 20) {
        findAndApply(classOf[ObjectItem], BigInteger.valueOf(t)) {
          oi: ObjectItem =>
            oi.getId must_== t
        }
      }
    }

    "execute a one/first-only-result native query" in {
      var i = 0
      oneResultQueryAndApply {
        oi: ObjectItem =>
          i = i + 1
      } withNativeQuery ("select oi from obj_item oi where oi.name_txt like ?1", "%Test%")
      i must_== 1
    }

    "execute a one/first-only-result QueryId query" in {
      var i = 0
      oneResultQueryAndApply {
        oi: ObjectItem =>
          i = i + 1
      } withQuery (QueryId("FindObjectItemFromName"), "%Test%")
      i must_== 1
    }

    "execute a Query and apply f on results" in {
      var i = 0
      forQueryResults {
        oi: ObjectItem =>
          i = i + 1
      } withQuery (QueryId("FindObjectItemFromName"), "%Test%")
      i must_== 1
    }
  }
}