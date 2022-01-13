package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.ReserveLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TemporaryTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.PCLogOnInfoOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.interval.IntervalTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemValueOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.snapshot.SnapShot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.AnnualOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.HolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SpecialHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SubstituteHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.TransferHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.YearlyReservedOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.shr.com.context.AppContexts;

/**
 * 月の計算中の日別実績データ
 * @author shuichi_ishida
 */
@Getter
@Setter
public class MonthlyCalculatingDailys {

	/** 日別実績の勤怠時間リスト */
	private Map<GeneralDate, AttendanceTimeOfDailyAttendance> attendanceTimeOfDailyMap;
	/** 日別実績の勤務情報リスト */
	private Map<GeneralDate, WorkInfoOfDailyAttendance> workInfoOfDailyMap;
	/** 日別実績の出退勤リスト */
	private Map<GeneralDate, TimeLeavingOfDailyAttd> timeLeaveOfDailyMap;
	/** 日別実績の臨時出退勤リスト */
	private Map<GeneralDate, TemporaryTimeOfDailyAttd> temporaryTimeOfDailyMap;
	/** 日別実績の特定日区分リスト */
	private Map<GeneralDate, SpecificDateAttrOfDailyAttd> specificDateAttrOfDailyMap;
	/** 社員の日別実績エラー一覧リスト */
	private List<EmployeeDailyPerError> employeeDailyPerErrorList;
	/** 日別実績の任意項目リスト */
	private Map<GeneralDate, AnyItemValueOfDailyAttd> anyItemValueOfDailyList;
	/** 日別実績のPCログオン情報リスト */
	private Map<GeneralDate, PCLogOnInfoOfDailyAttd> pcLogonInfoMap;
	/** 年休付与残数データリスト */
	private List<AnnualLeaveGrantRemainingData> grantRemainingDatas;
	/** 積立年休付与残数データリスト */
	private List<ReserveLeaveGrantRemainingData> rsvGrantRemainingDatas;
	/** 日別実績の所属情報 */
	private Map<GeneralDate, AffiliationInforOfDailyAttd> affiInfoOfDailyMap;

	private Map<GeneralDate, WorkingConditionItem> workConditions;

	private Map<GeneralDate, SnapShot> snapshots;

	public MonthlyCalculatingDailys(){
		this.attendanceTimeOfDailyMap = new HashMap<>();
		this.workInfoOfDailyMap = new HashMap<>();
		this.timeLeaveOfDailyMap = new HashMap<>();
		this.temporaryTimeOfDailyMap = new HashMap<>();
		this.specificDateAttrOfDailyMap = new HashMap<>();
		this.employeeDailyPerErrorList = new ArrayList<>();
		this.anyItemValueOfDailyList = new HashMap<>();
		this.pcLogonInfoMap = new HashMap<>();
		this.grantRemainingDatas = new ArrayList<>();
		this.rsvGrantRemainingDatas = new ArrayList<>();
		this.affiInfoOfDailyMap = new HashMap<>();
		this.workConditions = new HashMap<>();
		this.snapshots = new HashMap<>();
	}

	public List<IntegrationOfDaily> getDailyWorks(String sid) {

		return workInfoOfDailyMap.entrySet().stream().map(wi -> {

			return new IntegrationOfDaily(sid, wi.getKey(), wi.getValue(),
					null, affiInfoOfDailyMap.get(wi.getKey()),
					Optional.ofNullable(pcLogonInfoMap.get(wi.getKey())), new ArrayList<>(),
					Optional.empty(), new BreakTimeOfDailyAttd(new ArrayList<>()),
					Optional.ofNullable(attendanceTimeOfDailyMap.get(wi.getKey())),
					Optional.ofNullable(timeLeaveOfDailyMap.get(wi.getKey())), Optional.empty(),
					Optional.ofNullable(specificDateAttrOfDailyMap.get(wi.getKey())),
					Optional.empty(), Optional.ofNullable(anyItemValueOfDailyList.get(wi.getKey())),
					new ArrayList<>(), Optional.ofNullable(temporaryTimeOfDailyMap.get(wi.getKey())),
					new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
					Optional.ofNullable(snapshots.get(wi.getKey())));
		}).collect(Collectors.toList());
	}

