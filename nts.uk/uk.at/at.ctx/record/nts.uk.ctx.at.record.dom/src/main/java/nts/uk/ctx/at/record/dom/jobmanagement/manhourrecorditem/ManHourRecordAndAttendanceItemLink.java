package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;

/**
 * AR: 工数実績項目と勤怠項目の紐付け
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.工数実績項目と勤怠項目の紐付け
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class ManHourRecordAndAttendanceItemLink extends AggregateRoot {

	/** 応援勤務枠No*/
	private final SupportFrameNo frameNo;
	
	/** 工数実績項目ID*/
	private final int itemId;
	
	/** 勤怠項目ID*/
	private final int attendanceItemId;
}
