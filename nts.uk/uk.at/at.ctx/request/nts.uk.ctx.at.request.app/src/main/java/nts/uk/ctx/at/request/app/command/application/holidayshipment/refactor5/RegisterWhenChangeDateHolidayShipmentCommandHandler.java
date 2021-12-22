package nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.request.app.command.application.common.ApplicationInsertCmd;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5.command.AbsenceLeaveAppCmd;
import nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5.command.RecruitmentAppCmd;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto.DisplayInforWhenStarting;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto.LinkingManagementInforDto;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.shared.app.find.remainingnumber.paymana.PayoutSubofHDManagementDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.subhdmana.dto.LeaveComDayOffManaDto;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author thanhpv
 *	UKDesign.UniversalK.就業.KAF_申請.KAF011_振休振出申請.C：振休日変更.ユースケース.登録する(Đăng ký)
 */
@Stateless
public class RegisterWhenChangeDateHolidayShipmentCommandHandler {

	@Inject
	private ErrorCheckProcessingBeforeRegistrationKAF011 errorCheckProcessingBeforeRegistrationKAF011;
	
	@Inject
	private RecruitmentAppRepository recruitmentAppRepository;
	
	@Inject
	private AbsenceLeaveAppRepository absenceLeaveAppRepository;
	
	@Inject
	private SaveHolidayShipmentCommandHandlerRef5 saveHolidayShipmentCommandHandlerRef5;
	
	@Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	
	@Inject
	private PreRegistrationErrorCheck preRegistrationErrorCheck;
	
	@Inject
	private LeaveComDayOffManaRepository leaveComDayOffManaRepository;
	
	@Inject
	private PayoutSubofHDManaRepository payoutSubofHDManaRepository;
	
	@Inject
	private ApplicationRepository applicationRepository;
	
	/**
	 * @name 登録する
	 */
	public ProcessResult register(DisplayInforWhenStarting command, GeneralDate appDateNew, String appReason, Integer appStandardReasonCD){
		String companyId = AppContexts.user().companyId();
		
		AbsenceLeaveApp absNew = this.errorCheckWhenChangingHolidays(companyId, command, appDateNew, appReason, appStandardReasonCD, 
		        command.getApplicationForHoliday().getWorkTypeList().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
		
		ProcessResult processResult = this.registerProcess(companyId, command, absNew);
		
		processResult.setAppIDLst(Arrays.asList(absNew.getAppID()));
		
		return processResult;
	}
	
	/**
	 * @name 振休日変更時のエラーチェック
	 * @param companyId 会社ID
	 * @param displayInforWhenStarting 振休振出申請起動時の表示情報
	 * @param appDate 振休日_NEW
	 * @param appReason 申請理由_NEW
	 * @param appStandardReasonCD 定型理由コード_NEW
	 */
	public AbsenceLeaveApp errorCheckWhenChangingHolidays(String companyId, DisplayInforWhenStarting displayInforWhenStarting, GeneralDate appDate, String appReason, Integer appStandardReasonCD, List<WorkType> listWorkTypes) {
		//「INPUT．振休日_NEW = INPUT．振休振出申請起動時の表示情報．申請表示情報．申請詳細画面情報．申請(振休)．申請日」がtrue
		if(appDate.equals(displayInforWhenStarting.abs.application.toDomain().getAppDate().getApplicationDate())) {
			throw new BusinessException("Msg_1683");
		}
		
		AbsenceLeaveAppCmd absNew = displayInforWhenStarting.abs; 
		absNew.changeSourceHoliday = displayInforWhenStarting.abs.application.getAppDate();
		absNew.application.setAppDate(appDate.toString());
		absNew.application.setOpAppStartDate(appDate.toString());
		absNew.application.setOpAppEndDate(appDate.toString());
		absNew.application.setOpAppStandardReasonCD(appStandardReasonCD);
		absNew.application.setOpAppReason(appReason);
		absNew.applicationInsert = new ApplicationInsertCmd(absNew.application.toDomain());
	
		AbsenceLeaveApp abs = absNew.toDomainInsertAbs();
		abs.setAppID(IdentifierUtil.randomUniqueId());
		Optional<RecruitmentApp> rec = Optional.empty();
		if(displayInforWhenStarting.existRec()) {
			displayInforWhenStarting.rec.application.setOpAppReason(appReason);
			displayInforWhenStarting.rec.application.setOpAppStandardReasonCD(appStandardReasonCD);
			rec = recruitmentAppRepository.findByAppId(displayInforWhenStarting.rec.application.getAppID());
		}
	
		preRegistrationErrorCheck.applicationDateRelatedCheck(companyId, Optional.of(abs), rec, displayInforWhenStarting.appDispInfoStartup.getAppDispInfoWithDateOutput().getEmpHistImport().getEmploymentCode());
		
		errorCheckProcessingBeforeRegistrationKAF011.processing(companyId, Optional.of(abs), rec, displayInforWhenStarting.represent, 
				displayInforWhenStarting.appDispInfoStartup.toDomain().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()), 
				displayInforWhenStarting.appDispInfoStartup.toDomain().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(new ArrayList<ActualContentDisplay>()), 
				displayInforWhenStarting.appDispInfoStartup.toDomain(), 
				displayInforWhenStarting.existAbs() ? displayInforWhenStarting.abs.payoutSubofHDManagements.stream().map(c->c.toDomain()).collect(Collectors.toList()) : new ArrayList<>(),
				displayInforWhenStarting.existAbs() ? displayInforWhenStarting.abs.leaveComDayOffMana.stream().map(c -> c.toDomain()).collect(Collectors.toList()) : new ArrayList<>(), 
				false,
				true, 
				listWorkTypes);
		return abs;
	}
	
