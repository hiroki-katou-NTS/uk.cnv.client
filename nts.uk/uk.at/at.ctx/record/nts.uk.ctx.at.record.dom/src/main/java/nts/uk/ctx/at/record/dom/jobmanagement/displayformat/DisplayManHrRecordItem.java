package nts.uk.ctx.at.record.dom.jobmanagement.displayformat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;

/**
 * ValueObject: 表示する工数実績項目
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class DisplayManHrRecordItem implements DomainObject {

	// 項目ID: 工数実績項目ID
	private int itemId;

	// 表示順
	private int order;
}
