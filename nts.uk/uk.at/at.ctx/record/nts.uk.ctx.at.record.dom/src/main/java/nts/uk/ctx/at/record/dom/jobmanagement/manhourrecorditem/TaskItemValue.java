package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;

/**
 * ValueObject 作業項目値
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
@Setter
public class TaskItemValue extends DomainObject {
	
	/** 工数実績項目ID*/
	private int itemId;
	
	/** 値*/
	private String value;

}
