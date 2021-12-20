package nts.uk.screen.at.app.ksm008.query.j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.app.query.schedule.alarm.consecutivework.consecutiveattendance.ConsecutiveAttendanceOrgQuery;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.consecutivework.consecutiveattendance.MaxDaysOfConsAttOrgRepository;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.consecutivework.consecutiveworktime.MaxDaysOfContinuousWorkTimeOrganization;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.consecutivework.consecutiveworktime.MaxDaysOfContinuousWorkTimeOrganizationRepository;
import nts.uk.ctx.at.schedulealarm.app.query.alarmcheck.AlarmCheckConditionsQuery;
import nts.uk.ctx.at.schedulealarm.app.query.alarmcheck.AlarmCheckConditionsQueryDto;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.DisplayInfoOrganization;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetTargetIdentifiInforService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.WorkplaceInfo;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpAffiliationInforAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupImport;
import nts.uk.ctx.bs.employee.dom.workplace.group.AffWorkplaceGroupRespository;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceExportService;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.screen.at.app.ksm008.query.i.MaxDaysOfContinuousWorkTimeDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * Screen KSM008I : 初期起動の情報を取得する
 *
 * @author rafiqul.islam
 */
@Stateless
public class Ksm008JStartupOrgInfoScreenQuery {

    @Inject
    private ConsecutiveAttendanceOrgQuery consecutiveAttendanceOrgQuery;

    @Inject
    private WorkplaceGroupAdapter workplaceGroupAdapter;

    @Inject
    private WorkplaceExportService workplaceExportService;

    @Inject
    private AffWorkplaceGroupRespository affWorkplaceGroupRepo;

    @Inject
    MaxDaysOfConsAttOrgRepository maxDaysOfConsAttOrgRepository;

    @Inject
    MaxDaysOfContinuousWorkTimeOrganizationRepository maxDaysOfContinuousWorkTimeOrganizationRepository;

    @Inject
    private AlarmCheckConditionsQuery alarmCheckConditionsQuery;
    
    @Inject
   	private EmpAffiliationInforAdapter empAffiliationInforAdapter;

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
     * @return Ksm008JStartOrgInfoDto
     * @author rafiqul.islam
     */

    public Ksm008JStartOrgInfoDto getOrgInfo() {
        //1: 取得する(Require, 年月日, 社員ID): 対象組織識別情報
        Ksm008JStartupOrgInfoScreenQuery.RequireImpl require = new Ksm008JStartupOrgInfoScreenQuery.RequireImpl();
        GeneralDate systemDate = GeneralDate.today();
        String employeeId = AppContexts.user().employeeId();
        TargetOrgIdenInfor targeOrg = GetTargetIdentifiInforService.get(require, systemDate, employeeId);

        //2: 組織の表示情報を取得する(Require, 年月日): 組織の表示情報
        Ksm008JStartupOrgInfoScreenQuery.RequireWorkPlaceImpl requireWorkPlace = new Ksm008JStartupOrgInfoScreenQuery.RequireWorkPlaceImpl(workplaceGroupAdapter, workplaceExportService, affWorkplaceGroupRepo);
        DisplayInfoOrganization displayInfoOrganization = targeOrg.getDisplayInfor(requireWorkPlace, systemDate);
        return new Ksm008JStartOrgInfoDto(
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
     * 組織の就業時間帯の連続勤務できる上限日数リストを取得する
     *
     * @param Ksm008GetWkListRequestParam
     * @return List<MaxDayOfWorkTimeCompanyDto>
     * @author rafiqul.islam
     */

    public List<MaxDaysOfContinuousWorkTimeDto> getWorkTimeList(Ksm008GetWkListRequestParam requestParam) {
        TargetOrgIdenInfor targetOrgIdenInfor = requestParam.getWorkPlaceUnit() == TargetOrganizationUnit.WORKPLACE.value
                ? TargetOrgIdenInfor.creatIdentifiWorkplace(requestParam.getWorkPlaceId())
                : TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(requestParam.getWorkPlaceGroup());
        return getWorkTimeListLocal(targetOrgIdenInfor);
    }

    /**
     * 組織の就業時間帯の連続勤務できる上限日数リストを取得する
     *
     * @param TargetOrgIdenInfor
     * @return List<MaxDayOfWorkTimeCompanyDto>
     * @author rafiqul.islam
     */

    private List<MaxDaysOfContinuousWorkTimeDto> getWorkTimeListLocal(TargetOrgIdenInfor targeOrg) {
        List<MaxDaysOfContinuousWorkTimeOrganization> workTimeOrganizations = maxDaysOfContinuousWorkTimeOrganizationRepository.getAll(AppContexts.user().companyId(), targeOrg);
        List<MaxDaysOfContinuousWorkTimeDto> workTimeList = workTimeOrganizations
                .stream()
                .map(wrkTime -> new MaxDaysOfContinuousWorkTimeDto(
                        wrkTime.getCode().v(),
                        wrkTime.getName().v().trim(),
                        wrkTime.getMaxDaysContiWorktime().getNumberOfDays().v()
                )).collect(Collectors.toList());
        Collections.sort(workTimeList);
        return workTimeList;
    }


	private class RequireImpl implements GetTargetIdentifiInforService.Require {

		@Override
		public List<EmpOrganizationImport> getEmpOrganization(GeneralDate referenceDate, List<String> listEmpId) {

			return empAffiliationInforAdapter.getEmpOrganization(referenceDate, listEmpId);
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
            List<WorkplaceInfo> data = data1
                    .stream()
                    .map(item -> {
                        return new WorkplaceInfo(item.getWorkplaceId(),
                                Optional.ofNullable(item.getWorkplaceCode()),
                                Optional.ofNullable(item.getWorkplaceName()),
                                Optional.ofNullable(item.getHierarchyCode()),
                                Optional.ofNullable(item.getGenericName()),
                                Optional.ofNullable(item.getDisplayName()),
                                Optional.ofNullable(item.getExternalCode()));
                    })
                    .collect(Collectors.toList());
            return data;
        }

        @Override
        public List<String> getWKPID(String WKPGRPID) {
            List<String> data = affWorkplaceGroupRepo.getWKPID(AppContexts.user().companyId(), WKPGRPID);
            return data;
        }
    }
}
