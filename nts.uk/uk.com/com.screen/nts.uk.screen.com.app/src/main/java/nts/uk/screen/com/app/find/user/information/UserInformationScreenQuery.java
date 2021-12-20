package nts.uk.screen.com.app.find.user.information;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.bs.employee.dom.employee.data.management.contact.EmployeeContact;
import nts.uk.ctx.bs.employee.dom.employee.data.management.contact.EmployeeContactRepository;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHist;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistByEmployee;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistItem;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.pub.jobtitle.SyJobTitlePub;
import nts.uk.ctx.bs.person.dom.person.info.Person;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.uk.ctx.bs.person.dom.person.personal.anniversary.AnniversaryNotice;
import nts.uk.ctx.bs.person.dom.person.personal.anniversary.AnniversaryRepository;
import nts.uk.ctx.bs.person.dom.person.personal.avatar.AvatarRepository;
import nts.uk.ctx.bs.person.dom.person.personal.avatar.UserAvatar;
import nts.uk.ctx.bs.person.dom.person.personal.contact.PersonalContact;
import nts.uk.ctx.bs.person.dom.person.personal.contact.PersonalContactRepository;
import nts.uk.ctx.sys.auth.dom.adapter.employee.employeeinfo.EmployeeInfoAdapter;
import nts.uk.ctx.sys.auth.pub.role.RoleExportRepo;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethod;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethodRepository;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUser;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUserRepository;
import nts.uk.ctx.sys.gateway.dom.loginold.ContractCode;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.password.PasswordPolicy;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.password.PasswordPolicyRepository;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;
import nts.uk.screen.com.app.find.user.information.anniversary.AnniversaryNoticeDto;
import nts.uk.screen.com.app.find.user.information.avatar.UserAvatarDto;
import nts.uk.screen.com.app.find.user.information.employee.contact.EmployeeContactDto;
import nts.uk.screen.com.app.find.user.information.employee.data.management.information.EmployeeDataMngInfoDto;
import nts.uk.screen.com.app.find.user.information.password.changelog.PasswordChangeLogDto;
import nts.uk.screen.com.app.find.user.information.password.policy.PasswordPolicyDto;
import nts.uk.screen.com.app.find.user.information.personal.contact.EmergencyContactDto;
import nts.uk.screen.com.app.find.user.information.personal.contact.PersonalContactDto;
import nts.uk.screen.com.app.find.user.information.personal.infomation.PersonDto;
import nts.uk.screen.com.app.find.user.information.setting.ContactSettingDto;
import nts.uk.screen.com.app.find.user.information.setting.SettingContactInformationDto;
import nts.uk.screen.com.app.find.user.information.setting.UserInformationUseMethodDto;
import nts.uk.screen.com.app.find.user.information.user.UserDto;
import nts.uk.shr.com.context.AppContexts;

/*
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM048_ユーザー情報の設定(Setting thông tin user).A：ユーザー情報の設定.メニュー別OCD.ユーザ情報を取得する.ユーザ情報を取得する.ユーザ情報を取得する
 */

@Stateless
public class UserInformationScreenQuery {

    @Inject
    private UserInformationUseMethodRepository userInfoUseMethod_repository;
    
    @Inject
	EmployeeInfoAdapter employeeInfoAdapter;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private EmployeeDataMngInfoRepository employeeDataMngInfoRepository;

    @Inject
    private PersonalContactRepository personalContactRepository;

    @Inject
    private AffCompanyHistRepository affCompanyHistRepository;

    @Inject
    private AnniversaryRepository anniversaryRepository;

    @Inject
    private LoginPasswordOfUserRepository loginPasswordOfUserRepo;

    @Inject
    private PasswordPolicyRepository passwordPolicyRepository;

    @Inject
    private EmployeeContactRepository employeeContactRepository;

    @Inject
    private AvatarRepository avatarRepository;
    
