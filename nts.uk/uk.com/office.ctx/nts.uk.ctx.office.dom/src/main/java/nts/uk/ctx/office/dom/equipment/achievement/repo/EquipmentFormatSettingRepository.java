package nts.uk.ctx.office.dom.equipment.achievement.repo;

import java.util.Optional;

import nts.uk.ctx.office.dom.equipment.achievement.EquipmentPerformInputFormatSetting;

/**
 * 設備の実績入力フォーマット設定Repository
 * @author NWS-DungDV
 *
 */
public interface EquipmentFormatSettingRepository {
	
	/**
	 * [1] Insert(設備の実績入力フォーマット設定)
	 * @param domain 設備の実績入力フォーマット設定
	 */
	void insert(EquipmentPerformInputFormatSetting domain);
	
	/**
	 * [2] Delete(会社ID)
	 * @param cid 会社ID
	 */
	void delete(String cid);
	
	/**
	 * [3] Get
	 * @param cid 会社ID
	 * @return 設備の実績入力フォーマット設定
	 */
	Optional<EquipmentPerformInputFormatSetting> get(String cid);
	
	/**
	 * [4] 削除後Insertする
	 * @param domain	設備の実績入力フォーマット設定
	 */
	void insertAfterDelete(EquipmentPerformInputFormatSetting domain);
}
