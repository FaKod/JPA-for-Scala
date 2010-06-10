package org.pf4mip.persistence.popo

import java.math.BigInteger
import javax.persistence.{Column, MappedSuperclass, EntityListeners}
import reflect.BeanProperty
import java.lang.Long

/**
 * class whose mapping information is applied to the entities that inherit from it.
 * This means all popo classes. Added entity listener as a pre persist interceptor
 * to reset both values.
 *
 * @author Christopher Schmidt
 */

@MappedSuperclass
@EntityListeners(Array(classOf[MIPEntityInterceptor]))
class MIPEntity {
  @Column(name = "creator_id", nullable = false, length = 20)
  @BeanProperty
  protected var creatorId: BigInteger = _

  @Column(name = "update_seqnr", nullable = false, length = 15)
  @BeanProperty
  protected var updateSeqNr: Long = _
}