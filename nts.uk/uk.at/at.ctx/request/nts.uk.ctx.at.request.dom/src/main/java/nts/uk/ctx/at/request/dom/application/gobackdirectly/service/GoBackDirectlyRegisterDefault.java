
package nts.uk.ctx.at.request.dom.application.gobackdirectly.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationApprovalService;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.dailyattendancetime.DailyAttendanceTimeCaculation;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleAdapter;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.DetailAfterUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.RegisterAtApproveReflectionInfoService;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.after.NewAfterRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectlyRepository;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.InforGoBackCommonDirectOutput;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.ApplicationStatus;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.directgoback.GoBackReflect;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 直行直帰登録
 * 
 * @author ducpm
 */
@Stateless
public class GoBackDirectlyRegisterDefault implements GoBackDirectlyRegisterService {
	
	@Inject
	RegisterAtApproveReflectionInfoService registerAppReplection;
	
	@Inject
	ApplicationApprovalService appRepo;
	
	@Inject
	NewBeforeRegister processBeforeRegister;
	
	@Inject 
	NewAfterRegister newAfterRegister;
	
	@Inject
	ApplicationSettingRepository applicationSettingRepository;
	
	@Inject
	DailyAttendanceTimeCaculation dailyAttendanceTimeCaculation;
	
	@Inject
	ScBasicScheduleAdapter scBasicScheduleAdapter;
	
	@Inject
	private DetailBeforeUpdate detailBeforeUpdate;
	
	@Inject
	private GoBackDirectlyRepository goBackDirectlyRepository;
	
	@Inject
	private RegisterAtApproveReflectionInfoService registerAtApprove;
	
	@Inject
	private DetailAfterUpdate detailAfterUpdate;
	
	@Inject
	ApplicationApprovalService appRepository;
	
	@Inject
	ApplicationRepository appRe;
	
	@Inject 
	CommonAlgorithm commonAlgorith;
	
