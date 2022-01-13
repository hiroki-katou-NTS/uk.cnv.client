package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class KmlspPremiumSetPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "CID")
    public String companyID;

    @Column(name = "HIST_ID")
    public String histID;
    // 割増設定.割増時間項目NO
    @Column(name = "PREMIUM_NO")
    public int premiumNo;
}
