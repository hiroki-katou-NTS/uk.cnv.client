package nts.uk.ctx.at.record.pubimp.workrecord.erroralarm;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.daily.DailyCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.MasterCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.StatusOfEmployeeAdapterAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceHistImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.monthlycheckcondition.algorithm.MonthlyExtractCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.multimonth.algorithm.MultiMonthlyExtractCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.annual.ScheYearCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.ScheDailyCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.ScheMonCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.weekly.algorithm.WeeklyCheckService;
import nts.uk.ctx.at.record.pub.workrecord.erroralarm.AlarmListPersonExtractServicePub;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmEmployeeList;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmExtractionCondition;

@Stateless
public class AlarmListPersonExtractServicePubImpl implements AlarmListPersonExtractServicePub{
	
	@Inject
	private MasterCheckService masterCheck;
	
	@Inject
	private MonthlyExtractCheckService monthCheck;
	
	@Inject
	private MultiMonthlyExtractCheckService multiMonthCheck;
	
	@Inject
	private DailyCheckService dailyCheck;

	@Inject
	private ScheDailyCheckService scheDailyCheckService;

	@Inject
	private ScheYearCheckService scheYearCheckService;

	@Inject
	private ScheMonCheckService scheMonCheckService;

	@Inject
	private WeeklyCheckService weeklyCheckService;

	@Override
	public void extractMasterCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			String errorMasterCheckId,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<StatusOfEmployeeAdapterAl> lstStatusEmp,
			List<ResultOfEachCondition> lstResultCondition, 
			List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		masterCheck.extractMasterCheck(cid, lstSid, dPeriod, 
				errorMasterCheckId, getWplByListSidAndPeriod,
				lstStatusEmp, lstResultCondition, lstCheckType, counter, shouldStop,
				alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
		
	}

	
	@Override
	public void extractDailyCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			String errorDailyCheckId, List<String> extractConditionWorkRecord, List<String> errorDailyCheckCd,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<StatusOfEmployeeAdapterAl> lstStatusEmp, List<ResultOfEachCondition> lstResultCondition,
			List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions,
			String alarmCheckConditionCode) {
		
		dailyCheck.extractDailyCheck(cid, lstSid, dPeriod, errorDailyCheckId, extractConditionWorkRecord, 
				errorDailyCheckCd, getWplByListSidAndPeriod, lstStatusEmp,
				lstResultCondition, lstCheckType, counter, shouldStop, alarmEmployeeList,
				alarmExtractConditions, alarmCheckConditionCode);
		
	}

	@Override
	public void extractMonthlyCheckResult(String cid, List<String> lstSid, YearMonthPeriod mPeriod, String fixConId,
			List<String> lstAnyConID, List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		monthCheck.extractMonthlyAlarm(cid,
				lstSid,
				mPeriod,
				fixConId,
				lstAnyConID, 
				getWplByListSidAndPeriod,
				lstResultCondition,
				lstCheckType,
				counter,
				shouldStop,
				alarmEmployeeList,
				alarmExtractConditions,
				alarmCheckConditionCode);
		
	}


	@Override
	public void extractMultiMonthlyResult(String cid, List<String> lstSid, YearMonthPeriod mPeriod,
			List<String> lstAnyConID, List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
			List<AlarmEmployeeList> alarmEmployeeList, List<AlarmExtractionCondition> alarmExtractConditions,
			String alarmCheckConditionCode, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop) {
		multiMonthCheck.extractMultiMonthlyAlarm(cid,
				lstSid,
				mPeriod,
				lstAnyConID,
				getWplByListSidAndPeriod,
				lstResultCondition,
				lstCheckType,
				alarmEmployeeList,
				alarmExtractConditions,
				alarmCheckConditionCode,
				counter,
				shouldStop);
		
	}



	@Override
	public void extractScheDailyCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
											String errorDailyCheckId, String listOptionalItem, String listFixedItem,
											List<WorkPlaceHistImportAl> getWplByListSidAndPeriod, List<StatusOfEmployeeAdapterAl> lstStatusEmp,
											List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
											Consumer<Integer> counter, Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
											List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		scheDailyCheckService.extractScheDailyCheck(
				cid, lstSid, dPeriod, errorDailyCheckId, listOptionalItem,
				listFixedItem, getWplByListSidAndPeriod, lstStatusEmp,
				lstResultCondition, lstCheckType, counter, shouldStop, alarmEmployeeList,
                alarmExtractConditions, alarmCheckConditionCode);
	}


	@Override
	public void extractScheYearCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod, String errorCheckId,
										   String listOptionalItem, List<WorkPlaceHistImportAl> lstWkpIdAndPeriod,
										   List<StatusOfEmployeeAdapterAl> lstStaEmp, List<ResultOfEachCondition> lstResultCondition,
										   List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter, Supplier<Boolean> shouldStop,
										   List<AlarmEmployeeList> alarmEmployeeList, List<AlarmExtractionCondition> alarmExtractConditions,
										   String alarmCheckConditionCode) {
		scheYearCheckService.extractScheYearCheck(
				cid, lstSid, dPeriod, errorCheckId, listOptionalItem,
				lstWkpIdAndPeriod, lstStaEmp, lstResultCondition,
				lstCheckType, counter, shouldStop, alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
	}


	@Override
	public void extractScheMonCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod, String errorCheckId,
										  String listFixedItemId, String listOptionalItemId, List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
										  List<StatusOfEmployeeAdapterAl> lstStatusEmp, List<ResultOfEachCondition> lstResultCondition,
										  List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter, Supplier<Boolean> shouldStop,
										  List<AlarmEmployeeList> alarmEmployeeList, List<AlarmExtractionCondition> alarmExtractConditions,
										  String alarmCheckConditionCode) {
		scheMonCheckService.extractScheMonCheck(
				cid, lstSid,
				dPeriod, errorCheckId, listFixedItemId, listOptionalItemId,
				getWplByListSidAndPeriod, lstStatusEmp, lstResultCondition,
				lstCheckType, counter, shouldStop, alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
	}

	@Override
	public void extractWeeklyCheckResult(String cid, List<String> lstSid, DatePeriod period,
										 List<WorkPlaceHistImportAl> wplByListSidAndPeriods, String listOptionalItem,
										 List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
										 Consumer<Integer> counter, Supplier<Boolean> shouldStop,
										 List<AlarmEmployeeList> alarmEmployeeList, List<AlarmExtractionCondition> alarmExtractConditions,
										 String alarmCheckConditionCode) {
		weeklyCheckService.extractWeeklyCheck(
				cid, lstSid, period, wplByListSidAndPeriods, listOptionalItem,
				lstResultCondition, lstCheckType, counter, shouldStop, alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
	}

}
