package nts.uk.screen.com.app.find.cas014;


import nts.uk.ctx.bs.employee.pub.employee.EmployeeInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.ctx.sys.auth.app.query.GetListOfIndividualGrantsForRoleSetQuery;
import nts.uk.ctx.sys.auth.dom.grant.rolesetperson.RoleSetGrantedPerson;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.service.RoleSetService;
import nts.uk.ctx.sys.portal.dom.adapter.roleset.RoleSetDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UKDesign.UniversalK.共通.CAS_権限設定.CAS014_ロールセットの付与.B: 特別社員の付与設定.メニュー別OCD.初期表示を取得する
 * ScreenQuery : 初期表示を取得する
 * @author chinh.hm
 */
@Stateless
public class GetDataInitCas014ScreenQuery {

    @Inject
    private GetListOfIndividualGrantsForRoleSetQuery listOfIndividualGrantsQuery;

    @Inject
    private RoleSetService roleSetService;

    @Inject
    private SyEmployeePub syEmployeePub;

    public Cas014Dto getDataInitScreen(){
        List<RoleSetGrantedPerson> grantedPersonList = listOfIndividualGrantsQuery.getListIndividualRollSetGrant();
        List<RoleSetDto> roleSetList = roleSetService.getAllRoleSet().stream().map(this::convertToDto).collect(Collectors.toList());
        List<EmployeeInfoExport> employeeInfoExportList = syEmployeePub.getByListSid(grantedPersonList.stream().map(RoleSetGrantedPerson::getEmployeeID).collect(Collectors.toList()));
        return new Cas014Dto(
                grantedPersonList.stream().map(i -> new RoleSetGrantedPersonDto(i.getRoleSetCd().v(), i.getEmployeeID(), i.getValidPeriod().start(), i.getValidPeriod().end())).collect(Collectors.toList()),
                roleSetList,
                employeeInfoExportList
        );
    }

    private RoleSetDto convertToDto(RoleSet domain) {
        RoleSetDto roleSetDto = new RoleSetDto();
        roleSetDto.setRoleSetName(domain.getRoleSetName().v());
        roleSetDto.setCompanyId(domain.getCompanyId());
        roleSetDto.setRoleSetCd(domain.getRoleSetCd().v());
        //TODO dev delete field approvalAuthority
        //roleSetDto.setApprovalAuthority(domain.getApprovalAuthority().value);

        if (domain.getEmploymentRoleId().isPresent()) {
            roleSetDto.setEmploymentRoleId(domain.getEmploymentRoleId().get());
        }

        if (domain.getPersonInfRoleId().isPresent()) {
            roleSetDto.setPersonInfRoleId(domain.getPersonInfRoleId().get());
        }

        if (domain.getHRRoleId().isPresent()) {
            roleSetDto.setHRRoleId(domain.getHRRoleId().get());
        }

        if (domain.getSalaryRoleId().isPresent()) {
            roleSetDto.setSalaryRoleId(domain.getSalaryRoleId().get());
        }

        if (domain.getMyNumberRoleId().isPresent()) {
            roleSetDto.setMyNumberRoleId(domain.getMyNumberRoleId().get());
        }

        if (domain.getOfficeHelperRoleId().isPresent()) {
            roleSetDto.setOfficeHelperRoleId(domain.getOfficeHelperRoleId().get());
        }
        return roleSetDto;
    }
}
