package nts.uk.ctx.at.record.pubimp.workrecord.erroralarm;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.MasterCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.StatusOfEmployeeAdapterAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceHistImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.monthlycheckcondition.algorithm.MonthlyExtractCheckService;
import nts.uk.ctx.at.record.pub.workrecord.erroralarm.AlarmListPersonExtractServicePub;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;
@Stateless
public class AlarmListPersonExtractServicePubImpl implements AlarmListPersonExtractServicePub{
	
	@Inject
	private MasterCheckService masterCheck;
	@Inject
	private MonthlyExtractCheckService monthCheck;
	
	@Override
	public void extractMasterCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			String errorMasterCheckId,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<StatusOfEmployeeAdapterAl> lstStatusEmp,
			List<ResultOfEachCondition> lstResultCondition, 
			List<AlarmListCheckInfor> lstCheckType) {
		masterCheck.extractMasterCheck(cid, lstSid, dPeriod, 
				errorMasterCheckId, getWplByListSidAndPeriod, lstStatusEmp, lstResultCondition, lstCheckType);
		
	}

	
	@Override
	public void extractDailyCheckResult(String cid, List<String> lstSid, DatePeriod dPeriod,
			List<String> errorMasterCheckId, List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<StatusOfEmployeeAdapterAl> lstStatusEmp, List<ResultOfEachCondition> lstResultCondition,
			List<AlarmListCheckInfor> lstCheckType) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void extractMonthlyCheckResult(String cid, List<String> lstSid, YearMonthPeriod mPeriod, String fixConId,
			List<String> lstAnyConID, List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType) {
		monthCheck.extractMonthlyAlarm(cid, lstSid, mPeriod, fixConId, lstAnyConID, getWplByListSidAndPeriod, lstResultCondition, lstCheckType);
		
	}
}
