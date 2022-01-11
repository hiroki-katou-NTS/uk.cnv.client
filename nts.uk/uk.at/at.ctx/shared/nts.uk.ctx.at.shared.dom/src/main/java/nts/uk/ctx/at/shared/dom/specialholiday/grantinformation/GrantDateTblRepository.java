package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayCode;

/**
 * 特別休暇付与日数テーブル
 * @author masaaki_jinno
 *
 */
public interface GrantDateTblRepository {

	/**
	 *
	 * @param companyId
	 * @param specialHolidayCode
	 * @param grantDateCode
	 * @return
	 */
	Optional<GrantDateTbl> findByCode(
			CompanyId companyId, SpecialHolidayCode specialHolidayCode, GrantDateCode grantDateCode);

	/**
	 * Find Grant Date by Code
	 * @param companyId
	 * @param grantDateCode
	 * @return
	 */
	Optional<GrantDateTbl> findByCode(String companyId, int specialHolidayCode, String grantDateCode);

	/**
	 * [1]  insert(会社別月単位労働時間)
	 * Add new Grant Date Table
	 */
	void add(GrantDateTbl specialHoliday);

	/**
	 * [2]  update(会社別月単位労働時間)
	 * Update Grant Date Table
	 */
	void update(GrantDateTbl specialHoliday, GrantDateTbl specialHoliday2);

	/**
	 * [3]  delete(会社別月単位労働時間)
	 * Delete Grant Date Table
	 */
	void delete(String companyId, int specialHolidayCode, String grantDateCode);

	/**
	 * [4]  取得する
	 * Find all Grant Date Table data by Special Holiday Code
	 */
	List<GrantDateTbl> findBySphdCd(String companyId, int specialHolidayCode);

////	/**
////	 * đối ứng cho màn cps003
////	 * Find Grant Date by Code
////	 * @param companyId
////	 * @param list grantDateCode
////	 * @return
////	 */
////	Map<String, List<ElapseYear>> findElapseByGrantDateCdLst(String companyId, int specialHolidayCode, List<String> grantDateCode);
//
//	/**
//	 * Find Elapse by Grant Date Code
//	 * @param companyId
//	 * @param specialHolidayCode
//	 * @param grantDateCode
//	 * @return
//	 */
//	List<ElapseYear> findElapseByGrantDateCd(String companyId, int specialHolidayCode, String grantDateCode);
//
//

	/**
	 *
	 * @param specialHolidayCode
	 */
	void changeAllProvision(int specialHolidayCode);

//	/**
//	 * get 特別休暇付与テーブル with 規定のテーブルとする: True
//	 * @param companyId
//	 * @param specialHolidayCode
//	 * @return
//	 */
//	Optional<GrantDateTbl> findByCodeAndIsSpecified(String companyId, int specialHolidayCode);

}
