package nts.uk.ctx.at.record.infra.repository.optitem.calculation;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtCalcResultRange;
import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtCalcResultRangePK;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcRangeCheck;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcResultRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.CalcResultRangeRepository;

/**
 * @author anhnm
 *
 */
@Stateless
public class JpaCalcResultRangeRepository extends JpaRepository implements CalcResultRangeRepository {
    private final String QUERY = "SELECT * FROM KrcmtCalcResultRange WHERE CID = @companyID AND OPTIONAL_ITEM_NO = @optNo";

    @Override
    public void update(String companyID, int optionalItemNo, CalcResultRange domain) {
        Optional<KrcmtCalcResultRange> entityOpt = this.findEntity(companyID, optionalItemNo);
        if (entityOpt.isPresent()) {
            KrcmtCalcResultRange entity = entityOpt.get();
            
            entity.setUpperLimitAtr(domain.getUpperLimit().value);
            entity.setLowerLimitAtr(domain.getLowerLimit().value);
            
            if (domain.getUpperLimit().equals(CalcRangeCheck.SET)) {
                
                // Upper daily amount
                if (domain.getAmountRange().get().getDailyAmountRange().get().getUpperLimit().isPresent()) {
                    entity.setUpperdayAmountRange(domain.getAmountRange().get().getDailyAmountRange().get().getUpperLimit().get().v());
                } else {
                    entity.setUpperdayAmountRange(null);
                }
                // Upper monthly amount
                if (domain.getAmountRange().get().getMonthlyAmountRange().get().getUpperLimit().isPresent()) {
                    entity.setUpperMonAmountRange(domain.getAmountRange().get().getMonthlyAmountRange().get().getUpperLimit().get().v());
                } else {
                    entity.setUpperMonAmountRange(null);
                }
                // Upper daily time
                if (domain.getTimeRange().get().getDailyTimeRange().get().getUpperLimit().isPresent()) {
                    entity.setUpperDayTimeRange(domain.getTimeRange().get().getDailyTimeRange().get().getUpperLimit().get().v());
                } else {
                    entity.setUpperDayTimeRange(null);
                }
                // Upper Monthly time
                if (domain.getTimeRange().get().getMonthlyTimeRange().get().getUpperLimit().isPresent()) {
                    entity.setUpperMonTimeRange(domain.getTimeRange().get().getMonthlyTimeRange().get().getUpperLimit().get().v());
                } else {
                    entity.setUpperMonTimeRange(null);
                }
                // Upper daily number
                if (domain.getNumberRange().get().getDailyTimesRange().get().getUpperLimit().isPresent()) {
                    entity.setUpperDayNumberRange(domain.getNumberRange().get().getDailyTimesRange().get().getUpperLimit().get().v());
                } else {
                    entity.setUpperDayNumberRange(null);
                }
                // Upper Monthly number
                if (domain.getNumberRange().get().getMonthlyTimesRange().get().getUpperLimit().isPresent()) {
                    entity.setUpperMonNumberRange(domain.getNumberRange().get().getMonthlyTimesRange().get().getUpperLimit().get().v());
                } else {
                    entity.setUpperMonNumberRange(null);
                }
            }
            if (domain.getLowerLimit().equals(CalcRangeCheck.SET)) {
                // Lower daily amount
                if (domain.getAmountRange().get().getDailyAmountRange().get().getLowerLimit().isPresent()) {
                    entity.setLowerDayAmountRange(domain.getAmountRange().get().getDailyAmountRange().get().getLowerLimit().get().v());
                } else {
                    entity.setLowerDayAmountRange(null);
                }
                // Lower monthly amount
                if (domain.getAmountRange().get().getMonthlyAmountRange().get().getLowerLimit().isPresent()) {
                    entity.setLowerMonAmountRange(domain.getAmountRange().get().getMonthlyAmountRange().get().getLowerLimit().get().v());
                } else {
                    entity.setLowerMonAmountRange(null);
                }
                // Lower daily time
                if (domain.getTimeRange().get().getDailyTimeRange().get().getLowerLimit().isPresent()) {
                    entity.setLowerDayTimeRange(domain.getTimeRange().get().getDailyTimeRange().get().getLowerLimit().get().v());
                } else {
                    entity.setLowerDayTimeRange(null);
                }
                // Lower Monthly time
                if (domain.getTimeRange().get().getMonthlyTimeRange().get().getLowerLimit().isPresent()) {
                    entity.setLowerMonTimeRange(domain.getTimeRange().get().getMonthlyTimeRange().get().getLowerLimit().get().v());
                } else {
                    entity.setLowerMonTimeRange(null);
                }
                // Lower daily number
                if (domain.getNumberRange().get().getDailyTimesRange().get().getLowerLimit().isPresent()) {
                    entity.setLowerDayNumberRange(domain.getNumberRange().get().getDailyTimesRange().get().getLowerLimit().get().v());
                } else {
                    entity.setLowerDayNumberRange(null);
                }
                // Lower Monthly number
                if (domain.getNumberRange().get().getMonthlyTimesRange().get().getLowerLimit().isPresent()) {
                    entity.setLowerMonNumberRange(domain.getNumberRange().get().getMonthlyTimesRange().get().getLowerLimit().get().v());
                } else {
                    entity.setLowerMonNumberRange(null);
                }
            }
            
            // Update entity
            this.commandProxy().update(entity);
        }
    }

    @Override
    public CalcResultRange find(String companyID, int optionalItemNo) {
        KrcmtCalcResultRangePK entityPK = new KrcmtCalcResultRangePK(companyID, optionalItemNo);
        
        return this.queryProxy().find(entityPK, KrcmtCalcResultRange.class).get().toDomain();
    }
    

    private Optional<KrcmtCalcResultRange> findEntity(String companyID, int optionalItemNo) {
        KrcmtCalcResultRangePK entityPK = new KrcmtCalcResultRangePK(companyID, optionalItemNo);
        
        return this.queryProxy().find(entityPK, KrcmtCalcResultRange.class);
    }
}
