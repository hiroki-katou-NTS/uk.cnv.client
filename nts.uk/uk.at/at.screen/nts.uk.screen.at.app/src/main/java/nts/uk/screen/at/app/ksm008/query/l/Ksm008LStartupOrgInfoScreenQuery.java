package nts.uk.screen.at.app.ksm008.query.l;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.consecutivework.limitworktime.MaxDayOfWorkTimeOrganization;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.consecutivework.limitworktime.MaxDayOfWorkTimeOrganizationRepo;
import nts.uk.ctx.at.schedulealarm.app.query.alarmcheck.AlarmCheckConditionsQuery;
import nts.uk.ctx.at.schedulealarm.app.query.alarmcheck.AlarmCheckConditionsQueryDto;
import nts.uk.ctx.at.schedulealarm.dom.alarmcheck.AlarmCheckConditionSchedule;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.DisplayInfoOrganization;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetTargetIdentifiInforService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.WorkplaceInfo;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupImport;
import nts.uk.ctx.bs.employee.dom.workplace.group.AffWorkplaceGroupRespository;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceExportService;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.ctx.bs.employee.pub.workplace.export.EmpOrganizationPub;
import nts.uk.ctx.bs.employee.pub.workplace.workplacegroup.EmpOrganizationExport;
import nts.uk.screen.at.app.ksm008.query.j.Ksm008GetWkListRequestParam;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Screen KSM008L : 初期起動の情報を取得する
 *
 * @author rafiqul.islam
 */
@Stateless
public class Ksm008LStartupOrgInfoScreenQuery {

    @Inject
    private EmpOrganizationPub empOrganizationPub;

    @Inject
    private WorkplaceGroupAdapter workplaceGroupAdapter;

    @Inject
    private WorkplaceExportService workplaceExportService;

    @Inject
    private AffWorkplaceGroupRespository affWorkplaceGroupRepo;

    @Inject
    MaxDayOfWorkTimeOrganizationRepo maxDayOfWorkTimeOrganizationRepo;

    @Inject
    private AlarmCheckConditionsQuery alarmCheckConditionsQuery;

    /**
     * コードと名称と説明を取得する
     * @param code コード
     * @return 勤務予定のアラームチェック条件
     */
    public AlarmCheckConditionsQueryDto getAlarmCheckConSche(String code) {
        return alarmCheckConditionsQuery.getCodeNameDescription(code);
    }

    /**
     * Get startup info
     *
     * @return Ksm008LStartOrgInfoDto
     * @author rafiqul.islam
     */

    public Ksm008LStartOrgInfoDto getOrgInfo() {
        //1: 取得する(Require, 年月日, 社員ID): 対象組織識別情報
        Ksm008LStartupOrgInfoScreenQuery.RequireImpl require = new Ksm008LStartupOrgInfoScreenQuery.RequireImpl(empOrganizationPub);
        GeneralDate systemDate = GeneralDate.today();
        String employeeId = AppContexts.user().employeeId();
        TargetOrgIdenInfor targeOrg = GetTargetIdentifiInforService.get(require, systemDate, employeeId);
        //2: 組織の表示情報を取得する(Require, 年月日): 組織の表示情報
        Ksm008LStartupOrgInfoScreenQuery.RequireWorkPlaceImpl requireWorkPlace = new Ksm008LStartupOrgInfoScreenQuery.RequireWorkPlaceImpl(workplaceGroupAdapter, workplaceExportService, affWorkplaceGroupRepo);
        DisplayInfoOrganization displayInfoOrganization = targeOrg.getDisplayInfor(requireWorkPlace, systemDate);
        return new Ksm008LStartOrgInfoDto(
                targeOrg.getUnit().value,
                targeOrg.getWorkplaceId().orElse(null),
                targeOrg.getWorkplaceGroupId().orElse(null),
                targeOrg.getTargetId(),
                displayInfoOrganization.getCode(),
                displayInfoOrganization.getName(),
                this.getWorkTimeListLocal(targeOrg)
        );
    }

    /**
     * 組織の就業時間帯の上限一覧を選択する
     *
     * @param Ksm008GetWkListRequestParam
     * @return List<MaxDayOfWorkTimeCompanyDto>
     * @author rafiqul.islam
     */

    public List<MaxDaysOfWorkTimeDto> getWorkTimeList(Ksm008GetWkListRequestParam requestParam) {
        TargetOrgIdenInfor targetOrgIdenInfor = requestParam.getWorkPlaceUnit() == TargetOrganizationUnit.WORKPLACE.value
                ? TargetOrgIdenInfor.creatIdentifiWorkplace(requestParam.getWorkPlaceId())
                : TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(requestParam.getWorkPlaceGroup());
        return getWorkTimeListLocal(targetOrgIdenInfor);
    }

    /**
     * 組織の就業時間帯の上限一覧を選択する
     *
     * @param TargetOrgIdenInfor
     * @return List<MaxDaysOfWorkTimeDto>
     * @author rafiqul.islam
     */

    private List<MaxDaysOfWorkTimeDto> getWorkTimeListLocal(TargetOrgIdenInfor targeOrg) {
        List<MaxDayOfWorkTimeOrganization> workTimeOrganizations = maxDayOfWorkTimeOrganizationRepo.getAll(AppContexts.user().companyId(), targeOrg);
        if (workTimeOrganizations.isEmpty()) {
            return Collections.emptyList();
        }
        List<MaxDaysOfWorkTimeDto> workTimeList = workTimeOrganizations
                .stream()
                .map(wrkTime -> new MaxDaysOfWorkTimeDto(
                        wrkTime.getCode().v(),
                        wrkTime.getName().v().trim(),
                        wrkTime.getMaxDayOfWorkTime().getMaxDay().v()
                )).collect(Collectors.toList());
        Collections.sort(workTimeList);
        return workTimeList;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class RequireImpl implements GetTargetIdentifiInforService.Require {

        @Inject
        private EmpOrganizationPub empOrganizationPub;

        @Override
        public List<EmpOrganizationImport> getEmpOrganization(GeneralDate referenceDate, List<String> listEmpId) {
            List<EmpOrganizationExport> exports = empOrganizationPub.getEmpOrganiztion(referenceDate, listEmpId);
            if (exports.isEmpty()) {
                return Collections.emptyList();
            }
            List<EmpOrganizationImport> data = exports
                    .stream()
                    .map(i -> {
                        return new EmpOrganizationImport(new EmployeeId(i.getEmpId()), i.getBusinessName(), i.getEmpCd(), i.getWorkplaceId(), i.getWorkplaceGroupId());
                    })
                    .collect(Collectors.toList());
            return data;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class RequireWorkPlaceImpl implements TargetOrgIdenInfor.Require {

        @Inject
        private WorkplaceGroupAdapter workplaceGroupAdapter;
        @Inject
        private WorkplaceExportService workplaceExportService;
        @Inject
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