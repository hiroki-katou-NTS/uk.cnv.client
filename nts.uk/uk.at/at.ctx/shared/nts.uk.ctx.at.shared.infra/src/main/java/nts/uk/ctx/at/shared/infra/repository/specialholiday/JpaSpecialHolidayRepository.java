package nts.uk.ctx.at.shared.infra.repository.specialholiday;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.TargetItem;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.AgeRange;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.AgeStandard;
import nts.uk.ctx.at.shared.dom.specialholiday.grantcondition.SpecialLeaveRestriction;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.FixGrantDate;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDate;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantDateTblReferenceGrant;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantRegular;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.GrantedDays;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.PeriodGrantDate;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.RegularGrantDays;
import nts.uk.ctx.at.shared.dom.specialholiday.grantinformation.TypeTime;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.GrantDeadline;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.SpecialVacationDeadline;
import nts.uk.ctx.at.shared.dom.specialholiday.periodinformation.TimeLimitSpecification;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.KshmtHdsp;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.KshmtHdspFrameAbsence;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.KshmtHdspFrameHdsp;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.KshmtHdspGrantPeriod;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.KshmtHdspGrantPeriodPK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.KshstSpecialHolidayPK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.KshstSphdAbsencePK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.KshstSphdSpecLeavePK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantcondition.KshmtHdspCondCls;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantcondition.KshmtHdspCondEmp;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantcondition.KshstSpecClsPK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantcondition.KshstSpecEmpPK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantcondition.KshstSpecialLeaveRestriction;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantcondition.KshstSpecialLeaveRestrictionPK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantinformation.KshmtHdspGrant;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.grantinformation.KshmtHdspGrantPK;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.periodinformation.KshmtHdspGrantDeadline;
import nts.uk.ctx.at.shared.infra.entity.specialholiday.periodinformation.KshmtHdspGrantDeadlinePK;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.calendar.MonthDay;

/**
 * Jpa Special Holiday Repository
 *
 * @author tanlv
 *
 */
@Stateless
public class JpaSpecialHolidayRepository extends JpaRepository implements SpecialHolidayRepository {
	private final static String SELECT_SPHD_BY_COMPANY_ID_QUERY = "SELECT e.pk.companyId, e.pk.specialHolidayCode, e.specialHolidayName, e.autoGrant, e.memo FROM KshmtHdsp e "
			+ "WHERE e.pk.companyId = :companyId " + "ORDER BY e.pk.specialHolidayCode ASC";

	private final static String SELECT_SPHD_BY_CODE_QUERY = "SELECT sphd.CID, sphd.SPHD_CD, sphd.SPHD_NAME, sphd.SPHD_AUTO_GRANT, sphd.MEMO,"
			+ " sphd.CONTINUOUS_ACQUISITION, sphd.GRANT_TIMING, sphd.GRANT_DATE, "
			+ " pe.TIME_CSL_METHOD, gpe.PERIOD_START, gpe.PERIOD_END, gpe.GRANTED_DAYS AS pGRANTED_DAYS, pe.DEADLINE_MONTHS, pe.DEADLINE_YEARS, pe.LIMIT_CARRYOVER_DAYS,"
			+ " re.RESTRICTION_CLS, re.AGE_LIMIT, re.GENDER_REST, re.REST_EMP, re.AGE_CRITERIA_CLS, re.AGE_BASE_DATE, re.AGE_LOWER_LIMIT, re.AGE_HIGHER_LIMIT, re.GENDER,"
			+ " grantInfo.GRANT_MD, grantInfo.GRANTED_DAYS" + " FROM KSHMT_HDSP sphd"
			+ " LEFT JOIN KSHMT_HDSP_GRANT_DEADLINE pe" + " ON sphd.CID = pe.CID AND sphd.SPHD_CD = pe.SPHD_CD"
			+ " LEFT JOIN KSHMT_HDSP_CONDITION re" + " ON sphd.CID = re.CID AND sphd.SPHD_CD = re.SPHD_CD"
			+ " LEFT JOIN KSHMT_HDSP_GRANT_PERIOD gpe" + " ON sphd.CID = gpe.CID AND sphd.SPHD_CD = gpe.SPHD_CD "
			+ " LEFT JOIN KSHMT_HDSP_GRANT grantInfo"
			+ " ON sphd.CID = grantInfo.CID AND sphd.SPHD_CD = grantInfo.SPHD_CD "
			+ " WHERE sphd.CID = ? AND sphd.SPHD_CD = ?" + " ORDER BY sphd.SPHD_CD";

	private final static String SELECT_SPHD_BY_LIST_CODE = "SELECT e.pk.companyId, e.pk.specialHolidayCode, e.specialHolidayName, e.autoGrant, e.memo FROM KshmtHdsp e "
			+ "WHERE e.pk.companyId = :companyId " + " AND e.pk.specialHolidayCode IN :specialHolidayCodes"
			+ " ORDER BY e.pk.specialHolidayCode";

	private final static String SELECT_SPHD_ABSENCE_BY_CODE = "SELECT a FROM KshmtHdspFrameAbsence a "
			+ "WHERE a.pk.companyId = :companyID "
			+ "AND a.pk.specialHolidayCode = :specialHolidayCD ORDER BY a.pk.absFameNo";

	private final static String SELECT_SPHD_SPEC_LEAVE = "SELECT a FROM KshmtHdspFrameHdsp a "
			+ "WHERE a.pk.companyId = :companyID "
			+ "AND a.pk.specialHolidayCode = :specialHolidayCD ORDER BY a.pk.sphdNo";

	private final static String SELECT_SPEC_CLS = "SELECT a FROM KshmtHdspCondCls a "
			+ "WHERE a.pk.companyId = :companyID " + "AND a.pk.specialHolidayCode = :specialHolidayCD";

