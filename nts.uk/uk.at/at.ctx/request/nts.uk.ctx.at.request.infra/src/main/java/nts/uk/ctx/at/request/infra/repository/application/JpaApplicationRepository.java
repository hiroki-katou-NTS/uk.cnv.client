package nts.uk.ctx.at.request.infra.repository.application;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.util.Strings;

import lombok.SneakyThrows;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.ReflectionStatusOfDay;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.infra.entity.application.KrqdpAppReflectState;
import nts.uk.ctx.at.request.infra.entity.application.KrqdpApplication;
import nts.uk.ctx.at.request.infra.entity.application.KrqdtAppReflectState;
import nts.uk.ctx.at.request.infra.entity.application.KrqdtApplication;
import nts.uk.shr.com.context.AppContexts;

/**
 *
 * @author Doan Duy Hung
 *
 */
@Stateless
public class JpaApplicationRepository extends JpaRepository implements ApplicationRepository {

	/**
	 * use lesser value for nested split WHERE IN parameters to make sure total
	 * parameters < 2100
	 */

	private static final String SELECT_FROM_APPLICATION = "SELECT a FROM KrqdtApplication_New a"
			+ " WHERE a.krqdpApplicationPK.companyID = :companyID";

	// hoatt
	private static final String SELECT_BY_LIST_SID = SELECT_FROM_APPLICATION
			+ " AND ( a.employeeID IN :lstSID OR a.enteredPersonID IN :lstSID )"
			+ " AND a.endDate >= :startDate AND a.startDate <= :endDate and a.appType IN (0,1,2,4,6,10)";
	// hoatt
	private static final String SELECT_BY_LIST_APPLICANT = SELECT_FROM_APPLICATION + " AND a.employeeID IN :lstSID"
			+ " AND a.endDate >= :startDate AND a.startDate <= :endDate and a.appType IN :lstType";

	private static final String SELECT_APP_BY_SIDS = "SELECT a FROM KrqdtApplication a"
			+ " WHERE a.employeeID IN :employeeID" + " AND a.appDate >= :startDate AND a.appDate <= :endDate";

	private static final String SELECT_APP_BY_LIST_ID = "SELECT a FROM KrqdtApplication a"
			+ " WHERE a.pk.appID IN :listAppID AND a.pk.companyID = :companyID"
			+ " ORDER BY a.appDate";

	private static final String SELECT_APP_BY_CONDS = "SELECT a FROM KrqdtApplication_New a WHERE a.employeeID = :employeeID AND a.appDate >= :startDate AND a.appDate <= :endDate"
			+ " AND a.prePostAtr = 1 AND (a.stateReflectionReal = 0 OR a.stateReflectionReal = 1) ORDER BY a.appDate ASC, a.inputDate DESC";
	
	// hoatt
	private static final String FIND_BY_REF_PERIOD_TYPE = "SELECT app FROM KrqdtApplication app"
			+ " JOIN KrqdtAppReflectState ref ON app.pk.companyID = ref.pk.companyID  AND app.pk.appID = ref.pk.appID"
			+ " WHERE app.pk.companyID = :companyID" + " AND app.employeeID = :employeeID"
			+ " AND app.appDate >= :startDate" + " AND app.appDate <= :endDate" + " AND app.prePostAtr = :prePostAtr"
			+ " AND app.appType = :appType" + " AND  ref.actualReflectStatus IN :lstRef"
			+ " ORDER BY app.appType ASC, app.inputDate DESC";

	private static final String SELECT_MEMO =  "SELECT  a.EXCLUS_VER as aEXCLUS_VER, a.CONTRACT_CD as aCONTRACT_CD, a.CID as aCID, a.APP_ID as aAPP_ID, a.PRE_POST_ATR as aPRE_POST_ATR, " + 
			"a.INPUT_DATE as aINPUT_DATE, a.ENTERED_PERSON_SID as aENTERED_PERSON_SID,  " + 
			"a.REASON_REVERSION as aREASON_REVERSION, a.APP_DATE as aAPP_DATE, a.FIXED_REASON as aFIXED_REASON, a.APP_REASON as aAPP_REASON, a.APP_TYPE as aAPP_TYPE, " + 
			"a.APPLICANTS_SID as aAPPLICANTS_SID, a.APP_START_DATE as aAPP_START_DATE, a.APP_END_DATE as aAPP_END_DATE, a.STAMP_OPTION_ATR as aSTAMP_OPTION_ATR, " + 
			"b.CONTRACT_CD as bCONTRACT_CD, b.CID as bCID, b.APP_ID as bAPP_ID, b.APP_DATE as bAPP_DATE, b.REFLECT_PLAN_STATE as bREFLECT_PLAN_STATE, b.REFLECT_PER_STATE as bREFLECT_PER_STATE, " + 
			"b.REFLECT_PLAN_SCHE_REASON as bREFLECT_PLAN_SCHE_REASON, b.REFLECT_PLAN_TIME as bREFLECT_PLAN_TIME, " + 
			"b.REFLECT_PER_SCHE_REASON as bREFLECT_PER_SCHE_REASON, b.REFLECT_PER_TIME as bREFLECT_PER_TIME, " + 
			"b.CANCEL_PLAN_SCHE_REASON as bCANCEL_PLAN_SCHE_REASON, b.CANCEL_PLAN_TIME as bCANCEL_PLAN_TIME, " + 
			"b.CANCEL_PER_SCHE_REASON as bCANCEL_PER_SCHE_REASON, b.CANCEL_PER_TIME as bCANCEL_PER_TIME ";
	
	private static final String SELECT_BY_SID_PRE_POST_ATR_APPTYPE = "SELECT a FROM KrqdtApplication a "
			+ " WHERE a.employeeID = :employeeID" + " AND a.appDate = :appDate"
			+ " AND a.prePostAtr = :prePostAtr" + " AND a.appType = :appType";

