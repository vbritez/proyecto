package com.tigo.cs.android.helper.domain;

public class MetaMemberEntity extends BaseEntity {

    private MetaEntity meta;
    private Long memberCod;
    private String name;

    public MetaMemberEntity() {
    }

    public MetaEntity getMeta() {
        return meta;
    }

    public void setMeta(MetaEntity meta) {
        this.meta = meta;
    }

    public Long getMemberCod() {
        return memberCod;
    }

    public void setMemberCod(Long memberCod) {
        this.memberCod = memberCod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
