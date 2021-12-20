package nts.uk.ctx.sys.gateway.app.find.login.password.userpassword;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;
import nts.uk.shr.pereg.app.ComboBoxObject;
import nts.uk.shr.pereg.app.find.PeregFinder;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.PeregQueryByListEmp;
import nts.uk.shr.pereg.app.find.dto.DataClassification;
import nts.uk.shr.pereg.app.find.dto.GridPeregDomainBySidDto;
import nts.uk.shr.pereg.app.find.dto.GridPeregDomainDto;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

@Stateless
public class PeregLoginPasswordFinder implements PeregFinder<PeregLoginPasswordDto>{
	
	@Inject
	private UserRepository userRepo;

	@Override
	public String targetCategoryCode() {
		return "CS00100";
	}

	@Override
	public Class<PeregLoginPasswordDto> dtoClass() {
		return PeregLoginPasswordDto.class;
	}

	@Override
	public DataClassification dataType() {
		return DataClassification.PERSON;
	}

	@Override
	public PeregDomainDto getSingleData(PeregQuery query) {
		Optional<User> userOpt = userRepo.getByAssociatedPersonId(query.getPersonId());
		if(userOpt.isPresent())
			return PeregLoginPasswordDto.createFromDomain(userOpt.get());
		return null;
	}

	@Override
	public List<PeregDomainDto> getListData(PeregQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ComboBoxObject> getListFirstItems(PeregQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GridPeregDomainDto> getAllData(PeregQueryByListEmp query) {
		List<GridPeregDomainDto> result = new ArrayList<>();

		List<String> pids = query.getEmpInfos().stream().map(c -> c.getPersonId()).collect(Collectors.toList());
		
		query.getEmpInfos().forEach(c -> {
			result.add(new GridPeregDomainDto(c.getEmployeeId(), c.getPersonId(), null));
		});
		
		List<User> domains = userRepo.getListUserByListAsID(pids);
		
		result.stream().forEach(c ->{
			Optional<User> domainOpt = domains.stream().filter(p -> p.getAssociatedPersonID().isPresent() ? 
					p.getAssociatedPersonID().get().equals(c.getPersonId()) : null).findFirst();
			c.setPeregDomainDto(domainOpt.isPresent() == true ? PeregLoginPasswordDto.createFromDomain(domainOpt.get()) : null);
			
		});
		
		return result;
	}

	@Override
	public List<GridPeregDomainBySidDto> getListData(PeregQueryByListEmp query) {
		// TODO Auto-generated method stub
		return null;
	}

}
