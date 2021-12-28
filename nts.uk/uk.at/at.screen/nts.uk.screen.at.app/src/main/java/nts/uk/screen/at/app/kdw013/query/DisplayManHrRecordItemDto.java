package nts.uk.screen.at.app.kdw013.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.DisplayManHrRecordItem;

@Getter
@AllArgsConstructor
public class DisplayManHrRecordItemDto{

	// 項目ID: 工数実績項目ID
	public Integer itemId;

	// 表示順
	public Integer order;
	
	public DisplayManHrRecordItemDto(DisplayManHrRecordItem domain) {
		super();
		this.itemId = domain.getItemId();
		this.order = domain.getOrder();
	}
}
