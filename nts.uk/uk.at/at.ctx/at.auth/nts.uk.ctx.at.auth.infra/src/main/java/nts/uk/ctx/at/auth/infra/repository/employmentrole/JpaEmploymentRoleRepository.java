package nts.uk.ctx.at.auth.infra.repository.employmentrole;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRoleRepository;
import nts.uk.ctx.at.auth.infra.entity.employmentrole.KacmtRoleAttendance;
import nts.uk.ctx.at.auth.infra.entity.employmentrole.KacmtEmploymentRolePK;

@Stateless
public class JpaEmploymentRoleRepository extends JpaRepository implements EmploymentRoleRepository {

	private static final String GET_ALL_BY_COMPANY_ID = "SELECT e"
			+ " FROM KacmtRoleAttendance e"
			+ " WHERE e.kacmtEmploymentRolePK.companyID = :companyID";
	
	private static final String GET_EMPLOYMENT_BY_ID = GET_ALL_BY_COMPANY_ID
			+ " AND e.kacmtEmploymentRolePK.roleID = :roleID";

	@Override
	public List<EmploymentRole> getAllByCompanyId(String companyId) {
		return this.queryProxy().query(GET_ALL_BY_COMPANY_ID, KacmtRoleAttendance.class)
		.setParameter("companyID", companyId)
		.getList(c -> c.toDomain());
	}

	@Override
	public List<EmploymentRole> getListEmploymentRole(String companyId) {
		return this.queryProxy().query(GET_ALL_BY_COMPANY_ID, KacmtRoleAttendance.class)
				.setParameter("companyID", companyId)
				.getList(c -> c.toDomain());
	}
	
	@Override
	public Optional<EmploymentRole> getEmploymentRoleById(String companyId, String roleId) {
		return this.queryProxy().query(GET_EMPLOYMENT_BY_ID, KacmtRoleAttendance.class)
				.setParameter("companyID", companyId)
				.setParameter("roleID", roleId)
				.getSingle(c -> c.toDomain());
	}

	@Override
	public void addEmploymentRole(EmploymentRole employmentRole) {
		this.commandProxy().insert(KacmtRoleAttendance.toEntity(employmentRole));
		
	}

	@Override
	public void updateEmploymentRole(EmploymentRole employmentRole) {
		KacmtRoleAttendance dataUpdate = KacmtRoleAttendance.toEntity(employmentRole);
		KacmtRoleAttendance newData = this.queryProxy().find(dataUpdate.kacmtEmploymentRolePK, KacmtRoleAttendance.class).get();
		newData.setScheduleEmployeeRef(dataUpdate.scheduleEmployeeRef);
		newData.setBookEmployeeRef(dataUpdate.bookEmployeeRef);
		newData.setEmployeeRefSpecAgent(dataUpdate.employeeRefSpecAgent);
		newData.setPresentInqEmployeeRef(dataUpdate.presentInqEmployeeRef);
		newData.setFutureDateRefPermit(dataUpdate.futureDateRefPermit);
		this.commandProxy().update(newData);
	}

	@Override
	public void deleteEmploymentRole(String companyId, String roleId) {
		KacmtEmploymentRolePK kacmtEmploymentRolePK = new  KacmtEmploymentRolePK(companyId,roleId);
		this.commandProxy().remove(KacmtRoleAttendance.class,kacmtEmploymentRolePK);
	}

}