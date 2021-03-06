package nts.uk.ctx.at.request.app.find.application.businesstrip;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.app.find.application.ApplicationDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.BusinessTripMobileDto.ApproveTripRequestParam;
import nts.uk.ctx.at.request.app.find.application.businesstrip.BusinessTripMobileDto.CheckPeriodDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.BusinessTripMobileDto.DetailScreenInfo;
import nts.uk.ctx.at.request.app.find.application.businesstrip.BusinessTripMobileDto.StartScreenBDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.BusinessTripActualContentDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.BusinessTripDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.BusinessTripInfoOutputDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.BusinessTripOutputDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.ChangeWorkCodeParam;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.CheckBeforeRegisterDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.DetailScreenDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.DetailStartScreenInfoDto;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.ParamStartKDL003;
import nts.uk.ctx.at.request.app.find.application.businesstrip.businesstripdto.WorkTypeNameDto;
import nts.uk.ctx.at.request.app.find.application.gobackdirectly.ParamUpdate;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationDate;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripInfoOutput;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripWorkTypes;
import nts.uk.ctx.at.request.dom.application.businesstrip.service.BusinessTripService;
import nts.uk.ctx.at.request.dom.application.businesstrip.service.DetailScreenB;
import nts.uk.ctx.at.request.dom.application.businesstrip.service.ResultCheckInputCode;
import nts.uk.ctx.at.request.dom.application.businesstrip.service.ScreenWorkInfoName;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.init.DetailAppCommonSetService;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.businesstrip.AppTripRequestSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.businesstrip.AppTripRequestSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.BusinessTripAppWorkType;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class BusinessTripFinder {

    @Inject
    private CommonAlgorithm commonAlgorithm;

    @Inject
    private NewBeforeRegister processBeforeRegister;
    
    @Inject
    private DetailBeforeUpdate detailBeforeProcessRegisterService;

    @Inject
    private WorkTypeRepository wkTypeRepo;

    @Inject
    private BusinessTripService businessTripService;

    @Inject
    private AppTripRequestSetRepository appTripRequestSetRepository;

    @Inject
    private DetailAppCommonSetService appCommonSetService;

    /**
     * ????????????
     * @param paramStart
     * @return
     */
    public DetailStartScreenInfoDto initKAF008(ParamStart paramStart) {
        String cid = AppContexts.user().companyId();
        DetailStartScreenInfoDto result = new DetailStartScreenInfoDto();
        List<GeneralDate> dateList = paramStart.getDateLst().stream().map(i -> GeneralDate.fromString(i, "yyyy/MM/dd")).collect(Collectors.toList());

        AppDispInfoStartupOutput appDispInfoStartupOutput = paramStart.getAppDispInfoStartupOutput().toDomain();
        // ???????????????????????????????????????????????????????????????????????????
        BusinessTripInfoOutputDto businessTripInfoOutputDto = this.businessScreenInit_New(cid, paramStart.getApplicantList(), dateList, appDispInfoStartupOutput);
        // ?????????????????????????????????????????????????????????????????????????????????????????????
        // Check xem c?? ng??y n??o kh??ng c?? content kh??ng, n???u c?? add Error msg 1695 + date
        Optional<BusinessTripActualContentDto> itemNotHaveConent = businessTripInfoOutputDto.getBusinessTripActualContent()
                .stream()
                .filter(i -> i.getOpAchievementDetail() == null).findFirst();
        if (itemNotHaveConent.isPresent()) {
            result.setConfirmMsgOutputs(Arrays.asList(new ConfirmMsgOutput("Msg_1695", Arrays.asList(itemNotHaveConent.get().getDate()))));
            result.setResult(false);
        } else {
            result.setResult(true);
        }
        result.setBusinessTripInfoOutputDto(businessTripInfoOutputDto);
        return result;
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @param cid                      ??????ID
     * @param applicantList            ??????????????????
     * @param appDate                  ????????????????????????
     * @param appDispInfoStartupOutput ??????????????????
     */
    private BusinessTripInfoOutputDto businessScreenInit_New(String cid, List<String> applicantList, List<GeneralDate> appDate, AppDispInfoStartupOutput appDispInfoStartupOutput) {
        // ????????????????????????????????????????????????????????????
        String sid = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getEmpHistImport().getEmployeeId();
        Optional<AppTripRequestSet> tripRequestSet = appTripRequestSetRepository.findById(cid);
        Optional<AppEmploymentSet> opEmploymentSet = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet();
        // ???????????????????????????????????????
        List<WorkType> workDays = businessTripService.getBusinessAppWorkType(
                opEmploymentSet,
                EnumAdaptor.valueOf(BusinessTripAppWorkType.WORK_DAY.value, BusinessTripAppWorkType.class),
                new ArrayList<>(Arrays.asList(WorkTypeClassification.Attendance))
        );
        // ??????????????????????????????????????????????????????????????????????????????
        List<WorkType> holidayWorkType = businessTripService.getBusinessAppWorkType(
                opEmploymentSet,
                EnumAdaptor.valueOf(BusinessTripAppWorkType.HOLIDAY.value, BusinessTripAppWorkType.class),
                new ArrayList<>(Arrays.asList(WorkTypeClassification.Holiday, WorkTypeClassification.HolidayWork, WorkTypeClassification.Shooting))
        );

        Optional<List<ActualContentDisplay>> opActualContentDisplayLst = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst();

        // ???????????????????????????????????????????????????????????????????????????
        businessTripService.getBusinessTripNotApproved(sid, appDate, opActualContentDisplayLst);

        List<BusinessTripWorkTypes> businessTripWorkTypes = new ArrayList<>();

        if (opActualContentDisplayLst.isPresent() && !opActualContentDisplayLst.get().isEmpty()) {
            List<String> cds = opActualContentDisplayLst.get().stream().filter(i -> i.getOpAchievementDetail().isPresent())
                    .map(i -> i.getOpAchievementDetail().get().getWorkTypeCD())
                    .distinct()
                    .collect(Collectors.toList());
            // ??????????????????????????????????????????????????????
            Map<String, WorkType> mapWorkCds = wkTypeRepo.getPossibleWorkType(cid, cds).stream().collect(Collectors.toMap(i -> i.getWorkTypeCode().v(), i -> i));
            businessTripWorkTypes = opActualContentDisplayLst.get().stream().map(i -> new BusinessTripWorkTypes(
                    i.getDate(),
                    i.getOpAchievementDetail().isPresent() ? mapWorkCds.get(i.getOpAchievementDetail().get().getWorkTypeCD()) : null))
                    .collect(Collectors.toList());
        }
        // ?????????????????????OUTPUT????????????????????????????????????????????????????????????
        BusinessTripInfoOutput output = new BusinessTripInfoOutput(
                tripRequestSet.orElse(null),
                appDispInfoStartupOutput,
                Optional.of(holidayWorkType),
                Optional.of(workDays),
                opActualContentDisplayLst,
                Optional.of(businessTripWorkTypes),
                Optional.empty()
        );
        return BusinessTripInfoOutputDto.convertToDto(businessTripService.setInitValueAppWorkTime(output));
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     */
    public List<ConfirmMsgOutput> checkBeforeRegister(CheckBeforeRegisterDto param) {
        String sid = AppContexts.user().employeeId();
        String cid = AppContexts.user().companyId();
        List<ConfirmMsgOutput> confirmMsgOutputs = new ArrayList<>();

        ApplicationDto applicationDto = param.getApplication();

        Application application = Application.createFromNew(
                EnumAdaptor.valueOf(applicationDto.getPrePostAtr(), PrePostAtr.class),
                sid,
                EnumAdaptor.valueOf(applicationDto.getAppType(), ApplicationType.class),
                new ApplicationDate(GeneralDate.fromString(applicationDto.getAppDate(), "yyyy/MM/dd")),
                sid,
                Optional.empty(),
                Optional.empty(),
                applicationDto.getOpAppStartDate() == null ?
                        Optional.empty() : Optional.ofNullable(new ApplicationDate(GeneralDate.fromString(applicationDto.getOpAppStartDate(), "yyyy/MM/dd"))),
                applicationDto.getOpAppEndDate() == null ?
                        Optional.empty() : Optional.ofNullable(new ApplicationDate(GeneralDate.fromString(applicationDto.getOpAppEndDate(), "yyyy/MM/dd"))),
                applicationDto.getOpAppReason() == null ? Optional.empty() : Optional.of(new AppReason(applicationDto.getOpAppReason())),
                applicationDto.getOpAppStandardReasonCD() == null ? Optional.empty() : Optional.of(new AppStandardReasonCode(applicationDto.getOpAppStandardReasonCD())
                ));

        BusinessTripInfoOutput output = param.getBusinessTripInfoOutput().toDomain();
        BusinessTrip businessTrip = param.getBusinessTrip().toDomain(application);

        // ?????????????????????2-1.????????????????????????????????????????????????
        Optional<WorkTimeCode> optWorkTimeCode = businessTrip.getInfos().stream()
                .filter(x -> x.getWorkInformation().getWorkTimeCode() != null)
                .map(x -> x.getWorkInformation().getWorkTimeCode())
                .findFirst();
        confirmMsgOutputs = processBeforeRegister.processBeforeRegister_New(
                AppContexts.user().companyId(),
                EmploymentRootAtr.APPLICATION,
                true,
                application,
                null,
                output.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()),
                Collections.emptyList(),
                output.getAppDispInfoStartup(), 
                businessTrip.getInfos().stream().map(x -> x.getWorkInformation().getWorkTypeCode().v()).collect(Collectors.toList()), 
                Optional.empty(), 
                optWorkTimeCode.isPresent() ? optWorkTimeCode.map(WorkTimeCode::v) : Optional.empty(), 
                false
        );


        if (confirmMsgOutputs.isEmpty()) {
            // ??????????????????????????????????????????????????????????????????????????????
            if (businessTrip.getInfos().isEmpty()) {
                throw new BusinessException("Msg_1703");
            }
            businessTripService.businessTripIndividualCheck(
                    businessTrip.getInfos(),
                    output,
                    param.getScreenDetails()
                            .stream()
                            .collect(Collectors.toMap(
                                    i -> GeneralDate.fromString(i.getDate(), "yyyy/MM/dd"),
                                    i -> new ScreenWorkInfoName(i.getWorkTypeName(), i.getWorkTimeName()))
                            ));
        }
        return confirmMsgOutputs;
    }


    /**
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param businessTripInfoOutputDto ???????????????????????????
     * @param applicationDto
     * @return
     */
    public DetailStartScreenInfoDto updateAppDate(BusinessTripInfoOutputDto businessTripInfoOutputDto, ApplicationDto applicationDto) {
        String cid = AppContexts.user().companyId();
        String loginSid = AppContexts.user().employeeId();
        DetailStartScreenInfoDto result = new DetailStartScreenInfoDto();
        BusinessTripInfoOutput tripRequestInfoOutput = businessTripInfoOutputDto.toDomain();
        AppDispInfoStartupOutput appDispInfoStartupOutput = tripRequestInfoOutput.getAppDispInfoStartup();
        GeneralDate appStartDate = GeneralDate.fromString(applicationDto.getOpAppStartDate(), "yyyy/MM/dd");
        GeneralDate appEndDate = GeneralDate.fromString(applicationDto.getOpAppEndDate(), "yyyy/MM/dd");
        DatePeriod dates = new DatePeriod(appStartDate, appEndDate);
        List<GeneralDate> inputDates = dates.datesBetween();

        if ((ChronoUnit.DAYS.between(appStartDate.localDate(), appEndDate.localDate()) + 1)  > 31){
            throw new BusinessException("Msg_277");
        }

        // ?????????????????????????????????????????????????????????????????????????????????????????????
        // ????????????????????????????????????#Msg_1695????????????({0}????????????)
        Optional<List<ActualContentDisplay>> opActualContentDisplayLst = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst();

        if (opActualContentDisplayLst.isPresent()) {
            Optional<ActualContentDisplay> dateNotHaveContent = opActualContentDisplayLst.get().stream().filter(i -> !i.getOpAchievementDetail().isPresent() || i.getOpAchievementDetail() == null).findFirst();
            if (dateNotHaveContent.isPresent()) {
                throw new BusinessException("Msg_1695", dateNotHaveContent.get().getDate().toString());
            }
        }

        businessTripService.getBusinessTripNotApproved(loginSid, inputDates, opActualContentDisplayLst);

        List<BusinessTripWorkTypes> businessTripWorkTypes = new ArrayList<>();
        if (!opActualContentDisplayLst.get().isEmpty()) {
            List<String> cds = opActualContentDisplayLst.get().stream().filter(i -> i.getOpAchievementDetail().isPresent())
                    .map(i -> i.getOpAchievementDetail().get().getWorkTypeCD())
                    .distinct()
                    .collect(Collectors.toList());
            // ??????????????????????????????????????????????????????
            Map<String, WorkType> mapWorkCds = wkTypeRepo.getPossibleWorkType(cid, cds).stream().collect(Collectors.toMap(i -> i.getWorkTypeCode().v(), i -> i));
            businessTripWorkTypes = opActualContentDisplayLst.get().stream().map(i -> new BusinessTripWorkTypes(
                    i.getDate(),
                    i.getOpAchievementDetail().isPresent() ? mapWorkCds.get(i.getOpAchievementDetail().get().getWorkTypeCD()) : null
            )).collect(Collectors.toList());
        }

        tripRequestInfoOutput.setActualContentDisplay(opActualContentDisplayLst);
        tripRequestInfoOutput.setWorkTypeBeforeChange(Optional.of(businessTripWorkTypes));
        result.setResult(true);
//        result.setBusinessTripInfoOutputDto(BusinessTripInfoOutputDto.convertToDto(tripRequestInfoOutput));
        // ?????????????????????????????????????????????????????????????????????????????????????????????
        result.setBusinessTripInfoOutputDto(BusinessTripInfoOutputDto.convertToDto(businessTripService.setInitValueAppWorkTime(tripRequestInfoOutput)));

        return result;
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????
     * @param changeWorkCodeParam
     * @return
     */
    public BusinessTripInfoOutputDto changeWorkTypeCode(ChangeWorkCodeParam changeWorkCodeParam) {
        BusinessTripInfoOutput output = businessTripService.checkChangeWorkTypeCode(
                GeneralDate.fromString(changeWorkCodeParam.getDate(), "yyyy/MM/dd"),
                changeWorkCodeParam.getBusinessTripInfoOutputDto().toDomain(),
                changeWorkCodeParam.getTypeCode()
        );
        return BusinessTripInfoOutputDto.convertToDto(output);
    }

    /**
     * ????????????????????????????????????
     * @param changeWorkCodeParam
     * @return
     */
    public WorkTypeNameDto changeWorkTimeCode(ChangeWorkCodeParam changeWorkCodeParam) {
        WorkTypeNameDto result = new WorkTypeNameDto();
        String typeCode = changeWorkCodeParam.getTypeCode();
        String timeCode = changeWorkCodeParam.getTimeCode();
        Integer startWorkTime = changeWorkCodeParam.getStartWorkTime();
        Integer endWorkTime = changeWorkCodeParam.getEndWorkTime();


        BusinessTripInfoOutput businessTripInfoOutput = changeWorkCodeParam.getBusinessTripInfoOutputDto().toDomain();
        GeneralDate inputDate = GeneralDate.fromString(changeWorkCodeParam.getDate(), "yyyy/MM/dd");

        ResultCheckInputCode checkRequiredCode = businessTripService.checkRequireWorkTimeCode(typeCode, timeCode, startWorkTime, endWorkTime, true);
        if (!checkRequiredCode.isResult()) {
            if (checkRequiredCode.getMsg().equals("Msg_1912") || checkRequiredCode.getMsg().equals("Msg_1913")) {
                result.setMsg(checkRequiredCode.getMsg());
            } else {
                throw new BusinessException(checkRequiredCode.getMsg(), inputDate.toString());
            }
        }

        // ??????????????????????????????????????????????????????????????????????????????
        if (Strings.isBlank(timeCode)) {
            return result;
        }
        val workTimeSet = businessTripInfoOutput.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpWorkTimeLst();
        if (workTimeSet.isPresent() && !workTimeSet.get().isEmpty()) {
            Optional<WorkTimeSetting> existWorkTimeSet = workTimeSet.get().stream().filter(i -> i.getWorktimeCode().v().equals(timeCode)).findFirst();
            if (existWorkTimeSet.isPresent()) {
                result.setName(existWorkTimeSet.get().getWorkTimeDisplayName().getWorkTimeName().v());
            } else {
                throw new BusinessException("Msg_1685", inputDate.toString());
            }
        }
        return result;
    }

    /**
     * ????????????B?????????????????????
     * @param param
     * @return
     */
    public DetailScreenDto getDetailKAF008(ParamUpdate param) {
        // ?????????????????????????????????????????????????????????????????????????????????????????????
        AppDispInfoStartupOutput appDispInfoStartupOutput =
                appCommonSetService.getCommonSetBeforeDetail(param.getCompanyId(), param.getApplicationId());
        // ???????????????????????????????????????????????????????????????????????????
        DetailScreenB detailScreen = businessTripService.getDataDetail(param.getCompanyId(), param.getApplicationId(), appDispInfoStartupOutput);
        return DetailScreenDto.fromDomain(detailScreen);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     * @param param
     * @return
     */
    public boolean getFlagStartKDL003(ParamStartKDL003 param) {
        GeneralDate selectedDate = GeneralDate.fromString(param.getSelectedDate(), "yyyy/MM/dd");
        BusinessTripInfoOutput infoOutput = param.getBusinessTripInfoOutputDto().toDomain();
        WorkType selectedWorkType = infoOutput.getWorkTypeBeforeChange().get().stream().filter(i -> i.getDate().equals(selectedDate)).findFirst().get().getWorkType();
        return businessTripService.getBusinessTripClsContent(selectedWorkType);
    }

    public BusinessTripOutputDto startKAFS08(AppBusinessParam appBusinessParam) {
        boolean mode = appBusinessParam.getMode();
        String cid = AppContexts.user().companyId();
        ApplicationType appType;
        String employeeID = null;
        //Ki???m tra n???u employeeID null th?? l???y gi?? tr??? l??c login
        if(appBusinessParam.getEmployeeID() != null) {
            employeeID = appBusinessParam.getEmployeeID();
        }
        List<String> applicantlist = new ArrayList<String>();
        List<GeneralDate> dateList = appBusinessParam.getListDates().stream()
                .map(i -> GeneralDate.fromString(i, "yyyy/MM/dd")).collect(Collectors.toList());
        BusinessTripOutputDto result = new BusinessTripOutputDto();
        BusinessTripInfoOutput businessTripInfoOutput = appBusinessParam
                .getBusinessTripInfoOutput() == null ? null : appBusinessParam.getBusinessTripInfoOutput().toDomain();
        BusinessTrip businessTrip = appBusinessParam.getBusinessTrip() == null ? null : appBusinessParam.getBusinessTrip().toDomain(businessTripInfoOutput.getAppDispInfoStartup().getAppDetailScreenInfo().get().getApplication());
        // new mode th?? th???c hi???n thu???t to??n ????????????????????????
        if (mode) {
            AppDispInfoStartupOutput appDispInfoStartupOutput = commonAlgorithm.getAppDispInfoStart(cid,
            		ApplicationType.BUSINESS_TRIP_APPLICATION, Arrays.asList(AppContexts.user().employeeId()),dateList, mode, Optional.ofNullable(null),
                    Optional.ofNullable(null));
            BusinessTripInfoOutputDto businessTripInfoOutputDto = this.businessScreenInit_New(cid, applicantlist,
                    dateList, appDispInfoStartupOutput);
            result.setBusinessTripInfoOutput(businessTripInfoOutputDto);
            // INPUT???????????????????????????????????????????????????????????????
        } else {
            result.setBusinessTrip(BusinessTripDto.fromDomain(businessTrip));
            result.setBusinessTripInfoOutput(BusinessTripInfoOutputDto.convertToDto(businessTripInfoOutput));
        }
        return result;
    }

    /**
     * ???KAFS08_?????????????????????????????????B?????????????????????
     * @param param StartScreenBDto
     * @return
     */
    public DetailScreenDto startScreenB(StartScreenBDto param) {
        String cid = AppContexts.user().companyId();
        AppDispInfoStartupOutput appDispInfoStartupOutput = param.getAppDispInfoStartup().toDomain();
        DetailScreenB detailScreen = businessTripService.getDataDetail(cid, param.getAppId(), appDispInfoStartupOutput);
        return DetailScreenDto.fromDomain(detailScreen);
    }

    /**
     *
     * @param param
     * @return
     */
    public DetailScreenInfo approveTripRequest(ApproveTripRequestParam param) {
        String cid = AppContexts.user().companyId();
        DetailScreenInfo result = new DetailScreenInfo();
        List<GeneralDate> lstDate = param.getListDate().stream().map(i -> GeneralDate.fromString(i,"")).collect(Collectors.toList());
        boolean newMode = param.isNewMode();
        if (newMode) {
            AppDispInfoStartupOutput appDispInfoStartupOutput = commonAlgorithm.getAppDispInfoStart(
                    cid,
                    ApplicationType.BUSINESS_TRIP_APPLICATION,
                    param.getEmployeeIds(),
                    lstDate,
                    true,
                    Optional.empty(),
                    Optional.empty()
            );
            BusinessTripInfoOutputDto businessTripInfoOutput = this.businessScreenInit_New(cid, param.getEmployeeIds(), lstDate, appDispInfoStartupOutput);
            result.setBusinessTripInfoOutput(businessTripInfoOutput);
        } else {
            result.setBusinessTripInfoOutput(param.getBusinessTripInfoOutput());
            result.setBusinessTrip(param.getBusinessTrip());
        }
        return result;
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????
     */
    public List<ConfirmMsgOutput> checkBeforeRegisterMobile(DetailScreenDto param) {
        String sid = AppContexts.user().employeeId();
        String cid = AppContexts.user().companyId();
        List<ConfirmMsgOutput> confirmMsgOutputs = new ArrayList<>();

        ApplicationDto applicationDto = param.getApplication();

        Application application = Application.createFromNew(
                EnumAdaptor.valueOf(applicationDto.getPrePostAtr(), PrePostAtr.class),
                sid,
                EnumAdaptor.valueOf(applicationDto.getAppType(), ApplicationType.class),
                new ApplicationDate(GeneralDate.fromString(applicationDto.getAppDate(), "yyyy/MM/dd")),
                sid,
                Optional.empty(),
                Optional.empty(),
                applicationDto.getOpAppStartDate() == null ?
                        Optional.empty() : Optional.ofNullable(new ApplicationDate(GeneralDate.fromString(applicationDto.getOpAppStartDate(), "yyyy/MM/dd"))),
                applicationDto.getOpAppEndDate() == null ?
                        Optional.empty() : Optional.ofNullable(new ApplicationDate(GeneralDate.fromString(applicationDto.getOpAppEndDate(), "yyyy/MM/dd"))),
                applicationDto.getOpAppReason() == null ? Optional.empty() : Optional.of(new AppReason(applicationDto.getOpAppReason())),
                applicationDto.getOpAppStandardReasonCD() == null ? Optional.empty() : Optional.of(new AppStandardReasonCode(applicationDto.getOpAppStandardReasonCD())
                ));

        BusinessTripInfoOutput output = param.getBusinessTripInfoOutputDto().toDomain();
        BusinessTrip businessTrip = param.getBusinessTripDto().toDomain(application);

        if (param.isMode()) {
            // ?????????????????????2-1.????????????????????????????????????????????????
            Optional<WorkTimeCode> optWorkTimeCode = businessTrip.getInfos().stream()
                    .filter(x -> x.getWorkInformation().getWorkTimeCode() != null)
                    .map(x -> x.getWorkInformation().getWorkTimeCode())
                    .findFirst();
            confirmMsgOutputs = processBeforeRegister.processBeforeRegister_New(
                    cid,
                    EmploymentRootAtr.APPLICATION,
                    true,
                    application,
                    null,
                    output.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()),
                    Collections.emptyList(),
                    output.getAppDispInfoStartup(), 
                    businessTrip.getInfos().stream().map(x -> x.getWorkInformation().getWorkTypeCode().v()).collect(Collectors.toList()), 
                    Optional.empty(), 
                    optWorkTimeCode.isPresent() ? optWorkTimeCode.map(WorkTimeCode::v) : Optional.empty(), 
                            false
                    );
        } else {
         // ?????????????????????4-1.????????????????????????????????????????????????
            businessTrip.getInfos().stream().forEach(i -> {
                this.detailBeforeProcessRegisterService.processBeforeDetailScreenRegistration(
                        cid,
                        output.getAppDispInfoStartup().getAppDetailScreenInfo().get().getApplication().getApplication().getEmployeeID(),
                        i.getDate(),
                        EmploymentRootAtr.APPLICATION.value,
                        output.getAppDispInfoStartup().getAppDetailScreenInfo().get().getApplication().getApplication().getAppID(),
                        output.getAppDispInfoStartup().getAppDetailScreenInfo().get().getApplication().getApplication().getPrePostAtr(),
                        output.getAppDispInfoStartup().getAppDetailScreenInfo().get().getApplication().getApplication().getVersion(),
                        i.getWorkInformation().getWorkTypeCode().v(),
                        i.getWorkInformation().getWorkTimeCode() == null ? null : i.getWorkInformation().getWorkTimeCode().v(),
                        output.getAppDispInfoStartup(), 
                        businessTrip.getInfos().stream().map(x -> x.getWorkInformation().getWorkTypeCode().v()).collect(Collectors.toList()), 
                        Optional.empty(), 
                        false, 
                        Optional.of(i.getWorkInformation().getWorkTypeCode().v()), 
                        i.getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v)
                );
            });
        }


        if (confirmMsgOutputs.isEmpty()) {
            // ??????????????????????????????????????????????????????????????????????????????
            if (businessTrip.getInfos().isEmpty()) {
                throw new BusinessException("Msg_1703");
            }
            businessTripService.businessTripIndividualCheck(
                    businessTrip.getInfos(),
                    output,
                    param.getScreenDetails()
                            .stream()
                            .collect(Collectors.toMap(
                                    i -> GeneralDate.fromString(i.getDate(), "yyyy/MM/dd"),
                                    i -> new ScreenWorkInfoName(i.getWorkTypeName(), i.getWorkTimeName()))
                            ));
        }
        return confirmMsgOutputs;
    }

    /**
     * ??????????????????????????????????????????????????????
     * @param param
     */
    public DetailStartScreenInfoDto mobilePeriodCheck(CheckPeriodDto param) {

        String cid = AppContexts.user().companyId();
        String sid = AppContexts.user().employeeId();
        List<ConfirmMsgOutput> confirmMsgOutputs = new ArrayList<>();
        DetailStartScreenInfoDto result = new DetailStartScreenInfoDto();

        if(param.getIsNewMode()) {

            ApplicationDto applicationDto = param.getApplication();
            Application application = Application.createFromNew(
                    EnumAdaptor.valueOf(applicationDto.getPrePostAtr(), PrePostAtr.class),
                    sid,
                    EnumAdaptor.valueOf(applicationDto.getAppType(), ApplicationType.class),
                    new ApplicationDate(GeneralDate.fromString(applicationDto.getAppDate(), "yyyy/MM/dd")),
                    sid,
                    Optional.empty(),
                    Optional.empty(),
                    applicationDto.getOpAppStartDate() == null ?
                            Optional.empty() : Optional.ofNullable(new ApplicationDate(GeneralDate.fromString(applicationDto.getOpAppStartDate(), "yyyy/MM/dd"))),
                    applicationDto.getOpAppEndDate() == null ?
                            Optional.empty() : Optional.ofNullable(new ApplicationDate(GeneralDate.fromString(applicationDto.getOpAppEndDate(), "yyyy/MM/dd"))),
                    applicationDto.getOpAppReason() == null ? Optional.empty() : Optional.of(new AppReason(applicationDto.getOpAppReason())),
                    applicationDto.getOpAppStandardReasonCD() == null ? Optional.empty() : Optional.of(new AppStandardReasonCode(applicationDto.getOpAppStandardReasonCD())
                    ));
            BusinessTripInfoOutput businessTripInfoOutput = param.getBusinessTripInfoOutput().toDomain();
            List<GeneralDate> dates = new DatePeriod(application.getOpAppStartDate().get().getApplicationDate(), application.getOpAppEndDate().get().getApplicationDate()).datesBetween();

            // ?????????????????????2-1.????????????????????????????????????????????????
            confirmMsgOutputs = processBeforeRegister.processBeforeRegister_New(
                    AppContexts.user().companyId(),
                    EmploymentRootAtr.APPLICATION,
                    true,
                    application,
                    null,
                    businessTripInfoOutput.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()),
                    Collections.emptyList(),
                    businessTripInfoOutput.getAppDispInfoStartup(),
                    new ArrayList<>(), // in new mode, businessTrip = null
                    Optional.empty(),
                    Optional.empty(), // in new mode, businessTrip = null
                    false
            );

            // ?????????????????????????????????????????????????????????????????????
            AppDispInfoWithDateOutput appDispInfoWithDateOutput = commonAlgorithm.changeAppDateProcess(cid, dates,
                    ApplicationType.BUSINESS_TRIP_APPLICATION, businessTripInfoOutput.getAppDispInfoStartup().getAppDispInfoNoDateOutput(),
                    businessTripInfoOutput.getAppDispInfoStartup().getAppDispInfoWithDateOutput(), Optional.empty());

            Optional<List<ActualContentDisplay>> opActualContentDisplayLst = appDispInfoWithDateOutput.getOpActualContentDisplayLst();


            if (opActualContentDisplayLst.isPresent()) {
                // ?????????????????????????????????????????????????????????????????????????????????????????????
                val dateNotHaveContent = opActualContentDisplayLst.get().stream()
                        .filter(i -> !i.getOpAchievementDetail().isPresent() || i.getOpAchievementDetail().get() == null)
                        .findAny();
                if (dateNotHaveContent.isPresent()) {
                    // ????????????????????????????????????#Msg_1695????????????({0}????????????)
                    throw new BusinessException("Msg_1695", dateNotHaveContent.get().getDate().toString());
                }

                businessTripService.getBusinessTripNotApproved(sid, dates, opActualContentDisplayLst);

                List<BusinessTripWorkTypes> businessTripWorkTypes = new ArrayList<>();
                if (!opActualContentDisplayLst.get().isEmpty()) {
                    List<String> cds = opActualContentDisplayLst.get().stream().filter(i -> i.getOpAchievementDetail().isPresent())
                            .map(i -> i.getOpAchievementDetail().get().getWorkTypeCD())
                            .distinct()
                            .collect(Collectors.toList());
                    // ??????????????????????????????????????????????????????
                    Map<String, WorkType> mapWorkCds = wkTypeRepo.getPossibleWorkType(cid, cds).stream().collect(Collectors.toMap(i -> i.getWorkTypeCode().v(), i -> i));
                    businessTripWorkTypes = opActualContentDisplayLst.get().stream().map(i -> new BusinessTripWorkTypes(
                            i.getDate(),
                            i.getOpAchievementDetail().isPresent() ? mapWorkCds.get(i.getOpAchievementDetail().get().getWorkTypeCD()) : null
                    )).collect(Collectors.toList());
                }

                businessTripInfoOutput.setActualContentDisplay(appDispInfoWithDateOutput.getOpActualContentDisplayLst());
                businessTripInfoOutput.setWorkTypeBeforeChange(Optional.of(businessTripWorkTypes));

                result.setResult(true);
                result.setConfirmMsgOutputs(confirmMsgOutputs);
                result.setBusinessTripInfoOutputDto(BusinessTripInfoOutputDto.convertToDto(businessTripService.setInitValueAppWorkTime(businessTripInfoOutput)));
            }
        }
        return result;
    }

}
