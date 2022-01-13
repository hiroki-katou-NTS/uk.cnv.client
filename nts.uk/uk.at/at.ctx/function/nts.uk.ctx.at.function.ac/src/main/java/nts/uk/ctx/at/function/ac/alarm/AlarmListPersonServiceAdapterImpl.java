package nts.uk.ctx.at.function.ac.alarm;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.function.dom.adapter.WorkPlaceHistImport;
import nts.uk.ctx.at.function.dom.adapter.alarm.AlarmListPersonServiceAdapter;
import nts.uk.ctx.at.function.dom.adapter.companyRecord.StatusOfEmployeeAdapter;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.annual.ScheduleAnnualAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.schedaily.ScheduleDailyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.schemonthly.ScheduleMonthlyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.weekly.WeeklyAlarmCheckCond;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.daily.DailyAlarmCondition;
import nts.uk.ctx.at.function.dom.attendanceitemname.service.AttendanceItemNameDomainService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.StatusOfEmployeeAdapterAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceHistImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceIdAndPeriodImportAl;
import nts.uk.ctx.at.record.pub.workrecord.erroralarm.AlarmListPersonExtractServicePub;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmEmployeeList;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmExtractionCondition;

@Stateless
public class AlarmListPersonServiceAdapterImpl implements AlarmListPersonServiceAdapter{
	@Inject
	private AlarmListPersonExtractServicePub extractService;

