package nts.uk.ctx.at.record.app.command.workrecord.worktype;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.WorkingTypeChangedByEmpRepo;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.WorkingTypeChangedByEmployment;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class CopyEmploymentCommandHandler extends CommandHandler<CopyEmploymentCommand>{
	@Inject
	private WorkingTypeChangedByEmpRepo workRep;
	
	@Override
	protected void handle(CommandHandlerContext<CopyEmploymentCommand> context) {
		String companyId = AppContexts.user().companyId();
		CopyEmploymentCommand copyCommand = context.getCommand();
		
		//複写元のドメインモデル「雇用別の変更可能な勤務種類」を取得する
		WorkingTypeChangedByEmployment sourceData = workRep.get(new CompanyId(companyId), new EmploymentCode(copyCommand.getEmploymentCode()));
		
		workRep.copyEmployment(companyId, sourceData, copyCommand.getTargetEmploymentCodes());
	}

}
