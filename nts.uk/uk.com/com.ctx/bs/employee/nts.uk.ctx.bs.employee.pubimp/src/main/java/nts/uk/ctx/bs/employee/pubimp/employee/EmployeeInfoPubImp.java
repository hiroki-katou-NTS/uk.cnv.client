package nts.uk.ctx.bs.employee.pubimp.employee;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHist;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistByEmployee;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistItem;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmpInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoDto;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoDtoExport;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoPub;
import nts.uk.ctx.bs.person.dom.person.info.Person;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class EmployeeInfoPubImp implements EmployeeInfoPub {

	@Inject
	private EmployeeDataMngInfoRepository empDataMngRepo;

	@Inject
	private AffCompanyHistRepository affComHistRepo;

	@Inject
	PersonRepository personRepo;

	@Inject
	AffCompanyHistRepository affCompanyHistRepo;

	@Override
	public Optional<EmployeeInfoDtoExport> getEmployeeInfo(String companyId, String employeeCode) {
		// Req No.125

		Optional<EmployeeDataMngInfo> empInfo = empDataMngRepo.getEmployeeByCidScd(companyId, employeeCode);

		if (!empInfo.isPresent()) {
			return Optional.empty();
		} else {
			EmployeeDataMngInfo emp = empInfo.get();
			
			Optional<Person> person = personRepo.getByPId(emp.getPersonId());
			
			EmployeeInfoDtoExport result = new EmployeeInfoDtoExport(emp.getCompanyId(),
					emp.getEmployeeCode() == null ? null : emp.getEmployeeCode().v(), emp.getEmployeeId(),
					emp.getPersonId(), (person.isPresent() && person.get().getPersonNameGroup().getBusinessName() != null) ? person.get().getPersonNameGroup().getPersonName().getFullName().v() : null);
			return Optional.of(result);

		}
	}

	@Override
	public Optional<EmployeeInfoDto> getEmployeeInfoByCidPid(String companyId, String personId) {

		Optional<EmployeeDataMngInfo> empInfo = empDataMngRepo.findByCidPid(companyId, personId);

		if (!empInfo.isPresent()) {
			return Optional.empty();
		} else {
			EmployeeDataMngInfo emp = empInfo.get();

			EmployeeInfoDto result = new EmployeeInfoDto();
			result.setCompanyId(emp.getCompanyId());
			result.setPersonId(emp.getPersonId());
			result.setEmployeeId(emp.getEmployeeId());
			result.setEmployeeCode(emp.getEmployeeCode().v());
			result.setExternalCode(emp.getExternalCode() == null ? null : emp.getExternalCode().v());
			result.setDeleteDateTemporary(emp.getDeleteDateTemporary());
			/** 0 - NOTDELETED - 削除していない **/
			/** 1 - TEMPDELETED - 一時削除 **/
			/** 2 - PURGEDELETED - 完全削除 **/
			result.setDeletedStatus(emp.getDeletedStatus() == null ? null : emp.getDeletedStatus().value);
			result.setRemoveReason(emp.getRemoveReason() == null ? null : emp.getRemoveReason().v());
			return Optional.of(result);

		}
	}

	@Override
	public List<EmployeeInfoDtoExport> getEmployeesAtWorkByBaseDate(String companyId, GeneralDate standardDate) {

		List<EmployeeDataMngInfo> listEmpDomain = empDataMngRepo.findByCompanyId(companyId);

		List<EmployeeInfoDtoExport> result = new ArrayList<EmployeeInfoDtoExport>();

		if (CollectionUtil.isEmpty(listEmpDomain)) {
			return null;
		}

		List<String> employeeIds = listEmpDomain.stream().map(x -> x.getEmployeeId()).collect(Collectors.toList());
		
		
		List<AffCompanyHist> affCompanyHistList = affComHistRepo.getAffCompanyHistoryOfEmployees(employeeIds);
		
		Map<String, AffCompanyHist> map = affCompanyHistList.stream()
				.collect(Collectors.toMap(x -> x.getLstAffCompanyHistByEmployee().get(0).getSId(), x -> x));
		
		List<String> personIds = affCompanyHistList.stream().map(x -> x.getPId()).collect(Collectors.toList());
		
		Map<String, Person> personMap = personRepo.getPersonByPersonIds(personIds).stream().collect(Collectors.toMap( x -> x.getPersonId(), x -> x));
		
		
		result =  listEmpDomain.stream().map(x -> {
			AffCompanyHist affComHist = map.get(x.getEmployeeId());

			if (affComHist == null)
				return null;

			EmployeeInfoDtoExport employeeInfo = new EmployeeInfoDtoExport();
			AffCompanyHistByEmployee affComHistByEmp = affComHist.getAffCompanyHistByEmployee(x.getEmployeeId());

			if (affComHistByEmp.items() != null) {

				List<AffCompanyHistItem> filter = affComHistByEmp.getLstAffCompanyHistoryItem().stream().filter(m -> {
					return m.end().afterOrEquals(standardDate) && m.start().beforeOrEquals(standardDate);
				}).collect(Collectors.toList());

				if (!filter.isEmpty()) {

					Person person = personMap.get(affComHist.getPId());
					if (person != null ) {
						employeeInfo.setPersonId(person.getPersonId());
						employeeInfo.setPerName(person.getPersonNameGroup().getBusinessName() == null ? null
								: person.getPersonNameGroup().getPersonName().getFullName().v());
					}
				}
			}

			employeeInfo.setCompanyId(x.getCompanyId());
			employeeInfo.setEmployeeCode(x.getEmployeeCode() == null ? null : x.getEmployeeCode().v());
			employeeInfo.setEmployeeId(x.getEmployeeId());

			return employeeInfo;

		}).filter(y -> y != null).collect(Collectors.toList());

		return result;
		
	}

	/*
	 * @Override public List<EmpBasicInfoExport> getListEmpBasicInfo(List<String>
	 * sid) { List<Employee> listEmpDomain = repo.getByListEmployeeId(sid);
	 * List<EmpBasicInfoExport> listResult = new ArrayList<>();
	 * 
	 * if (!listEmpDomain.isEmpty()) {
	 * 
	 * listResult = listEmpDomain.stream() .map(item ->
	 * EmpBasicInfoExport.builder().employeeId(item.getSId()).employeeCode(item.
	 * getSCd().v())
	 * .pId(item.getPId()).companyMailAddress(item.getCompanyMail().v())
	 * .entryDate(item.getListEntryJobHist().get(0).getJoinDate())
	 * .retiredDate(item.getListEntryJobHist().get(0).getRetirementDate()).build ())
	 * .collect(Collectors.toList());
	 * 
	 * List<String> pids =
	 * listEmpDomain.stream().map(Employee::getPId).collect(Collectors.toList()) ;
	 * 
	 * List<Person> listPersonDomain = personRepo.getPersonByPersonIds(pids);
	 * 
	 * if (!listPersonDomain.isEmpty()) { for (int j = 0; j < listResult.size();
	 * j++) { EmpBasicInfoExport resultItem = listResult.get(j); Person per =
	 * listPersonDomain.stream().filter(m ->
	 * m.getPersonId().equals(resultItem.getPId()))
	 * .collect(Collectors.toList()).get(0);
	 * listResult.get(j).setPersonMailAddress(null);
	 * listResult.get(j).setPersonName(per.getPersonNameGroup().getPersonName().
	 * getFullName() == null ? "" :
	 * per.getPersonNameGroup().getPersonName().getFullName().v());
	 * listResult.get(j).setGender(per.getGender() == null ? 0 :
	 * per.getGender().value); listResult.get(j).setBirthDay(per.getBirthDate()); }
	 * } }
	 * 
	 * return listResult; }
	 */

	/**
	 * Get Employee Info By Pid. Requets List No.124
	 */
	@Override
	public List<EmpInfoExport> getEmpInfoByPid(String pid) {

		List<EmpInfoExport> listResult = new ArrayList<>();

		if (pid == null) {
			return null;
		}
		// get domain Affiliated Company History-所属会社履歴
		AffCompanyHist affCompanyHist = this.affCompanyHistRepo.getAffCompanyHistoryOfPerson(pid);

		// get systemDate
		Date date = new Date();
		GeneralDate systemDate = GeneralDate.legacyDate(date);

		if (affCompanyHist != null) {

			if (!CollectionUtil.isEmpty(affCompanyHist.getLstAffCompanyHistByEmployee())) {

				// check all item in List<AffCompanyHistItem>
				for (AffCompanyHistByEmployee affCompanyHistByEmployee : affCompanyHist
						.getLstAffCompanyHistByEmployee()) {

					if (!CollectionUtil.isEmpty(affCompanyHistByEmployee.getLstAffCompanyHistoryItem())) {

						for (AffCompanyHistItem affCompanyHistItem : affCompanyHistByEmployee
								.getLstAffCompanyHistoryItem()) {

							if (systemDate.beforeOrEquals(affCompanyHistItem.end())
									&& systemDate.afterOrEquals(affCompanyHistItem.start())) {
								Optional<Person> personOpt = personRepo.getByPersonId(affCompanyHist.getPId());
								if (personOpt.isPresent()) {
									Person person = personOpt.get();
									EmpInfoExport empInfoExport = new EmpInfoExport();
									empInfoExport.setPId(person.getPersonId() == null ? "" : person.getPersonId());

									empInfoExport.setPersonName(
											person.getPersonNameGroup().getPersonName().toString() == null ? ""
													: person.getPersonNameGroup().getPersonName().getFullName()
															.toString());

									empInfoExport.setEmployeeId(affCompanyHistByEmployee.getSId() == null ? ""
											: affCompanyHistByEmployee.getSId());

									if (affCompanyHistByEmployee.getSId() != null) {
										Optional<EmployeeDataMngInfo> employeeOpt = this.empDataMngRepo
												.findByEmpId(affCompanyHistByEmployee.getSId());

										if (employeeOpt.isPresent()) {
											EmployeeDataMngInfo employee = employeeOpt.get();
											empInfoExport.setEmployeeCode(employee.getEmployeeCode() == null ? ""
													: employee.getEmployeeCode().v());
											empInfoExport.setCompanyId(employee.getCompanyId());
										}
									}
									listResult.add(empInfoExport);
								}
							}
						}
					}
				}
			}
		}
		return listResult;
	}

}
