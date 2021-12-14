package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.val;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.AbsenceLeaveRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainData;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 集計前の月別実績データ
 * @author shuichi_ishida
 */
@Getter
public class MonthlyOldDatas {

	/** 月別実績の勤怠時間 */
	private Optional<AttendanceTimeOfMonthly> attendanceTime;
	/** 月別実績の任意項目 */
	private List<AnyItemOfMonthly> anyItemList;
	/** 年休月別残数データ */
	private Optional<AnnLeaRemNumEachMonth> annualLeaveRemain;
	/** 積立年休残数月別データ */
	private Optional<RsvLeaRemNumEachMonth> reserveLeaveRemain;
	/** 振休月別残数データ */
	private Optional<AbsenceLeaveRemainData> absenceLeaveRemain;
	/** 代休月別残数データ */
	private Optional<MonthlyDayoffRemainData> monthlyDayoffRemain;
	/** 特別休暇月別残数データ */
	private List<SpecialHolidayRemainData> specialLeaveRemainList;
	
	public MonthlyOldDatas(){
		this.attendanceTime = Optional.empty();
		this.anyItemList = new ArrayList<>();
		this.annualLeaveRemain = Optional.empty();
		this.reserveLeaveRemain = Optional.empty();
		this.absenceLeaveRemain = Optional.empty();
		this.monthlyDayoffRemain = Optional.empty();
		this.specialLeaveRemainList = new ArrayList<>();
	}

	/**
	 * データ取得
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param closureDate 締め日付
	 * @param repositories 月別集計が必要とするリポジトリ
	 * @return 集計前の月別実績データ
	 */
	public static MonthlyOldDatas loadData(
			RequireM1 require, String employeeId, YearMonth yearMonth, 
			ClosureId closureId, ClosureDate closureDate){

		MonthlyOldDatas result = new MonthlyOldDatas();
		
		// 月別実績の勤怠時間
		result.attendanceTime = require.attendanceTimeOfMonthly(employeeId, yearMonth, closureId, closureDate);
		
		// 月別実績の任意項目
		result.anyItemList = require.anyItemOfMonthly(employeeId, yearMonth, closureId, closureDate);
		
		// 年休月別残数データ
		result.annualLeaveRemain = require.annLeaRemNumEachMonth(employeeId, yearMonth, closureId, closureDate);
		
		// 積立年休月別残数データ
		result.reserveLeaveRemain = require.rsvLeaRemNumEachMonth(employeeId, yearMonth, closureId, closureDate);
		
		// 振休月別残数データ
		result.absenceLeaveRemain = require.absenceLeaveRemainData(employeeId, yearMonth, closureId, closureDate);
		
		// 代休月別残数データ
		result.monthlyDayoffRemain = require.monthlyDayoffRemainData(employeeId, yearMonth, closureId, closureDate);
		
		// 特別休暇月別残数データ
		result.specialLeaveRemainList = require.specialHolidayRemainData(employeeId, yearMonth, closureId, closureDate);
		
		return result;
	}

	/**
	 * データ取得
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param closureDate 締め日付
	 * @param monthlyWorkOpt 月別実績(WORK)
	 * @param repositories 月別集計が必要とするリポジトリ
	 * @return 集計前の月別実績データ
	 */
	public static MonthlyOldDatas loadData(RequireM1 require, String employeeId, YearMonth yearMonth, 
			ClosureId closureId, ClosureDate closureDate, Optional<IntegrationOfMonthly> monthlyWorkOpt){

		// 月別実績(WORK)指定がない時、データ読み込み
		if (!monthlyWorkOpt.isPresent()){
			return MonthlyOldDatas.loadData(require, employeeId, yearMonth, closureId, closureDate);
		}
		
		MonthlyOldDatas result = new MonthlyOldDatas();
		val monthlyWork = monthlyWorkOpt.get();
		
		// 月別実績の勤怠時間
		result.attendanceTime = monthlyWork.getAttendanceTime();
		
		// 月別実績の任意項目
		result.anyItemList = monthlyWork.getAnyItemList();
		
		// 年休月別残数データ
		result.annualLeaveRemain = monthlyWork.getAnnualLeaveRemain();
		
		// 積立年休月別残数データ
		result.reserveLeaveRemain = monthlyWork.getReserveLeaveRemain();
		
		// 振休月別残数データ
		result.absenceLeaveRemain = monthlyWork.getAbsenceLeaveRemain();
		
		// 代休月別残数データ
		result.monthlyDayoffRemain = monthlyWork.getMonthlyDayoffRemain();
		
		// 特別休暇月別残数データ
		result.specialLeaveRemainList = monthlyWork.getSpecialLeaveRemain();
		
		return result;
	}
	
	public static interface RequireM1 {
		
		Optional<AttendanceTimeOfMonthly> attendanceTimeOfMonthly(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);

		List<AnyItemOfMonthly> anyItemOfMonthly(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		Optional<AnnLeaRemNumEachMonth> annLeaRemNumEachMonth(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		Optional<RsvLeaRemNumEachMonth> rsvLeaRemNumEachMonth(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		Optional<AbsenceLeaveRemainData> absenceLeaveRemainData(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		Optional<MonthlyDayoffRemainData> monthlyDayoffRemainData(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		List<SpecialHolidayRemainData> specialHolidayRemainData(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
	}
}