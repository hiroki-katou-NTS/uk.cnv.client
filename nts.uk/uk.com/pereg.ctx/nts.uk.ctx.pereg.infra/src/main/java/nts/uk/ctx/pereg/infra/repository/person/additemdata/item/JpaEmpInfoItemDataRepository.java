package nts.uk.ctx.pereg.infra.repository.person.additemdata.item;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.pereg.dom.person.additemdata.item.EmpInfoItemData;
import nts.uk.ctx.pereg.dom.person.additemdata.item.EmpInfoItemDataRepository;
import nts.uk.ctx.pereg.dom.person.personinfoctgdata.item.DataStateType;
import nts.uk.ctx.pereg.infra.entity.person.additemdata.item.PpemtSyaDataItem;
import nts.uk.ctx.pereg.infra.entity.person.additemdata.item.PpemtEmpInfoItemDataPk;
import nts.uk.ctx.pereg.infra.entity.person.info.ctg.PpemtCtg;
import nts.uk.ctx.pereg.infra.entity.person.info.item.PpemtItem;
import nts.uk.ctx.pereg.infra.entity.person.info.item.PpemtItemCommon;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.ItemValueType;

@Stateless
public class JpaEmpInfoItemDataRepository extends JpaRepository implements EmpInfoItemDataRepository {

	private static final String SELECT_ALL_INFO_ITEM_NO_WHERE = "SELECT id,pi.requiredAtr,pi.itemName,pi.itemCd,pc.ppemtPerInfoCtgPK.perInfoCtgId,pc.categoryCd,pm.itemType,pm.selectionItemRefType,pm.selectionItemRefCode"
			+ " FROM PpemtSyaDataItem id"
			+ " INNER JOIN PpemtItem pi ON id.ppemtEmpInfoItemDataPk.perInfoDefId = pi.ppemtPerInfoItemPK.perInfoItemDefId"
			+ " INNER JOIN PpemtCtg pc ON id.ppemtEmpInfoItemDataPk.recordId = pc.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " INNER JOIN PpemtItemCommon pm ON pi.itemCd = pm.ppemtPerInfoItemCmPK.itemCd AND pc.categoryCd = pm.ppemtPerInfoItemCmPK.categoryCd"
			+ " INNER JOIN PpemtSyaDataCtg ic ON id.ppemtEmpInfoItemDataPk.recordId = ic.recordId";

	public static final String SELECT_ALL_INFO_ITEM_BY_CTD_CODE_QUERY_STRING = SELECT_ALL_INFO_ITEM_NO_WHERE
			+ " WHERE pi.abolitionAtr=0 AND pc.categoryCd = :categoryCd AND pc.cid = :companyId AND ic.employeeId= :employeeId";

	private static final String SELECT_ALL_INFO_ITEM_NO_WHERE_2 = "SELECT id, pi, pc ,pm FROM PpemtSyaDataItem id"
			+ " INNER JOIN PpemtItem pi ON id.ppemtEmpInfoItemDataPk.perInfoDefId = pi.ppemtPerInfoItemPK.perInfoItemDefId"
			+ " INNER JOIN PpemtCtg pc ON pi.perInfoCtgId = pc.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " INNER JOIN PpemtItemCommon pm ON pi.itemCd = pm.ppemtPerInfoItemCmPK.itemCd AND pc.categoryCd = pm.ppemtPerInfoItemCmPK.categoryCd";
	private static final String SELECT_ALL_INFO_ITEM_BY_RECODE_ID_QUERY_STRING = SELECT_ALL_INFO_ITEM_NO_WHERE_2
			+ " WHERE id.ppemtEmpInfoItemDataPk.recordId = :recordId";
	
	private static final String SELECT_ALL_INFO_ITEM_BY_RECODE_IDS_QUERY_STRING = SELECT_ALL_INFO_ITEM_NO_WHERE_2
			+ " WHERE id.ppemtEmpInfoItemDataPk.recordId IN :recordId";

	private static final String SELECT_ALL_INFO_ITEM_BY_ITEMDEF_ID_AND_RECODE_ID = SELECT_ALL_INFO_ITEM_NO_WHERE_2
			+ " WHERE id.ppemtEmpInfoItemDataPk.perInfoDefId = :perInfoDefId AND id.ppemtEmpInfoItemDataPk.recordId = :recordId";
	
	

