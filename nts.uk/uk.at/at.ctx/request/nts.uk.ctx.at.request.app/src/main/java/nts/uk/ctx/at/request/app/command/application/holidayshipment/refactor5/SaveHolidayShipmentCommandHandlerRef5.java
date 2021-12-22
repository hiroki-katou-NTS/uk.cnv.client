package nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto.DisplayInforWhenStarting;
import nts.uk.ctx.at.request.dom.application.ApplicationApprovalService;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.appabsence.service.AbsenceServiceProcess;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.RegisterAtApproveReflectionInfoService;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.after.NewAfterRegister;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRec;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRecRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.SyncState;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.TargetSelectionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author thanhpv
 *	UKDesign.UniversalK.就業.KAF_申請.KAF011_振休振出申請.A：振休振出申請（新規）.ユースケース.登録する(Đăng kí).登録する
 */
@Stateless
public class SaveHolidayShipmentCommandHandlerRef5 {

	@Inject
	private ErrorCheckProcessingBeforeRegistrationKAF011 errorCheckProcessingBeforeRegistrationKAF011;
	
	@Inject
	private RecruitmentAppRepository recruitmentAppRepository;
	
	@Inject
	private AbsenceLeaveAppRepository absenceLeaveAppRepository;
	
	@Inject
	private RegisterAtApproveReflectionInfoService registerAtApproveReflectionInfoService;
	
	@Inject
	private AbsenceServiceProcess absenceServiceProcess;
	
	@Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	
	@Inject
	private AppHdsubRecRepository compltLeaveSimMngRepository;
	
	@Inject
	private WorkTypeRepository workTypeRepo;
	
	@Inject
	private PayoutSubofHDManaRepository payoutSubofHDManaRepository;
	
	@Inject
	private NewAfterRegister newAfterRegister;
	
	@Inject
	private ApplicationApprovalService appRepository;
	
	/**
	 * @name 登録する
	 */
	public ProcessResult register(DisplayInforWhenStarting command){
		String companyId = AppContexts.user().companyId();
		Optional<AbsenceLeaveApp> abs = Optional.ofNullable(command.existAbs() ? command.abs.toDomainInsertAbs() : null);
		Optional<RecruitmentApp> rec = Optional.ofNullable(command.existRec() ? command.rec.toDomainInsertRec(): null);

		//申請表示情報 = 振休振出申請起動時の表示情報．申請表示情報
		AppDispInfoStartupOutput appDispInfoStartup = command.appDispInfoStartup.toDomain();
		
		//登録前のエラーチェック処理(Xử lý error check trước khi đăng ký)
		boolean existFlag = false;
        if (abs.isPresent() && rec.isPresent()) {
            existFlag = true;
        }
		this.errorCheckProcessingBeforeRegistrationKAF011.processing(
				companyId, 
				abs,
				rec, 
				command.represent, 
				appDispInfoStartup.getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()), 
				appDispInfoStartup.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(new ArrayList<ActualContentDisplay>()), 
				appDispInfoStartup, 
				command.existAbs() ? command.abs.payoutSubofHDManagements.stream().map(c->c.toDomain()).collect(Collectors.toList()) : new ArrayList<>(), 
				command.existAbs() ? command.abs.leaveComDayOffMana.stream().map(c->c.toDomain()).collect(Collectors.toList()) : new ArrayList<>(), 
				command.isCheckFlag(), 
				existFlag, 
				command.getApplicationForHoliday().getWorkTypeList().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
		
		//振休振出申請（新規）登録処理 (Xử lý đăng ký application nghỉ bù làm bù (New))
		//QA: http://192.168.50.4:3000/issues/113451 -> done
		//QA: http://192.168.50.4:3000/issues/113650
		return this.registrationApplicationProcess(
				companyId, 
				abs, 
				rec, 
				appDispInfoStartup.getAppDispInfoWithDateOutput().getBaseDate(),
				appDispInfoStartup.getAppDispInfoNoDateOutput().isMailServerSet(), 
				appDispInfoStartup.getAppDispInfoWithDateOutput().getOpListApprovalPhaseState().get(),
				command.existRec() ? command.rec.leaveComDayOffMana.stream().map(c->c.toDomain()).collect(Collectors.toList()) : new ArrayList<>(), 
				command.existAbs() ? command.abs.leaveComDayOffMana.stream().map(c->c.toDomain()).collect(Collectors.toList()) : new ArrayList<>(), 
				command.existAbs() ? command.abs.payoutSubofHDManagements.stream().map(c->c.toDomain()).collect(Collectors.toList()) : new ArrayList<>(), 
				EnumAdaptor.valueOf(command.holidayManage, ManageDistinct.class), 
				appDispInfoStartup.getAppDispInfoNoDateOutput().getApplicationSetting());
	}
	
