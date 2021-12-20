package nts.uk.screen.com.app.command.sys.auth.personrole.delete;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.pereg.dom.roles.functionauth.authsetting.PersonInfoAuthorityRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleService;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRoleRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.service.RoleSetService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class DeletePersonRoleCommandHandler extends CommandHandler<DeletePersonRoleCommand> {
	
	@Inject
	private RoleService roleService;
	
	@Inject
	private PersonRoleRepository personRoleRepo;
	
	@Inject
	private RoleSetRepository roleSetRepo;
	
	@Inject
	private RoleSetService roleSetService;
	
	@Inject
	private PersonInfoAuthorityRepository personInfoAuthRepo;

	@Override
	protected void handle(CommandHandlerContext<DeletePersonRoleCommand> context) {
		final DeletePersonRoleCommand command = context.getCommand();
		String roleId = command.getRoleId();
		String companyId = AppContexts.user().companyId();
		
		// アルゴリズム「ロール削除」を実行する
		roleService.removeRole(roleId);

		// アルゴリズム「個人情報ロール削除」を実行する
		personRoleRepo.remove(roleId);

		// ドメインモデル「ロール」.「担当区分」をチェックする
		if (command.getAssignAtr() == RoleAtr.GENERAL.value) {
			// ドメインモデルロールセットを取得する
			List<RoleSet> roleSets = roleSetRepo.findByCompanyIdAndPersonRole(companyId, roleId);
			if (!roleSets.isEmpty()) {
				// アルゴリズム「ロールセット更新登録」を実行する
				roleSets.forEach(rs -> {
					rs.removePersonInfRole();
					roleSetService.updateRoleSet(rs);
				});

			}
		}
		
		// アルゴリズム「指定ロールの個人情報の権限を削除する」を実行する
		personInfoAuthRepo.delete(companyId, roleId);
	}
	
	public void checkRoleUse(String roleId) {
		roleService.checksUseRole(roleId);
	}

}
