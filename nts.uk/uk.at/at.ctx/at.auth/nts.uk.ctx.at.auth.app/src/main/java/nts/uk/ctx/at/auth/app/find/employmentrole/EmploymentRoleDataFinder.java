package nts.uk.ctx.at.auth.app.find.employmentrole;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.auth.app.find.employmentrole.dto.EmploymentRoleDataDto;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.RoleDto;
import nts.uk.ctx.at.auth.dom.adapter.role.RoleAdaptor;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRoleRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class EmploymentRoleDataFinder {
	
	@Inject
	private EmploymentRoleRepository repo;
	
	@Inject
	private RoleAdaptor roleAdaptor;
	
	public List<EmploymentRoleDataDto> getListEmploymentRole() {
		String companyID = AppContexts.user().companyId();
		List<EmploymentRoleDataDto> data = repo.getAllByCompanyId(companyID).stream()
				.map(c-> EmploymentRoleDataDto.fromDomain(c)).collect(Collectors.toList());
		if(data.isEmpty())
			return Collections.emptyList();
		return data;
	}
	
	public EmploymentRoleDataDto getEmploymentRoleById( String roleId) {
		Optional<EmploymentRoleDataDto> data = repo.getEmploymentRoleById(roleId).map(c->EmploymentRoleDataDto.fromDomain(c));
		if(data.isPresent()) {
			EmploymentRoleDataDto result = data.get();
			result.setRole(roleAdaptor.findByRoleId(roleId).map(x -> new RoleDto(
					x.getCompanyId(), 
					x.getRoleId(), 
					x.getRoleCode(), 
					x.getName(), 
					x.getAssignAtr(), 
					x.getEmployeeReferenceRange())).orElse(null));
			return result;
		}
		return new EmploymentRoleDataDto();
	}
	
}