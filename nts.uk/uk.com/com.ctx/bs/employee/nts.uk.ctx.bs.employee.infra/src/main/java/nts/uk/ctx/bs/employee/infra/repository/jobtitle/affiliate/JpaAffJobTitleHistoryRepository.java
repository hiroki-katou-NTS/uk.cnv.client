package nts.uk.ctx.bs.employee.infra.repository.jobtitle.affiliate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.SneakyThrows;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistory;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryRepository;
import nts.uk.ctx.bs.employee.infra.entity.jobtitle.affiliate.BsymtAffJobTitleHist;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.arc.time.calendar.period.DatePeriod;

@Stateless
public class JpaAffJobTitleHistoryRepository extends JpaRepository implements AffJobTitleHistoryRepository {

	private static final String QUERY_GET_AFFJOBTITLEHIST_BYSID = "SELECT jb FROM BsymtAffJobTitleHist jb"
			+ " WHERE jb.sid = :sid and jb.cid = :cid ORDER BY jb.strDate";
	
	private static final String QUERY_GET_AFFJOBTITLEHIST_BYSID_DESC = QUERY_GET_AFFJOBTITLEHIST_BYSID + " DESC";
	
	private static final String GET_BY_SID_DATE = "select h from BsymtAffJobTitleHist h"
			+ " where h.sid = :sid and h.strDate <= :standardDate and h.endDate >= :standardDate";
	
	private static final String GET_BY_LISTSID_DATE = "SELECT h FROM BsymtAffJobTitleHist h"
			+ " where h.sid IN :lstSid AND h.strDate <= :standardDate and h.endDate >= :standardDate";
	
	private static final String GET_BY_LISTSIDS_JOBIDS_DATE = "SELECT h FROM BsymtAffJobTitleHist h LEFT JOIN BsymtAffJobTitleHistItem i ON h.hisId = i.hisId "
			+ " where h.sid IN :lstSid AND i.jobTitleId IN :lstJobTitleId AND h.strDate <= :standardDate and h.endDate >= :standardDate";
	
	private static final String GET_BY_HID_SID = "select h from BsymtAffJobTitleHist h"
			+ " where h.sid = :sid and h.hisId = :hisId";
	
	private static final String GET_BY_LIST_HID_SID = "select h from BsymtAffJobTitleHist h"
			+ " where h.sid IN :sids and h.hisId IN :hisIds";
	
	private static final String GET_BY_EMPIDS_PERIOD = "select h from BsymtAffJobTitleHist h"
			+ " where h.sid IN :lstSid and h.strDate <= :endDate and h.endDate >= :startDate"
			+ " ORDER BY h.sid, h.strDate";
	
	private static final String GET_BY_DATE = "select h from BsymtAffJobTitleHist h"
			+ " where h.strDate <= :date and h.endDate >= :date "
			+ " AND h.cid = :cid ";
	
	private static final String GET_SIDS_BY_SIDS_DATE = "SELECT m FROM BsymtAffJobTitleHist m"
			+ " WHERE m.sid IN :sids "
			+ "	AND m.strDate = :baseDate";
	
	private static final String GET_SIDS_BY_PERIOD = "SELECT m FROM BsymtAffJobTitleHist m"
			+ " WHERE m.cid = :cid "
			+ " AND m.strDate >= :startDate "
			+ "	AND m.strDate <= :endDate";
	
	/**
	 * Convert from domain to entity
	 * @param employeeId
	 * @param listHist
	 * @return
	 */
	private AffJobTitleHistory toAffJobTitleHist(List<BsymtAffJobTitleHist> listHist) {
		AffJobTitleHistory domain = new AffJobTitleHistory(listHist.get(0).getCid(), listHist.get(0).getSid(), new ArrayList<>());
		DateHistoryItem dateItem = null;
		for (BsymtAffJobTitleHist item : listHist) {
			dateItem = new DateHistoryItem(item.getHisId(), new DatePeriod(item.getStrDate(), item.getEndDate()));
			domain.getHistoryItems().add(dateItem);
		}
		return domain;
	}

	/**
	 * Convert from domain to BsymtAffJobTitleHist entity
	 * @param sId
	 * @param domain
	 * @return
	 */
	private BsymtAffJobTitleHist toEntity(String cid, String sId, DateHistoryItem domain) {
		return new BsymtAffJobTitleHist(domain.identifier(), sId, cid, domain.start(), domain.end());
	}

