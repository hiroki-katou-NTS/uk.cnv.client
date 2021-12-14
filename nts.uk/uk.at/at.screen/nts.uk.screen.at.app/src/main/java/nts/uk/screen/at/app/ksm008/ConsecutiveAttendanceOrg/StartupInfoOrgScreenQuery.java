package nts.uk.screen.at.app.ksm008.ConsecutiveAttendanceOrg;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.app.query.schedule.alarm.consecutivework.consecutiveattendance.ConsecutiveAttendanceOrgQuery;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.DisplayInfoOrganization;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetTargetIdentifiInforService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.WorkplaceInfo;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupImport;
import nts.uk.ctx.bs.employee.dom.workplace.group.AffWorkplaceGroupRespository;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceExportService;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.ctx.bs.employee.pub.workplace.export.EmpOrganizationPub;
import nts.uk.ctx.bs.employee.pub.workplace.workplacegroup.EmpOrganizationExport;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Screen H : 初期起動
 */
@Stateless
public class StartupInfoOrgScreenQuery {
    @Inject
    private ConsecutiveAttendanceOrgQuery consecutiveAttendanceOrgQuery;

    @Inject
    private EmpOrganizationPub empOrganizationPub;

    @Inject
    private WorkplaceGroupAdapter workplaceGroupAdapter;

    @Inject
    private WorkplaceExportService workplaceExportService;

    @Inject
    private AffWorkplaceGroupRespository affWorkplaceGroupRepo;

    /**
     * 初期起動の情報を取得する
     */
    public ConsecutiveAttendanceOrgDto getStartupInfoOrg() {
        //1. 組織情報を取得する()
        OrgInfoDto orgInfoDto = getOrgInfo();

        //2. 取得する
        Integer maxConsDays = consecutiveAttendanceOrgQuery.getMaxConsDays(orgInfoDto.getUnit(), orgInfoDto.getWorkplaceId(), orgInfoDto.getWorkplaceGroupId());

        return new ConsecutiveAttendanceOrgDto(
                orgInfoDto.getUnit(),
                orgInfoDto.getWorkplaceId(),
                orgInfoDto.getWorkplaceGroupId(),
                orgInfoDto.getCode(),
                orgInfoDto.getDisplayName(),
                maxConsDays
        );
    }

    /**
     * 組織情報を取得する
     *
     * @return 対象組織情報, 組織の表示情報
     */
    public OrgInfoDto getOrgInfo() {
        //1: 取得する(Require, 年月日, 社員ID): 対象組織識別情報
        RequireImpl require = new RequireImpl(empOrganizationPub);
        GeneralDate systemDate = GeneralDate.today();
        String employeeId = AppContexts.user().employeeId();
        TargetOrgIdenInfor targeOrg = GetTargetIdentifiInforService.get(require, systemDate, employeeId);

        //2: 組織の表示情報を取得する(Require, 年月日): 組織の表示情報
        RequireWorkPlaceImpl requireWorkPlace = new RequireWorkPlaceImpl(workplaceGroupAdapter, workplaceExportService, affWorkplaceGroupRepo);
        DisplayInfoOrganization displayInfoOrganization = targeOrg.getDisplayInfor(requireWorkPlace, systemDate);

        return new OrgInfoDto(
                targeOrg.getUnit().value,
                targeOrg.getWorkplaceId().orElse(null),
                targeOrg.getWorkplaceGroupId().orElse(null),
                displayInfoOrganization.getCode(),
                displayInfoOrganization.getDisplayName()
        );
    }

    @AllArgsConstructor
    private static class RequireImpl implements GetTargetIdentifiInforService.Require {

        private EmpOrganizationPub empOrganizationPub;

        @Override
        public List<EmpOrganizationImport> getEmpOrganization(GeneralDate referenceDate, List<String> listEmpId) {

            List<EmpOrganizationExport> exports = empOrganizationPub.getEmpOrganiztion(referenceDate, listEmpId);
            List<EmpOrganizationImport> data = exports.stream().map(i -> {
                return new EmpOrganizationImport(new EmployeeId(i.getEmpId()), i.getBusinessName(), i.getEmpCd(), i.getWorkplaceId(), i.getWorkplaceGroupId());
            }).collect(Collectors.toList());
            return data;
        }
    }

    @AllArgsConstructor
    private static class RequireWorkPlaceImpl implements TargetOrgIdenInfor.Require {

        private WorkplaceGroupAdapter workplaceGroupAdapter;
        private WorkplaceExportService workplaceExportService;
        private AffWorkplaceGroupRespository affWorkplaceGroupRepo;

        @Override
        public List<WorkplaceGroupImport> getSpecifyingWorkplaceGroupId(List<String> workplacegroupId) {
            List<WorkplaceGroupImport> data = workplaceGroupAdapter.getbySpecWorkplaceGroupID(workplacegroupId);
            return data;
        }

        @Override
        public List<WorkplaceInfo> getWorkplaceInforFromWkpIds(List<String> listWorkplaceId, GeneralDate baseDate) {
            List<WorkplaceInforParam> data1 = workplaceExportService
                    .getWorkplaceInforFromWkpIds(AppContexts.user().companyId(), listWorkplaceId, baseDate);
            if (data1.isEmpty()) {
                return new ArrayList<WorkplaceInfo>();
            }
            List<WorkplaceInfo> data = data1.stream().map(item -> {
                return new WorkplaceInfo(item.getWorkplaceId(),
                        Optional.ofNullable(item.getWorkplaceCode()),
                        Optional.ofNullable(item.getWorkplaceName()),
                        Optional.ofNullable(item.getHierarchyCode()),
                        Optional.ofNullable(item.getGenericName()),
                        Optional.ofNullable(item.getDisplayName()),
                        Optional.ofNullable(item.getExternalCode()));
            }).collect(Collectors.toList());
            return data;
        }

        @Override
        public List<String> getWKPID(String WKPGRPID) {
            List<String> data = affWorkplaceGroupRepo.getWKPID(AppContexts.user().companyId(), WKPGRPID);
            return data;
        }
    }
}
