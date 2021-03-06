package nts.uk.ctx.at.request.dom.application.timeleaveapplication.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.ChildCareNursePeriodImport;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.care.GetRemainingNumberCareAdapter;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.care.GetRemainingNumberChildCareAdapter;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.care.GetRemainingNumberNursingAdapter;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.childcare.GetRemainingNumberChildCareNurseAdapter;
import nts.uk.ctx.at.request.dom.adapter.record.remainingnumber.holidayover60h.AggrResultOfHolidayOver60hImport;
import nts.uk.ctx.at.request.dom.adapter.record.remainingnumber.holidayover60h.GetHolidayOver60hRemNumWithinPeriodAdapter;
import nts.uk.ctx.at.request.dom.adapter.record.remainingnumber.specialholiday.GetSpecialRemainingWithinPeriodAdapter;
import nts.uk.ctx.at.request.dom.adapter.record.remainingnumber.specialholiday.TotalResultOfSpecialLeaveImport;
import nts.uk.ctx.at.request.dom.application.ApplicationDate;
import nts.uk.ctx.at.request.dom.application.appabsence.apptimedigest.TimeDigestApplication;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualholidaymanagement.AnnualHolidayManagementAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualholidaymanagement.NextAnnualLeaveGrantImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.AnnLeaveRemainNumberAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.ReNumAnnLeaveImport;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeDigestAppType;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplication;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationDetail;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.output.ChildNursingManagement;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.output.SupHolidayManagement;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.output.TimeAllowanceManagement;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.output.TimeAnnualLeaveManagement;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.output.TimeLeaveApplicationOutput;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.output.TimeSpecialLeaveManagement;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.output.TimeSpecialVacationRemaining;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.output.TimeVacationManagementOutput;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.output.TimeVacationRemainingOutput;
import nts.uk.ctx.at.request.dom.workrecord.remainmanagement.InterimRemainDataMngCheckRegisterRequest;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSidPeriodDateEmploymentImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.AppRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ApplicationType;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.PrePostAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.AnnLeaEmpBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.procwithbasedate.NumberConsecutiveVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectCondition;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveApplicationReflect;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTblRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.SettingDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.TimeDigestiveUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.acquisitionrule.AcquisitionRule;
import nts.uk.ctx.at.shared.dom.vacation.setting.acquisitionrule.AcquisitionRuleRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcess;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AnnualHolidaySetOutput;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.SubstitutionHolidayOutput;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.sixtyhours.Com60HourVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.sixtyhours.Com60HourVacationRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.vacation.specialvacation.timespecialvacation.TimeSpecialLeaveManagementSetting;
import nts.uk.ctx.at.shared.dom.workrule.vacation.specialvacation.timespecialvacation.TimeSpecialLeaveMngSetRepository;
import nts.uk.ctx.at.shared.dom.worktype.DeprecateClassification;
import nts.uk.ctx.at.shared.dom.worktype.specialholidayframe.SpecialHolidayFrame;
import nts.uk.ctx.at.shared.dom.worktype.specialholidayframe.SpecialHolidayFrameRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class TimeLeaveApplicationServiceImpl implements TimeLeaveApplicationService {
    @Inject
    private TimeLeaveAppReflectRepository timeLeaveAppReflectRepo;

    @Inject
    private RemainNumberTempRequireService requireService;

    @Inject
    private Com60HourVacationRepository com60HourVacationRepo;

    @Inject
    private NursingLeaveSettingRepository nursingLeaveSettingRepo;

    @Inject
    private TimeSpecialLeaveMngSetRepository timeSpecialLeaveMngSetRepo;

    @Inject
    private SpecialHolidayFrameRepository specialHolidayFrameRepo;

    @Inject
    private AnnLeaveRemainNumberAdapter leaveAdapter;

    @Inject
    private AcquisitionRuleRepository acquisitionRuleRepository;

    @Inject
    private OtherCommonAlgorithm otherCommonAlgorithm;

    @Inject
    private InterimRemainDataMngCheckRegisterRequest interimRemainDataMngCheckRegister;

    @Inject
    private SpecialHolidayRepository specialHolidayRepository;

    @Inject
    private CommonAlgorithm commonAlgorithm;

    @Inject
    private SpecialLeaveGrantRepository specialLeaveGrantRepo;

    @Inject
    private ShareEmploymentAdapter shareEmploymentAdapter;

    @Inject
    private EmpEmployeeAdapter empEmployeeAdapter;

    @Inject
    private GrantDateTblRepository grantDateTblRepo;

    @Inject
    private AnnLeaEmpBasicInfoRepository annLeaEmpBasicInfoRepo;

    @Inject
    private InterimSpecialHolidayMngRepository interimSpecialHolidayMngRepo;

    @Inject
    private SpecialLeaveBasicInfoRepository specialLeaveBasicInfoRepo;

    @Inject
    private GetHolidayOver60hRemNumWithinPeriodAdapter getHolidayOver60hRemNumWithinPeriodAdapter;

    @Inject
    private GetRemainingNumberChildCareNurseAdapter getRemainingNumberChildCareNurseAdapter;
    
    @Inject
    private GetRemainingNumberChildCareAdapter getRemainingNumberChildCareAdapter;
    
    @Inject
    private GetRemainingNumberNursingAdapter getRemainingNumberNursingAdapter;

    @Inject
    private GetRemainingNumberCareAdapter getRemainingNumberCareAdapter;

    @Inject
    private WorkingConditionRepository workingConditionRepo;

    @Inject
    private GetSpecialRemainingWithinPeriodAdapter getSpecialRemainingWithinPeriodAdapter;

    @Inject
    private AnnualHolidayManagementAdapter holidayAdapter;

    @Inject
    private ComDayOffManaDataRepository comDayOffManaDataRepo;

    @Inject
    private LeaveComDayOffManaRepository leaveComDayOffManaRepo;

    @Inject
    private LeaveManaDataRepository leaveManaDataRepo;

    @Inject
    private CompensLeaveEmSetRepository compensLeaveEmSetRepo;

    @Inject
    private CompensLeaveComSetRepository compensLeaveComSetRepo;

    @Inject
    private InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;

    @Inject
    private ClosureEmploymentRepository closureEmploymentRepo;

    @Inject
    private ClosureRepository closureRepo;

    @Inject
    private PayoutSubofHDManaRepository payoutHdManaRepo;

    @Override
    public TimeLeaveApplicationReflect getTimeLeaveAppReflectSetting(String companyId) {
        TimeLeaveApplicationReflect reflectSetting = timeLeaveAppReflectRepo.findByCompany(companyId).orElse(null);
        if (reflectSetting == null
                || (reflectSetting.getCondition().getSuperHoliday60H() == NotUseAtr.NOT_USE
                && reflectSetting.getCondition().getAnnualVacationTime() == NotUseAtr.NOT_USE
                && reflectSetting.getCondition().getChildNursing() == NotUseAtr.NOT_USE
                && reflectSetting.getCondition().getNursing() == NotUseAtr.NOT_USE
                && reflectSetting.getCondition().getSpecialVacationTime() == NotUseAtr.NOT_USE
                && reflectSetting.getCondition().getSubstituteLeaveTime() == NotUseAtr.NOT_USE)
                || (reflectSetting.getDestination().getFirstBeforeWork() == NotUseAtr.NOT_USE
                && reflectSetting.getDestination().getFirstAfterWork() == NotUseAtr.NOT_USE
                && reflectSetting.getDestination().getSecondBeforeWork() == NotUseAtr.NOT_USE
                && reflectSetting.getDestination().getSecondAfterWork() == NotUseAtr.NOT_USE
                && reflectSetting.getDestination().getPrivateGoingOut() == NotUseAtr.NOT_USE
                && reflectSetting.getDestination().getUnionGoingOut() == NotUseAtr.NOT_USE)
                ) {
            throw new BusinessException("Msg_474");
        }
        return reflectSetting;
    }

    /**
     * ??????????????????????????????????????????
     * @param companyId
     * @param employeeId
     * @param baseDate
     * @param condition
     * @return
     */
    @Override
    public TimeVacationManagementOutput getTimeLeaveManagement(String companyId, String employeeId, GeneralDate baseDate, TimeLeaveAppReflectCondition condition) {
        RemainNumberTempRequireService.Require require = requireService.createRequire();
        CacheCarrier cache = new CacheCarrier();

        // 10-1.??????????????????????????????
        AnnualHolidaySetOutput annualHolidaySetOutput = AbsenceTenProcess.getSettingForAnnualHoliday(require, companyId);
        TimeAnnualLeaveManagement timeAnnualLeaveMng = new TimeAnnualLeaveManagement(
                EnumAdaptor.valueOf(annualHolidaySetOutput.getTimeYearRest(), TimeDigestiveUnit.class),
                annualHolidaySetOutput.isSuspensionTimeYearFlg()
        );

        // 10-2.??????????????????????????????
        SubstitutionHolidayOutput substitutionHoliday =  AbsenceTenProcess.getSettingForSubstituteHoliday(require, cache, companyId, employeeId, baseDate);
        TimeAllowanceManagement timeSubstituteLeaveMng = new TimeAllowanceManagement(
                EnumAdaptor.valueOf(substitutionHoliday.getDigestiveUnit(), TimeDigestiveUnit.class),
                substitutionHoliday.isTimeOfPeriodFlg()
        );

        // ????????????????????????60H????????????????????????????????????
        Com60HourVacation com60HourVacation = com60HourVacationRepo.findById(companyId).orElse(null);
        SupHolidayManagement super60HLeaveMng = new SupHolidayManagement(
                com60HourVacation == null ? null : com60HourVacation.getSetting().getDigestiveUnit(),
                com60HourVacation != null && com60HourVacation.isManaged()
        );

        // ??????????????????????????????????????????????????????????????????
        List<NursingLeaveSetting> nursingLeaveSettingList = nursingLeaveSettingRepo.findByCompanyId(companyId);
        NursingLeaveSetting careNursingLeaveSetting = nursingLeaveSettingList.stream().filter(i -> i.getNursingCategory() == NursingCategory.Nursing).findFirst().orElse(null);
        NursingLeaveSetting childCareNursingLeaveSetting = nursingLeaveSettingList.stream().filter(i -> i.getNursingCategory() == NursingCategory.ChildNursing).findFirst().orElse(null);
        ChildNursingManagement nursingLeaveMng = new ChildNursingManagement(
                careNursingLeaveSetting == null ? null : careNursingLeaveSetting.getTimeCareNursingSetting().getTimeDigestiveUnit(),
                careNursingLeaveSetting != null && careNursingLeaveSetting.isManaged() && careNursingLeaveSetting.getTimeCareNursingSetting().getManageDistinct() == ManageDistinct.YES,
                childCareNursingLeaveSetting == null ? null : childCareNursingLeaveSetting.getTimeCareNursingSetting().getTimeDigestiveUnit(),
                childCareNursingLeaveSetting != null && childCareNursingLeaveSetting.isManaged() && childCareNursingLeaveSetting.getTimeCareNursingSetting().getManageDistinct() == ManageDistinct.YES
        );

        // ???????????????????????????????????????????????????????????????????????????
        TimeSpecialLeaveManagementSetting timeSpecialLeaveManagementSetting = timeSpecialLeaveMngSetRepo.findByCompany(companyId).orElse(null);
        TimeSpecialLeaveManagement timeSpecialLeaveMng = new TimeSpecialLeaveManagement(
                timeSpecialLeaveManagementSetting == null ? null : timeSpecialLeaveManagementSetting.getTimeDigestiveUnit(),
                timeSpecialLeaveManagementSetting != null && timeSpecialLeaveManagementSetting.getManageType() == ManageDistinct.YES,
                new ArrayList<>()
        );
        if (timeSpecialLeaveMng.isTimeSpecialLeaveManagement()) {
            // ?????????????????????????????????????????????????????????
            List<SpecialHolidayFrame> listSpecialFrame = specialHolidayFrameRepo.findDataDisplay(companyId, 1, 1)
                    .stream()
                    .filter(i -> i.getDeprecateSpecialHd() == DeprecateClassification.Deprecated && i.getTimeMngAtr() == NotUseAtr.USE)
                    .sorted(Comparator.comparing(SpecialHolidayFrame::getSpecialHdFrameNo))
                    .collect(Collectors.toList());
            timeSpecialLeaveMng.getListSpecialFrame().addAll(listSpecialFrame);
        }

        if (!(condition.getAnnualVacationTime() == NotUseAtr.USE && timeAnnualLeaveMng.isTimeAnnualManagement())
                && !(condition.getSubstituteLeaveTime() == NotUseAtr.USE && timeSubstituteLeaveMng.isTimeBaseManagementClass())
                && !(condition.getChildNursing() == NotUseAtr.USE && nursingLeaveMng.isTimeChildManagementClass())
                && !(condition.getNursing() == NotUseAtr.USE && nursingLeaveMng.isTimeManagementClass())
                && !(condition.getSuperHoliday60H() == NotUseAtr.USE && super60HLeaveMng.isOverrest60HManagement())
                && !(condition.getSpecialVacationTime() == NotUseAtr.USE && timeSpecialLeaveMng.isTimeSpecialLeaveManagement())) {
            throw new BusinessException("Msg_474");
        }

        return  new TimeVacationManagementOutput(
                super60HLeaveMng,
                nursingLeaveMng,
                timeSubstituteLeaveMng,
                timeAnnualLeaveMng,
                timeSpecialLeaveMng
        );
    }

    /**
     * ???????????????????????????????????????
     * @param companyId
     * @param employeeId
     * @param baseDate
     * @param timeLeaveManagement
     * @return
     */
    @Override
    public TimeVacationRemainingOutput getTimeLeaveRemaining(String companyId, String employeeId, GeneralDate baseDate, TimeVacationManagementOutput timeLeaveManagement) {
        RemainNumberTempRequireService.Require require = requireService.createRequire();
        CacheCarrier cache = new CacheCarrier();

        TimeVacationRemainingOutput timeLeaveRemaining = new TimeVacationRemainingOutput();
        timeLeaveRemaining.setRemainingPeriod(DatePeriod.oneDay(baseDate));

        // INPUT?????????????????????????????????????????????
        if (!timeLeaveManagement.getTimeAnnualLeaveManagement().isTimeAnnualManagement()
                && !timeLeaveManagement.getTimeAllowanceManagement().isTimeBaseManagementClass()
                && !timeLeaveManagement.getSupHolidayManagement().isOverrest60HManagement()
                && !timeLeaveManagement.getChildNursingManagement().isTimeChildManagementClass()
                && !timeLeaveManagement.getChildNursingManagement().isTimeManagementClass()) {
            return timeLeaveRemaining;
        }

        // ????????????????????????????????????????????????
        DatePeriod closingPeriod = ClosureService.findClosurePeriod(require, cache, employeeId, baseDate);

        if (timeLeaveManagement.getTimeAnnualLeaveManagement().isTimeAnnualManagement()) {
            // ?????????????????????????????????????????????
            ReNumAnnLeaveImport reNumAnnLeave = leaveAdapter.getReferDateAnnualLeaveRemain(employeeId, baseDate);
            timeLeaveRemaining.setAnnualTimeLeaveRemainingDays(reNumAnnLeave.getRemainingDays());
            timeLeaveRemaining.setAnnualTimeLeaveRemainingTime(reNumAnnLeave.getRemainingTime());
            // [No.210]????????????????????????????????????
            List<NextAnnualLeaveGrantImport> nextGrantHolidays = holidayAdapter.acquireNextHolidayGrantDate(companyId, employeeId, baseDate);

            Optional<NextAnnualLeaveGrantImport> closestFuture = nextGrantHolidays.stream()
                    .filter(holiday -> holiday.grantDate.after(GeneralDate.today()))
                    .min(Comparator.comparing(h -> h.grantDate));

            timeLeaveRemaining.setGrantDate(closestFuture.map(NextAnnualLeaveGrantImport::getGrantDate));
            timeLeaveRemaining.setGrantedDays(closestFuture.map(holiday -> holiday.grantDays));
        }

        if (timeLeaveManagement.getTimeAllowanceManagement().isTimeBaseManagementClass()) {
            // ?????????????????????????????????????????????
//            BreakDayOffRemainMngRefactParam inputParam = new BreakDayOffRemainMngRefactParam(
//    				companyId,
//    				employeeId,
//    				new DatePeriod(closingPeriod.start(), closingPeriod.start().addYears(1).addDays(-1)),
//    				false,
//    				baseDate,
//    				false,
//    				Collections.emptyList(),
//    				Optional.empty(),
//    				Optional.empty(),
//    				Collections.emptyList(),
//    				Collections.emptyList(),
//    				Optional.empty(),
//    				new FixedManagementDataMonth(Collections.emptyList(), Collections.emptyList()));
//    		SubstituteHolidayAggrResult dataCheck = NumberRemainVacationLeaveRangeQuery
//    				.getBreakDayOffMngInPeriod(require, inputParam);
//            timeLeaveRemaining.setSubTimeLeaveRemainingTime(dataCheck.getRemainTime().v());
            // [No.505]???????????????????????????
            BreakDayOffMngInPeriodQuery.RequireM11 requireM11 = new RequireM11Imp(comDayOffManaDataRepo, leaveComDayOffManaRepo, leaveManaDataRepo, shareEmploymentAdapter, compensLeaveEmSetRepo, compensLeaveComSetRepo, interimBreakDayOffMngRepo, closureEmploymentRepo, closureRepo, payoutHdManaRepo);
            NumberConsecutiveVacation breakDay =  BreakDayOffMngInPeriodQuery.getBreakDayOffMngRemain(requireM11, cache, employeeId, baseDate);
            timeLeaveRemaining.setSubTimeLeaveRemainingTime(breakDay.getRemainTime().v());
        }

        if (timeLeaveManagement.getSupHolidayManagement().isOverrest60HManagement()) {
            // [RQ677]????????????60H???????????????????????????
            AggrResultOfHolidayOver60hImport aggrResultOfHolidayOver60h = this.getHolidayOver60hRemNumWithinPeriodAdapter.algorithm(
                    companyId,
                    employeeId,
                    new DatePeriod(closingPeriod.start(), closingPeriod.start().addYears(1).addDays(-1)),
                    InterimRemainMngMode.OTHER,
                    baseDate,
                    Optional.of(false),
                    Optional.empty(),
                    Optional.empty()
            );
            if (aggrResultOfHolidayOver60h != null
                    && aggrResultOfHolidayOver60h.getAsOfPeriodEnd()!= null
                    && aggrResultOfHolidayOver60h.getAsOfPeriodEnd().getRemainingNumber()!= null
                    && aggrResultOfHolidayOver60h.getAsOfPeriodEnd().getRemainingNumber().getRemainingTimeWithMinus()!= null)
                timeLeaveRemaining.setSuper60HRemainingTime(aggrResultOfHolidayOver60h.getAsOfPeriodEnd().getRemainingNumber().getRemainingTimeWithMinus().v());
        }

        if (timeLeaveManagement.getChildNursingManagement().isTimeChildManagementClass()) {
            // [NO.206]?????????????????????????????????????????????
//            ChildCareNursePeriodImport resultOfChildCareNurse = this.getRemainingNumberChildCareNurseAdapter.getChildCareNurseRemNumWithinPeriod(
//                    employeeId,
//                    new DatePeriod(closingPeriod.start(), closingPeriod.start().addYears(1).addDays(-1)),
//                    InterimRemainMngMode.OTHER,
//                    baseDate,
//                    Optional.of(false),
//                    Optional.empty(),
//                    Optional.empty(),
//                    Optional.empty(),
//                    Optional.empty()
//            );
            // ???????????????????????????????????????????????????
            ChildCareNursePeriodImport resultOfChildCareNurse = getRemainingNumberChildCareAdapter
                    .getRemainingNumberChildCare(companyId, employeeId, GeneralDate.today());
            
            timeLeaveRemaining.setChildCareRemainingDays(resultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getUsedDays());
            resultOfChildCareNurse.getStartdateDays().getThisYear().getRemainingNumber().getUsedTime().ifPresent(i -> {
                timeLeaveRemaining.setChildCareRemainingTime(i);
            });
        }

        if (timeLeaveManagement.getChildNursingManagement().isTimeManagementClass()) {
            // [NO.207]???????????????????????????????????????
//            ChildCareNursePeriodImport childCareNursePeriodImport = getRemainingNumberCareAdapter.getCareRemNumWithinPeriod(
//            		companyId,
//            		employeeId,
//                    new DatePeriod(closingPeriod.start(), closingPeriod.start().addYears(1).addDays(-1)),
//                    InterimRemainMngMode.OTHER,
//                    baseDate,
//                    Optional.of(false),
//                    new ArrayList<>(),
//                    Optional.empty(),
//                    Optional.empty(),
//                    Optional.empty()
//            );
            // ?????????????????????????????????????????????
            ChildCareNursePeriodImport childCareNursePeriodImport = getRemainingNumberNursingAdapter
                    .getRemainingNumberNursing(companyId, employeeId, GeneralDate.today());
            
            timeLeaveRemaining.setCareRemainingDays(childCareNursePeriodImport.getStartdateDays().getThisYear().getRemainingNumber().getUsedDays());
            childCareNursePeriodImport.getStartdateDays().getThisYear().getRemainingNumber().getUsedTime().ifPresent(i -> {
                timeLeaveRemaining.setCareRemainingTime(i);
            });
        }

        timeLeaveRemaining.setRemainingPeriod(new DatePeriod(closingPeriod.start(), closingPeriod.start().addYears(1).addDays(-1)));
        return timeLeaveRemaining;
    }

    /**
     * ???????????????????????????????????????
     * @param companyId
     * @param specialFrameNo
     * @param timeLeaveAppOutput
     * @return
     */
    @Override
    public TimeLeaveApplicationOutput getSpecialLeaveRemainingInfo(String companyId, Optional<Integer> specialFrameNo, TimeLeaveApplicationOutput timeLeaveAppOutput) {
        timeLeaveAppOutput.getTimeVacationRemaining().getSpecialTimeFrames().clear();
        if (specialFrameNo.isPresent()) {
            // ??????????????????????????????????????????????????????
            List<SpecialHoliday> specialHolidayList = specialHolidayRepository.findByCompanyIdWithTargetItem(companyId);
            Optional<SpecialHoliday> specialHoliday = specialHolidayList.stream().filter(i -> i.getTargetItem().getFrameNo().contains(specialFrameNo.get())).findFirst();
            if (!specialHoliday.isPresent()) {
                return timeLeaveAppOutput;
            }

            // [NO.273]???????????????????????????????????????
            TotalResultOfSpecialLeaveImport result = getSpecialRemainingWithinPeriodAdapter.algorithm(
                    companyId,
                    timeLeaveAppOutput.getAppDispInfoStartup().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(),
                    timeLeaveAppOutput.getTimeVacationRemaining().getRemainingPeriod(),
                    false,
                    timeLeaveAppOutput.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getBaseDate(),
                    specialHoliday.get().getSpecialHolidayCode().v(),
                    Optional.of(false),
                    Optional.empty(),
                    Optional.empty()
            );

            if (result.getAtEndPeriodInfo() != null && result.getAtEndPeriodInfo().isPresent()) {
                timeLeaveAppOutput.getTimeVacationRemaining().getSpecialTimeFrames().add(
                        new TimeSpecialVacationRemaining(
                                result.getAtEndPeriodInfo().get().getRemainingInfo().getSpecialLeaveWithMinus().getRemaining().getTotal().getDays(),
                                result.getAtEndPeriodInfo().get().getRemainingInfo().getSpecialLeaveWithMinus().getRemaining().getTotal().getTime().orElse(0),
                                specialFrameNo.get()
                        )
                );
            }
        }
        return timeLeaveAppOutput;
    }

    /**
     * ???????????????????????????????????????
     */
    @Override
    public void checkBeforeRegister(String companyId, TimeDigestAppType timeLeaveType, TimeLeaveApplication timeLeaveApplication, TimeLeaveApplicationOutput output) {
        // ???????????????????????????
        if (timeLeaveApplication.getLeaveApplicationDetails().isEmpty())
            throw new BusinessException("Msg_1654");

        //????????????????????????????????????
        if (timeLeaveApplication.getLeaveApplicationDetails()
                .stream()
                .anyMatch(detail -> detail.getTimeDigestApplication().getTimeSpecialVacation().v() > 0
                        && !detail.getTimeDigestApplication().getSpecialVacationFrameNO().isPresent())) {
            throw new BusinessException("Msg_1983");
        }

        //????????????????????????????????????
        timeLeavePriorityCheck(companyId, timeLeaveType, timeLeaveApplication, output.getTimeVacationRemaining(), output.getTimeVacationManagement());

        //??????????????????????????????
//        remainingTimeVacationCheck(companyId, timeLeaveType, timeLeaveApplication);

        //???????????????????????????????????????
        Optional<TimeDigestiveUnit> super60HDigestion = Optional.ofNullable(output.getTimeVacationManagement().getSupHolidayManagement().getSuper60HDigestion());
        Optional<TimeDigestiveUnit> timeBaseRestingUnit = Optional.ofNullable(output.getTimeVacationManagement().getTimeAllowanceManagement().getTimeBaseRestingUnit());
        Optional<TimeDigestiveUnit> timeAnnualLeaveUnit = Optional.ofNullable(output.getTimeVacationManagement().getTimeAnnualLeaveManagement().getTimeAnnualLeaveUnit());
        Optional<TimeDigestiveUnit> timeChildNursing = Optional.ofNullable(output.getTimeVacationManagement().getChildNursingManagement().getTimeChildDigestiveUnit());
        Optional<TimeDigestiveUnit> timeNursing = Optional.ofNullable(output.getTimeVacationManagement().getChildNursingManagement().getTimeDigestiveUnit());
        Optional<TimeDigestiveUnit> pendingUnit = Optional.ofNullable(output.getTimeVacationManagement().getTimeSpecialLeaveMng().getTimeSpecialLeaveUnit());
        timeLeaveApplication.getLeaveApplicationDetails().forEach(x -> {
            commonAlgorithm.vacationDigestionUnitCheck(x.getTimeDigestApplication(),
                super60HDigestion,
                timeBaseRestingUnit,
                timeAnnualLeaveUnit,
                timeChildNursing,
                timeNursing,
                pendingUnit
            );
        });

        // ????????????????????????????????????
        Optional<WorkingCondition> workingCondition = workingConditionRepo.getBySidAndStandardDate(
                companyId,
                timeLeaveApplication.getEmployeeID(),
                timeLeaveApplication.getAppDate().getApplicationDate()
        );
        if (!workingCondition.isPresent())
            throw new BusinessException("Msg_430");
        Optional<WorkingConditionItem> workingConditionItem = workingConditionRepo.getWorkingConditionItem(workingCondition.get().getDateHistoryItem().get(0).identifier());
        if (!workingConditionItem.isPresent())
            throw new BusinessException("Msg_430");
        //???????????????????????????
        checkContractTime(timeLeaveApplication, workingConditionItem.get());
    }

    /**
     * ????????????????????????????????????
     */
    private void timeLeavePriorityCheck(String companyId, TimeDigestAppType timeDigestAppType, TimeLeaveApplication timeLeaveApplication,
                                        TimeVacationRemainingOutput timeVacationRemainingOutput,
                                        TimeVacationManagementOutput timeVacationManagementOutput) {
        if (timeDigestAppType == TimeDigestAppType.TIME_ANNUAL_LEAVE
                || (timeDigestAppType == TimeDigestAppType.USE_COMBINATION && timeLeaveApplication.getLeaveApplicationDetails().stream().anyMatch(detail -> detail.getTimeDigestApplication().getTimeAnnualLeave().v() > 0))) {

            //??????????????????????????????????????????????????????????????????
            Optional<AcquisitionRule> acquisitionRule = acquisitionRuleRepository.findById(companyId);

            //?????????????????????????????????????????????

            //???????????????????????????
            boolean timeBaseManagementClass = timeVacationManagementOutput.getTimeAllowanceManagement().isTimeBaseManagementClass();
            //60H???????????????????????????
            boolean overrest60HManagement = timeVacationManagementOutput.getSupHolidayManagement().isOverrest60HManagement();
            //??????????????????
            List<TimeDigestApplication> timeDigestApplications = timeLeaveApplication.getLeaveApplicationDetails().stream().map(TimeLeaveApplicationDetail::getTimeDigestApplication).collect(Collectors.toList());

            Integer totalTimeAnnualLeave = timeDigestApplications.stream().reduce(0, (a, b) -> a + b.getTimeAnnualLeave().v(), Integer::sum);
            Integer totalSixtyOvertime = timeDigestApplications.stream().reduce(0, (a, b) -> a + b.getOvertime60H().v(), Integer::sum);
            Integer totalTimeOff = timeDigestApplications.stream().reduce(0, (a, b) -> a + b.getTimeOff().v(), Integer::sum);

            //???@?????????????????????????????????????????????????????????
            if (acquisitionRule.isPresent() && acquisitionRule.get().getCategory() == SettingDistinct.YES) {
                //???@?????????????????????????????????60H???????????????????????????INPUT???60H?????????????????????????????????????????????
                if (acquisitionRule.get().getAnnualHoliday().isSixtyHoursOverrideHoliday() && overrest60HManagement) {
                    //60H???????????????
                    int super60HRemainingTime = timeVacationRemainingOutput.getSuper60HRemainingTime();

                    //INPUT???????????????????????????INPUT???60H??????????????????????????????
                    if (totalTimeAnnualLeave > 0 && super60HRemainingTime > 0 && (super60HRemainingTime - totalSixtyOvertime) > 0) {
                        throw new BusinessException("Msg_1687", "Com_ExsessHoliday", "Com_PaidHoliday", "Com_ExsessHoliday");
                    }
                } else {
                    //???@????????????????????????????????????????????????????????????INPUT????????????????????????????????????????????????
                    if (acquisitionRule.get().getAnnualHoliday().isPriorityPause() && timeBaseManagementClass) {
                        //??????????????????
                        int timeOfTimeOff = timeVacationRemainingOutput.getSubTimeLeaveRemainingTime();

                        //INPUT???????????????????????????INPUT???????????????????????????????????????
                        if (totalTimeAnnualLeave > 0 && timeOfTimeOff > 0 && (timeOfTimeOff - totalTimeOff) > 0) {
                            throw new BusinessException("Msg_1687", "Com_CompensationHoliday", "Com_PaidHoliday", "Com_CompensationHoliday");
                        }
                    }
                }
            }
        }
    }

    /**
     * ??????????????????????????????
     */
    /**
    private void remainingTimeVacationCheck(String companyId, TimeDigestAppType timeDigestAppType, TimeLeaveApplication timeLeaveApplication) {
        //4.???????????????????????????????????????
        PeriodCurrentMonth periodCurrentMonth =
            otherCommonAlgorithm.employeePeriodCurrentMonthCalculate(companyId, timeLeaveApplication.getEmployeeID(), GeneralDate.today());
        //??????????????????????????????
        boolean chkSixtyHOvertime = timeLeaveApplication.getLeaveApplicationDetails().stream().filter(x -> x.getTimeDigestApplication().getOvertime60H().v() > 0).collect(Collectors.toList()).size() > 0;
        boolean chkHoursOfHoliday = timeLeaveApplication.getLeaveApplicationDetails().stream().filter(x -> x.getTimeDigestApplication().getTimeAnnualLeave().v() > 0).collect(Collectors.toList()).size() > 0;
        boolean chkHoursOfSubHoliday = timeLeaveApplication.getLeaveApplicationDetails().stream().filter(x -> x.getTimeDigestApplication().getTimeOff().v() > 0).collect(Collectors.toList()).size() > 0;
        boolean chkNursing = timeLeaveApplication.getLeaveApplicationDetails().stream().anyMatch(x -> x.getTimeDigestApplication().getNursingTime().v() > 0);
        boolean chkChild = timeLeaveApplication.getLeaveApplicationDetails().stream().anyMatch(x -> x.getTimeDigestApplication().getChildTime().v() > 0);

        InterimRemainCheckInputParam inputParam = new InterimRemainCheckInputParam(
                companyId,
                timeLeaveApplication.getEmployeeID(),
                new DatePeriod(periodCurrentMonth.getStartDate(), periodCurrentMonth.getStartDate().addYears(1).addDays(-1)),
                false,
                timeLeaveApplication.getAppDate().getApplicationDate(),
                new DatePeriod(
                        timeLeaveApplication.getOpAppStartDate().map(ApplicationDate::getApplicationDate).orElse(timeLeaveApplication.getAppDate().getApplicationDate()),
                        timeLeaveApplication.getOpAppEndDate().map(ApplicationDate::getApplicationDate).orElse(timeLeaveApplication.getAppDate().getApplicationDate())
                ),
                true,
                Collections.emptyList(),
                Collections.emptyList(),
                getAppData(timeLeaveApplication),
                timeDigestAppType == TimeDigestAppType.USE_COMBINATION ? chkHoursOfSubHoliday : timeDigestAppType == TimeDigestAppType.TIME_OFF,
                true,
                timeDigestAppType == TimeDigestAppType.USE_COMBINATION ? chkHoursOfHoliday : timeDigestAppType == TimeDigestAppType.TIME_ANNUAL_LEAVE,
                false,
                false,
                false,
                timeDigestAppType == TimeDigestAppType.USE_COMBINATION ? chkSixtyHOvertime : timeDigestAppType == TimeDigestAppType.SIXTY_H_OVERTIME,
                timeDigestAppType == TimeDigestAppType.USE_COMBINATION ? chkChild: timeDigestAppType == TimeDigestAppType.CHILD_NURSING_TIME,
                timeDigestAppType == TimeDigestAppType.USE_COMBINATION ? chkNursing : timeDigestAppType == TimeDigestAppType.NURSING_TIME

        );
        EarchInterimRemainCheck remainCheck = interimRemainDataMngCheckRegister.checkRegister(inputParam);

        if (remainCheck.isChkSubHoliday()
                || remainCheck.isChkPause()
                || remainCheck.isChkAnnual()
                || remainCheck.isChkFundingAnnual()
                || remainCheck.isChkSpecial()
                || remainCheck.isChkPublicHoliday()
                || remainCheck.isChkSuperBreak()) {
            throw new BusinessException("Msg_1409", timeDigestAppType.name);
        }
    }
    */

    /**
     * ???????????????????????????
     */
    private void checkContractTime(TimeLeaveApplication timeLeaveApplication, WorkingConditionItem workingConditionItem) {
        //1???????????????????????????????????????
        Integer totalAnnualHoliday = 0;
        for (TimeLeaveApplicationDetail detail : timeLeaveApplication.getLeaveApplicationDetails()) {
            totalAnnualHoliday += detail.getTimeDigestApplication().getTimeOff().v();
            totalAnnualHoliday += detail.getTimeDigestApplication().getTimeAnnualLeave().v();
            totalAnnualHoliday += detail.getTimeDigestApplication().getChildTime().v();
            totalAnnualHoliday += detail.getTimeDigestApplication().getNursingTime().v();
            totalAnnualHoliday += detail.getTimeDigestApplication().getOvertime60H().v();
            totalAnnualHoliday += detail.getTimeDigestApplication().getTimeSpecialVacation().v();
        }
        if (totalAnnualHoliday > workingConditionItem.getContractTime().v()) {
            throw new BusinessException("Msg_1706");
        }
    }

    private List<AppRemainCreateInfor> getAppData(TimeLeaveApplication application) {
        List<AppRemainCreateInfor> apps = application.getLeaveApplicationDetails().stream().map(detail -> {
            List<GeneralDate> listAppDates = new ArrayList<>();
//            if (application.getOpAppStartDate().isPresent() && application.getOpAppEndDate().isPresent()) {
//                listAppDates.addAll(new DatePeriod(application.getOpAppStartDate().get().getApplicationDate(), application.getOpAppEndDate().get().getApplicationDate()).datesBetween());
//            } else {
//                listAppDates.add(application.getAppDate().getApplicationDate());
//            }
            return new AppRemainCreateInfor(
                    application.getEmployeeID(),
                    application.getAppID(),
                    application.getInputDate(),
                    application.getAppDate().getApplicationDate(),
                    EnumAdaptor.valueOf(application.getPrePostAtr().value, PrePostAtr.class),
                    EnumAdaptor.valueOf(application.getAppType().value, ApplicationType.class),
                    Optional.empty(),
                    Optional.empty(),
                    Collections.emptyList(),
                    Optional.empty(),
                    Optional.empty(),
                    application.getOpAppStartDate().map(ApplicationDate::getApplicationDate),
                    application.getOpAppEndDate().map(ApplicationDate::getApplicationDate),
                    listAppDates,
                    Optional.empty(),
                    Optional.empty()
            );
        }).collect(Collectors.toList());

        return apps;
    }

    @AllArgsConstructor
    private class RequireM11Imp implements BreakDayOffMngInPeriodQuery.RequireM11{

        private ComDayOffManaDataRepository comDayOffManaDataRepo;

        private LeaveComDayOffManaRepository leaveComDayOffManaRepo;

        private LeaveManaDataRepository leaveManaDataRepo;

        private ShareEmploymentAdapter shareEmploymentAdapter;

        private CompensLeaveEmSetRepository compensLeaveEmSetRepo;

        private CompensLeaveComSetRepository compensLeaveComSetRepo;

        private InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;

        private ClosureEmploymentRepository closureEmploymentRepo;

        private ClosureRepository closureRepo;

        private PayoutSubofHDManaRepository payoutHdManaRepo;

        @Override
        public Optional<BsEmploymentHistoryImport> findEmploymentHistory(String companyId, String employeeId,
                                                                         GeneralDate baseDate) {
            return shareEmploymentAdapter.findEmploymentHistory(companyId, employeeId, baseDate);
        }

        @Override
        public CompensatoryLeaveEmSetting findComLeavEmpSet(String companyId, String employmentCode) {
            return compensLeaveEmSetRepo.find(companyId, employmentCode);
        }

        @Override
        public CompensatoryLeaveComSetting findComLeavComSet(String companyId) {
            return compensLeaveComSetRepo.find(companyId);
        }

        @Override
        public Optional<ClosureEmployment> employmentClosure(String companyID, String employmentCD) {
            return closureEmploymentRepo.findByEmploymentCD(companyID, employmentCD);
        }

        @Override
        public List<SharedSidPeriodDateEmploymentImport> employmentHistory(CacheCarrier cacheCarrier, List<String> sids,
                                                                           DatePeriod datePeriod) {
            return shareEmploymentAdapter.getEmpHistBySidAndPeriodRequire(cacheCarrier, sids, datePeriod);
        }

        @Override
        public List<Closure> closure(String companyId) {
            return closureRepo.findAll(companyId);
        }

        @Override
        public EmployeeImport employee(CacheCarrier cacheCarrier, String empId) {
            return empEmployeeAdapter.findByEmpIdRequire(cacheCarrier, empId);
        }

        @Override
        public List<PayoutSubofHDManagement> getPayoutSubWithDateUse(String sid, GeneralDate dateOfUse,
                                                                     GeneralDate baseDate) {
            return payoutHdManaRepo.getWithDateUse(sid, dateOfUse, baseDate);
        }

        @Override
        public List<PayoutSubofHDManagement> getPayoutSubWithOutbreakDay(String sid, GeneralDate outbreakDay,
                                                                         GeneralDate baseDate) {
            return payoutHdManaRepo.getWithOutbreakDay(sid, outbreakDay, baseDate);
        }

        @Override
        public List<LeaveComDayOffManagement> getLeaveComWithDateUse(String sid, GeneralDate dateOfUse,
                                                                     GeneralDate baseDate) {
            return leaveComDayOffManaRepo.getLeaveComWithDateUse(sid, dateOfUse, baseDate);
        }

        @Override
        public List<LeaveComDayOffManagement> getLeaveComWithOutbreakDay(String sid, GeneralDate outbreakDay,
                                                                         GeneralDate baseDate) {
            return leaveComDayOffManaRepo.getLeaveComWithOutbreakDay(sid, outbreakDay, baseDate);
        }

        @Override
        public List<Closure> closureActive(String companyId, UseClassification useAtr) {
            return closureRepo.findAllActive(companyId, useAtr);
        }

        @Override
        public List<LeaveComDayOffManagement> getDigestOccByListComId(String sid, DatePeriod period) {
            return leaveComDayOffManaRepo.getDigestOccByListComId(sid, period);
        }

        @Override
        public List<InterimDayOffMng> getTempDayOffBySidPeriod(String sid, DatePeriod period) {
            return interimBreakDayOffMngRepo.getDayOffBySidPeriod(sid, period);
        }

        @Override
        public List<CompensatoryDayOffManaData> getFixByDayOffDatePeriod(String sid) {
            return comDayOffManaDataRepo.getBySid(AppContexts.user().companyId(), sid);
        }

        @Override
        public List<InterimBreakMng> getTempBreakBySidPeriod(String sid, DatePeriod period) {
            return interimBreakDayOffMngRepo.getBySidPeriod(sid, period);
        }

        @Override
        public List<LeaveManagementData> getFixLeavByDayOffDatePeriod(String sid) {
            return leaveManaDataRepo.getBySid(AppContexts.user().companyId(), sid);
        }
    }
}
