package nts.uk.ctx.sys.auth.app.command.grant.rolesetjob;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.gul.text.StringUtil;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitle;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetCode;
import nts.uk.shr.com.context.AppContexts;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
@Transactional
public class RegisterRoleSetGrantedJobTitleCommandHandler extends CommandHandler<RoleSetGrantedJobTitleCommand> {

	@Inject
	private RoleSetGrantedJobTitleRepository roleSetJobRepo;

	@Override
	protected void handle(CommandHandlerContext<RoleSetGrantedJobTitleCommand> context) {
		String cid = AppContexts.user().companyId();
		RoleSetGrantedJobTitleCommand command = context.getCommand();
		List<RoleSetGrantedJobTitle> grantedJobTitles = roleSetJobRepo.getByCompanyId(cid);
		List<RoleSetGrantedJobTitleDetailCommand> details = command.getDetails();
		for (RoleSetGrantedJobTitleDetailCommand item : details) {
			if (!StringUtil.isNullOrEmpty(item.getJobTitleId(), true) && !StringUtil.isNullOrEmpty(item.getRoleSetCd(), true)) {
				Optional<RoleSetGrantedJobTitle> oldItem = grantedJobTitles.stream().filter(i -> i.getJobTitleId().equals(item.getJobTitleId())).findFirst();
				if (oldItem.isPresent()) {
					oldItem.get().setRoleSetCd(new RoleSetCode(item.getRoleSetCd()));
					roleSetJobRepo.update(oldItem.get());
				} else {
					roleSetJobRepo.insert(new RoleSetGrantedJobTitle(
							cid,
							item.getJobTitleId(),
							new RoleSetCode(item.getRoleSetCd())
					));
				}
			}
		}
	}
}
