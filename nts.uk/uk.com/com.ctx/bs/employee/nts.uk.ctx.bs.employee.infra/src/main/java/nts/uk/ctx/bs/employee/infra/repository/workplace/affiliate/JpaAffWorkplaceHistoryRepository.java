/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.infra.repository.workplace.affiliate;

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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.SneakyThrows;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.ctx.bs.employee.infra.entity.workplace.affiliate.BsymtAffiWorkplaceHist;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * The Class JpaAffWorkplaceHistoryRepository.
 */
@Stateless
public class JpaAffWorkplaceHistoryRepository extends JpaRepository implements AffWorkplaceHistoryRepository {
	private static final String QUERY_GET_AFFWORKPLACEHIST_BYSID = "SELECT aw FROM BsymtAffiWorkplaceHist aw "
			+ "WHERE aw.sid = :sid and aw.cid = :companyId ORDER BY aw.strDate";

	private static final String QUERY_GET_AFFWORKPLACEHIST_BYSID_DESC = QUERY_GET_AFFWORKPLACEHIST_BYSID + " DESC";

	private static final String SELECT_BY_EMPID_STANDDATE = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
			+ " WHERE aw.sid = :employeeId AND aw.strDate <= :standDate AND :standDate <= aw.endDate";

	private static final String SELECT_BY_HISTID = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
			+ " WHERE aw.hisId = :histId";

	private static final String SELECT_BY_LIST_WKPIDS_BASEDATE = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
			+ " INNER JOIN BsymtAffiWorkplaceHistItem awit on aw.hisId = awit.hisId"
			+ " WHERE awit.workPlaceId IN :wkpIds AND aw.strDate <= :standDate AND :standDate <= aw.endDate";

	private static final String SELECT_BY_WKPID_BASEDATE = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
			+ " INNER JOIN BsymtAffiWorkplaceHistItem awit on aw.hisId = awit.hisId"
			+ " WHERE awit.workPlaceId = :workplaceId AND aw.strDate <= :standDate AND :standDate <= aw.endDate";

//	private static final String SELECT_BY_LIST_EMPID_STANDDATE = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
//			+ " WHERE aw.sid IN :employeeIds AND aw.strDate <= :standDate AND :standDate <= aw.endDate";

	private static final String SELECT_BY_LIST_EMPID_BY_LIST_WKPIDS_BASEDATE = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
			+ " INNER JOIN BsymtAffiWorkplaceHistItem awit on aw.hisId = awit.hisId"
			+ " WHERE aw.sid IN :employeeIds AND awit.workPlaceId IN :wkpIds AND aw.strDate <= :standDate AND :standDate <= aw.endDate";

	private static final String SELECT_BY_HISTID_AND_DATE = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
			+ " INNER JOIN BsymtAffiWorkplaceHistItem awit on aw.hisId = awit.hisId"
			+ " WHERE aw.hisId = :histId AND aw.strDate <= :baseDate AND :baseDate <= aw.endDate";

	private static final String SELECT_BY_EMPIDS = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
			+ " INNER JOIN BsymtAffiWorkplaceHistItem awit on aw.hisId = awit.hisId"
			+ " WHERE aw.sid IN :employeeIds AND aw.strDate <= :standDate AND :standDate <= aw.endDate";
	
	private static final String SELECT_BY_EMPIDS_PERIOD = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
			+ " WHERE aw.sid IN :employeeIds AND aw.strDate <= :endDate AND aw.endDate >= :startDate"
			+ " ORDER BY aw.sid, aw.strDate";

	private static final String SELECT_BY_WKPID_PERIOD = "SELECT DISTINCT  a.sid FROM BsymtAffiWorkplaceHist a"
			+ " INNER JOIN BsymtAffiWorkplaceHistItem b ON a.hisId = b.hisId"
			+ " WHERE b.workPlaceId = :workPlaceId AND a.strDate <= :endDate AND  a.endDate >= :startDate";
	
	private static final String SELECT_BY_LIST_WKPID_PERIOD = "SELECT DISTINCT  a.sid FROM BsymtAffiWorkplaceHist a"
			+ " INNER JOIN BsymtAffiWorkplaceHistItem b ON a.hisId = b.hisId"
			+ " WHERE b.workPlaceId IN :lstWkpId AND a.strDate <= :endDate AND  a.endDate >= :startDate";

