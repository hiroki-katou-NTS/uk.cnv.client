package nts.uk.ctx.at.request.dom.application.common.adapter.scherec.convert;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeave;
import nts.uk.ctx.at.request.dom.application.appabsence.apptimedigest.TimeDigestApplication;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripInfo;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.holidayshipment.ApplicationForHolidays;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarly;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplication;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.ApplicationTime;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImage;
import nts.uk.ctx.at.request.dom.application.stamp.AppStamp;
import nts.uk.ctx.at.request.dom.application.stamp.DestinationTimeApp;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplication;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChange;
import nts.uk.ctx.at.shared.dom.scherec.application.appabsence.ApplyForLeaveShare;
import nts.uk.ctx.at.shared.dom.scherec.application.appabsence.ReflectFreeTimeAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.bussinesstrip.BusinessTripInfoShare;
import nts.uk.ctx.at.shared.dom.scherec.application.bussinesstrip.BusinessTripWorkTime;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationDateShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.StampRequestModeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.furiapp.AbsenceLeaveAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.furiapp.RecruitmentAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.furiapp.TypeApplicationHolidaysShare;
import nts.uk.ctx.at.shared.dom.scherec.application.gobackdirectly.GoBackDirectlyShare;
import nts.uk.ctx.at.shared.dom.scherec.application.holidayworktime.AppHolidayWorkShare;
import nts.uk.ctx.at.shared.dom.scherec.application.lateleaveearly.ArrivedLateLeaveEarlyShare;
import nts.uk.ctx.at.shared.dom.scherec.application.lateleaveearly.LateCancelationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.lateleaveearly.LateOrEarlyAtrShare;
import nts.uk.ctx.at.shared.dom.scherec.application.lateleaveearly.TimeReportShare;
import nts.uk.ctx.at.shared.dom.scherec.application.optional.OptionalItemApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.AppOverTimeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.ApplicationTimeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.AttendanceTypeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.HolidayMidNightTimeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.OverTimeShiftNightShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.OvertimeApplicationSettingShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.ReasonDivergenceShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppRecordImageShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppStampShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.DestinationTimeAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.DestinationTimeZoneAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.EngraveShareAtr;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppEnumShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppOtherShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeZoneStampClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.timeleaveapplication.TimeDigestApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.timeleaveapplication.TimeLeaveApplicationDetailShare;
import nts.uk.ctx.at.shared.dom.scherec.application.timeleaveapplication.TimeLeaveApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.workchange.AppWorkChangeShare;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceReasonContent;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

public class ConvertApplicationToShare {

	public static ApplicationShare toOnlyAppliction(Application application) {
		ApplicationShare appShare = new ApplicationShare(application.getVersion(), application.getAppID(),
				PrePostAtrShare.valueOf(application.getPrePostAtr().value), application.getEmployeeID(),
				ApplicationTypeShare.valueOf(application.getAppType().value),
				new ApplicationDateShare(application.getAppDate().getApplicationDate()),
				application.getEnteredPersonID(), application.getInputDate());
		appShare.setOpStampRequestMode(
				application.getOpStampRequestMode().map(x -> StampRequestModeShare.valueOf(x.value)));

		appShare.setOpAppStartDate(
				application.getOpAppStartDate().map(x -> new ApplicationDateShare(x.getApplicationDate())));

		appShare.setOpAppEndDate(
				application.getOpAppEndDate().map(x -> new ApplicationDateShare(x.getApplicationDate())));

		return appShare;
	}
	
	public static ApplicationShare toAppliction(Application application, GeneralDate dateTarget) {

		ApplicationShare appShare = toOnlyAppliction(application);

		return appDetail(appShare, application, dateTarget);
	}

