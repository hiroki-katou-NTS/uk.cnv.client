package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.app.cache.DateHistoryCache;
import nts.arc.primitive.PrimitiveValueBase;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkplaceImport;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.ScWorkplaceAdapter;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.event.CompanyEvent;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.event.CompanyEventRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.event.WorkplaceEvent;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.event.WorkplaceEventRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHoliday;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHolidayRepository;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.company.CompanySpecificDateItem;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.company.CompanySpecificDateRepository;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.item.SpecificDateItem;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.item.SpecificDateItemRepository;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.primitives.SpecificDateItemNo;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.workplace.WorkplaceSpecificDateItem;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.workplace.WorkplaceSpecificDateRepository;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.GetLegalWorkTimeOfEmployeeService;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.LegalWorkTimeOfEmployee;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSetting;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonAndWeekStatutoryTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyFlexStatutoryLaborTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyStatutoryWorkingHours;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSet;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeComRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeEmpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeShaRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeWkpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeComRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeEmpRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeShaRepo;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeWkpRepo;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisScheduleAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto.BasicInformationDto;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto.WorkPlaceHistoryDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.GetScheduleOfWorkInfo002;
import nts.uk.screen.at.app.query.ksu.ksu002.a.GetWorkActualOfWorkInfo002;
import nts.uk.screen.at.app.query.ksu.ksu002.a.KSU002Finder;
import nts.uk.shr.com.company.CompanyAdapter;
import nts.uk.shr.com.context.AppContexts;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 基本情報取得
 */
@Stateless
public class BasicInformationQuery {
    @Inject
    private CompanyAdapter company;
    @Inject
    private WorkplaceSpecificDateRepository workplaceSpecificDateRepo;
    @Inject
    private CompanySpecificDateRepository companySpecificDateRepo;
    @Inject
    private WorkplaceEventRepository workplaceEventRepo;
    @Inject
    private CompanyEventRepository companyEventRepo;
    @Inject
    private PublicHolidayRepository publicHolidayRepo;
    @Inject
    private SpecificDateItemRepository specificDateItemRepo;

    @Inject
    private ScWorkplaceAdapter scWorkplaceAdapter;

    @Inject
    private WorkplaceAdapter workplaceAdapter;
    @Inject
    private AffWorkplaceHistoryRepository workplaceHistoryRepository;

    @Inject
    private AffWorkplaceHistoryItemRepository affWorkplaceHistoryItemRepository;

    @Inject
    private WorkingConditionItemRepository workingConditionItemRepository;

    @Inject
    private EmploymentHisScheduleAdapter employmentHisScheduleAdapter;

    @Inject
    private GetWorkActualOfWorkInfo002 getWorkRecord;

    @Inject
    private GetScheduleOfWorkInfo002 getScheduleOfWorkInfo002;

    @Inject
    private UsageUnitSettingRepository usageUnitSettingRepository;

    @Inject
    private AffWorkplaceAdapter affWorkplaceAdapter;

    @Inject
    private MonthlyWorkTimeSetRepo monthlyWorkTimeSet;

    @Inject
    private RegularLaborTimeComRepo regularLaborTimeComRepo;

    @Inject
    private DeforLaborTimeComRepo deforLaborTimeComRepo;

    @Inject
    private RegularLaborTimeWkpRepo regularLaborTimeWkpRepo;

    @Inject
    private DeforLaborTimeWkpRepo deforLaborTimeWkpRepo;

    @Inject
    private RegularLaborTimeEmpRepo regularLaborTimeEmpRepo;

    @Inject
    private DeforLaborTimeEmpRepo deforLaborTimeEmpRepo;

    @Inject
    private RegularLaborTimeShaRepo regularLaborTimeShaRepo;

    @Inject
    private DeforLaborTimeShaRepo deforLaborTimeShaRepo;

    @Inject
    private KSU002Finder kSU002Finder;


