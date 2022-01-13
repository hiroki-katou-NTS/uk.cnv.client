package nts.uk.ctx.at.record.dom.jobmanagement.displayformat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;

/**
 * ValueObject 表示する勤怠項目
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class DisplayAttItem implements DomainObject {
	
	//項目ID: 勤怠項目ID
	private int attendanceItemId;
	
	//表示順
	private int order;
}
