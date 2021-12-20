package nts.uk.ctx.sys.auth.infra.repository.wkpmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.dom.export.wkpmanager.WorkPlaceSelectionExportData;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;
import nts.uk.ctx.sys.auth.dom.wplmanagementauthority.WorkPlaceFunction;
import nts.uk.ctx.sys.auth.infra.entity.wkpmanager.SacmtWkpManager;
import nts.uk.ctx.sys.auth.infra.entity.wkpmanager.SacmtWorkplaceManagerPK;

@Stateless
public class JpaWorkplaceManagerRepository extends JpaRepository implements WorkplaceManagerRepository {
	/**
	 * Query strings
	 */
	private static final String SELECT_ALL = "SELECT wm FROM SacmtWkpManager wm";
	private static final String SELECT_All_BY_SID_WKP_ID = SELECT_ALL
			+ " WHERE wm.employeeId = :employeeId AND wm.workplaceId = :workplaceId";
	private static final String SELECT_All_BY_WKP_ID = SELECT_ALL
			+ " WHERE wm.workplaceId = :workplaceId ORDER BY wm.employeeId, wm.startDate";
	private static final String SELECT_ALL_BY_SID_BASE_DATE = "SELECT wm FROM SacmtWkpManager wm"
			+ " WHERE wm.employeeId = :employeeId AND wm.startDate <= :baseDate AND wm.endDate >= :baseDate";
	
	private static final String SELECT_ALL_BY_WIDS_BASE_DATE = "SELECT wm FROM SacmtWkpManager wm"
			+ " WHERE wm.workplaceId IN :wkpIds AND wm.startDate <= :baseDate AND wm.endDate >= :baseDate";
	
	private static final String FIND_BY_WKP_DATE_MANAGER = "SELECT wm FROM SacmtWkpManager wm"
			+ " WHERE wm.workplaceId = :workplaceId" + " AND wm.startDate <= :baseDate AND wm.endDate >= :baseDate"
			+ " AND wm.kacmtWorkplaceManagerPK.workplaceManagerId IN :wkpManagerLst";

	private static final String FIND_BY_PERIOD_AND_WKPS = "SELECT wm FROM SacmtWkpManager wm "
			+ " WHERE wm.workplaceId IN :lstWkpId " + " AND wm.startDate <= :endDate AND wm.endDate >= :startDate ";

	private static final String WORKPLACE_SELECT_ALL = "SELECT wm.workplaceCode , wm.workplaceName , edm.employeeCode , ps.businessName , wi.startDate, wi.endDate ,wi.kacmtWorkplaceManagerPK.workplaceManagerId "
			+ "FROM BsymtWorkplaceInfor wm "
			+ "LEFT JOIN SacmtWkpManager wi ON wm.pk.workplaceId = wi.workplaceId "
			+ "LEFT JOIN BsymtEmployeeDataMngInfo edm ON wi.employeeId = edm.bsymtEmployeeDataMngInfoPk.sId "
			+ "LEFT JOIN BpsmtPerson ps ON edm.bsymtEmployeeDataMngInfoPk.pId = ps.bpsmtPersonPk.pId "
			+ "WHERE wm.pk.companyId =:companyId AND wi.kacmtWorkplaceManagerPK.workplaceManagerId IS NOT NULL ORDER BY wm.workplaceCode, edm.employeeCode, wi.startDate ASC";

	private static final String WORKPLACE_SELECT_ALL_BY_CID = "SELECT WKPCD , WKP_NAME , SCD , BUSINESS_NAME , START_DATE, END_DATE , [1], [2], [3] "
			+ "FROM " + "( "
			+ "SELECT wm.WKP_CD , wm.WKP_NAME , edm.SCD , ps.BUSINESS_NAME , wi.START_DATE, wi.END_DATE , AVAILABILITY, wkf.FUNCTION_NO "
			+ "FROM " + "BSYMT_WKP_INFO wm " + "LEFT JOIN SACMT_WKP_MANAGER wi ON wm.WKP_ID = wi.WKP_ID "
			+ "LEFT JOIN BSYMT_SYAIN edm ON wi.SID = edm.SID "
			+ "LEFT JOIN BPSMT_PERSON ps ON edm.PID = ps.PID "
			+ "INNER JOIN SACMT_WKP_AUTHORITY kwa ON wi.WKP_MANAGER_ID = kwa.ROLE_ID AND wm.CID = kwa.CID "
			+ "INNER JOIN SACCT_WKP_FUNCTION wkf on wkf.FUNCTION_NO = kwa.FUNCTION_NO "
			+ "WHERE wm.CID  =:companyId" + ") " + "AS sourceTable PIVOT ( " + "MAX(AVAILABILITY) "
			+ "FOR [FUNCTION_NO] IN ([1],[2],[3]) " + ") AS pvt ORDER BY WKPCD, SCD, START_DATE ASC";