    /**
     * 取得する
     *
     * @param isTotalDisplay
     * @param period
     * @return BasicInformationDto
     */
    public <T> BasicInformationDto<T> get(boolean isTotalDisplay, DatePeriod period) {
        String companyId = AppContexts.user().companyId();
        String employeeId = AppContexts.user().employeeId();
        // 基本情報取得
        // [RQ622]会社IDから会社情報を取得する
        String companyName = company.getCurrentCompany().orElseGet(() -> {
            throw new RuntimeException("System Error: Company Info");
        }).getCompanyName();

        //[No.650]社員が所属している職場を取得する
        String workplaceId = scWorkplaceAdapter.getAffWkpHistItemByEmpDate(employeeId, period.end()).getWorkplaceId();
        List<String> workplaceIds = new ArrayList<>();
        if (!workplaceId.isEmpty()) {
            workplaceIds.add(workplaceId);
        }
        //[No.560]職場IDから職場の情報をすべて取得する
        List<WorkplaceImport> workPlaceInforExports = workplaceAdapter.findWkpByWkpIdList(companyId, period.end(), workplaceIds);

        //社員（List）と期間から職場履歴を取得する
        List<WorkPlaceHistoryDto> workPlaceHitory = getWorkPlaceHitory(employeeId, period);
        final DateHistoryCache<WorkPlaceHistoryDto> historyCache;
        historyCache = new DateHistoryCache<>(workPlaceHitory.stream()
                .map(h -> DateHistoryCache.Entry.of(h.getDatePeriod(), h))
                .collect(Collectors.toList()));
        List<DateInformation> dateInformationList = new ArrayList<>();
        // Loop：年月日 in Input.対象期間
        period.datesBetween().forEach(date -> {
            val historyData = historyCache.get(date);
            String workPplaceId = historyData.isPresent() ? historyData.get().getHistoryItem().getWorkplaceId() : "";
            if (historyData.isPresent() && StringUtils.isNotEmpty(workPplaceId)) {
                TargetOrgIdenInfor targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(workPplaceId);
                // 作成する(Require, 年月日, 対象組織識別情報)
                DateInformation information = DateInformation.create(new DateInformation.Require() {
                    @Override
                    public List<WorkplaceSpecificDateItem> getWorkplaceSpecByDate(String workplaceId, GeneralDate specificDate) {
                        return workplaceSpecificDateRepo.getWorkplaceSpecByDate(workplaceId, specificDate);
                    }

                    @Override
                    public List<CompanySpecificDateItem> getComSpecByDate(GeneralDate specificDate) {
                        return companySpecificDateRepo.getComSpecByDate(companyId, specificDate);
                    }

                    @Override
                    public Optional<WorkplaceEvent> findByPK(String workplaceId, GeneralDate date) {
                        return workplaceEventRepo.findByPK(workplaceId, date);
                    }

                    @Override
                    public Optional<CompanyEvent> findCompanyEventByPK(GeneralDate date) {
                        return companyEventRepo.findByPK(companyId, date);
                    }

                    @Override
                    public Optional<PublicHoliday> getHolidaysByDate(GeneralDate date) {
                        return publicHolidayRepo.getHolidaysByDate(companyId, date);
                    }

                    @Override
                    public List<SpecificDateItem> getSpecifiDateByListCode(List<SpecificDateItemNo> lstSpecificDateItemNo) {
                        if (lstSpecificDateItemNo.isEmpty()) return new ArrayList<>();
                        List<Integer> _lstSpecificDateItemNo = lstSpecificDateItemNo.stream().map(PrimitiveValueBase::v).collect(Collectors.toList());
                        return specificDateItemRepo.getSpecifiDateByListCode(companyId, _lstSpecificDateItemNo);
                    }
                }, date, targetOrgIdenInfor);
                dateInformationList.add(information);
            }
        });
        Optional<LegalWorkTimeOfEmployee> legalWorkTimeOfEmployee = Optional.empty();
        //Input.週合計判定 == true
        if (isTotalDisplay) {
            //Optional<社員の法定労働時間>
            legalWorkTimeOfEmployee = GetLegalWorkTimeOfEmployeeService.get(new LegalWorkTimeRequireImpl(companyId), employeeId, period);
        }

        return new BasicInformationDto(
                companyName,
                workPlaceInforExports.get(0),
                dateInformationList,
                legalWorkTimeOfEmployee
        );
    }

