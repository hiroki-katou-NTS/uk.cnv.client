package nts.uk.ctx.pereg.infra.repository.person.info.ctg;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.pereg.dom.person.info.category.PerInfoCategoryRepositoty;
import nts.uk.ctx.pereg.dom.person.info.category.PersonInfoCategory;
import nts.uk.ctx.pereg.dom.person.info.daterangeitem.DateRangeItem;
import nts.uk.ctx.pereg.infra.entity.person.info.ctg.PpemtDateRangeItem;
import nts.uk.ctx.pereg.infra.entity.person.info.ctg.PpemtPerInfoCtg;
import nts.uk.ctx.pereg.infra.entity.person.info.ctg.PpemtPerInfoCtgCm;
import nts.uk.ctx.pereg.infra.entity.person.info.ctg.PpemtPerInfoCtgCmPK;
import nts.uk.ctx.pereg.infra.entity.person.info.ctg.PpemtPerInfoCtgOrder;
import nts.uk.ctx.pereg.infra.entity.person.info.ctg.PpemtPerInfoCtgPK;

@Stateless
public class JpaPerInfoCategoryRepositoty extends JpaRepository implements PerInfoCategoryRepositoty {

	private final static String SPECIAL_CTG_CODE = "CO";

	private final static String SELECT_CATEGORY_BY_COMPANY_ID_QUERY = "SELECT ca.ppemtPerInfoCtgPK.perInfoCtgId,"
			+ " ca.categoryCd, ca.categoryName, ca.abolitionAtr,"
			+ " co.categoryParentCd, co.categoryType, co.personEmployeeType, co.fixedAtr, po.disporder"
			+ " FROM PpemtPerInfoCtg ca INNER JOIN PpemtPerInfoCtgCm co"
			+ " ON ca.categoryCd = co.ppemtPerInfoCtgCmPK.categoryCd"
			+ " INNER JOIN PpemtPerInfoCtgOrder po ON ca.cid = po.cid AND"
			+ " ca.ppemtPerInfoCtgPK.perInfoCtgId = po.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " WHERE co.ppemtPerInfoCtgCmPK.contractCd = :contractCd AND ca.cid = :cid ORDER BY po.disporder";

	private final static String SELECT_CATEGORY_BY_CATEGORY_ID_QUERY = "SELECT ca.ppemtPerInfoCtgPK.perInfoCtgId, ca.categoryCd, ca.categoryName, ca.abolitionAtr,"
			+ " co.categoryParentCd, co.categoryType, co.personEmployeeType, co.fixedAtr"
			+ " FROM  PpemtPerInfoCtg ca, PpemtPerInfoCtgCm co"
			+ " WHERE ca.categoryCd = co.ppemtPerInfoCtgCmPK.categoryCd"
			+ " AND co.ppemtPerInfoCtgCmPK.contractCd = :contractCd"
			+ " AND ca.ppemtPerInfoCtgPK.perInfoCtgId = :perInfoCtgId";

	private final static String SELECT_CATEGORY_BY_PARENT_CD = "SELECT ca.ppemtPerInfoCtgPK.perInfoCtgId, ca.categoryCd, ca.categoryName, ca.abolitionAtr,"
			+ " co.categoryParentCd, co.categoryType, co.personEmployeeType, co.fixedAtr"
			+ " FROM  PpemtPerInfoCtg ca, PpemtPerInfoCtgCm co"
			+ " WHERE ca.categoryCd = co.ppemtPerInfoCtgCmPK.categoryCd"
			+ " AND co.ppemtPerInfoCtgCmPK.contractCd = :contractCd" + " AND CO.categoryParentCd = :parentCd";

	private final static String SELECT_CATEGORY_BY_PARENT_CD_WITH_ORDER = "SELECT ca.ppemtPerInfoCtgPK.perInfoCtgId, ca.categoryCd, ca.categoryName, ca.abolitionAtr,"
			+ " co.categoryParentCd, co.categoryType, co.personEmployeeType, co.fixedAtr"
			+ " FROM  PpemtPerInfoCtg ca, PpemtPerInfoCtgCm co"
			+ " INNER JOIN PpemtPerInfoCtgOrder po ON ca.cid = po.cid AND"
			+ " ca.ppemtPerInfoCtgPK.perInfoCtgId = po.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " WHERE ca.categoryCd = co.ppemtPerInfoCtgCmPK.categoryCd" + " AND ca.cid = :companyId"
			+ " AND co.ppemtPerInfoCtgCmPK.contractCd = :contractCd"
			+ " AND CO.categoryParentCd = :parentCd ORDER BY po.disporder";