	/**
	 * データ取得
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param repositories 月別集計が必要とするリポジトリ
	 * @return 月の計算中の日別実績データ
	 */
	public static MonthlyCalculatingDailys loadData(RequireM4 require, String employeeId,
			DatePeriod period, MonAggrEmployeeSettings settings){

		MonthlyCalculatingDailys result = load(require, employeeId, period);

		return correctExamDayTime(result, settings);
	}

	private static MonthlyCalculatingDailys load(RequireM4 require, String employeeId, DatePeriod period) {
		MonthlyCalculatingDailys result = new MonthlyCalculatingDailys();

		// 取得期間を　開始日-1月～終了日+1月　とする　（前月の最終週、36協定締め日違いの集計のため）
		DatePeriod findPeriod = new DatePeriod(period.start().addMonths(-1), period.end().addDays(31));

		// 日別実績の勤怠時間
		val attendanceTimeOfDailyList = require.dailyAttendanceTimes(employeeId, findPeriod);
		for (val attendanceTimeOfDaily : attendanceTimeOfDailyList.entrySet()){
			result.attendanceTimeOfDailyMap.putIfAbsent(attendanceTimeOfDaily.getKey(), attendanceTimeOfDaily.getValue());
		}

		// 共通処理
		result.loadDataCommon(require, employeeId, period);
		return result;
	}
	
	private static Map<String, MonthlyCalculatingDailys> loadClones(RequireM4 require, List<String> employeeId, DatePeriod period) {
		Map<String, MonthlyCalculatingDailys> results = new HashMap<String, MonthlyCalculatingDailys>();

		// 取得期間を　開始日-1月～終了日+1月　とする　（前月の最終週、36協定締め日違いの集計のため）
		DatePeriod findPeriod = new DatePeriod(period.start().addMonths(-1), period.end().addDays(31));

		// 日別実績の勤怠時間
		val attendanceTimeOfDailyLists = require.dailyAttendanceTimesclones(employeeId, findPeriod);
		
		for (val attendanceTimeOfDailyList : attendanceTimeOfDailyLists.entrySet()) {
			MonthlyCalculatingDailys result = new MonthlyCalculatingDailys();
			for (val attendanceTimeOfDaily : attendanceTimeOfDailyList.getValue().entrySet()){
				result.attendanceTimeOfDailyMap.putIfAbsent(attendanceTimeOfDaily.getKey(), attendanceTimeOfDaily.getValue());
			}
			// 共通処理
			result.loadDataCommon(require, attendanceTimeOfDailyList.getKey(), period);
			results.put(attendanceTimeOfDailyList.getKey(), result);
		}
		
		return results;
	}

	/**
	 * データ取得
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param attendanceTimeOfDailys 日別実績の勤怠時間リスト
	 * @return 月の計算中の日別実績データ
	 */
	public static MonthlyCalculatingDailys loadData(RequireM4 require, String employeeId, DatePeriod period,
			Map<GeneralDate, AttendanceTimeOfDailyAttendance> attendanceTimeOfDailys, MonAggrEmployeeSettings settings){

		// 期間内の全データ読み込み
		MonthlyCalculatingDailys result = MonthlyCalculatingDailys.loadData(require, employeeId, period, settings);

		// 日別実績の勤怠時間リストの指定がない時、終了
		if (attendanceTimeOfDailys.size() <= 0) return result;

		// 日別実績の勤怠時間を反映する
		for (val attendanceTimeOfDaily : attendanceTimeOfDailys.entrySet()){
			val ymd = attendanceTimeOfDaily.getKey();
			result.attendanceTimeOfDailyMap.put(ymd, attendanceTimeOfDaily.getValue());
		}

		return correctExamDayTime(result, settings);
	}

