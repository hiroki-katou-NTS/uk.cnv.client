package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.List;
import java.util.Optional;

/**
 * 
 * @author tutt
 * RP: 工数実績項目と勤怠項目の紐付けRepository
 */
public interface ManHourRecordAndAttendanceItemLinkRepository {
	
	/**
	 * [1] Insert(工数実績項目と勤怠項目の紐付け)
	 * @param link 工数実績項目と勤怠項目の紐付け
	 */
	void insert(ManHourRecordAndAttendanceItemLink link);
	
	/**
	 * [2] Get*
	 * すべての工数実績項目と勤怠項目の紐付けを取得する	
	 * @param cId 会社ID
	 * @return 紐付けリスト
	 */
	List<ManHourRecordAndAttendanceItemLink> get(String cId);
	
	/**
	 * [3] Get*
	 * 指定する工数実績項目リストの工数実績項目と勤怠項目の紐付けを取得する	
	 * @param cId 会社ID
	 * @param 工数実績項目リスト items
	 * @return 紐付けリスト
	 */
	List<ManHourRecordAndAttendanceItemLink> get(String cId, List<Integer> items);
	
	/**
	 * [4] Get
	 * 指定する勤怠項目IDから工数実績項目と勤怠項目の紐付けを取得する
	 * @param cId 会社ID
	 * @param item 勤怠項目ID
	 * @return 紐付け情報
	 */
	Optional<ManHourRecordAndAttendanceItemLink> get(String cId, int item);
}
