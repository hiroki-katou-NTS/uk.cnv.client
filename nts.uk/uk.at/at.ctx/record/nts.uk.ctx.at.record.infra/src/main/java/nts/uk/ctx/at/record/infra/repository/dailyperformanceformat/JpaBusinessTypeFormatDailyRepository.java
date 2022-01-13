package nts.uk.ctx.at.record.infra.repository.dailyperformanceformat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.BusinessTypeFormatDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.repository.BusinessTypeFormatDailyRepository;
import nts.uk.ctx.at.record.infra.entity.dailyperformanceformat.KrcmtBusinessFormatSheet;
import nts.uk.ctx.at.record.infra.entity.dailyperformanceformat.KrcmtBusinessFormatSheetPK;
import nts.uk.ctx.at.record.infra.entity.dailyperformanceformat.KrcmtBusinessTypeDaily;
import nts.uk.ctx.at.record.infra.entity.dailyperformanceformat.KrcmtBusinessTypeDailyPK;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaBusinessTypeFormatDailyRepository extends JpaRepository implements BusinessTypeFormatDailyRepository {

	private static final String FIND;
	
	private static final String FIND_LISTCODE;
	
	private static final String FIND_SHEET_LISTCODE;
	
	private static final String FIND_SHEET_NAME; 

	private static final String FIND_DETAIl;

	private static final String FIND_DETAIl_WITHOUT_SHEET_NO;

	private static final String UPDATE_BY_KEY;

	private static final String REMOVE_EXIST_DATA;
	
	private static final String REMOVE_EXIST_DATA_BY_CODE;
	
	private static final String IS_EXIST_DATA;

	private final static String SEL_FORMAT_BY_ATD_ITEM = "SELECT f FROM KrcmtBusinessTypeDaily f WHERE f.krcmtBusinessTypeDailyPK.companyId = :companyId AND f.krcmtBusinessTypeDailyPK.attendanceItemId IN :lstItem";
	
	private static final String FIND_BY_COMPANYID;
	
	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessTypeDaily a ");
		builderString.append("WHERE a.krcmtBusinessTypeDailyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.businessTypeCode = :businessTypeCode ");
		FIND = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessTypeDaily a ");
		builderString.append("WHERE a.krcmtBusinessTypeDailyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.businessTypeCode IN :listBusinessTypeCode ");
		FIND_LISTCODE = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessFormatSheet a ");
		builderString.append("WHERE a.krcmtBusinessFormatSheetPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessFormatSheetPK.businessTypeCode IN :listBusinessTypeCode ");
		FIND_SHEET_LISTCODE = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessFormatSheet a ");
		builderString.append("WHERE a.krcmtBusinessFormatSheetPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessFormatSheetPK.businessTypeCode = :businessTypeCode ");
		FIND_SHEET_NAME = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessTypeDaily a ");
		builderString.append("WHERE a.krcmtBusinessTypeDailyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.businessTypeCode = :businessTypeCode ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.sheetNo = :sheetNo ");
		FIND_DETAIl = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessTypeDaily a ");
		builderString.append("WHERE a.krcmtBusinessTypeDailyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.businessTypeCode = :businessTypeCode ");
		FIND_DETAIl_WITHOUT_SHEET_NO = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("UPDATE KrcmtBusinessTypeDaily a ");
		builderString.append("SET a.order = :order , a.columnWidth = :columnWidth ");
		builderString.append("WHERE a.krcmtBusinessTypeDailyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.businessTypeCode = :businessTypeCode ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.attendanceItemId = :attendanceItemId ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.sheetNo = :sheetNo ");
		UPDATE_BY_KEY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KrcmtBusinessTypeDaily a ");
		builderString.append("WHERE a.krcmtBusinessTypeDailyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.businessTypeCode = :businessTypeCode ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.sheetNo = :sheetNo ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.attendanceItemId IN :attendanceItemIds ");
		REMOVE_EXIST_DATA_BY_CODE = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KrcmtBusinessTypeDaily a ");
		builderString.append("WHERE a.krcmtBusinessTypeDailyPK.attendanceItemId IN :attendanceItemIds ");
		REMOVE_EXIST_DATA = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT COUNT(a) ");
		builderString.append("FROM KrcmtBusinessTypeDaily a ");
		builderString
				.append("WHERE a.krcmtBusinessTypeDailyPK.companyId = :companyId ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.businessTypeCode = :businessTypeCode ");
		builderString.append("AND a.krcmtBusinessTypeDailyPK.sheetNo = :sheetNo ");
		IS_EXIST_DATA = builderString.toString();
		
		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcmtBusinessTypeDaily a ");
		builderString.append("WHERE a.krcmtBusinessTypeDailyPK.companyId = :companyId ");
		FIND_BY_COMPANYID = builderString.toString();
	}

	@Override
	public List<BusinessTypeFormatDaily> getBusinessTypeFormat(String companyId, String businessTypeCode) {
		return this.queryProxy().query(FIND, KrcmtBusinessTypeDaily.class).setParameter("companyId", companyId)
				.setParameter("businessTypeCode", businessTypeCode).getList(f -> toDomain(f));
	}

	@Override
	public List<BusinessTypeFormatDaily> getBusinessTypeFormatDailyDetail(String companyId, String businessTypeCode,
			BigDecimal sheetNo) {

		return this.queryProxy().query(FIND_DETAIl, KrcmtBusinessTypeDaily.class).setParameter("companyId", companyId)
				.setParameter("businessTypeCode", businessTypeCode).setParameter("sheetNo", sheetNo)
				.getList(f -> toDomain(f));
	}

	@Override
	public void deleteExistData(List<Integer> attendanceItemIds) {
		CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			this.getEntityManager().createQuery(REMOVE_EXIST_DATA)
				.setParameter("attendanceItemIds", subList)
				.executeUpdate();
		});
		this.getEntityManager().flush();
	}

	@Override
	public void add(List<BusinessTypeFormatDaily> businessTypeFormatDailies) {
		businessTypeFormatDailies.forEach(f -> this.commandProxy().insert(toEntity(f)));
	}

	@Override
	public void update(BusinessTypeFormatDaily businessTypeFormatDaily) {
		this.getEntityManager().createQuery(UPDATE_BY_KEY)
				.setParameter("companyId", businessTypeFormatDaily.getCompanyId())
				.setParameter("businessTypeCode", businessTypeFormatDaily.getBusinessTypeCode().v())
				.setParameter("attendanceItemId", businessTypeFormatDaily.getAttendanceItemId())
				.setParameter("sheetNo", businessTypeFormatDaily.getSheetNo())
				.setParameter("columnWidth", businessTypeFormatDaily.getColumnWidth())
				.setParameter("order", businessTypeFormatDaily.getOrder()).executeUpdate();
	}

	@Override
	public boolean checkExistData(String companyId, String businessTypeCode, BigDecimal sheetNo) {
		return this.queryProxy().query(IS_EXIST_DATA, long.class).setParameter("companyId", companyId)
				.setParameter("businessTypeCode", businessTypeCode)
				.setParameter("sheetNo", sheetNo).getSingle().get() > 0;
	}

	private static BusinessTypeFormatDaily toDomain(KrcmtBusinessTypeDaily krcmtBusinessTypeDaily) {
		BusinessTypeFormatDaily workTypeFormatDaily = BusinessTypeFormatDaily.createFromJavaType(
				krcmtBusinessTypeDaily.krcmtBusinessTypeDailyPK.companyId,
				krcmtBusinessTypeDaily.krcmtBusinessTypeDailyPK.businessTypeCode,
				krcmtBusinessTypeDaily.krcmtBusinessTypeDailyPK.attendanceItemId,
				krcmtBusinessTypeDaily.krcmtBusinessTypeDailyPK.sheetNo, krcmtBusinessTypeDaily.order,
				krcmtBusinessTypeDaily.columnWidth);
		return workTypeFormatDaily;
	}
	
	private KrcmtBusinessTypeDaily toEntity(BusinessTypeFormatDaily businessTypeFormatDaily){
		val entity = new KrcmtBusinessTypeDaily();
		
		entity.krcmtBusinessTypeDailyPK = new KrcmtBusinessTypeDailyPK();
		entity.krcmtBusinessTypeDailyPK.attendanceItemId = businessTypeFormatDaily.getAttendanceItemId();
		entity.krcmtBusinessTypeDailyPK.businessTypeCode = businessTypeFormatDaily.getBusinessTypeCode().v();
		entity.krcmtBusinessTypeDailyPK.companyId = businessTypeFormatDaily.getCompanyId();
		entity.krcmtBusinessTypeDailyPK.sheetNo = businessTypeFormatDaily.getSheetNo();
		entity.columnWidth = businessTypeFormatDaily.getColumnWidth();
		entity.order = businessTypeFormatDaily.getOrder();
		
		return entity;
	}
	
	@Override
	public void updateColumnsWidth(Map<Integer, Integer> lstHeader) {
		List<KrcmtBusinessTypeDaily> lstBusDailyItem = new ArrayList<>(); 
		CollectionUtil.split(lstHeader, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subMap -> {
			lstBusDailyItem.addAll(this.queryProxy()
				.query(SEL_FORMAT_BY_ATD_ITEM, KrcmtBusinessTypeDaily.class)
				.setParameter("companyId", AppContexts.user().companyId())
				.setParameter("lstItem", subMap.keySet())
				.getList());
		});
		for(KrcmtBusinessTypeDaily busItem : lstBusDailyItem) {
			busItem.columnWidth =  new BigDecimal(lstHeader.get(busItem.krcmtBusinessTypeDailyPK.attendanceItemId));
		}
		this.commandProxy().updateAll(lstBusDailyItem);
	}

	@Override
	public List<BusinessTypeFormatDaily> getBusinessTypeFormatByCompanyId(String companyId) {
		return this.queryProxy().query(FIND_BY_COMPANYID, KrcmtBusinessTypeDaily.class).setParameter("companyId", companyId).getList(f -> toDomain(f));
	}

	@Override
	public void deleteExistDataByCode(String businesstypeCode, String companyId, int sheetNo,
			List<Integer> attendanceItemIds) {
		CollectionUtil.split(attendanceItemIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			this.getEntityManager().createQuery(REMOVE_EXIST_DATA_BY_CODE)
				.setParameter("companyId", companyId)
				.setParameter("businessTypeCode", businesstypeCode)
				.setParameter("sheetNo", sheetNo)
				.setParameter("attendanceItemIds", subList)
				.executeUpdate();
		});
		this.getEntityManager().flush();
	}

	@Override
	public List<BusinessTypeFormatDaily> getBusinessTypeFormatDailyDetail(String companyId, String businessTypeCode) {
		return this.queryProxy().query(FIND_DETAIl_WITHOUT_SHEET_NO, KrcmtBusinessTypeDaily.class).setParameter("companyId", companyId)
				.setParameter("businessTypeCode", businessTypeCode)
				.getList(f -> toDomain(f));
	}

	@Override
	public void copy(String companyId, String businessTypeCode, List<String> listBusinessTypeCode) {
		
		List<BusinessTypeFormatDaily> listBusinessTypeFormatDailyBySelectedCode = this.getBusinessTypeFormat(companyId, businessTypeCode);
		
		List<KrcmtBusinessFormatSheet> listKrcmtBusinessFormatSheetBySelectedCode = this.queryProxy().query(FIND_SHEET_NAME, KrcmtBusinessFormatSheet.class)
					.setParameter("companyId", companyId)
					.setParameter("businessTypeCode", businessTypeCode)
					.getList();
		
		List<KrcmtBusinessTypeDaily> listKrcmtBusinessTypeDaily = this.queryProxy().query(FIND_LISTCODE, KrcmtBusinessTypeDaily.class)
					.setParameter("companyId", companyId)
					.setParameter("listBusinessTypeCode", listBusinessTypeCode)
					.getList();
		
		List<KrcmtBusinessFormatSheet> listKrcmtBusinessFormatSheet = this.queryProxy().query(FIND_SHEET_LISTCODE, KrcmtBusinessFormatSheet.class)
					.setParameter("companyId", companyId)
					.setParameter("listBusinessTypeCode", listBusinessTypeCode)
					.getList();
		
		this.commandProxy().removeAll(listKrcmtBusinessTypeDaily);
		this.commandProxy().removeAll(listKrcmtBusinessFormatSheet);
		this.getEntityManager().flush();
		
		List<KrcmtBusinessFormatSheet> newListKrcmtBusinessFormatSheet = new ArrayList<KrcmtBusinessFormatSheet>();
		List<KrcmtBusinessTypeDaily> newListKrcmtBusinessTypeDaily = new ArrayList<KrcmtBusinessTypeDaily>();
		for (String code : listBusinessTypeCode) {
			newListKrcmtBusinessFormatSheet.addAll(cloneKrcmtBusinessFormatSheetWithOtherCode(code, listKrcmtBusinessFormatSheetBySelectedCode));
			newListKrcmtBusinessTypeDaily.addAll(toKrcmtBusinessTypeDailyFromList(code, listBusinessTypeFormatDailyBySelectedCode));
		}
		
		this.commandProxy().insertAll(newListKrcmtBusinessFormatSheet);
		this.commandProxy().insertAll(newListKrcmtBusinessTypeDaily);
		
	}	
	
	private List<KrcmtBusinessFormatSheet> cloneKrcmtBusinessFormatSheetWithOtherCode(String businessTypeCode, List<KrcmtBusinessFormatSheet> listEntity) {
		List<KrcmtBusinessFormatSheet> result = listEntity.stream()
				.map(e -> new KrcmtBusinessFormatSheet(
						new KrcmtBusinessFormatSheetPK(e.krcmtBusinessFormatSheetPK.companyId, businessTypeCode, e.krcmtBusinessFormatSheetPK.sheetNo),
						e.sheetName))
				.collect(Collectors.toList());
		 
		return result;
	}
	
	private List<KrcmtBusinessTypeDaily> toKrcmtBusinessTypeDailyFromList(String businessTypeCode, List<BusinessTypeFormatDaily> listBusinessTypeFormatDaily) {
		
		List<BusinessTypeFormatDaily> newListBusinessTypeFormatDaily = listBusinessTypeFormatDaily.stream()
				.map(e -> {
					BusinessTypeFormatDaily businessTypeFormatDaily = e.clone();
					businessTypeFormatDaily.setBusinessTypeCode(new BusinessTypeCode(businessTypeCode));
					return businessTypeFormatDaily;
				}).collect(Collectors.toList());
		
		List<KrcmtBusinessTypeDaily> result = newListBusinessTypeFormatDaily.stream()
				.map(e -> toEntity(e))
				.collect(Collectors.toList());
		return result;
	}
}
