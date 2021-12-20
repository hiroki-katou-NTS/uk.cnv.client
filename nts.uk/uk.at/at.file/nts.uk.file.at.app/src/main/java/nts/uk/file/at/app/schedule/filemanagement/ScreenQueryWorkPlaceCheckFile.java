package nts.uk.file.at.app.schedule.filemanagement;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.app.cache.DateHistoryCache;
import nts.arc.layer.app.cache.KeyDateHistoryCache;
import nts.arc.layer.app.cache.MapCache;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.RegulationInfoEmployeeAdapter;
import nts.uk.ctx.at.schedule.app.query.schedule.shift.management.shifttable.GetHolidaysByPeriod;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheAuthModifyDeadline;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheAuthModifyDeadlineRepository;
import nts.uk.ctx.at.schedule.dom.importschedule.CapturedRawData;
import nts.uk.ctx.at.schedule.dom.importschedule.ImportResult;
import nts.uk.ctx.at.schedule.dom.importschedule.ImportResultDetail;
import nts.uk.ctx.at.schedule.dom.importschedule.ImportStatus;
import nts.uk.ctx.at.schedule.dom.importschedule.WorkScheduleImportService;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ConfirmedATR;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.EmployeeAndYmd;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHoliday;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.PersonEmpBasicInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkPeriodImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmployeeLeaveJobPeriodImport;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemWithPeriod;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpComHisAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpEnrollPeriodImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisScheduleAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.EmployeeSearchCallSystemType;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.RegulationInfoEmpQuery;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpAffiliationInforAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.setting.code.EmployeeCodeEditSettingExport;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.setting.code.IEmployeeCESettingPub;
import nts.uk.query.pub.employee.EmployeeSearchQueryDto;
import nts.uk.query.pub.employee.RegulationInfoEmployeeExport;
import nts.uk.query.pub.employee.RegulationInfoEmployeePub;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;


/**
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL055_個人スケジュールの取込み.B：内容チェック.メニュー別OCD.取り込み内容を取得する
 * 取り込み内容を取得する
 * @author anhnm
 *
 */
@Stateless
public class ScreenQueryWorkPlaceCheckFile {

    @Inject
    private IEmployeeCESettingPub employeeSettingPub;

    @Inject
    private CheckFileService checkfileService;

    @Inject
    private ScheAuthModifyDeadlineRepository scheAuthModifyDeadlineRepository;

    @Inject
    private WorkplaceGroupAdapter workplaceGroupAdapter;

    @Inject
    private RegulationInfoEmployeeAdapter regulInfoEmpAdap;

    @Inject
    private RegulationInfoEmployeePub regulInfoEmpPub;

    @Inject
    private WorkingConditionRepository workingConditionRepo;

    @Inject
    private WorkTypeRepository workTypeRepo;

    @Inject
    private WorkTimeSettingRepository workTimeSettingRepository;

    @Inject
    private BasicScheduleService basicScheduleService;

    @Inject
    private FixedWorkSettingRepository fixedWorkSettingRepository;

    @Inject
    private FlowWorkSettingRepository flowWorkSettingRepository;

    @Inject
    private FlexWorkSettingRepository flexWorkSettingRepository;

    @Inject
    private PredetemineTimeSettingRepository predetemineTimeSettingRepository;

    @Inject
    private ShiftMasterRepository shiftMasterRepository;

    @Inject
    private WorkScheduleRepository workScheduleRepository;

    @Inject
    private EmpEmployeeAdapter empEmployeeAdapter;

    @Inject
    private EmpComHisAdapter comHisAdapter;

    @Inject
    private EmpLeaveWorkHistoryAdapter leaHisAdapter;

    @Inject
    private EmploymentHisScheduleAdapter scheAdapter;

    @Inject
    private GetHolidaysByPeriod getHolidaysByPeriod;

    @Inject
    private EmpLeaveHistoryAdapter empHisAdapter;
    
    @Inject
	private EmpAffiliationInforAdapter empAffiliationInforAdapter;

