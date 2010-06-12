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

  /**
   * init DB Content with one Object Item
   */
  def initDBcontent = {
    withTrxAndCommit {
        createQuery("Delete from ObjectItem") executeUpdate

        val item = new ObjectItem()
        item.setNameTxt("Test1")
        item.setObjItemCatCode("NKN")
        item.setUpdateSeqNr(1)
        item.setCreatorId(BigInteger.valueOf(1234)) // will be overwritten by interceptor
        persist(item)
      }
  }

  "JPAExtension" should { initDBcontent.before

    "return OI with query: Select oi from ObjectItem oi" in {
      withNoTrx {
        val result = createQuery[ObjectItem]("Select oi from ObjectItem oi").getResultList
        result.size must_== 1
      }
    }

    "use Scala-Filter-Object MyOIQuery and return one filtered ObjectItem instance" in {

      withNoTrx {
        val MyFilter: SomeFilter = newFilterInstance(QueryId("MyOIQuery"))

        MyFilter.creatorId = BigInteger.valueOf(815)
        MyFilter.nameTxt = "Test1"

        val result = createFilterQuery[ObjectItem](MyFilter).getResultList
        result.size must_== 1
      }

    }

    "use Java-Filter-Object and return one filtered ObjectItem instance" in {

      withNoTrx {
        val MyFilter: SomeJavaTestFilter = newFilterInstance(QueryId("MyOIQuery2"))

        MyFilter.creatorId = BigInteger.valueOf(815)
        MyFilter.nameTxt = "Test1"

        val result = createFilterQuery[ObjectItem](MyFilter).getResultList
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
        val result = createQuery[ObjectItem]("Select oi from ObjectItem oi").getResultList
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