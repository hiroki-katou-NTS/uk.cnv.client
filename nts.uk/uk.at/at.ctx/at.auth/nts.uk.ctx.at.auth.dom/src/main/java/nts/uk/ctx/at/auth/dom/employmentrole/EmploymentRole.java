package nts.uk.ctx.at.auth.dom.employmentrole;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/*
 * 就業ロール
 */
@Getter
public class EmploymentRole extends AggregateRoot {
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
	private ScheduleEmployeeRef scheduleEmployeeRef;
	/**
	 * 予約画面社員参照
	 */
	private EmployeeRefRange bookEmployeeRef; 
	/**
	 * 代行者指定時社員参照 
	 */
	private  EmployeeRefRange employeeRefSpecAgent;
	/**
	 * 在席照会社員参照 
	 */
	private EmployeeReferenceRange presentInqEmployeeRef; 
	/**
	 * 未来日参照許可 FUTURE_DATE_REF_PERMIT
	 */
	private DisabledSegment futureDateRefPermit;
	
	public static EmploymentRole createFromJavaType(String companyID, String roleId) {
		return new EmploymentRole(companyID, roleId);
	}

	public EmploymentRole(String companyId, String roleId) {
		super();
		this.companyId = companyId;
		this.roleId = roleId;
	}

	public EmploymentRole(String companyId, String roleId, ScheduleEmployeeRef scheduleEmployeeRef,
			EmployeeRefRange bookEmployeeRef, EmployeeRefRange employeeRefSpecAgent,
			EmployeeReferenceRange presentInqEmployeeRef, DisabledSegment futureDateRefPermit) {
		super();
		this.companyId = companyId;
		this.roleId = roleId;
		this.scheduleEmployeeRef = scheduleEmployeeRef;
		this.bookEmployeeRef = bookEmployeeRef;
		this.employeeRefSpecAgent = employeeRefSpecAgent;
		this.presentInqEmployeeRef = presentInqEmployeeRef;
		this.futureDateRefPermit = futureDateRefPermit;
	}

}
