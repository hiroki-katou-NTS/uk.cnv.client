/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.dom.loginold.adapter;

import java.util.List;

import nts.uk.ctx.sys.gateway.dom.loginold.dto.MailDestiImport;

/**
 * The Interface MailDestinationAdapter.
 */
public interface MailDestinationAdapter {
	
	/**
	 * Gets the mail desti of employee.
	 *
	 * @param cid the cid
	 * @param lstSid the lst sid
	 * @param functionId the function id
	 * @return the mail desti of employee
	 */
	MailDestiImport getMailDestiOfEmployee(String cid, List<String> lstSid, Integer functionId);
}
