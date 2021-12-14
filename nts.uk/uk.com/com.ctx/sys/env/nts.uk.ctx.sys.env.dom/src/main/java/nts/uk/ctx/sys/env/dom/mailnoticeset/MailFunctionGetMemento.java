/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.dom.mailnoticeset;

/**
 * The Interface MailFunctionGetMemento.
 */
public interface MailFunctionGetMemento {

	/**
	 * Gets the function id.
	 *
	 * @return the function id
	 */
	public FunctionId getFunctionId();

	/**
	 * Gets the function name.
	 *
	 * @return the function name
	 */
	public FunctionName getFunctionName();

	/**
	 * Checks if is propriety send mail setting atr.
	 *
	 * @return true, if is propriety send mail setting atr
	 */
	public boolean isProprietySendMailSettingAtr();

	/**
	 * Gets the sort order.
	 *
	 * @return the sort order
	 */
	public SortOrder getSortOrder();
}
