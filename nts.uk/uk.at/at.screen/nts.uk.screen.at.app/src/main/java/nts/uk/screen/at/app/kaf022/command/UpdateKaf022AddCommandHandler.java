package nts.uk.screen.at.app.kaf022.command;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.workchange.SaveAppWorkChangeSetCommandHandler;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExeConditionRepository;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.CancelAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.LateEarlyCancelAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationlatearrival.LateEarlyCancelAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.DisplayReason;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.DisplayReasonRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDispSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.businesstrip.AppTripRequestSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.stampsetting.ApplicationStampSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.substituteapplicationsetting.SubstituteHdWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandard;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandardRepository;
import nts.uk.ctx.at.request.dom.setting.company.emailset.AppEmailSetRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.GoBackReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.GoBackReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.lateearlycancellation.LateEarlyCancelReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectRepository;
import nts.uk.ctx.workflow.app.command.approvermanagement.setting.ApprovalSettingCommand_New;
import nts.uk.ctx.workflow.app.command.approvermanagement.setting.UpdateApprovalSettingCommandHandler;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class UpdateKaf022AddCommandHandler extends CommandHandler<Kaf022AddCommand>{
	
	@Inject
	private UpdateApprovalSettingCommandHandler updateAppro;

	@Inject
	private ApplicationSettingRepository applicationSettingRepo;

	@Inject
	private DisplayReasonRepository displayReasonRepo;

	@Inject
	private AppReflectExeConditionRepository appReflectConditionRepo;

	@Inject
    private OvertimeAppSetRepository overtimeAppSetRepo;

	@Inject
	private SaveAppWorkChangeSetCommandHandler saveAppWorkChangeSetCommandHandler;

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
	private HolidayWorkAppSetRepository holidayWorkAppSetRepo;

	@Inject
	private TimeLeaveAppReflectRepository timeLeaveAppReflectRepo;

	@Inject
	private SubstituteHdWorkAppSetRepository substituteHdWorkAppSetRepo;

	@Inject
	private AppReasonStandardRepository appReasonStandardRepo;

	@Override
	protected void handle(CommandHandlerContext<Kaf022AddCommand> context) {
		String companyId = AppContexts.user().companyId();
		Kaf022AddCommand kaf022 = context.getCommand();
		ApplicationSetting applicationSetting = kaf022.getApplicationSetting().toDomain(companyId);
		List<DisplayReason> reasonDisplaySettings = kaf022.getReasonDisplaySettings().stream().map(r -> r.toDomain(companyId)).collect(Collectors.toList());

		// 登録の前チェック処理
		checkBeforeRegister(companyId, reasonDisplaySettings);

		// 登録処理
		applicationSettingRepo.save(applicationSetting, reasonDisplaySettings, kaf022.getNightOvertimeReflectAtr());
		displayReasonRepo.saveHolidayAppReason(companyId, reasonDisplaySettings);

		updateAppro.handle(new ApprovalSettingCommand_New(kaf022.getApprovalByPersonAtr()));
		appReflectConditionRepo.save(kaf022.getAppReflectCondition().toDomain(companyId));

		overtimeAppSetRepo.saveOvertimeAppSet(kaf022.getOvertimeApplicationSetting().toDomain(companyId), kaf022.getOvertimeApplicationReflect().toDomain());
		holidayApplicationSettingRepo.save(companyId, kaf022.getHolidayApplicationSetting().toDomain(companyId), kaf022.getHolidayApplicationReflect().toDomain(companyId));

		saveAppWorkChangeSetCommandHandler.handle(kaf022.getAppWorkChangeSetting());

		if (appTripRequestSetRepo.findById(companyId).isPresent()) {
			appTripRequestSetRepo.update(kaf022.getTripRequestSetting().toDomain(companyId));
		} else {
			appTripRequestSetRepo.add(kaf022.getTripRequestSetting().toDomain(companyId));
		}

		if (goBackReflectRepo.findByCompany(companyId).isPresent()) {
			goBackReflectRepo.update(GoBackReflect.create(companyId, kaf022.getGoBackReflectAtr()));
		} else {
			goBackReflectRepo.add(GoBackReflect.create(companyId, kaf022.getGoBackReflectAtr()));
		}

		lateEarlyCancelRepo.save(
				companyId,
				new LateEarlyCancelAppSet(companyId, EnumAdaptor.valueOf(kaf022.getLateEarlyCancelAtr(), CancelAtr.class)),
				new LateEarlyCancelReflect(companyId, BooleanUtils.toBoolean(kaf022.getLateEarlyClearAlarmAtr()))
		);

		appStampSettingRepo.save(companyId, kaf022.getAppStampSetting().toDomain(companyId), kaf022.getAppStampReflect().toDomain(companyId));

		approvalListDispSetRepo.save(kaf022.getApprovalListDisplaySetting().toDomain(companyId));

		appEmailSetRepo.save(kaf022.getAppMailSetting().toDomain(companyId));

		holidayWorkAppSetRepo.save(companyId, kaf022.getHolidayWorkApplicationSetting().toDomain(companyId), kaf022.getHolidayWorkApplicationReflect().toDomain());

		timeLeaveAppReflectRepo.save(kaf022.getTimeLeaveApplicationReflect().toDomain(companyId));

		substituteHdWorkAppSetRepo.save(kaf022.getSubstituteHdWorkAppSetting().toDomain(companyId), kaf022.getSubstituteLeaveAppReflect().toDomain(), kaf022.getSubstituteWorkAppReflect().toDomain());
	}

	/**
	 * 定型理由必須のチェック
	 * @param companyId
	 * @param reasonDisplaySettings
	 */
	private void checkBeforeRegister(String companyId, List<DisplayReason> reasonDisplaySettings) {
		List<DisplayReason> checkTargets = reasonDisplaySettings.stream().filter(i -> i.getDisplayFixedReason() == DisplayAtr.DISPLAY).collect(Collectors.toList());
		if (checkTargets.size() > 0) {
			for (DisplayReason target : checkTargets) {
				if (target.getAppType() == ApplicationType.ABSENCE_APPLICATION) {
					Optional<AppReasonStandard> optionalAppReasonStandard =  appReasonStandardRepo.findByHolidayAppType(companyId, target.getOpHolidayAppType().get());
					if (!optionalAppReasonStandard.isPresent()
							|| CollectionUtil.isEmpty(optionalAppReasonStandard.get().getReasonTypeItemLst())) {
						BusinessException exception = new BusinessException("Msg_1751", target.getOpHolidayAppType().get().name);
						exception.setSuppliment("appType", target.getAppType().value);
						exception.setSuppliment("holidayAppType", target.getHolidayAppType());
						throw exception;
					}
				} else {
					Optional<AppReasonStandard> optionalAppReasonStandard =  appReasonStandardRepo.findByAppType(companyId, target.getAppType());
					if (!optionalAppReasonStandard.isPresent()
							|| CollectionUtil.isEmpty(optionalAppReasonStandard.get().getReasonTypeItemLst())) {
						BusinessException exception = new BusinessException("Msg_1751", target.getAppType().name);
						exception.setSuppliment("appType", target.getAppType().value);
						throw exception;
					}
				}
			}
		}
	}

}
