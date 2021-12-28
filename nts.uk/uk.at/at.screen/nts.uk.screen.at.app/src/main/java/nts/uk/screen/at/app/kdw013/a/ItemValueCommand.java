package nts.uk.screen.at.app.kdw013.a;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

@AllArgsConstructor
@Getter
public class ItemValueCommand {

	private String value;

	private Integer valueType;

	private String layoutCode;

	private Integer itemId;

	private String pathLink;

	private boolean isFixed;

	public static ItemValue toDomain(ItemValueCommand iv) {
		return new ItemValue(iv.getValue(),
				iv.getValueType() == null ? null : EnumAdaptor.valueOf(iv.getValueType(), ValueType.class),
				iv.getLayoutCode(),
				iv.getItemId(),
				iv.getPathLink(), 
				iv.isFixed());
	}
}
