/**
 * 
 */
package nts.uk.ctx.at.record.app.command.workrecord.worktype;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.ChangeableWorkTypeGroupName;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.ChangeableWorktypeGroup;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.WorkingTypeChangedByEmployment;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.WorkingTypeChangedByEmpRepo;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author danpv
 *
 */
@Stateless
public class WorkTypeEmploymentCommandHandler extends CommandHandler<WorkTypeEmploymentCommand> {

	@Inject
	private WorkingTypeChangedByEmpRepo workTypeRepo;

	@Override
	protected void handle(CommandHandlerContext<WorkTypeEmploymentCommand> context) {
		WorkTypeEmploymentCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		String employmentCode = command.getEmploymentCode();
		if (command.getGroups() == null || command.getGroups().isEmpty()) return;
		List<ChangeableWorktypeGroup> changeableWorkTypeGroups = command.getGroups().stream()
				.map(group -> new ChangeableWorktypeGroup(group.getNo(), group.getName() == null ? null : group.getName(), group.getWorkTypeList()))
				.collect(Collectors.toList());
		for(int i = 0; i < 4; i++){
			changeableWorkTypeGroups.get(i).setName(new ChangeableWorkTypeGroupName("")); 
		}
		WorkingTypeChangedByEmployment workingType = new WorkingTypeChangedByEmployment(new CompanyId(companyId),
				new EmploymentCode(employmentCode), changeableWorkTypeGroups);
		workTypeRepo.save(workingType);
	}

}
