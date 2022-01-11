package nts.uk.ctx.at.shared.infra.repository.specialholiday;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayCode;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateCode;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTbl;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTblRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantElapseYearMonth;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantinformation.KshmtHdspGrantTbl;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantinformation.KshmtHdspElapsedGrantDaysTbl;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantinformation.KshmtHdspElapsedGrantDaysTblPK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantinformation.KshstGrantDateTblPK;
import nts.uk.shr.com.context.AppContexts;

/**
 * 特別休暇付与日数テーブル
 * @author tanlv
 *
 */
@Stateless
public class JpaGrantDateTblRepository extends JpaRepository implements GrantDateTblRepository {
	
	private final static String SELECT_GRANDATE_BY_CODE_QUERY = "SELECT e.pk.grantDateCd, e.grantName, e.isSpecified, e.fixedAssign, e.numberOfDays "
			+ "FROM KshmtHdspGrantTbl e "
			+ "WHERE e.pk.companyId = :companyId AND e.pk.specialHolidayCode = :specialHolidayCode AND e.pk.grantDateCd = :grantDateCd "
			+ "ORDER BY e.pk.grantDateCd ASC";
	
	private final static String SELECT_ELAPSE_BY_GDCD_QUERY = "SELECT e.pk.specialHolidayCode, e.pk.grantDateCd, e.pk.elapseNo, e.grantedDays, e.months, e.years "
			+ "FROM KshmtHdspElapseYears e "
			+ "WHERE e.pk.companyId = :companyId AND e.pk.specialHolidayCode = :specialHolidayCode AND e.pk.grantDateCd = :grantDateCd "
			+ "ORDER BY e.pk.elapseNo ASC";
	
	private final static String SELECT_ELAPSE_BY_GDCD_LST_QUERY = "SELECT e.pk.specialHolidayCode, e.pk.grantDateCd, e.pk.elapseNo, e.grantedDays, e.months, e.years "
			+ "FROM KshmtHdspElapseYears e "
			+ "WHERE e.pk.companyId = :companyId AND e.pk.specialHolidayCode = :specialHolidayCode AND e.pk.grantDateCd IN :grantDateCd "
			+ "ORDER BY e.pk.elapseNo ASC";
	
	private final static String DELETE_All_ELAPSE = "DELETE FROM KshmtHdspElapseYears e "
			+ "WHERE e.pk.companyId =:companyId "
			+ "AND e.pk.grantDateCd =:grantDateCd "
			+ "AND e.pk.specialHolidayCode =:specialHolidayCode ";

	private final static String SELECT_GD_BY_SPHDCD_QUERY
		= "SELECT e.pk.companyId ,  e.pk.specialHolidayCode,  e.pk.grantDateCd, e.grantName, e.isSpecified, e.numberOfDays "
			+ "FROM KshmtHdspGrantTbl e "
			+ "WHERE e.pk.companyId = :companyId AND e.pk.specialHolidayCode = :specialHolidayCode "
			+ "ORDER BY e.pk.grantDateCd ASC";

	private final static String CHANGE_ALL_PROVISION = "UPDATE KshmtHdspGrantTbl e SET e.isSpecified = 0 "
			+ "WHERE e.pk.companyId = :companyId AND e.pk.specialHolidayCode = :specialHolidayCode";

	/**
	 * Create Grant Date Domain From Entity
	 * @param c
	 * @return
	 */
	private GrantDateTbl createDomainFromEntity(KshmtHdspGrantTbl c) {

		return GrantDateTbl.createFromJavaType(
				c.pk.companyId,
				c.pk.specialHolidayCode,
				c.pk.grantDateCd,
				c.grantName,
				c.isSpecified == 1,
				c.numberOfDays);
	}

	/**
	 * Create Grant Date from Domain
	 * @param domain
	 * @return
	 */
	private KshmtHdspGrantTbl toEntity(GrantDateTbl domain) {

		// 特別休暇付与日数テーブル
		KshstGrantDateTblPK pk = new KshstGrantDateTblPK(
				domain.getCompanyId(), domain.getSpecialHolidayCode().v(), domain.getGrantDateCode().v());

		Integer grantDays = null;
		if (domain.getGrantedDays().isPresent()){
			grantDays = domain.getGrantedDays().get().v();
		}

		KshmtHdspGrantTbl kshstGrantDateTbl
			= new KshmtHdspGrantTbl(
					pk,
					domain.getGrantDateName().v(),
					domain.isSpecified() ? 1 : 0,
					grantDays);

		return kshstGrantDateTbl;
	}

