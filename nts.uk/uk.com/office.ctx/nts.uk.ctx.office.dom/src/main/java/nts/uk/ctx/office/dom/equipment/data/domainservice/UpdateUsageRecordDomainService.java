package nts.uk.ctx.office.dom.equipment.data.domainservice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ErrorItem;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.ItemData;
import nts.uk.ctx.office.dom.equipment.data.RegisterResult;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.利用実績を変更登録する
 */
public class UpdateUsageRecordDomainService {

	/**
	 * [1]変更する
	 * 
	 * @param require
	 * @param cid              会社ID
	 * @param equipmentCode    会社ID
	 * @param inputDate        日時
	 * @param equipmentClsCode 設備分類コード
	 * @param sid              社員ID
	 * @param useDate          年月日
	 * @param itemDatas        List<項目データ>
	 * @return 登録する結果
	 */
	public static RegisterResult update(Require require, String cid, EquipmentCode equipmentCode,
			GeneralDateTime inputDate, EquipmentClassificationCode equipmentClsCode, String sid, GeneralDate useDate,
			List<ItemData> itemDatas) {
		Optional<EquipmentData> optEquipmentData = require.getEquipmentData(cid, equipmentCode, useDate, sid,
				inputDate);
		if (!optEquipmentData.isPresent()) {
			throw new BusinessException("Msg_2319");
		}
		List<EquipmentItemNo> itemNos = itemDatas.stream().map(ItemData::getItemNo).collect(Collectors.toList());

		// 設備利用実績の項目設定
		List<EquipmentUsageRecordItemSetting> itemSettings = require.getItemSettings(cid, itemNos);
		List<ErrorItem> errorItems = itemDatas.stream()
				.map(data -> UpdateUsageRecordDomainService.validateItemLimit(data, itemSettings))
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		// エラーがある
		if (!errorItems.isEmpty()) {
			return RegisterResult.withErrors(errorItems);
		}
		optEquipmentData.get().updateResultDatas(itemDatas);
		AtomTask persistTask = AtomTask.of(() -> require.updateEquipmentData(optEquipmentData.get()));
		return RegisterResult.withoutErrors(persistTask);
	}

	/**
	 * [prv-1]項目制限をチェックする結果を取得する
	 * 
	 * @param itemData     項目データ
	 * @param itemSettings 項目設定List
	 * @return Optional<エラー項目>
	 */
	private static Optional<ErrorItem> validateItemLimit(ItemData itemData,
			List<EquipmentUsageRecordItemSetting> itemSettings) {
		return itemSettings.stream().filter(data -> data.getItemNo().equals(itemData.getItemNo())).findFirst()
				.map(data -> data.check(Optional.ofNullable(itemData.getActualValue())).orElse(null));
	}

	public interface Require {

		// [R-1]設備利用実績の項目設定を取得する
		List<EquipmentUsageRecordItemSetting> getItemSettings(String cid, List<EquipmentItemNo> itemNos);

		// [R-2]設備利用実績データを取得する
		Optional<EquipmentData> getEquipmentData(String cid, EquipmentCode equipmentCode, GeneralDate useDate,
				String sid, GeneralDateTime inputDate);

		// [R-3]設備利用実績データをUpdateする
		void updateEquipmentData(EquipmentData domain);
	}
}
