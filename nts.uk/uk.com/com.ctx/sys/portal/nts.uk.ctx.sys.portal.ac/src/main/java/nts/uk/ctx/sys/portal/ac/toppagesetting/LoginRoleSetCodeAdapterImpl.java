package nts.uk.ctx.sys.portal.ac.toppagesetting;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.pub.role.RoleExportRepo;
import nts.uk.ctx.sys.portal.dom.adapter.roleset.RoleSetDto;
import nts.uk.ctx.sys.portal.dom.adapter.toppagesetting.LoginRoleSetCodeAdapter;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class LoginRoleSetCodeAdapterImpl.
 */
@Stateless
public class LoginRoleSetCodeAdapterImpl implements LoginRoleSetCodeAdapter {

	/** The export. */
	@Inject
	private RoleExportRepo export;
	
	/**
	 * Gets the login role set.
	 * ログイン者のロールセットを取得する
	 * @return the login role set
	 */
	@Override
	public Optional<RoleSetDto> getLoginRoleSet() {
		return this.export.getRoleSetFromUserId(AppContexts.user().userId(), GeneralDate.today())
				.map(dto -> new RoleSetDto(
						dto.getRoleSetCd(),
						dto.getCompanyId(),
						dto.getRoleSetName(),
						dto.getApprovalAuthority(),
						dto.getOfficeHelperRoleId(),
						dto.getMyNumberRoleId(),
						dto.getHRRoleId(),
						dto.getPersonInfRoleId(),
						dto.getEmploymentRoleId(),
						dto.getSalaryRoleId()
						));
	}

}