	/**
	 * 取得
	 */
	@Override
	public Optional<GrantDateTbl> findByCode(
			String companyId, int specialHolidayCode, String grantDateCode) {

		// 「特別休暇付与日数テーブル」
		Optional<GrantDateTbl> e = this.queryProxy()
				.find(new KshstGrantDateTblPK(
						companyId, specialHolidayCode, grantDateCode),
						KshmtHdspGrantTbl.class)
				.map(x -> this.createDomainFromEntity(x));

		if ( !e.isPresent()){ // 取得できないとき
			return Optional.empty();
		}

		// 「特別休暇経過付与日数テーブル」
		List<GrantElapseYearMonth> elapseYearList
			= findGrantElapseYearMonth(companyId, specialHolidayCode, grantDateCode);
		e.get().setElapseYear(elapseYearList);

		return e;
	}


	/**
	 * 削除
	 */
	@Override
	public void delete(String companyId, int specialHolidayCode, String grantDateCode) {

		// 「特別休暇付与日数テーブル」
		this.commandProxy().remove(
				KshmtHdspGrantTbl.class,
				new KshstGrantDateTblPK(companyId, specialHolidayCode, grantDateCode));

		// 「特別休暇経過付与日数テーブル」
		deleteGrantElapseYearMonth(companyId, specialHolidayCode, grantDateCode);
	}

	/**
	 * 更新
	 * @param grantDateTbl
	 */
	public void update(GrantDateTbl grantDateTbl, GrantDateTbl grantDateTbl2) {

		// 会社ID
		String companyId = grantDateTbl.getCompanyId();
		// 特別休暇コード
		int specialHolidayCode = grantDateTbl.getSpecialHolidayCode().v();
		// 付与テーブルコード
		String grantDateCode = grantDateTbl.getGrantDateCode().v();

		// 「特別休暇付与日数テーブル」 前処理
		// Entityへ変換
		KshmtHdspGrantTbl kshstGrantDateTbls = this.toEntity(grantDateTbl);

		// 「特別休暇経過付与日数テーブル」 前処理
		// Entityへ変換
		List<GrantElapseYearMonth> grantElapseYearMonthList
			= grantDateTbl2.getElapseYear();

		List<KshmtHdspElapsedGrantDaysTbl> kshstGrantDateElapseYearsTblList
			= new ArrayList<KshmtHdspElapsedGrantDaysTbl>();

		for(GrantElapseYearMonth e: grantElapseYearMonthList){
			kshstGrantDateElapseYearsTblList.add(
					toEntity(companyId, specialHolidayCode, grantDateCode, e));
		}

		// 「特別休暇付与日数テーブル」
		// 削除
		this.delete(companyId, specialHolidayCode, grantDateCode);
		// 追加
		this.commandProxy().insert(kshstGrantDateTbls);

		// 「特別休暇経過付与日数テーブル」
		// 削除
		this.deleteGrantElapseYearMonth(companyId, specialHolidayCode, grantDateCode);
		// 追加
		this.addGrantElapseYearMonth(kshstGrantDateElapseYearsTblList);
	}

	// -----------------------------------------------------------------------
	// 以下、特別休暇経過付与日数テーブル GrantElapseYearMonth

	private final static String SELECT_GRANT_DATE_ELAPSE_YEARS_TBL_QUERY
		= "SELECT e "
		+ "FROM KshmtHdspElapsedGrantDaysTbl e "
		+ "WHERE e.pk.companyId = :companyId AND e.pk.specialHolidayCode = :specialHolidayCode "
		+ "AND e.pk.grantDateCd = :grantDateCd ";

	private final static String DELETE_GRANT_DATE_ELAPSE_YEARS_TBL = "DELETE FROM KshmtHdspElapsedGrantDaysTbl e "
		+ "WHERE e.pk.companyId = :companyId AND e.pk.specialHolidayCode = :specialHolidayCode "
		+ "AND e.pk.grantDateCd = :grantDateCd ";

