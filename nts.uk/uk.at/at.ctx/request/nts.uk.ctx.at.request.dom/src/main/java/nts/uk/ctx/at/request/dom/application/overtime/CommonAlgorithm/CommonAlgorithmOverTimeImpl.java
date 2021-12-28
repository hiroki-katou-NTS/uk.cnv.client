package nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.request.dom.application.overtime.*;
import org.apache.commons.lang3.StringUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BundledBusinessException;
import nts.arc.error.BusinessException;
import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDate;
import nts.arc.time.clock.ClockHourMinute;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.UseAtr;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.CommonOvertimeHoliday;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.Time36UpperLimitCheck;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.OverTimeWorkHoursOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.TimePlaceOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.InitWkTypeWkTimeOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CalculatedFlag;
import nts.uk.ctx.at.request.dom.application.overtime.service.DisplayInfoOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.service.OverTimeContent;
import nts.uk.ctx.at.request.dom.application.overtime.service.OvertimeService;
import nts.uk.ctx.at.request.dom.application.overtime.service.SelectWorkOutput;
import nts.uk.ctx.at.request.dom.application.overtime.service.WorkHours;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.RecordDate;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.FlexWorkAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeQuotaSetUse;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.ApplicationDetailSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.AtWorkAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeLeaveAppCommonSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.TargetWorkTypeByApp;
import nts.uk.ctx.at.request.dom.workrecord.dailyrecordprocess.dailycreationwork.BreakTimeZoneSetting;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrame;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrameRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.AppReflectOtHdWork;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.AppReflectOtHdWorkRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRootRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeUseSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.Time36AgreementError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.Time36AgreementErrorAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.Time36ErrorInforList;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.DuplicateStateAtr;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.DuplicationStatusOfTimeZone;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.RangeOfDayTimeZoneService;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingService;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.internal.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.worktype.DeprecateClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class CommonAlgorithmOverTimeImpl implements ICommonAlgorithmOverTime {
	
	@Inject
	private OvertimeWorkFrameRepository overtimeWorkFrameRepository;
	
	@Inject
	private WorkingConditionRepository workingConditionRepository;
	
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@Inject
	private DivergenceTimeRootRepository divergenceTimeRoots;
	
	@Inject
	private OvertimeAppSetRepository overtimeAppSetRepository;
	
	@Inject
	private CommonOvertimeHoliday commonOvertimeholiday;
	
	@Inject
	private AppReflectOtHdWorkRepository appReflectOtHdWorkRepository;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private CommonOvertimeHoliday commonOverTime;
	
	@Inject
	public RangeOfDayTimeZoneService rangeOfDayTimeZoneService;
	
	@Inject 
	private WorkTimeSettingService workTimeSettingService;
	
	@Inject 
	private OvertimeService overTimeService;

	@Inject
	private DetailBeforeUpdate detailBeforeUpdate;
	
	@Inject
	private Time36UpperLimitCheck time36UpperLimitCheck;
	
	@Override
	public QuotaOuput getOvertimeQuotaSetUse(
			String companyId,
			String employeeId,
			GeneralDate date,
			OvertimeAppAtr overTimeAtr,
			OvertimeAppSet overtimeAppSet) {
		QuotaOuput output = new QuotaOuput();
		// 社員の労働条件を取得する
		Optional<WorkingConditionItem> workingConditionItemOp = WorkingConditionService.findWorkConditionByEmployee(createRequireM1(), employeeId, date);
		if (!workingConditionItemOp.isPresent()) return new QuotaOuput(false, Collections.emptyList()); // need to QA if workingConditionItemOp is empty
		WorkingConditionItem workingConditionItem = workingConditionItemOp.get();
		// 取得したドメインモデル「労働条件項目」．労働制を確認する
		List<OvertimeWorkFrame> frames = overtimeWorkFrameRepository.getOvertimeWorkFrameByFrameByCom(companyId, NotUseAtr.USE.value);
		List<Integer> listFNo = Collections.emptyList();
		List<OvertimeQuotaSetUse> overtimeQuotaSet;
		if (workingConditionItem.getLaborSystem() == WorkingSystem.FLEX_TIME_WORK) { // 労働制 = フレックス時間勤務(LaborSystem = FlexTimeWork)
			// OUTPUT「利用する残業枠」を更新する
			output.setFlexTimeClf(true);
			// ドメインモデル「残業枠」を取得
			/**
			 * ⇒「残業申請設定」の条件は：「残業枠設定．残業申請区分」 = INPUT．「残業申請区分」
　　　　　　　　　　　　　　　　　　「残業枠設定．フレックス勤務者区分」 = フレックス時間勤務
			 */
			overtimeQuotaSet = overtimeAppSet.getOvertimeQuotaSet().stream()
					.filter(x -> x.getFlexWorkAtr() == FlexWorkAtr.FLEX_TIME && x.getOvertimeAppAtr() == overTimeAtr)
					.collect(Collectors.toList());
			
			
			
		} else {
			// OUTPUT「利用する残業枠」を更新する
			output.setFlexTimeClf(false);
			/**
			 * ⇒「残業申請設定」の条件は：「残業枠設定．残業申請区分」 = INPUT．「残業申請区分」
　　　　　　　　　　　　　　　　　　「残業枠設定．フレックス勤務者区分」 = フレックス時間勤務以外
			 */
			overtimeQuotaSet = overtimeAppSet.getOvertimeQuotaSet().stream()
					.filter(x -> x.getFlexWorkAtr() == FlexWorkAtr.OTHER && x.getOvertimeAppAtr() == overTimeAtr)
					.collect(Collectors.toList());
			
		}
		
		if (!CollectionUtil.isEmpty(overtimeQuotaSet)) {

			listFNo = overtimeQuotaSet.stream()
					.flatMap(x -> x.getTargetOvertimeLimit().stream().map(y -> y.v()))
					.distinct()
					.collect(Collectors.toList());
		}
		if (CollectionUtil.isEmpty(listFNo)) {
			frames = Collections.emptyList();
		} else {
			final List<Integer> nos = listFNo;
			frames = frames.stream()
							.filter(x -> nos.contains(x.getOvertimeWorkFrNo().v().intValue()))
							.collect(Collectors.toList());
		}
		
		// OUTPUT「利用する残業枠」を更新して返す
		output.setOverTimeQuotaList(frames);
		
		
		return output;
	}
	
	
	private WorkingConditionService.RequireM1 createRequireM1() {
		return new WorkingConditionService.RequireM1() {
			
			@Override
			public Optional<WorkingConditionItem> workingConditionItem(String historyId) {
				return workingConditionItemRepository.getByHistoryId(historyId);
			}
			
			@Override
			public Optional<WorkingCondition> workingCondition(String companyId, String employeeId, GeneralDate baseDate) {
				return workingConditionRepository.getBySidAndStandardDate(companyId, employeeId, baseDate);
			}
		};
	}


	@Override
	public List<WorkType> getWorkType(Optional<AppEmploymentSet> appEmploymentSettingOp) {
		List<WorkType> workTypes = new ArrayList<>();
		Boolean isC1 = false;
		Boolean isC2 = false;
		Boolean isC3 = false;
		// INPUT．「雇用別申請承認設定」を確認する
		if (appEmploymentSettingOp.isPresent()) {
			AppEmploymentSet appEmploymentSetting = appEmploymentSettingOp.get();
			isC1 = !CollectionUtil.isEmpty(appEmploymentSetting.getTargetWorkTypeByAppLst());
			Optional<TargetWorkTypeByApp> targetWorkTypeByAppOp = appEmploymentSetting.getTargetWorkTypeByAppLst().stream().filter(x -> x.getAppType() == ApplicationType.OVER_TIME_APPLICATION).findFirst();
			if (isC1 && targetWorkTypeByAppOp.isPresent()) {
				isC2 = targetWorkTypeByAppOp.get().isDisplayWorkType();
				isC3 = !CollectionUtil.isEmpty(targetWorkTypeByAppOp.get().getWorkTypeLst());
			}
			// 「申請別対象勤務種類」をチェックする
			if (isC1 && isC2 && isC3 && targetWorkTypeByAppOp.isPresent()) {
				// ドメインモデル「勤務種類」を取得して返す
				List<WorkType> listWorkType = workTypeRepository.findByCidAndWorkTypeCodes(AppContexts.user().companyId(), targetWorkTypeByAppOp.get().getWorkTypeLst());
				if (!CollectionUtil.isEmpty(listWorkType)) {
					workTypes = listWorkType.stream().filter(x -> x.getDeprecate() == DeprecateClassification.NotDeprecated).sorted((x1, x2) -> x1.getWorkTypeCode().v().compareTo(x2.getWorkTypeCode().v())).collect(Collectors.toList());
				}
			}
			
		} 
		
		if (!(isC1 && isC2 && isC3)) {
			// ドメインモデル「勤務種類」を取得して返す
			workTypes = workTypeRepository.findWorkType(AppContexts.user().companyId(), 0, allDayAtrs(), halfAtrs())
										  .stream()
										  .sorted((x1, x2) -> x1.getWorkTypeCode().v().compareTo(x2.getWorkTypeCode().v()))
										  .collect(Collectors.toList());
		}
		// // 取得した「勤務種類」をチェック
		// if (CollectionUtil.isEmpty(workTypes)) throw new BusinessException("Msg_1567");
		return workTypes;
	}
	
	/**
	 * // １日の勤務＝以下に該当するもの
	 * 　出勤、休出、振出、連続勤務
	 * @return
	 */
	private List<Integer> allDayAtrs(){
		
		List<Integer> allDayAtrs = new ArrayList<>();
		//出勤
		allDayAtrs.add(0);
		//休出
		// allDayAtrs.add(11);
		//振出
		allDayAtrs.add(7);
		// 連続勤務
		allDayAtrs.add(10);
		return allDayAtrs;
	}
	/**
	 * 午前 また 午後 in (休日, 振出, 年休, 出勤, 特別休暇, 欠勤, 代休, 時間消化休暇)
	 * @return
	 */
	private List<Integer> halfAtrs(){
		List<Integer> halfAtrs = new ArrayList<>();
		// 休日
		// halfAtrs.add(1);
		// 振出
		halfAtrs.add(7);
		// 年休
		// halfAtrs.add(2);
		// 出勤
		halfAtrs.add(0);
		//特別休暇
		// halfAtrs.add(4);
		// 欠勤
		// halfAtrs.add(5);
		// 代休
		// halfAtrs.add(6);
		//時間消化休暇
		// halfAtrs.add(9);
		return halfAtrs;
	}


	@Override
	public InfoBaseDateOutput getInfoBaseDate(
			String companyId,
			String employeeId,
			GeneralDate date,
			OvertimeAppAtr overTimeAtr,
			List<WorkTimeSetting> workTime,
			Optional<AppEmploymentSet> appEmploymentSettingOp,
			OvertimeAppSet overtimeAppSet) {
		InfoBaseDateOutput output = new InfoBaseDateOutput();
		// 指定社員の申請残業枠を取得する
		QuotaOuput quotaOuput = this.getOvertimeQuotaSetUse(companyId, employeeId, date, overTimeAtr, overtimeAppSet);
		output.setQuotaOutput(quotaOuput);
		List<WorkType> worktypes = Collections.emptyList();
		if (overtimeAppSet.getApplicationDetailSetting().getTimeCalUse() == NotUseAtr.USE) {
			// 07_勤務種類取得
			worktypes = this.getWorkType(appEmploymentSettingOp);			
		}
		output.setWorktypes(worktypes);
		// // INPUT．「就業時間帯の設定<List>」をチェックする
		// if (workTime.isEmpty()) throw new BusinessException("Msg_1568");
		return output;
	}


	@Inject
	private DivergenceReasonInputMethodI divergenceReasonInputMethod;

	@Override
	public ReasonDissociationOutput getInfoNoBaseDate(
			String companyId,
			ApplicationType appType,
			Optional<OvertimeAppAtr> ovetTimeAtr,
			OvertimeLeaveAppCommonSet overtimeLeaveAppCommonSet) {
		ReasonDissociationOutput output = new ReasonDissociationOutput(); // emptyを返す
		// @「登録時の乖離時間チェック」を確認する
		if (overtimeLeaveAppCommonSet.getCheckDeviationRegister() == NotUseAtr.NOT_USE) // しない
			return output; // emptyを返す
		// する
		// INPUT．「申請種類」」をチェックする
		if (appType == ApplicationType.OVER_TIME_APPLICATION || appType == ApplicationType.HOLIDAY_WORK_APPLICATION) {// 残業申請
																														// OR
																														// 休出時間申請

			/*
			 * 
			 * INPUT．「申請種類」= 残業申請 AND INPUT．「残業申請区分」= 早出残業 ⇒ List<乖離時間NO> = 1 INPUT．「申請種類」=
			 * 残業申請 AND INPUT．「残業申請区分」= 通常残業 ⇒ List<乖離時間NO> = 2 INPUT．「申請種類」= 残業申請 AND
			 * INPUT．「残業申請区分」= 早出残業・通常残業 ⇒ List<乖離時間NO> = 1,2
			 */
			List<Integer> frames = new ArrayList<Integer>();
			if (appType == ApplicationType.HOLIDAY_WORK_APPLICATION) { // INPUT．「申請種類」= 休出時間申請 ⇒ List<乖離時間NO> = 3
				frames.add(3);
			} else if (appType == ApplicationType.OVER_TIME_APPLICATION) {
				if (ovetTimeAtr.isPresent()) {
					OvertimeAppAtr value = ovetTimeAtr.get();
					// 早出残業
					if (value == OvertimeAppAtr.EARLY_OVERTIME) {
						frames.add(1);
					} else if (value == OvertimeAppAtr.NORMAL_OVERTIME) { // 通常残業
						frames.add(2);
					} else { // 早出残業・通常残業
						frames.add(1);
						frames.add(2);
					}
				}
			}
			// [RQ694]乖離時間Listを取得する
			List<DivergenceTimeRoot> divergenceTimeRootList = divergenceTimeRoots.getList(companyId, frames);
			if (CollectionUtil.isEmpty(divergenceTimeRootList)) {
				return output;
			}
			output.setDivergenceTimeRoots(divergenceTimeRootList);
			// [RQ693]乖離理由の入力方法Listを取得する
			List<Integer> lstNo = divergenceTimeRootList.stream()
														   .filter(x -> x.getDivTimeUseSet() == DivergenceTimeUseSet.USE)
														   .map(y -> y.getDivergenceTimeNo())
														   .collect(Collectors.toList());
			List<DivergenceReasonInputMethod> divergenceReasonInputMethodListFilter = divergenceReasonInputMethod.getData(companyId, lstNo);
			
			if (CollectionUtil.isEmpty(divergenceReasonInputMethodListFilter)) {
				return output; // emptyを返す
			}
			output.setDivergenceReasonInputMethod(divergenceReasonInputMethodListFilter);

		} else {
			return output; // emptyを返す
		}
		// 取得した「乖離時間」と「乖離理由の入力方法」を返す
		return output;
	}


	@Override
	public InfoNoBaseDate getInfoNoBaseDate(
			String companyId,
			String employeeId,
			OvertimeAppAtr overtimeAppAtr) {
		InfoNoBaseDate output = new InfoNoBaseDate();
		// 残業申請設定を取得する
		Optional<OvertimeAppSet> overOptional = overtimeAppSetRepository.findSettingByCompanyId(companyId);
		
		OvertimeAppSet overtimeAppSet = overOptional.orElse(null);
		OvertimeLeaveAppCommonSet overtimeLeaveAppCommonSet = overtimeAppSet.getOvertimeLeaveAppCommonSet();
		// 乖離理由の表示区分を取得する
		ReasonDissociationOutput reasonDissociationOutput = this.getInfoNoBaseDate(companyId, ApplicationType.OVER_TIME_APPLICATION, Optional.ofNullable(overtimeAppAtr), overtimeLeaveAppCommonSet);
		Optional<OverTimeWorkHoursOutput> agreeOverTimeOutputOp = Optional.empty();
		if (overtimeAppSet.getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr() == NotUseAtr.USE) {
			// 01-02_時間外労働を取得
			agreeOverTimeOutputOp = commonOvertimeholiday.getAgreementTime(companyId, employeeId, overtimeLeaveAppCommonSet.getExtratimeExcessAtr(), overtimeLeaveAppCommonSet.getExtratimeDisplayAtr());
			
		}
		
		// ドメインモデル「残業休日出勤申請の反映」を取得する
		Optional<AppReflectOtHdWork> overTimeReflectOp = appReflectOtHdWorkRepository.findByCompanyId(companyId);
		// 取得した情報を「基準日に関係しない情報」にセットして返す
		output.setOverTimeAppSet(overtimeAppSet);
		output.setDivergenceReasonInputMethod(reasonDissociationOutput.getDivergenceReasonInputMethod());
		output.setDivergenceTimeRoot(reasonDissociationOutput.getDivergenceTimeRoots());
		output.setAgreeOverTimeOutput(agreeOverTimeOutputOp);
		if (overTimeReflectOp.isPresent()) {
			output.setOverTimeReflect(overTimeReflectOp.get());
		}
		
		return output;
	}


//	@Override
//	public InfoBaseDateOutput getInfoBaseDate(String companyId,
//			String employeeId,
//			GeneralDate date,
//			OvertimeAppAtr overTimeAtr,
//			List<WorkTimeSetting> workTime,
//			Optional<AppEmploymentSet> appEmploymentSetting) {
//		// TODO Auto-generated method stub
//		return null;
//	}


	@Override
	public InfoWithDateApplication getInfoAppDate(
			String companyId,
			Optional<GeneralDate> dateOp,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			List<WorkType> workTypeLst, 
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			OvertimeAppSet overtimeAppSet) {
		InfoWithDateApplication output = new InfoWithDateApplication();
		String employeeId = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid();
		Optional<AchievementDetail> archievementDetail = Optional.empty();
		if (appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().isPresent()){
			if (!CollectionUtil.isEmpty(appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().get())) {
				ActualContentDisplay actualContentDisplay = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().get().get(0);
				archievementDetail = actualContentDisplay.getOpAchievementDetail();
			}
		}
		// 09_勤務種類就業時間帯の初期選択をセットする
		InitWkTypeWkTimeOutput initWkTypeWkTimeOutput = commonAlgorithm.initWorkTypeWorkTime(employeeId,
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate(),
				dateOp.orElse(null),
				workTypeLst,
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().orElse(Collections.emptyList()),
				archievementDetail.orElse(null));
		if (!StringUtils.isBlank(initWkTypeWkTimeOutput.getWorkTypeCD()) && !StringUtils.isBlank(initWkTypeWkTimeOutput.getWorkTimeCD())) {
			// 16_勤務種類・就業時間帯を選択する
			SelectWorkOutput selectWorkOutput = overTimeService.selectWork(
					companyId,
					employeeId,
					dateOp,
					Optional.ofNullable(initWkTypeWkTimeOutput.getWorkTypeCD()).map(x -> new WorkTypeCode(x)).orElse(null),
					Optional.ofNullable(initWkTypeWkTimeOutput.getWorkTimeCD()).map(x -> new WorkTimeCode(x)).orElse(null),
					startTimeSPR,
					endTimeSPR,
					Optional.ofNullable(appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().map(x -> x.get(0)).orElse(null)),
					overtimeAppSet);
			output.setBreakTime(Optional.ofNullable(selectWorkOutput.getBreakTimeZoneSetting()));
			output.setWorkHours(selectWorkOutput.getWorkHours());
			output.setApplicationTime(Optional.ofNullable(selectWorkOutput.getApplicationTime()));
			
		}
		// 取得情報を「申請日に関係する情報」にセットして返す
		output.setWorkTypeCD(Optional.ofNullable(initWkTypeWkTimeOutput.getWorkTypeCD()));
		output.setWorkTimeCD(Optional.ofNullable(initWkTypeWkTimeOutput.getWorkTimeCD()));
		
		
		return output;
		
	}


	
	
	@Override
	public BreakTimeZoneSetting selectWorkTypeAndTime(
			String companyId,
			WorkTypeCode workTypeCode,
			WorkTimeCode workTimeCode,
			Optional<TimeWithDayAttr> startTimeOp,
			Optional<TimeWithDayAttr> endTimeOp,
			Optional<AchievementDetail> achievementDetail) {
		BreakTimeZoneSetting output = new BreakTimeZoneSetting();
		// INPUT．「実績詳細．打刻実績．休憩時間帯」をチェックする
		List<TimePlaceOutput> breakTimes = Collections.emptyList();
		if (achievementDetail.isPresent()) {
			breakTimes = achievementDetail.get().getStampRecordOutput().getBreakTime();		
		}
		
		if (breakTimes.isEmpty()) {
			// 休憩時間帯を取得する
			List<DeductionTime> timeZones = commonOverTime.getBreakTimes(
					companyId,
					workTypeCode != null ? workTypeCode.v() : null,
					workTimeCode != null ? workTimeCode.v() : null,
					startTimeOp, 
					endTimeOp);
			output.setTimeZones(timeZones);
		} else {
			// 「休憩時間帯設定」<List>を作成する
			List<DeductionTime> timeZones = breakTimes.stream()
					  .map(x -> new DeductionTime(x.getOpStartTime().orElse(null), x.getOpEndTime().orElse(null)))
					  .collect(Collectors.toList());
			output.setTimeZones(timeZones);
			// 勤務時間外の休憩時間を除く
			output = this.createBreakTime(startTimeOp, endTimeOp, output);
		}
		// 「開始時刻」の昇順にソートする(in excel)
		output.setTimeZones(output.getTimeZones().stream().sorted(Comparator.comparing(DeductionTime::getStart)).collect(Collectors.toList()));
		output.setCalculatedFlag(CalculatedFlag.UNCALCULATED);
		return output;
		
	}


	@Override
	public BreakTimeZoneSetting createBreakTime(Optional<TimeWithDayAttr> startTimeOp, Optional<TimeWithDayAttr> endTimeOp,
			BreakTimeZoneSetting breakTimeZoneSetting) {
		// Input．開始時刻とInput．終了時刻をチェック
		if (!startTimeOp.isPresent() || !endTimeOp.isPresent()) { // 開始時刻　OR　終了時刻　が無い場合
			// OUTPUT．「休憩時間帯設定」　＝　INPUT．「休憩時間帯設定」
			return breakTimeZoneSetting;
		} else {
			List<DeductionTime> result = new ArrayList<>();
			for(DeductionTime deductionTime : breakTimeZoneSetting.getTimeZones()){
				// 状態区分　＝　「重複の判断処理」を実行
				TimeWithDayAttr startTime = startTimeOp.get();
				TimeWithDayAttr endTime = endTimeOp.get();
				TimeSpanForCalc timeSpanFirstTime = new TimeSpanForCalc(endTime, startTime);
				TimeSpanForCalc timeSpanSecondTime = new TimeSpanForCalc(deductionTime.getEnd(), deductionTime.getStart());
				// アルゴリズム「時刻入力期間重複チェック」を実行する
				DuplicateStateAtr duplicateStateAtr = this.rangeOfDayTimeZoneService
						.checkPeriodDuplication(timeSpanFirstTime, timeSpanSecondTime);
				// 重複状態区分チェック
				DuplicationStatusOfTimeZone duplicationStatusOfTimeZone = this.rangeOfDayTimeZoneService
						.checkStateAtr(duplicateStateAtr);
				// 取得した状態区分をチェック
				if(duplicationStatusOfTimeZone != DuplicationStatusOfTimeZone.NON_OVERLAPPING){
					result.add(deductionTime);
				}
			}
			breakTimeZoneSetting.setTimeZones(result);
		}
		return breakTimeZoneSetting;
	}

	@Override
	public Optional<WorkHours> initAttendanceTime(
			String companyId,
			Optional<GeneralDate> dateOp,
			OverTimeContent overTimeContent,
			ApplicationDetailSetting applicationDetailSetting
			) {
		Optional<WorkHours> output = Optional.empty();
		// INPUT．「申請日」と「＠時間入力利用区分」をチェックする
		if (!dateOp.isPresent()) {
			return output;
		}
		// 勤務時間を取得する
		output = this.getWorkHours(companyId, overTimeContent, applicationDetailSetting.getAtworkTimeBeginDisp(), applicationDetailSetting);
		
		// INPUT．「申請内容．SPR連携時刻」を確認する
		if ( !overTimeContent.getSPRTime().flatMap(x -> x.getStartTimeOp1()).isPresent() 
				&& !overTimeContent.getSPRTime().flatMap(x -> x.getEndTimeOp1()).isPresent()) {
			return output;
		}

			
				
		
		// OUTPUT「勤務時間」を更新して返す
		if (output.isPresent()) {
			output = Optional.of(new WorkHours());
		}
		output.get().setStartTimeOp1(overTimeContent.getSPRTime().flatMap(x -> x.getStartTimeOp1()));
		output.get().setEndTimeOp1(overTimeContent.getSPRTime().flatMap(x -> x.getEndTimeOp1()));
		
		
		return output;
	}
	@Override
	public Optional<WorkHours> getWorkHours(
			String companyId,
			OverTimeContent overTimeContent,
			AtWorkAtr atworkTimeBeginDisp,
			ApplicationDetailSetting applicationDetailSetting
			) {
		Optional<WorkHours> workHoursOp = Optional.empty();
		// 「出退勤時刻初期表示区分」をチェックする
		if (atworkTimeBeginDisp == AtWorkAtr.NOTDISPLAY) { // 表示しない
			// OUTPUT「勤務時間」を返す
			return workHoursOp;
		} else if (atworkTimeBeginDisp == AtWorkAtr.AT_START_WORK_OFF_ENDWORK) { // 出勤は始業時刻、退勤は終業時刻を初期表示する
			// 所定時間帯を取得する
			PredetermineTimeSetForCalc predetermineTimeSetForCalc = workTimeSettingService.getPredeterminedTimezone(
					companyId,
					overTimeContent.getWorkTimeCode().map(x -> x.v()).orElse(null),
					overTimeContent.getWorkTypeCode().map(x -> x.v()).orElse(null),
					null);
			// OUTPUT「勤務時間」をセットする
			if (!predetermineTimeSetForCalc.getTimezones().isEmpty()) {
				
				for (int i = 0; i < predetermineTimeSetForCalc.getTimezones().stream().count(); i++) {
					TimezoneUse item = predetermineTimeSetForCalc.getTimezones().get(i);
					Optional<TimeWithDayAttr> startTime = Optional.of(item.getStart());
					Optional<TimeWithDayAttr> endTime = Optional.of(item.getEnd());
					if (item.getWorkNo() == TimezoneUse.SHIFT_ONE) {
						if (!workHoursOp.isPresent()) {
							workHoursOp = Optional.of(new WorkHours());
						}
						workHoursOp.get().setStartTimeOp1(startTime);
						workHoursOp.get().setEndTimeOp1(endTime);
					} else if (item.getWorkNo() == TimezoneUse.SHIFT_TWO) {
						if (!workHoursOp.isPresent()) {
							workHoursOp = Optional.of(new WorkHours());
						}
						workHoursOp.get().setStartTimeOp2(startTime);
						workHoursOp.get().setEndTimeOp2(endTime);
					}
				}
			}
			return workHoursOp;
		}
		if (atworkTimeBeginDisp == AtWorkAtr.AT_START_WORK_OFF_PERFORMANCE) { // 出勤は始業時刻、退勤は実績の退勤を初期表示する
			// 所定時間帯を取得する
			PredetermineTimeSetForCalc predetermineTimeSetForCalc = workTimeSettingService.getPredeterminedTimezone(
					companyId,
					overTimeContent.getWorkTimeCode().map(x -> x.v()).orElse(null),
					overTimeContent.getWorkTypeCode().map(x -> x.v()).orElse(null),
					null);
			// OUTPUT「勤務時間」をセットする
			if (!predetermineTimeSetForCalc.getTimezones().isEmpty()) {
				
				for (int i = 0; i < predetermineTimeSetForCalc.getTimezones().stream().count(); i++) {
					TimezoneUse item = predetermineTimeSetForCalc.getTimezones().get(i);
					Optional<TimeWithDayAttr> startTime = Optional.of(item.getStart());
					if (item.getWorkNo() == TimezoneUse.SHIFT_ONE) {
						if (!workHoursOp.isPresent()) {
							workHoursOp = Optional.of(new WorkHours());
						}
						workHoursOp.get().setStartTimeOp1(startTime);
						if (overTimeContent.getActualTime().isPresent()) {
							workHoursOp.get().setEndTimeOp1(overTimeContent.getActualTime().get().getEndTimeOp1());
						}
					} else if (item.getWorkNo() == TimezoneUse.SHIFT_TWO) {
						if (!workHoursOp.isPresent()) {
							workHoursOp = Optional.of(new WorkHours());
						}
						workHoursOp.get().setStartTimeOp2(startTime);
						if (overTimeContent.getActualTime().isPresent()) {
							workHoursOp.get().setEndTimeOp2(overTimeContent.getActualTime().get().getEndTimeOp2());
						}
					}
				}
			}
			
		}
		if (atworkTimeBeginDisp == AtWorkAtr.DISPLAY) { // 実績から出退勤を初期表示する
			// OUTPUT「勤務時間」をセットする
			if (overTimeContent.getActualTime().isPresent()) {
				workHoursOp = Optional.of(overTimeContent.getActualTime().get());				
			}
		}
		// 「退勤時刻がない時システム時刻を表示するか」をチェックする
		if (!applicationDetailSetting.isDispSystemTimeWhenNoWorkTime()) {
			return workHoursOp;
		}
		// OUTPUT「勤務時間．終了時刻1」をチェックする
		if (workHoursOp.get().getEndTimeOp1().isPresent()) {
			return workHoursOp;
		}
		// NULLの場合
		// システム時刻をOUTPUT「勤務時間」に更新する
		workHoursOp.get().setEndTimeOp1(Optional.of(new TimeWithDayAttr(ClockHourMinute.now().v())));
		// OUTPUT「勤務時間」を返す
		return workHoursOp;
	}


	@Override
	public void checkOverTime(ApplicationTime applicationTime) {
		Integer timeTotal = 0;
		// INPUT「申請時間詳細」<List>をチェックする
		if (!CollectionUtil.isEmpty(applicationTime.getApplicationTime())) {
			timeTotal = applicationTime.getApplicationTime().stream()
							.filter(x -> x.getAttendanceType() == AttendanceType_Update.NORMALOVERTIME)
							.mapToInt(x -> x.getApplicationTime().v())
							.sum();
		}
		// 申請時間．フレックス超過時間
		timeTotal += applicationTime.getFlexOverTime().map(x -> x.v()).orElse(0);
		// 申請時間．就業時間外深夜時間．残業深夜時間
		timeTotal += applicationTime.getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getOverTimeMidNight())).map(x -> x.v()).orElse(0);
		
		Integer result = applicationTime
			.getOverTimeShiftNight()
			.flatMap(x -> CollectionUtil.isEmpty(x.getMidNightHolidayTimes()) ? Optional.empty() : Optional.of(x.getMidNightHolidayTimes()))
			.orElse(Collections.emptyList())
			.stream()
			.mapToInt(x -> Optional.ofNullable(x.getAttendanceTime()).map(y -> y.v()).orElse(0))
			.sum();
		timeTotal += result;
		
		if (timeTotal == 0) {
			throw new BusinessException("Msg_1654");	
		}
	}


	@Override
	public List<ConfirmMsgOutput> checkExcess(
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime) {
		List<ConfirmMsgOutput> output = new ArrayList<ConfirmMsgOutput>();
		String sid = appOverTime.getApplication().getEmployeeID();
		// INPUT．「残業申請」をチェックする
		if (appOverTime.getPrePostAtr() != PrePostAtr.POSTERIOR) {
			return Collections.emptyList();
		}
		// INPUT．「残業申請の表示情報」をチェックする
		/**
		 * 【チェック内容】
			INPUT．「残業申請の表示情報．基準日に関係しない情報．残業申請設定．申請詳細設定．時刻計算利用区分」＝　利用しない
		*/
		Boolean c1 = displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getApplicationDetailSetting().getTimeCalUse() == NotUseAtr.USE;
		/*	
		 * OR
			INPUT．「残業申請の表示情報．計算結果．残業時間帯修正フラグ」　＝　1
		 */
		Boolean c2 = displayInfoOverTime.getCalculatedFlag() == CalculatedFlag.UNCALCULATED;
		OverStateOutput overStateOutput = null;
		if (c1 || c2) {
			// 事前申請・実績の時間超過をチェックする
			overStateOutput = displayInfoOverTime.getInfoNoBaseDate()
					.getOverTimeAppSet()
					.getOvertimeLeaveAppCommonSet()
					.checkPreApplication(
						EnumAdaptor.valueOf(appOverTime.getPrePostAtr().value, PrePostInitAtr.class),
						displayInfoOverTime
							.getAppDispInfoStartup()
							.getAppDispInfoWithDateOutput()
							.getOpPreAppContentDisplayLst()
							.flatMap(x -> CollectionUtil.isEmpty(x) ? Optional.empty() : Optional.of(x.get(0)))
							.flatMap(y -> y.getApOptional())
							.flatMap(z -> Optional.of(z.getApplicationTime())),
						Optional.of(appOverTime.getApplicationTime()),
						displayInfoOverTime.getInfoWithDateApplicationOp().map(x -> x.getApplicationTime()).orElse(Optional.empty()));
			
		}
		// 取得した「事前申請・実績の超過状態．事前申請なし」をチェックする
		if (overStateOutput != null) {
			if (overStateOutput.getIsExistApp()) {
				// メッセージ（Msg_1508）をOUTPUT「確認メッセージリスト」に追加する
				output.add(new ConfirmMsgOutput(
										"Msg_1508",
										displayInfoOverTime
											.getAppDispInfoStartup()
										    .getAppDispInfoNoDateOutput()
										    .getEmployeeInfoLst()
										    .stream()
										    .filter(x -> x.getSid().equals(appOverTime.getApplication().getEmployeeID()))
										    .map(x -> x.getBussinessName())
										    .collect(Collectors.toList()))
										    );
			} else { // false
				// 取得した「事前申請・実績の超過状態．事前超過」をチェックする
				if (overStateOutput.getAdvanceExcess().isAdvanceExcess()) {
					// handle msg
					output.add(new ConfirmMsgOutput("Msg_424", getContentMsg(displayInfoOverTime, overStateOutput.getAdvanceExcess(), ExcessState.EXCESS_ALARM, sid)));
				}
				
				
			}
			List<String> contentMsgs = new ArrayList<String>();
			contentMsgs.add(displayInfoOverTime.getAppDispInfoStartup()
											   .getAppDispInfoNoDateOutput()
											   .getEmployeeInfoLst()
											   .stream()
											   .filter(x -> x.getSid().equals(appOverTime.getApplication().getEmployeeID()))
											   .findFirst()
											   .map(x -> x.getBussinessName())
											   .orElse(""));
			contentMsgs.add(appOverTime.getApplication().getAppDate().getApplicationDate().toString());
			// 取得した「事前申請・実績の超過状態．実績状態」をチェックする
			if (overStateOutput.getAchivementStatus() == ExcessState.EXCESS_ERROR) {
				
				// エラーメッセージ（Msg_1746）を表示する
				throw new BusinessException("Msg_1746", contentMsgs.toArray(new String[contentMsgs.size()]));
			} else if (overStateOutput.getAchivementStatus() == ExcessState.EXCESS_ALARM) {
				// メッセージ（Msg_1745）をOUTPUT「確認メッセージリスト」に追加する
				output.add(new ConfirmMsgOutput("Msg_1745", contentMsgs));
				// OUTPUT「確認メッセージリスト」を返す
				
			} else {
				// 取得した「事前申請・実績の超過状態．実績超過」をチェックする
				if (overStateOutput.getAchivementExcess().isAdvanceExcessError()) {
					List<String> contens1748 = getContentMsg(displayInfoOverTime, overStateOutput.getAchivementExcess(), ExcessState.EXCESS_ERROR, sid);			
					// エラーメッセージ（Msg_1748）を表示する
					throw new BusinessException("Msg_1748", contens1748.toArray(new String[contentMsgs.size()]));
				} else {
					// 取得した「事前申請・実績の超過状態．実績超過」をチェックする
					if (overStateOutput.getAchivementExcess().isAdvanceExcess()) {
						// メッセージ（Msg_1747）をOUTPUT「確認メッセージリスト」に追加する
						output.add(new ConfirmMsgOutput("Msg_1747", getContentMsg(displayInfoOverTime, overStateOutput.getAchivementExcess(), ExcessState.EXCESS_ALARM, sid)));
						
					}
					// OUTPUT「確認メッセージリスト」を返す
				}
			}
			
		}
		
		return output;
	}

	public List<String> getContentMsg(
			DisplayInfoOverTime displayInfoOverTime,
			OutDateApplication outDateApplication,
			ExcessState type,
			String sid) {
		List<String> contentMsgs = new ArrayList<String>();
		// ・申請時間の超過状態．申請時間．type = 残業時間　に超過アラームがある
		List<Integer> lstFrameOverTime = outDateApplication.getExcessStateDetail()
					 .stream()
					 .filter(x -> x.getType() == AttendanceType_Update.NORMALOVERTIME
							 && x.getExcessState() == type)
					 .map(y -> y.getFrame().v())
					 .collect(Collectors.toList());
		
		// get name list of over times
		List<String> lstNameOverTime = displayInfoOverTime.getInfoBaseDateOutput()
							.getQuotaOutput()
							.getOverTimeQuotaList()
							.stream()
							.filter(x -> lstFrameOverTime.contains(x.getOvertimeWorkFrNo().v().intValue()))
							.map(y -> y.getOvertimeWorkFrName().v())
							.collect(Collectors.toList());
		contentMsgs.addAll(lstNameOverTime);
		
		// ・申請時間の超過状態．申請時間．type = 休出時間　に超過アラームがある
		List<Integer> lstFrameHoliday = outDateApplication.getExcessStateDetail()
				 .stream()
				 .filter(x -> x.getType() == AttendanceType_Update.BREAKTIME
						 && x.getExcessState() == type)
				 .map(y -> y.getFrame().v())
				 .collect(Collectors.toList());
		
		List<String> lstNameHoliday = displayInfoOverTime
				.getWorkdayoffFrames()
				.stream()
				.filter(x -> lstFrameHoliday.contains(x.getWorkdayoffFrNo().v().intValue()))
				.map(y -> y.getWorkdayoffFrName().v())
				.collect(Collectors.toList());
		
		contentMsgs.addAll(lstNameHoliday);
		
		// 申請時間の超過状態．休出深夜時間．法定区分 = 法定内休出　に超過アラームがある
		// 「#KAF005_341」を{1}に追加する
		outDateApplication.getExcessStateMidnight()
					 .stream()
					 .forEach(x -> {
						 if (x.getExcessState() == type) {
							 if (x.getLegalCfl() == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork) {
								 contentMsgs.add(I18NText.getText("KAF005_341"));
							 } else if (x.getLegalCfl() == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork) {
								 contentMsgs.add(I18NText.getText("KAF005_342"));
							 } else if (x.getLegalCfl() == StaturoryAtrOfHolidayWork.PublicHolidayWork) {
								 contentMsgs.add(I18NText.getText("KAF005_343"));
							 }										 
						 }
					 });
		
		if (outDateApplication.getOverTimeLate() == type) {
			contentMsgs.add(I18NText.getText("KAF005_63"));
		}
		if (outDateApplication.getFlex() == type) {
			contentMsgs.add(I18NText.getText("KAF005_65"));
		}
		
		// {1}：追加方法を下に参照　※複数枠が超過したら”、”を追加してください（nếu nhiều frame cung lỗi thi cach nhau bằng dấu "、"）
		String msgContent = contentMsgs.stream()
									   .collect(Collectors.joining("、"));
		contentMsgs.clear();
		contentMsgs.add(
				displayInfoOverTime.getAppDispInfoStartup()
				   .getAppDispInfoNoDateOutput()
				   .getEmployeeInfoLst()
				   .stream()
				   .filter(x -> x.getSid().equals(sid))
				   .findFirst()
				   .map(x -> x.getBussinessName())
				   .orElse(""));
		contentMsgs.add(msgContent);
		return contentMsgs;
	}
	@Override
	public void check36Limit(
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime
			) {
		// 18.３６時間の上限チェック(新規登録)_NEW
		Time36ErrorInforList time36ErrorInforList = time36UpperLimitCheck.checkRegister(
				companyId,
				appOverTime.getApplication().getEmployeeID(),
				displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getEmpHistImport().getEmploymentCode(),
				appOverTime.getApplication(),
				Optional.ofNullable(appOverTime),
				Optional.empty(),
				displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr(),
				displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr());
		// ある場合
		BundledBusinessException bundledBusinessExceptions = BundledBusinessException.newInstance();
		if (!CollectionUtil.isEmpty(time36ErrorInforList.getTime36AgreementErrorLst())) {
			/**
			 * 	・エラー情報一覧に「月間エラー」がある場合：
				　Msg_1535：{0:Time_Short_HM}＝一覧．実績時間、{1:Time_Short_HM}＝一覧．しきい値
				　例：月間の時間外時間(41:00)が36協定時間(40:00)を超えるため、登録できません。
				
				・エラー情報一覧に「年間エラー」がある場合：
				　Msg_1536：{0:Time_Short_HM}＝一覧．実績時間、{1:Time_Short_HM}＝一覧．しきい値
				　例：年間の時間外時間(361:00)が36協定時間(360:00)を超えるため、登録できません。
				
				・エラー情報一覧に「上限月間時間エラー」がある場合：
				　Msg_1537：{0:Time_Short_HM}＝一覧．実績時間、{1:Time_Short_HM}＝一覧．しきい値
				　例：対象月の時間外時間(101:00)が月間の上限規制時間(100:00)を超えるため、登録できません。
				
				・エラー情報一覧に「上限年間時間エラー」がある場合：
				　Msg_2056：{0:Time_Short_HM}＝一覧．実績時間、{1:Time_Short_HM}＝一覧．しきい値
				　例：年間の時間外時間(501:00)が上限規制時間(500:00)を超えるため、登録できません。
				
				・エラー情報一覧に「上限複数月平均時間エラー」がある場合：
				　Msg_1538：{0:Short_YM}＝一覧．複数月エラー期間．開始年月、{1:Short_YM}＝一覧．複数月エラー期間．終了年月、{2:Time_Short_HM}＝一覧．実績時間、{3:Time_Short_HM}＝一覧．しきい値
				　例：2019/4～2019/5の時間外時間の平均(40:00)が複数月平均(35:00)を超えるため、登録できません。


			 */
			for (Time36AgreementError el : time36ErrorInforList.getTime36AgreementErrorLst()) {
				if (el.getTime36AgreementErrorAtr() == Time36AgreementErrorAtr.MONTH_ERROR) {
					bundledBusinessExceptions.addMessage(
							"Msg_1535",
							this.convertTime_Short_HM(el.getAgreementTime()),
							this.convertTime_Short_HM(el.getThreshold())
							);
				}
				if (el.getTime36AgreementErrorAtr() == Time36AgreementErrorAtr.YEAR_ERROR) {
					
					bundledBusinessExceptions.addMessage(
							"Msg_1536",
							this.convertTime_Short_HM(el.getAgreementTime()),
							this.convertTime_Short_HM(el.getThreshold())
							);
				}
				if (el.getTime36AgreementErrorAtr() == Time36AgreementErrorAtr.MAX_MONTH_ERROR) {
					bundledBusinessExceptions.addMessage(
							"Msg_1537",
							this.convertTime_Short_HM(el.getAgreementTime()),
							this.convertTime_Short_HM(el.getThreshold())
									
							);
				}
				if (el.getTime36AgreementErrorAtr() == Time36AgreementErrorAtr.MAX_YEAR_ERROR) {
					bundledBusinessExceptions.addMessage(
							"Msg_2056",
							this.convertTime_Short_HM(el.getAgreementTime()),
							this.convertTime_Short_HM(el.getThreshold())
									
							);
					
				}
				if (el.getTime36AgreementErrorAtr() == Time36AgreementErrorAtr.MAX_MONTH_AVERAGE_ERROR) {
					bundledBusinessExceptions.addMessage(
							"Msg_1538",
							el.getOpYearMonthPeriod().map(x -> x.start().year() + "/" + x.start().month()).orElse(""),
							el.getOpYearMonthPeriod().map(x -> x.end().year() + "/" + x.end().month()).orElse(""),
							this.convertTime_Short_HM(el.getAgreementTime()),
							this.convertTime_Short_HM(el.getThreshold())
									
							);
					
				}
			}
			
			
		}
		if (!CollectionUtil.isEmpty(bundledBusinessExceptions.getMessageId())) {
			
			throw bundledBusinessExceptions;
		}
		
		
		
	}
	
	private String convertTime_Short_HM(int time) {
		return (time / 60 + ":" + (time % 60 < 10 ? "0" + time % 60 : time % 60));
	}
	// get 
	public Integer convertFramNo(StaturoryAtrOfHolidayWork legalClf) {
		if (legalClf == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork) return 11;
		if (legalClf == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork) return 12;
		return 13;
	}

	@Override
	public void commonAlgorithmAB(String companyId,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime,
			Integer mode) {
		if (appOverTime == null) return;
		// 画面のモードをチェックする
		if (mode == 1) { // 新規モードの場合
			List<GeneralDate> dates = new ArrayList<GeneralDate>();
			dates.add(appOverTime.getAppDate().getApplicationDate());
			List<String> workTypeLst = new ArrayList<String>();
			if (appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode().v()).isPresent()) {
				workTypeLst.add(appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode().v()).orElse(null));				
			}
			// 申請の矛盾チェック
			commonAlgorithm.appConflictCheck(
					companyId,
					displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0),
					dates,
					workTypeLst,
					displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList()));
		}
		
	}


	@Override
	public CheckBeforeOutput checkBeforeOverTime(
			Boolean require,
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime,
			Integer mode) {
		CheckBeforeOutput output = new CheckBeforeOutput();
		// 未計算チェックする
		commonOvertimeholiday.calculateButtonCheck(
				displayInfoOverTime.getCalculatedFlag(),
				EnumAdaptor.valueOf(displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getApplicationDetailSetting().getTimeCalUse().value, UseAtr.class));
		if (!displayInfoOverTime.getWorkInfo().isPresent()) {
			// 勤務種類、就業時間帯のマスタ未登録チェックする
			detailBeforeUpdate.displayWorkingHourCheck(
					companyId,
					appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTypeCode())).map(x -> x.v()).orElse(null),
					appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTimeCode())).map(x -> x.v()).orElse(null));			
		}
		// 申請する残業時間をチェックする
		this.checkOverTime(appOverTime.getApplicationTime());
		// 事前申請・実績超過チェックする
		List<ConfirmMsgOutput> confirmMsgOutputs = this.checkExcess(
				appOverTime,
				displayInfoOverTime);
		// 事前申請が必須か確認する
		displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getApplicationDetailSetting().checkAdvanceApp(
				ApplicationType.OVER_TIME_APPLICATION,
				appOverTime.getPrePostAtr(),
				displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpPreAppContentDisplayLst().map(x -> x.get(0).getApOptional()).orElse(Optional.empty()),
				Optional.empty());
		// 申請時の乖離時間をチェックする not done
		overTimeService.checkDivergenceTime(require,
				ApplicationType.OVER_TIME_APPLICATION,
				Optional.of(appOverTime),
				Optional.empty(),
				displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getOvertimeLeaveAppCommonSet());
		// ３６上限チェック
		this.check36Limit(companyId, appOverTime, displayInfoOverTime);
		
		// 申請日の矛盾チェック
		this.commonAlgorithmAB(
				companyId,
				displayInfoOverTime,
				appOverTime,
				mode);
		// 取得した「確認メッセージリスト」と「残業申請」を返す
		// output.setAppOverTime(appOverTime36);
		output.setAppOverTime(appOverTime);
		output.setConfirmMsgOutputs(confirmMsgOutputs);
		return output;
	}


	@Override
	public DisplayInfoOverTime changeApplicationDate(
			String companyId,
			GeneralDate date,
			DisplayInfoOverTime displayInfoOverTime) {
		AppDispInfoStartupOutput appDispInfoStartup = displayInfoOverTime.getAppDispInfoStartup();
		// INPUT．「残業申請の表示情報．申請表示情報(基準日関係なし) ．申請承認設定．申請設定」．承認ルートの基準日をチェックする
		if (appDispInfoStartup // 申請対象日時点
						   .getAppDispInfoNoDateOutput()
						   .getApplicationSetting()
						   .getRecordDate() == RecordDate.APP_DATE) {
			// 基準日に関する情報を取得する	
			InfoBaseDateOutput infoBaseDateOutput = this.getInfoBaseDate(
					companyId,
					appDispInfoStartup.getAppDispInfoNoDateOutput()
									  .getEmployeeInfoLst()
									  .get(0)
									  .getSid(),
					appDispInfoStartup.getAppDispInfoWithDateOutput()
									  .getBaseDate(),
				    displayInfoOverTime.getOvertimeAppAtr(),
				    appDispInfoStartup.getAppDispInfoWithDateOutput().getOpWorkTimeLst().orElse(Collections.emptyList()),
				    appDispInfoStartup.getAppDispInfoWithDateOutput().getOpEmploymentSet(),
				    displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet());
			
			// 「残業申請の表示情報」を更新する
			displayInfoOverTime.setInfoBaseDateOutput(infoBaseDateOutput);
				
		}
		
		// 申請日に関する情報を取得する
		InfoWithDateApplication infoWithDateApplication = this.getInfoAppDate(
				companyId,
				Optional.ofNullable(date),
				Optional.empty(),
				Optional.empty(),
				displayInfoOverTime.getInfoBaseDateOutput().getWorktypes(),
				appDispInfoStartup,
				displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet());
		
		boolean isCalUse = displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getApplicationDetailSetting().getTimeCalUse() == NotUseAtr.USE;
		if (isCalUse && CollectionUtil.isEmpty(displayInfoOverTime.getInfoBaseDateOutput().getWorktypes())) {
			throw new BusinessException("Msg_1567");
		}
		if (isCalUse && CollectionUtil.isEmpty(displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpWorkTimeLst().orElse(Collections.emptyList()))) {
			throw new BusinessException("Msg_1568");
		}
		
		displayInfoOverTime.setInfoWithDateApplicationOp(Optional.ofNullable(infoWithDateApplication));
		displayInfoOverTime.setCalculatedFlag(CalculatedFlag.UNCALCULATED);
		
		return displayInfoOverTime;
	}

}