	private final static String SELECT_SPEC_EMP = "SELECT a FROM KshmtHdspCondEmp a "
			+ "WHERE a.pk.companyId = :companyID " + "AND a.pk.specialHolidayCode = :specialHolidayCD";

//	private final static String FIND_GRANT_REGULAR = "SELECT e,f FROM KshmtHdspGrant e "
//			+ " LEFT JOIN KshmtHdspGrantTbl f "
//			+ " ON e.pk.companyId = f.pk.companyId AND e.pk.specialHolidayCode = f.pk.specialHolidayCode";

//	private final static String FIND_GRANT_REGULAR_TEST = FIND_GRANT_REGULAR
//			+ " WHERE e.pk.companyId = 'hung-test'";

//	private final static String FIND_SPECICAL_LEAVE =  "SELECT g,h,i FROM KshstSpecialLeaveRestriction g"
//			+ " LEFT JOIN KshmtHdspCondEmp h"
//			+ " ON g.pk.companyId = h.pk.companyId AND g.pk.specialHolidayCode = h.pk.specialHolidayCode"
//			+ "	LEFT JOIN KshmtHdspCondCls i"
//			+ " ON i.pk.companyId = i.pk.companyId AND i.pk.specialHolidayCode = i.pk.specialHolidayCode";

//	private final static String FIND_BY_CID = "SELECT a, b, c, d, k, l "
//			+ " FROM KshmtHdsp a"
//			+ " LEFT JOIN ("
//			+ FIND_GRANT_REGULAR
//			+ " ) b"
//			+ " ON a.pk.companyId = b.pk.companyId AND a.pk.specialHolidayCode = b.pk.specialHolidayCode"
//			+ " LEFT JOIN KshmtHdspPeriod c"
//			+ " ON a.pk.companyId = c.pk.companyId AND a.pk.specialHolidayCode = c.pk.specialHolidayCode"
//			+ " LEFT JOIN ("
//			+ FIND_SPECICAL_LEAVE
//			+ " ) d"
//			+ " ON a.pk.companyId = d.pk.companyId AND a.pk.specialHolidayCode = d.pk.specialHolidayCode"
//			+ " LEFT JOIN KshmtHdspFrameHdsp k"
//			+ " ON a.pk.companyId = k.pk.companyId AND a.pk.specialHolidayCode = k.pk.specialHolidayCode"
//			+ " LEFT JOIN KshmtHdspFrameAbsence l"
//			+ " ON a.pk.companyId = l.pk.companyId AND a.pk.specialHolidayCode = l.pk.specialHolidayCode"
//			+ " WHERE a.pk.companyId = :companyId ORDER BY e.pk.specialHolidayCode ASC";

	private final static String DELETE_SPECICAL_LEAVE_RESTRICTION = "DELETE FROM KshstSpecialLeaveRestriction a "
			+ "WHERE a.pk.companyId = :companyID " + "AND a.pk.specialHolidayCode = :specialHolidayCD";

	private final static String DELETE_SPHD_ABSENCE = "DELETE FROM KshmtHdspFrameAbsence a "
			+ "WHERE a.pk.companyId = :companyID " + "AND a.pk.specialHolidayCode = :specialHolidayCD";

	private final static String DELETE_SPHD_SPEC_LEAVE = "DELETE FROM KshmtHdspFrameHdsp a "
			+ "WHERE a.pk.companyId = :companyID " + "AND a.pk.specialHolidayCode = :specialHolidayCD";

	private final static String DELETE_SPEC_CLS = "DELETE FROM KshmtHdspCondCls a "
			+ "WHERE a.pk.companyId = :companyID " + "AND a.pk.specialHolidayCode = :specialHolidayCD";

	private final static String DELETE_SPEC_EMP = "DELETE FROM KshmtHdspCondEmp a "
			+ "WHERE a.pk.companyId = :companyID " + "AND a.pk.specialHolidayCode = :specialHolidayCD";

	private final static String DELETE_GRANT_DATE = "DELETE FROM KshmtHdspGrantTbl a "
			+ "WHERE a.pk.companyId = :companyID " + "AND a.pk.specialHolidayCode = :specialHolidayCD";

	private final static String DELETE_All_ELAPSE = "DELETE FROM KshmtHdspElapsedYearsTbl e "
			+ "WHERE e.pk.companyId =:companyID " + "AND e.pk.specialHolidayCode =:specialHolidayCD ";

	/**
	 * For delete releated domain of KDR001 (team G)
	 */
	private final static String DELETE_SPEC_HD = "DELETE FROM KfnmtRptRemlstOuthdsp a "
			+ "WHERE a.kfnmtSpecialHolidayPk.cid = :companyID "
			+ "AND a.kfnmtSpecialHolidayPk.specialCd = :specialHolidayCD";

	private String QUEYRY_BY_ABSFRAMENO = "SELECT c FROM KshmtHdspFrameAbsence c" + " WHERE c.pk.companyId = :companyId"
			+ " AND c.pk.absFameNo = :absFameNo";

	private String QUERY_BY_SPECLEAVE = "SELECT c FROM KshmtHdspFrameHdsp c" + " WHERE c.pk.companyId = :companyId"
			+ " AND c.pk.sphdNo = :sphdNo";

	private final static String SELECT_SPHD_BY_COMPANY_AND_NO = "SELECT e.pk.companyId, e.pk.specialHolidayCode, e.specialHolidayName, e.autoGrant, e.memo FROM KshmtHdsp e "
			+ "WHERE e.pk.companyId = :companyId " + "AND e.pk.specialHolidayCode IN :specialHolidayCode";

