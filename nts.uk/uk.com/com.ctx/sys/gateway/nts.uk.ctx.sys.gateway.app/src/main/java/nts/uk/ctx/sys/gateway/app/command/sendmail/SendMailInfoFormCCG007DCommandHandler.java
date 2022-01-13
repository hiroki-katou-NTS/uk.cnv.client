package nts.uk.ctx.sys.gateway.app.command.sendmail;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.gateway.app.command.sendmail.dto.SendMailCCG007DReturnDto;
import nts.uk.ctx.sys.gateway.app.service.login.LoginService;

/**
 * The Class SendMailInfoFormCCG007DCommandHandler.
 */
@Stateless
@Transactional
public class SendMailInfoFormCCG007DCommandHandler 
		extends CommandHandlerWithResult<SendMailInfoFormGCommand, SendMailCCG007DReturnDto> {
	
	/**
	* パスワード再設定メール送信（形式２、形式３）
	/** The user adapter. */
	@Inject
	private LoginService sendMailService;
	
	/*
	* (non-Javadoc)
	* 
	* @see
	* nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command
	* .CommandHandlerContext)
	*/
	@Override
	protected SendMailCCG007DReturnDto handle(CommandHandlerContext<SendMailInfoFormGCommand> context) {
		// get command
		SendMailInfoFormGCommand command = context.getCommand();
		
		String companyId = sendMailService.comanyId(command.getContractCode(), command.getCompanyCode());
		
		return sendMailService.sendMailCCG007D(companyId, command.getEmployeeCode(), command.getContractCode());
	}
}
