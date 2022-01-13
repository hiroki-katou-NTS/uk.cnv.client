package nts.uk.ctx.at.function.ac.role;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.role.RoleSetExportAdapter;
import nts.uk.ctx.at.function.dom.adapter.role.RoleSetExportDto;
import nts.uk.ctx.sys.auth.pub.role.RoleExportRepo;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

@Stateless
public class RoleSetExportAdapterImpl implements RoleSetExportAdapter {
    @Inject
    private RoleExportRepo roleExportRepo;

    @Override
    public Optional<RoleSetExportDto> getRoleSetFromUserId(String userId, GeneralDate baseDate) {
        return roleExportRepo.getRoleSetFromUserId(userId, baseDate)
                .map(x -> new RoleSetExportDto(
                        x.getRoleSetCd(),
                        x.getCompanyId(),
                        x.getRoleSetName(),
//                        x.getApprovalAuthority(),
                        x.getOfficeHelperRoleId(),
                        x.getMyNumberRoleId(),
                        x.getHRRoleId(),
                        x.getPersonInfRoleId(),
                        x.getEmploymentRoleId(),
                        x.getSalaryRoleId()
                ));
    }
}
