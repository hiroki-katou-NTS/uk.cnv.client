package nts.uk.ctx.at.request.dom.application.applist.service.detail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import nts.arc.i18n.I18NText;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeave;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeaveRepository;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.appabsence.appforspecleave.ApplyforSpecialLeave;
import nts.uk.ctx.at.request.dom.application.applist.extractcondition.ApplicationListAtr;
import nts.uk.ctx.at.request.dom.application.applist.service.ApplicationTypeDisplay;
import nts.uk.ctx.at.request.dom.application.applist.service.ListOfAppTypes;
import nts.uk.ctx.at.request.dom.application.applist.service.content.AppContentService;
import nts.uk.ctx.at.request.dom.application.applist.service.content.AppHolidayWorkData;
import nts.uk.ctx.at.request.dom.application.applist.service.content.AppOverTimeData;
import nts.uk.ctx.at.request.dom.application.applist.service.content.AppTimeFrameData;
import nts.uk.ctx.at.request.dom.application.applist.service.content.ArrivedLateLeaveEarlyItemContent;
import nts.uk.ctx.at.request.dom.application.applist.service.content.ComplementLeaveAppLink;
import nts.uk.ctx.at.request.dom.application.applist.service.content.OptionalItemOutput;
import nts.uk.ctx.at.request.dom.application.applist.service.content.OvertimeHolidayWorkActual;
import nts.uk.ctx.at.request.dom.application.applist.service.datacreate.StampAppOutputTmp;
import nts.uk.ctx.at.request.dom.application.applist.service.param.AttendanceNameItem;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripRepository;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectlyRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.TypeApplicationHolidays;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRec;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRecRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.SyncState;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarly;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarlyRepository;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.LateCancelation;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.LateOrEarlyAtr;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.TimeReport;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplication;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplicationRepository;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTimeRepository;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImage;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImageRepository;
import nts.uk.ctx.at.request.dom.application.stamp.AppStamp;
import nts.uk.ctx.at.request.dom.application.stamp.AppStampRepository;
import nts.uk.ctx.at.request.dom.application.stamp.DestinationTimeZoneApp;
import nts.uk.ctx.at.request.dom.application.stamp.StampFrameNo;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.dom.application.stamp.StartEndClassification;
import nts.uk.ctx.at.request.dom.application.stamp.TimeStampApp;
import nts.uk.ctx.at.request.dom.application.stamp.TimeStampAppEnum;
import nts.uk.ctx.at.request.dom.application.stamp.TimeStampAppOther;
import nts.uk.ctx.at.request.dom.application.stamp.TimeZoneStampClassification;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplication;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationDetail;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationRepository;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChange;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChangeRepository;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDisplaySetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemApplicationSetting;
import nts.uk.ctx.at.shared.dom.relationship.Relationship;
import nts.uk.ctx.at.shared.dom.relationship.repository.RelationshipRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * HOATT DOI UNG NOI DUNG DON XIN CMM045, KAF018, KDL030 GOP THANH 1 XU LY CHUNG
 * 2019.05.09 #107568
 * @author hoatt
 */
@Stateless
public class AppContentDetailImplCMM045 implements AppContentDetailCMM045 {

	@Inject
	private AppDetailInfoRepository appDetailInfoRepo;

	@Inject
	private ArrivedLateLeaveEarlyRepository arrivedLateLeaveEarlyRepository;

	@Inject
	private AppContentService appContentService;

	@Inject
	private AppStampRepository appStampRepository;

	@Inject
	private AppRecordImageRepository appRecordImageRepository;

	@Inject
	private GoBackDirectlyRepository goBackDirectlyRepository;

	@Inject
	private BusinessTripRepository businessTripRepository;

	@Inject
	private AppWorkChangeRepository appWorkChangeRepository;
	
	@Inject
	private OptionalItemApplicationRepository optionalItemApplicationRepository;
	
	@Inject
	private OptionalItemAppSetRepository optionalItemAppSetRepository;
	
	@Inject
	private OptionalItemRepository optionalItemRepository;
	
	@Inject
	private AppOverTimeRepository appOverTimeRepository;
	
	@Inject
	private AppHolidayWorkRepository appHolidayWorkRepository;
	
	@Inject
	private ApplyForLeaveRepository applyForLeaveRepository;
	
	@Inject
	private AbsenceLeaveAppRepository absenceLeaveAppRepository;
	
	@Inject
	private RecruitmentAppRepository recruitmentAppRepository;
	
	@Inject
	private AppHdsubRecRepository compltLeaveSimMngRepository;
	
	@Inject
	private ApplicationRepository applicationRepository;
	
	@Inject
	private RelationshipRepository relationshipRepository;
	
	@Inject
	private TimeLeaveApplicationRepository timeLeaveApplicationRepository;

	/**
	 * get content work change
	 * 勤務変更申請 kaf007 - appType = 2
	 * @param appId
	 * @param detailSet
	 * @return
	 */
	@Override
	public String getContentWorkChange(Application application, DisplayAtr appReasonDisAtr, List<WorkTimeSetting> workTimeLst, List<WorkType> workTypeLst, String companyID) {
		// ドメインモデル「勤務変更申請」を取得
		AppWorkChange appWorkChange = appWorkChangeRepository.findbyID(companyID, application.getAppID()).get();
		// 勤務就業名称を作成
		String workTypeName = appDetailInfoRepo.findWorkTypeName(workTypeLst, appWorkChange.getOpWorkTypeCD().map(x -> x.v()).orElse(null));
		String workTimeName = appDetailInfoRepo.findWorkTimeName(workTimeLst, appWorkChange.getOpWorkTimeCD().map(x -> x.v()).orElse(null));
		// 申請内容　＝　申請内容（勤務変更申請、直行直帰申請）
		return appContentService.getWorkChangeGoBackContent(
				ApplicationType.WORK_CHANGE_APPLICATION,
				workTypeName,
				workTimeName,
				appWorkChange.getStraightGo(),
				appWorkChange.getTimeZoneWithWorkNoLst().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getStartTime()).orElse(null),
				appWorkChange.getStraightBack(),
				appWorkChange.getTimeZoneWithWorkNoLst().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getEndTime()).orElse(null),
				appWorkChange.getTimeZoneWithWorkNoLst().stream().filter(x -> x.getWorkNo().v()==2).findAny().map(x -> x.getTimeZone().getStartTime()).orElse(null),
				appWorkChange.getTimeZoneWithWorkNoLst().stream().filter(x -> x.getWorkNo().v()==2).findAny().map(x -> x.getTimeZone().getEndTime()).orElse(null),
				null,
				null,
				appReasonDisAtr,
				application.getOpAppReason().orElse(null),
				application);
