package nts.uk.ctx.at.request.app.command.application.holidayshipment.refactor5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.ApplicationDate;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.PeriodCurrentMonth;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.MsgErrorOutput;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.workrecord.remainmanagement.InterimRemainDataMngCheckRegisterRequest;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.AppRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ApplicationType;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.EarchInterimRemainCheck;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainCheckInputParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.PrePostAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RecordRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ScheRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeUseInfor;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;

/**
 * @author thanhpv
 * @URL: UKDesign.UniversalK.就業.KAF_申請.KAF011_振休振出申請.A：振休振出申請（新規）.アルゴリズム.登録前のエラーチェック処理
 */
@Stateless
public class ErrorCheckProcessingBeforeRegistrationKAF011 {

	@Inject
	private PreRegistrationErrorCheck PreRegistrationErrorCheck;
	
	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;
	
	@Inject
	private InterimRemainDataMngCheckRegisterRequest interimRemainDataMngCheckRegister;
	
	@Inject
	private NewBeforeRegister newBeforeRegister;
	
	@Inject
	private HolidayApplicationSettingRepository holidayApplicationSettingRepo;
	
	/**
	 * 登録前のエラーチェック処理(Xử lý error check trước khi đăng ký)
	 * @param companyId 会社ID
	 * @param abs 振休申請
	 * @param rec 振出申請
	 * @param represent 代行申請か
	 * @param opErrorFlag 承認ルートのエラーフラグ
	 * @param opActualContentDisplayLst 表示する実績内容
	 * @param appDispInfoStartupOutput 表示する実績内容 
	 * 
	 */
	public void processing(String companyId, Optional<AbsenceLeaveApp> abs, Optional<RecruitmentApp> rec, boolean represent, 
	        List<MsgErrorOutput> msgErrorLst, List<ActualContentDisplay> opActualContentDisplayLst, 
	        AppDispInfoStartupOutput appDispInfoStartup, List<PayoutSubofHDManagement> payoutSubofHDManagements, 
	        boolean checkFlag, boolean existFlag) {
		
		//登録前エラーチェック（新規）(Check error trước khi đăng ký (New)
		this.PreRegistrationErrorCheck.errorCheck(companyId, abs, rec, 
												opActualContentDisplayLst,
												appDispInfoStartup.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0), 
												appDispInfoStartup.getAppDispInfoWithDateOutput().getEmpHistImport().getEmploymentCode(),
												Optional.empty(), 
												Optional.empty(),
												payoutSubofHDManagements, 
												checkFlag);
		//振休残数不足チェック (Check số nghỉ bù thiếu)
//		this.checkForInsufficientNumberOfHolidays(companyId, appDispInfoStartup.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(), abs, rec);
		
//		boolean existFlag = false;
//		if (abs.isPresent() && rec.isPresent()) {
//		    existFlag = true;
//		}
		
