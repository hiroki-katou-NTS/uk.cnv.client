package nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu;

import nts.arc.enums.EnumAdaptor;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.ポータル.トップページの部品.フローメニュー作成.既定区分
 */
public enum FixedClassification {
	/** 既定  */
	FIXED(0),
	/** 任意  */
	RANDOM(1);
	
	public int value;

	private FixedClassification(int value) {
		this.value = value;
	}
	
	public static FixedClassification valueOf(Integer value) {
		if (value == null) {
			return null;
		}
		return EnumAdaptor.valueOf(value, FixedClassification.class);
	}
}
