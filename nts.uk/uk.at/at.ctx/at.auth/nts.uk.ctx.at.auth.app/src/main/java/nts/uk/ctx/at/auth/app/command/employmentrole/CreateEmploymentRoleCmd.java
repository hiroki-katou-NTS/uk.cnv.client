package nts.uk.ctx.at.auth.app.command.employmentrole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.auth.dom.employmentrole.DisabledSegment;
import nts.uk.ctx.at.auth.dom.employmentrole.EmployeeRefRange;
import nts.uk.ctx.at.auth.dom.employmentrole.EmployeeReferenceRange;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;
import nts.uk.ctx.at.auth.dom.employmentrole.ScheduleEmployeeRef;

@Getter
@Setter
@NoArgsConstructor
public class CreateEmploymentRoleCmd {
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
	public CreateEmploymentRoleCmd(String companyId, String roleId, int scheduleEmployeeRef, int bookEmployeeRef,
			int employeeRefSpecAgent, int presentInqEmployeeRef, int futureDateRefPermit) {
		super();
		this.companyId = companyId;
		this.roleId = roleId;
		this.scheduleEmployeeRef = scheduleEmployeeRef;
		this.bookEmployeeRef = bookEmployeeRef;
		this.employeeRefSpecAgent = employeeRefSpecAgent;
		this.presentInqEmployeeRef = presentInqEmployeeRef;
		this.futureDateRefPermit = futureDateRefPermit;
	}
	
	public EmploymentRole toDomain() {
		return new  EmploymentRole(
				this.companyId,
				this.roleId,
				EnumAdaptor.valueOf(this.scheduleEmployeeRef, ScheduleEmployeeRef.class),
				EnumAdaptor.valueOf(this.bookEmployeeRef,EmployeeRefRange.class),
				EnumAdaptor.valueOf(this.employeeRefSpecAgent,EmployeeRefRange.class),
				EnumAdaptor.valueOf(this.presentInqEmployeeRef,EmployeeReferenceRange.class),
				EnumAdaptor.valueOf(this.futureDateRefPermit,DisabledSegment.class)
				);
	}
	
}
