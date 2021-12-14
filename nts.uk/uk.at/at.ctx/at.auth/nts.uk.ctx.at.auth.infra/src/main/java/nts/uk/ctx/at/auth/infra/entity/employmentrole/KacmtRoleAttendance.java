package nts.uk.ctx.at.auth.infra.entity.employmentrole;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.auth.dom.employmentrole.DisabledSegment;
import nts.uk.ctx.at.auth.dom.employmentrole.EmployeeRefRange;
import nts.uk.ctx.at.auth.dom.employmentrole.EmployeeReferenceRange;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;
import nts.uk.ctx.at.auth.dom.employmentrole.ScheduleEmployeeRef;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "KACMT_ROLE_ATTENDANCE")
@Setter
public class KacmtRoleAttendance extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = -5374494134003331017L;

	@EmbeddedId
	public KacmtEmploymentRolePK kacmtEmploymentRolePK;
	
	@Column(name = "SCHEDULE_EMPLOYEE_REF")
	public int scheduleEmployeeRef;
	
	@Column(name = "BOOK_EMPLOYEE_REF")
	public int bookEmployeeRef;
	
	@Column(name = "EMPLOYEE_REF_SPEC_AGEN")
	public int employeeRefSpecAgent;
	
	@Column(name = "PRESENT_INQ_EMPLOYEE_REF")
	public int presentInqEmployeeRef;
	
	@Column(name = "FUTURE_DATE_REF_PERMIT")
	public int futureDateRefPermit;

	@Override
	protected Object getKey() {
		return this.kacmtEmploymentRolePK;
	}
	

	public EmploymentRole toDomain() {
		return new EmploymentRole(
			this.kacmtEmploymentRolePK.companyID,
			this.kacmtEmploymentRolePK.roleID,
			EnumAdaptor.valueOf(this.scheduleEmployeeRef, ScheduleEmployeeRef.class),
			EnumAdaptor.valueOf(this.bookEmployeeRef,EmployeeRefRange.class),
			EnumAdaptor.valueOf(this.employeeRefSpecAgent,EmployeeRefRange.class),
			EnumAdaptor.valueOf(this.presentInqEmployeeRef,EmployeeReferenceRange.class),
			EnumAdaptor.valueOf(this.futureDateRefPermit,DisabledSegment.class)
		);
	}

	public KacmtRoleAttendance(KacmtEmploymentRolePK kacmtEmploymentRolePK) {
		super();
		this.kacmtEmploymentRolePK = kacmtEmploymentRolePK;
	}


	public KacmtRoleAttendance(KacmtEmploymentRolePK kacmtEmploymentRolePK, int scheduleEmployeeRef,
			int bookEmployeeRef, int employeeRefSpecAgent, int presentInqEmployeeRef, int futureDateRefPermit) {
		super();
		this.kacmtEmploymentRolePK = kacmtEmploymentRolePK;
		this.scheduleEmployeeRef = scheduleEmployeeRef;
		this.bookEmployeeRef = bookEmployeeRef;
		this.employeeRefSpecAgent = employeeRefSpecAgent;
		this.presentInqEmployeeRef = presentInqEmployeeRef;
		this.futureDateRefPermit = futureDateRefPermit;
	}
	
	public static KacmtRoleAttendance toEntity(EmploymentRole domain) {
		return new KacmtRoleAttendance(
				new KacmtEmploymentRolePK(domain.getCompanyId(),domain.getRoleId()),
				domain.getScheduleEmployeeRef().value,
				domain.getBookEmployeeRef().value,
				domain.getEmployeeRefSpecAgent().value,
				domain.getPresentInqEmployeeRef().value,
				domain.getFutureDateRefPermit().value
				);
	}

}