	/**
	 * @author hoatt get List Application phuc vu CMM045
	 */
	@Override
	public List<Application> getListAppModeApprCMM045(String companyID, DatePeriod period, List<String> lstAppId,
			boolean unapprovalStatus, boolean approvalStatus, boolean denialStatus, boolean agentApprovalStatus, 
			boolean remandStatus, boolean cancelStatus, List<Integer> lstType, List<PrePostAtr> prePostAtrLst, 
			List<String> employeeIDLst, List<StampRequestMode> stampRequestModeLst, List<OvertimeAppAtr> overtimeAppAtrLst) {
		if (lstAppId.isEmpty()) {
			return new ArrayList<>();
		}
		List<Integer> lstState = new ArrayList<>();
		if (unapprovalStatus || approvalStatus || agentApprovalStatus || remandStatus) {
			lstState.add(ReflectedState.NOTREFLECTED.value);
		}
		if (approvalStatus || agentApprovalStatus) {
			lstState.add(ReflectedState.WAITREFLECTION.value);
		}
		if (approvalStatus || agentApprovalStatus) {
			lstState.add(ReflectedState.REFLECTED.value);
		}
		if (agentApprovalStatus || cancelStatus) {
			lstState.add(ReflectedState.CANCELED.value);
		}
		if (denialStatus || agentApprovalStatus) {
			lstState.add(ReflectedState.DENIAL.value);
		}

		List<Application> lstResult = new ArrayList<>();
		CollectionUtil.split(lstAppId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subListId -> {
			String subListAppId = NtsStatement.In.createParamsString(subListId);
			String lstTypeString = NtsStatement.In.createParamsString(lstType);
			String lstStateString = NtsStatement.In.createParamsString(lstState);

			String whereCondition = "";
			String connectAppTypeString = "";
			String connectStampString = "";
			String connectOvertimeString = "";
			if(!CollectionUtil.isEmpty(lstType)) {
				connectAppTypeString = "select c.APP_ID from KRQDT_APPLICATION c left join KRQDT_APP_REFLECT_STATE d on c.APP_ID = d.APP_ID " +
						"where c.APP_ID IN @subListId AND c.APP_TYPE IN @lstType " +
						"AND c.CID = @companyID AND c.PRE_POST_ATR IN @prePostAtrLst";
			}
			if(!CollectionUtil.isEmpty(stampRequestModeLst)) {
				connectStampString = "select e.APP_ID from KRQDT_APPLICATION e left join KRQDT_APP_REFLECT_STATE f on e.APP_ID = f.APP_ID " +
						"where e.APP_ID IN @subListId AND e.STAMP_OPTION_ATR IN @stampRequestModeLst " +
						"AND e.CID = @companyID AND e.PRE_POST_ATR IN @prePostAtrLst";
			}
			if(!CollectionUtil.isEmpty(overtimeAppAtrLst)) {
				connectOvertimeString = "select g.APP_ID from KRQDT_APPLICATION g left join KRQDT_APP_REFLECT_STATE h on g.APP_ID = h.APP_ID " +
						"left join KRQDT_APP_OVERTIME i on g.APP_ID = i.APP_ID  where g.APP_ID IN @subListId " +
						"AND g.CID = @companyID AND g.PRE_POST_ATR IN @prePostAtrLst AND i.OVERTIME_ATR IN @overtimeAppAtrLst";
			}
			whereCondition = Arrays.asList(connectAppTypeString, connectStampString, connectOvertimeString).stream()
					.filter(x -> Strings.isNotBlank(x)).collect(Collectors.joining(" union "));
			String sql =
					"select a.EXCLUS_VER as aEXCLUS_VER, a.CONTRACT_CD as aCONTRACT_CD, a.CID as aCID, a.APP_ID as aAPP_ID, a.PRE_POST_ATR as aPRE_POST_ATR, " +
					"a.INPUT_DATE as aINPUT_DATE, a.ENTERED_PERSON_SID as aENTERED_PERSON_SID, " +
					"a.REASON_REVERSION as aREASON_REVERSION, a.APP_DATE as aAPP_DATE, a.FIXED_REASON as aFIXED_REASON, a.APP_REASON as aAPP_REASON, a.APP_TYPE as aAPP_TYPE, " +
					"a.APPLICANTS_SID as aAPPLICANTS_SID, a.APP_START_DATE as aAPP_START_DATE, a.APP_END_DATE as aAPP_END_DATE, a.STAMP_OPTION_ATR as aSTAMP_OPTION_ATR, " +
					"b.CONTRACT_CD as bCONTRACT_CD, b.CID as bCID, b.APP_ID as bAPP_ID, b.APP_DATE as bAPP_DATE, b.REFLECT_PLAN_STATE as bREFLECT_PLAN_STATE, b.REFLECT_PER_STATE as bREFLECT_PER_STATE, " +
					"b.REFLECT_PLAN_SCHE_REASON as bREFLECT_PLAN_SCHE_REASON, b.REFLECT_PLAN_TIME as bREFLECT_PLAN_TIME, " +
					"b.REFLECT_PER_SCHE_REASON as bREFLECT_PER_SCHE_REASON, b.REFLECT_PER_TIME as bREFLECT_PER_TIME, " +
					"b.CANCEL_PLAN_SCHE_REASON as bCANCEL_PLAN_SCHE_REASON, b.CANCEL_PLAN_TIME as bCANCEL_PLAN_TIME, " +
					"b.CANCEL_PER_SCHE_REASON as bCANCEL_PER_SCHE_REASON, b.CANCEL_PER_TIME as bCANCEL_PER_TIME " +
					"from KRQDT_APPLICATION a left join KRQDT_APP_REFLECT_STATE b " +
					"on a.CID = b.CID and a.APP_ID = b.APP_ID " +
					"WHERE a.APP_ID IN (" + whereCondition + ")";
			if(!CollectionUtil.isEmpty(employeeIDLst)) {
				sql += " AND a.APPLICANTS_SID IN @employeeIDLst";
			}
			NtsStatement ntsStatement = new NtsStatement(sql, this.jdbcProxy())
					.paramString("subListId", subListId)
					.paramString("companyID", companyID)
					.paramInt("prePostAtrLst", prePostAtrLst.stream().map(x -> x.value).collect(Collectors.toList()));
			if(!CollectionUtil.isEmpty(employeeIDLst)) {
				ntsStatement = ntsStatement.paramString("employeeIDLst", employeeIDLst);
			}
			if(!CollectionUtil.isEmpty(lstType)) {
				ntsStatement = ntsStatement.paramInt("lstType", lstType);
			}
			if(!CollectionUtil.isEmpty(stampRequestModeLst)) {
				ntsStatement = ntsStatement.paramInt("stampRequestModeLst", stampRequestModeLst.stream().map(x -> x.value).collect(Collectors.toList()));
			}
			if(!CollectionUtil.isEmpty(overtimeAppAtrLst)) {
				ntsStatement = ntsStatement.paramInt("overtimeAppAtrLst", overtimeAppAtrLst.stream().map(x -> x.value).collect(Collectors.toList()));
			}
			List<Map<String, Object>> mapLst = ntsStatement.getList(rec -> toObject(rec));
			List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
			if(!CollectionUtil.isEmpty(krqdtApplicationLst)) {
				List<Application> sublstResult = krqdtApplicationLst.stream().map(i -> i.toDomain()).collect(Collectors.toList());
				lstResult.addAll(sublstResult);
			}


//			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
//				int sizeListId = subListId.size();
//				int sizeLstType = lstType.size();
//				int sizeLstState = lstState.size();
//
//				for (int i = 0; i < sizeListId; i++) {
//					stmt.setString(i + 1, subListId.get(i));
//				}
//
//				for (int i = 0; i < sizeLstType; i++) {
//					stmt.setInt(i + sizeListId + 1, lstType.get(i));
//				}
//
//				for (int i = 0; i < sizeLstState; i++) {
//					stmt.setInt(i + sizeListId + sizeLstType + 1, lstState.get(i));
//				}
//
//				stmt.setString(sizeListId + sizeLstType + sizeLstState + 1, companyId);
//
//				lstResult.addAll(new NtsResultSet(stmt.executeQuery()).getList(rs -> new KrqdtApplication_New(
//						new KrqdpApplicationPK_New(rs.getString("CID"), rs.getString("APP_ID")),
//						rs.getLong("EXCLUS_VER"), rs.getInt("PRE_POST_ATR"), rs.getGeneralDateTime("INPUT_DATE"),
//						rs.getString("ENTERED_PERSON_SID"), rs.getString("REASON_REVERSION"),
//						rs.getGeneralDate("APP_DATE"), rs.getString("APP_REASON"), rs.getInt("APP_TYPE"),
//						rs.getString("APPLICANTS_SID"), rs.getGeneralDate("APP_START_DATE"),
//						rs.getGeneralDate("APP_END_DATE"), rs.getInt("REFLECT_PLAN_STATE"),
//						rs.getInt("REFLECT_PER_STATE"), rs.getInt("REFLECT_PLAN_ENFORCE_ATR"),
//						rs.getInt("REFLECT_PER_ENFORCE_ATR"), rs.getInt("REFLECT_PLAN_SCHE_REASON"),
//						rs.getInt("REFLECT_PER_SCHE_REASON"), rs.getGeneralDateTime("REFLECT_PLAN_TIME"),
//						rs.getGeneralDateTime("REFLECT_PER_TIME")).toDomain()));
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		});

		return lstResult;

	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<Application> getApplicationBySIDs(List<String> employeeID, GeneralDate startDate,
			GeneralDate endDate) {
		List<Application> data = new ArrayList<>();
		CollectionUtil.split(employeeID, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			data.addAll(this.queryProxy().query(SELECT_APP_BY_SIDS, KrqdtApplication.class)
					.setParameter("employeeID", subList).setParameter("startDate", startDate)
					.setParameter("endDate", endDate).getList(c -> c.toDomain()));
		});
		return data;
	}

