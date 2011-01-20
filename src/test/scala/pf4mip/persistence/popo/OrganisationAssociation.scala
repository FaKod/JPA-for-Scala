package org.pf4mip.persistence.popo

import javax.persistence._
import java.math.BigInteger
import reflect.BeanProperty
import org.jpaextension.CollectionUtil._

/**
 * User: FaKod
 * Date: 24.06.2010
 * Time: 07:09:06
 */

@Entity
@Table(name = "org_assoc")
@SequenceGenerator(name = "org_assoc_id_seq", sequenceName = "org_assoc_id_sequence")
class OrganisationAssociation extends MIPEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_assoc_id_seq")
  @Column(name = "org_assoc_id", nullable = false, length = 20)
  @BeanProperty
  var id: BigInteger = _

  @OneToMany
  @BeanProperty
  var j__organisations = :+[Organisation]

  def organisations_=(c: SC[Organisation]) = j__organisations = c

  def organisations: SC[Organisation] = j__organisations
}