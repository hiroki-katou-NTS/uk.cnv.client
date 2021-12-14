package nts.uk.screen.com.app.find.equipment.data;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EquipmentDataDto {

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
	private List<ResultDataDto> itemDatas;

	public static EquipmentDataDto fromDomain(EquipmentData domain) {
		return new EquipmentDataDto(domain.getInputDate(), domain.getUseDate(), domain.getSid(),
				domain.getEquipmentCode().v(), domain.getEquipmentClassificationCode().v(),
				domain.getResultDatas().stream().map(ResultDataDto::fromDomain).collect(Collectors.toList()));
	}
}
