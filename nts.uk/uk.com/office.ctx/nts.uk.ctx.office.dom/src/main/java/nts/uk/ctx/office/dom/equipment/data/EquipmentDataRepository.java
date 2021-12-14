package nts.uk.ctx.office.dom.equipment.data;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;

public interface EquipmentDataRepository {

	// [1] Insert(設備利用実績データ)
	void insert(EquipmentData domain);

	// [2] Update(設備利用実績データ)
	void update(EquipmentData domain);

	// [3] Delete(会社ID, 利用者ID、入力日)
	void delete(String cid, String sid, GeneralDateTime inputDate);

	// [4] Get*
	List<EquipmentData> findByEquipmentCodeAndPeriod(String cid, String equipmentCode, DatePeriod period);

	// [5] Get*
	List<EquipmentData> findByEquipmentClsCodeAndEquipmentCodeAndPeriod(String cid, String equipmentClsCode,
			String equipmentCode, DatePeriod period);

	// [6] Get
	Optional<EquipmentData> findByUsageInfo(String cid, String equipmentCode, GeneralDate useDate, String sid,
			GeneralDateTime inputDate);

	// [7] Get*
	List<EquipmentData> findByEquipmentClsCodeAndPeriod(String cid, String equipmentClsCode, DatePeriod period);

	// [8] Get*
	List<EquipmentData> findByPeriod(String cid, DatePeriod period);

	// [9]期間、設備コード、設備分類コードから実績データを取得する
	List<EquipmentData> findByPeriodAndOptionalInput(String cid, DatePeriod period, Optional<String> optEquipmentCode,
			Optional<String> optEquipmentClsCode);
}
