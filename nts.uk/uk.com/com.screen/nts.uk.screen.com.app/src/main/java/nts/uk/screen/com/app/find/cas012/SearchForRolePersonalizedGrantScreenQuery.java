package nts.uk.screen.com.app.find.cas012;


import nts.uk.ctx.sys.auth.app.query.GetEmployeeIDFromUserIDQuery;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyImport;
import nts.uk.ctx.sys.auth.dom.adapter.employee.EmployeeInfoImport;
import nts.uk.ctx.sys.auth.dom.adapter.employee.PersonalEmployeeInfoImport;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ScreenQuery: ロール個人別付与を検索する
 * UKDesign.UniversalK.共通.CAS_権限設定.CAS012_管理者ロールの付与.A:管理者ロールの付与.Ａ：メニュー別OCD.ロール個人別付与を検索する
 *
 * @author : chinh.hm
 */
@Stateless
public class SearchForRolePersonalizedGrantScreenQuery {
    @Inject
    private RoleIndividualGrantRepository roleIndividualGrantRepository;
    @Inject
    private CompanyAdapter companyAdapter;
    @Inject
    private GetEmployeeIDFromUserIDQuery userIDQuery;

    public List<Cas012aDto> getListPersonalizedGrant(Integer roleType, String cid) {
        List<RoleIndividualGrant> roleIndividualGrants = new ArrayList<>();
        List<Cas012aDto> rGrants = new ArrayList<>();
        if (roleType == RoleType.SYSTEM_MANAGER.value) {
            roleIndividualGrants.addAll(roleIndividualGrantRepository.findByRoleType(roleType));
        } else if (roleType == RoleType.COMPANY_MANAGER.value) {
            roleIndividualGrants.addAll(roleIndividualGrantRepository.findByCompanyIdAndRoleType(cid, roleType));
        }
        for (RoleIndividualGrant grant : roleIndividualGrants) {
            Optional<PersonalEmployeeInfoImport> optEmployeeInfoImport =
                    userIDQuery.getEmployeeIDFromUserID(grant.getUserId());

            if (optEmployeeInfoImport.isPresent()) {
                PersonalEmployeeInfoImport employeeInfoImport = optEmployeeInfoImport.get();
                List<EmployeeInfoImport> employeeInfos = employeeInfoImport.getEmployeeInfos();
                employeeInfos.forEach(e -> {
                    CompanyImport companyInfo = companyAdapter.findCompanyByCid(e.getCompanyId());
                    rGrants.add(Cas012aDto.fromDomain(
                            grant,
                            companyInfo == null ? null :companyInfo.getCompanyId(),
                            companyInfo == null ? null :companyInfo.getCompanyCode(),
                            companyInfo == null ? null :companyInfo.getCompanyName(),
                            e.getEmployeeId(),
                            e.getEmployeeCode(),
                            employeeInfoImport.getBussinessName()));
                });
            }
        }

        Comparator<Cas012aDto> compareByCode = Comparator
                .comparing(Cas012aDto::getEmployeeCode)
                .thenComparing(Cas012aDto::getCompanyCode);
        return rGrants.stream().sorted(compareByCode).collect(Collectors.toList());
    }
}
