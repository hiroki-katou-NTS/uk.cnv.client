package nts.uk.ctx.at.function.ac.role;

import lombok.val;
import nts.uk.ctx.at.function.dom.adapter.alarm.MailExportRolesDto;
import nts.uk.ctx.at.function.dom.adapter.role.AlarmMailSettingsAdapter;
import nts.uk.ctx.sys.auth.pub.role.RoleExportRepo;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AlarmMailSettingsAdapterImpl implements AlarmMailSettingsAdapter {

    @Inject
    private RoleExportRepo roleExportRepo;

    /**
     * @param lstroleId
     * @return List<MailExportRolesDto>
     */
    @Override
    public List<MailExportRolesDto> getRoleNameList(List<String> lstroleId) {
        val roleList = roleExportRepo.findByListRoleId(AppContexts.user().companyId(), lstroleId);
        if (roleList == null || roleList.isEmpty()) {
            return Collections.emptyList();
        }
        return roleList.stream().map(x -> (new MailExportRolesDto(x.getRoleId(), x.getRoleName(), x.getEmployeeReferenceRange()))).collect(Collectors.toList());
    }

    /**
     * find by company
     *
     * @param companyId
     * @return Role
     */
    @Override
    public List<MailExportRolesDto> findByCompanyId(String companyId) {
        val roleList = roleExportRepo.findByCompanyId(companyId);
        if (roleList == null || roleList.isEmpty()) {
            return Collections.emptyList();
        }
        return roleList.stream().map(x -> (new MailExportRolesDto(x.getRoleId(), x.getRoleName(), x.getEmployeeReferenceRange()))).collect(Collectors.toList());
    }
}
