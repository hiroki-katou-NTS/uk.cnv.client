package nts.uk.ctx.office.dom.equipment.information;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;

public interface EquipmentInformationRepository {
	
	//[1] Insert(設備情報)																							
	void insert(EquipmentInformation domain);
	
	//[2] Update(設備情報)																							
	void update(EquipmentInformation domain);
	
	//[3] Delete(会社ID, 設備コード)																							
	void delete(String cid, String code);
	
	//[4] Get*																							
	List<EquipmentInformation> findByCid(String cid);
	
	//[5] Get																							
	Optional<EquipmentInformation> findByPk(String cid, String code);
	
	//[6] Get*
	List<EquipmentInformation> findByCidAndClsCode(String cid, String equipmentClsCode);
	
	//[7] Get*																							
	List<EquipmentInformation> findByCidAndCodes(String cid, List<String> codes);
	
	//[8] 分類コードの有効の設備情報を取得する
	List<EquipmentInformation> findByClsCodeAndDate(String cid, String equipmentClsCode, GeneralDate date);
	
	//[9] Get
	List<EquipmentInformation> findByCode(String equipmentClsCode);
}
