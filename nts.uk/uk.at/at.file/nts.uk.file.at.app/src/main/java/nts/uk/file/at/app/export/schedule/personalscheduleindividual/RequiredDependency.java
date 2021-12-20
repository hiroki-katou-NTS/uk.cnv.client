package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonAndWeekStatutoryTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyFlexStatutoryLaborTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyStatutoryWorkingHours;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;

import java.util.Optional;

public class RequiredDependency {
    public static Optional<MonAndWeekStatutoryTime> monAndWeekStatutoryTime(String cid, String employmentCd, String employeeId, GeneralDate baseDate, YearMonth ym, WorkingSystem workingSystem) {
        //enum<労働制> = フレックス勤務、計算対象外　の場合
        return MonthlyStatutoryWorkingHours.monAndWeekStatutoryTime(new RecordDomRequireService().createRequire(), new CacheCarrier(), cid, employmentCd, employeeId, baseDate, ym, workingSystem);
    }

    public static MonthlyFlexStatutoryLaborTime flexMonAndWeekStatutoryTime(String cid, String employmentCd, String employeeId, GeneralDate baseDate, YearMonth ym) {

        return MonthlyStatutoryWorkingHours.flexMonAndWeekStatutoryTime(new RecordDomRequireService().createRequire(), new CacheCarrier(), cid, employmentCd, employeeId, baseDate, ym);
    }
}
