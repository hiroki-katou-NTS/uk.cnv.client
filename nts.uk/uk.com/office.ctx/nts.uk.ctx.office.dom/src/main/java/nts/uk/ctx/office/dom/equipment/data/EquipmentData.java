package nts.uk.ctx.office.dom.equipment.data;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.data.ResultData.Require;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.設備利用実績データ
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EquipmentData extends AggregateRoot {

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
	private final EquipmentCode equipmentCode;

	/**
	 * 設備分類コード
	 */
	private final EquipmentClassificationCode equipmentClassificationCode;

	/**
	 * 項目データ
	 */
	private List<ResultData> resultDatas;

	/**
	 * [C-1] 新規登録
	 * 
	 * @param require
	 * @param cid                         会社ID
	 * @param equipmentClassificationCode 設備分類コード
	 * @param equipmentCode               設備コード
	 * @param sid                         利用者ID
	 * @param useDate                     利用日
	 * @param itemDataMap                 項目データMap
	 * @return 設備利用実績データ
	 */
	public static EquipmentData createData(Require require, String cid,
			EquipmentClassificationCode equipmentClassificationCode, EquipmentCode equipmentCode, String sid,
			GeneralDate useDate, Map<EquipmentItemNo, ActualItemUsageValue> itemDataMap) {
		// $項目データ ＝ 項目データMap：map 実績データ#新規追加(require、会社ID、$.項目NO、$.項目値)
		List<ResultData> resultDatas = itemDataMap.entrySet().stream()
				.map(entry -> ResultData.createData(require, cid, entry.getKey(), entry.getValue()))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		return new EquipmentData(GeneralDateTime.now(), useDate, sid, equipmentCode, equipmentClassificationCode,
				resultDatas);
	}

	/**
	 * [1] 項目値を変更する
	 * @param itemDatas		項目データList
	 */
	public void updateResultDatas(List<ItemData> itemDatas) {
		// ＠項目データ：　$. 項目値を変更する(項目データList)
		this.resultDatas.forEach(data -> data.updateValue(itemDatas));
	}
}