	/**
	 * Create Domain From Entity
	 * @param c
	 * @return
	 */
	private GrantElapseYearMonth createDomainFromEntity(KshmtHdspElapsedGrantDaysTbl e) {
//		String companyId = e.pk.companyId;
//		int specialHolidayCode = e.pk.specialHolidayCode;
//		String grantDateCd = e.pk.grantDateCd;
		int grantCnt = e.pk.grantCnt;
		int grantDays = e.grantDays;

		return GrantElapseYearMonth.createFromJavaType(grantCnt, grantDays);
	}

	/**
	 * Create Elapse Year Domain From Entity
	 * @param c
	 * @return
	 */
	private List<GrantElapseYearMonth> createDomainFromEntityList(
			List<KshmtHdspElapsedGrantDaysTbl> list) {

		List<GrantElapseYearMonth> listOut = new ArrayList<GrantElapseYearMonth>();
		for( KshmtHdspElapsedGrantDaysTbl kshstGrantDateElapseYearsTbl : list){
			listOut.add(createDomainFromEntity(kshstGrantDateElapseYearsTbl));
		}
		return listOut;
	}

	/**
	 * Convert domain to entity
	 * @param domain
	 * @return
	 */
	private KshmtHdspElapsedGrantDaysTbl toEntity(
			String companyId,
			int specialHolidayCode,
			String grantDateCode,
			GrantElapseYearMonth domain) {

		KshmtHdspElapsedGrantDaysTblPK pk = new KshmtHdspElapsedGrantDaysTblPK(companyId,
				specialHolidayCode, grantDateCode, domain.getElapseNo());

		return new KshmtHdspElapsedGrantDaysTbl(pk, domain.getGrantedDays().v());
	}

	/**
	 * 取得
	 * @param companyId 会社ID
	 * @param specialHolidayCode　特別休暇コード
	 * @param grantDateCode　付与テーブルコード
	 * @return List<GrantDateTbl>
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private List<GrantElapseYearMonth> findGrantElapseYearMonth(
			String companyId, int specialHolidayCode, String grantDateCode) {

		List<KshmtHdspElapsedGrantDaysTbl> list
			= this.queryProxy().query(
				SELECT_GRANT_DATE_ELAPSE_YEARS_TBL_QUERY, KshmtHdspElapsedGrantDaysTbl.class)
				.setParameter("companyId", companyId)
				.setParameter("specialHolidayCode", specialHolidayCode)
				.setParameter("grantDateCd", grantDateCode)
				.getList();

		List<GrantElapseYearMonth> listOut
			= createDomainFromEntityList(list);

		return listOut;
	}

	/**
	 * 取得
	 */
	@Override
	public Optional<GrantDateTbl> findByCode(
			CompanyId companyId, SpecialHolidayCode specialHolidayCode,
			GrantDateCode grantDateCode) {
		return findByCode(companyId.v(), specialHolidayCode.v(), grantDateCode.v());
	}

	/**
	 * 削除
	 * @param companyId 会社ID
	 * @param specialHolidayCode 特別休暇コード
	 * @param grantDateCode 付与テーブルコード
	 */
	private void deleteGrantElapseYearMonth(
			String companyId, int specialHolidayCode, String grantDateCode) {

		this.getEntityManager().createQuery(DELETE_GRANT_DATE_ELAPSE_YEARS_TBL)
			.setParameter("companyId", companyId)
			.setParameter("specialHolidayCode", specialHolidayCode)
			.setParameter("grantDateCd", grantDateCode)
			.executeUpdate();
	}

	/**
	 * 追加
	 * @param list
	 */
	private void addGrantElapseYearMonth(List<KshmtHdspElapsedGrantDaysTbl> list) {
		this.commandProxy().insertAll(list);
	}

	/**
	 * 更新
	 */
	public void updateGrantElapseYearMonth(
			String companyId, int specialHolidayCode, String grantDateCode,
			List<KshmtHdspElapsedGrantDaysTbl> list) {

		// 削除
		deleteGrantElapseYearMonth(companyId, specialHolidayCode, grantDateCode);
		// 追加
		addGrantElapseYearMonth(list);
	}

