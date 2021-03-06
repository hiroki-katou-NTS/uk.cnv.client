package nts.uk.ctx.at.request.dom.applicationreflect.service.getapp;

import java.util.Optional;

import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeave;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarly;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplication;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImage;
import nts.uk.ctx.at.request.dom.application.stamp.AppStamp;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplication;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChange;

public class GetApplicationForReflect {

	public static Application getAppData(Require require, String companyId, ApplicationType appType, 
			String appID, Application app) {

		switch (appType) {
		case OVER_TIME_APPLICATION:
			// 0：残業申請
			return require.findOvertime(companyId, appID).map(x -> {
				x.setApplication(app);
				return x;
			}).orElse(null);
		case ABSENCE_APPLICATION:
			//1：休暇申請の反映
			return require.findApplyForLeave(companyId, appID).map(x -> {
				x.setApplication(app);
				return x;
			}).orElse(null);
		case WORK_CHANGE_APPLICATION:
			// 2：勤務変更申請
			return require.findAppWorkCg(companyId, appID, app).orElse(null);
		case BUSINESS_TRIP_APPLICATION:
			// 3：出張申請
			return require.findBusinessTripApp(companyId, appID, app).orElse(null);
		case GO_RETURN_DIRECTLY_APPLICATION:
			// 4：直行直帰申請
			return require.findGoBack(companyId, appID, app).orElse(null);
		case HOLIDAY_WORK_APPLICATION:
			// 6：休日出勤申請
			return require.findAppHolidayWork(companyId, appID).map(x -> {
				x.setApplication(app);
				return x;
			}).orElse(null);
		case STAMP_APPLICATION:
			// 7：打刻申請
			if(app.getOpStampRequestMode().get().equals(StampRequestMode.STAMP_ADDITIONAL)) {
				//打刻申請
				return require.findAppStamp(companyId, appID, app).orElse(null);
			}else {
				//レコーダイメージ申請
				return require.findAppRecordImage(companyId, appID, app).orElse(null);
			}
			
		case ANNUAL_HOLIDAY_APPLICATION:
			// 8：時間休暇申請
			return require.findTimeLeavById(companyId, appID).orElse(null);
		case EARLY_LEAVE_CANCEL_APPLICATION:
			// 9: 遅刻早退取消申請
			return require.findArrivedLateLeaveEarly(companyId, appID, app).orElse(null);
		case COMPLEMENT_LEAVE_APPLICATION:
			// 申請.振休振出申請
			AbsenceLeaveApp absence = require.findAbsenceByID(appID).orElse(null);
			if (absence != null) {
				// 振休申請
				return absence;
			} else {
				// 振出申請
				return require.findRecruitmentByID(appID).orElse(null);
			}

		case OPTIONAL_ITEM_APPLICATION:
			// 任意項目
			return require.getOptionalByAppId(companyId, appID).map(x -> {
				return new OptionalItemApplication(x.getCode(), x.getOptionalItems(), app);
			}).orElse(null);

		default:
			return null;
		}
	}

	public static interface Require {

		public Optional<AppWorkChange> findAppWorkCg(String companyId, String appID, Application app);

		public Optional<GoBackDirectly> findGoBack(String companyId, String appID, Application app);
		
		public Optional<AppStamp> findAppStamp(String companyId, String appID, Application app);

		public Optional<ArrivedLateLeaveEarly> findArrivedLateLeaveEarly(String companyId, String appID, Application application);

		public Optional<BusinessTrip> findBusinessTripApp(String companyId, String appID, Application app);
		
		public Optional<AppRecordImage> findAppRecordImage(String companyId, String appID, Application app);
		
		public Optional<TimeLeaveApplication> findTimeLeavById(String companyId, String appId);
		
		public Optional<AppOverTime> findOvertime(String companyId, String appId);
		
		public Optional<ApplyForLeave> findApplyForLeave(String CID, String appId);
		
		public Optional<AppHolidayWork> findAppHolidayWork(String companyId, String appId);
		
		public Optional<AbsenceLeaveApp> findAbsenceByID(String applicationID);

		public Optional<RecruitmentApp> findRecruitmentByID(String applicationID);
		
		public Optional<OptionalItemApplication> getOptionalByAppId(String companyId, String appId);
	}
}
