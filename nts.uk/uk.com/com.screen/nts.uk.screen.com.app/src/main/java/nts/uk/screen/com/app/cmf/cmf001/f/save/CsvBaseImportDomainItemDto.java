package nts.uk.screen.com.app.cmf.cmf001.f.save;

import lombok.Value;

@Value
public class CsvBaseImportDomainItemDto {

	/** 項目NO */
	private int itemNo;
	
	private boolean isFixedValue;
	
	/** CSV列番号 */
	private Integer csvItemNo;

	/** 固定値 */
	private String fixedValue;
}
