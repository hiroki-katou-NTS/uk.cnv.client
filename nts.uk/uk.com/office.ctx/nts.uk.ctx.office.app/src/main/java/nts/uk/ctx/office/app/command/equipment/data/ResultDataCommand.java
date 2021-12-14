package nts.uk.ctx.office.app.command.equipment.data;

import lombok.Value;

@Value
public class ResultDataCommand {

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
}
