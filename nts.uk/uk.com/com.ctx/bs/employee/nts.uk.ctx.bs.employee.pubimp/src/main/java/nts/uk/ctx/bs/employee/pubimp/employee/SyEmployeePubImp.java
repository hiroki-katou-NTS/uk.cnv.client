/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.pubimp.employee;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.dom.access.person.SyPersonAdapter;
import nts.uk.ctx.bs.employee.dom.access.person.dto.PersonImport;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHist;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistByEmployee;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistItem;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.ver1.AffJobTitleHistoryItemRepository_ver1;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.ver1.AffJobTitleHistoryItem_ver1;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository_v1;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory_ver1;
import nts.uk.ctx.bs.employee.pub.employee.ConcurrentEmployeeExport;
import nts.uk.ctx.bs.employee.pub.employee.EmployeeBasicInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.EmployeeExport;
import nts.uk.ctx.bs.employee.pub.employee.JobClassification;
import nts.uk.ctx.bs.employee.pub.employee.MailAddress;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.ctx.bs.person.dom.person.info.Person;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class SyEmployeePubImp.
 */
@Stateless
public class SyEmployeePubImp implements SyEmployeePub {


	/** The person repository. */
	@Inject
	private SyPersonAdapter syPersonAdapter;

	/** The workplace history repository. */
	@Inject
	private AffWorkplaceHistoryRepository_v1 workplaceHistoryRepository_v1;

	@Inject
	private AffJobTitleHistoryItemRepository_ver1 jobTitleHistoryItemRepository_v1;

	/** The person repository. */
	@Inject
	private PersonRepository personRepository;

	/** The emp data mng repo. */
	@Inject
	private EmployeeDataMngInfoRepository empDataMngRepo;