    /**
     * 社員（List）と期間から職場履歴を取得する
     *
     * @param employeeId
     * @param period
     */
    private List<WorkPlaceHistoryDto> getWorkPlaceHitory(String employeeId, DatePeriod period) {
        Optional<AffWorkplaceHistory> affWorkplaceHistoryOpt = workplaceHistoryRepository.findByEmployeesWithPeriod(Arrays.asList(employeeId), period).stream().findFirst();
        AffWorkplaceHistory affWorkplaceHistory = null;
        List<WorkPlaceHistoryDto> historyDtoList = new ArrayList<>();
        if (affWorkplaceHistoryOpt.isPresent()) {
            affWorkplaceHistory = affWorkplaceHistoryOpt.get();
            val historyItems = affWorkplaceHistory.getHistoryItems().stream().filter(
                    item -> {
                        val itemPriod = item.span();
                        return (itemPriod.start().beforeOrEquals(period.end()) && period.start().beforeOrEquals(itemPriod.end()));
                    }
            ).collect(Collectors.toList());
            List<nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem> affWorkplaceHistoryItems = affWorkplaceHistoryItemRepository.findByHistIds(historyItems.stream().map(x -> x.identifier()).collect(Collectors.toList()));
            historyItems.forEach(item -> {
                val history = affWorkplaceHistoryItems.stream().filter(x -> x.getHistoryId().equals(item.identifier())).findFirst();
                if (history.isPresent()) {
                    historyDtoList.add(new WorkPlaceHistoryDto(
                            item.span(),
                            history.get()
                    ));
                }
            });
        }
        return historyDtoList;
    }

    @RequiredArgsConstructor
    public class LegalWorkTimeRequireImpl implements GetLegalWorkTimeOfEmployeeService.Require {

        private final String companyId;

        @Override
        public Optional<WorkingConditionItem> getHistoryItemBySidAndBaseDate(String sid, GeneralDate baseDate) {
            return workingConditionItemRepository.getBySidAndStandardDate(sid, baseDate);
        }

        @Override
        public List<EmploymentPeriodImported> getEmploymentHistories(String sid, DatePeriod datePeriod) {
            List<String> listEmpId = new ArrayList<String>();
            listEmpId.add(sid);
            return employmentHisScheduleAdapter.getEmploymentPeriod(listEmpId, datePeriod);
        }

        @Override
        public MonthlyFlexStatutoryLaborTime flexMonAndWeekStatutoryTime(YearMonth ym, String employmentCd, String employeeId, GeneralDate baseDate) {
            return MonthlyStatutoryWorkingHours.flexMonAndWeekStatutoryTime(new Require1(), new CacheCarrier(), companyId, employmentCd, employeeId, baseDate, ym);
        }

        @Override
        public Optional<MonAndWeekStatutoryTime> monAndWeekStatutoryTime(YearMonth ym, String employmentCd, String employeeId, GeneralDate baseDate, WorkingSystem workingSystem) {
            return MonthlyStatutoryWorkingHours.monAndWeekStatutoryTime(new Require2(), new CacheCarrier(), companyId, employmentCd, employeeId, baseDate, ym, workingSystem);
        }

    }

    @RequiredArgsConstructor
    private class Require1 implements MonthlyStatutoryWorkingHours.RequireM1 {
        @Override
        public Optional<MonthlyWorkTimeSetSha> monthlyWorkTimeSetSha(String cid, String sid, MonthlyWorkTimeSet.LaborWorkTypeAttr laborAttr, YearMonth ym) {
            return monthlyWorkTimeSet.findEmployee(cid, sid, laborAttr, ym);
        }

        @Override
        public Optional<MonthlyWorkTimeSetEmp> monthlyWorkTimeSetEmp(String cid, String empCode, MonthlyWorkTimeSet.LaborWorkTypeAttr laborAttr, YearMonth ym) {
            return monthlyWorkTimeSet.findEmployment(cid, empCode, laborAttr, ym);
        }

        @Override
        public Optional<MonthlyWorkTimeSetCom> monthlyWorkTimeSetCom(String cid, MonthlyWorkTimeSet.LaborWorkTypeAttr laborAttr, YearMonth ym) {
            return monthlyWorkTimeSet.findCompany(cid, laborAttr, ym);
        }

