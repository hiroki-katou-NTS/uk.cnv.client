package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateAttrSheet;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecificDateAttrSheetDto {
	// 特定日項目NO
	private Integer specificDateItemNo;
	// するしない区分
	private Integer specificDateAttr;

	public static SpecificDateAttrSheetDto fromDomain(SpecificDateAttrSheet domain) {
		return new SpecificDateAttrSheetDto(domain.getSpecificDateItemNo().v(), domain.getSpecificDateAttr().value);
	}
}
