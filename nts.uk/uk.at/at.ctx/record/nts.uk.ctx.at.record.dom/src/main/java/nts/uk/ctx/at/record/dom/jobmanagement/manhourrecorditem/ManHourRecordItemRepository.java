package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.List;

/**
 * 
 * @author tutt 
 * RP: 工数実績項目Repository
 */
public interface ManHourRecordItemRepository {
	/**
	 * 	[1] Insert(工数実績項目)
	 * @param item 工数実績項目
	 */
	void insert(ManHourRecordItem item);

	/**
	 * 	[2] Update(工数実績項目)
	 * @param item 工数実績項目
	 */
	void update(ManHourRecordItem item);
	
	/**
	 * 	[3] Get*
	 * すべての工数実績項目を取得する
	 * @param cId 会社ID
	 * @return 工数実績項目リスト
	 */
	List<ManHourRecordItem> get(String cId);
	
	/**
	 * 	[4] Get*
	 * 指定工数実績項目IDの工数実績項目を取得する
	 * @param cId 会社ID
	 * @param items 項目リスト
	 * @return 工数実績項目リスト
	 */
	List<ManHourRecordItem> get(String cId, List<Integer> items);
}
