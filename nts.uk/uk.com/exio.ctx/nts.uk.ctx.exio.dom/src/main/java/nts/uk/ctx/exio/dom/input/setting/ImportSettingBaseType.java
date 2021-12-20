package nts.uk.ctx.exio.dom.input.setting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import nts.arc.enums.EnumAdaptor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ImportSettingBaseType {
	DOMAIN_BASE(1),
	CSV_BASE(2);

	public final int value;
	public static ImportSettingBaseType valueOf(int value) {
		return EnumAdaptor.valueOf(value, ImportSettingBaseType.class);
	}
}
