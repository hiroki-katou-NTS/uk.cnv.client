package nts.uk.ctx.sys.auth.app.command.grant.rolesetjob;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitle;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleDetail;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleRepository;
import nts.uk.shr.com.context.AppContexts;

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
		RoleSetGrantedJobTitleCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		Optional<RoleSetGrantedJobTitle> roleSetJob = roleSetJobRepo.getOneByCompanyId(companyId);
		if (roleSetJob.isPresent()) {
			update(roleSetJob.get(), command);
		} else {
			add(command, companyId);
		}
	}

	private void add(RoleSetGrantedJobTitleCommand command, String companyId) {
		RoleSetGrantedJobTitle domain = new RoleSetGrantedJobTitle(companyId, command.isApplyToConcurrentPerson(),
				command.getDetails().stream().map(item -> new RoleSetGrantedJobTitleDetail(item.getRoleSetCd(), item.getJobTitleId(), companyId))
						.collect(Collectors.toList()));
		roleSetJobRepo.insert(domain);
	}

	private void update(RoleSetGrantedJobTitle domain, RoleSetGrantedJobTitleCommand command) {
		domain.setApplyToConcurrentPerson(command.isApplyToConcurrentPerson());
		domain.setDetails(
				command.getDetails().stream().map(item -> new RoleSetGrantedJobTitleDetail(item.getRoleSetCd(),
						item.getJobTitleId(), domain.getCompanyId())).collect(Collectors.toList()));
		roleSetJobRepo.update(domain);
	}

}