	/**
	 * データ取得
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param dailyWorksOpt 日別実績(WORK)リスト
	 * @return 月の計算中の日別実績データ
	 */
	public static MonthlyCalculatingDailys loadData(RequireM4 require, String employeeId, DatePeriod period,
			Optional<List<IntegrationOfDaily>> dailyWorksOpt, MonAggrEmployeeSettings settings){

		// 期間内の全データ読み込み
		MonthlyCalculatingDailys result = MonthlyCalculatingDailys.loadData(require, employeeId, period, settings);

		// 日別実績(WORK)指定がない時、終了
		if (!dailyWorksOpt.isPresent()) return result;

		for (val dailyWork : dailyWorksOpt.get()){

			// 日別実績の勤怠時間がない日は、反映しない
			if (!dailyWork.getAttendanceTimeOfDailyPerformance().isPresent()) continue;

			// 日別実績の勤怠時間
			val attendanceTimeOfDaily = dailyWork.getAttendanceTimeOfDailyPerformance().get();
			val ymd = dailyWork.getYmd();
			result.attendanceTimeOfDailyMap.put(ymd, attendanceTimeOfDaily);

			// 日別実績の出退勤
			if (dailyWork.getAttendanceLeave().isPresent()){
				val timeLeaveOfDaily = dailyWork.getAttendanceLeave().get();
				result.timeLeaveOfDailyMap.put(ymd, timeLeaveOfDaily);
			}

			// 日別実績の勤務情報
			if (dailyWork.getWorkInformation() != null){
				val workInfoOfDaily = dailyWork.getWorkInformation();
				result.workInfoOfDailyMap.put(ymd, workInfoOfDaily);
			}

			// 日別実績の臨時出退勤
			if (dailyWork.getTempTime().isPresent()){
				val temporaryTimeOfDaily = dailyWork.getTempTime().get();
				result.temporaryTimeOfDailyMap.put(ymd, temporaryTimeOfDaily);
			}

			// 日別実績の特定日区分
			if (dailyWork.getSpecDateAttr().isPresent()){
				val specificDateAttrOfDaily = dailyWork.getSpecDateAttr().get();
				result.specificDateAttrOfDailyMap.put(ymd, specificDateAttrOfDaily);
			}

			// 社員の日別実績エラー一覧
			if (dailyWork.getEmployeeError() != null){
				val itrPerError = result.employeeDailyPerErrorList.listIterator();
				while (itrPerError.hasNext()){
					val perError = itrPerError.next();
					if (perError == null){
						itrPerError.remove();
						continue;
					}
					if (perError.getDate() == null) continue;
					if (perError.getDate().compareTo(ymd) != 0) continue;
					itrPerError.remove();
				}
				result.employeeDailyPerErrorList.addAll(dailyWork.getEmployeeError());
			}

			// 日別実績の任意項目
			if (dailyWork.getAnyItemValue().isPresent()){
				result.anyItemValueOfDailyList.replace(ymd, dailyWork.getAnyItemValue().get());
			}

			// PCログオン情報
			if (dailyWork.getPcLogOnInfo().isPresent()){
				val pcLogonInfo = dailyWork.getPcLogOnInfo().get();
				result.pcLogonInfoMap.put(ymd, pcLogonInfo);
			}

			// 日別実績の勤務種別
			// Không còn domain này trong EA
//			if (dailyWork.getBusinessType().isPresent()) {
//				val workType = dailyWork.getBusinessType().get();
//				result.workTypeOfDailyMap.put(workType.getDate(), workType);
//			}
		}

		return correctExamDayTime(result, settings);

	}

	public static MonthlyCalculatingDailys create(RequireM4 require, String sid,
			DatePeriod period, List<IntegrationOfDaily> dailyWorks,
			WorkingConditionItem workingConditionItem) {

		MonthlyCalculatingDailys result;

		if (dailyWorks.isEmpty()) {

			result = MonthlyCalculatingDailys.load(require, sid, period);
		} else {

			result = createFrom(dailyWorks);
		}

		return correctExamDayTime(result, workingConditionItem);

	}
	