	private static final String SELECT_ALL_INFO_ITEM_BY_CTGID_AND_SID = SELECT_ALL_INFO_ITEM_NO_WHERE
			+ " WHERE ic.personInfoCtgId = :ctgid AND ic.employeeId = :sid";
	
	
	private static final String DELETE_ITEM_DATA = "DELETE FROM PpemtSyaDataItem WHERE ppemtEmpInfoItemDataPk.recordId = :recordId";
	
	private static final String SELECT_ITEM_DATA_OF_RECORD_ID_LIST = "SELECT id FROM PpemtSyaDataItem id "
			+ "WHERE id.ppemtEmpInfoItemDataPk.perInfoDefId = :itemId AND id.ppemtEmpInfoItemDataPk.recordId IN :recordIds";

	public static final String SELECT_ALL_INFO_ITEM_BY_ALL_CID_QUERY_STRING = "SELECT id.ppemtEmpInfoItemDataPk.perInfoDefId"
			+ " FROM PpemtSyaDataItem id"
			+ " INNER JOIN PpemtItem pi ON id.ppemtEmpInfoItemDataPk.perInfoDefId = pi.ppemtPerInfoItemPK.perInfoItemDefId"
			+ " INNER JOIN PpemtCtg pc ON pi.perInfoCtgId = pc.ppemtPerInfoCtgPK.perInfoCtgId"
			+ " INNER JOIN PpemtItemCommon pm ON pi.itemCd = pm.ppemtPerInfoItemCmPK.itemCd AND pc.categoryCd = pm.ppemtPerInfoItemCmPK.categoryCd"
			+ " WHERE pm.ppemtPerInfoItemCmPK.itemCd =:itemCd"
			+ " AND pi.perInfoCtgId IN :perInfoCtgId";

	@Override
	public List<EmpInfoItemData> getAllInfoItem(String categoryCd, String companyId, String employeeId) {
		return this.queryProxy().query(SELECT_ALL_INFO_ITEM_BY_CTD_CODE_QUERY_STRING, Object[].class)
				.setParameter("categoryCd", categoryCd).setParameter("companyId", companyId)
				.setParameter("employeeId", employeeId).getList(c -> toDomain(c));
	}

	private EmpInfoItemData toDomainNew(Object[] entity) {
		PpemtSyaDataItem itemData = (PpemtSyaDataItem) entity[0];
		PpemtItem personInforItem = (PpemtItem) entity[1];
		PpemtCtg personInforCategory = (PpemtCtg) entity[2];
		PpemtItemCommon perInfoItemCm = (PpemtItemCommon) entity[3];

		return EmpInfoItemData.createFromJavaType(personInforItem.itemCd,
				personInforItem.ppemtPerInfoItemPK.perInfoItemDefId, itemData.ppemtEmpInfoItemDataPk.recordId,
				personInforCategory.ppemtPerInfoCtgPK.perInfoCtgId, personInforCategory.categoryCd,
				personInforItem.itemName, personInforItem.requiredAtr, itemData.saveDataType, itemData.stringValue,
				itemData.intValue, itemData.dateValue,
				perInfoItemCm.dataType == null ? itemData.saveDataType : perInfoItemCm.dataType.intValue());

	}

	private EmpInfoItemData toDomain(Object[] entity) {
		int dataStateType = entity[4] != null ? Integer.valueOf(entity[3].toString()) : 0;

		BigDecimal intValue = new BigDecimal(entity[6] != null ? Integer.valueOf(entity[6].toString()) : null);

		GeneralDate dateValue = GeneralDate.fromString(String.valueOf(entity[7].toString()), "yyyy-MM-dd");

		int isRequired = Integer.parseInt(entity[8] != null ? entity[9].toString() : "0");

		int dataType = Integer.parseInt(entity[13] != null ? entity[13].toString() : "0");

		int selectionItemRefType = Integer.parseInt(entity[14] != null ? entity[14].toString() : "0");

		String selectionItemRefCd = entity[15] != null ? entity[15].toString() : "";

		EmpInfoItemData newInfoItem = EmpInfoItemData.createFromJavaType(entity[10].toString(), entity[0].toString(),
				entity[1].toString(), entity[11].toString(), entity[12].toString(), entity[9].toString(), isRequired,
				dataStateType, entity[5].toString(), intValue, dateValue, dataType);

		newInfoItem.setSelectionItemRefType(selectionItemRefType);

		newInfoItem.setSelectionItemRefCd(selectionItemRefCd);
		
		return newInfoItem;
	}

