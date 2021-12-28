package nts.uk.ctx.at.request.infra.entity.application.optional;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class KrqdtAppAnyvPk {

    @Basic
    @Column(name = "CID")
    public String companyID;

    @Basic
    @Column(name = "APP_ID")
    public String appID;

    @Basic
    @Column(name = "ANYV_CD")
    public int anyvCd;

    @Basic
    @Column(name = "ANYV_NO")
    public int anyvNo;

}