	private static final String FIND_BY_WKP_AND_BASEDATE = "SELECT wm FROM SacmtWkpManager wm "
		+ " WHERE wm.workplaceId = :workplaceId " + " AND wm.startDate <= :baseDate AND wm.endDate >= :baseDate ";
	private static final String WORKPLACE_SELECT_ALL_CID;
	static {
		StringBuilder builderString = new StringBuilder();
		builderString = new StringBuilder();
		builderString
				.append("SELECT wkpcd , wkpName , employeeCode , businessName , startDate, endDate , [1], [2], [3]");
		builderString.append(" FROM");
		builderString.append(
				" (SELECT wm.workplaceCode , wm.workplaceName , edm.employeeCode , ps.businessName , wi.startDate, wi.endDate , kwa.availability, wkf.functionNo");
		builderString.append(" FROM BsymtWorkplaceInfor wm");
		builderString.append(" LEFT JOIN SacmtWkpManager wi ON wm.pk.workplaceId = wi.workplaceId");
		builderString.append(
				" LEFT JOIN BsymtEmployeeDataMngInfo edm ON wi.employeeId = edm.bsymtEmployeeDataMngInfoPk.sId");
		builderString.append(" LEFT JOIN BpsmtPerson ps ON edm.bsymtEmployeeDataMngInfoPk.pId = ps.bpsmtPersonPk.pId");
		builderString.append(
				" INNER JOIN KacmtWorkPlaceAuthority kwa ON wi.kacmtWorkplaceManagerPK.workplaceManagerId = kwa.kacmtWorkPlaceAuthorityPK.roleId AND wm.pk.companyId = kwa.kacmtWorkPlaceAuthorityPK.companyId");
		builderString.append(
				" INNER JOIN KacmtWorkPlaceFunction wkf ON kwa.kacmtWorkPlaceAuthorityPK.functionNo = wkf.functionNo");
		builderString.append(" WHERE wm.pk.companyId  =:companyId)");
		builderString.append(" AS sourceTable PIVOT ( ");
		builderString.append(" MAX(kwa.availability) ");
		builderString.append(" FOR [wkf.functionNo] IN ([1],[2],[3])");
		builderString.append(" ) AS pvt ORDER BY wkpcd, employeeCode, startDate ASC");

		WORKPLACE_SELECT_ALL_CID = builderString.toString();

	}
	
	@Override
	public Optional<WorkplaceManager> getWorkplaceManagerByID(String id) {
		return this.queryProxy().find(new SacmtWorkplaceManagerPK(id),SacmtWkpManager.class).map(SacmtWkpManager::toDomain);
	}

	/**
	 * Get workplace manager list by workplace id
	 * 
	 * 【input� ・会社ID ・職場ID ・基準日 【output� ・cls <職場表示�
	 */
	@Override
	public List<WorkplaceManager> getWkpManagerListByWkpId(String workplaceId) {
		return this.queryProxy().query(SELECT_All_BY_WKP_ID, SacmtWkpManager.class)
				.setParameter("workplaceId", workplaceId).getList(c -> c.toDomain());
	}

	/**
	 * Get workplace manager list by workplace id and employeeid
	 */
	@Override
	public List<WorkplaceManager> getWkpManagerBySIdWkpId(String employeeId, String workplaceId) {
		return this.queryProxy().query(SELECT_All_BY_SID_WKP_ID, SacmtWkpManager.class)
				.setParameter("employeeId", employeeId).setParameter("workplaceId", workplaceId)
				.getList(c -> c.toDomain());
	}

	@Override
	public void add(WorkplaceManager wkpManager) {
		this.commandProxy().insert(SacmtWkpManager.toEntity(wkpManager));

	}

