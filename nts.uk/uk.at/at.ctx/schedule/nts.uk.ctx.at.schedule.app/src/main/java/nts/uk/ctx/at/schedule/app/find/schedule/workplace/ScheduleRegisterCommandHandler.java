package nts.uk.ctx.at.schedule.app.find.schedule.workplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import lombok.val;
import nts.arc.layer.app.cache.DateHistoryCache;
import nts.arc.layer.app.cache.KeyDateHistoryCache;
import nts.arc.layer.app.cache.MapCache;
import nts.arc.layer.app.command.AsyncCommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.data.TaskDataSetter;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.app.command.schedule.workplace.ScheduleRegisterCommand;
import nts.uk.ctx.at.schedule.dom.adapter.classification.SyClassificationAdapter;
import nts.uk.ctx.at.schedule.dom.schedule.createworkschedule.createschedulecommon.correctworkschedule.CorrectWorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.CreateWorkScheduleByImportCode;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ResultOfRegisteringWorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.SClsHistImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSyEmploymentImport;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.SharedAffJobTitleHisImport;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.SharedAffJobtitleHisAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.SharedAffWorkPlaceHisAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.SharedAffWorkPlaceHisImport;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.employee.BusinessTypeOfEmployee;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.employee.repository.BusinessTypeEmpService;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemWithPeriod;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisScheduleAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
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
import nts.uk.shr.com.context.AppContexts;