	private SpecialHoliday createDomainFromEntity(NtsResultRecord c) {

		String companyId = c.getString("CID");
		int specialHolidayCode = c.getInt("SPHD_CD");
		String specialHolidayName = c.getString("SPHD_NAME");
		int autoGrant = c.getInt("SPHD_AUTO_GRANT");
		String memo = c.getString("MEMO");
		int grantTiming = c.getInt("GRANT_TIMING");
		Integer grantDate = c.getInt("GRANT_DATE");

		// KSHMT_HDSP_GRANT
		Integer grant_md = c.getInt("GRANT_MD");
		Integer grantedDays = c.getInt("GRANTED_DAYS");
		if (grantedDays == null) grantedDays = 0;
		// KSHMT_HDSP_GRANT_PERIOD
		GeneralDate startDate = c.getGeneralDate("PERIOD_START");
		GeneralDate endDate = c.getGeneralDate("PERIOD_END");
		Integer pGrantedDays = c.getInt("pGRANTED_DAYS");

		Integer timeMethod = c.getInt("TIME_CSL_METHOD");
		Integer deadlineMonths = c.getInt("DEADLINE_MONTHS");
		Integer deadlineYears = c.getInt("DEADLINE_YEARS");
		Integer limitCarryoverDays = c.getInt("LIMIT_CARRYOVER_DAYS");

		// 利用条件
		int restrictionCls = c.getInt("RESTRICTION_CLS") != null ? c.getInt("RESTRICTION_CLS") : 0;
		int ageLimit = c.getInt("AGE_LIMIT") != null ? c.getInt("AGE_LIMIT") : 0;
		int genderRest = c.getInt("GENDER_REST") != null ? c.getInt("GENDER_REST") : 0;
		int restEmp = c.getInt("REST_EMP") != null ? c.getInt("REST_EMP") : 0;
		int ageCriteriaCls = c.getInt("AGE_CRITERIA_CLS") != null ? c.getInt("AGE_CRITERIA_CLS") : 0;
		Integer ageBaseDateValue = c.getInt("AGE_BASE_DATE") != null ? c.getInt("AGE_BASE_DATE") : null;
		MonthDay ageBaseDate = null;
		if (ageBaseDateValue != null) {
			ageBaseDate = new MonthDay(ageBaseDateValue / 100, ageBaseDateValue % 100);
		}
		int ageLowerLimit = c.getInt("AGE_LOWER_LIMIT") != null ? c.getInt("AGE_LOWER_LIMIT") : 0;
		int ageHigherLimit = c.getInt("AGE_HIGHER_LIMIT") != null ? c.getInt("AGE_HIGHER_LIMIT") : 0;
		int gender = c.getInt("GENDER") != null ? c.getInt("GENDER") : 1;
		int continuousAcquisition = c.getInt("CONTINUOUS_ACQUISITION");

		Optional<GrantDateTblReferenceGrant> grantPeriodic = Optional.empty();
		Optional<FixGrantDate> fixGrantDate = Optional.empty();
		Optional<PeriodGrantDate> periodGrantDate = Optional.empty();

		switch (grantTiming) { // TypeTime
		case 1:// 付与テーブルを参照して付与する
			/*
			 * 期限 = KSHMT_HDSP_GRANT_DEADLINE
			 */
			grantPeriodic = Optional
					.of(new GrantDateTblReferenceGrant(createGrantDeadline(timeMethod, deadlineMonths, deadlineYears, limitCarryoverDays)));
			break;
		case 2:// 指定日に付与する
			/*
			 * 付与日数 = KSHMT_HDSP_GRANT.GRANTED_DAYS(KSHMT_HDSP_GRANT) 期限 =
			 * KSHMT_HDSP_GRANT_DEADLINE 付与月日 =
			 * KSHMT_HDSP_GRANT.GRANTED_MD(KSHMT_HDSP_GRANT)
			 */
			GrantDeadline deadline = createGrantDeadline(timeMethod, deadlineMonths, deadlineYears, limitCarryoverDays);
			fixGrantDate = Optional.of(
					FixGrantDate.createFromJavaType(companyId, specialHolidayCode, grantedDays, deadline, grant_md));
			break;
		case 3:// 期間で付与する
			/*
			 * 期間=KSHMT_HDSP_GRANT_PERIOD 付与日数=KSHMT_HDSP_GRANT_PERIOD
			 */
			/** 期間 */
			if (startDate == null || endDate == null || pGrantedDays == null) {
				periodGrantDate = Optional.empty();
				break;
			}
			DatePeriod period = new DatePeriod(startDate, endDate); // 要確認 初期値

			/** 期間付与 */
			periodGrantDate = Optional.of(PeriodGrantDate.of(
					/** 期間 */
					period
					/** 付与日数 */
					, RegularGrantDays.of(
							/** 付与日数.付与日数 */
							new GrantedDays(pGrantedDays))));

			break;
		}

		GrantRegular grantRegular = GrantRegular.of(EnumAdaptor.valueOf(grantTiming, TypeTime.class),
				grantDate != null ? Optional.of(EnumAdaptor.valueOf(grantDate, GrantDate.class)) : Optional.empty(),
				fixGrantDate, grantPeriodic, periodGrantDate);

		AgeStandard ageStandard = AgeStandard.createFromJavaType(ageCriteriaCls, ageBaseDate);
		AgeRange ageRange = AgeRange.createFromJavaType(ageLowerLimit, ageHigherLimit);
		SpecialLeaveRestriction specialLeaveRestriction = SpecialLeaveRestriction.createFromJavaType(companyId,
				specialHolidayCode, restrictionCls, ageLimit, genderRest, restEmp, ageStandard, ageRange, gender);

		return SpecialHoliday.of(companyId, specialHolidayCode, specialHolidayName, grantRegular,
				specialLeaveRestriction, new TargetItem(), autoGrant, memo,
				continuousAcquisition == 0 ? NotUseAtr.NOT_USE : NotUseAtr.USE);
	}

	private GrantDeadline createGrantDeadline(Integer timeMethod, Integer deadlineMonths, Integer deadlineYears,
			Integer limitCarryoverDays) {

		Optional<SpecialVacationDeadline> expirationDate = Optional.empty();
		if (timeMethod == 1)
			expirationDate = Optional.of(SpecialVacationDeadline.createFromJavaType(deadlineMonths, deadlineYears));

		return GrantDeadline.createFromJavaType(timeMethod, expirationDate, limitCarryoverDays);
	}

	private SpecialHoliday createSphdDomainFromEntity(Object[] c) {
		String companyId = String.valueOf(c[0]);
		int specialHolidayCode = Integer.parseInt(String.valueOf(c[1]));
		String specialHolidayName = String.valueOf(c[2]);
		int autoGrant = Integer.parseInt(String.valueOf(c[3]));
		String memo = String.valueOf(c[4]);

		return SpecialHoliday.createFromJavaType(companyId, specialHolidayCode, specialHolidayName, autoGrant, memo);
	}

	private KshmtHdsp createSpecialHolidayFromDomain(SpecialHoliday domain) {

		int grantDate = 0;
		if (domain.getGrantRegular().getGrantDate().isPresent()) {
			/** 特別休暇.付与・期限情報.付与基準日 */
			grantDate = domain.getGrantRegular().getGrantDate().get().value;
		}

		KshstSpecialHolidayPK pk = new KshstSpecialHolidayPK(domain.getCompanyId(), domain.getSpecialHolidayCode().v());
		return new KshmtHdsp(pk, domain.getSpecialHolidayName().v(), domain.getAutoGrant().value, /* 自動付与区分 */
				domain.getContinuousAcquisition().value, /* 連続で取得する */
				grantDate, /* 付与基準日 */
				domain.getMemo().v(), domain.getGrantRegular().getTypeTime().value);
	}

	private List<KshmtHdspFrameAbsence> createKshstSphdAbsenceLst(SpecialHoliday domain) {
		String companyID = domain.getCompanyId();
		int specialHolidayCD = domain.getSpecialHolidayCode().v();
		List<KshmtHdspFrameAbsence> kshstSphdAbsenceLst = domain.getTargetItem().getAbsenceFrameNo().stream()
				.map(x -> new KshmtHdspFrameAbsence(new KshstSphdAbsencePK(companyID, specialHolidayCD, x)))
				.collect(Collectors.toList());
		return kshstSphdAbsenceLst;
	}

	private List<KshmtHdspFrameHdsp> createKshstSphdSpecLeaveLst(SpecialHoliday domain) {
		String companyID = domain.getCompanyId();
		int specialHolidayCD = domain.getSpecialHolidayCode().v();
		List<KshmtHdspFrameHdsp> kshstSphdSpecLeaveLst = domain.getTargetItem().getFrameNo().stream()
				.map(x -> new KshmtHdspFrameHdsp(new KshstSphdSpecLeavePK(companyID, specialHolidayCD, x)))
				.collect(Collectors.toList());
		return kshstSphdSpecLeaveLst;
	}

//	private KshmtHdspGrant createKshstGrantRegular(SpecialHoliday domain) {
//
//		boolean isAutoGrant = domain.getAutoGrant().value == 0 ? false : true;
//		int typeTime = TypeTime.REFER_GRANT_DATE_TBL.value, grantDate = GrantDate.EMP_GRANT_DATE.value,
//				interval = 0, grantedDays = 0;
//		KshmtHdspGrant entity = new KshmtHdspGrant();
//		entity.pk = new KshstGrantRegularPK(domain.getCompanyId(), domain.getSpecialHolidayCode().v());
//		// update document ver 32
//		if (isAutoGrant) {
//			typeTime = domain.getGrantRegular().getTypeTime().value;
//			grantDate = domain.getGrantRegular().getGrantDate().get().value;
////			interval = domain.getGrantRegular().getGrantTime().getFixGrantDate().getInterval().v();
//			if ( domain.getGrantRegular().getFixGrantDate().isPresent() ) {
//				grantedDays = domain.getGrantRegular().getFixGrantDate().get().getGrantDays().getGrantDays().v();
//			}
//		}
//		entity.typeTime = typeTime;
//		entity.grantDate = grantDate;
//		entity.interval = interval;
//		entity.grantedDays = grantedDays;
//
//		return entity;
//	}

