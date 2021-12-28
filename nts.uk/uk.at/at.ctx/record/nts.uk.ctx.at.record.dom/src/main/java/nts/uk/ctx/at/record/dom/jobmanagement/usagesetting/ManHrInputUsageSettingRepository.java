package nts.uk.ctx.at.record.dom.jobmanagement.usagesetting;

import java.util.Optional;

/**
 * RP: 工数入力の利用設定Repository
 * @author tutt
 *
 */
public interface ManHrInputUsageSettingRepository {
	
	/**
	 * [1] Insert(工数入力の利用設定)
	 * @param usageSetting
	 */
	void insert(ManHrInputUsageSetting usageSetting);
	
	/**
	 * [2] Update(工数入力の利用設定)
	 * @param usageSetting
	 */
	void update(ManHrInputUsageSetting usageSetting);
	
	/**
	 * [3] Get
	 * @param cId
	 * @return
	 */
	Optional<ManHrInputUsageSetting> get(String cId);
	
}
