/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.infra.repository.workplace.affiliate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.SneakyThrows;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.infra.entity.workplace.affiliate.BsymtAffiWorkplaceHistItem;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

@Stateless
public class JpaAffWorkplaceHistoryItemRepository extends JpaRepository implements AffWorkplaceHistoryItemRepository{
	
	private static final String SELECT_BY_HISTID = "SELECT aw FROM BsymtAffiWorkplaceHistItem aw"
			+ " WHERE aw.hisId = :historyId";
	
	private static final String SELECT_BY_LIST_EMPID_BASEDATE = "SELECT awit FROM BsymtAffiWorkplaceHistItem awit"
			+ " INNER JOIN BsymtAffiWorkplaceHist aw on aw.hisId = awit.hisId"
			+ " WHERE aw.sid IN :employeeIds AND aw.strDate <= :standDate AND :standDate <= aw.endDate";
	
	private static final String SELECT_BY_EMPID_BASEDATE = "SELECT awit FROM BsymtAffiWorkplaceHistItem awit"
			+ " INNER JOIN BsymtAffiWorkplaceHist aw on aw.hisId = awit.hisId"
			+ " WHERE aw.sid = :employeeId AND aw.strDate <= :standDate AND :standDate <= aw.endDate";
	
	private static final String SELECT_BY_LIST_WKPID_BASEDATE = "SELECT awit FROM BsymtAffiWorkplaceHistItem awit"
			+ " INNER JOIN BsymtAffiWorkplaceHist aw on aw.hisId = awit.hisId"
			+ " WHERE awit.workPlaceId IN :workplaceIds AND aw.strDate <= :standDate AND :standDate <= aw.endDate";
	
	/** The Constant SELECT_BY_HISTIDS. */
	private static final String SELECT_BY_HISTIDS = "SELECT aw FROM BsymtAffiWorkplaceHistItem aw"
			+ " WHERE aw.hisId IN :historyId";
	
	private static final String SELECT_BY_WPLIDS = "SELECT aw FROM BsymtAffiWorkplaceHistItem aw"
			+ " WHERE aw.workPlaceId IN :wplIds";
	
	private static final String SELECT_BY_LIST_WKPID_DATEPERIOD = "SELECT awit FROM BsymtAffiWorkplaceHistItem awit"
			+ " INNER JOIN BsymtAffiWorkplaceHist aw on aw.hisId = awit.hisId"
			+ " WHERE awit.workPlaceId IN :workplaceIds AND aw.strDate <= :endDate AND :startDate <= aw.endDate";
	
	private static final String GET_LIST_SID_BY_LIST_WKPID_DATEPERIOD = "SELECT DISTINCT awit.sid FROM BsymtAffiWorkplaceHistItem awit"
			+ " INNER JOIN BsymtAffiWorkplaceHist aw on aw.hisId = awit.hisId"
			+ " WHERE awit.workPlaceId IN :workplaceIds AND aw.strDate <= :endDate AND :startDate <= aw.endDate";
	
	/**
	 * Convert from entity to domain
	 * 
	 * @param entity
	 * @return
	 */
	private AffWorkplaceHistoryItem toDomain(BsymtAffiWorkplaceHistItem entity){
		return AffWorkplaceHistoryItem.createFromJavaType(entity.getHisId(), entity.getSid(), entity.getWorkPlaceId(), 
				entity.getNormalWkpId());
	}
	
	/**
	 * Convert from domain to entity
	 * 
	 * @param domain
	 * @return
	 */
	private BsymtAffiWorkplaceHistItem toEntity(AffWorkplaceHistoryItem domain) {
		return new BsymtAffiWorkplaceHistItem(domain.getHistoryId(),domain.getEmployeeId(),domain.getWorkplaceId(),domain.getNormalWorkplaceId());
	}
	