	@Override
	public List<EmpInfoItemData> getAllInfoItemByRecordId(String recordId) {
		List<Object[]> lstEntity = this.queryProxy()
				.query(SELECT_ALL_INFO_ITEM_BY_RECODE_ID_QUERY_STRING, Object[].class)
				.setParameter("recordId", recordId).getList();
		if (lstEntity == null)
			return new ArrayList<>();
		return lstEntity.stream().map(c -> toDomainNew(c)).collect(Collectors.toList());
	}

	/**
	 * Convert from domain to entity
	 * 
	 * @param domain
	 * @return
	 */
	private PpemtSyaDataItem toEntiy(EmpInfoItemData domain) {
		PpemtEmpInfoItemDataPk key = new PpemtEmpInfoItemDataPk(domain.getPerInfoDefId(), domain.getRecordId());
		String stringValue = null;
		BigDecimal intValue = null;
		GeneralDate dateValue = null;
		switch (EnumAdaptor.valueOf(domain.getDataState().getDataStateType().value, ItemValueType.class)) {
		case STRING:
		case SELECTION:
			stringValue = domain.getDataState().getStringValue();
			break;
		case NUMERIC:
		case TIME:
		case TIMEPOINT:
			intValue = domain.getDataState().getNumberValue();
			break;
		case DATE:
			dateValue = domain.getDataState().getDateValue();
			break;
		default:
			break;
		}
		return new PpemtSyaDataItem(key, domain.getDataState().getDataStateType().value, stringValue, intValue,
				dateValue);
	}

	private void updateEntiy(EmpInfoItemData domain, PpemtSyaDataItem entity) {
		entity.saveDataType = domain.getDataState().getDataStateType().value;

		switch (EnumAdaptor.valueOf(entity.saveDataType, ItemValueType.class)) {
		case STRING:
		case SELECTION:
			entity.stringValue = domain.getDataState().getStringValue();
			break;
		case NUMERIC:
		case TIME:
		case TIMEPOINT:
			entity.intValue = domain.getDataState().getNumberValue();
			break;
		case DATE:
			entity.dateValue = domain.getDataState().getDateValue();
			break;
		default:
			break;
		}
	}

	@Override
	public void addItemData(EmpInfoItemData domain) {
		this.commandProxy().insert(toEntiy(domain));
	}
	
	@Override
	public void registerEmpInfoItemData(EmpInfoItemData domain) {
		// Get exist item
		PpemtEmpInfoItemDataPk key = new PpemtEmpInfoItemDataPk(domain.getPerInfoDefId(), domain.getRecordId());
		Optional<PpemtSyaDataItem> existItem = this.queryProxy().find(key, PpemtSyaDataItem.class);
		
		if (!existItem.isPresent()) {
			addItemData(domain);
		} else {
			updateEntiy(domain, existItem.get());
			// Update table
			this.commandProxy().update(existItem.get());
		}
	}

	@Override
	public void updateEmpInfoItemData(EmpInfoItemData domain) {
		// Get exist item
		PpemtEmpInfoItemDataPk key = new PpemtEmpInfoItemDataPk(domain.getPerInfoDefId(), domain.getRecordId());
		Optional<PpemtSyaDataItem> existItem = this.queryProxy().find(key, PpemtSyaDataItem.class);
		if (!existItem.isPresent()) {
			throw new RuntimeException("Invalid PpemtSyaDataItem");
		}
		updateEntiy(domain, existItem.get());
		// Update table
		this.commandProxy().update(existItem.get());
	}

	@Override
	public void deleteEmployInfoItemData(String recordId) {
		this.getEntityManager().createQuery(DELETE_ITEM_DATA).setParameter("recordId", recordId).executeUpdate();
		this.getEntityManager().flush();

	}

	@Override
	public List<EmpInfoItemData> getAllInfoItemBySidCtgId(String ctgId, String employeeId) {
		return this.queryProxy().query(SELECT_ALL_INFO_ITEM_BY_CTGID_AND_SID, Object[].class)
				.setParameter("ctgid", ctgId).setParameter("sid", employeeId).getList(c -> toDomain(c));
	}

	@Override
	public Optional<EmpInfoItemData> getInfoItemByItemDefIdAndRecordId(String itemDefId, String recordId) {
		return this.queryProxy().query(SELECT_ALL_INFO_ITEM_BY_ITEMDEF_ID_AND_RECODE_ID, Object[].class)
				.setParameter("recordId", recordId).setParameter("perInfoDefId", itemDefId)
				.getSingle(c -> toDomainNew(c));
	}
	