	@Override
	public void update(WorkplaceManager wkpManager) {
		SacmtWkpManager updateData = SacmtWkpManager.toEntity(wkpManager);
		SacmtWkpManager oldData = this.queryProxy()
				.find(updateData.kacmtWorkplaceManagerPK, SacmtWkpManager.class).get();
		oldData.employeeId = updateData.employeeId;
		oldData.workplaceId = updateData.workplaceId;
		oldData.startDate = updateData.startDate;
		oldData.endDate = updateData.endDate;

		this.commandProxy().update(oldData);
	}

	@Override
	public void delete(String wkpManagerId) {
		SacmtWorkplaceManagerPK kacmtWorkplaceManagerPK = new SacmtWorkplaceManagerPK(wkpManagerId);
		this.commandProxy().remove(SacmtWkpManager.class, kacmtWorkplaceManagerPK);
	}

	@Override
	public List<WorkplaceManager> findListWkpManagerByEmpIdAndBaseDate(String employeeId, GeneralDate baseDate) {
		return this.queryProxy().query(SELECT_ALL_BY_SID_BASE_DATE, SacmtWkpManager.class)
				.setParameter("employeeId", employeeId).setParameter("baseDate", baseDate).getList(c -> c.toDomain());
	}
	
	@Override
	public List<WorkplaceManager> findListWkpManagerByWkpIdsAndBaseDate(List<String> wkpIDLst, GeneralDate baseDate) {
		return this.queryProxy().query(SELECT_ALL_BY_WIDS_BASE_DATE, SacmtWkpManager.class)
				.setParameter("wkpIds", wkpIDLst).setParameter("baseDate", baseDate).getList(c -> c.toDomain());
	}

