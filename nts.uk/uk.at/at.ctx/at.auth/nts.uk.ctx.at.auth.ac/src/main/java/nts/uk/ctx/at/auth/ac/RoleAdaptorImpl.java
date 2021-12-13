package nts.uk.ctx.at.auth.ac;

import java.util.Optional;
import java.util.OptionalInt;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.auth.dom.adapter.role.RoleAdaptor;
import nts.uk.ctx.at.auth.dom.adapter.role.RoleImport;
import nts.uk.ctx.at.auth.dom.adapter.role.RoleInformationImport;
import nts.uk.ctx.sys.auth.pub.role.RoleExportRepo;
import nts.uk.ctx.sys.auth.pub.role.RollInformationExport;

@Stateless
public class RoleAdaptorImpl implements RoleAdaptor {

	@Inject
	private RoleExportRepo roleExportRepo;
	
	
	@Override
	public Optional<RoleImport> findByRoleId(String roleId) {
		return roleExportRepo.findByRoleId(roleId).map(x -> new RoleImport(
				x.getCompanyId(), 
				x.getRoleId(), 
				x.getRoleCode(), 
				x.getRoleName(), 
				x.getAssignAtr(), 
				x.getEmployeeReferenceRange()));
	}

	@Override
	public RoleInformationImport getRoleIncludCategoryFromUserID(String userId, int roleType, GeneralDate baseDate,
			String companyId) {
		
		RollInformationExport export = roleExportRepo.getRoleIncludCategoryFromUserID(userId, roleType, baseDate, companyId);
		if (export != null) {
			RoleInformationImport result = new RoleInformationImport(export.getRoleCharge(), export.getRoleId());
			return result;
		}
		return null;
	}

	@Override
	public OptionalInt findEmpRangeByRoleID(String roleID) {
		OptionalInt result = roleExportRepo.findEmpRangeByRoleID(roleID);
		return result;
	}

	@Override
	public Integer getEmployeeReferenceRange(String userId, int roleType, GeneralDate baseDate) {
		// TODO Auto-generated method stub
		return roleExportRepo.getEmployeeReferenceRange(userId, roleType, baseDate);
	}

}
