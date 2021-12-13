package nts.uk.ctx.at.function.dom.adapter.alarm;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.function.dom.adapter.WorkPlaceHistImport;
import nts.uk.ctx.at.function.dom.adapter.companyRecord.StatusOfEmployeeAdapter;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.annual.ScheduleAnnualAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.schedaily.ScheduleDailyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.schemonthly.ScheduleMonthlyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.weekly.WeeklyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.daily.DailyAlarmCondition;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.*;

public interface AlarmListPersonServiceAdapter {
	/**
	 * マスタチェック
	 * @param cid
	 * @param lstSid
	 * @param dPeriod
	 * @param errorMasterCheckId
	 * @param lstWplHist
	 * @param lstStatusEmp
	 * @param lstResultCondition
	 * @param lstCheckInfor
	 */
	void extractMasterCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			String errorMasterCheckId, List<WorkPlaceHistImport> lstWplHist,
			List<StatusOfEmployeeAdapter> lstStatusEmp,List<ResultOfEachCondition> lstResultCondition,
			List<AlarmListCheckInfor> lstCheckInfor, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode);
	
	/**
	 * 日次チェック
	 * @param cid
	 * @param lstSid
	 * @param dPeriod
	 * @param errorDailyCheckId
	 * @param dailyAlarmCondition
	 * @param lstStatusEmp
	 * @param lstResultCondition
	 * @param lstCheckType
	 */
	void extractDailyCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			String errorDailyCheckId, DailyAlarmCondition dailyAlarmCondition,
			List<WorkPlaceHistImport> getWplByListSidAndPeriod,
			List<StatusOfEmployeeAdapter> lstStatusEmp,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode);
	/**
	 * 月次
	 * @param cid
	 * @param lstSid
	 * @param mPeriod
	 * @param fixConId
	 * @param lstAnyConID
	 * @param lstWplHist
	 * @param lstResultCondition
	 * @param lstCheckInfor
	 */
	void extractMonthCheckResult(String cid, List<String> lstSid, YearMonthPeriod mPeriod, String fixConId,
			List<String> lstAnyConID, List<WorkPlaceHistImport> lstWplHist,
			List<ResultOfEachCondition> lstResultCondition,
			List<AlarmListCheckInfor> lstCheckInfor, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode);
	/**
	 * 複数月次
	 * @param cid
	 * @param lstSid
	 * @param mPeriod
	 * @param lstAnyConID
	 * @param lstWplHist
	 * @param lstResultCondition
	 * @param lstCheckInfor
	 */
	void extractMultiMonthCheckResult(String cid, List<String> lstSid, YearMonthPeriod mPeriod,
			List<String> lstAnyConID, 
			List<WorkPlaceHistImport> lstWplHist,
			List<ResultOfEachCondition> lstResultCondition,
			List<AlarmListCheckInfor> lstCheckInfor, List<AlarmEmployeeList> alarmEmployeeList,
			 List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode, Consumer<Integer> counter,
				Supplier<Boolean> shouldStop);
	
	/**
	 * スケジュール日
	 * @param cid
	 * @param lstSid
	 * @param dPeriod
	 * @param errorDailyCheckId
	 * @param scheDailyAlarmCondition
	 * @param getWplByListSidAndPeriod
	 * @param lstStatusEmp
	 * @param lstResultCondition
	 * @param lstCheckType
	 * @param counter
	 * @param shouldStop
	 */
	void extractScheDailyCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod, 
			String errorDailyCheckId, ScheduleDailyAlarmCheckCond scheDailyAlarmCondition,
			List<WorkPlaceHistImport> getWplByListSidAndPeriod, 
			List<StatusOfEmployeeAdapter> lstStatusEmp, 
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode);
	
	/**
	 * スケジュール年間
	 */
	void extractScheYearCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod, 
			String errorCheckId, ScheduleAnnualAlarmCheckCond scheYearAlarmCondition,
			List<WorkPlaceHistImport> wplByListSidAndPeriod, 
			List<StatusOfEmployeeAdapter> lstStatusEmp,
			List<ResultOfEachCondition> lstResultCondition, 
			List<AlarmListCheckInfor> lstCheckType, 
			Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode);
	
	/**
	 * スケジュール月次
	 *
	 */
	void extractScheMonCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,	
			String errorCheckId, ScheduleMonthlyAlarmCheckCond scheduleMonthlyAlarmCheckCond,
			List<WorkPlaceHistImport> getWplByListSidAndPeriod, 
			List<StatusOfEmployeeAdapter> lstStatusEmp, List<ResultOfEachCondition> lstResultCondition, 
			List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode);
	
	/**
	 * 週次
	 *
	 */
	void extractWeeklyCheckResult(
			String cid,
			List<String> lstSid,
			DatePeriod period,
			List<WorkPlaceHistImport> wplByListSidAndPeriods,
			WeeklyAlarmCheckCond weeklyAlarmCheckCond,
			List<ResultOfEachCondition> lstResultCondition,
			List<AlarmListCheckInfor> lstCheckType,
			Consumer<Integer> counter,
			Supplier<Boolean> shouldStop,
			List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode);
}
