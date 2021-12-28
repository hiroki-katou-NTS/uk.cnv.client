package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.premiumitem;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "KSRMT_PER_COST_CALC_HIST")
@AllArgsConstructor
@NoArgsConstructor
public class KscmtPerCostCalcHist extends UkJpaEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    public KscmtPerCostCalcHistPk pk;
    @Column(name = "START_YMD")
    public GeneralDate startDate;

    @Column(name = "END_YMD")
    public GeneralDate endDate;

    @Column(name = "CONTRACT_CD")
    public String contractCd ;



    @Override
    protected Object getKey() {
        return pk;
    }

    public KscmtPerCostCalcHist update(DateHistoryItem domain) {
        this.endDate = domain.end();
        this.startDate = domain.start();
        this.contractCd =  AppContexts.user().contractCode();
        return this;
    }
    public static KscmtPerCostCalcHist toEntity(GeneralDate startDate, GeneralDate endDate, String histId, String cid) {

        return new KscmtPerCostCalcHist(
                new KscmtPerCostCalcHistPk(cid, histId),
                startDate,
                endDate,
                AppContexts.user().contractCode()
        );
    }
}
