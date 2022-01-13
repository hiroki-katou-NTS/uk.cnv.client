package nts.uk.ctx.at.record.dom.jobmanagement.displayformat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;

/**
 * ValueObject: 実績欄表示項目
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class RecordColumnDisplayItem implements DomainObject{
	
	//表示順
	private int order;
	
	//対象項目: 勤怠項目ID
	private int attendanceItemId;
	
	//名称: 実績欄表示名称
	private RecordColumnDispName displayName;
}
