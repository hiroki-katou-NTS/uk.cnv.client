package nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.val;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.vacation.absenceleave.monthremaindata.AbsenceLeaveRemainData;
import nts.uk.ctx.at.record.dom.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.record.dom.monthly.vacation.dayoff.monthremaindata.MonthlyDayoffRemainData;
import nts.uk.ctx.at.record.dom.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.record.dom.monthly.vacation.specialholiday.monthremaindata.SpecialHolidayRemainData;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.IntegrationOfMonthly;
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
			Require require, String employeeId, YearMonth yearMonth, 
			ClosureId closureId, ClosureDate closureDate){

		MonthlyOldDatas result = new MonthlyOldDatas();
		
		// 月別実績の勤怠時間
		result.attendanceTime = require.monthAttendanceTime(
				employeeId, yearMonth, closureId, closureDate);
		
		// 月別実績の任意項目
		result.anyItemList = require.monthAnyItems(
				employeeId, yearMonth, closureId, closureDate);
		
		// 年休月別残数データ
		result.annualLeaveRemain = require.monthAnnRemain(
				employeeId, yearMonth, closureId, closureDate);
		
		// 積立年休月別残数データ
		result.reserveLeaveRemain = require.monthRsvRemain(
				employeeId, yearMonth, closureId, closureDate);
		
		// 振休月別残数データ
		result.absenceLeaveRemain = require.monthAbsLeaveRemain(
				employeeId, yearMonth, closureId, closureDate);
		
		// 代休月別残数データ
		result.monthlyDayoffRemain = require.monthDayOffRemain(
				employeeId, yearMonth, closureId, closureDate);
		
		// 特別休暇月別残数データ
		result.specialLeaveRemainList = require.monthSpecialHolRemain(
				employeeId, yearMonth, closureId, closureDate);
		
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
	public static MonthlyOldDatas loadData(
			Require require, String employeeId, YearMonth yearMonth, 
			ClosureId closureId, ClosureDate closureDate,
			Optional<IntegrationOfMonthly> monthlyWorkOpt){

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
		result.specialLeaveRemainList = monthlyWork.getSpecialLeaveRemainList();
		
		return result;
	}
	
	public static interface Require{
		
		Optional<AttendanceTimeOfMonthly> monthAttendanceTime(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);

		List<AnyItemOfMonthly> monthAnyItems(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		Optional<AnnLeaRemNumEachMonth> monthAnnRemain(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		Optional<RsvLeaRemNumEachMonth> monthRsvRemain(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		Optional<AbsenceLeaveRemainData> monthAbsLeaveRemain(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		Optional<MonthlyDayoffRemainData> monthDayOffRemain(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
		
		List<SpecialHolidayRemainData> monthSpecialHolRemain(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
	}
}