package nts.uk.ctx.at.request.dom.application.overtime.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.i18n.I18NText;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository_New;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.Application_New;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.UseAtr;
import nts.uk.ctx.at.request.dom.application.appabsence.AllDayHalfDayLeaveAtr;
import nts.uk.ctx.at.request.dom.application.appabsence.AppAbsence;
import nts.uk.ctx.at.request.dom.application.appabsence.AppAbsenceRepository;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AppCompltLeaveSyncOutput;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectlyRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.CompltLeaveSimMngRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.LateOrLeaveEarly;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.LateOrLeaveEarlyRepository;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeRepository;
import nts.uk.ctx.at.request.dom.application.stamp.AppStamp;
import nts.uk.ctx.at.request.dom.application.stamp.AppStampAtr;
import nts.uk.ctx.at.request.dom.application.stamp.AppStampOnlineRecord;
import nts.uk.ctx.at.request.dom.application.stamp.AppStampRepository;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChange;
import nts.uk.ctx.at.request.dom.application.workchange.IAppWorkChangeRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class ApplicationContenServicetImpl implements IApplicationContentService {

	@Inject
	private AppAbsenceRepository absenRepo;

	@Inject
	private GoBackDirectlyRepository goBackRepo;

	@Inject
	private AppHolidayWorkRepository holidayRepo;

	@Inject
	private LateOrLeaveEarlyRepository lateLeaveEarlyRepo;

	@Inject
	private OvertimeRepository overtimeRepo;

	@Inject
	private AppStampRepository appStampRepo;

	@Inject
	private IAppWorkChangeRepository workChangeRepo;

	@Inject
	private ApplicationRepository_New repoApp;

	@Inject
	private AbsenceLeaveAppRepository appLeaveRepo;

	@Inject
	private RecruitmentAppRepository recAppRepo;

	@Inject
	private CompltLeaveSimMngRepository compltRepo;

	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;

	@Override
	public String getApplicationContent(Application_New app) {
		String appReason = app.getAppReason().toString();
		ApplicationType appType = app.getAppType();
		String appID = app.getAppID();
		String companyID = AppContexts.user().companyId();
		switch (app.getAppType()) {
		case OVER_TIME_APPLICATION: {
			// OK
			return this.getOverTimeAppContent(app, companyID, appID, appReason);
		}
		case ABSENCE_APPLICATION: {
			// OK
			return this.getAbsenAppContent(app, companyID, appID, appReason);
		}
		case WORK_CHANGE_APPLICATION: {
			// OK
			return this.getWorkChangeAppContent(app, companyID, appID, appReason);
		}
		case BUSINESS_TRIP_APPLICATION: {
			// Pending
			return this.getBusinessTripContent(app, companyID, appID, appReason);
		}
		case GO_RETURN_DIRECTLY_APPLICATION: {
			// OK
			return this.getGoReturnDirectlyAppContent(app, companyID, appID, appReason);
		}
		case BREAK_TIME_APPLICATION: {
			// OK
			return this.getBreakTimeAppContent(app, companyID, appID, appReason);
		}
		case STAMP_APPLICATION: {
			// OK
			return this.getStampAppContent(app, companyID, appID, appReason);
		}
		case ANNUAL_HOLIDAY_APPLICATION: {
			// Pending
			return this.getAnnualAppContent(app, companyID, appID, appReason);
		}
		case EARLY_LEAVE_CANCEL_APPLICATION: {
			// OK
			return this.getEarlyLeaveAppContent(app, companyID, appID, appReason);
		}
		case COMPLEMENT_LEAVE_APPLICATION: {
			// Handling
			return this.getComplementLeaveAppContent(app, companyID, appID, appReason);
		}
		case STAMP_NR_APPLICATION: {
			// NO SPEC
			return this.getStampNrAppContent(app, companyID, appID, appReason);
		}
		case LONG_BUSINESS_TRIP_APPLICATION: {
			// Pending
			return this.getLongBusinessTripAppContent(app, companyID, appID, appReason);
		}
		case BUSINESS_TRIP_APPLICATION_OFFICE_HELPER: {
			// NO SPEC
			return this.getBusinessTripOfficeAppContent(app, companyID, appID, appReason);
		}
		case APPLICATION_36: {
			// NO SPEC
			return this.getApp36AppContent(app, companyID, appID, appReason);
		}
		}
		return "";
	}

	private String getOverTimeAppContent(Application_New app, String companyID, String appID, String appReason) {
		// OK
		String content = "";
		Optional<AppOverTime> op_overTime = overtimeRepo.getAppOvertimeFrame(companyID, appID);
		if (op_overTime.isPresent()) {
			AppOverTime overTime = op_overTime.get();
			switch (app.getPrePostAtr()) {
			case PREDICT: {
				// OK
				content += I18NText.getText("CMM045_268") + " " + clockShorHm(overTime.getWorkClockFrom1())
						+ I18NText.getText("CMM045_100") + clockShorHm(overTime.getWorkClockTo1());
				content += (!Objects.isNull(overTime.getWorkClockFrom1())
						? overTime.getWorkClockFrom1() + I18NText.getText("CMM045_100") : "");
				content += (!Objects.isNull(overTime.getWorkClockTo1()) ? overTime.getWorkClockTo1() : "");
				String moreInf = "";
				int count = 0;
				int totalWorkUnit = 0;
				for (val x : overTime.getOverTimeInput()) {
					if (x.getApplicationTime().v() > 0) {
						totalWorkUnit += x.getApplicationTime().v();
						if (count < 3) {
							String type = "";
							switch (x.getAttendanceType()) {
							case BONUSPAYTIME: {
								type = "加給時間";
							}
							case BONUSSPECIALDAYTIME: {
								type = "特定日加給時間";
							}
							case BREAKTIME: {
								type = "休出時間";
							}
							case NORMALOVERTIME: {
								type = "残業時間";
							}
							case RESTTIME: {
								type = "休憩時間";
							}
							}
							moreInf += type + " " + x.getApplicationTime().v() + " ";
						}
						count++;
					}
				}
				if (overTime.getOverTimeShiftNight() > 0) {
					totalWorkUnit += overTime.getOverTimeShiftNight();
					if (count < 3)
						moreInf += clockShorHm(overTime.getOverTimeShiftNight()) + " ";
					count++;
				}
				if (overTime.getFlexExessTime() > 0) {
					totalWorkUnit += overTime.getFlexExessTime();
					if (count < 3)
						moreInf += clockShorHm(overTime.getFlexExessTime()) + " ";
					count++;
				}
				String frameInfo = moreInf + (count > 3 ? I18NText.getText("CMM045_230", count - 3 + "") : "");
				content += I18NText.getText("CMM045_269") + totalWorkUnit + I18NText.getText("CMM045_279", frameInfo);
				break;
			}
			case POSTERIOR: {
				// PRE
				List<Application_New> listPreApp = repoApp.getApp(app.getEmployeeID(), app.getAppDate(),
						PrePostAtr.PREDICT.value, ApplicationType.OVER_TIME_APPLICATION.value);
				Application_New preApp = (listPreApp.size() > 0 ? listPreApp.get(0) : null);
				AppOverTime preOverTime = !Objects.isNull(preApp)
						? overtimeRepo.getAppOvertimeFrame(companyID, preApp.getAppID()).orElse(null) : null;
				if (!Objects.isNull(preOverTime)) {
					content += I18NText.getText("CMM045_272") + " " + I18NText.getText("CMM045_268") + " "
							+ clockShorHm(preOverTime.getWorkClockFrom1()) + I18NText.getText("CMM045_100")
							+ clockShorHm(preOverTime.getWorkClockTo1());
					content += (!Objects.isNull(preOverTime.getWorkClockFrom1())
							? preOverTime.getWorkClockFrom1() + I18NText.getText("CMM045_100") : "");
					content += (!Objects.isNull(preOverTime.getWorkClockTo1()) ? preOverTime.getWorkClockTo1() : "");
					String moreInf = "";
					int count = 0;
					int totalWorkUnit = 0;
					for (val x : preOverTime.getOverTimeInput()) {
						if (x.getApplicationTime().v() > 0) {
							totalWorkUnit += x.getApplicationTime().v();
							if (count < 3) {
								String type = "";
								switch (x.getAttendanceType()) {
								case BONUSPAYTIME: {
									type = "加給時間";
								}
								case BONUSSPECIALDAYTIME: {
									type = "特定日加給時間";
								}
								case BREAKTIME: {
									type = "休出時間";
								}
								case NORMALOVERTIME: {
									type = "残業時間";
								}
								case RESTTIME: {
									type = "休憩時間";
								}
								}
								moreInf += type + " " + x.getApplicationTime().v() + " ";
								count++;
							}
						}
					}
					if (preOverTime.getOverTimeShiftNight() > 0) {
						totalWorkUnit += preOverTime.getOverTimeShiftNight();
						if (count < 3)
							moreInf += clockShorHm(preOverTime.getOverTimeShiftNight()) + " ";
						count++;
					}
					if (preOverTime.getFlexExessTime() > 0) {
						totalWorkUnit += preOverTime.getFlexExessTime();
						if (count < 3)
							moreInf += clockShorHm(preOverTime.getFlexExessTime()) + " ";
						count++;
					}
					String frameInfo = moreInf + (count > 3 ? I18NText.getText("CMM045_230", count - 3 + "") : "");
					content += I18NText.getText("CMM045_269") + totalWorkUnit
							+ I18NText.getText("CMM045_279", frameInfo);
				}

				// AFTER
				content += "\n" + I18NText.getText("CMM045_274") + " " + I18NText.getText("CMM045_268")
						+ clockShorHm(overTime.getWorkClockFrom1()) + I18NText.getText("CMM045_100")
						+ clockShorHm(overTime.getWorkClockTo1());
				content += (!Objects.isNull(overTime.getWorkClockFrom1())
						? overTime.getWorkClockFrom1() + I18NText.getText("CMM045_100") : "");
				content += (!Objects.isNull(overTime.getWorkClockTo1()) ? overTime.getWorkClockTo1() : "");
				String moreInf = "";
				int count = 0;
				int totalWorkUnit = 0;
				for (val x : overTime.getOverTimeInput()) {
					if (x.getApplicationTime().v() > 0) {
						totalWorkUnit += x.getApplicationTime().v();
						if (count < 3) {
							String type = "";
							switch (x.getAttendanceType()) {
							case BONUSPAYTIME: {
								type = "加給時間";
							}
							case BONUSSPECIALDAYTIME: {
								type = "特定日加給時間";
							}
							case BREAKTIME: {
								type = "休出時間";
							}
							case NORMALOVERTIME: {
								type = "残業時間";
							}
							case RESTTIME: {
								type = "休憩時間";
							}
							}
							moreInf += type + " " + x.getApplicationTime().v() + " ";
						}
						count++;
					}
				}
				if (overTime.getOverTimeShiftNight() > 0) {
					totalWorkUnit += overTime.getOverTimeShiftNight();
					if (count < 3)
						moreInf += clockShorHm(overTime.getOverTimeShiftNight()) + " ";
					count++;
				}
				if (overTime.getFlexExessTime() > 0) {
					totalWorkUnit += overTime.getFlexExessTime();
					if (count < 3)
						moreInf += clockShorHm(overTime.getFlexExessTime()) + " ";
					count++;
				}
				String frameInfo = moreInf + (count > 3 ? I18NText.getText("CMM045_230", count - 3 + "") : "");
				content += I18NText.getText("CMM045_269") + totalWorkUnit + I18NText.getText("CMM045_279", frameInfo);
			}
			}
		}
		return content + "\n" + appReason;
	}

	private String getAbsenAppContent(Application_New app, String companyID, String appID, String appReason) {
		// OK
		String content = I18NText.getText("CMM045_279");
		Optional<AppAbsence> op_appAbsen = absenRepo.getAbsenceByAppId(companyID, appID);
		if (op_appAbsen.isPresent()) {
			AppAbsence appAbsen = op_appAbsen.get();
			if (appAbsen.getAllDayHalfDayLeaveAtr() == AllDayHalfDayLeaveAtr.ALL_DAY_LEAVE) {
				String holidayType = "";
				if (Objects.isNull(appAbsen.getAppForSpecLeave().getRelationshipCD())) {
					switch (appAbsen.getHolidayAppType()) {
					case ANNUAL_PAID_LEAVE: {
						holidayType = "年休名称";
						break;
					}
					case SUBSTITUTE_HOLIDAY: {
						holidayType = "代休名称";
						break;
					}
					case REST_TIME: {
						holidayType = "振休名称";
						break;
					}
					case ABSENCE: {
						holidayType = "欠勤名称";
						break;
					}
					case SPECIAL_HOLIDAY: {
						holidayType = "特別休暇名称";
						break;
					}
					case YEARLY_RESERVE: {
						holidayType = "積立年休名称";
						break;
					}
					case HOLIDAY: {
						holidayType = "休日名称";
						break;
					}
					case DIGESTION_TIME: {
						holidayType = "時間消化名称";
						break;
					}
					}
					content += I18NText.getText("CMM045_248") + I18NText.getText("CMM045_230", holidayType) + "\n"
							+ appReason;
				} else {
					holidayType = "特別休暇";
					content += holidayType + appAbsen.getAppForSpecLeave().getRelationshipCD().v();
					if (appAbsen.getAppForSpecLeave().isMournerFlag()) {
						content += (app.getStartDate().isPresent() && app.getEndDate().isPresent()
								? app.getEndDate().get().compareTo(app.getStartDate().get())
										+ I18NText.getText("CMM045_248")
								: "");
					}
					content += "\n" + appReason;
				}

			} else {
				content += I18NText.getText("CMM045_249")
						+ I18NText.getText("CMM045_230", appAbsen.getWorkTimeCode().v())
						+ (!Objects.isNull(appAbsen.getStartTime1())
								? appAbsen.getStartTime1().v() + I18NText.getText("CMM045_100") : "")
						+ (!Objects.isNull(appAbsen.getEndTime1()) ? appAbsen.getEndTime1().v() : "")
						+ (!Objects.isNull(appAbsen.getStartTime2())
								? appAbsen.getStartTime2().v() + I18NText.getText("CMM045_100") : "")
						+ (!Objects.isNull(appAbsen.getEndTime2()) ? appAbsen.getEndTime2().v() : "");
				content += "\n" + appReason;
			}
		}
		return content + "\n" + appReason;
	}

	private String getWorkChangeAppContent(Application_New app, String companyID, String appID, String appReason) {
		// OK
		String content = I18NText.getText("CMM045_250");
		Optional<AppWorkChange> op_appWork = workChangeRepo.getAppworkChangeById(companyID, appID);
		if (op_appWork.isPresent()) {
			AppWorkChange appWork = op_appWork.get();
			content += appWork.getWorkTypeName() + appWork.getWorkTimeName();
			if (!Objects.isNull(appWork.getWorkTimeStart1()) && !Objects.isNull(appWork.getWorkTimeEnd1())) {
				content += (appWork.getGoWorkAtr1() == 1 ? I18NText.getText("CMM045_252") + appWork.getWorkTimeStart1()
						+ I18NText.getText("CMM045_100") + appWork.getWorkTimeEnd1() : "");
				content += (appWork.getGoWorkAtr2() == 1 ? I18NText.getText("CMM045_252") + appWork.getWorkTimeStart2()
						+ I18NText.getText("CMM045_100") + appWork.getWorkTimeEnd2() : "");
			}
			if (!Objects.isNull(appWork.getBreakTimeStart1()) && !Objects.isNull(appWork.getBreakTimeEnd1())) {
				content += I18NText.getText("CMM045_251")
						+ (!Objects.isNull(appWork.getBreakTimeStart1()) ? appWork.getBreakTimeStart1()
								: "" + (!Objects.isNull(appWork.getBreakTimeEnd1()) ? appWork.getBreakTimeEnd1() : ""));
			}
		}

		return content + "\n" + appReason;
	}

	private String getBusinessTripContent(Application_New app, String companyID, String appID, String appReason) {
		// Pending
		String content = I18NText.getText("CMM045_254") + I18NText.getText("CMM045_255");
		return content + "\n" + appReason;
	}

	private String getGoReturnDirectlyAppContent(Application_New app, String companyID, String appID, String appReason) {
		// OK
		Optional<GoBackDirectly> op_appGoBack = goBackRepo.findByApplicationID(companyID, appID);
		String content = I18NText.getText("CMM045_258");
		if (op_appGoBack.isPresent()) {
			GoBackDirectly appGoBack = op_appGoBack.get();
			content += (appGoBack.getGoWorkAtr1() == UseAtr.USE
					? I18NText.getText("CMM045_259") + appGoBack.getWorkTimeStart1() : "")
					+ (appGoBack.getBackHomeAtr1() == UseAtr.USE
							? I18NText.getText("CMM045_260") + appGoBack.getWorkTimeEnd1() : "");
			content += (appGoBack.getGoWorkAtr2().isPresent() ? (appGoBack.getGoWorkAtr2().get() == UseAtr.USE
					? I18NText.getText("CMM045_259") + appGoBack.getWorkTimeStart2() : "") : "");
			content += (appGoBack.getBackHomeAtr2().isPresent() ? (appGoBack.getBackHomeAtr2().get() == UseAtr.USE
					? I18NText.getText("CMM045_260") + appGoBack.getWorkTimeEnd2() : "") : "");
		}
		return content + "\n" + appReason;
	}

	private String getBreakTimeAppContent(Application_New app, String companyID, String appID, String appReason) {
		// OK
		Optional<AppHolidayWork> op_appWork = holidayRepo.getAppHolidayWork(companyID, appID);
		String content = "";
		if (op_appWork.isPresent()) {
			AppHolidayWork appWork = op_appWork.get();
			switch (appWork.getApplication().getPrePostAtr()) {
			case PREDICT: {
				content += I18NText.getText("CMM045_275") + " " + appWork.getWorkTypeCode() + appWork.getWorkTimeCode();
				if (!Objects.isNull(appWork.getWorkClock1())) {
					if (!Objects.isNull(appWork.getWorkClock1().getStartTime())
							&& !Objects.isNull(appWork.getWorkClock1().getEndTime())) {
						content += appWork.getWorkClock1().getStartTime() + I18NText.getText("CMM045_100")
								+ appWork.getWorkClock1().getStartTime();
					}
					if (!Objects.isNull(appWork.getWorkClock2().getStartTime())
							&& !Objects.isNull(appWork.getWorkClock2().getEndTime())) {
						content += appWork.getWorkClock2().getStartTime() + I18NText.getText("CMM045_100")
								+ appWork.getWorkClock2().getStartTime();
					}
					String moreInf = "";
					int count = 0;
					int totalWorkUnit = 0;
					for (val x : appWork.getHolidayWorkInputs()) {
						if (x.getApplicationTime().v() > 0) {
							totalWorkUnit += x.getApplicationTime().v();
							if (count < 3) {
								String type = "";
								switch (x.getAttendanceType()) {
								case BONUSPAYTIME: {
									type = "加給時間";
								}
								case BONUSSPECIALDAYTIME: {
									type = "特定日加給時間";
								}
								case BREAKTIME: {
									type = "休出時間";
								}
								case NORMALOVERTIME: {
									type = "残業時間";
								}
								case RESTTIME: {
									type = "休憩時間";
								}
								}
								moreInf += type + " " + x.getApplicationTime().v() + " ";
							}
							count++;
						}
						String frameInfo = moreInf + (count > 3 ? I18NText.getText("CMM045_230", count - 3 + "") : "");
						content += I18NText.getText("CMM045_276") + totalWorkUnit
								+ I18NText.getText("CMM045_279", frameInfo);
					}
				}
				break;
			}
			case POSTERIOR: {
				List<Application_New> listPreApp = repoApp.getApp(app.getEmployeeID(), app.getAppDate(),
						PrePostAtr.PREDICT.value, ApplicationType.OVER_TIME_APPLICATION.value);
				Application_New preApp = (listPreApp.size() > 0 ? listPreApp.get(0) : null);
				AppHolidayWork preAppWork = !Objects.isNull(preApp)
						? holidayRepo.getAppHolidayWork(companyID, preApp.getAppID()).orElse(null) : null;
				if (!Objects.isNull(preAppWork)) {
					content += I18NText.getText("CMM045_272") + I18NText.getText("CMM045_275") + " "
							+ preAppWork.getWorkTypeCode() + preAppWork.getWorkTimeCode();
					if (!Objects.isNull(preAppWork.getWorkClock1())) {
						if (!Objects.isNull(preAppWork.getWorkClock1().getStartTime())
								&& !Objects.isNull(preAppWork.getWorkClock1().getEndTime())) {
							content += preAppWork.getWorkClock1().getStartTime() + I18NText.getText("CMM045_100")
									+ preAppWork.getWorkClock1().getStartTime();
						}
						if (!Objects.isNull(preAppWork.getWorkClock2().getStartTime())
								&& !Objects.isNull(preAppWork.getWorkClock2().getEndTime())) {
							content += preAppWork.getWorkClock2().getStartTime() + I18NText.getText("CMM045_100")
									+ preAppWork.getWorkClock2().getStartTime();
						}
						String moreInf = "";
						int count = 0;
						int totalWorkUnit = 0;
						for (val x : preAppWork.getHolidayWorkInputs()) {
							if (x.getApplicationTime().v() > 0) {
								totalWorkUnit += x.getApplicationTime().v();
								if (count < 3) {
									String type = "";
									switch (x.getAttendanceType()) {
									case BONUSPAYTIME: {
										type = "加給時間";
									}
									case BONUSSPECIALDAYTIME: {
										type = "特定日加給時間";
									}
									case BREAKTIME: {
										type = "休出時間";
									}
									case NORMALOVERTIME: {
										type = "残業時間";
									}
									case RESTTIME: {
										type = "休憩時間";
									}
									}
									moreInf += type + " " + x.getApplicationTime().v() + " ";
								}
								count++;
							}
							String frameInfo = moreInf
									+ (count > 3 ? I18NText.getText("CMM045_230", count - 3 + "") : "");
							content += I18NText.getText("CMM045_276") + totalWorkUnit
									+ I18NText.getText("CMM045_279", frameInfo);
						}
					}
				}
				content += "\n" + I18NText.getText("CMM045_275") + I18NText.getText("CMM045_275") + " "
						+ appWork.getWorkTypeCode() + appWork.getWorkTimeCode();
				if (!Objects.isNull(appWork.getWorkClock1())) {
					if (!Objects.isNull(appWork.getWorkClock1().getStartTime())
							&& !Objects.isNull(appWork.getWorkClock1().getEndTime())) {
						content += appWork.getWorkClock1().getStartTime() + I18NText.getText("CMM045_100")
								+ appWork.getWorkClock1().getStartTime();
					}
					if (!Objects.isNull(appWork.getWorkClock2().getStartTime())
							&& !Objects.isNull(appWork.getWorkClock2().getEndTime())) {
						content += appWork.getWorkClock2().getStartTime() + I18NText.getText("CMM045_100")
								+ appWork.getWorkClock2().getStartTime();
					}
					String moreInf = "";
					int count = 0;
					int totalWorkUnit = 0;
					for (val x : appWork.getHolidayWorkInputs()) {
						if (x.getApplicationTime().v() > 0) {
							totalWorkUnit += x.getApplicationTime().v();
							if (count < 3) {
								String type = "";
								switch (x.getAttendanceType()) {
								case BONUSPAYTIME: {
									type = "加給時間";
								}
								case BONUSSPECIALDAYTIME: {
									type = "特定日加給時間";
								}
								case BREAKTIME: {
									type = "休出時間";
								}
								case NORMALOVERTIME: {
									type = "残業時間";
								}
								case RESTTIME: {
									type = "休憩時間";
								}
								}
								moreInf += type + " " + x.getApplicationTime().v() + " ";
							}
							count++;
						}
						String frameInfo = moreInf + (count > 3 ? I18NText.getText("CMM045_230", count - 3 + "") : "");
						content += I18NText.getText("CMM045_276") + totalWorkUnit
								+ I18NText.getText("CMM045_279", frameInfo);
					}
				}
			}
			}
		}
		return content + "\n" + appReason;
	}

	private String getStampAppContent(Application_New app, String companyID, String appID, String appReason) {
		// OK
		String content = "";
		AppStamp appStamp = appStampRepo.findByAppID(companyID, appID);
		if (!Objects.isNull(appStamp)) {
			switch (appStamp.getStampRequestMode()) {
			case STAMP_GO_OUT_PERMIT: {
				int k = 0;
				boolean checkAppend = false;
				for (val x : appStamp.getAppStampGoOutPermits()) {
					if (x.getStampAtr() == AppStampAtr.GO_OUT) {
						content += I18NText.getText("CMM045_232") + " "
								+ I18NText.getText("CMM045_230", x.getStampGoOutAtr().name) + " "
								+ (x.getStartTime().isPresent() ? x.getStartTime().get().toString() : "") + " "
								+ I18NText.getText("CMM045_100") + " "
								+ (x.getEndTime().isPresent() ? x.getEndTime().get().toString() : "") + " ";
						if (k == 2)
							break;
						k++;
					} else if (x.getStampAtr() == AppStampAtr.CHILDCARE) {
						if (!checkAppend) {
							content += I18NText.getText("CMM045_233") + " ";
							checkAppend = true;
						}
						content += (x.getStartTime().isPresent() ? x.getStartTime().get().toString() : "") + " "
								+ I18NText.getText("CMM045_100") + " "
								+ (x.getEndTime().isPresent() ? x.getEndTime().get().toString() : "") + " ";
						if (k == 1)
							break;
						k++;
					} else if (x.getStampAtr() == AppStampAtr.CARE) {
						if (!checkAppend) {
							content += I18NText.getText("CMM045_234") + " ";
							checkAppend = true;
						}
						content += (x.getStartTime().isPresent() ? x.getStartTime().get().toString() : "") + " "
								+ I18NText.getText("CMM045_100") + " "
								+ (x.getEndTime().isPresent() ? x.getEndTime().get().toString() : "") + " ";
						if (k == 1)
							break;
						k++;
					}
				}
				content += (appStamp.getAppStampGoOutPermits().size() - k > 0
						? I18NText.getText("CMM045_231", (appStamp.getAppStampGoOutPermits().size() - k) + "") : "");
				break;
			}
			case STAMP_WORK: {
				int k = 0;
				content += I18NText.getText("CMM045_235") + " ";
				for (val x : appStamp.getAppStampWorks()) {
					if (k == 2)
						break;
					k++;
					content += x.getStampAtr().name + " "
							+ (x.getStartTime().isPresent() ? x.getStartTime().get().toString() : "") + " "
							+ I18NText.getText("CMM045_100") + " "
							+ (x.getStartTime().isPresent() ? x.getEndTime().get().toString() : "") + " ";
				}
				content += (appStamp.getAppStampGoOutPermits().size() - k > 0
						? I18NText.getText("CMM045_231", (appStamp.getAppStampGoOutPermits().size() - k) + "") : "");
				break;
			}
			case STAMP_ONLINE_RECORD: {
				// TO-DO
				content += I18NText.getText("CMM045_240");
				Optional<AppStampOnlineRecord> appStampRecord = appStamp.getAppStampOnlineRecord();
				if (appStampRecord.isPresent()) {
					content += appStampRecord.get().getStampCombinationAtr().name
							+ appStampRecord.get().getAppTime().toString();
				}
				break;
			}
			case STAMP_CANCEL: {
				content += I18NText.getText("CMM045_235");
				for (val x : appStamp.getAppStampCancels()) {
					switch (x.getStampAtr()) {
					// TO-DO
					case ATTENDANCE: {
						content += " ×出勤　9:00　×退勤　17:00 ";
					}
					case CARE: {
						content += " ×出勤　9:00　×退勤　17:00 ";
					}
					case CHILDCARE: {
						content += " ×出勤　9:00　×退勤　17:00 ";
					}
					case GO_OUT: {
						content += " ×出勤　9:00　×退勤　17:00 ";
					}
					case SUPPORT: {
						content += " ×出勤　9:00　×退勤　17:00 ";
					}
					}
				}
				break;
			}
			case OTHER: {
				int k = 0;
				for (val x : appStamp.getAppStampWorks()) {
					switch (x.getStampAtr()) {
					case ATTENDANCE: {
						if (k == 2)
							break;
						k++;
						content += x.getStampAtr().name + " ";
						content += (x.getStartTime().isPresent() ? x.getStartTime().get().toString() : "") + " "
								+ I18NText.getText("CMM045_100") + " "
								+ (x.getEndTime().isPresent() ? x.getEndTime().get().toString() : "") + " ";
					}
					case CARE: {
						if (k == 2)
							break;
						k++;
						content += I18NText.getText("CMM045_234") + " "
								+ (x.getStartTime().isPresent() ? x.getStartTime().get().toString() : "") + " "
								+ I18NText.getText("CMM045_100") + " "
								+ (x.getEndTime().isPresent() ? x.getEndTime().get().toString() : "") + " ";
					}
					case CHILDCARE: {
						if (k == 2)
							break;
						k++;
						content += I18NText.getText("CMM045_233") + " "
								+ (x.getStartTime().isPresent() ? x.getStartTime().get().toString() : "") + " "
								+ I18NText.getText("CMM045_100") + " "
								+ (x.getEndTime().isPresent() ? x.getEndTime().get().toString() : "") + " ";
					}
					case GO_OUT: {
						if (k == 2)
							break;
						k++;
						content += I18NText.getText("CMM045_232") + " "
								+ I18NText.getText("CMM045_230", x.getStampGoOutAtr().name) + " "
								+ (x.getStartTime().isPresent() ? x.getStartTime().get().toString() : "") + " "
								+ I18NText.getText("CMM045_100") + " "
								+ (x.getEndTime().isPresent() ? x.getEndTime().get().toString() : "") + " ";
					}
					case SUPPORT: {
						if (k == 2)
							break;
						k++;
						content += I18NText.getText("CMM045_242") + " "
								+ (x.getStartTime().isPresent() ? x.getStartTime().get().toString() : "") + " "
								+ I18NText.getText("CMM045_100") + " "
								+ (x.getEndTime().isPresent() ? x.getEndTime().get().toString() : "") + " ";
					}
					}
				}
				content += (appStamp.getAppStampGoOutPermits().size() - k > 0
						? I18NText.getText("CMM045_231", (appStamp.getAppStampGoOutPermits().size() - k) + "") : "");
				break;
			}
			}
		}
		return content + "\n" + appReason;
	}

	private String getAnnualAppContent(Application_New app, String companyID, String appID, String appReason) {
		// Pending
		String content = I18NText.getText("CMM045_264") + "\n" + appReason;
		return content;
	}

	private String getEarlyLeaveAppContent(Application_New app, String companyID, String appID, String appReason) {
		// OK
		String content = "";
		Optional<LateOrLeaveEarly> op_leaveApp = lateLeaveEarlyRepo.findByCode(companyID, appID);
		if (op_leaveApp.isPresent()) {
			LateOrLeaveEarly leaveApp = op_leaveApp.get();
			if (leaveApp.getActualCancelAtr() == 0) {
				if (app.getPrePostAtr().value == 0) {
					content += I18NText.getText("CMM045_243")
							+ (leaveApp.getEarly1().value == 0 ? ""
									: I18NText.getText("CMM045_246") + leaveApp.getLateTime1().toString())
							+ (leaveApp.getLate1().value == 0 ? ""
									: I18NText.getText("CMM045_247") + leaveApp.getEarlyTime1().toString())
							+ (leaveApp.getEarly2().value == 0 ? ""
									: I18NText.getText("CMM045_246") + leaveApp.getLateTime2().toString())
							+ (leaveApp.getLate2().value == 0 ? ""
									: I18NText.getText("CMM045_247") + leaveApp.getEarlyTime2().toString());
				} else if (app.getPrePostAtr().value == 1) {
					content += I18NText.getText("CMM045_243")
							+ (leaveApp.getEarly1().value == 0 ? ""
									: I18NText.getText("CMM045_246") + leaveApp.getLateTime1().toString())
							+ (leaveApp.getLate1().value == 0 ? ""
									: I18NText.getText("CMM045_247") + leaveApp.getEarlyTime1().toString())
							+ (leaveApp.getEarly2().value == 0 ? ""
									: I18NText.getText("CMM045_246") + leaveApp.getLateTime2().toString())
							+ (leaveApp.getLate2().value == 0 ? ""
									: I18NText.getText("CMM045_247") + leaveApp.getEarlyTime2().toString());
				}
			} else {
				if (leaveApp.getActualCancelAtr() == 0) {
					content += I18NText.getText("CMM045_243")
							+ (leaveApp.getEarly1().value == 0 ? ""
									: "×" + I18NText.getText("CMM045_246") + leaveApp.getLateTime1().toString())
							+ (leaveApp.getLate1().value == 0 ? ""
									: "×" + I18NText.getText("CMM045_247") + leaveApp.getEarlyTime1().toString())
							+ (leaveApp.getEarly2().value == 0 ? ""
									: "×" + I18NText.getText("CMM045_246") + leaveApp.getLateTime2().toString())
							+ (leaveApp.getLate2().value == 0 ? ""
									: "×" + I18NText.getText("CMM045_247") + leaveApp.getEarlyTime2().toString());
				}
			}
		}
		return content + "\n" + appReason;
	}

	private String getComplementLeaveAppContent(Application_New app, String companyID, String appID, String appReason) {
		AbsenceLeaveApp leaveApp = null;
		RecruitmentApp recApp = null;
		String content = "";
		AppCompltLeaveSyncOutput sync = otherCommonAlgorithm.getAppComplementLeaveSync(companyID, appID);
		if (!sync.isSync()) {
			if (sync.getType() == 0) {
				leaveApp = appLeaveRepo.findByAppId(appID).orElse(null);
				if (!Objects.isNull(leaveApp)) {
					content += I18NText.getText("CMM045_262") + " " + app.getAppDate().toString("MM/dd")
							+ I18NText.getText("CMM045_230", leaveApp.getWorkTypeCD());
					if (!Objects.isNull(leaveApp.getWorkTime1()) && !Objects.isNull(leaveApp.getWorkTime2())) {
						content += leaveApp.getWorkTime1().getStartTime() + I18NText.getText("CMM045_100")
								+ leaveApp.getWorkTime1().getEndTime();
					}
				}

			} else {
				recApp = recAppRepo.findByAppId(appID).orElse(null);
				if (!Objects.isNull(recApp)) {
					content += I18NText.getText("CMM045_263") + " " + app.getAppDate().toString("MM/dd")
							+ I18NText.getText("CMM045_230", recApp.getWorkTypeCD());
					if (!Objects.isNull(recApp.getWorkTime1()) && !Objects.isNull(recApp.getWorkTime2())) {
						content += recApp.getWorkTime1().getStartTime() + I18NText.getText("CMM045_100")
								+ recApp.getWorkTime1().getEndTime();
					}
				}
			}
		} else {
			leaveApp = appLeaveRepo.findByAppId(appID).orElse(null);
			recApp = recAppRepo.findByAppId(appID).orElse(null);
			if (!Objects.isNull(leaveApp) && !Objects.isNull(recApp)) {
				content += I18NText.getText("CMM045_262") + " " + app.getAppDate().toString("MM/dd")
						+ I18NText.getText("CMM045_230", leaveApp.getWorkTypeCD());
				if (!Objects.isNull(leaveApp.getWorkTime1()) && !Objects.isNull(leaveApp.getWorkTime2())) {
					content += leaveApp.getWorkTime1().getStartTime() + I18NText.getText("CMM045_100")
							+ leaveApp.getWorkTime1().getEndTime();
				}
				content += "\n" + I18NText.getText("CMM045_263") + " " + app.getAppDate().toString("MM/dd")
						+ I18NText.getText("CMM045_230", recApp.getWorkTypeCD());
				if (!Objects.isNull(recApp.getWorkTime1()) && !Objects.isNull(recApp.getWorkTime2())) {
					content += recApp.getWorkTime1().getStartTime() + I18NText.getText("CMM045_100")
							+ recApp.getWorkTime1().getEndTime();
				}
			}
		}
		return content + "\n" + appReason;
	}

	private String getStampNrAppContent(Application_New app, String companyID, String appID, String appReason) {
		return appReason;
	}

	private String getLongBusinessTripAppContent(Application_New app, String companyID, String appID, String appReason) {
		// Pending
		return appReason;
	}

	private String getBusinessTripOfficeAppContent(Application_New app, String companyID, String appID, String appReason) {
		// TODO
		return appReason;
	}

	private String getApp36AppContent(Application_New app, String companyID, String appID, String appReason) {
		// TODO
		return appReason;
	}

	private String clockShorHm(Integer minute) {
		return (minute / 60 + ":" + (minute % 60 < 10 ? "0" + minute % 60 : minute % 60));
	}
}
