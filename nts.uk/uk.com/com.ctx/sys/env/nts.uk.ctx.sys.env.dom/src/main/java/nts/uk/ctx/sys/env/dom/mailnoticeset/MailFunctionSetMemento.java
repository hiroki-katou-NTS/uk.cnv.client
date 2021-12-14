/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.dom.mailnoticeset;

/**
 * The Interface MailFunctionSetMemento.
 */
public interface MailFunctionSetMemento {
	
	/**
	 * Sets the function id.
	 *
	 * @param functionId the new function id
	 */
	public void setFunctionId(FunctionId functionId);

	/**
	 * Sets the function name.
	 *
	 * @param functionName the new function name
	 */
	public void setFunctionName(FunctionName functionName);

	/**
	 * Sets the propriety send mail setting atr.
	 *
	 * @param proprietySendMailSettingAtr the new propriety send mail setting atr
	 */
	public void setProprietySendMailSettingAtr(boolean proprietySendMailSettingAtr);

	/**
	 * Sets the sort order.
	 *
	 * @param sortOrder the new sort order
	 */
	public void setSortOrder(SortOrder sortOrder);
}
