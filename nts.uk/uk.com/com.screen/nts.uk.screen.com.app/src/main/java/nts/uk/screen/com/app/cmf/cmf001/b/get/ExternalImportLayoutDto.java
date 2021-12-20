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


	public static ExternalImportLayoutDto fromDomain(ImportableItem itemDomain, ImportingItemMapping mappingDomain) {

		return new ExternalImportLayoutDto(
				itemDomain.getItemNo(),
				itemDomain.getItemName(),
				itemDomain.isRequired() || itemDomain.isPrimaryKey(),
				itemDomain.getItemType().getResourceText(),
				checkImportSource(mappingDomain));
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