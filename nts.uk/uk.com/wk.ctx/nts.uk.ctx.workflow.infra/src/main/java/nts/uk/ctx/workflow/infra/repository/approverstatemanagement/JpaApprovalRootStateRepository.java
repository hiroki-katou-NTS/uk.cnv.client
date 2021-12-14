package nts.uk.ctx.workflow.infra.repository.approverstatemanagement;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.OptimisticLockException;

import org.apache.logging.log4j.util.Strings;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalBehaviorAtr;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalFrame;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalPhaseState;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalRootState;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalRootStateRepository;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApproverInfor;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.application.WwfdpApprovalPhaseStatePK;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.application.WwfdpApproverStatePK;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.application.WwfdtAppInstApprover;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.application.WwfdtAppInstRoute;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.application.WwfdtAppRootStateSimple;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.application.WwfdtApprovalPhaseState;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.application.WwfdtFullJoinState;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.confirmday.WwfdpApprovalRootDayPK;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.confirmday.WwfdtAppRootDaySimple;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.confirmday.WwfdtApprovalRootDay;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.confirmmonth.WwfdpApprovalRootMonthPK;
import nts.uk.ctx.workflow.infra.entity.approverstatemanagement.confirmmonth.WwfdtApprovalRootMonth;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaApprovalRootStateRepository extends JpaRepository implements ApprovalRootStateRepository {

	private static final String BASIC_SELECT;

	private static final String SELECT_APP_BY_ID;

	private static final String SELECT_APP_BY_DATE;
	private static final String SELECT_CF_DAY_BY_DATE;
	private static final String SELECT_CF_MONTH_BY_DATE;

	private static final String SELECT_SIMPLE_APP_BY_DATE;

	private static final String SELECT_APP_BY_EMP_DATE;
	private static final String SELECT_CF_DAY_BY_EMP_DATE;
	private static final String SELECT_CF_MONTH_BY_EMP_DATE;

	private static final String SELECT_CF_DAY_BY_EMP_DATE_SP;

	private static final String SELECT_DAY_BY_LIST_EMP_DATE;
	private static final String SELECT_MONTH_BY_LIST_EMP_DATE;
	private static final String SELECT_BY_LIST_EMP_DATE;

	private static final String SELECT_APPS_BY_EMP_AND_DATES;
	private static final String SELECT_CFS_DAY_BY_EMP_AND_DATES;
	private static final String SELECT_CFS_MONTH_BY_EMP_AND_DATES;

	private static final String SELECT_APPS_BY_APPROVER;
	private static final String SELECT_CFS_DAY_BY_APPROVER;
	private static final String SELECT_CFS_MONTH_BY_APPROVER;
	private static final String FIND_PHASE_APPROVAL_MAX = "SELECT a FROM WwfdtApprovalPhaseState a"
			+ " WHERE a.wwfdpApprovalPhaseStatePK.rootStateID = :appID"
			+ " AND a.approvalAtr = 1 ORDER BY a.wwfdpApprovalPhaseStatePK.phaseOrder ASC";
	static {
		StringBuilder builderString = new StringBuilder();
		
		builderString.append("SELECT root.ROOT_STATE_ID, root.EMPLOYEE_ID, root.APPROVAL_RECORD_DATE, ");
		builderString.append("phase.PHASE_ORDER, phase.APP_PHASE_ATR, phase.APPROVAL_FORM, ");
		builderString.append("approver.APPROVER_ORDER, approver.APPROVER_ID, approver.APPROVAL_ATR, approver.CONFIRM_ATR, ");
		builderString.append("approver.AGENT_ID, approver.APPROVAL_DATE, approver.APPROVAL_REASON, approver.APP_DATE, approver.APPROVER_LIST_ORDER ");
		builderString.append("FROM WWFDT_APP_INST_ROUTE root ");
		builderString.append("LEFT JOIN WWFDT_APP_INST_PHASE phase ");
		builderString.append("ON root.ROOT_STATE_ID = phase.ROOT_STATE_ID "); 
		builderString.append("LEFT JOIN WWFDT_APP_INST_APPROVER approver ");
		builderString.append("ON phase.ROOT_STATE_ID = approver.ROOT_STATE_ID ");
		builderString.append("AND phase.PHASE_ORDER = approver.PHASE_ORDER");
		
		BASIC_SELECT = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtAppInstRoute e");
		builderString.append(" WHERE e.rootStateID = :rootStateID");
		SELECT_APP_BY_ID = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtAppInstRoute e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		SELECT_APP_BY_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtApprovalRootDay e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		SELECT_CF_DAY_BY_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtApprovalRootMonth e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		SELECT_CF_MONTH_BY_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtAppRootStateSimple e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		SELECT_SIMPLE_APP_BY_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtAppInstRoute e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		builderString.append(" AND e.employeeID = :employeeID");
		SELECT_APP_BY_EMP_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtApprovalRootDay e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		builderString.append(" AND e.employeeID = :employeeID");
		SELECT_CF_DAY_BY_EMP_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtApprovalRootMonth e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		builderString.append(" AND e.employeeID = :employeeID");
		SELECT_CF_MONTH_BY_EMP_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtAppRootDaySimple e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		builderString.append(" AND e.employeeID = :employeeID");
		SELECT_CF_DAY_BY_EMP_DATE_SP = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtApprovalRootDay e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		builderString.append(" AND e.employeeID IN :employeeID");
		SELECT_DAY_BY_LIST_EMP_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtApprovalRootMonth e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		builderString.append(" AND e.employeeID IN :employeeID");
		SELECT_MONTH_BY_LIST_EMP_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtAppInstRoute e");
		builderString.append(" WHERE e.recordDate >= :startDate");
		builderString.append(" AND e.recordDate <= :endDate");
		//builderString.append(" AND e.rootType = :rootType");
		builderString.append(" AND e.employeeID IN :employeeID");
		SELECT_BY_LIST_EMP_DATE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtAppInstRoute e");
		builderString.append(" WHERE e.recordDate IN :recordDate");
		builderString.append(" AND e.employeeID IN :employeeID");
		SELECT_APPS_BY_EMP_AND_DATES = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtApprovalRootDay e");
		builderString.append(" WHERE e.recordDate IN :recordDate");
		builderString.append(" AND e.employeeID IN :employeeID");
		SELECT_CFS_DAY_BY_EMP_AND_DATES = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT e");
		builderString.append(" FROM WwfdtApprovalRootMonth e");
		builderString.append(" WHERE e.recordDate IN :recordDate");
		builderString.append(" AND e.employeeID IN :employeeID");
		SELECT_CFS_MONTH_BY_EMP_AND_DATES = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT c");
		builderString.append(" FROM WwfdtAppInstRoute c");
		builderString.append(" WHERE c.rootStateID IN ");
		builderString.append("(SELECT DISTINCT a.wwfdpApprovalRootStatePK.rootStateID");
		builderString.append(" FROM WwfdtAppRootStateSimple a JOIN WwfdtAppStateSimple b ");
		builderString.append(" ON a.rootStateID = b.wwfdpApproverStatePK.rootStateID ");
		builderString.append(" WHERE (b.wwfdpApproverStatePK.approverID = :approverID");
		builderString.append(" OR b.wwfdpApproverStatePK.approverID IN");
		builderString.append(" (SELECT d.cmmmtAgentPK.employeeId FROM WwfmtAgent d WHERE d.agentSid1 = :approverID");
		builderString.append(" AND :systemDate <= d.endDate AND :systemDate >= d.startDate))");
		builderString.append(" AND b.companyID = :companyID");
		builderString.append(" AND b.recordDate >= :startDate AND b.recordDate <= :endDate)");
		SELECT_APPS_BY_APPROVER = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT c");
		builderString.append(" FROM WwfdtApprovalRootDay c");
		builderString.append(" WHERE c.wwfdpApprovalRootDayPK.rootStateID IN ");
		builderString.append("(SELECT DISTINCT a.wwfdpApprovalRootDayPK.rootStateID");
		builderString.append(" FROM WwfdtAppRootDaySimple a JOIN WwfdtApproverDaySimple b ");
		builderString.append(" ON a.wwfdpApprovalRootDayPK.rootStateID = b.wwfdpApproverDayPK.rootStateID ");
		builderString.append(" WHERE (b.wwfdpApproverDayPK.approverID = :approverID");
		builderString.append(" OR b.wwfdpApproverDayPK.approverID IN");
		builderString.append(" (SELECT d.cmmmtAgentPK.employeeId FROM WwfmtAgent d WHERE d.agentSid1 = :approverID");
		builderString.append(" AND :systemDate <= d.endDate AND :systemDate >= d.startDate))");
		builderString.append(" AND b.companyID = :companyID");
		builderString.append(" AND b.recordDate >= :startDate AND b.recordDate <= :endDate)");
		SELECT_CFS_DAY_BY_APPROVER = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT c");
		builderString.append(" FROM WwfdtApprovalRootMonth c");
		builderString.append(" WHERE c.wwfdpApprovalRootMonthPK.rootStateID IN ");
		builderString.append("(SELECT DISTINCT a.wwfdpApprovalRootMonthPK.rootStateID");
		builderString.append(" FROM WwfdtApprovalRootMonth a JOIN WwfdtApproverMonth b ");
		builderString.append(" ON a.wwfdpApprovalRootMonthPK.rootStateID = b.wwfdpApproverMonthPK.rootStateID ");
		builderString.append(" WHERE (b.wwfdpApproverMonthPK.approverID = :approverID");
		builderString.append(" OR b.wwfdpApproverMonthPK.approverID IN");
		builderString.append(" (SELECT d.cmmmtAgentPK.employeeId FROM WwfmtAgent d WHERE d.agentSid1 = :approverID");
		builderString.append(" AND :systemDate <= d.endDate AND :systemDate >= d.startDate))");
		builderString.append(" AND b.companyID = :companyID");
		builderString.append(" AND b.recordDate >= :startDate AND b.recordDate <= :endDate)");
		SELECT_CFS_MONTH_BY_APPROVER = builderString.toString();

	}

	@Override
	@SneakyThrows
	public Optional<ApprovalRootState> findByID(String rootStateID, Integer rootType) {
		String query = BASIC_SELECT + " WHERE root.ROOT_STATE_ID = 'rootStateID'";
		query = query.replaceFirst("rootStateID", rootStateID);
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			
			List<WwfdtFullJoinState> listFullData = WwfdtFullJoinState.fromResultSet(new NtsResultSet(pstatement.executeQuery()));
			List<ApprovalRootState> listAppRootState = WwfdtFullJoinState.toDomain(listFullData);
			if (CollectionUtil.isEmpty(listAppRootState)) {
				return Optional.empty();
			} else {
				return Optional.of(listAppRootState.get(0));
			}
		}
		
	}

	@Override
	public List<ApprovalRootState> findEmploymentApps(List<String> rootStateIDs, String approverID) {
		String companyID = AppContexts.user().companyId();
		List<ApprovalRootState> result = new ArrayList<>();
		CollectionUtil.split(rootStateIDs, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {

			internalQuery(approverID, companyID, result, subList);

		});
		return result;
	}

	@SneakyThrows
	private void internalQuery(String approverID, String companyID, List<ApprovalRootState> result,
			List<String> subList) {
		GeneralDate sysDate = GeneralDate.today();
		String rootStateIDLst = "";

		for (int i = 0; i < subList.size(); i++) {
			rootStateIDLst += "'" + subList.get(i) + "'";
			if (i != (subList.size() - 1)) {
				rootStateIDLst += ",";
			}
		}

		String query = "SELECT root.ROOT_STATE_ID, root.EMPLOYEE_ID, root.APPROVAL_RECORD_DATE, " +
				"phase.PHASE_ORDER, phase.APPROVAL_FORM, phase.APP_PHASE_ATR, approver.APPROVER_ORDER, " +
				"approver.APPROVER_ID, approver.APPROVAL_ATR, approver.CONFIRM_ATR, approver.AGENT_ID, " +
				"approver.APPROVAL_DATE, approver.APPROVAL_REASON, approver.APP_DATE, approver.APPROVER_LIST_ORDER " +
				"FROM WWFDT_APP_INST_ROUTE root " +
				"LEFT JOIN WWFDT_APP_INST_PHASE phase ON root.ROOT_STATE_ID = phase.ROOT_STATE_ID " +
				"LEFT JOIN WWFDT_APP_INST_APPROVER approver ON phase.ROOT_STATE_ID = approver.ROOT_STATE_ID AND phase.PHASE_ORDER = approver.PHASE_ORDER " +
				"WHERE root.ROOT_STATE_ID IN " +
				"( " +
				"	SELECT DISTINCT c.ROOT_STATE_ID FROM " +
				"		( " +
				"		SELECT a.ROOT_STATE_ID FROM WWFDT_APP_INST_APPROVER a WHERE a.APPROVER_ID = 'approverID' " +
				"		UNION ALL " +
				"		SELECT b.ROOT_STATE_ID FROM WWFDT_APP_INST_APPROVER b WHERE b.APPROVER_ID IN " +
				"		( SELECT c.SID FROM WWFMT_AGENT c where c.AGENT_SID1 = 'approverID' and c.START_DATE <= 'sysDate' and c.END_DATE >= 'sysDate') " +
				"	) c " +
				"	WHERE c.ROOT_STATE_ID IN (rootStateIDs) " +
				") " ;

		query = query.replaceAll("approverID", approverID);
		query = query.replaceFirst("rootStateIDs", rootStateIDLst);
		query = query.replaceAll("sysDate", sysDate.toString());
		List<WwfdtFullJoinState> listFullData = new ArrayList<>();
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			listFullData = WwfdtFullJoinState.fromResultSet(new NtsResultSet(pstatement.executeQuery()));
		}
		List<ApprovalRootState> entityRoot = WwfdtFullJoinState.toDomain(listFullData);
		result.addAll(entityRoot);
	}

	@Override
	public Optional<ApprovalRootState> findEmploymentApp(String rootStateID) {
		return this.queryProxy().query(SELECT_APP_BY_ID, WwfdtAppInstRoute.class)
				.setParameter("rootStateID", rootStateID).getSingle(x -> x.toDomain());
	}

	@Override
	public void insert(String companyID, ApprovalRootState approvalRootState, Integer rootType) {
		switch (rootType) {
		case 1:
			this.commandProxy().insert(WwfdtApprovalRootDay.fromDomain(companyID, approvalRootState));
			break;
		case 2:
			this.commandProxy().insert(WwfdtApprovalRootMonth.fromDomain(companyID, approvalRootState));
			break;
		default:
			this.commandProxy().insert(WwfdtAppInstRoute.fromDomain(approvalRootState));
		}
		this.getEntityManager().flush();
	}

	@Override
	public void update(ApprovalRootState root, Integer rootType) {
		try {
			WwfdtAppInstRoute wwfdtApprovalRootState = this.queryProxy()
					.find(root.getRootStateID(),WwfdtAppInstRoute.class).get();
			wwfdtApprovalRootState.listWwfdtPhase = root.getListApprovalPhaseState().stream()
					.map(x -> updateEntityWwfdtApprovalPhaseState(root.getRootStateID(), x)).collect(Collectors.toList());
			this.commandProxy().update(wwfdtApprovalRootState);
			this.getEntityManager().flush();
		} catch(OptimisticLockException ope) {
			ope.printStackTrace();
			throw new BusinessException("Msg_197");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void delete(String rootStateID, Integer rootType) {
		switch (rootType) {
		case 1:
			this.commandProxy().remove(WwfdtApprovalRootDay.class, new WwfdpApprovalRootDayPK(rootStateID));
			break;
		case 2:
			this.commandProxy().remove(WwfdtApprovalRootMonth.class, new WwfdpApprovalRootMonthPK(rootStateID));
			break;
		default:
			this.commandProxy().remove(WwfdtAppInstRoute.class, rootStateID);
		}
	}

	private WwfdtApprovalPhaseState updateEntityWwfdtApprovalPhaseState(String rootId, ApprovalPhaseState phase) {
		WwfdtApprovalPhaseState entityPhase = this.queryProxy().find(
				new WwfdpApprovalPhaseStatePK(rootId, phase.getPhaseOrder()), WwfdtApprovalPhaseState.class).get();
		entityPhase.approvalAtr = phase.getApprovalAtr().value;
		entityPhase.approvalForm = phase.getApprovalForm().value;
		List<WwfdtAppInstApprover> lstEntityApprover = new ArrayList<>();
		for (ApprovalFrame frame : phase.getListApprovalFrame()) {
			lstEntityApprover.addAll(this.updateEntityWwfdtApprovalFrame(rootId, phase.getPhaseOrder(), frame));
		}
		entityPhase.listWwfdtApprover = lstEntityApprover;
		return entityPhase;
	}

	private List<WwfdtAppInstApprover> updateEntityWwfdtApprovalFrame(String rootId, int phaseOrder, ApprovalFrame frame) {
		List<ApproverInfor> lstApprover = frame.getLstApproverInfo();
		List<WwfdtAppInstApprover> lstEntityApprover = new ArrayList<>();
		for (ApproverInfor approver : lstApprover) {
			WwfdtAppInstApprover entityApprover = this.queryProxy()
					.find(new WwfdpApproverStatePK(rootId, phaseOrder,
							frame.getFrameOrder(), approver.getApproverID()), WwfdtAppInstApprover.class)
					.get();
			entityApprover.approvalAtr = approver.getApprovalAtr().value;
			entityApprover.confirmAtr = frame.getConfirmAtr().value;
			entityApprover.wwfdpApprovrStatePK.approverId = approver.getApproverID();
			entityApprover.agentID = approver.getAgentID();
			entityApprover.approvalDate = approver.getApprovalDate();
			entityApprover.approvalReason = approver.getApprovalReason()==null ? null : approver.getApprovalReason().v();
			lstEntityApprover.add(entityApprover);
		}
		return lstEntityApprover;
	}

	@Override
	public List<ApprovalRootState> findEmployeeAppByApprovalRecordDate(GeneralDate startDate, GeneralDate endDate,
			String approverID, Integer rootType) {
		List<ApprovalRootState> result = new ArrayList<>();
		switch (rootType) {
		case 1:
			result = this.queryProxy().query(SELECT_CF_DAY_BY_DATE, WwfdtApprovalRootDay.class)
					.setParameter("startDate", startDate).setParameter("endDate", endDate).getList(x -> x.toDomain());
			break;
		case 2:
			result = this.queryProxy().query(SELECT_CF_MONTH_BY_DATE, WwfdtApprovalRootMonth.class)
					.setParameter("startDate", startDate).setParameter("endDate", endDate).getList(x -> x.toDomain());
			break;
		default:
			result = this.queryProxy().query(SELECT_APP_BY_DATE, WwfdtAppInstRoute.class)
					.setParameter("startDate", startDate).setParameter("endDate", endDate).getList(x -> x.toDomain());
		}
		return result;
	}

	@Override
	public List<ApprovalRootState> findAppByEmployeeIDRecordDate(GeneralDate startDate, GeneralDate endDate,
			String employeeID, Integer rootType) {
		switch (rootType) {
		case 1:
			return this.queryProxy().query(SELECT_CF_DAY_BY_EMP_DATE, WwfdtApprovalRootDay.class)
					.setParameter("startDate", startDate).setParameter("endDate", endDate)
					.setParameter("employeeID", employeeID).getList(x -> x.toDomain());
		case 2:
			return this.queryProxy().query(SELECT_CF_MONTH_BY_EMP_DATE, WwfdtApprovalRootMonth.class)
					.setParameter("startDate", startDate).setParameter("endDate", endDate)
					.setParameter("employeeID", employeeID).getList(x -> x.toDomain());
		default:
			return this.queryProxy().query(SELECT_APP_BY_EMP_DATE, WwfdtAppInstRoute.class)
					.setParameter("startDate", startDate).setParameter("endDate", endDate)
					.setParameter("employeeID", employeeID).getList(x -> x.toDomain());
		}
	}

	@Override
	public List<ApprovalRootState> getRootStateByApproverDate(String companyID, String approverID, GeneralDate date) {
		List<String> listAppRootStateSimp = this.queryProxy()
				.query(SELECT_SIMPLE_APP_BY_DATE, WwfdtAppRootStateSimple.class).setParameter("startDate", date)
				.setParameter("endDate", date).getList(x -> x.wwfdpApprovalRootStatePK.rootStateID);
		List<ApprovalRootState> listAppRootState = new ArrayList<>();
		CollectionUtil.split(listAppRootStateSimp, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			internalQuery2(companyID, listAppRootState, subList);
		});
		return listAppRootState;
	}

	@SneakyThrows
	private void internalQuery2(String companyID, List<ApprovalRootState> listAppRootState, List<String> subList) {
		String rootStateIDLst = "";
		if (CollectionUtil.isEmpty(subList)) {
			rootStateIDLst = "''";
		} else {
			for (int i = 0; i < subList.size(); i++) {
				rootStateIDLst += "'" + subList.get(i) + "'";
				if (i != (subList.size() - 1)) {
					rootStateIDLst += ",";
				}
			}
		}
		String query = BASIC_SELECT + " WHERE root.ROOT_STATE_ID IN (rootStateIDs)" + " AND approver.CID = 'companyID'";
		query = query.replaceFirst("rootStateIDs", rootStateIDLst);
		query = query.replaceAll("companyID", companyID);
		List<WwfdtFullJoinState> listFullData = new ArrayList<>();
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			listFullData = WwfdtFullJoinState.fromResultSet(new NtsResultSet(pstatement.executeQuery()));
		}
		listAppRootState.addAll(WwfdtFullJoinState.toDomain(listFullData));
	}

	@Override
	public List<ApprovalRootState> findEmployeeAppByApprovalRecordDateAndNoRootType(String companyID,
			GeneralDate startDate, GeneralDate endDate, String approverID) {
		List<ApprovalRootState> result = new ArrayList<>();
		result.addAll(this.queryProxy().query(SELECT_APPS_BY_APPROVER, WwfdtAppInstRoute.class)
				.setParameter("companyID", companyID).setParameter("startDate", startDate)
				.setParameter("endDate", endDate).setParameter("approverID", approverID)
				.setParameter("systemDate", GeneralDate.today()).getList(x -> x.toDomain()));
		result.addAll(this.queryProxy().query(SELECT_CFS_DAY_BY_APPROVER, WwfdtApprovalRootDay.class)
				.setParameter("companyID", companyID).setParameter("startDate", startDate)
				.setParameter("endDate", endDate).setParameter("approverID", approverID)
				.setParameter("systemDate", GeneralDate.today()).getList(x -> x.toDomain()));
		result.addAll(this.queryProxy().query(SELECT_CFS_MONTH_BY_APPROVER, WwfdtApprovalRootMonth.class)
				.setParameter("companyID", companyID).setParameter("startDate", startDate)
				.setParameter("endDate", endDate).setParameter("approverID", approverID)
				.setParameter("systemDate", GeneralDate.today()).getList(x -> x.toDomain()));
		return result;
	}

	@Override
	public List<ApprovalRootState> findAppByListEmployeeIDRecordDate(GeneralDate startDate, GeneralDate endDate,
			List<String> employeeIDs, Integer rootType) {
		switch (rootType) {
			case 1:
				return this.queryProxy().query(SELECT_DAY_BY_LIST_EMP_DATE, WwfdtApprovalRootDay.class)
						.setParameter("startDate", startDate).setParameter("endDate", endDate)
						.setParameter("employeeID", employeeIDs).getList(x -> x.toDomain());
			case 2:
				return this.queryProxy().query(SELECT_MONTH_BY_LIST_EMP_DATE, WwfdtApprovalRootMonth.class)
						.setParameter("startDate", startDate).setParameter("endDate", endDate)
						.setParameter("employeeID", employeeIDs).getList(x -> x.toDomain());
			default:
				return this.queryProxy().query(SELECT_BY_LIST_EMP_DATE, WwfdtAppInstRoute.class)
						.setParameter("startDate", startDate).setParameter("endDate", endDate)
						.setParameter("employeeID", employeeIDs).getList(x -> x.toDomain());
		}

	}

	@Override
	public List<ApprovalRootState> findAppByListEmployeeIDAndListRecordDate(List<GeneralDate> approvalRecordDates,
			List<String> employeeIDs, Integer rootType) {
		switch (rootType) {
		case 1:
			return this.queryProxy().query(SELECT_CFS_DAY_BY_EMP_AND_DATES, WwfdtApprovalRootDay.class)
					.setParameter("recordDate", approvalRecordDates).setParameter("employeeID", employeeIDs)
					.getList(x -> x.toDomain());
		case 2:
			return this.queryProxy().query(SELECT_CFS_MONTH_BY_EMP_AND_DATES, WwfdtApprovalRootMonth.class)
					.setParameter("recordDate", approvalRecordDates).setParameter("employeeID", employeeIDs)
					.getList(x -> x.toDomain());
		default:
			return this.queryProxy().query(SELECT_APPS_BY_EMP_AND_DATES, WwfdtAppInstRoute.class)
					.setParameter("recordDate", approvalRecordDates).setParameter("employeeID", employeeIDs)
					.getList(x -> x.toDomain());
		}
	}

	@Override
	public List<ApprovalRootState> findByApprover(String companyID, GeneralDate startDate, GeneralDate endDate,
			String approverID, Integer rootType) {
		List<ApprovalRootState> result = new ArrayList<>();
		switch (rootType) {
		case 1:
			result = this.queryProxy().query(SELECT_CFS_DAY_BY_APPROVER, WwfdtApprovalRootDay.class)
					.setParameter("companyID", companyID).setParameter("startDate", startDate)
					.setParameter("endDate", endDate).setParameter("approverID", approverID)
					.setParameter("systemDate", GeneralDate.today()).getList(x -> x.toDomain());
			break;
		case 2:
			result = this.queryProxy().query(SELECT_CFS_MONTH_BY_APPROVER, WwfdtApprovalRootMonth.class)
					.setParameter("companyID", companyID).setParameter("startDate", startDate)
					.setParameter("endDate", endDate).setParameter("approverID", approverID)
					.setParameter("systemDate", GeneralDate.today()).getList(x -> x.toDomain());
			break;
		default:
			result = this.queryProxy().query(SELECT_APPS_BY_APPROVER, WwfdtAppInstRoute.class)
					.setParameter("companyID", companyID).setParameter("startDate", startDate)
					.setParameter("endDate", endDate).setParameter("approverID", approverID)
					.setParameter("systemDate", GeneralDate.today()).getList(x -> x.toDomain());
		}
		return result;
	}

	@Override
	public List<ApprovalRootState> findEmployeeAppByApprovalRecordDateNew(GeneralDate startDate, GeneralDate endDate,
			Integer rootType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteConfirmDay(String employeeID, GeneralDate date) {
		List<WwfdpApprovalRootDayPK> rootDayKeyList = this.queryProxy()
				.query(SELECT_CF_DAY_BY_EMP_DATE_SP, WwfdtAppRootDaySimple.class).setParameter("startDate", date)
				.setParameter("endDate", date).setParameter("employeeID", employeeID)
				.getList(x -> new WwfdpApprovalRootDayPK(x.wwfdpApprovalRootDayPK.rootStateID));
		this.commandProxy().removeAll(WwfdtApprovalRootDay.class, rootDayKeyList);
	}

	@Override
	public List<ApprovalPhaseState> findPhaseApprovalMax(String appID) {
		return this.queryProxy().query(FIND_PHASE_APPROVAL_MAX, WwfdtApprovalPhaseState.class)
				.setParameter("appID", appID).getList(c -> c.toDomain());
	}

	// private ApprovalPhaseState toDomainPhase(WwfdtApprovalPhaseState entity)
	// {
	// return new
	// ApprovalPhaseState(entity.wwfdpApprovalPhaseStatePK.rootStateID,
	// entity.wwfdpApprovalPhaseStatePK.phaseOrder,
	// EnumAdaptor.valueOf(entity.approvalAtr, ApprovalBehaviorAtr.class),
	// EnumAdaptor.valueOf(entity.approvalForm, ApprovalForm.class),
	// null);
	// }

	@Override
	@SneakyThrows
	public List<ApprovalRootState> getByApproverPeriod(String companyID, String approverID, DatePeriod period) {
		List<ApprovalRootState> listAppRootState = new ArrayList<>();

		String query = BASIC_SELECT + " LEFT JOIN KRQDT_APPLICATION app" + " ON root.ROOT_STATE_ID = app.APP_ID"
				+ " WHERE approver.APPROVER_ID = 'approverID'"
				+ " AND app.APP_DATE >= 'startDate'" + " AND app.APP_DATE <= 'endDate'";

		query = query.replaceAll("startDate", period.start().toString("yyyy-MM-dd"));
		query = query.replaceAll("endDate", period.end().toString("yyyy-MM-dd"));
		query = query.replaceAll("approverID", approverID);
		query = query.replaceAll("companyID", companyID);
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			List<WwfdtFullJoinState> listFullData = WwfdtFullJoinState.fromResultSet(new NtsResultSet(pstatement.executeQuery()));
			listAppRootState = WwfdtFullJoinState.toDomain(listFullData);
		}
		return listAppRootState;
	}

	@Override
	@SneakyThrows
	public List<ApprovalRootState> getByApproverAgentPeriod(String companyID, String approverID, DatePeriod period,
			DatePeriod agentPeriod) {

		String query = BASIC_SELECT + " LEFT JOIN KRQDT_APPLICATION app" + " ON root.ROOT_STATE_ID = app.APP_ID"
				+ " WHERE approver.APPROVER_CHILD_ID = 'approverID'" + " AND approver.CID = 'companyID'"
				+ " AND app.APP_DATE >= 'startDate'" + " AND app.APP_DATE <= 'endDate'"
				+ " AND app.APP_DATE >= 'agentStartDate'" + " AND app.APP_DATE <= 'agentEndDate'";

		query = query.replaceAll("startDate", period.start().toString("yyyy-MM-dd"));
		query = query.replaceAll("endDate", period.end().toString("yyyy-MM-dd"));
		query = query.replaceAll("agentStartDate", agentPeriod.start().toString("yyyy-MM-dd"));
		query = query.replaceAll("agentEndDate", agentPeriod.end().toString("yyyy-MM-dd"));
		query = query.replaceAll("approverID", approverID);
		query = query.replaceAll("companyID", companyID);
		List<ApprovalRootState> listApprovalRootState = new ArrayList<>();
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			List<WwfdtFullJoinState> listFullData = WwfdtFullJoinState.fromResultSet(new NtsResultSet(pstatement.executeQuery()));
			listApprovalRootState = WwfdtFullJoinState.toDomain(listFullData);
		}
		return listApprovalRootState;
	}

	@Override
	public List<ApprovalRootState> findEmploymentAppCMM045(String approverID, List<String> agentLst, DatePeriod period,
			boolean unapprovalStatus, boolean approvalStatus, boolean denialStatus, boolean agentApprovalStatus,
			boolean remandStatus, boolean cancelStatus) {
		String companyID = AppContexts.user().companyId();
		List<ApprovalRootState> result = new ArrayList<>();
		//CollectionUtil.split(agentLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
		internalQuery045(companyID, result, approverID, agentLst, period, unapprovalStatus, approvalStatus, denialStatus,
				agentApprovalStatus, remandStatus, cancelStatus);
		//});
		return result;
	}

	@SneakyThrows
	private void internalQuery045(String companyID, List<ApprovalRootState> result, String approverID, List<String> agentLst,
			DatePeriod period, boolean unapprovalStatus, boolean approvalStatus, boolean denialStatus,
			boolean agentApprovalStatus, boolean remandStatus, boolean cancelStatus) {
		List<String> lstApproverID = new ArrayList<>();
		lstApproverID.add(approverID);
		lstApproverID.addAll(agentLst);
		// Phase
		List<Integer> lstPhaseStt = new ArrayList<>();

		if (unapprovalStatus || approvalStatus || denialStatus || agentApprovalStatus) {
			lstPhaseStt.add(0);
		}

		if (approvalStatus || agentApprovalStatus || denialStatus) {
			lstPhaseStt.add(1);
		}

		if (denialStatus || remandStatus) {
			lstPhaseStt.add(3);
		}

		if (denialStatus || cancelStatus) {
			lstPhaseStt.add(2);
			lstPhaseStt.add(4);
		}
		// Frame
		List<Integer> lstFrameStt = new ArrayList<>();

		if (unapprovalStatus || denialStatus || remandStatus || cancelStatus || approvalStatus) {
			lstFrameStt.add(0);
		}

		if (approvalStatus || agentApprovalStatus || denialStatus || remandStatus || cancelStatus) {
			lstFrameStt.add(1);
		}

		if (denialStatus || remandStatus || cancelStatus) {
			lstFrameStt.add(2);
			lstFrameStt.add(3);
			lstFrameStt.add(4);
		}

		String query = "";
		String lstPhase = NtsStatement.In.createParamsString(lstPhaseStt);
		String lstFrame = NtsStatement.In.createParamsString(lstFrameStt);
		String lstIds = NtsStatement.In.createParamsString(lstApproverID);

		query = "SELECT root.ROOT_STATE_ID, root.EMPLOYEE_ID, root.APPROVAL_RECORD_DATE, "
				+ 	"phase.PHASE_ORDER, phase.APPROVAL_FORM, phase.APP_PHASE_ATR, approver.APPROVER_ORDER, "
				+ 	"approver.APPROVER_ID, approver.APPROVAL_ATR, approver.CONFIRM_ATR, approver.AGENT_ID, "
				+   "approver.APPROVAL_DATE, approver.APPROVAL_REASON, approver.APP_DATE, approver.APPROVER_LIST_ORDER  "
				+ "FROM WWFDT_APP_INST_ROUTE root "
				+ "LEFT JOIN WWFDT_APP_INST_PHASE phase ON root.ROOT_STATE_ID = phase.ROOT_STATE_ID "
				+ "LEFT JOIN WWFDT_APP_INST_APPROVER approver ON phase.ROOT_STATE_ID = approver.ROOT_STATE_ID AND phase.PHASE_ORDER = approver.PHASE_ORDER "
				+ "WHERE root.ROOT_STATE_ID IN "
				+ 	"(SELECT DISTINCT a.ROOT_STATE_ID " + "FROM WWFDT_APP_INST_APPROVER a "
				+ 		"inner join WWFDT_APP_INST_PHASE c on a.ROOT_STATE_ID = c.ROOT_STATE_ID and a.PHASE_ORDER = c.PHASE_ORDER "
				+ 		"and a.APP_DATE >= ?  and a.APP_DATE <= ? " 
				+ 		"and c.APP_PHASE_ATR IN (" + lstPhase + ") " 
				+ 		"and a.APPROVAL_ATR IN (" + lstFrame + ") " 
				+ 		"and (a.APPROVER_ID IN (" + lstIds + ") or (a.AGENT_ID = ?)) "
				+ 	")";

		List<WwfdtFullJoinState> listFullData = new ArrayList<>();

		try (val pstatement = this.connection().prepareStatement(query)) {
			pstatement.setString(1, period.start().toString("yyyy-MM-dd"));
			pstatement.setString(2, period.end().toString("yyyy-MM-dd"));

			for (int i = 0; i < lstPhaseStt.size(); i++) {
				pstatement.setInt(i + 3, lstPhaseStt.get(i));
			}

			for (int i = 0; i < lstFrameStt.size(); i++) {
				pstatement.setInt(i + 3 + lstPhaseStt.size(), lstFrameStt.get(i));
			}

			for (int i = 0; i < lstApproverID.size(); i++) {
				pstatement.setString(i + 3 + lstPhaseStt.size() + lstFrameStt.size(), lstApproverID.get(i));
			}
			
			pstatement.setString(3 + lstPhaseStt.size() + lstFrameStt.size() + lstApproverID.size(), approverID);

			listFullData.addAll(WwfdtFullJoinState.fromResultSet(new NtsResultSet(pstatement.executeQuery())));
		}

		// List<ApprovalRootState> entityRoot = WwfdtFullJoinState.toDomain(listFullData);
		
		if(unapprovalStatus) {
			List<String> unapprovalStatusStrLst = listFullData.stream().filter(x -> {
						return x.getApprovalAtr()==ApprovalBehaviorAtr.UNAPPROVED.value && 
								x.getAppPhaseAtr()==ApprovalBehaviorAtr.UNAPPROVED.value &&
								lstApproverID.contains(x.getApproverID());
					}).map(x -> x.getRootStateID()).collect(Collectors.toList());
			List<WwfdtFullJoinState> unapprovalStatusLst = listFullData.stream().filter(x -> unapprovalStatusStrLst.contains(x.getRootStateID()))
					.collect(Collectors.toList());
			result.addAll(WwfdtFullJoinState.toDomain(unapprovalStatusLst));
		}
		if(approvalStatus) {
			List<String> approvalStatusStrLst = listFullData.stream().filter(x -> {
						return (x.getApprovalAtr()==ApprovalBehaviorAtr.APPROVED.value ||
								(x.getApprovalAtr()==ApprovalBehaviorAtr.UNAPPROVED.value && x.getAppPhaseAtr()==ApprovalBehaviorAtr.APPROVED.value)) &&
								(approverID.equals(x.getApproverID()) || 
								(Strings.isNotBlank(x.getAgentID()) && approverID.equals(x.getAgentID())));
					}).map(x -> x.getRootStateID()).collect(Collectors.toList());
			List<WwfdtFullJoinState> approvalStatusLst = listFullData.stream().filter(x -> approvalStatusStrLst.contains(x.getRootStateID()))
					.collect(Collectors.toList());
			result.addAll(WwfdtFullJoinState.toDomain(approvalStatusLst));
		}
		if(agentApprovalStatus) {
			List<String> agentApprovalStatusStrLst = listFullData.stream().filter(x -> {
						return x.getApprovalAtr()==ApprovalBehaviorAtr.APPROVED.value &&
								Strings.isNotBlank(x.getAgentID()) && 
								approverID.equals(x.getApproverID());
					}).map(x -> x.getRootStateID()).collect(Collectors.toList());
			List<WwfdtFullJoinState> agentApprovalStatusLst = listFullData.stream().filter(x -> agentApprovalStatusStrLst.contains(x.getRootStateID()))
					.collect(Collectors.toList());
			result.addAll(WwfdtFullJoinState.toDomain(agentApprovalStatusLst));
		}
		if(remandStatus) {
			List<String> remandStatusStrLst = listFullData.stream().filter(x -> {
						return x.getAppPhaseAtr()==ApprovalBehaviorAtr.REMAND.value &&
								lstApproverID.contains(x.getApproverID());
					}).map(x -> x.getRootStateID()).collect(Collectors.toList());
			List<WwfdtFullJoinState> remandStatusLst = listFullData.stream().filter(x -> remandStatusStrLst.contains(x.getRootStateID()))
					.collect(Collectors.toList());
			result.addAll(WwfdtFullJoinState.toDomain(remandStatusLst));
		}
		if(denialStatus) {
			List<String> denialStatusStrLst = listFullData.stream().filter(x -> {
						boolean condition1 = x.getAppPhaseAtr()==ApprovalBehaviorAtr.DENIAL.value;
						boolean condition2 = approverID.equals(x.getApproverID());
						if(Strings.isNotBlank(x.getAgentID())) {
							condition2 = approverID.equals(x.getAgentID());
						}
						return condition1 && condition2;
					}).map(x -> x.getRootStateID()).collect(Collectors.toList());
			List<WwfdtFullJoinState> denialStatusLst = listFullData.stream().filter(x -> denialStatusStrLst.contains(x.getRootStateID()))
					.collect(Collectors.toList());
			result.addAll(WwfdtFullJoinState.toDomain(denialStatusLst));
		}
		
		// result.addAll(entityRoot);
	}

	@Override
	public List<String> resultKTG002Mobile(GeneralDate startDate, GeneralDate endDate, String approverID,
			Integer rootType, String companyID) {
		String loginSID = AppContexts.user().employeeId();
		GeneralDate baseDate = GeneralDate.today();
		List<String> data = new ArrayList<>();
		String SELECT = "SELECT SYONIN.ROOT_STATE_ID FROM ( "
				+ "SELECT APS.ROOT_STATE_ID AS ROOT_STATE_ID, APS.PHASE_ORDER AS PHASE_ORDER "
				+ "FROM WWFDT_APP_INST_APPROVER APS WHERE APS.APPROVER_ID = ? "
				+ "AND APS.APPROVAL_ATR = '0' AND APS.APP_DATE >= ? AND APS.APP_DATE <= ? UNION ALL "
				+ "SELECT APS.ROOT_STATE_ID AS ROOT_STATE_ID, APS.PHASE_ORDER AS PHASE_ORDER "
				+ "FROM WWFDT_APP_INST_APPROVER APS INNER JOIN WWFMT_AGENT AG "
				+ "ON APS.APPROVER_ID = AG.SID WHERE APS.APPROVAL_ATR = '0' "
				+ "AND APS.APP_DATE >= ? AND APS.APP_DATE <= ? "
				+ "AND AG.START_DATE <= ? AND AG.END_DATE >= ? AND AG.AGENT_APP_TYPE1 = '0' "
				+ "AND AG.AGENT_SID1 = ? ) AS SYONIN "
				+ "INNER JOIN ( SELECT AP.ROOT_STATE_ID AS ROOT_STATE_ID, MAX(PHASE_ORDER) AS NOW_PHASE_ORDER "
				+ "FROM WWFDT_APP_INST_PHASE AP WHERE AP.APP_PHASE_ATR IN ('0','3') "
				+ "GROUP BY AP.ROOT_STATE_ID ) AS NOWFAS "
				+ "ON SYONIN.ROOT_STATE_ID = NOWFAS.ROOT_STATE_ID "
				+ "AND SYONIN.PHASE_ORDER = NOWFAS.NOW_PHASE_ORDER";
		try (PreparedStatement stmt = this.connection().prepareStatement(SELECT)) {
			stmt.setString(1, loginSID);
			stmt.setDate(2, Date.valueOf(startDate.localDate()));
			stmt.setDate(3, Date.valueOf(endDate.localDate()));
			stmt.setDate(4, Date.valueOf(startDate.localDate()));
			stmt.setDate(5, Date.valueOf(endDate.localDate()));
			stmt.setDate(6, Date.valueOf(baseDate.localDate()));
			stmt.setDate(7, Date.valueOf(baseDate.localDate()));
			stmt.setString(8, loginSID);
			
			data = new NtsResultSet(stmt.executeQuery())
						.getList(result -> result.getString("ROOT_STATE_ID"));

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return data;
	}

	@Override
	public void insertApp(ApprovalRootState approvalRootState) {
		this.commandProxy().insert(WwfdtAppInstRoute.fromDomain(approvalRootState));
		this.getEntityManager().flush();
	}

	@Override
	public Map<String, List<ApprovalPhaseState>> getApprovalPhaseByID(List<String> appIDLst) {
		Map<String, List<ApprovalPhaseState>> mapResult = new HashMap<>();
		String sql = "select * from WWFDT_APP_INST_PHASE phase " +
				"left join WWFDT_APP_INST_APPROVER approver " +
				"on phase.ROOT_STATE_ID = approver.ROOT_STATE_ID and phase.PHASE_ORDER = approver.PHASE_ORDER " +
				"where phase.ROOT_STATE_ID in @appIDLst";
		CollectionUtil.split(appIDLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
					.paramString("appIDLst", subList)
					.getList(rec -> toObjectPhase(rec));
			Map<String, List<ApprovalPhaseState>> subMapResult = convertToDomainPhase(mapLst);
			mapResult.putAll(subMapResult);
		});
		return mapResult;
	}
	
	private Map<String, Object> toObjectPhase(NtsResultRecord rec) {
		Map<String, Object> map = new HashMap<String, Object>();
		// WWFDT_APP_INST_PHASE
		map.put("ROOT_STATE_ID", rec.getString("ROOT_STATE_ID"));
		map.put("PHASE_ORDER", rec.getInt("PHASE_ORDER"));
		map.put("APP_PHASE_ATR", rec.getInt("APP_PHASE_ATR"));
		map.put("APPROVAL_FORM", rec.getInt("APPROVAL_FORM"));
		// KRQDT_APP_REFLECT_STATE
		map.put("APPROVER_ORDER", rec.getInt("APPROVER_ORDER"));
		map.put("APPROVER_ID", rec.getString("APPROVER_ID"));
		map.put("APPROVAL_ATR", rec.getInt("APPROVAL_ATR"));
		map.put("CONFIRM_ATR", rec.getInt("CONFIRM_ATR"));
		map.put("AGENT_ID", rec.getString("AGENT_ID"));
		map.put("APPROVAL_DATE", rec.getGeneralDateTime("APPROVAL_DATE"));
		map.put("APPROVAL_REASON", rec.getString("APPROVAL_REASON"));
		map.put("APP_DATE", rec.getGeneralDate("APP_DATE"));
		map.put("APPROVER_LIST_ORDER", rec.getInt("APPROVER_LIST_ORDER"));
		return map;
	}
	
	private Map<String, List<ApprovalPhaseState>> convertToDomainPhase(List<Map<String, Object>> mapLst) {
		return mapLst.stream().collect(Collectors.groupingBy(r -> (String) r.get("ROOT_STATE_ID"))).entrySet()
			.stream().collect(Collectors.toMap(key -> (String) key.getKey(), x -> {
				return x.getValue().stream().collect(Collectors.groupingBy(y -> y.get("PHASE_ORDER"))).entrySet()
					.stream().map(y -> {
						List<ApprovalFrame> listAppFrame = y.getValue().stream().collect(Collectors.groupingBy(z -> z.get("APPROVER_ORDER"))).entrySet()
								.stream().map(z -> {
									List<ApproverInfor> listApprover = z.getValue().stream()
											.collect(Collectors.groupingBy(t -> t.get("APPROVER_ID")))
											.entrySet().stream().map(t -> {
												return ApproverInfor.convert(
														(String) t.getValue().get(0).get("APPROVER_ID"), 
														(int) t.getValue().get(0).get("APPROVAL_ATR"), 
														(String) t.getValue().get(0).get("AGENT_ID"), 
														(GeneralDateTime) t.getValue().get(0).get("APPROVAL_DATE"), 
														(String) t.getValue().get(0).get("APPROVAL_REASON"),
														(int) t.getValue().get(0).get("APPROVER_LIST_ORDER"));
											}).sorted(Comparator.comparing(ApproverInfor::getApproverInListOrder))
											.collect(Collectors.toList());
									return ApprovalFrame.convert(
											(int) z.getValue().get(0).get("APPROVER_ORDER"), 
											(int) z.getValue().get(0).get("CONFIRM_ATR"), 
											(GeneralDate) z.getValue().get(0).get("APP_DATE"), 
											listApprover);
								}).collect(Collectors.toList());
						return ApprovalPhaseState.createFormTypeJava(
								(int) y.getValue().get(0).get("PHASE_ORDER"), 
								(int) y.getValue().get(0).get("APP_PHASE_ATR"), 
								(int) y.getValue().get(0).get("APPROVAL_FORM"), 
								listAppFrame);
					}).collect(Collectors.toList());
			}));
	}

	@Override
	public List<ApprovalRootState> findByApproverAndPeriod(String companyID, GeneralDate startDate, GeneralDate endDate,
			List<String> approverIDs) {
		String query = "SELECT c"
				+ " FROM WwfdtAppInstRoute c"
				+ " WHERE c.rootStateID IN "
				+ "(SELECT DISTINCT a.wwfdpApprovalRootStatePK.rootStateID"
				+ " FROM WwfdtAppRootStateSimple a JOIN WwfdtAppStateSimple b "
				+ " ON a.wwfdpApprovalRootStatePK.rootStateID = b.wwfdpApproverStatePK.rootStateID "
				+ " WHERE (b.wwfdpApproverStatePK.rootStateID IN :approverID"
				+ " OR b.wwfdpApproverStatePK.approverId IN"
				+ " (SELECT d.cmmmtAgentPK.employeeId FROM WwfmtAgent d WHERE d.agentSid1 IN :approverID"
				+ " AND :systemDate <= d.endDate AND :systemDate >= d.startDate))"
				+ " AND b.companyID = :companyID"
				+ " AND b.recordDate >= :startDate AND b.recordDate <= :endDate)";
			
			return this.queryProxy().query(query, WwfdtAppInstRoute.class).setParameter("companyID", companyID)
				.setParameter("startDate", startDate).setParameter("endDate", endDate)
				.setParameter("approverID", approverIDs).setParameter("systemDate", GeneralDate.today())
				.getList(s -> s.toDomain());
	}

	@Override
	@SneakyThrows
	public List<ApprovalRootState> findApprovalRootStateIds(String companyId, List<String> approverIds,
			GeneralDate startDate, GeneralDate endDate) {
		GeneralDate baseDate = GeneralDate.today();
		String query = "SELECT SYONIN.ROOT_STATE_ID, SYONIN.APPROVER_ID, SYONIN.APP_DATE FROM ( "
				+ "SELECT APS.ROOT_STATE_ID AS ROOT_STATE_ID, APS.PHASE_ORDER AS PHASE_ORDER, "
				+ "APS.APPROVER_ID AS APPROVER_ID, APS.APP_DATE AS APP_DATE "
				+ "FROM WWFDT_APP_INST_APPROVER APS WHERE APS.APPROVER_ID IN @APPROVER_IDs "
				+ "AND APS.APPROVAL_ATR = 0 AND APS.APP_DATE >= @sAPP_DATE AND APS.APP_DATE <= @eAPP_DATE UNION ALL "
				+ "SELECT APS.ROOT_STATE_ID AS ROOT_STATE_ID, APS.PHASE_ORDER AS PHASE_ORDER, "
				+ "APS.APPROVER_ID AS APPROVER_ID, APS.APP_DATE AS APP_DATE "
				+ "FROM WWFDT_APP_INST_APPROVER APS INNER JOIN WWFMT_AGENT AG "
				+ "ON APS.APPROVER_ID = AG.SID WHERE APS.APPROVAL_ATR = 0 "
				+ "AND APS.APP_DATE >= @sAPP_DATE AND APS.APP_DATE <= @eAPP_DATE "
				+ "AND AG.START_DATE <= @sSTART_DATE AND AG.END_DATE >= @eEND_DATE AND AG.AGENT_APP_TYPE1 = 0 "
				+ "AND AG.AGENT_SID1 IN @APPROVER_IDs) AS SYONIN "
				+ "INNER JOIN ( SELECT AP.ROOT_STATE_ID AS ROOT_STATE_ID, MAX(PHASE_ORDER) AS NOW_PHASE_ORDER "
				+ "FROM WWFDT_APP_INST_PHASE AP WHERE AP.APP_PHASE_ATR IN (0,3) "
				+ "GROUP BY AP.ROOT_STATE_ID ) AS NOWFAS "
				+ "ON SYONIN.ROOT_STATE_ID = NOWFAS.ROOT_STATE_ID "
				+ "AND SYONIN.PHASE_ORDER = NOWFAS.NOW_PHASE_ORDER";
		List<ApprovalRootState> lstResult = new NtsStatement(query, this.jdbcProxy())
				.paramString("APPROVER_IDs", approverIds)
				.paramDate("sAPP_DATE", startDate)
				.paramDate("eAPP_DATE", endDate)
				.paramDate("sSTART_DATE", baseDate)
				.paramDate("eEND_DATE", baseDate)
				.getList(r -> {
					ApprovalRootState root = new ApprovalRootState();
					root.setRootStateID(r.getString("ROOT_STATE_ID"));
					List<ApprovalPhaseState> phaseList = new ArrayList<>();
					root.setListApprovalPhaseState(phaseList);
					ApprovalPhaseState phase = new ApprovalPhaseState();
					phaseList.add(phase);
			
					List<ApprovalFrame> frameList = new ArrayList<>();
					phase.setListApprovalFrame(frameList);
					ApprovalFrame frame = new ApprovalFrame();
					frame.setAppDate(r.getGeneralDate("APP_DATE"));
					frameList.add(frame);
			
					List<ApproverInfor> approverInfoList = new ArrayList<>();
					frame.setLstApproverInfo(approverInfoList);
					ApproverInfor approverInfo = new ApproverInfor();
					approverInfo.setApproverID(r.getString("APPROVER_ID"));
					approverInfoList.add(approverInfo);
					return root;
		});
		return lstResult;
	}
}