package nts.uk.ctx.at.shared.dom.specialholiday;

import java.util.List;
import java.util.Optional;

/**
 * Special Holiday Repository
 * 
 * @author tanlv
 *
 */
public interface SpecialHolidayRepository {
	/**
	 * Find By CompanyId
	 * @param companyId
	 * @return
	 */
	List<SpecialHoliday> findByCompanyId(String companyId);
	
	/**
	 * 
	 * @param companyId
	 * @param specialHolidayCodes
	 * @return
	 */
	List<SpecialHoliday> findSimpleByListCode(String companyId, List<Integer> specialHolidayCodes);
	
	/**
	 * Add Special Holiday
	 * @param specialHoliday
	 */
	void add(SpecialHoliday specialHoliday);

	/**
	 * Update Special Holiday
	 * @param specialHoliday
	 */
	void update(SpecialHoliday specialHoliday);
	
	/**
	 * Delete Special Holiday
	 * @param companyId
	 * @param specialHolidayCode
	 */
	void delete(String companyId, int specialHolidayCode);
	
	Optional<SpecialHoliday> findBySingleCD(String companyID, int specialHolidayCD);
	/**
	 * 欠勤枠Noから特別休暇CDを取得する
	 * @param cid
	 * @param absFrameNo 特別休暇枠NO
	 * @return
	 */
	List<Integer> findByAbsframeNo(String cid, int absFrameNo);
	/**
	 * Find By CompanyId
	 * @param companyId
	 * @return
	 */
	List<SpecialHoliday> findByCompanyIdWithTargetItem(String companyId);
	
	/**
	 * 特別休暇枠Noから特別休暇CDを取得する
	 * @param cid
	 * @param absFrameNo 特別休暇枠NO
	 * @return
	 */
	List<Integer> findBySphdSpecLeave(String cid, int sphdSpecLeaveNo);
	
	/**
	 * Find By CompanyId
	 * @param companyId
	 * @return
	 */
	List<SpecialHoliday> findSimpleByCompanyIdNoMaster(String companyId, List<Integer> specialHolidayCodes);
}