	public static Map<String, MonthlyCalculatingDailys> createClones(RequireM4 require, List<String> sid,
			DatePeriod period, List<IntegrationOfDaily> dailyWorks,
			List<WorkingConditionItem> workingConditionItems) {

		Map<String, MonthlyCalculatingDailys> data = new HashMap<String, MonthlyCalculatingDailys>();

		if (dailyWorks.isEmpty()) {

			data = MonthlyCalculatingDailys.loadClones(require, sid, period);
		} else {
			for (String id : sid) {
				data.put(id, createFrom(dailyWorks.stream().filter(c->c.getEmployeeId().equals(id)).collect(Collectors.toList())));
			}
		}
		Map<String, MonthlyCalculatingDailys> results = new HashMap<String, MonthlyCalculatingDailys>();
		for (val result : data.entrySet()) {
			Optional<WorkingConditionItem> workingConditionItem = workingConditionItems.stream().filter(c->c.getEmployeeId().equals(result.getKey())).findAny();
			if(workingConditionItem.isPresent()) {
				results.put(result.getKey(), correctExamDayTime(result.getValue(), workingConditionItem.get()));
			}
		}

		return results;

	}

	private static MonthlyCalculatingDailys createFrom(List<IntegrationOfDaily> dailyWorks) {
		// 期間内の全データ読み込み
		MonthlyCalculatingDailys result = new MonthlyCalculatingDailys();

		for (val dailyWork : dailyWorks){

			// 日別実績の勤怠時間がない日は、反映しない
			if (!dailyWork.getAttendanceTimeOfDailyPerformance().isPresent()) continue;

			// 日別実績の勤怠時間
			val attendanceTimeOfDaily = dailyWork.getAttendanceTimeOfDailyPerformance().get();
			val ymd = dailyWork.getYmd();
			result.attendanceTimeOfDailyMap.put(ymd, attendanceTimeOfDaily);

			// 日別実績の出退勤
			if (dailyWork.getAttendanceLeave().isPresent()){
				val timeLeaveOfDaily = dailyWork.getAttendanceLeave().get();
				result.timeLeaveOfDailyMap.put(ymd, timeLeaveOfDaily);
			}

			// 日別実績の勤務情報
			if (dailyWork.getWorkInformation() != null){
				val workInfoOfDaily = dailyWork.getWorkInformation();
				result.workInfoOfDailyMap.put(ymd, workInfoOfDaily);
			}

			// 日別実績の臨時出退勤
			if (dailyWork.getTempTime().isPresent()){
				val temporaryTimeOfDaily = dailyWork.getTempTime().get();
				result.temporaryTimeOfDailyMap.put(ymd, temporaryTimeOfDaily);
			}

			// 日別実績の特定日区分
			if (dailyWork.getSpecDateAttr().isPresent()){
				val specificDateAttrOfDaily = dailyWork.getSpecDateAttr().get();
				result.specificDateAttrOfDailyMap.put(ymd, specificDateAttrOfDaily);
			}

			// 社員の日別実績エラー一覧
			if (dailyWork.getEmployeeError() != null){
				val itrPerError = result.employeeDailyPerErrorList.listIterator();
				while (itrPerError.hasNext()){
					val perError = itrPerError.next();
					if (perError == null){
						itrPerError.remove();
						continue;
					}
					if (perError.getDate() == null) continue;
					if (perError.getDate().compareTo(ymd) != 0) continue;
					itrPerError.remove();
				}
				result.employeeDailyPerErrorList.addAll(dailyWork.getEmployeeError());
			}

			// 日別実績の任意項目
			if (dailyWork.getAnyItemValue().isPresent()){
				result.anyItemValueOfDailyList.replace(ymd, dailyWork.getAnyItemValue().get());
			}

			// PCログオン情報
			if (dailyWork.getPcLogOnInfo().isPresent()){
				val pcLogonInfo = dailyWork.getPcLogOnInfo().get();
				result.pcLogonInfoMap.put(ymd, pcLogonInfo);
			}
		}
		return result;
	}
	
