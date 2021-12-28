package nts.uk.ctx.workflow.infra.repository.resultrecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.gul.util.value.MutableValue;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalForm;
import nts.uk.ctx.workflow.dom.resultrecord.AppApproveInsTmp;
import nts.uk.ctx.workflow.dom.resultrecord.AppFrameInsTmp;
import nts.uk.ctx.workflow.dom.resultrecord.AppFrameInstance;
import nts.uk.ctx.workflow.dom.resultrecord.AppPhaseInsTmp;
import nts.uk.ctx.workflow.dom.resultrecord.AppPhaseInstance;
import nts.uk.ctx.workflow.dom.resultrecord.AppRootInstance;
import nts.uk.ctx.workflow.dom.resultrecord.AppRootInstanceRepository;
import nts.uk.ctx.workflow.dom.resultrecord.RecordRootType;
import nts.uk.ctx.workflow.infra.entity.resultrecord.FullJoinAppRootInstance;
import nts.uk.ctx.workflow.infra.entity.resultrecord.WwfdpAppApproveInstancePK;
import nts.uk.ctx.workflow.infra.entity.resultrecord.WwfdpAppFrameInstancePK;
import nts.uk.ctx.workflow.infra.entity.resultrecord.WwfdpAppPhaseInstancePK;
import nts.uk.ctx.workflow.infra.entity.resultrecord.WwfdtInstApprove;
import nts.uk.ctx.workflow.infra.entity.resultrecord.WwfdtInstFrame;
import nts.uk.ctx.workflow.infra.entity.resultrecord.WwfdtInstPhase;
import nts.uk.ctx.workflow.infra.entity.resultrecord.WwfdtInstRoute;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.jdbc.JDBCUtil;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class JpaAppRootInstanceRepository extends JpaRepository implements AppRootInstanceRepository {

	private final String BASIC_SELECT =
			"SELECT appRoot.ROOT_ID, appRoot.CID, appRoot.EMPLOYEE_ID, appRoot.START_DATE, appRoot.END_DATE, appRoot.ROOT_TYPE, "
					+ "phase.PHASE_ORDER, phase.APPROVAL_FORM, frame.FRAME_ORDER, frame.CONFIRM_ATR, appApprover.APPROVER_CHILD_ID "
					+ "FROM WWFDT_INST_ROUTE appRoot " + "LEFT JOIN WWFDT_INST_PHASE phase "
					+ "ON appRoot.ROOT_ID = phase.ROOT_ID " + "LEFT JOIN WWFDT_INST_FRAME frame "
					+ "ON phase.ROOT_ID = frame.ROOT_ID " + "AND phase.PHASE_ORDER = frame.PHASE_ORDER "
					+ "LEFT JOIN WWFDT_INST_APPROVE appApprover " + "ON frame.ROOT_ID = appApprover.ROOT_ID "
					+ "AND frame.PHASE_ORDER = appApprover.PHASE_ORDER "
					+ "AND frame.FRAME_ORDER = appApprover.FRAME_ORDER";

	private final String FIND_BY_ID = BASIC_SELECT + " WHERE appRoot.ROOT_ID = 'rootID'";
	
	private final String FIND_BY_EMP_DATE = BASIC_SELECT + " WHERE appRoot.EMPLOYEE_ID = 'employeeID'"
			+ " AND appRoot.CID = 'companyID'" + " AND appRoot.ROOT_TYPE = rootType"
			+ " AND appRoot.START_DATE <= 'recordDate'";

	private final String FIND_BY_EMP_FROM_DATE = BASIC_SELECT + " WHERE appRoot.EMPLOYEE_ID = 'employeeID'"
			+ " AND appRoot.CID = 'companyID'" + " AND appRoot.ROOT_TYPE = rootType"
			+ " AND appRoot.START_DATE >= 'recordDate'";

	private final String FIND_ROOT_BY_EMP_FROM_DATE = "SELECT appRoot.ROOT_ID"
			+ " FROM WWFDT_INST_ROUTE appRoot " + " WHERE appRoot.CID = 'companyID'"
			+ " AND appRoot.EMPLOYEE_ID = 'employeeID'" + " AND appRoot.ROOT_TYPE = rootType"
			+ " AND appRoot.START_DATE >= 'recordDate'";
	
	private final String FIND_BY_EMP_DATE_NEWEST = BASIC_SELECT + " WHERE appRoot.ROOT_ID IN ("
			+ " SELECT ROOT_ID FROM WWFDT_INST_ROUTE" + " WHERE EMPLOYEE_ID = 'employeeID'"
			+ " AND CID = 'companyID'" + " AND ROOT_TYPE = rootType " + "order by START_DATE desc)";

	private final String FIND_BY_EMP_DATE_NEWEST_BELOW =  
			"SELECT appRoot.ROOT_ID, appRoot.CID, appRoot.EMPLOYEE_ID, appRoot.START_DATE, appRoot.END_DATE, appRoot.ROOT_TYPE "
			+ "FROM WWFDT_INST_ROUTE appRoot "
			+ " WHERE appRoot.ROOT_ID IN ("
			+ " SELECT ROOT_ID FROM WWFDT_INST_ROUTE" + " WHERE CID = 'companyID' AND EMPLOYEE_ID = 'employeeID'"
			+ " AND ROOT_TYPE = rootType " + " AND START_DATE < 'recordDate' order by START_DATE desc)";
	
	private final String FIND_PHASE_BY_ID = "select ROOT_ID, PHASE_ORDER, APPROVAL_FORM from WWFDT_INST_PHASE where ROOT_ID = @rootID";
	
	private final String FIND_FRAME_BY_ID = "select ROOT_ID, PHASE_ORDER, FRAME_ORDER, CONFIRM_ATR from WWFDT_INST_FRAME where ROOT_ID = @rootID";
	
	private final String FIND_APPROVE_BY_ID = "select ROOT_ID, PHASE_ORDER, FRAME_ORDER, APPROVER_CHILD_ID from WWFDT_INST_APPROVE where ROOT_ID = @rootID";
	
	private final String FIND_ID_BY_EMP_DATE_NEWEST_BELOW = " SELECT ROOT_ID, START_DATE, END_DATE FROM WWFDT_INST_ROUTE"
			+ " WHERE CID = 'companyID' AND EMPLOYEE_ID = 'employeeID'"
			+ " AND ROOT_TYPE = rootType " + " AND START_DATE < 'recordDate' order by START_DATE desc";
	
	private final String UPDATE_END_BY_ID = "update WWFDT_INST_ROUTE set END_DATE = @endDate where ROOT_ID = @rootID";

	private final String FIND_BY_EMPS_PERIOD = BASIC_SELECT + " WHERE appRoot.EMPLOYEE_ID IN (employeeIDLst)"
			+ " AND appRoot.CID = 'companyID'" + " AND appRoot.ROOT_TYPE = rootType"
			+ " AND appRoot.END_DATE >= 'startDate'" + " AND appRoot.START_DATE <= 'endDate'";

	private final String FIND_BY_APPROVER_PERIOD = BASIC_SELECT + " WHERE appRoot.ROOT_ID IN (SELECT ROOT_ID FROM ("
			+ BASIC_SELECT + " WHERE appApprover.APPROVER_CHILD_ID = 'approverID'" + " AND appRoot.CID = 'companyID'"
			+ " AND appRoot.ROOT_TYPE = rootType" + " AND appRoot.END_DATE >= 'startDate'"
			+ " AND appRoot.START_DATE <= 'endDate') result)";

	private final String FIND_BY_CONTAIN_DATE = BASIC_SELECT + " WHERE appRoot.CID = 'companyID'"
			+ " AND appRoot.EMPLOYEE_ID = 'employeeID'" + " AND appRoot.ROOT_TYPE = rootType"
			+ " AND appRoot.START_DATE <= 'recordDate'" + " AND appRoot.END_DATE >= 'recordDate'";
	
	private final String FIND_ROOT_ID_BY_CONTAIN_DATE = 
			"SELECT appRoot.ROOT_ID, appRoot.CID, appRoot.EMPLOYEE_ID, appRoot.START_DATE, appRoot.END_DATE, appRoot.ROOT_TYPE "
			+ "FROM WWFDT_INST_ROUTE appRoot "
			+ " WHERE appRoot.CID = 'companyID'"
			+ " AND appRoot.EMPLOYEE_ID = 'employeeID'" + " AND appRoot.ROOT_TYPE = rootType"
			+ " AND appRoot.START_DATE <= 'recordDate'" + " AND appRoot.END_DATE >= 'recordDate'";

	private final String FIND_EMP_RQ610 = BASIC_SELECT + " WHERE appApprover.APPROVER_CHILD_ID = 'approverID'"
			+ " AND appRoot.CID = 'companyID'" + " AND appRoot.ROOT_TYPE = rootType"
			+ " AND appRoot.END_DATE >= 'startDate'" + " AND appRoot.START_DATE <= 'endDate'" + " UNION " + BASIC_SELECT
			+ " WHERE appApprover.APPROVER_CHILD_ID IN"
			+ " (SELECT c.SID FROM WWFMT_AGENT c where c.AGENT_SID1 = 'approverID' and c.START_DATE <= 'sysDate' and c.END_DATE >= 'sysDate')"
			+ " AND appRoot.CID = 'companyID'" + " AND appRoot.ROOT_TYPE = rootType"
			+ " AND appRoot.END_DATE >= 'startDate'" + " AND appRoot.START_DATE <= 'endDate'";
			
	@Override
	@SneakyThrows
	public Optional<AppRootInstance> findByID(String rootID) {
		String query = FIND_BY_ID;
		query = query.replaceAll("rootID", rootID);
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstance(new NtsResultSet(pstatement.executeQuery())));
			if (CollectionUtil.isEmpty(listResult)) {
				return Optional.empty();
			} else {
				return Optional.of(listResult.get(0));
			}
		}

	}

	@Override
	public void insert(AppRootInstance appRootInstance) {
		this.commandProxy().insert(fromDomain(appRootInstance));
		this.getEntityManager().flush();
	}

	@Override
	public void update(AppRootInstance appRootInstance) {
		this.commandProxy().update(fromDomain(appRootInstance));
		this.getEntityManager().flush();
	}

	@Override
	public void delete(AppRootInstance appRootInstance) {
		this.commandProxy().remove(WwfdtInstRoute.class, appRootInstance.getRootID());
		this.getEntityManager().flush();
	}
	
	@Override
	public void deleteByIDLst(List<String> rootIDLst) {
		this.commandProxy().removeAll(WwfdtInstRoute.class, rootIDLst);
		this.getEntityManager().flush();
	}

	private WwfdtInstRoute fromDomain(AppRootInstance appRootInstance) {
		return new WwfdtInstRoute(appRootInstance.getRootID(), appRootInstance.getCompanyID(),
				appRootInstance.getEmployeeID(), appRootInstance.getDatePeriod().start(),
				appRootInstance.getDatePeriod().end(), appRootInstance.getRootType().value,
				appRootInstance.getListAppPhase().stream()
						.map(x -> new WwfdtInstPhase(new WwfdpAppPhaseInstancePK(appRootInstance.getRootID(), x
								.getPhaseOrder()), x.getApprovalForm().value,
								null,
								x.getListAppFrame().stream()
										.map(y -> new WwfdtInstFrame(
												new WwfdpAppFrameInstancePK(appRootInstance.getRootID(),
														x.getPhaseOrder(), y.getFrameOrder()),
												y.isConfirmAtr(), null,
												y.getListApprover().stream().map(z -> new WwfdtInstApprove(
														new WwfdpAppApproveInstancePK(appRootInstance.getRootID(),
																x.getPhaseOrder(), y.getFrameOrder(), z),
														null)).collect(Collectors.toList())))
										.collect(Collectors.toList())))
						.collect(Collectors.toList()));
	}

	private List<AppRootInstance> toDomain(List<FullJoinAppRootInstance> listFullJoin) {
		return listFullJoin.stream().collect(Collectors.groupingBy(FullJoinAppRootInstance::getRootID)).entrySet()
				.stream().map(x -> {
					FullJoinAppRootInstance first = x.getValue().get(0);
					String companyID = first.getCompanyID();
					String rootID = first.getRootID();
					GeneralDate startDate = first.getStartDate();
					GeneralDate endDate = first.getEndDate();
					RecordRootType rootType = EnumAdaptor.valueOf(first.getRootType(), RecordRootType.class);
					String employeeID = first.getEmployeeID();
					List<AppPhaseInstance> listAppPhase = x.getValue().stream()
							.collect(Collectors.groupingBy(FullJoinAppRootInstance::getPhaseOrder)).entrySet().stream()
							.map(y -> {
								Integer phaseOrder = y.getValue().get(0).getPhaseOrder();
								ApprovalForm approvalForm = EnumAdaptor.valueOf(y.getValue().get(0).getApprovalForm(),
										ApprovalForm.class);
								List<AppFrameInstance> listAppFrame = y.getValue().stream()
										.collect(Collectors.groupingBy(FullJoinAppRootInstance::getFrameOrder))
										.entrySet().stream().map(z -> {
											Integer frameOrder = z.getValue().get(0).getFrameOrder();
											Boolean confirmAtr = z.getValue().get(0).getConfirmAtr() == 1 ? true
													: false;
											List<String> approvalIDLst = z.getValue().stream()
													.map(t -> t.getApproverChildID()).collect(Collectors.toList());
											return new AppFrameInstance(frameOrder, confirmAtr, approvalIDLst);
										}).collect(Collectors.toList());
								return new AppPhaseInstance(phaseOrder, approvalForm, listAppFrame);
							}).collect(Collectors.toList());
					return new AppRootInstance(rootID, companyID, employeeID, new DatePeriod(startDate, endDate),
							rootType, listAppPhase);
				}).collect(Collectors.toList());
	}

	@SneakyThrows
	private List<FullJoinAppRootInstance> createFullJoinAppRootInstance(NtsResultSet nrs) {
		List<FullJoinAppRootInstance> listFullData = 
				nrs.getList(rs -> new FullJoinAppRootInstance(
							rs.getString("ROOT_ID"),
							rs.getString("CID"),
							rs.getString("EMPLOYEE_ID"),
							rs.getGeneralDate("START_DATE"),
							rs.getGeneralDate("END_DATE"),
							Integer.valueOf(rs.getString("ROOT_TYPE")), Integer.valueOf(rs.getString("PHASE_ORDER")),
							Integer.valueOf(rs.getString("APPROVAL_FORM")), Integer.valueOf(rs.getString("FRAME_ORDER")),
							rs.getBoolean("CONFIRM_ATR") ? 1 : 0, rs.getString("APPROVER_CHILD_ID")));


		return listFullData;
	}



	@Override
	@SneakyThrows
	public Optional<AppRootInstance> findByEmpDate(String companyID, String employeeID, GeneralDate recordDate,
			RecordRootType rootType) {
		String query = FIND_BY_EMP_DATE;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("employeeID", employeeID);
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		query = query.replaceAll("recordDate", recordDate.toString("yyyy-MM-dd"));
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstance(new NtsResultSet(pstatement.executeQuery())));
			if (CollectionUtil.isEmpty(listResult)) {
				return Optional.empty();
			} else {
				return Optional.of(listResult.get(0));
			}
		}
	}

	@Override
	@SneakyThrows
	public Optional<AppRootInstance> findByEmpDateNewest(String companyID, String employeeID, RecordRootType rootType) {
		Connection con = this.getEntityManager().unwrap(Connection.class);
		String query = FIND_BY_EMP_DATE_NEWEST;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("employeeID", employeeID);
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstance(new NtsResultSet(pstatement.executeQuery())));
			return listResult.stream().findFirst();
		}
	}

	@Override
	@SneakyThrows
	public List<AppRootInstance> findByEmpLstPeriod(List<String> employeeIDLst, DatePeriod period,
			RecordRootType rootType) {
		// Đối ứng SPR
		String companyID = "000000000000-0001";
		String loginCompanyID = AppContexts.user().companyId();
		if (Strings.isNotBlank(loginCompanyID)) {
			companyID = loginCompanyID;
		}

		String query = FIND_BY_EMPS_PERIOD;

		String employeeIDLstParam = "";
		if (CollectionUtil.isEmpty(employeeIDLst)) {
			employeeIDLstParam = "''";
		} else {
			for (int i = 0; i < employeeIDLst.size(); i++) {
				employeeIDLstParam += "'" + employeeIDLst.get(i) + "'";
				if (i < employeeIDLst.size() - 1) {
					employeeIDLstParam += ",";
				}
			}
		}
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("employeeIDLst", employeeIDLstParam);
		query = query.replaceAll("startDate", period.start().toString("yyyy-MM-dd"));
		query = query.replaceAll("endDate", period.end().toString("yyyy-MM-dd"));
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstance(new NtsResultSet(pstatement.executeQuery())));
			if (!CollectionUtil.isEmpty(listResult)) {
				return listResult;
			} else {
				return Collections.emptyList();
			}
		}
	}

	@Override
	public List<AppRootInstance> findByEmpLstPeriod(String compID, List<String> employeeIDLst, DatePeriod period,
			RecordRootType rootType) {
		if (employeeIDLst.isEmpty()) {
			return new ArrayList<>();
		}
		List<AppRootInstance> result = new ArrayList<>();
		MutableValue<Exception> exception = new MutableValue<>(null);

		CollectionUtil.split(employeeIDLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, c -> {
			if (exception.optional().isPresent()) {
				return;
			}
			try {
				StringBuilder sql = new StringBuilder();
				sql.append(
						"SELECT appRoot.ROOT_ID, appRoot.CID, appRoot.EMPLOYEE_ID, appRoot.START_DATE, appRoot.END_DATE, appRoot.ROOT_TYPE, ");
				sql.append(
						" frame.PHASE_ORDER, phase.APPROVAL_FORM, frame.FRAME_ORDER, frame.CONFIRM_ATR, a.APPROVER_CHILD_ID  ");
				sql.append(
						" FROM WWFDT_INST_ROUTE appRoot LEFT JOIN WWFDT_INST_PHASE phase ON appRoot.ROOT_ID = phase.ROOT_ID ");
				sql.append(
						" LEFT JOIN WWFDT_INST_FRAME frame ON phase.ROOT_ID = frame.ROOT_ID AND phase.PHASE_ORDER = frame.PHASE_ORDER ");
				sql.append(
						" LEFT JOIN WWFDT_INST_APPROVE a ON frame.ROOT_ID = a.ROOT_ID AND frame.PHASE_ORDER = a.PHASE_ORDER AND frame.FRAME_ORDER = a.FRAME_ORDER ");
				sql.append(
						" WHERE appRoot.CID = ? AND appRoot.ROOT_TYPE = ? AND appRoot.END_DATE >= ? AND appRoot.START_DATE <= ? ");
				sql.append(" AND appRoot.EMPLOYEE_ID IN ( ");
				sql.append(joinParam(c));
				sql.append(" ) ");

				try (PreparedStatement statement = this.connection().prepareStatement(sql.toString())) {
					statement.setString(1, compID);
					statement.setInt(2, rootType.value);
					statement.setDate(3, Date.valueOf(period.start().localDate()));
					statement.setDate(4, Date.valueOf(period.end().localDate()));
					for (int i = 0; i < employeeIDLst.size(); i++) {
						statement.setString(i + 5, employeeIDLst.get(i));
					}
					result.addAll(toDomain(new NtsResultSet(statement.executeQuery())
							.getList(rs -> createFullJoinAppRootInstance(rs, compID, rootType.value))));
				}
			} catch (SQLException e) {
				exception.set(e);
				throw new RuntimeException(e);
			}
		});

		if (exception.optional().isPresent()) {
			throw new RuntimeException(exception.get());
		}

		return result;
	}

	private String joinParam(List<String> employeeIDLst) {
		return employeeIDLst.stream().map(x -> "?").collect(Collectors.joining(","));
	}

	private FullJoinAppRootInstance createFullJoinAppRootInstance(NtsResultRecord rs, String comID, int rootType) {
		return new FullJoinAppRootInstance(rs.getString("ROOT_ID"), comID, rs.getString("EMPLOYEE_ID"),
				rs.getGeneralDate("START_DATE"), rs.getGeneralDate("END_DATE"), rootType, rs.getInt("PHASE_ORDER"),
				rs.getInt("APPROVAL_FORM"), rs.getInt("FRAME_ORDER"), rs.getInt("CONFIRM_ATR"),
				rs.getString("APPROVER_CHILD_ID"));
	}

	@Override
	@SneakyThrows
	public List<AppRootInstance> findByApproverPeriod(String approverID, DatePeriod period, RecordRootType rootType) {
		String companyID = AppContexts.user().companyId();
		String query = BASIC_SELECT + 
				" WHERE appRoot.ROOT_ID IN (SELECT ROOT_ID FROM (" + BASIC_SELECT 
				+ " WHERE appApprover.APPROVER_CHILD_ID = ?" 
				+ " AND appRoot.START_DATE <= ?" 
				+ " AND appRoot.END_DATE >= ?"
				+ " AND appRoot.CID = ?"
				+ " AND appRoot.ROOT_TYPE = ?) result)";
		
			Query pstatement = this.getEntityManager().createNativeQuery(query);
			pstatement.setParameter(1, approverID);
			pstatement.setParameter(2, Date.valueOf(period.end().localDate()));
			pstatement.setParameter(3, Date.valueOf(period.start().localDate()));
			pstatement.setParameter(4, companyID);
			pstatement.setParameter(5, rootType.value);
			
		
			@SuppressWarnings("unchecked")
			List<Object[]> rs = pstatement.getResultList();
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstanceResponse(rs));
			
			if (CollectionUtil.isEmpty(listResult)) {
				return Collections.emptyList();
			}
			
			return listResult;
	}
	
	@SneakyThrows
	private List<FullJoinAppRootInstance> createFullJoinAppRootInstanceResponse(List<Object[]> rs) {
		List<FullJoinAppRootInstance> listFullData = new ArrayList<>();
		listFullData.addAll(rs.stream().parallel().map(mapper ->
			new FullJoinAppRootInstance(
					String.valueOf(mapper[0]),
					String.valueOf(mapper[1]),
					String.valueOf(mapper[2]),
					GeneralDate.localDate(((Timestamp)mapper[3]).toLocalDateTime().toLocalDate()),
					GeneralDate.localDate(((Timestamp)mapper[4]).toLocalDateTime().toLocalDate()),
					Integer.valueOf(String.valueOf(mapper[5])),
					Integer.valueOf(String.valueOf(mapper[6])),
					Integer.valueOf(String.valueOf(mapper[7])),
					Integer.valueOf(String.valueOf(mapper[8])),
					String.valueOf(mapper[9]).length() > 1 ? BooleanUtils.toIntegerObject(Boolean.valueOf(String.valueOf(mapper[9]))) : Integer.valueOf(String.valueOf(mapper[9])),
					String.valueOf(mapper[10])))
				.collect(Collectors.toList()));
		return listFullData;
	}

	public List<AppRootInstance> findByApproverEmployeePeriod(String companyId, String approverID,
			List<String> employeeIDs, DatePeriod period, RecordRootType rootType) {

		List<String> rootIds = NtsStatement.In.split(employeeIDs, subEmpIds -> {

			String sql = BASIC_SELECT + " where appRoot.CID = ?" + " and appRoot.ROOT_TYPE = ?"
					+ " and appRoot.EMPLOYEE_ID in (" + NtsStatement.In.createParamsString(subEmpIds) + ")"
					+ " and appRoot.START_DATE <= ?" + " and appRoot.END_DATE >= ?"
					+ " and appApprover.APPROVER_CHILD_ID = ?";

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, companyId);
				stmt.setInt(2, rootType.value);
				for (int i = 0; i < subEmpIds.size(); i++) {
					stmt.setString(3 + i, subEmpIds.get(i));
				}
				stmt.setDate(3 + subEmpIds.size(), Date.valueOf(period.end().localDate()));
				stmt.setDate(4 + subEmpIds.size(), Date.valueOf(period.start().localDate()));
				stmt.setString(5 + subEmpIds.size(), approverID);

				return new NtsResultSet(stmt.executeQuery()).getList(rs -> rs.getString("ROOT_ID"));

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		});

		return NtsStatement.In.split(rootIds, subRootIds -> {

			String sql = BASIC_SELECT + " where appRoot.ROOT_ID in (" + NtsStatement.In.createParamsString(subRootIds)
					+ ")";

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				for (int i = 0; i < subRootIds.size(); i++) {
					stmt.setString(1 + i, subRootIds.get(i));
				}

				return toDomain(createFullJoinAppRootInstance(new NtsResultSet(stmt.executeQuery())));

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		});
	}

	@Override
	@SneakyThrows
	public List<AppRootInstance> findByEmpFromDate(String companyID, String employeeID, GeneralDate recordDate,
			RecordRootType rootType) {
		String query = FIND_BY_EMP_FROM_DATE;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("employeeID", employeeID);
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		query = query.replaceAll("recordDate", recordDate.toString("yyyy-MM-dd"));
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstance(new NtsResultSet(pstatement.executeQuery())));
			if (CollectionUtil.isEmpty(listResult)) {
				return Collections.emptyList();
			} else {
				return listResult;
			}
		}
	}
	
	@Override
	@SneakyThrows
	public List<String> findRootByEmpFromDate(String companyID, String employeeID, GeneralDate recordDate,
			RecordRootType rootType) {
		String query = FIND_ROOT_BY_EMP_FROM_DATE;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("employeeID", employeeID);
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		query = query.replaceAll("recordDate", recordDate.toString("yyyy-MM-dd"));
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			return new NtsResultSet(pstatement.executeQuery()).getList(x -> x.getString("ROOT_ID"));
		}
	}

	@Override
	@SneakyThrows
	public Optional<AppRootInstance> findByEmpDateNewestBelow(String companyID, String employeeID,
			GeneralDate recordDate, RecordRootType rootType) {
		String query = FIND_BY_EMP_DATE_NEWEST_BELOW;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("employeeID", employeeID);
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		query = query.replaceAll("recordDate", recordDate.toString("yyyy-MM-dd"));
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstance(new NtsResultSet(pstatement.executeQuery())));
			return listResult.stream().findFirst();
		}
	}
	
	@Override
	@SneakyThrows
	public Pair<String, DatePeriod> findIDByEmpDateNewestBelow(String companyID, String employeeID,
			GeneralDate recordDate, RecordRootType rootType) {
		String query = FIND_ID_BY_EMP_DATE_NEWEST_BELOW;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("employeeID", employeeID);
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		query = query.replaceAll("recordDate", recordDate.toString("yyyy-MM-dd"));
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			val listResult = new NtsResultSet(pstatement.executeQuery())
					.getList(x -> Pair.of(x.getString("ROOT_ID"), new DatePeriod(x.getGeneralDate("START_DATE"), x.getGeneralDate("END_DATE"))))
					.stream().findFirst();
			if(listResult.isPresent()){
				return listResult.get();
			}else{
				return Pair.of("", new DatePeriod(GeneralDate.fromString("1900/01/01", "yyyy/MM/dd"), GeneralDate.fromString("1900/01/01", "yyyy/MM/dd")));
			}
		}
	}
	
	@Override
	@SneakyThrows
	public void updateEndByID(String rootID, GeneralDate endDate) {
		new NtsStatement(JDBCUtil.toUpdateWithCommonField(UPDATE_END_BY_ID), this.jdbcProxy())
		.paramDate("endDate", endDate)
		.paramString("rootID", rootID)
		.execute();
	}

	@Override
	@SneakyThrows
	public Optional<AppRootInstance> findByContainDate(
			String companyID,
			String employeeID,
			GeneralDate recordDate,
			RecordRootType rootType) {
		String query = FIND_BY_CONTAIN_DATE;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("employeeID", employeeID);
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		query = query.replaceAll("recordDate", recordDate.toString("yyyy-MM-dd"));
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstance(new NtsResultSet(pstatement.executeQuery())));
			if (CollectionUtil.isEmpty(listResult)) {
				return Optional.empty();
			} else {
				return Optional.of(listResult.get(0));
			}
		}
	}
	
	@Override
	@SneakyThrows
	public String findIDByContainDate(
			String companyID,
			String employeeID,
			GeneralDate recordDate,
			RecordRootType rootType) {
		String query = FIND_ROOT_ID_BY_CONTAIN_DATE;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("employeeID", employeeID);
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		query = query.replaceAll("recordDate", recordDate.toString("yyyy-MM-dd"));
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			return new NtsResultSet(pstatement.executeQuery()).getSingle(x -> x.getString("ROOT_ID")).orElse(null);
		}
	}

	@Override
	@SneakyThrows
	public List<AppRootInstance> findByApproverDateCID(
			String companyID,
			String approverID,
			GeneralDate date,
			RecordRootType rootType) {
		String query = FIND_BY_APPROVER_PERIOD;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("approverID", approverID);
		query = query.replaceAll("startDate", date.toString("yyyy-MM-dd"));
		query = query.replaceAll("endDate", date.toString("yyyy-MM-dd"));
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		
		
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstance(new NtsResultSet(pstatement.executeQuery())));
			if (!CollectionUtil.isEmpty(listResult)) {
				return listResult;
			} else {
				return Collections.emptyList();
			}
		}
	}

	@Override
	@SneakyThrows
	public List<String> findEmpLstByRq610(
			String approverID,
			DatePeriod period,
			RecordRootType rootType) {
		String companyID = AppContexts.user().companyId();
		String query = FIND_EMP_RQ610;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("approverID", approverID);
		query = query.replaceAll("startDate", period.start().toString("yyyy-MM-dd"));
		query = query.replaceAll("endDate", period.end().toString("yyyy-MM-dd"));
		query = query.replaceAll("sysDate", GeneralDate.today().toString("yyyy-MM-dd"));
		query = query.replaceAll("rootType", String.valueOf(rootType.value));
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			
			List<AppRootInstance> listResult = toDomain(createFullJoinAppRootInstance(new NtsResultSet(pstatement.executeQuery())));
			if (!CollectionUtil.isEmpty(listResult)) {
				return listResult.stream().map(x -> x.getEmployeeID()).distinct().collect(Collectors.toList());
			} else {
				return Collections.emptyList();
			}
		}
	}

	@Override
	public List<AppPhaseInsTmp> findPhaseTmpByID(String rootID) {
		return new NtsStatement(FIND_PHASE_BY_ID, this.jdbcProxy())
				.paramString("rootID", rootID)
				.getList(rec -> new AppPhaseInsTmp(
						rec.getString("ROOT_ID"), 
						rec.getInt("PHASE_ORDER"), 
						rec.getInt("APPROVAL_FORM")));
	}

	@Override
	public List<AppFrameInsTmp> findFrameTmpByID(String rootID) {
		return new NtsStatement(FIND_FRAME_BY_ID, this.jdbcProxy())
				.paramString("rootID", rootID)
				.getList(rec -> new AppFrameInsTmp(
						rec.getString("ROOT_ID"), 
						rec.getInt("PHASE_ORDER"), 
						rec.getInt("FRAME_ORDER"),
						rec.getBoolean("CONFIRM_ATR") ? 1 : 0));
	}

	@Override
	public List<AppApproveInsTmp> findApproverTmpByID(String rootID) {
		return new NtsStatement(FIND_APPROVE_BY_ID, this.jdbcProxy())
				.paramString("rootID", rootID)
				.getList(rec -> new AppApproveInsTmp(
						rec.getString("ROOT_ID"), 
						rec.getInt("PHASE_ORDER"), 
						rec.getInt("FRAME_ORDER"),
						rec.getString("APPROVER_CHILD_ID"))); 
	}

}
