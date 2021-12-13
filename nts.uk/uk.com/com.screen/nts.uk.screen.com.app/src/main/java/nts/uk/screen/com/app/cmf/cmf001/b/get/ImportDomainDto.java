package nts.uk.screen.com.app.cmf.cmf001.b.get;

import java.util.List;

import lombok.Value;

@Value
public class ImportDomainDto {

	/** 受入ドメインID */
	private int domainId;

	/** レイアウト項目リスト */
	private List<Integer> itemNoList;
	
}
