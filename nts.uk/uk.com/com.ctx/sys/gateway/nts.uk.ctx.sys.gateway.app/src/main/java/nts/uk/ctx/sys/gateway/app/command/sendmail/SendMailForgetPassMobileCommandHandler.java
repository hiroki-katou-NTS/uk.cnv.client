/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.app.command.sendmail;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.gateway.app.command.sendmail.dto.SendMailReturnDto;
import nts.uk.ctx.sys.gateway.app.service.login.LoginService;

/**
 * The Class SendMailInfoCommandHandler.
 */
@Stateless
@Transactional
public class SendMailForgetPassMobileCommandHandler
		extends CommandHandlerWithResult<SendMailInfoFormGCommand, List<SendMailReturnDto>> {
	
	/** The login Service. */
	@Inject
	private LoginService loginService;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command
	 * .CommandHandlerContext)
	 */
	@Override
	protected List<SendMailReturnDto> handle(CommandHandlerContext<SendMailInfoFormGCommand> context) {
		// get command
		SendMailInfoFormGCommand command = context.getCommand();

		String companyId = loginService.comanyId(command.getContractCode(), command.getCompanyCode());
		
		String employeeCode = loginService.employeeCodeEdit(command.getEmployeeCode(), companyId);
		
		return loginService.sendMail(companyId, employeeCode, command.getContractCode());
	}
}