//		if(wkChange == null){
//			//ドメインモデル「勤務変更申請」を取得
//			wkChange = appDetailInfoRepo.getAppWorkChangeInfo(companyId, appId, lstWkType, lstWkTime);
//		}
//		//申請内容　＝　申請内容（勤務変更申請、直行直帰申請）
//		String go1 = wkChange.getGoWorkAtr1() == 0 ? "" + I18NText.getText("CMM045_252") + wkChange.getWorkTimeStart1() :
//				wkChange.getWorkTimeStart1();
//		String back1 = wkChange.getBackHomeAtr1() == 0 ? I18NText.getText("CMM045_252") + wkChange.getWorkTimeEnd1() :
//				wkChange.getWorkTimeEnd1();
//		String time1 = go1 == "" ? "" : go1 + I18NText.getText("CMM045_100") + back1;
//		String go2 = (wkChange.getGoWorkAtr2() == null || wkChange.getGoWorkAtr2() == 1) ? wkChange.getWorkTimeStart2() :
//				"" + I18NText.getText("CMM045_252") + wkChange.getWorkTimeStart2();
//		String back2 = (wkChange.getBackHomeAtr2() == null || wkChange.getBackHomeAtr2() == 1) ? wkChange.getWorkTimeEnd2() :
//				I18NText.getText("CMM045_252") + wkChange.getWorkTimeEnd2();
//		String time2 = go2 == "" ? "" : go2 + I18NText.getText("CMM045_100") + back2;
//		String breakTime = wkChange.getBreakTimeStart1() == "" ? "" : I18NText.getText("CMM045_251") +
//				wkChange.getBreakTimeEnd1() + I18NText.getText("CMM045_100") + wkChange.getBreakTimeEnd1();
//		String workName = wkChange.getWorkTypeName() == "" ? wkChange.getWorkTimeName() : wkChange.getWorkTimeName() == "" ?
//				wkChange.getWorkTypeName() : wkChange.getWorkTypeName() + "　" + wkChange.getWorkTimeName();
//		String time = time1 == "" ? time2 : time2 == "" ? time1 : time1 + "　" + time2;
//		String cont1 = workName == "" ? time : time == "" ? workName : workName + "　" + time;
//        String cont2 = cont1 == "" ? breakTime : breakTime == "" ? cont1 : cont1 + "　" + breakTime;
//		return this.checkAddReason(cont2, appReason, appReasonDisAtr, screenAtr);
	}

	/**
	 * get content go back
	 * 直行直帰申請 kaf009 - appType = 4
	 * ※申請モード、承認モード(事前)用レイアウト
	 * @param appId
	 * @param detailSet
	 * @return
	 */
	@Override
	public String getContentGoBack(Application application, DisplayAtr appReasonDisAtr, List<WorkTimeSetting> workTimeLst, List<WorkType> workTypeLst, ScreenAtr screenAtr) {
		String companyID = AppContexts.user().companyId();
		// ドメインモデル「直行直帰申請」を取得 ( Lấy domain 「直行直帰申請」)
		GoBackDirectly goBackDirectly = goBackDirectlyRepository.find(companyID, application.getAppID()).get();
		// 申請内容　＝　申請内容（勤務変更申請、直行直帰申請） ( Nội dung application = Nội dung application (Work change application, application đi thẳng về nhà)
		return appContentService.getWorkChangeGoBackContent(
				ApplicationType.GO_RETURN_DIRECTLY_APPLICATION,
				Strings.EMPTY,
				Strings.EMPTY,
				goBackDirectly.getStraightDistinction(),
				new TimeWithDayAttr(0),
				goBackDirectly.getStraightLine(),
				new TimeWithDayAttr(0),
				new TimeWithDayAttr(0),
				new TimeWithDayAttr(0),
				new TimeWithDayAttr(0),
				new TimeWithDayAttr(0),
				appReasonDisAtr,
				application.getOpAppReason().orElse(null),
				application);
//		if(goBack == null){
//			//ドメインモデル「直行直帰申請」を取得
//			goBack = appDetailInfoRepo.getAppGoBackInfo(companyId, appId);
//		}
//		//申請内容　＝　申請内容（勤務変更申請、直行直帰申請）
//		String go1 = goBack.getGoWorkAtr1() == 1 ? goBack.getWorkTimeStart1() == "" ? I18NText.getText("CMM045_259") + goBack.getWorkTimeStart1() :
//			I18NText.getText("CMM045_259") + "　" + goBack.getWorkTimeStart1() : "";
//		String back1 = goBack.getBackHomeAtr1() == 1 ? goBack.getWorkTimeEnd1() == "" ? I18NText.getText("CMM045_260") + goBack.getWorkTimeEnd1() :
//			I18NText.getText("CMM045_260") + "　" + goBack.getWorkTimeEnd1() : "";
//		String go2 = goBack.getGoWorkAtr2() != null && goBack.getGoWorkAtr2() == 1 ? goBack.getWorkTimeStart2() == "" ?
//				I18NText.getText("CMM045_259") + goBack.getWorkTimeStart2() : I18NText.getText("CMM045_259") + "　" + goBack.getWorkTimeStart2() : "";
//        String back2 = goBack.getBackHomeAtr2() != null && goBack.getBackHomeAtr2() == 1 ? goBack.getWorkTimeEnd2() == "" ?
//        		I18NText.getText("CMM045_260") + goBack.getWorkTimeEnd2() : I18NText.getText("CMM045_260") + "　" + goBack.getWorkTimeEnd2() : "";
//        String goback1 = go1 == "" ? back1 : back1 == "" ? go1 : go1 + "　" + back1;
//        String goback2 = go2 == "" ? back2 : back2 == "" ? go2 : go2 + "　" + back2;
//        String gobackA = goback1 == "" ? goback2 : goback2 == "" ? goback1 : goback1 + "　" + goback2;
//		return this.checkAddReason(gobackA, appReason, appReasonDisAtr, screenAtr);
	}

    @Override
	public AppStampDataOutput createAppStampData(Application application, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr,
			String companyID, ListOfAppTypes listOfAppTypes) {
		// 申請.打刻申請モード
		if(application.getOpStampRequestMode().get()==StampRequestMode.STAMP_ONLINE_RECORD) {
			// ドメインモデル「レコーダイメージ申請」を取得する
			AppRecordImage appRecordImage = appRecordImageRepository.findByAppID(companyID, application.getAppID()).get();
			// 申請内容＝#CMM045_293＋'　'＋レコーダイメージ申請.打刻区分
			String result = I18NText.getText("CMM045_293") + " " + appRecordImage.getAppStampCombinationAtr().name;
			// レコーダイメージ申請.外出理由がemptyでない場合
			if(appRecordImage.getAppStampGoOutAtr().isPresent()) {
				// 申請内容＋＝#CMM045_230：{0}＝外出理由（Enum）
				result += I18NText.getText("CMM045_230", appRecordImage.getAppStampGoOutAtr().get().nameId);
			}
			// 申請内容＋＝’　’＋レコーダイメージ申請.申請時刻
			result += " " + new TimeWithDayAttr(appRecordImage.getAttendanceTime().v()).getFullText();
			// アルゴリズム「申請内容の申請理由」を実行する
			String appReasonContent = appContentService.getAppReasonContent(
					appReasonDisAtr,
					application.getOpAppReason().orElse(null),
					screenAtr,
					application.getOpAppStandardReasonCD().orElse(null),
					application.getAppType(),
					Optional.empty());
			if(Strings.isNotBlank(appReasonContent)) {
				result += "\n" + appReasonContent;
			}
			return new AppStampDataOutput(
					result,
					Optional.of(ApplicationTypeDisplay.STAMP_ONLINE_RECORD));
		}
		// ドメインモデル「打刻申請」を取得する
		AppStamp appStamp = appStampRepository.findByAppID(companyID, application.getAppID()).get();
		List<StampAppOutputTmp> listTmp = new ArrayList<>();
		// 「打刻申請.時刻の取消」よりリストを収集する
		appStamp.getListTimeStampApp().stream().collect(Collectors.groupingBy(x -> x.getDestinationTimeApp().getTimeStampAppEnum().value))
		.entrySet().forEach(entry -> {
			entry.getValue().stream().collect(Collectors.groupingBy(x -> x.getDestinationTimeApp().getEngraveFrameNo()))
			.entrySet().forEach(subEntry -> {
				Optional<TimeStampApp> opStartTimeStampApp = subEntry.getValue().stream()
						.filter(x -> x.getDestinationTimeApp().getStartEndClassification()==StartEndClassification.START)
						.findAny();
				Optional<TimeStampApp> opEndTimeStampApp = subEntry.getValue().stream()
						.filter(x -> x.getDestinationTimeApp().getStartEndClassification()==StartEndClassification.END)
						.findAny();
				listTmp.add(new StampAppOutputTmp(
						0,
						false,
						entry.getValue().get(0).getDestinationTimeApp().getTimeStampAppEnum().value,
					 	new StampFrameNo(subEntry.getKey()),
						opStartTimeStampApp.map(x -> x.getTimeOfDay()),
						subEntry.getValue().get(0).getAppStampGoOutAtr(),
						Optional.empty(),
						opEndTimeStampApp.map(x -> x.getTimeOfDay())));
			});
		});
		// 「打刻申請.時刻の取消」よりリストを収集する
		appStamp.getListDestinationTimeApp().stream().collect(Collectors.groupingBy(x -> x.getTimeStampAppEnum().value))
		.entrySet().forEach(entry -> {
			entry.getValue().stream().collect(Collectors.groupingBy(x -> x.getEngraveFrameNo()))
			.entrySet().forEach(subEntry -> {
				listTmp.add(new StampAppOutputTmp(
						0,
						true,
						entry.getValue().get(0).getTimeStampAppEnum().value,
					 	new StampFrameNo(subEntry.getKey()),
					 	Optional.empty(),
					 	Optional.empty(),
						Optional.empty(),
						Optional.empty()));
			});
		});
		// 「打刻申請.時間帯」よりリストを収集する
		for(TimeStampAppOther timeStampAppOther : appStamp.getListTimeStampAppOther()) {
			listTmp.add(new StampAppOutputTmp(
					1,
					false,
					timeStampAppOther.getDestinationTimeZoneApp().getTimeZoneStampClassification().value,
				 	new StampFrameNo(timeStampAppOther.getDestinationTimeZoneApp().getEngraveFrameNo()),
				 	Optional.of(timeStampAppOther.getTimeZone().getStartTime()),
					Optional.empty(),
					Optional.empty(),
					Optional.of(timeStampAppOther.getTimeZone().getEndTime())));
		}
		// 「打刻申請.時間帯の取消」よりリストを収集する
		for(DestinationTimeZoneApp destinationTimeZoneApp : appStamp.getListDestinationTimeZoneApp()) {
			listTmp.add(new StampAppOutputTmp(
					1,
					true,
					destinationTimeZoneApp.getTimeZoneStampClassification().value,
				 	new StampFrameNo(destinationTimeZoneApp.getEngraveFrameNo()),
				 	Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty()));
		}
		// 「打刻申請出力用Tmp」を並べて項目名をセットする
		listTmp.sort(Comparator.comparing((StampAppOutputTmp x) -> {
			String frameNoStr = String.format("%02d", x.getStampFrameNo().v());
			return String.valueOf(x.getTimeItem()) + String.valueOf(x.getStampAtr()) + frameNoStr;
		}));
		for(StampAppOutputTmp itemTmp : listTmp) {
			if(itemTmp.getTimeItem() == 0 && itemTmp.getStampAtr() == TimeStampAppEnum.ATTEENDENCE_OR_RETIREMENT.value) {
				// 枠NO
				if(itemTmp.getStampFrameNo().v()==1) {
					// 項目名＝#KAF002_103（勤務時間）
					itemTmp.setOpItemName(Optional.of(I18NText.getText("KAF002_103", itemTmp.getStampFrameNo().v().toString())));
				}
				if(itemTmp.getStampFrameNo().v()==2) {
					// 項目名＝#KAF002_65（勤務時間２）
					itemTmp.setOpItemName(Optional.of(I18NText.getText("KAF002_65", itemTmp.getStampFrameNo().v().toString())));
				}
			}
			if(itemTmp.getTimeItem() == 0 && itemTmp.getStampAtr() == TimeStampAppEnum.EXTRAORDINARY.value) {
				// 項目名＝#KAF002_66（臨時時間）：枠NO
				itemTmp.setOpItemName(Optional.of(I18NText.getText("KAF002_66", itemTmp.getStampFrameNo().v().toString())));
			}
			if(itemTmp.getTimeItem() == 0 && itemTmp.getStampAtr() == TimeStampAppEnum.GOOUT_RETURNING.value) {
				// 項目名＝#KAF002_67（外出時間）：枠NO
				// 項目名＋＝#CMM045_230（）：{0}=打刻申請出力用Tmp.外出理由
				if(itemTmp.getOpGoOutReasonAtr().isPresent()) {
					itemTmp.setOpItemName(Optional.of(I18NText.getText("KAF002_67", itemTmp.getStampFrameNo().v().toString()) + I18NText.getText("CMM045_230", itemTmp.getOpGoOutReasonAtr().get().nameId)));
				} else {
					itemTmp.setOpItemName(Optional.of(I18NText.getText("KAF002_67", itemTmp.getStampFrameNo().v().toString())));
				}
			}
			if(itemTmp.getTimeItem() == 1 && itemTmp.getStampAtr() == TimeZoneStampClassification.BREAK.value) {
				// 項目名＝#KAF002_75（休憩時間）：枠NO
				itemTmp.setOpItemName(Optional.of(I18NText.getText("KAF002_75", itemTmp.getStampFrameNo().v().toString())));
			}
			if(itemTmp.getTimeItem() == 1 && itemTmp.getStampAtr() == TimeZoneStampClassification.PARENT.value) {
				// 項目名＝#KAF002_68（育児時間）：枠NO
				itemTmp.setOpItemName(Optional.of(I18NText.getText("KAF002_68", itemTmp.getStampFrameNo().v().toString())));
			}
			if(itemTmp.getTimeItem() == 1 && itemTmp.getStampAtr() == TimeZoneStampClassification.NURSE.value) {
				// 項目名＝#KAF002_69（介護時間）：枠NO
				itemTmp.setOpItemName(Optional.of(I18NText.getText("KAF002_69", itemTmp.getStampFrameNo().v().toString())));
			}
		}
		// アルゴリズム「申請内容（打刻申請）」を実行する
		String content = appContentService.getAppStampContent(
				appReasonDisAtr,
				application.getOpAppReason().orElse(null),
				screenAtr,
				listTmp,
				application.getAppType(),
				application.getOpAppStandardReasonCD().orElse(null));
		return new AppStampDataOutput(
				content,
				Optional.of(ApplicationTypeDisplay.STAMP_ADDITIONAL));
	}

	@Override
	public String createArrivedLateLeaveEarlyData(Application application, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr,
			String companyID) {
		// ドメインモデル「遅刻早退取消申請」
		ArrivedLateLeaveEarly arrivedLateLeaveEarly = arrivedLateLeaveEarlyRepository.getLateEarlyApp(companyID, application.getAppID(), application);
		List<ArrivedLateLeaveEarlyItemContent> itemContentLst = new ArrayList<>();
		// 「遅刻早退取消申請.時刻報告」
		for(TimeReport timeReport : arrivedLateLeaveEarly.getLateOrLeaveEarlies()) {
			String itemName = Strings.EMPTY;
			if(timeReport.getLateOrEarlyClassification() == LateOrEarlyAtr.LATE) {
				itemName = I18NText.getText("CMM045_236");
			} else if(timeReport.getLateOrEarlyClassification() == LateOrEarlyAtr.EARLY) {
				itemName = I18NText.getText("CMM045_238");
			}
			itemContentLst.add(new ArrivedLateLeaveEarlyItemContent(
					itemName,
					timeReport.getWorkNo(),
					timeReport.getLateOrEarlyClassification(),
					Optional.of(timeReport.getTimeWithDayAttr()),
					false));
		}
		// 「遅刻早退取消申請.取消」
		for(LateCancelation lateCancelation : arrivedLateLeaveEarly.getLateCancelation()) {
			String itemName = Strings.EMPTY;
			if(lateCancelation.getLateOrEarlyClassification() == LateOrEarlyAtr.LATE) {
				itemName = I18NText.getText("CMM045_236");
			} else if(lateCancelation.getLateOrEarlyClassification() == LateOrEarlyAtr.EARLY) {
				itemName = I18NText.getText("CMM045_238");
			}
			itemContentLst.add(new ArrivedLateLeaveEarlyItemContent(
					itemName,
					lateCancelation.getWorkNo(),
					lateCancelation.getLateOrEarlyClassification(),
					Optional.empty(),
					true));
		}
		// <List>を勤務NO+区分（遅刻、早退の順）で並べる
		itemContentLst.sort(Comparator.comparing((ArrivedLateLeaveEarlyItemContent x) -> {
			return String.valueOf(x.getWorkNo()) + String.valueOf(x.getLateOrEarlyAtr().value);
		}));
		// アルゴリズム「申請内容（遅刻早退取消）」を実行する
		return appContentService.getArrivedLateLeaveEarlyContent(
				application.getOpAppReason().orElse(null),
				appReasonDisAtr,
				screenAtr,
				itemContentLst,
				application.getAppType(),
				application.getOpAppStandardReasonCD().orElse(null));
	}

	@Override
	public String createBusinessTripData(Application application, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr,
			String companyID) {
		String content = Strings.EMPTY;
		// ドメインモデル「出張申請」を取得する
		BusinessTrip businessTrip = businessTripRepository.findByAppId(companyID, application.getAppID()).get();
		// @＝''
		String paramString = "";
		// 出張申請.出発時刻が入力されている場合
		if(businessTrip.getDepartureTime().isPresent()) {
			// 申請内容＝#CMM045_290＋"　"＋出張申請.出発時刻＋”　”
			content += I18NText.getText("CMM045_290") + " " + new TimeWithDayAttr(businessTrip.getDepartureTime().get().v()).getFullText() + " ";
			// @＝'　'
			paramString = " ";
		}
		// 出張申請.帰着時刻が入力されている場合
		if(businessTrip.getReturnTime().isPresent()) {
			// 申請内容＋＝@＋#CMM045_291＋"　"＋出張申請.帰着時刻
			content += paramString + I18NText.getText("CMM045_291") + " " + new TimeWithDayAttr(businessTrip.getReturnTime().get().v()).getFullText();
		}
		// アルゴリズム「申請内容の申請理由」を実行する
		String appReasonContent = appContentService.getAppReasonContent(
				appReasonDisAtr,
				application.getOpAppReason().orElse(null),
				screenAtr,
				application.getOpAppStandardReasonCD().orElse(null),
				ApplicationType.BUSINESS_TRIP_APPLICATION,
				Optional.empty());
		if(Strings.isNotBlank(appReasonContent)) {
			content += "\n" + appReasonContent;
		}
		return content;
	}

	@Override
	public String createOptionalItemApp(Application application, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr,
			String companyID) {
		// ドメインモデル「任意項目申請」を取得する
		OptionalItemApplication optionalItemApp = optionalItemApplicationRepository.getByAppId(companyID, application.getAppID()).get();
		// ドメインモデル「任意項目申請設定」を取得する
		Optional<OptionalItemApplicationSetting> opOptionalItemApplicationSetting = optionalItemAppSetRepository
				.findByCompanyAndCode(companyID, optionalItemApp.getCode().toString());
		if(!opOptionalItemApplicationSetting.isPresent()) {
			return optionalItemApp.getCode().toString() + "未登録";
		}
		// ドメインモデル「任意項目」より取得する
		List<OptionalItem> optionalItemLst = optionalItemRepository.findByListNos(
				companyID, 
				optionalItemApp.getOptionalItems().stream().map(x -> x.getItemNo().v()).collect(Collectors.toList()));
		List<OptionalItemOutput> optionalItemOutputLst = new ArrayList<>();
		for(OptionalItem optionalItem : optionalItemLst) {
			// リストを作成する
			optionalItemOutputLst.add(new OptionalItemOutput(
					optionalItem.getOptionalItemName(), 
					optionalItemApp.getOptionalItems().stream().filter(x -> x.getItemNo().v()==optionalItem.getOptionalItemNo().v()).findAny().get(), 
					optionalItem.getOptionalItemAtr(), 
					optionalItem.getUnit().get()));
		}
		// アルゴリズム「申請内容（任意項目申請）」を実行する
		return appContentService.getOptionalItemAppContent(
				application.getOpAppReason().orElse(null), 
				appReasonDisAtr, 
				screenAtr, 
				opOptionalItemApplicationSetting.get().getName(), 
				optionalItemOutputLst, 
				application.getAppType(), 
				application.getOpAppStandardReasonCD().orElse(null)	);
	}

	@Override
	public AppOvertimeDataOutput createOvertimeContent(Application application, List<WorkType> workTypeLst, List<WorkTimeSetting> workTimeSettingLst, 
			List<AttendanceNameItem> attendanceNameItemLst, ApplicationListAtr applicationListAtr, ApprovalListDisplaySetting approvalListDisplaySetting,
			String companyID, Map<String, Pair<Integer, Integer>> cacheTime36, ScreenAtr screenAtr) {
		// ドメインモデル「休日出勤申請」を取得してデータを作成
		AppOverTime appOverTime = appOverTimeRepository.find(companyID, application.getAppID()).get();
		// 勤務就業名称を作成
		String workTypeName = appDetailInfoRepo.findWorkTypeName(
				workTypeLst, 
				appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode().v()).orElse(null));
		String workTimeName = appDetailInfoRepo.findWorkTimeName(
				workTimeSettingLst, 
				appOverTime.getWorkInfoOp().map(x -> x.getWorkTimeCodeNotNull().map(y -> y.v()).orElse(null)).orElse(null));
		// ドメインモデル「申請」．事前事後区分をチェック
		OvertimeHolidayWorkActual overtimeHolidayWorkActual = null;
		Integer excessTimeNumber = null;
		Integer excessTime = null;
		String backgroundColor = "";
		if(application.getPrePostAtr()==PrePostAtr.POSTERIOR && applicationListAtr==ApplicationListAtr.APPROVER) {
			// 申請一覧リスト取得実績
			overtimeHolidayWorkActual = appContentService.getOvertimeHolidayWorkActual(
					companyID, 
					application,
					workTypeLst,
					workTimeSettingLst,
					attendanceNameItemLst,
					appOverTime,
					null,
					appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode()).orElse(null), 
					appOverTime.getWorkInfoOp().map(x -> x.getWorkTimeCodeNotNull().orElse(null)).orElse(null));
			// 背景色　＝　取得したデータ．背景色
			backgroundColor = overtimeHolidayWorkActual==null ? "" : overtimeHolidayWorkActual.getBackgroundColor();
			// アルゴリズム「申請一覧36協定時間の取得」を実行する