	private static MonthlyCalculatingDailys correctExamDayTime(MonthlyCalculatingDailys calcDaily,
			MonAggrEmployeeSettings settings){


		for (Entry<GeneralDate, AttendanceTimeOfDailyAttendance> dailyAttenTime : calcDaily.attendanceTimeOfDailyMap.entrySet()) {

			GeneralDate currentDate = dailyAttenTime.getKey();

			if (calcDaily.workInfoOfDailyMap.containsKey(currentDate)) {
				WorkInfoOfDailyAttendance wi = calcDaily.workInfoOfDailyMap.get(currentDate);

				settings.getWorkingConditions().entrySet().stream()
				.filter(wc -> wc.getValue().contains(currentDate))
				.findFirst().flatMap(wc -> settings.getWorkingConditionItems().stream()
														.filter(wci -> wci.getHistoryId().equals(wc))
														.map(wci -> wci.getLaborSystem())
														.findFirst())
				.ifPresent(laborSystem -> {
					if (laborSystem == WorkingSystem.FLEX_TIME_WORK) {
						calcDaily.attendanceTimeOfDailyMap.put(currentDate,
								examDayTimeCorrect(dailyAttenTime.getValue(), wi));
					}
				});
			}
		}

		return calcDaily;
	}

	private static MonthlyCalculatingDailys correctExamDayTime(MonthlyCalculatingDailys calcDaily,
			WorkingConditionItem workingConditionItem){


		for (Entry<GeneralDate, AttendanceTimeOfDailyAttendance> dailyAttenTime : calcDaily.attendanceTimeOfDailyMap.entrySet()) {

			GeneralDate currentDate = dailyAttenTime.getKey();

			if (calcDaily.workInfoOfDailyMap.containsKey(currentDate)) {
				WorkInfoOfDailyAttendance wi = calcDaily.workInfoOfDailyMap.get(currentDate);

				if (workingConditionItem.getLaborSystem() == WorkingSystem.FLEX_TIME_WORK) {
					calcDaily.attendanceTimeOfDailyMap.put(currentDate,
							examDayTimeCorrect(dailyAttenTime.getValue(), wi));
				}
			}
		}

		return calcDaily;
	}

