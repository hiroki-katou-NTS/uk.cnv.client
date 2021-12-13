package nts.uk.ctx.workflow.infra.repository.approvermanagement.workroot;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.EmploymentRootAtr;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.SystemAtr;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.WorkplaceApprovalRoot;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.WorkplaceApprovalRootRepository;
import nts.uk.ctx.workflow.infra.entity.approvermanagement.workroot.WwfmtApprovalRouteWp;
import nts.uk.ctx.workflow.infra.entity.approvermanagement.workroot.WwfmtWpApprovalRootPK;
/**
 * 
 * @author hoatt
 *
 */
@RequestScoped
public class JpaWorkplaceApprovalRootRepository extends JpaRepository implements WorkplaceApprovalRootRepository{

	private static final String FIND_BY_ALL = "SELECT c FROM WwfmtApprovalRouteWp c";
	private static final String FIND_BY_CID = FIND_BY_ALL
			+ " WHERE c.wwfmtWpApprovalRootPK.companyId = :companyId";
	private static final String FIND_BY_WKPID = FIND_BY_ALL
			+ " WHERE c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.wwfmtWpApprovalRootPK.workplaceId = :workplaceId";
	private static final String SELECT_WPAPR_BY_EDATE = FIND_BY_WKPID
			+ " AND c.endDate = :endDate"
			+ " AND c.employmentRootAtr = :employmentRootAtr"
			+ " AND c.applicationType = :applicationType";
	private static final String SELECT_WPAPR_BY_EDATE_CM_SYS = FIND_BY_WKPID
			   + " AND c.endDate = :endDate"
			   + " AND c.employmentRootAtr = 0"
			   + " AND c.sysAtr = :sysAtr";
	private static final String SELECT_WPAPR_BY_EDATE_CONFIRM = FIND_BY_WKPID
			   + " AND c.endDate = :endDate" 
			   + " AND c.confirmationRootType = :confirmationRootType"
			   + " AND c.employmentRootAtr = :employmentRootAtr";
	private static final String FIND_ALL_BY_BASEDATE = FIND_BY_ALL + " WHERE  c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.startDate <= :baseDate"
			+ " AND c.endDate >= :baseDate"
			+ " AND c.sysAtr = :sysAtr";
	private static final String FIND_BY_APP_TYPE = FIND_BY_WKPID
			+ " AND c.employmentRootAtr = :employmentRootAtr"
			+ " AND c.applicationType = :applicationType"
			+ " ORDER BY c.startDate DESC";
	private static final String SELECT_WPAPR_BY_COMMON_SYS = FIND_BY_WKPID
			   + " AND c.employmentRootAtr = 0"
			   + " AND c.sysAtr = :sysAtr"
			   + " ORDER BY c.startDate DESC";
	private static final String FIND_BY_CFR_TYPE = FIND_BY_WKPID
			   + " AND c.confirmationRootType = :confirmationRootType"
			   + " AND c.employmentRootAtr = :employmentRootAtr"
			   + " ORDER BY c.startDate DESC";
	private static final String FIND_WP_APP_LAST = FIND_BY_WKPID
			+ " AND c.endDate = :endDate"
			+ " AND c.sysAtr = :sysAtr";
	private static final String FIND_BY_DATE_EMP_CONFIRM = FIND_BY_CID 
			+ " AND c.startDate <= :baseDate"
			+ " AND c.endDate >= :baseDate"
			+ " AND c.confirmationRootType = :confirmationRootType"
			+ " AND c.employmentRootAtr = :employmentRootAtr";
	private static final String FIND_BY_DATE_EMP = FIND_BY_CID 
			+ " AND c.startDate <= :baseDate"
			+ " AND c.endDate >= :baseDate"
			+ " AND c.employmentRootAtr = :employmentRootAtr";	
	//CMM018_ver2
	private static final String FIND_BY_ATR_WORK1 = "SELECT c FROM WwfmtApprovalRouteWp c"
			+ " WHERE c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.wwfmtWpApprovalRootPK.workplaceId = :workplaceId"
			+ " AND c.sysAtr = 0"
			+ " AND c.employmentRootAtr = 1"
			+ " AND c.applicationType IN :lstAppType";
	private static final String FIND_BY_ATR_WORK02 = "SELECT c FROM WwfmtApprovalRouteWp c"
			+ " WHERE c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.wwfmtWpApprovalRootPK.workplaceId = :workplaceId"
			+ " AND c.sysAtr = 0"
			+ " AND c.employmentRootAtr IN (0,2)";
	private static final String FIND_BY_ATR_HR0 = "SELECT c FROM WwfmtApprovalRouteWp c"
			+ " WHERE c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.wwfmtWpApprovalRootPK.workplaceId = :workplaceId"
			+ " AND c.sysAtr = 1"
			+ " AND c.employmentRootAtr = 0";
	private static final String FIND_BY_ATR_HR4 = "SELECT c FROM WwfmtApprovalRouteWp c"
			+ " WHERE c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.wwfmtWpApprovalRootPK.workplaceId = :workplaceId"
			+ " AND c.sysAtr = 1"
			+ " AND c.employmentRootAtr = 4"
			+ " AND c.noticeId IN :lstNoticeID";
	private static final String FIND_BY_ATR_HR5 = "SELECT c FROM WwfmtApprovalRouteWp c"
			+ " WHERE c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.wwfmtWpApprovalRootPK.workplaceId = :workplaceId"
			+ " AND c.sysAtr = 1"
			+ " AND c.employmentRootAtr = 5"
			+ " AND c.busEventId IN :lstEventID";
	private static final String FIND_BY_NTR_TYPE = FIND_BY_WKPID
			   + " AND c.noticeId = :noticeId"
			   + " AND c.employmentRootAtr = :employmentRootAtr"
			   + " ORDER BY c.startDate DESC";
	private static final String FIND_BY_EVR_TYPE = FIND_BY_WKPID
			   + " AND c.busEventId = :busEventId"
			   + " AND c.employmentRootAtr = :employmentRootAtr"
			   + " ORDER BY c.startDate DESC";
	private static final String SELECT_WPAPR_BY_EDATE_NOTICE = FIND_BY_WKPID
			   + " AND c.endDate = :endDate" 
			   + " AND c.noticeId = :noticeId"
			   + " AND c.employmentRootAtr = :employmentRootAtr";
	private static final String SELECT_WPAPR_BY_EDATE_EVENT = FIND_BY_WKPID
			   + " AND c.endDate = :endDate" 
			   + " AND c.busEventId = :busEventId"
			   + " AND c.employmentRootAtr = :employmentRootAtr";
	
