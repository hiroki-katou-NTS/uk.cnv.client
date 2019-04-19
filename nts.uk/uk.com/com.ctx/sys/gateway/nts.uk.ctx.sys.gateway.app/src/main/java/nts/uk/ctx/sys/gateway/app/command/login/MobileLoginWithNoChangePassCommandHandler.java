/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.app.command.login;

import javax.ejb.Stateless;

/**
 * The Class SubmitLoginFormThreeCommandHandler.
 */
@Stateless
public class MobileLoginWithNoChangePassCommandHandler extends MobileLoginCommandHandler {

	@Override
	protected boolean needShowChangePass(){
		return true;
	}
	
}
