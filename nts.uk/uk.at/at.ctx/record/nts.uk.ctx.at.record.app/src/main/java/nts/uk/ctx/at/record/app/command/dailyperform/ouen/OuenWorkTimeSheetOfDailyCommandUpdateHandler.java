package nts.uk.ctx.at.record.app.command.dailyperform.ouen;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.shared.app.util.attendanceitem.CommandFacade;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;

@Stateless
public class OuenWorkTimeSheetOfDailyCommandUpdateHandler extends CommandFacade<OuenWorkTimeSheetOfDailyCommand> {

	@Inject
	private OuenWorkTimeSheetOfDailyRepo repo;
	
	@Inject
	private WorkplaceGroupAdapter adapter;
	
	@Override
	protected void handle(CommandHandlerContext<OuenWorkTimeSheetOfDailyCommand> context) {
		
		String workplaceId = adapter.getAffWkpHistItemByEmpDate(context.getCommand().getEmployeeId(), GeneralDate.today());
		OuenWorkTimeSheetOfDailyCommand command = context.getCommand().corectOuenCommand(context.getCommand(), workplaceId);
		
		if (!command.getData().isEmpty()) {
			OuenWorkTimeSheetOfDaily domain = new OuenWorkTimeSheetOfDaily(context.getCommand().getEmployeeId(), context.getCommand().getWorkDate(), command.getData());
			ArrayList<OuenWorkTimeSheetOfDaily> domains = new ArrayList<>();
			domains.add(domain);
			repo.update(domains);
		}
	}
}
