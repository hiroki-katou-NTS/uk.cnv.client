package nts.uk.ctx.office.dom.equipment.achievement.repo;

import java.util.Optional;

import nts.uk.ctx.office.dom.equipment.achievement.EquipmentFormSetting;

public interface EquipmentFormSettingRepository {
	
	// [1] Insert(設備帳票設定)
	void insert(EquipmentFormSetting domain);

	// [2] Delete(会社ID)
	void delete(String cid);

	// [3] Get
	Optional<EquipmentFormSetting> findByCid(String cid);
	
	// [4] 削除後Insertする																							
	void insertAfterDelete(EquipmentFormSetting domain);
}
