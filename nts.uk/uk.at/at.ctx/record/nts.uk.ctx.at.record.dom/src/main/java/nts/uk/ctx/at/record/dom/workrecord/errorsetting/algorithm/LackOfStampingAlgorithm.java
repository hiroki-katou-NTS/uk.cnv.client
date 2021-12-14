package nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.algorithm.GetWorkTypeServiceShare;
import nts.uk.shr.com.time.TimeWithDayAttr;

/*
 * 打刻漏れ - (出退勤打刻漏れ)
 */
@Stateless
public class LackOfStampingAlgorithm {

	@Inject
	private GetWorkTypeServiceShare  getWorkTypeService;
	
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;

	public Optional<EmployeeDailyPerError> lackOfStamping(String companyID, String employeeID, GeneralDate processingDate,
			WorkInfoOfDailyPerformance workInfoOfDailyPerformance,
			TimeLeavingOfDailyPerformance timeLeavingOfDailyPerformance) {

		EmployeeDailyPerError employeeDailyPerError = null;

		Optional<WorkType> workType = getWorkTypeService
				.getWorkType(workInfoOfDailyPerformance.getWorkInformation().getRecordInfo().getWorkTypeCode().v());
		if (!workType.isPresent())
			return Optional.empty();

		WorkStyle workStyle = workType.get().checkWorkDay();
		if (workStyle != WorkStyle.ONE_DAY_REST) {

			if (timeLeavingOfDailyPerformance != null && timeLeavingOfDailyPerformance.getAttendance()!=null
					&& !timeLeavingOfDailyPerformance.getAttendance().getTimeLeavingWorks().isEmpty()) {
				List<TimeLeavingWork> timeLeavingWorkList = timeLeavingOfDailyPerformance.getAttendance().getTimeLeavingWorks();
				List<Integer> attendanceItemIDList = new ArrayList<>();
				
				//所定時間設定を取得
				Optional<PredetemineTimeSetting> predetemineTimeSet = Optional.empty();
				if(workInfoOfDailyPerformance.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().isPresent()) {
					predetemineTimeSet = predetemineTimeSettingRepository.findByWorkTimeCode(
							companyID,
							workInfoOfDailyPerformance.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().get().v());
				}
				
				//所定労働時間帯の件数を取得
				int predTimeSpanCount = predetemineTimeSet.isPresent()
						? predetemineTimeSet.get().getTimezoneByAmPmAtrForCalc(workStyle.toAmPmAtr().orElse(AmPmAtr.ONE_DAY)).size()
						: 0;
				//１から所定労働時間帯の件数までループする
				for(int number = 1;number<=predTimeSpanCount;number++) { //start for 1
					boolean checkExist = false;
					WorkNo workNo = new WorkNo(number);
					Optional<TimeLeavingWork> timeLeavingWork = timeLeavingWorkList.stream().filter(t -> t.getWorkNo().equals(workNo)).findFirst();
					if(timeLeavingWork.isPresent()) {
						Optional<TimeWithDayAttr> attendanceTimeWithDay = timeLeavingWork.get().getAttendanceTime();
						Optional<TimeWithDayAttr> leavingTimeWithDay = timeLeavingWork.get().getLeaveTime();
						if (leavingTimeWithDay.isPresent() && !attendanceTimeWithDay.isPresent()) {
							if (timeLeavingWork.get().getWorkNo().v().intValue() == 1) {
								attendanceItemIDList.add(31);
							} else if (timeLeavingWork.get().getWorkNo().v().intValue() == 2) {
								attendanceItemIDList.add(41);
							}
						} else if (!leavingTimeWithDay.isPresent() && attendanceTimeWithDay.isPresent()) {
							if (timeLeavingWork.get().getWorkNo().v().intValue() == 1) {
								attendanceItemIDList.add(34);
							} else if (timeLeavingWork.get().getWorkNo().v().intValue() == 2) {
								attendanceItemIDList.add(44);
							}
						} else if (!leavingTimeWithDay.isPresent() && !attendanceTimeWithDay.isPresent()) {
							if (timeLeavingWork.get().getWorkNo().v().intValue() == 1) {
								attendanceItemIDList.add(31);
								attendanceItemIDList.add(34);
							} else if (timeLeavingWork.get().getWorkNo().v().intValue() == 2) {
								attendanceItemIDList.add(41);
								attendanceItemIDList.add(44);
							}
						}
						checkExist =true;
					}
					if(!checkExist) { //両方存在しない(không có cả 2)
						if (number == 1) {
							attendanceItemIDList.add(31);
							attendanceItemIDList.add(34);
						} else if (number == 2) {
							attendanceItemIDList.add(41);
							attendanceItemIDList.add(44);
						}
					}
				}//end for 1
				
				if (!attendanceItemIDList.isEmpty()) {
					employeeDailyPerError = new EmployeeDailyPerError(companyID, employeeID, processingDate,
							new ErrorAlarmWorkRecordCode("S001"), attendanceItemIDList);
				}
			}else {
				List<Integer> attendanceItemIDList = new ArrayList<>();
				attendanceItemIDList.add(31);
				attendanceItemIDList.add(34);
				employeeDailyPerError = new EmployeeDailyPerError(companyID, employeeID, processingDate,
						new ErrorAlarmWorkRecordCode("S001"), attendanceItemIDList);
			}
		}

		return Optional.ofNullable(employeeDailyPerError);
	}

}
