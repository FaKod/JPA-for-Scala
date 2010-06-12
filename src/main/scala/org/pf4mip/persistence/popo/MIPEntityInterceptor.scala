package org.pf4mip.persistence.popo

import javax.persistence.PrePersist
import java.math.BigInteger

/**
 * Specifies the callback listener classes to be used for an entity or mapped superclass.
 * resets both common values
 *
 * @author Christopher Schmidt
 */

class MIPEntityInterceptor {
  @PrePersist
  def prePersist(entity: MIPEntity): Unit = {
    entity.setUpdateSeqNr(1234L)
    entity.setCreatorId(BigInteger.valueOf(815))
  }

}