	@Override
	public Optional<AffJobTitleHistory> getListBySid(String cid, String sid) {
		List<BsymtAffJobTitleHist> listHist = this.queryProxy()
				.query(QUERY_GET_AFFJOBTITLEHIST_BYSID, BsymtAffJobTitleHist.class)
				.setParameter("cid", cid).setParameter("sid", sid).getList();
		if (listHist != null && !listHist.isEmpty()) {
			return Optional.of(toAffJobTitleHist(listHist));
		}
		return Optional.empty();
	}
	
	@Override
	public Optional<AffJobTitleHistory> getListBySidDesc(String cid, String sid) {
		List<BsymtAffJobTitleHist> listHist = this.queryProxy()
				.query(QUERY_GET_AFFJOBTITLEHIST_BYSID_DESC, BsymtAffJobTitleHist.class)
				.setParameter("cid", cid).setParameter("sid", sid).getList();
		if (listHist != null && !listHist.isEmpty()) {
			return Optional.of(toAffJobTitleHist(listHist));
		}
		return Optional.empty();
	}

	@Override
	public void add(String cid, String sid, DateHistoryItem item) {
		this.commandProxy().insert(toEntity(cid, sid, item));
	}

	@Override
	public void update(DateHistoryItem item) {
		Optional<BsymtAffJobTitleHist> itemToBeUpdated = this.queryProxy().find(item.identifier(),
				BsymtAffJobTitleHist.class);

		if (!itemToBeUpdated.isPresent()) {
			throw new RuntimeException("Invalid BsymtAffJobTitleHist");
		}
		// Update entity
		updateEntity(item, itemToBeUpdated.get());
		this.commandProxy().update(itemToBeUpdated.get());
	}

	@Override
	public void delete(String histId) {
		Optional<BsymtAffJobTitleHist> itemToBeDeleted = this.queryProxy().find(histId, BsymtAffJobTitleHist.class);

		if (!itemToBeDeleted.isPresent()) {
			throw new RuntimeException("Invalid BsymtAffJobTitleHist");
		}
		this.commandProxy().remove(BsymtAffJobTitleHist.class, histId);
	}

	/**
	 * Update entity from domain
	 * @param employeeID
	 * @param item
	 * @return
	 */
	private void updateEntity(DateHistoryItem item, BsymtAffJobTitleHist entity) {
		entity.setStrDate(item.start());
		entity.setEndDate(item.end());
	}

	@Override
	public Optional<AffJobTitleHistory> getByHistoryId(String historyId) {
		Optional<BsymtAffJobTitleHist> optionData = this.queryProxy().find(historyId, BsymtAffJobTitleHist.class);
		if (optionData.isPresent()) {
			return Optional.of(toDomain(optionData.get()));
		}
		return Optional.empty();
	}

	private AffJobTitleHistory toDomain(BsymtAffJobTitleHist ent) {
		AffJobTitleHistory domain = new AffJobTitleHistory(ent.getCid(), ent.getSid(), new ArrayList<>());
		DateHistoryItem dateItem = new DateHistoryItem(ent.getHisId(),
				new DatePeriod(ent.getStrDate(), ent.getEndDate()));

		domain.getHistoryItems().add(dateItem);

		return domain;
	}

	@Override
	public Optional<AffJobTitleHistory> getByEmpIdAndStandardDate(String employeeId, GeneralDate standardDate) {
		if (standardDate == null) {
			return Optional.empty();
		}
		Optional<BsymtAffJobTitleHist> optionaData = this.queryProxy()
				.query(GET_BY_SID_DATE, BsymtAffJobTitleHist.class)
				.setParameter("sid", employeeId).setParameter("standardDate", standardDate).getSingle();
		if ( optionaData.isPresent()) {
			return Optional.of(toDomain(optionaData.get()));
		}
		return Optional.empty();
	}

