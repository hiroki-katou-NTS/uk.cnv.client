package nts.uk.ctx.exio.dom.input.errors;

import lombok.Value;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;

/**
 * 実行エラー（全体が実行不可能なエラー）の場合、行も項目のNULL
 * レコードのエラーなら項目がNULL
 * 項目のエラーなら全て非NULL
 */
@Value
public class ExternalImportError {

	/** CSV行番号 */
	Integer csvRowNo;

	/** ドメインID */
	ImportingDomainId domainId;
	
	/** 項目NO */
	Integer itemNo;
	
	/** エラーメッセージ */
	String message;
	
	public static ExternalImportError execution(String message) {
		return new ExternalImportError(null, null, null, message);
	}
	
	public static ExternalImportError record(int csvRowNo, ImportingDomainId domainId, String message) {
		return new ExternalImportError(csvRowNo, domainId, null, message);
	}
	
	public static ExternalImportError of(int csvRowNo, ImportingDomainId domainId, ItemError error) {
		return new ExternalImportError(csvRowNo, domainId, error.getItemNo(), error.getMessage());
	}
	
	public static ExternalImportError of(ImportingDomainId domainId, RecordError error) {
		return new ExternalImportError(error.getCsvRowNo(), domainId, null, error.getMessage());
	}
	
	public boolean isExecution() {
		return csvRowNo == null;
	}
	
	public boolean isRecord() {
		return !isExecution() && itemNo == null;
	}
	
	public boolean isItem() {
		return !isExecution() && itemNo != null;
	}
	
	public void toText(RequireToText require, StringBuilder sb) {
		
		if (isExecution()) {
			sb.append("実行エラー");
		} else {
			sb.append(String.format("%8d 行", csvRowNo));
		}
		
		sb.append("\t");
		
		if (isItem()) {
			String domainName = require.getDomainName(domainId);
			sb.append(domainName);
			sb.append("\t");
			String itemName = require.getImportableItem(domainId, itemNo).getItemName();
			sb.append(itemName);
		}
		
		sb.append("\t");
		
		sb.append(message);
	}
	
	public static interface RequireToText {

		String getDomainName(ImportingDomainId domainId);
		
		ImportableItem getImportableItem(ImportingDomainId domainId, int itemNo);
	}
}
