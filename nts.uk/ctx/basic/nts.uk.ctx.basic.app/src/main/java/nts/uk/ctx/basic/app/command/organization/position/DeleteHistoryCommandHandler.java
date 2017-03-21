package nts.uk.ctx.basic.app.command.organization.position;


import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.basic.dom.organization.position.JobHistory;
import nts.uk.ctx.basic.dom.organization.position.PositionRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class DeleteHistoryCommandHandler extends CommandHandler<DeleteHistoryCommand> {

	@Inject
	private PositionRepository positionRepository;

	@Override
	protected void handle(CommandHandlerContext<DeleteHistoryCommand> context) {

//		String companyCode = AppContexts.user().companyCode();
//		String hitoryId = IdentifierUtil.randomUniqueId();
//
//		
//		positionRepository.deleteHist(companyCode, hitoryId );
		
		String companyCode = AppContexts.user().companyCode();
		String test = context.getCommand().getCompanyCode();
		String checkDateUpdate = context.getCommand().getStartDate().toString();
		
		
		
		GeneralDate endDate = (context.getCommand().getEndDate());
		if(checkDateUpdate.compareTo("0")==0){
		Optional<JobHistory> histEndDate = positionRepository.getHistoryByEdate(companyCode, test);
		if(histEndDate.isPresent()){
			JobHistory jobHis = histEndDate.get();
			jobHis.setEndDate(endDate);
			positionRepository.updateHistory(jobHis);
			positionRepository.deleteHist(companyCode,  
					context.getCommand().getHistoryId());
			
		}
		}else{
			positionRepository.deleteHist(companyCode,  
					context.getCommand().getHistoryId());
		}
	}

}
