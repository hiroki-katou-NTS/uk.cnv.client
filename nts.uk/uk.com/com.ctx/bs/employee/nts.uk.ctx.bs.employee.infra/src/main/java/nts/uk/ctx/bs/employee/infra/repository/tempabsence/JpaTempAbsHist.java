package nts.uk.ctx.bs.employee.infra.repository.tempabsence;

import java.math.BigDecimal;
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
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsHistRepository;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsenceHisItem;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsenceHistory;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TimeoffLeaveRecordWithPeriod;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.TempAbsenceFrameNo;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.state.GenericString;
import nts.uk.ctx.bs.employee.infra.entity.temporaryabsence.BsymtTempAbsHisItem;
import nts.uk.ctx.bs.employee.infra.entity.temporaryabsence.BsymtTempAbsHistory;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.arc.time.calendar.period.DatePeriod;

@Stateless
public class JpaTempAbsHist extends JpaRepository implements TempAbsHistRepository {

	private static final String QUERY_GET_TEMPORARYABSENCE_BYSID = "SELECT ta FROM BsymtTempAbsHistory ta"
			+ " WHERE ta.sid = :sid and ta.cid = :cid ORDER BY ta.startDate";

	private static final String QUERY_GET_TEMPORARYABSENCE_BYSID_AND_CLS = String.join(" ",
			"SELECT ta FROM BsymtTempAbsHistory ta INNER JOIN BsymtTempAbsHisItem item",
			"ON  ta.histId = item.histId AND ta.sid = item.sid",
			"WHERE ta.sid = :sid AND ta.cid = :cid  AND item.tempAbsFrameNo = 1", "ORDER BY ta.startDate");

	private static final String QUERY_GET_TEMPORARYABSENCE_BYSID_DESC = QUERY_GET_TEMPORARYABSENCE_BYSID + " DESC";

	private static final String GET_BY_SID_DATE = "select h from BsymtTempAbsHistory h"
			+ " where h.sid = :sid and h.startDate <= :standardDate and h.endDate >= :standardDate";
	private static final String SELECT_BY_LIST_SID_DATEPERIOD = "SELECT th FROM BsymtTempAbsHistory th"
			+ " WHERE th.sid IN :employeeIds AND th.startDate <= :endDate AND :startDate <= th.endDate"
			+ " ORDER BY th.sid, th.startDate";

	private static final String GET_LST_SID_BY_LSTSID_DATEPERIOD = "SELECT tah.sid FROM BsymtTempAbsHistory tah"
			+ " WHERE tah.sid IN :employeeIds AND tah.startDate <= :endDate AND :startDate <= tah.endDate ";

	private static final String SELECT_BY_LIST_SID = "SELECT th.sid FROM BsymtTempAbsHistory th"
			+ " WHERE th.sid IN :employeeIds ORDER BY th.sid ";
	

	/**
	 * Convert from domain to entity
	 * 
	 * @param employeeID
	 * @param item
	 * @return
	 */
	private BsymtTempAbsHistory toEntity(String companyId, String employeeID, DateHistoryItem item) {
		return new BsymtTempAbsHistory(item.identifier(), companyId, employeeID, item.start(), item.end());
	}

	/**
	 * Update entity from domain
	 * 
	 * @param employeeID
	 * @param item
	 * @return
	 */
	private void updateEntity(DateHistoryItem item, BsymtTempAbsHistory entity) {
		entity.startDate = item.start();
		entity.endDate = item.end();
	}

	@Override
	public void add(String cid, String sid, DateHistoryItem item) {
		this.commandProxy().insert(toEntity(cid, sid, item));
	}

	@Override
	public void update(DateHistoryItem item) {

		Optional<BsymtTempAbsHistory> histItem = this.queryProxy().find(item.identifier(), BsymtTempAbsHistory.class);
		if (!histItem.isPresent()) {
			throw new RuntimeException("invalid BsymtAffiWorkplaceHist");
		}
		updateEntity(item, histItem.get());
		this.commandProxy().update(histItem.get());
	}

	@Override
	public void delete(String histId) {
		Optional<BsymtTempAbsHistory> histItem = this.queryProxy().find(histId, BsymtTempAbsHistory.class);
		if (!histItem.isPresent()) {
			throw new RuntimeException("invalid BsymtTempAbsHistory");
		}
		this.commandProxy().remove(BsymtTempAbsHistory.class, histId);
	}

