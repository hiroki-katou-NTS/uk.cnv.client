package nts.uk.ctx.bs.employee.pub.employee;

import java.util.List;

import lombok.Value;

/**
 * 個人社員情報Exported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.基幹.社員.Export.個人社員情報Exported
 * @author lan_lt
 *
 */
@Value
public class PersonalEmployeeInfoExport {
	/** 個人ID **/
	private String personId;
	
	/** 個人名 **/
	private String personName;
	
	/** ビジネスネーム **/
	private String bussinessName;
	
	/** 社員情報リスト **/
	private List<EmployeeDataMngInfoExport> employeeInfos;
	
}
