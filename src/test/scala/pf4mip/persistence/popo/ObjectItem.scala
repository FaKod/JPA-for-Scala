package org.pf4mip.persistence.popo

import java.math.BigInteger
import javax.persistence._
import reflect.BeanProperty

/**
 * uses inheritance strategy joined
 * uses a sequence for primary key
 * 
 * @author Christopher Schmidt
 */

@Entity
@Table(name = "obj_item")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = "obj_item_id_seq", sequenceName = "obj_item_id_sequence", allocationSize = 1)
class ObjectItem extends MIPEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obj_item_id_seq")
  @Column(name = "obj_item_id", nullable = false, length = 20)
  @BeanProperty
  var id: BigInteger = _

  @Column(name = "cat_code", nullable = false, length = 6)
  @BeanProperty
  var objItemCatCode: String = _

  @Column(name = "name_txt", nullable = false, length = 100)
  @BeanProperty
  var nameTxt: String = _
}