package nts.uk.ctx.exio.dom.input.canonicalize.result;

import java.util.Optional;

import lombok.Value;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;

/**
 * 正準化データ1行分
 */
@Value
public class CanonicalizedDataRecord {

	/** 受入行番号 */
	int rowNo;

	/** 正準化したデータ */
	CanonicalItemList items;
	
	public static CanonicalizedDataRecord noChange(RevisedDataRecord revisedData) {
		
		return new CanonicalizedDataRecord(
				revisedData.getRowNo(),
				CanonicalItemList.of(revisedData.getItems()));
	}
	
	/**
	 * 指定された項目NoのDataItemを返す(beforeよりもafter優先)
	 * @return
	 */
	public Optional<CanonicalItem> getItemByNo(int itemNo) {
		return items.getItemByNo(itemNo);
	}
}
