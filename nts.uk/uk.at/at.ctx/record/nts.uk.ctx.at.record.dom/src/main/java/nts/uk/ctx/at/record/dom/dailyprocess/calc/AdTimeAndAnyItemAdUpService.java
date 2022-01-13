package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 日別実績の勤怠時間と任意項目を同時更新し、ストアドを実行するためのサービス
 * @author keisuke_hoshina
 *
 */
public interface AdTimeAndAnyItemAdUpService {

	void addAndUpdate(String empId, GeneralDate ymd, Optional<AttendanceTimeOfDailyPerformance> attendanceTime,
			Optional<AnyItemValueOfDaily> anyItem, Optional<OuenWorkTimeOfDaily> ouenTime);
	
	List<IntegrationOfDaily> addAndUpdate(List<IntegrationOfDaily> daily);
	
	List<IntegrationOfDaily> saveOnly(List<IntegrationOfDaily> daily);
	
	IntegrationOfDaily addAndUpdate(IntegrationOfDaily daily);
}
