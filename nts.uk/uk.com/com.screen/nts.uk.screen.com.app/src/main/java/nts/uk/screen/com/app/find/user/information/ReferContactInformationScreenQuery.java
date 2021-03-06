package nts.uk.screen.com.app.find.user.information;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.classification.Classification;
import nts.uk.ctx.bs.employee.dom.classification.ClassificationRepository;
import nts.uk.ctx.bs.employee.dom.employee.service.EmpBasicInfo;
import nts.uk.ctx.bs.employee.dom.employee.service.SearchEmployeeService;
import nts.uk.ctx.bs.employee.dom.employment.Employment;
import nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository;
import nts.uk.ctx.bs.employee.pub.generalinfo.EmployeeGeneralInfoDto;
import nts.uk.ctx.bs.employee.pub.generalinfo.EmployeeGeneralInfoPub;
import nts.uk.ctx.sys.env.dom.mailnoticeset.adapter.EmployeeInfoContactAdapter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.adapter.PersonContactAdapter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.ContactUsageSetting;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethodRepository;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.service.ContactDisplaySetting;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.service.ContactInformation;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.service.OtherContactDisplaySetting;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.service.UserInformationUseMethodService;
import nts.uk.ctx.sys.env.dom.mailnoticeset.dto.EmployeeInfoContactImport;
import nts.uk.ctx.sys.env.dom.mailnoticeset.dto.PersonContactImport;
import nts.uk.ctx.workflow.dom.adapter.bs.SyJobTitleAdapter;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.JobTitleImport;
import nts.uk.ctx.workflow.dom.adapter.workplace.WkpDepInfo;
import nts.uk.ctx.workflow.dom.adapter.workplace.WorkplaceApproverAdapter;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

/**
 * UKDesign.UniversalK.??????.CDL_?????????????????????.CDL010_??????????????????.???????????????OCD.??????????????????????????????
 * @author DungDV
 *
 */
@Stateless
public class ReferContactInformationScreenQuery {
	@Inject
	private SearchEmployeeService employeeService;

	@Inject
	private EmployeeGeneralInfoPub employeeGeneralInfoPub;

	@Inject
	private EmploymentRepository employmentRepository;

	@Inject
	private ClassificationRepository classificationRepository;
	
	@Inject
	private PersonContactAdapter personContactAdapter;
	
	@Inject
	private EmployeeInfoContactAdapter employeeInfoContactAdapter;
	
	@Inject
	private UserInformationUseMethodRepository userInformationUseMethodRepository;
	
	@Inject
	private WorkplaceApproverAdapter workplaceApproverAdapter;
	
	@Inject
	private SyJobTitleAdapter syJobTitleAdapter;