	// KshmtHdspGrant
	private KshmtHdspGrant createKshmtHdspGrant(SpecialHoliday domain) {

		boolean isAutoGrant = domain.getAutoGrant().value == 0 ? false : true;
		int typeTime = TypeTime.REFER_GRANT_DATE_TBL.value, grantDate = GrantDate.EMP_GRANT_DATE.value, interval = 0,
				grantedDays = 0;
		KshmtHdspGrant entity = new KshmtHdspGrant();
		entity.pk = new KshmtHdspGrantPK(domain.getCompanyId(), domain.getSpecialHolidayCode().v());
		// update document ver 32
		if (isAutoGrant) {
			typeTime = domain.getGrantRegular().getTypeTime().value;
			// ver 33: typeTime == 2 || typeTime == 1
			if (domain.getGrantRegular().getGrantDate().isPresent())
				grantDate = domain.getGrantRegular().getGrantDate().get().value;
//			interval = domain.getGrantRegular().getGrantTime().getFixGrantDate().getInterval().v();
			// ver 33: typeTime == 2
			if (domain.getGrantRegular().getFixGrantDate().isPresent()) {
				grantedDays = domain.getGrantRegular().getFixGrantDate().get().getGrantDays().getGrantDays().v();
				if (domain.getGrantRegular().getFixGrantDate().get().getGrantMonthDay().isPresent()) {
					/** 特別休暇.付与・期限情報.指定日付与.付与月日. */
					int month = domain.getGrantRegular().getFixGrantDate().get().getGrantMonthDay().get().getMonth();

					/** 特別休暇.付与・期限情報.指定日付与.付与月日. */
					int day = domain.getGrantRegular().getFixGrantDate().get().getGrantMonthDay().get().getDay();
					entity.grantMd = month * 100 + day;
				}
			}
		}

		entity.grantedDays = grantedDays;

		return entity;
	}

	private KshmtHdspGrantDeadline createKshmtHdspGrantDeadline(SpecialHoliday domain) {

		KshmtHdspGrantDeadline entity = new KshmtHdspGrantDeadline();
		boolean isAutoGrant = domain.getAutoGrant().value == 0 ? false : true;
		int timeSpecifyMethod = TimeLimitSpecification.INDEFINITE_PERIOD.value, limitCarryoverDays = 999;
		entity.pk = new KshmtHdspGrantDeadlinePK(domain.getCompanyId(), domain.getSpecialHolidayCode().v());

		if (isAutoGrant) {
			if (domain.getGrantRegular().getGrantPeriodic().isPresent()) {
				timeSpecifyMethod = domain.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().getTimeSpecifyMethod().value;
			}
			limitCarryoverDays = 0;

			if (domain.getGrantRegular().getFixGrantDate().isPresent()) {
				if (domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getLimitAccumulationDays()
						.isPresent()) {
					if (domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getLimitAccumulationDays()
							.get().getLimitCarryoverDays().isPresent()) {
						/** 特別休暇.付与・期限情報.指定日付与.期限.蓄積上限.繰越上限日数 */
						limitCarryoverDays = domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic()
								.getLimitAccumulationDays().get().getLimitCarryoverDays().get().v();
					}
				}
			}
		}

		entity.timeMethod = timeSpecifyMethod;
//		if (domain.getGrantRegular().getPeriodGrantDate().isPresent()){
//			/** 特別休暇.付与・期限情報.期間付与.期間 */
//			entity.startDate = domain.getGrantRegular().getPeriodGrantDate().get().getPeriod().start().month() * 100
//					+ domain.getGrantRegular().getPeriodGrantDate().get().getPeriod().start().day();
//			entity.endDate = domain.getGrantRegular().getPeriodGrantDate().get().getPeriod().end().month() * 100
//					+ domain.getGrantRegular().getPeriodGrantDate().get().getPeriod().end().day();
//		}
		// ver 33: typeTime == 2
		if (domain.getGrantRegular().getFixGrantDate().isPresent()) {
			/** 特別休暇.付与・期限情報.指定日付与.期限.期限指定方法 */
			timeSpecifyMethod = domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic()
					.getTimeSpecifyMethod().value;
			if (domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getLimitAccumulationDays()
					.isPresent()
					&& domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getLimitAccumulationDays()
							.get().getLimitCarryoverDays().isPresent()) {
				limitCarryoverDays = domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic()
						.getLimitAccumulationDays().get().getLimitCarryoverDays().get().v();
			}
			if (domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getExpirationDate().isPresent()) {
				/** 特別休暇.付与・期限情報.指定日付与.期限.有効期限.月数 */
				entity.deadlineMonths = domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic()
						.getExpirationDate().get().getMonths().v();
				/** 特別休暇.付与・期限情報.指定日付与.期限.有効期限.年数 */
				entity.deadlineYears = domain.getGrantRegular().getFixGrantDate().get().getGrantPeriodic()
						.getExpirationDate().get().getYears().v();
			}
		}
		// ver 33: typeTime == 1
		if (domain.getGrantRegular().getGrantPeriodic().isPresent()) {
			timeSpecifyMethod = domain.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().getTimeSpecifyMethod().value;
			if (domain.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().getLimitAccumulationDays().isPresent()
					&& domain.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().getLimitAccumulationDays().get()
							.getLimitCarryoverDays().isPresent()) {
				limitCarryoverDays = domain.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().getLimitAccumulationDays().get()
						.getLimitCarryoverDays().get().v();
			}
			if (domain.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().getExpirationDate().isPresent()) {
				entity.deadlineMonths = domain.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().getExpirationDate().get()
						.getMonths().v();
				entity.deadlineYears = domain.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().getExpirationDate().get()
						.getYears().v();
			}
		}
		entity.timeMethod = timeSpecifyMethod;
		entity.limitCarryoverDays = limitCarryoverDays;

		return entity;
	}

