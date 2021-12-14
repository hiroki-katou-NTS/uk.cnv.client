package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workrule.closure.PresentClosingPeriodImport;
import nts.uk.ctx.at.record.dom.adapter.workschedule.WorkScheduleWorkInforImport;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.TypeOfDays;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.export.attdstatus.AttendanceStatusList;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.WorkTypeDaysCountTable;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.VacationAddSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.WorkDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workrule.workdeadline.algorithm.MonthIsBeforeThisMonthChecking;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト.スケジュール日次・月次・年間.スケジュール月次のアラームリストのチェック条件.アルゴリズム.使用しない.特定属性の項目の予定を作成する.日数チェック条件をチェック.休暇日数を計算
 */
@Stateless
public class CalculateVacationDayService {
	@Inject
	private MonthIsBeforeThisMonthChecking monthIsBeforeThisMonthChecking;
	@Inject
	private GetWorkTypeService getWorkTypeService;
	@Inject 
	private RecordDomRequireService requireService;
	@Inject
	private PredetemineTimeSettingRepository predTimeSetRepo;
	
	/**
	 * 休暇日数を計算
	 * 
	 * @param cid 会社ID
	 * @param sid 社員ID
	 * @param ym 年月
	 * @param presentClosingPeriod 現在の締め期間
	 * @param attendanceTimeOfMonthly 月別実績の勤怠時間
	 * @param lstDaily List＜日別実績＞
	 * @param lstWorkSchedule  List＜勤務予定＞
	 * @param typeOfDay 日数の種類
	 * @param lstWorkType List＜勤務種類＞
	 * 
	 */
	public Double calVacationDay(String cid, String sid, YearMonth ym, PresentClosingPeriodImport presentClosingPeriod,
			AttendanceTimeOfMonthly attendanceTimeOfMonthly, List<IntegrationOfDaily> lstDaily,
			List<WorkScheduleWorkInforImport> lstWorkSchedule, TypeOfDays typeOfDay, List<WorkType> lstWorkType,
			Map<DatePeriod, WorkingConditionItem> workingConditionItemMap) {
		Double totalTime = 0.0;
		
		if (presentClosingPeriod != null) {
			// 当月より前の月かチェック
			boolean isBeforeThisMonth = monthIsBeforeThisMonthChecking.checkMonthIsBeforeThisMonth(ym,
					presentClosingPeriod.getProcessingYm());
			if (isBeforeThisMonth) {
				if (attendanceTimeOfMonthly == null) {
					return totalTime;
				}
				
				// 日数 を計算
				WorkDaysOfMonthly workDays = attendanceTimeOfMonthly.getVerticalTotal().getWorkDays();
				return getNumberOfDays(typeOfDay, workDays);
			}
		}

		// Input．年月の開始日から終了日までループする
		DatePeriod dPeriod = new DatePeriod(ym.firstGeneralDate(), ym.lastGeneralDate());
		List<GeneralDate> listDate = dPeriod.datesBetween();
		for(int day = 0; day < listDate.size(); day++) {
			GeneralDate exDate = listDate.get(day);
			ScheMonWorkTypeWorkTime monWorkTypeWorkTime = getWorkTypeService.getWorkTypeCode(exDate, lstWorkSchedule, lstDaily);
			if (monWorkTypeWorkTime == null) {
				continue;
			}
			
			Optional<WorkType> workTypeOpt = lstWorkType.stream().filter(x -> x.getWorkTypeCode().v().equals(monWorkTypeWorkTime.getWorkTypeCode())).findFirst();
			Optional<IntegrationOfDaily> dailyOpt = lstDaily.stream().filter(x -> x.getYmd().equals(exDate)).findFirst();
			WorkingConditionItem workingCondtionItem = null;
			Optional<DatePeriod> condExDatePeriod = workingConditionItemMap.keySet().stream().filter(x -> x.start().beforeOrEquals(exDate) && x.end().afterOrEquals(exDate)).findFirst();
			if (!condExDatePeriod.isPresent()) {
				continue;
			}
			
			if (!monWorkTypeWorkTime.getWorkTimeCode().isPresent()) {
				continue;
			}
			
			workingCondtionItem = workingConditionItemMap.get(condExDatePeriod.get());
			Optional<PredetemineTimeSetting> predTimeSetInDayOpt = this.predTimeSetRepo.findByWorkTimeCode(cid, monWorkTypeWorkTime.getWorkTimeCode().get());
			
			if (!workTypeOpt.isPresent() || !dailyOpt.isPresent() || !predTimeSetInDayOpt.isPresent() || workingCondtionItem == null) {
				continue;
			}
			
			// Input．日数の種類をチェック
			totalTime += getNumberOfDays(typeOfDay, workTypeOpt.get(), dailyOpt.get(), workingCondtionItem, predTimeSetInDayOpt.get());
		}
		
		return totalTime;
	}

