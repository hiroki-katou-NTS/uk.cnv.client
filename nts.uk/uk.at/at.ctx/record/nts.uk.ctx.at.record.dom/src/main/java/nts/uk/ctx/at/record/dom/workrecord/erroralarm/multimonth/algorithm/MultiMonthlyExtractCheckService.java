package nts.uk.ctx.at.record.dom.workrecord.erroralarm.multimonth.algorithm;

import java.util.List;

import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceHistImportAl;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;

public interface MultiMonthlyExtractCheckService {
	/**
	 * 複数月の集計処理
	 * @param cid
	 * @param lstSid
	 * @param mPeriod
	 * @param lstAnyConID
	 * @param getWplByListSidAndPeriod
	 * @param lstResultCondition
	 * @param lstCheckType
	 */
	void extractMultiMonthlyAlarm(String cid, List<String> lstSid, YearMonthPeriod mPeriod,
			List<String> lstAnyConID,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod, 
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType);
}