	private static final String GET_WKPS_AS_APPROVERS = "SELECT m.wwfmtWpApprovalRootPK.workplaceId"
			+ " FROM WwfmtApprovalRouteWp m JOIN WwfmtAppover d"
			+ " ON m.wwfmtWpApprovalRootPK.approvalId = d.wwfmtAppoverPK.approvalId AND d.employeeId = :sid"
			+ " WHERE m.startDate <= :referDate AND m.endDate >= :referDate";
	
	private static final String FIND_COMMON;
	private static final String FIND_APPLICATION;
	private static final String FIND_CONFIRMATION;
	private static final String FIND_ANYITEM;
	private static final String FIND_NOTICE;
	private static final String FIND_BUS_EVENT;
	static {
			
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT CID, APPROVAL_ID, WKPID, HIST_ID, START_DATE, END_DATE, APP_TYPE, ");
		builder.append("CONFIRMATION_ROOT_TYPE, EMPLOYMENT_ROOT_ATR, SYSTEM_ATR, NOTICE_ID, BUS_EVENT_ID ");
		builder.append("FROM WWFMT_APPROVAL_ROUTE_WP WHERE CID = 'companyID' AND WKPID = 'workplaceID' ");
		builder.append("AND SYSTEM_ATR = 'sysAtr' AND START_DATE <= 'date' AND END_DATE >= 'date' ");
		builder.append("AND EMPLOYMENT_ROOT_ATR = 'rootAtr'");
		FIND_COMMON = builder.toString();
		
		builder = new StringBuilder();
		builder.append(FIND_COMMON);
		builder.append(" AND APP_TYPE = 'targetType'");
		FIND_APPLICATION = builder.toString();
		
		builder = new StringBuilder();
		builder.append(FIND_COMMON);
		builder.append(" AND CONFIRMATION_ROOT_TYPE = 'targetType'");
		FIND_CONFIRMATION = builder.toString();
		
		builder = new StringBuilder();
		builder.append(FIND_COMMON);
		// builder.append(" AND ANYITEM_APP_ID = 'targetType'");
		FIND_ANYITEM = builder.toString();
		
		builder = new StringBuilder();
		builder.append(FIND_COMMON);
		builder.append(" AND NOTICE_ID = 'targetType'");
		FIND_NOTICE = builder.toString();
		
		builder = new StringBuilder();
		builder.append(FIND_COMMON);
		builder.append(" AND BUS_EVENT_ID = 'targetType'");
		FIND_BUS_EVENT = builder.toString();
	}
	
