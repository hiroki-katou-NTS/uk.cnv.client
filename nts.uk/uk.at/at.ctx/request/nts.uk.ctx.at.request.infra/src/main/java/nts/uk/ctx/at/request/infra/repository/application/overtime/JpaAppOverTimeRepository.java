package nts.uk.ctx.at.request.infra.repository.application.overtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.uk.ctx.at.request.infra.entity.application.overtime.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationDate;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReasonForReversion;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTimeRepository;
import nts.uk.ctx.at.request.dom.application.overtime.ApplicationTime;
import nts.uk.ctx.at.request.dom.application.overtime.HolidayMidNightTime;
import nts.uk.ctx.at.request.dom.application.overtime.OverTimeShiftNight;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeApplicationSetting;
import nts.uk.ctx.at.request.dom.application.overtime.ReasonDivergence;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.DivergenceReason;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36Agree;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeAnnual;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeMonth;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeUpperLimit;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeUpperLimitAverage;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeUpperLimitMonth;
import nts.uk.ctx.at.request.dom.application.overtime.time36.Time36AgreeUpperLimitPerMonth;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeYear;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DiverdenceReasonCode;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.AgreementOneYearTime;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaAppOverTimeRepository extends JpaRepository implements AppOverTimeRepository{
	public static final String FIND_BY_ID = "SELECT *  "
			+ "FROM KRQDT_APP_OVERTIME as a INNER JOIN KRQDT_APPLICATION as b ON a.APP_ID = b.APP_ID"
			+ " WHERE a.APP_ID = @appID AND a.CID = @companyId";
	
	public static final String SELECT_ALL_BY_JPA = "SELECT a FROM KrqdtAppOverTime as a WHERE a.krqdtAppOvertimePK.cid = :cid and a.krqdtAppOvertimePK.appId = :appId";
	
	public static final String SELECT_ALL_BY_APP_IDs = "SELECT a FROM KrqdtAppOverTime a WHERE a.krqdtAppOvertimePK.cid = :cid and a.krqdtAppOvertimePK.appId IN :appIds";
	
	@Override
	public Optional<AppOverTime> find(String companyId, String appId) {
		return this.queryProxy()
				   .query(SELECT_ALL_BY_JPA, KrqdtAppOverTime.class)
				   .setParameter("cid", companyId)
				   .setParameter("appId", appId)
				   .getSingle(x -> x.toDomain());
		
	}

	@Override
	public Optional<AppOverTime> findLatestMultipleOvertimeApp(String employeeId, GeneralDate appDate, PrePostAtr prePostAtr) {
		return this.queryProxy().query("SELECT o FROM KrqdtAppOverTime o, KrqdtApplication a " +
				"WHERE a.pk.companyID = o.krqdtAppOvertimePK.cid AND a.pk.appID = o.krqdtAppOvertimePK.appId " +
				"AND o.overtimeAtr = 3 AND a.employeeID = :empId AND a.appDate = :appDate AND a.prePostAtr = :prePost " +
				"ORDER BY a.inputDate DESC", KrqdtAppOverTime.class)
				.setParameter("empId", employeeId)
				.setParameter("appDate", appDate)
				.setParameter("prePost", prePostAtr.value)
				.getList(KrqdtAppOverTime::toDomain)
				.stream().findFirst();
	}

	// KrqdtAppOverTime
	@Override
	public void add(AppOverTime appOverTime) {
		this.commandProxy().insert(toEntity(appOverTime));
		this.getEntityManager().flush();
	}
	private KrqdtAppOverTime toEntity(AppOverTime appOverTime) {
		KrqdtAppOverTime krqdtAppOverTime = new KrqdtAppOverTime();
		KrqdtAppOvertimePK krqdtAppOvertimePK = new KrqdtAppOvertimePK(
				AppContexts.user().companyId(),
				appOverTime.getAppID());
				
		krqdtAppOverTime.krqdtAppOvertimePK = krqdtAppOvertimePK;
		krqdtAppOverTime.overtimeAtr = appOverTime.getOverTimeClf().value;
		krqdtAppOverTime.workTypeCode = appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTypeCode())).map(x -> x.v()).orElse(null);
		krqdtAppOverTime.workTimeCode = appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTimeCode())).map(x -> x.v()).orElse(null);
		List<TimeZoneWithWorkNo> workHours = appOverTime.getWorkHoursOp().orElse(Collections.emptyList());
		workHours.stream().forEach(item -> {
			if (item.getWorkNo().v() == 1) {
				krqdtAppOverTime.workTimeStart1 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.workTimeEnd1 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 2) {
				krqdtAppOverTime.workTimeStart2 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.workTimeEnd2 = item.getTimeZone().getEndTime().v();
			}
		});
		// 
		appOverTime.getApplicationTime()
			.getReasonDissociation()
			.orElse(Collections.emptyList())
			.stream()
			.forEach(item -> {
				if (item.getDiviationTime() == 1) {
					krqdtAppOverTime.divergenceNo1 = item.getDiviationTime();
					krqdtAppOverTime.divergenceCD1 = item.getReasonCode() == null ? null : item.getReasonCode().v();
					krqdtAppOverTime.divergenceReason1 = item.getReason() == null ? null :item.getReason().v();		
				} 
				if (item.getDiviationTime() == 2) {
					krqdtAppOverTime.divergenceNo2 = item.getDiviationTime();
					krqdtAppOverTime.divergenceCD2 = item.getReasonCode() == null ? null : item.getReasonCode().v();
					krqdtAppOverTime.divergenceReason2 = item.getReason() == null ? null : item.getReason().v();
				}
			});
		
		// 
		krqdtAppOverTime.flexExcessTime = appOverTime.getApplicationTime().getFlexOverTime().map(x -> x.v()).orElse(null);
		krqdtAppOverTime.overTimeNight = appOverTime.getApplicationTime().getOverTimeShiftNight().map(x -> x.getOverTimeMidNight() == null ? null : x.getOverTimeMidNight().v()).orElse(null);
		krqdtAppOverTime.totalNight = 0;
		if (krqdtAppOverTime.overTimeNight != null) {
			krqdtAppOverTime.totalNight += krqdtAppOverTime.overTimeNight;
		}
				// appOverTime.getApplicationTime().getOverTimeShiftNight().map(x -> x.getMidNightOutSide() == null ? null : x.getMidNightOutSide().v()).orElse(null);
		if (appOverTime.getApplicationTime().getOverTimeShiftNight().isPresent()) {
			if (!CollectionUtil.isEmpty(appOverTime.getApplicationTime().getOverTimeShiftNight().get().getMidNightHolidayTimes())) {
				appOverTime.getApplicationTime()
							.getOverTimeShiftNight()
							.get()
							.getMidNightHolidayTimes()
							.stream()
							.forEach(i -> {
								if (i.getLegalClf() == StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork) {
									krqdtAppOverTime.totalNight += i.getAttendanceTime().v();
									krqdtAppOverTime.legalHdNight = i.getAttendanceTime().v();
								} else if (i.getLegalClf() == StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork) {
									krqdtAppOverTime.totalNight += i.getAttendanceTime().v();
									krqdtAppOverTime.nonLegalHdNight = i.getAttendanceTime().v();
								} else if (i.getLegalClf() == StaturoryAtrOfHolidayWork.PublicHolidayWork) {
									krqdtAppOverTime.totalNight += i.getAttendanceTime().v();
									krqdtAppOverTime.nonLegalPublicHdNight = i.getAttendanceTime().v();
								}
							});
			}
		}
		if (krqdtAppOverTime.totalNight == 0) {
			krqdtAppOverTime.totalNight = null;
		}
		
		
		List<TimeZoneWithWorkNo> breakTimes = appOverTime.getBreakTimeOp().orElse(Collections.emptyList());
		breakTimes.stream().forEach(item -> {
			if (item.getWorkNo().v() == 1) {
				krqdtAppOverTime.breakTimeStart1 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd1 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 2) {
				krqdtAppOverTime.breakTimeStart2 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd2 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 3) {
				krqdtAppOverTime.breakTimeStart3 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd3 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 4) {
				krqdtAppOverTime.breakTimeStart4 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd4 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 5) {
				krqdtAppOverTime.breakTimeStart5 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd5 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 6) {
				krqdtAppOverTime.breakTimeStart6 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd6 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 7) {
				krqdtAppOverTime.breakTimeStart7 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd7 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 8) {
				krqdtAppOverTime.breakTimeStart8 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd8 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 9) {
				krqdtAppOverTime.breakTimeStart9 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd9 = item.getTimeZone().getEndTime().v();
			} else if (item.getWorkNo().v() == 10) {
				krqdtAppOverTime.breakTimeStart10 = item.getTimeZone().getStartTime().v();
				krqdtAppOverTime.breakTimeEnd10 = item.getTimeZone().getEndTime().v();
			}
		});
		krqdtAppOverTime.overtimeInputs = new ArrayList<KrqdtAppOvertimeInput>();
		// ------
		List<OvertimeApplicationSetting> overtimeApplicationSettings = appOverTime.getApplicationTime().getApplicationTime();
		if (!CollectionUtil.isEmpty(overtimeApplicationSettings)) {
			krqdtAppOverTime.overtimeInputs = overtimeApplicationSettings
				.stream()
				.map(x -> new KrqdtAppOvertimeInput(
										new KrqdtOvertimeInputPK(
												AppContexts.user().companyId(),
												appOverTime.getAppID(),
												x.getAttendanceType().value,
												x.getFrameNo().v()),
										x.getApplicationTime().v(),
										null))
				.collect(Collectors.toList());
				
									
		}

		if (appOverTime.getMultipleTimesOp().isPresent()) {
			krqdtAppOverTime.multipleOvertimes = KrqdtAppOvertimeMultiTimes.fromDomain(
					AppContexts.user().companyId(),
					appOverTime.getAppID(),
					appOverTime.getMultipleTimesOp().get()
			);
		}
		
		return krqdtAppOverTime;
	}
	@Override
	public void update(AppOverTime appOverTime) {
		KrqdtAppOverTime krqdtAppOverTime = toEntity(appOverTime);
		Optional<KrqdtAppOverTime> updateAppOverTime = this.queryProxy().find(krqdtAppOverTime.krqdtAppOvertimePK, KrqdtAppOverTime.class);
		if (!updateAppOverTime.isPresent()) return;
		updateAppOverTime.get().overtimeAtr = krqdtAppOverTime.overtimeAtr;
		updateAppOverTime.get().workTypeCode = krqdtAppOverTime.workTypeCode;
		updateAppOverTime.get().workTimeCode = krqdtAppOverTime.workTimeCode;
		updateAppOverTime.get().workTimeStart1 = krqdtAppOverTime.workTimeStart1;
		updateAppOverTime.get().workTimeEnd1 = krqdtAppOverTime.workTimeEnd1;
		updateAppOverTime.get().workTimeStart2 = krqdtAppOverTime.workTimeStart2;
		updateAppOverTime.get().workTimeEnd2 = krqdtAppOverTime.workTimeEnd2;
		
		
		updateAppOverTime.get().flexExcessTime = krqdtAppOverTime.flexExcessTime;
		updateAppOverTime.get().overTimeNight = krqdtAppOverTime.overTimeNight;
		
		updateAppOverTime.get().legalHdNight = krqdtAppOverTime.legalHdNight;
		updateAppOverTime.get().nonLegalHdNight = krqdtAppOverTime.nonLegalHdNight;
		updateAppOverTime.get().nonLegalPublicHdNight = krqdtAppOverTime.nonLegalPublicHdNight;
		
		updateAppOverTime.get().totalNight = 0;
		if (krqdtAppOverTime.legalHdNight != null) {
			updateAppOverTime.get().totalNight += krqdtAppOverTime.legalHdNight;
		}
		if (krqdtAppOverTime.nonLegalHdNight != null) {
			updateAppOverTime.get().totalNight += krqdtAppOverTime.nonLegalHdNight;
		}
		if (krqdtAppOverTime.nonLegalPublicHdNight != null) {
			updateAppOverTime.get().totalNight += krqdtAppOverTime.nonLegalPublicHdNight;
		}
		
		if (updateAppOverTime.get().totalNight == 0) {
			updateAppOverTime.get().totalNight = null;
		}
		updateAppOverTime.get().divergenceNo1 = krqdtAppOverTime.divergenceNo1;
		updateAppOverTime.get().divergenceCD1 = krqdtAppOverTime.divergenceCD1;
		updateAppOverTime.get().divergenceReason1 = krqdtAppOverTime.divergenceReason1;
		
		updateAppOverTime.get().divergenceNo2 = krqdtAppOverTime.divergenceNo2;
		updateAppOverTime.get().divergenceCD2 = krqdtAppOverTime.divergenceCD2;
		updateAppOverTime.get().divergenceReason2 = krqdtAppOverTime.divergenceReason2;
		
		updateAppOverTime.get().breakTimeStart1 = krqdtAppOverTime.breakTimeStart1;
		updateAppOverTime.get().breakTimeEnd1 = krqdtAppOverTime.breakTimeEnd1;
		updateAppOverTime.get().breakTimeStart2 = krqdtAppOverTime.breakTimeStart2;
		updateAppOverTime.get().breakTimeEnd2 = krqdtAppOverTime.breakTimeEnd2;
		updateAppOverTime.get().breakTimeStart3 = krqdtAppOverTime.breakTimeStart3;
		updateAppOverTime.get().breakTimeEnd3 = krqdtAppOverTime.breakTimeEnd3;
		updateAppOverTime.get().breakTimeStart4 = krqdtAppOverTime.breakTimeStart4;
		updateAppOverTime.get().breakTimeEnd4 = krqdtAppOverTime.breakTimeEnd4;
		updateAppOverTime.get().breakTimeStart5 = krqdtAppOverTime.breakTimeStart5;
		updateAppOverTime.get().breakTimeEnd5 = krqdtAppOverTime.breakTimeEnd5;
		updateAppOverTime.get().breakTimeStart6 = krqdtAppOverTime.breakTimeStart6;
		updateAppOverTime.get().breakTimeEnd6 = krqdtAppOverTime.breakTimeEnd6;
		updateAppOverTime.get().breakTimeStart7 = krqdtAppOverTime.breakTimeStart7;
		updateAppOverTime.get().breakTimeEnd7 = krqdtAppOverTime.breakTimeEnd7;
		updateAppOverTime.get().breakTimeStart8 = krqdtAppOverTime.breakTimeStart8;
		updateAppOverTime.get().breakTimeEnd8 = krqdtAppOverTime.breakTimeEnd8;
		updateAppOverTime.get().breakTimeStart9 = krqdtAppOverTime.breakTimeStart9;
		updateAppOverTime.get().breakTimeEnd9 = krqdtAppOverTime.breakTimeEnd9;
		updateAppOverTime.get().breakTimeStart10 = krqdtAppOverTime.breakTimeStart10;
		updateAppOverTime.get().breakTimeEnd10 = krqdtAppOverTime.breakTimeEnd10;
		
		List<KrqdtAppOvertimeInput> overtimeInputs = new ArrayList<KrqdtAppOvertimeInput>();
		
		krqdtAppOverTime.overtimeInputs.stream().forEach(x -> {
			Optional<KrqdtAppOvertimeInput> result = updateAppOverTime.get().overtimeInputs.stream().filter(
					a -> a.krqdtOvertimeInputPK.getAttendanceType() == x.krqdtOvertimeInputPK.getAttendanceType()
							&& a.krqdtOvertimeInputPK.getAppId() == x.krqdtOvertimeInputPK.getAppId()
							&& a.krqdtOvertimeInputPK.getCid() == x.krqdtOvertimeInputPK.getCid()
							&& a.krqdtOvertimeInputPK.getFrameNo() == x.krqdtOvertimeInputPK.getFrameNo()
					).findFirst();
			KrqdtAppOvertimeInput krqdtOvertimeInput;
			if (result.isPresent()) {
				result.get().applicationTime = x.applicationTime;
				krqdtOvertimeInput = result.get();
			} else {
				krqdtOvertimeInput = x;
				krqdtOvertimeInput.contractCd = AppContexts.user().contractCode();
			}
			overtimeInputs.add(krqdtOvertimeInput);
		});
		updateAppOverTime.get().overtimeInputs = overtimeInputs;
		
		if (updateAppOverTime.get().multipleOvertimes != null) updateAppOverTime.get().multipleOvertimes.clear();
		
		this.commandProxy().update(updateAppOverTime.get());
		this.getEntityManager().flush();

		if (!CollectionUtil.isEmpty(krqdtAppOverTime.multipleOvertimes)) {
			this.commandProxy().insertAll(krqdtAppOverTime.multipleOvertimes);
		}
	}