	/**
	 * 
	 * @param employeeId
	 * @param referenceDate
	 * @return ReferContactInformationDto
	 */
	public ReferContactInformationDto getContactInfomation(String employeeId, StartMode startMode) {
		String cid = AppContexts.user().companyId();
		String sid = employeeId.isEmpty() ? AppContexts.user().employeeId() : employeeId;
		GeneralDate baseDate = GeneralDate.today();
		String personalId = AppContexts.user().personId();
		String employmentCode = "";
		String classificationCode = "";
		String jobTitleCode = "";
		String wkpId = "";
		// step 1: <call> ??????ID???????????????????????????????????????
		EmpBasicInfo empBasicInfo = new EmpBasicInfo();

		List<EmpBasicInfo> listEmpInfo = employeeService.getEmpBasicInfo(Arrays.asList(sid));
		if (!listEmpInfo.isEmpty()) {
			empBasicInfo = listEmpInfo.get(0);
			personalId = empBasicInfo.getPId();
		}
		
		// 3: create() #116371
		ContactDisplaySetting settingContactInformation = ContactDisplaySetting.builder().build();

		// step 2 : [??????????????????0]:get(??????ID): Optional<?????????????????????????????????>
		if(startMode == StartMode.ATTENDANCE_INQUIRY) {
			userInformationUseMethodRepository.findByCId(cid).ifPresent(item -> {
				item.getSettingContactInformation().ifPresent(consumer -> {
					settingContactInformation.setDialInNumberDisplay(consumer.getDialInNumber().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setCompanyEmailAddressDisplay(consumer.getCompanyEmailAddress().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setCompanyMobileEmailAddressDisplay(consumer.getCompanyMobileEmailAddress().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setPersonalEmailAddressDisplay(consumer.getPersonalEmailAddress().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setPersonalMobileEmailAddressDisplay(consumer.getPersonalMobileEmailAddress().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setExtensionNumberDisplay(consumer.getExtensionNumber().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setCompanyMobilePhoneDisplay(consumer.getCompanyMobilePhone().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setPersonalMobilePhoneDisplay(consumer.getPersonalMobilePhone().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setEmergencyNumber1Display(consumer.getEmergencyNumber1().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setEmergencyNumber2Display(consumer.getEmergencyNumber2().getContactUsageSetting() == ContactUsageSetting.USE);
					settingContactInformation.setOtherContacts(consumer.getOtherContacts().stream().map(other -> {
						return OtherContactDisplaySetting.builder()
								.no(other.getNo())
								.contactName(other.getContactName().v())
								.contactUsageSettingDisplay(other.getContactUsageSetting() == ContactUsageSetting.USE)
								.build(); 
					}).collect(Collectors.toList()));
				});
			});;
		}
		
		// 4: ????????????????????????(Require, ??????ID, ??????ID, ??????ID)
		RequireImpl require = new RequireImpl(personContactAdapter, employeeInfoContactAdapter);
		ContactInformation contactInfo = UserInformationUseMethodService.get(require, AppContexts.user().companyId(), sid, personalId, settingContactInformation);
		ContactInformationDTO contactInformation = ContactInformationDTO.builder()
				.companyMobileEmailAddress(contactInfo.getCompanyMobileEmailAddress() != null ? contactInfo.getCompanyMobileEmailAddress().orElse("") : "")
				.personalMobileEmailAddress(contactInfo.getPersonalMobileEmailAddress() != null ? contactInfo.getPersonalMobileEmailAddress().orElse("") : "")
				.personalEmailAddress(contactInfo.getPersonalEmailAddress() != null ? contactInfo.getPersonalEmailAddress().orElse("") : "")
				.companyEmailAddress(contactInfo.getCompanyEmailAddress() != null ? contactInfo.getCompanyEmailAddress().orElse("") : "")
				.otherContactsInfomation(contactInfo.getOtherContactsInfomation())
				.seatDialIn(contactInfo.getSeatDialIn() != null ? contactInfo.getSeatDialIn().v() : "")
				.seatExtensionNumber(contactInfo.getSeatExtensionNumber() != null ? contactInfo.getSeatExtensionNumber().v() : "")
				.emergencyNumber1(contactInfo.getEmergencyNumber1() != null ? contactInfo.getEmergencyNumber1().orElse("") : "")
				.emergencyNumber2(contactInfo.getEmergencyNumber2() != null ? contactInfo.getEmergencyNumber2().orElse("") : "")
				.personalMobilePhoneNumber(contactInfo.getPersonalMobilePhoneNumber() != null ? contactInfo.getPersonalMobilePhoneNumber().orElse("") : "")
				.companyMobilePhoneNumber(contactInfo.getCompanyMobilePhoneNumber() != null ? contactInfo.getCompanyMobilePhoneNumber().orElse("") : "")
				.build();

		// step 5 : <call> ??????ID???List?????????????????????????????????????????????
		EmployeeGeneralInfoDto employeeGeneralInfoImport = employeeGeneralInfoPub.getPerEmpInfo(Arrays.asList(sid),
				DatePeriod.oneDay(baseDate), true, true, true, true, false);

		// step 6 : get*(??????????????????ID??????????????????) - get domain ??????
		if (!employeeGeneralInfoImport.getEmploymentDto().isEmpty()
				&& !employeeGeneralInfoImport.getEmploymentDto().get(0).getEmploymentItems().isEmpty()) {
			employmentCode = employeeGeneralInfoImport.getEmploymentDto().get(0).getEmploymentItems().get(0)
					.getEmploymentCode();
		}
		Optional<Employment> employment = this.employmentRepository.findEmployment(cid, employmentCode);

		// step 7 : get*(??????????????????ID??????????????????) - get domain ??????
		if (!employeeGeneralInfoImport.getClassificationDto().isEmpty()
				&& !employeeGeneralInfoImport.getClassificationDto().get(0).getClassificationItems().isEmpty()) {
			classificationCode = employeeGeneralInfoImport.getClassificationDto().get(0).getClassificationItems().get(0)
					.getClassificationCode();
		}
		Optional<Classification> classification = classificationRepository.findClassification(cid, classificationCode);

		// step 8 : get*(??????????????????ID?????????ID) - get domain ????????????
		if (!employeeGeneralInfoImport.getJobTitleDto().isEmpty()
				&& !employeeGeneralInfoImport.getJobTitleDto().get(0).getJobTitleItems().isEmpty()) {
			jobTitleCode = employeeGeneralInfoImport.getJobTitleDto().get(0).getJobTitleItems().get(0).getJobTitleId();
		}
		JobTitleImport jobTitleInfo = syJobTitleAdapter.findJobTitleByPositionId(cid, jobTitleCode, baseDate);

		// step 9 : get*(??????????????????ID?????????ID) - get domain ????????????
		if (!employeeGeneralInfoImport.getWorkplaceDto().isEmpty()
				&& !employeeGeneralInfoImport.getWorkplaceDto().get(0).getWorkplaceItems().isEmpty()) {
			wkpId = employeeGeneralInfoImport.getWorkplaceDto().get(0).getWorkplaceItems().get(0)
					.getWorkplaceId();
		}
		Optional<WkpDepInfo> workplaceInfo = this.workplaceApproverAdapter.findByWkpIdNEW(cid, wkpId, baseDate);
		return ReferContactInformationDto.builder()
				.businessName(empBasicInfo.getBusinessName().isEmpty() ? TextResource.localize("CDL010_19") : empBasicInfo.getBusinessName())
				.employmentName(employment.map(mapper -> mapper.getEmploymentName().v()).orElse(TextResource.localize("CDL010_19")))
				.workplaceName(workplaceInfo.map(mapper -> mapper.getName()).orElse(TextResource.localize("CDL010_19")))
				.jobTitleName(jobTitleInfo != null ? jobTitleInfo.getPositionName() : TextResource.localize("CDL010_19"))
				.classificationName(classification.map(mapper -> mapper.getClassificationName().v()).orElse(TextResource.localize("CDL010_19")))
				.contactInformation(contactInformation)
				.build();
	}
	
	@AllArgsConstructor
    private static class RequireImpl implements UserInformationUseMethodService.Require {
		
		@Inject
		private PersonContactAdapter personContactAdapter;
		
		@Inject
		private EmployeeInfoContactAdapter employeeInfoContactAdapter;

		@Override
		public Optional<PersonContactImport> getByPersonalId(String personalId) {
			return this.personContactAdapter.getPersonalContact(personalId);
		}

		@Override
		public Optional<EmployeeInfoContactImport> getByContactInformation(String employeeId) {
			return this.employeeInfoContactAdapter.get(employeeId);
		}
		
	}
	
	public enum StartMode {
	    // ????????????
	    ATTENDANCE_INQUIRY(0),

	    // ??????????????????
	    SCHEDULE(1);

	    public final int value;

	    /**
	     *
	     * @param code
	     */
	    private StartMode(int value) {
	        this.value = value;
	    }
	}
}
