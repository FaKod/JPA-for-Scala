package org.pf4mip.persistence.popo

import javax.persistence.{Column, Entity, Table, PrimaryKeyJoinColumn}
import java.math.BigInteger

/**
 * User: FaKod
 * Date: 20.05.2010
 * Time: 15:15:24
 */

@Entity@Table(name = "org")
@PrimaryKeyJoinColumn(name = "org_id")
class Organisation extends ObjectItem {
  @Column(name = "cat_code", length = 6, nullable = false)
  protected var orgCatCode: String = _
}