	/** The aff com hist repo. */
	@Inject
	private AffCompanyHistRepository affComHistRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.basic.pub.company.organization.employee.EmployeePub#
	 * findByWpkIds(java.lang.String, java.util.List, nts.arc.time.GeneralDate)
	 */
	@Override
	public List<EmployeeExport> findByWpkIds(String companyId, List<String> workplaceIds, GeneralDate baseDate) {
		// Query
		// update use AffWorkplaceHistory_ver1 - get list Aff WorkplaceHistory by list wkpIds and base data
		List<AffWorkplaceHistory_ver1> affWorkplaceHistories = this.workplaceHistoryRepository_v1.getWorkplaceHistoryByWkpIdsAndDate(baseDate, workplaceIds);

		List<String> employeeIds = affWorkplaceHistories.stream().map(AffWorkplaceHistory_ver1::getEmployeeId)
				.collect(Collectors.toList());

		List<EmployeeDataMngInfo> employeeList = empDataMngRepo.findByListEmployeeId(companyId, employeeIds);
		String cid = AppContexts.user().companyId();
		Date date = new Date();
		GeneralDate systemDate = GeneralDate.legacyDate(date);

		return employeeList.stream().map(employee -> {

			EmployeeExport result = new EmployeeExport();

			AffCompanyHist affComHist = affComHistRepo.getAffCompanyHistoryOfEmployee(cid,employee.getEmployeeId());

			AffCompanyHistByEmployee affComHistByEmp = affComHist.getAffCompanyHistByEmployee(employee.getEmployeeId());

			AffCompanyHistItem affComHistItem = new AffCompanyHistItem();

			if (affComHistByEmp.items() != null) {

				List<AffCompanyHistItem> filter = affComHistByEmp.getLstAffCompanyHistoryItem().stream().filter(m -> {
					return m.end().afterOrEquals(systemDate) && m.start().beforeOrEquals(systemDate);
				}).collect(Collectors.toList());

				if (!filter.isEmpty()) {
					affComHistItem = filter.get(0);
					result.setJoinDate(affComHistItem.start());
					result.setRetirementDate(affComHistItem.end());
				}
			}

			result.setCompanyId(employee.getCompanyId());
			result.setSCd(employee.getEmployeeCode() == null ? null : employee.getEmployeeCode().v());
			result.setSId(employee.getEmployeeId());
			result.setPId(employee.getPersonId());
			result.setSMail("");// bo mail roi.

			return result;
		}).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub#getConcurrentEmployee(
	 * java.lang.String, java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public List<ConcurrentEmployeeExport> getConcurrentEmployee(String companyId, String jobId, GeneralDate baseDate) {
		// Query
		List<AffJobTitleHistoryItem_ver1> affJobTitleHistories = this.jobTitleHistoryItemRepository_v1.getByJobIdAndReferDate(jobId, baseDate);

		List<String> employeeIds = affJobTitleHistories.stream().map(AffJobTitleHistoryItem_ver1::getEmployeeId)
				.collect(Collectors.toList());

		List<EmployeeDataMngInfo> employeeList = empDataMngRepo.findByListEmployeeId(companyId, employeeIds);

		List<String> personIds = employeeList.stream().map(EmployeeDataMngInfo::getPersonId)
				.collect(Collectors.toList());

		List<PersonImport> persons = this.syPersonAdapter.findByPersonIds(personIds);

		Map<String, String> personNameMap = persons.stream()
				.collect(Collectors.toMap(PersonImport::getPersonId, PersonImport::getPersonName));

		// Return
		// TODO: Du san Q&A for jobCls #
		return employeeList.stream()
				.map(item -> ConcurrentEmployeeExport.builder().employeeId(item.getEmployeeId())
						.employeeCd(item.getEmployeeCode().v()).personName(personNameMap.get(item.getPersonId()))
						.jobId(jobId).jobCls(JobClassification.Principal).build())
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub#findByEmpId(java.lang.
	 * String)
	 */
	@Override
	public EmployeeBasicInfoExport findBySId(String sId) {

		EmployeeBasicInfoExport result = new EmployeeBasicInfoExport();
		// Employee Opt
		Optional<EmployeeDataMngInfo> empOpt = this.empDataMngRepo.findByEmpId(sId);
		if (!empOpt.isPresent()) {
			return null;
		}
		// Get Employee
		EmployeeDataMngInfo emp = empOpt.get();
		// Person Opt
		Optional<Person> personOpt = this.personRepository.getByPersonId(emp.getPersonId());
		if (!personOpt.isPresent()) {
			return null;
		}
		// Get Person
		Person person = personOpt.get();
		// String pname = person.getPersonNameGroup().getPersonName().getFullName().v();
		// EmployeeMail comMailAddr = emp.getCompanyMail();

		result.setPId(person.getPersonId());
		result.setEmployeeId(emp.getEmployeeId());
		result.setPName(person.getPersonNameGroup().getPersonName().getFullName().v());
		result.setGender(person.getGender().value);
		result.setPMailAddr(new MailAddress(""));
		result.setEmployeeCode(emp.getEmployeeCode().v());
		result.setCompanyMailAddr(new MailAddress(""));
		result.setBirthDay(person.getBirthDate());

		Date date = new Date();
		GeneralDate systemDate = GeneralDate.legacyDate(date);
		String cid = AppContexts.user().companyId();
		AffCompanyHist affComHist = affComHistRepo.getAffCompanyHistoryOfEmployee(cid,emp.getEmployeeId());

		AffCompanyHistByEmployee affComHistByEmp = affComHist.getAffCompanyHistByEmployee(emp.getEmployeeId());

		AffCompanyHistItem affComHistItem = new AffCompanyHistItem();

		if (affComHistByEmp.items() != null) {

			List<AffCompanyHistItem> filter = affComHistByEmp.getLstAffCompanyHistoryItem().stream().filter(m -> {
				return m.end().afterOrEquals(systemDate) && m.start().beforeOrEquals(systemDate);
			}).collect(Collectors.toList());

			if (!filter.isEmpty()) {
				affComHistItem = filter.get(0);
				result.setRetiredDate(affComHistItem.end());
				result.setEntryDate(affComHistItem.start());
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc) req 126
	 * 
	 * @see nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub#findBySIds(java.util.
	 * List)
	 */
	@Override
	public List<EmployeeBasicInfoExport> findBySIds(List<String> sIds) {

		EmployeeBasicInfoExport result = new EmployeeBasicInfoExport();
		String cid = AppContexts.user().companyId();
		Date date = new Date();
		GeneralDate systemDate = GeneralDate.legacyDate(date);

		List<EmployeeDataMngInfo> emps = this.empDataMngRepo.findByListEmployeeId(sIds);
		
		if(CollectionUtil.isEmpty(emps)) {
			return null;
		}

		List<String> pIds = emps.stream().map(EmployeeDataMngInfo::getPersonId).collect(Collectors.toList());

		List<Person> persons = this.personRepository.getPersonByPersonIds(pIds);

		Map<String, Person> mapPersons = persons.stream()
				.collect(Collectors.toMap(Person::getPersonId, Function.identity()));

		return emps.stream().map(employee -> {

			// Get Person
			Person person = mapPersons.get(employee.getPersonId());

			if (person != null) {
				result.setGender(person.getGender().value);
				result.setPName(person.getPersonNameGroup().getBusinessName() == null ? null
						: person.getPersonNameGroup().getBusinessName().v());
				result.setBirthDay(person.getBirthDate());
			}

			AffCompanyHist affComHist = affComHistRepo.getAffCompanyHistoryOfEmployee(cid,employee.getEmployeeId());

			AffCompanyHistByEmployee affComHistByEmp = affComHist.getAffCompanyHistByEmployee(employee.getEmployeeId());

			AffCompanyHistItem affComHistItem = new AffCompanyHistItem();

			if (affComHistByEmp.items() != null) {

				List<AffCompanyHistItem> filter = affComHistByEmp.getLstAffCompanyHistoryItem().stream().filter(m -> {
					return m.end().afterOrEquals(systemDate) && m.start().beforeOrEquals(systemDate);
				}).collect(Collectors.toList());

				if (!filter.isEmpty()) {
					affComHistItem = filter.get(0);
					result.setEntryDate(affComHistItem.getDatePeriod().start());
					result.setRetiredDate(affComHistItem.getDatePeriod().end());
				}
			}

			result.setPId(employee.getPersonId());
			result.setCompanyMailAddr(null);
			result.setEmployeeCode(employee.getEmployeeCode() == null ? null : employee.getEmployeeCode().v());
			result.setEmployeeId(employee.getEmployeeId());
			result.setPMailAddr(null);

			return result;
		}).collect(Collectors.toList());

	}

}
