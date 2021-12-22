package nts.uk.file.at.app.export.dailyschedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.dailyattendanceitem.AttendanceResultImport;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.snapshot.DailySnapshotWork;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.snapshot.DailySnapshotWorkRepository;
import nts.uk.ctx.at.shared.dom.worktype.DailyWork;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.file.at.app.export.dailyschedule.totalsum.DayType;
import nts.uk.file.at.app.export.dailyschedule.totalsum.NumberOfItem;
import nts.uk.file.at.app.export.dailyschedule.totalsum.TotalCountDay;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.shr.com.context.AppContexts;

/**
 * Total day count class
 * @author HoangNDH
 *
 */
@Stateless
public class TotalDayCountWs {
	@Inject
	private AttendanceResultImportAdapter attendanceResultAdapter;
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@Inject
	private WorkInformationRepository workInformationRepository;
	
	@Inject
	private DailySnapshotWorkRepository dailySnapshotWorkRepository;
	
	/**
	 * 所定日数を計算する
	 * @param employeeId
	 * @param lstDate
	 */
	private TotalCountDay calculatePredeterminedDay(String employeeId, DateRange dateRange, TotalCountDay totalCountDay) {
		// ドメインモッ�「勤務種類」を取得す�
		List<WorkType> lstWorkType = workTypeRepository.findByCompanyId(AppContexts.user().companyId()).stream()
			.filter(data -> this.checkWorkType(data.getDailyWork())).collect(Collectors.toList());
		if (lstWorkType.size() > 0) {
			// ドメインモデル「日別勤怠のスナップショット.勤務情報」を取得する
			List<DailySnapshotWork> dailySnapshotWorks = this.dailySnapshotWorkRepository
					.find(Arrays.asList(employeeId), new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()));
			// 日別勤怠のスナップショット.勤務情報の勤務種類コードを取得する
			List<WorkTypeCode> snapshotWorkTypeCds = dailySnapshotWorks.stream()
					.map(data -> data.getSnapshot().getWorkInfo().getWorkTypeCode())
					.collect(Collectors.toList());
			// 日別勤怠のスナップショット.勤務情報の勤務種類コードの値と合致するか
			List<WorkTypeCode> workTypeCds = lstWorkType.stream().map(WorkType::getWorkTypeCode)
					.collect(Collectors.toList());
			double count = snapshotWorkTypeCds.stream()
					.filter(data -> workTypeCds.contains(data))
					.mapToDouble(data -> this.getDayCount(lstWorkType, data, true)).sum();
			totalCountDay.setPredeterminedDay(count);
		}
		else {
			totalCountDay.setPredeterminedDay(0d);
		}
		return totalCountDay;
	}
	
	/**
	 * 所定以外の日数を計算する
	 * @param employeeId
	 * @param dateRange
	 * @param dayType
	 * @return
	 */
	private TotalCountDay calculateNonPredeterminedDay(String employeeId, DateRange dateRange, TotalCountDay totalCountDay) {
		String companyId = AppContexts.user().companyId();
		
		List<WorkInfoOfDailyPerformance> workInformationList = this.workInformationRepository
				.findByPeriodOrderByYmd(employeeId, new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()));
		
		for (DayType dayType : DayType.values()) {
			// ドメインモデル「勤務種類」を取得する 
			List<WorkType> lstWorkType = workTypeRepository.findByCompanyId(companyId).stream()
					.filter(data -> this.checkWorkType(data.getDailyWork(), dayType)).collect(Collectors.toList());
			
			dateRange.toListDate().forEach(date -> {
				// ドメインモデル「日別実績の勤務情報」を取得する
				Optional<WorkInfoOfDailyPerformance> optWorkInfo = workInformationList.stream()
						.filter(data -> data.getYmd().equals(date)).findFirst();
				// 勤務種類コードを取得する 
				optWorkInfo.ifPresent(workInfo -> {
					Optional<WorkType> optWorkType = lstWorkType.stream()
							.filter(data -> data.getWorkTypeCode().equals(workInfo.getWorkInformation().getRecordInfo().getWorkTypeCode()))
							.findFirst();
					// 日別勤務表の勤務種類コードの値と合致するか
					if (optWorkType.isPresent()) {
						// 日数をカウントする 
						double dayCount = this.getDayCount(lstWorkType, workInfo.getWorkInformation().getRecordInfo().getWorkTypeCode(), false);
						switch (dayType) {
						case ATTENDANCE:
							totalCountDay.setWorkingDay(totalCountDay.getWorkingDay() + dayCount);
							break;
						case HOLIDAY:
							totalCountDay.setHolidayDay(totalCountDay.getHolidayDay() + dayCount);
							break;
						case ABSENCE:
							totalCountDay.setAbsenceDay(totalCountDay.getAbsenceDay() + dayCount);
							break;
						case ANNUAL_HOLIDAY:
							totalCountDay.setYearOffUsage(totalCountDay.getYearOffUsage() + dayCount);
							break;
						case OFF_WORK:
							totalCountDay.setOffDay(totalCountDay.getOffDay() + dayCount);
							break;
						case SPECIAL:
							totalCountDay.setSpecialHoliday(totalCountDay.getSpecialHoliday() + dayCount);
							break;
						case YEARLY_RESERVED:
							totalCountDay.setHeavyHolDay(totalCountDay.getHeavyHolDay() + dayCount);
							break;
						}
					}
				});
			});
		}
		return totalCountDay;
	}
	
	/**
	 * 回数を計算する
	 * @param employeeId
	 * @param dateRange
	 * @param totalCountDay
	 */
	private TotalCountDay calculateDayCount(String employeeId, DateRange dateRange, TotalCountDay totalCountDay) {
		// 勤�頛�ID = 592 or 598 or 604 or 610
		List<Integer> lstAttendanceId = new ArrayList<>();
		lstAttendanceId.add(592);
		lstAttendanceId.add(598);
		lstAttendanceId.add(604);
		lstAttendanceId.add(610);
		
		List<String> lstEmployeeId = new ArrayList<>();
		lstEmployeeId.add(employeeId);
		
		List<AttendanceResultImport> attendanceResultImport = attendanceResultAdapter.getValueOf(lstEmployeeId, new DatePeriod(dateRange.getStartDate(), dateRange.getEndDate()), lstAttendanceId);
		
		
		for (NumberOfItem item : NumberOfItem.values()) {
			List<GeneralDate> lstDate = dateRange.toListDate();
			
			// Variable to count by item through date list
			int dayCount = 0;
			
			for (GeneralDate currentDate : lstDate) {
				// Literally 0-1 result
				Optional<AttendanceResultImport> optAttendanceResult = attendanceResultImport.stream().filter(x -> x.getWorkingDate().compareTo(currentDate) == 0).findFirst();
				if (optAttendanceResult.isPresent()) {
					AttendanceResultImport attendanceResult = optAttendanceResult.get();
					
					// Count by item attendance id
					dayCount += attendanceResult.getAttendanceItems().stream().filter(x -> x.getItemId() == item.attendanceId).
									filter(x -> x.getValue() != null && Integer.parseInt(x.getValue()) != 0).count();
				}
			}
			
			switch (item) {
			case LATE_COME_1:
			case LATE_COME_2:
				totalCountDay.setLateComeDay(totalCountDay.getLateComeDay() + dayCount);
				break;
			case EARLY_LEAVE_1:
			case EARLY_LEAVE_2:
				totalCountDay.setEarlyLeaveDay(totalCountDay.getEarlyLeaveDay() + dayCount);
				break;
			}
		}
		return totalCountDay;
	}
	
	/**
	 * 日数計を合算す�
	 * @param employeeId
	 * @param dateRange
	 * @param totalCountDay
	 */
	public TotalCountDay calculateAllDayCount(String employeeId, DateRange dateRange, TotalCountDay totalCountDay) {
		totalCountDay = calculatePredeterminedDay(employeeId, dateRange, totalCountDay);
		totalCountDay = calculateNonPredeterminedDay(employeeId, dateRange, totalCountDay);
		totalCountDay = calculateDayCount(employeeId, dateRange, totalCountDay);
		return totalCountDay;
	}
	
	private boolean checkWorkType(DailyWork dailyWork) {
		switch (dailyWork.getWorkTypeUnit()) {
		case OneDay:
			return this.isPredetermineWorkType(dailyWork.getOneDay());
		case MonringAndAfternoon:
			return this.isPredetermineWorkType(dailyWork.getMorning()) || this.isPredetermineWorkType(dailyWork.getAfternoon());
		}
		return false;
	}
	
	private boolean checkWorkType(DailyWork dailyWork, DayType dayType) {
		switch (dailyWork.getWorkTypeUnit()) {
		case OneDay:
			return dayType.compareToWorkTypeCls(dailyWork.getOneDay());
		case MonringAndAfternoon:
			return dayType.compareToWorkTypeCls(dailyWork.getMorning())
					|| dayType.compareToWorkTypeCls(dailyWork.getAfternoon());
		}
		return false;
	}
	
	/**
	 * 勤務種類　＝　0：出勤、2：年休、3：積立年休、
　　　　　　　　 　4：特別休暇、5：欠勤、6：代休、
　　　　　　　　　 7：振出、9：時間消化休暇
	 */
	private boolean isPredetermineWorkType(WorkTypeClassification workTypeClassification) {
		return Arrays.asList(WorkTypeClassification.Attendance, WorkTypeClassification.AnnualHoliday,
				WorkTypeClassification.YearlyReserved, WorkTypeClassification.SpecialHoliday, WorkTypeClassification.SpecialHoliday,
				WorkTypeClassification.Absence, WorkTypeClassification.SubstituteHoliday, WorkTypeClassification.Shooting,
				WorkTypeClassification.TimeDigestVacation).contains(workTypeClassification);
	}
	
	/**
	 * Calculate day used for worktype (0/0.5/1)
	 * @param workTypes
	 * @param code
	 * @param isPredetermined	所定日数?
	 * @return
	 */
	private double getDayCount(List<WorkType> workTypes, WorkTypeCode code, boolean isPredetermined) {
		Optional<WorkType> optWorkType = workTypes.stream()
				.filter(data -> data.getWorkTypeCode().equals(code)).findFirst();
		if (optWorkType.isPresent()) {
			DailyWork dailyWork = optWorkType.get().getDailyWork();
			switch (dailyWork.getWorkTypeUnit()) {
			case OneDay:
				return 1.0d;
			case MonringAndAfternoon:
				// Calculate sum if 所定日数
				if (isPredetermined) {
					return (this.isPredetermineWorkType(dailyWork.getMorning()) ? 0.5d : 0d)
							+ (this.isPredetermineWorkType(dailyWork.getAfternoon()) ? 0.5d : 0d);
				}
				return 0.5d;
			}
		}
		return 0d;
	}
}
