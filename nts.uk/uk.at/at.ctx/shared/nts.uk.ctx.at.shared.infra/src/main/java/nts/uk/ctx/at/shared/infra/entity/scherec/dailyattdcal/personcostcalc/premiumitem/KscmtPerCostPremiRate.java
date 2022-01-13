package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculation;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "KSRMT_PER_COST_PREMI_RATE")
@AllArgsConstructor
@NoArgsConstructor
public class KscmtPerCostPremiRate extends ContractUkJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public KmlspPremiumSetPK pk;
    // 割増設定-割増率
    @Column(name = "PREMIUM_RATE")
    public int premiumRate;

    // 単価 ->人件費計算設定.単価
    @Column(name = "UNIT_PRICE")
    public int unitPrice;

	// 割増時間合計に含める
	@Column(name = "INCLUDE_TOTAL")
	public boolean includeTotal;

    @Override
    protected Object getKey() {
        return pk;
    }

    public static List<KscmtPerCostPremiRate> toEntity(PersonCostCalculation domain, String histId) {
        return domain.getPremiumSettings().stream().map(e ->
                new KscmtPerCostPremiRate(
                        new KmlspPremiumSetPK(domain.getCompanyID(), histId, e.getID().value),
                        e.getRate().v(),
                        e.getUnitPrice().value,
                        e.isIncludeTotal()
                )
        ).collect(Collectors.toList());

    }
}