	private static ApplicationShare appDetail(ApplicationShare appShare, Application application, GeneralDate dateTarget) {

		switch (application.getAppType()) {
		case OVER_TIME_APPLICATION:

			AppOverTime appOver = (AppOverTime) application;

			// 申請時間
			ApplicationTimeShare appTimeShare = converAppTime(appOver.getApplicationTime());

			AppOverTimeShare overShare = new AppOverTimeShare(appTimeShare,
					appOver.getBreakTimeOp().orElse(new ArrayList<>()),
					appOver.getWorkHoursOp().orElse(new ArrayList<>()), appOver.getWorkInfoOp());
			overShare.setApplication(appShare);
			return overShare;

		case ABSENCE_APPLICATION:
			ApplyForLeave appAbsence = (ApplyForLeave) application;
			val reflectFreeTimeDom = appAbsence.getReflectFreeTimeApp();
			ReflectFreeTimeAppShare reflectFreeTimeApp = new ReflectFreeTimeAppShare(
					reflectFreeTimeDom.getWorkingHours().orElse(new ArrayList<>()),
					reflectFreeTimeDom.getTimeDegestion().map(x -> convertTimeDigest(x)),
					reflectFreeTimeDom.getWorkInfo(), reflectFreeTimeDom.getWorkChangeUse());

			return new ApplyForLeaveShare(appShare, reflectFreeTimeApp);

		case WORK_CHANGE_APPLICATION:
			AppWorkChange appWCh = (AppWorkChange) application;
			return new AppWorkChangeShare(appWCh.getStraightGo(), appWCh.getStraightBack(), appWCh.getOpWorkTypeCD(),
					appWCh.getOpWorkTimeCD(), appWCh.getTimeZoneWithWorkNoLst(), appShare);

		case BUSINESS_TRIP_APPLICATION:
			BusinessTrip bussinessTrip = (BusinessTrip) application;
			BusinessTripInfo info = bussinessTrip.getInfos().stream().filter(x -> x.getDate().equals(dateTarget))
					.findFirst().orElse(null);
			return new BusinessTripInfoShare(appShare, info.getWorkInformation(), info.getWorkingHours().stream()
							.map(y -> new BusinessTripWorkTime(y.getWorkNo(),
									y.getStartDate(),
									y.getEndDate()))
							.collect(Collectors.toList()));

		case GO_RETURN_DIRECTLY_APPLICATION:
			GoBackDirectly goBack = (GoBackDirectly) application;
			return new GoBackDirectlyShare(goBack.getStraightDistinction(), goBack.getStraightLine(),
					goBack.getIsChangedWork(), goBack.getDataWork(), appShare);

		case HOLIDAY_WORK_APPLICATION:
			// AppHolidayWorkShare
			AppHolidayWork appHolWork = (AppHolidayWork) application;

			return new AppHolidayWorkShare(appShare, appHolWork.getWorkInformation(),
					converAppTime(appHolWork.getApplicationTime()), appHolWork.getBackHomeAtr() == NotUseAtr.USE,
					appHolWork.getGoWorkAtr() == NotUseAtr.USE, appHolWork.getBreakTimeList().orElse(new ArrayList<>()),
					appHolWork.getWorkingTimeList().orElse(new ArrayList<>()));

		case STAMP_APPLICATION:
			if (!application.getOpStampRequestMode().isPresent()
					|| application.getOpStampRequestMode().get() == StampRequestMode.STAMP_ADDITIONAL) {
				// AppStampShare
				AppStamp appStamp = (AppStamp) application;
				return new AppStampShare(appStamp.getListTimeStampApp().stream().map(x -> {
					return new TimeStampAppShare(converDesTimeApp(x.getDestinationTimeApp()), x.getTimeOfDay(),
							x.getWorkLocationCd().map(y -> new WorkLocationCD(y.v())), x.getAppStampGoOutAtr(),
							Optional.empty());// TODO: domain sinsei hasn't workplaceId
				}).collect(Collectors.toList()), // 時刻

						appStamp.getListDestinationTimeApp().stream().map(y -> converDesTimeApp(y))
								.collect(Collectors.toList()), // 時刻の取消

						appStamp.getListTimeStampAppOther().stream().map(x -> {
							return new TimeStampAppOtherShare(new DestinationTimeZoneAppShare(
									x.getDestinationTimeZoneApp().getTimeZoneStampClassification().value,
									x.getDestinationTimeZoneApp().getEngraveFrameNo()), x.getTimeZone());
						}).collect(Collectors.toList()), // 時間帯

						appStamp.getListDestinationTimeZoneApp().stream().map(x -> {
							return new DestinationTimeZoneAppShare(
									TimeZoneStampClassificationShare.valueOf(x.getTimeZoneStampClassification().value),
									x.getEngraveFrameNo());
						}).collect(Collectors.toList()), // 時間帯の取消
						appShare);
			} else {
				// レコーダイメージ申請
				AppRecordImage appImg = (AppRecordImage) application;

				return new AppRecordImageShare(EngraveShareAtr.valueOf(appImg.getAppStampCombinationAtr().value),
						appImg.getAttendanceTime(), appImg.getAppStampGoOutAtr(), appShare);
			}

		case ANNUAL_HOLIDAY_APPLICATION:
			TimeLeaveApplication appTimeLeav = (TimeLeaveApplication) application;

			return new TimeLeaveApplicationShare(appShare, appTimeLeav.getLeaveApplicationDetails().stream().map(x -> {
				return new TimeLeaveApplicationDetailShare(x.getAppTimeType(), x.getTimeZoneWithWorkNoLst(),
						convertTimeDigest(x.getTimeDigestApplication()));
			}).collect(Collectors.toList()));

		case EARLY_LEAVE_CANCEL_APPLICATION:
			ArrivedLateLeaveEarly early = (ArrivedLateLeaveEarly) application;
			return new ArrivedLateLeaveEarlyShare(
					early.getLateCancelation().stream()
							.map(x -> new LateCancelationShare(x.getWorkNo(),
									LateOrEarlyAtrShare.valueOf(x.getLateOrEarlyClassification().value)))
							.collect(Collectors.toList()),
					early.getLateOrLeaveEarlies().stream()
							.map(x -> new TimeReportShare(x.getWorkNo(),
									LateOrEarlyAtrShare.valueOf(x.getLateOrEarlyClassification().value),
									x.getTimeWithDayAttr()))
							.collect(Collectors.toList()),
					appShare);

		case COMPLEMENT_LEAVE_APPLICATION:
			TypeApplicationHolidaysShare typeAppHolidayShare = EnumAdaptor.valueOf(
					((ApplicationForHolidays) application).getTypeApplicationHolidays().value,
					TypeApplicationHolidaysShare.class);
			if (typeAppHolidayShare == TypeApplicationHolidaysShare.Abs) {
				// 振休申請
				AbsenceLeaveApp absence = (AbsenceLeaveApp) application;
				return new AbsenceLeaveAppShare(absence.getWorkingHours(), absence.getWorkInformation(),
						absence.getWorkChangeUse(), absence.getChangeSourceHoliday(), typeAppHolidayShare, appShare);
			} else {
				// 振出申請
				RecruitmentApp recruit = (RecruitmentApp) application;
				return new RecruitmentAppShare(recruit.getWorkInformation(), recruit.getWorkingHours(),
						typeAppHolidayShare, appShare);
			}

		case OPTIONAL_ITEM_APPLICATION:
			OptionalItemApplication optionalApp = (OptionalItemApplication) application;
			return new OptionalItemApplicationShare(optionalApp.getOptionalItems(), appShare);

		default:
			return appShare;
		}
	}

