package nts.uk.ctx.at.request.dom.application.workchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.error.BundledBusinessException;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.WorkInformationForApplication;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.WorkInfoListOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.MsgErrorOutput;
import nts.uk.ctx.at.request.dom.application.overtime.service.CheckWorkingInfoResult;
import nts.uk.ctx.at.request.dom.application.workchange.output.AppWorkChangeDetailOutput;
import nts.uk.ctx.at.request.dom.application.workchange.output.AppWorkChangeDispInfo;
import nts.uk.ctx.at.request.dom.application.workchange.output.AppWorkChangeOutput;
import nts.uk.ctx.at.request.dom.application.workchange.output.ChangeWkTypeTimeOutput;
import nts.uk.ctx.at.request.dom.application.workchange.output.WorkChangeCheckRegOutput;
import nts.uk.ctx.at.request.dom.application.workchange.output.WorkTypeWorkTimeSelect;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.workchange.AppWorkChangeSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.workchange.InitDisplayWorktimeAtr;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.TargetWorkTypeByApp;
import nts.uk.ctx.at.request.dom.setting.request.application.workchange.AppWorkChangeSettingOutput;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.workchangeapp.ReflectWorkChangeApp;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class AppWorkChangeServiceImpl implements AppWorkChangeService {

	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;
	
	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;
	
	@Inject 
	private BasicScheduleService basicScheduleService;
	
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;
	
	@Inject
	private NewBeforeRegister newBeforeRegister;
	
	
	@Inject
	private AppWorkChangeRepository appWorkChangeRepositoryNew;
	
	@Inject
	private DetailBeforeUpdate detailBeforeUpdate;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private AppWorkChangeSetRepository appWorkChangeSetRepoNew;

	
	@Override
	public AppWorkChangeDispInfo getStartNew(String companyID, List<String> employeeIDLst, List<GeneralDate> dateLst,
			AppDispInfoStartupOutput appDispInfoStartupOutput) {
		AppWorkChangeDispInfo result = new AppWorkChangeDispInfo();
		// 勤務変更申請設定を取得する
		AppWorkChangeSettingOutput appWorkChangeSettingOutput = this.getAppWorkChangeSettingOutput(companyID);
		// ドメインモデル「勤務変更申請設定」を取得する
		AppWorkChangeSet appWorkChangeSet = appWorkChangeSetRepoNew.findByCompanyId(companyID).get();
		AppEmploymentSet employmentSet = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet().orElse(null);
		// 勤務種類を取得
		List<WorkType> workTypeLst = this.getWorkTypeLst(employmentSet);
		// 勤務種類・就業時間帯の初期選択項目を取得する
		String employeeID = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid();
		GeneralDate date = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate();
		List<WorkTimeSetting> workTimeLst = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().get();
		WorkTypeWorkTimeSelect workTypeWorkTimeSelect = 
				this.initWorkTypeWorkTime(companyID, employeeID, date, workTypeLst, workTimeLst);
		// 勤務種類・就業時間帯を変更する時
		String workTypeCD = workTypeWorkTimeSelect.getWorkType().getWorkTypeCode().v();
		String workTimeCD = !workTypeWorkTimeSelect.getWorkTime().isPresent() ? null : workTypeWorkTimeSelect.getWorkTime().get().getWorktimeCode().v();
		ChangeWkTypeTimeOutput changeWkTypeTimeOutput =
				this.changeWorkTypeWorkTime(companyID, workTypeCD, Optional.ofNullable(workTimeCD), appWorkChangeSet);
		// 取得した情報をOUTPUT「勤務変更申請の表示情報」にセットしてを返す
		result.setAppDispInfoStartupOutput(appDispInfoStartupOutput);
		result.setAppWorkChangeSet(appWorkChangeSet);
		result.setWorkTypeLst(workTypeLst);
		result.setSetupType(Optional.of(changeWkTypeTimeOutput.getSetupType()));
		result.setPredetemineTimeSetting(changeWkTypeTimeOutput.getOpPredetemineTimeSetting());
		result.setWorkTypeCD(Optional.of(workTypeCD));
		result.setWorkTimeCD(Optional.ofNullable(workTimeCD));
		result.setReflectWorkChangeApp(appWorkChangeSettingOutput.getAppWorkChangeReflect());
		return result;
	}

	@Override
	public List<WorkType> getWorkTypeLst(AppEmploymentSet appEmploymentSet) {
		String companyID = AppContexts.user().companyId();
		if(appEmploymentSet == null) {
			return workTypeRepository.findNotDeprecated(companyID);
		}
		// INPUT．「雇用別申請承認設定．申請別対象勤務種類」を確認する
		Optional<TargetWorkTypeByApp> opTargetWorkTypeByApp = appEmploymentSet.getTargetWorkTypeByAppLst().stream()
				.filter(x -> x.getAppType() == ApplicationType.WORK_CHANGE_APPLICATION).findAny();
		if(!opTargetWorkTypeByApp.isPresent()) {
			// ドメインモデル「勤務種類」を取得して返す
			return workTypeRepository.findNotDeprecated(companyID);
		}
		// 「表示する勤務種類を設定する」と「勤務種類リスト」をチェックする
		if(!opTargetWorkTypeByApp.get().isDisplayWorkType() || CollectionUtil.isEmpty(opTargetWorkTypeByApp.get().getWorkTypeLst())) {
			// ドメインモデル「勤務種類」を取得して返す
			return workTypeRepository.findNotDeprecated(companyID);
		}
		// INPUT．「雇用別申請承認設定．申請別対象勤務種類．勤務種類リスト」を返す
		List<String> workTypeCDLst = opTargetWorkTypeByApp.get().getWorkTypeLst();
		return workTypeRepository.findNotDeprecatedByListCode(companyID, workTypeCDLst);
	}

	@Override
	public WorkTypeWorkTimeSelect initWorkTypeWorkTime(
			String companyID,
			String employeeID,
			GeneralDate date,
			List<WorkType> workTypeLst,
			List<WorkTimeSetting> workTimeLst) {
		// INPUT．「勤務種類リスト」をチェックする
		if (CollectionUtil.isEmpty(workTypeLst)) {
			throw new BusinessException("Msg_43");
		}
		WorkTypeWorkTimeSelect result = new WorkTypeWorkTimeSelect();
		// 社員の労働条件を取得する
		Optional<WorkingConditionItem> personalLablorCodition = workingConditionItemRepository
				.getBySidAndStandardDate(employeeID, date);
		
		
		
		if(!personalLablorCodition.isPresent()) {
			// OUTPUT「選択する勤務種類」と「選択する就業時間帯」をセットする
			result.setWorkType(workTypeLst.get(0));
			result.setWorkTime(CollectionUtil.isEmpty(workTimeLst) ? Optional.empty() : Optional.of(workTimeLst.get(0)));
			// OUTPUT「選択する勤務種類」と「選択する就業時間帯」を返す
			return result;
		}
		
		WorkingConditionItem workingConditionItem = personalLablorCodition.get();
		
		// $勤務情報　＝　労働条件項目.区分別勤務.出勤日の勤務情報を取得する()
		WorkInformation workInfo = workingConditionItem.getWorkCategory().getWorkInformationWorkDay();
		
		// $勤務情報.勤務種類コードがINPUT．「勤務種類リスト」にあるかをチェックする
		String workType = workInfo.getWorkTypeCode().v();
		if (workTypeLst.stream().anyMatch(x -> x.getWorkTypeCode().v().equals(workType))) {
			
			// $勤務情報.勤務種類コードを選択する
			WorkType selectedWorkType = 
					workTypeLst.stream()
							   .filter(
								   x -> x.getWorkTypeCode().v().equals(workType))
						  	   .findFirst().orElse(null);
			
			result.setWorkType(selectedWorkType);
			// 12.マスタ勤務種類、就業時間帯データをチェック
			CheckWorkingInfoResult checkWorkingInfoResult = 
					otherCommonAlgorithm.checkWorkingInfo(
							companyID,
							workingConditionItem.getWorkCategory().getWorkType().getWeekdayTimeWTypeCode().v(),
							null);
			if (checkWorkingInfoResult.isWkTypeError()) {
				
				// OUTPUT「選択する勤務種類」と「選択する就業時間帯」をセットする
				result.setWorkType(workTypeLst.get(0));
				result.setWorkTime(CollectionUtil.isEmpty(workTimeLst) ? Optional.empty() : Optional.of(workTimeLst.get(0)));
				
			}
			
		} else {
			// OUTPUT「選択する勤務種類」をセットする
			result.setWorkType(workTypeLst.get(0));
		}
		
		// $勤務情報.就業時間帯コードがINPUT．「就業時間帯リスト」にあるかをチェックする
		String workTime = workInfo.getWorkTimeCodeNotNull().map(x -> x.v()).orElse(null);
		
		if (workTime != null && workTimeLst.stream().anyMatch(x -> x.getWorktimeCode().v().equals(workTime))) {
			// $勤務情報.就業時間帯コードを選択する
			WorkTimeSetting selectedWorkTime = 
					workTimeLst.stream()
					.filter(x -> x.getWorktimeCode().v().equals(workTime))
					.findFirst()
					.orElse(null);
			result.setWorkTime(Optional.ofNullable(selectedWorkTime));
			// 12.マスタ勤務種類、就業時間帯データをチェック
			CheckWorkingInfoResult checkWorkingInfoResult = 
					otherCommonAlgorithm.checkWorkingInfo(
							companyID,
							null,
							workingConditionItem.getWorkCategory()
												.getWorkTime()
												.getWeekdayTime()
												.getWorkTimeCode()
												.map(x -> x.v())
												.orElse(null)
												);
			
			if (checkWorkingInfoResult.isWkTimeError()) {
				// OUTPUT「選択する就業時間帯」をセットする
				result.setWorkTime(CollectionUtil.isEmpty(workTimeLst) ? Optional.empty() : Optional.of(workTimeLst.get(0)));
			
			}
			
			
		} else {
			// OUTPUT「選択する就業時間帯」をセットする
			result.setWorkTime(CollectionUtil.isEmpty(workTimeLst) ? Optional.empty() : Optional.of(workTimeLst.get(0)));
			// OUTPUT「選択する勤務種類」と「選択する就業時間帯」を返す
			return result;
		}
		
		
		// OUTPUT「選択する勤務種類」と「選択する就業時間帯」を返す
		return result;
	}

	@Override
	public ChangeWkTypeTimeOutput changeWorkTypeWorkTime(String companyID, String workTypeCD, Optional<String> workTimeCD,
			AppWorkChangeSet appWorkChangeSet) {
		ChangeWkTypeTimeOutput result = new ChangeWkTypeTimeOutput();
		result.setOpPredetemineTimeSetting(Optional.empty());
		// 就業時間帯の必須チェック
		SetupType setupType = basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCD);
		result.setSetupType(setupType);
		// INPUT．「勤務変更申請設定」をチェックする
		if(workTimeCD.isPresent() && appWorkChangeSet.getInitDisplayWorktimeAtr() == InitDisplayWorktimeAtr.FIXEDTIME) {
			// ドメインモデル所定時間設定」を取得する
			Optional<PredetemineTimeSetting> opPredetemineTimeSetting = 
					predetemineTimeSettingRepository.findByWorkTimeCode(companyID, workTimeCD.get());
			result.setOpPredetemineTimeSetting(opPredetemineTimeSetting);
		}	
		// 取得した「必須任意不要区分」と「所定時間設定」を返す
		return result;
	}

	@Override
	public AppWorkChangeDispInfo changeAppDate(String companyID, List<GeneralDate> dateLst,
			AppWorkChangeDispInfo appWorkChangeDispInfo) {
		// 申請表示情報(基準日関係あり)を取得する
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = commonAlgorithm.getAppDispInfoWithDate(companyID,
				ApplicationType.WORK_CHANGE_APPLICATION,
				dateLst,
				appWorkChangeDispInfo.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput(),
				true,
				Optional.empty());
		// 「勤務変更申請の表示情報」を更新して返す
		appWorkChangeDispInfo.getAppDispInfoStartupOutput().setAppDispInfoWithDateOutput(appDispInfoWithDateOutput);
		return appWorkChangeDispInfo;
	}

	@Override
	public WorkChangeCheckRegOutput checkBeforeRegister(String companyID, List<MsgErrorOutput> msgErrorLst, Application application,
			AppWorkChange appWorkChange, AppDispInfoStartupOutput appDispInfoStartupOutput) {
		WorkChangeCheckRegOutput output = new WorkChangeCheckRegOutput();
		// 登録時チェック処理（勤務変更申請）
		this.checkRegisterWorkChange(application, appWorkChange);
		// 勤務種類、就業時間帯チェックのメッセージを表示
		detailBeforeUpdate.displayWorkingHourCheck(
						AppContexts.user().companyId(),
						appWorkChange.getOpWorkTypeCD().map(x -> x.v()).orElse(null),
						appWorkChange.getOpWorkTimeCD().map(x -> x.v()).orElse(null));
		List<GeneralDate> lstDateHd = null;
		if (application.getOpAppStartDate().isPresent() && application.getOpAppEndDate().isPresent()) {
			// 休日の申請日を取得する
			lstDateHd = this.checkHoliday(
					application.getEmployeeID(), 
					new DatePeriod(application.getOpAppStartDate().get().getApplicationDate(), application.getOpAppEndDate().get().getApplicationDate()));
		}
		// 登録時チェック処理（全申請共通）
		List<ConfirmMsgOutput> confirmMsgLst = null;
				newBeforeRegister.processBeforeRegister_New(
				companyID, 
				EmploymentRootAtr.APPLICATION, 
				false, 
				application, 
				null, 
				msgErrorLst, 
				lstDateHd,
				appDispInfoStartupOutput, 
				appWorkChange.getOpWorkTypeCD().isPresent() ? Arrays.asList(appWorkChange.getOpWorkTypeCD().get().v()) : new ArrayList<String>(), 
		        Optional.empty(), 
		        appWorkChange.getOpWorkTimeCD().map(WorkTimeCode::v), 
		        false);
		// 「確認メッセージリスト」を全てと取得した「休日の申請日<List>」を返す
		output.setConfirmMsgLst(confirmMsgLst);
		output.setHolidayDateLst(lstDateHd);
		return output;
	}

	@Override
	public void checkRegisterWorkChange(Application application, AppWorkChange appWorkChange) {
		BundledBusinessException bundledBusinessExceptions = BundledBusinessException.newInstance();
		// アルゴリズム「勤務変更申請就業時間チェックの内容」を実行する
		List<String> errorLst = this.detailWorkHoursCheck(application, appWorkChange);
		if(!CollectionUtil.isEmpty(errorLst)) {
			// 全てのエラーを「エラーリスト」で表示する
			for(String error : errorLst) {
				bundledBusinessExceptions.addMessage(error);
			}
			throw bundledBusinessExceptions;
		}
	}

	@Override
	public List<String> detailWorkHoursCheck(Application application, AppWorkChange appWorkChange) {
		List<String> errorLst = new ArrayList<>();
		// INPUT．「申請．申請開始日」と「INPUT．「申請．申請終了日」をチェックする
		if(application.getOpAppEndDate().isPresent() && application.getOpAppStartDate().isPresent() 
				) {
			if (application.getOpAppStartDate().get().getApplicationDate().after(application.getOpAppEndDate().get().getApplicationDate()))
			// エラーメッセージ(Msg_150)をOUTPUT「エラーリスト」にセットする
			errorLst.add("Msg_150");
		}
		Optional<TimeZoneWithWorkNo> time1 = appWorkChange.getTimeZoneWithWorkNoLst().stream().filter(item -> item.getWorkNo().v() == 1).findFirst();
		
		if (time1.isPresent()) {
			// INPUT．「勤務変更申請．勤務時間開始1」と「INPUT．「勤務変更申請．勤務時間終了1」の大小チェック
			if(time1.get().getTimeZone().getStartTime().v() != null 
					&& time1.get().getTimeZone().getEndTime().v() != null 
					&& time1.get().getTimeZone().getStartTime().v() > time1.get().getTimeZone().getEndTime().v())  {
				// エラーメッセージ(Msg_579)をOUTPUT「エラーリスト」にセットする
				errorLst.add("Msg_579");
			}
		}
		
		Optional<TimeZoneWithWorkNo> time2 = appWorkChange.getTimeZoneWithWorkNoLst().stream().filter(item -> item.getWorkNo().v() == 2).findFirst();
		
		if (time2.isPresent()) {
			// INPUT．「勤務変更申請．勤務時間開始2」と「INPUT．「勤務変更申請．勤務時間終了2」の大小チェック
			if(time2.get().getTimeZone().getStartTime().v() != null 
					&& time2.get().getTimeZone().getEndTime().v() != null 
					&& time2.get().getTimeZone().getStartTime().v() > time2.get().getTimeZone().getEndTime().v())  {
				// エラーメッセージ(Msg_580)をOUTPUT「エラーリスト」にセットする
				errorLst.add("Msg_580");
			}
		}
		if (time1.isPresent() && time2.isPresent()) {
			// INPUT．「勤務変更申請．勤務時間終了1」と「INPUT．「勤務変更申請．勤務時間開始2」の大小チェック
			if(time1.get().getTimeZone().getEndTime().v() != null 
					&& time2.get().getTimeZone().getStartTime().v() != null 
					&& time1.get().getTimeZone().getEndTime().v() > time2.get().getTimeZone().getStartTime().v())  {
				// エラーメッセージ(Msg_581)をOUTPUT「エラーリスト」にセットする
				errorLst.add("Msg_581");
			}
		}
		if (time1.isPresent()) {
			// INPUT．「勤務変更申請．勤務時間開始1」の入力範囲チェック
			if(time1.get().getTimeZone().getStartTime().v() != null 
					&& time1.get().getTimeZone().getStartTime().v() < 5 * 60) {
				// エラーメッセージ(Msg_307)をOUTPUT「エラーリスト」にセットする
				errorLst.add("Msg_307");
			}
			// INPUT．「勤務変更申請．勤務時間終了1」の入力範囲チェック
			if(time1.get().getTimeZone().getEndTime().v() != null 
					&& time1.get().getTimeZone().getEndTime().v() > 29 * 60) {
				// エラーメッセージ(Msg_307)をOUTPUT「エラーリスト」にセットする
				errorLst.add("Msg_307");
			}
		}
		
		return errorLst.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<GeneralDate> checkHoliday(String employeeID, DatePeriod period) {
		// Output．日付一覧<List>　＝　Empty
		List<GeneralDate> result = new ArrayList<>();
		// 申請期間から休日の申請日を取得する
		result = otherCommonAlgorithm.lstDateIsHoliday(employeeID, period, Collections.emptyList());

		// http://192.168.50.4:3000/issues/120857
        /*
         * if (result.size() == period.datesBetween().size()) { // 日付一覧(output)の件数 > 0
         * String dateListString = "";
         * 
         * for (int i = 0; i < result.size(); i++) { if (dateListString != "") {
         * dateListString += "、"; } dateListString +=
         * result.get(i).toString("yyyy/MM/dd"); } throw new
         * BusinessException("Msg_1459",dateListString); }
         */
        return result;
	}

	@Override
	public AppWorkChangeDetailOutput startDetailScreen(String companyID, String appID, AppDispInfoStartupOutput appDispInfoStartupOutput) {
		AppWorkChangeDetailOutput result = new AppWorkChangeDetailOutput();
		AppWorkChangeDispInfo appWorkChangeDispInfo = new AppWorkChangeDispInfo();
		
		// ドメインモデル「勤務変更申請」より取得する (Lấy từ domain 「勤務変更申請」)
		AppWorkChange appWorkChange = appWorkChangeRepositoryNew.findbyID(companyID, appID).get();

		
		// 勤務変更申請設定を取得する
		AppWorkChangeSettingOutput appWorkChangeSettingOutput = this.getAppWorkChangeSettingOutput(companyID);
		
		// 勤務種類を取得する
		Optional<AppEmploymentSet> opAppEmploymentSet = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet();
		List<WorkType> workTypeLst = Collections.emptyList();
		if (opAppEmploymentSet.isPresent()) {
			workTypeLst = this.getWorkTypeLst(opAppEmploymentSet.get());			
		}else {
			workTypeLst = this.getWorkTypeLst(null);
		}
		
		// 勤務種類・就業時間帯を変更する時 #111189
		String workTypeCD = appWorkChange.getOpWorkTypeCD().isPresent() ? appWorkChange.getOpWorkTypeCD().get().v() : null;
		String workTimeCD = appWorkChange.getOpWorkTimeCD().isPresent() ? appWorkChange.getOpWorkTimeCD().get().v() : null;
		ChangeWkTypeTimeOutput changeWkTypeTimeOutput =
				this.changeWorkTypeWorkTime(companyID, workTypeCD, Optional.ofNullable(workTimeCD), appWorkChangeSettingOutput.getAppWorkChangeSet());
		// 申請中の勤務種類・就業時間帯を取得する
		Optional<List<WorkTimeSetting>> opWorkTimeLst = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst();
		WorkInfoListOutput workinfos = this.commonAlgorithm.getWorkInfoList(companyID, workTypeCD, Optional.ofNullable(workTimeCD), workTypeLst, opWorkTimeLst.isPresent() ? opWorkTimeLst.get() : new ArrayList<WorkTimeSetting>());
		
		WorkInformationForApplication workInformationForApplication = null;
		if (workTimeCD != null || workTypeCD != null) {
		    workInformationForApplication = new WorkInformationForApplication(
		            workTimeCD != null ? new WorkTimeCode(workTimeCD) : null, 
		            workTypeCD != null ? new WorkTypeCode(workTypeCD) : null);
		}
		
		// 取得した情報をOUTPUT「勤務変更申請の表示情報」にセットする
		appDispInfoStartupOutput.getAppDispInfoWithDateOutput().setOpWorkTimeLst(Optional.of(workinfos.getWorkTimes()));
		appWorkChangeDispInfo.setWorkInformationForApplication(Optional.ofNullable(workInformationForApplication));
		appWorkChangeDispInfo.setAppDispInfoStartupOutput(appDispInfoStartupOutput);
		appWorkChangeDispInfo.setAppWorkChangeSet(appWorkChangeSettingOutput.getAppWorkChangeSet());
		appWorkChangeDispInfo.setReflectWorkChangeApp(appWorkChangeSettingOutput.getAppWorkChangeReflect());
		appWorkChangeDispInfo.setWorkTypeLst(workinfos.getWorkTypes());
		appWorkChangeDispInfo.setPredetemineTimeSetting(changeWkTypeTimeOutput.getOpPredetemineTimeSetting());
		appWorkChangeDispInfo.setSetupType(Optional.ofNullable(changeWkTypeTimeOutput.getSetupType()));
		// 「勤務変更申請の表示情報」と「勤務変更申請」を返す
		result.setAppWorkChangeDispInfo(appWorkChangeDispInfo);
		result.setAppWorkChange(appWorkChange);
		return result;
	}

	@Override
	public List<ConfirmMsgOutput> checkBeforeUpdate(String companyID, Application application,
			AppWorkChange appWorkChange, boolean agentAtr, AppDispInfoStartupOutput appDispInfoStartupOutput, AppWorkChangeDispInfo appWorkChangeDispInfo) {
		List<ConfirmMsgOutput> result = new ArrayList<>();
		String workTypeCD = null;
		String workTimeCD = null;
		if (appWorkChangeDispInfo.getWorkInformationForApplication().isPresent()) {
			if (appWorkChange.getOpWorkTypeCD().isPresent()) {
				workTypeCD = appWorkChange.getOpWorkTypeCD().get().v();
				if (appWorkChangeDispInfo.getWorkInformationForApplication().get().getWorkTypeCode() != null) {
					String workType = appWorkChangeDispInfo.getWorkInformationForApplication().get().getWorkTypeCode().v();
					if (workType.equals(workTypeCD)) {
						workTypeCD = null;
					}
				}
			}
			
			if (appWorkChange.getOpWorkTimeCD().isPresent()) {
				workTimeCD = appWorkChange.getOpWorkTimeCD().get().v();
				if (appWorkChangeDispInfo.getWorkInformationForApplication().get().getWorkTimeCode() != null) {
					String workTime = appWorkChangeDispInfo.getWorkInformationForApplication().get().getWorkTimeCode().v();
					if (workTime.equals(workTimeCD)) {
						workTimeCD = null;
					}
				}
			}
		}
		
		
		// 詳細画面の登録時チェック処理（全申請共通）
		detailBeforeUpdate.processBeforeDetailScreenRegistration(
				companyID, 
				application.getEmployeeID(), 
				application.getAppDate().getApplicationDate(), 
				EmploymentRootAtr.APPLICATION.value, 
				application.getAppID(), 
				application.getPrePostAtr(), 
				application.getVersion(), 
				workTypeCD , 
				workTimeCD,
				appDispInfoStartupOutput, 
				appWorkChange.getOpWorkTypeCD().isPresent() ? Arrays.asList(appWorkChange.getOpWorkTypeCD().get().v()) : new ArrayList<String>(), 
				Optional.empty(), 
				false, 
				appWorkChange.getOpWorkTypeCD().map(WorkTypeCode::v), 
				appWorkChange.getOpWorkTimeCD().map(WorkTimeCode::v));
		// 登録時チェック処理（勤務変更申請）
		this.checkRegisterWorkChange(application, appWorkChange);
		return result;
	}
	
	@Override
	public AppWorkChangeOutput getAppWorkChangeOutput(boolean mode, String companyId,
			Optional<String> employeeId, Optional<List<GeneralDate>> dates, Optional<AppWorkChangeDispInfo> appWorkChangeDispInfo,
			Optional<AppWorkChange> appWorkChange) {
		AppWorkChangeOutput appWorkChangeOutput = new AppWorkChangeOutput();	
		// new mode
		if (mode) {
			List<String> sids = new ArrayList<String>();
			String sid = employeeId.orElse(null);
			if (!StringUtils.isBlank(sid)) {
				sids.add(sid);
			}
			AppDispInfoStartupOutput appDispInfoStartupOutput = commonAlgorithm.getAppDispInfoStart(
					companyId,
					ApplicationType.WORK_CHANGE_APPLICATION,
					sids,
					dates.orElse(Collections.emptyList()),
					mode,
					Optional.empty(),
					Optional.empty());
			// 申請共通起動処理
//			AppDispInfoStartupOutput appDispInfoStartupOutput = algorithmMobile.appCommonStartProcess(
//					mode, 
//					companyId, 
//					employeeId.isPresent() ? employeeId.get() : null, 
//					ApplicationType.WORK_CHANGE_APPLICATION, 
//					Optional.ofNullable(null), 
//					dates.isPresent() ? dates.get(): null, 
//					Optional.ofNullable(null));
			// 勤務変更申請画面初期（新規）
			AppWorkChangeDispInfo appWorkChangeDisp = this.getAppWorkChangeDisInfo(
					companyId, 
					employeeId.isPresent() ? employeeId.get() : null, 
					dates.isPresent() ? dates.get() : null,
					appDispInfoStartupOutput);
			appWorkChangeOutput.setAppWorkChangeDispInfo(appWorkChangeDisp);
			appWorkChangeOutput.setAppWorkChange(Optional.ofNullable(null));
			
		}else {
			if (appWorkChangeDispInfo.isPresent()) {
				appWorkChangeOutput.setAppWorkChangeDispInfo(appWorkChangeDispInfo.get());
			}
			appWorkChangeOutput.setAppWorkChange(appWorkChange);

		}
		return appWorkChangeOutput;
	}
	
	@Override
	public AppWorkChangeDispInfo getAppWorkChangeDisInfo(String companyId, String employeeId, List<GeneralDate> dates,
			AppDispInfoStartupOutput appDispInfoStartupOutput) {
		AppWorkChangeDispInfo appWorkChangeDispInfo = new AppWorkChangeDispInfo();
		
		// 勤務変更申請設定を取得する
		AppWorkChangeSettingOutput appWorkChangeSettingOutput = this.getAppWorkChangeSettingOutput(companyId);
		
		// 勤務種類を取得する
		Optional<AppEmploymentSet> appEmploymentSetting = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet();
		List<WorkType> workTypes = this.getWorkTypeLst(appEmploymentSetting.isPresent() ? appEmploymentSetting.get() : null);
		
		
		
		// 勤務種類・就業時間帯の初期選択項目を取得する
		WorkTypeWorkTimeSelect woSelect = this.initWorkTypeWorkTime(
				companyId,
				employeeId,
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate(), 
				workTypes,
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().isPresent() ? appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().get() : null);
		
		// 勤務種類・就業時間帯を変更する時
		Optional<String> workTimeOp = Optional.empty();
		if (woSelect != null) {
			if (woSelect.getWorkTime().isPresent()) {
				if (woSelect.getWorkTime().get().getWorktimeCode() != null) {
					workTimeOp = Optional.ofNullable(woSelect.getWorkTime().get().getWorktimeCode().v());
				}
				
			}
			
		}
		ChangeWkTypeTimeOutput changeWkTypeTimeOutput = this.changeWorkTypeWorkTime(
				companyId,
				woSelect.getWorkType().getWorkTypeCode().v(),
				workTimeOp,
				appWorkChangeSettingOutput.getAppWorkChangeSet());
		
		
		// assign value to output
		
		appWorkChangeDispInfo.setAppWorkChangeSet(appWorkChangeSettingOutput.getAppWorkChangeSet());
		
		appWorkChangeDispInfo.setAppDispInfoStartupOutput(appDispInfoStartupOutput);
		appWorkChangeDispInfo.setWorkTypeLst(workTypes);
		appWorkChangeDispInfo.setSetupType(Optional.ofNullable(changeWkTypeTimeOutput.getSetupType()));
		appWorkChangeDispInfo.setPredetemineTimeSetting(changeWkTypeTimeOutput.getOpPredetemineTimeSetting());
		appWorkChangeDispInfo.setWorkTypeCD(Optional.ofNullable(woSelect.getWorkType().getWorkTypeCode().v()));
		appWorkChangeDispInfo.setWorkTimeCD(workTimeOp);
		
		// 勤務変更申請の表示情報．勤務変更申請の反映 = 取得した「勤務変更申請の反映」
		appWorkChangeDispInfo.setReflectWorkChangeApp(appWorkChangeSettingOutput.getAppWorkChangeReflect());
		
		return appWorkChangeDispInfo;
	}

	@Override
	public AppWorkChangeSettingOutput getAppWorkChangeSettingOutput(String companyId) {
		AppWorkChangeSettingOutput appWorkChangeSettingOutput = new AppWorkChangeSettingOutput();
		
		// ドメインモデル「勤務変更申請設定」を取得する
		Optional<AppWorkChangeSet> appWorkChangeSet = appWorkChangeSetRepoNew.findByCompanyId(companyId);
		// ドメインモデル「勤務変更申請の反映」を取得する
		// table is not existed
		ReflectWorkChangeApp appWorkChangeReflect = new ReflectWorkChangeApp();
		appWorkChangeReflect.setCompanyID(companyId);
		appWorkChangeReflect.setWhetherReflectAttendance(NotUseAtr.valueOf(1));
		Optional<ReflectWorkChangeApp> reflectOptional = appWorkChangeSetRepoNew.findByCompanyIdReflect(companyId);
		appWorkChangeReflect = reflectOptional.isPresent() ? reflectOptional.get() : null;
		
		appWorkChangeSettingOutput.setAppWorkChangeSet(appWorkChangeSet.isPresent() ? appWorkChangeSet.get() : null);
		
		appWorkChangeSettingOutput.setAppWorkChangeReflect(appWorkChangeReflect);
		
		return appWorkChangeSettingOutput;
	}

	
	@Override
	public WorkChangeCheckRegOutput checkBeforeRegister(Boolean mode, String companyId, Application application,
			AppWorkChange appWorkChange, List<MsgErrorOutput> msgErrorLst, AppDispInfoStartupOutput appDispInfoStartupOutput, AppWorkChangeDispInfo appWorkChangeDispInfo) {
		WorkChangeCheckRegOutput workChangeCheckRegOutput = new WorkChangeCheckRegOutput();
//		INPUT．「画面モード」をチェックする
		if (mode ) {
//			登録前のエラーチェック処理
			workChangeCheckRegOutput = this.checkBeforeRegister(companyId, msgErrorLst, application, appWorkChange, appDispInfoStartupOutput);
		}else {
//			更新前のエラーチェック処理
			workChangeCheckRegOutput.setConfirmMsgLst(this.checkBeforeUpdate(companyId, application, appWorkChange, false, appDispInfoStartupOutput, appWorkChangeDispInfo));
		}

		return workChangeCheckRegOutput;
	}

}
