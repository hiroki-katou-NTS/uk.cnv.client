package nts.uk.ctx.at.shared.infra.repository.remainingnumber;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.error.BusinessException;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.paymana.KrcdtPayoutMng;
import nts.uk.shr.com.context.AppContexts;


@Stateless
public class JpaPayoutManagementDataRepo extends JpaRepository implements PayoutManagementDataRepository {

	private static final String QUERY_BYSID = "SELECT p FROM KrcdtPayoutMng p WHERE p.cID = :cid AND p.sID =:employeeId ";
	
	private static final String QUERY_BY_SID_CID_DAYOFF = "SELECT p FROM KrcdtPayoutMng p WHERE p.cID = :cid AND p.sID =:employeeId AND p.dayOff = :dayoffDate ";

	private static final String QUERY_BY_SID_CID_LIST_DAYOFF = "SELECT p FROM KrcdtPayoutMng p WHERE p.cID = :cid AND p.sID =:employeeId AND p.dayOff IN :listDayOff ";
	
	private static final String QUERY_BYSID_WITH_COND = String.join(" ", QUERY_BYSID, " AND p.stateAtr = :state ");

	private static final String QUERY_BY_SID_DATEPERIOD = "SELECT p FROM KrcdtPayoutMng p WHERE p.sID =:sid AND (p.stateAtr = :state OR p.payoutId =:subOfHDID))";

	private static final String  QUERY_BY_SID_STATE_AND_IN_SUB = "SELECT p FROM KrcdtPayoutMng p "
			+ " inner join KrcmtSubOfHDManaData s on s.subOfHDID = p.payoutId "
			+ " WHERE p.sID = :sid AND (p.stateAtr = 0 AND s.remainDays <> 0) ORDER BY p.unknownDate, p.dayOff";

	private static final String  QUERY_BY_SID_PERIOD_AND_IN_SUB = "SELECT p FROM KrcdtPayoutMng p "
			+ "WHERE p.sID = :sid AND ((p.dayOff >= :startDate AND p.dayOff <= :endDate)";
	
	private static final String DELETE_QUERY = "DELETE FROM KrcdtPayoutMng a WHERE a.payoutId= :payoutId";

	private static final String QUERY_BY_SUBID = "SELECT p FROM KrcdtPayoutMng p where p.payoutId =:subOfHDID";
	
//	private static final String QUERY_DELETE_SUB = "DELETE FROM KrcmtPayoutSubOfHDMana p WHERE p.krcmtPayoutSubOfHDManaPK.payoutId = :payoutId ";
	
	private static final String QUERY_SID_DATE_PERIOD = "SELECT c FROM KrcdtPayoutMng c"
			+ " WHERE c.sID = :sid"
			+ " AND c.dayOff >= :startDate"
			+ " AND c.dayOff <= :endDate";
	
	private static final String QUERY_BY_STATEATR = "SELECT c FROM KrcdtPayoutMng c"
			+ " WHERE c.sID = :sid"
			+ " AND c.stateAtr = :stateAtr";
	private static final String QUERY_BY_EACH_PERIOD = QUERY_SID_DATE_PERIOD
			+ " AND (c.unUsedDays > :unUsedDays AND c.expiredDate >= :sDate AND c.expiredDate <= :eDate)"
			+ " OR (c.stateAtr = :stateAtr AND c.disapearDate >= :sDate AND c.disapearDate <= :eDate) ";
	private static final String QUERY_BY_SID_HOLIDAY = "SELECT c FROM KrcdtPayoutMng c"
			+ " WHERE c.sID = :employeeId"
			+ " AND c.unknownDate = :unknownDate"
			+ " AND c.dayOff >= :startDate";
	private static final String QUERY_BYSID_COND_DATE = QUERY_BYSID_WITH_COND + " AND p.dayOff < :dayOff";
	private String QUERY_UNUSER_STATE = "SELECT p FROM KrcdtPayoutMng p "
			+ " WHERE p.cID = :cid"
			+ " AND p.sID =:employeeId"
			+ " AND (p.dayOff < :dayOff OR p.dayOff is null)"
			+ " AND p.unUsedDays > :unUsedDays"
			+ " AND p.stateAtr = :stateAtr";

	private static final String QUERY_BY_SID = "SELECT p FROM KrcdtPayoutMng p WHERE p.sID =:employeeId ORDER BY p.unknownDate, p.dayOff";
	
