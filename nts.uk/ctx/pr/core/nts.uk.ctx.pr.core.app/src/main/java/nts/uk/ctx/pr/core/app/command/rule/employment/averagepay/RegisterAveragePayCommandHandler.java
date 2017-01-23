package nts.uk.ctx.pr.core.app.command.rule.employment.averagepay;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.ctx.pr.core.dom.rule.employment.averagepay.AttendDayGettingSet;
import nts.uk.ctx.pr.core.dom.rule.employment.averagepay.AveragePay;
import nts.uk.ctx.pr.core.dom.rule.employment.averagepay.AveragePayRepository;
import nts.uk.ctx.pr.core.dom.rule.employment.averagepay.ExceptionPayRate;
import nts.uk.ctx.pr.core.dom.rule.employment.averagepay.RoundDigitSet;
import nts.uk.ctx.pr.core.dom.rule.employment.averagepay.RoundTimingSet;
import nts.uk.shr.com.context.AppContexts;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@RequestScoped
public class RegisterAveragePayCommandHandler extends CommandHandler<RegisterAveragePayCommand> {

	@Inject
	private AveragePayRepository averagePayRepository;
	
	@Override
	protected void handle(CommandHandlerContext<RegisterAveragePayCommand> context) {
		RegisterAveragePayCommand command = context.getCommand();
	
		String companyCode = AppContexts.user().companyCode();
		
		AveragePay averagePay = new AveragePay(
				new CompanyCode(companyCode),
				EnumAdaptor.valueOf(command.getAttendDayGettingSet(), AttendDayGettingSet.class), 
				new ExceptionPayRate(command.getExceptionPayRate()), 
				EnumAdaptor.valueOf(command.getRoundDigitSet(), RoundDigitSet.class),
				EnumAdaptor.valueOf(command.getRoundTimingSet(), RoundTimingSet.class));	
		 
		averagePayRepository.add(averagePay);
	}

}
