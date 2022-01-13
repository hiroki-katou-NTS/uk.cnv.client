package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class KmlmpPersonCostCalculationPK implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "CID")
    public String companyID;

    @Column(name = "HIST_ID")
    public String histID;

}