	private void updateEntity(AffWorkplaceHistoryItem domain, BsymtAffiWorkplaceHistItem entity) {
		entity.setWorkPlaceId(domain.getWorkplaceId());
		entity.setNormalWkpId(domain.getNormalWorkplaceId());
	}
	@Override
	public void add(AffWorkplaceHistoryItem domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void delete(String histID) {
		Optional<BsymtAffiWorkplaceHistItem> existItem = this.queryProxy().find(histID, BsymtAffiWorkplaceHistItem.class);
		if (!existItem.isPresent()){
			throw new RuntimeException("invalid BsymtAffiWorkplaceHistItem");
		}
		this.commandProxy().remove(BsymtAffiWorkplaceHistItem.class, histID);
	}

	@Override
	public void update(AffWorkplaceHistoryItem domain) {
		Optional<BsymtAffiWorkplaceHistItem> existItem = this.queryProxy().find(domain.getHistoryId(), BsymtAffiWorkplaceHistItem.class);
		if (!existItem.isPresent()){
			throw new RuntimeException("invalid BsymtAffiWorkplaceHistItem");
		}
		updateEntity(domain, existItem.get());
		this.commandProxy().update(existItem.get());
	}

	@Override
	public Optional<AffWorkplaceHistoryItem> getByHistId(String historyId) {
		return this.queryProxy().query(SELECT_BY_HISTID, BsymtAffiWorkplaceHistItem.class)
				.setParameter("historyId", historyId).getSingle(x -> toDomain(x));
	}

	@Override
	public List<AffWorkplaceHistoryItem> getAffWrkplaHistItemByListEmpIdAndDate(GeneralDate basedate,
			List<String> employeeId) {
		List<BsymtAffiWorkplaceHistItem> listHistItem = new ArrayList<>();
		CollectionUtil.split(employeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listHistItem.addAll(this.queryProxy().query(SELECT_BY_LIST_EMPID_BASEDATE, BsymtAffiWorkplaceHistItem.class)
				.setParameter("employeeIds", subList).setParameter("standDate", basedate)
				.getList());
		});
		if(listHistItem.isEmpty()){
			return Collections.emptyList();
		}
		return listHistItem.stream().map(e -> {
			AffWorkplaceHistoryItem domain = this.toDomain(e);
			return domain;
		}).collect(Collectors.toList());
	}
	
