package nts.uk.ctx.office.dom.equipment.data.domainservice;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ErrorItem;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.ItemData;
import nts.uk.ctx.office.dom.equipment.data.RegisterResult;
import nts.uk.ctx.office.dom.equipment.data.ResultData;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.利用実績を新規登録する
 */
public class InsertUsageRecordDomainService {

	/**
	 * [1] 新規登録する
	 * 
	 * @param require
	 * @param cid							会社ID
	 * @param equipmentCode					設備コード
	 * @param useDate						利用日
	 * @param sid							利用者ID
	 * @param equipmentClassificationCode	設備分類コード
	 * @param itemDatas						項目データList
	 * @return 登録する結果
	 */
	public static RegisterResult insert(Require require, String cid, EquipmentCode equipmentCode, GeneralDate useDate,
			String sid, EquipmentClassificationCode equipmentClassificationCode, List<ItemData> itemDatas) {
		// 設備の有効期限をチェックする
		Optional<EquipmentInformation> optEquipmentInfo = require.getEquipmentInfo(cid, equipmentCode);
		if (optEquipmentInfo.isPresent() && !optEquipmentInfo.get().isValid(useDate)) {
			throw new BusinessException("Msg_2233");
		}
		List<EquipmentItemNo> itemNos = itemDatas.stream().map(ItemData::getItemNo).collect(Collectors.toList());
		// 設備利用実績の項目設定
		List<EquipmentUsageRecordItemSetting> itemSettings = require.getItemSettings(cid, itemNos);
		List<ErrorItem> errorItems = itemDatas.stream()
				.map(data -> InsertUsageRecordDomainService.validateItemLimit(data, itemSettings))
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		// エラーがある
		if (!errorItems.isEmpty()) {
			return RegisterResult.withErrors(errorItems);
		}
		// エラーがない、登録する
		Map<EquipmentItemNo, ActualItemUsageValue> itemDataMap = itemDatas.stream()
				.collect(Collectors.toMap(ItemData::getItemNo, ItemData::getActualValue));
		EquipmentData equipmentData = EquipmentData.createData(require, cid, equipmentClassificationCode,
				equipmentCode, sid, useDate, itemDataMap);
		AtomTask persistTask = AtomTask.of(() -> require.insertEquipmentData(equipmentData));
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

	public interface Require extends ResultData.Require {

		// [R-1] 設備情報を取得する
		Optional<EquipmentInformation> getEquipmentInfo(String cid, EquipmentCode equipmentCode);

		// [R-2]設備利用実績の項目設定を取得する
		List<EquipmentUsageRecordItemSetting> getItemSettings(String cid, List<EquipmentItemNo> itemNos);

		// [R-3]設備利用実績データをInsertする
		void insertEquipmentData(EquipmentData domain);
	}
}