	@Override
	public void add(GrantDateTbl specialHoliday) {
		// TODO 自動生成されたメソッド・スタブ

	}

	/**
	 * Create Grant Date Domain From Entity
	 * @param c
	 * @return
	 */
	private GrantDateTbl createGdDomainFromEntity(Object[] c) {
		String companyId = String.valueOf(c[0]);
		int specialHolidayCode = Integer.parseInt(String.valueOf(c[1]));
		String grantDateCd = String.valueOf(c[2]);
		String grantName = String.valueOf(c[3]);
		boolean isSpecified = Integer.parseInt(String.valueOf(c[4])) == 1 ? true : false;
		int numberOfDays = c[5] != null ? Integer.parseInt(String.valueOf(c[5])) : 0;

		return GrantDateTbl.createFromJavaType(
				companyId, specialHolidayCode, grantDateCd, grantName, isSpecified, numberOfDays);
	}

	/**
	 * Find all Grant Date Table data by Special Holiday Code
	 * @param companyId
	 * @param specialHolidayCode
	 * @return
	 */
//	@Override
//	public List<GrantDateTbl> findBySphdCd(String companyId, int specialHolidayCode){
//
//		return new ArrayList<GrantDateTbl>();
//	}

	@Override
	public List<GrantDateTbl> findBySphdCd(String companyId, int specialHolidayCode) {
		return this.queryProxy().query(SELECT_GD_BY_SPHDCD_QUERY, Object[].class)
				.setParameter("companyId", companyId)
				.setParameter("specialHolidayCode", specialHolidayCode)
				.getList(c -> {
					return createGdDomainFromEntity(c);
				});
	}

	@Override
	public void changeAllProvision(int specialHolidayCode) {
		String companyId = AppContexts.user().companyId();

		this.getEntityManager().createQuery(CHANGE_ALL_PROVISION)
					.setParameter("companyId", companyId)
					.setParameter("specialHolidayCode", specialHolidayCode)
					.executeUpdate();
	}


//	@Override
//	public Optional<GrantDateTbl> findByCodeAndIsSpecified(String companyId, int specialHolidayCode) {
//		return this.queryProxy().query(SELECT_CODE_ISSPECIAL, KshmtHdspGrantTbl.class)
//				.setParameter("companyId", companyId)
//				.setParameter("specialHolidayCode", specialHolidayCode)
//				.getSingle(c -> {
//					String grantDateCd = String.valueOf(c.pk.grantDateCd);
//					String grantName = String.valueOf(c.grantName);
//					boolean isSpecified = Integer.parseInt(String.valueOf(c.isSpecified)) == 1 ? true : false;
////					boolean fixedAssign = Integer.parseInt(String.valueOf(c.fixedAssign)) == 1 ? true : false;
//					int numberOfDays = c.numberOfDays != null ? Integer.parseInt(String.valueOf(c.numberOfDays)) : 0;
//
//					return GrantDateTbl.createFromJavaType(grantDateCd, grantName, isSpecified, numberOfDays);
//				});
//	}
//
//	@Override
//	public Map<String, List<GrantDateTbl>> findElapseByGrantDateCdLst(String companyId, int specialHolidayCode, List<String> grantDateCode) {
//		if(grantDateCode.isEmpty()) return new HashMap<>();
//		List<GrantDateTbl> result = this.queryProxy().query(SELECT_ELAPSE_BY_GDCD_LST_QUERY, Object[].class)
//					.setParameter("companyId", companyId)
//					.setParameter("specialHolidayCode", specialHolidayCode)
//					.setParameter("grantDateCd", grantDateCode)
//				.getList(c -> {
//					return createDomainFromEntity(c);
//				});
//		 return result.stream().collect(Collectors.groupingBy(c -> c.getGrantDateCode()));
//	}

//	@Override
//	public List<GrantDateTbl> findElapseByGrantDateCd(String companyId, int specialHolidayCode, String grantDateCode) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void delete(String companyId, int specialHolidayCode, String grantDateCode) {
//		// TODO Auto-generated method stub
//
//	}
}