	@Override
	public List<AffWorkplaceHistoryItem> getAffWrkplaHistItemByListEmpIdAndDateV2(GeneralDate basedate,
			List<String> employeeId) {
		List<AffWorkplaceHistoryItem> data = new ArrayList<>();
		CollectionUtil.split(employeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			try (PreparedStatement statement = this.connection().prepareStatement(
						"SELECT h.HIST_ID, h.SID, h.WORKPLACE_ID, h.NORMAL_WORKPLACE_ID from BSYMT_AFF_WPL_HIST_ITEM h"
						+ " INNER JOIN BSYMT_AFF_WORKPLACE_HIST wh ON wh.HIST_ID = h.HIST_ID"
						+ " WHERE wh.START_DATE <= ? and wh.END_DATE >= ? AND h.SID IN (" + subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")")) {
				statement.setDate(1, Date.valueOf(basedate.localDate()));
				statement.setDate(2, Date.valueOf(basedate.localDate()));
				for (int i = 0; i < subList.size(); i++) {
					statement.setString(i + 3, subList.get(i));
				}
				data.addAll(new NtsResultSet(statement.executeQuery()).getList(rec -> {
					return new AffWorkplaceHistoryItem(rec.getString("HIST_ID"), rec.getString("SID"), rec.getString("WORKPLACE_ID"), rec.getString("NORMAL_WORKPLACE_ID"));
				}));
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.dom.workplace.affiliate.
	 * AffWorkplaceHistoryItemRepository#getAffWrkplaHistItemByEmpIdAndDate(
	 * nts.arc.time.GeneralDate, java.lang.String)
	 */
	@Override
	public List<AffWorkplaceHistoryItem> getAffWrkplaHistItemByEmpIdAndDate(GeneralDate basedate,
			String employeeId) {
		List<BsymtAffiWorkplaceHistItem> listHistItem = this.queryProxy()
				.query(SELECT_BY_EMPID_BASEDATE, BsymtAffiWorkplaceHistItem.class)
				.setParameter("employeeId", employeeId).setParameter("standDate", basedate)
				.getList();

		// Check exist items
		if (listHistItem.isEmpty()) {
			return Collections.emptyList();
		}

		// Return
		return listHistItem.stream().map(e -> {
			AffWorkplaceHistoryItem domain = this.toDomain(e);
			return domain;
		}).collect(Collectors.toList());
	}

	@Override
	public List<AffWorkplaceHistoryItem> getAffWrkplaHistItemByListWkpIdAndDate(GeneralDate basedate,
			List<String> workplaceId) {
		List<BsymtAffiWorkplaceHistItem> listHistItem = new ArrayList<>();
		CollectionUtil.split(workplaceId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listHistItem.addAll(this.queryProxy().query(SELECT_BY_LIST_WKPID_BASEDATE, BsymtAffiWorkplaceHistItem.class)
					.setParameter("workplaceIds", subList).setParameter("standDate", basedate)
					.getList());
		});
		if(listHistItem.isEmpty()){
			return Collections.emptyList();
		}
		return listHistItem.stream().map(e -> {
			AffWorkplaceHistoryItem domain = this.toDomain(e);
			return domain;
		}).collect(Collectors.toList());
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository
	 * #findByHistIds(java.util.List)
	 */
	@Override
	public List<AffWorkplaceHistoryItem> findByHistIds(List<String> hisIds) {
		if (CollectionUtil.isEmpty(hisIds)) {
			return new ArrayList<>();
		}
		List<BsymtAffiWorkplaceHistItem> listHistItem = new ArrayList<>();
		CollectionUtil.split(hisIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listHistItem.addAll(this.queryProxy().query(SELECT_BY_HISTIDS, BsymtAffiWorkplaceHistItem.class)
					.setParameter("historyId", subList).getList());
		});
		return listHistItem.stream().map(item -> toDomain(item))
				.collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository
	 * #findeByWplIDs(java.util.List)
	 */
	@Override
	public List<AffWorkplaceHistoryItem> findeByWplIDs(List<String> wplIDs) {
		if (CollectionUtil.isEmpty(wplIDs)) {
			return new ArrayList<>();
		}
		List<BsymtAffiWorkplaceHistItem> listHistItem = new ArrayList<>();
		CollectionUtil.split(wplIDs, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listHistItem.addAll(this.queryProxy().query(SELECT_BY_WPLIDS, BsymtAffiWorkplaceHistItem.class)
					.setParameter("wplIds", subList).getList());
		});
		return listHistItem.stream().map(item -> toDomain(item))
				.collect(Collectors.toList());
	}

	@Override
	public List<AffWorkplaceHistoryItem> getAffWkpHistItemByListWkpIdAndDatePeriod(DatePeriod dateperiod,
			List<String> workplaceId) {
		List<BsymtAffiWorkplaceHistItem> listHistItem = new ArrayList<>();
		CollectionUtil.split(workplaceId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listHistItem.addAll(this.queryProxy().query(SELECT_BY_LIST_WKPID_DATEPERIOD, BsymtAffiWorkplaceHistItem.class)
					.setParameter("workplaceIds", subList)
					.setParameter("startDate", dateperiod.start())
					.setParameter("endDate", dateperiod.end())
					.getList());
		});
		if(listHistItem.isEmpty()){
			return Collections.emptyList();
		}
		return listHistItem.stream().map(e -> {
			AffWorkplaceHistoryItem domain = this.toDomain(e);
			return domain;
		}).collect(Collectors.toList());
	}