	/**
	 * 大塚カスタマイズ（試験日対応）
	 */
	public static AttendanceTimeOfDailyAttendance examDayTimeCorrect(AttendanceTimeOfDailyAttendance atTime,
			WorkInfoOfDailyAttendance workInfo) {

		/** 大塚モードかを確認する */
		if (!AppContexts.optionLicense().customize().ootsuka())
			return atTime;
		
		/** 就業時間帯が試験日かどうか判断 */
		if (workInfo.getRecordInfo().isExamWorkTime()) {

			/** フレックス計算に影響する項目を0：00にする */
			WorkScheduleTimeOfDaily correctedSche = new WorkScheduleTimeOfDaily(atTime.getWorkScheduleTimeOfDaily().getWorkScheduleTime(),
																				atTime.getWorkScheduleTimeOfDaily().getRecordPrescribedLaborTime());

			ActualWorkingTimeOfDaily beforeActualWork = atTime.getActualWorkingTimeOfDaily();
			ActualWorkingTimeOfDaily correctedActualWork = ActualWorkingTimeOfDaily.of(
										beforeActualWork.getConstraintDifferenceTime(),
										beforeActualWork.getConstraintTime(),
										beforeActualWork.getTimeDifferenceWorkingHours(),
										new TotalWorkingTime(beforeActualWork.getTotalWorkingTime().getTotalTime(),
												beforeActualWork.getTotalWorkingTime().getTotalCalcTime(),
												beforeActualWork.getTotalWorkingTime().getActualTime(),
												WithinStatutoryTimeOfDaily.createWithinStatutoryTimeOfDaily(
														new AttendanceTime(0),
														new AttendanceTime(0),
														beforeActualWork.getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getWithinPrescribedPremiumTime(),
														beforeActualWork.getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getWithinStatutoryMidNightTime(),
														AttendanceAmountDaily.ZERO),
												beforeActualWork.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily(),
												beforeActualWork.getTotalWorkingTime().getLateTimeOfDaily(),
												beforeActualWork.getTotalWorkingTime().getLeaveEarlyTimeOfDaily(),
												beforeActualWork.getTotalWorkingTime().getBreakTimeOfDaily(),
												beforeActualWork.getTotalWorkingTime().getOutingTimeOfDailyPerformance(),
												beforeActualWork.getTotalWorkingTime().getRaiseSalaryTimeOfDailyPerfor(),
												beforeActualWork.getTotalWorkingTime().getWorkTimes(),
												beforeActualWork.getTotalWorkingTime().getTemporaryTime(),
												beforeActualWork.getTotalWorkingTime().getShotrTimeOfDaily(),
												new HolidayOfDaily(
														beforeActualWork.getTotalWorkingTime().getHolidayOfDaily().getAbsence(),
														beforeActualWork.getTotalWorkingTime().getHolidayOfDaily().getTimeDigest(),
														new YearlyReservedOfDaily(new AttendanceTime(0)),
														new SubstituteHolidayOfDaily(new AttendanceTime(0),
																beforeActualWork.getTotalWorkingTime().getHolidayOfDaily().getSubstitute().getDigestionUseTime()),
														beforeActualWork.getTotalWorkingTime().getHolidayOfDaily().getOverSalary(),
														new SpecialHolidayOfDaily(new AttendanceTime(0),
																beforeActualWork.getTotalWorkingTime().getHolidayOfDaily().getSpecialHoliday().getDigestionUseTime()),
														new AnnualOfDaily(new AttendanceTime(0),
																beforeActualWork.getTotalWorkingTime().getHolidayOfDaily().getAnnual().getDigestionUseTime()),
														new TransferHolidayOfDaily(beforeActualWork.getTotalWorkingTime().getHolidayOfDaily().getTransferHoliday().getUseTime())),
												IntervalTimeOfDaily.empty()),
										beforeActualWork.getDivTime(),
										beforeActualWork.getPremiumTimeOfDailyPerformance());

			return new AttendanceTimeOfDailyAttendance(correctedSche, correctedActualWork,
														atTime.getStayingTime(), atTime.getBudgetTimeVariance(),
														atTime.getUnEmployedTime());
		}

		return atTime;
	}

