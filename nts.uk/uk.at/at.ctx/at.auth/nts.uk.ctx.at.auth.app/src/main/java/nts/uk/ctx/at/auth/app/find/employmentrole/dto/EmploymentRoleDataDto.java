package nts.uk.ctx.at.auth.app.find.employmentrole.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentRoleDataDto {
	/**
	 * 会社ID
	 */
	private String companyId;
	/**
	 * ロールID
	 */
	private String roleId;
	/**
	 * スケジュール画面社員参照
	 */
	private int scheduleEmployeeRef;
	/**
	 * 予約画面社員参照
	 */
	private int bookEmployeeRef; 
	/**
	 * 代行者指定時社員参照 
	 */
	private  int employeeRefSpecAgent;
	/**
	 * 在席照会社員参照 
	 */
	private int presentInqEmployeeRef; 
	/**
	 * 未来日参照許可 FUTURE_DATE_REF_PERMIT
	 */
	private int futureDateRefPermit;
	
	/**
	 * ロール
	 */
	private RoleDto role;
	
	public static EmploymentRoleDataDto fromDomain(EmploymentRole domain ){
		//TODO return は修正お願いいたします。
		/*		return new EmploymentRoleDataDto(
				domain.getCompanyId(),
				domain.getRoleId(),
				domain.getScheduleEmployeeRef().value,
				domain.getBookEmployeeRef().value,
				domain.getEmployeeRefSpecAgent().value,
				domain.getPresentInqEmployeeRef().value,
				domain.getFutureDateRefPermit().value,
				null);*/
		
		return new EmploymentRoleDataDto();
	}
}