    public CapturedRawDataDto processingFile(WorkPlaceScheCheckFileParam param) throws Exception {
        try {
            Optional<EmployeeCodeEditSettingExport> employeeCESettingOpt = employeeSettingPub.getByComId(AppContexts.user().companyId());
            EmployeeCodeEditSettingExport employeeCodeEditSettingExport = null;
            if (employeeCESettingOpt.isPresent()) {
                employeeCodeEditSettingExport = employeeCESettingOpt.get();
            }
            return CapturedRawDataDto.fromDomain(checkfileService.processingFile(param, employeeCodeEditSettingExport));

        } catch (Exception e) {
            throw e;
        }
    }

    public CaptureDataOutput getCaptureData(CapturedRawDataDto data, boolean overwrite) {
        // 1: 取り込む(Require, 取り込み内容)
        long startImport = System.currentTimeMillis();
        System.out.println("Start import");
        CapturedRawData rawData = data.toDomain();
        ImportResult importResult = WorkScheduleImportService.importFrom( new RequireImp( rawData ), rawData );
        long endImport = System.currentTimeMillis();
        System.out.println("Time Import File: " + (endImport - startImport));

        // 2: 取り込み可能な社員IDリストを取得する()
        long startGetImportaleEmp = System.currentTimeMillis();
        List<EmployeeId> employeeIds = importResult.getImportableEmployees();
        long endGetImportaleEmp = System.currentTimeMillis();
        System.out.println("Time Get Importable Emp: " + (endGetImportaleEmp - startGetImportaleEmp));

        // 3: 取り込み可能な年月日リストを取得する()
        long startGetImportableDates = System.currentTimeMillis();
        List<GeneralDate> importableDates = importResult.getImportableDates();
        long endGetImportableDates = System.currentTimeMillis();
        System.out.println("Time Get Importable Date: " + (endGetImportableDates - startGetImportableDates));

        // 4: 処理2の結果
        long startGetListEmp = System.currentTimeMillis();
        List<PersonEmpBasicInfoImport> listPersonEmp = this.empEmployeeAdapter.getPerEmpBasicInfo(employeeIds.stream().map(x -> x.v()).collect(Collectors.toList()));
        long endGetListEmp = System.currentTimeMillis();
        System.out.println("Time Get List Emp: " + (endGetListEmp - startGetListEmp));

        // 5: create 取り込みエラーDto
        long startMappingError = System.currentTimeMillis();
        List<MappingErrorDto> mappingErrorList = new ArrayList<MappingErrorDto>();
        long endMappingError = System.currentTimeMillis();
        System.out.println("Time Mapping Error: " + (endMappingError - startMappingError));
        // <<create>>
        //            取り込み結果.取り込み不可日 :
        //　　map 取り込みエラーDto (empty, empty, $, #Msg_2121)
        long startMap2121 = System.currentTimeMillis();
        importResult.getUnimportableDates().forEach(x -> {
            mappingErrorList.add(new MappingErrorDto(Optional.empty(), Optional.empty(), Optional.of(x), TextResource.localize("Msg_2121")));
        });
        long endMap2121 = System.currentTimeMillis();
        System.out.println("Time Mapping 2121: " + (endMap2121 - startMap2121));
        // <<create>>
        //            取り込み結果.存在しない社員 :
        //　　map 取り込みエラーDto ($, empty, empty, #Msg_2175)
        long startMap2175 = System.currentTimeMillis();
        importResult.getUnexistsEmployees().forEach(x -> {
            mappingErrorList.add(new MappingErrorDto(Optional.of(x), Optional.empty(), Optional.empty(), TextResource.localize("Msg_2175")));
        });
        long endMap2175 = System.currentTimeMillis();
        System.out.println("Time Mapping 2175: " + (endMap2175 - startMap2175));
        // <<create>>
        //            取り込み結果.1件分の取り込み結果 :
        //　　find $.状態.取り込み不可か()
        //　　map { $対象社員 = $社員 in 処理4の結果 : find $社員.社員ID == $.社員ID;
        //　　　　　return 取り込みエラーDto ($対象社員.社員コード, $対象社員.ビジネスネーム, $.年月日, $.状態.エラーメッセージ) }
        long startMapDetailErr = System.currentTimeMillis();
        List<ImportResultDetail> resultDetails1 = importResult.getResults().stream().filter(x -> x.getStatus().isUnimportable()).collect(Collectors.toList());

        resultDetails1.forEach(x -> {
            Optional<PersonEmpBasicInfoImport> personEmpOptional = listPersonEmp.stream()
                    .filter(y -> y.getEmployeeId().equals(x.getEmployeeId().v())).findFirst();
            if (personEmpOptional.isPresent()) {
                mappingErrorList.add(new MappingErrorDto(
                        Optional.of(personEmpOptional.get().getEmployeeCode()),
                        Optional.of(personEmpOptional.get().getBusinessName()),
                        Optional.of(x.getYmd()),
                        x.getStatus().getMessageId().isPresent() ? TextResource.localize(x.getStatus().getMessageId().get().replace("#", "")) : ""));
            }
        });
        long endMapDetailErr = System.currentTimeMillis();
        System.out.println("Time Mapping Detail Error: " + (endMapDetailErr - startMapDetailErr));
        // <<create>>
        //            取り込み結果.1件分の取り込み結果 :
        //　　find $.状態 == すでに勤務予定が存在する && Input.上書きするか == false
        //　　map { $対象社員 = $社員 in 処理4の結果 : find $社員.社員ID == $.社員ID;
        //　　　　　return 取り込みエラーDto ($対象社員.社員コード, $対象社員.ビジネスネーム, $.年月日, $.状態.エラーメッセージ) }
        long startMapDetailErr2 = System.currentTimeMillis();
        List<ImportResultDetail> resultDetails2 = importResult.getResults().stream().filter(x -> {
            return x.getStatus().equals(ImportStatus.SCHEDULE_IS_EXISTS) && !overwrite;
        }).collect(Collectors.toList());
        resultDetails2.forEach(x -> {
            Optional<PersonEmpBasicInfoImport> personEmpOptional = listPersonEmp.stream()
                    .filter(y -> y.getEmployeeId().equals(x.getEmployeeId().v())).findFirst();
            if (personEmpOptional.isPresent()) {
                mappingErrorList.add(new MappingErrorDto(
                        Optional.of(personEmpOptional.get().getEmployeeCode()),
                        Optional.of(personEmpOptional.get().getBusinessName()),
                        Optional.of(x.getYmd()),
                        x.getStatus().getMessageId().isPresent() ? TextResource.localize(x.getStatus().getMessageId().get().replace("#", "")) : ""));
            }
        });
        long endMapDetailErr2 = System.currentTimeMillis();
        System.out.println("Time Mapping Detail Error 2: " + (endMapDetailErr2 - startMapDetailErr2));

        // 6: 曜日()
        // Use List<GeneralDate> importableDates step 3

        //7: 取得する(期間)
        long startGetHolidays = System.currentTimeMillis();
        List<PublicHoliday> holidays = new ArrayList<PublicHoliday>();
        if (importableDates.size() > 0) {
            holidays = getHolidaysByPeriod.get(new DatePeriod(importableDates.get(0), importableDates.get(importableDates.size() - 1)));
        }
        long endGetHolidays = System.currentTimeMillis();
        System.out.println("Time Get Holidays: " + (endGetHolidays - startGetHolidays));

        return new CaptureDataOutput(listPersonEmp, importableDates, holidays, importResult, mappingErrorList);
    }

