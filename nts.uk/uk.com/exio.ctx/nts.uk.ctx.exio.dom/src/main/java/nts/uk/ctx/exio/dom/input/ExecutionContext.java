package nts.uk.ctx.exio.dom.input;

import lombok.Value;
import nts.uk.ctx.exio.dom.input.canonicalize.ImportingMode;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;

/**
 * 外部受入の実行コンテキスト
 */
@Value
public class ExecutionContext {

	/** 会社ID */
	String companyId;
	
	/** 受入設定コード */
	String settingCode;
	
	/** 受入グループID */
	ImportingDomainId domainId;
	
	/** 受入モード */
	ImportingMode mode;
		
	public static ExecutionContext createForTemporaryTableName(String companyId, ImportingDomainId domainId) {
		// 一時テーブル名生成用は、現状companyId、ドメインIDしか使っていない
		return new ExecutionContext(companyId, "", domainId, null); 
	}

	public static ExecutionContext createForErrorTableName(String companyId) {
		return new ExecutionContext(companyId, "", null, null); 
	}
	
	public ExternalImportCode getExternalImportCode() {
		return new ExternalImportCode(settingCode);
	}
	
}