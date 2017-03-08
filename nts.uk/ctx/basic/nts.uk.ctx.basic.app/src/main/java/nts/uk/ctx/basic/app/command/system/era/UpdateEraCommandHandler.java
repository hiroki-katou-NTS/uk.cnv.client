package nts.uk.ctx.basic.app.command.system.era;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.basic.dom.system.era.Era;
import nts.uk.ctx.basic.dom.system.era.EraRepository;

@RequestScoped
@Transactional
public class UpdateEraCommandHandler  extends CommandHandler<UpdateEraCommand>{
	@Inject
	private EraRepository eraRepository;

	@Override
	protected void handle(CommandHandlerContext<UpdateEraCommand> context) {
		Era era = context.getCommand().toDomain();
		
		Optional<Era> eraHistUpdate = this.eraRepository.getHistIdUpdate(era.getEraHist());
		if(!eraHistUpdate.isPresent()){
			throw new BusinessException("eeeee");
		};
		
		Optional<Era> eraBefore = this.eraRepository.getEndDateBefore(eraHistUpdate.get().getStartDate().addDays(-1));
		eraHistUpdate.get().setStartDate(era.getStartDate());
		eraHistUpdate.get().setEraName(era.getEraName());
		eraHistUpdate.get().setEraMark(era.getEraMark());
		if(eraBefore.isPresent()){
			eraBefore.get().setEndDate(era.getStartDate().addDays(-1));
			
			this.eraRepository.update(eraHistUpdate.get());
			this.eraRepository.update(eraBefore.get());
		}else{
			this.eraRepository.update(eraHistUpdate.get());
		}
	}
}