    private class RequireImp implements WorkScheduleImportService.Require {
        private final MapCache<String, ScheAuthModifyDeadline> scheAuthModifyDeadlineCache;

        private final MapCache<GetEmpCanReferByWorkplaceGroupParam, List<String>> workplaceGroupCache;

        private final MapCache<GetAllEmpCanReferByWorkplaceGroupParam, List<String>> workplaceGroupAllCache;

        private KeyDateHistoryCache<String, WorkingConditionItemWithPeriod> workCondItemWithPeriodCache;

        private final KeyDateHistoryCache<String, EmployeeLeaveJobPeriodImport> empHisCache;

        private final MapCache<String, SetupType> basicScheduleCache;

        private final MapCache<WorkTimeCode, FixedWorkSetting> fixedWorkSettingCache;

        private final MapCache<WorkTimeCode, FlowWorkSetting> flowWorkSettingCache;

        private final MapCache<WorkTimeCode, FlexWorkSetting> flexWorkSettingCache;

        private final MapCache<WorkTimeCode, PredetemineTimeSetting> predetemineTimeSettingCache;

        private final List<ShiftMaster> shiftMasterCache;

        private final MapCache<String, WorkType> workTypeCache;

        private final MapCache<String, WorkTimeSetting> workTimeSettingCache;

