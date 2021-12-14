package nts.uk.ctx.at.request.dom.application.businesstrip.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.ReflectionStatusOfDay;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeave;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeaveRepository;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripInfo;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripInfoOutput;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripRepository;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripWorkHour;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripWorkTypes;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.AtEmployeeAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeInfoImport;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
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
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.businesstrip.AppTripRequestSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.businesstrip.AppTripRequestSetRepository;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.BusinessTripAppWorkType;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.TargetWorkTypeByApp;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.DeprecateClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class BusinessTripServiceImlp implements BusinessTripService {

    @Inject
    private BusinessTripRepository businessTripRepository;

    @Inject
    private ApplicationRepository appRepo;

    @Inject
    private AppWorkChangeRepository appWorkChangeRepository;
    
    @Inject
    private WorkTypeRepository wkTypeRepo;

    @Inject
    private WorkTimeSettingRepository wkTimeRepo;

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

    @Inject
    private AtEmployeeAdapter atEmployeeAdapter;

    @Inject
    private CommonAlgorithm commonAlgorithm;

    @Inject
    private AppHolidayWorkRepository appHolidayWorkRepository;

    @Inject
    private GoBackDirectlyRepository goBackDirectlyRepository;

    @Inject
    private ApplyForLeaveRepository applyForLeaveRepository;
    
    @Inject
    private PredetemineTimeSettingRepository predetemineRepo;

    private static final String NO_INPUT_NAME = "なし";

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請画面初期（新規）.アルゴリズム「出張申請未承認申請を取得」を実行する
     *
     * @param sid                       社員ID
     * @param appDate                   申請対象日リスト
     * @param opActualContentDisplayLst 申請表示情報.申請表示情報(基準日関係あり).表示する実績内容
     */
    @Override
    public void getBusinessTripNotApproved(String sid, List<GeneralDate> appDate, Optional<List<ActualContentDisplay>> opActualContentDisplayLst) {

        if (appDate.isEmpty()) {
            return;
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
                    .filter(i -> i.getOpAppStartDate().get().getApplicationDate().beforeOrEquals(date) && date.beforeOrEquals(i.getOpAppEndDate().get().getApplicationDate()))
                    .sorted(Comparator.nullsLast((e1, e2) -> e2.getInputDate().compareTo(e1.getInputDate())))
                    .collect(Collectors.toList());

            // Lấy Content của ngày loop
            Optional<ActualContentDisplay> currentContent = opActualContentDisplayLst.get().stream().filter(i -> i.getDate().equals(date)).findFirst();

            if (validApps.isEmpty()) {
                continue;
            } else {
                // 対象年月日を含む申請が存在を確認
                // Lấy Application có ngày đăng ký mới nhất sau khi sort
                Application newestApp = validApps.get(0);

                // アルゴリズム「反映状態を取得する」を実行する
                ReflectedState reflectedState = newestApp.getAppReflectedState();

                // 未反映、反映待ちの場合
                if (reflectedState == ReflectedState.WAITREFLECTION || reflectedState == ReflectedState.NOTREFLECTED) {
                    Optional<ReflectionStatusOfDay> actualReflectStatus = newestApp.getReflectionStatus().getListReflectionStatusOfDay().stream().filter(i -> i.getTargetDate().compareTo(date) == 0).findAny();
                    if (actualReflectStatus.isPresent()) {
                        // 未反映、反映待ちの場合
                        if (actualReflectStatus.get().getActualReflectStatus() == ReflectedState.WAITREFLECTION || actualReflectStatus.get().getActualReflectStatus() == ReflectedState.NOTREFLECTED) {
                            // アルゴリズム「出張申請内容より勤務情報を取得」を実行する
                            this.getWorkInfoFromTripReqContent(newestApp.getAppID(), newestApp.getAppType(), date, currentContent.get());
                        }
                    }
                }
            }
        }
    }

    /**
     * アルゴリズム「出張申請画面初期（更新）」を実行する
     * @param companyId
     * @param appId
     * @param appDispInfoStartupOutput
     * @return
     */
    @Override
    public DetailScreenB getDataDetail(String companyId, String appId, AppDispInfoStartupOutput appDispInfoStartupOutput) {
        DetailScreenB result = new DetailScreenB();
        BusinessTripInfoOutput output = new BusinessTripInfoOutput();
        // ドメインモデル「出張申請設定」を取得する
        Optional<AppTripRequestSet> tripRequestSet = appTripRequestSetRepository.findById(companyId);
        tripRequestSet.ifPresent(output::setSetting);
        // ドメインモデル「出張申請」を取得する
        Optional<BusinessTrip> businessTrip = businessTripRepository.findByAppId(companyId, appId);
        val applicationOpt = appRepo.findByID(appId);

        Optional<AppEmploymentSet> opEmploymentSet = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet();
        // アルゴリズム「出張申請勤務種類を取得する」を実行する
        List<WorkType> workDays = this.getBusinessAppWorkType(
                opEmploymentSet,
                EnumAdaptor.valueOf(BusinessTripAppWorkType.WORK_DAY.value, BusinessTripAppWorkType.class),
                new ArrayList<>(Arrays.asList(WorkTypeClassification.Attendance))
        );

        // アルゴリズム「出張申請勤務種類を取得する」を実行する
        List<WorkType> holidayWorkType = this.getBusinessAppWorkType(
                opEmploymentSet,
                EnumAdaptor.valueOf(BusinessTripAppWorkType.HOLIDAY.value, BusinessTripAppWorkType.class),
                new ArrayList<>(Arrays.asList(WorkTypeClassification.Holiday, WorkTypeClassification.HolidayWork, WorkTypeClassification.Shooting))
        );

        // ドメインモデル「勤務種類」を取得する
        List<BusinessTripWorkTypes> businessTripWorkTypes = new ArrayList<>();
        if (businessTrip.isPresent()) {
            if (applicationOpt.isPresent()) {
                businessTrip.get().setApplication(applicationOpt.get());
            }
            
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
        result.setBusinessTrip(businessTrip.orElse(null));
        return result;
    }

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請画面初期（新規）.アルゴリズム「出張申請勤務種類を取得する」を実行する
     *
     * @param appEmploymentSet       ドメインモデル「雇用別申請承認設定」
     * @param workStyle              出勤休日区分
     * @param workTypeClassification 勤務分類(LIST)
     */
    @Override
    public List<WorkType> getBusinessAppWorkType(Optional<AppEmploymentSet> appEmploymentSet, BusinessTripAppWorkType workStyle, List<WorkTypeClassification> workTypeClassification) {
        String cid = AppContexts.user().companyId();
        List<WorkType> result = Collections.emptyList();

        Optional<TargetWorkTypeByApp> opTargetWorkTypeByApp = !appEmploymentSet.isPresent() ? Optional.empty() : appEmploymentSet.get()
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
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請個別エラーチェック.アルゴリズム「出張申請就業時間帯チェック」を実行する
     * @param wkTypeCd              対象日の画面の勤務種類コード
     * @param wkTimeCd              対象日の画面の就業時間帯コード
     * @param startWorkTime         対象日の出勤時刻
     * @param endWorkTime           対象日の退勤時刻
     * @param checkInputTime        入力チェック
     */
    @Override
    public ResultCheckInputCode checkRequireWorkTimeCode(String wkTypeCd, String wkTimeCd, Integer startWorkTime, Integer endWorkTime, boolean checkInputTime) {
        SetupType checkNeededOfWorkTime = basicScheduleService.checkNeededOfWorkTimeSetting(wkTypeCd);
        switch (checkNeededOfWorkTime) {
            case REQUIRED:
                if (Strings.isBlank(wkTimeCd)) {
                    return new ResultCheckInputCode(false, "Msg_24");
                } else {
                    if (!checkInputTime && Strings.isBlank(wkTimeCd)) {
                        return new ResultCheckInputCode(true, null);
                    } else {
                        return this.checkInputTimeCode(startWorkTime, endWorkTime);
                    }
                }
            case OPTIONAL:
                if (!(!checkInputTime && Strings.isBlank(wkTimeCd))) {
                    return this.checkInputTimeCode(startWorkTime, endWorkTime);
                }
                break;
            case NOT_REQUIRED:
                if (Strings.isNotBlank(wkTimeCd)) {
                    return new ResultCheckInputCode(false, "Msg_23");
                } else {
                    if (!checkInputTime && Strings.isBlank(wkTimeCd)) {
                        return new ResultCheckInputCode(true, null);
                    } else {
                        return this.checkInputTimeCode(startWorkTime, endWorkTime);
                    }
                }
        }
        return new ResultCheckInputCode(true, null);
    }

    private ResultCheckInputCode checkInputTimeCode(Integer startWorkTime, Integer endWorkTime) {
        // 出勤時刻、退勤時刻のいずれかが空白の場合
//        if (startWorkTime == null || endWorkTime == null) {
//            return new ResultCheckInputCode(false, "Msg_1912");
//        } else {
//        }
        // 出勤時刻＞退勤時刻となっている場合
        if (startWorkTime != null && endWorkTime != null && startWorkTime > endWorkTime) {
            return new ResultCheckInputCode(false, "Msg_1913");
        }
        return new ResultCheckInputCode(true, null);
    }

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請勤務変更ダイアログ用情報の取得.アルゴリズム「出張申請勤務種類分類内容取得」を実行する
     * @param workType INPUT.勤務種類
     * @return 出勤日(True/False)
     */
    @Override
    public boolean getBusinessTripClsContent(WorkType workType) {
        Boolean result = true;
        val workCls = workType.getDailyWork().getWorkTypeUnit();
        List<WorkTypeClassification> workDays = Arrays.asList(
                WorkTypeClassification.Attendance, // 出勤
                WorkTypeClassification.AnnualHoliday, // 年休
                WorkTypeClassification.YearlyReserved, // 積立年休
                WorkTypeClassification.SpecialHoliday, // 特別年休
                WorkTypeClassification.Absence, // 欠勤
                WorkTypeClassification.SubstituteHoliday, // 代休
                WorkTypeClassification.Pause, // 振休
                WorkTypeClassification.TimeDigestVacation // 時間消化休暇
        );
        List<WorkTypeClassification> holidays = Arrays.asList(
                WorkTypeClassification.Holiday, // 休日
                WorkTypeClassification.HolidayWork, // 休日出勤
                WorkTypeClassification.Shooting // 振出
        );
        switch (workCls) {
            case OneDay:
                WorkTypeClassification workTypeClassification = workType.getDailyWork().getOneDay();
                if (workDays.contains(workTypeClassification)) {
                    result = true;
                }
                if (holidays.contains(workTypeClassification)) {
                    result = false;
                }
                break;
            case MonringAndAfternoon:
                WorkTypeClassification afternoonWork = workType.getDailyWork().getAfternoon();
                WorkTypeClassification morningWork = workType.getDailyWork().getMorning();
                if (holidays.contains(morningWork) && holidays.contains(afternoonWork)) {
                    result = false;
                }
                if (workDays.contains(morningWork) || workDays.contains(afternoonWork)) {
                    result = true;
                }
                break;
        }
        return result;
    }

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請登録前エラーチェック.アルゴリズム「出張申請個別エラーチェック」を実行する
     * @param infos                 出張申請
     * @param output                出張申請の表示情報
     * @param screenWorkInfoName
     */
    @Override
    public void businessTripIndividualCheck(List<BusinessTripInfo> infos, BusinessTripInfoOutput output, Map<GeneralDate, ScreenWorkInfoName> screenWorkInfoName) {
        String sid = AppContexts.user().employeeId();
        String cid = AppContexts.user().companyId();
        List<EmployeeInfoImport> employeeInfoImports = atEmployeeAdapter.getByListSID(Arrays.asList(sid));

        // loop 年月日　in　期間
        infos.forEach(i -> {
            String wkTypeCd = i.getWorkInformation().getWorkTypeCode().v();
            String wkTimeCd = i.getWorkInformation().getWorkTimeCode() == null ? null : i.getWorkInformation().getWorkTimeCode().v();
            Integer workTimeStart = null;
            Integer workTimeEnd = null;

            workTimeStart = i.getWorkingHours().get(0).getStartDate().isPresent() ? 
                    i.getWorkingHours().get(0).getStartDate().map(TimeWithDayAttr::v).get() : null;
            workTimeEnd =  i.getWorkingHours().get(0).getEndDate().isPresent() ? 
                    i.getWorkingHours().get(0).getEndDate().map(TimeWithDayAttr::v).get() : null;

            // アルゴリズム「出張申請就業時間帯チェック」を実行する
            ResultCheckInputCode checkRequiredCode = this.checkRequireWorkTimeCode(wkTypeCd, wkTimeCd, workTimeStart, workTimeEnd, false);
            if (!checkRequiredCode.isResult()) {
                // エラーを「年月日＋メッセージ」とする
                throw new BusinessException(checkRequiredCode.getMsg(), i.getDate().toString());
            }

            // アルゴリズム「出張申請登録前入力コードチェック」を実行する
            ResultCheckInputCode checkInputValidCode = this.checkInputCodeBeforeRegister(
                    i.getDate(),
                    output,
                    wkTypeCd,
                    wkTimeCd,
                    screenWorkInfoName.containsKey(i.getDate()) ? screenWorkInfoName.get(i.getDate()).getWorkTypeName() : "",
                    screenWorkInfoName.containsKey(i.getDate()) ? screenWorkInfoName.get(i.getDate()).getWorkTimeName() : ""
            );
            if (!checkInputValidCode.isResult()) {
                // エラーを「年月日＋メッセージ」とする
                throw new BusinessException(checkInputValidCode.getMsg(), i.getDate().toString());
            }

            // アルゴリズム「申請の矛盾チェック」を実行する
            this.commonAlgorithm.appConflictCheck(
                    cid,
                    employeeInfoImports.get(0),
                    Arrays.asList(i.getDate()),
                    new ArrayList<>(Arrays.asList(i.getWorkInformation().getWorkTypeCode().v())),
                    output.getAppDispInfoStartup()
                            .getAppDispInfoWithDateOutput()
                            .getOpActualContentDisplayLst().isPresent() ?
                            output.getAppDispInfoStartup()
                                    .getAppDispInfoWithDateOutput()
                                    .getOpActualContentDisplayLst().get() : Collections.emptyList()
            );
            
            // 勤務種類により出退勤時刻をチェックする
            this.checkTimeByWorkType(i.getDate(), wkTypeCd, workTimeStart, workTimeEnd);

        });
    }

    @Override
    public BusinessTripInfoOutput checkChangeWorkTypeCode(GeneralDate inputDate, BusinessTripInfoOutput infoOutput, String inputCode) {
        String cid = AppContexts.user().companyId();

        // コードが未入力
        if (Strings.isBlank(inputCode)) {
            throw new BusinessException("Msg_1329", inputDate.toString());
        }

        // アルゴリズム「出張申請入力コードチェック」を実行する
        CheckChangeWorkCode checkInputCode = this.checkInputCode(inputDate, infoOutput, inputCode);

        if (checkInputCode.isResult()) {
            // ドメインモデル「勤務種類」を取得する
            Optional<WorkType> getWorkTypeInfo = wkTypeRepo.findByPK(cid, checkInputCode.getCode());
            if (infoOutput.getWorkTypeAfterChange().isPresent()) {
                // 変更後勤務種類にセット
                BusinessTripWorkTypes itemAfterChange = new BusinessTripWorkTypes(inputDate, getWorkTypeInfo.get());
                if (infoOutput.getWorkTypeAfterChange().get().contains(itemAfterChange)) {
                    int index = infoOutput.getWorkTypeAfterChange().get().indexOf(itemAfterChange);
                    infoOutput.getWorkTypeAfterChange().get().set(index, itemAfterChange);
                } else {
                    infoOutput.getWorkTypeAfterChange().get().add(itemAfterChange);
                }
            }
        } else {
            // #Msg_457(利用できる勤怠項目がありません。)を表示する
            throw new BusinessException("Msg_457", inputDate.toString());
        }

        return infoOutput;
    }

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.ユースケース.勤務種類コードを入力する.システム.アルゴリズム「出張申請入力コードチェック」を実行する
     * @param inputDate         変更対象の年月日
     * @param infoOutput        出張申請の表示情報
     * @param inputCode         画面入力した勤務種類CD
     * @return 結果(True/False)
     */
    private CheckChangeWorkCode checkInputCode(GeneralDate inputDate, BusinessTripInfoOutput infoOutput, String inputCode) {

        Optional<BusinessTripWorkTypes> currentDateWorkType = infoOutput.getWorkTypeBeforeChange().get().stream().filter(i -> i.getDate().equals(inputDate)).findFirst();

        List<WorkType> workTypesBeforeChange = new ArrayList<>();

        if (currentDateWorkType.isPresent()) {
            // アルゴリズム「出張申請勤務変更ダイアログ用情報の取得」を実行する
            workTypesBeforeChange = getBusinessTripWorkChangeInfo(infoOutput, currentDateWorkType.get().getWorkType());
        }

        // 取得した勤務種類リストの中に、INPUT.勤務種類コードが存在する
        Optional<WorkType> inputWorkType = workTypesBeforeChange.stream().filter(i -> i.getWorkTypeCode().v().equals(inputCode)).findFirst();

        return inputWorkType.map(workType -> new CheckChangeWorkCode(true, workType.getWorkTypeCode().v())).orElseGet(() -> new CheckChangeWorkCode(false, null));

    }


    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請入力コードチェック.アルゴリズム「出張申請勤務変更ダイアログ用情報の取得」を実行する
     * @param businessTripInfoOutput    出張申請の表示情報
     * @param workType                  出張申請の表示情報.変更前勤務種類.勤務種類（年月日＝年月日）
     * @return 勤務種類リスト
     */
    private List<WorkType> getBusinessTripWorkChangeInfo(BusinessTripInfoOutput businessTripInfoOutput, WorkType workType) {
        Boolean businessTripWorkCls = this.getBusinessTripClsContent(workType);
        if (businessTripWorkCls) {
            // 勤務種類リスト＝出張申請の表示情報.出勤日勤務種類リスト
            return businessTripInfoOutput.getWorkDayCds().get();
        } else {
            // 勤務種類リスト＝出張申請の表示情報.休日勤務種類リスト
            return businessTripInfoOutput.getHolidayCds().get();
        }
    }

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請個別エラーチェック.アルゴリズム「出張申請登録前入力コードチェック」を実行する
     * @param date              年月日
     * @param output            出張申請の表示情報
     * @param wkTypeCd          勤務種類コード
     * @param wkTimeCd          就業時間帯コード
     * @param wkTypeName        勤務種類名称
     * @param wkTimeName        就業時間帯名称
     * @return 結果(True/False)
     */
    public ResultCheckInputCode checkInputCodeBeforeRegister(GeneralDate date, BusinessTripInfoOutput output, String wkTypeCd, String wkTimeCd, String wkTypeName, String wkTimeName) {

        if (Strings.isNotBlank(wkTypeCd) && wkTypeName.equals(NO_INPUT_NAME)) {
            CheckChangeWorkCode checkWkCode = this.checkInputCode(date, output, wkTypeCd);
            if (!checkWkCode.isResult()) {
                return new ResultCheckInputCode(false, "Msg_457");
            }
        }
        if (Strings.isNotBlank(wkTimeCd) && wkTimeName.equals(NO_INPUT_NAME)) {
            Optional<List<WorkTimeSetting>> wkTimeSetList = output.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpWorkTimeLst();
            if (wkTimeSetList.isPresent()) {
                Optional<WorkTimeSetting> timeSet = wkTimeSetList.get().stream().filter(i -> i.getWorktimeCode().v().equals(wkTimeCd)).findFirst();
                if (!timeSet.isPresent()) {
                    return new ResultCheckInputCode(false, "Msg_1685");
                }
            }
        }

        return new ResultCheckInputCode(true, null);
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
        String wkTypeCd = content.getOpAchievementDetail().get().getWorkTypeCD();
        String wkTimeCd = content.getOpAchievementDetail().get().getWorkTimeCD();
        Optional<String> wkTypeName = content.getOpAchievementDetail().get().getOpWorkTypeName();
        Optional<String> wkTimeName = content.getOpAchievementDetail().get().getOpWorkTimeName();
        Optional<Integer> opWorkTime = content.getOpAchievementDetail().get().getOpWorkTime();
        Optional<Integer> opLeaveTime = content.getOpAchievementDetail().get().getOpLeaveTime();

        switch (appType) {
            case HOLIDAY_WORK_APPLICATION:
                //休日出勤申請 6
                Optional<AppHolidayWork> appHolidayWork = appHolidayWorkRepository.find(cid, appId);
                if (appHolidayWork.isPresent()) {
                    wkTypeCd = appHolidayWork.get().getWorkInformation().getWorkTypeCode().v();
                    wkTimeCd = appHolidayWork.get().getWorkInformation().getWorkTimeCode() == null ? null : appHolidayWork.get().getWorkInformation().getWorkTimeCode().v();
                    opWorkTime = appHolidayWork.get().getWorkingTimeList().isPresent() ?
                            Optional.of(appHolidayWork.get().getWorkingTimeList().get().get(0).getTimeZone().getStartTime().v()) : Optional.empty();
                    opLeaveTime = appHolidayWork.get().getWorkingTimeList().isPresent() ?
                            Optional.of(appHolidayWork.get().getWorkingTimeList().get().get(0).getTimeZone().getEndTime().v()) : Optional.empty();
                }
                break;
            case GO_RETURN_DIRECTLY_APPLICATION:
                // 直行直帰申請 4
                Optional<GoBackDirectly> goBackDirectly = goBackDirectlyRepository.find(cid, appId);
                if (goBackDirectly.isPresent() && goBackDirectly.get().getDataWork().isPresent()) {
                    wkTypeCd = goBackDirectly.get().getDataWork().get().getWorkTypeCode().v();
                    wkTimeCd = goBackDirectly.get().getDataWork().get().getWorkTimeCode() == null ? null : goBackDirectly.get().getDataWork().get().getWorkTimeCode().v();
                }
                break;
            case WORK_CHANGE_APPLICATION:
                // 勤務変更 2
                Optional<AppWorkChange> appWorkChange = appWorkChangeRepository.findbyID(cid, appId);
                if (appWorkChange.isPresent()
                        && appWorkChange.get().getOpWorkTimeCD().isPresent()
                        && appWorkChange.get().getOpWorkTimeCD().isPresent()) {
                    wkTypeCd = appWorkChange.get().getOpWorkTypeCD().get().v();
                    wkTimeCd = appWorkChange.get().getOpWorkTimeCD().get() == null ? null : appWorkChange.get().getOpWorkTimeCD().get().v();
                    if (!appWorkChange.get().getTimeZoneWithWorkNoLst().isEmpty()) {
                        val workChangeStart = appWorkChange.get().getTimeZoneWithWorkNoLst().get(0);
                        opWorkTime = Optional.of(workChangeStart.getTimeZone().getStartTime().v());
                        opLeaveTime = Optional.of(workChangeStart.getTimeZone().getEndTime().v());
                    } else {
                        opWorkTime = Optional.empty();
                        opLeaveTime = Optional.empty();
                    }
                }
                break;
            case BUSINESS_TRIP_APPLICATION:
                // 出張申請 3
                Optional<BusinessTrip> businessTrip = businessTripRepository.findById(cid, appId, date);
                if (businessTrip.isPresent() && !businessTrip.get().getInfos().isEmpty()) {
                    Optional<BusinessTripInfo> info = businessTrip.get().getInfos().stream().filter(i -> i.getDate().compareTo(date) == 0).findFirst();
                    if (info.isPresent()) {
                        wkTimeCd = info.get().getWorkInformation().getWorkTimeCode() == null ? null : info.get().getWorkInformation().getWorkTimeCode().v();
                        wkTypeCd = info.get().getWorkInformation().getWorkTypeCode().v();
                        
                        Optional<BusinessTripWorkHour> workTimeFilter = info.get().getWorkingHours().stream().filter(x -> x.getWorkNo().v() == 1).findFirst();
                        opWorkTime = workTimeFilter.isPresent() ? workTimeFilter.get().getStartDate().map(TimeWithDayAttr::v) : Optional.empty();
                        opLeaveTime = workTimeFilter.isPresent() ? workTimeFilter.get().getEndDate().map(TimeWithDayAttr::v) : Optional.empty();
                    }
                }
                break;
            case ABSENCE_APPLICATION:
                // 休暇申請 1
                Optional<ApplyForLeave> appAbsence = applyForLeaveRepository.findApplyForLeave(cid, appId);
                if (appAbsence.isPresent()) {
                    wkTypeCd = appAbsence.get().getReflectFreeTimeApp().getWorkInfo().getWorkTypeCode().v();
                    wkTimeCd = appAbsence.get().getReflectFreeTimeApp().getWorkInfo().getWorkTimeCode() == null ? null : appAbsence.get().getReflectFreeTimeApp().getWorkInfo().getWorkTimeCode().v();
                    opWorkTime = appAbsence.get().getReflectFreeTimeApp().getWorkingHours().isPresent() ?
                            Optional.of(appAbsence.get().getReflectFreeTimeApp().getWorkingHours().get().get(0).getTimeZone().getStartTime().v()) : Optional.empty();
                    opLeaveTime = appAbsence.get().getReflectFreeTimeApp().getWorkingHours().isPresent() ?
                            Optional.of(appAbsence.get().getReflectFreeTimeApp().getWorkingHours().get().get(0).getTimeZone().getEndTime().v()) : Optional.empty();
                }
                break;
            case COMPLEMENT_LEAVE_APPLICATION:
                // 振休振出申請 10
                Optional<RecruitmentApp> recAppOpt = recRepo.findByID(appId);
                if (recAppOpt.isPresent()) {
                    // 振出申請
                    wkTypeCd = Optional.of(recAppOpt.get().getWorkInformation().getWorkTypeCode().v()).orElse(null);
                    wkTimeCd = Optional.of(recAppOpt.get().getWorkInformation().getWorkTimeCode().v()).orElse(null);
                    opWorkTime = Optional.of(recAppOpt.get().getWorkTime(new WorkNo(1)).get().getTimeZone().getStartTime().v());
                    opLeaveTime = Optional.of(recAppOpt.get().getWorkTime(new WorkNo(1)).get().getTimeZone().getEndTime().v());
                } else {
                    // 振休申請
                    Optional<AbsenceLeaveApp> absAppOpt = absRepo.findByID(appId);
                    if (absAppOpt.isPresent()) {
                        wkTypeCd = absAppOpt.get().getWorkInformation().getWorkTypeCode().v();
                        wkTimeCd = absAppOpt.get().getWorkInformation().getWorkTimeCodeNotNull().isPresent() ? absAppOpt.get().getWorkInformation().getWorkTimeCodeNotNull().get().v() : null;
                        opWorkTime = absAppOpt.get().getWorkTime(new WorkNo(1)).map(c -> c.getTimeZone().getStartTime().v());
                        opLeaveTime = absAppOpt.get().getWorkTime(new WorkNo(1)).map(c -> c.getTimeZone().getEndTime().v());
                    }
                }
                break;
        }

        //ドメインモデル「勤務種類」を1件取得する - (lấy 1 dữ liệu của domain 「WorkType」)
        wkTypeName = Strings.isBlank(wkTypeCd) ? Optional.empty() : wkTypeRepo.findByPK(cid, wkTypeCd).map(x -> x.getName().v());
        //ドメインモデル「就業時間帯」を1件取得する - (lấy 1 dữ liệu của domain 「WorkTime」)
        wkTimeName = Strings.isBlank(wkTimeCd) ? Optional.empty() : wkTimeRepo.findByCode(cid, wkTimeCd).map(x -> x.getWorkTimeDisplayName().getWorkTimeName().v());

        content.getOpAchievementDetail().get().setWorkTypeCD(wkTypeCd);
        content.getOpAchievementDetail().get().setWorkTimeCD(wkTimeCd);
        content.getOpAchievementDetail().get().setOpWorkTypeName(wkTypeName);
        content.getOpAchievementDetail().get().setOpWorkTimeName(wkTimeName);
        content.getOpAchievementDetail().get().setOpWorkTime(opWorkTime);
        content.getOpAchievementDetail().get().setOpLeaveTime(opLeaveTime);

    }

    @Override
    public BusinessTripInfoOutput setInitValueAppWorkTime(BusinessTripInfoOutput input) {
        val result = input;
        
        Optional<List<ActualContentDisplay>> opActualContentDisplayLst = result.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst();
        // 申請表示情報.申請設定(基準日関連なし）.表示する実績内容.実績詳細
        if (opActualContentDisplayLst.isPresent()) {
//            List<BusinessTripWorkingHours> workingHours = opActualContentDisplayLst.get().stream()
//                    .filter(x -> x.getOpAchievementDetail().isPresent())
//                    .map(x -> BusinessTripWorkingHours.fromAchievementDetail(x.getOpAchievementDetail().get()))
//                    .collect(Collectors.toList());
            List<BusinessTripWorkingHours> workingHours = new ArrayList<BusinessTripWorkingHours>();
            for (int i = 0; i < opActualContentDisplayLst.get().size(); i++) {
                final int index = i;
                
                Optional<AchievementDetail> optAchievementDetail = opActualContentDisplayLst.get().get(index).getOpAchievementDetail();
                if (optAchievementDetail.isPresent()) {
                    // 対象項目いずれかが未登録の場合
                    Optional<Integer> opWorkTime = optAchievementDetail.get().getOpWorkTime();
                    Optional<Integer> opLeaveTime = optAchievementDetail.get().getOpLeaveTime();
                    String workTimeCd = optAchievementDetail.get().getWorkTimeCD();
                    if (!opWorkTime.isPresent() || !opLeaveTime.isPresent()) {
                        // アルゴリズム「出張申請就業時刻を取得する」を実行する
                        WorkType workType = result.getWorkTypeBeforeChange().isPresent() ? 
                                result.getWorkTypeBeforeChange().get().stream()
                                .filter(x -> x.getDate().equals(opActualContentDisplayLst.get().get(index).getDate()))
                                .map(x -> x.getWorkType())
                                .findFirst().orElse(null) : null;
                        WorkTimeGetOuput workTimeGetOutput = getWorkTimeBusinessTrip(workType, workTimeCd, workingHours);
                        if (!workTimeGetOutput.getWorkingHours().isEmpty()) {
                            workingHours = workTimeGetOutput.getWorkingHours();
                        }
                        // 未登録の対象項目に値をセットする
                        if (!opWorkTime.isPresent()) {
                            optAchievementDetail.get().setOpWorkTime(workTimeGetOutput.getStartTime1());
                        }
                        if (!opLeaveTime.isPresent()) {
                            optAchievementDetail.get().setOpLeaveTime(workTimeGetOutput.getEndTime1());
                        }
                        if (!optAchievementDetail.get().getOpWorkTime2().isPresent()) {
                            optAchievementDetail.get().setOpWorkTime2(workTimeGetOutput.getStartTime2());
                        }
                        if (!optAchievementDetail.get().getOpDepartureTime2().isPresent()) {
                            optAchievementDetail.get().setOpDepartureTime2(workTimeGetOutput.getEndTime2());
                        }
                    }
                    
                    if (workTimeCd == null) {
                        optAchievementDetail.get().setOpWorkTime(Optional.empty());
                        optAchievementDetail.get().setOpLeaveTime(Optional.empty());
                        optAchievementDetail.get().setOpWorkTime2(Optional.empty());
                        optAchievementDetail.get().setOpDepartureTime2(Optional.empty());
                    }
                }
            }
        }
        
        result.setActualContentDisplay(opActualContentDisplayLst);
        
        return result;
    }

    @Override
    public WorkTimeGetOuput getWorkTimeBusinessTrip(WorkType workType, String workTimeCd, List<BusinessTripWorkingHours> workingHours) {
        WorkTimeGetOuput output = new WorkTimeGetOuput();
        output.setWorkingHours(workingHours);
        
        // 就業時間帯コードが存在するか
        if (workTimeCd == null) {
            return output;
        }
        
        // List＜出張申請就業時間帯時刻＞の内容をチェックする
        if (workingHours.stream().filter(x -> x.getWorkTypeCd().equals(workType.getWorkTypeCode().v()) && x.getWorkTimeCd().equals(workTimeCd)).findFirst().isPresent()) {
            BusinessTripWorkingHours workingHour = workingHours.stream().findFirst().get();
            output.setStartTime1(Optional.of(workingHour.getStartTime1()));
            output.setEndTime1(Optional.of(workingHour.getEndTime1()));
            output.setStartTime2(Optional.of(workingHour.getStartTime2()));
            output.setEndTime2(Optional.of(workingHour.getEndTime2()));
            return output;
        }
        
        // ドメインモデル「所定時間設定」を取得する
        Optional<PredetemineTimeSetting> predetemineTimeSetting = this.predetemineRepo.
                findByWorkTimeCode(AppContexts.user().companyId(), workTimeCd);
        if (!predetemineTimeSetting.isPresent()) {
            return output;
        }
        
        // 勤務種類.１日の勤務.勤務区分
        WorkTypeUnit workTypeUnit = workType.getDailyWork().getWorkTypeUnit();
        
        if (workTypeUnit.equals(WorkTypeUnit.OneDay)) {
            // 勤務種類.1日の勤務.1日をチェック（※勤務種類の判断条件参照）
            if (isWorkTypeWork(workType.getDailyWork().getOneDay())) {
                // 取得した「所定時間設定」の「午前午後区分に応じた所定時間帯」を実施
                List<TimezoneUse> lstTimezone = predetemineTimeSetting.get().getPrescribedTimezoneSetting().getLstTimezone();
                if (lstTimezone.stream().filter(x -> x.getWorkNo() == 1).findFirst().isPresent()) {
                    output.setStartTime1(lstTimezone.stream().filter(x -> x.getWorkNo() == 1).findFirst().map(x -> x.getStart().v()));
                    output.setEndTime1(lstTimezone.stream().filter(x -> x.getWorkNo() == 1).findFirst().map(x -> x.getEnd().v()));
                }
                if (lstTimezone.stream().filter(x -> x.getWorkNo() == 2).findFirst().isPresent()) {
                    output.setStartTime2(lstTimezone.stream().filter(x -> x.getWorkNo() == 2).findFirst().map(x -> x.getStart().v()));
                    output.setEndTime2(lstTimezone.stream().filter(x -> x.getWorkNo() == 2).findFirst().map(x -> x.getEnd().v()));
                }
            }
        } else if (workTypeUnit.equals(WorkTypeUnit.MonringAndAfternoon)) {
            // 勤務種類.1日の勤務.午前をチェック
            // 勤務種類.1日の勤務.午後をチェック
            if ((isWorkTypeHoliday(workType.getDailyWork().getMorning()) && isWorkTypeWork(workType.getDailyWork().getAfternoon())) || 
                    (isWorkTypeWork(workType.getDailyWork().getMorning()))) {
                // 取得した「所定時間設定」の「午前午後区分に応じた所定時間帯」を実施
                List<TimezoneUse> lstTimezone = predetemineTimeSetting.get().getPrescribedTimezoneSetting().getLstTimezone();
                if (lstTimezone.stream().filter(x -> x.getWorkNo() == 1).findFirst().isPresent()) {
                    output.setStartTime1(lstTimezone.stream().filter(x -> x.getWorkNo() == 1).findFirst().map(x -> x.getStart().v()));
                    output.setEndTime1(lstTimezone.stream().filter(x -> x.getWorkNo() == 1).findFirst().map(x -> x.getEnd().v()));
                }
                if (lstTimezone.stream().filter(x -> x.getWorkNo() == 2).findFirst().isPresent()) {
                    output.setStartTime2(lstTimezone.stream().filter(x -> x.getWorkNo() == 2).findFirst().map(x -> x.getStart().v()));
                    output.setEndTime2(lstTimezone.stream().filter(x -> x.getWorkNo() == 2).findFirst().map(x -> x.getEnd().v()));
                }
            }
        }
        
        // キャッシュに結果を保存する
        output.getWorkingHours().addAll(Arrays.asList(new BusinessTripWorkingHours(
                workType.getWorkTypeCode().v(), 
                workTimeCd, 
                output.getStartTime1().orElse(0), 
                output.getEndTime1().orElse(0), 
                output.getStartTime2().orElse(0), 
                output.getEndTime2().orElse(0))));
        System.out.println(output.getWorkingHours().size());
        return output;
    }
    
    public boolean isWorkTypeWork(WorkTypeClassification workTypeAtr) {
        switch (workTypeAtr) {
        case Attendance:
        case Shooting: 
        case HolidayWork:
            return true;

        default:
            return false;
        }
    }
    
    public boolean isWorkTypeHoliday(WorkTypeClassification workTypeAtr) {
        switch (workTypeAtr) {
        case Holiday:
        case AnnualHoliday: 
        case YearlyReserved: 
        case SpecialHoliday: 
        case Absence: 
        case SubstituteHoliday: 
        case Pause: 
        case TimeDigestVacation: 
        case LeaveOfAbsence: 
        case Closure:
            return true;

        default:
            return false;
        }
    }

    @Override
    public void checkTimeByWorkType(GeneralDate date, String workTypeCode, Integer startTime,
            Integer endTime) {
        // 「勤務種類」を取得する
        Optional<WorkType> wkTypeOpt = wkTypeRepo.findByPK(AppContexts.user().companyId(), workTypeCode);
        
        if (!wkTypeOpt.isPresent()) {
            return;
        }
        
        // 分類を判断する
        boolean isWorkingType = this.isWorkingType(wkTypeOpt.get());
        
        // 分類と出勤時刻・退勤時刻をチェックする
        if (isWorkingType) {
            if (startTime == null && endTime == null) {
                throw new BusinessException("Msg_2301", date.toString());
            }
        } else {
            if (startTime != null || endTime != null) {
                throw new BusinessException("Msg_2302", date.toString());
            }
        }
    }
    
    private boolean isWorkingType(WorkType workType) {
        WorkTypeUnit workTypeUnit = workType.getDailyWork().getWorkTypeUnit();
        WorkTypeClassification oneDay = workType.getDailyWork().getOneDay();
        WorkTypeClassification morning = workType.getDailyWork().getMorning();
        WorkTypeClassification afternoon = workType.getDailyWork().getAfternoon();
        
        if (workTypeUnit.equals(WorkTypeUnit.OneDay)) {
            if (oneDay.equals(WorkTypeClassification.Attendance)
                    || oneDay.equals(WorkTypeClassification.Shooting) 
                    || oneDay.equals(WorkTypeClassification.ContinuousWork) 
                    || oneDay.equals(WorkTypeClassification.HolidayWork)) {
                return true;
            }
        }
        if (workTypeUnit.equals(WorkTypeUnit.MonringAndAfternoon)) {
            if (morning.equals(WorkTypeClassification.Attendance)
                    || morning.equals(WorkTypeClassification.Shooting) 
                    || morning.equals(WorkTypeClassification.ContinuousWork) 
                    || morning.equals(WorkTypeClassification.HolidayWork)) {
                return true;
            }
            if (afternoon.equals(WorkTypeClassification.Attendance)
                    || afternoon.equals(WorkTypeClassification.Shooting) 
                    || afternoon.equals(WorkTypeClassification.ContinuousWork) 
                    || afternoon.equals(WorkTypeClassification.HolidayWork)) {
                return true;
            }
        }
        
        
        return false;
    }

}