/**
 * @author anhnm
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ScheduleRegisterCommandHandler extends AsyncCommandHandler<ScheduleRegisterCommand> {

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
    private EmploymentHisScheduleAdapter employmentHisScheduleAdapter;

    @Inject
    private SharedAffJobtitleHisAdapter sharedAffJobtitleHisAdapter;

    @Inject
    private SharedAffWorkPlaceHisAdapter sharedAffWorkPlaceHisAdapter;

    @Inject
    private SyClassificationAdapter syClassificationAdapter;

    @Inject
    private BusinessTypeEmpService businessTypeEmpService;

    @Inject
    private WorkingConditionRepository workingConditionRepo;

    @Inject
    private WorkScheduleRepository workScheduleRepo;

    @Inject
    private ShiftMasterRepository shiftMasterRepository;

    @Inject
    private CorrectWorkSchedule correctWorkSchedule;

    @Inject
    private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;

    @Inject
    private EmpEmployeeAdapter empEmployeeAdapter;
    
    @Inject
    private ManagedParallelWithContext parallel;

//    public List<RegisterWorkScheduleOutput> register(ScheduleRegisterCommand command) {
//        List<RegisterWorkScheduleOutput> outputs = new ArrayList<RegisterWorkScheduleOutput>();
//        List<String> importCodes = command.getTargets().stream().map(x -> x.getImportCode()).distinct().collect(Collectors.toList());
//        List<String> employeeList = command.getTargets().stream().map(x -> x.getEmployeeId()).distinct().collect(Collectors.toList());
//        List<GeneralDate> dates = command.getTargets().stream()
//                .map(x -> GeneralDate.fromString(x.getDate(), "yyyy/MM/dd"))
//                .distinct().sorted().collect(Collectors.toList());
//        DatePeriod period = dates.size() > 0 ? new DatePeriod(dates.get(0), dates.get(dates.size() - 1)) : null;
//        RequireImp requireImp = new RequireImp(importCodes, employeeList, period);
//        ScheduleRegister sr =  command.toDomain();
//        
//        Map<String, List<ScheduleRegisterTarget>> mapTargetBySid = sr.getTargets().stream().
//                collect(Collectors.groupingBy(target -> target.getEmployeeId()));
//        List<List<ScheduleRegisterTarget>> listTargetBySid = mapTargetBySid.entrySet().stream().map(x -> x.getValue())
//                .collect(Collectors.toList());
//        List<String> employeeIds = new ArrayList<String>();
//        this.parallel.forEach(listTargetBySid, targets -> {
//            // 1.1: 作る(Require, 社員ID, 年月日, シフトマスタ取り込みコード, boolean)
//            List<ResultOfRegisteringWorkSchedule> resultOfRegisteringWorkSchedule = 
//                    targets
//                    .stream()
//                    .map(x -> CreateWorkScheduleByImportCode.create(
//                            requireImp, 
//                            x.getEmployeeId(), 
//                            x.getDate(), 
//                            x.getImportCode(), 
//                            sr.isOverWrite())
//                            ).collect(Collectors.toList());
//            
//            // 1.2: <call>
////            if (outputs.size() > 0) {
////                return;
////            }
//            resultOfRegisteringWorkSchedule.stream().filter(result -> !result.isHasError()).collect(Collectors.toList())
//            .forEach(result -> {
//                Optional<AtomTask> atomTaskOpt = result.getAtomTask();
//                
//                if (atomTaskOpt.isPresent()) {
//                    atomTaskOpt.get().run();
//                }
//            });
//            
//            // 2: List<勤務予定の登録処理結果> : anyMatch $.エラーがあるか == true 社員IDを指定して社員を取得する(List<社員ID>)
//            resultOfRegisteringWorkSchedule.stream().forEach(x -> {
//                if (x.isHasError()) {
//                    employeeIds.add(x.getErrorInformation().get(0).getEmployeeId());
//                }
//            });
//            List<EmployeeImport> employeeImports = empEmployeeAdapter.findByEmpId(employeeIds);
//            employeeImports.stream().forEach(x -> {
//                List<ResultOfRegisteringWorkSchedule> results = resultOfRegisteringWorkSchedule.stream()
//                        .filter(y -> y.isHasError() ? y.getErrorInformation().get(0).getEmployeeId().equals(x.getEmployeeId()) : y.isHasError()).collect(Collectors.toList());
//                results.forEach(result -> {
//                    outputs.add(new RegisterWorkScheduleOutput(
//                            x.getEmployeeCode(),
//                            x.getEmployeeName(), 
//                            result.getErrorInformation().get(0).getDate().toString("yyyy/MM/dd"),
//                            result.getErrorInformation().get(0).getAttendanceItemId().isPresent() ? result.getErrorInformation().get(0).getAttendanceItemId().get() : 0,
//                                    result.getErrorInformation().get(0).getErrorMessage()));
//                });
//            });
//        });
//        
//
//
//        return outputs;
//    }
    


    @Override
    protected void handle(CommandHandlerContext<ScheduleRegisterCommand> context) {
        ScheduleRegisterCommand command = context.getCommand();
        val asyncTask = context.asAsync();
        TaskDataSetter setter = asyncTask.getDataSetter();
        
        List<String> importCodes = command.getTargets().stream().map(x -> x.getImportCode()).distinct().collect(Collectors.toList());
        // loop:社員ID in 社員IDリスト
        List<String> employeeList = command.getTargets().stream().map(x -> x.getEmployeeId()).distinct().collect(Collectors.toList());
        // loop:年月日 in 年月日リスト
        List<GeneralDate> dates = command.getTargets().stream()
                .map(x -> GeneralDate.fromString(x.getDate(), "yyyy/MM/dd"))
                .distinct().sorted().collect(Collectors.toList());
        DatePeriod period = dates.size() > 0 ? new DatePeriod(dates.get(0), dates.get(dates.size() - 1)) : null;
        RequireImp requireImp = new RequireImp(importCodes, employeeList, period);
        
        // 1.1: 作る(Require, 社員ID, 年月日, シフトマスタ取り込みコード, boolean)
        ScheduleRegisterDto sr =  command.toDomain();
        List<ResultOfRegisteringWorkSchedule> resultOfRegisteringWorkSchedule = 
                sr.getTargets()
                  .stream()
                  .map(x -> CreateWorkScheduleByImportCode.create(
                           requireImp, 
                            x.getEmployeeId(), 
                            x.getDate(), 
                            x.getImportCode(), 
                            sr.isOverWrite())
                  ).collect(Collectors.toList());
        
        // 1.2: call()
        boolean isRegister = false;
        for (ResultOfRegisteringWorkSchedule result : resultOfRegisteringWorkSchedule) {
            Optional<AtomTask> atomTaskOpt = result.getAtomTask();
            
            if (atomTaskOpt.isPresent()) {
                isRegister = true;
                atomTaskOpt.get().run();
            }
        }
        
        // 2: List<勤務予定の登録処理結果> : anyMatch $.エラーがあるか == true 社員IDを指定して社員を取得する(List<社員ID>)
        List<String> employeeIds = new ArrayList<String>();
        resultOfRegisteringWorkSchedule.stream().forEach(x -> {
            if (x.isHasError()) {
                employeeIds.add(x.getErrorInformation().get(0).getEmployeeId());
            }
        });
        
        boolean isError = false;
        if (employeeIds.size() > 0) {
            isError = true;
        }
        List<EmployeeImport> employeeImports = empEmployeeAdapter.findByEmpId(employeeIds);
        for (int i = 0; i < employeeImports.size(); i++) {
            EmployeeImport employeeImport = employeeImports.get(i);
            List<ResultOfRegisteringWorkSchedule> results = resultOfRegisteringWorkSchedule.stream()
                    .filter(y -> y.isHasError() ? 
                            y.getErrorInformation().get(0).getEmployeeId()
                            .equals(employeeImport.getEmployeeId()) : y.isHasError())
                    .collect(Collectors.toList());
            for (int j = 0; j < results.size(); j++) {
                JsonObject value = Json.createObjectBuilder()
                        .add("employeeCode", employeeImport.getEmployeeCode())
                        .add("employeename", employeeImport.getEmployeeName())
                        .add("date", results.get(j).getErrorInformation().get(0).getDate().toString("yyyy/MM/dd"))
                        .add("errorItemId", results.get(j).getErrorInformation().get(0).getAttendanceItemId().isPresent() ? results.get(j).getErrorInformation().get(0).getAttendanceItemId().get() : 0)
                        .add("errorMessage", results.get(j).getErrorInformation().get(0).getErrorMessage())
                        .build();
                setter.setData("ERROR" + employeeImport.getEmployeeCode() + j, value);
            }
        }
        setter.setData("STATUS_REGISTER", isRegister);
        setter.setData("STATUS_ERROR", isError);
    }
    
    private class RequireImp implements CreateWorkScheduleByImportCode.Require {
        
        private final MapCache<String, WorkType> workTypeCache;
        
        private final MapCache<String, WorkTimeSetting> workTimeSettingCache;
        
        private final MapCache<String, SetupType> basicScheduleCache;
        
        private final MapCache<String, FixedWorkSetting> fixedWorkSettingCache;
        
        private final MapCache<String, FlowWorkSetting> flowWorkSettingCache;
        
        private final MapCache<String, FlexWorkSetting> flexWorkSettingCache;
        
        private final MapCache<String, PredetemineTimeSetting> predetemineTimeSettingCache;
        
        private final KeyDateHistoryCache<String, SharedSyEmploymentImport> employmentHisScheduleCache;
        
        private final DateHistoryCache<SharedAffWorkPlaceHisImport> sharedAffWorkPlaceHisCache;
        
        private final DateHistoryCache<SClsHistImport> syClassificationCache;
        
        private final KeyDateHistoryCache<String, WorkingConditionItemWithPeriod> workingConditionCache;
        
        private final Map<String, ShiftMaster> shiftMasterCache;
        
        public RequireImp(List<String> importCodes, List<String> employeeIds, DatePeriod period) {
            List<ShiftMaster> shiftMasters = shiftMasterRepository.getByListImportCodes(AppContexts.user().companyId(), importCodes);
            shiftMasterCache = shiftMasters.stream()
                    .collect(Collectors.toMap(
                            shiftMaster -> shiftMaster.getImportCode().get().v(), 
                            shiftMaster -> shiftMaster, 
                            (value1, value2) -> {
                                return value1;
                            }));
            
            workTypeCache = MapCache.incremental(workTypeCd -> workTypeRepo.findByPK(AppContexts.user().companyId(), workTypeCd));
            
            workTimeSettingCache = MapCache.incremental(workTimeCode -> workTimeSettingRepository.findByCode(AppContexts.user().companyId(), workTimeCode));
            
            basicScheduleCache = MapCache.incremental(workTypeCode -> Optional.ofNullable(basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode)));
            
            fixedWorkSettingCache = MapCache.incremental(code -> fixedWorkSettingRepository.findByKey(AppContexts.user().companyId(), code));
                    
            flowWorkSettingCache = MapCache.incremental(code -> flowWorkSettingRepository.find(AppContexts.user().companyId(), code));
            
            flexWorkSettingCache = MapCache.incremental(code -> flexWorkSettingRepository.find(AppContexts.user().companyId(), code));
            
            predetemineTimeSettingCache = MapCache.incremental(code -> predetemineTimeSettingRepository.findByWorkTimeCode(AppContexts.user().companyId(), code));
            
            List<EmploymentPeriodImported> listEmpHist = employmentHisScheduleAdapter
                    .getEmploymentPeriod(employeeIds, period);
            Map<String, List<SharedSyEmploymentImport>> data = listEmpHist.stream()
                    .map(x -> new SharedSyEmploymentImport(x.getEmpID(), x.getEmploymentCd(), "",x.getDatePeriod()))
                    .collect(Collectors.groupingBy(item -> item.getEmployeeId()));
            employmentHisScheduleCache = KeyDateHistoryCache.loaded(createEntries1(data));
            
            sharedAffWorkPlaceHisCache = new DateHistoryCache<SharedAffWorkPlaceHisImport>();
            
            syClassificationCache = new DateHistoryCache<SClsHistImport>();
            
            List<WorkingConditionItemWithPeriod> workingConditionPeriod = workingConditionRepo.getWorkingConditionItemWithPeriod(AppContexts.user().companyId(), employeeIds, period);
            Map<String, List<WorkingConditionItemWithPeriod>> data2 = workingConditionPeriod.stream()
                    .collect(Collectors.groupingBy(item -> item.getWorkingConditionItem().getEmployeeId()));
            workingConditionCache = KeyDateHistoryCache.loaded(createEntries2(data2));
        }
        
        private Map<String, List<DateHistoryCache.Entry<SharedSyEmploymentImport>>>  createEntries1(Map<String, List<SharedSyEmploymentImport>> data) {
            Map<String, List<DateHistoryCache.Entry<SharedSyEmploymentImport>>> rs = new HashMap<>();
            data.forEach( (k,v) -> {
                List<SharedSyEmploymentImport> listwoDuplicate = v.stream()
                        .collect(Collectors.toMap(SharedSyEmploymentImport::getPeriod, Function.identity(), (o1, o2) -> o1))
                        .values().stream()
                        .collect(Collectors.toList());
                List<DateHistoryCache.Entry<SharedSyEmploymentImport>> s = listwoDuplicate.stream().map(i->new DateHistoryCache.Entry<SharedSyEmploymentImport>(i.getPeriod(),i)).collect(Collectors.toList()) ;
                rs.putIfAbsent(k, s);
            });
            return rs;
        }
        
        private Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>>  createEntries2(Map<String, List<WorkingConditionItemWithPeriod>> data) {
            Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>> rs = new HashMap<>();
            data.forEach( (k,v) -> {
                List<WorkingConditionItemWithPeriod> listwoDuplicate = v.stream()
                        .collect(Collectors.toMap(WorkingConditionItemWithPeriod::getDatePeriod, Function.identity(), (o1, o2) -> o1))
                        .values().stream()
                        .collect(Collectors.toList());
                List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>> s = listwoDuplicate.stream().map(i->new DateHistoryCache.Entry<WorkingConditionItemWithPeriod>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
                rs.putIfAbsent(k, s);
            });
            return rs;
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
            return fixedWorkSettingCache.get(code.v()).orElse(null);
        }

        @Override
        public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
            return flowWorkSettingCache.get(code.v()).orElse(null);
        }

        @Override
        public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
            return flexWorkSettingCache.get(code.v()).orElse(null);
        }

        @Override
        public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
            return predetemineTimeSettingCache.get(wktmCd.v()).orElse(null);
        }

        @Override
        public SharedSyEmploymentImport getAffEmploymentHistory(String employeeId, GeneralDate standardDate) {
//            List<EmploymentPeriodImported> listEmpHist = employmentHisScheduleAdapter
//                    .getEmploymentPeriod(Arrays.asList(employeeId), new DatePeriod(standardDate, standardDate));
//            if(listEmpHist.isEmpty())
//                return null;
//            return new SharedSyEmploymentImport(listEmpHist.get(0).getEmpID(), listEmpHist.get(0).getEmploymentCd(), "",
//                    listEmpHist.get(0).getDatePeriod());
            return employmentHisScheduleCache.get(employeeId, standardDate).orElse(null);
        }

        @Override
        public SharedAffJobTitleHisImport getAffJobTitleHistory(String employeeId, GeneralDate standardDate) {
            List<SharedAffJobTitleHisImport> listAffJobTitleHis =  sharedAffJobtitleHisAdapter.findAffJobTitleHisByListSid(Arrays.asList(employeeId), standardDate);
            if(listAffJobTitleHis.isEmpty())
                return null;
            return listAffJobTitleHis.get(0);
        }

        @Override
        public SharedAffWorkPlaceHisImport getAffWorkplaceHistory(String employeeId, GeneralDate standardDate) {
//            Optional<SharedAffWorkPlaceHisImport> rs = sharedAffWorkPlaceHisAdapter.getAffWorkPlaceHis(employeeId, standardDate);
//            return rs.isPresent() ? rs.get() : null;
            return sharedAffWorkPlaceHisCache.get(standardDate, e -> 
                sharedAffWorkPlaceHisAdapter.getAffWorkPlaceHis(employeeId, standardDate)
                .map(h -> DateHistoryCache.Entry.of(h.getDateRange(), h))).orElse(null);
        }

        @Override
        public SClsHistImport getClassificationHistory(String employeeId, GeneralDate standardDate) {
//            Optional<SClsHistImported> imported = syClassificationAdapter.findSClsHistBySid(AppContexts.user().companyId(), employeeId, standardDate);
//            if (!imported.isPresent()) {
//                return null;
//            }
//            return new SClsHistImport(imported.get().getPeriod(), imported.get().getEmployeeId(),
//                    imported.get().getClassificationCode(), imported.get().getClassificationName());
            return syClassificationCache.get(standardDate, e -> 
                syClassificationAdapter.findSClsHistBySid(AppContexts.user().companyId(), employeeId, standardDate)
                .map(h -> DateHistoryCache.Entry.of(h.getPeriod(), 
                        new SClsHistImport(h.getPeriod(), h.getEmployeeId(), h.getClassificationCode(), h.getClassificationName()))))
                    .orElse(null);
        }

        @Override
        public Optional<BusinessTypeOfEmployee> getBusinessType(String employeeId, GeneralDate standardDate) {
            List<BusinessTypeOfEmployee> list = businessTypeEmpService.getData(employeeId, standardDate);
            if (list.isEmpty())
                return Optional.empty();
            return Optional.of(list.get(0));
        }

        @Override
        public Optional<WorkingConditionItem> getWorkingConditionHistory(String employeeId, GeneralDate standardDate) {
//            Optional<WorkingConditionItem> rs = workingConditionRepo.getWorkingConditionItemByEmpIDAndDate(AppContexts.user().companyId(), standardDate, employeeId);
//            return rs;
            Optional<WorkingConditionItemWithPeriod> workingConditionIPeriod = workingConditionCache.get(employeeId, standardDate);
            return workingConditionIPeriod.isPresent() ? Optional.of(workingConditionIPeriod.get().getWorkingConditionItem()) : Optional.empty();
        }

        @Override
        public String getLoginEmployeeId() {
            return AppContexts.user().employeeId();
        }

        @Override
        public boolean isWorkScheduleExisted(String employeeId, GeneralDate date) {
            return workScheduleRepo.checkExists(Arrays.asList(employeeId), new DatePeriod(date, date))
                    .entrySet()
                    .stream()
                    .filter(x -> x.getKey().getEmployeeId().equals(employeeId) && x.getKey().getYmd().equals(date))
                    .findFirst()
                    .flatMap(x -> Optional.ofNullable(x.getValue()))
                    .orElse(false);
        }

        @Override
        public Optional<ShiftMaster> getShiftMaster(ShiftMasterImportCode importCode) {
            return Optional.ofNullable(shiftMasterCache.get(importCode.v()));
        }

        @Override
        public WorkSchedule correctWorkSchedule(WorkSchedule workSchedule) {
            WorkSchedule rs = correctWorkSchedule.correctWorkSchedule(workSchedule, workSchedule.getEmployeeID(), workSchedule.getYmd());
            return rs;
        }

        @Override
        public void insertWorkSchedule(WorkSchedule workSchedule) {
            workScheduleRepo.insert(workSchedule);
        }

        @Override
        public void updateWorkSchedule(WorkSchedule workSchedule) {
            workScheduleRepo.update(workSchedule);
        }

        @Override
        public void registerTemporaryData(String employeeId, GeneralDate date) {
            interimRemainDataMngRegisterDateChange.registerDateChange(AppContexts.user().companyId(), employeeId, Arrays.asList(date));
        }
        
    }
}