	private static DestinationTimeAppShare converDesTimeApp(DestinationTimeApp app) {
		return new DestinationTimeAppShare(TimeStampAppEnumShare.valueOf(app.getTimeStampAppEnum().value),
				app.getEngraveFrameNo(), StartEndClassificationShare.valueOf(app.getStartEndClassification().value),
				app.getSupportWork());

	}

	private static TimeDigestApplicationShare convertTimeDigest(TimeDigestApplication data) {
		return new TimeDigestApplicationShare(data.getOvertime60H(), data.getNursingTime(), data.getChildTime(),
				data.getTimeOff(), data.getTimeSpecialVacation(), data.getTimeAnnualLeave(),
				data.getSpecialVacationFrameNO());
	}
	
	private static ApplicationTimeShare converAppTime(ApplicationTime appTime) {
		return new ApplicationTimeShare(
				appTime.getApplicationTime().stream().map(x -> {
					return new OvertimeApplicationSettingShare(x.getFrameNo().v(),
							EnumAdaptor.valueOf(x.getAttendanceType().value, AttendanceTypeShare.class),
							x.getApplicationTime().v());
				}).collect(Collectors.toList()), appTime.getFlexOverTime(),
				appTime.getOverTimeShiftNight().map(x -> {
					return new OverTimeShiftNightShare(x.getMidNightHolidayTimes().stream().map(y -> {
						return new HolidayMidNightTimeShare(y.getAttendanceTime(), y.getLegalClf());
					}).collect(Collectors.toList()), 
							x.getMidNightOutSide(), x.getOverTimeMidNight());
				}), appTime.getAnyItem().orElse(new ArrayList<>()), //
				appTime.getReasonDissociation()
						.map(x -> x.stream()
								.map(y -> new ReasonDivergenceShare(y.getReason() == null ? null : new DivergenceReasonContent(y.getReason().v()),
										y.getReasonCode(), y.getDiviationTime()))
								.collect(Collectors.toList()))
						.orElse(new ArrayList<>()));
	}
	
	}