        @Override
        public Optional<MonthlyWorkTimeSetWkp> monthlyWorkTimeSetWkp(String cid, String workplaceId, MonthlyWorkTimeSet.LaborWorkTypeAttr laborAttr, YearMonth ym) {
            return monthlyWorkTimeSet.findWorkplace(cid, workplaceId, laborAttr, ym);
        }

        @Override
        public List<String> getCanUseWorkplaceForEmp(CacheCarrier cacheCarrier, String companyId, String employeeId, GeneralDate baseDate) {
            return affWorkplaceAdapter.findAffiliatedWorkPlaceIdsToRootRequire(cacheCarrier, companyId, employeeId, baseDate);
        }

        @Override
        public Optional<UsageUnitSetting> usageUnitSetting(String companyId) {
            return usageUnitSettingRepository.findByCompany(companyId);
        }
    }


    @RequiredArgsConstructor
    private class Require2 implements MonthlyStatutoryWorkingHours.RequireM4 {
        @Override
        public Optional<RegularLaborTimeCom> regularLaborTimeByCompany(String companyId) {
            return regularLaborTimeComRepo.find(companyId);
        }

        @Override
        public Optional<DeforLaborTimeCom> deforLaborTimeByCompany(String companyId) {
            return deforLaborTimeComRepo.find(companyId);
        }

        @Override
        public Optional<RegularLaborTimeWkp> regularLaborTimeByWorkplace(String cid, String wkpId) {
            return regularLaborTimeWkpRepo.find(cid, wkpId);
        }

        @Override
        public Optional<DeforLaborTimeWkp> deforLaborTimeByWorkplace(String cid, String wkpId) {
            return deforLaborTimeWkpRepo.find(cid, wkpId);
        }

        @Override
        public Optional<RegularLaborTimeEmp> regularLaborTimeByEmployment(String cid, String employmentCode) {
            return regularLaborTimeEmpRepo.findById(cid, employmentCode);
        }

        @Override
        public Optional<DeforLaborTimeEmp> deforLaborTimeByEmployment(String cid, String employmentCode) {
            return deforLaborTimeEmpRepo.find(cid, employmentCode);
        }

        @Override
        public Optional<RegularLaborTimeSha> regularLaborTimeByEmployee(String Cid, String EmpId) {
            return regularLaborTimeShaRepo.find(Cid, EmpId);
        }

        @Override
        public Optional<DeforLaborTimeSha> deforLaborTimeByEmployee(String cid, String empId) {
            return deforLaborTimeShaRepo.find(cid, empId);
        }

        @Override
        public List<String> getCanUseWorkplaceForEmp(CacheCarrier cacheCarrier, String companyId, String employeeId, GeneralDate baseDate) {
            return affWorkplaceAdapter.findAffiliatedWorkPlaceIdsToRootRequire(cacheCarrier, companyId, employeeId, baseDate);
        }

        @Override
        public Optional<MonthlyWorkTimeSetWkp> monthlyWorkTimeSetWkp(String cid, String workplaceId, MonthlyWorkTimeSet.LaborWorkTypeAttr laborAttr, YearMonth ym) {
            return monthlyWorkTimeSet.findWorkplace(cid, workplaceId, laborAttr, ym);
        }

        @Override
        public Optional<MonthlyWorkTimeSetSha> monthlyWorkTimeSetSha(String cid, String sid, MonthlyWorkTimeSet.LaborWorkTypeAttr laborAttr, YearMonth ym) {
            return monthlyWorkTimeSet.findEmployee(cid, sid, laborAttr, ym);
        }

        @Override
        public Optional<MonthlyWorkTimeSetEmp> monthlyWorkTimeSetEmp(String cid, String empCode, MonthlyWorkTimeSet.LaborWorkTypeAttr laborAttr, YearMonth ym) {
            return monthlyWorkTimeSet.findEmployment(cid, empCode, laborAttr, ym);
        }

        @Override
        public Optional<MonthlyWorkTimeSetCom> monthlyWorkTimeSetCom(String cid, MonthlyWorkTimeSet.LaborWorkTypeAttr laborAttr, YearMonth ym) {
            return monthlyWorkTimeSet.findCompany(cid, laborAttr, ym);
        }

        @Override
        public Optional<UsageUnitSetting> usageUnitSetting(String companyId) {
            return usageUnitSettingRepository.findByCompany(companyId);
        }
    }
}
