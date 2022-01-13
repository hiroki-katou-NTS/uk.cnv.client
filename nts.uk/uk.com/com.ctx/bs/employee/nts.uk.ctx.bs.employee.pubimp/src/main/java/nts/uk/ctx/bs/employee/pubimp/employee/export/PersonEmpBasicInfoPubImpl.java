package nts.uk.ctx.bs.employee.pubimp.employee.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHist;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistByEmployee;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.pub.employee.EmployeeBasicInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.export.PersonEmpBasicInfoPub;
import nts.uk.ctx.bs.employee.pub.employee.export.dto.PersonEmpBasicInfoDto;
import nts.uk.ctx.bs.person.dom.person.info.Person;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.arc.time.calendar.period.DatePeriod;

@Stateless
public class PersonEmpBasicInfoPubImpl implements PersonEmpBasicInfoPub {

	@Inject
	private EmployeeDataMngInfoRepository empDataRepo;
	
	@Inject
	private AffCompanyHistRepository affComHisRepo;
	
	@Inject
	private PersonRepository personRepo;

	@Override
	public List<EmployeeBasicInfoExport> getFromEmployeeIdList(List<String> employeeIds) {

		List<EmployeeDataMngInfo> employeeDataInfoList = empDataRepo.findByListEmployeeId(employeeIds);
		if (employeeDataInfoList.isEmpty()) {
			return new ArrayList<>();
		}
		List<String> personIds = employeeDataInfoList.stream().map(e -> e.getPersonId()).collect(Collectors.toList());
		
		Map<String, Person> personMap = personRepo.getPersonByPersonIds(personIds).stream()
				.collect(Collectors.toMap(x -> x.getPersonId(), x -> x));
		
		if (personMap.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<EmployeeBasicInfoExport> resultList = new ArrayList<>();
		
		for (EmployeeDataMngInfo emp : employeeDataInfoList) {
			Person person = personMap.get(emp.getPersonId());
			
			if (person == null) {
				continue;
			}
			
			//:TODO
			
		}
		
		return resultList;
	}

	@Override
	public List<PersonEmpBasicInfoDto> getPerEmpBasicInfo(List<String> employeeIds) {
		List<EmployeeDataMngInfo> employeeDataInfoList = empDataRepo.findByListEmployeeId(employeeIds);
		if (employeeDataInfoList.isEmpty()) {
			return new ArrayList<>();
		}

		Map<String, DatePeriod> employeeHistMap = new HashMap<>();
		List<AffCompanyHist> affCompanyHistList = affComHisRepo.getAffCompanyHistoryOfEmployees(employeeIds);
		for (AffCompanyHist affComHist : affCompanyHistList) {
			List<AffCompanyHistByEmployee> lstAffCompanyHistByEmployee = affComHist.getLstAffCompanyHistByEmployee();
			for (AffCompanyHistByEmployee employeeHist : lstAffCompanyHistByEmployee) {
				employeeHistMap.put(employeeHist.getSId(), employeeHist.getLatestPeriod());
			}
		}

		Set<String> personIds = employeeDataInfoList.stream().map(e -> e.getPersonId()).collect(Collectors.toSet());

		Map<String, Person> personMap =  personRepo.getPersonByPersonIds(personIds.stream().collect(Collectors.toList())).stream()
				.filter(distinctByKey( p -> p.getPersonId()))
				.collect(Collectors.toMap(x -> x.getPersonId(), x -> x));

		return employeeDataInfoList.stream()
				.filter(emp -> employeeHistMap.get(emp.getEmployeeId()) != null)
				.filter(emp -> personMap.get(emp.getPersonId()) != null)
				.map(emp -> {
					DatePeriod period = employeeHistMap.get(emp.getEmployeeId());
					Person person = personMap.get(emp.getPersonId());
					return new PersonEmpBasicInfoDto(emp.getPersonId(), emp.getEmployeeId(),
							person.getPersonNameGroup().getBusinessName().v(), person.getGender().value,
							person.getBirthDate(), emp.getEmployeeCode().v(), period.start(), period.end());
				}).collect(Collectors.toList());
	}

	@Override
	public List<PersonEmpBasicInfoDto> getEmpBasicInfo(String companyId, List<String> employeeIds) {
		List<EmployeeDataMngInfo> employeeDataInfoList = empDataRepo.findByListEmployeeId(companyId, employeeIds);
		if (employeeDataInfoList.isEmpty()) {
			return new ArrayList<>();
		}

		Map<String, DatePeriod> employeeHistMap = new HashMap<>();
		List<AffCompanyHist> affCompanyHistList = affComHisRepo.getAffCompanyHistoryOfEmployees(employeeIds);
		for (AffCompanyHist affComHist : affCompanyHistList) {
			List<AffCompanyHistByEmployee> lstAffCompanyHistByEmployee = affComHist.getLstAffCompanyHistByEmployee();
			for (AffCompanyHistByEmployee employeeHist : lstAffCompanyHistByEmployee) {
				employeeHistMap.put(employeeHist.getSId(), employeeHist.getLatestPeriod());
			}
		}

		List<String> personIds = employeeDataInfoList.stream().map(e -> e.getPersonId()).collect(Collectors.toList());

		Map<String, Person> personMap = personRepo.getPersonByPersonIds(personIds).stream()
				.collect(Collectors.toMap(x -> x.getPersonId(), x -> x));

		return employeeDataInfoList.stream()
				.filter(emp -> employeeHistMap.get(emp.getEmployeeId()) != null)
				.filter(emp -> personMap.get(emp.getPersonId()) != null)
				.map(emp -> {
					DatePeriod period = employeeHistMap.get(emp.getEmployeeId());
					Person person = personMap.get(emp.getPersonId());
					return new PersonEmpBasicInfoDto(emp.getPersonId(), emp.getEmployeeId(),
							person.getPersonNameGroup().getBusinessName().v(), person.getGender().value,
							person.getBirthDate(), emp.getEmployeeCode().v(), period.start(), period.end());
				}).collect(Collectors.toList());
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {

		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	// Pub get all Sid
	@Override
	public List<String> getAllSidByCid (String cid) {
		return empDataRepo.getAllSidByCid(cid);
	}
}
