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
	 * 4-1.??????????????????????????????
	 */
	public void processBeforeDetailScreenRegistration(String companyID, String employeeID, GeneralDate appDate,
			int employeeRouteAtr, String appID, PrePostAtr postAtr, int version, String wkTypeCode,
			String wkTimeCode, AppDispInfoStartupOutput appDispInfoStartupOutput, List<String> workTypeCds, 
            Optional<TimeDigestionParam> timeDigestionUsageInfor, boolean flag, Optional<String> workTypeRemainChk, Optional<String> workTimeRemainChk) {
		//?????????????????????????????????????????????????????????????????????
		displayWorkingHourCheck(companyID, wkTypeCode, wkTimeCode);
		// ?????????????????????????????????????????????(check s??? m??u thu???n c???a worktype ???? ch???n)
		// selectedWorkTypeConflictCheck();
		
		Application application = applicationRepository.findByID(companyID, appID).get();
		GeneralDate startDate = application.getAppDate().getApplicationDate();
		GeneralDate endDate = application.getAppDate().getApplicationDate();
		// ??????????????????????????????????????????????????????????????????
		for(GeneralDate loopDate = startDate; loopDate.beforeOrEquals(endDate); loopDate = loopDate.addDays(1)){
			if(application.getPrePostAtr() == PrePostAtr.PREDICT && application.getAppType() == ApplicationType.OVER_TIME_APPLICATION){
				newBeforeRegister.confirmCheckOvertime(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}else{
				// ?????????????????????????????????????????????????????????
				newBeforeRegister.confirmationCheck(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}
		}

		// ????????????????????????????????????????????????????????? (th???c hi???n x??? l?? ???check version???)
		exclusiveCheck(companyID, appID, version);
		
		// 4.???????????????????????????????????????
		PeriodCurrentMonth periodCurrentMonth = otherCommonAlgorithm.employeePeriodCurrentMonthCalculate(companyID, employeeID, GeneralDate.today());
        
        // ???????????????????????????????????????????????????
        List<GeneralDate> holidays = otherCommonAlgorithm.lstDateIsHoliday(
                employeeID, 
                new DatePeriod(startDate, endDate), 
                appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(new ArrayList<ActualContentDisplay>()));
        
        if (!flag) {
            // ??????????????????????????????
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
            
            // ?????????????????? or ?????????????????? or ?????????????????? or ?????????????????? or ?????????????????????or ?????????????????????OR??????????????????????????????OR????????????????????? = true??????????????????
//        if (earchInterimRemainCheck.isChkSubHoliday() 
//                || earchInterimRemainCheck.isChkPause()
//                || earchInterimRemainCheck.isChkAnnual()
//                || earchInterimRemainCheck.isChkFundingAnnual()
//                || earchInterimRemainCheck.isChkSpecial()
//                || earchInterimRemainCheck.isChkSuperBreak()
//                || earchInterimRemainCheck.isChkChildNursing()
//                || earchInterimRemainCheck.isChkLongTermCare()) {
//            // ???????????????????????????Msg_1409???
//            throw new BusinessException("Msg_1409");
//        }
            if (earchInterimRemainCheck.isChkSubHoliday()) {
                throw new BusinessException("Msg_1409", "??????");
            }
            if (earchInterimRemainCheck.isChkPause()) {
                throw new BusinessException("Msg_1409", "??????");
            }
            if (earchInterimRemainCheck.isChkAnnual()) {
                throw new BusinessException("Msg_1409", "??????");
            }
            if (earchInterimRemainCheck.isChkFundingAnnual()) {
                throw new BusinessException("Msg_1409", "??????");
            }
            if (earchInterimRemainCheck.isChkSpecial()) {
                throw new BusinessException("Msg_1409", "??????");
            }
            if (earchInterimRemainCheck.isChkSuperBreak()) {
                throw new BusinessException("Msg_1409", "??????");
            }
            if (earchInterimRemainCheck.isChkChildNursing()) {
                throw new BusinessException("Msg_1409", "????????????");
            }
            if (earchInterimRemainCheck.isChkLongTermCare()) {
                throw new BusinessException("Msg_1409", "??????");
            }
        }
	}
	
	/**
	 * ?????????????????????????????????????????????????????????????????????
	 * @param companyID
	 * @param wkTypeCode
	 * @param wkTimeCode
	 */
	@Override
	public void displayWorkingHourCheck(String companyID, String wkTypeCode, String wkTimeCode) {
		// 12.???????????????????????????????????????????????????????????????
		CheckWorkingInfoResult checkResult = otherCommonAlgorithm.checkWorkingInfo(companyID, wkTypeCode, wkTimeCode);
		if (checkResult.isWkTypeError() || checkResult.isWkTimeError()) {
			String text = "";
			if (checkResult.isWkTypeError()) {
				text = "?????????????????????" + wkTypeCode;
			}
			if (checkResult.isWkTimeError()) {
				text = "????????????????????????" + wkTimeCode;
			}
			if (checkResult.isWkTypeError() && checkResult.isWkTimeError()) {
				text = "?????????????????????" + wkTypeCode + "???" + "????????????????????????" + wkTimeCode;
				;
			}
			throw new BusinessException("Msg_1530", text);
		}
	}

	/**
	 * 1.??????????????????
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
	 * 4-1.?????????????????????????????? (CMM045)
	 * 
	 * @author hoatt
	 */
	@Override
	public boolean processBefDetailScreenReg(String companyID, String employeeID, GeneralDate appDate,
			int employeeRouteAtr, String appID, PrePostAtr postAtr, int version, AppDispInfoStartupOutput appDispInfoStartupOutput) {
		// ?????????????????????????????????????????????(check s??? m??u thu???n c???a worktype ???? ch???n)
		// selectedWorkTypeConflictCheck();

		Application application = applicationRepository.findByID(companyID, appID).get();
		GeneralDate startDate = application.getAppDate().getApplicationDate();
		GeneralDate endDate = application.getAppDate().getApplicationDate();
		// ??????????????????????????????????????????????????????????????????
		for(GeneralDate loopDate = startDate; loopDate.beforeOrEquals(endDate); loopDate = loopDate.addDays(1)){
			if(loopDate.equals(GeneralDate.today()) && application.getPrePostAtr().equals(PrePostAtr.PREDICT) && application.isOverTimeApp()){
				newBeforeRegister.confirmCheckOvertime(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}else{
				// ?????????????????????????????????????????????????????????
				newBeforeRegister.confirmationCheck(companyID, application.getEmployeeID(), loopDate, appDispInfoStartupOutput);
			}
		}
		
		// ?????????????????????????????????????????????????????????(th???c hi???n x??? l?? ????????????????????????)
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