	@Override
	public List<AffJobTitleHistory> getAllBySid(String sid) {
		String cid = AppContexts.user().companyId();
		List<AffJobTitleHistory> lstAffJobTitleHistory = new ArrayList<>();
		List<BsymtAffJobTitleHist> listHist = this.queryProxy()
				.query(QUERY_GET_AFFJOBTITLEHIST_BYSID, BsymtAffJobTitleHist.class)
				.setParameter("cid", cid).setParameter("sid", sid).getList();
		if (listHist != null && !listHist.isEmpty()) {
			for (BsymtAffJobTitleHist item : listHist) {
				AffJobTitleHistory domain = new AffJobTitleHistory(item.getCid(), item.getSid(), new ArrayList<>());
				DateHistoryItem dateItem = null;
				dateItem = new DateHistoryItem(item.getHisId(), new DatePeriod(item.getStrDate(), item.getEndDate()));
				domain.getHistoryItems().add(dateItem);
				lstAffJobTitleHistory.add(domain);
			}
		}
		if (lstAffJobTitleHistory != null && !lstAffJobTitleHistory.isEmpty()) {
			return lstAffJobTitleHistory;
		}
		return null;
	}

	// TODO: HoangDD check lại, không nhất thiết phải truyền cả SID
	@Override
	public Optional<AffJobTitleHistory> getListByHidSid(String hid, String sid) {
		Optional<BsymtAffJobTitleHist> optHist = this.queryProxy()
				.query(GET_BY_HID_SID, BsymtAffJobTitleHist.class)
				.setParameter("hisId", hid).setParameter("sid", sid).getSingle();
		if (optHist.isPresent()) {
			BsymtAffJobTitleHist affJobTitleHist = optHist.get();
			List<BsymtAffJobTitleHist> listHist = new ArrayList<>();
			listHist.add(affJobTitleHist);
			return Optional.of(toAffJobTitleHist(listHist)); 
		}
		return Optional.empty();
	}
	
	@Override
	public List<AffJobTitleHistory> getListByListHidSid(List<String> hids, List<String> sids) {
		if(hids.isEmpty() || sids.isEmpty())
			return Collections.emptyList();
		
		List<BsymtAffJobTitleHist> optHist = new ArrayList<>();
		CollectionUtil.split(hids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstH -> {
			CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, listS -> {
				optHist.addAll(this.queryProxy().query(GET_BY_LIST_HID_SID, BsymtAffJobTitleHist.class)
						.setParameter("hisIds", lstH)
						.setParameter("sids", listS)
						.getList());
			});
		});
		List<AffJobTitleHistory> listAffJobTitleHistory = new ArrayList<>();
		for(String sid :sids ) {
			List<BsymtAffJobTitleHist> listBsymtAffJobTitleHist = new ArrayList<>();
			for(BsymtAffJobTitleHist bsymtAffJobTitleHist :optHist ) {
				if(sid.equals(bsymtAffJobTitleHist.getSid())) {
					listBsymtAffJobTitleHist.add(bsymtAffJobTitleHist);
				}
			}
			if(!listBsymtAffJobTitleHist.isEmpty()) {
				listAffJobTitleHistory.add(toAffJobTitleHist(listBsymtAffJobTitleHist));
			}
		}
		return listAffJobTitleHistory;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.
	 * AffJobTitleHistoryRepository#searchJobTitleHistory(nts.arc.time.
	 * GeneralDate, java.util.List)
	 */
	@Override
	public List<AffJobTitleHistory> searchJobTitleHistory(GeneralDate baseDate, List<String> employeeIds, List<String> jobTitleIds) {
		// Check conditions
		if (CollectionUtil.isEmpty(employeeIds) || CollectionUtil.isEmpty(jobTitleIds)) {
			return Collections.emptyList();
		}
		
		// Split employee id list.
		List<BsymtAffJobTitleHist> resultList = new ArrayList<>();
		
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			CollectionUtil.split(jobTitleIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, jobSubList -> {
				resultList.addAll(this.queryProxy()
						.query(GET_BY_LISTSIDS_JOBIDS_DATE, BsymtAffJobTitleHist.class)
						.setParameter("lstSid", subList).setParameter("lstJobTitleId", jobSubList)
						.setParameter("standardDate", baseDate).getList());
			});
		});
			
		// Return
		return resultList.stream().map(entity -> this.toDomain(entity)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.
	 * AffJobTitleHistoryRepository#findAllJobTitleHistory(nts.arc.time.
	 * GeneralDate, java.util.List)
	 */
	@Override
	public List<AffJobTitleHistory> findAllJobTitleHistory(GeneralDate baseDate, List<String> employeeIds) {
		if (CollectionUtil.isEmpty(employeeIds)) {
			return new ArrayList<>();
		}
		
		// Split employee id list.
		List<BsymtAffJobTitleHist> resultList = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(GET_BY_LISTSID_DATE, BsymtAffJobTitleHist.class)
					.setParameter("lstSid", subList).setParameter("standardDate", baseDate).getList());
		});
		return resultList.stream().map(entity -> this.toDomain(entity)).collect(Collectors.toList());
	}
	
	@Override
	public List<AffJobTitleHistory> getByEmployeeListPeriod(List<String> employeeIds, DatePeriod period) {
		
		List<BsymtAffJobTitleHist> entities = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIds -> {
			String sql = "select * from BSYMT_AFF_JOB_HIST h"
					+ " inner join BSYMT_AFF_JOB_HIST_ITEM i"
					+ " on h.HIST_ID = i.HIST_ID"
					+ " where h.SID in (" + NtsStatement.In.createParamsString(subIds) + ")"
					+ " and h.START_DATE <= ?"
					+ " and h.END_DATE >= ?";
			
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				
				int i = 0;
				for (; i < subIds.size(); i++) {
					stmt.setString(1 + i, subIds.get(i));
				}

				stmt.setDate(1 + i, Date.valueOf(period.end().localDate()));
				stmt.setDate(2 + i, Date.valueOf(period.start().localDate()));
				
				List<BsymtAffJobTitleHist> ents = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					BsymtAffJobTitleHist ent = new BsymtAffJobTitleHist();
					ent.hisId = rec.getString("HIST_ID");
					ent.cid = rec.getString("CID");
					ent.sid = rec.getString("SID");
					ent.strDate = rec.getGeneralDate("START_DATE");
					ent.endDate = rec.getGeneralDate("END_DATE");
					return ent;
				});
				entities.addAll(ents);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
