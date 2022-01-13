package nts.uk.ctx.at.record.dom.jobmanagement.manhourinput;

import java.util.List;

import nts.arc.time.GeneralDate;

/**
 * 時間帯別勤怠の削除一覧
 * @author tutt
 *
 */
public interface DeleteAttendancesByTimezoneRepo {
	
	/**
	 * [1] 登録する
	 * @param deleteAttendancesByTimezone 時間帯別勤怠の削除一覧
	 */
	void register(DeleteAttendancesByTimezone deleteAttendancesByTimezone);
	
	/**
	 * [2] 取得する
	 * @param sId 社員ID
	 * @param ymdList 年月日リスト
	 * @return domain
	 */
	List<DeleteAttendancesByTimezone> get(String sId, List<GeneralDate> ymdList);
	
	/**
	 * [3] 複数日を削除する
	 * @param sId
	 * @param ymdList
	 */
	void deleteDays(String sId, List<GeneralDate> ymdList);
}
