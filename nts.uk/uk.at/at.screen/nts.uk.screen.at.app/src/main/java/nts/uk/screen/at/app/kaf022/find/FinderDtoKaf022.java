package nts.uk.screen.at.app.kaf022.find;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.request.app.find.application.gobackdirectly.GoBackReflectDto;
import nts.uk.ctx.at.request.app.find.application.stamp.dto.AppStampSettingDto;
import nts.uk.ctx.at.request.app.find.application.workchange.AppWorkChangeSetDto;
import nts.uk.ctx.at.request.app.find.applicationreflect.AppReflectExeConditionDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.applicationsetting.DisplayReasonDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDispSettingDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemAppSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.substituteapplicationsetting.SubstituteHdWorkAppSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSettingDto;
import nts.uk.ctx.at.request.app.find.setting.company.emailset.AppEmailSetDto;
import nts.uk.ctx.at.request.app.find.setting.request.application.businesstrip.BusinessTripSetDto;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChangeSetRepository;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExeConditionRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.LateEarlyCancelAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.LateEarlyCancelAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.DisplayReasonRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDispSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.businesstrip.AppTripRequestSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.ApplicationStampSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.substituteapplicationsetting.SubstituteHdWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.emailset.AppEmailSet;
import nts.uk.ctx.at.request.dom.setting.company.emailset.AppEmailSetRepository;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.othdwork.hdworkapply.HdWorkAppReflectDto_Old;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.othdwork.otworkapply.OtWorkAppReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.vacationapplication.leaveapplication.HolidayApplicationReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.vacationapplication.subleaveapp.SubLeaveAppReflectDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.ClosureHistoryFinder;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.GoBackReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.lateearlycancellation.LateEarlyCancelReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.lateearlycancellation.LateEarlyCancelReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.OtHdWorkAppSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.hdworkapply.HdWorkAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply.OtWorkAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.leaveapplication.VacationApplicationReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.subleaveapp.SubLeaveAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.workchangeapp.ReflectWorkChangeApp;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuNameExport;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuNameQuery;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuPub;
import nts.uk.ctx.workflow.app.find.approvermanagement.setting.ApprovalSettingFinder;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class FinderDtoKaf022 {
	
	@Inject
	private ClosureHistoryFinder closureHistoryFinder;
	
    @Inject
    private AppWorkChangeSetRepository appWorkChangeSetRepo;
	
	@Inject
	private ApprovalSettingFinder approvalSettingFinder;

	@Inject
	private StandardMenuPub menuPub;

	@Inject
	private ApplicationSettingRepository appSettingRepo;

	@Inject
	private DisplayReasonRepository displayReasonRepo;

	@Inject
	private OvertimeAppSetRepository overtimeAppSetRepo;

	@Inject
    private AppReflectExeConditionRepository appReflectConditionRepo;

	@Inject
	private GoBackReflectRepository goBackReflectRepo;

	@Inject
    private LateEarlyCancelAppSetRepository lateEarlyCancelRepo;

	@Inject
    private ApplicationStampSettingRepository appStampSettingRepo;

    @Inject
    private ApprovalListDispSetRepository approvalListDispSetRepo;

    @Inject
	private AppTripRequestSetRepository appTripRequestSetRepo;

    @Inject
	private AppEmailSetRepository appEmailSetRepo;

	@Inject
	private HolidayApplicationSettingRepository holidayApplicationSettingRepo;

	@Inject
	private VacationApplicationReflectRepository holidayApplicationReflectRepo;

	@Inject
	private HolidayWorkAppSetRepository holidayWorkAppSetRepo;

	@Inject
	private TimeLeaveAppReflectRepository timeLeaveAppReflectRepo;

	@Inject
	private OptionalItemAppSetRepository optionalItemAppSetRepo;

	@Inject
	private SubstituteHdWorkAppSetRepository substituteHdWorkAppSetRepo;

//	@Inject
//	private AppReflectOtHdWorkRepository otHdWorkAppReflectRepo;

    @Inject
    private OtHdWorkAppSettingRepository appSetRepo;

    @Inject
    private OtWorkAppReflectRepository otWorkReflectRepo;

    @Inject
    private HdWorkAppReflectRepository hdWorkReflectRepo;

	@Inject
	private SubLeaveAppReflectRepository substituteLeaveAppReflectRepo;

	@Inject
	private SubstituteWorkAppReflectRepository substituteWorkAppReflectRepo;

	@Inject
	private LateEarlyCancelReflectRepository lateEarlyCancelReflectRepo;

	@Inject
	private StampAppReflectRepository stampAppReflectRepo;

	/**
	 * 起動初期の処理
	 */
	public DtoKaf022 findDtoKaf022() {
		String companyId = AppContexts.user().companyId();
		// 申請設定の取得
		ApplicationSettingDto applicationSetting = appSettingRepo.findByCompanyId(companyId).map(ApplicationSettingDto::fromDomain).orElse(null);
		List<DisplayReasonDto> displayReasons = displayReasonRepo.findByCompanyId(companyId).stream().map(DisplayReasonDto::fromDomain).collect(Collectors.toList());
		OvertimeAppSetDto overTimeAppSetting = overtimeAppSetRepo.findSettingByCompanyId(companyId).map(OvertimeAppSetDto::fromDomain).orElse(null);
        AppWorkChangeSetDto appWorkChangeSetDto = appWorkChangeSetRepo.findByCompanyId(companyId).map(AppWorkChangeSetDto::fromDomain).orElse(null);
        LateEarlyCancelAppSet lateEarlyCancelAppSet = lateEarlyCancelRepo.getByCId(companyId);
        AppStampSettingDto appStampSetting = appStampSettingRepo.findSettingByCompanyId(companyId).map(AppStampSettingDto::fromDomain).orElse(null);
        ApprovalListDispSettingDto approvalListDispSetting = approvalListDispSetRepo.findByCID(companyId).map(ApprovalListDispSettingDto::fromDomain).orElse(null);
		AppEmailSet appEmailSet = appEmailSetRepo.findByCID(companyId);
		HolidayApplicationSettingDto holidayApplicationSetting = holidayApplicationSettingRepo.findSettingByCompanyId(companyId).map(HolidayApplicationSettingDto::fromDomain).orElse(null);
		HolidayWorkAppSetDto holidayWorkAppSet = holidayWorkAppSetRepo.findSettingByCompany(companyId).map(HolidayWorkAppSetDto::fromDomain).orElse(null);
		SubstituteHdWorkAppSetDto substituteHdWorkAppSetDto = substituteHdWorkAppSetRepo.findSettingByCompany(companyId).map(SubstituteHdWorkAppSetDto::fromDomain).orElse(null);
		List<OptionalItemAppSetDto> optionalItemAppSetDtos = optionalItemAppSetRepo.findByCompany(companyId).stream().map(OptionalItemAppSetDto::fromDomain).collect(Collectors.toList());

		// 反映設定の取得
//		Optional<AppReflectOtHdWork> optionalAppReflectOtHdWork = otHdWorkAppReflectRepo.findByCompanyId(companyId);
        AppReflectExeConditionDto appReflectCondition = appReflectConditionRepo.findByCompanyId(companyId).map(AppReflectExeConditionDto::fromDomain).orElse(null);
        ReflectWorkChangeApp reflectWorkChangeApp = appWorkChangeSetRepo.findByCompanyIdReflect(companyId).orElse(null);
		BusinessTripSetDto tripRequestSet = appTripRequestSetRepo.findById(companyId).map(BusinessTripSetDto::fromDomain).orElse(null);
		GoBackReflectDto goBackReflect = goBackReflectRepo.findByCompany(companyId).map(GoBackReflectDto::fromDomain).orElse(null);
        LateEarlyCancelReflect lateEarlyCancelReflect = lateEarlyCancelReflectRepo.getByCompanyId(companyId);
		StampAppReflectDto stampAppReflectDto = stampAppReflectRepo.findReflectByCompanyId(companyId).map(StampAppReflectDto::fromDomain).orElse(null);
		HolidayApplicationReflectDto holidayApplicationReflect = holidayApplicationReflectRepo.findReflectByCompanyId(companyId).map(HolidayApplicationReflectDto::fromDomain).orElse(null);
		TimeLeaveAppReflectDto timeLeaveAppReflectDto = timeLeaveAppReflectRepo.findByCompany(companyId).map(TimeLeaveAppReflectDto::fromDomain).orElse(null);
		SubLeaveAppReflectDto substituteLeaveAppReflectDto = substituteLeaveAppReflectRepo.findSubLeaveAppReflectByCompany(companyId).map(SubLeaveAppReflectDto::fromDomain).orElse(null);
		SubstituteWorkAppReflectDto substituteWorkAppReflectDto = substituteWorkAppReflectRepo.findSubWorkAppReflectByCompany(companyId).map(SubstituteWorkAppReflectDto::fromDomain).orElse(null);

		// メニューの表示名を取得する
		List<StandardMenuNameQuery> queries = new ArrayList<>();
		queries.add(new StandardMenuNameQuery("KAF005", "A", Optional.of("overworkatr=0")));
		queries.add(new StandardMenuNameQuery("KAF005", "A", Optional.of("overworkatr=1")));
		queries.add(new StandardMenuNameQuery("KAF005", "A", Optional.of("overworkatr=2")));
		queries.add(new StandardMenuNameQuery("KAF005", "A", Optional.of("overworkatr=3")));
		queries.add(new StandardMenuNameQuery("KAF006", "A", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF007", "A", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF008", "A", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF009", "A", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF010", "A", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF012", "A", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF004", "A", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF002", "A", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF002", "B", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF011", "A", Optional.empty()));
		queries.add(new StandardMenuNameQuery("KAF020", "A", Optional.empty()));
		List<StandardMenuNameExport> menuList = menuPub.getMenuDisplayName(companyId, queries);

		DtoKaf022 result = new DtoKaf022();
		result.setAllClosure(closureHistoryFinder.findAll());

		// refactor 4
		// A
		result.setApplicationSetting(applicationSetting);
		result.setReasonDisplaySettings(displayReasons);
		result.setMenus(menuList);
		result.setAppReflectCondition(appReflectCondition);
		result.setNightOvertimeReflectAtr(appSetRepo.getNightOvertimeReflectAtr(companyId));
		result.setApprovalSettingDto(approvalSettingFinder.findApproSet());

		// B
        result.setOvertimeAppSetting(overTimeAppSetting);
        result.setOvertimeAppReflect(otWorkReflectRepo.findReflectByCompanyId(companyId).map(OtWorkAppReflectDto::fromDomain).orElse(null));

        // C
		result.setHolidayApplicationSetting(holidayApplicationSetting);
		result.setHolidayApplicationReflect(holidayApplicationReflect);

        // D
        result.setAppChange(appWorkChangeSetDto);
        result.setWorkTimeReflectAtr(reflectWorkChangeApp != null ? reflectWorkChangeApp.getWhetherReflectAttendance().value : 0);

        // E
		result.setTripRequestSetting(tripRequestSet);

        // F
		result.setGoBackReflect(goBackReflect);

		// G
		result.setHolidayWorkApplicationSetting(holidayWorkAppSet);
		result.setHolidayWorkApplicationReflect(hdWorkReflectRepo.findReflectByCompany(companyId).map(HdWorkAppReflectDto_Old::fromDomain).orElse(null));

		// H
		result.setTimeLeaveApplicationReflect(timeLeaveAppReflectDto);

		// I
        result.setLateEarlyCancelAtr(lateEarlyCancelAppSet != null ? lateEarlyCancelAppSet.getCancelAtr().value : 0);
        result.setLateEarlyClearAlarmAtr(lateEarlyCancelReflect != null ? BooleanUtils.toInteger(lateEarlyCancelReflect.isClearLateReportWarning()) : 0);

        // J
        result.setAppStampSetting(appStampSetting);
        result.setAppStampReflect(stampAppReflectDto);

        // K
		result.setSubstituteHdWorkApplicationSetting(substituteHdWorkAppSetDto);
		result.setSubstituteLeaveApplicationReflect(substituteLeaveAppReflectDto);
		result.setSubstituteWorkApplicationReflect(substituteWorkAppReflectDto);

        // N
		result.setOptionalItemApplicationSettings(optionalItemAppSetDtos);

        // Q
        result.setApprovalListDisplaySetting(approvalListDispSetting);

        // Y
		result.setAppMailSetting(appEmailSet == null ? null : new AppEmailSetDto(appEmailSet));

		return result;
	}
}
