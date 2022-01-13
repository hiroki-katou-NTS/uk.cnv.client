package nts.uk.screen.com.app.cmf.cmf001.f.save;

import java.util.List;

import lombok.Value;

@Value
public class CsvBaseImportDomainDto {
	/** 受入設定コード */
	private int domainId;
	private List<CsvBaseImportDomainItemDto> items;

}