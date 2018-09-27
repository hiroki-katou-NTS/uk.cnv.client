package nts.uk.ctx.at.shared.dom.scherec.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
public class OptionalItemAtrExport {
	/**
	 * 実績区分
	 */
	private PerformanceAtr performanceAtr;

	/**
	 * 任意項目の属性
	 */
	private OptionalItemAtr optionalItemAtr;

	/**
	 * 任意項目NO
	 */
	private OptionalItemNo optionalItemNo;

	public OptionalItemAtrExport(int performanceAtr, int optionalItemAtr, Integer optionalItemNo) {
		super();
		this.performanceAtr = EnumAdaptor.valueOf(performanceAtr, PerformanceAtr.class);
		this.optionalItemAtr = EnumAdaptor.valueOf(optionalItemAtr, OptionalItemAtr.class);
		this.optionalItemNo = new OptionalItemNo(optionalItemNo);
	}
}
