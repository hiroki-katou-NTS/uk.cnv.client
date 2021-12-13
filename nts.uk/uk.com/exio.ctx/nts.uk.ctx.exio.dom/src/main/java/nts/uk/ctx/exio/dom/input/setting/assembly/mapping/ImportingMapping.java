package nts.uk.ctx.exio.dom.input.setting.assembly.mapping;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Value;
import lombok.val;
import nts.uk.ctx.exio.dom.input.DataItemList;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.csvimport.CsvRecord;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrorsRequire;
import nts.uk.ctx.exio.dom.input.errors.ItemError;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;

/**
 * 受入マッピング
 */
@Value
public class ImportingMapping {

	/** マッピング一覧 */
	private List<ImportingItemMapping> mappings;


	/**
	 * マッピング一覧の値を更新する
	 * @param itemList
	 */
	public void merge(RequireMerge require, List<Integer> itemList) {

		List<ImportingItemMapping> newMappings = new ArrayList<>();

		for(int itemNo : itemList) {

			val found = mappings.stream().filter(m -> m.getItemNo() == itemNo).findFirst();

			if(found.isPresent()) { //値を変えないやつら
				newMappings.add(found.get());
			} else { //新しく追加されたやつら
				newMappings.add(ImportingItemMapping.noSetting(itemNo));
			}
		}
		
		// 不要になった「項目の編集」を削除
		{
			List<Integer> newItems = newMappings.stream().map(m -> m.getItemNo()).collect(toList());
			List<Integer> deletingItems = mappings.stream()
					.map(m -> m.getItemNo())
					.filter(m -> !newItems.contains(m))
					.collect(toList());
			
			require.deleteReviseItems(deletingItems);
		}
		
		mappings.clear();
		mappings.addAll(newMappings);
		this.resetCsvColumnNoByOrder();
	}
	
	public static interface RequireMerge {
		void deleteReviseItems(List<Integer> itemNos);
	}
	
	/**
	 * 新しくマッピングを作ったときに初期値を設定する
	 * @param items
	 * @return
	 */

	public static ImportingMapping defaultSet(List<Integer> items){

		val list = items.stream()
			.map(item -> ImportingItemMapping.noSetting(item))
			.collect(Collectors.toList());


		return new ImportingMapping(list);
	}



	/**
	 * 受入データを組み立てる。エラーがある場合はemptyを返す。
	 * @param require
	 * @param context
	 * @param csvRecord
	 * @return
	 */
	public Optional<RevisedDataRecord> assemble(RequireAssemble require, ExecutionContext context, CsvRecord csvRecord) {

		val assembled = new DataItemList();
		val errors = new ArrayList<ItemError>();

		for (val mapping : mappings) {
			mapping.assemble(require, context, csvRecord)
				.ifRight(r -> assembled.add(r))
				.ifLeft(e -> errors.add(e));
		}

		if (!errors.isEmpty()) {
			for (val error : errors) {
				require.add(ExternalImportError.of(csvRecord.getRowNo(), context.getDomainId(), error));
			}
			return Optional.empty();
		}

		return Optional.of(new RevisedDataRecord(csvRecord.getRowNo(), assembled));
	}

	public static interface RequireAssemble extends
			ImportingItemMapping.RequireAssemble,
			ExternalImportErrorsRequire {
	}

	public List<Integer> getAllItemNo() {

		return mappings.stream()
				.map(i -> i.getItemNo())
				.collect(toList());
	}

	/**
	 * 項目を取得する
	 * @param itemNo
	 * @return
	 */
	public Optional<ImportingItemMapping> getByItemNo(int itemNo) {

		return mappings.stream()
				.filter(m -> m.getItemNo() == itemNo)
				.findFirst();
	}

	/**
	 * 指定した項目を未設定に変更する
	 */
	public void setNoSetting(int itemNo, boolean withReset) {

		getByItemNo(itemNo).get().setNoSetting();
		if(withReset) {
			resetCsvColumnNoByOrder();
		}
	}

	/**
	 * 指定した項目をCSVマッピングに変更する
	 */
	public void setCsvMapping(int itemNo, boolean withReset) {
		if(withReset) {
			// 一旦ダミーの値を入れてCSVマッピングに切り替えた状態で順番リセット
			getByItemNo(itemNo).get().setCsvColumnNo(-1);
			resetCsvColumnNoByOrder();
		}
		else {
			getByItemNo(itemNo).get().setFixedValue(null);
			if(!getByItemNo(itemNo).get().getCsvColumnNo().isPresent()){
				getByItemNo(itemNo).get().setCsvColumnNo(1); // CSV項目NOが未指定の場合いったん先頭列を入れておく
			}
		}
	}

	/**
	 * 指定した項目を固定値に変更する
	 */
	public void setFixedValue(int itemNo, StringifiedValue fixedValue, boolean withReset) {

		getByItemNo(itemNo).get().setFixedValue(fixedValue);
		if(withReset) {
			resetCsvColumnNoByOrder();
		}
	}

	private void resetCsvColumnNoByOrder() {

		int columnNo = 1;

		for (val mapping : mappings) {
			if (mapping.isCsvMapping()) {
				mapping.setCsvColumnNo(columnNo);
				columnNo++;
			}
		}
	}
}