	/**
	 * @name 振休日変更時の登録処理
	 * @param companyId 会社ID
	 * @param displayInforWhenStarting 振休振出申請起動時の表示情報
	 * @param absNew 振休申請_NEW：申請
	 */
	public ProcessResult registerProcess(String companyId, DisplayInforWhenStarting displayInforWhenStarting, AbsenceLeaveApp absNew) {
		Optional<AbsenceLeaveApp> absOld = absenceLeaveAppRepository.findByAppId(displayInforWhenStarting.abs.application.getAppID());
		LinkingManagementInforDto linkingManagementInfor = null;
		// 申請の組み合わせをチェックする
		if (displayInforWhenStarting.existRec()) {
			Optional<RecruitmentApp> rec = recruitmentAppRepository.findByAppId(displayInforWhenStarting.rec.application.getAppID());
			// 古い振休振出申請のステータスを更新する
			rec.get().getApplication().getReflectionStatus().getListReflectionStatusOfDay().forEach(x -> {
				x.setActualReflectStatus(ReflectedState.CANCELED);
			});
			applicationRepository.update(rec.get().getApplication());
			absOld.get().getApplication().getReflectionStatus().getListReflectionStatusOfDay().forEach(x -> {
				x.setActualReflectStatus(ReflectedState.CANCELED);
			});
			applicationRepository.update(absOld.get().getApplication());
			//振出振休紐付け管理を作り直す
	        linkingManagementInfor = this.recreateTheTieUpManagement(absNew.getAppDate().getApplicationDate(), displayInforWhenStarting.substituteManagement, displayInforWhenStarting.holidayManage, displayInforWhenStarting.abs.leaveComDayOffMana, displayInforWhenStarting.abs.payoutSubofHDManagements);
	        
			// 暫定データの登録
			interimRemainDataMngRegisterDateChange.registerDateChange(
					companyId, 
					absOld.get().getEmployeeID(), 
					Arrays.asList(absOld.get().getAppDate().getApplicationDate(), rec.get().getAppDate().getApplicationDate()));
		} else {
			// 古い振休申請のステータスを更新する
			absOld.get().getApplication().getReflectionStatus().getListReflectionStatusOfDay().forEach(x -> {
				x.setActualReflectStatus(ReflectedState.CANCELED);
			});
			applicationRepository.update(absOld.get().getApplication());
			//振出振休紐付け管理を作り直す
	        linkingManagementInfor = this.recreateTheTieUpManagement(absNew.getAppDate().getApplicationDate(), displayInforWhenStarting.substituteManagement, displayInforWhenStarting.holidayManage, displayInforWhenStarting.abs.leaveComDayOffMana, displayInforWhenStarting.abs.payoutSubofHDManagements);
	        
			// 暫定データの登録
			interimRemainDataMngRegisterDateChange.registerDateChange(
					companyId, 
					absOld.get().getEmployeeID(), 
					Arrays.asList(absOld.get().getAppDate().getApplicationDate()));
		}
		
		//振出振休紐付け管理を作り直す
//		LinkingManagementInforDto linkingManagementInfor = this.recreateTheTieUpManagement(absNew.getAppDate().getApplicationDate(), displayInforWhenStarting.substituteManagement, displayInforWhenStarting.holidayManage, displayInforWhenStarting.abs.leaveComDayOffMana, displayInforWhenStarting.abs.payoutSubofHDManagements);
		
		Optional<RecruitmentAppCmd> recNew = displayInforWhenStarting.existRec()?Optional.of(displayInforWhenStarting.rec):Optional.empty();
		recNew.ifPresent(c->c.applicationInsert = new ApplicationInsertCmd(c.application.toDomain()));
		
		//振休振出申請（新規）登録処理
		return saveHolidayShipmentCommandHandlerRef5.registrationApplicationProcess(companyId, Optional.of(absNew), Optional.ofNullable(recNew.map(c->c.toDomainInsertRec()).orElse(null)), 
				displayInforWhenStarting.appDispInfoStartup.getAppDispInfoWithDateOutput().toDomain().getBaseDate(), 
				displayInforWhenStarting.appDispInfoStartup.getAppDispInfoNoDateOutput().isMailServerSet(), 
				displayInforWhenStarting.appDispInfoStartup.toDomain().getAppDispInfoWithDateOutput().getOpListApprovalPhaseState().get(), 
				displayInforWhenStarting.existRec() ? displayInforWhenStarting.rec.leaveComDayOffMana.stream().map(c->c.toDomain()).collect(Collectors.toList()) : new ArrayList<>(), 
				linkingManagementInfor.absLeaveComDayOffMana.stream().map(c->c.toDomain()).collect(Collectors.toList()), 
				linkingManagementInfor.absPayoutSubofHDManagements.stream().map(c->c.toDomain()).collect(Collectors.toList()), 
				EnumAdaptor.valueOf(displayInforWhenStarting.holidayManage, ManageDistinct.class), 
				displayInforWhenStarting.appDispInfoStartup.toDomain().getAppDispInfoNoDateOutput().getApplicationSetting());
	}
	
