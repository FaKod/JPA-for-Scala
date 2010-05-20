package org.pf4mip.persistence.popo

import java.math.BigInteger
import javax.persistence.{Column, MappedSuperclass, EntityListeners}
import reflect.BeanProperty
import java.lang.Long

/**
 * User: FaKod
 * Date: 20.05.2010
 * Time: 10:28:35
 */

@MappedSuperclass
//@EntityListeners(Array(classOf[MIPEntityInterceptor]))
class MIPEntity {
  @Column(name = "creator_id", nullable = false, length = 20)
  @BeanProperty
  protected var creatorId: BigInteger = _

  @Column(name = "update_seqnr", nullable = false, length = 15)
  @BeanProperty
  protected var updateSeqNr: Long = _
}