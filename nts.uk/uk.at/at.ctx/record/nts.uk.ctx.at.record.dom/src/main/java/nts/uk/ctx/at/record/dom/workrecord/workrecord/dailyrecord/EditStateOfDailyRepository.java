package nts.uk.ctx.at.record.dom.workrecord.workrecord.dailyrecord;

import java.util.List;

import nts.arc.time.GeneralDate;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別実績.日別実績の編集状態Repository
 * @author tutt
 *
 */
public interface EditStateOfDailyRepository {
	/**
	 * [1] 削除する
	 * @param sId 社員ID
	 * @param ymd 年月日
	 * @param itemIdList 勤怠項目ID一覧
	 */
	void deleteByListItemId(String sId, GeneralDate ymd, List<Integer> attendanceItemIds);
}
