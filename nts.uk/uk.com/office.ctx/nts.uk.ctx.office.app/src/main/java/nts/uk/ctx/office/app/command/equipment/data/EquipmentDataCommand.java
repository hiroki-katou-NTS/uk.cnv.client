package nts.uk.ctx.office.app.command.equipment.data;

import java.util.List;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;

@Value
public class EquipmentDataCommand {
	/**
	 * 入力日
	 */
	private final GeneralDateTime inputDate;

	/**
	 * 利用日
	 */
	private final GeneralDate useDate;

	/**
	 * 利用者ID
	 */
	private final String sid;

	/**
	 * 設備コード
	 */
	private final String equipmentCode;

	/**
	 * 設備分類コード
	 */
	private final String equipmentClassificationCode;

	/**
	 * 項目データ
	 */
	private List<ResultDataCommand> itemDatas;
}