	@Inject
	private EmployeeRequestAdapter Rq30;
	
	@Inject
	private SyJobTitlePub Rq33;
	
	@Inject
	private RoleExportRepo Rq50;

    public UserInformationDto getUserInformation() {
        String loginCid = AppContexts.user().companyId();
        String loginUserId = AppContexts.user().userId();
        String loginPersonalId = AppContexts.user().personId();
        String loginEmployeeId = AppContexts.user().employeeId();
        String loginContractCode = AppContexts.user().contractCode();

        //SQ1 - get ユーザー情報の使用方法 from CMM049
        Optional<UserInformationUseMethod> userInfoUseMethod = userInfoUseMethod_repository.findByCId(loginCid);
        UserInformationUseMethodDto settingInformationDto = UserInformationUseMethodDto.builder()
                .settingContactInformationDto(SettingContactInformationDto.builder()
                        .dialInNumber(ContactSettingDto.builder().build())
                        .companyEmailAddress(ContactSettingDto.builder().build())
                        .companyMobileEmailAddress(ContactSettingDto.builder().build())
                        .personalEmailAddress(ContactSettingDto.builder().build())
                        .personalMobileEmailAddress(ContactSettingDto.builder().build())
                        .extensionNumber(ContactSettingDto.builder().build())
                        .companyMobilePhone(ContactSettingDto.builder().build())
                        .personalMobilePhone(ContactSettingDto.builder().build())
                        .emergencyNumber1(ContactSettingDto.builder().build())
                        .emergencyNumber2(ContactSettingDto.builder().build())
                        .otherContacts(new ArrayList<>())
                        .build())
                .build();
        userInfoUseMethod.ifPresent(method -> method.setMemento(settingInformationDto));

        //SQ2 - call
        String positionName = this.Rq33.findBySid(loginEmployeeId, GeneralDate.today()).map(item -> item.getJobTitleName()).orElse("");
        String wkpDisplayName = this.Rq30.getSWkpHistByEmployeeID(loginEmployeeId, GeneralDate.today()).getWkpDisplayName();
        Boolean isInCharge = this.Rq50.getWhetherLoginerCharge(AppContexts.user().roles()).isEmployeeCharge();

        //SQ3 - Handle ユーザ情報の使用方法.Empty
        if (
        		!userInfoUseMethod.isPresent()
        		|| (userInfoUseMethod.get().getUseOfProfile().isNotUse()
	        		&& userInfoUseMethod.get().getUseOfNotice().isNotUse()
	        		&& userInfoUseMethod.get().getUseOfPassword().isNotUse()
	        		&& userInfoUseMethod.get().getUseOfLanguage().isNotUse())
        ) {
            if (isInCharge) {
                throw new BusinessException("Msg_1775");
            } else {
                throw new BusinessException("Msg_1774");
            }
        }

        //SQ4 - get ユーザ
        Optional<User> loginUser = userRepository.getByUserID(loginUserId);
        UserDto userDto = loginUser.map(UserDto::toDto).orElse(new UserDto());

        //SQ5 - get 個人
        Optional<Person> personalInfo = personRepository.getByPersonId(loginPersonalId);
        PersonDto personDto = personalInfo.map(PersonDto::toDto).orElse(new PersonDto());

        //SQ6 - get 社員データ管理情報
        Optional<EmployeeDataMngInfo> employeeInfo = employeeDataMngInfoRepository.findByEmpId(loginEmployeeId);
        EmployeeDataMngInfoDto employeeDataMngInfoDto = employeeInfo
                .map(EmployeeDataMngInfoDto::toDto)
                .orElse(new EmployeeDataMngInfoDto());

        //SQ7 - get 個人連絡先
        Optional<PersonalContact> personalContact = personalContactRepository.getByPersonalId(loginPersonalId);
        PersonalContactDto personalContactDto = PersonalContactDto.builder()
                .emergencyContact1(EmergencyContactDto.builder().build())
                .emergencyContact2(EmergencyContactDto.builder().build())
                .otherContacts(new ArrayList<>())
                .build();
        personalContact.ifPresent(contact -> contact.setMemento(personalContactDto));

        //SQ8 - call 所属会社履歴
        AffCompanyHist affCompanyHist = affCompanyHistRepository.getAffCompanyHistoryOfEmployee(loginEmployeeId);
        List<AffCompanyHistByEmployee> affCompanyHistByEmployees = affCompanyHist.getLstAffCompanyHistByEmployee();
        List<AffCompanyHistItem> affCompanyHistItems = affCompanyHistByEmployees.stream()
                .map(AffCompanyHistByEmployee::getLstAffCompanyHistoryItem)
                .flatMap(List::stream)
                .filter(item -> this.checkHireDate(item.getDatePeriod()))
                .collect(Collectors.toList());
        GeneralDate hireDate = affCompanyHistItems.get(affCompanyHistItems.size() - 1).start();

        //SQ9 - get 個人の記念日情報
        List<AnniversaryNotice> anniversaryNoticeList = anniversaryRepository.getByPersonalId(loginPersonalId);
        List<AnniversaryNoticeDto> anniversaryNoticeDtos = new ArrayList<>();
        for (AnniversaryNotice anniversaryNotice : anniversaryNoticeList) {
            AnniversaryNoticeDto anniversaryNoticeDto = AnniversaryNoticeDto.builder().build();
            anniversaryNotice.setMemento(anniversaryNoticeDto);
            anniversaryNoticeDtos.add(anniversaryNoticeDto);
        }

        //SQ10 - get パスワード変更ログ
        Optional<LoginPasswordOfUser> optLoginPasswordOfUser = loginPasswordOfUserRepo.find(loginUserId);
        PasswordChangeLogDto passwordChangeLogDto = optLoginPasswordOfUser.isPresent() ? PasswordChangeLogDto.toDto(optLoginPasswordOfUser.get()) : null;

        //SQ11 - get パスワードポリシー
        PasswordPolicy passwordPolicy = passwordPolicyRepository
                .getPasswordPolicy(new ContractCode(loginContractCode));
        PasswordPolicyDto passwordPolicyDto = PasswordPolicyDto.toDto(passwordPolicy);

        //SQ12 - get 社員連絡先
        Optional<EmployeeContact> employeeContact = employeeContactRepository.getByEmployeeId(loginEmployeeId);
        EmployeeContactDto employeeContactDto = EmployeeContactDto.builder().build();
        employeeContact.ifPresent(contact -> contact.setMemento(employeeContactDto));

        //SQ13 - get 個人の顔写真
        Optional<UserAvatar> userAvatar = avatarRepository.getAvatarByPersonalId(loginPersonalId);
        UserAvatarDto userAvatarDto = UserAvatarDto.builder().build();
        userAvatar.ifPresent(avatar -> avatar.setMemento(userAvatarDto));

        return UserInformationDto.builder()
                .passwordPolicy(passwordPolicyDto)
                .isInCharge(isInCharge)
                .settingInformation(settingInformationDto)
                .hireDate(hireDate)
                .user(userDto)
                .person(personDto)
                .personalContact(personalContactDto)
                .employeeDataMngInfo(employeeDataMngInfoDto)
                .employeeContact(employeeContactDto)
                .passwordChangeLog(passwordChangeLogDto)
                .anniversaryNotices(anniversaryNoticeDtos)
                .userAvatar(userAvatarDto)
                .positionName(positionName)
                .wkpDisplayName(wkpDisplayName)
                .build();
    }

    private boolean checkHireDate(DatePeriod datePeriod) {
        return (
	    	datePeriod.start().compareTo(GeneralDate.today()) <= 0 
	    	&& datePeriod.end().compareTo(GeneralDate.today()) >= 0
       );
    }
}
