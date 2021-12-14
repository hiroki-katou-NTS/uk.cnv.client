package nts.uk.ctx.at.record.pubimp.dailyprocess.attendancetime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.actualworkinghours.repository.AttendanceTimeRepository;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.DailyExcessTotalTimePub;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.DailyExcessTotalTimePubExport;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.DailyExcessTotalTimePubImport;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.exportparam.DailyExcessTotalTimeExpParam;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

@Stateless
public class DailyExcessTotalTimePubImpl implements DailyExcessTotalTimePub{

	@Inject
	private AttendanceTimeRepository attendanceTimeRepository;


	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public DailyExcessTotalTimePubExport getExcessTotalTime(DailyExcessTotalTimePubImport imp) {
		val domainList = attendanceTimeRepository.findByPeriodOrderByYmd(imp.getEmployeeId(), imp.getYmdSpan());
		return getParam(domainList);
	}
	
	private DailyExcessTotalTimePubExport getParam(List<AttendanceTimeOfDailyPerformance> domainList) {
		Map<GeneralDate,DailyExcessTotalTimeExpParam> returnMap = new HashMap<>();
		for(AttendanceTimeOfDailyPerformance domain : domainList) {
			AttendanceTime overTime = new AttendanceTime(0);
			AttendanceTime holidayWorkTime = new AttendanceTime(0);
			AttendanceTime flexOverTime = new AttendanceTime(0);
			AttendanceTime excessMidTime = new AttendanceTime(0);
			if(domain != null
			   && domain.getTime().getActualWorkingTimeOfDaily() != null
			   && domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime() != null) {
				if(domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily() != null) {
					//残業＆フレ
					if(domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
						overTime = new AttendanceTime(domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().calcTotalFrameTime().v()
												  		+ domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().calcTransTotalFrameTime().v());
						flexOverTime = domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getFlexTime().getCalcTime().greaterThan(0)
										? new AttendanceTime(domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getFlexTime().getCalcTime().valueAsMinutes())
										: new AttendanceTime(0);
					}
					//休出
					if(domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent()) {
						holidayWorkTime = new AttendanceTime(domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().calcTotalFrameTime().v()
																+ domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().calcTransTotalFrameTime().v());
						
					}
					excessMidTime = domain.getTime().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getExcessOfStatutoryMidNightTime().getTime().getCalcTime();
				}
			}
			returnMap.put(domain.getYmd(), new DailyExcessTotalTimeExpParam(overTime, holidayWorkTime, flexOverTime, excessMidTime));
		}
		return new DailyExcessTotalTimePubExport(returnMap);
	}
}
