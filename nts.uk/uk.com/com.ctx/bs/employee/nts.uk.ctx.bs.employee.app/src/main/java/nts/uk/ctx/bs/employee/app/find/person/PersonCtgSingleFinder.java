package nts.uk.ctx.bs.employee.app.find.person;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.bs.person.dom.person.info.Person;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.uk.ctx.bs.person.dom.person.personinfoctgdata.item.PerInfoItemDataRepository;
import nts.uk.shr.pereg.app.find.PeregSingleFinder;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.dto.PeregDto;
import nts.uk.shr.pereg.app.find.dto.PersonOptionalDto;

@Stateless
public class PersonCtgSingleFinder implements PeregSingleFinder<PeregPersonDto> {

	@Inject
	private PersonRepository personRepo;

	@Inject
	private PerInfoItemDataRepository perInfoItemDataRepository;

	@Override
	public String targetCategoryCode() {
		return "CS00001";
	}

	@Override
	public Class<PeregPersonDto> dtoClass() {
		return PeregPersonDto.class;
	}

	/**
	 * the function handles finder return: PeregQueryResult
	 */
	@Override
	public PeregDto getSingleData(PeregQuery query) {
		Optional<Person> person = personRepo.getByPersonId(query.getPersonId());
		List<PersonOptionalDto> lstCtgItemOptionalDto = perInfoItemDataRepository
				.getAllInfoItemByRecordId(person.get().getPersonId()).stream().map(itemData -> itemData.genToPeregDto())
				.collect(Collectors.toList());
		return PeregDto.createWithPersonOptionData(PeregPersonDto.createFromDomain(person.get()),
				lstCtgItemOptionalDto);
	}
}
