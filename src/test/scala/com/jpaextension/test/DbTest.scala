package com.jpaextension.test

/**
 * User: FaKod
 * Date: 08.01.2010
 * Time: 11:07:15
 */

import data.{SomeJavaTestFilter, SomeFilter}
import com.pf4mip.persistence.popo.ObjectItem
import java.math.BigInteger
import com.jpaextension.filter.QueryId
import org.specs.Specification
import com.jpaextension.manager.UsesEntityManager

class DbTest extends Specification with UsesEntityManager {
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
  }

}