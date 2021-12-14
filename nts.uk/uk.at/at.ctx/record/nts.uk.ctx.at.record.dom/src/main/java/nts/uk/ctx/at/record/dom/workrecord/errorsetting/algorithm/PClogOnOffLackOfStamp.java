package nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.PCLogOnInfoOfDaily;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.LogOnInfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;

/**
 * 打刻漏れ(PCログオンオグオフ) - PCログオン打刻漏れ
 * @author nampt
 *
 */
@Stateless
public class PClogOnOffLackOfStamp {

	@Inject
	private BasicScheduleService basicScheduleService;
	
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;
	
// fix bug 106204
//	@Inject
//	private ManagedParallelWithContext managedParallelWithContext;

	public EmployeeDailyPerError pClogOnOffLackOfStamp(String companyId, String employeeId, GeneralDate processingDate,
			PCLogOnInfoOfDaily pCLogOnInfoOfDaily, WorkInfoOfDailyPerformance workInfoOfDailyPerformance) {
		
		EmployeeDailyPerError employeeDailyPerError = null;
		
		// 1日半日出勤・1日休日系の判定
		WorkStyle workStyle = basicScheduleService
				.checkWorkDay(workInfoOfDailyPerformance.getWorkInformation().getRecordInfo().getWorkTypeCode().v());
		if (workStyle != WorkStyle.ONE_DAY_REST) {
			if (pCLogOnInfoOfDaily != null && !pCLogOnInfoOfDaily.getTimeZone().getLogOnInfo().isEmpty()) {
				List<Integer> attendanceItemIDList = new ArrayList<>();
				
				//所定時間設定を取得
				Optional<PredetemineTimeSetting> predetemineTimeSet = Optional.empty();
				if(workInfoOfDailyPerformance.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().isPresent()) {
					predetemineTimeSet = predetemineTimeSettingRepository.findByWorkTimeCode(
							companyId,
							workInfoOfDailyPerformance.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().get().v());
				}
				
				//所定労働時間帯の件数を取得
				int predTimeSpanCount = predetemineTimeSet.isPresent()
						? predetemineTimeSet.get().getTimezoneByAmPmAtrForCalc(workStyle.toAmPmAtr().orElse(AmPmAtr.ONE_DAY)).size()
						: 0;
				
				List<LogOnInfo> logOnInfos = pCLogOnInfoOfDaily.getTimeZone().getLogOnInfo();
				for(int number = 1;number<=predTimeSpanCount;number++) {//start for 1
					// fix bug 106204
					for (LogOnInfo logOnInfo : logOnInfos) {//start for 2
						if(logOnInfo.getWorkNo().v().intValue() == number) {
							// ログオフのみ存在している(only has Logoff time)
							if (!logOnInfo.leakageCheck() && !logOnInfo.getLogOn().isPresent()) {
								if (logOnInfo.getWorkNo().v() == 1) {
									attendanceItemIDList.add(794);
								} else if (logOnInfo.getWorkNo().v() == 2) {
									attendanceItemIDList.add(796);
								}
							}
							// ログオンのみ存在している(only has Logon time)
							if (!logOnInfo.leakageCheck() && !logOnInfo.getLogOff().isPresent()) {
								if (logOnInfo.getWorkNo().v() == 1) {
									attendanceItemIDList.add(795);
								} else if (logOnInfo.getWorkNo().v() == 2) {
									attendanceItemIDList.add(797);
								}
							}
						}
					}
				}

				if (!attendanceItemIDList.isEmpty()){
					employeeDailyPerError = new EmployeeDailyPerError(companyId,
							employeeId, processingDate, new ErrorAlarmWorkRecordCode("S002"),
							attendanceItemIDList);
				}
			}
		}
		return employeeDailyPerError;
	}

}
