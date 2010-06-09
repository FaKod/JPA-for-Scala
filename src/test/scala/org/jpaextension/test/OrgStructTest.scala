package org.jpaextension.test

import org.specs.SpecificationWithJUnit
import org.jpaextension.manager.{UsesEntityManager, QueryHelper, SimpleEntityManagerFactory, ThreadLocalEntityManager}
import java.math.BigInteger
import collection.mutable.Queue
import org.pf4mip.persistence.popo.{OrganisationStructure, Organisation, ObjectItem}

/**
 * User: FaKod
 * Date: 09.06.2010
 * Time: 11:46:31
 */

class OrgStructTest extends SpecificationWithJUnit with UsesEntityManager with QueryHelper with SimpleEntityManagerFactory with ThreadLocalEntityManager {
  def getPersistenceUnitName = "mip"

  var ids: Queue[BigInteger] = _

  /**
   * init DB Content with one Object Item
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
        org.setCreatorId(BigInteger.valueOf(i)) // will be overwritten by interceptor
        org.setOrgCatCode("NKN")
        persistAndFlush(org)
        ids.enqueue(org.getId)
      }
    }
  }

  "JPAExtension" should {
    initDBcontent.before

    "return 20 Organisations with query: Select org from Organisation org" in {
      val result = withNoTrx {createQuery[Organisation]("Select org from Organisation org").getResultList}
      withTrxAndCommit {
        result.foreach {
          org =>
            val orgStruct = new OrganisationStructure
            orgStruct.setOrgStructRootOrg(org)
            orgStruct.setNameTxt(org.getNameTxt + "_StructNameHere")
            persistAndFlush(orgStruct)
        }
      }
    }

  }

}