	private static final String SELECT_BY_LISTSID = "SELECT aw FROM BsymtAffiWorkplaceHist aw"
			+ " INNER JOIN BsymtAffiWorkplaceHistItem awit on aw.hisId = awit.hisId"
			+ " WHERE aw.sid IN :listSid ";
	
	/**
	 * Convert from domain to entity
	 *
	 * @param employeeID
	 * @param item
	 * @return
	 */
	private BsymtAffiWorkplaceHist toEntity(String cid, String employeeID, DateHistoryItem item) {
		return new BsymtAffiWorkplaceHist(item.identifier(), employeeID, cid, item.start(), item.end());
	}

	/**
	 * Update entity from domain
	 *
	 * @param employeeID
	 * @param item
	 * @return
	 */
	private void updateEntity(DateHistoryItem item, BsymtAffiWorkplaceHist entity) {
		entity.setStrDate(item.start());
		entity.setEndDate(item.end());
	}

	/**
	 * Convert from entity to domain
	 *
	 * @param entity
	 * @return
	 */
	private AffWorkplaceHistory toDomainTemp(List<BsymtAffiWorkplaceHist> listHist) {
		AffWorkplaceHistory domain = new AffWorkplaceHistory(listHist.get(0).getCid(),
				listHist.get(0).getSid(), new ArrayList<DateHistoryItem>());
		for (BsymtAffiWorkplaceHist item : listHist) {
			DateHistoryItem dateItem = new DateHistoryItem(item.getHisId(),
					new DatePeriod(item.getStrDate(), item.getEndDate()));
			domain.getHistoryItems().add(dateItem);
		}
		return domain;
	}

	@Override
	public Optional<AffWorkplaceHistory> getByEmployeeId(String companyId, String employeeId) {
		List<BsymtAffiWorkplaceHist> listHist = this.queryProxy()
				.query(QUERY_GET_AFFWORKPLACEHIST_BYSID, BsymtAffiWorkplaceHist.class).setParameter("sid", employeeId)
				.setParameter("companyId", companyId).getList();
		if (listHist != null && !listHist.isEmpty()) {
			return Optional.of(toDomainTemp(listHist));
		}
		return Optional.empty();
	}

	@Override
	public Optional<AffWorkplaceHistory> getByEmployeeIdDesc(String companyId, String employeeId) {
		List<BsymtAffiWorkplaceHist> listHist = this.queryProxy()
				.query(QUERY_GET_AFFWORKPLACEHIST_BYSID_DESC, BsymtAffiWorkplaceHist.class)
				.setParameter("sid", employeeId).setParameter("companyId", companyId).getList();
		if (listHist != null && !listHist.isEmpty()) {
			return Optional.of(toDomainTemp(listHist));
		}
		return Optional.empty();
	}

	@Override
	public void add(String cid, String sid, DateHistoryItem item) {
		this.commandProxy().insert(toEntity(cid, sid, item));
	}

	@Override
	public void delete(String histId) {

		Optional<BsymtAffiWorkplaceHist> histItem = this.queryProxy().find(histId, BsymtAffiWorkplaceHist.class);
		if (!histItem.isPresent()) {
			throw new RuntimeException("invalid BsymtAffiWorkplaceHist");
		}
		this.commandProxy().remove(BsymtAffiWorkplaceHist.class, histId);
	}

