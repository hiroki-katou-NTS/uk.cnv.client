package nts.uk.ctx.pereg.app.command.addemployee;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHist;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistRepository;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyInfo;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyInfoRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDeletionAttr;
import nts.uk.ctx.bs.person.dom.person.common.ConstantUtils;
import nts.uk.ctx.bs.person.dom.person.info.GenderPerson;
import nts.uk.ctx.bs.person.dom.person.info.Person;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.ItemValue;
import nts.uk.shr.pereg.app.ItemValueType;
import nts.uk.shr.pereg.app.command.ItemsByCategory;

@Stateless
public class AddEmployeeCommandHelper {

	@Inject
	private AffCompanyHistRepository companyHistRepo;

	@Inject
	private AffCompanyInfoRepository companyInfoRepo;

	@Inject
	private EmployeeDataMngInfoRepository empDataRepo;

	@Inject
	private PersonRepository personRepo;

	private static final String CS00003 ="CS00003";
	private static final String IS00021 = "IS00021";
	public void addBasicData(AddEmployeeCommand command, List<ItemsByCategory> inputs, String personId, String employeeId, String comHistId,
			String companyId) {
		// lấy data cho IS00021 - item ngày nghỉ việc
		Optional<ItemsByCategory> affComHist = command.getInputs().stream().filter(c -> c.getCategoryCd().equals(CS00003)).findFirst();
		
		if (!affComHist.isPresent()){
			affComHist = inputs.stream().filter(c -> c.getCategoryCd().equals(CS00003)).findFirst();
		}
		
		// add newPerson

		addNewPerson(personId, command.getEmployeeName());

		// addmngInfo

		addEmployeeDataMngInfo(personId, employeeId, command.getEmployeeCode(), companyId);

		// add AffCompanyHist
		GeneralDate entry = null;
		if(affComHist.isPresent()) {
			Optional<ItemValue>  entryDate = 
					affComHist
						.get()
						.getItems()
						.stream()
						.filter(c -> c.logType() == ItemValueType.DATE.value ? !"Invalid date".equals(c.valueAfter()) : true)
						.filter( c ->  c.itemCode().equals(IS00021) && (c.value() != null))
						.findFirst();
			if(entryDate.isPresent()) {
				entry = entryDate.get().value();
			}
		}
		addAffCompanyHist(personId, employeeId, command.getHireDate(), entry, companyId, comHistId);
		
		

	}

	private void addNewPerson(String personId, String employeeName) {
		Person newPerson = Person.createFromJavaType(ConstantUtils.minDate(), null, GenderPerson.Male.value, personId,
				" ", "", employeeName, " ", "", "", "", "", "", "", "", "", "", "");

		this.personRepo.addNewPerson(newPerson);

	}

	private void addEmployeeDataMngInfo(String personId, String employeeId, String employeeCode, String companyId) {
		// check duplicate employeeCode
		Optional<EmployeeDataMngInfo> empInfo = this.empDataRepo.findByEmployeCD(employeeCode,
				AppContexts.user().companyId());

		if (empInfo.isPresent()) {
			throw new BusinessException("Msg_345");
		}
		
		// add system data
		this.empDataRepo.add(EmployeeDataMngInfo.createFromJavaType(companyId, personId, employeeId, employeeCode,
				EmployeeDeletionAttr.NOTDELETED.value, null, "", ""));

	}

	private void addAffCompanyHist(String personId, String employeeId, GeneralDate hireDate, GeneralDate entryDate, String companyId,
			String comHistId) {
		AffCompanyHist newComHist = AffCompanyHist.createNewEmployeeHist(personId, employeeId, comHistId, hireDate, entryDate);
		this.companyHistRepo.add(newComHist);

		AffCompanyInfo newComInfo = AffCompanyInfo.createFromJavaType(employeeId, comHistId, " ", null, null);
		this.companyInfoRepo.add(newComInfo);

	}

}
