package nts.uk.ctx.office.dom.equipment.achievement.repo;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;

public interface EquipmentRecordItemSettingRepository {

	// [1] InsertAll(List<設備利用実績の項目設定>)
	void insertAll(List<EquipmentUsageRecordItemSetting> domains);

	// [2] DeleteAll(会社ID)
	void deleteAll(String cid);

	// [3] Get*
	List<EquipmentUsageRecordItemSetting> findByCid(String cid);

	// [4] Get
	Optional<EquipmentUsageRecordItemSetting> findByCidAndItemNo(String cid, String itemNo);
	
	// [5] Get*																							
	List<EquipmentUsageRecordItemSetting> findByCidAndItemNos(String cid, List<String> itemNos);
	
	// [6] 削除後Insertする																							
	void insertAllAfterDeleteAll(String cid, List<EquipmentUsageRecordItemSetting> domains);
}