	/**
	 * RequestList 232 param 反映状態 ＝ 「反映済み」または「反映待ち」 RequestList 233 param 反映状態 ＝
	 * 「未承認」または「差戻し」 RequestList 234 param 反映状態 ＝ 「否認」 RequestList 235 param
	 * 反映状態 ＝ 「差戻し」
	 */
	private static final String SELECT_LIST_REFSTATUS = "SELECT a FROM KrqdtApplication a"
			+ " JOIN KrqdtAppReflectState ref ON a.pk.companyID = ref.pk.companyID  AND a.pk.appID = ref.pk.appID"
			+ " WHERE a.pk.companyID =:companyID" + " AND a.employeeID = :employeeID "
			+ " AND a.appDate >= :startDate AND a.appDate <= :endDate"
			+ " AND ref.actualReflectStatus IN :listReflecInfor" + " ORDER BY a.appDate ASC," + " a.prePostAtr DESC";
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Application> getByListRefStatus(String companyID, String employeeID, GeneralDate startDate,
			GeneralDate endDate, List<Integer> listReflecInfor) {
		// TODO Auto-generated method stub
		if (listReflecInfor.size() == 0) {
			return Collections.emptyList();
		}
		List<Application> resultList = new ArrayList<>();
		CollectionUtil.split(listReflecInfor, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(SELECT_LIST_REFSTATUS, KrqdtApplication.class)
					.setParameter("companyID", companyID).setParameter("employeeID", employeeID)
					.setParameter("startDate", startDate).setParameter("endDate", endDate)
					.setParameter("listReflecInfor", subList).getList(x -> x.toDomain()));
		});
		resultList.sort(Comparator.comparing(Application::getPrePostAtr));
		return resultList;
	}

	@Override
	public List<Application> findByListID(String companyID, List<String> listAppID) {
		if (CollectionUtil.isEmpty(listAppID)) {
			return Collections.emptyList();
		}
		List<Application> resultList = new ArrayList<>();
		CollectionUtil.split(listAppID, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(SELECT_APP_BY_LIST_ID, KrqdtApplication.class)
					.setParameter("listAppID", subList).setParameter("companyID", companyID)
					.getList(x -> x.toDomain()));
		});
		return resultList;
	}

	/**
	 * OUTPUTに反映状態を含まない
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Application> getListLateOrLeaveEarly(String companyID, String employeeID, GeneralDate startDate,
			GeneralDate endDate) {
		String sql = "SELECT app FROM KrqdtApplication app"
				+ " join KrqdtAppReflectState ref"
				+ "  on app.pk.appID = ref.pk.appID and  app.pk.companyID = ref.pk.companyID"
				+ " WHERE  app.employeeID =  :sid "
				+ " and  app.pk.companyID = :companyID"
				+ " AND app.opAppStartDate <= :strData " + " AND app.opAppEndDate >= :endData " + " AND app.appType  = 9 "
				+ " AND ref.actualReflectStatus  = 0" + " ORDER BY app.appDate ASC";

		return this.queryProxy().query(sql, KrqdtApplication.class)
				.setParameter("sid", employeeID)
				.setParameter("endData", startDate)
				.setParameter("strData", endDate)
				.setParameter("companyID", companyID)
				.getList(c -> c.toDomain());
	}

	/**
	 * OUTPUTに反映状態を含まない
	 */
	@Override
	public List<Application> getByPeriodReflectType(String sid, DatePeriod dateData, List<Integer> reflect,
			List<Integer> appType) {
		String sql = SELECT_MEMO
				+ " FROM KRQDT_APPLICATION a" 
				+ " join KRQDT_APP_REFLECT_STATE b"
				+ "  on a.APP_ID = b.APP_ID and  a.CID = b.CID"
				+ " WHERE  a.APPLICANTS_SID =  @sid "
				+ " AND a.APP_START_DATE >= @strData " + " AND a.APP_END_DATE <= @endData " + " AND a.APP_TYPE IN @appType " 
				+ " AND b.REFLECT_PER_STATE IN @recordStatus" + " ORDER BY a.INPUT_DATE ASC";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("sid", sid)
				.paramDate("strData", dateData.start())
				.paramDate("endData", dateData.end())
				.paramInt("recordStatus", reflect)
				.paramInt("appType", appType)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		return krqdtApplicationLst.stream().map(c -> c.toDomain()).collect(Collectors.toList());
		
	}

	final static String FIND_WITH_BASEDATE = SELECT_MEMO
			+ " FROM KRQDT_APPLICATION a" 
			+ " join KRQDT_APP_REFLECT_STATE b"
			+ "  on a.APP_ID = b.APP_ID and  a.CID = b.CID"
			+ " WHERE  a.APPLICANTS_SID =  @sid "
			+ " AND a.APP_START_DATE <= @baseDate " + " AND a.APP_END_DATE >= @baseDate " + " AND a.APP_TYPE IN @appType " 
			+ " AND b.REFLECT_PER_STATE IN @recordStatus" + " ORDER BY a.INPUT_DATE ASC";
	@Override
	public List<Application> getByPeriodReflectType(String sid, GeneralDate baseDate, List<Integer> reflect,
			List<Integer> appType) {
		List<Map<String, Object>> mapLst = new NtsStatement(FIND_WITH_BASEDATE, this.jdbcProxy())
				.paramString("sid", sid)
				.paramDate("baseDate", baseDate)
				.paramInt("recordStatus", reflect)
				.paramInt("appType", appType)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		return krqdtApplicationLst.stream().map(c -> c.toDomain()).collect(Collectors.toList());
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Application> getListAppByType(String companyId, String employeeID, GeneralDate startDate,
			GeneralDate endDate, int prePostAtr, int appType, List<Integer> lstRef) {
		if (lstRef.isEmpty()) {
			return new ArrayList<>();
		}
		List<Application> resultList = new ArrayList<>();
		CollectionUtil.split(lstRef, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_REF_PERIOD_TYPE, KrqdtApplication.class)
					.setParameter("companyID", companyId).setParameter("employeeID", employeeID)
					.setParameter("prePostAtr", prePostAtr).setParameter("startDate", startDate)
					.setParameter("endDate", endDate).setParameter("appType", appType).setParameter("lstRef", subList)
					.getList(c -> c.toDomain()));
		});
		resultList.sort((o1, o2) -> {
			int tmp = o1.getAppType().value - o2.getAppType().value;
			if (tmp != 0) {
				return tmp;
			}
			return o2.getInputDate().compareTo(o1.getInputDate()); // DESC
		});
		return resultList;
	}

	/**
	 * OUTPUTに反映状態を含まない
	 */
	@Override
	@SneakyThrows
	public List<Application> getAppForReflect(String sid, DatePeriod dateData, List<Integer> recordStatus,
			List<Integer> scheStatus, List<Integer> appType) {
		String sql = SELECT_MEMO
				+ " FROM KRQDT_APPLICATION a" 
				+ " join KRQDT_APP_REFLECT_STATE b"
				+ "  on a.APP_ID = b.APP_ID and  a.CID = b.CID"
				+ " WHERE  a.APPLICANTS_SID =  @sid "
				+ " AND a.APP_START_DATE <= @endData " + " AND a.APP_END_DATE >= @strData" + " AND a.APP_TYPE IN @appType " 
				+ " AND (b.REFLECT_PLAN_STATE IN @scheStatus " 
				+ " OR b.REFLECT_PER_STATE IN @recordStatus)" + " ORDER BY a.INPUT_DATE ASC";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("sid", sid)
				.paramDate("strData", dateData.start())
				.paramDate("endData", dateData.end())
				.paramInt("recordStatus", recordStatus)
				.paramInt("scheStatus", scheStatus)
				.paramInt("appType", appType)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		return krqdtApplicationLst.stream().map(c -> c.toDomain()).collect(Collectors.toList());
	}

	/**
	 * OUTPUTに反映状態を含まな�
	 */
	@Override
	@SneakyThrows
	public List<Application> getByListDateReflectType(String sid, List<GeneralDate> dateData, List<Integer> reflect,
			List<Integer> appType) {
		String sql = SELECT_MEMO
				+ "FROM KRQDT_APPLICATION a" 
				+ " join KRQDT_APP_REFLECT_STATE b"
				+ "  on a.APP_ID = b.APP_ID and  a.CID = b.CID"
				+ " WHERE  a.APPLICANTS_SID =  @sid "
				+ " AND a.APP_DATE IN @dateData " + " AND a.APP_TYPE IN @appType " 
				+ " AND (b.REFLECT_PLAN_STATE IN @scheStatus " 
				+ " OR b.REFLECT_PER_STATE IN @recordStatus)" + " ORDER BY a.INPUT_DATE ASC";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("sid", sid)
				.paramDate("dateData", dateData)
				.paramInt("appType", appType)
				.paramInt("scheStatus", reflect)
				.paramInt("recordStatus", reflect)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		return krqdtApplicationLst.stream().map(c -> c.toDomain()).collect(Collectors.toList());
	}

	@Override
	@SneakyThrows
	public List<Application> getByListDateReflectType2(String sid, List<GeneralDate> dateData, List<Integer> appTypes, List<Integer> reflectStatuses) {
		if (dateData.isEmpty() || appTypes.isEmpty() || reflectStatuses.isEmpty()) return new ArrayList<>();
		String sql = SELECT_MEMO
				+ "FROM KRQDT_APPLICATION a"
				+ " join KRQDT_APP_REFLECT_STATE b"
				+ "  on a.APP_ID = b.APP_ID and  a.CID = b.CID"
				+ " WHERE  a.APPLICANTS_SID =  @sid "
				+ " AND a.APP_DATE IN @dateData "
				+ " AND a.APP_TYPE IN @appType "
				+ " AND (b.REFLECT_PLAN_STATE IN @scheStatus AND b.REFLECT_PER_STATE IN @recordStatus)"
				+ " ORDER BY a.INPUT_DATE ASC";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("sid", sid)
				.paramDate("dateData", dateData)
				.paramInt("appType", appTypes)
				.paramInt("scheStatus", reflectStatuses)
				.paramInt("recordStatus", reflectStatuses)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		return krqdtApplicationLst.stream().map(c -> c.toDomain()).collect(Collectors.toList());
	}
	
	@Override
	@SneakyThrows
	public List<Application> getByListDateReflectType(String sid, DatePeriod period, List<Integer> reflect,
			List<Integer> appType) {
		String sql = SELECT_MEMO
				+ "FROM KRQDT_APPLICATION a" 
				+ " join KRQDT_APP_REFLECT_STATE b"
				+ "  on a.APP_ID = b.APP_ID and  a.CID = b.CID"
				+ " WHERE  a.APPLICANTS_SID =  @sid "
				+ " AND a.APP_DATE >= @startDate " 
				+ " AND a.APP_DATE <= @endDate " 
				+ " AND a.APP_TYPE IN @appType " 
				+ " AND b.REFLECT_PER_STATE IN @recordStatus" + " ORDER BY a.INPUT_DATE ASC";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("sid", sid)
				.paramDate("startDate", period.start())
				.paramDate("endDate", period.end())
				.paramInt("recordStatus", reflect)
				.paramInt("appType", appType)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		return krqdtApplicationLst.stream().map(c -> c.toDomain()).collect(Collectors.toList());
	}

	@Override
	@SneakyThrows
	public Map<String, Integer> getParamCMMS45(String companyId, String configName, List<String> subName) {
		String lstsubName = "";
		for (int i = 0; i < subName.size(); i++) {
			lstsubName += "'" + subName.get(i) + "'";
			if (i != (subName.size() - 1)) {
				lstsubName += ",";
			}
		}
		String sql = "SELECT * FROM CISCT_SYSTEM_CONFIG_K " + "WHERE CID = 'companyId' "
				+ "AND CONFIG_NAME = 'configName' " + "AND SUB_NAME IN (lstsubName)";
		sql = sql.replaceAll("companyId", companyId);
		sql = sql.replaceAll("configName", configName);
		sql = sql.replaceAll("lstsubName", lstsubName);
		Map<String, Integer> mapResult = new HashMap<>();
		try (PreparedStatement pstatement = this.connection().prepareStatement(sql)) {
			NtsResultSet nrs = new NtsResultSet(pstatement.executeQuery());
			
			nrs.getList(rs -> mapResult.put(rs.getString("SUB_NAME"), Integer.valueOf(rs.getString("CONFIG_VALUE"))));
		}
		return mapResult;
	}

	// refactor 4

	@Override
	public Optional<Application> findByID(String companyID, String appID) {
		return this.findByID(appID);
	}

	@Override
	public void insert(Application application) {
		this.commandProxy().insert(KrqdtApplication.fromDomain(application));
		this.getEntityManager().flush();

	}

	@Override
	public void update(Application application) {
		KrqdtApplication krqdtApplication = this.findEntityByID(application.getAppID()).get();
		krqdtApplication.setOpReversionReason(application.getOpReversionReason().map(x -> x.v()).orElse(null));
		krqdtApplication.setOpAppReason(application.getOpAppReason().map(x -> x.v()).orElse(null));
		krqdtApplication.setOpAppStandardReasonCD(application.getOpAppStandardReasonCD().map(x -> x.v()).orElse(null));
		for(ReflectionStatusOfDay reflectionStatusOfDay : application.getReflectionStatus().getListReflectionStatusOfDay()) {
			Optional<KrqdtAppReflectState> opKrqdtAppReflectState = krqdtApplication.getKrqdtAppReflectStateLst().stream()
					.filter(x -> x.getPk().getTargetDate().equals(reflectionStatusOfDay.getTargetDate())).findAny();
			if(opKrqdtAppReflectState.isPresent()) {
				KrqdtAppReflectState krqdtAppReflectState = opKrqdtAppReflectState.get();
				krqdtAppReflectState.setScheReflectStatus(reflectionStatusOfDay.getScheReflectStatus().value);
				krqdtAppReflectState.setActualReflectStatus(reflectionStatusOfDay.getActualReflectStatus().value);
				krqdtAppReflectState.setOpReasonScheCantReflect(
						reflectionStatusOfDay.getOpUpdateStatusAppReflect().map(x -> x.getOpReasonScheCantReflect().map(y -> y.value).orElse(null)).orElse(null));
				krqdtAppReflectState.setOpScheReflectDateTime(
						reflectionStatusOfDay.getOpUpdateStatusAppReflect().map(x -> x.getOpScheReflectDateTime().orElse(null)).orElse(null));
				krqdtAppReflectState.setOpReasonActualCantReflect(
						reflectionStatusOfDay.getOpUpdateStatusAppReflect().map(x -> x.getOpReasonActualCantReflect().map(y -> y.value).orElse(null)).orElse(null));
				krqdtAppReflectState.setOpActualReflectDateTime(
						reflectionStatusOfDay.getOpUpdateStatusAppReflect().map(x -> x.getOpActualReflectDateTime().orElse(null)).orElse(null));
				krqdtAppReflectState.setOpReasonScheCantReflectCancel(
						reflectionStatusOfDay.getOpUpdateStatusAppCancel().map(x -> x.getOpReasonScheCantReflect().map(y -> y.value).orElse(null)).orElse(null));
				krqdtAppReflectState.setOpScheReflectDateTimeCancel(
						reflectionStatusOfDay.getOpUpdateStatusAppCancel().map(x -> x.getOpScheReflectDateTime().orElse(null)).orElse(null));
				krqdtAppReflectState.setOpReasonActualCantReflectCancel(
						reflectionStatusOfDay.getOpUpdateStatusAppCancel().map(x -> x.getOpReasonActualCantReflect().map(y -> y.value).orElse(null)).orElse(null));
				krqdtAppReflectState.setOpActualReflectDateTimeCancel(
						reflectionStatusOfDay.getOpUpdateStatusAppCancel().map(x -> x.getOpActualReflectDateTime().orElse(null)).orElse(null));
			}
		}
		this.commandProxy().update(krqdtApplication);
		this.getEntityManager().flush();
	}

	@Override
	public void remove(String appID) {
		String companyID = AppContexts.user().companyId();
		this.commandProxy().remove(KrqdtApplication.class, new KrqdpApplication(companyID, appID));
	}

	@Override
	public Optional<Application> findByID(String appID) {
		return this.findEntityByID(appID).map(x -> x.toDomain());
	}

	private Map<String, Object> toObject(NtsResultRecord rec) {
		Map<String, Object> map = new HashMap<String, Object>();
		// KRQDT_APPLICATION
		map.put("aEXCLUS_VER", rec.getInt("aEXCLUS_VER"));
		map.put("aCONTRACT_CD", rec.getString("aCONTRACT_CD"));
		map.put("aCID", rec.getString("aCID"));
		map.put("aAPP_ID", rec.getString("aAPP_ID"));
		map.put("aPRE_POST_ATR", rec.getInt("aPRE_POST_ATR"));
		map.put("aINPUT_DATE", rec.getGeneralDateTime("aINPUT_DATE"));
		map.put("aENTERED_PERSON_SID", rec.getString("aENTERED_PERSON_SID"));
		map.put("aREASON_REVERSION", rec.getString("aREASON_REVERSION"));
		map.put("aAPP_DATE", rec.getGeneralDate("aAPP_DATE"));
		map.put("aFIXED_REASON", rec.getInt("aFIXED_REASON"));
		map.put("aAPP_REASON", rec.getString("aAPP_REASON"));
		map.put("aAPP_TYPE", rec.getInt("aAPP_TYPE"));
		map.put("aAPPLICANTS_SID", rec.getString("aAPPLICANTS_SID"));
		map.put("aAPP_START_DATE", rec.getGeneralDate("aAPP_START_DATE"));
		map.put("aAPP_END_DATE", rec.getGeneralDate("aAPP_END_DATE"));
		map.put("aSTAMP_OPTION_ATR", rec.getInt("aSTAMP_OPTION_ATR"));
		// KRQDT_APP_REFLECT_STATE
		map.put("bCONTRACT_CD", rec.getString("bCONTRACT_CD"));
		map.put("bCID", rec.getString("bCID"));
		map.put("bAPP_ID", rec.getString("bAPP_ID"));
		map.put("bAPP_DATE", rec.getGeneralDate("bAPP_DATE"));
		map.put("bREFLECT_PLAN_STATE", rec.getInt("bREFLECT_PLAN_STATE"));
		map.put("bREFLECT_PER_STATE", rec.getInt("bREFLECT_PER_STATE"));
		map.put("bREFLECT_PLAN_SCHE_REASON", rec.getInt("bREFLECT_PLAN_SCHE_REASON"));
		map.put("bREFLECT_PLAN_TIME", rec.getGeneralDateTime("bREFLECT_PLAN_TIME"));
		map.put("bREFLECT_PER_SCHE_REASON", rec.getInt("bREFLECT_PER_SCHE_REASON"));
		map.put("bREFLECT_PER_TIME", rec.getGeneralDateTime("bREFLECT_PER_TIME"));
		map.put("bCANCEL_PLAN_SCHE_REASON", rec.getInt("bCANCEL_PLAN_SCHE_REASON"));
		map.put("bCANCEL_PLAN_TIME", rec.getGeneralDateTime("bCANCEL_PLAN_TIME"));
		map.put("bCANCEL_PER_SCHE_REASON", rec.getInt("bCANCEL_PER_SCHE_REASON"));
		map.put("bCANCEL_PER_TIME", rec.getGeneralDateTime("bCANCEL_PER_TIME"));
		return map;
	}

	private List<KrqdtApplication> convertToEntity(List<Map<String, Object>> mapLst) {
		List<KrqdtApplication> result = mapLst.stream().collect(Collectors.groupingBy(x -> x.get("aAPP_ID"))).entrySet()
			.stream().map(x -> {
				List<KrqdtAppReflectState> krqdtAppReflectStateLst = x.getValue().stream().collect(Collectors.groupingBy(y -> y.get("bAPP_DATE"))).entrySet()
					.stream().map(y -> {
						int scheReflectStatus = (int) y.getValue().get(0).get("bREFLECT_PLAN_STATE");
						int actualReflectStatus = (int) y.getValue().get(0).get("bREFLECT_PER_STATE");
						GeneralDateTime opActualReflectDateTime = (GeneralDateTime) y.getValue().get(0).get("bREFLECT_PER_TIME");
						GeneralDateTime opScheReflectDateTime = (GeneralDateTime) y.getValue().get(0).get("bREFLECT_PLAN_TIME");
						Integer opReasonActualCantReflect = (Integer) y.getValue().get(0).get("bREFLECT_PER_SCHE_REASON");
						Integer opReasonScheCantReflect = (Integer) y.getValue().get(0).get("bREFLECT_PLAN_SCHE_REASON");
						GeneralDateTime opActualReflectDateTimeCancel = (GeneralDateTime) y.getValue().get(0).get("bCANCEL_PER_TIME");
						GeneralDateTime opScheReflectDateTimeCancel = (GeneralDateTime) y.getValue().get(0).get("bCANCEL_PLAN_TIME");
						Integer opReasonActualCantReflectCancel = (Integer) y.getValue().get(0).get("bCANCEL_PER_SCHE_REASON");
						Integer opReasonScheCantReflectCancel = (Integer) y.getValue().get(0).get("bCANCEL_PLAN_SCHE_REASON");
						KrqdtAppReflectState krqdtAppReflectState = new KrqdtAppReflectState(
								new KrqdpAppReflectState(
										(String) y.getValue().get(0).get("bCID"),
										(String) y.getValue().get(0).get("bAPP_ID"),
										(GeneralDate) y.getValue().get(0).get("bAPP_DATE")),
								scheReflectStatus,
								actualReflectStatus,
								opReasonScheCantReflect,
								opScheReflectDateTime,
								opReasonActualCantReflect,
								opActualReflectDateTime,
								opReasonScheCantReflectCancel,
								opScheReflectDateTimeCancel,
								opReasonActualCantReflectCancel,
								opActualReflectDateTimeCancel,
								null);
						krqdtAppReflectState.contractCd = (String) y.getValue().get(0).get("bCONTRACT_CD");
						return krqdtAppReflectState;
					}).collect(Collectors.toList());
				KrqdtApplication krqdtApplication = new KrqdtApplication(
						new KrqdpApplication(
								(String) x.getValue().get(0).get("aCID"),
								(String) x.getValue().get(0).get("aAPP_ID")),
						(int) x.getValue().get(0).get("aEXCLUS_VER"),
						(int) x.getValue().get(0).get("aPRE_POST_ATR"),
						(GeneralDateTime) x.getValue().get(0).get("aINPUT_DATE"),
						(String) x.getValue().get(0).get("aENTERED_PERSON_SID"),
						(String) x.getValue().get(0).get("aREASON_REVERSION"),
						(GeneralDate) x.getValue().get(0).get("aAPP_DATE"),
						(Integer) x.getValue().get(0).get("aFIXED_REASON"),
						(String) x.getValue().get(0).get("aAPP_REASON"),
						(int) x.getValue().get(0).get("aAPP_TYPE"),
						(String) x.getValue().get(0).get("aAPPLICANTS_SID"),
						(GeneralDate) x.getValue().get(0).get("aAPP_START_DATE"),
						(GeneralDate) x.getValue().get(0).get("aAPP_END_DATE"),
						(Integer) x.getValue().get(0).get("aSTAMP_OPTION_ATR"),
						krqdtAppReflectStateLst);
				krqdtApplication.contractCd = (String) x.getValue().get(0).get("aCONTRACT_CD");
				return krqdtApplication;
			}).collect(Collectors.toList());
		return result;
	}

	private Optional<KrqdtApplication> findEntityByID(String appID) {
		String companyID = AppContexts.user().companyId();
		if(appID.equals("sample")) {
			appID = new NtsStatement("select top 1 APP_ID from KRQDT_APPLICATION where CID = @companyID order by INS_DATE desc", this.jdbcProxy())
					.paramString("companyID", companyID)
					.getSingle(rec -> rec.getString("APP_ID")).get();
		}
		
		String sql = "select a.EXCLUS_VER as aEXCLUS_VER, a.CONTRACT_CD as aCONTRACT_CD, a.CID as aCID, a.APP_ID as aAPP_ID, a.PRE_POST_ATR as aPRE_POST_ATR, " +
				"a.INPUT_DATE as aINPUT_DATE, a.ENTERED_PERSON_SID as aENTERED_PERSON_SID, " +
				"a.REASON_REVERSION as aREASON_REVERSION, a.APP_DATE as aAPP_DATE, a.FIXED_REASON as aFIXED_REASON, a.APP_REASON as aAPP_REASON, a.APP_TYPE as aAPP_TYPE, " +
				"a.APPLICANTS_SID as aAPPLICANTS_SID, a.APP_START_DATE as aAPP_START_DATE, a.APP_END_DATE as aAPP_END_DATE, a.STAMP_OPTION_ATR as aSTAMP_OPTION_ATR, " +
				"b.CONTRACT_CD as bCONTRACT_CD, b.CID as bCID, b.APP_ID as bAPP_ID, b.APP_DATE as bAPP_DATE, b.REFLECT_PLAN_STATE as bREFLECT_PLAN_STATE, b.REFLECT_PER_STATE as bREFLECT_PER_STATE, " +
				"b.REFLECT_PLAN_SCHE_REASON as bREFLECT_PLAN_SCHE_REASON, b.REFLECT_PLAN_TIME as bREFLECT_PLAN_TIME, " +
				"b.REFLECT_PER_SCHE_REASON as bREFLECT_PER_SCHE_REASON, b.REFLECT_PER_TIME as bREFLECT_PER_TIME, " +
				"b.CANCEL_PLAN_SCHE_REASON as bCANCEL_PLAN_SCHE_REASON, b.CANCEL_PLAN_TIME as bCANCEL_PLAN_TIME, " +
				"b.CANCEL_PER_SCHE_REASON as bCANCEL_PER_SCHE_REASON, b.CANCEL_PER_TIME as bCANCEL_PER_TIME " +
				"from KRQDT_APPLICATION a left join KRQDT_APP_REFLECT_STATE b " +
				"on a.CID = b.CID and a.APP_ID = b.APP_ID " +
				"where a.APP_ID = @appID";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("appID", appID)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		if(CollectionUtil.isEmpty(krqdtApplicationLst)) {
			return Optional.empty();
		}
		return Optional.of(krqdtApplicationLst.get(0));
	}

	@Override
	public List<Application> getByAppTypeList(List<String> employeeLst, GeneralDate startDate, GeneralDate endDate, List<ApplicationType> appTypeLst, 
			List<PrePostAtr> prePostAtrLst, List<StampRequestMode> stampRequestModeLst, List<OvertimeAppAtr> overtimeAppAtrLst) {
		String whereCondition = "";
		String connectAppTypeString = "";
		String connectStampString = "";
		String connectOvertimeString = "";
		if(!CollectionUtil.isEmpty(appTypeLst)) {
			connectAppTypeString = "select APP_ID from KRQDT_APPLICATION where APPLICANTS_SID in @employeeLst " +
					"and APP_START_DATE <= @endDate and APP_END_DATE >= @startDate " +
					"and APP_TYPE in @appTypeLst and PRE_POST_ATR in @prePostAtrLst";
		}
		if(!CollectionUtil.isEmpty(stampRequestModeLst)) {
			connectStampString = "select APP_ID from KRQDT_APPLICATION where APPLICANTS_SID in @employeeLst " +
					"and APP_START_DATE <= @endDate and APP_END_DATE >= @startDate " +
					"and STAMP_OPTION_ATR in @stampRequestModeLst and PRE_POST_ATR in @prePostAtrLst";
		}
		if(!CollectionUtil.isEmpty(overtimeAppAtrLst)) {
			connectOvertimeString = "select c.APP_ID from KRQDT_APPLICATION c left join KRQDT_APP_OVERTIME d on c.APP_ID = d.APP_ID " +
					"where c.APPLICANTS_SID in @employeeLst " +
					"and c.APP_START_DATE <= @endDate and c.APP_END_DATE >= @startDate " +
					"and c.PRE_POST_ATR in @prePostAtrLst and d.OVERTIME_ATR in @overtimeAppAtrLst";
		}
		whereCondition = Arrays.asList(connectAppTypeString, connectStampString, connectOvertimeString).stream()
				.filter(x -> Strings.isNotBlank(x)).collect(Collectors.joining(" union "));

		String sql = "select a.EXCLUS_VER as aEXCLUS_VER, a.CONTRACT_CD as aCONTRACT_CD, a.CID as aCID, a.APP_ID as aAPP_ID, a.PRE_POST_ATR as aPRE_POST_ATR, " +
				"a.INPUT_DATE as aINPUT_DATE, a.ENTERED_PERSON_SID as aENTERED_PERSON_SID, " +
				"a.REASON_REVERSION as aREASON_REVERSION, a.APP_DATE as aAPP_DATE, a.FIXED_REASON as aFIXED_REASON, a.APP_REASON as aAPP_REASON, a.APP_TYPE as aAPP_TYPE, " +
				"a.APPLICANTS_SID as aAPPLICANTS_SID, a.APP_START_DATE as aAPP_START_DATE, a.APP_END_DATE as aAPP_END_DATE, a.STAMP_OPTION_ATR as aSTAMP_OPTION_ATR, " +
				"b.CONTRACT_CD as bCONTRACT_CD, b.CID as bCID, b.APP_ID as bAPP_ID, b.APP_DATE as bAPP_DATE, b.REFLECT_PLAN_STATE as bREFLECT_PLAN_STATE, b.REFLECT_PER_STATE as bREFLECT_PER_STATE, " +
				"b.REFLECT_PLAN_SCHE_REASON as bREFLECT_PLAN_SCHE_REASON, b.REFLECT_PLAN_TIME as bREFLECT_PLAN_TIME, " +
				"b.REFLECT_PER_SCHE_REASON as bREFLECT_PER_SCHE_REASON, b.REFLECT_PER_TIME as bREFLECT_PER_TIME, " +
				"b.CANCEL_PLAN_SCHE_REASON as bCANCEL_PLAN_SCHE_REASON, b.CANCEL_PLAN_TIME as bCANCEL_PLAN_TIME, " +
				"b.CANCEL_PER_SCHE_REASON as bCANCEL_PER_SCHE_REASON, b.CANCEL_PER_TIME as bCANCEL_PER_TIME " +
				"from KRQDT_APPLICATION a left join KRQDT_APP_REFLECT_STATE b " +
				"on a.CID = b.CID and a.APP_ID = b.APP_ID " +
				"where a.APP_ID in (" + whereCondition + ")";
		NtsStatement ntsStatement = new NtsStatement(sql, this.jdbcProxy())
				.paramString("employeeLst", employeeLst)
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.paramInt("prePostAtrLst", prePostAtrLst.stream().map(x -> x.value).collect(Collectors.toList()));
		if(!CollectionUtil.isEmpty(appTypeLst)) {
			ntsStatement = ntsStatement.paramInt("appTypeLst", appTypeLst.stream().map(x -> x.value).collect(Collectors.toList()));
		}
		if(!CollectionUtil.isEmpty(stampRequestModeLst)) {
			ntsStatement = ntsStatement.paramInt("stampRequestModeLst", stampRequestModeLst.stream().map(x -> x.value).collect(Collectors.toList()));
		}
		if(!CollectionUtil.isEmpty(overtimeAppAtrLst)) {
			ntsStatement = ntsStatement.paramInt("overtimeAppAtrLst", overtimeAppAtrLst.stream().map(x -> x.value).collect(Collectors.toList()));
		}
		List<Map<String, Object>> mapLst = ntsStatement.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		if(CollectionUtil.isEmpty(krqdtApplicationLst)) {
			return Collections.emptyList();
		}
		return krqdtApplicationLst.stream().map(x -> x.toDomain()).collect(Collectors.toList());
	}

	/**
	 * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請未承認申請を取得.ドメインモデル「申請」を取得する
	 * @param sID
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Override
	public List<Application> getAppForKAF008(String sID, GeneralDate startDate, GeneralDate endDate) {
		String sql = "select a.EXCLUS_VER as aEXCLUS_VER, a.CONTRACT_CD as aCONTRACT_CD, a.CID as aCID, a.APP_ID as aAPP_ID, a.PRE_POST_ATR as aPRE_POST_ATR, " +
				"a.INPUT_DATE as aINPUT_DATE, a.ENTERED_PERSON_SID as aENTERED_PERSON_SID, " +
				"a.REASON_REVERSION as aREASON_REVERSION, a.APP_DATE as aAPP_DATE, a.FIXED_REASON as aFIXED_REASON, a.APP_REASON as aAPP_REASON, a.APP_TYPE as aAPP_TYPE, " +
				"a.APPLICANTS_SID as aAPPLICANTS_SID, a.APP_START_DATE as aAPP_START_DATE, a.APP_END_DATE as aAPP_END_DATE, a.STAMP_OPTION_ATR as aSTAMP_OPTION_ATR, " +
				"b.CONTRACT_CD as bCONTRACT_CD, b.CID as bCID, b.APP_ID as bAPP_ID, b.APP_DATE as bAPP_DATE, b.REFLECT_PLAN_STATE as bREFLECT_PLAN_STATE, b.REFLECT_PER_STATE as bREFLECT_PER_STATE, " +
				"b.REFLECT_PLAN_SCHE_REASON as bREFLECT_PLAN_SCHE_REASON, b.REFLECT_PLAN_TIME as bREFLECT_PLAN_TIME, " +
				"b.REFLECT_PER_SCHE_REASON as bREFLECT_PER_SCHE_REASON, b.REFLECT_PER_TIME as bREFLECT_PER_TIME, " +
				"b.CANCEL_PLAN_SCHE_REASON as bCANCEL_PLAN_SCHE_REASON, b.CANCEL_PLAN_TIME as bCANCEL_PLAN_TIME, " +
				"b.CANCEL_PER_SCHE_REASON as bCANCEL_PER_SCHE_REASON, b.CANCEL_PER_TIME as bCANCEL_PER_TIME " +
				"from KRQDT_APPLICATION a left join KRQDT_APP_REFLECT_STATE b " +
				"on a.CID = b.CID and a.APP_ID = b.APP_ID " +
				"where a.APPLICANTS_SID = @sid and a.APP_START_DATE <= @endDate and a.APP_END_DATE >= @startDate " +
				"and b.REFLECT_PER_STATE IN (0,1) and a.APP_TYPE IN (2,3,4,6,1,10) "+
				"order by a.INPUT_DATE DESC";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("sid", sID)
				.paramDate("startDate", startDate)
				.paramDate("endDate", endDate)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		if(CollectionUtil.isEmpty(krqdtApplicationLst)) {
			return Collections.emptyList();
		}
		return krqdtApplicationLst.stream().map(i -> i.toDomain()).collect(Collectors.toList());
	}

	@Override
	public List<Application> getAppReflected(String sid, GeneralDate appDate) {
		String sql = SELECT_MEMO
				+ "FROM KRQDT_APPLICATION a" 
				+ " join KRQDT_APP_REFLECT_STATE b"
				+ "  on a.APP_ID = b.APP_ID and  a.CID = b.CID"
				+ " WHERE  a.APPLICANTS_SID =  @sid "
				+ " AND a.APP_DATE = @appDate " 
				+ " AND b.REFLECT_PLAN_STATE = 2 " 
				+ " AND b.REFLECT_PER_STATE = 2 " 
				+ " AND b.APP_DATE = @appDate"
				+ " ORDER BY a.INPUT_DATE ASC";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("sid", sid)
				.paramDate("appDate", appDate)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		return krqdtApplicationLst.stream().map(c -> c.toDomain()).collect(Collectors.toList());
	}

	@Override
	public List<Application> getApplication(PrePostAtr prePostAtr, GeneralDateTime inputDate, GeneralDate appDate,
			ApplicationType appType, String employeeID) {
		String SELECT_APP_NR = "SELECT a FROM KrqdtApplication a" + " WHERE a.employeeID = :employeeID"
				+ " AND a.appDate = :appDate AND a.prePostAtr = :prePostAtr"
				+ " AND a.inputDate = :inputDate  AND a.appType = :appType";
		return this.queryProxy().query(SELECT_APP_NR, KrqdtApplication.class).setParameter("employeeID", employeeID)
				.setParameter("appDate", appDate)
				.setParameter("prePostAtr", prePostAtr.value)
				.setParameter("inputDate", inputDate)
				.setParameter("appType", appType.value).getList(x -> x.toDomain());
	}

	public List<Application> getApprSttByEmpPeriod(String employeeID, DatePeriod period) {
		String sql = "select a.EXCLUS_VER as aEXCLUS_VER, a.CONTRACT_CD as aCONTRACT_CD, a.CID as aCID, a.APP_ID as aAPP_ID, a.PRE_POST_ATR as aPRE_POST_ATR, " +
				"a.INPUT_DATE as aINPUT_DATE, a.ENTERED_PERSON_SID as aENTERED_PERSON_SID, " +
				"a.REASON_REVERSION as aREASON_REVERSION, a.APP_DATE as aAPP_DATE, a.FIXED_REASON as aFIXED_REASON, a.APP_REASON as aAPP_REASON, a.APP_TYPE as aAPP_TYPE, " +
				"a.APPLICANTS_SID as aAPPLICANTS_SID, a.APP_START_DATE as aAPP_START_DATE, a.APP_END_DATE as aAPP_END_DATE, a.STAMP_OPTION_ATR as aSTAMP_OPTION_ATR, " +
				"b.CONTRACT_CD as bCONTRACT_CD, b.CID as bCID, b.APP_ID as bAPP_ID, b.APP_DATE as bAPP_DATE, b.REFLECT_PLAN_STATE as bREFLECT_PLAN_STATE, b.REFLECT_PER_STATE as bREFLECT_PER_STATE, " +
				"b.REFLECT_PLAN_SCHE_REASON as bREFLECT_PLAN_SCHE_REASON, b.REFLECT_PLAN_TIME as bREFLECT_PLAN_TIME, " +
				"b.REFLECT_PER_SCHE_REASON as bREFLECT_PER_SCHE_REASON, b.REFLECT_PER_TIME as bREFLECT_PER_TIME, " +
				"b.CANCEL_PLAN_SCHE_REASON as bCANCEL_PLAN_SCHE_REASON, b.CANCEL_PLAN_TIME as bCANCEL_PLAN_TIME, " +
				"b.CANCEL_PER_SCHE_REASON as bCANCEL_PER_SCHE_REASON, b.CANCEL_PER_TIME as bCANCEL_PER_TIME " +
				"from KRQDT_APPLICATION a left join KRQDT_APP_REFLECT_STATE b " +
				"on a.CID = b.CID and a.APP_ID = b.APP_ID " +
				"where a.APPLICANTS_SID = @employeeID " + 
				"and a.APP_START_DATE <= @endDate " +
				"and a.APP_END_DATE >= @startDate " + 
				"and b.REFLECT_PER_STATE not in (3)";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("employeeID", employeeID)
				.paramDate("startDate", period.start())
				.paramDate("endDate", period.end())
				.getList(rec -> toObject(rec));
		return convertToEntity(mapLst).stream().map(x -> x.toDomain()).collect(Collectors.toList());
	}

	@Override
	public Optional<String> getNewestPreAppIDByEmpDate(String employeeID, GeneralDate date, ApplicationType appType) {
		String companyID = AppContexts.user().companyId();
		String sql = "select APP_ID from KRQDT_APPLICATION where CID = @companyID and APP_TYPE = @appType " +
				"and APPLICANTS_SID = @employeeID and APP_DATE = @date and PRE_POST_ATR = 0 order by INPUT_DATE desc";
		return new NtsStatement(sql, this.jdbcProxy())
				.paramString("companyID", companyID)
				.paramInt("appType", appType.value)
				.paramString("employeeID", employeeID)
				.paramDate("date", date)
				.getList(rec -> rec.getString("APP_ID"))
				.stream().findFirst();
	}
	
	//http://192.168.50.4:3000/issues/113816
	private static final String SELECT_BY_SIDS_DATEPERIOD_REFSTATUS = "SELECT a FROM KrqdtApplication a"
			+ " JOIN KrqdtAppReflectState ref ON a.pk.companyID = ref.pk.companyID  AND a.pk.appID = ref.pk.appID"
			+ " WHERE a.employeeID IN :sids "
			+ " AND a.appDate >= :startDate AND a.appDate <= :endDate"
			+ " AND ref.actualReflectStatus NOT IN :listReflecInfor" 
			+ " ORDER BY a.appDate ASC," 
			+ " a.prePostAtr DESC";

	@Override
	public Map<String, List<Application>> getMapListApplicationNew(List<String> sids, DatePeriod datePeriod,
			List<Integer> listReflecInfor) {
		
		if (listReflecInfor.isEmpty() || sids.isEmpty()) {
			return Collections.emptyMap();
		}
		
		List<Application> listApplication = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subSids -> {
			CollectionUtil.split(listReflecInfor, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subListReflecInfor -> {
				listApplication
						.addAll(this.queryProxy().query(SELECT_BY_SIDS_DATEPERIOD_REFSTATUS, KrqdtApplication.class)
								.setParameter("sids", subSids).setParameter("startDate", datePeriod.start())
								.setParameter("endDate", datePeriod.end())
								.setParameter("listReflecInfor", subListReflecInfor).getList(x -> x.toDomain()));
			});
		});
		
		Map<String, List<Application>> returnMap = new HashMap<>();
		sids.stream().forEach(sid -> {
			List<Application> listApp = listApplication.stream()
					.filter(app -> app.getEmployeeID().equalsIgnoreCase(sid)).collect(Collectors.toList());
			returnMap.put(sid, listApp);
		});
		return returnMap;
	}

	// get application by list employee and date period
	@Override
	public List<Application> getAllApplication(List<String> sID, DatePeriod period) {
		if (CollectionUtil.isEmpty(sID)) return Collections.emptyList();
		String sql = SELECT_MEMO
				+ "FROM KRQDT_APPLICATION a " 
				+ "join KRQDT_APP_REFLECT_STATE b "
				+ "on a.APP_ID = b.APP_ID and  a.CID = b.CID "
				+ "WHERE a.APPLICANTS_SID IN @sID "
				+ "AND a.APP_DATE >= @startDate "
				+ "AND a.APP_DATE <= @endDate "
				+ " ORDER BY a.INPUT_DATE ASC";
		
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("sID", sID)
				.paramDate("startDate", period.start())
				.paramDate("endDate", period.end())
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		return krqdtApplicationLst.stream().map(c -> c.toDomain()).collect(Collectors.toList());
	}

	@Override
	public List<Application> findByIDLst(List<String> appIDLst) {
		String sql = "select a.EXCLUS_VER as aEXCLUS_VER, a.CONTRACT_CD as aCONTRACT_CD, a.CID as aCID, a.APP_ID as aAPP_ID, a.PRE_POST_ATR as aPRE_POST_ATR, " +
				"a.INPUT_DATE as aINPUT_DATE, a.ENTERED_PERSON_SID as aENTERED_PERSON_SID, " +
				"a.REASON_REVERSION as aREASON_REVERSION, a.APP_DATE as aAPP_DATE, a.FIXED_REASON as aFIXED_REASON, a.APP_REASON as aAPP_REASON, a.APP_TYPE as aAPP_TYPE, " +
				"a.APPLICANTS_SID as aAPPLICANTS_SID, a.APP_START_DATE as aAPP_START_DATE, a.APP_END_DATE as aAPP_END_DATE, a.STAMP_OPTION_ATR as aSTAMP_OPTION_ATR, " +
				"b.CONTRACT_CD as bCONTRACT_CD, b.CID as bCID, b.APP_ID as bAPP_ID, b.APP_DATE as bAPP_DATE, b.REFLECT_PLAN_STATE as bREFLECT_PLAN_STATE, b.REFLECT_PER_STATE as bREFLECT_PER_STATE, " +
				"b.REFLECT_PLAN_SCHE_REASON as bREFLECT_PLAN_SCHE_REASON, b.REFLECT_PLAN_TIME as bREFLECT_PLAN_TIME, " +
				"b.REFLECT_PER_SCHE_REASON as bREFLECT_PER_SCHE_REASON, b.REFLECT_PER_TIME as bREFLECT_PER_TIME, " +
				"b.CANCEL_PLAN_SCHE_REASON as bCANCEL_PLAN_SCHE_REASON, b.CANCEL_PLAN_TIME as bCANCEL_PLAN_TIME, " +
				"b.CANCEL_PER_SCHE_REASON as bCANCEL_PER_SCHE_REASON, b.CANCEL_PER_TIME as bCANCEL_PER_TIME " +
				"from KRQDT_APPLICATION a left join KRQDT_APP_REFLECT_STATE b " +
				"on a.CID = b.CID and a.APP_ID = b.APP_ID " +
				"where a.APP_ID in @appIDLst";
		List<Map<String, Object>> mapLst = new NtsStatement(sql, this.jdbcProxy())
				.paramString("appIDLst", appIDLst)
				.getList(rec -> toObject(rec));
		List<KrqdtApplication> krqdtApplicationLst = convertToEntity(mapLst);
		return krqdtApplicationLst.stream().map(x -> x.toDomain()).collect(Collectors.toList());
	}
	
	//申請データを取得する
	@Override
	public List<Application> getAllApplicationByAppTypeAndPrePostAtr(String employeeID, int appType,
			GeneralDate appDate, int prePostAtr) {
		
		return this.queryProxy().query(SELECT_BY_SID_PRE_POST_ATR_APPTYPE, KrqdtApplication.class)
				.setParameter("employeeID", employeeID)
				.setParameter("appDate", appDate).setParameter("appType", appType)
				.setParameter("prePostAtr", prePostAtr).getList(c -> c.toDomain());
	}
}