	private KshstSpecialLeaveRestriction createKshstSpecialLeaveRestriction(SpecialHoliday domain) {
		MonthDay ageBaseDate = domain.getSpecialLeaveRestriction().getAgeStandard().getAgeBaseDate();
		return new KshstSpecialLeaveRestriction(
				new KshstSpecialLeaveRestrictionPK(domain.getCompanyId(), domain.getSpecialHolidayCode().v()),
				domain.getSpecialLeaveRestriction().getRestrictionCls().value,
				domain.getSpecialLeaveRestriction().getAgeLimit().value,
				domain.getSpecialLeaveRestriction().getGenderRest().value,
				domain.getSpecialLeaveRestriction().getRestEmp().value,
				domain.getSpecialLeaveRestriction().getAgeStandard().getAgeCriteriaCls().value,
				ageBaseDate.getMonth() * 100 + ageBaseDate.getDay(),
				domain.getSpecialLeaveRestriction().getAgeRange() != null
						? (domain.getSpecialLeaveRestriction().getAgeRange().getAgeLowerLimit() != null
								? domain.getSpecialLeaveRestriction().getAgeRange().getAgeLowerLimit().v()
								: 0)
						: 0,
				domain.getSpecialLeaveRestriction().getAgeRange() != null
						? (domain.getSpecialLeaveRestriction().getAgeRange().getAgeHigherLimit() != null
								? domain.getSpecialLeaveRestriction().getAgeRange().getAgeHigherLimit().v()
								: 0)
						: 0,
				domain.getSpecialLeaveRestriction().getGender().value);
	}

	private List<KshmtHdspCondCls> createKshstSpecClsLst(SpecialHoliday domain) {
		String companyID = domain.getCompanyId();
		int specialHolidayCD = domain.getSpecialHolidayCode().v();
		return domain.getSpecialLeaveRestriction().getListCls().stream()
				.map(x -> new KshmtHdspCondCls(new KshstSpecClsPK(companyID, specialHolidayCD, x)))
				.collect(Collectors.toList());
	}

	private List<KshmtHdspCondEmp> createKshstSpecEmpLst(SpecialHoliday domain) {
		String companyID = domain.getCompanyId();
		int specialHolidayCD = domain.getSpecialHolidayCode().v();
		return domain.getSpecialLeaveRestriction().getListEmp().stream()
				.map(x -> new KshmtHdspCondEmp(new KshstSpecEmpPK(companyID, specialHolidayCD, x)))
				.collect(Collectors.toList());
	}

