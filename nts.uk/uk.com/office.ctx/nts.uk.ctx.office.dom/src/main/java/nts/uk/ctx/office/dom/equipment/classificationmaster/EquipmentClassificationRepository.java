package nts.uk.ctx.office.dom.equipment.classificationmaster;

import java.util.List;
import java.util.Optional;

/**
 * 設備分類Repository
 * @author NWS-DungDV
 *
 */
public interface EquipmentClassificationRepository {
	
	/**
	 * [1] Insert(設備分類)
	 * @param domain 設備分類
	 */
	void insert(EquipmentClassification domain);
	
	/**
	 * [2] Update(設備分類)
	 * @param domain 設備分類
	 */
	void update(EquipmentClassification domain);
	
	/**
	 * [3] Delete(契約コード, 設備分類コード)
	 * @param contractCd 契約コード
	 * @param equipmentClsCd 設備分類コード
	 */
	void delete(String contractCd, String equipmentClsCd);
	
	/**
	 * [4] Get*
	 * 全ての設備分類を取得する
	 * @param contractCd 契約コード
	 * @return 設備分類リスト
	 */
	List<EquipmentClassification> getAll(String contractCd);

	/**
	 * [5] Get
	 * 指定する設備分類コードの設備分類を取得する
	 * @param contractCd 契約コード
	 * @param clsCd コード
	 * @return 設備分類
	 */
	Optional<EquipmentClassification> getByClassificationCode(String contractCd, String clsCd);
	
	/**
	 * [6] Get*
	 * 分類コードから設備分類Listを取得する
	 * @param contractCd 契約コード
	 * @param clsCds コードList
	 * @return 設備分類リスト
	 */
	List<EquipmentClassification> getFromClsCodeList(String contractCd, List<String> clsCds); 

	/**
	 * [7] 最小の設備分類を取得する
	 * 最小の設備分類コードの設備分類を取得する																					
	 * @param contractCd 契約コード
	 * @return 設備分類
	 */
	Optional<EquipmentClassification> getFirst(String contractCd);
	
	/**
	 * [8] 初期の設備分類を取得する
	 * 設備分類コードがないけれど、最小の設備分類コードの設備分類を取得する
	 * 設備分類コードがあって、DBにはなくて、最小の設備分類コードの設備分類を取得する
	 * 設備分類コードがあって、DBにもあって、もらう分類コードの設備分類を取得する
	 * @param contractCd 契約コード
	 * @param optClsCd コード
	 * @return 設備分類
	 */
	Optional<EquipmentClassification> getByOptionalCode(String contractCd, Optional<String> optClsCd);
}
