package nts.uk.ctx.at.schedule.pubimp.schedule.workschedule.employeeinfo;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.EmployeeRank;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.EmployeeRankRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.GetEmRankInforService;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.Rank;
import nts.uk.ctx.at.schedule.dom.employeeinfo.rank.RankRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeam;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.BelongScheduleTeamRepository;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.ScheduleTeam;
import nts.uk.ctx.at.schedule.dom.employeeinfo.scheduleteam.ScheduleTeamRepository;
import nts.uk.ctx.at.schedule.dom.workschedule.displaysetting.DisplayControlPersonalCondition;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.employeeinfo.EmpRankInfoExport;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.employeeinfo.EmpRankInfoPub;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpMedicalWorkFormHisItem;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.EmpMedicalWorkStyleHistoryRepository;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassification;
import nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle.NurseClassificationRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class EmpRankInfoPubImp implements EmpRankInfoPub {
    @Inject
    private BelongScheduleTeamRepository belongScheduleTeamRepo;
    @Inject
    private ScheduleTeamRepository scheduleTeamRepo;
    @Inject
    private EmployeeRankRepository employeeRankRepo;
    @Inject
    private RankRepository rankRepo;
    @Inject
    private EmpMedicalWorkStyleHistoryRepository empMedicalWorkStyleHistoryRepo;
    @Inject
    private NurseClassificationRepository nurseClassificationRepo;

    /**
     * @param listEmpId
     * @return List<社員所属チーム情報Exported>
     */
    @Override
    public List<EmpRankInfoExport> get(List<String> listEmpId) {
        RequireImplDispControlPerCond requireImplDispControlPerCond = new RequireImplDispControlPerCond(
                belongScheduleTeamRepo, scheduleTeamRepo, employeeRankRepo, rankRepo, empMedicalWorkStyleHistoryRepo,
                nurseClassificationRepo);
        //return DS_社員ランク情報を取得する.取得する( require, 社員リスト ):
        //map 社員ランク情報Export( $.社員ID, $.ランクコード, $.ランク記号)
        return GetEmRankInforService.get(requireImplDispControlPerCond, listEmpId).stream().map(x -> new EmpRankInfoExport(
                x.getEmpId(),
                x.getRankCode().isPresent() ? x.getRankCode().get().v() : "",
                x.getRankSymbol().isPresent() ? x.getRankSymbol().get().v() : ""
        )).collect(Collectors.toList());
    }

    @AllArgsConstructor
    private static class RequireImplDispControlPerCond implements DisplayControlPersonalCondition.Require {
        @Inject
        private BelongScheduleTeamRepository belongScheduleTeamRepo;
        @Inject
        private ScheduleTeamRepository scheduleTeamRepo;
        @Inject
        private EmployeeRankRepository employeeRankRepo;
        @Inject
        private RankRepository rankRepo;
        @Inject
        private EmpMedicalWorkStyleHistoryRepository empMedicalWorkStyleHistoryRepo;
        @Inject
        private NurseClassificationRepository nurseClassificationRepo;

        @Override
        public List<BelongScheduleTeam> get(List<String> lstEmpId) {
            List<BelongScheduleTeam> data = belongScheduleTeamRepo.get(AppContexts.user().companyId(), lstEmpId);
            return data;
        }

        @Override
        public List<ScheduleTeam> getAllSchedule(List<String> listWKPGRPID) {
            List<ScheduleTeam> data = scheduleTeamRepo.getAllSchedule(AppContexts.user().companyId(), listWKPGRPID);
            return data;
        }

        @Override
        public List<EmployeeRank> getAll(List<String> lstSID) {
            List<EmployeeRank> data = employeeRankRepo.getAll(lstSID);
            return data;
        }

        @Override
        public List<Rank> getListRank() {
            List<Rank> data = rankRepo.getListRank(AppContexts.user().companyId());
            return data;
        }

        @Override
        public List<EmpMedicalWorkFormHisItem> getEmpClassifications(List<String> listEmp, GeneralDate referenceDate) {
            List<EmpMedicalWorkFormHisItem> data = empMedicalWorkStyleHistoryRepo.get(listEmp, referenceDate);
            return data;
        }

        @Override
        public List<NurseClassification> getListCompanyNurseCategory() {
            List<NurseClassification> data = nurseClassificationRepo
                    .getListCompanyNurseCategory(AppContexts.user().companyId());
            return data;
        }
    }
}