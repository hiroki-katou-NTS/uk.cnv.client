package nts.uk.ctx.at.auth.infra.repository.employmentrole;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRoleRepository;
import nts.uk.ctx.at.auth.infra.entity.employmentrole.KacmtRoleAttendance;

@Stateless
public class JpaEmploymentRoleRepository extends JpaRepository implements EmploymentRoleRepository {

	private static final String GET_ALL_BY_COMPANY_ID = "SELECT e"
			+ " FROM KacmtRoleAttendance e"
			+ " WHERE e.companyID = :companyID";
	@Override
	public List<EmploymentRole> getAllByCompanyId(String companyId) {
		return this.queryProxy().query(GET_ALL_BY_COMPANY_ID, KacmtRoleAttendance.class)
		.setParameter("companyID", companyId)
		.getList(KacmtRoleAttendance::toDomain);
	}
	
	@Override
	public Optional<EmploymentRole> getEmploymentRoleById( String roleId) {
		val entityOpt = this.queryProxy().find(roleId,KacmtRoleAttendance.class);
		return entityOpt.map(KacmtRoleAttendance::toDomain);
	}

	@Override
	public void addEmploymentRole(EmploymentRole employmentRole) {
		this.commandProxy().insert(KacmtRoleAttendance.toEntity(employmentRole));
		
	}

	@Override
	public void updateEmploymentRole(EmploymentRole employmentRole) {
		KacmtRoleAttendance dataUpdate = KacmtRoleAttendance.toEntity(employmentRole);
		val oldDataOpt = this.queryProxy().find(dataUpdate.roleID, KacmtRoleAttendance.class);
		if(oldDataOpt.isPresent()){
			val oldData = oldDataOpt.get();
			oldData.setCompanyID(dataUpdate.companyID);
			oldData.setFutureDateRefPermit(dataUpdate.futureDateRefPermit);
			this.commandProxy().update(oldData);
		}
	}

	@Override
	public void deleteEmploymentRole(String roleId) {
		this.commandProxy().remove(KacmtRoleAttendance.class,roleId);
	}

}