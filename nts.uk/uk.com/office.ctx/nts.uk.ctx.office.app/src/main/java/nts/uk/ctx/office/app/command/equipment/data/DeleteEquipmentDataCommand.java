package nts.uk.ctx.office.app.command.equipment.data;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;

@Value
public class DeleteEquipmentDataCommand {

	/**
	 * 設備コード
	 */
	private String equipmentCode;
	
	/**
	 * 利用者ID
	 */
	private String sid;
	
	/**
	 * 利用日
	 */
	private GeneralDate useDate;
	
	/**
	 * 入力日
	 */
	private GeneralDateTime inputDate;
}
