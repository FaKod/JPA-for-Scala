package org.pf4mip.persistence.popo

import java.math.BigInteger
import javax.persistence._
import reflect.BeanProperty

/**
 * User: FaKod
 * Date: 20.05.2010
 * Time: 15:29:53
 */

@Entity
//@Access(AccessType.PROPERTY)
@Table(name = "org_struct")
@IdClass(classOf[OrganisationStructureId])
@SequenceGenerator(name = "org_struct_index_seq", sequenceName = "org_struct_index_sequence", allocationSize = 1)
class OrganisationStructure extends MIPEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_struct_index_seq")
  @Column(name = "org_struct_ix", nullable = false, length = 20)
  @BeanProperty
  protected var ix: BigInteger = _

  @Id
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "org_struct_root_org_id", nullable = false, updatable = false)
  @BeanProperty
  protected var orgStructRootOrg: Organisation = _

  @Column(name = "name_txt", length = 100)
  @BeanProperty
  protected var nameTxt: String = _
}

//@Access(AccessType.PROPERTY)
class OrganisationStructureId {
  @BeanProperty
  var orgStructRootOrg: BigInteger = _
  @BeanProperty
  var ix: BigInteger = _

  override def equals(obj: Any): Boolean = {
    val o = obj.asInstanceOf[AnyRef]
    if (this == o) return true
    if (o == null || getClass() != o.getClass) return false

    val that = o.asInstanceOf[OrganisationStructureId]

    ix match {
      case null => if (that.ix != null) return false
      case x => if (!x.equals(that.ix)) return false
    }

    if (orgStructRootOrg == null)
      (that.orgStructRootOrg == null) else orgStructRootOrg.equals(that.orgStructRootOrg)
  }

  override def hashCode = {
    val result = if (orgStructRootOrg != null) orgStructRootOrg.hashCode else 0
    31 * result + (if (ix != null) ix.hashCode else 0)
  }
}