	/**
	 * 日数 を計算
	 * 
	 * @param typeOfDay
	 *            日数の種類
	 * @param attendanceTimeOfMonthly
	 *            月別実績の勤怠時間
	 */
	private Double getNumberOfDays(TypeOfDays typeOfDay, WorkDaysOfMonthly workDays) {
		Double numberOfDays = 0.0;

		switch (typeOfDay) {
		case WORKING_DAY_NUMBER:
			// Input．月別実績の勤怠時間．縦計．勤務日数．出勤日数．日数
			numberOfDays = workDays.getAttendanceDays().getDays().v();
			break;
		case HOLIDAY_NUMBER:
			// Input．月別実績の勤怠時間．縦計．勤務日数．休日日数．日数
			numberOfDays = workDays.getHolidayDays().getDays().v();
			break;
		case DAYOFF_NUMBER:
			// Input．月別実績の勤怠時間．縦計．勤務日数．休出日数．日数
			numberOfDays = workDays.getHolidayWorkDays().getDays().v();
			break;
		case SPECIAL_HOLIDAY_NUMBER:
			// Input．月別実績の勤怠時間．縦計．勤務日数．特別休暇日数．特別休暇合計日数
			numberOfDays = workDays.getSpecialVacationDays().getTotalSpcVacationDays().v();
			break;
		case ABSENTEEDAY_NUMBER:
			// Input．月別実績の勤怠時間．縦計．勤務日数．欠勤．欠勤合計日数
			numberOfDays = workDays.getAbsenceDays().getTotalAbsenceDays().v();
			break;
		case SCHE_WORKINGD_AND_WORKINGD:
			// Input．月別実績の勤怠時間．縦計．勤務日数．勤務日数．日数
			numberOfDays = workDays.getWorkDays().getDays().v();
			break;
		default:
			break;
		}

		return numberOfDays;
	}
	
	private Double getNumberOfDays(
			TypeOfDays typeOfDay,
			WorkType workType,
			IntegrationOfDaily daily,
			WorkingConditionItem workingCondtionItem,
			PredetemineTimeSetting predetemineTimeSetting) {
		
		WorkDaysOfMonthly workDays = new WorkDaysOfMonthly();
		WorkTypeDaysCountTable workTypeDaysCountTable = new WorkTypeDaysCountTable(
				workType, new VacationAddSet(), Optional.empty());
		Map<GeneralDate, AttendanceTimeOfDailyAttendance> attendanceTimeOfDailys = new HashMap<>();
		Map<GeneralDate, TimeLeavingOfDailyAttd> timeLeavingOfDailys = new HashMap<>();
		daily.getAttendanceTimeOfDailyPerformance().ifPresent(c -> attendanceTimeOfDailys.put(daily.getYmd(), c));
		daily.getAttendanceLeave().ifPresent(c -> timeLeavingOfDailys.put(daily.getYmd(), c));
		// 出勤状態を取得する
		AttendanceStatusList attendanceStatusList = new AttendanceStatusList(attendanceTimeOfDailys, timeLeavingOfDailys);
		
		val require = requireService.createRequire();
				
		workDays.aggregate(
				require, daily.getEmployeeId(), AppContexts.user().companyId(),
				daily.getYmd(),
				workingCondtionItem.getLaborSystem(), 
				workType, 
				daily.getAttendanceTimeOfDailyPerformance().get(), 
				daily.getSpecDateAttr().get(),
				workTypeDaysCountTable, 
				daily.getWorkInformation(), 
				predetemineTimeSetting,
				attendanceStatusList.isAttendanceDay(daily.getYmd()), //confirm with Du san
				attendanceStatusList.isTwoTimesStampExists(daily.getYmd())); //confirm with Du san

		return getNumberOfDays(typeOfDay, workDays);
	}
}