	@Override
	public List<EmpInfoItemData> getItemsData(String itemDefId, List<String> recordIds) {
		if (recordIds.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<PpemtSyaDataItem> entites = new ArrayList<>();
		CollectionUtil.split(recordIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			entites.addAll(this.queryProxy()
				.query(SELECT_ITEM_DATA_OF_RECORD_ID_LIST, PpemtSyaDataItem.class).setParameter("itemId", itemDefId)
				.setParameter("recordIds", subList).getList());
		});
		
		return entites.stream()
				.map(ent -> EmpInfoItemData.createFromJavaType(ent.ppemtEmpInfoItemDataPk.perInfoDefId,
						ent.ppemtEmpInfoItemDataPk.recordId, ent.saveDataType, ent.stringValue, ent.intValue,
						ent.dateValue))
				.collect(Collectors.toList());
	}

	@Override
	public boolean hasItemData(String itemCd, List<String> perInfoCtgId) {
		List<Object[]> item = new ArrayList<>();
		CollectionUtil.split(perInfoCtgId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			item.addAll(this.queryProxy().query(SELECT_ALL_INFO_ITEM_BY_ALL_CID_QUERY_STRING, Object[].class)
			.setParameter("itemCd", itemCd)
			.setParameter("perInfoCtgId", subList)
			.getList());
		});
		return item.size() > 0;
	}

	@Override
	public List<EmpInfoItemData> getAllInfoItemByRecordId(List<String> recordIds) {
		if(recordIds.isEmpty()) { return new ArrayList<>();}
		List<Object[]> lstEntity = this.queryProxy()
				.query(SELECT_ALL_INFO_ITEM_BY_RECODE_IDS_QUERY_STRING, Object[].class)
				.setParameter("recordId", recordIds).getList();
		if (lstEntity == null)
			return new ArrayList<>();
		return lstEntity.stream().map(c -> toDomainNew(c)).collect(Collectors.toList());
	}

	@Override
	public List<EmpInfoItemData> getAllInfoItemByRecordId(List<String> itemIds, List<String> recordIds) {
		List<EmpInfoItemData> result = new ArrayList<>();
		CollectionUtil.split(recordIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT RECORD_ID, PER_INFO_DEF_ID FROM PPEMT_SYA_DATA_ITEM WHERE PER_INFO_DEF_ID IN ("
					+ NtsStatement.In.createParamsString(itemIds) + ")" + " AND RECORD_ID IN ( "
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				for (int i = 0 ; i < itemIds.size(); i++) {
					stmt.setString( 1 + i, itemIds.get(i));
				}
				for (int i = 0 ; i < subList.size(); i++) {
					stmt.setString(itemIds.size() + 1 + i, subList.get(i));
				}

				new NtsResultSet(stmt.executeQuery()).forEach(rec -> {
					EmpInfoItemData empItemData = new EmpInfoItemData();
					empItemData.setRecordId(rec.getString("RECORD_ID"));
					empItemData.setPerInfoDefId(rec.getString("PER_INFO_DEF_ID"));
					result.add(empItemData);
				});
				
			}catch (SQLException e) {
				throw new RuntimeException(e);
			}
			
		});
		return result;
	}
	
	@Override
	public void addAll(List<EmpInfoItemData> domains) {
		String INS_SQL = "INSERT INTO PPEMT_SYA_DATA_ITEM ( INS_DATE, INS_CCD, INS_SCD, INS_PG, "
				+ "  UPD_DATE,  UPD_CCD,  UPD_SCD, UPD_PG, "
				+ "  EXCLUS_VER, CONTRACT_CD, "
				+ "  RECORD_ID, PER_INFO_DEF_ID, SAVE_DATA_ATR, STRING_VAL , INT_VAL , DATE_VAL)"
				+ "  VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ "  UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL, "
				+ "  EXCLUS_VER_VAL, CONTRACT_CD_VAL, "
				+ "  RECORD_ID_VAL, PER_INFO_DEF_ID_VAL, SAVE_DATA_ATR_VAL, STRING_VAL_VAL, INT_VAL_VAL, DATE_VAL_VAL);";
    	GeneralDateTime insertTime = GeneralDateTime.now();
    	String insCcd = AppContexts.user().companyCode();
    	String insScd = AppContexts.user().employeeCode();
    	String insPg = AppContexts.programId();
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg =  insPg;
		String contractCd = AppContexts.user().contractCode();
		StringBuilder sb = new StringBuilder();
		domains.stream().forEach(c ->{
			String sql = INS_SQL;
			sql = sql.replace("INS_DATE_VAL", "'" + insertTime +"'");
			sql = sql.replace("INS_CCD_VAL", "'" + insCcd +"'");
			sql = sql.replace("INS_SCD_VAL", "'" + insScd +"'");
			sql = sql.replace("INS_PG_VAL", "'" + insPg +"'");
			
			sql = sql.replace("UPD_DATE_VAL", "'" + insertTime +"'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd +"'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd +"'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg +"'");
			
			sql = sql.replace("EXCLUS_VER_VAL", "0");
			sql = sql.replace("CONTRACT_CD_VAL", "'" + contractCd + "'");
			
			sql = sql.replace("RECORD_ID_VAL", "'" + c.getRecordId() +"'").replace("_noData", "");
			sql = sql.replace("PER_INFO_DEF_ID_VAL", "'" + c.getPerInfoDefId() +"'");
			
			sql = sql.replace("SAVE_DATA_ATR_VAL", ""+c.getDataState().getDataStateType().value +"");
			
			if(c.getDataState().getDataStateType() == DataStateType.String) {
				sql = sql.replace("STRING_VAL_VAL", c.getDataState().getStringValue() == null ? "null": "'" + c.getDataState().getStringValue() +"'");
			}else {
				sql = sql.replace("STRING_VAL_VAL", "null");
			}
		
			if(c.getDataState().getDataStateType() == DataStateType.Numeric) {
				sql = sql.replace("INT_VAL_VAL",  c.getDataState().getNumberValue() == null? "null": "" + c.getDataState().getNumberValue() +"");
			}else {
				sql = sql.replace("INT_VAL_VAL",  "null");
			}
			if(c.getDataState().getDataStateType() == DataStateType.Date) {
				sql = sql.replace("DATE_VAL_VAL", c.getDataState().getDateValue() == null? "null": "'" + c.getDataState().getDateValue() +"'");
			}else {
				sql = sql.replace("DATE_VAL_VAL", "null");
			}
			
			sb.append(sql);
		});
		int  records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
	}

	@Override
	public void updateAll(List<EmpInfoItemData> domains) {
		String UP_SQL = "UPDATE PPEMT_SYA_DATA_ITEM SET  UPD_DATE = UPD_DATE_VAL,  UPD_CCD = UPD_CCD_VAL,  UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL,"
				+ " SAVE_DATA_ATR = SAVE_DATA_ATR_VAL, STRING_VAL = STRING_VAL_VAL, INT_VAL = INT_VAL_VAL, DATE_VAL = DATE_VAL_VAL"
				+ " WHERE  RECORD_ID = RECORD_ID_VAL AND  PER_INFO_DEF_ID = PER_INFO_DEF_ID_VAL; ";
    	GeneralDateTime insertTime = GeneralDateTime.now();
		String updCcd = AppContexts.user().companyCode();
		String updScd = AppContexts.user().employeeCode();
		String updPg =  AppContexts.programId();
		StringBuilder sb = new StringBuilder();
		domains.stream().forEach(c ->{
			String sql = UP_SQL;
			
			sql = sql.replace("UPD_DATE_VAL", "'" + insertTime +"'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd +"'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd +"'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg +"'");
			
			sql = sql.replace("RECORD_ID_VAL", "'" + c.getRecordId() +"'");
			sql = sql.replace("PER_INFO_DEF_ID_VAL", "'" + c.getPerInfoDefId()+"'");
			sql = sql.replace("SAVE_DATA_ATR_VAL", "" + c.getDataState().getDataStateType().value +"");
			if(c.getDataState().getDataStateType() == DataStateType.String) {
				sql = sql.replace("STRING_VAL_VAL", c.getDataState().getStringValue() == null ? "null": "'" + c.getDataState().getStringValue() +"'");
			}else {
				sql = sql.replace("STRING_VAL_VAL", "null");
			}
			
			if(c.getDataState().getDataStateType() == DataStateType.Numeric) {
				sql = sql.replace("INT_VAL_VAL",  c.getDataState().getNumberValue() == null? "null": "" + c.getDataState().getNumberValue() +"");
			}else {
				sql = sql.replace("INT_VAL_VAL",  "null");
			}
			if(c.getDataState().getDataStateType() == DataStateType.Date) {
				sql = sql.replace("DATE_VAL_VAL", c.getDataState().getDateValue() == null? "null": "'" + c.getDataState().getDateValue() +"'");
			}else {
				sql = sql.replace("DATE_VAL_VAL", "null");
			}
			sb.append(sql);
		});
		int  records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}
}
