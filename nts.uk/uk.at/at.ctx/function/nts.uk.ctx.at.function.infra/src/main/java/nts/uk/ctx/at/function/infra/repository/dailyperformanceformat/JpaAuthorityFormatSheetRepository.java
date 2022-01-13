package nts.uk.ctx.at.function.infra.repository.dailyperformanceformat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFormatSheet;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.primitivevalue.DailyPerformanceFormatCode;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.repository.AuthorityFormatSheetRepository;
import nts.uk.ctx.at.function.infra.entity.dailyperformanceformat.KfnmtAuthorityDailyItem;
import nts.uk.ctx.at.function.infra.entity.dailyperformanceformat.KfnmtAuthorityFormSheet;
import nts.uk.ctx.at.function.infra.entity.dailyperformanceformat.KfnmtAuthorityFormSheetPK;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class JpaAuthorityFormatSheetRepository extends JpaRepository implements AuthorityFormatSheetRepository {

	private static final String FIND;
	
	private static final String FIND_BY_CODE;

	private static final String UPDATE_BY_KEY;

	private static final String DEL_BY_KEY;
	
	private static final String IS_EXIST_DATA;

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KfnmtAuthorityFormSheet a ");
		builderString.append("WHERE a.kfnmtAuthorityFormSheetPK.companyId = :companyId ");
		builderString
				.append("AND a.kfnmtAuthorityFormSheetPK.dailyPerformanceFormatCode = :dailyPerformanceFormatCode ");
		builderString.append("AND a.kfnmtAuthorityFormSheetPK.sheetNo = :sheetNo ");
		FIND = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KfnmtAuthorityFormSheet a ");
		builderString.append("WHERE a.kfnmtAuthorityFormSheetPK.companyId = :companyId ");
		builderString.append("AND a.kfnmtAuthorityFormSheetPK.dailyPerformanceFormatCode = :dailyPerformanceFormatCode ");
		FIND_BY_CODE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("UPDATE KfnmtAuthorityFormSheet a ");
		builderString.append("SET a.sheetName = :sheetName ");
		builderString.append("WHERE a.kfnmtAuthorityFormSheetPK.companyId = :companyId ");
		builderString
				.append("AND a.kfnmtAuthorityFormSheetPK.dailyPerformanceFormatCode = :dailyPerformanceFormatCode ");
		builderString.append("AND a.kfnmtAuthorityFormSheetPK.sheetNo = :sheetNo ");
		UPDATE_BY_KEY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KfnmtAuthorityFormSheet a ");
		builderString.append("WHERE a.kfnmtAuthorityFormSheetPK.companyId = :companyId ");
		builderString
				.append("AND a.kfnmtAuthorityFormSheetPK.dailyPerformanceFormatCode = :dailyPerformanceFormatCode ");
		DEL_BY_KEY = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT COUNT(a) ");
		builderString.append("FROM KfnmtAuthorityFormSheet a ");
		builderString
				.append("WHERE a.kfnmtAuthorityFormSheetPK.companyId = :companyId ");
		builderString.append("AND a.kfnmtAuthorityFormSheetPK.dailyPerformanceFormatCode = :dailyPerformanceFormatCode ");
		builderString.append("AND a.kfnmtAuthorityFormSheetPK.sheetNo = :sheetNo ");
		IS_EXIST_DATA = builderString.toString();
		
		
		
	}

	@Override
	public Optional<AuthorityFormatSheet> find(String companyId, DailyPerformanceFormatCode dailyPerformanceFormatCode,
			BigDecimal sheetNo) {
		return this.queryProxy().query(FIND, KfnmtAuthorityFormSheet.class).setParameter("companyId", companyId)
				.setParameter("dailyPerformanceFormatCode", dailyPerformanceFormatCode.v())
				.setParameter("sheetNo", sheetNo).getSingle(f -> toDomain(f));
	}

	@Override
	public void add(AuthorityFormatSheet authorityFormatSheet) {
		this.commandProxy().insert(toEntity(authorityFormatSheet));
	}
	
	@Override
	public void add(List<AuthorityFormatSheet> listAuthorityFormatSheet) {
		List<KfnmtAuthorityFormSheet> listEntity = listAuthorityFormatSheet.stream()
				.map(e -> toEntity(e))
				.collect(Collectors.toList());
		this.commandProxy().insertAll(listEntity);	
	}

	@Override
	public void update(AuthorityFormatSheet authorityFormatSheet) {
		this.getEntityManager().createQuery(UPDATE_BY_KEY)
				.setParameter("companyId", authorityFormatSheet.getCompanyId())
				.setParameter("dailyPerformanceFormatCode", authorityFormatSheet.getDailyPerformanceFormatCode().v())
				.setParameter("sheetNo", authorityFormatSheet.getSheetNo())
				.setParameter("sheetName", authorityFormatSheet.getSheetName()).executeUpdate();
	}

	@Override
	public void remove(String companyId, DailyPerformanceFormatCode dailyPerformanceFormatCode) {
		this.getEntityManager().createQuery(DEL_BY_KEY).setParameter("companyId", companyId)
				.setParameter("dailyPerformanceFormatCode", dailyPerformanceFormatCode.v()).executeUpdate();
	}

	@Override
	public boolean checkExistData(String companyId, DailyPerformanceFormatCode dailyPerformanceFormatCode,
			BigDecimal sheetNo) {
		return this.queryProxy().query(IS_EXIST_DATA, long.class).setParameter("companyId", companyId)
				.setParameter("dailyPerformanceFormatCode", dailyPerformanceFormatCode.v())
				.setParameter("sheetNo", sheetNo).getSingle().get() > 0;
	}

	private static AuthorityFormatSheet toDomain(KfnmtAuthorityFormSheet kfnmtAuthorityFormSheet) {
		AuthorityFormatSheet authorityFormatSheet = AuthorityFormatSheet.createJavaTye(
				kfnmtAuthorityFormSheet.kfnmtAuthorityFormSheetPK.companyId,
				kfnmtAuthorityFormSheet.kfnmtAuthorityFormSheetPK.dailyPerformanceFormatCode,
				kfnmtAuthorityFormSheet.kfnmtAuthorityFormSheetPK.sheetNo, kfnmtAuthorityFormSheet.sheetName);
		return authorityFormatSheet;
	}

	private KfnmtAuthorityFormSheet toEntity(AuthorityFormatSheet authorityFormatSheet) {
		val entity = new KfnmtAuthorityFormSheet();

		entity.kfnmtAuthorityFormSheetPK = new KfnmtAuthorityFormSheetPK();
		entity.kfnmtAuthorityFormSheetPK.companyId = authorityFormatSheet.getCompanyId();
		entity.kfnmtAuthorityFormSheetPK.dailyPerformanceFormatCode = authorityFormatSheet
				.getDailyPerformanceFormatCode().v();
		entity.kfnmtAuthorityFormSheetPK.sheetNo = authorityFormatSheet.getSheetNo();
		entity.sheetName = authorityFormatSheet.getSheetName();

		return entity;
	}
	
	private static final String FIND_BY_SHEET = "SELECT c FROM KfnmtAuthorityDailyItem c "
			+ " WHERE c.kfnmtAuthorityDailyItemPK.companyId = :companyId "
			+ " AND c.kfnmtAuthorityDailyItemPK.dailyPerformanceFormatCode = :dailyPerformanceFormatCode "
			+ " AND c.kfnmtAuthorityDailyItemPK.sheetNo = :sheetNo ";
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void deleteBySheetNo(String companyId, String dailyPerformanceFormatCode, BigDecimal sheetNo) {
		this.commandProxy().remove(KfnmtAuthorityFormSheet.class,new KfnmtAuthorityFormSheetPK(
					companyId,dailyPerformanceFormatCode,sheetNo
				));
		List<KfnmtAuthorityDailyItem> listData = this.queryProxy().query(FIND_BY_SHEET,KfnmtAuthorityDailyItem.class)
				.setParameter("companyId", companyId)
				.setParameter("dailyPerformanceFormatCode", dailyPerformanceFormatCode)
				.setParameter("sheetNo", sheetNo)
				.getList();
		this.commandProxy().removeAll(listData);
	}

	@Override
	public List<AuthorityFormatSheet> findByCode(String companyId, String dailyPerformanceFormatCode) {
		return this.queryProxy().query(FIND_BY_CODE, KfnmtAuthorityFormSheet.class).setParameter("companyId", companyId)
				.setParameter("dailyPerformanceFormatCode", dailyPerformanceFormatCode)
				.getList(f -> toDomain(f));
	}
}