	/**
	 * データ取得共通処理
	 * @param employeeId 社員ID
	 * @param period 期間
	 */
	public void loadDataCommon(RequireM1 require, String employeeId, DatePeriod period){

		List<String> employeeIds = new ArrayList<>();
		employeeIds.add(employeeId);

		// データ取得共通処理　（36協定時間用）
		this.loadDataCommonForAgreement(require, employeeId, period);

		// 取得期間を　開始日-1月～終了日+1月　とする　（前月の最終週、36協定締め日違いの集計のため）
		DatePeriod findPeriod = new DatePeriod(period.start().addMonths(-1), period.end().addDays(31));

		// 日別実績の出退勤
		val timeLeaveOfDailyList = require.dailyTimeLeavings(employeeId, findPeriod);
		for (val timeLeaveOfDaily : timeLeaveOfDailyList.entrySet()){
			this.timeLeaveOfDailyMap.putIfAbsent(timeLeaveOfDaily.getKey(), timeLeaveOfDaily.getValue());
		}

		// ※　以下は、期間外の配慮不要。

		// 日別実績の臨時出退勤
		val temporaryTimeOfDailys = require.dailyTemporaryTimes(employeeId, period);
		for (val temporaryTimeOfDaily : temporaryTimeOfDailys.entrySet()){
			val ymd = temporaryTimeOfDaily.getKey();
			this.temporaryTimeOfDailyMap.putIfAbsent(ymd, temporaryTimeOfDaily.getValue());
		}

		// 日別実績の特定日区分
		val specificDateAttrOfDailys = require.dailySpecificDates(employeeId, period);
		for (val specificDateAttrOfDaily : specificDateAttrOfDailys.entrySet()){
			val ymd = specificDateAttrOfDaily.getKey();
			this.specificDateAttrOfDailyMap.putIfAbsent(ymd, specificDateAttrOfDaily.getValue());
		}

		// 社員の日別実績エラー一覧
		this.employeeDailyPerErrorList = require.dailyEmpErrors(employeeId, period);
		val itrPerError = this.employeeDailyPerErrorList.listIterator();
		while (itrPerError.hasNext()){
			val perError = itrPerError.next();
			if (perError == null) itrPerError.remove();
		}

		// 日別実績の任意項目
		this.anyItemValueOfDailyList = require.dailyAnyItems(employeeIds, period);

		// PCログオン情報
		val pcLogonInfos = require.dailyPcLogons(employeeIds, period);
		for (val pcLogonInfo : pcLogonInfos.entrySet()){
			val ymd = pcLogonInfo.getKey();
			this.pcLogonInfoMap.putIfAbsent(ymd, pcLogonInfo.getValue());
		}

		// 年休付与残数データリスト
		this.grantRemainingDatas = require.annualLeaveGrantRemainingData(employeeId);
		// 積立年休付与残数データリスト
		this.rsvGrantRemainingDatas = require.reserveLeaveGrantRemainingData(employeeId);

		// 日別実績の所属情報
		val workTypes = require.dailyAffiliationInfors(employeeIds, period);
		for (val workType : workTypes.entrySet()){
			val ymd = workType.getKey();
			this.affiInfoOfDailyMap.putIfAbsent(ymd, workType.getValue());
		}

		val snapshots = require.snapshot(employeeId, period);
		for (val snapshot : snapshots.entrySet()){
			val ymd = snapshot.getKey();
			this.snapshots.putIfAbsent(ymd, snapshot.getValue());
		}
	}

	/**
	 * データ取得　（36協定時間用）
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @return 月の計算中の日別実績データ
	 */
	public static MonthlyCalculatingDailys loadDataForAgreement(RequireM3 require,
			String employeeId, DatePeriod period, MonAggrEmployeeSettings settings){

		MonthlyCalculatingDailys result = new MonthlyCalculatingDailys();

		// 取得期間を　開始日-1月～終了日+1月　とする　（前月の最終週、36協定締め日違いの集計のため）
		DatePeriod findPeriod = new DatePeriod(period.start().addMonths(-1), period.end().addDays(31));

		// 日別実績の勤怠時間
		val attendanceTimeOfDailyList = require.dailyAttendanceTimes(employeeId, findPeriod);
		for (val attendanceTimeOfDaily : attendanceTimeOfDailyList.entrySet()){
			result.attendanceTimeOfDailyMap.putIfAbsent(attendanceTimeOfDaily.getKey(), attendanceTimeOfDaily.getValue());
		}

		// データ取得共通処理　（36協定時間用）
		result.loadDataCommonForAgreement(require, employeeId, period);

		return correctExamDayTime(result, settings);
	}

	/**
	 * データ取得　（36協定時間用）
	 * @param employeeId 社員ID
	 * @param period 期間
	 * @param attendanceTimeOfDailys 日別実績の勤怠時間リスト
	 * @return 月の計算中の日別実績データ
	 */
	public static MonthlyCalculatingDailys loadDataForAgreement(RequireM3 require, String employeeId,
			DatePeriod period, Map<GeneralDate, AttendanceTimeOfDailyAttendance> attendanceTimeOfDailys,
			MonAggrEmployeeSettings settings){

		MonthlyCalculatingDailys result = new MonthlyCalculatingDailys();

		// 取得期間を　開始日-1月～終了日+1月　とする　（前月の最終週、36協定締め日違いの集計のため）
		DatePeriod findPeriod = new DatePeriod(period.start().addMonths(-1), period.end().addDays(31));

		// 日別実績の勤怠時間
		val attendanceTimeOfDailyList = require.dailyAttendanceTimes(employeeId, findPeriod);
		for (val attendanceTimeOfDaily : attendanceTimeOfDailyList.entrySet()){
			result.attendanceTimeOfDailyMap.putIfAbsent(attendanceTimeOfDaily.getKey(), attendanceTimeOfDaily.getValue());
		}

		// 指定の日別実績の勤怠時間リストを上書き反映する
		for (val attendanceTimeOfDaily : attendanceTimeOfDailys.entrySet()){
			result.attendanceTimeOfDailyMap.put(attendanceTimeOfDaily.getKey(), attendanceTimeOfDaily.getValue());
		}

		// データ取得共通処理　（36協定時間用）
		result.loadDataCommonForAgreement(require, employeeId, period);

		return correctExamDayTime(result, settings);
	}

