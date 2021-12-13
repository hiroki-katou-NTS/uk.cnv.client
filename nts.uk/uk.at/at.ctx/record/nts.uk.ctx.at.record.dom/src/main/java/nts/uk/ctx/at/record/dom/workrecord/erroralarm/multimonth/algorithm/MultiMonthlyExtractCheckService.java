package nts.uk.ctx.at.record.dom.workrecord.erroralarm.multimonth.algorithm;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceHistImportAl;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmEmployeeList;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.AlarmExtractionCondition;

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
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
			List<AlarmEmployeeList> alarmEmployeeList, List<AlarmExtractionCondition> alarmExtractConditions,
			String alarmCheckConditionCode,Consumer<Integer> counter,
			  Supplier<Boolean> shouldStop);
}
