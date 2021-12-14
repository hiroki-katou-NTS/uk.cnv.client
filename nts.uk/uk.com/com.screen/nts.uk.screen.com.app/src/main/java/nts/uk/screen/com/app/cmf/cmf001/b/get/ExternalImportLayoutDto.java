package nts.uk.screen.com.app.cmf.cmf001.b.get;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;
import lombok.val;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingItemMapping;

@Value
public class ExternalImportLayoutDto {

	/** 項目NO */
	private int itemNo;

	/** 項目名 */
	private String name;

	/** 必須項目 */
	private boolean required;

	/** 項目型 */
	private String type;

	/** 受入元 */
	private String source;


	public static ExternalImportLayoutDto fromDomain(Require require,
			ExternalImportCode settingCode,
			ImportingDomainId domainId,
			ImportingItemMapping domain) {

		return new ExternalImportLayoutDto(
				domain.getItemNo(),
				getItemName(require, domainId, domain),
				checkRequired(require, domainId, domain),
				getItemType(require, domainId, domain),
				checkImportSource(domain));
	}

	private static String getItemName(Require require, ImportingDomainId domainId, ImportingItemMapping mapping) {
		val importableItems = require.getImportableItems(domainId);
		return importableItems.stream()
				.filter(i -> i.getItemNo() == mapping.getItemNo()).collect(Collectors.toList()).get(0).getItemName();
	}

	private static String getItemType(Require require, ImportingDomainId domainId, ImportingItemMapping mapping) {
		val importableItems = require.getImportableItems(domainId);
		return importableItems.stream()
				.filter(i -> i.getItemNo() == mapping.getItemNo()).collect(Collectors.toList()).get(0).getItemType().getResourceText();
	}

	private static boolean checkRequired(Require require, ImportingDomainId domainId, ImportingItemMapping mapping) {
		val importableItems = require.getImportableItems(domainId);

		boolean required = importableItems.stream()
								.filter(i -> i.getItemNo() == mapping.getItemNo())
								.collect(Collectors.toList()).get(0).isRequired();

		boolean primary = importableItems.stream()
								.filter(i -> i.getItemNo() == mapping.getItemNo())
								.collect(Collectors.toList()).get(0).isPrimaryKey();


		return required || primary;
	}

	private static String checkImportSource(ImportingItemMapping mapping) {
		val optCsvColumnNo = mapping.getCsvColumnNo();
		val optFixedValue = mapping.getFixedValue();
		if(optCsvColumnNo.isPresent()){
			return "CSV";
		}
		else if(optFixedValue.isPresent()){
			return "固定値";
		}
		else {
			return "未設定";
		}
	}


	public static interface Require {
		List<ImportableItem> getImportableItems(ImportingDomainId domainId);
	}
}