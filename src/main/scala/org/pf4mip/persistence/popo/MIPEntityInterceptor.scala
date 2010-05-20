package org.pf4mip.persistence.popo

import javax.persistence.PrePersist
import java.math.BigInteger

/**
 * User: FaKod
 * Date: 20.05.2010
 * Time: 10:26:06
 */

object MIPEntityInterceptor {
  private val id_offset = "00000000000000";
}

class MIPEntityInterceptor {
  @PrePersist
  def prePersist(entity: MIPEntity): Unit = {
    entity.setUpdateSeqNr(0L)
    entity.setCreatorId(getOffset)
  }

  protected def getOffset:BigInteger = {
    val partyId = "185"
    val nodeId = "106"
    new BigInteger(partyId + nodeId + MIPEntityInterceptor.id_offset)
  }
}