	// to addFunction: insert data to KSHMT_HDSP_GRANT_PERIOD table
	private KshmtHdspGrantPeriod createKshmtHdspGrantPeriod(SpecialHoliday domain) {
		String companyId = domain.getCompanyId();
		int specialHolidayCD = domain.getSpecialHolidayCode().v();

		PeriodGrantDate periodGrantDate = domain.getGrantRegular().getPeriodGrantDate().get();
		String dateFormat = "yyyy/MM/dd";

		return new KshmtHdspGrantPeriod(new KshmtHdspGrantPeriodPK(companyId, specialHolidayCD),
				periodGrantDate.getGrantDays().getGrantDays().v(),
				GeneralDate.fromString(periodGrantDate.getPeriod().start().toString(), dateFormat),
				GeneralDate.fromString(periodGrantDate.getPeriod().end().toString(), dateFormat));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<SpecialHoliday> findByCompanyId(String companyId) {
		return this.queryProxy().query(SELECT_SPHD_BY_COMPANY_ID_QUERY, Object[].class)
				.setParameter("companyId", companyId).getList(c -> {
					return createSphdDomainFromEntity(c);
				});
	}

	@SneakyThrows
	private Optional<SpecialHoliday> findByCode(String companyId, int specialHolidayCode) {

		try (PreparedStatement stmt = this.connection().prepareStatement(SELECT_SPHD_BY_CODE_QUERY)) {

			stmt.setString(1, companyId);
			stmt.setInt(2, specialHolidayCode);

			return new NtsResultSet(stmt.executeQuery()).getSingle(x -> {
				return createDomainFromEntity(x);
			});
		}
	}

	@Override
	public void add(SpecialHoliday specialHoliday) {
		this.commandProxy().insert(createSpecialHolidayFromDomain(specialHoliday));
		this.commandProxy().insertAll(createKshstSphdAbsenceLst(specialHoliday));
		this.commandProxy().insertAll(createKshstSphdSpecLeaveLst(specialHoliday));
		this.commandProxy().insert(createKshstSpecialLeaveRestriction(specialHoliday));
		this.commandProxy().insertAll(createKshstSpecClsLst(specialHoliday));
		this.commandProxy().insertAll(createKshstSpecEmpLst(specialHoliday));

		switch (specialHoliday.getGrantRegular().getTypeTime()) {
		case REFER_GRANT_DATE_TBL:
			this.commandProxy().insert(createKshmtHdspGrantDeadline(specialHoliday));
			break;
		case GRANT_SPECIFY_DATE:
			this.commandProxy().insert(createKshmtHdspGrant(specialHoliday));
			this.commandProxy().insert(createKshmtHdspGrantDeadline(specialHoliday));
			break;
		case GRANT_PERIOD:
			this.commandProxy().insert(createKshmtHdspGrantPeriod(specialHoliday));
			break;
		}

	}

	@Override
	public void update(SpecialHoliday specialHoliday) {

		// ooooo 要修正 jinno
		boolean isAutoGrant = specialHoliday.getAutoGrant().value == 0 ? false : true;
		int typeTime = TypeTime.REFER_GRANT_DATE_TBL.value, grantDate = GrantDate.EMP_GRANT_DATE.value,
				interval = 0, grantedDays = 0, timeSpecifyMethod = TimeLimitSpecification.INDEFINITE_PERIOD.value,
				limitCarryoverDays = 999;
		KshstSpecialHolidayPK pk = new KshstSpecialHolidayPK(specialHoliday.getCompanyId(), specialHoliday.getSpecialHolidayCode().v());
		KshmtHdsp old = this.queryProxy().find(pk, KshmtHdsp.class).orElse(null);
		old.autoGrant = specialHoliday.getAutoGrant().value;
		old.specialHolidayName = specialHoliday.getSpecialHolidayName().v();
		old.memo = specialHoliday.getMemo().v();
		old.continuousAcquisition = specialHoliday.getContinuousAcquisition().value;
		old.grantTiming = specialHoliday.getGrantRegular().getTypeTime().value;
		if (specialHoliday.getGrantRegular().getTypeTime() == TypeTime.REFER_GRANT_DATE_TBL
				|| specialHoliday.getGrantRegular().getTypeTime() == TypeTime.GRANT_SPECIFY_DATE) {
			specialHoliday.getGrantRegular().getGrantDate().ifPresent(gd -> {
				old.grantDate = gd.value;
			});
		}
		this.commandProxy().update(old);

		KshmtHdspGrantDeadlinePK grantPeriodicPK = new KshmtHdspGrantDeadlinePK(
				specialHoliday.getCompanyId(),
				specialHoliday.getSpecialHolidayCode().v());
		
		GrantDateTblReferenceGrant grantPeriodic = null;
		/** #118010s */
		// reset [KSHMT_HDSP_GRANT]
		KshmtHdspGrantPK oldHDSPGrantPK = new KshmtHdspGrantPK(specialHoliday.getCompanyId(), specialHoliday.getSpecialHolidayCode().v());
		KshmtHdspGrant oldHDSPGrant = this.queryProxy().find(oldHDSPGrantPK, KshmtHdspGrant.class).orElse(null);
		if(oldHDSPGrant != null) {
			oldHDSPGrant.grantedDays = 0;
			oldHDSPGrant.grantMd = null;
			this.commandProxy().update(oldHDSPGrant);
		}
		/** #118010e */
		
		switch (specialHoliday.getGrantRegular().getTypeTime()) {
			case REFER_GRANT_DATE_TBL:
				Optional<KshmtHdspGrantDeadline> oldGrantPeriodicOpt1 = this.queryProxy().find(grantPeriodicPK, KshmtHdspGrantDeadline.class);
				if (!oldGrantPeriodicOpt1.isPresent()) {
					this.commandProxy().insert(createKshmtHdspGrantDeadline(specialHoliday));
				} else if(specialHoliday.getGrantRegular().getGrantPeriodic().isPresent()) {
					val oldGrantPeriodic1 = oldGrantPeriodicOpt1.get();
					grantPeriodic = specialHoliday.getGrantRegular().getGrantPeriodic().get();
					grantPeriodic.getGrantDeadline().getExpirationDate().ifPresent(d -> {
						oldGrantPeriodic1.deadlineMonths = d.getMonths().v();
						oldGrantPeriodic1.deadlineYears = d.getYears().v();
					});
					
					AtomicBoolean isLimitCarryoverDaysPresent = new AtomicBoolean(false);
					grantPeriodic.getGrantDeadline().getLimitAccumulationDays().ifPresent(d -> {
						d.getLimitCarryoverDays().ifPresent(c -> {
							oldGrantPeriodic1.limitCarryoverDays = c.v();
							isLimitCarryoverDaysPresent.set(true);
						});
					});
					if(!isLimitCarryoverDaysPresent.get()) {
						oldGrantPeriodic1.limitCarryoverDays = null;
					}
					
					oldGrantPeriodic1.timeMethod = grantPeriodic.getGrantDeadline().getTimeSpecifyMethod().value;
					this.commandProxy().update(oldGrantPeriodic1);
				}
				
				break;
			case GRANT_SPECIFY_DATE:
				Optional<KshmtHdspGrantDeadline> oldGrantPeriodicOpt3 = this.queryProxy().find(grantPeriodicPK, KshmtHdspGrantDeadline.class);
				if (!oldGrantPeriodicOpt3.isPresent()) {
					this.commandProxy().insert(createKshmtHdspGrantDeadline(specialHoliday));
				} else if (specialHoliday.getGrantRegular().getFixGrantDate().isPresent()) {
					val oldGrantPeriodic3 = oldGrantPeriodicOpt3.get();
					/** 特別休暇.付与・期限情報.指定日付与.期限.期限指定方法 */
					TimeLimitSpecification timeLimitSpec = specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getTimeSpecifyMethod();
					if (timeLimitSpec == TimeLimitSpecification.INDEFINITE_PERIOD) {
						
					} else if (timeLimitSpec == TimeLimitSpecification.AVAILABLE_GRANT_DATE_DESIGNATE) {
						if (specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getExpirationDate().isPresent()){
							/** 特別休暇.付与・期限情報.指定日付与.期限.有効期限.月数 */
							oldGrantPeriodic3.deadlineMonths = specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getExpirationDate().get().getMonths().v();
							/** 特別休暇.付与・期限情報.指定日付与.期限.有効期限.年数 */
							oldGrantPeriodic3.deadlineYears = specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getExpirationDate().get().getYears().v();
						}
					} else if (timeLimitSpec == TimeLimitSpecification.AVAILABLE_UNTIL_NEXT_GRANT_DATE) {
						
					}
					AtomicBoolean isLimitCarryoverDaysPresent = new AtomicBoolean(false);
					specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getLimitAccumulationDays().ifPresent(d -> {
						d.getLimitCarryoverDays().ifPresent(c -> {
							oldGrantPeriodic3.limitCarryoverDays = c.v();
							isLimitCarryoverDaysPresent.set(true);
						});
					});
					if(!isLimitCarryoverDaysPresent.get()) {
						oldGrantPeriodic3.limitCarryoverDays = null;
					}
					if(null != timeLimitSpec) {
						oldGrantPeriodic3.timeMethod = timeLimitSpec.value;
					} else {
						oldGrantPeriodic3.timeMethod = 0;
					}
					
					this.commandProxy().update(oldGrantPeriodic3);
					
					KshmtHdspGrantPK grantPK = new KshmtHdspGrantPK(
							specialHoliday.getCompanyId(),
							specialHoliday.getSpecialHolidayCode().v());
					KshmtHdspGrant oldGrant = this.queryProxy().find(grantPK, KshmtHdspGrant.class).orElse(null);
					GrantRegular grantRegular = specialHoliday.getGrantRegular();
					typeTime = grantRegular.getTypeTime().value;
					if ( grantRegular.getGrantDate().isPresent() ) {
						grantDate = grantRegular.getGrantDate().get().value;
					}
					if(null == oldGrant) {
						this.commandProxy().insert(createKshmtHdspGrant(specialHoliday));
					} else {
						if (specialHoliday.getGrantRegular().getFixGrantDate().isPresent()){
							/** 特別休暇.付与・期限情報.指定日付与.付与日数.付与日数 */
							grantedDays = specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantDays().getGrantDays().v();
							if(specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantMonthDay().isPresent()) {
								oldGrant.grantMd = 
										specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantMonthDay().get().getMonth() * 100 + 
										specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantMonthDay().get().getDay();
							} else {
								oldGrant.grantMd = null;
							}
							oldGrant.grantedDays = grantedDays;
							this.commandProxy().update(oldGrant);
						}	
					}
				}
				
				break;
			case GRANT_PERIOD:
				KshmtHdspGrantPeriodPK periodPk = new KshmtHdspGrantPeriodPK(
						specialHoliday.getCompanyId(),
						specialHoliday.getSpecialHolidayCode().v());
				
				Optional<KshmtHdspGrantPeriod> grantPeriodOpt = this.queryProxy().find(periodPk, KshmtHdspGrantPeriod.class);
				if (grantPeriodOpt.isPresent()) {
					val grantPeriod = grantPeriodOpt.get();
					specialHoliday.getGrantRegular().getPeriodGrantDate().ifPresent(d -> {
						grantPeriod.periodStart = d.getPeriod().start();
						grantPeriod.periodEnd = d.getPeriod().end();
						grantPeriod.grantDays = d.getGrantDays().getGrantDays().v();
					});	
					this.commandProxy().update(grantPeriod);
				} else {
					this.commandProxy().insert(createKshmtHdspGrantPeriod(specialHoliday));
				}			
				break;
		}
		
//		if(specialHoliday.getGrantRegular().getGrantPeriodic().isPresent())
//			grantPeriodic = specialHoliday.getGrantRegular().getGrantPeriodic().get();
//		if (specialHoliday.getGrantRegular().getFixGrantDate().isPresent()){
//			/** 特別休暇.付与・期限情報.指定日付与.期限.期限指定方法 */
//			specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getTimeSpecifyMethod();
//			if (specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getExpirationDate().isPresent()){
//				/** 特別休暇.付与・期限情報.指定日付与.期限.有効期限.月数 */
//				oldGrantPeriodic.deadlineMonths = specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getExpirationDate().get().getMonths().v();
//				/** 特別休暇.付与・期限情報.指定日付与.期限.有効期限.年数 */
//				oldGrantPeriodic.deadlineYears = specialHoliday.getGrantRegular().getFixGrantDate().get().getGrantPeriodic().getExpirationDate().get().getYears().v();
//			}
//		}

		// 要修正　date →　int ?
//		if (specialHoliday.getGrantRegular().getPeriodGrantDate().isPresent()){
			/** 特別休暇.付与・期限情報.期間付与.期間 */
////			oldGrantPeriodic.startDate = grantPeriodic.getAvailabilityPeriod().getStartDateValue();
////			oldGrantPeriodic.endDate = grantPeriodic.getAvailabilityPeriod().getEndDateValue();
//			oldGrantPeriodic.startDate = specialHoliday.getGrantRegular().getPeriodGrantDate().get().getPeriod().start().toString("MMdd");
//			oldGrantPeriodic.endDate = specialHoliday.getGrantRegular().getPeriodGrantDate().get().getPeriod().end();
//		}

//		oldGrantPeriodic.timeMethod = timeSpecifyMethod;
//
//		oldGrantPeriodic.limitCarryoverDays = limitCarryoverDays;
		
		KshstSpecialLeaveRestrictionPK specialLeaveRestrictionPK = new KshstSpecialLeaveRestrictionPK(
				specialHoliday.getCompanyId(),
				specialHoliday.getSpecialHolidayCode().v());
		KshstSpecialLeaveRestriction oldSpecialLeaveRestriction = this.queryProxy()
				.find(specialLeaveRestrictionPK, KshstSpecialLeaveRestriction.class).orElse(null);
		oldSpecialLeaveRestriction.restrictionCls = specialHoliday.getSpecialLeaveRestriction().getRestrictionCls().value;
		oldSpecialLeaveRestriction.ageLimit = specialHoliday.getSpecialLeaveRestriction().getAgeLimit().value;
		oldSpecialLeaveRestriction.genderRest = specialHoliday.getSpecialLeaveRestriction().getGenderRest().value;
		oldSpecialLeaveRestriction.restEmp = specialHoliday.getSpecialLeaveRestriction().getRestEmp().value;
		oldSpecialLeaveRestriction.ageCriteriaCls = specialHoliday.getSpecialLeaveRestriction().getAgeStandard().getAgeCriteriaCls().value;
		MonthDay ageBaseDate = specialHoliday.getSpecialLeaveRestriction().getAgeStandard().getAgeBaseDate();
		oldSpecialLeaveRestriction.ageBaseDate = ageBaseDate.getMonth()*100+ageBaseDate.getDay();
		oldSpecialLeaveRestriction.ageLowerLimit = specialHoliday.getSpecialLeaveRestriction().getAgeRange() != null
				? (specialHoliday.getSpecialLeaveRestriction().getAgeRange().getAgeLowerLimit() != null
				? specialHoliday.getSpecialLeaveRestriction().getAgeRange().getAgeLowerLimit().v() : 0)
			: null;
		oldSpecialLeaveRestriction.ageHigherLimit = specialHoliday.getSpecialLeaveRestriction().getAgeRange() != null
				? (specialHoliday.getSpecialLeaveRestriction().getAgeRange().getAgeHigherLimit() != null
				? specialHoliday.getSpecialLeaveRestriction().getAgeRange().getAgeHigherLimit().v() : 0)
			: null;
		oldSpecialLeaveRestriction.gender = specialHoliday.getSpecialLeaveRestriction().getGender().value;
		this.commandProxy().update(oldSpecialLeaveRestriction);

		this.getEntityManager().createQuery(DELETE_SPHD_ABSENCE)
			.setParameter("companyID", specialHoliday.getCompanyId())
			.setParameter("specialHolidayCD", specialHoliday.getSpecialHolidayCode().v())
			.executeUpdate();
		this.getEntityManager().flush();
		this.commandProxy().insertAll(createKshstSphdAbsenceLst(specialHoliday));

		this.getEntityManager().createQuery(DELETE_SPHD_SPEC_LEAVE)
			.setParameter("companyID", specialHoliday.getCompanyId())
			.setParameter("specialHolidayCD", specialHoliday.getSpecialHolidayCode().v())
			.executeUpdate();
		this.getEntityManager().flush();
		this.commandProxy().insertAll(createKshstSphdSpecLeaveLst(specialHoliday));

		this.getEntityManager().createQuery(DELETE_SPEC_CLS)
			.setParameter("companyID", specialHoliday.getCompanyId())
			.setParameter("specialHolidayCD", specialHoliday.getSpecialHolidayCode().v())
			.executeUpdate();
		this.getEntityManager().flush();
		this.commandProxy().insertAll(createKshstSpecClsLst(specialHoliday));

		this.getEntityManager().createQuery(DELETE_SPEC_EMP)
			.setParameter("companyID", specialHoliday.getCompanyId())
			.setParameter("specialHolidayCD", specialHoliday.getSpecialHolidayCode().v())
			.executeUpdate();
		this.getEntityManager().flush();
		this.commandProxy().insertAll(createKshstSpecEmpLst(specialHoliday));

	}

	@Override
	public void delete(String companyId, int specialHolidayCode) {
		KshstSpecialHolidayPK pk = new KshstSpecialHolidayPK(companyId, specialHolidayCode);
		this.commandProxy().remove(KshmtHdsp.class, pk);

		KshmtHdspGrantPK grantPK = new KshmtHdspGrantPK(companyId, specialHolidayCode);
		this.commandProxy().remove(KshmtHdspGrant.class, grantPK);

		KshmtHdspGrantDeadlinePK grantPeriodicPK = new KshmtHdspGrantDeadlinePK(companyId, specialHolidayCode);
		this.commandProxy().remove(KshmtHdspGrantDeadline.class, grantPeriodicPK);

		this.getEntityManager().createQuery(DELETE_SPECICAL_LEAVE_RESTRICTION).setParameter("companyID", companyId)
				.setParameter("specialHolidayCD", specialHolidayCode).executeUpdate();

		this.getEntityManager().createQuery(DELETE_SPHD_ABSENCE).setParameter("companyID", companyId)
				.setParameter("specialHolidayCD", specialHolidayCode).executeUpdate();

		this.getEntityManager().createQuery(DELETE_SPHD_SPEC_LEAVE).setParameter("companyID", companyId)
				.setParameter("specialHolidayCD", specialHolidayCode).executeUpdate();

		this.getEntityManager().createQuery(DELETE_SPEC_CLS).setParameter("companyID", companyId)
				.setParameter("specialHolidayCD", specialHolidayCode).executeUpdate();

		this.getEntityManager().createQuery(DELETE_SPEC_EMP).setParameter("companyID", companyId)
				.setParameter("specialHolidayCD", specialHolidayCode).executeUpdate();

		this.getEntityManager().createQuery(DELETE_GRANT_DATE).setParameter("companyID", companyId)
				.setParameter("specialHolidayCD", specialHolidayCode).executeUpdate();

		this.getEntityManager().createQuery(DELETE_All_ELAPSE).setParameter("companyID", companyId)
				.setParameter("specialHolidayCD", specialHolidayCode).executeUpdate();

		/**
		 * For delete releated domain of KDR001 (team G)
		 */
//		this.getEntityManager().createQuery(DELETE_SPEC_HD)
//			.setParameter("companyID", companyId)
//			.setParameter("specialHolidayCD", specialHolidayCode)
//			.executeUpdate();
	}

	/*
	 * @Override public void findByCID(String companyID) { List<Object[]> result1 =
	 * this.queryProxy().query(FIND_GRANT_REGULAR_TEST, Object[].class).getList();
	 * Map<KshmtHdspGrant, List<KshmtHdspGrantTbl>> gratnRegularMap = result1
	 * .stream().collect(Collectors.groupingBy(item -> { return
	 * (KshmtHdspGrant)item[0]; }, Collectors.collectingAndThen(Collectors.toList(),
	 * list -> list.stream().filter(c -> c[1] != null) .map(c ->
	 * (KshmtHdspGrantTbl)c[1])))); List<Object[]> result2 =
	 * this.queryProxy().query(FIND_SPECICAL_LEAVE, Object[].class).getList();
	 * List<Object[]> result = this.queryProxy().query(FIND_BY_CID,
	 * Object[].class).getList(); System.out.println(result); }
	 */

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Optional<SpecialHoliday> findBySingleCD(String companyID, int specialHolidayCD) {

		return this.findByCode(companyID, specialHolidayCD).map(x -> {
			List<Integer> absenceFrameNoLst = this.queryProxy()
					.query(SELECT_SPHD_ABSENCE_BY_CODE, KshmtHdspFrameAbsence.class)
					.setParameter("companyID", companyID).setParameter("specialHolidayCD", specialHolidayCD)
					.getList(c -> Integer.valueOf(c.pk.absFameNo));
			List<Integer> frameNoLst = this.queryProxy().query(SELECT_SPHD_SPEC_LEAVE, KshmtHdspFrameHdsp.class)
					.setParameter("companyID", companyID).setParameter("specialHolidayCD", specialHolidayCD)
					.getList(c -> c.pk.sphdNo);
			List<String> listCls = this.queryProxy().query(SELECT_SPEC_CLS, KshmtHdspCondCls.class)
					.setParameter("companyID", companyID).setParameter("specialHolidayCD", specialHolidayCD)
					.getList(c -> c.pk.clsCode);
			List<String> listEmp = this.queryProxy().query(SELECT_SPEC_EMP, KshmtHdspCondEmp.class)
					.setParameter("companyID", companyID).setParameter("specialHolidayCD", specialHolidayCD)
					.getList(c -> c.pk.empCode);
			if (!absenceFrameNoLst.isEmpty()) {
				x.getTargetItem().setAbsenceFrameNo(absenceFrameNoLst);
			}
			if (!frameNoLst.isEmpty()) {
				x.getTargetItem().setFrameNo(frameNoLst);
			}
			if (!listCls.isEmpty()) {
				x.getSpecialLeaveRestriction().setListCls(listCls);
			}
			if (!listEmp.isEmpty()) {
				x.getSpecialLeaveRestriction().setListEmp(listEmp);
			}
			return x;
		});

	}

	@Override
	public List<Integer> findByAbsframeNo(String cid, int absFrameNo) {
		return this.queryProxy().query(QUEYRY_BY_ABSFRAMENO, KshmtHdspFrameAbsence.class).setParameter("companyId", cid)
				.setParameter("absFameNo", absFrameNo).getList().stream().map(c -> {
					return c.pk.specialHolidayCode;
				}).collect(Collectors.toList());
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<SpecialHoliday> findSimpleByListCode(String companyId, List<Integer> specialHolidayCodes) {
		if (specialHolidayCodes.isEmpty())
			return Collections.emptyList();
		List<SpecialHoliday> resultList = new ArrayList<>();
		CollectionUtil.split(specialHolidayCodes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(SELECT_SPHD_BY_LIST_CODE, Object[].class)
					.setParameter("companyId", companyId).setParameter("specialHolidayCodes", subList).getList(c -> {
						return createSphdDomainFromEntity(c);
					}));
		});
		resultList.sort(Comparator.comparing(SpecialHoliday::getSpecialHolidayCode));
		return resultList;
	}