        private final Map<String, String> empEmployeeCache;

        private final KeyDateHistoryCache<String, EmpEnrollPeriodImport> affCompanyHistByEmployeeCache;

        private final KeyDateHistoryCache<String, EmploymentPeriodImported> employmentPeriodCache;

        private final KeyDateHistoryCache<String, EmpLeaveWorkPeriodImport> empLeaveWorkPeriodCache;

        private final Map<EmployeeAndYmd, Boolean> workScheExistedCache;

        private final Map<EmployeeAndYmd, ConfirmedATR> workScheConfirmAtrMap;

        public RequireImp(CapturedRawData rawData) {

            List<String> employeeCodes = rawData.getEmployeeCodes();
            List<String> importCodes = rawData.getContents().stream().map( e -> e.getImportCode().v() ).distinct().collect(Collectors.toList());

            shiftMasterCache = shiftMasterRepository.getByListImportCodes(AppContexts.user().companyId(), importCodes);

            workTypeCache = MapCache.incremental(item -> workTypeRepo.findByPK(AppContexts.user().companyId(), item));

            workTimeSettingCache = MapCache.incremental(item -> workTimeSettingRepository.findByCode(AppContexts.user().companyId(), item));

            empEmployeeCache = empEmployeeAdapter.getEmployeeIDListByCode(AppContexts.user().companyId(), employeeCodes);

            List<String> employeeIds = employeeCodes.stream().map(x -> empEmployeeCache.get(x)).collect(Collectors.toList());

            scheAuthModifyDeadlineCache = MapCache.incremental(role -> scheAuthModifyDeadlineRepository.get(AppContexts.user().companyId(), role));

            workplaceGroupCache = MapCache.
                    incremental(item -> Optional.ofNullable(workplaceGroupAdapter.getReferableEmp(item.getEmpId(), item.getDate(), DatePeriod.oneDay(item.getDate()), item.getWorkplaceGroupID())));

            workplaceGroupAllCache = MapCache.
                    incremental(item -> Optional.ofNullable(workplaceGroupAdapter.getAllReferableEmp(item.getEmpId(), item.getDate(), DatePeriod.oneDay(item.getDate()))));

            basicScheduleCache = MapCache.incremental(item -> Optional.ofNullable(basicScheduleService.checkNeededOfWorkTimeSetting(item)));

            fixedWorkSettingCache = MapCache.incremental(item -> fixedWorkSettingRepository.findByKey(AppContexts.user().companyId(), item.v()));

            flowWorkSettingCache = MapCache.incremental(item -> flowWorkSettingRepository.find(AppContexts.user().companyId(), item.v()));

            flexWorkSettingCache = MapCache.incremental(item -> flexWorkSettingRepository.find(AppContexts.user().companyId(), item.v()));

            predetemineTimeSettingCache = MapCache.incremental(item -> predetemineTimeSettingRepository.findByWorkTimeCode(AppContexts.user().companyId(), item.v()));


            List<GeneralDate> ymdList = rawData.getYmdList();
            if ( ymdList.isEmpty() ) {
                affCompanyHistByEmployeeCache = null;
                employmentPeriodCache = null;
                workCondItemWithPeriodCache = null;
                empHisCache = null;
                empLeaveWorkPeriodCache = null;
                workScheExistedCache = null;
                workScheConfirmAtrMap = null;
                return;
            }

            DatePeriod period = new DatePeriod( ymdList.get(0), ymdList.get( ymdList.size() - 1 ) );

            List<EmpEnrollPeriodImport> affCompanyHists =  comHisAdapter.getEnrollmentPeriod(employeeIds, period);
            Map<String, List<EmpEnrollPeriodImport>> data2 = affCompanyHists.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
            affCompanyHistByEmployeeCache = KeyDateHistoryCache.loaded(createEntries1(data2));

            List<EmploymentPeriodImported> listEmploymentPeriodImported = scheAdapter.getEmploymentPeriod(employeeIds, period);
            Map<String, List<EmploymentPeriodImported>> data3 = listEmploymentPeriodImported.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
            employmentPeriodCache = KeyDateHistoryCache.loaded(createEntries2(data3));

            List<WorkingConditionItemWithPeriod> listData = workingConditionRepo.getWorkingConditionItemWithPeriod(AppContexts.user().companyId(), employeeIds, period);
            Map<String, List<WorkingConditionItemWithPeriod>> data6 = listData.stream().collect(Collectors.groupingBy(item ->item.getWorkingConditionItem().getEmployeeId()));
            workCondItemWithPeriodCache = KeyDateHistoryCache.loaded(createEntries5(data6));

            List<EmployeeLeaveJobPeriodImport> listEmpPeriod = empHisAdapter.getLeaveBySpecifyingPeriod(employeeIds, period);
            Map<String, List<EmployeeLeaveJobPeriodImport>> data7 = listEmpPeriod.stream().collect(Collectors.groupingBy(item -> item.getEmpID()));
            empHisCache = KeyDateHistoryCache.loaded(createEntries7(data7));

            List<EmpLeaveWorkPeriodImport> empLeaveWorkPeriods =  leaHisAdapter.getHolidayPeriod(employeeIds, period);
            Map<String, List<EmpLeaveWorkPeriodImport>> data5 = empLeaveWorkPeriods.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
            empLeaveWorkPeriodCache = KeyDateHistoryCache.loaded(createEntries4(data5));

            workScheExistedCache = workScheduleRepository.checkExists(employeeIds, period);

            workScheConfirmAtrMap = workScheduleRepository.getConfirmedStatus(employeeIds, period);
        }