	/**
	 * @name 振出振休紐付け管理を作り直す
	 * @param abs
	 * @param substituteManagement 代休紐付け管理区分
	 * @param holidayManage 振休紐付け管理区分
	 * @param leaveComDayOffMana 休出代休紐付け管理<List>
	 * @param payoutSubofHDManagements 振出振休紐付け管理<List>
	 */
	public LinkingManagementInforDto recreateTheTieUpManagement(GeneralDate absDate, Integer substituteManagement, Integer holidayManage, List<LeaveComDayOffManaDto> leaveComDayOffMana, List<PayoutSubofHDManagementDto> payoutSubofHDManagements) {
		if(substituteManagement == 1) {
			if(leaveComDayOffMana != null) {
				for (LeaveComDayOffManaDto item : leaveComDayOffMana) {
					//ドメインモデル「休出代休紐付け管理」を削除する
					leaveComDayOffManaRepository.delete(item.getSid(), item.getOutbreakDay(), item.getDateOfUse());
					//INPUT．休出代休紐付け管理．使用日＝INPUT．申請日_NEW
					item.setDateOfUse(absDate);
				}
			}
		}
//		if(holidayManage == 1) {
			if(payoutSubofHDManagements != null) {
				for (PayoutSubofHDManagementDto item : payoutSubofHDManagements) {
					//ドメインモデル「休出代休紐付け管理」を削除する
					payoutSubofHDManaRepository.delete(item.getSid(), item.getOutbreakDay(), item.getDateOfUse());
					//INPUT．休出代休紐付け管理．使用日＝INPUT．申請日_NEW
					item.setDateOfUse(absDate);
				}
			}
//		}
		return new LinkingManagementInforDto(new ArrayList<>(), leaveComDayOffMana, payoutSubofHDManagements);
	}
	
	
}
