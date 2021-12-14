package nts.uk.ctx.exio.dom.input.canonicalize;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Value;
import nts.gul.util.OptionalUtil;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;

/**
 * 正準化データ1行分
 */
@Value
public class CanonicalizedDataRecord {

	/** 受入行番号 */
	int rowNo;

	/** 正準化したデータ */
	CanonicalItemList itemsAfterCanonicalize;
	
	/** 正準化対象の正準化前データ */
	CanonicalItemList itemsBeforeCanonicalize;
	
	/** 正準化対象ではないデータ */
	CanonicalItemList itemsNotCanonicalize;
	
	public static CanonicalizedDataRecord noChange(RevisedDataRecord revisedData) {
		
		return new CanonicalizedDataRecord(
				revisedData.getRowNo(),
				new CanonicalItemList(),
				new CanonicalItemList(),
				CanonicalItemList.of(revisedData.getItems()));
	}
	
	/**
	 * 指定された項目NoのDataItemを返す(beforeよりもafter優先)
	 * @return
	 */
	public Optional<CanonicalItem> getItemByNo(int itemNo) {
		
		Stream<CanonicalItemList> lists = Stream.of(
				itemsAfterCanonicalize,  // NotにはSIDなどにNULLが入っているので、Notより前にしないとダメ
				itemsNotCanonicalize,
				itemsBeforeCanonicalize);
		
		return lists
				.map(l -> l.getItemByNo(itemNo))
				.filter(opt -> opt.isPresent())
				.flatMap(OptionalUtil::stream)
				.findFirst();
	}
}