        private Map<String, List<DateHistoryCache.Entry<EmpEnrollPeriodImport>>>  createEntries1(Map<String, List<EmpEnrollPeriodImport>> data) {
            Map<String, List<DateHistoryCache.Entry<EmpEnrollPeriodImport>>> rs = new HashMap<>();
            data.forEach( (k,v) -> {
                List<DateHistoryCache.Entry<EmpEnrollPeriodImport>> s = v.stream().map(i->new DateHistoryCache.Entry<EmpEnrollPeriodImport>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
                rs.put(k, s);
            });
            return rs;
        }

        private Map<String, List<DateHistoryCache.Entry<EmploymentPeriodImported>>>  createEntries2(Map<String, List<EmploymentPeriodImported>> data) {
            Map<String, List<DateHistoryCache.Entry<EmploymentPeriodImported>>> rs = new HashMap<>();
            data.forEach( (k,v) -> {
                List<DateHistoryCache.Entry<EmploymentPeriodImported>> s = v.stream().map(i->new DateHistoryCache.Entry<EmploymentPeriodImported>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
                rs.put(k, s);
            });
            return rs;
        }

        private Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>>  createEntries5(Map<String, List<WorkingConditionItemWithPeriod>> data) {
            Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>> rs = new HashMap<>();
            data.forEach( (k,v) -> {
                List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>> s = v.stream().map(i->new DateHistoryCache.Entry<WorkingConditionItemWithPeriod>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
                rs.put(k, s);
            });
            return rs;
        }

        private Map<String, List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>>> createEntries7(Map<String, List<EmployeeLeaveJobPeriodImport>> data) {
            Map<String, List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>>> rs = new HashMap<>();
            data.forEach( (k, v) -> {
                List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>> sEntries = v.stream().map(i->new DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>(i.getDatePeriod(), i)).collect(Collectors.toList());
                rs.put(k, sEntries);
            });
            return rs;
        }

        private Map<String, List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>>>  createEntries4(Map<String, List<EmpLeaveWorkPeriodImport>> data) {
            Map<String, List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>>> rs = new HashMap<>();
            data.forEach( (k,v) -> {
                List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>> s = v.stream().map(i->new DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
                rs.put(k, s);
            });
            return rs;
        }

        @Override
        public Optional<ScheAuthModifyDeadline> getScheAuthModifyDeadline(String roleID) {
            return scheAuthModifyDeadlineCache.get(roleID);
        }

//        @Override
//        public List<String> getEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period, String workplaceGroupID) {
//            return workplaceGroupAdapter.getReferableEmp(empId, date, period, workplaceGroupID);
//        }

        @Override
        public List<String> getEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period, String workplaceGroupID) {
            return workplaceGroupCache.get(new GetEmpCanReferByWorkplaceGroupParam(date, empId, workplaceGroupID)).orElse(new ArrayList<String>());
        }

//        @Override
//        public List<String> getAllEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period) {
//            return workplaceGroupAdapter.getAllReferableEmp(empId, date, period);
//        }

        @Override
        public List<String> getAllEmpCanReferByWorkplaceGroup(String empId, GeneralDate date, DatePeriod period) {
            return workplaceGroupAllCache.get(new GetAllEmpCanReferByWorkplaceGroupParam(date, empId)).orElse(new ArrayList<String>());
        }

        @Override
        public List<String> sortEmployee(List<String> employeeIdList, EmployeeSearchCallSystemType systemType,
                Integer sortOrderNo, GeneralDate date, Integer nameType) {
            return regulInfoEmpAdap.sortEmployee(
                    AppContexts.user().companyId(),
                    employeeIdList,
                    systemType.value,
                    sortOrderNo,
                    nameType,
                    GeneralDateTime.fromString(date.toString() + " " + "00:00", "yyyy/MM/dd HH:mm"));
        }

        @Override
        public String getRoleID() {
            return AppContexts.user().roles().forAttendance();
        }

        @Override
        public List<String> searchEmployee(RegulationInfoEmpQuery regulationInfoEmpQuery, String roleId) {
            EmployeeSearchQueryDto query = EmployeeSearchQueryDto.builder()
                    .baseDate(GeneralDateTime.fromString(regulationInfoEmpQuery.getBaseDate().toString() + " " + "00:00", "yyyy/MM/dd HH:mm"))
                    .referenceRange(regulationInfoEmpQuery.getReferenceRange().value)
                    .systemType(regulationInfoEmpQuery.getSystemType().value)
                    .filterByWorkplace(regulationInfoEmpQuery.getFilterByWorkplace() == null ? false : regulationInfoEmpQuery.getFilterByWorkplace())
                    .workplaceCodes(regulationInfoEmpQuery.getWorkplaceIds())
                    .filterByEmployment(false)
                    .employmentCodes(new ArrayList<String>())
                    .filterByDepartment(false)
                    .departmentCodes(new ArrayList<String>())
                    .filterByClassification(false)
                    .classificationCodes(new ArrayList<String>())
                    .filterByJobTitle(false)
                    .jobTitleCodes(new ArrayList<String>())
                    .filterByWorktype(false)
                    .worktypeCodes(new ArrayList<String>())
                    .filterByClosure(false)
                    .closureIds(new ArrayList<Integer>())
                    .periodStart( GeneralDateTime.fromString(regulationInfoEmpQuery.getPeriodStart() + " 00:00", "yyyy/MM/dd HH:mm") )
                    .periodEnd( GeneralDateTime.fromString(regulationInfoEmpQuery.getPeriodEnd() + " 00:00", "yyyy/MM/dd HH:mm") )
                    .includeIncumbents(true)
                    .includeWorkersOnLeave(true)
                    .includeOccupancy(true)
                    .includeRetirees(false)
                    .includeAreOnLoan(false)
                    .includeGoingOnLoan(false)
                    .retireStart(GeneralDateTime.now())
                    .retireEnd(GeneralDateTime.now())
                    .sortOrderNo(null)
                    .nameType(null)

                    .build();
            List<RegulationInfoEmployeeExport> data = regulInfoEmpPub.find(query);
            List<String> resultList = data.stream().map(item -> item.getEmployeeId())
                    .collect(Collectors.toList());
            return resultList;
        }

        @Override
        public Optional<EmpEnrollPeriodImport> getAffCompanyHistByEmployee(String employeeId, GeneralDate date) {
            Optional<EmpEnrollPeriodImport> data = affCompanyHistByEmployeeCache.get(employeeId, date);
            return data;
        }

        @Override
        public Optional<WorkingConditionItem> getBySidAndStandardDate(String employeeId, GeneralDate date) {
            Optional<WorkingConditionItemWithPeriod> data = workCondItemWithPeriodCache.get(employeeId, date);
            return data.isPresent() ? Optional.of(data.get().getWorkingConditionItem()) : Optional.empty();
        }

        @Override
        public Optional<EmployeeLeaveJobPeriodImport> getByDatePeriod(String employeeId, GeneralDate date) {
//            val result = empHisAdapter.getLeaveBySpecifyingPeriod(Arrays.asList(employeeId),
//                    new DatePeriod(date, date));
//            if (result.isEmpty())
//                return Optional.empty();
//            return Optional.of(result.get(0));
            Optional<EmployeeLeaveJobPeriodImport> data =empHisCache.get(employeeId, date);
            return data;
        }

        @Override
        public Optional<EmpLeaveWorkPeriodImport> specAndGetHolidayPeriod(String employeeId, GeneralDate date) {
            Optional<EmpLeaveWorkPeriodImport> data = empLeaveWorkPeriodCache.get(employeeId, date);
            return data;
        }

        @Override
        public Optional<EmploymentPeriodImported> getEmploymentHistory(String employeeId, GeneralDate date) {
            Optional<EmploymentPeriodImported> data = employmentPeriodCache.get(employeeId, date);
            return data;
        }

        @Override
        public Optional<WorkType> getWorkType(String workTypeCd) {
            return workTypeCache.get(workTypeCd);
        }

        @Override
        public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
            return workTimeSettingCache.get(workTimeCode);
        }

        @Override
        public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
            return basicScheduleCache.get(workTypeCode).orElse(null);
        }

        @Override
        public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
            return fixedWorkSettingCache.get(code).orElse(null);
        }