//			List<BsymtAffJobTitleHist> subEntities = this.queryProxy()
//					.query(GET_BY_EMPIDS_PERIOD, BsymtAffJobTitleHist.class).setParameter("lstSid", subIds)
//					.setParameter("startDate", period.start()).setParameter("endDate", period.end()).getList();
//			entities2.addAll(subEntities);
		});
		
		entities.sort((o1, o2) -> {
			int tmp = o1.hisId.compareTo(o2.hisId);
			if (tmp != 0) return tmp;
			return o1.strDate.compareTo(o2.strDate);
		});
		
		Map<String, List<BsymtAffJobTitleHist>> entitiesByEmployee = entities.stream()
				.collect(Collectors.groupingBy(x -> x.sid));

		String companyId = AppContexts.user().companyId();

		List<AffJobTitleHistory> resultList = new ArrayList<>();

		entitiesByEmployee.forEach((employeeId, entitiesOfEmp) -> {
			List<DateHistoryItem> historyItems = convertToDateHistoryItem(entitiesOfEmp);
			resultList.add(new AffJobTitleHistory(companyId, employeeId, historyItems));
		});
		return resultList;
	}
	
	private List<DateHistoryItem> convertToDateHistoryItem(List<BsymtAffJobTitleHist> entities) {
		return entities.stream()
				.map(ent -> new DateHistoryItem(ent.hisId,
						new DatePeriod(ent.strDate, ent.endDate)))
				.collect(Collectors.toList());
	}

	@Override
	@SneakyThrows
	public Optional<SingleHistoryItem> getSingleHistoryItem(String employeeId, GeneralDate baseDate) {

		try (PreparedStatement statement = this.connection().prepareStatement(
				"select * from BSYMT_AFF_JOB_HIST h"
				+ " inner join BSYMT_AFF_JOB_HIST_ITEM i"
				+ " on h.HIST_ID = i.HIST_ID"
				+ " where h.SID = ?"
				+ " and h.START_DATE <= ?"
				+ " and h.END_DATE >= ?")) {
			
			statement.setString(1, employeeId);
			statement.setDate(2, Date.valueOf(baseDate.localDate()));
			statement.setDate(3, Date.valueOf(baseDate.localDate()));
			
			return new NtsResultSet(statement.executeQuery()).getSingle(rec -> {
				return new SingleHistoryItem(
						rec.getString("SID"),
						rec.getString("HIST_ID"),
						new DatePeriod(
								rec.getGeneralDate("START_DATE"),
								rec.getGeneralDate("END_DATE")),
						rec.getString("JOB_TITLE_ID"),
						rec.getString("NOTE")
						);
			});
		}
	}

	// request list 515
	@Override
	public Optional<AffJobTitleHistory> getListEmployee(GeneralDate baseDate, String cid) {
		List<BsymtAffJobTitleHist> listEntity =  this.queryProxy().query(GET_BY_DATE, BsymtAffJobTitleHist.class)
								.setParameter("date", baseDate)
								.setParameter("cid", cid)
								.getList();
		if (!listEntity.isEmpty()) {
			return Optional.of(toAffJobTitleHist(listEntity));
		}
		return Optional.empty();
	}

	@Override
	public List<AffJobTitleHistory> getListByListHidSid(List<String> sid, GeneralDate targetDate) {
		List<AffJobTitleHistory> data = new ArrayList<>();
		CollectionUtil.split(sid, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			try (PreparedStatement statement = this.connection().prepareStatement(
						"SELECT h.HIST_ID, h.SID, h.CID, h.END_DATE, h.START_DATE from BSYMT_AFF_JOB_HIST h"
						+ " WHERE h.START_DATE <= ? and h.END_DATE >= ? AND h.SID IN (" + subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")")) {
				statement.setDate(1, Date.valueOf(targetDate.localDate()));
				statement.setDate(2, Date.valueOf(targetDate.localDate()));
				for (int i = 0; i < subList.size(); i++) {
					statement.setString(i + 3, subList.get(i));
				}
				List<Map<String, Object>> map = new NtsResultSet(statement.executeQuery()).getList(rec -> {
					Map<String, Object> m = new HashMap<>();
					m.put("HIST_ID", rec.getString("HIST_ID"));
					m.put("SID", rec.getString("SID"));
					m.put("CID", rec.getString("CID"));
					m.put("START_DATE", rec.getGeneralDate("START_DATE"));
					m.put("END_DATE", rec.getGeneralDate("END_DATE"));
					return m;
				});
				
				map.stream().collect(Collectors.groupingBy(c -> c.get("SID"), Collectors.collectingAndThen(Collectors.toList(), list -> {
					AffJobTitleHistory his = new AffJobTitleHistory(list.get(0).get("CID").toString(), list.get(0).get("SID").toString(), list.stream().map(c -> {
						return new DateHistoryItem(c.get("HIST_ID").toString(), new DatePeriod((GeneralDate) c.get("START_DATE"), (GeneralDate) c.get("END_DATE")));
					}).collect(Collectors.toList()));
					data.add(his);
					return his;
				})));
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		return data;
	}

	@Override
	public List<AffJobTitleHistory> getListBySids(String cid, List<String> sids) {
		List<AffJobTitleHistory> data = new ArrayList<>();
		
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT h.HIST_ID, h.SID, h.CID, h.END_DATE, h.START_DATE from BSYMT_AFF_JOB_HIST h"
					+ " WHERE h.CID = ? AND h.SID IN (" + NtsStatement.In.createParamsString(subList) + ")"+ " ORDER BY h.SID, h.START_DATE DESC";;
			try (PreparedStatement statement = this.connection().prepareStatement(sql)) {
				statement.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					statement.setString( 2 + i, subList.get(i));
				}
				List<Map<String, Object>> map = new NtsResultSet(statement.executeQuery()).getList(rec -> {
					Map<String, Object> m = new HashMap<>();
					m.put("HIST_ID", rec.getString("HIST_ID"));
					m.put("SID", rec.getString("SID"));
					m.put("CID", rec.getString("CID"));
					m.put("START_DATE", rec.getGeneralDate("START_DATE"));
					m.put("END_DATE", rec.getGeneralDate("END_DATE"));
					return m;
				});
				
				map.stream().collect(Collectors.groupingBy(c -> c.get("SID"), Collectors.collectingAndThen(Collectors.toList(), list -> {
					AffJobTitleHistory his = new AffJobTitleHistory(list.get(0).get("CID").toString(), list.get(0).get("SID").toString(), list.stream().map(c -> {
						return new DateHistoryItem(c.get("HIST_ID").toString(), new DatePeriod((GeneralDate) c.get("START_DATE"), (GeneralDate) c.get("END_DATE")));
					}).collect(Collectors.toList()));
					data.add(his);
					return his;
				})));
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		return data;
	}
	
	@Override
	public List<DateHistoryItem> getListByListSidsNoWithPeriod(String cid, List<String> sids) {
		List<DateHistoryItem> result = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			try (PreparedStatement statement = this.connection().prepareStatement(
						"SELECT h.HIST_ID, h.SID, h.CID, h.END_DATE, h.START_DATE from BSYMT_AFF_JOB_HIST h"
						+ " WHERE h.CID = ? AND h.SID IN (" + subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")" + " ORDER BY START_DATE ASC" )) {
				statement.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					statement.setString( 2 + i, subList.get(i));
				}
				List<DateHistoryItem> lstObj = new NtsResultSet(statement.executeQuery()).getList(rec -> {
					return new DateHistoryItem(rec.getString("HIST_ID"),
							new DatePeriod(rec.getGeneralDate("START_DATE"), rec.getGeneralDate("END_DATE")));
				}).stream().collect(Collectors.toList());
				result.addAll(lstObj);
				
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	@Override
	public void addAll(Map<String, DateHistoryItem> items) {
		String INS_SQL = "INSERT INTO BSYMT_AFF_JOB_HIST (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " CONTRACT_CD, HIST_ID, SID, CID,"
				+ " START_DATE, END_DATE)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " CONTRACT_CD_VAL, HIST_ID_VAL, SID_VAL, CID_VAL, START_DATE_VAL, END_DATE_VAL); ";
		
		String contractCode = AppContexts.user().contractCode();
		String cid = AppContexts.user().companyId();
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();
		
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
		items.entrySet().stream().forEach(c ->{
			String sql = INS_SQL;
			DateHistoryItem dateHistItem = c.getValue();
			sql = sql.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("INS_CCD_VAL", "'" + insCcd + "'");
			sql = sql.replace("INS_SCD_VAL", "'" + insScd + "'");
			sql = sql.replace("INS_PG_VAL", "'" + insPg + "'");

			sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");
			
			sql = sql.replace("CONTRACT_CD_VAL", "'" + contractCode + "'");
			sql = sql.replace("HIST_ID_VAL", "'" + dateHistItem.identifier() + "'");
			sql = sql.replace("CID_VAL", "'" + cid + "'");
			sql = sql.replace("SID_VAL", "'" + c.getKey() + "'");
			sql = sql.replace("START_DATE_VAL", "'" + dateHistItem.start() + "'");
			sql = sql.replace("END_DATE_VAL","'" +  dateHistItem.end() + "'");
			
			sb.append(sql);
		});
		
		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
	}

	@Override
	public void updateAll(List<DateHistoryItem> items) {
		String UP_SQL = "UPDATE BSYMT_AFF_JOB_HIST SET UPD_DATE = UPD_DATE_VAL, UPD_CCD = UPD_CCD_VAL, UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL,"
				+ " START_DATE = START_DATE_VAL, END_DATE = END_DATE_VAL"
				+ " WHERE HIST_ID = HIST_ID_VAL AND CID = CID_VAL;";
		String cid = AppContexts.user().companyId();
		String updCcd = AppContexts.user().companyCode();
		String updScd = AppContexts.user().employeeCode();
		String updPg = AppContexts.programId();
		
		StringBuilder sb = new StringBuilder();
		items.stream().forEach(c ->{
			String sql = UP_SQL;
			sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() +"'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd +"'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd +"'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg +"'");
			
			sql = sql.replace("START_DATE_VAL", "'" + c.start() + "'");
			sql = sql.replace("END_DATE_VAL","'" +  c.end() + "'");
			
			sql = sql.replace("HIST_ID_VAL", "'" + c.identifier() +"'");
			sql = sql.replace("CID_VAL", "'" + cid +"'");
			sb.append(sql);
		});
		int  records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
	}

	@Override
	public List<String> getBySidsAndBaseDate(List<String> sids, GeneralDate baseDate) {
		
		if (sids.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<String> returnList = new ArrayList<>();
		
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subSids -> {
			
			List<String> subResults = this.queryProxy()
					.query(GET_SIDS_BY_SIDS_DATE, BsymtAffJobTitleHist.class)
					.setParameter("sids", subSids)
					.setParameter("baseDate", baseDate)
					.getList(item -> item.sid);
			
			returnList.addAll(subResults);
		});
		
		return returnList;
	}

	@Override
	public List<String> getByDatePeriod(String cid, DatePeriod datePeriod) {
		return this.queryProxy()
				.query(GET_SIDS_BY_PERIOD, BsymtAffJobTitleHist.class)
				.setParameter("cid", cid)
				.setParameter("startDate", datePeriod.start())
				.setParameter("endDate", datePeriod.end())
				.getList(item -> item.sid);
	}
}
