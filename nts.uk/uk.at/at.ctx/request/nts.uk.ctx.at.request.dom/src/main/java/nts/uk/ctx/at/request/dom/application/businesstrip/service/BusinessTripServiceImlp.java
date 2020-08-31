package nts.uk.ctx.at.request.dom.application.businesstrip.service;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.request.dom.application.*;
import nts.uk.ctx.at.request.dom.application.appabsence.AppAbsence;
import nts.uk.ctx.at.request.dom.application.appabsence.AppAbsenceRepository;
import nts.uk.ctx.at.request.dom.application.businesstrip.*;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.OvertimeLeaveTime;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.*;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectlyRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChange;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChangeRepository;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.BusinessTripAppWorkType;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.TargetWorkTypeByApp;
import nts.uk.ctx.at.request.dom.setting.request.application.businesstrip.AppTripRequestSet;
import nts.uk.ctx.at.request.dom.setting.request.application.businesstrip.AppTripRequestSetRepository;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.shortworktime.ShortWorkTime;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.*;
import nts.uk.shr.com.context.AppContexts;
import org.apache.logging.log4j.util.Strings;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class BusinessTripServiceImlp implements BusinessTripService {

    @Inject
    private BusinessTripRepository businessTripRepository;

    @Inject
    private ApplicationRepository appRepo;

    @Inject
    private GoBackDirectlyRepository goBackDirectlyRepository;

    @Inject
    private AppWorkChangeRepository appWorkChangeRepository;

    @Inject
    private AppAbsenceRepository appAbsenceRepository;

    @Inject
    private WorkTypeRepository wkTypeRepo;

    @Inject
    private WorkTimeSettingRepository wkTimeRepo;

    @Inject
    private AppHolidayWorkRepository appHolidayWorkRepository;

    @Inject
    private AbsenceLeaveAppRepository absRepo;

    @Inject
    private RecruitmentAppRepository recRepo;

    @Inject
    private AppTripRequestSetRepository appTripRequestSetRepository;

    @Inject
    private WorkTypeRepository workTypeRepository;

    @Inject
    private BasicScheduleService basicScheduleService;


    /**
     * アルゴリズム「出張申請未承認申請を取得」を実行する
     *
     * @param sid                       社員ID
     * @param appDate                   申請対象日リスト
     * @param opActualContentDisplayLst 申請表示情報.申請表示情報(基準日関係あり).表示する実績内容
     */
    @Override
    public List<ActualContentDisplay> getBusinessTripNotApproved(String sid, List<GeneralDate> appDate, Optional<List<ActualContentDisplay>> opActualContentDisplayLst) {
        List<ActualContentDisplay> result = new ArrayList<>();
        if(appDate.isEmpty()) {
            return Collections.emptyList();
        }
        // ドメインモデル「申請」を取得する
        // Get Application có hiệu lực trong khoảng thời gian trên màn hình
        List<Application> apps = appRepo.getAppForKAF008(
                sid,
                appDate.get(0),
                appDate.get(appDate.size() - 1)
        );

        for (GeneralDate date : appDate) {
            // Lấy Application có hiệu lực trong từng ngày, sort theo ngày đăng ký AppDate
            List<Application> validApps = apps
                    .stream()
                    .filter(i -> i.getOpAppStartDate().get().getApplicationDate().afterOrEquals(date) && date.afterOrEquals(i.getOpAppEndDate().get().getApplicationDate()))
                    .sorted(Comparator.nullsLast((e1, e2) -> e2.getAppDate().getApplicationDate().compareTo(e1.getAppDate().getApplicationDate())))
                    .collect(Collectors.toList());
            if (validApps.isEmpty()) {
                result.add(new ActualContentDisplay(date, Optional.empty()));
            } else {
                // 対象年月日を含む申請が存在を確認
                // Lấy Application có ngày đăng ký mới nhất sau khi sort
                Application newestApp = validApps.get(0);
                // Lấy Content của ngày loop
                Optional<ActualContentDisplay> currentContent = Optional.empty();
                if (opActualContentDisplayLst.isPresent() && !opActualContentDisplayLst.get().isEmpty()) {
                    currentContent = opActualContentDisplayLst.get().stream().filter(i -> i.getDate().equals(date)).findFirst();
                }
                // アルゴリズム「反映状態を取得する」を実行する
                ReflectedState reflectStatus = this.getRefectionStatus(newestApp.getReflectionStatus());
                // 未反映、反映待ちの場合
                if (reflectStatus != null && reflectStatus.value == ReflectedState.WAITREFLECTION.value) {
                    val actualReflectStatus = newestApp.getReflectionStatus().getListReflectionStatusOfDay().stream().filter(i -> i.getTargetDate().compareTo(date) == 0).findAny();
                    if (actualReflectStatus.isPresent() && actualReflectStatus.get().getActualReflectStatus().value == ReflectedState.WAITREFLECTION.value) {
                        if (currentContent.isPresent() && currentContent.get().getOpAchievementDetail().isPresent()) {
                            // 表示する実績内容に反映する
                            this.getWorkInfoFromTripReqContent(newestApp.getAppID(), newestApp.getAppType(), date, currentContent.get());
                            ActualContentDisplay actualContent = new ActualContentDisplay(date, currentContent.get().getOpAchievementDetail());
                            result.add(actualContent);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public DetailScreenB getDataDetail(String companyId, String appId, AppDispInfoStartupOutput appDispInfoStartupOutput) {
        DetailScreenB result = new DetailScreenB();
        BusinessTripInfoOutput output = new BusinessTripInfoOutput();
        // ドメインモデル「出張申請設定」を取得する
        Optional<AppTripRequestSet> tripRequestSet = appTripRequestSetRepository.findById(companyId);
        if (tripRequestSet.isPresent()) {
            output.setSetting(tripRequestSet.get());
        }
        // ドメインモデル「出張申請」を取得する
        Optional<BusinessTrip> businessTrip = businessTripRepository.findByAppId(companyId, appId);

        Optional<AppEmploymentSet> opEmploymentSet = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet();
        // アルゴリズム「出張申請勤務種類を取得する」を実行する
        List<WorkType> workDays = this.getBusinessAppWorkType(
                opEmploymentSet.get(),
                EnumAdaptor.valueOf(BusinessTripAppWorkType.WORK_DAY.value, BusinessTripAppWorkType.class),
                new ArrayList<>(Arrays.asList(WorkTypeClassification.Attendance))
        );

        // アルゴリズム「出張申請勤務種類を取得する」を実行する
        List<WorkType> holidayWorkType = this.getBusinessAppWorkType(
                opEmploymentSet.get(),
                EnumAdaptor.valueOf(BusinessTripAppWorkType.HOLIDAY.value, BusinessTripAppWorkType.class),
                new ArrayList<>(Arrays.asList(WorkTypeClassification.Holiday, WorkTypeClassification.HolidayWork, WorkTypeClassification.Shooting))
        );

        // ドメインモデル「勤務種類」を取得する
        List<BusinessTripWorkTypes> businessTripWorkTypes = new ArrayList<>();
        if (businessTrip.isPresent()) {
            List<String> cds = businessTrip.get().getInfos().stream()
                    .map(i -> i.getWorkInformation().getWorkTypeCode().v())
                    .distinct()
                    .collect(Collectors.toList());
            // ドメインモデル「勤務種類」を取得する
            Map<String, WorkType> mapWorkCds = wkTypeRepo.getPossibleWorkType(companyId, cds).stream().collect(Collectors.toMap(i -> i.getWorkTypeCode().v(), i -> i));
            businessTripWorkTypes = businessTrip.get().getInfos().stream().map(i -> new BusinessTripWorkTypes(
                    i.getDate(),
                    mapWorkCds.get(i.getWorkInformation().getWorkTypeCode().v())
            )).collect(Collectors.toList());
        }
        List<WorkTimeSetting> workTimeSettings = new ArrayList<>();
        if (appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().isPresent()) {
            workTimeSettings = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().get();
        } else {
            if (businessTrip.isPresent()) {
                List<String> cds = businessTrip.get().getInfos().stream()
                        .map(i -> i.getWorkInformation().getWorkTimeCode().v())
                        .distinct()
                        .collect(Collectors.toList());
                // ドメインモデル「就業時間帯の設定」を取得する
                workTimeSettings = wkTimeRepo.findByCodes(companyId, cds);
            }
        }
        appDispInfoStartupOutput.getAppDispInfoWithDateOutput().setOpWorkTimeLst(Optional.of(workTimeSettings));
        output.setAppDispInfoStartup(appDispInfoStartupOutput);
        output.setWorkTypeBeforeChange(Optional.of(businessTripWorkTypes));
        output.setWorkDayCds(Optional.of(workDays));
        output.setHolidayCds(Optional.of(holidayWorkType));
        output.setWorkTypeAfterChange(Optional.empty());
        output.setActualContentDisplay(Optional.empty());
        result.setBusinessTripInfoOutput(output);
        result.setBusinessTrip(businessTrip.isPresent() ? businessTrip.get() : null);
        return result;
    }

    @Override
    public List<WorkType> getBusinessAppWorkType(AppEmploymentSet appEmploymentSet, BusinessTripAppWorkType workStyle, List<WorkTypeClassification> workTypeClassification) {
        String cid = AppContexts.user().companyId();
        List<WorkType> result = Collections.emptyList();
        Optional<TargetWorkTypeByApp> opTargetWorkTypeByApp = appEmploymentSet
                .getTargetWorkTypeByAppLst()
                .stream()
                .filter((x ->
                        x.getAppType() == ApplicationType.BUSINESS_TRIP_APPLICATION && x.getOpBusinessTripAppWorkType().isPresent() && x.getOpBusinessTripAppWorkType().get().value == workStyle.value
                ))
                .findAny();
        if (opTargetWorkTypeByApp.isPresent()) {
            if (opTargetWorkTypeByApp.get().isDisplayWorkType()
                    && opTargetWorkTypeByApp.get().getOpBusinessTripAppWorkType().isPresent()
                    && opTargetWorkTypeByApp.get().getOpBusinessTripAppWorkType().get().value == workStyle.value) {
                //INPUT．「雇用別申請承認設定．申請別対象勤務種類．勤務種類リスト」を返す
                List<String> workTypeCDLst = opTargetWorkTypeByApp.get().getWorkTypeLst();
                result = workTypeRepository.findNotDeprecatedByListCode(cid, workTypeCDLst);
            } else {
                // ドメインモデル「勤務種類」を取得して返す
                if (!workTypeClassification.isEmpty()) {
                    result = workTypeRepository.findForAppKAF008(
                            cid,
                            DeprecateClassification.NotDeprecated.value,
                            WorkTypeUnit.OneDay.value,
                            workTypeClassification.stream().map(i -> i.value).collect(Collectors.toList())
                    );
                } else {
                    result = workTypeRepository.findByDepreacateAtrAndWorkTypeAtr(
                            cid,
                            DeprecateClassification.NotDeprecated.value,
                            WorkTypeUnit.OneDay.value
                    );
                }
                opTargetWorkTypeByApp.get().setDisplayWorkType(true);
                opTargetWorkTypeByApp.get().setWorkTypeLst(result.stream().map(i-> i.getWorkTypeCode().v()).collect(Collectors.toList()));
                opTargetWorkTypeByApp.get().setOpBusinessTripAppWorkType(Optional.of(EnumAdaptor.valueOf(workStyle.value, BusinessTripAppWorkType.class)));
            }
        } else {
            // ドメインモデル「勤務種類」を取得して返す
            if (!workTypeClassification.isEmpty()) {
                result = workTypeRepository.findForAppKAF008(
                        cid,
                        DeprecateClassification.NotDeprecated.value,
                        WorkTypeUnit.OneDay.value,
                        workTypeClassification.stream().map(i -> i.value).collect(Collectors.toList())
                );
            } else {
                result = workTypeRepository.findByDepreacateAtrAndWorkTypeAtr(
                        cid,
                        DeprecateClassification.NotDeprecated.value,
                        WorkTypeUnit.OneDay.value
                );
            }
        }
        return result;
    }

    /**
     * アルゴリズム「出張申請就業時間帯チェック」を実行する
     * @param wkTypeCd
     * @param wkTimeCd
     */
    @Override
    public void checkInputWorkCode(String wkTypeCd, String wkTimeCd, GeneralDate inputDate) {
        SetupType checkNeededOfWorkTime = basicScheduleService.checkNeededOfWorkTimeSetting(wkTypeCd);
        switch (checkNeededOfWorkTime) {
            case REQUIRED:
                if (StringUtil.isNullOrEmpty(wkTypeCd, true)) {
                    throw new BusinessException("Msg_24", inputDate.toString());
                }
                break;
            case OPTIONAL:
                break;
            case NOT_REQUIRED:
                if (StringUtil.isNullOrEmpty(wkTypeCd, true)) {
                    throw new BusinessException("23", inputDate.toString());
                }
                break;
        }
    }

    /**
     * 反映状態を取得する
     *
     * @param status 反映状態＝申請.反映状態
     * @return 申請の反映状態＝反映状態（Enum）
     */
    private ReflectedState getRefectionStatus(ReflectionStatus status) {
        ReflectedState result = null;
        loop:
        for (ReflectionStatusOfDay eachState : status.getListReflectionStatusOfDay()) {
            switch (eachState.getActualReflectStatus()) {
                case REFLECTED:
                    continue loop;
                case CANCELED:
                    continue loop;
                default:
                    return eachState.getActualReflectStatus();
            }

        }
        return result;
    }

    /**
     * アルゴリズム「出張申請内容より勤務情報を取得」を実行する
     *
     * @param appId   申請ID
     * @param appType 申請種類
     * @param date    年月日
     */
    private void getWorkInfoFromTripReqContent(String appId, ApplicationType appType, GeneralDate date, ActualContentDisplay content) {
        String cid = AppContexts.user().companyId();
        String wkTypeCd = Strings.EMPTY;
        String wkTimeCd = Strings.EMPTY;
        Optional<String> wkTypeName;
        Optional<String> wkTimeName;
        Optional<Integer> opWorkTime = Optional.empty();
        Optional<Integer> opLeaveTime = Optional.empty();

        switch (appType) {
            case HOLIDAY_WORK_APPLICATION:
                //休日出勤申請 6
                Optional<AppHolidayWork> appHolidayWork = appHolidayWorkRepository.getFullAppHolidayWork(cid, appId);
                if (appHolidayWork.isPresent()) {
                    wkTypeCd = appHolidayWork.get().getWorkTypeCode().v();
                    wkTimeCd = appHolidayWork.get().getWorkTimeCode().v();
                    opWorkTime = appHolidayWork.map(i -> i.getWorkClock1().getStartTime().v());
                    opLeaveTime = appHolidayWork.map(i -> i.getWorkClock1().getEndTime().v());
                }
                break;
            case GO_RETURN_DIRECTLY_APPLICATION:
                // 直行直帰申請 4
                Optional<GoBackDirectly> goBackDirectly = goBackDirectlyRepository.find(cid, appId);
                if (goBackDirectly.isPresent() && goBackDirectly.get().getDataWork().isPresent()) {
                    wkTypeCd = goBackDirectly.get().getDataWork().get().getWorkTypeCode().v();
                    wkTimeCd = goBackDirectly.get().getDataWork().get().getWorkTimeCode().v();
                }
                break;
            case WORK_CHANGE_APPLICATION:
                // 勤務変更 2
                Optional<AppWorkChange> appWorkChange = appWorkChangeRepository.findbyID(cid, appId);
                if (appWorkChange.isPresent()
                        && appWorkChange.get().getOpWorkTimeCD().isPresent()
                        && appWorkChange.get().getOpWorkTimeCD().isPresent()) {
                    wkTypeCd = appWorkChange.get().getOpWorkTypeCD().get().v();
                    wkTimeCd = appWorkChange.get().getOpWorkTimeCD().get().v();
                    if (!appWorkChange.get().getTimeZoneWithWorkNoLst().isEmpty()) {
                        val workChangeStart = appWorkChange.get().getTimeZoneWithWorkNoLst().get(0);
                        opWorkTime = Optional.of(workChangeStart.getTimeZone().getStartTime().v());
                        opLeaveTime = Optional.of(workChangeStart.getTimeZone().getEndTime().v());
                    }
                }
                break;
            case BUSINESS_TRIP_APPLICATION:
                // 出張申請 3
                Optional<BusinessTrip> businessTrip = businessTripRepository.findById(cid, appId, date);
                if (businessTrip.isPresent() && !businessTrip.get().getInfos().isEmpty()) {
                    Optional<BusinessTripInfo> info = businessTrip.get().getInfos().stream().filter(i -> i.getDate().compareTo(date) == 0).findFirst();
                    if (info.isPresent()) {
                        wkTimeCd = info.get().getWorkInformation().getWorkTimeCode().v();
                        wkTypeCd = info.get().getWorkInformation().getWorkTypeCode().v();
                        opWorkTime = info.get().getWorkingHours().isPresent() ? Optional.of(info.get().getWorkingHours().get().get(0).getTimeZone().getStartTime().v()) : Optional.empty();
                        opLeaveTime = info.get().getWorkingHours().isPresent() ? Optional.of(info.get().getWorkingHours().get().get(0).getTimeZone().getEndTime().v()) : Optional.empty();
                    }
                }
                break;
            case ABSENCE_APPLICATION:
                // 休暇申請 1
                Optional<AppAbsence> appAbsence = appAbsenceRepository.getAbsenceByAppId(cid, appId);
                if (appAbsence.isPresent()) {
                    wkTypeCd = appAbsence.get().getWorkTypeCode().v();
                    wkTimeCd = appAbsence.get().getWorkTimeCode().v();
                    opWorkTime = appAbsence.map(i -> i.getStartTime1().getDayTime());
                    opLeaveTime = appAbsence.map(i -> i.getEndTime1().getDayTime());
                }
                break;
            case COMPLEMENT_LEAVE_APPLICATION:
                // 振休振出申請 10
                Optional<RecruitmentApp> recAppOpt = recRepo.findByID(appId);
                if (recAppOpt.isPresent()) {
                    // 振出申請
                    wkTypeCd = Optional.of(recAppOpt.get().getWorkTypeCD().v()).orElse(null);
                    wkTimeCd = Optional.of(recAppOpt.get().getWorkTimeCD().v()).orElse(null);
                    opWorkTime = recAppOpt.isPresent() ? Optional.of(recAppOpt.get().getWorkTime1().getStartTime().v()) : Optional.empty();
                    opLeaveTime = recAppOpt.isPresent() ? Optional.of(recAppOpt.get().getWorkTime1().getEndTime().v()) : Optional.empty();
                } else {
                    // 振休申請
                    Optional<AbsenceLeaveApp> absAppOpt = absRepo.findByID(appId);
                    wkTypeCd = Optional.of(absAppOpt.get().getWorkTypeCD().v()).orElse(null);
                    wkTimeCd = Optional.of(absAppOpt.get().getWorkTimeCD()).orElse(null);
                    opWorkTime = absAppOpt.isPresent() ? Optional.of(absAppOpt.get().getWorkTime1().getStartTime().v()) : Optional.empty();
                    opLeaveTime = absAppOpt.isPresent() ? Optional.of(absAppOpt.get().getWorkTime1().getEndTime().v()) : Optional.empty();
                }
                break;
        }

        //ドメインモデル「勤務種類」を1件取得する - (lấy 1 dữ liệu của domain 「WorkType」)
        wkTypeName = StringUtil.isNullOrEmpty(wkTypeCd, true) ? Optional.empty() : wkTypeRepo.findByPK(cid, wkTypeCd).map(x -> x.getName().v());
        //ドメインモデル「就業時間帯」を1件取得する - (lấy 1 dữ liệu của domain 「WorkTime」)
        wkTimeName = StringUtil.isNullOrEmpty(wkTimeCd, true) ? Optional.empty() : wkTimeRepo.findByCode(cid, wkTimeCd).map(x -> x.getWorkTimeDisplayName().getWorkTimeName().v());
        if (Strings.isBlank(wkTypeCd)) {
            content.getOpAchievementDetail().get().setWorkTypeCD(wkTypeCd);
        }
        if (Strings.isBlank(wkTimeCd)) {
            content.getOpAchievementDetail().get().setWorkTimeCD(wkTimeCd);
        }
        if (wkTypeName.isPresent()) {
            content.getOpAchievementDetail().get().setOpWorkTypeName(wkTypeName);
        }
        if (wkTimeName.isPresent()) {
            content.getOpAchievementDetail().get().setOpWorkTimeName(wkTimeName);
        }
        if (opWorkTime.isPresent()) {
            content.getOpAchievementDetail().get().setOpWorkTime(opWorkTime);
        }
        if (opLeaveTime.isPresent()) {
            content.getOpAchievementDetail().get().setOpLeaveTime(opLeaveTime);
        }
    }

}
