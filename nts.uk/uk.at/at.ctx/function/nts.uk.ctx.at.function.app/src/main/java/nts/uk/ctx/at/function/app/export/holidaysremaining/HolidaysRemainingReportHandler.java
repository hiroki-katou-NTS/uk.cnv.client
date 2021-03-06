package nts.uk.ctx.at.function.app.export.holidaysremaining;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.gul.util.value.MutableValue;
import nts.uk.ctx.at.function.app.find.holidaysremaining.HdRemainManageFinder;
import nts.uk.ctx.at.function.dom.adapter.RegulationInfoEmployeeAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationQueryDtoImport;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmploymentImport;
import nts.uk.ctx.at.function.dom.adapter.child.ChildNursingLeaveThisMonthFutureSituation;
import nts.uk.ctx.at.function.dom.adapter.child.GetRemainingNumberCareNurseAdapter;
import nts.uk.ctx.at.function.dom.adapter.child.NursingCareLeaveThisMonthFutureSituation;
import nts.uk.ctx.at.function.dom.adapter.holidayover60h.AggrResultOfHolidayOver60hImport;
import nts.uk.ctx.at.function.dom.adapter.holidayover60h.GetHolidayOver60hPeriodAdapter;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.*;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.ComplileInPeriodOfSpecialLeaveAdapter;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialVacationImported;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialVacationImportedKdr;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.ReserveHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.ReservedYearHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.RsvLeaUsedCurrentMonImported;
import nts.uk.ctx.at.function.dom.adapter.vacation.CurrentHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.vacation.StatusHolidayImported;
import nts.uk.ctx.at.function.dom.holidaysremaining.HolidaysRemainingManagement;
import nts.uk.ctx.at.function.dom.holidaysremaining.VariousVacationControl;
import nts.uk.ctx.at.function.dom.holidaysremaining.VariousVacationControlService;
import nts.uk.ctx.at.function.dom.holidaysremaining.report.*;
import nts.uk.ctx.at.record.dom.monthly.vacation.specialholiday.monthremaindata.export.SpecialHolidayRemainDataSevice;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.NumberCompensatoryLeavePeriodProcess;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.NumberCompensatoryLeavePeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.AbsRecMngInPeriodRefactParamInput;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.NumberRemainVacationLeaveRangeProcess;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.NumberRemainVacationLeaveRangeQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.BreakDayOffRemainMngRefactParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SubstituteHolidayAggrResult;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMerge;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMergeRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.ChildNursingLeaveStatus;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.IGetChildcareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.NursingCareLeaveMonthlyRemaining;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.workrule.closure.*;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.company.CompanyAdapter;
import nts.uk.shr.com.company.CompanyInfor;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class HolidaysRemainingReportHandler extends ExportService<HolidaysRemainingReportQuery> {

    @Inject
    private HolidaysRemainingReportGenerator reportGenerator;
    @Inject
    private HdRemainManageFinder hdFinder;
    @Inject
    private RegulationInfoEmployeeAdapter regulationInfoEmployeeAdapter;
    @Inject
    private EmployeeInformationAdapter employeeInformationAdapter;
    @Inject
    private HdRemainManageFinder hdRemainManageFinder;
    @Inject
    private ClosureRepository closureRepository;
    @Inject
    private AnnLeaveRemainingAdapter annLeaveAdapter;
    @Inject
    private ComplileInPeriodOfSpecialLeaveAdapter specialLeaveAdapter;
    @Inject
    private ChildNursingLeaveRemainingAdapter childNursingAdapter;
    @Inject
    private NursingLeaveRemainingAdapter nursingLeaveAdapter;
    @Inject
    private VariousVacationControlService varVacaCtrSv;
    @Inject
    private CompanyAdapter companyRepo;
    @Inject
    private ManagedParallelWithContext parallel;
    @Inject
    private HolidayRemainMergeAdapter hdRemainAdapter;
    @Inject
    private NumberRemainVacationLeaveRangeProcess numberRemainVacationLeaveRangeProcess;
    @Inject
    private NumberCompensatoryLeavePeriodProcess numberCompensatoryLeavePeriodProcess;
    @Inject
    private RemainNumberTempRequireService requireService;
    @Inject
    private GetHolidayOver60hPeriodAdapter getHolidayOver60hRemNumWithinPeriodAdapter;
    @Inject
    private RemainMergeRepository repoRemainMer;
    @Inject
    private SpecialHolidayRemainDataSevice rq263;
    @Inject
    private IGetChildcareRemNumEachMonth getChildcareRemNumEachMonth;
    @Inject
    private GetRemainingNumberCareNurseAdapter getRemainingNumberChildCareNurseAdapter;
    @Inject
    private ClosureEmploymentRepository closureEmploymentRepository;

    @Override
    protected void handle(ExportServiceContext<HolidaysRemainingReportQuery> context) {
        val query = context.getQuery();
        val hdRemainCond = query.getHolidayRemainingOutputCondition();
        String cId = AppContexts.user().companyId();

        // ????????????????????????????????????
        //---------------------------------------------------
        // 2021.12.01 inaguma ??????
        //val baseDate = GeneralDate.fromString(hdRemainCond.getBaseDate(), "yyyy/MM/dd");
        //---------------------------------------------------

        val startDate = GeneralDate.fromString(hdRemainCond.getStartMonth(), "yyyy/MM/dd");		// ??????????????????From(??????)/01
        val endDate   = GeneralDate.fromString(hdRemainCond.getEndMonth(),   "yyyy/MM/dd");		// ??????????????????To  (??????)/?????????

        // ????????????????????????????????????????????????????????????????????????????????????
        val _hdManagement = hdFinder.findByLayOutId(hdRemainCond.getLayOutId());
        _hdManagement.ifPresent((HolidaysRemainingManagement hdManagement) -> {

			// ?????????????????????????????????????????????ID(0???5)??????????????????
			// ??????ID???(1???5)?????????
            int closureId = hdRemainCond.getClosureId();
            // ??????????????????ID??????0?????????????????????????????????1???????????????ID???1???????????????
            if (closureId == 0) {
                closureId = 1;
            }

			//(???) ??????????????????????????????????????????????????????????????????????????????????????????
			//(???) ??????????????????ID???????????????????????????????????????????????????
            Optional<Closure> closureOpt = closureRepository.findById(cId, closureId);
            if (!closureOpt.isPresent()) {
                return;
            }
            // ??????????????????????????????????????????????????????????????????????????????????????????
            List<DatePeriod> periodByYearMonths = closureOpt.get().getPeriodByYearMonth(endDate.yearMonth());
            // ??????????????????????????????
            Optional<DatePeriod> criteriaDatePeriodOpt = periodByYearMonths.stream()
                    .max(Comparator.comparing(DatePeriod::end));
            if (!criteriaDatePeriodOpt.isPresent()) {
                return;
            }
            GeneralDate criteriaDate = criteriaDatePeriodOpt.get().end();	// ???????????????
            //-----------------------------------------------------------------------------------
            // 2021.12.01 ?????? ?????? START
            GeneralDate baseDate = criteriaDate;	// ???????????????
            // 2021.12.01 ?????? ?????? END
            //-----------------------------------------------------------------------------------

            // ???????????????A2_2????????????????????????????????????????????????????????????ID
            List<String> employeeIds = query.getLstEmpIds().stream().map(EmployeeQuery::getEmployeeId)
                    .collect(Collectors.toList());
            // <<Public>> ????????????????????????
            employeeIds = this.regulationInfoEmployeeAdapter.sortEmployee(
                    cId,
                    employeeIds,
                    AppContexts.system().getInstallationType().value, null, null,
                    GeneralDateTime.legacyDateTime(criteriaDate.date()));

            Map<String, EmployeeQuery> empMap = query.getLstEmpIds().stream()
                    .collect(Collectors.toMap(EmployeeQuery::getEmployeeId, Function.identity()));

            // <<Public>> ??????????????????????????????
            List<EmployeeInformationImport> listEmployeeInformationImport = employeeInformationAdapter
                    .getEmployeeInfo(new EmployeeInformationQueryDtoImport(employeeIds, criteriaDate, true, false, true,
                            true, false, false));
            // ????????????????????????????????????????????????
            if (listEmployeeInformationImport.isEmpty()) {
                throw new BusinessException("Msg_885");
            }

			// ??????????????????????????????????????????????????????????????????????????????
            val varVacaCtr = varVacaCtrSv.getVariousVacationControl();

            // ?????????????????????????????????(??????????????????)???????????????????????????????????????(??????)??????????????????????????????
            //---------------------------------------------------
            // 2021.11.30 inaguma ??????
            //val closureInforOpt = this.getClosureInfo(closureId);
            //---------------------------------------------------

            if (!varVacaCtr.isAnnualHolidaySetting()) {
                hdManagement.getListItemsOutput().getAnnualHoliday().setYearlyHoliday(false);
            }

            if (!varVacaCtr.isYearlyReservedSetting()) {
                hdManagement.getListItemsOutput().getYearlyReserved().setYearlyReserved(false);
            }

            if (!varVacaCtr.isSubstituteHolidaySetting()) {
                hdManagement.getListItemsOutput().getSubstituteHoliday().setOutputItemSubstitute(false);
            }
            if (!varVacaCtr.isPauseItemHolidaySettingCompany()) {
                hdManagement.getListItemsOutput().getPause().setPauseItem(false);
            }
            List<Integer> checkItem = hdManagement.getListItemsOutput().getSpecialHoliday();
            boolean listSpecialHoliday = true;
            for (Integer item : checkItem) {
                for (SpecialHoliday list : varVacaCtr.getListSpecialHoliday()) {
                    if (list.getSpecialHolidayCode().v().equals(item)) {
                        listSpecialHoliday = true;
                        break;
                    } else {
                        listSpecialHoliday = false;
                    }
                }

            }
            if (!listSpecialHoliday) {
                hdManagement.getListItemsOutput().setSpecialHoliday(new ArrayList<>());
            }

            if (!hdManagement.getListItemsOutput().getAnnualHoliday().isYearlyHoliday()
                    && !hdManagement.getListItemsOutput().getYearlyReserved().isYearlyReserved()
                    && !hdManagement.getListItemsOutput().getSubstituteHoliday().isOutputItemSubstitute()
                    && !hdManagement.getListItemsOutput().getPause().isPauseItem()
                    && (hdManagement.getListItemsOutput().getSpecialHoliday().size() == 0)) {

                throw new BusinessException("Msg_885");
            }
            MutableValue<Boolean> isSameCurrentMonth = new MutableValue<>();
            MutableValue<Boolean> isFirstEmployee = new MutableValue<>();
            isSameCurrentMonth.set(true);
            isFirstEmployee.set(true);
            MutableValue<YearMonth> currentMonthOfFirstEmp = new MutableValue<>();
            Map<String, HolidaysRemainingEmployee> mapTmp = Collections
                    .synchronizedMap(new HashMap<String, HolidaysRemainingEmployee>());

			////////////////////////////////////////////////////////////////////////////////
			// ??????????????????START
			////////////////////////////////////////////////////////////////////////////////
            parallel.forEach(listEmployeeInformationImport, emp -> {
                String wpCode = emp.getWorkplace() != null ? emp.getWorkplace().getWorkplaceCode() : "";
                String wpName = emp.getWorkplace() != null ? emp.getWorkplace().getWorkplaceName() : TextResource.localize("KDR001_55");
                String empmentName = emp.getEmployment() != null ? emp.getEmployment().getEmploymentName() : "";
                String positionName = emp.getPosition() != null ? emp.getPosition().getPositionName() : "";
                String employmentCode = emp.getEmployment() != null ? emp.getEmployment().getEmploymentCode() : "";
                String positionCode = emp.getPosition() != null ? emp.getPosition().getPositionCode() : "";

                //-----------------------------------------------------------------------------------
                // 2021.12.06 - 3S - chinh.hm  - issues #121626- ?????? START
                // ?????????????????????(emp.getEmployeeId())??????????????????????????????????????????????????????????????????????????????
                // ????????????????????????ID?????????????????????????????????????????? ????????????????????????????????????????????????????????????
                EmploymentImport employment = emp.getEmployment();
                if(employment == null){
                    return;
                }
                Optional<ClosureEmployment> closureEmployment = closureEmploymentRepository.findByEmploymentCD(cId,employment.getEmploymentCode());
                if(!closureEmployment.isPresent()){
                    return;
                }
                int empClosureId = closureEmployment.get().getClosureId() ;
                val closureInforOpt = this.getClosureInfo(empClosureId);
                // 2021.12.06 - 3S - chinh.hm  - issues #121626- ?????? END
                //-----------------------------------------------------------------------------------

	       		// ??????
                Optional<YearMonth> currentMonth = hdRemainManageFinder.getCurrentMonth(
                        cId,
                        emp.getEmployeeId(),
                        baseDate);

				// ?????????
                if (isFirstEmployee.get()) {
                    isFirstEmployee.set(false);
                    currentMonthOfFirstEmp.set(currentMonth.orElse(null));
                } else {
                    if (isSameCurrentMonth.get() && currentMonth.isPresent() && !currentMonth.get().equals(currentMonthOfFirstEmp.get())) {
                        isSameCurrentMonth.set(false);
                    }
                }

				////////////////////////////////////////////////////////////////////////////////
	        	// ????????????????????????????????????
				////////////////////////////////////////////////////////////////////////////////
                HolidayRemainingInfor holidayRemainingInfor = this.getHolidayRemainingInfor(
                        varVacaCtr,				// ????????????????????????
                        closureInforOpt,		// (2021.12.06)????????????????????????????????????????????????
                        emp.getEmployeeId(),	// ??????ID
                        baseDate,				// ??????????????????
                        startDate,				// ??????????????????From(??????)/01
                        endDate,				// ??????????????????To  (??????)/?????????
                        currentMonth);			// ??????
                mapTmp.put(emp.getEmployeeId(),
                        new HolidaysRemainingEmployee(
                                emp.getEmployeeId(),
                                emp.getEmployeeCode(),
                                empMap.get(emp.getEmployeeId()).getEmployeeName(),
                                empMap.get(emp.getEmployeeId()).getWorkplaceId(),
                                wpCode,
                                wpName,
                                empmentName,
                                employmentCode,
                                positionName,
                                positionCode,
                                currentMonth,
                                holidayRemainingInfor));
            });
			////////////////////////////////////////////////////////////////////////////////
			// ??????????????????END
			////////////////////////////////////////////////////////////////////////////////

            Map<String, HolidaysRemainingEmployee> mapEmp = new HashMap<>(mapTmp);
            Optional<CompanyInfor> companyCurrent = this.companyRepo.getCurrentCompany();

            HolidayRemainingDataSource dataSource = new HolidayRemainingDataSource(
                    hdRemainCond.getStartMonth(),
                    hdRemainCond.getEndMonth(),
                    varVacaCtr, hdRemainCond.getPageBreak(),
                    hdRemainCond.getBaseDate(),
                    hdManagement, isSameCurrentMonth.get(),
                    employeeIds, mapEmp,
                    companyCurrent.isPresent() ? companyCurrent.get().getCompanyName() : "",
                    hdRemainCond.getTitle());

            this.reportGenerator.generate(context.getGeneratorContext(), dataSource);
        });
    }

	////////////////////////////////////////////////////////////////////////////////
	// ????????????????????????????????????
	////////////////////////////////////////////////////////////////////////////////
    private HolidayRemainingInfor getHolidayRemainingInfor(VariousVacationControl variousVacationControl,	// ????????????????????????
                                                           Optional<ClosureInfo> closureInforOpt,			// (2021.12.06)????????????????????????????????????????????????
                                                           String employeeId,								// ??????ID
                                                           GeneralDate baseDate,							// ??????????????????
                                                           GeneralDate startDate,							// ??????????????????From(??????)/01
                                                           GeneralDate endDate,								// ??????????????????To  (??????)/?????????
                                                           Optional<YearMonth> currMonth) {					// ??????
        // 263New
        List<SpecialVacationPastSituation> getSpeHdOfConfMonVer2;
        // RequestList369
        Optional<GeneralDate> grantDate = Optional.empty();
        // RequestList281
        List<AnnLeaGrantNumberImported> listAnnLeaGrantNumber = null;
        // RequestList265
        //AnnLeaveOfThisMonthImported annLeaveOfThisMonth = null;
        // RequestList255
        List<AnnualLeaveUsageImported> listAnnualLeaveUsage = new ArrayList<>();
        // RequestList363
        //List<AnnLeaveUsageStatusOfThisMonthImported> listAnnLeaveUsageStatusOfThisMonth = null;
        // RequestList268
        ReserveHolidayImported reserveHoliday = null;
        // RequestList258
        List<ReservedYearHolidayImported> listReservedYearHoliday = null;
        // RequestList364
        List<RsvLeaUsedCurrentMonImported> listRsvLeaUsedCurrentMon = null;
        // RequestList269
        //List<CurrentHolidayImported> listCurrentHoliday = new ArrayList<>();
        // RequestList259
        List<StatusHolidayImported> listStatusHoliday = null;
        // RequestList204
        List<CurrentHolidayRemainImported> listCurrentHolidayRemain = new ArrayList<>();
        // RequestList260
        List<StatusOfHolidayImported> listStatusOfHoliday = null;
        // RequestList206
        ChildNursingLeaveCurrentSituationImported childNursingLeave = null;
        // RequestList207
        NursingLeaveCurrentSituationImported nursingLeave = null;
        //add by HieuLT
        //CurrentHolidayImported currentHolidayLeft = null;
        CurrentHolidayRemainImported currentHolidayRemainLeft = null;
        List<AggrResultOfAnnualLeaveEachMonthKdr> getRs363 = new ArrayList<>();
        //  RQ 203 right
        Map<YearMonth, SubstituteHolidayAggrResult> substituteHolidayAggrResultsRight = new HashMap<>();
        //  RQ 203 left
        SubstituteHolidayAggrResult substituteHolidayAggrResult;
        // RQ 342
        List<ChildNursingLeaveStatus> monthlyConfirmedCareForEmployees;
        // RQ 344
        List<NursingCareLeaveMonthlyRemaining> obtainMonthlyConfirmedCareForEmployees;
        List<ChildNursingLeaveThisMonthFutureSituation> childCareRemNumWithinPeriodRight = new ArrayList<>();
        ChildNursingLeaveThisMonthFutureSituation childCareRemNumWithinPeriodLeft;

        List<NursingCareLeaveThisMonthFutureSituation> nursingCareLeaveThisMonthFutureSituationRight = new ArrayList<>();
        NursingCareLeaveThisMonthFutureSituation nursingCareLeaveThisMonthFutureSituationLeft;

        if (!closureInforOpt.isPresent()) {
            return null;
        }

		// ????????????
        YearMonth currentMonth = closureInforOpt.get().getCurrentMonth();
        val cId = AppContexts.user().companyId();

		// ??????????????????From(??????)/01???????????????????????????To  (??????)/???????????????????????????
        val datePeriod = new DatePeriod(startDate, endDate);
        // hoatt


		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////
		// ?????????
		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////

		// ???????????????????????????????????????
        YearMonthPeriod period = new YearMonthPeriod(startDate.yearMonth(), currentMonth.previousMonth());

		////////////////////////////////////////////////////////////////////////////////
		// ????????????????????????????????????????????????
		////////////////////////////////////////////////////////////////////////////////
        // Mer RQ255,258,259,260,263
        HolidayRemainMerEx hdRemainMer = hdRemainAdapter.getRemainMer(employeeId, period);

		// ????????????????????????????????????
        val lstYrMon = ConvertHelper.yearMonthsBetween(period);
        Map<YearMonth, List<RemainMerge>> mapRemainMer = repoRemainMer.findBySidsAndYrMons(employeeId, lstYrMon);


		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////
	    // ???????????????????????????
		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////

        // Mer RQ265,268,269,363,364,369
        boolean call265 = variousVacationControl.isAnnualHolidaySetting();		//[?????????]	????????????(??????)
        boolean call268 = variousVacationControl.isYearlyReservedSetting();		//			????????????(??????)
        boolean call269 = variousVacationControl.isSubstituteHolidaySetting();	//[?????????]	????????????(???????????????????????????)
        boolean call363 = variousVacationControl.isAnnualHolidaySetting()		//			????????????(???????????????????????????)
                && currentMonth.compareTo(endDate.yearMonth()) <= 0;
        boolean call364 = variousVacationControl.isYearlyReservedSetting()		//			????????????(??????????????????)
                && currentMonth.compareTo(endDate.yearMonth()) <= 0;
        boolean call369 = variousVacationControl.isAnnualHolidaySetting();		//			????????????(?????????????????????)

		////////////////////////////////////////////////////////////////////////////////
	    // ??????????????????????????????????????????????????????(363??????????????????)
		////////////////////////////////////////////////////////////////////////////////
        CheckCallRequest check = new CheckCallRequest(call265, call268, call269, call363, call364, call369);
        HdRemainDetailMerEx remainDel = hdRemainAdapter.getRemainDetailMer(
        								employeeId,
        								currentMonth,
        								baseDate,
										new DatePeriod(startDate, endDate),
										check);

		////////////////////////////////////////////////////////////////////////////////
		// ??????
		////////////////////////////////////////////////////////////////////////////////
        if (variousVacationControl.isAnnualHolidaySetting()) {

            // Call RequestList369					// ?????????????????????
            grantDate = remainDel.getResult369();

            // Call RequestList281					// ??????????????????(??????????????????)
            listAnnLeaGrantNumber = annLeaveAdapter.getAnnLeaGrantNumberImporteds(employeeId);
            listAnnLeaGrantNumber = listAnnLeaGrantNumber.stream()
                    .sorted(Comparator.comparing(AnnLeaGrantNumberImported::getGrantDate)).collect(Collectors.toList());

            // Call RequestList265					// ???????????????(?????????)
            //annLeaveOfThisMonth = remainDel.getResult265();

            // Call RequestList255 ver2 - hoatt		// ??????????????????(?????????)
            if (currentMonth.compareTo(startDate.yearMonth()) > 0) {
                listAnnualLeaveUsage = hdRemainMer.getResult255();
            }

            // Call RequestList363					// ??????????????????(??????????????????),???????????????
            if (currentMonth.compareTo(endDate.yearMonth()) <= 0) {
				////////////////////////////////////////////////////////////////////////////////
			    // ??????????????????????????????????????????????????????(363??????)
				////////////////////////////////////////////////////////////////////////////////
                //listAnnLeaveUsageStatusOfThisMonth = remainDel.getResult363();
                getRs363 = hdRemainAdapter.getRs363(employeeId, 						// ??????ID
                									currentMonth, 						// ??????
                									baseDate,							// ??????????????????
                									new DatePeriod(startDate, endDate), // ??????????????????From(??????)/01???????????????????????????To  (??????)/?????????
                									true);								//
            }
        }

		////////////////////////////////////////////////////////////////////////////////
		// ????????????
		////////////////////////////////////////////////////////////////////////////////
        if (variousVacationControl.isYearlyReservedSetting()) {
            // Call RequestList268						// ???????????????
            reserveHoliday = remainDel.getResult268();
            // Call RequestList258 ver2 - hoatt			// ??????????????????(?????????)
            if (currentMonth.compareTo(startDate.yearMonth()) > 0) {
                listReservedYearHoliday = hdRemainMer.getResult258();
            }
            // Call RequestList364						// ??????????????????(??????????????????)
            if (currentMonth.compareTo(endDate.yearMonth()) <= 0) {
                listRsvLeaUsedCurrentMon = remainDel.getResult364();
            }
        }

		////////////////////////////////////////////////////////////////////////////////
		// ??????
		////////////////////////////////////////////////////////////////////////////////
        if (variousVacationControl.isSubstituteHolidaySetting()) {

            val breakDayOffMngInPeriodQueryRequire = numberRemainVacationLeaveRangeProcess.createRequire();

			//========================================
			// ??????????????????
			//========================================
            // Call RequestList269
            //for (YearMonth s = currentMonth; s.lessThanOrEqualTo(endDate.yearMonth()); s = s.addMonths(1)) {
                //GeneralDate end = GeneralDate.ymd(s.year(), s.month(), 1).addMonths(1).addDays(-1);
                //DatePeriod periodDate = new DatePeriod(GeneralDate.ymd(s.year(), s.month(), 1),
                //        endDate.before(end) ? endDate : end);
                //BreakDayOffRemainMngRefactParam inputRefactor = new BreakDayOffRemainMngRefactParam(
                //        cId, employeeId,
                //       periodDate,
                //        false,
                //        closureInforOpt.get().getPeriod().end(),
                //        false,
                //        new ArrayList<>(),
                //        Optional.empty(),
                //        Optional.empty(),
                //        new ArrayList<>(),
                //        new ArrayList<>(),
                //        Optional.empty(), new FixedManagementDataMonth());
                //SubstituteHolidayAggrResult currentHoliday = NumberRemainVacationLeaveRangeQuery
                //        .getBreakDayOffMngInPeriod(breakDayOffMngInPeriodQueryRequire, inputRefactor);

                //listCurrentHoliday.add(new CurrentHolidayImported(s, currentHoliday.getCarryoverDay().v(),
                //        currentHoliday.getOccurrenceDay().v(), currentHoliday.getDayUse().v(),
                //        currentHoliday.getUnusedDay().v(), currentHoliday.getRemainDay().v()));
            //}

			//========================================
			// ?????????
			//========================================
            // Call RequestList259 ver2 - hoatt
            if (currentMonth.compareTo(startDate.yearMonth()) > 0) {
                listStatusHoliday = hdRemainMer.getResult259();
            }

			//========================================
			// ??????
			//========================================
			// Call RequestList269
            //DatePeriod periodDate = new DatePeriod(GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1), GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1).addMonths(1).addDays(-1));
            //BreakDayOffRemainMngRefactParam inputRefactor = new BreakDayOffRemainMngRefactParam(
            //        cId, employeeId,
            //        periodDate,
            //        false,
            //        closureInforOpt.get().getPeriod().end(),
            //        false,
            //        new ArrayList<>(),
            //        Optional.empty(),
            //        Optional.empty(),
            //        new ArrayList<>(),
            //        new ArrayList<>(),
            //        Optional.empty(), new FixedManagementDataMonth());
            //SubstituteHolidayAggrResult currentHoliday = NumberRemainVacationLeaveRangeQuery
             //       .getBreakDayOffMngInPeriod(breakDayOffMngInPeriodQueryRequire, inputRefactor);
            //currentHolidayLeft = new CurrentHolidayImported(
            //        currentMonth,
            //        currentHoliday.getCarryoverDay().v(),
            //        currentHoliday.getOccurrenceDay().v(),
            //        currentHoliday.getDayUse().v(),
            //        currentHoliday.getUnusedDay().v(),
            //        currentHoliday.getRemainDay().v());
        }



		////////////////////////////////////////////////////////////////////////////////
		// ??????
		////////////////////////////////////////////////////////////////////////////////
        if (variousVacationControl.isPauseItemHolidaySetting()) {

			//========================================
			// ??????????????????
			//========================================
			// Call RequestList204

            //-----------------------------------------------------------------------------------
            // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
            GeneralDate start  =  closureInforOpt.get().getPeriod().start();
            GeneralDate end    =  closureInforOpt.get().getPeriod().end();
            // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
            //-----------------------------------------------------------------------------------

            for (YearMonth s = currentMonth; s.lessThanOrEqualTo(endDate.yearMonth()); s = s.addMonths(1)) {

            	//-----------------------------------------------------------------------------------
            	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
                //GeneralDate end        =                GeneralDate.ymd(s.year(), s.month(), 1).addMonths(1).addDays(-1);
                //DatePeriod  periodDate = new DatePeriod(GeneralDate.ymd(s.year(), s.month(), 1), endDate.before(end) ? endDate : end);
                DatePeriod  periodDate = new DatePeriod(start,end);
            	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
            	//-----------------------------------------------------------------------------------

               val mngParam = new AbsRecMngInPeriodRefactParamInput(
                		cId,
                		employeeId,
                		periodDate,
                        closureInforOpt.get().getPeriod().end(),
                        false,
                        false,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        new FixedManagementDataMonth());
                CompenLeaveAggrResult remainMng = NumberCompensatoryLeavePeriodQuery
                        .process(numberCompensatoryLeavePeriodProcess.createRequire(), mngParam);
                listCurrentHolidayRemain.add(
                        new CurrentHolidayRemainImported(
                                s,
                                remainMng.getCarryoverDay().v(),
                                remainMng.getOccurrenceDay().v(),
                                remainMng.getDayUse().v(),
                                remainMng.getUnusedDay().v(),
                                remainMng.getRemainDay().v()));

            	//-----------------------------------------------------------------------------------
            	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
                // ?????????????????????????????????1???????????????????????????????????????????????????????????????????????????????????????
                start = start.addMonths(1);
                end   = end.addMonths(1);
            	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
            	//-----------------------------------------------------------------------------------

            }

			//========================================
			// ?????????
			//========================================
            // Call RequestList260 ver2 - hoatt
            if (currentMonth.compareTo(startDate.yearMonth()) > 0) {
                listStatusOfHoliday = hdRemainMer.getResult260();
            }

			//========================================
			// ??????
			//========================================
			// Call RequestList204

            //-----------------------------------------------------------------------------------
            // 2021.12.06 - 3S - chinh.hm  - issues #120916???- ?????? START
            // DatePeriod periodDate = new DatePeriod( GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1),
            //										GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1).addMonths(1).addDays(-1));
            // 2021.12.06 - 3S - chinh.hm  - issues #120916???- ?????? END
            //-----------------------------------------------------------------------------------

            val param = new AbsRecMngInPeriodRefactParamInput(
                    cId,
                    employeeId,
             		//-----------------------------------------------------------------------------------
             		// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
                    //periodDate,
                    closureInforOpt.get().getPeriod(),
             		// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
             		//-----------------------------------------------------------------------------------
                    closureInforOpt.get().getPeriod().end(),
                    false,
                    false,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    new FixedManagementDataMonth());
            CompenLeaveAggrResult remainMng = NumberCompensatoryLeavePeriodQuery.process(
                    numberCompensatoryLeavePeriodProcess.createRequire(), param);
            currentHolidayRemainLeft = new CurrentHolidayRemainImported(
                    currentMonth,
                    remainMng.getCarryoverDay().v(),
                    remainMng.getOccurrenceDay().v(),
                    remainMng.getDayUse().v(),
                    remainMng.getUnusedDay().v(),
                    remainMng.getRemainDay().v());
        }

		////////////////////////////////////////////////////////////////////////////////
		// ????????????
		////////////////////////////////////////////////////////////////////////////////
        // hoatt
        //Map<Integer, SpecialVacationImported> mapSpecVaca = new HashMap<>();
        //Map<YearMonth, Map<Integer, SpecialVacationImported>> lstMapSPVaCurrMon = new HashMap<>();// key
        //Map<Integer, List<SpecialHolidayImported>> mapSpeHd = new HashMap<>();
        Map<Integer, SpecialVacationImportedKdr> map273New = new HashMap<>();
        Map<YearMonth, Map<Integer, SpecialVacationImportedKdr>> lstMap273CurrMon = new HashMap<>();// key

		//========================================
		// ??????????????????
		//========================================
        // Call RequestList273
        if (currMonth.isPresent() && currMonth.get().lessThanOrEqualTo(endDate.yearMonth())) {
            List<YearMonth> lstMon = new ArrayList<>();
            YearMonth monCheck = currMonth.get().greaterThanOrEqualTo(startDate.yearMonth()) ? currMonth.get()
                    : startDate.yearMonth();
            for (YearMonth i = monCheck; i.lessThanOrEqualTo(endDate.yearMonth()); i = i.addMonths(1)) {
                lstMon.add(i);
            }

            //-----------------------------------------------------------------------------------
            // 2021.12.06 - 3S - chinh.hm  - issues #120916???- ?????? START
            GeneralDate start  =  closureInforOpt.get().getPeriod().start();
            GeneralDate end    =  closureInforOpt.get().getPeriod().end();
            // 2021.12.06 - 3S - chinh.hm  - issues #120916???- ?????? END
            //-----------------------------------------------------------------------------------

            for (YearMonth ym : lstMon) {// year mon
                //Map<Integer, SpecialVacationImported> mapSPVaCurrMon = new HashMap<>();

                Map<Integer, SpecialVacationImportedKdr> mapSP273CurrMon = new HashMap<>();
                for (SpecialHoliday specialHolidayDto : variousVacationControl.getListSpecialHoliday()) {// sphdCd
                    int sphdCode = specialHolidayDto.getSpecialHolidayCode().v();
                    //YearMonth ymEnd = ym.addMonths(1);
                    //SpecialVacationImported spVaImported = specialLeaveAdapter.complileInPeriodOfSpecialLeave(cId,
                    //        employeeId,
                    //        new DatePeriod(GeneralDate.ymd(ym.year(), ym.month(), 1),
                    //                GeneralDate.ymd(ymEnd.year(), ymEnd.month(), 1).addDays(-1)),
                    //        false, baseDate, sphdCode, false);

					// ??????New RQ273
                    SpecialVacationImportedKdr spVaImportedNew = specialLeaveAdapter.get273New(
                    		cId,
                            employeeId,
             				//-----------------------------------------------------------------------------------
             				// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
                            //new DatePeriod(	GeneralDate.ymd(ym.year(),    ym.month(),    1),
                            //        		GeneralDate.ymd(ymEnd.year(), ymEnd.month(), 1).addDays(-1)),
                            new DatePeriod(start,end),
             				// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
             				//-----------------------------------------------------------------------------------
                            false,
             				//-----------------------------------------------------------------------------------
             				// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
                            //baseDate, 
                            closureInforOpt.get().getPeriod().end(),
             				// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
             				//-----------------------------------------------------------------------------------
                            sphdCode,
                            false);

                    //mapSPVaCurrMon.put(sphdCode, spVaImported);
                    mapSP273CurrMon.put(sphdCode, spVaImportedNew);
                }
                //lstMapSPVaCurrMon.put(ym, mapSPVaCurrMon);
                lstMap273CurrMon.put(ym, mapSP273CurrMon);

                //-----------------------------------------------------------------------------------
                // 2021.12.06 - 3S - chinh.hm  - issues #120916???- ?????? START
                //?????????????????????????????????1???????????????????????????????????????????????????????????????????????????????????????
                start = start.addMonths(1);
                end   = end.addMonths(1);
                // 2021.12.06 - 3S - chinh.hm  - issues #120916???- ?????? END
                //-----------------------------------------------------------------------------------

            }
        }

		//========================================
		// ??????
		//========================================
        for (SpecialHoliday specialHolidayDto : variousVacationControl.getListSpecialHoliday()) {
            int sphdCode = specialHolidayDto.getSpecialHolidayCode().v();
            // Call RequestList273
            //SpecialVacationImported specialVacationImported = specialLeaveAdapter.complileInPeriodOfSpecialLeave(
            //        cId,
            //        employeeId,
            //        closureInforOpt.get().getPeriod(),
            //        false,
            //        baseDate,
            //       sphdCode,
            //        false);

			// ??????New RQ273
            SpecialVacationImportedKdr specialVacationImportedNew = specialLeaveAdapter.get273New(
                    cId,
                    employeeId,
                    closureInforOpt.get().getPeriod(),
                    false,
            		//-----------------------------------------------------------------------------------
            		// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
                    //baseDate,
                    closureInforOpt.get().getPeriod().end(),
            		// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
            		//-----------------------------------------------------------------------------------
                    sphdCode,
                    false);
            //mapSpecVaca.put(sphdCode, specialVacationImported);
            map273New.put(sphdCode, specialVacationImportedNew);

			//========================================
			// ???????????????
			//========================================
            // Call RequestList263 ver2 -
            //if (currentMonth.compareTo(startDate.yearMonth()) > 0) {
            //    List<SpecialHolidayImported> specialHolidayList = specialLeaveAdapter.getSpeHoliOfConfirmedMonthly(
            //            employeeId,
            //            startDate.yearMonth(),
            //            currentMonth.previousMonth(),
            //            Collections.singletonList(sphdCode));
            //    mapSpeHd.put(sphdCode, specialHolidayList);

            //} else {
            //    mapSpeHd.put(sphdCode, new ArrayList<>());
            //}
        }

		////////////////////////////////////////////////////////////////////////////////
		// ????????????
		////////////////////////////////////////////////////////////////////////////////
        //if (variousVacationControl.isChildNursingSetting()) {
        //    // Call RequestList206
        //    childNursingLeave = childNursingAdapter.getChildNursingLeaveCurrentSituation(
        //            cId,
        //            employeeId,
        //            datePeriod);
        //}

		////////////////////////////////////////////////////////////////////////////////
		// ??????
		////////////////////////////////////////////////////////////////////////////////
        //if (variousVacationControl.isNursingCareSetting()) {
        //    // Call RequestList207
        //    nursingLeave = nursingLeaveAdapter.getNursingLeaveCurrentSituation(
        //            cId,
        //            employeeId,
        //            datePeriod);
        //}





		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////
		// ???????????????????????????????????????????????????????????????return??????????????????????????????????????????
		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////


		////////////////////////////////////////////////////////////////////////////////
		// ??????
		////////////////////////////////////////////////////////////////////////////////

        val rq = requireService.createRequire();
        // ?????????????????????????????????????????????

		//========================================
		// ??????
		//========================================
		// RequestList203

        //-----------------------------------------------------------------------------------
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
        //DatePeriod periodDate = new DatePeriod(
        //        GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1),
        //        GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1).addMonths(1).addDays(-1));
        DatePeriod periodDate = closureInforOpt.get().getPeriod();
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
        //-----------------------------------------------------------------------------------

        BreakDayOffRemainMngRefactParam inputRefactor = new BreakDayOffRemainMngRefactParam(
                cId,
                employeeId,
                periodDate,
                false,
                closureInforOpt.get().getPeriod().end(),
                false,
                new ArrayList<>(),
                Optional.empty(),
                Optional.empty(),
                new ArrayList<>(),
                new ArrayList<>(),
                Optional.empty(), new FixedManagementDataMonth());
        substituteHolidayAggrResult = NumberRemainVacationLeaveRangeQuery
                .getBreakDayOffMngInPeriod(rq, inputRefactor);

		//========================================
		// ??????????????????
		//========================================
        // RequestList203

        //-----------------------------------------------------------------------------------
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
        GeneralDate start  =  closureInforOpt.get().getPeriod().start();
        GeneralDate end    =  closureInforOpt.get().getPeriod().end();
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
        //-----------------------------------------------------------------------------------

        for (YearMonth s = currentMonth; s.lessThanOrEqualTo(endDate.yearMonth()); s = s.addMonths(1)) {

        	//-----------------------------------------------------------------------------------
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
            //GeneralDate end    =                 GeneralDate.ymd(s.year(), s.month(), 1).addMonths(1).addDays(-1);
            //DatePeriod  periods = new DatePeriod(GeneralDate.ymd(s.year(), s.month(), 1), endDate.before(end) ? endDate : end);
            DatePeriod  periods = new DatePeriod(start,end);
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
        	//-----------------------------------------------------------------------------------

            BreakDayOffRemainMngRefactParam input = new BreakDayOffRemainMngRefactParam(
                    cId,
                    employeeId,
                    periods,
                    false,
                    closureInforOpt.get().getPeriod().end(),
                    false,
                    new ArrayList<>(),
                    Optional.empty(),
                    Optional.empty(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    Optional.empty(), new FixedManagementDataMonth());
            val item = NumberRemainVacationLeaveRangeQuery
                    .getBreakDayOffMngInPeriod(rq, input);
            substituteHolidayAggrResultsRight.put(s, item);

        	//-----------------------------------------------------------------------------------
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
            // ?????????????????????????????????1???????????????????????????????????????????????????????????????????????????????????????
            start = start.addMonths(1);
            end   = end.addMonths(1);
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
        	//-----------------------------------------------------------------------------------

        }

		////////////////////////////////////////////////////////////////////////////////
		// ??????
		////////////////////////////////////////////////////////////////////////////////
//        CompenLeaveAggrResult subVaca = null;
//        if (closureInforOpt.isPresent()) {
//            val param = new AbsRecMngInPeriodRefactParamInput(
//                    cId,
//                    employeeId,
//                    periodDate,
//                    closureInforOpt.get().getPeriod().end(),
//                    false,
//                    false,
//                    new ArrayList<>(),
//                    new ArrayList<>(),
//                    new ArrayList<>(),
//                    Optional.empty()
//                    , Optional.empty()
//                    , Optional.empty(),
//                    new FixedManagementDataMonth());
//            subVaca = NumberCompensatoryLeavePeriodQuery
//                    .process(rq, param);
//        }



		////////////////////////////////////////////////////////////////////////////////
		// 60H??????
		////////////////////////////////////////////////////////////////////////////////

		//========================================
		// 60H??????????????????????????????
		//========================================

        // [RQ677]????????????60H???????????????????????????
        AggrResultOfHolidayOver60hImport aggrResultOfHolidayOver60h = this.getHolidayOver60hRemNumWithinPeriodAdapter.algorithm(
                cId,
                employeeId,
                datePeriod,
                InterimRemainMngMode.OTHER,
                baseDate,
                Optional.of(false),
                Optional.empty(),
                Optional.empty()
        );


		////////////////////////////////////////////////////////////////////////////////
		// ????????????
		////////////////////////////////////////////////////////////////////////////////

		//========================================
		// ?????????
		//========================================
		// Call RequestList263
        getSpeHdOfConfMonVer2 =
                rq263.getSpeHdOfConfMonVer2(employeeId,
                							period,
                							mapRemainMer).stream().map(e -> new SpecialVacationPastSituation(
                        e.getSid(),
                        e.getYm(),
                        e.getSpecialHolidayCd(),
                        e.getUseDays(),
                        e.getUseTimes(),
                        e.getAfterRemainDays() == 0 ?e.getBeforeRemainDays() :e.getAfterRemainDays(),
                        e.getAfterRemainTimes() == 0 ?e.getBeforeRemainTimes():e.getAfterRemainTimes()
                )).collect(Collectors.toList());


		////////////////////////////////////////////////////////////////////////////////
		// ????????????
		// ??????
		////////////////////////////////////////////////////////////////////////////////

		//========================================
		// ?????????
		//========================================
        // RQ 342
        monthlyConfirmedCareForEmployees = getChildcareRemNumEachMonth.getMonthlyConfirmedCareForEmployees(employeeId, lstYrMon);
        // RQ 344
        obtainMonthlyConfirmedCareForEmployees = getChildcareRemNumEachMonth.getObtainMonthlyConfirmedCareForEmployees(employeeId, lstYrMon);

		//========================================
		// ??????????????????
		//========================================

		//-----------------------------------------------------------------------------------
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
        GeneralDate closureStart  =  closureInforOpt.get().getPeriod().start();
        GeneralDate closureEnd    =  closureInforOpt.get().getPeriod().end();
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
		//-----------------------------------------------------------------------------------

        for (YearMonth s = currentMonth; s.lessThanOrEqualTo(endDate.yearMonth()); s = s.addMonths(1)) {

        	//-----------------------------------------------------------------------------------
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
            //GeneralDate end     =                GeneralDate.ymd(s.year(), s.month(), 1).addMonths(1).addDays(-1);
            //DatePeriod  periods = new DatePeriod(GeneralDate.ymd(s.year(), s.month(), 1), endDate.before(end) ? endDate : end);
            DatePeriod  periods = new DatePeriod(closureStart,closureEnd);
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
        	//-----------------------------------------------------------------------------------

			// RQ206(???)
            childCareRemNumWithinPeriodRight.add(
            				getRemainingNumberChildCareNurseAdapter.getChildCareRemNumWithinPeriod(
	                            cId,
	                            employeeId,
	                            periods,
        						//-----------------------------------------------------------------------------------
        						// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
	                            //end)
	                            closureInforOpt.get().getPeriod().end())
        						// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
        						//-----------------------------------------------------------------------------------
	                        );
			// RQ207(???)
            nursingCareLeaveThisMonthFutureSituationRight.add(
            				getRemainingNumberChildCareNurseAdapter.getNursingCareLeaveThisMonthFutureSituation(
                            	cId,
                            	employeeId,
                            	periods,
        						//-----------------------------------------------------------------------------------
        						// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
	                            //end)
	                            closureInforOpt.get().getPeriod().end())
        						// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
        						//-----------------------------------------------------------------------------------
                            );

			//-----------------------------------------------------------------------------------
            // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
            //?????????????????????????????????1???????????????????????????????????????????????????????????????????????????????????????
            closureStart = closureStart.addMonths(1);
            closureEnd   = closureEnd.addMonths(1);
            // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
			//-----------------------------------------------------------------------------------

        }

		//========================================
		// ??????
		//========================================

		//-----------------------------------------------------------------------------------
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
        //GeneralDate end     =                GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1).addMonths(1).addDays(-1);
        //DatePeriod  periods = new DatePeriod(GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1), endDate.before(end) ? endDate : end);
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
		//-----------------------------------------------------------------------------------

		// RQ206(???)
        childCareRemNumWithinPeriodLeft = getRemainingNumberChildCareNurseAdapter.getChildCareRemNumWithinPeriod(
        									cId, 
        									employeeId, 
        									//-----------------------------------------------------------------------------------
        									// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
        									//periods,
        									//end
        									closureInforOpt.get().getPeriod(), 
        									closureInforOpt.get().getPeriod().end()
        									// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
        									//-----------------------------------------------------------------------------------
        								);
		// RQ207(???)
        nursingCareLeaveThisMonthFutureSituationLeft = getRemainingNumberChildCareNurseAdapter.getNursingCareLeaveThisMonthFutureSituation(
        									cId, 
        									employeeId, 
        									//-----------------------------------------------------------------------------------
        									// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? START
        									//periods,
        									//end
        									closureInforOpt.get().getPeriod(), 
        									closureInforOpt.get().getPeriod().end()
        									// 2021.12.06 - 3S - chinh.hm  - issues #120916- ?????? END
        									//-----------------------------------------------------------------------------------
        								);


		////////////////////////////////////////////////////////////////////////////////
		// RETURN
		////////////////////////////////////////////////////////////////////////////////
        return new HolidayRemainingInfor(
                grantDate,
                listAnnLeaGrantNumber,
                //annLeaveOfThisMonth,
                listAnnualLeaveUsage,
                reserveHoliday,
                listReservedYearHoliday,
                listRsvLeaUsedCurrentMon,
                //listCurrentHoliday,
                listStatusHoliday,
                listCurrentHolidayRemain,
                listStatusOfHoliday,
                //currentHolidayLeft,
                currentHolidayRemainLeft,
                substituteHolidayAggrResult,
                //subVaca,
                aggrResultOfHolidayOver60h,
                getRs363,
                getSpeHdOfConfMonVer2,
                substituteHolidayAggrResultsRight,
                closureInforOpt,
                lstMap273CurrMon,
                map273New,
                monthlyConfirmedCareForEmployees,
                obtainMonthlyConfirmedCareForEmployees,
                childCareRemNumWithinPeriodLeft,
                childCareRemNumWithinPeriodRight,
                nursingCareLeaveThisMonthFutureSituationRight,
                nursingCareLeaveThisMonthFutureSituationLeft);
    }

    private Optional<ClosureInfo> getClosureInfo(int closureId) {

        val listClosureInfo = ClosureService.getAllClosureInfo(ClosureService.createRequireM2(closureRepository));
        return listClosureInfo.stream().filter(i -> i.getClosureId().value == closureId).findFirst();
    }
}