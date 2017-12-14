/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.app.command.singlesignon;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.gateway.dom.singlesignon.OtherSysAccount;
import nts.uk.ctx.sys.gateway.dom.singlesignon.OtherSysAccountRepository;

/**
 * The Class RemoveOtherSysAccountCommandHandler.
 */
@Stateless
public class RemoveOtherSysAccountCommandHandler extends CommandHandler<RemoveOtherSysAccountCommand> {

	/** The other sys account repository. */
	@Inject
	private OtherSysAccountRepository otherSysAccountRepository;

	/* (non-Javadoc)
	 * @see nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command.CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<RemoveOtherSysAccountCommand> context) {
		
		// Get command
		RemoveOtherSysAccountCommand command = context.getCommand();
		
		Optional<OtherSysAccount> opOtherSysAcc = otherSysAccountRepository.findByUserId(command.getUserId());
		
		if(opOtherSysAcc.isPresent()){
			// remove
			this.otherSysAccountRepository.remove(opOtherSysAcc.get().getUserId(), opOtherSysAcc.get().getCompanyCode(), opOtherSysAcc.get().getUserName());
			
		}
		
	}

}
