package nts.uk.screen.at.app.kdw013.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordItem;

/**
 * @author thanhpv
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ManHourRecordItemDto {
	
	/** 項目ID*/
	public Integer itemId;
	
	/** 名称*/
	public String name;
	
	/** フォーマット設定に表示する*/
	public Integer useAtr;

	public ManHourRecordItemDto(ManHourRecordItem domain) {
		this.itemId = domain.getItemId();
		this.name = domain.getName();
		this.useAtr = domain.getUseAtr().value;
	}
	
}
