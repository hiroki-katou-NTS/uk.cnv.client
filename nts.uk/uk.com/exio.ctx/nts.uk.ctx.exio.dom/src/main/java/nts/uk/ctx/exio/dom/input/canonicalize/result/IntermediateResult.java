package nts.uk.ctx.exio.dom.input.canonicalize.result;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Optional;

import lombok.Value;
import lombok.val;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;

/**
 * 正準化の中間結果
 * 正準化処理で使うメソッドがいくつか生えている
 */
@Value
public class IntermediateResult {
	
	int rowNo;
	
	CanonicalItemList items;
	
	public static IntermediateResult create(RevisedDataRecord revisedData) {
		return new IntermediateResult(
				revisedData.getRowNo(),
				CanonicalItemList.of(revisedData.getItems()));
	}
	
	/**
	 * 任意項目の既定値を指定する（受け入れていなければ指定した既定値を埋める）
	 * @param item 既定値
	 * @return
	 */
	public IntermediateResult optionalItem(CanonicalItem item) {
		
		if (!isImporting(item.getItemNo())) {
			return addCanonicalized(item);
		}
		
		return this;
	}
	
	/**
	 * 現在の保持内容に対して新たに正準化した分を追加する
	 * @param canonicalizedItems
	 * @param targetItemNos
	 * @return
	 */
	public IntermediateResult addCanonicalized(CanonicalItemList canonicalizedItems) {
		return canonicalizedItems.stream().reduce(
				this,
				(interm, item) -> interm.addCanonicalized(item),
				(a, b) -> a.addCanonicalized(b.items));
	}
	
	/**
	 * 現在の保持内容に対して新たに正準化した分を追加する
	 * @param canonicalizedItem
	 * @param targetItemNos
	 * @return
	 */
	public IntermediateResult addCanonicalized(CanonicalItem canonicalizedItem) {
		
		// 同じ項目NOのものは古いデータを上書き
		val newItems = items.stream()
				.filter(item -> item.getItemNo() != canonicalizedItem.getItemNo())
				.collect(toList());
		
		val itemList = new ArrayList<CanonicalItem>(newItems);
		itemList.add(canonicalizedItem);
		return new IntermediateResult(this.rowNo, new CanonicalItemList(itemList));
	}
	
	/**
	 * 指定した項目のデータを取り出す
	 * afterとbeforeの両方に入っている場合は、afterの値が優先
	 * @param itemNo
	 * @return
	 */
	public Optional<CanonicalItem> getItemByNo(int itemNo) {
		return items.getItemByNo(itemNo);
	}
	
	/**
	 * 指定した項目を受け入れているか
	 * @param itemNo
	 * @return
	 */
	public boolean isImporting(int itemNo) {
		return getItemByNo(itemNo).map(e -> e.getValue()).orElse(null) != null;
	}
	
	/**
	 * CanonicalizedDataRecordに変換する
	 * @return
	 */
	public CanonicalizedDataRecord complete() {
		return new CanonicalizedDataRecord(this.rowNo, items);
	}
}