	/**
	 * データ取得共通処理　（36協定時間用）
	 * @param employeeId 社員ID
	 * @param period 期間
	 */
	public void loadDataCommonForAgreement(RequireM2 require, String employeeId, DatePeriod period){

		// 取得期間を　開始日-1月～終了日+1月　とする　（前月の最終週、36協定締め日違いの集計のため）
		DatePeriod findPeriod = new DatePeriod(period.start().addMonths(-1), period.end().addDays(31));

		// 日別実績の勤務情報
		val workInfoOfDailyList = require.dailyWorkInfos(employeeId, findPeriod);
		for (val workInfoOfDaily : workInfoOfDailyList.entrySet()){
			this.workInfoOfDailyMap.putIfAbsent(workInfoOfDaily.getKey(), workInfoOfDaily.getValue());
		}
	}

	/**
	 * 期間内の日別実績の勤怠時間が存在するか
	 * @param period 期間
	 * @return true：存在する、false：存在しない
	 */
	public boolean isExistDailyRecord(DatePeriod period){

		for (val attendanceTimeOfDaily : this.attendanceTimeOfDailyMap.entrySet()){
			val ymd = attendanceTimeOfDaily.getKey();
			if (!period.contains(ymd)) continue;
			return true;
		}
		return false;
	}

	public static interface RequireM3 extends RequireM2 {

		Map<GeneralDate, AttendanceTimeOfDailyAttendance> dailyAttendanceTimes(String employeeId, DatePeriod datePeriod);
		
		Map<String, Map<GeneralDate, AttendanceTimeOfDailyAttendance>> dailyAttendanceTimesclones(List<String> employeeId, DatePeriod datePeriod);
	}

	public static interface RequireM2 {

		Map<GeneralDate, WorkInfoOfDailyAttendance> dailyWorkInfos(String employeeId, DatePeriod datePeriod);
	}

	public static interface RequireM4 extends RequireM1, RequireM3 {}

	public static interface RequireM1 extends RequireM2 {

		Map<GeneralDate, SnapShot> snapshot(String employeeId, DatePeriod datePeriod);

		Map<GeneralDate, TimeLeavingOfDailyAttd> dailyTimeLeavings(String employeeId, DatePeriod datePeriod);

		Map<GeneralDate, TemporaryTimeOfDailyAttd> dailyTemporaryTimes(String employeeId, DatePeriod datePeriod);

		Map<GeneralDate, SpecificDateAttrOfDailyAttd> dailySpecificDates(String employeeId, DatePeriod datePeriod);

		List<EmployeeDailyPerError> dailyEmpErrors(String employeeId, DatePeriod datePeriod);

		Map<GeneralDate, AnyItemValueOfDailyAttd> dailyAnyItems(List<String> employeeId, DatePeriod baseDate);

		Map<GeneralDate, PCLogOnInfoOfDailyAttd> dailyPcLogons(List<String> employeeId, DatePeriod baseDate);

		List<AnnualLeaveGrantRemainingData> annualLeaveGrantRemainingData(String employeeId);

		List<ReserveLeaveGrantRemainingData> reserveLeaveGrantRemainingData(String employeeId);

		Map<GeneralDate, AffiliationInforOfDailyAttd> dailyAffiliationInfors(List<String> employeeId, DatePeriod baseDate);
	}
}