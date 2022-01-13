package nts.uk.ctx.at.schedule.app.command.task.taskpalette;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteDisplayInfo;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteName;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganization;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganizationRepository;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteRemark;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.context.AppContexts;

/**
 * <<command>> 作業パレットを登録する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.職場.対象組織識別情報.組織の表示情報を取得する
 * @author quytb
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TaskPaletteAddCommandHandler extends CommandHandler<TaskPaletteCommand> {
	@Inject
	private TaskPaletteOrganizationRepository repository;

	@Override
	protected void handle(CommandHandlerContext<TaskPaletteCommand> context) {
		TaskPaletteCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		Map<Integer, TaskCode> tasks = new HashMap<Integer, TaskCode>();
				
		TargetOrgIdenInfor targetOrg = TargetOrgIdenInfor
				.createFromTargetUnit(TargetOrganizationUnit.valueOf(command.getUnit()), command.getTargetId());
		
		TaskPaletteDisplayInfo displayInfo = new TaskPaletteDisplayInfo(new TaskPaletteName(command.getName()),
				Optional.ofNullable(new TaskPaletteRemark(command.getRemarks())));

		if(command.getPosition() != null && !command.getPosition().isEmpty()) {
			for(int i = 0; i< command.getPosition().size(); i++) {
				tasks.put(command.getPosition().get(i), new TaskCode(command.getTaskCode().get(i)));
			}
		}		
		
		Optional<TaskPaletteOrganization> optional = repository.getByPage(targetOrg, command.getPage());
		TaskPaletteOrganization domain = TaskPaletteOrganization.create(targetOrg, command.getPage(), displayInfo,
				tasks);
		
		if (!optional.isPresent()) {			
			this.repository.insert(companyId, domain);
		} else {
			this.repository.update(companyId, domain);
		}	
		
	}
}