//			Pair<Integer, Integer> pair = appContentService.getAgreementTime36(
//					application.getEmployeeID(), 
//					new YearMonth(Integer.valueOf(application.getAppDate().getApplicationDate().toString("YYYYMM"))), 
//					cacheTime36);
			Pair<Integer, Integer> pair = Pair.of(0, 0);
			excessTimeNumber = pair.getRight();
			excessTime = pair.getLeft();
		}
		// 　申請内容　＝　残業申請の申請内容
		AppOverTimeData appOverTimeData = new AppOverTimeData(
				appOverTime.getWorkHoursOp().map(x -> x.stream().filter(y -> y.getWorkNo().v()==1).findAny().map(y -> y.getTimeZone().getStartTime().v()).orElse(null)).orElse(null), 
				appOverTime.getOverTimeClf().value, 
				appOverTime.getWorkHoursOp().map(x -> x.stream().filter(y -> y.getWorkNo().v()==1).findAny().map(y -> y.getTimeZone().getEndTime().v()).orElse(null)).orElse(null), 
				appOverTime.getAppID(),
				excessTimeNumber,
				excessTime,
				appOverTime.getApplicationTime().getFlexOverTime().map(x -> x.v()), 
				appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode().v()), 
				Optional.ofNullable(workTypeName), 
				overtimeHolidayWorkActual==null ? Optional.empty() : Optional.ofNullable(overtimeHolidayWorkActual.getPostAppData()), 
				overtimeHolidayWorkActual==null ? Optional.empty() : Optional.ofNullable(overtimeHolidayWorkActual.getAppOverTimeData()), 
				appOverTime.getApplicationTime().getOverTimeShiftNight().map(x -> x.getOverTimeMidNight().v()), 
				appOverTime.getWorkInfoOp().map(x -> x.getWorkTimeCodeNotNull().map(y -> y.v())).orElse(Optional.empty()), 
				Optional.ofNullable(workTimeName), 
				overtimeHolidayWorkActual==null ? Optional.empty() : Optional.ofNullable(overtimeHolidayWorkActual.getBackgroundColor()), 
				appOverTime.getApplicationTime().getApplicationTime().stream().map(x -> new AppTimeFrameData(
						null, 
						x.getFrameNo().v(), 
						x.getAttendanceType(), 
						attendanceNameItemLst.stream().filter(y -> y.getAttendanceNo()==x.getFrameNo().v()&&y.getAttendanceType()==x.getAttendanceType())
							.findAny().map(y -> y.getAttendanceName()).orElse(""), 
						null, 
						x.getApplicationTime().v())).collect(Collectors.toList()));
		String appContent = appContentService.getOvertimeHolidayWorkContent(
				appOverTimeData, 
				null,
				application.getAppType(), 
				application.getPrePostAtr(), 
				applicationListAtr, 
				application.getOpAppReason().orElse(null), 
				approvalListDisplaySetting.getAppReasonDisAtr(), 
				screenAtr,
				overtimeHolidayWorkActual==null ? false : overtimeHolidayWorkActual.isActualStatus(),
				application);
		Optional<ApplicationTypeDisplay> opAppTypeDisplay = Optional.empty();
		switch (appOverTime.getOverTimeClf()) {
		case EARLY_OVERTIME:
			opAppTypeDisplay = Optional.of(ApplicationTypeDisplay.EARLY_OVERTIME);
			break;
		case NORMAL_OVERTIME:
			opAppTypeDisplay = Optional.of(ApplicationTypeDisplay.NORMAL_OVERTIME);
			break;
		case EARLY_NORMAL_OVERTIME:
			opAppTypeDisplay = Optional.of(ApplicationTypeDisplay.EARLY_NORMAL_OVERTIME);
			break;
		default:
			break;
		}
		return new AppOvertimeDataOutput(appContent, opAppTypeDisplay, backgroundColor);
	}

	@Override
	public AppHolidayWorkDataOutput createHolidayWorkContent(Application application, List<WorkType> workTypeLst, List<WorkTimeSetting> workTimeSettingLst, 
			List<AttendanceNameItem> attendanceNameItemLst, ApplicationListAtr applicationListAtr, ApprovalListDisplaySetting approvalListDisplaySetting,
			String companyID, Map<String, Pair<Integer, Integer>> cacheTime36, ScreenAtr screenAtr) {
		// ドメインモデル「休日出勤申請」を取得してデータを作成
		AppHolidayWork appHolidayWork = appHolidayWorkRepository.find(companyID, application.getAppID()).get();
		// 勤務就業名称を作成
		String workTypeName = appDetailInfoRepo.findWorkTypeName(
				workTypeLst, 
				appHolidayWork.getWorkInformation().getWorkTypeCode().v());
		String workTimeName = appDetailInfoRepo.findWorkTimeName(
				workTimeSettingLst, 
				appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().map(y -> y.v()).orElse(null));
		// ドメインモデル「申請」．事前事後区分をチェック
		OvertimeHolidayWorkActual overtimeHolidayWorkActual = null;
		Integer excessTimeNumber = null;
		Integer excessTime = null;
		String backgroundColor = "";
		if(application.getPrePostAtr()==PrePostAtr.POSTERIOR && applicationListAtr==ApplicationListAtr.APPROVER) {
			// 申請一覧リスト取得実績
			overtimeHolidayWorkActual = appContentService.getOvertimeHolidayWorkActual(
					companyID, 
					application,
					workTypeLst,
					workTimeSettingLst,
					attendanceNameItemLst,
					null,
					appHolidayWork,
					appHolidayWork.getWorkInformation().getWorkTypeCode(), 
					appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().orElse(null));
			// 背景色　＝　取得したデータ．背景色
			backgroundColor = overtimeHolidayWorkActual==null ? "" : overtimeHolidayWorkActual.getBackgroundColor();
			// アルゴリズム「申請一覧36協定時間の取得」を実行する
//			Pair<Integer, Integer> pair = appContentService.getAgreementTime36(
//					application.getEmployeeID(), 
//					new YearMonth(Integer.valueOf(application.getAppDate().getApplicationDate().toString("YYYYMM"))), 
//					cacheTime36);
			Pair<Integer, Integer> pair = Pair.of(0, 0);
			excessTimeNumber = pair.getRight();
			excessTime = pair.getLeft();
		}
		// 申請内容　＝　休日出勤申請の申請内容
		AppHolidayWorkData appHolidayWorkData = new AppHolidayWorkData(
				appHolidayWork.getWorkingTimeList().map(x -> x.stream().filter(y -> y.getWorkNo().v()==1).findAny().map(y -> y.getTimeZone().getStartTime().v()).orElse(null)).orElse(null), 
				appHolidayWork.getWorkingTimeList().map(x -> x.stream().filter(y -> y.getWorkNo().v()==1).findAny().map(y -> y.getTimeZone().getEndTime().v()).orElse(null)).orElse(null), 
				appHolidayWork.getAppID(),
				excessTimeNumber,
				excessTime,
				appHolidayWork.getApplicationTime().getApplicationTime().stream().map(x -> new AppTimeFrameData(
						null, 
						x.getFrameNo().v(), 
						x.getAttendanceType(), 
						attendanceNameItemLst.stream().filter(y -> y.getAttendanceNo()==x.getFrameNo().v()&&y.getAttendanceType()==x.getAttendanceType())
							.findAny().map(y -> y.getAttendanceName()).orElse(""), 
						null, 
						x.getApplicationTime().v())).collect(Collectors.toList()), 
				Optional.of(appHolidayWork.getWorkInformation().getWorkTypeCode().v()), 
				Optional.ofNullable(workTypeName), 
				overtimeHolidayWorkActual==null ? Optional.empty() : Optional.ofNullable(overtimeHolidayWorkActual.getPostAppData()), 
				overtimeHolidayWorkActual==null ? Optional.empty() : Optional.ofNullable(overtimeHolidayWorkActual.getAppHolidayWorkData()), 
				appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().map(y -> y.v()), 
				Optional.ofNullable(workTimeName), 
				overtimeHolidayWorkActual==null ? Optional.empty() : Optional.ofNullable(overtimeHolidayWorkActual.getBackgroundColor()));
		String appContent = appContentService.getOvertimeHolidayWorkContent(
				null, 
				appHolidayWorkData,
				application.getAppType(), 
				application.getPrePostAtr(), 
				applicationListAtr, 
				application.getOpAppReason().orElse(null), 
				approvalListDisplaySetting.getAppReasonDisAtr(), 
				screenAtr,
				overtimeHolidayWorkActual==null ? false : overtimeHolidayWorkActual.isActualStatus(),
				application);
		return new AppHolidayWorkDataOutput(appContent, backgroundColor);
	}

	@Override
	public CompLeaveAppDataOutput getContentComplementLeave(Application application, String companyID, List<WorkType> workTypeLst, DisplayAtr appReasonDisAtr,
			ScreenAtr screenAtr) {
		// 振休振出申請紐付けを取得 (Lấy đơn liên kết nghỉ bù làm bù)
		LinkComplementLeaveOutput linkComplementLeaveOutput = this.getLinkComplementLeave(application.getAppID(), workTypeLst, companyID);
		ComplementLeaveAppLink complementLeaveAppLink = new ComplementLeaveAppLink();
		complementLeaveAppLink.setLinkAppID(linkComplementLeaveOutput.getAppID());
		if(linkComplementLeaveOutput.getComplementLeaveFlg()!=null) {
			complementLeaveAppLink.setComplementLeaveFlg(linkComplementLeaveOutput.getComplementLeaveFlg().value);
		}
		if(Strings.isNotBlank(linkComplementLeaveOutput.getAppID())) {
			// ドメインモデル「申請」を取得 (Lấy domain model 「application」)
			Application applicationLink = applicationRepository.findByID(linkComplementLeaveOutput.getAppID()).get();
			complementLeaveAppLink.setApplication(applicationLink);
			// 振休振出申請紐付け．紐づけ申請日　＝　取得した申請．申請日 ( AbsResSub.AppDate =  AppDate của đơn lấy được)
			complementLeaveAppLink.setLinkAppDate(applicationLink.getAppDate().getApplicationDate());
		}
		// 申請内容　＝　振休振出申請内容 ( appContent = appContetn của đơn xin nghỉ làm bù)
		String content = appContentService.getComplementLeaveContent(
				linkComplementLeaveOutput.getAbsenceLeaveApp(), 
				linkComplementLeaveOutput.getRecruitmentApp(), 
				appReasonDisAtr, 
				screenAtr, 
				complementLeaveAppLink, 
				application,
				workTypeLst);
		return new CompLeaveAppDataOutput(content, complementLeaveAppLink);
	}

	@Override
	public LinkComplementLeaveOutput getLinkComplementLeave(String appID, List<WorkType> workTypeLst, String companyID) {
		// ドメインモデル「振休申請」を取得 ( Lấy Domain model 「振休申請」
		Optional<AbsenceLeaveApp> opAbsenceLeaveApp = absenceLeaveAppRepository.findByID(appID);
		if(opAbsenceLeaveApp.isPresent()) {
			AbsenceLeaveApp absenceLeaveApp = opAbsenceLeaveApp.get();
			// ドメインモデル「振休振出同時申請管理」を取得 (Lấy domail model 「CompltLeaveSimMng」
			Optional<AppHdsubRec> opCompltLeaveSimMng = compltLeaveSimMngRepository.findByAbsID(appID).filter(x -> x.getSyncing()==SyncState.SYNCHRONIZING);
			if(!opCompltLeaveSimMng.isPresent()) {
				return new LinkComplementLeaveOutput(null, null, absenceLeaveApp, null);
			}
			// ドメインモデル「振出申請」を取得 ( Lấy domain model 「振出申請」)
			RecruitmentApp recruitmentApp = recruitmentAppRepository.findByID(opCompltLeaveSimMng.get().getRecAppID()).get();
			return new LinkComplementLeaveOutput(recruitmentApp.getAppID(), TypeApplicationHolidays.Rec, absenceLeaveApp, recruitmentApp);
		} else {
			// ドメインモデル「振出申請」を取得 (Lấy domain model 「振出申請」)
			RecruitmentApp recruitmentApp = recruitmentAppRepository.findByID(appID).get();
			// ドメインモデル「振休振出同時申請管理」を取得 (Lấy domail model 「CompltLeaveSimMng」
			Optional<AppHdsubRec> opCompltLeaveSimMng = compltLeaveSimMngRepository.findByRecID(appID).filter(x -> x.getSyncing()==SyncState.SYNCHRONIZING);
			if(!opCompltLeaveSimMng.isPresent()) {
				return new LinkComplementLeaveOutput(null, null, null, recruitmentApp);
			}
			// ドメインモデル「振休申請」を取得 ( Lấy domain model 「振休申請」)
			AbsenceLeaveApp absenceLeaveApp = absenceLeaveAppRepository.findByID(opCompltLeaveSimMng.get().getAbsenceLeaveAppID()).get();
			return new LinkComplementLeaveOutput(absenceLeaveApp.getAppID(), TypeApplicationHolidays.Abs, absenceLeaveApp, recruitmentApp);
		}
	}
	
	@Override
	public String getContentApplyForLeave(Application application, String companyID, List<WorkType> workTypeLst,
			DisplayAtr appReasonDisAtr, ScreenAtr screenAtr) {
		String result = "";
		// ドメインモデル「休暇申請」を取得してデータを作成 ( Lấy domain 「休暇申請」 để tạo data)
		ApplyForLeave applyForLeave = applyForLeaveRepository.findApplyForLeave(companyID, application.getAppID()).get();
		// 休暇申請.休暇種類をチェック(Check  đơn xin nghỉ. loại nghỉ)
		if(applyForLeave.getVacationInfo().getHolidayApplicationType()==HolidayAppType.SUBSTITUTE_HOLIDAY) {
			// 休暇申請.画面描画情報.補足情報.代休日変更の申請期間.開始日＝empty
			if(applyForLeave.getVacationInfo().getInfo().getDatePeriod().isPresent()) {
				// 申請内容＝「値」＋改行(nội dung đơn xin = 「value」 + xuống dòng)
				DatePeriod datePeriod = applyForLeave.getVacationInfo().getInfo().getDatePeriod().get();
				if(datePeriod.start().equals(datePeriod.end())) {
					result += I18NText.getText("CMM045_304", datePeriod.start().toString());
				} else {
					result += I18NText.getText("CMM045_304", datePeriod.start().toString() + I18NText.getText("CMM045_100") + datePeriod.end().toString());
				}
				result += "\n";
			}
		}
		// 勤務就業名称を作成 ( Tạo tên workEmployment)
		String workTypeName = appDetailInfoRepo.findWorkTypeName(workTypeLst, applyForLeave.getReflectFreeTimeApp().getWorkInfo().getWorkTypeCode().v());
		// 申請内容＋　＝　取得した勤務種類名称 ( Nội dung application = Tên Work type đã lấy)
		result += workTypeName;
		if(applyForLeave.getVacationInfo().getHolidayApplicationType()==HolidayAppType.SPECIAL_HOLIDAY) {
			// ドメインモデル「特別休暇申請」を取得 ( Lấy domain 「特別休暇申請」)
			ApplyforSpecialLeave applyforSpecialLeave = applyForLeave.getVacationInfo().getInfo().getApplyForSpeLeaveOptional().isPresent() ? 
			        applyForLeave.getVacationInfo().getInfo().getApplyForSpeLeaveOptional().get() : null;
			if (applyforSpecialLeave != null) {
			    // imported(就業.Shared)「続柄」を取得する ( Lấy imported(就業.Shared)「relationship」
			    Optional<Relationship> opRelationship = relationshipRepository.findByCode(companyID, applyforSpecialLeave.getRelationshipCD().map(x -> x.v()).orElse(null));
			    // 申請内容　+＝　”　”　+　続柄名称(nội dung đơn xin +＝　”　”　+ tên quan hệ)
			    result += " " + opRelationship.map(x -> x.getRelationshipName().v()).orElse("");
			    if(applyforSpecialLeave.isMournerFlag()) {
			        // 申請内容　+＝　”　”　+　#CMM045_277 ( Nội dung application　+＝　”　”　+　#CMM045_277)
			        result += " " + I18NText.getText("CMM045_277");
			    }
			}
			// 申請内容　+＝　”　”　+　申請．申請日数を取得する　+　#CMM045_278
			result += " ";
			DatePeriod period = null;
			if(application.getOpAppStartDate().isPresent() && application.getOpAppEndDate().isPresent()) {
				period = new DatePeriod(application.getOpAppStartDate().get().getApplicationDate(), application.getOpAppEndDate().get().getApplicationDate());
				result += String.valueOf(period.datesBetween().size());
			} else {
				period = new DatePeriod(application.getAppDate().getApplicationDate(), application.getAppDate().getApplicationDate());
				result += String.valueOf(period.datesBetween().size());
			}
			result += I18NText.getText("CMM045_278");
		}
		// 申請理由内容　＝　申請内容の申請理由
		String appReasonContent = appContentService.getAppReasonContent(
				appReasonDisAtr, 
				application.getOpAppReason().orElse(null),
				screenAtr,
				application.getOpAppStandardReasonCD().orElse(null), 
				application.getAppType(), 
				Optional.of(applyForLeave.getVacationInfo().getHolidayApplicationType()));
		// 申請内容を改行
		if(Strings.isNotBlank(appReasonContent)) {
			result += "\n" + appReasonContent;
		}
		return result;
	}

	@Override
	public String createAnnualHolidayData(Application application, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr,
			String companyID) {
		// ドメインモデル「時間休暇申請」を取得する
		TimeLeaveApplication timeLeaveApplication = timeLeaveApplicationRepository.findById(companyID, application.getAppID()).get();
		// 取得したドメインモデルより<List>を作成する※「値＝０又はempty」の場合はemptyとする
		List<TimeLeaveApplicationDetail> leaveApplicationDetails = timeLeaveApplication.getLeaveApplicationDetails().stream()
				.sorted(Comparator.comparing((TimeLeaveApplicationDetail x) -> x.getAppTimeType().value)).collect(Collectors.toList());
		// アルゴリズム「申請内容（時間年休申請）」を実行する
		return appContentService.getAnnualHolidayContent(
				application.getOpAppReason().orElse(null), 
				appReasonDisAtr, 
				screenAtr, 
				leaveApplicationDetails, 
				application.getOpAppStandardReasonCD().orElse(null));
	}
	
}
