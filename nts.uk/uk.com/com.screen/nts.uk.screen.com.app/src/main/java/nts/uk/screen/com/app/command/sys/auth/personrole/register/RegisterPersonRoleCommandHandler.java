package nts.uk.screen.com.app.command.sys.auth.personrole.register;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.pereg.dom.roles.functionauth.authsetting.PersonInfoAuthority;
import nts.uk.ctx.pereg.dom.roles.functionauth.authsetting.PersonInfoAuthorityRepository;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleService;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRole;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRoleRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class RegisterPersonRoleCommandHandler extends CommandHandler<RegisterPersonRoleCommand> {

	@Inject
	private PersonRoleRepository personRoleRepo;

	@Inject
	private RoleService roleService;

	@Inject
	private PersonInfoAuthorityRepository authRepo;

	@Override
	protected void handle(CommandHandlerContext<RegisterPersonRoleCommand> context) {
		RegisterPersonRoleCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		String contractCode = AppContexts.user().contractCode();

		Role role = command.toDomain(companyId, contractCode);
		// 未来日参照許可：「しない」固定で登録
		PersonRole personRole = new PersonRole(role.getRoleId(), companyId, false);

		if (command.getCreateMode()) {
			// 画面で指定されたロールの内容でドメインモデル「ロール」を新規作成する
			roleService.insertRole(role);

			// 画面で指定された個人情報ロールの内容でドメインモデル「個人情報ロール」を新規作成する
			personRoleRepo.insert(personRole);
		} else {
			// 画面内で指定されたロールの内容でドメインモデル「ロール」を更新する
			roleService.updateRole(role);

			// 画面で指定された個人情報ロールの内容でドメインモデル「個人情報ロール」を更新する
			personRoleRepo.update(personRole);
		}

		// アルゴリズム「個人情報の権限を新規作成する」を実行する
		saveFunctionAuth(command, role.getRoleId(), companyId);

	}

	private void saveFunctionAuth(RegisterPersonRoleCommand command, String roleId, String companyId) {

		Map<Integer, PersonInfoAuthority> authMap = authRepo.getListOfRole(companyId, roleId);

		List<RegisterFuncAuthCommandParam> params = command.getFunctionAuthList().stream().map(
				auth -> new RegisterFuncAuthCommandParam(companyId, roleId, auth.getFunctionNo(), auth.isAvailable()))
				.collect(Collectors.toList());

		params.forEach(param -> {
			PersonInfoAuthority authDomain = new PersonInfoAuthority(param);
			if (authMap.containsKey(param.functionNo())) {
				authRepo.update(authDomain);
			} else {
				authRepo.add(authDomain);
			}
		});
	}
}