	/**
	 * @name 振休振出申請（新規）登録処理 (Xử lý đăng ký application nghỉ bù làm bù (New))
	 * @param companyId 会社ID
	 * @param abs 振休申請
	 * @param rec 振出申請
	 * @param baseDate 基準日
	 * @param mailServerSet メールサーバ設定済区分
	 * @param approvalLst 承認ルートインスタンス
	 * @param leaveComDayOffMana_Rec 振出_休出代休紐付け管理
	 * @param leaveComDayOffMana_Abs 振休_休出代休紐付け管理
	 * @param payoutSubofHDManagement_Abs 振休_振出振休紐付け管理
	 * @param holidayManage 振休紐付け管理区分
	 * @param applicationSetting 申請表示情報
	 */
	public ProcessResult registrationApplicationProcess(String companyId, Optional<AbsenceLeaveApp> abs,
			Optional<RecruitmentApp> rec, GeneralDate baseDate, boolean mailServerSet,
			List<ApprovalPhaseStateImport_New> approvalLst, List<LeaveComDayOffManagement> leaveComDayOffMana_Rec,
			List<LeaveComDayOffManagement> leaveComDayOffMana_Abs,
			List<PayoutSubofHDManagement> payoutSubofHDManagement_Abs, ManageDistinct holidayManage,
			ApplicationSetting applicationSetting) {
		if(rec.isPresent() && abs.isPresent()) {
			//振休申請・振出申請の同時登録(đăng ký đồng thời đơn xin nghỉ bù/ đơn xin làm bù)
			leaveComDayOffMana_Rec.addAll(leaveComDayOffMana_Abs);
			return this.registerRecAndAbs(companyId, baseDate, rec, abs, mailServerSet, approvalLst, leaveComDayOffMana_Rec, holidayManage, applicationSetting);
		}else if(rec.isPresent()){
			//振出申請の登録(đăng ký đơn xin làm bù)
			return this.registerRec(companyId, baseDate, rec, mailServerSet, approvalLst, leaveComDayOffMana_Rec, holidayManage, applicationSetting);
		}else if(abs.isPresent()){
			//振休申請の登録(đăng ký đơn xin nghỉ bù)
			return this.registerAbs(companyId, baseDate, abs, mailServerSet, approvalLst, leaveComDayOffMana_Abs, payoutSubofHDManagement_Abs, applicationSetting);
		}
		return new ProcessResult();
	}
	
	/**
	 * @name メールを送信する(新規)Send an email (new)
	 */
	public void sendEmail() {}
	
