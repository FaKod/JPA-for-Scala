package com.pf4mip.persistence.popo;

import javax.persistence.*;
import java.math.BigInteger;


@Entity
@Table(name="obj_item")
@SequenceGenerator(name = "obj_item_id_seq", sequenceName = "obj_item_id_seq", allocationSize = 1)
public class ObjectItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obj_item_id_seq")
    @Column(name = "obj_item_id", nullable = false, length = 20)
    private BigInteger id;

    @Column(name = "cat_code", nullable = false, length = 6)
    private String objItemCatCode;

    @Column(name = "name_txt", nullable = false, length = 100)
    private String nameTxt;

    @Column(name = "creator_id", nullable = false, length = 20)
    private BigInteger creatorId;

    @Column(name = "update_seqnr", nullable = false, length = 15)
    private Long updateSeqNr;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getObjItemCatCode() {
        return objItemCatCode;
    }

    public void setObjItemCatCode(String objItemCatCode) {
        this.objItemCatCode = objItemCatCode;
    }

    public String getNameTxt() {
        return nameTxt;
    }

    public void setNameTxt(String nameTxt) {
        this.nameTxt = nameTxt;
    }

    public BigInteger getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(BigInteger creatorId) {
        this.creatorId = creatorId;
    }

    public Long getUpdateSeqNr() {
        return updateSeqNr;
    }

    public void setUpdateSeqNr(Long updateSeqNr) {
        this.updateSeqNr = updateSeqNr;
    }
}
