package nts.uk.ctx.sys.auth.pubimp.roleset;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.pub.roleset.DefaultRoleSetDto;
import nts.uk.ctx.sys.auth.pub.roleset.RoleSetDto;
import nts.uk.ctx.sys.auth.pub.roleset.RoleSetPublisher;

@Stateless
public class RoleSetPublisherImpl implements RoleSetPublisher {

	@Inject
	private DefaultRoleSetRepository defaultRoleSetRepo;
	
	@Inject
	private RoleSetRepository roleSetRepo;
	
	@Override
	public Optional<DefaultRoleSetDto> getDefault(String companyId) {
		return defaultRoleSetRepo.findByCompanyId(companyId)
				.map(r -> new DefaultRoleSetDto(r.getCompanyId(), r.getRoleSetCd().v()));
	}
	
	@Override
	public Optional<RoleSetDto> getRoleSet(String companyId, String roleSetCd) {
		return roleSetRepo.findByRoleSetCdAndCompanyId(roleSetCd, companyId)
				.map(domain -> convertToDto(domain));
	}
	
	private RoleSetDto convertToDto(RoleSet domain) {
		RoleSetDto roleSetDto = new RoleSetDto();
		roleSetDto.setCompanyId(domain.getCompanyId());
		roleSetDto.setRoleSetCd(domain.getRoleSetCd().v());
		roleSetDto.setRoleSetName(domain.getRoleSetName().v());
		
		if(domain.getEmploymentRoleId().isPresent()) {
			roleSetDto.setEmploymentRoleId(domain.getEmploymentRoleId().get());
		}
		
		if(domain.getPersonInfRoleId().isPresent()) {
			roleSetDto.setPersonInfRoleId(domain.getPersonInfRoleId().get());
		}
		
		if(domain.getHRRoleId().isPresent()) {
			roleSetDto.setHRRoleId(domain.getHRRoleId().get());
		}
		
		if(domain.getSalaryRoleId().isPresent()) {
			roleSetDto.setSalaryRoleId(domain.getSalaryRoleId().get());
		}
		
		if(domain.getMyNumberRoleId().isPresent()) {
			roleSetDto.setMyNumberRoleId(domain.getMyNumberRoleId().get());
		}
		
		if(domain.getOfficeHelperRoleId().isPresent()) {
			roleSetDto.setOfficeHelperRoleId(domain.getOfficeHelperRoleId().get());
		}
    	return roleSetDto;
    }

}