	@Override
	public Optional<DateHistoryItem> getByHistId(String histId) {

		Optional<BsymtTempAbsHistory> existItem = this.queryProxy().find(histId, BsymtTempAbsHistory.class);
		if (existItem.isPresent()) {
			BsymtTempAbsHistory entity = existItem.get();
			return Optional.of(new DateHistoryItem(entity.histId, new DatePeriod(entity.startDate, entity.endDate)));
		}
		return Optional.empty();

	}

	@Override
	public Optional<DateHistoryItem> getItemByEmpIdAndStandardDate(String employeeId, GeneralDate standardDate) {
		Optional<BsymtTempAbsHistory> optionData = this.queryProxy().query(GET_BY_SID_DATE, BsymtTempAbsHistory.class)
				.setParameter("sid", employeeId).setParameter("standardDate", standardDate).getSingle();
		if (optionData.isPresent()) {
			BsymtTempAbsHistory entity = optionData.get();
			return Optional.of(new DateHistoryItem(entity.histId, new DatePeriod(entity.startDate, entity.endDate)));
		}
		return Optional.empty();
	}

	/**
	 * Convert to domain TempAbsenceHistory
	 * 
	 * @param employeeId
	 * @param listHist
	 * @return
	 */
	private TempAbsenceHistory toDomainTemp(List<BsymtTempAbsHistory> listHist) {
		TempAbsenceHistory domain = new TempAbsenceHistory(listHist.get(0).cid, listHist.get(0).sid,
				new ArrayList<DateHistoryItem>());
		for (BsymtTempAbsHistory item : listHist) {
			DateHistoryItem dateItem = new DateHistoryItem(item.histId, new DatePeriod(item.startDate, item.endDate));
			domain.getDateHistoryItems().add(dateItem);
		}
		return domain;
	}

	@Override
	public Optional<TempAbsenceHistory> getByEmployeeId(String cid, String employeeId) {
		List<BsymtTempAbsHistory> listHist = this.queryProxy()
				.query(QUERY_GET_TEMPORARYABSENCE_BYSID, BsymtTempAbsHistory.class).setParameter("sid", employeeId)
				.setParameter("cid", cid).getList();
		if (listHist != null && !listHist.isEmpty()) {
			return Optional.of(toDomainTemp(listHist));
		}
		return Optional.empty();
	}

	@Override
	public Optional<TempAbsenceHistory> getByEmployeeIdDesc(String cid, String employeeId) {
		List<BsymtTempAbsHistory> listHist = this.queryProxy()
				.query(QUERY_GET_TEMPORARYABSENCE_BYSID_DESC, BsymtTempAbsHistory.class).setParameter("sid", employeeId)
				.setParameter("cid", cid).getList();
		if (listHist != null && !listHist.isEmpty()) {
			return Optional.of(toDomainTemp(listHist));
		}
		return Optional.empty();
	}