//	public AppOverTime toDomain(NtsResultRecord res) {
//		String pattern = "yyyy/MM/dd HH:mm:ss";
//		String pattern2 = "yyyy/MM/dd";
//		DateFormat df = new SimpleDateFormat(pattern);
//		DateFormat df2 = new SimpleDateFormat(pattern2);
//		String appID = res.getString("APP_ID");
//		Integer version = res.getInt("EXCLUS_VER");
//		PrePostAtr prePostAtr = EnumAdaptor.valueOf(res.getInt("PRE_POST_ATR"), PrePostAtr.class);
//		String enteredPerson = res.getString("ENTERED_PERSON_SID");
//		GeneralDateTime inputDate = GeneralDateTime.fromString(df.format(res.getDate("INPUT_DATE")), pattern);
//		ApplicationDate appDate = new ApplicationDate(GeneralDate.fromString(df2.format(res.getDate("APP_DATE")), pattern2));
//		ApplicationType appType = EnumAdaptor.valueOf(res.getInt("APP_TYPE"), ApplicationType.class);
//		String employeeID = res.getString("APPLICANTS_SID");
//		Application application = new Application(version, appID, prePostAtr, employeeID, appType, appDate, enteredPerson, inputDate, null);
//		if (res.getString("REASON_REVERSION") == null) {
//			application.setOpReversionReason(Optional.ofNullable(null));
//		}else {
//			application.setOpReversionReason(Optional.ofNullable(new ReasonForReversion(res.getString("REASON_REVERSION"))));
//		}
////		application.setAppDate(new ApplicationDate(GeneralDate.fromString(df2.format(res.getDate("APP_DATE")), pattern2)));
//		if (res.getInt("FIXED_REASON") == null) {
//			application.setOpAppStandardReasonCD(Optional.ofNullable(null));
//		}else {
//			application.setOpAppStandardReasonCD(Optional.ofNullable(new AppStandardReasonCode(res.getInt("FIXED_REASON"))));
//		}
//		if (res.getString("APP_REASON") == null) {
//			application.setOpAppReason(Optional.ofNullable(null));
//		}else {
//			application.setOpAppReason(Optional.ofNullable(new AppReason(res.getString("APP_REASON"))));
//		}
////		application.setAppType(EnumAdaptor.valueOf(res.getInt("APP_TYPE"), ApplicationType.class));
////		application.setEmployeeID(res.getString("APPLICANTS_SID"));
//		if (res.getDate("APP_START_DATE") == null) {
//			application.setOpAppStartDate(Optional.ofNullable(null));
//		}else {
//			application.setOpAppStartDate(Optional.of(new ApplicationDate(GeneralDate.fromString(df2.format(res.getDate("APP_START_DATE")), pattern2))));
//		}
//		if (res.getDate("APP_END_DATE") == null) {
//			application.setOpAppEndDate(Optional.ofNullable(null));
//		} else {
//			application.setOpAppEndDate(Optional.of(new ApplicationDate(GeneralDate.fromString(df2.format(res.getDate("APP_END_DATE")), pattern2))));
//		}
//
//		if (res.getInt("STAMP_OPTION_ATR") == null) {
//			application.setOpStampRequestMode(Optional.ofNullable(null));
//		}else {
//			application.setOpStampRequestMode(Optional.of(EnumAdaptor.valueOf(res.getInt("STAMP_OPTION_ATR"), StampRequestMode.class)));
//		}
//
//		AppOverTime appOverTime = new AppOverTime(application);
//
//		Integer overtimeAtr = res.getInt("OVERTIME_ATR");
//
//		String workTypeCode = res.getString("WORK_TYPE_CD");
//
//		String workTimeCode = res.getString("WORK_TIME_CD");
//
//		Integer workTimeStart1 = res.getInt("WORK_TIME_START1");
//
//		Integer workTimeEnd1 = res.getInt("WORK_TIME_END1");
//
//		Integer workTimeStart2 = res.getInt("WORK_TIME_START2");
//
//		Integer workTimeEnd2 = res.getInt("WORK_TIME_END2");
//
//		Integer divergenceNo1 = res.getInt("DIVERGENCE_NO1");
//
//		String divergenceCD1 = res.getString("DIVERGENCE_CD1");
//
//		String divergenceReason1 = res.getString("DIVERGENCE_REASON1");
//
//		Integer divergenceNo2 = res.getInt("DIVERGENCE_NO2");
//
//		String divergenceCD2 = res.getString("DIVERGENCE_CD2");
//
//		String divergenceReason2 = res.getString("DIVERGENCE_REASON2");
//
//		Integer flexExcessTime = res.getInt("FLEX_EXCESS_TIME");
//
//		Integer overTimeNight = res.getInt("OVERTIME_NIGHT");
//
//		Integer totalNight = res.getInt("TOTAL_NIGHT");
//
//		Integer legalHdNight = res.getInt("LEGAL_HD_NIGHT");
//
//		Integer nonLegalHdNight = res.getInt("NON_LEGAL_HD_NIGHT");
//
//		Integer nonLegalPublicHdNight = res.getInt("NON_LEGAL_PUBLIC_HD_NIGHT");
//
//		Integer breakTimeStart1 = res.getInt("BREAK_TIME_START1");
//
//		Integer breakTimeEnd1 = res.getInt("BREAK_TIME_END1");
//
//		Integer breakTimeStart2 = res.getInt("BREAK_TIME_START2");
//
//		Integer breakTimeEnd2 = res.getInt("BREAK_TIME_END2");
//
//		Integer breakTimeStart3 = res.getInt("BREAK_TIME_START3");
//
//		Integer breakTimeEnd3 = res.getInt("BREAK_TIME_END3");
//
//		Integer breakTimeStart4 = res.getInt("BREAK_TIME_START4");
//
//		Integer breakTimeEnd4 = res.getInt("BREAK_TIME_END4");
//
//		Integer breakTimeStart5 = res.getInt("BREAK_TIME_START5");
//
//		Integer breakTimeEnd5 = res.getInt("BREAK_TIME_END5");
//
//		Integer breakTimeStart6 = res.getInt("BREAK_TIME_START6");
//
//		Integer breakTimeEnd6 = res.getInt("BREAK_TIME_END6");
//
//		Integer breakTimeStart7 = res.getInt("BREAK_TIME_START7");
//
//		Integer breakTimeEnd7 = res.getInt("BREAK_TIME_END7");
//
//		Integer breakTimeStart8 = res.getInt("BREAK_TIME_START8");
//
//		Integer breakTimeEnd8 = res.getInt("BREAK_TIME_END8");
//
//		Integer breakTimeStart9 = res.getInt("BREAK_TIME_START9");
//
//		Integer breakTimeEnd9 = res.getInt("BREAK_TIME_END9");
//
//		Integer breakTimeStart10 = res.getInt("BREAK_TIME_START10");
//
//		Integer breakTimeEnd10 = res.getInt("BREAK_TIME_END10");
//
//
//		appOverTime.setOverTimeClf(EnumAdaptor.valueOf(overtimeAtr, OvertimeAppAtr.class));
//		if (StringUtils.isNotBlank(workTimeCode) || StringUtils.isNotBlank(workTimeCode)) {
//			WorkInformation workInformation = new WorkInformation("", "");
//			appOverTime.setWorkInfoOp(Optional.of(workInformation));
//			if (StringUtils.isNotBlank(workTypeCode)) {
//				workInformation.setWorkTypeCode(workTypeCode);
//			}
//			if (StringUtils.isNotBlank(workTimeCode)) {
//				workInformation.setWorkTimeCode(workTimeCode);
//			}
//		}
//		List<TimeZoneWithWorkNo> workHoursOp = new ArrayList<TimeZoneWithWorkNo>();
//		List<TimeZoneWithWorkNo> breakTimeOp = new ArrayList<TimeZoneWithWorkNo>();
//		if (workTimeStart1 != null && workTimeEnd1 != null) {
//
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, workTimeStart1, workTimeEnd1);
//			workHoursOp.add(timeZoneWithWorkNo);
//			appOverTime.setWorkHoursOp(Optional.of(workHoursOp));
//		}
//		if (workTimeStart2 != null && workTimeEnd2 != null) {
//
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(2, workTimeStart2, workTimeEnd2);
//			workHoursOp.add(timeZoneWithWorkNo);
//		}
//		appOverTime.setOpReversionReason(Optional.empty());
//		ReasonDivergence reasonDivergence1 = new ReasonDivergence();
//		ReasonDivergence reasonDivergence2 = new ReasonDivergence();
//
//		ApplicationTime applicationTime = new ApplicationTime();
//		applicationTime.setFlexOverTime(Optional.ofNullable(flexExcessTime != null ? new AttendanceTimeOfExistMinus(flexExcessTime) : null));
//		// 112610
//		if (divergenceNo1 != null) {
//			reasonDivergence1.setDiviationTime(divergenceNo1);
//		}
//		if (divergenceCD1 != null) {
//			DiverdenceReasonCode reasonCode = new DiverdenceReasonCode(divergenceCD1);
//			reasonDivergence1.setReasonCode(reasonCode);
//		}
//		if (divergenceReason1 != null) {
//			DivergenceReason diReason = new DivergenceReason(divergenceReason1);
//			reasonDivergence1.setReason(diReason);
//		}
//
//		if (divergenceNo2 != null) {
//			reasonDivergence2.setDiviationTime(divergenceNo2);
//		}
//		if (divergenceCD2 != null) {
//			DiverdenceReasonCode reasonCode = new DiverdenceReasonCode(divergenceCD2);
//			reasonDivergence2.setReasonCode(reasonCode);
//		}
//		if (divergenceReason2 != null) {
//			DivergenceReason diReason = new DivergenceReason(divergenceReason2);
//			reasonDivergence2.setReason(diReason);
//		}
//		List<ReasonDivergence> reasonDissociation;
//		List<ReasonDivergence> reasonDissociation2;
//
//		if (!reasonDivergence1.isNullProp()) {
//
//			reasonDissociation = new ArrayList<ReasonDivergence>();
//			reasonDissociation.add(reasonDivergence1);
//			applicationTime.setReasonDissociation(Optional.of(reasonDissociation));
//		}
//
//		if (!reasonDivergence2.isNullProp()) {
//			reasonDissociation2 = new ArrayList<ReasonDivergence>();
//			reasonDissociation2.add(reasonDivergence2);
//			if(!applicationTime.getReasonDissociation().isPresent()) {
//				applicationTime.setReasonDissociation(Optional.of(reasonDissociation2));
//			}
//			if (applicationTime.getReasonDissociation().isPresent()) {
//				applicationTime.getReasonDissociation().get().add(reasonDivergence2);
//			}
//		}
//
//		OverTimeShiftNight overTimeShiftNight = new OverTimeShiftNight();
//		if (overTimeNight == null
//				&& totalNight == null
//				&& legalHdNight == null
//				&& nonLegalHdNight == null
//				&& nonLegalPublicHdNight == null) {
//			applicationTime.setOverTimeShiftNight(Optional.empty());
//
//		} else {
//			if (overTimeNight != null) {
//				overTimeShiftNight.setOverTimeMidNight(new AttendanceTime(overTimeNight));
//			}
//			if (totalNight != null) {
//				overTimeShiftNight.setMidNightOutSide(new AttendanceTime(totalNight));
//			}
//			List<HolidayMidNightTime> midNightHolidayTimes = new ArrayList<HolidayMidNightTime>();
//			if (legalHdNight != null) {
//				HolidayMidNightTime holidayMidNightTime = new HolidayMidNightTime();
//				holidayMidNightTime.setAttendanceTime(new AttendanceTime(legalHdNight));
//				holidayMidNightTime.setLegalClf(StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork);
//				midNightHolidayTimes.add(holidayMidNightTime);
//			}
//
//			if (nonLegalHdNight != null) {
//				HolidayMidNightTime holidayMidNightTime = new HolidayMidNightTime();
//				holidayMidNightTime.setAttendanceTime(new AttendanceTime(nonLegalHdNight));
//				holidayMidNightTime.setLegalClf(StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork);
//				midNightHolidayTimes.add(holidayMidNightTime);
//			}
//
//
//			if (nonLegalPublicHdNight != null) {
//				HolidayMidNightTime holidayMidNightTime = new HolidayMidNightTime();
//				holidayMidNightTime.setAttendanceTime(new AttendanceTime(nonLegalPublicHdNight));
//				holidayMidNightTime.setLegalClf(StaturoryAtrOfHolidayWork.PublicHolidayWork);
//				midNightHolidayTimes.add(holidayMidNightTime);
//			}
//			applicationTime.setOverTimeShiftNight(Optional.of(overTimeShiftNight));
//
//		}
//
//		if (breakTimeStart1 != null && breakTimeEnd1 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart1, breakTimeEnd1);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//		}
//
//		if (breakTimeStart2 != null && breakTimeEnd2 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart2, breakTimeEnd2);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			if (!appOverTime.getBreakTimeOp().isPresent()) {
//				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//			}
//		}
//		if (breakTimeStart3 != null && breakTimeEnd3 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart3, breakTimeEnd3);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			if (!appOverTime.getBreakTimeOp().isPresent()) {
//				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//			}
//		}
//		if (breakTimeStart4 != null && breakTimeEnd4 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart4, breakTimeEnd4);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			if (!appOverTime.getBreakTimeOp().isPresent()) {
//				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//			}
//		}
//
//		if (breakTimeStart5 != null && breakTimeEnd5 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart5, breakTimeEnd5);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			if (!appOverTime.getBreakTimeOp().isPresent()) {
//				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//			}
//		}
//
//		if (breakTimeStart6 != null && breakTimeEnd6 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart6, breakTimeEnd6);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			if (!appOverTime.getBreakTimeOp().isPresent()) {
//				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//			}
//		}
//
//		if (breakTimeStart7 != null && breakTimeEnd7 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart7, breakTimeEnd7);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			if (!appOverTime.getBreakTimeOp().isPresent()) {
//				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//			}
//		}
//
//		if (breakTimeStart8 != null && breakTimeEnd8 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart8, breakTimeEnd8);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			if (!appOverTime.getBreakTimeOp().isPresent()) {
//				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//			}
//		}
//
//		if (breakTimeStart9 != null && breakTimeEnd9 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart9, breakTimeEnd9);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			if (!appOverTime.getBreakTimeOp().isPresent()) {
//				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//			}
//		}
//
//		if (breakTimeStart10 != null && breakTimeEnd10 != null) {
//			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart10, breakTimeEnd10);
//			breakTimeOp.add(timeZoneWithWorkNo);
//			if (!appOverTime.getBreakTimeOp().isPresent()) {
//				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
//			}
//		}
//
//
//		Integer applicationTime_detail = res.getInt("APPLICATION_TIME");
//		Integer year_month = res.getInt("YEAR_MONTH");
//		Integer actualTime = res.getInt("ACTUAL_TIME");
//		Integer limitErrorTime = res.getInt("LIMIT_ERROR_TIME");
//		Integer limitAlarmTime = res.getInt("LIMIT_ALARM_TIME");
//		Integer excLimitErrorTime = res.getInt("EXCEPTION_LIMIT_ERROR_TIME");
//		Integer excLimitAlarmTime = res.getInt("EXCEPTION_LIMIT_ALARM_TIME");
//		Integer numOfYear36Over = res.getInt("NUM_OF_YEAR36_OVER");
//		Integer actualTimeYear = res.getInt("ACTUAL_TIME_YEAR");
//		Integer limitTimeYear = res.getInt("LIMIT_TIME_YEAR");
//		Integer regApplicationTime = res.getInt("REG_APPLICATION_TIME");
//		Integer refActualTime = res.getInt("REG_ACTUAL_TIME");
//		Integer regLimitTime = res.getInt("REG_LIMIT_TIME");
//		Integer regLimitTimeMulti = res.getInt("REG_LIMIT_TIME_MULTI");
//
//		Time36Agree time36Agree = new Time36Agree();
//		if (applicationTime_detail != null) {
//			time36Agree.setApplicationTime(new AttendanceTimeMonth(applicationTime_detail));
//		}
//		Time36AgreeMonth agreeMonth = new Time36AgreeMonth();
//		agreeMonth.setActualTime(actualTime);
//		agreeMonth.setLimitErrorTime(limitErrorTime);
//		agreeMonth.setLimitAlarmTime(limitAlarmTime);
//		if (excLimitErrorTime != null) {
//			agreeMonth.setExceptionLimitErrorTime(excLimitErrorTime);
//		}
//		if (excLimitAlarmTime != null) {
//			agreeMonth.setExceptionLimitAlarmTime(excLimitAlarmTime);
//		}
//		agreeMonth.setNumOfYear36Over(numOfYear36Over);
//		time36Agree.setAgreeMonth(agreeMonth);
//		Time36AgreeAnnual agreeAnnual = new Time36AgreeAnnual();
//		agreeAnnual.setActualTime(new AttendanceTimeYear(actualTimeYear));
//		agreeAnnual.setLimitTime(new AgreementOneYearTime(limitTimeYear));
//		time36Agree.setAgreeAnnual(agreeAnnual);
//
//		Time36AgreeUpperLimit time36AgreeUpperLimit = new Time36AgreeUpperLimit();
//		time36AgreeUpperLimit.setApplicationTime(new AttendanceTimeMonth(regApplicationTime));
//		Time36AgreeUpperLimitMonth agreeUpperLimitMonth = new Time36AgreeUpperLimitMonth();
//		agreeUpperLimitMonth.updateOverTime(refActualTime);
//		agreeUpperLimitMonth.updateUpperLimitTime(regLimitTime);
//		time36AgreeUpperLimit.setAgreeUpperLimitMonth(agreeUpperLimitMonth);
//
//		Time36AgreeUpperLimitAverage agreeUpperLimitAverage = new Time36AgreeUpperLimitAverage();
//		List<Time36AgreeUpperLimitPerMonth> averageTimeLst = new ArrayList<Time36AgreeUpperLimitPerMonth>();
//		Integer startYm = res.getInt("START_YM");
//		Integer endYm = res.getInt("END_YM");
//		Integer avTime = res.getInt("AVE_TIME");
//		Integer totalTime = res.getInt("TOTAL_TIME");
//		Time36AgreeUpperLimitPerMonth time36AgreeUpperLimitPerMonth = new Time36AgreeUpperLimitPerMonth(
//				startYm,
//				endYm,
//				avTime,
//				totalTime);
//		averageTimeLst.add(time36AgreeUpperLimitPerMonth);
//		agreeUpperLimitAverage.setAverageTimeLst(averageTimeLst);
//		agreeUpperLimitAverage.setUpperLimitTime(new AgreementOneMonthTime(regLimitTimeMulti));
//
//		return appOverTime;
//	}
	@Override
	public void remove(String companyID, String appID) {
		this.commandProxy().remove(KrqdtAppOverTime.class, new KrqdtAppOvertimePK(companyID, appID));
		
	}
	
	@Override
	public Map<String, Integer> getByAppIdAndOTAttr(String companyId, List<String> appIds) { //#115387
		Map<String, Integer> returnMap = new HashMap<>();
		CollectionUtil.split(appIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			returnMap.putAll(this.queryProxy()
					   .query(SELECT_ALL_BY_APP_IDs, KrqdtAppOverTime.class)
					   .setParameter("cid", companyId)
					   .setParameter("appIds", subList)
					   .getList().stream().collect(Collectors.toMap(item -> item.krqdtAppOvertimePK.appId, item -> item.overtimeAtr)));
		});
		return returnMap;
	}
	@Override
	public Map<String, AppOverTime> getHashMapByID(String companyId, List<String> appIds) {
		Map<String, AppOverTime> result = new HashMap<>();
		CollectionUtil.split(appIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			this.queryProxy()
					   .query(SELECT_ALL_BY_APP_IDs, KrqdtAppOverTime.class)
					   .setParameter("cid", companyId)
					   .setParameter("appIds", subList)
					   .getList(x -> result.put(x.krqdtAppOvertimePK.appId, x.toDomain()));
		});
		
		return result;
	}
}
