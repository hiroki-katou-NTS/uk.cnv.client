package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

/**
 * Temporary
 * 工数実績変換結果
 * @author tutt
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class ManHrRecordConvertResult {
	
	/** 年月日*/
	private final GeneralDate ymd;
	
	/** 作業リスト*/
	private final List<ManHrTaskDetail> taskList;
	
	/** 実績内容 */
	private final List<ItemValue> manHrContents;
}