	private static final String FIND_ALL_BY_BASEDATE_CM = "SELECT c FROM WwfmtApprovalRouteWp c"
			+ " WHERE  c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.startDate <= :baseDate"
			+ " AND c.endDate >= :baseDate"
			+ " AND c.sysAtr = 1"
			+ " AND c.employmentRootAtr = 0";
	private static final String FIND_ALL_BY_BASEDATE_NT = "SELECT c FROM WwfmtApprovalRouteWp c"
			+ " WHERE  c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.startDate <= :baseDate"
			+ " AND c.endDate >= :baseDate"
			+ " AND c.sysAtr = 1"
			+ " AND c.employmentRootAtr = 4"
			+ " AND c.noticeId IN :lstNoticeID";
	private static final String FIND_ALL_BY_BASEDATE_EV = "SELECT c FROM WwfmtApprovalRouteWp c"
			+ " WHERE  c.wwfmtWpApprovalRootPK.companyId = :companyId"
			+ " AND c.startDate <= :baseDate"
			+ " AND c.endDate >= :baseDate"
			+ " AND c.sysAtr = 1"
			+ " AND c.employmentRootAtr = 5"
			+ " AND c.busEventId IN :lstEventID";
	/**
	 * get All Workplace Approval Root
	 * @param companyId
	 * @param workplaceId
	 * @return
	 */
	@Override
	public List<WorkplaceApprovalRoot> getWpRootStart(String companyId, String workplaceId, int sysAtr,
			List<Integer> lstAppType, List<Integer> lstNoticeID, List<String> lstEventID) {
		List<WorkplaceApprovalRoot> lstWp = new ArrayList<>();
		if(sysAtr == SystemAtr.WORK.value){//就業
			lstWp.addAll(this.queryProxy().query(FIND_BY_ATR_WORK02, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.getList(c->toDomainWpApR(c)));
			if(!lstAppType.isEmpty()){
				lstWp.addAll(this.queryProxy().query(FIND_BY_ATR_WORK1, WwfmtApprovalRouteWp.class)
						.setParameter("companyId", companyId)
						.setParameter("workplaceId", workplaceId)
						.setParameter("lstAppType", lstAppType)
						.getList(c->toDomainWpApR(c)));
			}
		}else{//人事
			lstWp.addAll(this.queryProxy().query(FIND_BY_ATR_HR0, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.getList(c->toDomainWpApR(c)));
			if(!lstNoticeID.isEmpty()){
				lstWp.addAll(this.queryProxy().query(FIND_BY_ATR_HR4, WwfmtApprovalRouteWp.class)
						.setParameter("companyId", companyId)
						.setParameter("workplaceId", workplaceId)
						.setParameter("lstNoticeID", lstNoticeID)
						.getList(c->toDomainWpApR(c)));
			}
			if(!lstEventID.isEmpty()){
				lstWp.addAll(this.queryProxy().query(FIND_BY_ATR_HR5, WwfmtApprovalRouteWp.class)
						.setParameter("companyId", companyId)
						.setParameter("workplaceId", workplaceId)
						.setParameter("lstEventID", lstEventID)
						.getList(c->toDomainWpApR(c)));
			}
		}
		return lstWp;
	}
	
	/**
	 * get WpApprovalRoot
	 * @param companyId
	 * @param approvalId
	 * @param workplaceId
	 * @param historyId
	 * @return
	 */
	@Override
	public Optional<WorkplaceApprovalRoot> getWpApprovalRoot(String companyId, String approvalId, String workplaceId, String historyId) {
		WwfmtWpApprovalRootPK pk = new WwfmtWpApprovalRootPK(companyId, approvalId, workplaceId, historyId);
		return this.queryProxy().find(pk, WwfmtApprovalRouteWp.class).map(c->toDomainWpApR(c));
	}
	
	/**
	 * get Workplace Approval Root By End date
	 * @param companyId
	 * @param workplaceId
	 * @param endDate
	 * @return
	 */
	@Override
	public List<WorkplaceApprovalRoot> getWpApprovalRootByEdate(String companyId, String workplaceId, GeneralDate endDate, 
			Integer applicationType, int employmentRootAtr, String id, int sysAtr) {
		//common
		if(employmentRootAtr == 0){
			return this.queryProxy().query(SELECT_WPAPR_BY_EDATE_CM_SYS, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.setParameter("endDate", endDate)
					.setParameter("sysAtr", sysAtr)
					.getList(c->toDomainWpApR(c));
		}
		//confirm
		if(employmentRootAtr == 2){
			return this.queryProxy().query(SELECT_WPAPR_BY_EDATE_CONFIRM, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.setParameter("endDate", endDate)
					.setParameter("confirmationRootType", applicationType)
					.setParameter("employmentRootAtr", employmentRootAtr)
					.getList(c->toDomainWpApR(c));
		}
		//notice
		if(employmentRootAtr == 4){
			return this.queryProxy().query(SELECT_WPAPR_BY_EDATE_NOTICE, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.setParameter("endDate", endDate)
					.setParameter("noticeId", applicationType)
					.setParameter("employmentRootAtr", employmentRootAtr)
					.getList(c->toDomainWpApR(c));
		}
		//event
		if(employmentRootAtr == 5){
			return this.queryProxy().query(SELECT_WPAPR_BY_EDATE_EVENT, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.setParameter("endDate", endDate)
					.setParameter("busEventId", id)
					.setParameter("employmentRootAtr", employmentRootAtr)
					.getList(c->toDomainWpApR(c));
		}
		//15 app type
		return this.queryProxy().query(SELECT_WPAPR_BY_EDATE, WwfmtApprovalRouteWp.class)
				.setParameter("companyId", companyId)
				.setParameter("workplaceId", workplaceId)
				.setParameter("endDate", endDate)
				.setParameter("applicationType", applicationType)
				.setParameter("employmentRootAtr", employmentRootAtr)
				.getList(c->toDomainWpApR(c));
	}
	
	@Override
	public Optional<WorkplaceApprovalRoot> findByBaseDate(String companyID, String workplaceID, GeneralDate date, EmploymentRootAtr rootAtr,
			String targetType, int sysAtr) {
		String query = "";
		switch (rootAtr) {
		case APPLICATION:
			query = FIND_APPLICATION;
			break;
		case CONFIRMATION:
			query = FIND_CONFIRMATION;
			break;
		case ANYITEM:
			query = FIND_ANYITEM;
			break;
		case NOTICE:
			query = FIND_NOTICE;
			break;
		case BUS_EVENT:
			query = FIND_BUS_EVENT;
			break;
		default:
			return Optional.empty();
		}
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("workplaceID", workplaceID);
		query = query.replaceAll("sysAtr", String.valueOf(sysAtr));
		query = query.replaceAll("date", date.toString("yyyy-MM-dd"));
		query = query.replaceAll("rootAtr", String.valueOf(rootAtr.value));
		query = query.replaceAll("targetType", targetType);
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			return new NtsResultSet(pstatement.executeQuery())
			.getSingle(x -> convertNtsResult(x));
		} catch (Exception e) {
			throw new RuntimeException("WorkplaceApprovalRoot error");
		}
	}
	
	@Override
	public Optional<WorkplaceApprovalRoot> findByBaseDateOfCommon(String companyID, String workplaceID, GeneralDate date, int sysAtr) {
		String query = FIND_COMMON;
		query = query.replaceAll("companyID", companyID);
		query = query.replaceAll("workplaceID", workplaceID);
		query = query.replaceAll("sysAtr", String.valueOf(sysAtr));
		query = query.replaceAll("date", date.toString("yyyy-MM-dd"));
		query = query.replaceAll("rootAtr", "0");
		try (PreparedStatement pstatement = this.connection().prepareStatement(query)) {
			return new NtsResultSet(pstatement.executeQuery())
			.getSingle(x -> convertNtsResult(x));
		} catch (Exception e) {
			throw new RuntimeException("WorkplaceApprovalRoot error");
		}
	}
	
	private WorkplaceApprovalRoot convertNtsResult(NtsResultRecord record) {
		return WorkplaceApprovalRoot.createSimpleFromJavaType(
				record.getString("CID"), 
				record.getString("APPROVAL_ID"), 
				record.getString("WKPID"), 
				record.getString("HIST_ID"), 
				record.getInt("APP_TYPE"), 
				record.getGeneralDate("START_DATE").toString("yyyy-MM-dd"), 
				record.getGeneralDate("END_DATE").toString("yyyy-MM-dd"), 
				// record.getString("BRANCH_ID"), 
				// record.getString("ANYITEM_APP_ID"), 
				record.getInt("CONFIRMATION_ROOT_TYPE"), 
				record.getInt("EMPLOYMENT_ROOT_ATR"), 
				record.getInt("SYSTEM_ATR"), 
				record.getInt("NOTICE_ID"), 
				record.getString("BUS_EVENT_ID"));
	}
	
	/**
	 * add Workplace Approval Root
	 * @param wpAppRoot
	 */
	@Override
	public void addWpApprovalRoot(WorkplaceApprovalRoot wpAppRoot) {
		this.commandProxy().insert(toEntityWpApR(wpAppRoot));
	}
	/**
	 * add All Workplace Approval Root
	 * @param wpAppRoot
	 */
	@Override
	public void addAllWpApprovalRoot(List<WorkplaceApprovalRoot> wpAppRoot) {
		List<WwfmtApprovalRouteWp> lstEntity = new ArrayList<>();
		for (WorkplaceApprovalRoot wp : wpAppRoot) {
			lstEntity.add(toEntityWpApR(wp));
		}
		this.commandProxy().insertAll(lstEntity);
	}
	/**
	 * update Workplace Approval Root
	 * @param wpAppRoot
	 */
	@Override
	public void updateWpApprovalRoot(WorkplaceApprovalRoot wpAppRoot) {
		WwfmtApprovalRouteWp a = toEntityWpApR(wpAppRoot);
		WwfmtApprovalRouteWp x = this.queryProxy().find(a.wwfmtWpApprovalRootPK, WwfmtApprovalRouteWp.class).get();
		x.setStartDate(a.startDate);
		x.setEndDate(a.endDate);
		x.setApplicationType(a.applicationType);
		x.setConfirmationRootType(a.confirmationRootType);
		x.setEmploymentRootAtr(a.employmentRootAtr);
		this.commandProxy().update(x);
	}
	/**
	 * update All Workplace Approval Root
	 * @param wpAppRoot
	 */
	@Override
	public void updateAllWpApprovalRoot(List<WorkplaceApprovalRoot> wpAppRoot) {
		List<WwfmtApprovalRouteWp> lstEntity = new ArrayList<>();
		for (WorkplaceApprovalRoot wp : wpAppRoot) {
			WwfmtApprovalRouteWp a = toEntityWpApR(wp);
			WwfmtApprovalRouteWp x = this.queryProxy().find(a.wwfmtWpApprovalRootPK, WwfmtApprovalRouteWp.class).get();
			x.setStartDate(a.startDate);
			x.setEndDate(a.endDate);
			x.setApplicationType(a.applicationType);
			x.setConfirmationRootType(a.confirmationRootType);
			x.setEmploymentRootAtr(a.employmentRootAtr);
			lstEntity.add(x);
		}
		
		this.commandProxy().updateAll(lstEntity);
	}
	/**
	 * delete Person Approval Root
	 * @param companyId
	 * @param workplaceId
	 * @param historyId
	 */
	@Override
	public void deleteWpApprovalRoot(String companyId, String approvalId, String workplaceId, String historyId) {
		WwfmtWpApprovalRootPK comPK = new WwfmtWpApprovalRootPK(companyId, approvalId, workplaceId, historyId);
		this.commandProxy().remove(WwfmtApprovalRouteWp.class,comPK);
	}
	
	/**
	 * convert entity WwfmtApprovalRouteWp to domain WorkplaceApprovalRoot
	 * @param entity
	 * @return
	 */
	private WorkplaceApprovalRoot toDomainWpApR(WwfmtApprovalRouteWp entity){
		val domain = WorkplaceApprovalRoot.convert(entity.wwfmtWpApprovalRootPK.companyId,
				entity.wwfmtWpApprovalRootPK.approvalId,
				entity.wwfmtWpApprovalRootPK.workplaceId,
				entity.wwfmtWpApprovalRootPK.historyId,
				entity.employmentRootAtr != 2 ? entity.applicationType : entity.confirmationRootType,
				entity.startDate,
				entity.endDate,
				entity.confirmationRootType,
				entity.employmentRootAtr,
				entity.sysAtr,
				entity.noticeId,
				entity.busEventId);
		return domain;
	}
	
	/**
	 * convert domain WorkplaceApprovalRoot to entity WwfmtApprovalRouteWp
	 * @param domain
	 * @return
	 */
	private WwfmtApprovalRouteWp toEntityWpApR(WorkplaceApprovalRoot domain){
		val entity = new WwfmtApprovalRouteWp();
		entity.wwfmtWpApprovalRootPK = new WwfmtWpApprovalRootPK(domain.getCompanyId(), domain.getApprovalId(),
				domain.getWorkplaceId(), domain.getApprRoot().getHistoryItems().get(0).getHistoryId());
		entity.sysAtr = domain.getApprRoot().getSysAtr().value;
		entity.startDate = domain.getApprRoot().getHistoryItems().get(0).start();
		entity.endDate = domain.getApprRoot().getHistoryItems().get(0).end();
		entity.employmentRootAtr = domain.getApprRoot().getEmploymentRootAtr().value;
		entity.applicationType = domain.getApprRoot().getEmploymentRootAtr().equals(EmploymentRootAtr.APPLICATION) ?
				domain.getApprRoot().getApplicationType().value : null;
		entity.confirmationRootType = domain.getApprRoot().getEmploymentRootAtr().equals(EmploymentRootAtr.CONFIRMATION) ?
				domain.getApprRoot().getConfirmationRootType().value : null;
		entity.noticeId = domain.getApprRoot().getEmploymentRootAtr().equals(EmploymentRootAtr.NOTICE) ?
				domain.getApprRoot().getNoticeId() : null;
		entity.busEventId = domain.getApprRoot().getEmploymentRootAtr().equals(EmploymentRootAtr.BUS_EVENT) ?
				domain.getApprRoot().getBusEventId() : null;
		return entity;
	}
	
	public List<WorkplaceApprovalRoot> findAllByBaseDate(String companyId, GeneralDate baseDate, int sysAtr){
		List<WorkplaceApprovalRoot> data = this.queryProxy()
				.query(FIND_ALL_BY_BASEDATE, WwfmtApprovalRouteWp.class)
				.setParameter("companyId", companyId)
				.setParameter("baseDate", baseDate)
				.setParameter("sysAtr", sysAtr)
				.getList(c->toDomainWpApR(c));
		return data;
	}
	/**
	 * get Work place Approval Root By type
	 * @param companyId
	 * @param workplaceId
	 * @param applicationType
	 * @param employmentRootAtr
	 * @return
	 */
	@Override
	public List<WorkplaceApprovalRoot> getWpApprovalRootByType(String companyId, String workplaceId, Integer applicationType,
			int employmentRootAtr, String id, int sysAtr) {
		//common
		if(employmentRootAtr == 0){
			return this.queryProxy().query(SELECT_WPAPR_BY_COMMON_SYS, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.setParameter("sysAtr", sysAtr)
					.getList(c->toDomainWpApR(c));
		}
		//confirm
		if(employmentRootAtr == 2){
			return this.queryProxy().query(FIND_BY_CFR_TYPE, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.setParameter("confirmationRootType", applicationType)
					.setParameter("employmentRootAtr", employmentRootAtr)
					.getList(c->toDomainWpApR(c));
		}
		//notice
		if(employmentRootAtr == 4){
			return this.queryProxy().query(FIND_BY_NTR_TYPE, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.setParameter("noticeId", applicationType)
					.setParameter("employmentRootAtr", employmentRootAtr)
					.getList(c->toDomainWpApR(c));
		}
		//event
		if(employmentRootAtr == 5){
			return this.queryProxy().query(FIND_BY_EVR_TYPE, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("workplaceId", workplaceId)
					.setParameter("busEventId", id)
					.setParameter("employmentRootAtr", employmentRootAtr)
					.getList(c->toDomainWpApR(c));
		}
		//15 app type
		return this.queryProxy().query(FIND_BY_APP_TYPE, WwfmtApprovalRouteWp.class)
				.setParameter("companyId", companyId)
				.setParameter("workplaceId", workplaceId)
				.setParameter("applicationType", applicationType)
				.setParameter("employmentRootAtr", employmentRootAtr)
				.getList(c->toDomainWpApR(c));
	}

	@Override
	public List<WorkplaceApprovalRoot> getWpAppRootLast(String companyId, String workplaceId, GeneralDate endDate, int sysAtr) {
		
		return this.queryProxy().query(FIND_WP_APP_LAST, WwfmtApprovalRouteWp.class)
				.setParameter("companyId", companyId)
				.setParameter("workplaceId", workplaceId)
				.setParameter("endDate", endDate)
				.setParameter("sysAtr", sysAtr)
				.getList(c -> toDomainWpApR(c));
	}

	@Override
	public List<WorkplaceApprovalRoot> getWpAppRoot(String companyID, GeneralDate date, 
			Integer employmentRootAtr, Integer confirmRootAtr) {
		if(confirmRootAtr==null){
			return this.queryProxy().query(FIND_BY_DATE_EMP, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyID)
					.setParameter("baseDate", date)
					.setParameter("employmentRootAtr", employmentRootAtr)
					.getList(c -> toDomainWpApR(c));
		}
		return this.queryProxy().query(FIND_BY_DATE_EMP_CONFIRM, WwfmtApprovalRouteWp.class)
				.setParameter("companyId", companyID)
				.setParameter("baseDate", date)
				.setParameter("confirmationRootType", confirmRootAtr)
				.setParameter("employmentRootAtr", employmentRootAtr)
				.getList(c -> toDomainWpApR(c));
	}

	@Override
	public List<WorkplaceApprovalRoot> findByBaseDateJinji(String companyId, GeneralDate baseDate, 
		List<Integer> lstNoticeID, List<String> lstEventID) {
		List<WorkplaceApprovalRoot> lstResult = new ArrayList<>();
		lstResult.addAll(this.queryProxy().query(FIND_ALL_BY_BASEDATE_CM, WwfmtApprovalRouteWp.class)
				.setParameter("companyId", companyId)
				.setParameter("baseDate", baseDate)
				.getList(c->toDomainWpApR(c)));
		if(!lstNoticeID.isEmpty()) {
			lstResult.addAll(this.queryProxy().query(FIND_ALL_BY_BASEDATE_NT, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("baseDate", baseDate)
					.setParameter("lstNoticeID", lstNoticeID)
					.getList(c->toDomainWpApR(c)));
		}
		if(!lstEventID.isEmpty()) {
			lstResult.addAll(this.queryProxy().query(FIND_ALL_BY_BASEDATE_EV, WwfmtApprovalRouteWp.class)
					.setParameter("companyId", companyId)
					.setParameter("baseDate", baseDate)
					.setParameter("lstEventID", lstEventID)
					.getList(c->toDomainWpApR(c)));
		}
		return lstResult;
	}

	@Override
	public List<WorkplaceApprovalRoot> getAppRootByDatePeriod(String cid, DatePeriod period, SystemAtr sysAtr,
			List<Integer> lstRootAtr) {
		String sql = "SELECT * "
				+ "FROM WWFMT_APPROVAL_ROUTE_WP WHERE CID = @companyID "
				+ "AND SYSTEM_ATR = @sysAtr AND START_DATE <= @eDate AND END_DATE >= @sDate  "
				+ "AND EMPLOYMENT_ROOT_ATR IN @rootAtr";

		List<WorkplaceApprovalRoot> lstResult =  new NtsStatement(sql, this.jdbcProxy())
				.paramString("companyID", cid)
				.paramInt("sysAtr", sysAtr.value)
				.paramDate("sDate", period.start())
				.paramDate("eDate", period.end())
				.paramInt("rootAtr", lstRootAtr)
				.getList(x -> convertNtsResult(x));
		return lstResult;
	}

	@Override
	public List<String> getWkpsAsApprovers(String sid, GeneralDate referDate) {
		return this.queryProxy().query(GET_WKPS_AS_APPROVERS, String.class)
				.setParameter("sid", sid)
				.setParameter("referDate", referDate)
				.getList();
	}
}
