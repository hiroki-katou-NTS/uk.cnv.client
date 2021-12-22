package nts.uk.ctx.at.request.ws.application.holidayshipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5.ErrorCheckProcessingBeforeRegistrationKAF011;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5.HdShipmentMobileCmd;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5.PreRegistrationErrorCheck;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5.PreUpdateErrorCheck;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5.SaveHolidayShipmentCommandHandlerRef5;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5.UpdateHolidayShipmentCommandHandlerRef5;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5.command.HdShipmentMBTimeZoneParam;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.HolidayShipmentScreenAFinder;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto.DisplayInforWhenStarting;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto.HdShipmentMBTimeZoneDto;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.appabsence.service.AbsenceServiceProcess;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.VacationCheckOutput;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.shared.app.find.common.TimeZoneWithWorkNoDto;
import nts.uk.ctx.at.shared.app.find.worktime.predset.dto.TimeZone_NewDto;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.predset.UseSetting;
import nts.uk.shr.com.context.AppContexts;

@Path("at/request/application/holidayshipment/mobile")
@Produces("application/json")
public class HolidayShipmentMobileWS extends WebService {
	
	@Inject
	private HolidayShipmentScreenAFinder screenAFinder;
	
	@Inject
	private PreRegistrationErrorCheck preRegistrationErrorCheck;
	
	@Inject
	private ErrorCheckProcessingBeforeRegistrationKAF011 errorCheckProcessingBeforeRegistrationKAF011;
	
	@Inject
	private NewBeforeRegister newBeforeRegister;
	
	@Inject
	private SaveHolidayShipmentCommandHandlerRef5 saveHolidayShipmentCommandHandlerRef5;
	
	@Inject
	private PreUpdateErrorCheck preUpdateErrorCheck;
	
	@Inject
	private UpdateHolidayShipmentCommandHandlerRef5 updateHolidayShipmentCommandHandlerRef5;
	
	@Inject
	private AbsenceServiceProcess absenceServiceProcess;
	
	@POST
	@Path("start")
	public DisplayInforWhenStarting startPageARefactor(HdShipmentMobileStartParam param) {
		// INPUT．「画面モード」を確認する
		if(!param.isNewMode()) {
			// INPUT「振休振出申請起動時の表示情報」を返す
			return param.getDisplayInforWhenStarting();
		}
		// 起動時の申請表示情報を取得する
		// trên UI
		// 1.振休振出申請（新規）起動処理
		String companyID = AppContexts.user().companyId();
		return screenAFinder.startPageARefactor(
				companyID, 
				Arrays.asList(param.getEmployeeID()), 
				param.getDateLst().stream().map(x -> GeneralDate.fromString(x, "yyyy/MM/dd")).collect(Collectors.toList()),
				param.getAppDispInfoStartupCmd()
			);
	}
	
	@POST
	@Path("checkBeforeSubmit")
	public List<ConfirmMsgOutput> checkBeforeSubmit(HdShipmentMobileCmd command) {
		// INPUT．「画面モード」をチェックする
		if(command.isNewMode()) {
			// 登録前のエラーチェック処理
			return this.checkBeforeRegister(command);
		} else {
			// 振休振出申請（詳細）登録前のチェック
			return this.checkBeforeUpdate(command);
		}
	}
	
	private List<ConfirmMsgOutput> checkBeforeRegister(HdShipmentMobileCmd command) {
		List<ConfirmMsgOutput> result = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		Optional<AbsenceLeaveApp> abs = command.abs == null ? Optional.empty() : Optional.of(command.abs.toDomainInsertAbs());
		Optional<RecruitmentApp> rec = command.rec == null ? Optional.empty() : Optional.of(command.rec.toDomainInsertRec());
		DisplayInforWhenStarting displayInforWhenStarting = command.getDisplayInforWhenStarting();
		AppDispInfoStartupOutput appDispInfoStartup = displayInforWhenStarting.appDispInfoStartup.toDomain();
		//登録前エラーチェック（新規）(Check error trước khi đăng ký (New)
		preRegistrationErrorCheck.errorCheck(
				companyId, 
				abs, 
				rec, 
				appDispInfoStartup.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(new ArrayList<ActualContentDisplay>()),
				appDispInfoStartup.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0), 
				appDispInfoStartup.getAppDispInfoWithDateOutput().getEmpHistImport().getEmploymentCode(), 
				Optional.ofNullable(displayInforWhenStarting.getApplicationForHoliday() == null ? null : displayInforWhenStarting.getApplicationForHoliday().getWorkInformationForApplication()), 
				Optional.ofNullable(displayInforWhenStarting.getApplicationForWorkingDay() == null ? null : displayInforWhenStarting.getApplicationForWorkingDay().getWorkInformationForApplication()), 
				command.getAbsWorkMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()), 
				command.getAbsHolidayMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()), 
				command.isCheckFlag(), 
				command.getDisplayInforWhenStarting().getApplicationForHoliday().getWorkTypeList().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
		//振休残数不足チェック (Check số nghỉ bù thiếu)