	@Inject
    private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	
	/**Refactor 4
	 * 	直行直帰登録前チェック
	 * @param companyId
	 * @param agenAtr
	 * @param at
	 * @param goBackDirectly
	 * @param inforGoBackCommonDirectOutput
	 * @return 確認メッセージリスト
	 */
	public List<ConfirmMsgOutput> getBeforeRegisterMessageList(String companyId, boolean agenAtr,
			Application application, GoBackDirectly goBackDirectly,
			InforGoBackCommonDirectOutput inforGoBackCommonDirectOutput) {
		// String employeeID = AppContexts.user().employeeId();
		// this.inconsistencyCheck(companyId, employeeID, GeneralDate.today());
		EmployeeInfoImport employeeInfo = inforGoBackCommonDirectOutput.getAppDispInfoStartup().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0);
		GeneralDate appDate = application.getAppDate().getApplicationDate();
		List<GeneralDate> dateLst = new ArrayList<GeneralDate>();
		dateLst.add(appDate);
		List<String> workTypeLst = new ArrayList<String>();
		if (goBackDirectly != null) {
			Optional<WorkInformation> dataWork = goBackDirectly.getDataWork();
			if (dataWork.isPresent()) {
				if (dataWork.get().getWorkTypeCode() != null) {
					if (StringUtils.isNotBlank(dataWork.get().getWorkTypeCode().v())) {
						workTypeLst.add(dataWork.get().getWorkTypeCode().v());
					}
				}
			}
		}
		List<ActualContentDisplay> actualContentDisplayLst = inforGoBackCommonDirectOutput.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList());
		// 申請の矛盾チェック
		commonAlgorith.appConflictCheck(companyId, employeeInfo, dateLst, workTypeLst, actualContentDisplayLst);
		// アルゴリズム「2-1.新規画面登録前の処理」を実行する 
		List<ConfirmMsgOutput> listResult = processBeforeRegister.processBeforeRegister_New(companyId,
				EmploymentRootAtr.APPLICATION, agenAtr, application, null, 
				inforGoBackCommonDirectOutput
				.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()),
				Collections.emptyList(),
				inforGoBackCommonDirectOutput.getAppDispInfoStartup(), 
                goBackDirectly.getDataWork().isPresent() ? Arrays.asList(goBackDirectly.getDataWork().get().getWorkTypeCode().v()) : new ArrayList<String>(), 
                Optional.empty(),
                goBackDirectly.getDataWork().isPresent() ? goBackDirectly.getDataWork().get().getWorkTimeCodeNotNull().map(WorkTimeCode::v) : Optional.empty(), false);
		return listResult;
	}
	/**Refactor4
	 * 直行直帰更新前チェック
	 * UKDesign.UniversalK.就業.KAF_申請.KAF009_直行直帰の申請.B:直行直帰の申請（更新）.アルゴリズム.直行直帰更新前チェック
	 * * @param companyId
	 * @param agenAtr
	 * @param at
	 * @param goBackDirectly
	 * @param inforGoBackCommonDirectOutput
	 * @return 確認メッセージリスト
	 */
	public void getBeforeUpdateRegisterMessageList(String companyId, boolean agenAtr,
			Application application, GoBackDirectly goBackDirectly,
			InforGoBackCommonDirectOutput inforGoBackCommonDirectOutput) {
		
			EmployeeInfoImport employeeInfo = inforGoBackCommonDirectOutput.getAppDispInfoStartup().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0);
			GeneralDate appDate = application.getAppDate().getApplicationDate();
			List<GeneralDate> dateLst = new ArrayList<GeneralDate>();
			dateLst.add(appDate);
			List<String> workTypeLst = new ArrayList<String>();
			if (goBackDirectly != null) {
				Optional<WorkInformation> dataWork = goBackDirectly.getDataWork();
				if (dataWork.isPresent()) {
					if (dataWork.get().getWorkTypeCode() != null) {
						if (StringUtils.isNotBlank(dataWork.get().getWorkTypeCode().v())) {
							workTypeLst.add(dataWork.get().getWorkTypeCode().v());
						}
					}
				}
			}
			List<ActualContentDisplay> actualContentDisplayLst = inforGoBackCommonDirectOutput.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList());
			// 申請の矛盾チェック
			commonAlgorith.appConflictCheck(companyId, employeeInfo, dateLst, workTypeLst, actualContentDisplayLst);
			
			String workTypeCode = goBackDirectly.getDataWork().flatMap(x -> Optional.ofNullable(x.getWorkTypeCode())).map(x -> x.v()).orElse(null);
			String workTimeCode = goBackDirectly.getDataWork().flatMap(x -> x.getWorkTimeCodeNotNull()).map(x -> x.v()).orElse(null);
			
			if (inforGoBackCommonDirectOutput.getWorkInfo().isPresent()) {
				if (workTypeCode != null) {
					workTypeCode = !workTypeCode.equals(inforGoBackCommonDirectOutput.getWorkInfo().get().getWorkType()) ? workTypeCode : null;
				}
				if (workTimeCode != null) {
					workTimeCode = !workTimeCode.equals(inforGoBackCommonDirectOutput.getWorkInfo().get().getWorkTime()) ? workTimeCode : null;
				}
				}
			
			// アルゴリズム「4-1.詳細画面登録前の処理」を実行する
			detailBeforeUpdate.processBeforeDetailScreenRegistration(
				companyId,
				application.getEmployeeID(),
				application.getAppDate().getApplicationDate(),
				EmploymentRootAtr.APPLICATION.value,
				application.getAppID(),
				application.getPrePostAtr(),
				application.getVersion(),
				workTypeCode,
				workTimeCode,
				inforGoBackCommonDirectOutput.getAppDispInfoStartup(), 
				goBackDirectly.getDataWork().isPresent() ? Arrays.asList(goBackDirectly.getDataWork().get().getWorkTypeCode().v()) : new ArrayList<String>(), 
				Optional.empty(), 
				false, 
				goBackDirectly.getDataWork().map(x -> x.getWorkTypeCode().v()), 
				goBackDirectly.getDataWork().isPresent() ? goBackDirectly.getDataWork().get().getWorkTimeCodeNotNull().map(WorkTimeCode::v) : Optional.empty());
	}
	/**
	 * 共通登録前のエラーチェック処理
	 * @param companyId 会社ID
	 * @param agentAtr 代行申請区分
	 * @param application 申請
	 * @param goBackDirectly 直行直帰申請
	 * @param inforGoBackCommonDirectOutput 直行直帰申請起動時の表示情報
	 * @param mode モード＝　新規
	 * @return 確認メッセージリスト
	 */
	public List<ConfirmMsgOutput> checkBeforRegisterNew(String companyId, boolean agentAtr, Application application,
			GoBackDirectly goBackDirectly, InforGoBackCommonDirectOutput inforGoBackCommonDirectOutput, boolean mode) {
		// #110892
		if (goBackDirectly.getStraightDistinction() == NotUseAtr.NOT_USE && goBackDirectly.getStraightLine() == NotUseAtr.NOT_USE) {
			throw new BusinessException("Msg_1808");
		}
		
		// 確認メッセージリスト＝Empty
		List<ConfirmMsgOutput> lstConfirm = new ArrayList<ConfirmMsgOutput>();
		// mode new
		// モードチェックする
		if (mode) {
			// 直行直帰登録前チェック
			lstConfirm = this.getBeforeRegisterMessageList(companyId, agentAtr, application, goBackDirectly,
					inforGoBackCommonDirectOutput);

		} else {
			// 直行直帰更新前チェック
			this.getBeforeUpdateRegisterMessageList(companyId, agentAtr, application, goBackDirectly, inforGoBackCommonDirectOutput);
		}
//		確認メッセージリスト＝取得した確認メッセージリスト 
		GoBackDirectAtr check = goBackDirectCheckNew(goBackDirectly);
		
//		if (check == GoBackDirectAtr.NOT) {
////			確認メッセージリストに（Msg_338）を追加する
//			lstConfirm.add(new ConfirmMsgOutput("Msg_338", Collections.emptyList()));
//		}
		return lstConfirm;
	}

	
	public void createThrowMsg(String msgConfirm, List<String> msgLst){
		if(msgLst.size() >= 4){
			throw new BusinessException(msgConfirm, msgLst.get(0), msgLst.get(1), msgLst.get(2), msgLst.get(3));
		} else if (msgLst.size() >= 2){
			throw new BusinessException(msgConfirm, msgLst.get(0), msgLst.get(1));
		} else {
			throw new BusinessException(msgConfirm);
		}
	}
	
	
	/**
	 * 01_直行直帰_勤務種類の分類チェック
	 * @param workType
	 * @return
	 */
	private boolean workTypeInconsistencyCheck(WorkType workType){
		// INPUT.ドメインモデル「勤務種類.勤務の単位(WORK_ATR)」をチェックする
		if(workType.getDailyWork().getWorkTypeUnit()==WorkTypeUnit.OneDay){
			// INPUT.ドメインモデル「勤務種類.1日勤務分類(ONE_DAY_CLS)」をチェックする
			WorkTypeClassification workTypeClassification = workType.getDailyWork().getOneDay();
			if(workTypeClassification==WorkTypeClassification.Attendance||
				workTypeClassification==WorkTypeClassification.Shooting||
				workTypeClassification==WorkTypeClassification.HolidayWork){
				return false;
			}
			return true;
		}
		// INPUT.ドメインモデル「勤務種類.午前の勤務分類(MORNING_CLS)」をチェックする
		WorkTypeClassification workTypeClassMorning = workType.getDailyWork().getMorning();
		if(workTypeClassMorning!=WorkTypeClassification.Holiday&&
			workTypeClassMorning!=WorkTypeClassification.AnnualHoliday&&
			workTypeClassMorning!=WorkTypeClassification.YearlyReserved&&
			workTypeClassMorning!=WorkTypeClassification.SpecialHoliday&&
			workTypeClassMorning!=WorkTypeClassification.Absence&&
			workTypeClassMorning!=WorkTypeClassification.SubstituteHoliday&&
			workTypeClassMorning!=WorkTypeClassification.Pause&&
			workTypeClassMorning!=WorkTypeClassification.TimeDigestVacation){
			return false;
		}
		// INPUT.ドメインモデル「勤務種類.午後の勤務分類(AFTERNOON_CLS)」をチェックする
		WorkTypeClassification workTypeClassAfternoon = workType.getDailyWork().getAfternoon();
		if(workTypeClassAfternoon!=WorkTypeClassification.Holiday&&
			workTypeClassAfternoon!=WorkTypeClassification.AnnualHoliday&&
			workTypeClassAfternoon!=WorkTypeClassification.YearlyReserved&&
			workTypeClassAfternoon!=WorkTypeClassification.SpecialHoliday&&
			workTypeClassAfternoon!=WorkTypeClassification.Absence&&
			workTypeClassAfternoon!=WorkTypeClassification.SubstituteHoliday&&
			workTypeClassAfternoon!=WorkTypeClassification.Pause&&
			workTypeClassAfternoon!=WorkTypeClassification.TimeDigestVacation){
			return false;
		}
		return true;
	}
	@Override
	public GoBackDirectAtr goBackDirectCheckNew(GoBackDirectly goBackDirectly) {
		if (goBackDirectly.getStraightDistinction().value == GoBackDirectAtr.IS.value && goBackDirectly.getStraightLine().value == GoBackDirectAtr.IS.value) {
			return GoBackDirectAtr.IS;
		}
		return GoBackDirectAtr.NOT;
	}
	@Override
	public ProcessResult register(GoBackDirectly goBackDirectly, Application application, InforGoBackCommonDirectOutput inforGoBackCommonDirectOutput) {
		

		GoBackReflect goBackReflect = inforGoBackCommonDirectOutput.getGoBackReflect();
		ApplicationStatus status = goBackReflect.getReflectApplication();
//		INPUT.「直行直帰申請起動時の表示情報.直行直帰申請の反映」．勤務情報を反映するをチェックする
		// handle  直行直帰申請.勤務を変更する = しない (112366)
		Boolean c1 = status == ApplicationStatus.DO_NOT_REFLECT;
		Boolean c2 = false;
		if (goBackDirectly.getIsChangedWork().isPresent()) {
			c2 = goBackDirectly.getIsChangedWork().get() == NotUseAtr.NOT_USE;
		}
		if (c1 || c2 ) { // 「直行直帰申請.勤務情報」をクリアする
			Optional<WorkInformation> dataWork = Optional.empty();
			goBackDirectly.setDataWork(dataWork);

		} 


		appRepository.insertApp(
				application,
				inforGoBackCommonDirectOutput.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpListApprovalPhaseState().isPresent() ? inforGoBackCommonDirectOutput.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpListApprovalPhaseState().get() : null);
		

		// ドメインモデル「直行直帰申請」の新規登録する
		goBackDirectlyRepository.add(goBackDirectly);
		// 2-2.新規画面登録時承認反映情報の整理
		String reflectAppId = registerAtApprove.newScreenRegisterAtApproveInfoReflect(application.getEmployeeID(), application);
		List<GeneralDate> listDates = new ArrayList<>();
		listDates.add(application.getAppDate().getApplicationDate());
		// 暫定データの登録
		interimRemainDataMngRegisterDateChange.registerDateChange(
				AppContexts.user().companyId(),
				application.getEmployeeID(), 
				listDates);

		
		// アルゴリズム「2-3.新規画面登録後の処理」を実行する
		AppTypeSetting appTypeSetting = inforGoBackCommonDirectOutput.getAppDispInfoStartup().getAppDispInfoNoDateOutput().getApplicationSetting().getAppTypeSettings()
				.stream().filter(x -> x.getAppType()==application.getAppType()).findAny().get();
		ProcessResult processResult = newAfterRegister.processAfterRegister(
				Arrays.asList(application.getAppID()), 
				appTypeSetting,
				inforGoBackCommonDirectOutput.getAppDispInfoStartup().getAppDispInfoNoDateOutput().isMailServerSet(),
				false);
		if(Strings.isNotBlank(reflectAppId)) {
			processResult.setReflectAppIdLst(Arrays.asList(reflectAppId));
		}
		return processResult;
	}
	@Override
	public ProcessResult update(String companyId, Application application, GoBackDirectly goBackDirectly,
			InforGoBackCommonDirectOutput inforGoBackCommonDirectOutput) {
		// INPUT.「直行直帰申請起動時の表示情報.直行直帰申請の反映」．勤務情報を反映するをチェックする
		GoBackReflect goBackReflect = inforGoBackCommonDirectOutput.getGoBackReflect();
		ApplicationStatus status = goBackReflect.getReflectApplication();
//		INPUT.「直行直帰申請起動時の表示情報.直行直帰申請の反映」．勤務情報を反映するをチェックする
		// handle  直行直帰申請.勤務を変更する = しない (112366)
		Boolean c1 = status == ApplicationStatus.DO_NOT_REFLECT;
		Boolean c2 = false;
		if (goBackDirectly.getIsChangedWork().isPresent()) {
			c2 = goBackDirectly.getIsChangedWork().get() == NotUseAtr.NOT_USE;
		}
		if (c1 || c2 ) { // 「直行直帰申請.勤務情報」をクリアする
			Optional<WorkInformation> dataWork = Optional.empty();
			goBackDirectly.setDataWork(dataWork);

		} 

		appRe.update(application);
		// ドメインモデル「直行直帰申請」の更新する
		// params is appId or application
		goBackDirectlyRepository.update(goBackDirectly);
		List<GeneralDate> listDates = new ArrayList<>();
		listDates.add(application.getAppDate().getApplicationDate());
		// 暫定データの登録
		interimRemainDataMngRegisterDateChange.registerDateChange(AppContexts.user().companyId(),
				application.getEmployeeID(), listDates);
//		アルゴリズム「4-2.詳細画面登録後の処理」を実行する
		return detailAfterUpdate.processAfterDetailScreenRegistration(companyId, application.getAppID(), inforGoBackCommonDirectOutput.getAppDispInfoStartup());
//		return null;

	}
	
}
