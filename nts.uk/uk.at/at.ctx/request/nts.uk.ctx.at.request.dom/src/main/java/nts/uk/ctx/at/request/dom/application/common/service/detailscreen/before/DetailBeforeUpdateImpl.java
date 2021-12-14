package nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationDate;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.PeriodCurrentMonth;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.overtime.service.CheckWorkingInfoResult;
import nts.uk.ctx.at.request.dom.workrecord.remainmanagement.InterimRemainDataMngCheckRegisterRequest;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.AppRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.EarchInterimRemainCheck;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainCheckInputParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RecordRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ScheRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeUseInfor;
import nts.uk.ctx.at.shared.dom.worktype.specialholidayframe.SpecialHdFrameNo;
@Stateless
public class DetailBeforeUpdateImpl implements DetailBeforeUpdate {

	@Inject
	private NewBeforeRegister newBeforeRegister;
	
	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;
	
	@Inject
	private ApplicationRepository applicationRepository;
	
	@Inject
    private InterimRemainDataMngCheckRegisterRequest interimRemainDataMngCheckRegisterRequest;
	
	/**
	 * 4-1.詳細画面登録前の処理
	 */
	public void processBeforeDetailScreenRegistration(String companyID, String employeeID, GeneralDate appDate,
			int employeeRouteAtr, String appID, PrePostAtr postAtr, int version, String wkTypeCode,
			String wkTimeCode, AppDispInfoStartupOutput appDispInfoStartupOutput, List<String> workTypeCds, 
            Optional<TimeDigestionParam> timeDigestionUsageInfor, boolean flag, Optional<String> workTypeRemainChk, Optional<String> workTimeRemainChk) {
		//勤務種類、就業時間帯チェックのメッセージを表示
		displayWorkingHourCheck(companyID, wkTypeCode, wkTimeCode);
		// 選択した勤務種類の矛盾チェック(check sự mâu thuẫn của worktype đã chọn)
		// selectedWorkTypeConflictCheck();
		
		Application application = applicationRepository.findByID(companyID, appID).get();
		GeneralDate startDate = application.getAppDate().getApplicationDate();
		GeneralDate endDate = application.getAppDate().getApplicationDate();
		// 申請する開始日～申請する終了日までループする
		for(GeneralDate loopDate = startDate; loopDate.beforeOrEquals(endDate); loopDate = loopDate.addDays(1)){
			if(application.getPrePostAtr() == PrePostAtr.PREDICT && application.getAppType() == ApplicationType.OVER_TIME_APPLICATION){
				newBeforeRegister.confirmCheckOvertime(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}else{
				// アルゴリズム「確定チェック」を実施する
				newBeforeRegister.confirmationCheck(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}
		}

		// アルゴリズム「排他チェック」を実行する (thực hiện xử lý 「check version」)
		exclusiveCheck(companyID, appID, version);
		
		// 4.社員の当月の期間を算出する
		PeriodCurrentMonth periodCurrentMonth = otherCommonAlgorithm.employeePeriodCurrentMonthCalculate(companyID, employeeID, GeneralDate.today());
        
        // 申請期間から休日の申請日を取得する
        List<GeneralDate> holidays = otherCommonAlgorithm.lstDateIsHoliday(
                employeeID, 
                new DatePeriod(startDate, endDate), 
                appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(new ArrayList<ActualContentDisplay>()));
        
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
                    workTypeRemainChk,
                    workTimeRemainChk, 
                    vacationTimeInforNews, 
                    Optional.of(application.getAppType().equals(ApplicationType.HOLIDAY_WORK_APPLICATION) && timeDigestionUsageInfor.isPresent() 
                            ? timeDigestionUsageInfor.get().getOverHolidayTime() : 0), 
                    Optional.of(application.getAppType().equals(ApplicationType.OVER_TIME_APPLICATION) && timeDigestionUsageInfor.isPresent()
                            ? timeDigestionUsageInfor.get().getOverHolidayTime() : 0), 
                    application.getOpAppStartDate().map(ApplicationDate::getApplicationDate), 
                    application.getOpAppEndDate().map(ApplicationDate::getApplicationDate), 
                    holidays, 
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
//        if (earchInterimRemainCheck.isChkSubHoliday() 
//                || earchInterimRemainCheck.isChkPause()
//                || earchInterimRemainCheck.isChkAnnual()
//                || earchInterimRemainCheck.isChkFundingAnnual()
//                || earchInterimRemainCheck.isChkSpecial()
//                || earchInterimRemainCheck.isChkSuperBreak()
//                || earchInterimRemainCheck.isChkChildNursing()
//                || earchInterimRemainCheck.isChkLongTermCare()) {
//            // エラーメッセージ（Msg_1409）
//            throw new BusinessException("Msg_1409");
//        }
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
	}
	
	/**
	 * 勤務種類、就業時間帯チェックのメッセージを表示
	 * @param companyID
	 * @param wkTypeCode
	 * @param wkTimeCode
	 */
	@Override
	public void displayWorkingHourCheck(String companyID, String wkTypeCode, String wkTimeCode) {
		// 12.マスタ勤務種類、就業時間帯データをチェック
		CheckWorkingInfoResult checkResult = otherCommonAlgorithm.checkWorkingInfo(companyID, wkTypeCode, wkTimeCode);
		if (checkResult.isWkTypeError() || checkResult.isWkTimeError()) {
			String text = "";
			if (checkResult.isWkTypeError()) {
				text = "勤務種類コード" + wkTypeCode;
			}
			if (checkResult.isWkTimeError()) {
				text = "就業時間帯コード" + wkTimeCode;
			}
			if (checkResult.isWkTypeError() && checkResult.isWkTimeError()) {
				text = "勤務種類コード" + wkTypeCode + "、" + "就業時間帯コード" + wkTimeCode;
				;
			}
			throw new BusinessException("Msg_1530", text);
		}
	}

	/**
	 * 1.排他チェック
	 */
	public void exclusiveCheck(String companyID, String appID, int version) {
		if (applicationRepository.findByID(companyID, appID).isPresent()) {
			Application application = applicationRepository.findByID(companyID, appID).get();
			if (application.getVersion() != version) {
				throw new BusinessException("Msg_197");
			}
		} else {
			throw new BusinessException("Msg_198");
		}
	}

	/**
	 * 4-1.詳細画面登録前の処理 (CMM045)
	 * 
	 * @author hoatt
	 */
	@Override
	public boolean processBefDetailScreenReg(String companyID, String employeeID, GeneralDate appDate,
			int employeeRouteAtr, String appID, PrePostAtr postAtr, int version, AppDispInfoStartupOutput appDispInfoStartupOutput) {
		// 選択した勤務種類の矛盾チェック(check sự mâu thuẫn của worktype đã chọn)
		// selectedWorkTypeConflictCheck();

		Application application = applicationRepository.findByID(companyID, appID).get();
		GeneralDate startDate = application.getAppDate().getApplicationDate();
		GeneralDate endDate = application.getAppDate().getApplicationDate();
		// 申請する開始日～申請する終了日までループする
		for(GeneralDate loopDate = startDate; loopDate.beforeOrEquals(endDate); loopDate = loopDate.addDays(1)){
			if(loopDate.equals(GeneralDate.today()) && application.getPrePostAtr().equals(PrePostAtr.PREDICT) && application.isOverTimeApp()){
				newBeforeRegister.confirmCheckOvertime(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}else{
				// アルゴリズム「確定チェック」を実施する
				newBeforeRegister.confirmationCheck(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}
		}
		
		// アルゴリズム「排他チェック」を実行する(thực hiện xử lý 「排他チェック」)
		return exclusiveCheckErr(companyID, appID, version);
	}

	@Override
	public boolean exclusiveCheckErr(String companyID, String appID, int version) {
		if (applicationRepository.findByID(companyID, appID).isPresent()) {
			Application application = applicationRepository.findByID(companyID, appID).get();
			if (application.getVersion() != version) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
}
