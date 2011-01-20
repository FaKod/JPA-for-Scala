package org.jpaextension.test

import org.specs.SpecificationWithJUnit
import org.pf4mip.persistence.popo.ObjectItem
import java.math.BigInteger
import org.jpaextension.filter.QueryId
import collection.mutable.Queue
import org.jpaextension.manager.{ThreadLocalEntityManager, SimpleEntityManagerFactory, QueryHelper, UsesEntityManager}

/**
 * User: FaKod
 * Date: 30.04.2010
 * Time: 20:36:06
 */

class QueryTest extends SpecificationWithJUnit with UsesEntityManager with QueryHelper with SimpleEntityManagerFactory with ThreadLocalEntityManager {

  def getPersistenceUnitName = "mip"

  var ids:Queue[BigInteger] = _

  /**
   * init DB Content with 20 Object Item
   */
  def initDBcontent = {
    ids = new Queue[BigInteger]()
    withTrxAndCommit {
      createQuery("Delete from ObjectItem") executeUpdate

      for (i <- 1 to 20) {
        val item = new ObjectItem()
        item.setNameTxt("Test:" + i)
        item.setObjItemCatCode("NKN")
        item.setUpdateSeqNr(i)
        item.setCreatorId(BigInteger.valueOf(i))
        persistAndFlush(item)
        ids.enqueue(item.getId)
      }
    }
  }
  "A Query" should { initDBcontent.before

    "find an entity" in {
      ids.foreach {
        id =>
          val item = find[ObjectItem](id).get
          item.getId must_== id
      }
    }

    "find an entity and apply" in {
      ids.foreach {
        id =>
          findAndApply(id) {
            oi: ObjectItem =>
              oi.getId must_== id
          }
      }
    }

    "execute a one/first-only-result native query" in {
      initDBcontent
      var i: Long = 0
      oneResultQueryAndApply {
        count: Long =>
          i = count
      } withNativeQuery ("select count(*) from obj_item oi where oi.name_txt like ?1", "%Test%")
      i must_== 20
    }

    "execute a one/first-only-result QueryId query" in {
      initDBcontent
      var i = 0
      oneResultQueryAndApply {
        oi: ObjectItem =>
          i = i + 1
      } withQuery (QueryId("FindObjectItemFromName"), "Test:10")
      i must_== 1
    }

    "execute a one/first-only-result String query" in {
      initDBcontent
      var i = 0
      oneResultQueryAndApply {
        oi: ObjectItem =>
          i = i + 1
      } withQuery ("select oi from ObjectItem oi where oi.nameTxt like ?1", "Test:10")
      i must_== 1
    }

    "execute a Query and apply f on results" in {
      initDBcontent
      var i = 0
      forQueryResults {
        oi: ObjectItem =>
          i = i + 1
      } withQuery (QueryId("FindObjectItemFromName"), "%Test%")
      i must_== 20
    }
  }
}