        @Override
        public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
            return flowWorkSettingCache.get(code).orElse(null);
        }

        @Override
        public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
            return flexWorkSettingCache.get(code).orElse(null);
        }

        @Override
        public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
            return predetemineTimeSettingCache.get(wktmCd).orElse(null);
        }

        @Override
        public String getOwnAttendanceRoleId() {
            return AppContexts.user().roles().forAttendance();
        }

        @Override
        public EmployeeId getOwnEmployeeId() {
            return new EmployeeId(AppContexts.user().employeeId());
        }

        @Override
        public Map<String, String> getEmployeeIds(List<String> employeeCodes) {
            return empEmployeeCache;
        }

        @Override
        public List<ShiftMaster> getShiftMasters(List<ShiftMasterImportCode> importCodes) {
            return shiftMasterCache;
        }

        @Override
        public boolean isWorkScheduleExisted(EmployeeId employeeId, GeneralDate ymd) {
            return workScheExistedCache.get(new EmployeeAndYmd(employeeId.v(), ymd));
        }

        @Override
        public boolean isWorkScheduleComfirmed(EmployeeId employeeId, GeneralDate ymd) {
        	return workScheConfirmAtrMap.get(new EmployeeAndYmd(employeeId.v(), ymd)).equals(ConfirmedATR.CONFIRMED) ? true : false;
        }

		@Override
		public List<EmpOrganizationImport> getEmpOrganization(GeneralDate baseDate, List<String> lstEmpId) {
			return empAffiliationInforAdapter.getEmpOrganization(baseDate, lstEmpId);
		}
    }

    @AllArgsConstructor
    @Getter
    public static class GetEmpCanReferByWorkplaceGroupParam {
        GeneralDate date;
        String empId;
        String workplaceGroupID;
    }

    @AllArgsConstructor
    @Getter
    public static class GetAllEmpCanReferByWorkplaceGroupParam {
        GeneralDate date;
        String empId;
    }
}