	private final static String SELECT_GET_CATEGORY_CODE_LASTEST_QUERY = "SELECT co.ppemtPerInfoCtgCmPK.categoryCd FROM PpemtPerInfoCtgCm co"
			+ " WHERE co.ppemtPerInfoCtgCmPK.contractCd = :contractCd ORDER BY co.ppemtPerInfoCtgCmPK.categoryCd DESC";

	private final static String SELECT_GET_DISPORDER_CTG_OF_COMPANY_QUERY = "SELECT od.disporder FROM PpemtPerInfoCtgOrder od"
			+ " WHERE od.cid = :companyId ORDER BY od.disporder DESC";

	private final static String SELECT_LIST_CTG_ID_QUERY = "SELECT c.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " FROM PpemtPerInfoCtg c WHERE c.cid IN :companyIdList AND c.categoryCd = :categoryCd";

	private final static String SELECT_CHECK_CTG_NAME_QUERY = "SELECT c.categoryName"
			+ " FROM PpemtPerInfoCtg c WHERE c.cid = :companyId AND c.categoryName = :categoryName"
			+ " AND c.ppemtPerInfoCtgPK.perInfoCtgId != :ctgId";

	private final static String SELECT_CATEGORY_BY_NAME = "SELECT ca.ppemtPerInfoCtgPK.perInfoCtgId,"
			+ " ca.categoryCd, ca.categoryName, ca.abolitionAtr,"
			+ " co.categoryParentCd, co.categoryType, co.personEmployeeType, co.fixedAtr, po.disporder"
			+ " FROM PpemtPerInfoCtg ca INNER JOIN PpemtPerInfoCtgCm co"
			+ " ON ca.categoryCd = co.ppemtPerInfoCtgCmPK.categoryCd"
			+ " INNER JOIN PpemtPerInfoCtgOrder po ON ca.cid = po.cid AND"
			+ " ca.ppemtPerInfoCtgPK.perInfoCtgId = po.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " WHERE co.ppemtPerInfoCtgCmPK.contractCd = :contractCd AND ca.cid = :cid"
			+ " AND ca.categoryName LIKE CONCAT('%', :categoryName, '%') AND co.categoryParentCd IS AND co.categoryType != 2 AND co.categoryType !=5  NULL ORDER BY po.disporder";

	private final static String GET_DATE_RANGE_ID_BY_CTG_ID = "SELECT d FROM PpemtDateRangeItem d"
			+ " WHERE d.ppemtPerInfoCtgPK.perInfoCtgId = :perInfoCtgId";

	private final static String GET_DATE_RANGE_ID_BY_CTG_ID_2 = "SELECT d FROM PpemtDateRangeItem d"
			+ " WHERE d.ppemtPerInfoCtgPK.perInfoCtgId = :perInfoCtgId";

	private final static String SELECT_CATEGORY_BY_CATEGORY_CD_QUERY = "SELECT ca.ppemtPerInfoCtgPK.perInfoCtgId, ca.categoryCd, ca.categoryName, ca.abolitionAtr,"
			+ " co.categoryParentCd, co.categoryType, co.personEmployeeType, co.fixedAtr"
			+ " FROM  PpemtPerInfoCtg ca, PpemtPerInfoCtgCm co"
			+ " WHERE ca.categoryCd = co.ppemtPerInfoCtgCmPK.categoryCd" + " AND ca.categoryCd = :categoryCd"
			+ " AND ca.cid = :cid";

	private final static String SELECT_CATEGORY_BY_COMPANY_ID_QUERY_1 = "SELECT ca.ppemtPerInfoCtgPK.perInfoCtgId,"
			+ " ca.categoryCd, ca.categoryName, ca.abolitionAtr,"
			+ " co.categoryParentCd, co.categoryType, co.personEmployeeType, co.fixedAtr, po.disporder"
			+ " FROM PpemtPerInfoCtg ca "
			+ " INNER JOIN PpemtPerInfoCtgCm co ON ca.categoryCd = co.ppemtPerInfoCtgCmPK.categoryCd"
			+ " INNER JOIN PpemtPerInfoCtgOrder po ON ca.cid = po.cid AND ca.ppemtPerInfoCtgPK.perInfoCtgId = po.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " WHERE ca.cid = :cid AND co.categoryParentCd IS NULL ORDER BY po.disporder";

