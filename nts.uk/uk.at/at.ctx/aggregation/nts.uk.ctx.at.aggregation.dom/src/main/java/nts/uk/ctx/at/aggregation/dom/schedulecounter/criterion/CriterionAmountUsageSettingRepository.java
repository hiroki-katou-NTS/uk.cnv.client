package nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion;

import java.util.Optional;

/**
 * 目安利用区分Repository
 * @author lan_lt
 *
 */
public interface CriterionAmountUsageSettingRepository {

	/**
	 * insert
	 * @param cid 会社ID
	 * @param usageSetting 目安利用区分
	 */
	void insert(CriterionAmountUsageSetting usageSetting);

	/**
	 * update
	 * @param cid 会社ID
	 * @param usageSetting 目安利用区分
	 */
	void update(CriterionAmountUsageSetting usageSetting);

	/**
	 * exists
	 * @param cid 会社ID
	 * @return
	 */
	boolean exist(String cid);

	/**
	 * get
	 * @param cid 会社ID
	 * @return
	 */
	Optional<CriterionAmountUsageSetting> get(String cid);

}
