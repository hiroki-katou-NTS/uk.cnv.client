package nts.uk.ctx.at.request.dom.application.stamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository_New;
import nts.uk.ctx.at.request.dom.application.ApplicationType_Old;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.dailyattendanceitem.AttendanceResultImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.dailyattendanceitem.DailyAttendanceItemAdapter;
import nts.uk.ctx.at.request.dom.application.stamp.output.AppStampSetOutput;
import nts.uk.ctx.at.request.dom.setting.applicationreason.ApplicationReason;
import nts.uk.ctx.at.request.dom.setting.applicationreason.ApplicationReasonRepository;
import nts.uk.ctx.at.request.dom.setting.company.request.stamp.StampRequestSetting_Old;
import nts.uk.ctx.at.request.dom.setting.company.request.stamp.StampRequestSettingRepository;
import nts.uk.ctx.at.request.dom.setting.request.application.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.request.application.applicationsetting.ApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.request.application.apptypediscretesetting.AppTypeDiscreteSetting;
import nts.uk.ctx.at.request.dom.setting.request.application.apptypediscretesetting.AppTypeDiscreteSettingRepository;
import nts.uk.ctx.at.request.dom.setting.request.application.common.RequiredFlg;
import nts.uk.ctx.at.request.dom.setting.request.gobackdirectlycommon.primitive.AppDisplayAtr;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class AppStampCommonDefaultImpl implements AppStampCommonDomainService {

	@Inject
	private StampRequestSettingRepository stampRequestSettingRepository;
	
	@Inject
	private ApplicationReasonRepository applicationReasonRepository;
	
	@Inject
	private ApplicationSettingRepository applicationSettingRepository;
	
	@Inject
	private EmployeeRequestAdapter employeeAdapter;
	
	@Inject
	private AppStampRepository appStampRepository;
	
	@Inject
	private ApplicationRepository_New applicationRepository;
	
	@Inject
	private AppTypeDiscreteSettingRepository appTypeDiscreteSettingRepository;
	
	@Inject
	private DailyAttendanceItemAdapter dailyAttendanceItemAdapter;
	
	@Override
	public void appReasonCheck(String applicationReason, AppStamp_Old appStamp) {
		appStamp.getApplication_New().setAppReason(new AppReason(applicationReason));
	}

	@Override
	public AppStampSetOutput appStampSet(String companyID) {
		StampRequestSetting_Old stampRequestSetting = this.stampRequestSettingRepository.findByCompanyID(companyID).get();
		List<ApplicationReason> applicationReasons = this.applicationReasonRepository.getReasonByAppType(companyID, ApplicationType_Old.STAMP_APPLICATION.value);
		return new AppStampSetOutput(stampRequestSetting, applicationReasons);
	}

	@Override
	public void validateReason(AppStamp_Old appStamp) {
		/*申請承認設定->申請設定->申請制限設定.申請理由が必須＝trueのとき、申請理由が未入力 (#Msg_115#)
		 ※詳細はアルゴリズム参照*/
		Optional<ApplicationSetting> applicationSettingOp = applicationSettingRepository.getApplicationSettingByComID(appStamp.getApplication_New().getCompanyID());
		ApplicationSetting applicationSetting = applicationSettingOp.get();
		AppTypeDiscreteSetting appTypeDiscreteSetting = appTypeDiscreteSettingRepository.getAppTypeDiscreteSettingByAppType(
				appStamp.getApplication_New().getCompanyID(), 
				ApplicationType_Old.STAMP_APPLICATION.value).get();
		if(appTypeDiscreteSetting.getTypicalReasonDisplayFlg().equals(AppDisplayAtr.DISPLAY)
				||appTypeDiscreteSetting.getDisplayReasonFlg().equals(AppDisplayAtr.DISPLAY)){
			if(applicationSetting.getRequireAppReasonFlg().equals(RequiredFlg.REQUIRED)&&
					Strings.isEmpty(appStamp.getApplication_New().getAppReason().v())){
						throw new BusinessException("Msg_115");
			}
		}
		StampRequestSetting_Old stampRequestSetting = stampRequestSettingRepository.findByCompanyID(appStamp.getApplication_New().getCompanyID()).get();
		appStamp.customValidate(stampRequestSetting.getStampPlaceDisp());
	}

	@Override
	public String getEmployeeName(String employeeID) {
		return employeeAdapter.getEmployeeName(employeeID);
	}

	@Override
	public AppStamp_Old findByID(String companyID, String appID) {
		AppStamp_Old appStamp = appStampRepository.findByAppID(companyID, appID);
		appStamp.setApplication_New(applicationRepository.findByID(companyID, appID).get());
		return appStamp;
	}
	
	@Override
	public List<AttendanceResultImport> getAttendanceResult(String companyID, List<String> employeeIDLst, GeneralDate date, StampRequestMode_Old stampRequestMode){
		List<AttendanceResultImport> attendanceResultLst = new ArrayList<>();
		List<Integer> itemIDGoout = Arrays.asList(
				86,87,88,89,90,
				91,92,93,94,95,
				96,65,67,68,70,
				101,102,103,104,105,
				106,107,108,109,110,
				111,112,113,114,115,
				116,117,118,119,120,
				121,122,123,124,125,
				126,127,128,129,130,
				131,132,133,134,135);
		List<Integer> itemIDChildCare = Arrays.asList(747,748,749,750);
		List<Integer> itemIDCare = Arrays.asList(751,752,753,754);
		List<Integer> itemIDWork = Arrays.asList(
				30,31,33,34,
				40,41,43,44,
				50,51,52,53,
				58,59,60,61,
				66,67,68,69);
		switch (stampRequestMode) {
		case STAMP_GO_OUT_PERMIT: 
			List<Integer> goOutList = new ArrayList<>();
			goOutList.addAll(itemIDGoout);
			goOutList.addAll(itemIDChildCare);
			goOutList.addAll(itemIDCare);
			attendanceResultLst = dailyAttendanceItemAdapter.getValueOf(employeeIDLst, new DatePeriod(date, date), goOutList);
			break;
		case STAMP_WORK:
			attendanceResultLst = dailyAttendanceItemAdapter.getValueOf(employeeIDLst, new DatePeriod(date, date), itemIDWork);
			break;
		case STAMP_CANCEL:
			List<Integer> cancelList = new ArrayList<>();
			cancelList.addAll(itemIDGoout);
			cancelList.addAll(itemIDChildCare);
			cancelList.addAll(itemIDCare);
			cancelList.addAll(itemIDWork);
			attendanceResultLst = dailyAttendanceItemAdapter.getValueOf(employeeIDLst, new DatePeriod(date, date), cancelList);
			break;
		case OTHER:
			List<Integer> otherList = new ArrayList<>();
			otherList.addAll(itemIDGoout);
			otherList.addAll(itemIDChildCare);
			otherList.addAll(itemIDCare);
			otherList.addAll(itemIDWork);
			attendanceResultLst = dailyAttendanceItemAdapter.getValueOf(employeeIDLst, new DatePeriod(date, date), otherList);
			break;
		default:
			break;
		}
		return attendanceResultLst;
	}
	
}