//		errorCheckProcessingBeforeRegistrationKAF011.checkForInsufficientNumberOfHolidays(
//				companyId, 
//				appDispInfoStartup.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(), 
//				abs, 
//				rec);
		boolean existFlag = false;
		if (abs.isPresent() && rec.isPresent()) {
		    existFlag = true;
		}
		
		if(rec.isPresent()) {
			List<ConfirmMsgOutput> comfirmLst1 = newBeforeRegister.processBeforeRegister_New(
					companyId, 
					EmploymentRootAtr.APPLICATION, 
					displayInforWhenStarting.isRepresent(), 
					rec.get(), 
					null, 
					appDispInfoStartup.getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()), 
					new ArrayList<>(), 
					appDispInfoStartup, 
					Arrays.asList(rec.get().getWorkInformation().getWorkTypeCode().v()), 
					Optional.empty(), 
					rec.get().getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v), 
					existFlag);
			result.addAll(comfirmLst1);
		}
		
		if(abs.isPresent()) {
			List<ConfirmMsgOutput> comfirmLst2 = newBeforeRegister.processBeforeRegister_New(
					companyId, 
					EmploymentRootAtr.APPLICATION, 
					displayInforWhenStarting.isRepresent(), 
					abs.get(), 
					null, 
					appDispInfoStartup.getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()), 
					new ArrayList<>(), 
					appDispInfoStartup, 
                    Arrays.asList(abs.get().getWorkInformation().getWorkTypeCode().v()), 
                    Optional.empty(), 
                    abs.get().getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v), 
                    existFlag);
			result.addAll(comfirmLst2);
		}
		return result;
	}
	
	private List<ConfirmMsgOutput> checkBeforeUpdate(HdShipmentMobileCmd command) {
		String companyID = AppContexts.user().companyId();
		Optional<AbsenceLeaveApp> abs = Optional.empty(); 
		if(command.abs!=null) {
			abs = Optional.of(command.abs.toDomainUpdateAbs(command.abs.application));
		}
		Optional<RecruitmentApp> rec = Optional.empty();
		if(command.rec!=null) {
			rec = Optional.of(command.rec.toDomainUpdateRec(command.rec.application));
		}
		DisplayInforWhenStarting displayInforWhenStarting = command.getDisplayInforWhenStarting();
		preUpdateErrorCheck.errorCheck(
		        companyID, 
		        abs, 
		        rec, 
		        displayInforWhenStarting, 
		        abs.isPresent() ? command.getAbsWorkMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()) : new ArrayList<>(), 
		        abs.isPresent() ? command.getAbsHolidayMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()) : new ArrayList<>(), 
		        command.isCheckFlag(), 
		        command.getDisplayInforWhenStarting().getApplicationForHoliday().getWorkTypeList().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
		return Collections.emptyList();
	}
	
	@POST
	@Path("submit")
	public ProcessResult submit(HdShipmentMobileCmd command) {
		ProcessResult processResult = new ProcessResult();
		String companyID = AppContexts.user().companyId();
		DisplayInforWhenStarting displayInforWhenStarting = command.getDisplayInforWhenStarting();
		AppDispInfoStartupOutput appDispInfoStartup = displayInforWhenStarting.appDispInfoStartup.toDomain();
		// INPUT．「画面モード」をチェックする
		if(command.isNewMode()) {
			// 振休振出申請（新規）登録処理
			Optional<AbsenceLeaveApp> abs = command.abs == null ? Optional.empty() : Optional.of(command.abs.toDomainInsertAbs());
			Optional<RecruitmentApp> rec = command.rec == null ? Optional.empty() : Optional.of(command.rec.toDomainInsertRec());
			saveHolidayShipmentCommandHandlerRef5.registrationApplicationProcess(
				companyID,
				abs,
				rec,
				appDispInfoStartup.getAppDispInfoWithDateOutput().getBaseDate(),
				appDispInfoStartup.getAppDispInfoNoDateOutput().isMailServerSet(),
				appDispInfoStartup.getAppDispInfoWithDateOutput().getOpListApprovalPhaseState().get(),
				CollectionUtil.isEmpty(command.getRecHolidayMngLst()) ? new ArrayList<>() : command.getRecHolidayMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()),
				CollectionUtil.isEmpty(command.getAbsHolidayMngLst()) ? new ArrayList<>() : command.getAbsHolidayMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()),
				CollectionUtil.isEmpty(command.getAbsWorkMngLst()) ? new ArrayList<>() : command.getAbsWorkMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()),
				EnumAdaptor.valueOf(displayInforWhenStarting.holidayManage, ManageDistinct.class),
				appDispInfoStartup.getAppDispInfoNoDateOutput().getApplicationSetting());
			if(abs.isPresent()) {
				processResult.setAppIDLst(Arrays.asList(abs.get().getAppID()));
			}
			if(rec.isPresent()) {
				processResult.setAppIDLst(Arrays.asList(rec.get().getAppID()));
			}
		} else {
			// 振休振出申請の更新登録
			Optional<AbsenceLeaveApp> abs = Optional.empty(); 
			if(command.abs!=null) {
				abs = Optional.of(command.abs.toDomainUpdateAbs(command.abs.application));
			}
			Optional<RecruitmentApp> rec = Optional.empty();
			if(command.rec!=null) {
				rec = Optional.of(command.rec.toDomainUpdateRec(command.rec.application));
			}
			updateHolidayShipmentCommandHandlerRef5.updateApplicationProcess(
					companyID,
					rec, 
					abs, 
					CollectionUtil.isEmpty(command.getRecHolidayMngLst()) ? new ArrayList<>() : command.getRecHolidayMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()), 
					CollectionUtil.isEmpty(command.getAbsHolidayMngLst()) ? new ArrayList<>() : command.getAbsHolidayMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()), 
					CollectionUtil.isEmpty(command.getAbsWorkMngLst()) ? new ArrayList<>() : command.getAbsWorkMngLst().stream().map(x -> x.toDomain()).collect(Collectors.toList()), 
					appDispInfoStartup);
			if(abs.isPresent()) {
				processResult.setAppIDLst(Arrays.asList(abs.get().getAppID()));
			}
			if(rec.isPresent()) {
				processResult.setAppIDLst(Arrays.asList(rec.get().getAppID()));
			}
		}
		return processResult;
	}
	
	@POST
	@Path("getTimeZoneValue")
	public HdShipmentMBTimeZoneDto getTimeZoneValue(HdShipmentMBTimeZoneParam command) {
		String companyID = AppContexts.user().companyId();
		List<TimeZoneWithWorkNoDto> timeZoneLst = new ArrayList<>();
		if(Strings.isNotBlank(command.getWorkTimeCD())) {
			// 勤務時間初期値の取得
		    List<TimeZone> timeZones = absenceServiceProcess.initWorktimeCode(
					companyID, 
					command.getWorkTypeNew().getWorkTypeCode(), 
					command.getWorkTimeCD());
		    List<TimezoneUse> timezoneUses = new ArrayList<TimezoneUse>();
		    for (int i = 0; i < timeZones.size(); i++) {
                timezoneUses.add(new TimezoneUse(timeZones.get(i).getStart(), timeZones.get(i).getEnd(), UseSetting.USE, i + 1));
            }
//			if(predetermineTimeSetForCalc!=null) {
				for(TimezoneUse timezoneUse : timezoneUses) {
					if(timezoneUse.isUsed()) {
						timeZoneLst.add(new TimeZoneWithWorkNoDto(timezoneUse.getWorkNo(), new TimeZone_NewDto(timezoneUse.getStart().v(), timezoneUse.getEnd().v())));
					}
				}
//			}
		}
		VacationCheckOutput vacationCheckOutput = null;
		if(command.isChangeWorkType()) {
			// 休暇紐付管理をチェックする
			vacationCheckOutput = absenceServiceProcess.checkVacationTyingManage(
					command.getWorkTypeOld() != null ? command.getWorkTypeOld().toDomain(): null, 
					command.getWorkTypeNew().toDomain(), 
					command.getLeaveComDayOffMana().stream().map(x -> x.toDomain()).collect(Collectors.toList()), 
					command.getPayoutSubofHDManagements().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
		}
		return new HdShipmentMBTimeZoneDto(timeZoneLst, vacationCheckOutput);
	}
}