	@Override
	public void update(DateHistoryItem item) {
		Optional<BsymtAffiWorkplaceHist> histItem = this.queryProxy().find(item.identifier(),
				BsymtAffiWorkplaceHist.class);
		if (!histItem.isPresent()) {
			throw new RuntimeException("invalid BsymtAffiWorkplaceHist");
		}
		updateEntity(item, histItem.get());
		this.commandProxy().update(histItem.get());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Optional<AffWorkplaceHistory> getByEmpIdAndStandDate(String employeeId, GeneralDate standDate) {
		List<BsymtAffiWorkplaceHist> listHist = this.queryProxy()
				.query(SELECT_BY_EMPID_STANDDATE, BsymtAffiWorkplaceHist.class).setParameter("employeeId", employeeId)
				.setParameter("standDate", standDate).getList();
		if (!listHist.isEmpty()) {
			return Optional.of(toDomainTemp(listHist));
		}
		return Optional.empty();
	}

	@Override
	public Optional<AffWorkplaceHistory> getByHistId(String histId) {
		List<BsymtAffiWorkplaceHist> listHist = this.queryProxy().query(SELECT_BY_HISTID, BsymtAffiWorkplaceHist.class)
				.setParameter("histId", histId).getList();
		if (!listHist.isEmpty()) {
			return Optional.of(toDomainTemp(listHist));
		}
		return Optional.empty();
	}

	@Override
	public List<AffWorkplaceHistory> findByEmployees(List<String> employeeIds, GeneralDate date) {
		if (CollectionUtil.isEmpty(employeeIds)) {
			return new ArrayList<>();
		}

		List<BsymtAffiWorkplaceHist> resultList = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			// Query.
			resultList.addAll(this.queryProxy().query(SELECT_BY_EMPIDS, BsymtAffiWorkplaceHist.class)
					.setParameter("employeeIds", subList).setParameter("standDate", date).getList());
		});

		// Group by his id.
		Map<String, List<BsymtAffiWorkplaceHist>> resultMap = resultList.stream()
				.collect(Collectors.groupingBy(BsymtAffiWorkplaceHist::getHisId));