	@Override
	public List<SpecialHoliday> findByCompanyIdWithTargetItem(String companyId) {
		return this.queryProxy().query(SELECT_SPHD_BY_COMPANY_ID_QUERY, Object[].class)
				.setParameter("companyId", companyId).getList(c -> {
					return createSphdDomainFromEntityWithTargetItem(c);
				});
	}

	private SpecialHoliday createSphdDomainFromEntityWithTargetItem(Object[] c) {
		String companyId = String.valueOf(c[0]);
		int specialHolidayCode = Integer.parseInt(String.valueOf(c[1]));
		return this.findBySingleCD(companyId, specialHolidayCode).get();
	}

	@Override
	public List<Integer> findBySphdSpecLeave(String cid, int sphdSpecLeaveNo) {
		return this.queryProxy().query(QUERY_BY_SPECLEAVE, KshmtHdspFrameHdsp.class).setParameter("companyId", cid)
				.setParameter("sphdNo", sphdSpecLeaveNo).getList().stream().map(c -> {
					return c.pk.specialHolidayCode;
				}).collect(Collectors.toList());
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<SpecialHoliday> findSimpleByCompanyIdNoMaster(String companyId, List<Integer> specialHolidayCodes) {
		if (specialHolidayCodes.isEmpty())
			return Collections.emptyList();
		List<SpecialHoliday> resultList = new ArrayList<>();
		CollectionUtil.split(specialHolidayCodes, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(SELECT_SPHD_BY_COMPANY_AND_NO, Object[].class)
					.setParameter("companyId", companyId).setParameter("specialHolidayCode", subList).getList(c -> {
						return createSphdDomainFromEntity(c);
					}));
		});
		return resultList;
	}
}
