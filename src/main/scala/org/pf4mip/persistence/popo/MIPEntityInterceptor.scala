package org.pf4mip.persistence.popo

import javax.persistence.PrePersist
import java.math.BigInteger

/**
 * User: FaKod
 * Date: 20.05.2010
 * Time: 10:26:06
 */

class MIPEntityInterceptor {
  @PrePersist
  def prePersist(entity: MIPEntity): Unit = {
    entity.setUpdateSeqNr(1234L)
    entity.setCreatorId(BigInteger.valueOf(815))
  }

}