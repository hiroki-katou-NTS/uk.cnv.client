package nts.uk.screen.com.app.cmf.cmf001.f.get;

import java.util.List;

import lombok.Value;
import lombok.val;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingItemMapping;

@Value
public class CsvBaseLayoutDto {

	/** 項目NO */
	private int itemNo;

	/** 項目名 */
	private String name;

	/** 必須項目 */
	private boolean required;

	private Integer isFixedValue;

	private Integer selectedCsvItemNo;
	
	private String fixedValue;
	
	private String csvData;

	public static CsvBaseLayoutDto fromDomain(
			ImportableItem importableItem,
			ExternalImportCode settingCode,
			ImportingDomainId domainId,
			ImportingItemMapping domain,
			String csvData) {

		return new CsvBaseLayoutDto(
				domain.getItemNo(),
				importableItem.getItemName(),
				checkRequired(importableItem),
				checkImportSource(domain),
				domain.getCsvColumnNo().orElse(null),
				domain.getFixedValue().map(v -> v.asString()).orElse(null),
				csvData);
	}

	private static boolean checkRequired(ImportableItem importableItem) {
		return importableItem.isRequired() || importableItem.isPrimaryKey();
	}

	private static int checkImportSource(ImportingItemMapping mapping) {
		val optCsvColumnNo = mapping.getCsvColumnNo();
		if(optCsvColumnNo.isPresent()){
			return 0;
		}
		else {
			return 1;
		}
	}


	public static interface Require {
		List<ImportableItem> getImportableItems(ImportingDomainId domainId);
	}
}