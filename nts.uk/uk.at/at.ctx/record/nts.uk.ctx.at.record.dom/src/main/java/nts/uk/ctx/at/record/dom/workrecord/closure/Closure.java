/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.dom.workrecord.closure;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * The Class Closure.
 */
// 締め
@Getter
public class Closure extends AggregateRoot {

	/** The company id. */
	// 会社ID
	private CompanyId companyId;

	/** The closure id. */
	// 締めＩＤ
	private Integer closureId;

	/** The use classification. */
	// 使用区分
	private UseClassification useClassification;

	/** The month. */
	// 当月
	private ClosureMonth closureMonth;

	/** The closure histories. */
	// 締め変更履歴
	@Setter
	private List<ClosureHistory> closureHistories;
	
	/**
	 * Instantiates a new closure.
	 *
	 * @param memento the memento
	 */
	public Closure(ClosureGetMemento memento){
		this.companyId = memento.getCompanyId();
		this.closureId = memento.getClosureId();
		this.useClassification = memento.getUseClassification();
		this.closureMonth = memento.getClosureMonth();
		this.closureHistories = memento.getClosureHistories();
	}
	
	
	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(ClosureSetMemento memento){
		memento.setCompanyId(this.companyId);
		memento.setClosureId(this.closureId);
		memento.setUseClassification(this.useClassification);
		memento.setClosureMonth(this.closureMonth);
		memento.setClosureHistories(this.closureHistories);
	}
}