		// Convert to domain.
		return resultMap.keySet().stream().map(key -> {
			return this.toDomainTemp(resultMap.get(key));
		}).collect(Collectors.toList());
	}
	
	@Override
	public Map<GeneralDate, List<AffWorkplaceHistory>> findByEmployees(String companyId, List<String> employeeIds, List<GeneralDate> listdate) {
		Map<GeneralDate, List<AffWorkplaceHistory>> result = new HashMap<GeneralDate, List<AffWorkplaceHistory>>();
		if (CollectionUtil.isEmpty(employeeIds)) {
			for (GeneralDate generalDate : listdate) {
				result.put(generalDate, new ArrayList<>());
			}
			return result;
		}

		String query = "SELECT aw FROM BsymtAffiWorkplaceHist aw  WHERE aw.cid = :companyId AND aw.sid IN :employeeIds";
		List<BsymtAffiWorkplaceHist> datatList = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			// Query.
			datatList.addAll(this.queryProxy().query(query, BsymtAffiWorkplaceHist.class)
					.setParameter("companyId", companyId)
					.setParameter("employeeIds", subList)
					.getList());
		});
		
		for (GeneralDate date : listdate) {
			List<BsymtAffiWorkplaceHist> list = datatList.stream().filter(c -> c.getStrDate().beforeOrEquals(date) && c.getEndDate().afterOrEquals(date)).collect(Collectors.toList());
			// Group by his id.
			Map<String, List<BsymtAffiWorkplaceHist>> resultMap = list.stream()
					.collect(Collectors.groupingBy(BsymtAffiWorkplaceHist::getHisId));

			// Convert to domain.
			List<AffWorkplaceHistory> data = resultMap.keySet().stream().map(key -> {
				return this.toDomainTemp(resultMap.get(key));
			}).collect(Collectors.toList());
			result.put(date, data);
		}

		return result;
	}
	
	@Override
	public List<AffWorkplaceHistory> findByEmployeesWithPeriod(List<String> employeeIds, DatePeriod period) {
		if (CollectionUtil.isEmpty(employeeIds)) {
			return new ArrayList<>();
		}
		
		String companyId = AppContexts.user().companyId();
		
		List<BsymtAffiWorkplaceHist> workPlaceEntities = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIds -> {
			String sql = "select * from BSYMT_AFF_WKP_HIST h"
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
				
				List<BsymtAffiWorkplaceHist> ents = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					BsymtAffiWorkplaceHist ent = new BsymtAffiWorkplaceHist();
					ent.setHisId(rec.getString("HIST_ID"));
					ent.setCid(rec.getString("CID"));
					ent.setSid(rec.getString("SID"));
					ent.setStrDate(rec.getGeneralDate("START_DATE"));
					ent.setEndDate(rec.getGeneralDate("END_DATE"));
					return ent;
				});
				workPlaceEntities.addAll(ents);
				
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
//			List<BsymtAffiWorkplaceHist> subEntities = this.queryProxy()
//					.query(SELECT_BY_EMPIDS_PERIOD, BsymtAffiWorkplaceHist.class).setParameter("employeeIds", subIds)
//					.setParameter("startDate", period.start()).setParameter("endDate", period.end()).getList();
//			workPlaceEntities2.addAll(subEntities);
		});
		
		
		Map<String, List<BsymtAffiWorkplaceHist>> workPlaceByEmployeeId = workPlaceEntities.stream()
				.collect(Collectors.groupingBy(BsymtAffiWorkplaceHist::getEmployeeId));
		
		List<AffWorkplaceHistory> workplaceHistoryList = new ArrayList<>();
		
		workPlaceByEmployeeId.forEach((employeeId, entities) -> {
			List<DateHistoryItem> historyItems = convertToHistoryItems(entities);
			workplaceHistoryList.add(new AffWorkplaceHistory(companyId, employeeId, historyItems));
		});
		
		return workplaceHistoryList;
	}
	
	public List<DateHistoryItem> convertToHistoryItems(List<BsymtAffiWorkplaceHist> entities) {
		return entities.stream()
				.map(ent -> new DateHistoryItem(ent.getHisId(), new DatePeriod(ent.getStrDate(), ent.getEndDate())))
				.collect(Collectors.toList());
	}
	

	@Override
	public Optional<AffWorkplaceHistory> getByHistIdAndBaseDate(String histId, GeneralDate date) {
		List<BsymtAffiWorkplaceHist> listHist = this.queryProxy()
				.query(SELECT_BY_HISTID_AND_DATE, BsymtAffiWorkplaceHist.class).setParameter("histId", histId)
				.setParameter("baseDate", date).getList();
		if (!listHist.isEmpty()) {
			return Optional.of(toDomainTemp(listHist));
		}
		return Optional.empty();
	}

	@Override
	public List<AffWorkplaceHistory> getWorkplaceHistoryByEmployeeIdAndDate(GeneralDate baseDate,
			String employeeId) {
		List<BsymtAffiWorkplaceHist> listWkpHist = this.queryProxy()
				.query(SELECT_BY_EMPID_STANDDATE, BsymtAffiWorkplaceHist.class).setParameter("employeeId", employeeId)
				.setParameter("standDate", baseDate).getList();
		if (listWkpHist.isEmpty()) {
			return Collections.emptyList();
		}
		return listWkpHist.stream().map(e -> {
			AffWorkplaceHistory domain = this.toDomain(e);
			return domain;
		}).collect(Collectors.toList());
	}

	@Override
	public List<AffWorkplaceHistory> getWorkplaceHistoryByWkpIdsAndDate(GeneralDate baseDate,
			List<String> workplaceIds) {
		List<BsymtAffiWorkplaceHist> resultList = new ArrayList<>();
		CollectionUtil.split(workplaceIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(SELECT_BY_LIST_WKPIDS_BASEDATE, BsymtAffiWorkplaceHist.class)
					.setParameter("wkpIds", subList).setParameter("standDate", baseDate).getList());
		});
		if (resultList.isEmpty()) {
			return Collections.emptyList();
		}
		return resultList.stream().map(e -> {
			AffWorkplaceHistory domain = this.toDomain(e);
			return domain;
		}).collect(Collectors.toList());
	}

	@Override
	public List<AffWorkplaceHistory> getWorkplaceHistoryByWorkplaceIdAndDate(GeneralDate baseDate,
			String workplaceId) {
		List<BsymtAffiWorkplaceHist> listWkpHist = this.queryProxy()
				.query(SELECT_BY_WKPID_BASEDATE, BsymtAffiWorkplaceHist.class).setParameter("workplaceId", workplaceId)
				.setParameter("standDate", baseDate).getList();
		if (listWkpHist.isEmpty()) {
			return Collections.emptyList();
		}
		return listWkpHist.stream().map(e -> {
			AffWorkplaceHistory domain = this.toDomain(e);
			return domain;
		}).collect(Collectors.toList());
	}

	//fix sửa thành jdbc -> tăng tốc độ truy vấn
	@Override
	public List<AffWorkplaceHistory> getWorkplaceHistoryByEmpIdsAndDate(GeneralDate baseDate,
			List<String> employeeIds) {
		List<AffWorkplaceHistory> result = new ArrayList<>();
		
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM BSYMT_AFF_WKP_HIST WHERE  START_DATE <= ? AND END_DATE >= ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setDate(1, Date.valueOf(baseDate.toLocalDate()));
				stmt.setDate(2, Date.valueOf(baseDate.toLocalDate()));
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(3 + i, subList.get(i));
				}

				List<AffWorkplaceHistory> affWorkplaceHistLst = new NtsResultSet(stmt.executeQuery()).getList(r -> {
					BsymtAffiWorkplaceHist history = new BsymtAffiWorkplaceHist(r.getString("HIST_ID"),
							r.getString("SID"), r.getString("CID"), r.getGeneralDate("START_DATE"),
							r.getGeneralDate("END_DATE"));
					return toDomain(history);
				}).stream().collect(Collectors.toList());
				result.addAll(affWorkplaceHistLst);

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});

		return result;
	}

	@Override
	public List<AffWorkplaceHistory> searchWorkplaceHistory(GeneralDate baseDate,
			List<String> employeeIds, List<String> workplaceIds) {

		if (CollectionUtil.isEmpty(employeeIds) || CollectionUtil.isEmpty(workplaceIds)) {
			return Collections.emptyList();
		}

		List<BsymtAffiWorkplaceHist> resultList = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, empSubList -> {
			CollectionUtil.split(workplaceIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, wplSubList -> {
				resultList.addAll(this.queryProxy()
						.query(SELECT_BY_LIST_EMPID_BY_LIST_WKPIDS_BASEDATE, BsymtAffiWorkplaceHist.class)
						.setParameter("employeeIds", employeeIds).setParameter("wkpIds", workplaceIds)
						.setParameter("standDate", baseDate).getList());
			});
		});

		return resultList.stream().map(e ->  this.toDomain(e)).collect(Collectors.toList());
	}

	// convert to domain
	private AffWorkplaceHistory toDomain(BsymtAffiWorkplaceHist entity) {
		AffWorkplaceHistory domain = new AffWorkplaceHistory(entity.getCid(), entity.getSid(),
				new ArrayList<DateHistoryItem>());
		DateHistoryItem dateItem = new DateHistoryItem(entity.getHisId(),
				new DatePeriod(entity.getStrDate(), entity.getEndDate()));
		domain.getHistoryItems().add(dateItem);

		return domain;
	}

	@Override
	@SneakyThrows
	public List<String> getByWplIdAndPeriod(String workplaceId, GeneralDate startDate, GeneralDate endDate) {

		String sql = "select distinct h.SID from BSYMT_AFF_WKP_HIST h"
				+ " inner join BSYMT_AFF_WKP_HIST_ITEM i"
				+ " on h.HIST_ID = i.HIST_ID"
				+ " where i.WORKPLACE_ID = ?"
				+ " and h.START_DATE <= ? and h.END_DATE >= ?";
		
		try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
			stmt.setString(1, workplaceId);
			stmt.setDate(2, Date.valueOf(endDate.localDate()));
			stmt.setDate(3, Date.valueOf(startDate.localDate()));
			
			return new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				return rec.getString("SID");
			});
		}
	}
	
	@Override
	public List<String> getByLstWplIdAndPeriod(List<String> lstWkpId, GeneralDate startDate, GeneralDate endDate) {
		// Split query.
		List<String> resultList = new ArrayList<>();

		CollectionUtil.split(lstWkpId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			resultList.addAll(this.queryProxy().query(SELECT_BY_LIST_WKPID_PERIOD, String.class)
					.setParameter("lstWkpId", subList)
					.setParameter("startDate", startDate)
					.setParameter("endDate", endDate).getList());
		});

		if (!resultList.isEmpty()) {
			return resultList;
		} else {
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<AffWorkplaceHistory> getByListSid(List<String> listSid) {
	  
		// Split query.
		List<BsymtAffiWorkplaceHist> resultList = new ArrayList<>();

		CollectionUtil.split(listSid, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {
			resultList.addAll(this.queryProxy().query(SELECT_BY_LISTSID, BsymtAffiWorkplaceHist.class)
				.setParameter("listSid", subList).getList());
		});

		return resultList.stream().map(entity -> this.toDomain(entity)).collect(Collectors.toList());
	}

	@Override
	public List<AffWorkplaceHistory> getBySidsAndCid(String cid, List<String> sids) {
		List<AffWorkplaceHistory> result = new ArrayList<>();
		
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM BSYMT_AFF_WKP_HIST WHERE  CID = ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")" + " ORDER BY SID, START_DATE DESC";

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString( 1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}

				Map<String, List<BsymtAffiWorkplaceHist>> affWorkplaceHistLst = new NtsResultSet(stmt.executeQuery()).getList(r -> {
					BsymtAffiWorkplaceHist history = new BsymtAffiWorkplaceHist(r.getString("HIST_ID"),
							r.getString("SID"), r.getString("CID"), r.getGeneralDate("START_DATE"),
							r.getGeneralDate("END_DATE"));
					return history;
				}).stream().collect(Collectors.groupingBy(c -> c.getSid()));
				
				if(!affWorkplaceHistLst.isEmpty()) {
					result.addAll(affWorkplaceHistLst.entrySet().stream().map(c -> toDomainTemp(c.getValue())).collect(Collectors.toList()));
				}
				
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}
	
	// get data cps013
	@Override
	public List<DateHistoryItem> getListByListSidsNoWithPeriod(String cid, List<String> sids) {
		
		List<DateHistoryItem> result = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			try (PreparedStatement statement = this.connection().prepareStatement(
						"SELECT * from BSYMT_AFF_WKP_HIST h"
						+ " WHERE h.CID = ? AND h.SID IN (" + subList.stream().map(s -> "?").collect(Collectors.joining(",")) + ")" + " ORDER BY START_DATE ASC ")) {
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
	@SneakyThrows
	public List<AffWorkplaceHistory> getWorkplaceHistoryBySidsAndDateV2(GeneralDate baseDate,
			List<String> sids) {
		List<AffWorkplaceHistory> result = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, (subList) -> {

			String sql = "SELECT * FROM  BSYMT_AFF_WKP_HIST h" + " WHERE h.START_DATE >= ? AND SID IN (" +  NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setDate(1, Date.valueOf(baseDate.toLocalDate()));
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}
				
				List<BsymtAffiWorkplaceHist> entities = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					BsymtAffiWorkplaceHist entity = new BsymtAffiWorkplaceHist(rec.getString("HIST_ID"), rec.getString("SID"), rec.getString("CID"), rec.getGeneralDate("START_DATE"), rec.getGeneralDate("END_DATE"));
					return entity;
				});
				
				Map<String, List<BsymtAffiWorkplaceHist>> workPlaceByEmployeeId = entities.stream()
						.collect(Collectors.groupingBy(BsymtAffiWorkplaceHist::getEmployeeId));
				
				workPlaceByEmployeeId.forEach((sid, entity) -> {
					List<DateHistoryItem> historyItems = convertToHistoryItems(entity);
					result.add(new AffWorkplaceHistory(entity.get(0).getCid(), sid, historyItems));
				});
				
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	@Override
	public void addAll(Map<String, DateHistoryItem> dateHistItems) {
		String cid = AppContexts.user().companyId();
		String INS_SQL = "INSERT INTO BSYMT_AFF_WKP_HIST (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " CONTRACT_CD, HIST_ID, SID, CID,"
				+ " START_DATE, END_DATE)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " CONTRACT_CD_VAL, HIST_ID_VAL, SID_VAL, CID_VAL, START_DATE_VAL, END_DATE_VAL); ";
		
		String contractCode = AppContexts.user().contractCode();
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();
		
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
		dateHistItems.entrySet().stream().forEach(c ->{
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
		
		String UP_SQL = "UPDATE BSYMT_AFF_WKP_HIST SET UPD_DATE = UPD_DATE_VAL, UPD_CCD = UPD_CCD_VAL, UPD_SCD = UPD_SCD_VAL, UPD_PG = UPD_PG_VAL,"
				+ " START_DATE = START_DATE_VAL, END_DATE = END_DATE_VAL"
				+ " WHERE HIST_ID = HIST_ID_VAL AND CID = CID_VAL;";
		String cid = AppContexts.user().companyId();
		String updCcd = AppContexts.user().companyCode();
		String updScd = AppContexts.user().employeeCode();
		String updPg = AppContexts.programId();
		
		StringBuilder sb = new StringBuilder();
		items.stream().forEach(c ->{
			String sql = UP_SQL;
			sql = UP_SQL.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() +"'");
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

}
