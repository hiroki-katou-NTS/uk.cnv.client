package nts.uk.ctx.sys.auth.dom.adapter.employee;

import java.util.List;

import lombok.Value;

/**
 * 個人社員情報Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.Imported.contexts.社員.個人社員情報Imported
 * @author lan_lt
 */

@Value
public class PersonalEmployeeInfoImport {
	/** 個人ID **/
	private String personId;
	
	/** 個人名 **/
	private String personName;
	
	/** ビジネスネーム **/
	private String bussinessName;
	
	/** 社員情報リスト **/
	private List<EmployeeInfoImport> employeeInfos;
}
