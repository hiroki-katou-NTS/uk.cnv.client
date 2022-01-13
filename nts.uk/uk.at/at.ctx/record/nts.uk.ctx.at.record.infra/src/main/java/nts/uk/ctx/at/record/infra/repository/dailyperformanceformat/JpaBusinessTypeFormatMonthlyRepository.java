package nts.uk.ctx.at.record.infra.repository.dailyperformanceformat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.BusinessTypeFormatMonthly;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.repository.BusinessTypeFormatMonthlyRepository;
import nts.uk.ctx.at.record.infra.entity.dailyperformanceformat.KrcmtBusinessTypeMonthly;
import nts.uk.ctx.at.record.infra.entity.dailyperformanceformat.KrcmtBusinessTypeMonthlyPK;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;

@Stateless
public class JpaBusinessTypeFormatMonthlyRepository extends JpaRepository
		implements BusinessTypeFormatMonthlyRepository {

	private static final String FIND;
	
	private static final String FIND_BY_LIST_CODE;

	private static final String UPDATE_BY_KEY;

	private static final String REMOVE_EXIST_DATA;
	
	private static final String IS_EXIST_DATA;
	
	private static final String FIND_BY_COMPANYID;

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessTypeMonthly a ");
		builderString.append("WHERE a.krcmtBusinessTypeMonthlyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeMonthlyPK.businessTypeCode = :businessTypeCode ");
		FIND = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessTypeMonthly a ");
		builderString.append("WHERE a.krcmtBusinessTypeMonthlyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeMonthlyPK.businessTypeCode IN :listBusinessTypeCode ");
		builderString.append("ORDER BY a.order ASC ");
		FIND_BY_LIST_CODE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("UPDATE KrcmtBusinessTypeMonthly a ");
		builderString.append("SET a.order = :order , a.columnWidth = :columnWidth ");
		builderString.append("WHERE a.krcmtBusinessTypeMonthlyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeMonthlyPK.businessTypeCode = :businessTypeCode ");
		builderString.append("AND a.krcmtBusinessTypeMonthlyPK.attendanceItemId = :attendanceItemId ");
		UPDATE_BY_KEY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KrcmtBusinessTypeMonthly a ");
		builderString.append("WHERE a.krcmtBusinessTypeMonthlyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeMonthlyPK.businessTypeCode = :businessTypeCode ");
		builderString.append("AND a.krcmtBusinessTypeMonthlyPK.attendanceItemId IN :attendanceItemIds ");
		REMOVE_EXIST_DATA = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT COUNT(a) ");
		builderString.append("FROM KrcmtBusinessTypeMonthly a ");
		builderString.append("WHERE a.krcmtBusinessTypeMonthlyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeMonthlyPK.businessTypeCode = :businessTypeCode ");
		IS_EXIST_DATA = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessTypeMonthly a ");
		builderString.append("WHERE a.krcmtBusinessTypeMonthlyPK.companyId = :companyId ");
		FIND_BY_COMPANYID = builderString.toString();
	}

	@Override
	public List<BusinessTypeFormatMonthly> getMonthlyDetail(String companyId, String businessTypeCode) {
		return this.queryProxy().query(FIND, KrcmtBusinessTypeMonthly.class).setParameter("companyId", companyId)
				.setParameter("businessTypeCode", businessTypeCode).getList(f -> toDomain(f));
	}

	@Override
	public void update(BusinessTypeFormatMonthly businessTypeFormatMonthly) {
		this.getEntityManager().createQuery(UPDATE_BY_KEY)
				.setParameter("companyId", businessTypeFormatMonthly.getCompanyId())
				.setParameter("businessTypeCode", businessTypeFormatMonthly.getBusinessTypeCode().v())
				.setParameter("attendanceItemId", businessTypeFormatMonthly.getAttendanceItemId())
				.setParameter("columnWidth", businessTypeFormatMonthly.getColumnWidth())
				.setParameter("order", businessTypeFormatMonthly.getOrder()).executeUpdate();
	}

	/*
	 * Remove attendanceItemId not exist in list that need update
	 */
	@Override
	public void deleteExistData(String companyId, String businessTypeCode, List<Integer> attendanceItemIds) {
		CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			this.getEntityManager().createQuery(REMOVE_EXIST_DATA)
				.setParameter("companyId", companyId)
				.setParameter("businessTypeCode", businessTypeCode)
				.setParameter("attendanceItemIds", subList)
				.executeUpdate();
		});
		this.getEntityManager().flush();
	}

	@Override
	public void add(List<BusinessTypeFormatMonthly> businessTypeFormatMonthlyAdds) {
		businessTypeFormatMonthlyAdds.forEach(f -> this.commandProxy().insert(toEntity(f)));
	}

	@Override
	public boolean checkExistData(String companyId, String businessTypeCode) {
		return this.queryProxy().query(IS_EXIST_DATA, long.class).setParameter("companyId", companyId)
				.setParameter("businessTypeCode", businessTypeCode).getSingle().get() > 0;
	}

	private static BusinessTypeFormatMonthly toDomain(KrcmtBusinessTypeMonthly krcmtBusinessTypeMonthly) {
		BusinessTypeFormatMonthly workTypeFormatMonthly = BusinessTypeFormatMonthly.createFromJavaType(
				krcmtBusinessTypeMonthly.krcmtBusinessTypeMonthlyPK.companyId,
				krcmtBusinessTypeMonthly.krcmtBusinessTypeMonthlyPK.businessTypeCode,
				krcmtBusinessTypeMonthly.krcmtBusinessTypeMonthlyPK.attendanceItemId, krcmtBusinessTypeMonthly.order,
				krcmtBusinessTypeMonthly.columnWidth);
		return workTypeFormatMonthly;
	}
	
	private KrcmtBusinessTypeMonthly toEntity(BusinessTypeFormatMonthly businessTypeFormatMonthly){
		val entity = new KrcmtBusinessTypeMonthly();
		
		entity.krcmtBusinessTypeMonthlyPK = new KrcmtBusinessTypeMonthlyPK();
		entity.krcmtBusinessTypeMonthlyPK.companyId = businessTypeFormatMonthly.getCompanyId();
		entity.krcmtBusinessTypeMonthlyPK.attendanceItemId = businessTypeFormatMonthly.getAttendanceItemId();
		entity.krcmtBusinessTypeMonthlyPK.businessTypeCode = businessTypeFormatMonthly.getBusinessTypeCode().v();
		entity.columnWidth = businessTypeFormatMonthly.getColumnWidth();
		entity.order = businessTypeFormatMonthly.getOrder();
		
		return entity;
	}

	@Override
	public List<BusinessTypeFormatMonthly> getMonthlyDetailByCompanyId(String companyId) {
		return this.queryProxy().query(FIND_BY_COMPANYID, KrcmtBusinessTypeMonthly.class).setParameter("companyId", companyId).getList(f -> toDomain(f));
	}
	
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<BusinessTypeFormatMonthly> getListBusinessTypeFormat(String companyId, Collection<String> listBusinessTypeCode) {
		List<BusinessTypeFormatMonthly> resultList = new ArrayList<>();
		CollectionUtil.split(new ArrayList<String>(listBusinessTypeCode), DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_LIST_CODE, KrcmtBusinessTypeMonthly.class)
					.setParameter("companyId", companyId)
					.setParameter("listBusinessTypeCode", subList)
					.getList(f -> toDomain(f)));
		});
		resultList.sort(Comparator.comparing(BusinessTypeFormatMonthly::getOrder));
		return resultList;
	}

	@Override
	public void updateColumnsWidth(String companyId, Map<Integer, Integer> lstHeader, List<String> formatCodes) {
		List<Integer> itemIds = new ArrayList<>();
		itemIds.addAll(lstHeader.keySet());
		List<KrcmtBusinessTypeMonthly> items = this.getListBusinessTypeFormat(companyId, formatCodes).stream()
				.map(x -> toEntity(x)).collect(Collectors.toList());
		List<KrcmtBusinessTypeMonthly> entities = items.stream().filter(x -> lstHeader.containsKey(x.krcmtBusinessTypeMonthlyPK.attendanceItemId))
				.map(x -> new KrcmtBusinessTypeMonthly(x.krcmtBusinessTypeMonthlyPK, x.order,
						new BigDecimal(lstHeader.get(x.krcmtBusinessTypeMonthlyPK.attendanceItemId))))
				.collect(Collectors.toList());
		this.commandProxy().updateAll(entities);
	}

	@Override
	public void copy(String companyId, String businessTypeCode, List<String> listBusinessTypeCode) {
		List<BusinessTypeFormatMonthly> listBySelectedCode = this.getMonthlyDetail(companyId, businessTypeCode);
		
		List<KrcmtBusinessTypeMonthly> listKrcmtBusinessTypeMonthly = this.getListBusinessTypeFormat(companyId, listBusinessTypeCode)
				.stream()
				.map(e -> toEntity(e))
				.collect(Collectors.toList());
		
		if (!listKrcmtBusinessTypeMonthly.isEmpty()) {
			this.commandProxy().removeAll(KrcmtBusinessTypeMonthly.class, listKrcmtBusinessTypeMonthly.stream().map(e -> e.krcmtBusinessTypeMonthlyPK).collect(Collectors.toList()));
			this.getEntityManager().flush();
		}
		
		List<KrcmtBusinessTypeMonthly> newListKrcmtBusinessTypeMonthly = new ArrayList<KrcmtBusinessTypeMonthly>();
		
		for (String code : listBusinessTypeCode) {
			newListKrcmtBusinessTypeMonthly.addAll(toListEntity(code, listBySelectedCode));
		}
		
		this.commandProxy().insertAll(newListKrcmtBusinessTypeMonthly);
	}
	
	private List<KrcmtBusinessTypeMonthly> toListEntity(String code, List<BusinessTypeFormatMonthly> listBySelectedCode) {
		
		List<KrcmtBusinessTypeMonthly> result = listBySelectedCode.stream()
				.map(e -> {
					e.setBusinessTypeCode(new BusinessTypeCode(code));
					return toEntity(e);
				})
				.collect(Collectors.toList());
		
		return result;
	}
}