	@Override
	public List<PersonInfoCategory> getAllPerInfoCategory(String companyId, String contractCd) {
		return this.queryProxy().query(SELECT_CATEGORY_BY_COMPANY_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("cid", companyId).getList(c -> {
					return createDomainFromEntity(c);
				});
	}

	@Override
	public Optional<PersonInfoCategory> getPerInfoCategory(String perInfoCategoryId, String contractCd) {
		return this.queryProxy().query(SELECT_CATEGORY_BY_CATEGORY_ID_QUERY, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("perInfoCtgId", perInfoCategoryId).getSingle(c -> {
					return createDomainFromEntity(c);
				});
	}

	@Override
	public String getPerInfoCtgCodeLastest(String contractCd) {
		List<String> ctgCodeLastest = this.getEntityManager()
				.createQuery(SELECT_GET_CATEGORY_CODE_LASTEST_QUERY, String.class)
				.setParameter("contractCd", contractCd).getResultList();
		return ctgCodeLastest.stream().filter(c -> c.contains(SPECIAL_CTG_CODE)).findFirst().orElse(null);
	}

	@Override
	public List<String> getPerInfoCtgIdList(List<String> companyIdList, String categoryCd) {
		return this.queryProxy().query(SELECT_LIST_CTG_ID_QUERY, String.class)
				.setParameter("companyIdList", companyIdList).setParameter("categoryCd", categoryCd).getList();
	}

	@Override
	public void addPerInfoCtgRoot(PersonInfoCategory perInfoCtg, String contractCd) {
		this.commandProxy().insert(createPerInfoCtgCmFromDomain(perInfoCtg, contractCd));
		this.commandProxy().insert(createPerInfoCtgFromDomain(perInfoCtg));
		addOrderPerInfoCtgRoot(perInfoCtg.getPersonInfoCategoryId(), perInfoCtg.getCompanyId());
	}

	@Override
	public void addPerInfoCtgWithListCompany(PersonInfoCategory perInfoCtg, String contractCd,
			List<String> companyIdList) {
		List<PpemtPerInfoCtg> lstPpemtPerInfoCtg = companyIdList.stream().map(p -> {
			return createPerInfoCtgFromDomainWithCid(perInfoCtg, p);
		}).collect(Collectors.toList());
		this.commandProxy().insertAll(lstPpemtPerInfoCtg);
		addOrderPerInfoCtgWithListCompany(lstPpemtPerInfoCtg);
	}

	@Override
	public void updatePerInfoCtg(PersonInfoCategory perInfoCtg, String contractCd) {
		PpemtPerInfoCtgPK perInfoCtgPK = new PpemtPerInfoCtgPK(perInfoCtg.getPersonInfoCategoryId());
		PpemtPerInfoCtg perInfoCtgOld = this.queryProxy().find(perInfoCtgPK, PpemtPerInfoCtg.class).orElse(null);
		perInfoCtgOld.categoryName = perInfoCtg.getCategoryName().v();
		this.commandProxy().update(perInfoCtgOld);
		PpemtPerInfoCtgCmPK perInfoCtgCmPK = new PpemtPerInfoCtgCmPK(contractCd, perInfoCtgOld.categoryCd);
		PpemtPerInfoCtgCm perInfoCtgCmOld = this.queryProxy().find(perInfoCtgCmPK, PpemtPerInfoCtgCm.class)
				.orElse(null);
		perInfoCtgCmOld.categoryType = perInfoCtg.getCategoryType().value;
		this.commandProxy().update(perInfoCtgCmOld);
	}

	@Override
	public boolean checkCtgNameIsUnique(String companyId, String newCtgName, String ctgId) {
		List<String> categoryNames = this.queryProxy().query(SELECT_CHECK_CTG_NAME_QUERY, String.class)
				.setParameter("companyId", companyId).setParameter("categoryName", newCtgName)
				.setParameter("ctgId", ctgId).getList();
		if (categoryNames == null || categoryNames.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void addDateRangeItemRoot(DateRangeItem dateRangeItem) {
		this.commandProxy().insert(createDateRangeItemFromDomain(dateRangeItem));
	}

	@Override
	public void addListDateRangeItem(List<DateRangeItem> dateRangeItems) {
		this.commandProxy().insertAll(dateRangeItems.stream().map(c -> {
			return createDateRangeItemFromDomain(c);
		}).collect(Collectors.toList()));

	}

	private void addOrderPerInfoCtgRoot(String perInfoCtgId, String companyId) {
		int newdisOrderLastest = getDispOrderLastestCtgOfCompany(companyId) + 1;
		this.commandProxy().insert(createPerInfoCtgOrderFromDomain(perInfoCtgId, companyId, newdisOrderLastest));
	}

	private void addOrderPerInfoCtgWithListCompany(List<PpemtPerInfoCtg> lstPpemtPerInfoCtg) {
		this.commandProxy().insertAll(lstPpemtPerInfoCtg.stream().map(p -> {
			int newdisOrderLastest = getDispOrderLastestCtgOfCompany(p.cid) + 1;
			return createPerInfoCtgOrderFromDomain(p.ppemtPerInfoCtgPK.perInfoCtgId, p.cid, newdisOrderLastest);
		}).collect(Collectors.toList()));
	}

	private int getDispOrderLastestCtgOfCompany(String companyId) {
		List<Integer> dispOrderLastests = this.getEntityManager()
				.createQuery(SELECT_GET_DISPORDER_CTG_OF_COMPANY_QUERY, Integer.class)
				.setParameter("companyId", companyId).setMaxResults(1).getResultList();
		if (dispOrderLastests != null && !dispOrderLastests.isEmpty()) {
			return dispOrderLastests.get(0);
		}
		return 0;
	}

	// mapping
	private PersonInfoCategory createDomainFromEntity(Object[] c) {
		String personInfoCategoryId = String.valueOf(c[0]);
		String categoryCode = String.valueOf(c[1]);
		String categoryName = String.valueOf(c[2]);
		int abolitionAtr = Integer.parseInt(String.valueOf(c[3]));
		String categoryParentCd = (c[4] != null) ? String.valueOf(c[4]) : null;
		int categoryType = Integer.parseInt(String.valueOf(c[5]));
		int personEmployeeType = Integer.parseInt(String.valueOf(c[6]));
		int fixedAtr = Integer.parseInt(String.valueOf(c[7]));
		return PersonInfoCategory.createFromEntity(personInfoCategoryId, null, categoryCode, categoryParentCd,
				categoryName, personEmployeeType, abolitionAtr, categoryType, fixedAtr);

	}

	private PpemtPerInfoCtg createPerInfoCtgFromDomain(PersonInfoCategory perInfoCtg) {
		PpemtPerInfoCtgPK perInfoCtgPK = new PpemtPerInfoCtgPK(perInfoCtg.getPersonInfoCategoryId());
		return new PpemtPerInfoCtg(perInfoCtgPK, perInfoCtg.getCompanyId(), perInfoCtg.getCategoryCode().v(),
				perInfoCtg.getCategoryName().v(), perInfoCtg.getIsAbolition().value);

	}

	private PpemtPerInfoCtg createPerInfoCtgFromDomainWithCid(PersonInfoCategory perInfoCtg, String companyId) {
		PpemtPerInfoCtgPK perInfoCtgPK = new PpemtPerInfoCtgPK(IdentifierUtil.randomUniqueId());
		return new PpemtPerInfoCtg(perInfoCtgPK, companyId, perInfoCtg.getCategoryCode().v(),
				perInfoCtg.getCategoryName().v(), perInfoCtg.getIsAbolition().value);

	}

	private PpemtPerInfoCtgCm createPerInfoCtgCmFromDomain(PersonInfoCategory perInfoCtg, String contractCd) {
		PpemtPerInfoCtgCmPK perInfoCtgCmPK = new PpemtPerInfoCtgCmPK(contractCd, perInfoCtg.getCategoryCode().v());
		String categoryParentCode = (perInfoCtg.getCategoryParentCode() == null
				|| perInfoCtg.getCategoryParentCode().v().isEmpty()) ? null : perInfoCtg.getCategoryParentCode().v();
		return new PpemtPerInfoCtgCm(perInfoCtgCmPK, categoryParentCode, perInfoCtg.getCategoryType().value,
				perInfoCtg.getPersonEmployeeType().value, perInfoCtg.getIsFixed().value);
	}

	private PpemtPerInfoCtgOrder createPerInfoCtgOrderFromDomain(String perInfoCtgId, String companyId, int disOrder) {
		PpemtPerInfoCtgPK perInfoCtgPK = new PpemtPerInfoCtgPK(perInfoCtgId);
		return new PpemtPerInfoCtgOrder(perInfoCtgPK, companyId, disOrder);
	}

	private PpemtDateRangeItem createDateRangeItemFromDomain(DateRangeItem dateRangeItem) {
		PpemtPerInfoCtgPK perInfoCtgPK = new PpemtPerInfoCtgPK(dateRangeItem.getPersonInfoCtgId());
		return new PpemtDateRangeItem(perInfoCtgPK, dateRangeItem.getStartDateItemId(),
				dateRangeItem.getEndDateItemId(), dateRangeItem.getDateRangeItemId());
	}

	// vinhpx: start

	@Override
	public List<PersonInfoCategory> getPerInfoCategoryByName(String companyId, String contractCd, String name) {
		return this.queryProxy().query(SELECT_CATEGORY_BY_NAME, Object[].class).setParameter("contractCd", contractCd)
				.setParameter("cid", companyId).setParameter("categoryName", name).getList(c -> {
					return createDomainFromEntity(c);
				});
	}

	@Override
	public List<PersonInfoCategory> getPerInfoCtgByParentCode(String parentCtgCd, String contractCd) {
		return this.queryProxy().query(SELECT_CATEGORY_BY_PARENT_CD, Object[].class)
				.setParameter("contractCd", contractCd).setParameter("parentCd", parentCtgCd).getList(c -> {
					return createDomainFromEntity(c);
				});
	}

	@Override
	public DateRangeItem getDateRangeItemByCtgId(String perInfoCtgId) {
		Optional<PpemtDateRangeItem> itemOpt = this.queryProxy()
				.query(GET_DATE_RANGE_ID_BY_CTG_ID, PpemtDateRangeItem.class).setParameter("perInfoCtgId", perInfoCtgId)
				.getSingle();
		if (!itemOpt.isPresent()) {
			return null;
		}
		PpemtDateRangeItem item = itemOpt.get();
		return DateRangeItem.createFromJavaType(item.ppemtPerInfoCtgPK.perInfoCtgId, item.startDateItemId,
				item.endDateItemId, item.dateRangeItemId);
	}

	@Override
	public List<PersonInfoCategory> getPerInfoCtgByParentCdWithOrder(String parentCtgCd, String contractCd,
			String companyId, boolean isASC) {
		return this.queryProxy()
				.query(SELECT_CATEGORY_BY_PARENT_CD_WITH_ORDER + (isASC ? " ASC" : " DESC"), Object[].class)
				.setParameter("contractCd", contractCd).setParameter("parentCd", parentCtgCd)
				.setParameter("companyId", companyId).getList(c -> {
					return createDomainFromEntity(c);
				});
	}

	@Override
	public DateRangeItem getDateRangeItemByCategoryId(String perInfoCtgId) {
		PpemtDateRangeItem item = this.queryProxy().query(GET_DATE_RANGE_ID_BY_CTG_ID_2, PpemtDateRangeItem.class)
				.setParameter("perInfoCtgId", perInfoCtgId).getSingleOrNull();
		DateRangeItem s = DateRangeItem.createFromJavaType(item.ppemtPerInfoCtgPK.perInfoCtgId, item.startDateItemId,
				item.endDateItemId, item.dateRangeItemId);
		return s;
	}

	// vinhpx: end

	@Override
	public Optional<PersonInfoCategory> getPerInfoCategoryByCtgCD(String categoryCD, String companyID) {
		return this.queryProxy().query(SELECT_CATEGORY_BY_CATEGORY_CD_QUERY, Object[].class)
				.setParameter("categoryCd", categoryCD).setParameter("cid", companyID).getSingle(c -> {
					return createDomainFromEntity(c);
				});
	}

	/**
	 * case : Employee Selected trùng với employee đang nhập
	 */
	@Override
	public List<PersonInfoCategory> getAllPerInfoCtg(String companyId) {
		// TODO Auto-generated method stub
		return this.queryProxy().query(SELECT_CATEGORY_BY_COMPANY_ID_QUERY_1, Object[].class)
				.setParameter("cid", companyId).getList(c -> {
					return createDomainPerInfoCtgFromEntity(c);
				});

	}

	/**
	 * case : Employee Selected khác với employee đang nhập
	 */
	@Override
	public List<PersonInfoCategory> getAllPerInfoCtgOtherEmp(String companyId) {
		// TODO Auto-generated method stub
		return null;
	}

	// mapping
	private PersonInfoCategory createDomainPerInfoCtgFromEntity(Object[] c) {
		String personInfoCategoryId = String.valueOf(c[0]);
		String categoryCode = String.valueOf(c[1]);
		String categoryName = String.valueOf(c[2]);
		int abolitionAtr = Integer.parseInt(String.valueOf(c[3]));
		String categoryParentCd = (c[4] != null) ? String.valueOf(c[4]) : null;
		int categoryType = Integer.parseInt(String.valueOf(c[5]));
		int personEmployeeType = Integer.parseInt(String.valueOf(c[6]));
		int fixedAtr = Integer.parseInt(String.valueOf(c[7]));
		return PersonInfoCategory.createFromEntity(personInfoCategoryId, null, categoryCode, categoryParentCd,
				categoryName, personEmployeeType, abolitionAtr, categoryType, fixedAtr);
	}

}