	/**
	 * @name 振休申請・振出申請の同時登録
	 * @param companyId 会社ID
	 * @param baseDate 基準日
	 * @param rec 振出申請
	 * @param abs 振休申請
	 * @param managementUnit 管理単位
	 * @param mailServerSet メールサーバ設定済区分
	 * @param approvalLst 承認ルートインスタンス
	 * @param leaveComDayOffMana_Rec 休出代休紐付け管理
	 * @param holidayManage 振休紐付け管理区分
	 * @param applicationSetting 申請表示情報
	 */
	public ProcessResult registerRecAndAbs(String companyId, GeneralDate baseDate, Optional<RecruitmentApp> rec,
			Optional<AbsenceLeaveApp> abs, boolean mailServerSet,
			List<ApprovalPhaseStateImport_New> approvalLst, List<LeaveComDayOffManagement> leaveComDayOffMana_Rec,
			ManageDistinct holidayManage, ApplicationSetting applicationSetting) {
		//ドメイン「振出申請」を1件登録する(đăng ký 1 domain[đơn xin nghì bù])
		appRepository.insertApp(rec.get(), approvalLst);
		recruitmentAppRepository.insert(rec.get());
		//アルゴリズム「登録前共通処理（新規）」を実行する(Thực hiện thuật toán [xử lý chung trước khi đăng ký(new)])
		String recReflectAppId = registerAtApproveReflectionInfoService.newScreenRegisterAtApproveInfoReflect(rec.get().getEmployeeID(), rec.get());
		//休暇紐付け管理を登録する
		absenceServiceProcess.registerVacationLinkManage(leaveComDayOffMana_Rec, new ArrayList<>());
//		//暫定データの登録(đăng ký data tạm thời)
//		interimRemainDataMngRegisterDateChange.registerDateChange(
//				companyId, 
//				rec.get().getEmployeeID(), 
//				Arrays.asList(rec.get().getAppDate().getApplicationDate()));
		
		//ドメイン「振休申請」を1件登録する
		appRepository.insertApp(abs.get(), approvalLst);
		absenceLeaveAppRepository.insert(abs.get());
		//アルゴリズム「登録前共通処理（新規）」を実行する(Thực hiện thuật toán [xử lý chung trước khi đăng ký(new)])
		String absReflectAppId = registerAtApproveReflectionInfoService.newScreenRegisterAtApproveInfoReflect(abs.get().getEmployeeID(), abs.get());
		
		//振休振出同時登録時紐付け管理を登録する
		this.registerTheLinkManagement(companyId, abs.get(), rec.get());
		
		//暫定データの登録(đăng ký data tạm thời)
		interimRemainDataMngRegisterDateChange.registerDateChange(
				companyId, 
				abs.get().getEmployeeID(), 
				Arrays.asList(abs.get().getAppDate().getApplicationDate(), rec.get().getAppDate().getApplicationDate()));
		
		//ドメイン「振休振出同時申請管理」を1件登録する
		//QA: http://192.168.50.4:3000/issues/113413 => done
		compltLeaveSimMngRepository.insert(new AppHdsubRec(rec.get().getAppID(), abs.get().getAppID(), SyncState.SYNCHRONIZING));

		
		//アルゴリズム「新規画面登録後の処理」を実行する(thực hiện thuật toán [xử lý sau khi đăng ký màn hình new])
		//QA: http://192.168.50.4:3000/issues/113416 => done
		Optional<AppTypeSetting> appTypeSetting = applicationSetting.getAppTypeSettings().stream().filter(x -> x.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION).findAny();
		ProcessResult result = new ProcessResult();
		if(appTypeSetting.isPresent()) {
			result = newAfterRegister.processAfterRegister(
					Arrays.asList(abs.get().getAppID(), rec.get().getAppID()), 
					appTypeSetting.get(), 
					mailServerSet, 
					false);
		}
		List<String> reflectAppIdLst = new ArrayList<>();
		if(Strings.isNotBlank(recReflectAppId)) {
			reflectAppIdLst.add(recReflectAppId);
		}
		if(Strings.isNotBlank(absReflectAppId)) {
			reflectAppIdLst.add(absReflectAppId);
		}
		result.setReflectAppIdLst(reflectAppIdLst);
		return result;
	}
	
	/**
	 * @name 振出申請の登録
	 * @param companyId 会社ID
	 * @param baseDate 基準日
	 * @param rec 振出申請
	 * @param managementUnit 管理単位
	 * @param mailServerSet メールサーバ設定済区分
	 * @param approvalLst 承認ルートインスタンス
	 * @param leaveComDayOffMana 休出代休紐付け管理
	 * @param holidayManage 振休紐付け管理区分
	 * @param applicationSetting 申請表示情報
	 */
	public ProcessResult registerRec(String companyId, GeneralDate baseDate, Optional<RecruitmentApp> rec,
			boolean mailServerSet, List<ApprovalPhaseStateImport_New> approvalLst,
			List<LeaveComDayOffManagement> leaveComDayOffMana_Rec, ManageDistinct holidayManage,
			ApplicationSetting applicationSetting) {
		//ドメイン「振出申請」を1件登録する(đăng ký 1 domain[đơn xin nghì bù])
		appRepository.insertApp(rec.get(), approvalLst);
		recruitmentAppRepository.insert(rec.get());
		//アルゴリズム「登録前共通処理（新規）」を実行する(Thực hiện thuật toán [xử lý chung trước khi đăng ký(new)])
		String reflectAppId = registerAtApproveReflectionInfoService.newScreenRegisterAtApproveInfoReflect(rec.get().getEmployeeID(), rec.get());
		//休暇紐付け管理を登録する
		absenceServiceProcess.registerVacationLinkManage(leaveComDayOffMana_Rec, new ArrayList<>());
		//暫定データの登録(đăng ký data tạm thời)
		interimRemainDataMngRegisterDateChange.registerDateChange(
				companyId, 
				rec.get().getEmployeeID(), 
				Arrays.asList(rec.get().getAppDate().getApplicationDate()));
		
		//アルゴリズム「新規画面登録後の処理」を実行する(thực hiện thuật toán [xử lý sau khi đăng ký màn hình new])
		//QA: http://192.168.50.4:3000/issues/113442 => done
		Optional<AppTypeSetting> appTypeSetting = applicationSetting.getAppTypeSettings().stream().filter(x -> x.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION).findAny();
		ProcessResult result = new ProcessResult();
		if(appTypeSetting.isPresent()) {
			result = newAfterRegister.processAfterRegister(
					Arrays.asList(rec.get().getAppID()), 
					appTypeSetting.get(), 
					mailServerSet,
					false);
		}
		if(Strings.isNotBlank(reflectAppId)) {
			result.setReflectAppIdLst(Arrays.asList(reflectAppId));
		}
		return result;
	}
	
