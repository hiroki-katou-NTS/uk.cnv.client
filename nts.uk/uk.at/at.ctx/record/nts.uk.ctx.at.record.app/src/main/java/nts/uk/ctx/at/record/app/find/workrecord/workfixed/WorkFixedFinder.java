/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.app.find.workrecord.workfixed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.adapter.person.EmpBasicInfoImport;
import nts.uk.ctx.at.record.dom.adapter.person.PersonInfoAdapter;
import nts.uk.ctx.at.record.dom.adapter.person.PersonInfoImport;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmed;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmedRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class WorkFixedFinder.
 */
@Stateless
public class WorkFixedFinder {

	/** The workfixed repository. */
	@Inject
	private EmploymentConfirmedRepository workfixedRepository;

	/** The person info adapter. */
	@Inject
	private PersonInfoAdapter personInfoAdapter;

	/**
	 * Find work fixed by wkp id and closure id.
	 *
	 * @param workPlaceId the work place id
	 * @param closureId the closure id
	 * @param cid the cid
	 * @return the work fixed finder dto
	 */
//	public WorkFixedFinderDto findWorkFixedByWkpIdAndClosureId(String workPlaceId, Integer closureId, String cid) {
//
//		Optional<EmploymentConfirmed> workFixed = this.workfixedRepository.get(cid, workPlaceId, ClosureIdclosureId, new YearMonth(1));
//		WorkFixedFinderDto workFixedDto = new WorkFixedFinderDto();
//
//		if (workFixed.isPresent()) {
//			workFixed.get().saveToMemento(workFixedDto);
//		}
//
//		return workFixedDto;
//	}

	/**
	 * Find work fixed info.
	 *
	 * @param listDto the list dto
	 * @return the list
	 */
	public List<WorkFixedFinderDto> findWorkFixedInfo(List<WorkFixedFinderDto> listDto) {
		// Get company id
		String companyId = AppContexts.user().companyId();
		
		// Get WorkFixed info
		List<EmploymentConfirmed> listWorkFixed = this.workfixedRepository.getListByCompany(companyId);
		
		List<EmpBasicInfoImport> employees = this.personInfoAdapter.getListPersonInfo(listWorkFixed.stream().map(m -> m.getEmployeeId()).collect(Collectors.toList()));
		
		List<WorkFixedFinderDto> result = new ArrayList<>();
		
		result = listWorkFixed.stream().map(m -> {
			Optional<EmpBasicInfoImport> exist = employees.stream().filter(f -> f.getEmployeeId().equals(m.getEmployeeId())).findFirst();
			return new WorkFixedFinderDto(
					m.getClosureId().value,
					exist.map(e -> e.getPId()).orElse(""),
					m.getWorkplaceId().v(),
					null,
					m.getDate().toDate(),
					m.getProcessYM().v(),
					m.getCompanyId().toString(),
					exist.map(e -> e.getNamePerson()).orElse(""));
		}).collect(Collectors.toList());
		
		return result;
		

//		// Get WorkFixed info
//		List<EmploymentConfirmed> listWorkFixed = this.workfixedRepository.getListByCompany(companyId);
//		Map<Entry<Integer, String>, EmploymentConfirmed> mapWorkFixed = listWorkFixed
//				.stream()
//				.collect(Collectors.toMap(
//						item -> new ImmutablePair<Integer, String>(item.getClosureId(), item.getWkpId()),
//						Function.identity()));
//
//		// Get all Person info
//		List<String> listPersonId = listWorkFixed.stream()
//				.map(EmploymentConfirmed::getConfirmPid)
//				.distinct()
//				.filter(Objects::nonNull)
//				.collect(Collectors.toList());
//		Map<String, PersonInfoImport> listPerson = this.personInfoAdapter.getByListId(listPersonId)
//				.stream()
//				.collect(Collectors.toMap(PersonInfoImport::getPersonId, Function.identity()));
//
//		return listDto.stream()
//				.map(dto -> {
//					WorkFixed workFixed = mapWorkFixed
//							.get(new ImmutablePair<Integer, String>(dto.getClosureId(), dto.getWkpId()));
//					if (workFixed == null) {
//						return null;
//					}
//		
//					// Get WorkFixed info
//					if (!Strings.isEmpty(workFixed.getConfirmPid())) {
//						// Get Person info
//						PersonInfoImport person = listPerson.get(workFixed.getConfirmPid());
//						if (person == null) {
//							workFixed.saveToMemento(dto);
//							return dto;
//						}
//						dto.setEmployeeName(person.getPersonName());
//					}
//		
//					workFixed.saveToMemento(dto);
//					return dto;
//				})
//				.filter(Objects::nonNull)
//				.collect(Collectors.toList());
	}

	/**
	 * Find current person name.
	 *
	 * @return the person info work fixed dto
	 */
	public PersonInfoWorkFixedDto findCurrentPersonName() {
		// Get Person Id
		String personId = AppContexts.user().personId();
		PersonInfoImport personImportDto = this.personInfoAdapter.getPersonInfo(personId);
		if (personImportDto == null) {
			return null;
		}
		return PersonInfoWorkFixedDto.builder()
				.employeeId(personImportDto.getPersonId())
				.employeeName(personImportDto.getPersonName())
				.build();
	}
}