	@Override
	public List<String> getSidByListWkpIdAndDatePeriod(DatePeriod dateperiod, List<String> workplaceId) {
		
		List<String> lstSid = new ArrayList<>();
		CollectionUtil.split(workplaceId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			lstSid.addAll(this.queryProxy().query(GET_LIST_SID_BY_LIST_WKPID_DATEPERIOD, String.class)
					.setParameter("workplaceIds", subList)
					.setParameter("startDate", dateperiod.start())
					.setParameter("endDate", dateperiod.end())
					.getList());
		});
		if(lstSid.isEmpty()){
			return Collections.emptyList();
		}
		return lstSid;
	}

	@Override
	public void addAll(List<AffWorkplaceHistoryItem> domain) {
		String INS_SQL = "INSERT INTO BSYMT_AFF_WPL_HIST_ITEM (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " HIST_ID, SID, WORKPLACE_ID,"
				+ " NORMAL_WORKPLACE_ID)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " HIST_ID_VAL, SID_VAL, WORKPLACE_ID_VAL, NORMAL_ID_VAL); ";
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();
		
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
		domain.stream().forEach(c ->{
			String sql = INS_SQL;
			sql = sql.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("INS_CCD_VAL", "'" + insCcd + "'");
			sql = sql.replace("INS_SCD_VAL", "'" + insScd + "'");
			sql = sql.replace("INS_PG_VAL", "'" + insPg + "'");

			sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");
			
			sql = sql.replace("HIST_ID_VAL", "'" + c.getHistoryId() + "'");
			sql = sql.replace("SID_VAL", "'" + c.getEmployeeId() + "'");
			sql = sql.replace("WORKPLACE_ID_VAL", c.getWorkplaceId() == null? "null": "'" + c.getWorkplaceId() + "'");
			sql = sql.replace("NORMAL_ID_VAL", c.getNormalWorkplaceId() == null? "null" : "'" +  c.getNormalWorkplaceId() + "'");
			
			sb.append(sql);
		});
		
		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

	@Override
	public void updateAll(List<AffWorkplaceHistoryItem> domain) {
		
		String UP_SQL = "UPDATE BSYMT_AFF_WPL_HIST_ITEM SET UPD_DATE = UPD_DATE_VAL, UPD_CCD = UPD_CCD_VAL, UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL,"
				+ " WORKPLACE_ID = WORKPLACE_ID_VAL, NORMAL_WORKPLACE_ID = NORMAL_ID_VAL"
				+ " WHERE HIST_ID = HIST_ID_VAL AND SID = SID_VAL;";
		String updCcd = AppContexts.user().companyCode();
		String updScd = AppContexts.user().employeeCode();
		String updPg = AppContexts.programId();
		
		StringBuilder sb = new StringBuilder();
		domain.stream().forEach(c ->{
			String sql = UP_SQL;
			sql = UP_SQL.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() +"'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd +"'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd +"'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg +"'");
			
			sql = sql.replace("WORKPLACE_ID_VAL", c.getWorkplaceId() == null? "null" : "'" + c.getWorkplaceId() + "'");
			sql = sql.replace("NORMAL_ID_VAL", c.getNormalWorkplaceId() == null? "null" : "'" +  c.getNormalWorkplaceId() + "'");
			
			sql = sql.replace("HIST_ID_VAL", "'" + c.getHistoryId() +"'");
			sql = sql.replace("SID_VAL", "'" + c.getEmployeeId() +"'");
			sb.append(sql);
		});
		int  records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
	}
	@SneakyThrows
	@Override
	public List<String> getHistIdLstBySidAndPeriod(String sid, DatePeriod period) {
		
		List<String> lstWorkplace = new ArrayList<>();
			try (PreparedStatement statement = this.connection().prepareStatement(
					"SELECT h.WORKPLACE_ID from BSYMT_AFF_WPL_HIST_ITEM h"
					+ " INNER JOIN BSYMT_AFF_WORKPLACE_HIST wh ON wh.HIST_ID = h.HIST_ID"
					+ " WHERE wh.START_DATE <= ? and wh.END_DATE >= ? AND h.SID = ?")) {
			statement.setDate(1, Date.valueOf(period.end().localDate()));
			statement.setDate(2, Date.valueOf(period.start().localDate()));
			statement.setString(3, sid);
			lstWorkplace.addAll(new NtsResultSet(statement.executeQuery()).getList(rec -> {
				return rec.getString("WORKPLACE_ID");
			}));
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
			
		if(lstWorkplace.isEmpty()){
			return Collections.emptyList();
		}
		return lstWorkplace;
	}

	@Override
	public List<String> getHistIdLstByWorkplaceIdsAndPeriod(List<String> workplaceIds, DatePeriod period) {

		List<String> sids = new ArrayList<>();
		CollectionUtil.split(workplaceIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			try (PreparedStatement statement = this.connection()
					.prepareStatement("SELECT DISTINCT h.SID from BSYMT_AFF_WPL_HIST_ITEM h"
							+ " INNER JOIN BSYMT_AFF_WORKPLACE_HIST wh ON wh.HIST_ID = h.HIST_ID"
							+ " WHERE wh.START_DATE <= ? and wh.END_DATE >= ? AND  h.WORKPLACE_ID IN (" + subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")")) {
				statement.setDate(1, Date.valueOf(period.end().localDate()));
				statement.setDate(2, Date.valueOf(period.start().localDate()));
				for (int i = 0; i < subList.size(); i++) {
					statement.setString(i + 3, subList.get(i));
				}
				sids.addAll(new NtsResultSet(statement.executeQuery()).getList(rec -> {
					return rec.getString("SID");
				}));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		});


		if (sids.isEmpty()) {
			return Collections.emptyList();
		}
		return sids;
	}

}
