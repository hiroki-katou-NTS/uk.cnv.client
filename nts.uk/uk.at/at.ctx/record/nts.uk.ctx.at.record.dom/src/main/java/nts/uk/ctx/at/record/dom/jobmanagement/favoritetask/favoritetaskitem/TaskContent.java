package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;

/**
 * ValueObject: 作業内容
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.お気に入り作業項目.作業内容
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class TaskContent implements DomainValue {
	
	/** 項目ID: 工数実績項目ID*/
	private final int itemId; 
	
	/** 作業コード*/
	private final WorkCode taskCode;
}
