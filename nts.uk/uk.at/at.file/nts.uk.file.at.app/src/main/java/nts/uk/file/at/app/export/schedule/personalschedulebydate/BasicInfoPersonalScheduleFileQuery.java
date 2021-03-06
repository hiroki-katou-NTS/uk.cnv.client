package nts.uk.file.at.app.export.schedule.personalschedulebydate;

import lombok.AllArgsConstructor;
import nts.arc.primitive.PrimitiveValueBase;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationQueryDtoImport;
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
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.DisplayInfoOrganization;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.WorkplaceInfo;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupImport;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.AffWorkplaceAdapter;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.WorkplaceExportServiceAdapter;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.dto.BasicInfoPersonalScheduleDto;
import nts.uk.shr.com.company.CompanyAdapter;
import nts.uk.shr.com.company.CompanyInfor;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ??????????????????
 * UKDesign.UniversalK.??????.KSU_??????????????????.KSU003_??????????????????????????????(?????????).D??????????????????.???????????????OCD.??????????????????.??????????????????
 */
@Stateless
public class BasicInfoPersonalScheduleFileQuery {
    @Inject
    private CompanyAdapter company;

    @Inject
    private EmployeeInformationAdapter employeeInfoAdapter;

    @Inject
    private WorkplaceGroupAdapter groupAdapter;

    @Inject
    private WorkplaceExportServiceAdapter serviceAdapter;

    @Inject
    private AffWorkplaceAdapter wplAdapter;

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

    public BasicInfoPersonalScheduleDto getInfo(int orgUnit, String orgId, GeneralDate baseDate, List<String> sortedEmployeeIds) {
        String companyId = AppContexts.user().companyId();

        // 1. [RQ622]??????ID?????????????????????????????????
        CompanyInfor companyInfo = company.getCurrentCompany().orElseGet(() -> {
            throw new RuntimeException("System Error: Company Info");
        });

        // 2. ????????????????????????????????????????????????????????????(??????????????????ID): Input.????????????.??????????????????????????????
        // 3. ????????????????????????????????????????????????(??????ID) : Input.????????????.??????????????????
        TargetOrgIdenInfor targetOrgIdenInfor = orgUnit == TargetOrganizationUnit.WORKPLACE.value
                ? TargetOrgIdenInfor.creatIdentifiWorkplace(orgId)
                : TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(orgId);

        // 4. ????????????????????????????????????(Require, ?????????): output ?????????????????????
        DisplayInfoOrganization displayInfoOrganization = targetOrgIdenInfor.getDisplayInfor(new TargetOrgIdenInfor.Require() {
            @Override
            public List<WorkplaceGroupImport> getSpecifyingWorkplaceGroupId(List<String> workplacegroupId) {
                return groupAdapter.getbySpecWorkplaceGroupID(workplacegroupId);
            }

            @Override
            public List<WorkplaceInfo> getWorkplaceInforFromWkpIds(List<String> listWorkplaceId, GeneralDate baseDate) {
                return serviceAdapter.getWorkplaceInforByWkpIds(companyId, listWorkplaceId, baseDate).stream()
                        .map(mapper -> new WorkplaceInfo(mapper.getWorkplaceId(), Optional.ofNullable(mapper.getWorkplaceCode()), Optional.ofNullable(mapper.getWorkplaceName()), Optional.ofNullable(mapper.getWorkplaceExternalCode()),
                                Optional.ofNullable(mapper.getWorkplaceGenericName()), Optional.ofNullable(mapper.getWorkplaceDisplayName()), Optional.ofNullable(mapper.getHierarchyCode()))).collect(Collectors.toList());
            }

            @Override
            public List<String> getWKPID(String WKPGRPID) {
                return wplAdapter.getWKPID(companyId, WKPGRPID);
            }
        }, baseDate);

        // 5. ??????????????? : ????????????(Require, ?????????, ????????????????????????) : param: require,Input.????????????Input.????????????
        DateInfoImpl dateInfoRequire = new DateInfoImpl(workplaceSpecificDateRepo, companySpecificDateRepo, workplaceEventRepo,
                companyEventRepo, publicHolidayRepo, specificDateItemRepo);
        DateInformation dateInfo = DateInformation.create(dateInfoRequire, baseDate, targetOrgIdenInfor);

        // 6. Create ???????????????????????????: false, false, false, false, false, false
        // 7. call <<Public>> ??????????????????????????????
        List<EmployeeInformationImport> employeeInfoList = this.employeeInfoAdapter
                .getEmployeeInfo(new EmployeeInformationQueryDtoImport(sortedEmployeeIds, baseDate, false, false, false,
                        false, false, false));
        List<EmployeeInformationImport> sortedEmployeeInfoList = new ArrayList<>();
        for (int i = 0; i < sortedEmployeeIds.size(); i++) {
            String employee = sortedEmployeeIds.get(i);
            Optional<EmployeeInformationImport> empInfo = employeeInfoList.stream().filter(x -> x.getEmployeeId().equals(employee)).findFirst();
            empInfo.ifPresent(sortedEmployeeInfoList::add);
        }


        return new BasicInfoPersonalScheduleDto(companyInfo, displayInfoOrganization, dateInfo, sortedEmployeeInfoList);
    }

    @AllArgsConstructor
    public static class DateInfoImpl implements DateInformation.Require {
        private WorkplaceSpecificDateRepository workplaceSpecificDateRepo;

        private CompanySpecificDateRepository companySpecificDateRepo;

        private WorkplaceEventRepository workplaceEventRepo;

        private CompanyEventRepository companyEventRepo;

        private PublicHolidayRepository publicHolidayRepo;

        private SpecificDateItemRepository specificDateItemRepo;

        private final String companyId = AppContexts.user().companyId();

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
    }
}
