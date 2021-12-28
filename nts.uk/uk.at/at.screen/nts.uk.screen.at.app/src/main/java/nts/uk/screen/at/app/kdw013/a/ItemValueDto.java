package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemValueDto {

	private String value;

	private Integer valueType;

	private String layoutCode;

	private int itemId;

	private String pathLink;

	private boolean isFixed;

	public static ItemValueDto fromDomain(ItemValue domain) {

		return new ItemValueDto(domain.getValue(), domain.getValueType().value, domain.getLayoutCode(), domain.getItemId(), domain.getPathLink(), domain.isFixed());
	}

}