		if(rec.isPresent()) {
			this.newBeforeRegister.processBeforeRegister_New(
			        companyId, 
			        EmploymentRootAtr.APPLICATION, 
			        represent, 
			        rec.get(), 
			        null, 
			        msgErrorLst, 
			        new ArrayList<>(), 
			        appDispInfoStartup,
			        Arrays.asList(rec.get().getWorkInformation().getWorkTypeCode().v()), 
			        Optional.empty(), 
			        rec.get().getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v), 
			        existFlag);
		}
		
		if(abs.isPresent()) {
			this.newBeforeRegister.processBeforeRegister_New(
			        companyId, 
			        EmploymentRootAtr.APPLICATION, 
			        represent, 
			        abs.get(), 
			        null, 
			        msgErrorLst, 
			        new ArrayList<>(), 
			        appDispInfoStartup,
			        Arrays.asList(abs.get().getWorkInformation().getWorkTypeCode().v()), 
                    Optional.empty(), 
                    abs.get().getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v), 
                    existFlag);
		}
	}
	
	
	/**
	 * 振休残数不足チェック (Check số nghỉ bù thiếu)
	 */
	/**
	public void checkForInsufficientNumberOfHolidays(String companyId, String employeeId, Optional<AbsenceLeaveApp> abs, Optional<RecruitmentApp> rec) {
		//4.社員の当月の期間を算出する (Tính thời gian tháng hiện tại của nhân viên)
		PeriodCurrentMonth periodCurrentMonth = this.otherCommonAlgorithm.employeePeriodCurrentMonthCalculate(companyId, employeeId, GeneralDate.today());
		
		if(abs.isPresent()) {
			InterimRemainCheckInputParam inputParam = new InterimRemainCheckInputParam(
					companyId, 
					employeeId, 
					new DatePeriod(periodCurrentMonth.getStartDate(), periodCurrentMonth.getStartDate().addYears(1).addDays(-1)), 
					false, 
					abs.get().getAppDate().getApplicationDate(), 
					new DatePeriod(abs.get().getAppDate().getApplicationDate(), abs.get().getAppDate().getApplicationDate()), 
					true, 
					new ArrayList<RecordRemainCreateInfor>(), 
					new ArrayList<ScheRemainCreateInfor>(), 
					createAppRemain(abs.get()), 
					false, 
					true, // #116616
					false, 
					false, 
					true, 
					false, 
					true, 
					false, 
					false);
			EarchInterimRemainCheck earchInterimRemainCheck = this.interimRemainDataMngCheckRegister.checkRegister(inputParam); 
			if(!earchInterimRemainCheck.isChkSubHoliday()) {
				throw new BusinessException("Msg_1409", "代休不足区分");
			}
			if(!earchInterimRemainCheck.isChkPause()) {
				throw new BusinessException("Msg_1409", "振休不足区分");
			}
			if(!earchInterimRemainCheck.isChkAnnual()) {
				throw new BusinessException("Msg_1409", "年休不足区分");
			}
			if(!earchInterimRemainCheck.isChkFundingAnnual()) {
				throw new BusinessException("Msg_1409", "積休不足区分");
			}
			if(!earchInterimRemainCheck.isChkSpecial()) {
				throw new BusinessException("Msg_1409", "特休不足区分 ");
			}
		}
	}
	*/
	
	private List<AppRemainCreateInfor> createAppRemain(AbsenceLeaveApp application) {
	    List<AppRemainCreateInfor> result = new ArrayList<AppRemainCreateInfor>();
        String workTypeCode = null;
        String workTimeCode = null;
        
        if (application.getWorkInformation() != null 
                && application.getWorkInformation().getWorkTypeCode() != null) {
            workTypeCode = application.getWorkInformation().getWorkTypeCode().v();
        }
        
        if (application.getWorkInformation() != null 
                && application.getWorkInformation().getWorkTimeCodeNotNull().isPresent()) {
            workTimeCode = application.getWorkInformation().getWorkTypeCode().v();
        }
        
        AppRemainCreateInfor appRemainCreateInfor = new AppRemainCreateInfor(
                application.getEmployeeID(), 
                application.getAppID(), 
                application.getInputDate(), 
                application.getAppDate().getApplicationDate(), 
                EnumAdaptor.valueOf(application.getPrePostAtr().value, PrePostAtr.class), 
                EnumAdaptor.valueOf(application.getAppType().value, ApplicationType.class), 
                Optional.ofNullable(workTypeCode), 
                Optional.ofNullable(workTimeCode),  
                new ArrayList<VacationTimeUseInfor>(), 
                Optional.empty(), 
                Optional.empty(), 
                application.getOpAppStartDate().map(ApplicationDate::getApplicationDate),
                application.getOpAppEndDate().map(ApplicationDate::getApplicationDate), 
                new ArrayList<GeneralDate>(), 
                Optional.empty(), Optional.empty());
        result.add(appRemainCreateInfor);
        
	    return result;
	}
}