	/**
	 * @name 振休申請の登録
	 * @param companyId 会社ID
	 * @param baseDate 基準日
	 * @param abs 振休申請
	 * @param managementUnit 管理単位
	 * @param mailServerSet メールサーバ設定済区分
	 * @param approvalLst 承認ルートインスタンス
	 * @param leaveComDayOffMana 休出代休紐付け管理
	 * @param payoutSubofHDManagement_Abs 振出振休紐付け管理
	 * @param applicationSetting 申請表示情報
	 */
	public ProcessResult registerAbs(String companyId, GeneralDate baseDate, Optional<AbsenceLeaveApp> abs,
			boolean mailServerSet, List<ApprovalPhaseStateImport_New> approvalLst,
			List<LeaveComDayOffManagement> leaveComDayOffMana_Abs, List<PayoutSubofHDManagement> payoutSubofHDManagement_Abs,
			ApplicationSetting applicationSetting) {
		//ドメイン「振休申請」を1件登録する
		appRepository.insertApp(abs.get(), approvalLst);
		absenceLeaveAppRepository.insert(abs.get());
		//アルゴリズム「登録前共通処理（新規）」を実行する(Thực hiện thuật toán [xử lý chung trước khi đăng ký(new)])
		String reflectAppId = registerAtApproveReflectionInfoService.newScreenRegisterAtApproveInfoReflect(abs.get().getEmployeeID(), abs.get());
		//休暇紐付け管理を登録する
		absenceServiceProcess.registerVacationLinkManage(leaveComDayOffMana_Abs, payoutSubofHDManagement_Abs);
		//暫定データの登録(đăng ký data tạm thời)
		interimRemainDataMngRegisterDateChange.registerDateChange(
				companyId, 
				abs.get().getEmployeeID(), 
				Arrays.asList(abs.get().getAppDate().getApplicationDate()));
		
		//アルゴリズム「新規画面登録後の処理」を実行する(thực hiện thuật toán [xử lý sau khi đăng ký màn hình new])
		//QA: http://192.168.50.4:3000/issues/113442 => done
		Optional<AppTypeSetting> appTypeSetting = applicationSetting.getAppTypeSettings().stream().filter(x -> x.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION).findAny();
		ProcessResult result = new ProcessResult();
		if(appTypeSetting.isPresent()) {
			result = newAfterRegister.processAfterRegister(
					Arrays.asList(abs.get().getAppID()), 
					appTypeSetting.get(), 
					mailServerSet,
					false);
		}
		if(Strings.isNotBlank(reflectAppId)) {
			result.setReflectAppIdLst(Arrays.asList(reflectAppId));
		}
		return result;
	}
	
	/**
	 * @name 振休振出同時登録時紐付け管理を登録する
	 * @param companyId 会社ID
	 * @param abs 振休申請
	 * @param rec 振出申請
	 * @param holidayManage 振休紐付け管理区分
	 */
	public void registerTheLinkManagement(String companyId, AbsenceLeaveApp abs, RecruitmentApp rec) {
	    //<<Public>> 指定した勤務種類をすべて取得する
	    Optional<WorkType> workTypes = workTypeRepo.findByDeprecated(companyId, rec.getWorkInformation().getWorkTypeCode().v());;
	    if(workTypes.isPresent()) {
	        //ドメインモデル「振出振休紐付け管理」を登録する
	        payoutSubofHDManaRepository.add(
	                new PayoutSubofHDManagement(
	                        rec.getEmployeeID(), 
	                        rec.getAppDate().getApplicationDate(), 
	                        abs.getAppDate().getApplicationDate(), 
	                        workTypes.get().getDailyWork().getWorkTypeUnit() == WorkTypeUnit.OneDay ? 1.0 : 0.5,
	                                TargetSelectionAtr.REQUEST.value));
	    }
//		if(holidayManage == ManageDistinct.YES) {
//		}
	}
}
