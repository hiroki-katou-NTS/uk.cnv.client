/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.dom.mailnoticeset;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * The Class MailFunction.
 */
//メール機能
@Getter
public class MailFunction extends AggregateRoot {

	/** The function id. */
	//機能ID
	private FunctionId functionId;
	
	/** The function name. */
	//機能名
	private FunctionName functionName;
	
	/** The propriety send mail setting atr. */
	//メール送信設定可否区分
	private boolean proprietySendMailSettingAtr;
	
	/** The sort order. */
	//並び順
	private SortOrder sortOrder;
	
	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(MailFunctionSetMemento memento)
	{
		memento.setFunctionId(this.functionId);
		memento.setFunctionName(this.functionName);
		memento.setProprietySendMailSettingAtr(this.proprietySendMailSettingAtr);
		memento.setSortOrder(this.sortOrder);
	}
	
	/**
	 * Instantiates a new mail function.
	 *
	 * @param memento the memento
	 */
	public MailFunction(MailFunctionGetMemento memento)
	{
		this.functionId= memento.getFunctionId();
		this.functionName = memento.getFunctionName();
		this.proprietySendMailSettingAtr = memento.isProprietySendMailSettingAtr();
		this.sortOrder = memento.getSortOrder();
	}
}
