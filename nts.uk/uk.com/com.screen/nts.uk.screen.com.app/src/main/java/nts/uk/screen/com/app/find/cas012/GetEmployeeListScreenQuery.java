package nts.uk.screen.com.app.find.cas012;


import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.pub.employee.ResultRequest600Export;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoDtoExport;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoPub;
import nts.uk.ctx.bs.employee.pub.jobtitle.AffJobTitleBasicExport;
import nts.uk.ctx.bs.employee.pub.jobtitle.EmployeeJobHistExport;
import nts.uk.ctx.bs.employee.pub.jobtitle.SyJobTitlePub;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.dto.EmployeeBasicInfoDto;
import nts.uk.ctx.sys.auth.app.query.GetEmployeeIDFromUserIDQuery;
import nts.uk.ctx.sys.auth.dom.GetPersonalEmployeeInfoByUserIdService;
import nts.uk.ctx.sys.auth.dom.adapter.person.EmployeeBasicInforAuthImport;
import nts.uk.ctx.sys.auth.dom.adapter.person.PersonAdapter;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ScreenQuery: 社員リストを取得する
 * UKDesign.UniversalK.共通.CAS_権限設定.CAS012_管理者ロールの付与.B:個人選択.B：メニュー別OCD.社員リストを取得する.社員リストを取得する
 * @author : chinh.hm
 */

@Stateless
public class GetEmployeeListScreenQuery {
    @Inject
    private WorkplacePub workplacePub;

    @Inject
    private SyJobTitlePub syJobTitlePub;

    @Inject
    private EmployeeInfoPub employeeInfoPub;
    @Inject
    private SyEmployeePub syEmployeePub;
    @Inject
    private UserRepository userRepo;

    @Inject
    private PersonAdapter personAdapter;

    public List<EmployeeBasicInfoDto> getListEmployee(String cid){
        val rs = new ArrayList<nts.uk.ctx.sys.auth.app.find.grant.roleindividual.dto.EmployeeBasicInfoDto>();
        val baseDate = GeneralDate.today();

        // 社員情報リストを取得する
        Map<String, EmployeeInfoDtoExport> employeeInfoDtoExportMap = employeeInfoPub.getEmployeesAtWorkByBaseDate(cid, baseDate)
                .stream().collect(Collectors.toMap(EmployeeInfoDtoExport::getEmployeeId, i -> i));
        val siDs = new ArrayList<>(employeeInfoDtoExportMap.keySet());
        val mapSid = personAdapter.listPersonInfor(siDs)
                .stream().collect(Collectors.toMap(EmployeeBasicInforAuthImport::getEmployeeId, i -> i));
        List<String> listPid = mapSid.values().stream().map(EmployeeBasicInforAuthImport::getPid).collect(Collectors.toList());
        val mapUidPerId = userRepo.getListUserByListAsID(listPid).stream()
                .filter(User::hasAssociatedPersonID)
                .collect(Collectors.toMap(e->e.getAssociatedPersonID().get(), e->e));
        for (val sid : siDs) {
            val pid = mapSid.get(sid) != null ? mapSid.get(sid).getPid() : null;
            val wpInf = workplacePub.findBySidNew(cid, sid, baseDate);
            Optional<AffJobTitleBasicExport> jobHistExportMap = syJobTitlePub.getBySidAndBaseDate(sid, baseDate);
            rs.add(new EmployeeBasicInfoDto(
                    pid,
                    mapUidPerId.get(pid).getUserID(),
                    sid,
                    employeeInfoDtoExportMap.get(sid).getEmployeeCode(),
                    employeeInfoDtoExportMap.get(sid).getPerName(),
                    jobHistExportMap.isPresent() ? jobHistExportMap.get().getJobTitleId() : null,
                    jobHistExportMap.isPresent() ? jobHistExportMap.get().getJobTitleCode() : null,
                    jobHistExportMap.isPresent() ? jobHistExportMap.get().getJobTitleName() : null,
                    wpInf.isPresent() ? wpInf.get().getWorkplaceId() : null,
                    wpInf.isPresent() ? wpInf.get().getWorkplaceCode() : null,
                    wpInf.isPresent() ? wpInf.get().getWorkplaceName() : null,
                    wpInf.isPresent() ? wpInf.get().getWkpDisplayName() : null
            ));
        }
        return rs.stream().sorted(Comparator.comparing(e -> e.employeeCode)).collect(Collectors.toList());
    }
}