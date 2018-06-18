package nts.uk.ctx.at.shared.app.command.era.name;

import java.time.LocalDate;
import java.time.Month;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.era.name.EraName;
import nts.uk.ctx.at.shared.dom.era.name.EraNameDom;
import nts.uk.ctx.at.shared.dom.era.name.EraNameDomRepository;
import nts.uk.ctx.at.shared.dom.era.name.SymbolName;
import nts.uk.ctx.at.shared.dom.era.name.SystemType;

@Stateless
public class EraNameSaveCommandHandler extends CommandHandler<EraNameSaveCommand> {
	
	private final GeneralDate LAST_END_DATE = GeneralDate.localDate(LocalDate.of(9999, Month.DECEMBER, 31));
	
	@Inject
	private EraNameDomRepository repo;

	@Override
	protected void handle(CommandHandlerContext<EraNameSaveCommand> context) {
		
		EraNameSaveCommand command = context.getCommand();
		
		// get era name from db
		EraNameDom domain = this.repo.getEraNameById(command.getEraId());
		
		// update
		if(domain != null) {
			if(SystemType.SYSTEM.value.equals(command.getSystemType())){
				throw new RuntimeException("Invalid EraName");
			}
	
			// get previous era name item
			EraNameDom preItem = this.repo.getEraNameByEndDate(GeneralDate.localDate(domain.getStartDate().localDate().minusDays(1)));
			// check if the last item
			if(domain.getEndDate().equals(LAST_END_DATE)) {
				// check if start date is invalid
				if(!domain.getStartDate().after(preItem.getStartDate()))
					throw new RuntimeException("Invalid Start Date");
			}
			else {
				// get next era name items
				EraNameDom nextItem = this.repo.getEraNameByStartDate(domain.getEndDate().addDays(1));
				// check if start date is invalid
				if(!domain.getStartDate().after(preItem.getStartDate()) || domain.getStartDate().before(nextItem.getStartDate()))
					throw new RuntimeException("Invalid Start Date");
			}
			// update saved item
			domain.setEraName(new EraName(command.getEraName()));
			domain.setSymbol(new SymbolName(command.getEraSymbol()));
			domain.setStartDate(command.getStartDate());
			this.repo.updateEraName(domain);
			
			// update end date of previous item
			preItem.setEndDate(GeneralDate.localDate(command.getStartDate().localDate().minusDays(1)));
			this.repo.updateEraName(preItem);
			
		}
		// add new
		else {
			// get previous era name item
			EraNameDom preItem = this.repo.getEraNameByEndDate(LAST_END_DATE);

			// add new era name item
			domain = command.toDomain();
			// check if start date is invalid
			if(!domain.getStartDate().after(preItem.getStartDate()))
				throw new RuntimeException("Invalid Start Date");
			
			domain.setSystemType(SystemType.NOT_SYSTEM);
			domain.setEndDate(LAST_END_DATE);
			this.repo.addNewEraName(domain);

			// update end date of previous item
			preItem.setEndDate(GeneralDate.localDate(command.getStartDate().localDate().minusDays(1)));
			this.repo.updateEraName(preItem);
		}
	}
	
}