	private static final String QUERY_BY_UNKNOWN_DATE = "SELECT p FROM KrcdtPayoutMng p"
			+ " WHERE p.sID =:employeeId"
			+ " AND p.dayOff IN :dayOffDates ORDER BY p.dayOff ASC";
	
	private static final String QUERY_BY_SID_AND_ATR = "SELECT p FROM KrcdtPayoutMng p WHERE p.cID = :cid AND p.sID =:employeeId AND p.stateAtr = 0 ORDER BY p.dayOff";
	
 	@Override
	public List<PayoutManagementData> getSidWithCodDate(String cid, String sid, int state, GeneralDate ymd) {
		List<KrcdtPayoutMng> list = this.queryProxy().query(QUERY_BYSID_COND_DATE, KrcdtPayoutMng.class)
				.setParameter("cid", cid)
				.setParameter("employeeId", sid)
				.setParameter("state", state)
				.setParameter("dayOff", ymd)
				.getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<PayoutManagementData> getSid(String cid, String sid) {
		List<KrcdtPayoutMng> list = this.queryProxy().query(QUERY_BYSID, KrcdtPayoutMng.class)
				.setParameter("cid", cid).setParameter("employeeId", sid).getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}
	@Override
	public List<PayoutManagementData> getBySidAndStateAtr(String cid, String sid) {
		List<KrcdtPayoutMng> list = this.queryProxy().query(QUERY_BY_SID_AND_ATR, KrcdtPayoutMng.class)
				.setParameter("cid", cid).setParameter("employeeId", sid).getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}
	@Override
	public List<PayoutManagementData> getSidWithCod(String cid, String sid, int state) {
		List<KrcdtPayoutMng> list = this.queryProxy().query(QUERY_BYSID_WITH_COND, KrcdtPayoutMng.class)
				.setParameter("cid", cid).setParameter("employeeId", sid).setParameter("state", state).getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	/**
	 * Convert to domain
	 * 
	 * @param entity
	 * @return
	 */
	private PayoutManagementData toDomain(KrcdtPayoutMng entity) {
		return new PayoutManagementData(entity.payoutId, entity.cID, entity.sID, entity.unknownDate, entity.dayOff,
				entity.expiredDate, entity.lawAtr, entity.occurredDays, entity.unUsedDays, entity.stateAtr);
	}

	@Override
	public void create(PayoutManagementData domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	/**
	 * Convert to entity
	 * 
	 * @param domain
	 * @return
	 */
	private KrcdtPayoutMng toEntity(PayoutManagementData domain) {
		KrcdtPayoutMng entity = new KrcdtPayoutMng();
		entity.payoutId = domain.getPayoutId();
		entity.sID = domain.getSID();
		entity.cID = domain.getCID();
		entity.unknownDate = domain.getPayoutDate().isUnknownDate();
		if (domain.getPayoutDate().getDayoffDate().isPresent()) {
			entity.dayOff = domain.getPayoutDate().getDayoffDate().get();
		}
		entity.expiredDate = domain.getExpiredDate();
		entity.lawAtr = domain.getLawAtr().value;
		entity.occurredDays = domain.getOccurredDays().v();
		entity.unUsedDays = domain.getUnUsedDays().v();
		entity.stateAtr = domain.getStateAtr().value;
		return entity;
	}

//	@Override
//	public void deletePayoutSubOfHDMana(String payoutId) {
//		this.getEntityManager().createQuery(QUERY_DELETE_SUB).setParameter("payoutId", payoutId).executeUpdate();
//	}

	@Override
	public void delete(String payoutId) {
		Optional<KrcdtPayoutMng> entity = this.queryProxy().find(payoutId, KrcdtPayoutMng.class);
		if (entity.isPresent()) {
			this.getEntityManager().createQuery(DELETE_QUERY).setParameter("payoutId", payoutId).executeUpdate();
		}else{
			throw new BusinessException("Msg_198");
		}
		
	}
	
	@Override
	public void deleteAllByEmployeeId(String employeeId) {
		String jpql = "DELETE FROM KrcdtPayoutMng a WHERE a.sID = :sid";
		this.getEntityManager().createQuery(jpql).setParameter("sid", employeeId).executeUpdate();
	}

	@Override
	public void update(PayoutManagementData domain) {
		this.commandProxy().update(toEntity(domain));
	}

	@Override
	public Optional<PayoutManagementData> findByID(String ID) {
		String QUERY_BY_ID = "SELECT s FROM KrcdtPayoutMng s WHERE s.payoutId = :payoutId";
		Optional<KrcdtPayoutMng> entity = this.queryProxy().query(QUERY_BY_ID, KrcdtPayoutMng.class).setParameter("payoutId", ID).getSingle();
		if (entity.isPresent()) {
			return Optional.ofNullable(toDomain(entity.get()));
		}
		return Optional.empty();
	}

	@Override
	public List<PayoutManagementData> getBySidDatePeriod(String sid, String subOfHDID, int digestionAtr) {
		List<KrcdtPayoutMng> listSubOfHD = this.queryProxy()
				.query(QUERY_BY_SID_DATEPERIOD, KrcdtPayoutMng.class).setParameter("sid", sid)
				.setParameter("state", digestionAtr).setParameter("subOfHDID", subOfHDID).getList();
		return listSubOfHD.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<PayoutManagementData> getBySidStateAndInSub(String sid) {
		List<KrcdtPayoutMng> listpayout = this.queryProxy()
				.query(QUERY_BY_SID_STATE_AND_IN_SUB, KrcdtPayoutMng.class).setParameter("sid", sid).getList();
		return listpayout.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	public Optional<PayoutManagementData> find(String cID, String sID, GeneralDate dayoffDate) {
		return this.queryProxy().
				query(QUERY_BY_SID_CID_DAYOFF, KrcdtPayoutMng.class).setParameter("employeeId", sID)
				.setParameter("cid", cID).setParameter("dayoffDate", dayoffDate).getSingle().map(i -> toDomain(i));
	}
	
	public List<PayoutManagementData> getByListPayoutDate(String cID, String sID, List<GeneralDate> lstDayOffDate) {
		return this.queryProxy().
				query(QUERY_BY_SID_CID_LIST_DAYOFF, KrcdtPayoutMng.class).setParameter("employeeId", sID)
				.setParameter("cid", cID).setParameter("listDayOff", lstDayOffDate).getList()
				.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}
	
	public List<PayoutManagementData> getBySidPeriodAndInSub(String sid, GeneralDate startDate, GeneralDate endDate) {
		List<KrcdtPayoutMng> listpayout = this.queryProxy()
				.query(QUERY_BY_SID_PERIOD_AND_IN_SUB, KrcdtPayoutMng.class).setParameter("sid", sid)
				.setParameter("startDate", startDate).setParameter("endDate", endDate).getList();
		return listpayout.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<PayoutManagementData> getDayoffDateBysubOfHDID(String subOfHDID) {
		List<KrcdtPayoutMng> listpayout = this.queryProxy().query(QUERY_BY_SUBID, KrcdtPayoutMng.class ).setParameter("subOfHDID", subOfHDID).getList(); 
		return listpayout.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<PayoutManagementData> getBySidAndDatePeriod(String sid, DatePeriod dateData) {
		List<KrcdtPayoutMng> listpayout = this.queryProxy().query(QUERY_SID_DATE_PERIOD, KrcdtPayoutMng.class )
				.setParameter("sid", sid)
				.setParameter("startDate", dateData.start())
				.setParameter("endDate", dateData.end())
				.getList(); 
		return listpayout.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<PayoutManagementData> getByStateAtr(String sid, DigestionAtr stateAtr) {
		List<KrcdtPayoutMng> list = this.queryProxy().query(QUERY_BY_STATEATR, KrcdtPayoutMng.class)
				.setParameter("sid", sid)
				.setParameter("stateAtr", stateAtr.value)
				.getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<PayoutManagementData> getEachPeriod(String sid, DatePeriod dateTmp, DatePeriod dateData,
			Double unUseDays, DigestionAtr stateAtr) {
		List<KrcdtPayoutMng> listpayout = this.queryProxy().query(QUERY_BY_EACH_PERIOD, KrcdtPayoutMng.class )
				.setParameter("sid", sid)
				.setParameter("startDate", dateTmp.start())
				.setParameter("endDate", dateTmp.end())
				.setParameter("unUsedDays", unUseDays)
				.setParameter("sDate", dateData.start())
				.setParameter("eDate", dateData.end())
				.setParameter("stateAtr", stateAtr.value)
				.getList(); 
		return listpayout.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<PayoutManagementData> getByHoliday(String sid, Boolean unknownDate, DatePeriod dayOff) {
		List<KrcdtPayoutMng> listLeaveData = this.queryProxy()
				.query(QUERY_BY_SID_HOLIDAY, KrcdtPayoutMng.class)
				.setParameter("employeeId", sid)
				.setParameter("unknownDate", unknownDate)
				.setParameter("startDate", dayOff.start()).getList();
		return listLeaveData.stream().map(x -> toDomain(x)).collect(Collectors.toList());
	}
	@Override
	public void deleteById(List<String> payoutId) {
		this.commandProxy().removeAll(KrcdtPayoutMng.class, payoutId);
	}

	@Override
	public List<PayoutManagementData> getByUnUseState(String cid, String sid, GeneralDate ymd, double unUse, DigestionAtr state) {
		List<KrcdtPayoutMng> list = this.queryProxy().query(QUERY_UNUSER_STATE, KrcdtPayoutMng.class)
				.setParameter("cid", cid)
				.setParameter("employeeId", sid)
				.setParameter("dayOff", ymd)
				.setParameter("unUsedDays", unUse)
				.setParameter("stateAtr", state.value).getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository#getSidWithCod(java.lang.String, java.util.List, int)
	 */
	@Override
	public Map<String ,Double> getAllSidWithCod(String cid, List<String> sid, int state) {
		Map <String ,Double> result = new HashMap<>();
		CollectionUtil.split(sid, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_PAYOUT_MNG WHERE  CID = ? AND STATE_ATR = ?   AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				stmt.setInt(2, state);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(3 + i, subList.get(i));
				}
				List<KrcdtPayoutMng> data = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					KrcdtPayoutMng entity = new KrcdtPayoutMng();
					
					entity.cID = rec.getString("CID");
					entity.sID = rec.getString("SID");
					entity.unUsedDays = rec.getDouble("UNUSED_DAYS");
					return entity;
			
				});
				Map<String, List<KrcdtPayoutMng>> dataMap = data.parallelStream().collect(Collectors.groupingBy(c -> c.sID));
				dataMap.entrySet().parallelStream().forEach(c ->{
					result.put(c.getKey(), c.getValue().parallelStream().mapToDouble(i -> i.unUsedDays).sum());
				});
			
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	@Override
	public List<PayoutManagementData> getBySidsAndCid(String cid, List<String> sid) {
		List<PayoutManagementData> result = new ArrayList<>();
		CollectionUtil.split(sid, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_PAYOUT_MNG WHERE  CID = ?  AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}
				List<PayoutManagementData> data = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					KrcdtPayoutMng entity = new KrcdtPayoutMng();
					entity.payoutId = rec.getString("PAYOUT_ID");
					entity.cID = rec.getString("CID");
					entity.sID = rec.getString("SID");
					entity.unknownDate = rec.getBoolean("UNKNOWN_DATE");
					
					entity.dayOff = rec.getGeneralDate("DAYOFF_DATE");
					entity.expiredDate = rec.getGeneralDate("EXPIRED_DATE");
					entity.lawAtr = rec.getInt("LAW_ATR");
					
					entity.occurredDays = rec.getDouble("OCCURRENCE_DAYS");
					entity.unUsedDays = rec.getDouble("UNUSED_DAYS");
					
					entity.stateAtr = rec.getInt("STATE_ATR");
					entity.disapearDate = rec.getGeneralDate("DISAPEAR_DATE");
					return toDomain(entity);
			
				});
				result.addAll(data);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	@Override
	public void addAll(List<PayoutManagementData> domains) {
		String INS_SQL = "INSERT INTO KRCDT_PAYOUT_MNG (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " PAYOUT_ID, CONTRACT_CD, CID, SID, UNKNOWN_DATE, DAYOFF_DATE, EXPIRED_DATE, LAW_ATR, OCCURRENCE_DAYS,"
				+ " UNUSED_DAYS, STATE_ATR, DISAPEAR_DATE)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " PAYOUT_ID_VAL, CONTRACT_CD_VAL, CID_VAL, SID_VAL, UNKNOWN_DATE_VAL, DAYOFF_DATE_VAL, EXPIRED_DATE_VAL, LAW_ATR_VAL, OCCURRENCE_DAYS_VAL,"
				+ " UNUSED_DAYS_VAL, STATE_ATR_VAL, DATE_VAL); ";
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();
		String contractCd = AppContexts.user().contractCode();
		
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
		boolean isPostgreSQL = this.database().is(DatabaseProduct.POSTGRESQL);
		domains.parallelStream().forEach(c -> {
			String sql = INS_SQL;
			sql = sql.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("INS_CCD_VAL", "'" + insCcd + "'");
			sql = sql.replace("INS_SCD_VAL", "'" + insScd + "'");
			sql = sql.replace("INS_PG_VAL", "'" + insPg + "'");

			sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

			sql = sql.replace("PAYOUT_ID_VAL", "'" + c.getPayoutId() + "'");
			sql = sql.replace("CONTRACT_CD_VAL", "'" + contractCd + "'");
			sql = sql.replace("CID_VAL", "'" + c.getCID()+ "'");
			sql = sql.replace("SID_VAL", "'" + c.getSID()+ "'");
			
			if (isPostgreSQL) {
				sql = sql.replace("UNKNOWN_DATE_VAL", "" + c.getPayoutDate().isUnknownDate());
			} else {
	            sql = sql.replace("UNKNOWN_DATE_VAL", c.getPayoutDate().isUnknownDate() == true? "1":"0");
			}
			sql = sql.replace("DAYOFF_DATE_VAL", c.getPayoutDate().getDayoffDate().isPresent()
					? "'" + c.getPayoutDate().getDayoffDate().get() +"'" : "null");
			sql = sql.replace("EXPIRED_DATE_VAL", c.getExpiredDate() == null? "null" : "'" + c.getExpiredDate() + "'");
			
			sql = sql.replace("LAW_ATR_VAL", ""+ c.getLawAtr().value +"");
			sql = sql.replace("OCCURRENCE_DAYS_VAL", c.getOccurredDays() == null? "null": ""+ c.getOccurredDays().v() + "");
			
			
			sql = sql.replace("UNUSED_DAYS_VAL", c.getUnUsedDays() == null? "null": "" + c.getUnUsedDays().v() +"");
			sql = sql.replace("STATE_ATR_VAL", "" + c.getStateAtr().value +"");
			sql = sql.replace("DATE_VAL", c.getDisapearDate() == null? "null": (c.getDisapearDate().isPresent()? "'"+ c.getDisapearDate().get()+"'": "null"));
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}
	@Override
	public List<PayoutManagementData> getAllBySid(String sid){
		List<KrcdtPayoutMng> list = this.queryProxy().query(QUERY_BY_SID, KrcdtPayoutMng.class)
				.setParameter("employeeId", sid)
				.getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}
	
	@Override
	public List<PayoutManagementData> getAllByUnknownDate(String sid, List<String> dayOffDates) {
		if (dayOffDates.isEmpty()) {
			return new ArrayList<>();
		}
		List<KrcdtPayoutMng> list = this.queryProxy().query(QUERY_BY_UNKNOWN_DATE, KrcdtPayoutMng.class)
				.setParameter("employeeId", sid)
				.setParameter("dayOffDates", dayOffDates.stream()
												.map(x -> GeneralDate.fromString(x, "yyyy-MM-dd"))
												.collect(Collectors.toList()))
				.getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public void delete(List<PayoutManagementData> payoutManagementDatas) {
		String QUERY_DELETE_LIST_ID = "DELETE FROM KrcdtPayoutMng a WHERE a.payoutId IN :payoutIds";
		List<String> payoutIds = payoutManagementDatas.stream().map(x -> x.getPayoutId()).collect(Collectors.toList());
		this.getEntityManager().createQuery(QUERY_DELETE_LIST_ID).setParameter("payoutIds", payoutIds).executeUpdate();
	}
	
	@Override
	public List<PayoutManagementData> getByListId(List<String> payoutIds) {
		String QUERY_BY_LIST_ID = "SELECT s FROM KrcdtPayoutMng s WHERE s.payoutId IN :payoutIds";
		return this.queryProxy().query(QUERY_BY_LIST_ID, KrcdtPayoutMng.class).setParameter("payoutIds", payoutIds)
				.getList(x -> toDomain(x));
	}
	
	@Override
	public List<PayoutManagementData> getByIdAndUnUse(String cid, String sid, GeneralDate expiredDate, double unUse) {
		String QUERY = "SELECT s FROM KrcdtPayoutMng s"
				+ " WHERE s.sID = :sid"
				+ " AND s.cID = :cid"
				+ " AND s.expiredDate >= :expiredDate"
				+ " AND s.unUsedDays >= :unUse"
				+ " ORDER BY s.dayOff ASC";
		return this.queryProxy().query(QUERY, KrcdtPayoutMng.class)
				.setParameter("sid", sid)
				.setParameter("cid", cid)
				.setParameter("expiredDate", expiredDate)
				.setParameter("unUse", unUse)
				.getList(x -> toDomain(x));
	}
}