	@Override
	public List<TempAbsenceHistory> getByListSid(List<String> employeeIds, DatePeriod dateperiod) {

		// ResultList
		List<BsymtTempAbsHistory> tempAbsHistoryEntities = new ArrayList<>();
		// Split employeeId List if size of employeeId List is greater than 1000
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			List<BsymtTempAbsHistory> lstBsymtAffCompanyHist = this.queryProxy()
					.query(SELECT_BY_LIST_SID_DATEPERIOD, BsymtTempAbsHistory.class)
					.setParameter("employeeIds", subList).setParameter("startDate", dateperiod.start())
					.setParameter("endDate", dateperiod.end()).getList();
			tempAbsHistoryEntities.addAll(lstBsymtAffCompanyHist);
		});
		tempAbsHistoryEntities.sort((o1, o2) -> {
			int tmp = o1.sid.compareTo(o2.sid);
			if (tmp != 0)
				return tmp;
			return o1.startDate.compareTo(o2.startDate);
		});

		Map<String, List<BsymtTempAbsHistory>> tempAbsEntityForEmp = tempAbsHistoryEntities.stream()
				.collect(Collectors.groupingBy(x -> x.sid));
		List<TempAbsenceHistory> resultList = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		tempAbsEntityForEmp.forEach((empId, listTempAbsHist) -> {
			List<DateHistoryItem> dateHistoryItems = convertToDateHistoryItems(listTempAbsHist);
			resultList.add(new TempAbsenceHistory(companyId, empId, dateHistoryItems));
		});
		return resultList;
	}

	private List<DateHistoryItem> convertToDateHistoryItems(List<BsymtTempAbsHistory> entities) {
		return entities.stream().map(ent -> new DateHistoryItem(ent.histId, new DatePeriod(ent.startDate, ent.endDate)))
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getLstSidByListSidAndDatePeriod(List<String> employeeIds, DatePeriod dateperiod) {
		List<String> listSid = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listSid.addAll(this.queryProxy().query(GET_LST_SID_BY_LSTSID_DATEPERIOD, String.class)
					.setParameter("employeeIds", subList).setParameter("startDate", dateperiod.start())
					.setParameter("endDate", dateperiod.end()).getList());
		});
		if (listSid.isEmpty()) {
			return Collections.emptyList();
		}
		return listSid;
	}

	@Override
	public List<String> getByListSid(List<String> employeeIds) {
		List<String> listSid = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listSid.addAll(this.queryProxy().query(SELECT_BY_LIST_SID, String.class)
					.setParameter("employeeIds", subList).getList());
		});
		if (listSid.isEmpty()) {
			return Collections.emptyList();
		}
		return listSid;
	}

	@Override
	public Optional<TempAbsenceHistory> getBySidAndLeave(String cid, String employeeId) {
		List<BsymtTempAbsHistory> listHist = this.queryProxy()
				.query(QUERY_GET_TEMPORARYABSENCE_BYSID_AND_CLS, BsymtTempAbsHistory.class)
				.setParameter("sid", employeeId).setParameter("cid", cid).getList();
		if (listHist != null && !listHist.isEmpty()) {
			return Optional.of(toDomainTemp(listHist));
		}
		return Optional.empty();
	}

	@Override
	@SneakyThrows
	public List<DateHistoryItem> getAllBySidAndCidAndBaseDate(String cid, List<String> sids, GeneralDate standardDate) {
		List<DateHistoryItem> tempAbsHistoryEntities = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			String sql = "SELECT * FROM BSYMT_TEMP_ABS_HISTORY" + " WHERE CID = ?" + " AND START_DATE <= ?"
					+ " AND END_DATE >= ?" + " AND SID IN (" + NtsStatement.In.createParamsString(subList) + ")"
					+ " ORDER BY SID, START_DATE DESC";;

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {

				stmt.setString(1, cid);
				stmt.setDate(2, Date.valueOf(standardDate.toLocalDate()));
				stmt.setDate(3, Date.valueOf(standardDate.toLocalDate()));
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(4 + i, subList.get(i));
				}

				List<Map<String, Object>> map = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					Map<String, Object> m = new HashMap<>();
					m.put("HIST_ID", rec.getString("HIST_ID"));
					m.put("SID", rec.getString("SID"));
					m.put("CID", rec.getString("CID"));
					m.put("START_DATE", rec.getGeneralDate("START_DATE"));
					m.put("END_DATE", rec.getGeneralDate("END_DATE"));
					return m;
				});
				map.stream().collect(Collectors.groupingBy(c -> c.get("SID"),
						Collectors.collectingAndThen(Collectors.toList(), list -> {
							TempAbsenceHistory his = new TempAbsenceHistory(list.get(0).get("CID").toString(),
									list.get(0).get("SID").toString(), list.stream().map(c -> {
										return new DateHistoryItem(c.get("HIST_ID").toString(), new DatePeriod(
												(GeneralDate) c.get("START_DATE"), (GeneralDate) c.get("END_DATE")));
										// tempAbsHistoryEntities.add(his);
									}).collect(Collectors.toList()));
							tempAbsHistoryEntities.add(his.getDateHistoryItems().get(0));
							return his;
						})));

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return tempAbsHistoryEntities;
	}

	@Override
	@SneakyThrows
	public List<TempAbsenceHistory> getBySidsAndCid(String cid, List<String> employeeIds) {
		List<TempAbsenceHistory> tempAbsenceHistory = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			String sql = "SELECT * FROM BSYMT_TEMP_ABS_HISTORY" + " WHERE CID = ?  AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")" + " ORDER BY START_DATE";

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}

				List<Map<String, Object>> map = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					Map<String, Object> m = new HashMap<>();
					m.put("HIST_ID", rec.getString("HIST_ID"));
					m.put("SID", rec.getString("SID"));
					m.put("CID", rec.getString("CID"));
					m.put("START_DATE", rec.getGeneralDate("START_DATE"));
					m.put("END_DATE", rec.getGeneralDate("END_DATE"));
					return m;
				});
				map.stream().collect(Collectors.groupingBy(c -> c.get("SID"),
						Collectors.collectingAndThen(Collectors.toList(), list -> {
							TempAbsenceHistory his = new TempAbsenceHistory(list.get(0).get("CID").toString(),
									list.get(0).get("SID").toString(), list.stream().map(c -> {
										return new DateHistoryItem(c.get("HIST_ID").toString(), new DatePeriod(
												(GeneralDate) c.get("START_DATE"), (GeneralDate) c.get("END_DATE")));
									}).collect(Collectors.toList()));
							tempAbsenceHistory.add(his);
							return his;
						})));

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return tempAbsenceHistory;
	}

	@Override
	public List<DateHistoryItem> getListByListSidsNoWithPeriod(String cid, List<String> sids) {

		List<DateHistoryItem> result = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			String sql = "SELECT * FROM BSYMT_TEMP_ABS_HISTORY" + " WHERE CID = ?  AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")" + " ORDER BY START_DATE ASC";

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}

				List<DateHistoryItem> lstObj = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					return new DateHistoryItem(rec.getString("HIST_ID"),
							new DatePeriod(rec.getGeneralDate("START_DATE"), rec.getGeneralDate("END_DATE")));
				}).stream().collect(Collectors.toList());
				result.addAll(lstObj);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	@Override
	public void addAll(Map<String, DateHistoryItem> dateHistItemsMap) {
		String cid = AppContexts.user().companyId();
		String INS_SQL = "INSERT INTO BSYMT_TEMP_ABS_HISTORY (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," + " HIST_ID, CID, SID," + " START_DATE, END_DATE)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " HIST_ID_VAL, CID_VAL, SID_VAL, START_DATE_VAL, END_DATE_VAL); ";
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();

		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
		dateHistItemsMap.entrySet().stream().forEach(c -> {
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

			sql = sql.replace("HIST_ID_VAL", "'" + dateHistItem.identifier() + "'");
			sql = sql.replace("CID_VAL", "'" + cid + "'");
			sql = sql.replace("SID_VAL", "'" + c.getKey() + "'");
			sql = sql.replace("START_DATE_VAL", "'" + dateHistItem.start() + "'");
			sql = sql.replace("END_DATE_VAL", "'" + dateHistItem.end() + "'");

			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);

	}

	@Override
	public void updateAll(List<DateHistoryItem> items) {
		String UP_SQL = "UPDATE BSYMT_TEMP_ABS_HISTORY SET UPD_DATE = UPD_DATE_VAL, UPD_CCD = UPD_CCD_VAL, UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL,"
				+ " START_DATE = START_DATE_VAL, END_DATE = END_DATE_VAL"
				+ " WHERE HIST_ID = HIST_ID_VAL AND CID = CID_VAL;";
		String cid = AppContexts.user().companyId();
		String updCcd = AppContexts.user().companyCode();
		String updScd = AppContexts.user().employeeCode();
		String updPg = AppContexts.programId();

		StringBuilder sb = new StringBuilder();
		items.stream().forEach(c -> {
			String sql = UP_SQL;
			sql = UP_SQL.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

			sql = sql.replace("START_DATE_VAL", "'" + c.start() + "'");
			sql = sql.replace("END_DATE_VAL", "'" + c.end() + "'");

			sql = sql.replace("HIST_ID_VAL", "'" + c.identifier() + "'");
			sql = sql.replace("CID_VAL", "'" + cid + "'");
			sb.append(sql);
		});
		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
	}

	@Override
	public List<TempAbsenceHistory> getHistoryBySidAndCidAndBaseDate(String cid, List<String> sids,
			GeneralDate standardDate) {
		List<TempAbsenceHistory> tempAbsenceHistory = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			String sql = "SELECT * FROM BSYMT_TEMP_ABS_HISTORY" + " WHERE CID = ?" + " AND START_DATE <= ?"
					+ " AND END_DATE >= ?" + " AND SID IN (" + NtsStatement.In.createParamsString(subList) + ")";

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {

				stmt.setString(1, cid);
				stmt.setDate(2, Date.valueOf(standardDate.toLocalDate()));
				stmt.setDate(3, Date.valueOf(standardDate.toLocalDate()));
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(4 + i, subList.get(i));
				}

				List<Map<String, Object>> map = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					Map<String, Object> m = new HashMap<>();
					m.put("HIST_ID", rec.getString("HIST_ID"));
					m.put("SID", rec.getString("SID"));
					m.put("CID", rec.getString("CID"));
					m.put("START_DATE", rec.getGeneralDate("START_DATE"));
					m.put("END_DATE", rec.getGeneralDate("END_DATE"));
					return m;
				});
				map.stream().collect(Collectors.groupingBy(c -> c.get("SID"),
						Collectors.collectingAndThen(Collectors.toList(), list -> {
							TempAbsenceHistory his = new TempAbsenceHistory(list.get(0).get("CID").toString(),
									list.get(0).get("SID").toString(), list.stream().map(c -> {
										return new DateHistoryItem(c.get("HIST_ID").toString(), new DatePeriod(
												(GeneralDate) c.get("START_DATE"), (GeneralDate) c.get("END_DATE")));
									}).collect(Collectors.toList()));
							tempAbsenceHistory.add(his);
							return his;
						})));

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return tempAbsenceHistory;
	}

	@Override
	public void insert(TempAbsenceHistory tempAbsenceHistory, TempAbsenceHisItem tempAbsenceHisItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(TempAbsenceHistory tempAbsenceHistory, TempAbsenceHisItem tempAbsenceHisItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String companyID, String empID, String historyID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String companyID, String empID) {
		// TODO Auto-generated method stub

	}

	@Override
	public Optional<TempAbsenceHistory> specifyEmpIDGetHistory(String companyID, String employeeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<TempAbsenceHisItem> getHistoryItemBySpecifyingHistoryID(String hisID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final String GET_DATA5_1  = " SELECT k FROM BsymtTempAbsHisItem k"
            + " WHERE k.histId IN :lstHisId ";
	private static final String GET_DATA5_2  = " SELECT k FROM BsymtTempAbsHisItem k"
			+ " WHERE k.histId IN :lstHisId "
			+ " AND k.tempAbsFrameNo IN :lstTempAbsenceFrNo ";

	@Override
	public List<TempAbsenceHisItem> specifyHisAndFrameNotGetHisItem(List<String> lstHisId,
			List<TempAbsenceFrameNo> lstTempAbsenceFrNo) {
		if (lstHisId.isEmpty()) {
			return new ArrayList<>();
		}
		List<Integer> lstTempAbsenceFrNos = lstTempAbsenceFrNo.stream().map(item -> item.v().intValue()).collect(Collectors.toList());
		List<TempAbsenceHisItem> data = new ArrayList<TempAbsenceHisItem>();
		if (lstTempAbsenceFrNo.isEmpty()) {
			data = this.queryProxy().query(GET_DATA5_1, BsymtTempAbsHisItem.class).setParameter("lstHisId", lstHisId)
					.getList(x -> BsymtTempAbsHisItem.toDomainHistItem(x));
		} else {
			data = this.queryProxy().query(GET_DATA5_2, BsymtTempAbsHisItem.class)
					.setParameter("lstHisId", lstHisId)
					.setParameter("lstTempAbsenceFrNo", lstTempAbsenceFrNos)
					.getList(x -> BsymtTempAbsHisItem.toDomainHistItem(x));
		}

		return data;
	}

	@Override
	public List<TempAbsenceHisItem> getHisItemsAsOfDate(String companyId, GeneralDate date) {
		// TODO Auto-generated method stub
		return null;
	}

	// [8-1] 期間付き履歴項目を取得する
	@Override
	public List<TimeoffLeaveRecordWithPeriod> getHistoryItemWithPeriod(String companyID, List<String> lstEmpId,
			DatePeriod datePeriod, List<TempAbsenceFrameNo> lstTempAbsenceFrameNo) {
		// TODO Auto-generated method stub
		// $履歴リスト = [3-2] *社員IDを指定して履歴を取得する ( 会社ID, 社員IDリスト )
		List<TempAbsenceHistory> absenceHistories = this.getHistoryByListEmp(companyID, lstEmpId);

		// $汎用履歴項目リスト = $履歴リスト:
		List<DateHistoryItem> dateHistoryItems = new ArrayList<>();
		// flatMap $.期間
		dateHistoryItems = absenceHistories.stream().flatMap(c -> c.getDateHistoryItems().stream())
				.collect(Collectors.toList());
		// filter
		dateHistoryItems = dateHistoryItems.stream()
				.filter(x -> (datePeriod.contains(x.start()) || datePeriod.contains(x.end())
						|| x.contains(datePeriod.start()) || x.contains(datePeriod.end())))
				.collect(Collectors.toList());

		// $履歴IDリスト = $汎用履歴項目リスト: map $.履歴ID
		List<String> lstHisId = dateHistoryItems.stream().map(mapper -> mapper.identifier())
				.collect(Collectors.toList());

		// $履歴項目リスト = [5] 履歴IDと枠NOを指定して履歴項目を取得する ( $履歴IDリスト, 枠NOリスト )
		List<TempAbsenceHisItem> lstHisItems = this.specifyHisAndFrameNotGetHisItem(lstHisId, lstTempAbsenceFrameNo);

		// return $汎用履歴項目 in $汎用履歴項目リスト:
		List<TimeoffLeaveRecordWithPeriod> result = new ArrayList<>();
		for (DateHistoryItem item : dateHistoryItems) {
			// $履歴項目
			Optional<TempAbsenceHisItem> tempAbsenceHis = lstHisItems.stream()
					.map(mapper -> new TempAbsenceHisItem(new TempAbsenceFrameNo(mapper.getTempAbsenceFrNo().v()),
							mapper.getHistoryId(), mapper.getEmployeeId(), new GenericString(mapper.getRemarks().v()),
							mapper.getSoInsPayCategory(), mapper.getFamilyMemberId()))
					.collect(Collectors.toList()).stream()
					.filter(predicate -> predicate.getHistoryId().equals(item.identifier())).findFirst();

			// if $履歴項目.isEmpty()
			TimeoffLeaveRecordWithPeriod recordWithPeriod = new TimeoffLeaveRecordWithPeriod(datePeriod,
						tempAbsenceHis.isPresent() ? tempAbsenceHis.get() : null);
			result.add(recordWithPeriod);
		}
		result = result.stream().filter(predicate -> predicate.getTempAbsenceHisItem() != null).collect(Collectors.toList());
		
		return result;
	}

	// [8-2] 期間付き休職履歴項目を取得する
	@Override
	public List<TimeoffLeaveRecordWithPeriod> getLeaveHistoryItemsWithPeriod(String companyID, List<String> lstEmpId,
			DatePeriod datePeriod) {
		List<TempAbsenceFrameNo> lstTempAbsenceFrameNo = new ArrayList<TempAbsenceFrameNo>();
		lstTempAbsenceFrameNo.add(new TempAbsenceFrameNo(BigDecimal.valueOf(1)));
		List<TimeoffLeaveRecordWithPeriod> recordWithPeriods = this.getHistoryItemWithPeriod(companyID, lstEmpId,
				datePeriod, lstTempAbsenceFrameNo);
		return recordWithPeriods;
	}

	// [8-3] 期間付き休業履歴項目を取得する
	@Override
	public List<TimeoffLeaveRecordWithPeriod> getAbsenceHistoryItemPeriod(String companyId, List<String> lstEmpId,
			DatePeriod datePeriod) {

		List<TempAbsenceFrameNo> lstTempAbsenceFrameNo = new ArrayList<TempAbsenceFrameNo>();

		for (int i = 2; i < 11; i++) {
			lstTempAbsenceFrameNo.add(new TempAbsenceFrameNo(BigDecimal.valueOf(i)));
		}

		List<TimeoffLeaveRecordWithPeriod> recordWithPeriods = this.getHistoryItemWithPeriod(companyId, lstEmpId,
				datePeriod, lstTempAbsenceFrameNo);
		return recordWithPeriods;
	}
	
	private static final String GET_DATA32 = " SELECT k FROM BsymtTempAbsHistory k "
            + " WHERE k.cid = :companyId "   
            + " AND k.sid IN :lstEmpId ";

	// [3-2]
	@Override
	public List<TempAbsenceHistory> getHistoryByListEmp(String companyId, List<String> lstEmpId) {
		List<TempAbsenceHistory> data = new ArrayList<>();
		List<BsymtTempAbsHistory> data1 = this.queryProxy().query(GET_DATA32, BsymtTempAbsHistory.class)
							.setParameter("companyId", companyId)
							.setParameter("lstEmpId", lstEmpId)
							.getList();
		
		for(String emp :lstEmpId){
			List<BsymtTempAbsHistory> temp = data1.stream().filter(x->x.getSid().equals(emp)).collect(Collectors.toList());
			Optional<TempAbsenceHistory> tempAbsenceHistory = BsymtTempAbsHistory.toDomainHis(temp);
			if(tempAbsenceHistory.isPresent()){
				data.add(tempAbsenceHistory.get());	
			}
		}
		return data;
	}
}
