package org.jpaextension.test

import org.specs.SpecificationWithJUnit
import org.jpaextension.manager.{UsesEntityManager, QueryHelper, SimpleEntityManagerFactory, ThreadLocalEntityManager}
import collection.mutable.Queue
import java.math.BigInteger
import org.pf4mip.persistence.popo.{Organisation, OrganisationAssociation, ObjectItem}

/**
 * User: FaKod
 * Date: 24.06.2010
 * Time: 09:31:48
 */

class CollectionTest extends SpecificationWithJUnit with UsesEntityManager with QueryHelper with SimpleEntityManagerFactory with ThreadLocalEntityManager {

  def getPersistenceUnitName = "mip"

  var ids:Queue[BigInteger] = _

  /**
   * init DB Content with 20 Object Item
   */
  def initDBcontent = {
    ids = new Queue[BigInteger]()
    withTrxAndCommit {
      createQuery("Delete from Organisation") executeUpdate

      for (i <- 1 to 20) {
        val org = new Organisation()
        org.setNameTxt("Test:" + i)
        org.setObjItemCatCode("NKN")
        org.setUpdateSeqNr(i)
        org.setCreatorId(BigInteger.valueOf(i))
        org.setOrgCatCode("NKN")
        persistAndFlush(org)
        ids.enqueue(org.getId)
      }
    }
  }
  "A Query" should { initDBcontent.before

    "find an entity" in {
      val id =
      withTrxAndCommit {
        val oa = new OrganisationAssociation
        ids.foreach {
          id =>
          val item = find[Organisation](id).get
          oa.organisations + item
        }
        persistAndFlush(oa)
        oa.getId
      }

      val oaNew = find[OrganisationAssociation](id).get
      refresh(oaNew)
      oaNew.organisations.size must_==20

      withTrxAndCommit {
        ids.foreach {
          id =>
          val item = find[Organisation](id).get
          oaNew.organisations + item
        }
      }

      refresh(oaNew)
      oaNew.organisations.size must_==40
    }
  }
}