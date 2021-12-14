package nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal;

import java.util.Optional;

import lombok.Value;

/**
 * 
 * @author xuannt
 *
 * 社員データ
 */
@Value
public class EmpDataImport {
	/** 会社ID */
	private String companyId;
	
	/** 個人ID */
	private String personId;
	
	/** 社員ID */
	private String employeeId;
	
	/** 社員コード */
	private String employeeCode;
	
	/** 外部コード */
	private Optional<String> externalCode;
	

}