	@Override
	public List<WorkplaceManager> findByWkpDateAndManager(String wkpID, GeneralDate baseDate,
			List<String> wkpManagerIDLst) {
		List<WorkplaceManager> resultList = new ArrayList<>();
		if(wkpManagerIDLst.isEmpty())
			return resultList;
		CollectionUtil.split(wkpManagerIDLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_WKP_DATE_MANAGER, SacmtWkpManager.class)
					.setParameter("workplaceId", wkpID).setParameter("baseDate", baseDate)
					.setParameter("wkpManagerLst", subList).getList(c -> c.toDomain()));
		});
		return resultList;
	}

	/*
	 * @Override public List<WorkPlaceSelectionExportData>
	 * findAllWorkPlaceSelection(String companyId) { return
	 * this.queryProxy().query(WORKPLACE_SELECT_ALL_CID,
	 * Object[].class).setParameter("companyId", companyId).getList(x ->
	 * toReportData(x)); }
	 * 
	 * private WorkPlaceSelectionExportData toReportData(Object[] entity) { //
	 * TODO Auto-generated method stub return new
	 * WorkPlaceSelectionExportData(entity[0] == null ? null :
	 * entity[0].toString(), entity[1] == null ? null : entity[1].toString(),
	 * entity[2] == null ? null : entity[2].toString(), entity[3] == null ? null
	 * : entity[3].toString(), entity[4] == null ? null : (GeneralDate)
	 * entity[4], entity[5] == null ? null : (GeneralDate) entity[5], //
	 * entity[6] == null ? null : (Integer)entity[6], entity[6] == null ? null :
	 * entity[6].toString()); }
	 */

	// Export Data table
	@Override
	@SneakyThrows
	public List<WorkPlaceSelectionExportData> findAllWorkPlaceSelection(String companyId,
			List<WorkPlaceFunction> workPlaceFunction) {	
		String functions = workPlaceFunction.stream().map(x -> x.getFunctionNo().v().toString())
				.collect(Collectors.toList()).stream().collect(Collectors.joining("], [", "[", "]"));
		String SQL = "SELECT WKPCD , WKP_NAME , SCD , BUSINESS_NAME , START_DATE, END_DATE , %s " + "FROM ( "
				+ "SELECT wm.WKP_CD , wm.WKP_NAME , edm.SCD , ps.BUSINESS_NAME , wi.START_DATE, wi.END_DATE , AVAILABILITY, wkf.FUNCTION_NO "
				+ "FROM " + "BSYMT_WKP_INFO wm " + "LEFT JOIN SACMT_WKP_MANAGER wi ON wm.WKP_ID = wi.WKP_ID "
				+ "LEFT JOIN BSYMT_SYAIN edm ON wi.SID = edm.SID "
				+ "LEFT JOIN BPSMT_PERSON ps ON edm.PID = ps.PID "
				+ "INNER JOIN SACMT_WKP_AUTHORITY kwa ON wi.WKP_MANAGER_ID = kwa.ROLE_ID AND wm.CID = kwa.CID "
				+ "INNER JOIN SACCT_WKP_FUNCTION wkf on wkf.FUNCTION_NO = kwa.FUNCTION_NO " + "WHERE wm.CID = ? "
				+ ") " + "AS sourceTable PIVOT ( " + "MAX(AVAILABILITY) " + "FOR [FUNCTION_NO] IN (%s) "
				+ ") AS pvt ORDER BY WKPCD, SCD, START_DATE ASC ";

		try (val stmt = this.connection().prepareStatement(String.format(SQL, functions, functions))) {
			stmt.setString(1, companyId);
			return new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				//WorkPlaceSelectionExportData item = new WorkPlaceSelectionExportData();
				return toReportDataTable(rec, workPlaceFunction);
			});
		}
	}

	@Override
	public List<WorkplaceManager> findByPeriodAndWkpIds(List<String> wkpIds, DatePeriod datePeriod) {
		List<WorkplaceManager> resultList = new ArrayList<>();
		CollectionUtil.split(wkpIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_PERIOD_AND_WKPS, SacmtWkpManager.class)
				.setParameter("startDate", datePeriod.start())
				.setParameter("endDate", datePeriod.end())
				.setParameter("lstWkpId", subList).getList(c -> c.toDomain()));
		});
		return resultList;
	}

	@Override
	public List<WorkplaceManager> findByPeriodAndBaseDate(String wkpId, GeneralDate baseDate) {

			return this.queryProxy().query(FIND_BY_WKP_AND_BASEDATE, SacmtWkpManager.class)
				.setParameter("workplaceId", wkpId)
				.setParameter("baseDate", baseDate)
				.getList(c -> c.toDomain());
	}

	private WorkPlaceSelectionExportData toReportDataTable(NtsResultRecord rec,
			List<WorkPlaceFunction> workPlaceFunction) {
		Map<String, String> values =new HashMap<String, String>();
	
		for (int i = 0 ; i < workPlaceFunction.size(); i++) {
			values.put(workPlaceFunction.get(i).getFunctionNo().v().toString(), rec.getString((i + 1) + ""));
		}
		WorkPlaceSelectionExportData item = new WorkPlaceSelectionExportData(
				rec.getString("WKPCD"),
				rec.getString("WKP_NAME"), 
				rec.getString("SCD"), 
				rec.getString("BUSINESS_NAME"),
				rec.getGeneralDate("START_DATE"), 
				rec.getGeneralDate("END_DATE"), 
				values);
		return item;
	}

	@Override
	public List<WorkplaceManager> getWkpManagerByWorkplaceIdAndSid(String workplaceId, String sid) {
		String FIND_BY_WKP_WPL_ID_AND_SID = " SELECT wm FROM SacmtWkpManager wm "
				+ " WHERE wm.workplaceId = :workplaceId "
				+ " AND wm.employeeId = :sid "
				+ " ORDER BY wm.startDate ASC ";

		return this.queryProxy().query(FIND_BY_WKP_WPL_ID_AND_SID,SacmtWkpManager.class)
				.setParameter("workplaceId", workplaceId)
				.setParameter("sid", sid)
				.getList(SacmtWkpManager::toDomain);
	}

	@Override
	public List<WorkplaceManager> getWkpManagerListBySid(String sid) {
		String FIND_BY_SID = " SELECT wm FROM SacmtWkpManager wm "
				+ " WHERE wm.employeeId = :sid ";
		return this.queryProxy().query(FIND_BY_SID,SacmtWkpManager.class)
				.setParameter("sid",sid)
				.getList(SacmtWkpManager::toDomain);
	}

	@Override
	public void deleteByWorkplaceIdAndSid(String workplaceId, String sid) {
		String DEL_BY_WKP_WPL_ID_AND_SID =
				 " DELETE  FROM SacmtWkpManager wm "
				+" WHERE wm.workplaceId = :workplaceId  "
				+" AND wm.employeeId = :sid ";
		this.getEntityManager().createQuery(DEL_BY_WKP_WPL_ID_AND_SID)
				.setParameter("workplaceId", workplaceId)
				.setParameter("sid", sid)
				.executeUpdate();
		this.getEntityManager().flush();
	}

}
