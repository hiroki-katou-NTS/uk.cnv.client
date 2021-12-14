package nts.uk.screen.com.app.find.equipment.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;
import nts.uk.ctx.office.dom.equipment.data.ResultData;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultDataDto {

	/**
	 * 項目NO
	 */
	private String itemNo;

	/**
	 * 項目分類
	 */
	private int itemClassification;

	/**
	 * 項目値
	 */
	private String actualValue;

	public static ResultDataDto fromDomain(ResultData domain) {
		return new ResultDataDto(domain.getItemNo().v(), domain.getItemClassification().value,
				domain.getActualValue().map(ActualItemUsageValue::v).orElse(null));
	}
}
