/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.dom.executionlog;

import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;


/**
 * The Class ScheduleCreateContent.
 */
//Domain: スケジュール作成内容
@Getter
public class ScheduleCreateContent extends AggregateRoot{

	/** The execution id. */
	// 実行ID
	private String executionId;

	/** The re confirm. */
	// 	確定済みにする
	private Boolean confirm;

	/** The creation type. */
	//作成種類
	private ImplementAtr implementAtr;

	/** The specify creation. */
	// 作成方法の指定
	private SpecifyCreation specifyCreation;

	//再作成条件
	private Optional<RecreateCondition> recreateCondition;

	public ScheduleCreateContent (String executionId,Boolean confirm,ImplementAtr creationType,
								  SpecifyCreation specifyCreation,Optional<RecreateCondition> recreateCondition){
		this.executionId = executionId;
		this.confirm =confirm;
		this.implementAtr = creationType;
		this.specifyCreation = specifyCreation;
		this.recreateCondition =recreateCondition;
	}
	/**
	 * To domain.
	 *
	 * @param memento the memento
	 * @return the execution content
	 */
	public ScheduleCreateContent (ScheduleCreateContentGetMemento memento){
		this.executionId = memento.getExecutionId();
		this.confirm = memento.getConfirm();
		this.implementAtr = memento.getCreationType();
		this.specifyCreation = memento.getSpecifyCreation();
		this.recreateCondition = memento.getRecreateCondition();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(ScheduleCreateContentSetMemento memento){
		memento.setConfirm(this.confirm);
		memento.setcreationType(this.implementAtr);
		memento.setExecutionId(this.executionId);
		memento.setSpecifyCreation(this.specifyCreation);
		memento.setRecreateCondition(this.recreateCondition);
	}

	public void setImplementAtr(ImplementAtr implementAtr) {
		this.implementAtr = implementAtr;
	}


	public ScheduleCreateContent() {
	}

	public void setConfirm(Boolean confirm) {
		this.confirm = confirm;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

    public void setSpecifyCreation(SpecifyCreation specifyCreation) {
        this.specifyCreation = specifyCreation;
    }
    public void setRecreateCondition(Optional<RecreateCondition> recreateCondition) {
        this.recreateCondition = recreateCondition;
    }
}
