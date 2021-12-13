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

        // システム日付　★使用禁止
        //---------------------------------------------------
        // 2021.12.01 inaguma 削除
        //val baseDate = GeneralDate.fromString(hdRemainCond.getBaseDate(), "yyyy/MM/dd");
        //---------------------------------------------------

        val startDate = GeneralDate.fromString(hdRemainCond.getStartMonth(), "yyyy/MM/dd");		// 画面入力期間From(年月)/01
        val endDate   = GeneralDate.fromString(hdRemainCond.getEndMonth(),   "yyyy/MM/dd");		// 画面入力期間To  (年月)/月末日

        // ドメインモデル「休暇残数管理表の出力項目設定」を取得する
        val _hdManagement = hdFinder.findByLayOutId(hdRemainCond.getLayOutId());
        _hdManagement.ifPresent((HolidaysRemainingManagement hdManagement) -> {

			// 社員範囲選択画面で指定した締めID(0～5)　★使用禁止
			// 締めIDを(1～5)にする
            int closureId = hdRemainCond.getClosureId();
            // ※該当の締めIDが「0：全締め」のときは、「1締め（締めID＝1）」とする
            if (closureId == 0) {
                closureId = 1;
            }

			//(誤) アルゴリズム「指定した年月の期間をすべて取得する」を実行する
			//(正) 指定した締めIDのドメインモデル「締め」を取得する
            Optional<Closure> closureOpt = closureRepository.findById(cId, closureId);
            if (!closureOpt.isPresent()) {
                return;
            }
            // アルゴリズム「指定した年月の期間をすべて取得する」を実行する
            List<DatePeriod> periodByYearMonths = closureOpt.get().getPeriodByYearMonth(endDate.yearMonth());
            // 処理基準日を決定する
            Optional<DatePeriod> criteriaDatePeriodOpt = periodByYearMonths.stream()
                    .max(Comparator.comparing(DatePeriod::end));
            if (!criteriaDatePeriodOpt.isPresent()) {
                return;
            }
            GeneralDate criteriaDate = criteriaDatePeriodOpt.get().end();	// 処理基準日
            //-----------------------------------------------------------------------------------
            // 2021.12.01 稲熊 追加 START
            GeneralDate baseDate = criteriaDate;	// 処理基準日
            // 2021.12.01 稲熊 追加 END
            //-----------------------------------------------------------------------------------

            // 画面項目「A2_2：社員リスト」で選択されている社員の社員ID
            List<String> employeeIds = query.getLstEmpIds().stream().map(EmployeeQuery::getEmployeeId)
                    .collect(Collectors.toList());
            // <<Public>> 社員を並べ替える
            employeeIds = this.regulationInfoEmployeeAdapter.sortEmployee(
                    cId,
                    employeeIds,
                    AppContexts.system().getInstallationType().value, null, null,
                    GeneralDateTime.legacyDateTime(criteriaDate.date()));

            Map<String, EmployeeQuery> empMap = query.getLstEmpIds().stream()
                    .collect(Collectors.toMap(EmployeeQuery::getEmployeeId, Function.identity()));

            // <<Public>> 社員の情報を取得する
            List<EmployeeInformationImport> listEmployeeInformationImport = employeeInformationAdapter
                    .getEmployeeInfo(new EmployeeInformationQueryDtoImport(employeeIds, criteriaDate, true, false, true,
                            true, false, false));
            // 出力するデータ件数をチェックする
            if (listEmployeeInformationImport.isEmpty()) {
                throw new BusinessException("Msg_885");
            }

			// 休暇設定画面で設定した各休暇の「管理区分」を取得する
            val varVacaCtr = varVacaCtrSv.getVariousVacationControl();

            // ドメインモデル「締め」(社員範囲選択)より当月、締め期間、締め日(日付)を求める　★使用禁止
            //---------------------------------------------------
            // 2021.11.30 inaguma 削除
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
			// 社員のループSTART
			////////////////////////////////////////////////////////////////////////////////
            parallel.forEach(listEmployeeInformationImport, emp -> {
                String wpCode = emp.getWorkplace() != null ? emp.getWorkplace().getWorkplaceCode() : "";
                String wpName = emp.getWorkplace() != null ? emp.getWorkplace().getWorkplaceName() : TextResource.localize("KDR001_55");
                String empmentName = emp.getEmployment() != null ? emp.getEmployment().getEmploymentName() : "";
                String positionName = emp.getPosition() != null ? emp.getPosition().getPositionName() : "";
                String employmentCode = emp.getEmployment() != null ? emp.getEmployment().getEmploymentCode() : "";
                String positionCode = emp.getPosition() != null ? emp.getPosition().getPositionCode() : "";

                //-----------------------------------------------------------------------------------
                // 2021.12.06 - 3S - chinh.hm  - issues #121626- 追加 START
                // ループ中の社員(emp.getEmployeeId())の雇用を取得⇒取得できなかった場合は次の社員の処理へ
                // 雇用に紐付く締めIDを取得　　　　　　　　　　　 ⇒取得できなかった場合は次の社員の処理へ
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
                // 2021.12.06 - 3S - chinh.hm  - issues #121626- 追加 END
                //-----------------------------------------------------------------------------------

	       		// 当月
                Optional<YearMonth> currentMonth = hdRemainManageFinder.getCurrentMonth(
                        cId,
                        emp.getEmployeeId(),
                        baseDate);

				// 赤丸？
                if (isFirstEmployee.get()) {
                    isFirstEmployee.set(false);
                    currentMonthOfFirstEmp.set(currentMonth.orElse(null));
                } else {
                    if (isSameCurrentMonth.get() && currentMonth.isPresent() && !currentMonth.get().equals(currentMonthOfFirstEmp.get())) {
                        isSameCurrentMonth.set(false);
                    }
                }

				////////////////////////////////////////////////////////////////////////////////
	        	// 「休暇残数管理表の作成」
				////////////////////////////////////////////////////////////////////////////////
                HolidayRemainingInfor holidayRemainingInfor = this.getHolidayRemainingInfor(
                        varVacaCtr,				// 各休暇の管理区分
                        closureInforOpt,		// (2021.12.06)着目社員の「締め期間」などの情報
                        emp.getEmployeeId(),	// 社員ID
                        baseDate,				// システム日付
                        startDate,				// 画面入力期間From(年月)/01
                        endDate,				// 画面入力期間To  (年月)/月末日
                        currentMonth);			// 当月
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
			// 社員のループEND
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
	// 「休暇残数管理表の作成」
	////////////////////////////////////////////////////////////////////////////////
    private HolidayRemainingInfor getHolidayRemainingInfor(VariousVacationControl variousVacationControl,	// 各休暇の管理区分
                                                           Optional<ClosureInfo> closureInforOpt,			// (2021.12.06)着目社員の「締め期間」などの情報
                                                           String employeeId,								// 社員ID
                                                           GeneralDate baseDate,							// システム日付
                                                           GeneralDate startDate,							// 画面入力期間From(年月)/01
                                                           GeneralDate endDate,								// 画面入力期間To  (年月)/月末日
                                                           Optional<YearMonth> currMonth) {					// 当月
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

		// 処理年月
        YearMonth currentMonth = closureInforOpt.get().getCurrentMonth();
        val cId = AppContexts.user().companyId();

		// 画面入力期間From(年月)/01　～　画面入力期間To  (年月)/月末日　★使用禁止
        val datePeriod = new DatePeriod(startDate, endDate);
        // hoatt


		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////
		// 過去月
		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////

		// 過去月の開始年月と終了年月
        YearMonthPeriod period = new YearMonthPeriod(startDate.yearMonth(), currentMonth.previousMonth());

		////////////////////////////////////////////////////////////////////////////////
		// 過去月の月別残数データを取得する
		////////////////////////////////////////////////////////////////////////////////
        // Mer RQ255,258,259,260,263
        HolidayRemainMerEx hdRemainMer = hdRemainAdapter.getRemainMer(employeeId, period);

		// 過去月の年月のリスト作成
        val lstYrMon = ConvertHelper.yearMonthsBetween(period);
        Map<YearMonth, List<RemainMerge>> mapRemainMer = repoRemainMer.findBySidsAndYrMons(employeeId, lstYrMon);


		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////
	    // 現在、当月・未来月
		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////

        // Mer RQ265,268,269,363,364,369
        boolean call265 = variousVacationControl.isAnnualHolidaySetting();		//[旧番号]	年休　　(現在)
        boolean call268 = variousVacationControl.isYearlyReservedSetting();		//			積立年休(現在)
        boolean call269 = variousVacationControl.isSubstituteHolidaySetting();	//[旧番号]	代休　　(現在、当月・未来月)
        boolean call363 = variousVacationControl.isAnnualHolidaySetting()		//			年休　　(現在、当月・未来月)
                && currentMonth.compareTo(endDate.yearMonth()) <= 0;
        boolean call364 = variousVacationControl.isYearlyReservedSetting()		//			積立年休(当月・未来月)
                && currentMonth.compareTo(endDate.yearMonth()) <= 0;
        boolean call369 = variousVacationControl.isAnnualHolidaySetting();		//			年休　　(次回年休付与日)

		////////////////////////////////////////////////////////////////////////////////
	    // 現在、当月・未来月のデータを取得する(363は不要かも…)
		////////////////////////////////////////////////////////////////////////////////
        CheckCallRequest check = new CheckCallRequest(call265, call268, call269, call363, call364, call369);
        HdRemainDetailMerEx remainDel = hdRemainAdapter.getRemainDetailMer(
        								employeeId,
        								currentMonth,
        								baseDate,
										new DatePeriod(startDate, endDate),
										check);

		////////////////////////////////////////////////////////////////////////////////
		// 年休
		////////////////////////////////////////////////////////////////////////////////
        if (variousVacationControl.isAnnualHolidaySetting()) {

            // Call RequestList369					// 次回年休付与日
            grantDate = remainDel.getResult369();

            // Call RequestList281					// 年休付与情報(有効期限あり)
            listAnnLeaGrantNumber = annLeaveAdapter.getAnnLeaGrantNumberImporteds(employeeId);
            listAnnLeaGrantNumber = listAnnLeaGrantNumber.stream()
                    .sorted(Comparator.comparing(AnnLeaGrantNumberImported::getGrantDate)).collect(Collectors.toList());

            // Call RequestList265					// 現在の状況(未使用)
            //annLeaveOfThisMonth = remainDel.getResult265();

            // Call RequestList255 ver2 - hoatt		// 月別利用状況(過去月)
            if (currentMonth.compareTo(startDate.yearMonth()) > 0) {
                listAnnualLeaveUsage = hdRemainMer.getResult255();
            }

            // Call RequestList363					// 月別利用状況(当月・未来月),現在の状況
            if (currentMonth.compareTo(endDate.yearMonth()) <= 0) {
				////////////////////////////////////////////////////////////////////////////////
			    // 現在、当月・未来月のデータを取得する(363専用)
				////////////////////////////////////////////////////////////////////////////////
                //listAnnLeaveUsageStatusOfThisMonth = remainDel.getResult363();
                getRs363 = hdRemainAdapter.getRs363(employeeId, 						// 社員ID
                									currentMonth, 						// 当月
                									baseDate,							// システム日付
                									new DatePeriod(startDate, endDate), // 画面入力期間From(年月)/01　～　画面入力期間To  (年月)/月末日
                									true);								//
            }
        }

		////////////////////////////////////////////////////////////////////////////////
		// 積立年休
		////////////////////////////////////////////////////////////////////////////////
        if (variousVacationControl.isYearlyReservedSetting()) {
            // Call RequestList268						// 現在の状況
            reserveHoliday = remainDel.getResult268();
            // Call RequestList258 ver2 - hoatt			// 月別利用状況(過去月)
            if (currentMonth.compareTo(startDate.yearMonth()) > 0) {
                listReservedYearHoliday = hdRemainMer.getResult258();
            }
            // Call RequestList364						// 月別利用状況(当月・未来月)
            if (currentMonth.compareTo(endDate.yearMonth()) <= 0) {
                listRsvLeaUsedCurrentMon = remainDel.getResult364();
            }
        }

		////////////////////////////////////////////////////////////////////////////////
		// 代休
		////////////////////////////////////////////////////////////////////////////////
        if (variousVacationControl.isSubstituteHolidaySetting()) {

            val breakDayOffMngInPeriodQueryRequire = numberRemainVacationLeaveRangeProcess.createRequire();

			//========================================
			// 当月・未来月
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
			// 過去月
			//========================================
            // Call RequestList259 ver2 - hoatt
            if (currentMonth.compareTo(startDate.yearMonth()) > 0) {
                listStatusHoliday = hdRemainMer.getResult259();
            }

			//========================================
			// 現在
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
		// 振休
		////////////////////////////////////////////////////////////////////////////////
        if (variousVacationControl.isPauseItemHolidaySetting()) {

			//========================================
			// 当月・未来月
			//========================================
			// Call RequestList204

            //-----------------------------------------------------------------------------------
            // 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 START
            GeneralDate start  =  closureInforOpt.get().getPeriod().start();
            GeneralDate end    =  closureInforOpt.get().getPeriod().end();
            // 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 END
            //-----------------------------------------------------------------------------------

            for (YearMonth s = currentMonth; s.lessThanOrEqualTo(endDate.yearMonth()); s = s.addMonths(1)) {

            	//-----------------------------------------------------------------------------------
            	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
                //GeneralDate end        =                GeneralDate.ymd(s.year(), s.month(), 1).addMonths(1).addDays(-1);
                //DatePeriod  periodDate = new DatePeriod(GeneralDate.ymd(s.year(), s.month(), 1), endDate.before(end) ? endDate : end);
                DatePeriod  periodDate = new DatePeriod(start,end);
            	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
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
            	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 START
                // ループが終了するたびに1か月を足すことで、翌月の締め開始日、締め終了日を取得する。
                start = start.addMonths(1);
                end   = end.addMonths(1);
            	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 END
            	//-----------------------------------------------------------------------------------

            }

			//========================================
			// 過去月
			//========================================
            // Call RequestList260 ver2 - hoatt
            if (currentMonth.compareTo(startDate.yearMonth()) > 0) {
                listStatusOfHoliday = hdRemainMer.getResult260();
            }

			//========================================
			// 現在
			//========================================
			// Call RequestList204

            //-----------------------------------------------------------------------------------
            // 2021.12.06 - 3S - chinh.hm  - issues #120916　- 削除 START
            // DatePeriod periodDate = new DatePeriod( GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1),
            //										GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1).addMonths(1).addDays(-1));
            // 2021.12.06 - 3S - chinh.hm  - issues #120916　- 削除 END
            //-----------------------------------------------------------------------------------

            val param = new AbsRecMngInPeriodRefactParamInput(
                    cId,
                    employeeId,
             		//-----------------------------------------------------------------------------------
             		// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
                    //periodDate,
                    closureInforOpt.get().getPeriod(),
             		// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
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
		// 特別休暇
		////////////////////////////////////////////////////////////////////////////////
        // hoatt
        //Map<Integer, SpecialVacationImported> mapSpecVaca = new HashMap<>();
        //Map<YearMonth, Map<Integer, SpecialVacationImported>> lstMapSPVaCurrMon = new HashMap<>();// key
        //Map<Integer, List<SpecialHolidayImported>> mapSpeHd = new HashMap<>();
        Map<Integer, SpecialVacationImportedKdr> map273New = new HashMap<>();
        Map<YearMonth, Map<Integer, SpecialVacationImportedKdr>> lstMap273CurrMon = new HashMap<>();// key

		//========================================
		// 当月・未来月
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
            // 2021.12.06 - 3S - chinh.hm  - issues #120916　- 追加 START
            GeneralDate start  =  closureInforOpt.get().getPeriod().start();
            GeneralDate end    =  closureInforOpt.get().getPeriod().end();
            // 2021.12.06 - 3S - chinh.hm  - issues #120916　- 追加 END
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

					// ↓　New RQ273
                    SpecialVacationImportedKdr spVaImportedNew = specialLeaveAdapter.get273New(
                    		cId,
                            employeeId,
             				//-----------------------------------------------------------------------------------
             				// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
                            //new DatePeriod(	GeneralDate.ymd(ym.year(),    ym.month(),    1),
                            //        		GeneralDate.ymd(ymEnd.year(), ymEnd.month(), 1).addDays(-1)),
                            new DatePeriod(start,end),
             				// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
             				//-----------------------------------------------------------------------------------
                            false,
             				//-----------------------------------------------------------------------------------
             				// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
                            //baseDate, 
                            closureInforOpt.get().getPeriod().end(),
             				// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
             				//-----------------------------------------------------------------------------------
                            sphdCode,
                            false);

                    //mapSPVaCurrMon.put(sphdCode, spVaImported);
                    mapSP273CurrMon.put(sphdCode, spVaImportedNew);
                }
                //lstMapSPVaCurrMon.put(ym, mapSPVaCurrMon);
                lstMap273CurrMon.put(ym, mapSP273CurrMon);

                //-----------------------------------------------------------------------------------
                // 2021.12.06 - 3S - chinh.hm  - issues #120916　- 追加 START
                //ループが終了するたびに1か月を足すことで、翌月の締め開始日、締め終了日を取得する。
                start = start.addMonths(1);
                end   = end.addMonths(1);
                // 2021.12.06 - 3S - chinh.hm  - issues #120916　- 追加 END
                //-----------------------------------------------------------------------------------

            }
        }

		//========================================
		// 現在
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

			// ↓　New RQ273
            SpecialVacationImportedKdr specialVacationImportedNew = specialLeaveAdapter.get273New(
                    cId,
                    employeeId,
                    closureInforOpt.get().getPeriod(),
                    false,
            		//-----------------------------------------------------------------------------------
            		// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
                    //baseDate,
                    closureInforOpt.get().getPeriod().end(),
            		// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
            		//-----------------------------------------------------------------------------------
                    sphdCode,
                    false);
            //mapSpecVaca.put(sphdCode, specialVacationImported);
            map273New.put(sphdCode, specialVacationImportedNew);

			//========================================
			// ？過去月？
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
		// 子の看護
		////////////////////////////////////////////////////////////////////////////////
        //if (variousVacationControl.isChildNursingSetting()) {
        //    // Call RequestList206
        //    childNursingLeave = childNursingAdapter.getChildNursingLeaveCurrentSituation(
        //            cId,
        //            employeeId,
        //            datePeriod);
        //}

		////////////////////////////////////////////////////////////////////////////////
		// 介護
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
		// ここからも引き続きデータ取得メソッド呼出、return値への項目移送を実施している
		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////


		////////////////////////////////////////////////////////////////////////////////
		// 代休
		////////////////////////////////////////////////////////////////////////////////

        val rq = requireService.createRequire();
        // 期間内の休出代休残数を取得する

		//========================================
		// 現在
		//========================================
		// RequestList203

        //-----------------------------------------------------------------------------------
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
        //DatePeriod periodDate = new DatePeriod(
        //        GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1),
        //        GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1).addMonths(1).addDays(-1));
        DatePeriod periodDate = closureInforOpt.get().getPeriod();
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
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
		// 当月・未来月
		//========================================
        // RequestList203

        //-----------------------------------------------------------------------------------
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 START
        GeneralDate start  =  closureInforOpt.get().getPeriod().start();
        GeneralDate end    =  closureInforOpt.get().getPeriod().end();
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 END
        //-----------------------------------------------------------------------------------

        for (YearMonth s = currentMonth; s.lessThanOrEqualTo(endDate.yearMonth()); s = s.addMonths(1)) {

        	//-----------------------------------------------------------------------------------
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
            //GeneralDate end    =                 GeneralDate.ymd(s.year(), s.month(), 1).addMonths(1).addDays(-1);
            //DatePeriod  periods = new DatePeriod(GeneralDate.ymd(s.year(), s.month(), 1), endDate.before(end) ? endDate : end);
            DatePeriod  periods = new DatePeriod(start,end);
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
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
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 START
            // ループが終了するたびに1か月を足すことで、翌月の締め開始日、締め終了日を取得する。
            start = start.addMonths(1);
            end   = end.addMonths(1);
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 END
        	//-----------------------------------------------------------------------------------

        }

		////////////////////////////////////////////////////////////////////////////////
		// 振休
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
		// 60H超休
		////////////////////////////////////////////////////////////////////////////////

		//========================================
		// 60H超休の検証は後日実施
		//========================================

        // [RQ677]期間中の60H超休残数を取得する
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
		// 特別休暇
		////////////////////////////////////////////////////////////////////////////////

		//========================================
		// 過去月
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
		// 子の看護
		// 介護
		////////////////////////////////////////////////////////////////////////////////

		//========================================
		// 過去月
		//========================================
        // RQ 342
        monthlyConfirmedCareForEmployees = getChildcareRemNumEachMonth.getMonthlyConfirmedCareForEmployees(employeeId, lstYrMon);
        // RQ 344
        obtainMonthlyConfirmedCareForEmployees = getChildcareRemNumEachMonth.getObtainMonthlyConfirmedCareForEmployees(employeeId, lstYrMon);

		//========================================
		// 当月・未来月
		//========================================

		//-----------------------------------------------------------------------------------
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 START
        GeneralDate closureStart  =  closureInforOpt.get().getPeriod().start();
        GeneralDate closureEnd    =  closureInforOpt.get().getPeriod().end();
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 END
		//-----------------------------------------------------------------------------------

        for (YearMonth s = currentMonth; s.lessThanOrEqualTo(endDate.yearMonth()); s = s.addMonths(1)) {

        	//-----------------------------------------------------------------------------------
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
            //GeneralDate end     =                GeneralDate.ymd(s.year(), s.month(), 1).addMonths(1).addDays(-1);
            //DatePeriod  periods = new DatePeriod(GeneralDate.ymd(s.year(), s.month(), 1), endDate.before(end) ? endDate : end);
            DatePeriod  periods = new DatePeriod(closureStart,closureEnd);
        	// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
        	//-----------------------------------------------------------------------------------

			// RQ206(改)
            childCareRemNumWithinPeriodRight.add(
            				getRemainingNumberChildCareNurseAdapter.getChildCareRemNumWithinPeriod(
	                            cId,
	                            employeeId,
	                            periods,
        						//-----------------------------------------------------------------------------------
        						// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
	                            //end)
	                            closureInforOpt.get().getPeriod().end())
        						// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
        						//-----------------------------------------------------------------------------------
	                        );
			// RQ207(改)
            nursingCareLeaveThisMonthFutureSituationRight.add(
            				getRemainingNumberChildCareNurseAdapter.getNursingCareLeaveThisMonthFutureSituation(
                            	cId,
                            	employeeId,
                            	periods,
        						//-----------------------------------------------------------------------------------
        						// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
	                            //end)
	                            closureInforOpt.get().getPeriod().end())
        						// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
        						//-----------------------------------------------------------------------------------
                            );

			//-----------------------------------------------------------------------------------
            // 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 START
            //ループが終了するたびに1か月を足すことで、翌月の締め開始日、締め終了日を取得する。
            closureStart = closureStart.addMonths(1);
            closureEnd   = closureEnd.addMonths(1);
            // 2021.12.06 - 3S - chinh.hm  - issues #120916- 追加 END
			//-----------------------------------------------------------------------------------

        }

		//========================================
		// 現在
		//========================================

		//-----------------------------------------------------------------------------------
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- 削除 START
        //GeneralDate end     =                GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1).addMonths(1).addDays(-1);
        //DatePeriod  periods = new DatePeriod(GeneralDate.ymd(currentMonth.year(), currentMonth.month(), 1), endDate.before(end) ? endDate : end);
        // 2021.12.06 - 3S - chinh.hm  - issues #120916- 削除 END
		//-----------------------------------------------------------------------------------

		// RQ206(改)
        childCareRemNumWithinPeriodLeft = getRemainingNumberChildCareNurseAdapter.getChildCareRemNumWithinPeriod(
        									cId, 
        									employeeId, 
        									//-----------------------------------------------------------------------------------
        									// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
        									//periods,
        									//end
        									closureInforOpt.get().getPeriod(), 
        									closureInforOpt.get().getPeriod().end()
        									// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
        									//-----------------------------------------------------------------------------------
        								);
		// RQ207(改)
        nursingCareLeaveThisMonthFutureSituationLeft = getRemainingNumberChildCareNurseAdapter.getNursingCareLeaveThisMonthFutureSituation(
        									cId, 
        									employeeId, 
        									//-----------------------------------------------------------------------------------
        									// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 START
        									//periods,
        									//end
        									closureInforOpt.get().getPeriod(), 
        									closureInforOpt.get().getPeriod().end()
        									// 2021.12.06 - 3S - chinh.hm  - issues #120916- 変更 END
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