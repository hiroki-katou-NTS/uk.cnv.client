package nts.uk.ctx.sys.auth.pubimp.grant;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleRepository;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPersonRepository;
import nts.uk.ctx.sys.auth.pub.grant.RoleSetGrantedJobTitleDetailDto;
import nts.uk.ctx.sys.auth.pub.grant.RoleSetGrantedJobTitleDto;
import nts.uk.ctx.sys.auth.pub.grant.RoleSetGrantedPersonDto;
import nts.uk.ctx.sys.auth.pub.grant.RoleSetGrantedPublisher;

@Stateless
public class RoleSetGrantedPublisherImpl implements RoleSetGrantedPublisher {

	@Inject
	private RoleSetGrantedPersonRepository roleSetPersonRepo;
	
	@Inject
	private RoleSetGrantedJobTitleRepository roleSetJobRepo;
	
	@Override
	public Optional<RoleSetGrantedPersonDto> getPersonGranted(String employeeId) {
		return roleSetPersonRepo.getByEmployeeDate(employeeId, GeneralDate.today())
				.map(r -> new RoleSetGrantedPersonDto(r.getRoleSetCd().v(), r.getCompanyId(), 
						r.getValidPeriod(), r.getEmployeeID()));
	}

	@Override
	public Optional<RoleSetGrantedJobTitleDto> getJobTitleGranted(String companyId) {
		return roleSetJobRepo.getOneByCompanyId(companyId).map(j -> {
			List<RoleSetGrantedJobTitleDetailDto> details = j.getDetails().stream()
				.map(d -> new RoleSetGrantedJobTitleDetailDto(d.getRoleSetCd().v(), 
						d.getJobTitleId(), d.getCompanyId()))
				.collect(Collectors.toList());
			return new RoleSetGrantedJobTitleDto(j.getCompanyId(), j.isApplyToConcurrentPerson(), details);
		});
	}

}
