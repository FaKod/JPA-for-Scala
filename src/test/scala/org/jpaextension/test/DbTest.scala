package org.jpaextension.test

/**
 * User: FaKod
 * Date: 08.01.2010
 * Time: 11:07:15
 */

import data.{SomeJavaTestFilter, SomeFilter}
import org.pf4mip.persistence.popo.ObjectItem
import java.math.BigInteger
import org.jpaextension.filter.QueryId
import org.specs.{SpecificationWithJUnit}
import org.jpaextension.manager._


class DbTest extends SpecificationWithJUnit with UsesEntityManager with QueryHelper with SimpleEntityManagerFactory with ThreadLocalEntityManager {

  def getPersistenceUnitName = "mip"

  "JPAExtension" should {

    "return D all and C one OI" in {

      withTrxAndCommit {
        createQuery("Delete from ObjectItem") executeUpdate

        val item = new ObjectItem()
        item.setNameTxt("Test1")
        item.setObjItemCatCode("NKN")
        item.setUpdateSeqNr(1)
        item.setCreatorId(BigInteger.valueOf(815))

        persist(item)
      }

      withNoTrx {
        val result = createQuery("Select oi from ObjectItem oi").getResultList
        result.size must_== 1
      }
    }

    "Scala Filter should return one filtered ObjectItem instance" in {

      withNoTrx {
        val MyFilter: SomeFilter = newFilterInstance(QueryId("MyOIQuery"), classOf[ObjectItem])

        MyFilter.creatorId = BigInteger.valueOf(815)
        MyFilter.nameTxt = "Test1"

        val result = createFilterQuery(MyFilter).getResultList
        result.size must_== 1
      }

    }

    "Java Filter should return one filtered ObjectItem instance" in {

      withNoTrx {
        val MyFilter: SomeJavaTestFilter = newFilterInstance(QueryId("MyOIQuery2"), classOf[ObjectItem])

        MyFilter.creatorId = BigInteger.valueOf(815)
        MyFilter.nameTxt = "Test1"

        val result = createFilterQuery(MyFilter).getResultList
        result.size must_== 1
      }
    }

    "use Transaction and Commit" in {

      val created: ObjectItem = withTrxAndCommit {
        createQuery("Delete from ObjectItem") executeUpdate

        val item = new ObjectItem()
        item.setNameTxt("Test1")
        item.setObjItemCatCode("NKN")
        item.setUpdateSeqNr(1)
        item.setCreatorId(BigInteger.valueOf(815))
        persist(item)
        item
      }

      withNoTrx {
        val result = createQuery[ObjectItem]("Select oi from ObjectItem oi").getResultList
        result.size must_== 1
        val resItem = result.head
        resItem.getId must_== created.getId
      }
    }

    "use Transaction and Rollback" in {

      withTrxAndCommit {
        createQuery("Delete from ObjectItem") executeUpdate
      }

      val created: ObjectItem = withTrxAndRollback {
        val item = new ObjectItem()
        item.setNameTxt("Test1")
        item.setObjItemCatCode("NKN")
        item.setUpdateSeqNr(1)
        item.setCreatorId(BigInteger.valueOf(815))
        persist(item)
        item
      }

      withNoTrx {
        val result = createQuery("Select oi from ObjectItem oi").getResultList
        result.size must_== 0
      }
    }

    "use no Transaction" in {

      withNoTrx {
        val item = new ObjectItem()
        item.setNameTxt("Test1")
        item.setObjItemCatCode("NKN")
        item.setUpdateSeqNr(1)
        item.setCreatorId(BigInteger.valueOf(815))
        persist(item)
      }
    }
  }

}