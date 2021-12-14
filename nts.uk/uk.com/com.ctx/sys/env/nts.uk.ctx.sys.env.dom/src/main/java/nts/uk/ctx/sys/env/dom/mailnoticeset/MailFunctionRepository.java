/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.dom.mailnoticeset;

import java.util.List;

/**
 * The Interface MailFunctionRepository.
 */
public interface MailFunctionRepository {

	/**
	 * Find all.
	 *
	 * @param proprietySendMailSettingAtr the propriety send mail setting atr
	 * @return the list
	 */
	public List<MailFunction> findAll(Boolean proprietySendMailSettingAtr);

	public List<MailFunction> findAll();
}
