package nts.uk.ctx.at.aggregation.dom.adapter.workschedule;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 勤務予定Adapter
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.Imported.勤務予定.勤務予定
 * @author dan_pv
 */
public interface WorkScheduleAdapter {
	
	/**
	 * 取得する
	 * @param employeeIds 社員リスト
	 * @param period 期間
	 * @return
	 */
	public List<IntegrationOfDaily> getList(List<String> employeeIds, DatePeriod period);

}
