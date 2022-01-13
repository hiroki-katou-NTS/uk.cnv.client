package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumSetting;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Doan Duy Hung
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Table(name = "KSRMT_PER_COST_PREMIUM")
public class KscmtPerCostPremium extends ContractUkJpaEntity {
    @EmbeddedId
    public KmldpPremiumAttendancePK kmldpPremiumAttendancePK;
    @Override
    protected Object getKey() {
        return kmldpPremiumAttendancePK;
    }

    public static List<KscmtPerCostPremium> toEntity(List<PremiumSetting> premiumSettings, String histId) {
        List<KscmtPerCostPremium> rs = new ArrayList<>();
        premiumSettings.forEach(
                e -> {
                    rs.addAll(e.getAttendanceItems().stream().map(i -> new KscmtPerCostPremium(
                            new KmldpPremiumAttendancePK(
                                    e.getCompanyID(),
                                    histId,
                                    e.getID().value,
                                    i.toString()
                            )
                    )).collect(Collectors.toList()));
                }
        );
        return rs;
    }
}
