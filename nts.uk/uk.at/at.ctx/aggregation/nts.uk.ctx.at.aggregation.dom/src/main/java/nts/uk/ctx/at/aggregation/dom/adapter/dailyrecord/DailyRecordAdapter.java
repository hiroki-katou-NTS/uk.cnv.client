package nts.uk.ctx.at.aggregation.dom.adapter.dailyrecord;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 日別実績Adapter
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.Imported.日別実績.日別実績
 * @author dan_pv
 */
public interface DailyRecordAdapter {
	
	/**
	 * 予定管理対象者の実績を取得する
	 * @param employeeIds 社員リスト
	 * @param period 期間
	 * @return
	 */
	public List<IntegrationOfDaily> getDailyRecordByScheduleManagement(List<String> employeeIds, DatePeriod period);

}
