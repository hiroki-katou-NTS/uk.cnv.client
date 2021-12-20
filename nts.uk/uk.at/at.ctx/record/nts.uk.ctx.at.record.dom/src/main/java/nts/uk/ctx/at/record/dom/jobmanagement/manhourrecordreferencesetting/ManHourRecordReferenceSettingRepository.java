package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting;

import java.util.Optional;

/**
 * @author thanhpv
 * @name 	工数実績参照設定Repository	
 */
public interface ManHourRecordReferenceSettingRepository {

	/**
	 * @name [1] Insert(工数実績参照設定)
	 */
	void insert(ManHourRecordReferenceSetting domain);
	
	/**
	 * @name [2] Delete(工数実績参照設定)
	 */
	void delete(ManHourRecordReferenceSetting domain);
	
	/**
	 * @name [3] Update(工数実績参照設定)
	 */
	void update(ManHourRecordReferenceSetting domain);
	
	/**
	 * @name [4] Get
	 * @Description 工数実績参照設定を取得する			
	 * @input companyId 会社ID
	 * @output Optional<工数実績参照設定>
	 */
	Optional<ManHourRecordReferenceSetting> get(String companyId);
}
