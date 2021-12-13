package nts.uk.ctx.at.function.ac.annleauseddays;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.annleauseddays.FindAnnLeaUsedDaysAdapter;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.FindAnnLeaUsedDaysFromPreviousToNextGrantDatePub;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class FindAnnLeaUsedDaysAdapterImpl implements FindAnnLeaUsedDaysAdapter {

    @Inject
    private FindAnnLeaUsedDaysFromPreviousToNextGrantDatePub nextGrantDatePub;
    @Inject
    private RecordDomRequireService requireService;
    @Override
    public AnnualLeaveUsedDayNumber findUsedDays(String employeeId) {
        val require = requireService.createRequire();
        val cacheCarrier = new CacheCarrier();

        // 社員に対応する締め開始日を取得する
        val closureStartOpt = GetClosureStartForEmployee.algorithm(require, cacheCarrier, employeeId);
        if(closureStartOpt.isPresent()){
            GeneralDate closingStartDate = closureStartOpt.get();
            return nextGrantDatePub.findUsedDays(employeeId,closingStartDate);
        }
        return null;

    }
}
