package nts.uk.ctx.at.record.pub.dailyresult;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別実績.Export.日別実績
 * 日別実績
 * @author dan_pv
 */
public interface DailyRecordPub {
	
	/**
	 * 予定管理対象者の実績を取得する
	 * @param employeeIds 社員リスト
	 * @param period 期間
	 * @return List<日別勤怠(Work)
	 */
	public List<IntegrationOfDaily> getDailyRecordByScheduleManagement(List<String> employeeIds, DatePeriod period);
	
}
