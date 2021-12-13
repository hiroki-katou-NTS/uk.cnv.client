package nts.uk.ctx.at.request.dom.application.common.service.newscreen.before;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationDate;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.PesionInforImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalRootContentImport_New;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.CheckBeforeRegisMultiEmpOutput;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.PeriodCurrentMonth;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoNoDateOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.MsgErrorOutput;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.setting.UseDivision;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationrestrictionsetting.AppLimitSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationrestrictionsetting.service.ActualLockingCheck;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationrestrictionsetting.service.DayActualConfirmDoneCheck;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationrestrictionsetting.service.MonthActualConfirmDoneCheck;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationrestrictionsetting.service.WorkConfirmDoneCheck;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.ReceptionRestrictionSetting;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApplicationUseSetting;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApprovalFunctionSet;
import nts.uk.ctx.at.request.dom.workrecord.remainmanagement.InterimRemainDataMngCheckRegisterRequest;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.AppRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.EarchInterimRemainCheck;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainCheckInputParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RecordRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ScheRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionUsageInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeUseInfor;
import nts.uk.ctx.at.shared.dom.worktype.specialholidayframe.SpecialHdFrameNo;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class NewBeforeRegisterImpl implements NewBeforeRegister {
	
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	
	@Inject
	private EmployeeRequestAdapter employeeAdaptor;
	
	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithmService;
	
	@Inject
	private DayActualConfirmDoneCheck dayActualConfirmDoneCheck;

	@Inject
	private MonthActualConfirmDoneCheck monthActualConfirmDoneCheck;

	@Inject
	private WorkConfirmDoneCheck workConfirmDoneCheck;

	@Inject
	private ActualLockingCheck actualLockingCheck;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private InterimRemainDataMngCheckRegisterRequest interimRemainDataMngCheckRegisterRequest;
	
	// moi nguoi chi co the o mot cty vao mot thoi diem
	// check xem nguoi xin con trong cty k
	public void retirementCheckBeforeJoinCompany(String companyID, String employeeID, GeneralDate date){
		// Imported(就業)「社員」を取得する
		PesionInforImport pesionInforImport = employeeAdaptor.getEmployeeInfor(employeeID);
		// 入社前をチェックする
		// データが１件以上取得できた 且つ 申請対象日 < Imported(就業)「社員」．入社年月日
		if(pesionInforImport.getEntryDate() != null && date.before(pesionInforImport.getEntryDate())) {
			throw new BusinessException("Msg_235");
		}
		
		// 退職後をチェックする
		// データが１件以上取得できた 且つ 申請対象日 > Imported(就業)「社員」．退職年月日
		if(pesionInforImport.getRetiredDate() != null && date.after(pesionInforImport.getRetiredDate())) {
			throw new BusinessException("Msg_391");
		}
	}
	
	public void deadlineAppCheck(GeneralDate appStartDate, GeneralDate appEndDate,
			GeneralDate closureStartDate, GeneralDate closureEndDate, AppDispInfoWithDateOutput appDispInfoWithDateOutput){
		// INPUT．申請表示情報(基準日関係あり)．申請締め切り日利用区分をチェックする
		if(appDispInfoWithDateOutput.getAppDeadlineUseCategory()==NotUseAtr.NOT_USE) {
			return;
		}
		// INPUT．申請する開始日からINPUT．申請する終了日までループする
		for(GeneralDate loopAppDate = appStartDate; loopAppDate.beforeOrEquals(appEndDate); loopAppDate = loopAppDate.addDays(1)) {
			// 当月の申請をするかチェックする
			if(loopAppDate.afterOrEquals(closureStartDate) && loopAppDate.beforeOrEquals(closureEndDate)) {
				// システム日付とINPUT．申請表示情報(基準日関係あり)．申請締め切り日を比較する
				if(GeneralDate.today().after(appDispInfoWithDateOutput.getOpAppDeadline().get())) {
					// エラーメッセージ(Msg_327)
					throw new BusinessException("Msg_327", loopAppDate.toString());
				}
			}
		}
	}
	
	public void appAcceptanceRestrictionsCheck(PrePostAtr prePostAtr, GeneralDate appStartDate, GeneralDate appEndDate,
			ApplicationType appType, OvertimeAppAtr overtimeAppAtr, AppDispInfoNoDateOutput appDispInfoNoDateOutput){
		GeneralDate systemDate = GeneralDate.today();
		ReceptionRestrictionSetting receptionRestrictionSetting = appDispInfoNoDateOutput.getApplicationSetting().getReceptionRestrictionSettings()
				.stream().filter(x -> x.getAppType() == appType).findAny().orElse(null);
		// INPUT．事前事後区分をチェックする
		if(prePostAtr==PrePostAtr.POSTERIOR) {
			// INPUT．申請設定（基準日関係なし）．申請承認設定．申請設定．受付制限設定．事後の受付制限．未来日許可しないをチェックする
			if(!receptionRestrictionSetting.getAfterhandRestriction().isAllowFutureDay()) {
				return;
			}
			// 未来日の事後申請かチェックする
			if(appStartDate.after(systemDate) || appEndDate.after(systemDate)) {
				// エラーメッセージ(Msg_328)
				throw new BusinessException("Msg_328");
			}
			return;
		}
		// INPUT．申請表示情報(基準日関係なし)．事前申請の受付制限をチェックする
		if(appDispInfoNoDateOutput.getAdvanceAppAcceptanceLimit()==NotUseAtr.NOT_USE) {
			return;
		}
		// INPUT．申請する開始日からINPUT．申請する終了日までループする
		for(GeneralDate loopAppDate = appStartDate; loopAppDate.beforeOrEquals(appEndDate); loopAppDate = loopAppDate.addDays(1)) {
			// 対象日が申請可能かを判定する
			boolean errorFlg = receptionRestrictionSetting.applyPossibleCheck(
					appType, 
					loopAppDate, 
					overtimeAppAtr, 
					appDispInfoNoDateOutput.getOpAdvanceReceptionDate().orElse(null), 
					appDispInfoNoDateOutput.getOpAdvanceReceptionHours().orElse(null));
			if(errorFlg) {
				// エラーメッセージ(Msg_327)
				throw new BusinessException("Msg_327", loopAppDate.toString());
			}
		}
	}
	
	public void confirmationCheck(String companyID, String employeeID, GeneralDate appDate, AppDispInfoStartupOutput appDispInfoStartupOutput){
		AppLimitSetting appLimitSetting = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getApplicationSetting().getAppLimitSetting();
		// ドメインモデル「申請制限設定」．日別実績が確認済なら申請できないをチェックする(check domain 「申請制限設定」．日別実績が確認済なら申請できない)
		boolean hasError = false;
		hasError = dayActualConfirmDoneCheck.check(appLimitSetting.isCanAppAchievementConfirm(), companyID, employeeID, appDate);
		if (hasError == true) {
			throw new BusinessException("Msg_448");
		}
		// ドメインモデル「申請制限設定」．月別実績が確認済なら申請できないをチェックする
		hasError = monthActualConfirmDoneCheck.check(false, appLimitSetting.isCanAppAchievementMonthConfirm(), companyID, employeeID, appDate);
		if (hasError == true) {
			throw new BusinessException("Msg_449");
		}
		
		// ドメインモデル「申請制限設定」．就業確定済の場合申請できないをチェックする
		hasError = workConfirmDoneCheck.check(appLimitSetting.isCanAppFinishWork(), companyID, employeeID, appDate);
		if (hasError == true) {
			throw new BusinessException("Msg_450");
		}

		// ドメインモデル「申請制限設定」．実績修正がロック状態なら申請できないをチェックする
		hasError = actualLockingCheck.check(appLimitSetting.isCanAppAchievementLock(), companyID, employeeID, appDate);
		if (hasError == true) {
			throw new BusinessException("Msg_451");
		}
	}
	public void confirmCheckOvertime(String companyID, String employeeID, GeneralDate appDate, AppDispInfoStartupOutput appDispInfoStartupOutput){
		AppLimitSetting appLimitSetting = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getApplicationSetting().getAppLimitSetting();
		boolean hasError = false;
		// ドメインモデル「申請制限設定」．月別実績が確認済なら申請できないをチェックする
		hasError = monthActualConfirmDoneCheck.check(true, appLimitSetting.isCanAppAchievementMonthConfirm(), companyID, employeeID, appDate);
		if (hasError == true) {
			throw new BusinessException("Msg_449");
		}
		
		// ドメインモデル「申請制限設定」．就業確定済の場合申請できないをチェックする
		hasError = workConfirmDoneCheck.check(appLimitSetting.isCanAppFinishWork(), companyID, employeeID, appDate);
		if (hasError == true) {
			throw new BusinessException("Msg_450");
		}

		// ドメインモデル「申請制限設定」．実績修正がロック状態なら申請できないをチェックする
		hasError = actualLockingCheck.check(appLimitSetting.isCanAppAchievementLock(), companyID, employeeID, appDate);
		if (hasError == true) {
			throw new BusinessException("Msg_451");
		}
	}

	@Override
	public List<ConfirmMsgOutput> processBeforeRegister_New(String companyID, EmploymentRootAtr employmentRootAtr, boolean agentAtr,
			Application application, OvertimeAppAtr overtimeAppAtr, List<MsgErrorOutput> msgErrorLst,
			List<GeneralDate> lstDateHd, AppDispInfoStartupOutput appDispInfoStartupOutput, List<String> workTypeCds, 
			Optional<TimeDigestionParam> timeDigestionUsageInfor, Optional<String> workTimeCode, boolean flag) {
		List<ConfirmMsgOutput> result = new ArrayList<>();
		// アルゴリズム「未入社前チェック」を実施する
		retirementCheckBeforeJoinCompany(companyID, application.getEmployeeID(), application.getAppDate().getApplicationDate());
		
		// アルゴリズム「社員の当月の期間を算出する」を実行する
		PeriodCurrentMonth periodCurrentMonth = otherCommonAlgorithmService.employeePeriodCurrentMonthCalculate(
				companyID, application.getEmployeeID(), application.getAppDate().getApplicationDate());
		
		Optional<GeneralDate> opStartDate = application.getOpAppStartDate().map(x -> x.getApplicationDate());
		if(!opStartDate.isPresent()) {
			opStartDate = Optional.of(application.getAppDate().getApplicationDate());
		}
		Optional<GeneralDate> opEndDate = application.getOpAppEndDate().map(x -> x.getApplicationDate());
		if(!opEndDate.isPresent()) {
			opEndDate = Optional.of(application.getAppDate().getApplicationDate());
		}
		if (opStartDate.isPresent() && opEndDate.isPresent()) {
			// 登録する期間のチェック
			//((TimeSpan)(申請する終了日 - 申請する開始日)).Days > 31がtrue
			if((ChronoUnit.DAYS.between(opStartDate.get().localDate(), opEndDate.get().localDate()) + 1)  > 31){
				throw new BusinessException("Msg_277");
			}
			// 登録可能期間のチェック(１年以内)
			//EA修正履歴 No.3210
			//hoatt 2019.03.22
			if(periodCurrentMonth.getStartDate().addYears(1).beforeOrEquals(opEndDate.get())) {
				//締め期間．開始年月日.AddYears(1) <= 申請する終了日がtrue
				// エラーメッセージメッセージ（Msg_1518）
				throw new BusinessException("Msg_1518", periodCurrentMonth.getStartDate().addYears(1).toString(DATE_FORMAT));
			}
			
			// 過去月のチェック
			if(opStartDate.get().before(periodCurrentMonth.getStartDate())) {
				throw new BusinessException("Msg_236");			
			}
		}		
		
		// キャッシュから承認ルートを取得する(Lấy comfirm root từ cache)	
		if(!CollectionUtil.isEmpty(msgErrorLst)) {
			MsgErrorOutput msgErrorOutput = msgErrorLst.get(0);
			I18NText.Builder builder = I18NText.main(msgErrorOutput.getMsgID());
			for(String param : msgErrorOutput.getMsgParam()) {
				builder.addId(param);
			}
			throw new BusinessException(builder.build());
		}

		// アルゴリズム「申請の締め切り期限をチェック」を実施する
		deadlineAppCheck(opStartDate.get(), opEndDate.get(), 
				periodCurrentMonth.getStartDate(), periodCurrentMonth.getEndDate(), appDispInfoStartupOutput.getAppDispInfoWithDateOutput());
		
		// アルゴリズム「申請の受付制限をチェック」を実施する
		appAcceptanceRestrictionsCheck(application.getPrePostAtr(), opStartDate.get(), opEndDate.get(), 
				application.getAppType(), overtimeAppAtr, appDispInfoStartupOutput.getAppDispInfoNoDateOutput());
		
		// 申請する開始日～申請する終了日までループする
		for(GeneralDate loopDate = opStartDate.get(); loopDate.beforeOrEquals(opEndDate.get()); loopDate = loopDate.addDays(1)){
            //hoatt 2019/10/14 #109087を対応
            if(lstDateHd != null && lstDateHd.contains(loopDate)){
                continue;
            }
			if(loopDate.equals(GeneralDate.today()) && application.getPrePostAtr().equals(PrePostAtr.PREDICT) && 
					application.getAppType() == ApplicationType.OVER_TIME_APPLICATION){
				// アルゴリズム「6.確定チェック（事前残業申請用）」を実施する
				confirmCheckOvertime(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}else{
				// アルゴリズム「確定チェック」を実施する
				confirmationCheck(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}
		}
		
		if (!flag) {
		    // 登録時の残数チェック
		    List<VacationTimeUseInfor> vacationTimeInforNews = timeDigestionUsageInfor.isPresent() ? 
		            timeDigestionUsageInfor.get().getTimeLeaveApplicationDetails().stream().map(x -> 
		            new VacationTimeUseInfor(
		                    x.getAppTimeType(), 
		                    x.getTimeDigestApplication().getTimeAnnualLeave(), 
		                    x.getTimeDigestApplication().getTimeOff(), 
		                    x.getTimeDigestApplication().getOvertime60H(), 
		                    x.getTimeDigestApplication().getTimeSpecialVacation(), 
		                    x.getTimeDigestApplication().getChildTime(), 
		                    x.getTimeDigestApplication().getNursingTime(), 
		                    x.getTimeDigestApplication().getSpecialVacationFrameNO().map(y -> new SpecialHdFrameNo(y))))
		            .collect(Collectors.toList()) : new ArrayList<VacationTimeUseInfor>();
		            AppRemainCreateInfor appRemainCreateInfor = new AppRemainCreateInfor(
		                    application.getEmployeeID(), 
		                    application.getAppID(), 
		                    application.getInputDate(), 
		                    application.getAppDate().getApplicationDate(), 
		                    EnumAdaptor.valueOf(application.getPrePostAtr().value, nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.PrePostAtr.class), 
		                    EnumAdaptor.valueOf(application.getAppType().value, nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ApplicationType.class), 
		                    workTypeCds.isEmpty() ? Optional.empty() : Optional.of(workTypeCds.get(0)),
		                            workTimeCode, 
		                            vacationTimeInforNews,
		                            Optional.of(application.getAppType().equals(ApplicationType.HOLIDAY_WORK_APPLICATION) && timeDigestionUsageInfor.isPresent() 
		                                    ? timeDigestionUsageInfor.get().getOverHolidayTime() : 0), 
		                            Optional.of(application.getAppType().equals(ApplicationType.OVER_TIME_APPLICATION) && timeDigestionUsageInfor.isPresent()
		                                    ? timeDigestionUsageInfor.get().getOverHolidayTime() : 0), 
		                            application.getOpAppStartDate().map(ApplicationDate::getApplicationDate), 
		                            application.getOpAppEndDate().map(ApplicationDate::getApplicationDate), 
		                            lstDateHd, 
		                            timeDigestionUsageInfor.map(TimeDigestionParam::toTimeDigestionUsageInfor), Optional.empty());
		            InterimRemainCheckInputParam param = new InterimRemainCheckInputParam(
		                    companyID, 
		                    application.getEmployeeID(), 
		                    new DatePeriod(periodCurrentMonth.getStartDate(), periodCurrentMonth.getStartDate().addYears(1).addDays(-1)), 
		                    false, 
		                    application.getAppDate().getApplicationDate(), 
		                    new DatePeriod(application.getOpAppStartDate().get().getApplicationDate(), application.getOpAppEndDate().get().getApplicationDate()), 
		                    true, 
		                    new ArrayList<RecordRemainCreateInfor>(), 
		                    new ArrayList<ScheRemainCreateInfor>(), 
		                    Arrays.asList(appRemainCreateInfor), 
		                    workTypeCds, 
		                    timeDigestionUsageInfor);
		            EarchInterimRemainCheck earchInterimRemainCheck = interimRemainDataMngCheckRegisterRequest.checkRegister(param);
		            
		            // 代休不足区分 or 振休不足区分 or 年休不足区分 or 積休不足区分 or 特休不足区分　or 超休不足区分　OR　子の看護不足区分　OR　介護不足区分 = true（残数不足）
//		if (earchInterimRemainCheck.isChkSubHoliday() 
//		        || earchInterimRemainCheck.isChkPause()
//		        || earchInterimRemainCheck.isChkAnnual()
//		        || earchInterimRemainCheck.isChkFundingAnnual()
//		        || earchInterimRemainCheck.isChkSpecial()
//		        || earchInterimRemainCheck.isChkSuperBreak()
//		        || earchInterimRemainCheck.isChkChildNursing()
//		        || earchInterimRemainCheck.isChkLongTermCare()) {
//		    // エラーメッセージ（Msg_1409）
//		    throw new BusinessException("Msg_1409");
//		}
		            if (earchInterimRemainCheck.isChkSubHoliday()) {
		                throw new BusinessException("Msg_1409", "代休");
		            }
		            if (earchInterimRemainCheck.isChkPause()) {
		                throw new BusinessException("Msg_1409", "振休");
		            }
		            if (earchInterimRemainCheck.isChkAnnual()) {
		                throw new BusinessException("Msg_1409", "年休");
		            }
		            if (earchInterimRemainCheck.isChkFundingAnnual()) {
		                throw new BusinessException("Msg_1409", "積休");
		            }
		            if (earchInterimRemainCheck.isChkSpecial()) {
		                throw new BusinessException("Msg_1409", "特休");
		            }
		            if (earchInterimRemainCheck.isChkSuperBreak()) {
		                throw new BusinessException("Msg_1409", "超休");
		            }
		            if (earchInterimRemainCheck.isChkChildNursing()) {
		                throw new BusinessException("Msg_1409", "子の看護");
		            }
		            if (earchInterimRemainCheck.isChkLongTermCare()) {
		                throw new BusinessException("Msg_1409", "介護");
		            }
		}
		
		return result;
	}
	
	@Override
	public CheckBeforeRegisMultiEmpOutput processBeforeRegisterMultiEmp(String companyID, List<String> employeeIDLst,
			Application application, OvertimeAppAtr overtimeAppAtr, AppDispInfoStartupOutput appDispInfoStartupOutput) {
		CheckBeforeRegisMultiEmpOutput result = new CheckBeforeRegisMultiEmpOutput();
		try {
			result = this.checkBeforeRegisterMultiEmp(companyID, employeeIDLst, application, overtimeAppAtr, appDispInfoStartupOutput);
		} catch (BusinessException e) {
			String newMsg = "";
			switch (e.getMessageId()) {
			case "Msg_235": newMsg = "Msg_1995"; break;
			case "Msg_391": newMsg = "Msg_1996"; break;
			case "Msg_323": newMsg = "Msg_1997"; break;
			case "Msg_1134": newMsg = "Msg_1998"; break;
			case "Msg_1518": newMsg = "Msg_1999"; break;
			case "Msg_236": newMsg = "Msg_2000"; break;
			case "Msg_327": newMsg = "Msg_2001"; break;
			case "Msg_448": newMsg = "Msg_2002"; break;
			case "Msg_449": newMsg = "Msg_2003"; break;
			case "Msg_450": newMsg = "Msg_2004"; break;
			case "Msg_451": newMsg = "Msg_2005"; break;
			case "Msg_324": newMsg = "Msg_2008"; break;
			case "Msg_237": newMsg = "Msg_2009"; break;
			case "Msg_238": newMsg = "Msg_2010"; break;
			case "Msg_1409": newMsg = "Msg_2011"; break;
			case "Msg_1535": newMsg = "Msg_2012"; break;
			case "Msg_1536": newMsg = "Msg_2013"; break;
			case "Msg_1537": newMsg = "Msg_2014"; break;
			case "Msg_1538": newMsg = "Msg_2015"; break;
			case "Msg_1508": newMsg = "Msg_2019"; break;
			case "Msg_2056": newMsg = "Msg_2057"; break;
			default:
				newMsg = e.getMessageId();
				break;
			}
			I18NText.Builder builder = I18NText.main(newMsg);
			for(String param : e.getParameters()) {
				builder.addId(param);
			}
			throw new BusinessException(builder.build());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return result;
	}

	private CheckBeforeRegisMultiEmpOutput checkBeforeRegisterMultiEmp(String companyID, List<String> employeeIDLst,
			Application application, OvertimeAppAtr overtimeAppAtr, AppDispInfoStartupOutput appDispInfoStartupOutput) {
		CheckBeforeRegisMultiEmpOutput result = new CheckBeforeRegisMultiEmpOutput();
		// 5.申請の受付制限をチェック
		this.appAcceptanceRestrictionsCheck(
				application.getPrePostAtr(), 
				application.getOpAppStartDate().map(x -> x.getApplicationDate()).orElse(null), 
				application.getOpAppEndDate().map(x -> x.getApplicationDate()).orElse(null), 
				application.getAppType(), 
				overtimeAppAtr, 
				appDispInfoStartupOutput.getAppDispInfoNoDateOutput());
		// INPUT．申請者リストをループする
		for(String employeeID : employeeIDLst) {
			String empErrorName = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().stream()
					.filter(x -> x.getSid().equals(employeeID)).findAny().map(x -> x.getBussinessName()).orElse("");
			try {
				result.setEmpErrorName(empErrorName);
				// 1.入社前退職チェック
				this.retirementCheckBeforeJoinCompany(companyID, employeeID, application.getAppDate().getApplicationDate());
				// 社員IDから申請承認設定情報の取得
				ApprovalFunctionSet approvalFunctionSet = commonAlgorithm.getApprovalFunctionSet(
						companyID, 
						employeeID, 
						appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate(), 
						application.getAppType());
				// 取得したドメインモデル「申請承認機能設定．申請利用設定」．利用区分をチェックする
				Optional<ApplicationUseSetting> opApplicationUseSetting = approvalFunctionSet.getAppUseSetLst().stream()
						.filter(x -> x.getAppType()==application.getAppType()).findAny();
				if(opApplicationUseSetting.isPresent()) {
					if(opApplicationUseSetting.get().getUseDivision()==UseDivision.NOT_USE) {
						// エラーメッセージ(Msg_323)を表示する
						throw new BusinessException("Msg_323", empErrorName);
					}
				}
				// 4.社員の当月の期間を算出する
				PeriodCurrentMonth periodCurrentMonth = otherCommonAlgorithmService.employeePeriodCurrentMonthCalculate(
						companyID, 
						employeeID, 
						appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate());
				// 登録可能期間のチェック(１年以内)
				if(periodCurrentMonth.getStartDate().addYears(1).beforeOrEquals(application.getAppDate().getApplicationDate())) {
					// エラーメッセージ（Msg_1518）を表示する
					throw new BusinessException("Msg_1518", empErrorName);
				}
				// 過去日のチェック
				if(application.getOpAppStartDate().isPresent()) {
					if(application.getOpAppStartDate().get().getApplicationDate().before(periodCurrentMonth.getStartDate())) {
						// エラーメッセージ(Msg_236)
						throw new BusinessException("Msg_236", empErrorName);
					}
				}
				// 2.申請の締め切り期限のチェック
				this.deadlineAppCheck(
						application.getOpAppStartDate().map(x -> x.getApplicationDate()).orElse(null), 
						application.getOpAppEndDate().map(x -> x.getApplicationDate()).orElse(null), 
						periodCurrentMonth.getStartDate(), 
						periodCurrentMonth.getEndDate(), 
						appDispInfoStartupOutput.getAppDispInfoWithDateOutput());
				// 申請対象、申請日、事前事後区分をチェックする
				if(application.getAppDate().getApplicationDate().equals(GeneralDate.today()) &&
						application.getAppType()==ApplicationType.OVER_TIME_APPLICATION && application.getPrePostAtr()==PrePostAtr.PREDICT) {
					// 6.確定チェック（事前残業申請用）
					confirmCheckOvertime(companyID, employeeID, application.getAppDate().getApplicationDate(), appDispInfoStartupOutput);
				} else {
					// 3.確定チェック
					confirmationCheck(companyID, employeeID, application.getAppDate().getApplicationDate(), appDispInfoStartupOutput);
				}
				// 12_承認ルートを取得
				ApprovalRootContentImport_New approvalRootContentImport = commonAlgorithm.getApprovalRoot(
						companyID, 
						employeeID, 
						EmploymentRootAtr.APPLICATION, 
						application.getAppType(), 
						appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate());
				//承認ルートの内容をOUTPUT．List＜社員ID, 承認ルートの内容＞に追加する
				result.getMapEmpContentAppr().put(employeeID, approvalRootContentImport);
				switch (approvalRootContentImport.getErrorFlag()) {
				case NO_APPROVER:
					// エラーメッセージ(Msg_324)を表示する
					throw new BusinessException("Msg_324", empErrorName);
				case APPROVER_UP_10:
					// エラーメッセージ(Msg_237)を表示する
					throw new BusinessException("Msg_237", empErrorName);
				case NO_CONFIRM_PERSON:
					// エラーメッセージ(Msg_238)を表示する
					throw new BusinessException("Msg_238", empErrorName);
				default:
					break;
				}
			} catch (BusinessException e) {
				throw new BusinessException(e.getMessageId(), empErrorName);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		result.setEmpErrorName("");
		
		return result;
	}
}