	@Override
	public void extractMasterCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			String errorMasterCheckId, List<WorkPlaceHistImport> lstWplHist,
			List<StatusOfEmployeeAdapter> lstStatusEmp, List<ResultOfEachCondition> lstResultCondition,
			List<AlarmListCheckInfor> lstCheckInfor, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		List<WorkPlaceHistImportAl> lstWkpIdAndPeriod = lstWplHist.stream().map(x -> 
					new WorkPlaceHistImportAl(x.getEmployeeId(), 
							x.getLstWkpIdAndPeriod().stream()
							.map(a -> new WorkPlaceIdAndPeriodImportAl(a.getDatePeriod(), a.getWorkplaceId())).collect(Collectors.toList()))
				).collect(Collectors.toList());
		List<StatusOfEmployeeAdapterAl> lstStaEmp = lstStatusEmp.stream()
				.map(x -> new StatusOfEmployeeAdapterAl(x.getEmployeeId(), x.getListPeriod())).collect(Collectors.toList());
		extractService.extractMasterCheckResult(cid,
				lstSid,
				dPeriod,
				errorMasterCheckId,
				lstWkpIdAndPeriod,
				lstStaEmp,
				lstResultCondition,
				lstCheckInfor, counter, shouldStop,
				alarmEmployeeList,
				alarmExtractConditions,
				alarmCheckConditionCode);
		
	}
	
	/**
	 * 日次
	 */
	@Override
	public void extractDailyCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			String errorDailyCheckId, DailyAlarmCondition dailyAlarmCondition,
			List<WorkPlaceHistImport> getWplByListSidAndPeriod,
			List<StatusOfEmployeeAdapter> lstStatusEmp,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		
		List<String> extractConditionWorkRecord = dailyAlarmCondition.getExtractConditionWorkRecord();
		List<String> errorDailyCheckCd = dailyAlarmCondition.getErrorAlarmCode();
		
		List<WorkPlaceHistImportAl> lstWkpIdAndPeriod = getWplByListSidAndPeriod.stream().map(x -> 
					new WorkPlaceHistImportAl(x.getEmployeeId(), 
							x.getLstWkpIdAndPeriod().stream()
							.map(a -> new WorkPlaceIdAndPeriodImportAl(a.getDatePeriod(), a.getWorkplaceId())).collect(Collectors.toList()))
				).collect(Collectors.toList());
		
		List<StatusOfEmployeeAdapterAl> lstStaEmp = lstStatusEmp.stream()
			.map(x -> new StatusOfEmployeeAdapterAl(x.getEmployeeId(), x.getListPeriod())).collect(Collectors.toList());
		
		extractService.extractDailyCheckResult(cid, lstSid, dPeriod, errorDailyCheckId, extractConditionWorkRecord,
				errorDailyCheckCd, lstWkpIdAndPeriod, lstStaEmp, lstResultCondition, lstCheckType,
				counter, shouldStop, alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
	}

	@Override
	public void extractMonthCheckResult(String cid, List<String> lstSid, YearMonthPeriod mPeriod, String fixConId,
			List<String> lstAnyConID, List<WorkPlaceHistImport> lstWplHist,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckInfor, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		List<WorkPlaceHistImportAl> lstWkpIdAndPeriod = lstWplHist.stream().map(x -> 
			new WorkPlaceHistImportAl(x.getEmployeeId(), 
					x.getLstWkpIdAndPeriod().stream()
					.map(a -> new WorkPlaceIdAndPeriodImportAl(a.getDatePeriod(), a.getWorkplaceId())).collect(Collectors.toList())))
					.collect(Collectors.toList());		
		extractService.extractMonthlyCheckResult(cid,
				lstSid,
				mPeriod,
				fixConId,
				lstAnyConID,
				lstWkpIdAndPeriod,
				lstResultCondition,
				lstCheckInfor,
				counter,
				shouldStop,
				alarmEmployeeList,
				alarmExtractConditions,
				alarmCheckConditionCode);
	}

	@Override
	public void extractMultiMonthCheckResult(String cid, List<String> lstSid, YearMonthPeriod mPeriod,
			List<String> lstAnyConID, List<WorkPlaceHistImport> lstWplHist,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckInfor,
			List<AlarmEmployeeList> alarmEmployeeList, List<AlarmExtractionCondition> alarmExtractConditions,
			String alarmCheckConditionCode, Consumer<Integer> counter,
			Supplier<Boolean> shouldStop) {
		List<WorkPlaceHistImportAl> lstWkpIdAndPeriod = lstWplHist.stream().map(x -> 
			new WorkPlaceHistImportAl(x.getEmployeeId(), 
					x.getLstWkpIdAndPeriod().stream()
					.map(a -> new WorkPlaceIdAndPeriodImportAl(a.getDatePeriod(), a.getWorkplaceId())).collect(Collectors.toList())))
					.collect(Collectors.toList());
		extractService.extractMultiMonthlyResult(cid,
				lstSid,
				mPeriod,
				lstAnyConID,
				lstWkpIdAndPeriod,
				lstResultCondition,
				lstCheckInfor,
				alarmEmployeeList,
				alarmExtractConditions,
				alarmCheckConditionCode,
				counter,
				shouldStop);
	}

	@Override
	public void extractScheDailyCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			String errorDailyCheckId, ScheduleDailyAlarmCheckCond dailyAlarmCondition,
			List<WorkPlaceHistImport> getWplByListSidAndPeriod, List<StatusOfEmployeeAdapter> lstStatusEmp,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
			Consumer<Integer> counter, Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
				
		String listOptionalItem = dailyAlarmCondition.getListOptionalItem();
		String listFixedItem = dailyAlarmCondition.getListFixedItem();
		
		List<WorkPlaceHistImportAl> lstWkpIdAndPeriod = getWplByListSidAndPeriod.stream().map(x -> 
			new WorkPlaceHistImportAl(x.getEmployeeId(), 
					x.getLstWkpIdAndPeriod().stream()
					.map(a -> new WorkPlaceIdAndPeriodImportAl(a.getDatePeriod(), a.getWorkplaceId())).collect(Collectors.toList()))).collect(Collectors.toList());

		List<StatusOfEmployeeAdapterAl> lstStaEmp = lstStatusEmp.stream()
				.map(x -> new StatusOfEmployeeAdapterAl(x.getEmployeeId(), x.getListPeriod())).collect(Collectors.toList());
		
		extractService.extractScheDailyCheckResult(
				cid, lstSid, dPeriod, errorDailyCheckId, 
				listOptionalItem, listFixedItem, 
				lstWkpIdAndPeriod, lstStaEmp, lstResultCondition, lstCheckType, counter, shouldStop,
				alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
	}

	@Override
	public void extractScheYearCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			String errorCheckId, ScheduleAnnualAlarmCheckCond scheYearAlarmCondition,
			List<WorkPlaceHistImport> wplByListSidAndPeriod, List<StatusOfEmployeeAdapter> lstStatusEmp,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
			Consumer<Integer> counter, Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		String listOptionalItem = scheYearAlarmCondition.getListOptionalItem();
		
		List<WorkPlaceHistImportAl> lstWkpIdAndPeriod = wplByListSidAndPeriod.stream().map(x -> 
			new WorkPlaceHistImportAl(x.getEmployeeId(), 
					x.getLstWkpIdAndPeriod().stream()
					.map(a -> new WorkPlaceIdAndPeriodImportAl(a.getDatePeriod(), a.getWorkplaceId())).collect(Collectors.toList()))).collect(Collectors.toList());

		List<StatusOfEmployeeAdapterAl> lstStaEmp = lstStatusEmp.stream()
				.map(x -> new StatusOfEmployeeAdapterAl(x.getEmployeeId(), x.getListPeriod())).collect(Collectors.toList());
		
		extractService.extractScheYearCheckResult(
				cid, lstSid, dPeriod, errorCheckId, 
				listOptionalItem, 
				lstWkpIdAndPeriod, lstStaEmp, lstResultCondition, lstCheckType, counter, shouldStop,
				alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
	}

	@Override
	public void extractWeeklyCheckResult(String cid, List<String> lstSid, DatePeriod period,
			List<WorkPlaceHistImport> wplByListSidAndPeriods, WeeklyAlarmCheckCond weeklyAlarmCheckCond,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
			Consumer<Integer> counter, Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		String listOptionalItemId = weeklyAlarmCheckCond.getListOptionalItem();
		
		List<WorkPlaceHistImportAl> lstWkpIdAndPeriod = wplByListSidAndPeriods.stream().map(x -> 
			new WorkPlaceHistImportAl(x.getEmployeeId(), 
					x.getLstWkpIdAndPeriod().stream()
					.map(a -> new WorkPlaceIdAndPeriodImportAl(a.getDatePeriod(), a.getWorkplaceId())).collect(Collectors.toList()))).collect(Collectors.toList());
		
		extractService.extractWeeklyCheckResult(
				cid, lstSid, period, lstWkpIdAndPeriod, 
				listOptionalItemId, lstResultCondition, lstCheckType, counter, shouldStop,
				alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
	}

	@Override
	public void extractScheMonCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod, String errorCheckId,
			ScheduleMonthlyAlarmCheckCond scheduleMonthlyAlarmCheckCond,
			List<WorkPlaceHistImport> getWplByListSidAndPeriod, List<StatusOfEmployeeAdapter> lstStatusEmp,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
			Consumer<Integer> counter, Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		String listOptionalItemId = scheduleMonthlyAlarmCheckCond.getListOptionalItem();
		String listFixedItemId = scheduleMonthlyAlarmCheckCond.getListFixedItem();
		
		List<WorkPlaceHistImportAl> lstWkpIdAndPeriod = getWplByListSidAndPeriod.stream().map(x -> 
			new WorkPlaceHistImportAl(x.getEmployeeId(), 
					x.getLstWkpIdAndPeriod().stream()
					.map(a -> new WorkPlaceIdAndPeriodImportAl(a.getDatePeriod(), a.getWorkplaceId())).collect(Collectors.toList()))).collect(Collectors.toList());

		List<StatusOfEmployeeAdapterAl> lstStaEmp = lstStatusEmp.stream()
				.map(x -> new StatusOfEmployeeAdapterAl(x.getEmployeeId(), x.getListPeriod())).collect(Collectors.toList());
		
		extractService.extractScheMonCheckResult(
				cid, lstSid, dPeriod, errorCheckId, 
				listFixedItemId, listOptionalItemId, 
				lstWkpIdAndPeriod, lstStaEmp, 
				lstResultCondition, lstCheckType, counter, shouldStop, alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
	}

	
}
