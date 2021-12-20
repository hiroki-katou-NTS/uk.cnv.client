package nts.uk.ctx.exio.dom.input.setting.assembly.mapping;

import java.util.Optional;

import lombok.Getter;
import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.uk.ctx.exio.dom.input.DataItem;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.csvimport.CsvRecord;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.errors.ItemError;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItem;
import nts.gul.util.Either;

/**
 * 項目マッピング
 */
@Getter
public class ImportingItemMapping {

	/** 項目NO */
	private final int itemNo;

	/** 固定値か？ **/
	private boolean isFixedValue;

	/** CSV列番号 */
	private Optional<Integer> csvColumnNo;

	/** 固定値 */
	private Optional<StringifiedValue> fixedValue;

	public static ImportingItemMapping noSetting(int itemNo) {
		return new ImportingItemMapping(itemNo, true, Optional.empty(), Optional.empty());
	}

	public ImportingItemMapping(int itemNo, boolean isFixedValue, Optional<Integer> csvColumnNo, Optional<StringifiedValue> fixedValue) {

		if (csvColumnNo.isPresent() && fixedValue.isPresent()) {
			throw new RuntimeException("両方同時には設定できない");
		}

		this.isFixedValue = isFixedValue;
		this.itemNo = itemNo;
		this.csvColumnNo = csvColumnNo;
		this.fixedValue = fixedValue;
	}

	/**
	 * 設定済みならtrue
	 * @return
	 */
	public boolean isConfigured() {
		return csvColumnNo.isPresent() || fixedValue.isPresent();
	}

	public boolean isCsvMapping() {
		return !isFixedValue;
	}

	public boolean isFixedValue() {
		return isFixedValue;
	}

	public void setNoSetting() {
		isFixedValue = true;
		csvColumnNo = Optional.empty();
		fixedValue = Optional.empty();
	}

	public void setCsvColumnNo(int columnNo) {
		csvColumnNo = Optional.ofNullable(columnNo);
		fixedValue = Optional.empty();
		isFixedValue = !csvColumnNo.isPresent();
	}

	public void setFixedValue(StringifiedValue value) {
		fixedValue = Optional.ofNullable(value);
		if(fixedValue.isPresent()) {
			csvColumnNo = Optional.empty();
			isFixedValue = true;
		}
	}

	public Either<ItemError, DataItem> assemble(RequireAssemble require, ExecutionContext context, CsvRecord csvRecord) {

		if (!isConfigured()) {
			throw new BusinessException(new RawErrorMessage("未設定の受入項目があります。設定を確認してください。"));
		}

		// 固定値
		if (fixedValue.isPresent()) {
			val importableItem = require.getImportableItem(context.getDomainId(), itemNo);
			Object value = fixedValue.get().asTypeOf(importableItem.getItemType());
			return Either.right(new DataItem(itemNo, value));
		}

		// CSV項目
		else {
			val csvItem = readCsv(csvRecord);

			// 編集
			return require.getReviseItem(context.getExternalImportCode(), context.getDomainId(), itemNo)
					.map(r -> r.revise(csvItem.getCsvValue()))
					.orElseGet(() -> noRevise(require, context, csvItem));
		}
	}

	private ImportingCsvItem readCsv(CsvRecord record) {

		String value = record.getItemByColumnNo(csvColumnNo.get())
				.orElseThrow(() -> new BusinessException(new RawErrorMessage(String.format(
						"受入CSVファイルの%d行目に%d列目が存在しないため、処理を続行できません。",
						record.getRowNo(),
						csvColumnNo.get()))));

		return new ImportingCsvItem(itemNo, value);
	}

	private static Either<ItemError, DataItem> noRevise(RequireAssemble require, ExecutionContext context, ImportingCsvItem csvItem) {

			return require.getImportableItem(context.getDomainId(), csvItem.getItemNo())
					.parse(csvItem.getCsvValue())
					.map(value -> new DataItem(csvItem.getItemNo(), value));
	}

	public static interface RequireAssemble {

		Optional<ReviseItem> getReviseItem(ExternalImportCode settingCode, ImportingDomainId domainId, int itemNo);

		ImportableItem getImportableItem(ImportingDomainId domainId, int itemNo);
	}
}
