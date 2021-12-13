package nts.uk.ctx.at.schedule.infra.repository.workschedules;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.schedule.schedulemaster.requestperiodchange.AffInfoForWorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ConfirmedATR;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.EmployeeAndYmd;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchAtdLvwTime;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchAtdLvwTimePK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBasicInfo;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBasicInfoPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBonusPay;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBonusPayPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBreakTs;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBreakTsPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchComeLate;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchComeLatePK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchEditState;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchEditStatePK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchGoingOut;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchGoingOutPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchGoingOutTs;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchGoingOutTsPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchHolidayWork;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchHolidayWorkPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchLeaveEarly;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchLeaveEarlyPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchOvertimeWork;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchOvertimeWorkPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchLeaveEarly;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchPremium;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchPremiumPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchShortTime;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchShortTimePK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchShortTimeTs;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchShortTimeTsPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchTask;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchTaskPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchTime;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchTimePK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaWorkScheduleRepository extends JpaRepository implements WorkScheduleRepository {

	private static final String SELECT_BY_KEY = "SELECT c FROM KscdtSchBasicInfo c WHERE c.pk.sid = :employeeID AND c.pk.ymd = :ymd";

	private static final String SELECT_BY_LIST = "SELECT c FROM KscdtSchBasicInfo c WHERE c.pk.sid IN :sids AND (c.pk.ymd >= :startDate AND c.pk.ymd <= :endDate) ";

	private static final String SELECT_CHECK_UPDATE = "SELECT count (c) FROM KscdtSchBasicInfo c WHERE c.pk.sid = :employeeID AND c.pk.ymd = :ymd";

	private static final String WHERE_PK = "WHERE a.pk.sid = :sid AND a.pk.ymd >= :ymdStart AND a.pk.ymd <= :ymdEnd";

	private static final String DELETE_BY_LIST_DATE = "WHERE a.pk.sid = :sid AND a.pk.ymd IN :ymds";
	
	private static final List<String> DELETE_TABLES = Arrays.asList(
				"DELETE FROM KscdtSchTime a ",
				"DELETE FROM KscdtSchOvertimeWork a ",
				"DELETE FROM KscdtSchHolidayWork a ",
				"DELETE FROM KscdtSchBonusPay a ",
				"DELETE FROM KscdtSchPremium a ",
				"DELETE FROM KscdtSchShortTime a ",
				"DELETE FROM KscdtSchBasicInfo a ",
				"DELETE FROM KscdtSchEditState a ",
				"DELETE FROM KscdtSchAtdLvwTime a ",
				"DELETE FROM KscdtSchShortTimeTs a ",
				"DELETE FROM KscdtSchBreakTs a ",
				"DELETE FROM KscdtSchComeLate a ",
				"DELETE FROM KscdtSchGoingOut a ",
				"DELETE FROM KscdtSchLeaveEarly a "
			);


	public KscdtSchBasicInfo toEntity(WorkSchedule workSchedule, String cID) {
		return KscdtSchBasicInfo.toEntity(workSchedule, cID);
	}



	@Override
	public Optional<WorkSchedule> get(String employeeID, GeneralDate ymd) {
		Optional<WorkSchedule> workSchedule = this.queryProxy().query(SELECT_BY_KEY, KscdtSchBasicInfo.class)
				.setParameter("employeeID", employeeID).setParameter("ymd", ymd)
				.getSingle(c -> c.toDomain(employeeID, ymd));
		return workSchedule;
	}


	@Override
	public List<WorkSchedule> getListBySid(String sid, DatePeriod period) {

		return this.getList(Arrays.asList(sid), period);
	}

	@Override
	public boolean checkExists(String employeeID, GeneralDate ymd) {
		return this.queryProxy()
				.query(SELECT_CHECK_UPDATE, Long.class)
				.setParameter("employeeID", employeeID)
				.setParameter("ymd", ymd).getSingle().get() > 0;
	}

	@Override
	public Map<EmployeeAndYmd, Boolean> checkExists(List<String> employeeIds, DatePeriod period) {

		if ( employeeIds.isEmpty() ) {
			return Collections.emptyMap();
		}
		List<WorkSchedule> ws = 
				this.queryProxy()
					.query(SELECT_BY_LIST, KscdtSchBasicInfo.class)
					.setParameter("sids", employeeIds)
					.setParameter("startDate", period.start())
					.setParameter("endDate", period.end())
					.getList(c -> c.toDomain(c.pk.sid, c.pk.ymd));

		return employeeIds.stream()
			.flatMap( empId -> period.stream().map( ymd -> new EmployeeAndYmd( empId, ymd ) ))
			.collect(Collectors.toMap(
					key -> key
				,	key -> ws.stream().anyMatch(x -> x.getEmployeeID().equals(key.getEmployeeId()) && x.getYmd().equals(key.getYmd()))
				,   (value1, value2) -> {
				    return value1 ? value1 : value2;
				}
			));

	}


	@Override
	public void insert(WorkSchedule workSchedule) {
		String cID = AppContexts.user().companyId();
		this.commandProxy().insert(this.toEntity(workSchedule, cID));
	}

	@Override
	public void insertAll(String cID, List<WorkSchedule> workSchedules) {
		this.commandProxy()
				.insertAll(workSchedules.stream().map(s -> this.toEntity(s, cID)).collect(Collectors.toList()));
	}

	@Override
	public void update(WorkSchedule workSchedule) {
		String cID = AppContexts.user().companyId();
		/*
		 * Optional<KscdtSchBasicInfo> oldData = this.queryProxy().query(SELECT_BY_KEY,
		 * KscdtSchBasicInfo.class) .setParameter("employeeID",
		 * workSchedule.getEmployeeID()).setParameter("ymd", workSchedule.getYmd())
		 * .getSingle(c -> c);
		 */
		KscdtSchBasicInfo entity = KscdtSchBasicInfo.toEntity(workSchedule, cID);
		Optional<KscdtSchBasicInfo> oldData = this.queryProxy().find(entity.getPk(), KscdtSchBasicInfo.class);
		if (oldData.isPresent()) {
			KscdtSchBasicInfo newData = KscdtSchBasicInfo.toEntity(workSchedule, cID);
			oldData.get().confirmedATR = newData.confirmedATR;
			oldData.get().empCd = newData.empCd;
			oldData.get().jobId = newData.jobId;
			oldData.get().wkpId = newData.wkpId;
			oldData.get().clsCd = newData.clsCd;
			oldData.get().busTypeCd = newData.busTypeCd;
			oldData.get().nurseLicense = newData.nurseLicense;
			oldData.get().wktpCd = newData.wktpCd;
			oldData.get().wktmCd = newData.wktmCd;
			oldData.get().goStraightAtr = newData.goStraightAtr;
			oldData.get().backStraightAtr = newData.backStraightAtr;
			oldData.get().treatAsSubstituteAtr = newData.treatAsSubstituteAtr;
			oldData.get().treatAsSubstituteDays = newData.treatAsSubstituteDays;

			// kscdtSchTime
			if (oldData.get().kscdtSchTime != null) {
				oldData.get().kscdtSchTime.cid = newData.kscdtSchTime.cid;
				oldData.get().kscdtSchTime.count = newData.kscdtSchTime.count;
				oldData.get().kscdtSchTime.totalTime = newData.kscdtSchTime.totalTime;
				oldData.get().kscdtSchTime.totalTimeAct = newData.kscdtSchTime.totalTimeAct;
				oldData.get().kscdtSchTime.prsWorkTime = newData.kscdtSchTime.prsWorkTime;
				oldData.get().kscdtSchTime.prsWorkTimeAct = newData.kscdtSchTime.prsWorkTimeAct;
				oldData.get().kscdtSchTime.prsPrimeTime = newData.kscdtSchTime.prsPrimeTime;
				oldData.get().kscdtSchTime.prsMidniteTime = newData.kscdtSchTime.prsMidniteTime;
				oldData.get().kscdtSchTime.extBindTimeOtw = newData.kscdtSchTime.extBindTimeOtw;
				oldData.get().kscdtSchTime.extBindTimeHw = newData.kscdtSchTime.extBindTimeHw;
				oldData.get().kscdtSchTime.extVarwkOtwTimeLegal = newData.kscdtSchTime.extVarwkOtwTimeLegal;
				oldData.get().kscdtSchTime.extFlexTime = newData.kscdtSchTime.extFlexTime;
				oldData.get().kscdtSchTime.extFlexTimePreApp = newData.kscdtSchTime.extFlexTimePreApp;
				oldData.get().kscdtSchTime.extMidNiteOtwTime = newData.kscdtSchTime.extMidNiteOtwTime;
				oldData.get().kscdtSchTime.extMidNiteHdwTimeLghd = newData.kscdtSchTime.extMidNiteHdwTimeLghd;
				oldData.get().kscdtSchTime.extMidNiteHdwTimeIlghd = newData.kscdtSchTime.extMidNiteHdwTimeIlghd;
				oldData.get().kscdtSchTime.extMidNiteHdwTimePubhd = newData.kscdtSchTime.extMidNiteHdwTimePubhd;
				oldData.get().kscdtSchTime.extMidNiteTotal = newData.kscdtSchTime.extMidNiteTotal;
				oldData.get().kscdtSchTime.extMidNiteTotalPreApp = newData.kscdtSchTime.extMidNiteTotalPreApp;
				oldData.get().kscdtSchTime.intervalAtdClock = newData.kscdtSchTime.intervalAtdClock;
				oldData.get().kscdtSchTime.intervalTime = newData.kscdtSchTime.intervalTime;
				oldData.get().kscdtSchTime.brkTotalTime = newData.kscdtSchTime.brkTotalTime;
				oldData.get().kscdtSchTime.hdPaidTime = newData.kscdtSchTime.hdPaidTime;
				oldData.get().kscdtSchTime.hdPaidHourlyTime = newData.kscdtSchTime.hdPaidHourlyTime;
				oldData.get().kscdtSchTime.hdComTime = newData.kscdtSchTime.hdComTime;
				oldData.get().kscdtSchTime.hdComHourlyTime = newData.kscdtSchTime.hdComHourlyTime;
				oldData.get().kscdtSchTime.hd60hTime = newData.kscdtSchTime.hd60hTime;
				oldData.get().kscdtSchTime.hd60hHourlyTime = newData.kscdtSchTime.hd60hHourlyTime;
				oldData.get().kscdtSchTime.hdspTime = newData.kscdtSchTime.hdspTime;
				oldData.get().kscdtSchTime.hdspHourlyTime = newData.kscdtSchTime.hdspHourlyTime;
				oldData.get().kscdtSchTime.hdstkTime = newData.kscdtSchTime.hdstkTime;
				oldData.get().kscdtSchTime.hdHourlyTime = newData.kscdtSchTime.hdHourlyTime;
				oldData.get().kscdtSchTime.hdHourlyShortageTime = newData.kscdtSchTime.hdHourlyShortageTime;
				oldData.get().kscdtSchTime.absenceTime = newData.kscdtSchTime.absenceTime;
				oldData.get().kscdtSchTime.vacationAddTime = newData.kscdtSchTime.vacationAddTime;
				oldData.get().kscdtSchTime.staggeredWhTime = newData.kscdtSchTime.staggeredWhTime;
			}

			if (oldData.get().kscdtSchTime != null) {
				// List<KscdtSchOvertimeWork> overtimeWorks
				oldData.get().kscdtSchTime.overtimeWorks = removeInsertData(oldData.get().kscdtSchTime.overtimeWorks,
						newData.kscdtSchTime.overtimeWorks, (x, y) -> {
							return y.pk.frameNo == x.pk.frameNo;
						});

				// List<KscdtSchHolidayWork> holidayWorks
				oldData.get().kscdtSchTime.holidayWorks = removeInsertData(oldData.get().kscdtSchTime.holidayWorks,
						newData.kscdtSchTime.holidayWorks, (x, y) -> {
							return y.pk.frameNo == x.pk.frameNo;
						});

				// List<KscdtSchBonusPay> bonusPays
				if (!oldData.get().kscdtSchTime.bonusPays.isEmpty()) {
					for (KscdtSchBonusPay y : newData.kscdtSchTime.bonusPays) {
						oldData.get().kscdtSchTime.bonusPays.forEach(x -> {
							if (y.pk.frameNo == x.pk.frameNo && y.pk.bonuspayType == x.pk.bonuspayType) {
								x.cid = y.cid;
								x.premiumTime = y.premiumTime;
								x.premiumTimeWithIn = y.premiumTimeWithIn;
								x.premiumTimeWithOut = y.getPremiumTimeWithOut();
							}
						});
					}
				}

				// List<KscdtSchPremium> premiums
				if (!oldData.get().kscdtSchTime.premiums.isEmpty()) {
					for (KscdtSchPremium y : newData.kscdtSchTime.premiums) {
						oldData.get().kscdtSchTime.premiums.forEach(x -> {
							if (y.pk.frameNo == x.pk.frameNo) {
								x.cid = y.cid;
								x.premiumTime = y.premiumTime;
							}
						});
					}
				}

				// List<KscdtSchShortTime> shortTimes
				if (!oldData.get().kscdtSchTime.shortTimes.isEmpty()) {
					for (KscdtSchShortTime y : newData.kscdtSchTime.shortTimes) {
						oldData.get().kscdtSchTime.shortTimes.forEach(x -> {
							if (y.pk.childCareAtr == x.pk.childCareAtr) {
								x.cid = y.cid;
								x.count = y.count;
								x.totalTime = y.totalTime;
								x.totalTimeWithIn = y.totalTimeWithIn;
								x.totalTimeWithOut = y.getTotalTimeWithOut();
							}
						});
					}
				}
				///

				// #114431
				// KSCDT_SCH_COME_LATE
				if (!oldData.get().kscdtSchTime.kscdtSchComeLate.isEmpty()) {
					// get list insert and update data exist
					List<KscdtSchComeLate> listInsert = new ArrayList<>();
					for (KscdtSchComeLate schComeLate : newData.kscdtSchTime.kscdtSchComeLate) {
						boolean checkExist = false;
						for (KscdtSchComeLate schComeLateOld : oldData.get().kscdtSchTime.kscdtSchComeLate) {
							if (schComeLate.pk.workNo == schComeLateOld.pk.workNo
									&& schComeLate.pk.sid.equals(schComeLateOld.pk.sid)
									&& schComeLate.pk.ymd.equals(schComeLateOld.pk.ymd)) {
								schComeLateOld.useHourlyHdPaid = schComeLate.useHourlyHdPaid;
								schComeLateOld.useHourlyHdCom = schComeLate.useHourlyHdCom;
								schComeLateOld.useHourlyHd60h = schComeLate.useHourlyHd60h;
								schComeLateOld.useHourlyHdSpNO = schComeLate.useHourlyHdSpNO;
								schComeLateOld.useHourlyHdSpTime = schComeLate.useHourlyHdSpTime;
								schComeLateOld.useHourlyHdChildCare = schComeLate.useHourlyHdChildCare;
								schComeLateOld.useHourlyHdNurseCare = schComeLate.useHourlyHdNurseCare;
								checkExist = true;
							}
						}

						if (!checkExist) {
							listInsert.add(schComeLate);
						}
					}
					// get list remove
					List<KscdtSchComeLate> listRemove = new ArrayList<>();
					for (KscdtSchComeLate schComeLateOld : oldData.get().kscdtSchTime.kscdtSchComeLate) {
						boolean checkExist = false;
						for (KscdtSchComeLate schComeLate : newData.kscdtSchTime.kscdtSchComeLate) {
							if (schComeLate.pk.workNo == schComeLateOld.pk.workNo
									&& schComeLate.pk.sid.equals(schComeLateOld.pk.sid)
									&& schComeLate.pk.ymd.equals(schComeLateOld.pk.ymd)) {
								checkExist = true;
								break;
							}
						}
						if (!checkExist) {
							listRemove.add(schComeLateOld);
						}
					}

					// remove
					String delete = "delete from KscdtSchComeLate o " + " where o.pk.sid = :sid "
							+ " and o.pk.ymd = :ymd " + " and o.pk.workNo = :workNo";
					for (KscdtSchComeLate sle : listRemove) {
						this.getEntityManager().createQuery(delete)
								.setParameter("sid", sle.pk.sid)
								.setParameter("ymd", sle.pk.ymd)
								.setParameter("workNo", sle.pk.workNo)
								.executeUpdate();
					}
					// add
					for (KscdtSchComeLate sle : listInsert) {
						this.commandProxy().insert(sle);
					}

				} else {
					oldData.get().kscdtSchTime.kscdtSchComeLate = newData.kscdtSchTime.kscdtSchComeLate;
				}

				// KSCDT_SCH_GOING_OUT
				if (!oldData.get().kscdtSchTime.kscdtSchGoingOut.isEmpty()) {
					// get list insert and update data exist
					List<KscdtSchGoingOut> listInsert = new ArrayList<>();
					for (KscdtSchGoingOut schGoingOut : newData.kscdtSchTime.kscdtSchGoingOut) {
						boolean checkExist = false;
						for (KscdtSchGoingOut schGoingOutOld : oldData.get().kscdtSchTime.kscdtSchGoingOut) {
							if (schGoingOut.pk.reasonAtr == schGoingOutOld.pk.reasonAtr
									&& schGoingOut.pk.sid.equals(schGoingOutOld.pk.sid)
									&& schGoingOut.pk.ymd.equals(schGoingOutOld.pk.ymd)) {
								schGoingOutOld.useHourlyHdPaid = schGoingOut.useHourlyHdPaid;
								schGoingOutOld.useHourlyHdCom = schGoingOut.useHourlyHdCom;
								schGoingOutOld.useHourlyHd60h = schGoingOut.useHourlyHd60h;
								schGoingOutOld.useHourlyHdSpNO = schGoingOut.useHourlyHdSpNO;
								schGoingOutOld.useHourlyHdSpTime = schGoingOut.useHourlyHdSpTime;
								schGoingOutOld.useHourlyHdChildCare = schGoingOut.useHourlyHdChildCare;
								schGoingOutOld.useHourlyHdNurseCare = schGoingOut.useHourlyHdNurseCare;
								checkExist = true;
							}
						}

						if (!checkExist) {
							listInsert.add(schGoingOut);
						}
					}
					// get list remove
					List<KscdtSchGoingOut> listRemove = new ArrayList<>();
					for (KscdtSchGoingOut schGoingOutOld : oldData.get().kscdtSchTime.kscdtSchGoingOut) {
						boolean checkExist = false;
						for (KscdtSchGoingOut schGoingOut : newData.kscdtSchTime.kscdtSchGoingOut) {
							if (schGoingOut.pk.reasonAtr == schGoingOutOld.pk.reasonAtr
									&& schGoingOut.pk.sid.equals(schGoingOutOld.pk.sid)
									&& schGoingOut.pk.ymd.equals(schGoingOutOld.pk.ymd)) {
								checkExist = true;
								break;
							}
						}
						if (!checkExist) {
							listRemove.add(schGoingOutOld);
						}
					}

					// remove
					String delete = "delete from KscdtSchGoingOut o " + " where o.pk.sid = :sid "
							+ " and o.pk.ymd = :ymd " + " and o.pk.reasonAtr = :reasonAtr";
					for (KscdtSchGoingOut sgo : listRemove) {
						this.getEntityManager().createQuery(delete)
								.setParameter("sid", sgo.pk.sid)
								.setParameter("ymd", sgo.pk.ymd)
								.setParameter("reasonAtr", sgo.pk.reasonAtr)
								.executeUpdate();
					}
					// add
					for (KscdtSchGoingOut sgo : listInsert) {
						this.commandProxy().insert(sgo);
					}

				} else {
					oldData.get().kscdtSchTime.kscdtSchGoingOut = newData.kscdtSchTime.kscdtSchGoingOut;
				}

				// kscdtSchLeaveEarly
				if (!oldData.get().kscdtSchTime.kscdtSchLeaveEarly.isEmpty()) {
					// get list insert and update data exist
					List<KscdtSchLeaveEarly> listInsert = new ArrayList<>();
					for (KscdtSchLeaveEarly schLeaveEarly : newData.kscdtSchTime.kscdtSchLeaveEarly) {
						boolean checkExist = false;
						for (KscdtSchLeaveEarly schLeaveEarlyOld : oldData.get().kscdtSchTime.kscdtSchLeaveEarly) {
							if (schLeaveEarly.pk.workNo == schLeaveEarlyOld.pk.workNo
									&& schLeaveEarly.pk.sid.equals(schLeaveEarlyOld.pk.sid)
									&& schLeaveEarly.pk.ymd.equals(schLeaveEarlyOld.pk.ymd)) {
								schLeaveEarlyOld.useHourlyHdPaid = schLeaveEarly.useHourlyHdPaid;
								schLeaveEarlyOld.useHourlyHdCom = schLeaveEarly.useHourlyHdCom;
								schLeaveEarlyOld.useHourlyHd60h = schLeaveEarly.useHourlyHd60h;
								schLeaveEarlyOld.useHourlyHdSpNO = schLeaveEarly.useHourlyHdSpNO;
								schLeaveEarlyOld.useHourlyHdSpTime = schLeaveEarly.useHourlyHdSpTime;
								schLeaveEarlyOld.useHourlyHdChildCare = schLeaveEarly.useHourlyHdChildCare;
								schLeaveEarlyOld.useHourlyHdNurseCare = schLeaveEarly.useHourlyHdNurseCare;
								checkExist = true;
							}
						}

						if (!checkExist) {
							listInsert.add(schLeaveEarly);
						}
					}
					// get list remove
					List<KscdtSchLeaveEarly> listRemove = new ArrayList<>();
					for (KscdtSchLeaveEarly schLeaveEarlyOld : oldData.get().kscdtSchTime.kscdtSchLeaveEarly) {
						boolean checkExist = false;
						for (KscdtSchLeaveEarly schLeaveEarly : newData.kscdtSchTime.kscdtSchLeaveEarly) {
							if (schLeaveEarly.pk.workNo == schLeaveEarlyOld.pk.workNo
									&& schLeaveEarly.pk.sid.equals(schLeaveEarlyOld.pk.sid)
									&& schLeaveEarly.pk.ymd.equals(schLeaveEarlyOld.pk.ymd)) {
								checkExist = true;
								break;
							}
						}
						if (!checkExist) {
							listRemove.add(schLeaveEarlyOld);
						}
					}

					// remove
					String delete = "delete from KscdtSchLeaveEarly o " + " where o.pk.sid = :sid "
							+ " and o.pk.ymd = :ymd " + " and o.pk.workNo = :workNo";
					for (KscdtSchLeaveEarly sle : listRemove) {
						this.getEntityManager().createQuery(delete)
								.setParameter("sid", sle.pk.sid)
								.setParameter("ymd", sle.pk.ymd)
								.setParameter("workNo", sle.pk.workNo)
								.executeUpdate();
					}
					// add
					for (KscdtSchLeaveEarly sle : listInsert) {
						this.commandProxy().insert(sle);
					}

				} else {
					oldData.get().kscdtSchTime.kscdtSchLeaveEarly = newData.kscdtSchTime.kscdtSchLeaveEarly;
				}
			}

			// List<KscdtSchTask> kscdtSchTask;
			if (!oldData.get().kscdtSchTime.kscdtSchTask.isEmpty()) {
				// remove
				String delete = "delete from KscdtSchTask o " + " where o.pk.sid = :sid " + " and o.pk.ymd = :ymd";
				this.getEntityManager().createQuery(delete).setParameter("sid", newData.pk.sid)
									.setParameter("ymd", newData.pk.ymd)
									.executeUpdate();
				
				oldData.get().kscdtSchTime.kscdtSchTask = new ArrayList<KscdtSchTask>();
				this.commandProxy().insertAll(newData.kscdtSchTime.kscdtSchTask);

			} else {
				oldData.get().kscdtSchTime.kscdtSchTask = newData.kscdtSchTime.kscdtSchTask;
			}

			// List<KscdtSchEditState> editStates;
			if (!oldData.get().editStates.isEmpty()) {
				// get list insert and update data exist
				List<KscdtSchEditState> listInsert = new ArrayList<>();
				for (KscdtSchEditState schState : newData.editStates) {
					List<KscdtSchEditState> checkLst = new ArrayList<>();
					oldData.get().editStates.forEach(x -> {
						if (schState.pk.sid.equals(x.pk.sid)
								&& schState.pk.ymd.equals(x.pk.ymd)
								&& schState.pk.atdItemId == x.pk.atdItemId) {
							x.sditState = schState.sditState;
							x.cid = schState.cid;
							checkLst.add(x);
						}
					});
					if (checkLst.isEmpty()) {
						listInsert.add(schState);
					}
				}

				List<KscdtSchEditState> listRemove = new ArrayList<>();
				for (KscdtSchEditState editOld : oldData.get().editStates) {
					boolean checkExist = false;
					for (KscdtSchEditState edit : newData.editStates) {
						if (edit.pk.atdItemId == editOld.pk.atdItemId && edit.pk.sid.equals(editOld.pk.sid)
								&& edit.pk.ymd.equals(editOld.pk.ymd)) {
							checkExist = true;
							break;
						}
					}

					if (!checkExist) {
						listRemove.add(editOld);
					}
				}

				// remove
				String delete = "delete from KscdtSchEditState o " + " where o.pk.sid = :sid " + " and o.pk.ymd = :ymd "
						+ " and o.pk.atdItemId = :atdItemId";
				for (KscdtSchEditState sle : listRemove) {
					this.getEntityManager().createQuery(delete).setParameter("sid", sle.pk.sid)
							.setParameter("ymd", sle.pk.ymd).setParameter("atdItemId", sle.pk.atdItemId)
							.executeUpdate();
				}

				// add
				for (KscdtSchEditState sle : listInsert) {
					this.commandProxy().insert(sle);
				}

			} else {
				oldData.get().editStates = newData.editStates;
			}

			// List<KscdtSchAtdLvwTime> atdLvwTimes;
			if (!oldData.get().atdLvwTimes.isEmpty()) {
				// get list insert and update data exist
				List<KscdtSchAtdLvwTime> listInsert = new ArrayList<>();
				for (KscdtSchAtdLvwTime atdLvw : newData.atdLvwTimes) {
					List<KscdtSchAtdLvwTime> checkLst = new ArrayList<>();
					oldData.get().atdLvwTimes.forEach(x -> {
						// Update data
						if (atdLvw.pk.workNo == x.pk.workNo) {
							x.cid = atdLvw.cid;
							x.atdClock = atdLvw.atdClock;
							x.lwkClock = atdLvw.lwkClock;
							x.atdHourlyHDTSStart = atdLvw.atdHourlyHDTSStart;
							x.atdHourlyHDTSEnd = atdLvw.atdHourlyHDTSEnd;
							x.lvwHourlyHDTSStart = atdLvw.lvwHourlyHDTSStart;
							x.lvwHourlyHDTSEnd = atdLvw.lvwHourlyHDTSEnd;
							checkLst.add(x);
						}
					});
					if (checkLst.isEmpty()) {
						listInsert.add(atdLvw);
					}
				}

				List<KscdtSchAtdLvwTime> listRemove = new ArrayList<>();
				for (KscdtSchAtdLvwTime atdLvwOld : oldData.get().atdLvwTimes) {
					boolean checkExist = false;
					for (KscdtSchAtdLvwTime atdLvw : newData.atdLvwTimes) {
						if (atdLvw.pk.workNo == atdLvwOld.pk.workNo && atdLvw.pk.sid.equals(atdLvwOld.pk.sid)
								&& atdLvw.pk.ymd.equals(atdLvwOld.pk.ymd)) {
							checkExist = true;
							break;
						}
					}
					if (!checkExist) {
						listRemove.add(atdLvwOld);
					}
				}

				// remove
				String delete = "delete from KscdtSchAtdLvwTime o " + " where o.pk.sid = :sid "
						+ " and o.pk.ymd = :ymd " + " and o.pk.workNo = :workNo";
				for (KscdtSchAtdLvwTime sle : listRemove) {
					this.getEntityManager().createQuery(delete).setParameter("sid", sle.pk.sid)
							.setParameter("ymd", sle.pk.ymd).setParameter("workNo", sle.pk.workNo).executeUpdate();
				}
				// add
				for (KscdtSchAtdLvwTime sle : listInsert) {
					this.commandProxy().insert(sle);
				}

			} else {
				oldData.get().atdLvwTimes = newData.atdLvwTimes;
			}

			// List<KscdtSchShortTimeTs> schShortTimeTs
			if (!oldData.get().schShortTimeTs.isEmpty()) {
				// get list insert and update data exist
				List<KscdtSchShortTimeTs> listInsert = new ArrayList<>();
				for (KscdtSchShortTimeTs shortTs : newData.schShortTimeTs) {
					List<KscdtSchShortTimeTs> checkLst = new ArrayList<>();
					oldData.get().schShortTimeTs.forEach(x -> {
						// Update data
						if (x.pk.frameNo == shortTs.pk.frameNo && x.pk.childCareAtr == shortTs.pk.childCareAtr) {
							x.cid = shortTs.cid;
							x.shortTimeTsStart = shortTs.shortTimeTsStart;
							x.shortTimeTsEnd = shortTs.shortTimeTsEnd;
							checkLst.add(x);
						}
					});
					if (checkLst.isEmpty()) {
						listInsert.add(shortTs);
					}
				}

				List<KscdtSchShortTimeTs> listRemove = new ArrayList<>();
				for (KscdtSchShortTimeTs shortTsOld : oldData.get().schShortTimeTs) {
					boolean checkExist = false;
					for (KscdtSchShortTimeTs shortTs : newData.schShortTimeTs) {
						if (shortTs.pk.frameNo == shortTsOld.pk.frameNo && shortTs.pk.sid.equals(shortTsOld.pk.sid)
								&& shortTs.pk.ymd.equals(shortTsOld.pk.ymd)) {
							checkExist = true;
							break;
						}
					}
					if (!checkExist) {
						listRemove.add(shortTsOld);
					}
				}

				// remove
				String delete = "delete from KscdtSchShortTimeTs o " + " where o.pk.sid = :sid "
						+ " and o.pk.ymd = :ymd " + " and o.pk.frameNo = :frameNo";
				for (KscdtSchShortTimeTs sle : listRemove) {
					this.getEntityManager().createQuery(delete).setParameter("sid", sle.pk.sid)
							.setParameter("ymd", sle.pk.ymd).setParameter("frameNo", sle.pk.frameNo).executeUpdate();
				}
				// add
				for (KscdtSchShortTimeTs sle : listInsert) {
					this.commandProxy().insert(sle);
				}

			} else {
				oldData.get().schShortTimeTs = newData.schShortTimeTs;
			}

			// KscdtSchBreakTs
			if (!oldData.get().breakTs.isEmpty()) {
				// get list insert and update data exist
				List<KscdtSchBreakTs> listInsert = new ArrayList<>();
				for (KscdtSchBreakTs schBrk : newData.breakTs) {
					List<KscdtSchBreakTs> checkLst = new ArrayList<>();
					oldData.get().breakTs.forEach(x -> {
						if (schBrk.pk.sid.equals(x.pk.sid)
								&& schBrk.pk.ymd.equals(x.pk.ymd)
								&& schBrk.pk.frameNo == x.pk.frameNo) {
							x.cid = schBrk.cid;
							x.breakTsStart = schBrk.breakTsStart;
							x.breakTsEnd = schBrk.breakTsEnd;
							checkLst.add(x);
						}
					});
					if (checkLst.isEmpty()) {
						listInsert.add(schBrk);
					}
				}

				// get list remove
				List<KscdtSchBreakTs> listRemove = new ArrayList<>();
				for (KscdtSchBreakTs schBrkTsOld : oldData.get().breakTs) {
					boolean checkLst = false;
					for (KscdtSchBreakTs schBrk : newData.breakTs) {
						if (schBrk.pk.frameNo == schBrkTsOld.pk.frameNo
								&& schBrk.pk.sid.equals(schBrkTsOld.pk.sid)
								&& schBrk.pk.ymd.equals(schBrkTsOld.pk.ymd)) {
							checkLst = true;
							break;
						}
					}
					if (!checkLst) {
						listRemove.add(schBrkTsOld);
					}
				}

				// remove
				String delete = "delete from KscdtSchBreakTs o "
						+ " where o.pk.sid = :sid "
						+ " and o.pk.ymd = :ymd "
						+ " and o.pk.frameNo = :frameNo";
				for (KscdtSchBreakTs sle : listRemove) {
					this.getEntityManager().createQuery(delete)
							.setParameter("sid", sle.pk.sid)
							.setParameter("ymd", sle.pk.ymd)
							.setParameter("frameNo", sle.pk.frameNo)
							.executeUpdate();
				}

				// add
				for (KscdtSchBreakTs sle : listInsert) {
					this.commandProxy().insert(sle);
				}

			} else {
				oldData.get().breakTs = newData.breakTs;
			}

			// #114431
			if (!oldData.get().kscdtSchGoingOutTs.isEmpty()) {
				oldData.get().kscdtSchGoingOutTs.sort(Comparator.comparing(KscdtSchGoingOutTs::getGoingOutClock));
				newData.kscdtSchGoingOutTs.sort(Comparator.comparing(KscdtSchGoingOutTs::getGoingOutClock));
				int sizeOld = oldData.get().kscdtSchGoingOutTs.size();
				int sizeNew = newData.kscdtSchGoingOutTs.size();

				if (sizeOld > sizeNew) {
					// remove
					for (int i = 0; i < sizeNew; i++) {
						oldData.get().kscdtSchGoingOutTs.get(i).cid = newData.kscdtSchGoingOutTs.get(i).cid;
						oldData.get().kscdtSchGoingOutTs.get(i).reasonAtr = newData.kscdtSchGoingOutTs.get(i).reasonAtr;
						oldData.get().kscdtSchGoingOutTs.get(i).goingOutClock = newData.kscdtSchGoingOutTs.get(i).goingOutClock;
						oldData.get().kscdtSchGoingOutTs.get(i).goingBackClock = newData.kscdtSchGoingOutTs.get(i).goingBackClock;
					}
					for (int i = sizeOld - 1; i >= sizeNew; i--) {
						oldData.get().kscdtSchGoingOutTs.remove(i);
					}

				} else {
					for (int i = 0; i < sizeOld; i++) {
						oldData.get().kscdtSchGoingOutTs.get(i).cid = newData.kscdtSchGoingOutTs.get(i).cid;
						oldData.get().kscdtSchGoingOutTs.get(i).reasonAtr = newData.kscdtSchGoingOutTs.get(i).reasonAtr;
						oldData.get().kscdtSchGoingOutTs.get(i).goingOutClock = newData.kscdtSchGoingOutTs.get(i).goingOutClock;
						oldData.get().kscdtSchGoingOutTs.get(i).goingBackClock = newData.kscdtSchGoingOutTs.get(i).goingBackClock;
					}
					for (int i = sizeOld; i < sizeNew; i++) {
						oldData.get().kscdtSchGoingOutTs.add(newData.kscdtSchGoingOutTs.get(i));
					}
				}
			} else {
				oldData.get().kscdtSchGoingOutTs = newData.kscdtSchGoingOutTs;
			}
			this.commandProxy().update(oldData.get());
		}
	}


	@Override
	public void delete(String sid, DatePeriod datePeriod) {
		for (val deleteTable : DELETE_TABLES) {
			this.getEntityManager().createQuery(deleteTable + WHERE_PK)
					.setParameter("sid", sid)
					.setParameter("ymdStart", datePeriod.start())
					.setParameter("ymdEnd", datePeriod.end())
					.executeUpdate();
		}
	}

	@Override
	public void delete(String sid, GeneralDate ymd) {
		Optional<WorkSchedule> optWorkSchedule = this.get(sid, ymd);
		if (optWorkSchedule.isPresent()) {
			KscdtSchBasicInfoPK pk = new KscdtSchBasicInfoPK(optWorkSchedule.get().getEmployeeID(),
					optWorkSchedule.get().getYmd());
			this.commandProxy().remove(KscdtSchBasicInfo.class, pk);
			this.getEntityManager().flush();
		}
	}

	@Override
	public void deleteListDate(String sid, List<GeneralDate> ymds) {
		if (ymds.isEmpty())
			return;
		for (val deleteTable : DELETE_TABLES) {
			this.getEntityManager().createQuery(deleteTable + DELETE_BY_LIST_DATE)
					.setParameter("sid", sid)
					.setParameter("ymds", ymds)
					.executeUpdate();
		}
	}


	private static final String GET_MAX_DATE_WORK_SCHE_BY_LIST_EMP = "SELECT c.pk.ymd FROM KscdtSchBasicInfo c "
			+ " WHERE c.pk.sid IN :listEmp"
			+ " ORDER BY c.pk.ymd desc ";
	@Override
	public Optional<GeneralDate> getMaxDateWorkSche(List<String> listEmp) {
		List<GeneralDate> data = this.getEntityManager()
				.createQuery(GET_MAX_DATE_WORK_SCHE_BY_LIST_EMP, GeneralDate.class)
				.setParameter("listEmp", listEmp)
				.setMaxResults(1).getResultList();
		if (data.isEmpty())
			return Optional.empty();
		return Optional.of(data.get(0));
	}
	
	@Override
	public List<WorkSchedule> getList(List<String> sids, DatePeriod period) {
		if (sids.isEmpty() || period == null)
			return new ArrayList<>();
		
		List<WorkSchedule> result = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String listEmp = "(";
			for (int i = 0; i < subList.size(); i++) {
				listEmp += "'" + subList.get(i) + "',";
			}
			// remove last , in string and add )
			listEmp = listEmp.substring(0, listEmp.length() - 1) + ")";
			
			// ActualWorkingTimeOfDaily
			Map<Pair<String, GeneralDate>, KscdtSchTime> mapPairSchTime = this.getKscdtSchTimes(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchOvertimeWork>> mapPairOvertimeWork = this.getOvertimeWorks(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchHolidayWork>> mapPairHolidayWork = this.getHolidayWorks(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchBonusPay>> mapPairBonusPay = this.getBonusPays(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchPremium>> mapPairSchPremium = this.getPremiums(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchShortTime>> mapPairShortTime = this.getShortTimes(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchComeLate>> mapPairComeLate = this.getKscdtSchComeLates(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchGoingOut>> mapPairGoingOut = this.getKscdtSchGoingOuts(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchLeaveEarly>> mapPairLeaveEarly = this.getKscdtSchLeaveEarlys(listEmp, period);
			 Map<Pair<String, GeneralDate>, List<KscdtSchTask>> mapPairKscdtSchTask =  this.getKscdtSchTasks(listEmp, period);
			
			// WorkSchedule
			Map<Pair<String, GeneralDate>, KscdtSchBasicInfo> mapPairSchBasicInfo = this.getSchBasicInfo(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchEditState>> mapPairSchEditState = this.getSchEditState(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchAtdLvwTime>> mapPairSchAtdLvwTime = this.getSchAtdLvwTime(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchShortTimeTs>> mapPairSchShortTimeTs = this.getSchShortTimeTs(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchBreakTs>> mapPairSchBreakTs = this.getKscdtSchBreakTs(listEmp, period);
			Map<Pair<String, GeneralDate>, List<KscdtSchGoingOutTs>> mapPairGoingOutTs = this.getKscdtSchGoingOutTs(listEmp, period);
			
			for (int i = 0; i < subList.size(); i++) {
				String sid = subList.get(i);
				period.datesBetween().forEach(ymd -> {
					Pair<String, GeneralDate> key = Pair.of(sid, ymd);
					
					if (mapPairSchBasicInfo.containsKey(key)) {
						
						KscdtSchBasicInfo basicInfo = mapPairSchBasicInfo.get(key);
						basicInfo.editStates = mapPairSchEditState.getOrDefault(key, new ArrayList<>()); 
						basicInfo.atdLvwTimes = mapPairSchAtdLvwTime.getOrDefault(key, new ArrayList<>()); 
						basicInfo.schShortTimeTs = mapPairSchShortTimeTs.getOrDefault(key, new ArrayList<>()); 
						basicInfo.breakTs = mapPairSchBreakTs.getOrDefault(key, new ArrayList<>()); 
						basicInfo.kscdtSchGoingOutTs = mapPairGoingOutTs.getOrDefault(key, new ArrayList<>()); 
						
						if(mapPairSchTime.containsKey(key)){
							KscdtSchTime scheTime = mapPairSchTime.get(key);
							
							scheTime.overtimeWorks = mapPairOvertimeWork.getOrDefault(key, new ArrayList<>()); 
							scheTime.holidayWorks = mapPairHolidayWork.getOrDefault(key, new ArrayList<>());
							scheTime.bonusPays = mapPairBonusPay.getOrDefault(key, new ArrayList<>());
							scheTime.premiums = mapPairSchPremium.getOrDefault(key, new ArrayList<>());
							scheTime.shortTimes = mapPairShortTime.getOrDefault(key, new ArrayList<>());
							scheTime.kscdtSchComeLate = mapPairComeLate.getOrDefault(key, new ArrayList<>());
							scheTime.kscdtSchGoingOut = mapPairGoingOut.getOrDefault(key, new ArrayList<>());
							scheTime.kscdtSchLeaveEarly = mapPairLeaveEarly.getOrDefault(key, new ArrayList<>());
							scheTime.kscdtSchTask = mapPairKscdtSchTask.getOrDefault(key, new ArrayList<>());
							
							basicInfo.kscdtSchTime = scheTime;
						}
						
						result.add(basicInfo.toDomain(sid, ymd));
					}
				});
			}
		});
		return result;

	}
	
	@Override
	public Map<EmployeeAndYmd, ConfirmedATR> getConfirmedStatus(List<String> employeeIds, DatePeriod period) {

		if ( employeeIds.isEmpty() ) {
			return Collections.emptyMap();
		}
		List<WorkSchedule> ws = 
				this.queryProxy()
					.query(SELECT_BY_LIST, KscdtSchBasicInfo.class)
					.setParameter("sids", employeeIds)
					.setParameter("startDate", period.start())
					.setParameter("endDate", period.end())
					.getList(c -> c.toDomain(c.pk.sid, c.pk.ymd));

		return ws.stream()
			.collect(Collectors.toMap(
					key -> new EmployeeAndYmd(((WorkSchedule)key).getEmployeeID(), ((WorkSchedule)key).getYmd()),
					key -> ((WorkSchedule)key).getConfirmedATR()
			));

	}
	
	// KSCDT_SCH_BASIC_INFO
	private Map<Pair<String, GeneralDate>, KscdtSchBasicInfo> getSchBasicInfo(String listEmp, DatePeriod period) {

		List<KscdtSchBasicInfo> listSchBasicInfo = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_BASIC_INFO.SID, KSCDT_SCH_BASIC_INFO.YMD, KSCDT_SCH_BASIC_INFO.CID, KSCDT_SCH_BASIC_INFO.DECISION_STATUS, KSCDT_SCH_BASIC_INFO.EMP_CD, "
				+ " KSCDT_SCH_BASIC_INFO.JOB_ID, KSCDT_SCH_BASIC_INFO.WKP_ID, KSCDT_SCH_BASIC_INFO.CLS_CD, KSCDT_SCH_BASIC_INFO.BUSTYPE_CD, KSCDT_SCH_BASIC_INFO.NURSE_LICENSE, "
				+ " KSCDT_SCH_BASIC_INFO.WKTP_CD, KSCDT_SCH_BASIC_INFO.WKTM_CD, KSCDT_SCH_BASIC_INFO.GO_STRAIGHT_ATR, KSCDT_SCH_BASIC_INFO.BACK_STRAIGHT_ATR, "
				+ " KSCDT_SCH_BASIC_INFO.TREAT_AS_SUBSTITUTE_ATR, KSCDT_SCH_BASIC_INFO.TREAT_AS_SUBSTITUTE_DAYS"
				+ " FROM KSCDT_SCH_BASIC_INFO" 
				+ " WHERE KSCDT_SCH_BASIC_INFO.SID IN " + listEmp + " AND KSCDT_SCH_BASIC_INFO.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listSchBasicInfo = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Boolean confirmedATR = rs.getBoolean("DECISION_STATUS");
				String empCd = rs.getString("EMP_CD");
				String jobId = rs.getString("JOB_ID");
				String wkpId = rs.getString("WKP_ID");
				String clsCd = rs.getString("CLS_CD");
				String busTypeCd = rs.getString("BUSTYPE_CD");
				String nurseLicense = rs.getString("NURSE_LICENSE");
				String wktpCd = rs.getString("WKTP_CD");
				String wktmCd = rs.getString("WKTM_CD");
				Boolean goStraightAtr = rs.getBoolean("GO_STRAIGHT_ATR");
				Boolean backStraightAtr = rs.getBoolean("BACK_STRAIGHT_ATR");
				Integer treatAsSubstituteAtr = rs.getInt("TREAT_AS_SUBSTITUTE_ATR");
				Double treatAsSubstituteDays = rs.getDouble("TREAT_AS_SUBSTITUTE_DAYS");

				return new KscdtSchBasicInfo(new KscdtSchBasicInfoPK(sid, ymd), cid, confirmedATR,
						empCd, jobId, wkpId, clsCd, busTypeCd, nurseLicense, wktpCd, wktmCd,
						goStraightAtr, backStraightAtr, treatAsSubstituteAtr,
						treatAsSubstituteDays, null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
						new ArrayList<>(), new ArrayList<>());
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, KscdtSchBasicInfo> mapPairSchBasicInfo = listSchBasicInfo.stream()
				.collect(Collectors.toMap(x -> Pair.of(x.pk.sid, x.pk.ymd), x -> x));

		return mapPairSchBasicInfo;
	}

	// KSCDT_SCH_EDIT_STATE
	private Map<Pair<String, GeneralDate>, List<KscdtSchEditState>> getSchEditState(String listEmp, DatePeriod period) {

		List<KscdtSchEditState> listSchEditState = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_EDIT_STATE.SID, KSCDT_SCH_EDIT_STATE.YMD, KSCDT_SCH_EDIT_STATE.CID,"
				+ " KSCDT_SCH_EDIT_STATE.ATD_ITEM_ID, KSCDT_SCH_EDIT_STATE.EDIT_STATE" 
				+ " FROM KSCDT_SCH_EDIT_STATE"
				+ " WHERE KSCDT_SCH_EDIT_STATE.SID IN " + listEmp + " AND KSCDT_SCH_EDIT_STATE.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listSchEditState = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer atdItemId = rs.getInt("ATD_ITEM_ID");
				Integer editState = rs.getInt("EDIT_STATE");

				return new KscdtSchEditState(new KscdtSchEditStatePK(sid, ymd, atdItemId), cid, editState);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchEditState>> mapPairSchEditState = listSchEditState.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairSchEditState;
	}

	// KSCDT_SCH_ATD_LVW_TIME
	private Map<Pair<String, GeneralDate>, List<KscdtSchAtdLvwTime>> getSchAtdLvwTime(String listEmp,
			DatePeriod period) {

		List<KscdtSchAtdLvwTime> listSchAtdLvwTime = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_ATD_LVW_TIME.SID, KSCDT_SCH_ATD_LVW_TIME.YMD, KSCDT_SCH_ATD_LVW_TIME.CID,"
				+ " KSCDT_SCH_ATD_LVW_TIME.WORK_NO, "
				+ " KSCDT_SCH_ATD_LVW_TIME.ATD_CLOCK, KSCDT_SCH_ATD_LVW_TIME.ATD_HOURLY_HD_TS_START, KSCDT_SCH_ATD_LVW_TIME.ATD_HOURLY_HD_TS_END,"
				+ " KSCDT_SCH_ATD_LVW_TIME.LVW_CLOCK, KSCDT_SCH_ATD_LVW_TIME.LVW_HOURLY_HD_TS_START, KSCDT_SCH_ATD_LVW_TIME.LVW_HOURLY_HD_TS_END"
				+ " FROM KSCDT_SCH_ATD_LVW_TIME" 
				+ " WHERE KSCDT_SCH_ATD_LVW_TIME.SID IN " + listEmp + " AND KSCDT_SCH_ATD_LVW_TIME.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listSchAtdLvwTime = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer workNo = rs.getInt("WORK_NO");
				Integer atdClock = rs.getInt("ATD_CLOCK");
				Integer atdHourlyHDTSStart = rs.getInt("ATD_HOURLY_HD_TS_START");
				Integer atdHourlyHDTSEnd = rs.getInt("ATD_HOURLY_HD_TS_END");
				Integer lwkClock = rs.getInt("LVW_CLOCK");
				Integer lvwHourlyHDTSStart = rs.getInt("LVW_HOURLY_HD_TS_START");
				Integer lvwHourlyHDTSEnd = rs.getInt("LVW_HOURLY_HD_TS_END");
				return new KscdtSchAtdLvwTime(new KscdtSchAtdLvwTimePK(sid, ymd, workNo), cid, atdClock,
						atdHourlyHDTSStart, atdHourlyHDTSEnd, lwkClock, lvwHourlyHDTSStart, lvwHourlyHDTSEnd);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		
		Map<Pair<String, GeneralDate>, List<KscdtSchAtdLvwTime>> mapPairSchAtdLvwTime = listSchAtdLvwTime.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairSchAtdLvwTime;
	}

	// KSCDT_SCH_SHORTTIME_TS
	private Map<Pair<String, GeneralDate>, List<KscdtSchShortTimeTs>> getSchShortTimeTs(String listEmp,
			DatePeriod period) {

		List<KscdtSchShortTimeTs> listSchShortTimeTs = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_SHORTTIME_TS.SID, KSCDT_SCH_SHORTTIME_TS.YMD, KSCDT_SCH_SHORTTIME_TS.CID,"
				+ " KSCDT_SCH_SHORTTIME_TS.CHILD_CARE_ATR, KSCDT_SCH_SHORTTIME_TS.FRAME_NO, KSCDT_SCH_SHORTTIME_TS.SHORTTIME_TS_START, KSCDT_SCH_SHORTTIME_TS.SHORTTIME_TS_END"
				+ " FROM KSCDT_SCH_SHORTTIME_TS" 
				+ " WHERE KSCDT_SCH_SHORTTIME_TS.SID IN " + listEmp + " AND KSCDT_SCH_SHORTTIME_TS.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listSchShortTimeTs = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer childCareAtr = rs.getInt("CHILD_CARE_ATR");
				Integer frameNo = rs.getInt("FRAME_NO");
				Integer shortTimeTsStart = rs.getInt("SHORTTIME_TS_START");
				Integer shortTimeTsEnd = rs.getInt("SHORTTIME_TS_END");
				return new KscdtSchShortTimeTs(new KscdtSchShortTimeTsPK(sid, ymd, childCareAtr, frameNo), cid, shortTimeTsStart, shortTimeTsEnd);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchShortTimeTs>> mapPairSchShortTimeT = listSchShortTimeTs.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairSchShortTimeT;

	}

	// KSCDT_SCH_BREAK_TS
	private Map<Pair<String, GeneralDate>, List<KscdtSchBreakTs>> getKscdtSchBreakTs(String listEmp,
			DatePeriod period) {

		List<KscdtSchBreakTs> listSchBreakTs = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_BREAK_TS.SID, KSCDT_SCH_BREAK_TS.YMD, KSCDT_SCH_BREAK_TS.CID,"
				+ " KSCDT_SCH_BREAK_TS.FRAME_NO, KSCDT_SCH_BREAK_TS.BREAK_TS_START, KSCDT_SCH_BREAK_TS.BREAK_TS_END"
				+ " FROM KSCDT_SCH_BREAK_TS" 
				+ " WHERE KSCDT_SCH_BREAK_TS.SID IN " + listEmp + " AND KSCDT_SCH_BREAK_TS.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listSchBreakTs = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer frameNo = rs.getInt("FRAME_NO");
				Integer breakTsStart = rs.getInt("BREAK_TS_START");
				Integer breakTsEnd = rs.getInt("BREAK_TS_END");
				return new KscdtSchBreakTs(new KscdtSchBreakTsPK(sid, ymd, frameNo), cid, breakTsStart, breakTsEnd);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchBreakTs>> mapPairSchBreakTs = listSchBreakTs.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairSchBreakTs;
	}

	// KSCDT_SCH_GOING_OUT_TS
	private Map<Pair<String, GeneralDate>, List<KscdtSchGoingOutTs>> getKscdtSchGoingOutTs(String listEmp,
			DatePeriod period) {

		List<KscdtSchGoingOutTs> listSchGoingOutTs = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_GOING_OUT_TS.SID, KSCDT_SCH_GOING_OUT_TS.YMD, KSCDT_SCH_GOING_OUT_TS.CID,"
				+ " KSCDT_SCH_GOING_OUT_TS.FRAME_NO, KSCDT_SCH_GOING_OUT_TS.REASON_ATR, KSCDT_SCH_GOING_OUT_TS.GOING_OUT_CLOCK, KSCDT_SCH_GOING_OUT_TS.GOING_BACK_CLOCK"
				+ " FROM KSCDT_SCH_GOING_OUT_TS" 
				+ " WHERE KSCDT_SCH_GOING_OUT_TS.SID IN " + listEmp+ " AND KSCDT_SCH_GOING_OUT_TS.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listSchGoingOutTs = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer frameNo = rs.getInt("FRAME_NO");
				Integer reasonAtr = rs.getInt("REASON_ATR");
				Integer goingOutClock = rs.getInt("GOING_OUT_CLOCK");
				Integer goingBackClock = rs.getInt("GOING_BACK_CLOCK");
				return new KscdtSchGoingOutTs(new KscdtSchGoingOutTsPK(sid, ymd, frameNo), cid, reasonAtr, goingOutClock, goingBackClock);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchGoingOutTs>> mapPairGoingOutTs = listSchGoingOutTs.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairGoingOutTs;
	}

	@Override
	public void updateConfirmedState(WorkSchedule workSchedule) {
		Optional<KscdtSchBasicInfo> entity = this.queryProxy().find(new KscdtSchBasicInfoPK(workSchedule.getEmployeeID(), workSchedule.getYmd()), KscdtSchBasicInfo.class);
		if(entity.isPresent()){
			entity.get().confirmedATR = workSchedule.getConfirmedATR().value == 1 ? true : false;
			this.commandProxy().update(entity.get());
		}
	}
	
	@Override	
	public List<AffInfoForWorkSchedule> getAffiliationInfor(String sid, DatePeriod period) {
		List<WorkSchedule>  data = this.getListBySid(sid, period);
		List<AffInfoForWorkSchedule> result = data.stream().map(c->new AffInfoForWorkSchedule(c.getEmployeeID(), c.getYmd(), c.getAffInfo()) ).collect(Collectors.toList());
		return result;
	}
	// KSCDT_SCH_TIME
	private Map<Pair<String, GeneralDate>, KscdtSchTime> getKscdtSchTimes(String listEmp, DatePeriod period) {

		List<KscdtSchTime> listKscdtSchTime = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_TIME.SID, KSCDT_SCH_TIME.YMD, KSCDT_SCH_TIME.CID, KSCDT_SCH_TIME.COUNT, KSCDT_SCH_TIME.TOTAL_TIME, KSCDT_SCH_TIME.TOTAL_TIME_ACT,"
				+ " KSCDT_SCH_TIME.PRS_WORK_TIME, KSCDT_SCH_TIME.PRS_WORK_TIME_ACT, KSCDT_SCH_TIME.PRS_PRIME_TIME, KSCDT_SCH_TIME.PRS_MIDNITE_TIME, KSCDT_SCH_TIME.EXT_BIND_TIME_OTW,"
				+ " KSCDT_SCH_TIME.EXT_BIND_TIME_HDW, KSCDT_SCH_TIME.EXT_VARWK_OTW_TIME_LEGAL, KSCDT_SCH_TIME.EXT_FLEX_TIME, KSCDT_SCH_TIME.EXT_FLEX_TIME_PREAPP,"
				+ " KSCDT_SCH_TIME.EXT_MIDNITE_OTW_TIME, KSCDT_SCH_TIME.EXT_MIDNITE_HDW_TIME_LGHD, KSCDT_SCH_TIME.EXT_MIDNITE_HDW_TIME_ILGHD, KSCDT_SCH_TIME.EXT_MIDNITE_HDW_TIME_PUBHD,"
				+ " KSCDT_SCH_TIME.EXT_MIDNITE_TOTAL, KSCDT_SCH_TIME.EXT_MIDNITE_TOTAL_PREAPP, KSCDT_SCH_TIME.INTERVAL_ATD_CLOCK, KSCDT_SCH_TIME.INTERVAL_TIME,"
				+ " KSCDT_SCH_TIME.BRK_TOTAL_TIME, KSCDT_SCH_TIME.USE_DAYLY_HD_PAID, KSCDT_SCH_TIME.USE_HOURLY_HD_PAID, KSCDT_SCH_TIME.USE_DAYLY_HD_COM, KSCDT_SCH_TIME.USE_HOURLY_HD_COM,"
				+ " KSCDT_SCH_TIME.USE_DAYLY_HD_60H, KSCDT_SCH_TIME.USE_HOURLY_HD_60H, KSCDT_SCH_TIME.USE_DAYLY_HD_SP, KSCDT_SCH_TIME.USE_HOURLY_HD_SP, KSCDT_SCH_TIME.USE_DAYLY_HD_STK,"
				+ " KSCDT_SCH_TIME.HOURLY_HD_USETIME, KSCDT_SCH_TIME.HOURLY_HD_SHORTAGETIME, KSCDT_SCH_TIME.ABSENCE_TIME, KSCDT_SCH_TIME.VACATION_ADD_TIME, KSCDT_SCH_TIME.STAGGERED_WH_TIME,"
				+ " KSCDT_SCH_TIME.PRS_WORK_TIME_AMOUNT, KSCDT_SCH_TIME.PREMIUM_WORK_TIME_TOTAL , KSCDT_SCH_TIME.PREMIUM_AMOUNT_TOTAL, KSCDT_SCH_TIME.USE_DAILY_HD_SUB "
				+ " FROM KSCDT_SCH_TIME" 
				+ " WHERE KSCDT_SCH_TIME.SID IN " + listEmp + " AND KSCDT_SCH_TIME.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchTime = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer count = rs.getInt("COUNT");
				Integer totalTime = rs.getInt("TOTAL_TIME");
				Integer totalTimeAct = rs.getInt("TOTAL_TIME_ACT");
				Integer prsWorkTime = rs.getInt("PRS_WORK_TIME");
				Integer prsWorkTimeAct = rs.getInt("PRS_WORK_TIME_ACT");
				Integer prsPrimeTime = rs.getInt("PRS_PRIME_TIME");
				Integer prsMidniteTime = rs.getInt("PRS_MIDNITE_TIME");
				Integer extBindTimeOtw = rs.getInt("EXT_BIND_TIME_OTW");
				Integer extBindTimeHw = rs.getInt("EXT_BIND_TIME_HDW");
				Integer extVarwkOtwTimeLegal = rs.getInt("EXT_VARWK_OTW_TIME_LEGAL");
				Integer extFlexTime = rs.getInt("EXT_FLEX_TIME");
				Integer extFlexTimePreApp = rs.getInt("EXT_FLEX_TIME_PREAPP");
				Integer extMidNiteOtwTime = rs.getInt("EXT_MIDNITE_OTW_TIME");
				Integer extMidNiteHdwTimeLghd = rs.getInt("EXT_MIDNITE_HDW_TIME_LGHD");
				Integer extMidNiteHdwTimeIlghd = rs.getInt("EXT_MIDNITE_HDW_TIME_ILGHD");
				Integer extMidNiteHdwTimePubhd = rs.getInt("EXT_MIDNITE_HDW_TIME_PUBHD");
				Integer extMidNiteTotal = rs.getInt("EXT_MIDNITE_TOTAL");
				Integer extMidNiteTotalPreApp = rs.getInt("EXT_MIDNITE_TOTAL_PREAPP");
				Integer intervalAtdClock = rs.getInt("INTERVAL_ATD_CLOCK");
				Integer intervalTime = rs.getInt("INTERVAL_TIME");
				Integer brkTotalTime = rs.getInt("BRK_TOTAL_TIME");
				
				Integer hdPaidTime = rs.getInt("USE_DAYLY_HD_PAID");
				Integer hdPaidHourlyTime = rs.getInt("USE_HOURLY_HD_PAID");
				Integer hdComTime = rs.getInt("USE_DAYLY_HD_COM");
				Integer hdComHourlyTime = rs.getInt("USE_HOURLY_HD_COM");
				Integer hd60hTime = rs.getInt("USE_DAYLY_HD_60H");
				Integer hd60hHourlyTime = rs.getInt("USE_HOURLY_HD_60H");
				Integer hdspTime = rs.getInt("USE_DAYLY_HD_SP");
				Integer hdspHourlyTime = rs.getInt("USE_HOURLY_HD_SP");
				Integer hdstkTime = rs.getInt("USE_DAYLY_HD_STK");
				Integer hdHourlyTime = rs.getInt("HOURLY_HD_USETIME");
				Integer hdHourlyShortageTime = rs.getInt("HOURLY_HD_SHORTAGETIME");
				
				Integer absenceTime = rs.getInt("ABSENCE_TIME");
				Integer vacationAddTime = rs.getInt("VACATION_ADD_TIME");
				Integer staggeredWhTime = rs.getInt("STAGGERED_WH_TIME");
				Integer prsWorkTimeAmount = rs.getInt("PRS_WORK_TIME_AMOUNT");
				Integer premiumWorkTimeTotal = rs.getInt("PREMIUM_WORK_TIME_TOTAL");
				Integer premiumAmountTotal = rs.getInt("PREMIUM_AMOUNT_TOTAL");
				Integer useDailyHDSub = rs.getInt("USE_DAILY_HD_SUB");
				
				return new KscdtSchTime(new KscdtSchTimePK(sid, ymd), cid, count, totalTime, totalTimeAct, prsWorkTime,
						prsWorkTimeAct, prsPrimeTime, prsMidniteTime, extBindTimeOtw, extBindTimeHw,
						extVarwkOtwTimeLegal, extFlexTime, extFlexTimePreApp, extMidNiteOtwTime, extMidNiteHdwTimeLghd,
						extMidNiteHdwTimeIlghd, extMidNiteHdwTimePubhd, extMidNiteTotal, extMidNiteTotalPreApp,
						intervalAtdClock, intervalTime, brkTotalTime, hdPaidTime, hdPaidHourlyTime, hdComTime,
						hdComHourlyTime, hd60hTime, hd60hHourlyTime, hdspTime, hdspHourlyTime, hdstkTime, hdHourlyTime,
						hdHourlyShortageTime, absenceTime, vacationAddTime, staggeredWhTime,
						new ArrayList<>(),new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 
						new ArrayList<>(), new ArrayList<>(),new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
						prsWorkTimeAmount, premiumWorkTimeTotal, premiumAmountTotal, useDailyHDSub);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, KscdtSchTime> mapPairSchTime = listKscdtSchTime.stream()
				.collect(Collectors.toMap(x -> Pair.of(x.pk.sid, x.pk.ymd), x -> x));

		return mapPairSchTime;
	}

	// KSCDT_SCH_OVERTIME_WORK
	private Map<Pair<String, GeneralDate>, List<KscdtSchOvertimeWork>> getOvertimeWorks(String listEmp,
			DatePeriod period) {

		List<KscdtSchOvertimeWork> listKscdtSchOvertimeWork = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_OVERTIME_WORK.SID, KSCDT_SCH_OVERTIME_WORK.YMD, KSCDT_SCH_OVERTIME_WORK.CID, KSCDT_SCH_OVERTIME_WORK.FRAME_NO,"
				+ " KSCDT_SCH_OVERTIME_WORK.OVERTIME_WORK_TIME, KSCDT_SCH_OVERTIME_WORK.OVERTIME_WORK_TIME_TRANS, KSCDT_SCH_OVERTIME_WORK.OVERTIME_WORK_TIME_PREAPP"
				+ " FROM KSCDT_SCH_OVERTIME_WORK" 
				+ " WHERE KSCDT_SCH_OVERTIME_WORK.SID IN " + listEmp + " AND KSCDT_SCH_OVERTIME_WORK.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchOvertimeWork = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer frameNo = rs.getInt("FRAME_NO");
				Integer overtimeWorkTime = rs.getInt("OVERTIME_WORK_TIME");
				Integer overtimeWorkTimeTrans = rs.getInt("OVERTIME_WORK_TIME_TRANS");
				Integer overtimeWorkTimePreApp = rs.getInt("OVERTIME_WORK_TIME_PREAPP");
				return new KscdtSchOvertimeWork(new KscdtSchOvertimeWorkPK(sid, ymd, frameNo), cid, overtimeWorkTime,
						overtimeWorkTimeTrans, overtimeWorkTimePreApp);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchOvertimeWork>> mapPairOvertimeWork = listKscdtSchOvertimeWork
				.stream().collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairOvertimeWork;
	}

	// KSCDT_SCH_HOLIDAY_WORK
	private Map<Pair<String, GeneralDate>, List<KscdtSchHolidayWork>> getHolidayWorks(String listEmp,
			DatePeriod period) {

		List<KscdtSchHolidayWork> listKscdtSchHolidayWork = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_HOLIDAY_WORK.SID, KSCDT_SCH_HOLIDAY_WORK.YMD, KSCDT_SCH_HOLIDAY_WORK.CID, KSCDT_SCH_HOLIDAY_WORK.FRAME_NO,"
				+ " KSCDT_SCH_HOLIDAY_WORK.HOLIDAY_WORK_TS_START, KSCDT_SCH_HOLIDAY_WORK.HOLIDAY_WORK_TS_END, KSCDT_SCH_HOLIDAY_WORK.HOLIDAY_WORK_TIME,"
				+ " KSCDT_SCH_HOLIDAY_WORK.HOLIDAY_WORK_TIME_TRANS, KSCDT_SCH_HOLIDAY_WORK.HOLIDAY_WORK_TIME_PREAPP"
				+ " FROM KSCDT_SCH_HOLIDAY_WORK" 
				+ " WHERE KSCDT_SCH_HOLIDAY_WORK.SID IN " + listEmp + " AND KSCDT_SCH_HOLIDAY_WORK.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchHolidayWork = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer frameNo = rs.getInt("FRAME_NO");
				Integer holidayWorkTsStart = rs.getInt("HOLIDAY_WORK_TS_START");
				Integer holidayWorkTsEnd = rs.getInt("HOLIDAY_WORK_TS_END");
				Integer holidayWorkTime = rs.getInt("HOLIDAY_WORK_TIME");
				Integer holidayWorkTimeTrans = rs.getInt("HOLIDAY_WORK_TIME_TRANS");
				Integer holidayWorkTimePreApp = rs.getInt("HOLIDAY_WORK_TIME_PREAPP");

				return new KscdtSchHolidayWork(new KscdtSchHolidayWorkPK(sid, ymd, frameNo), cid, holidayWorkTsStart,
						holidayWorkTsEnd, holidayWorkTime, holidayWorkTimeTrans, holidayWorkTimePreApp);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchHolidayWork>> mapPairHolidayWork = listKscdtSchHolidayWork.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairHolidayWork;
	}

	// KSCDT_SCH_BONUSPAY
	private Map<Pair<String, GeneralDate>, List<KscdtSchBonusPay>> getBonusPays(String listEmp, DatePeriod period) {

		List<KscdtSchBonusPay> listKscdtSchBonusPay = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_BONUSPAY.SID, KSCDT_SCH_BONUSPAY.YMD,KSCDT_SCH_BONUSPAY.CID,"
				+ " KSCDT_SCH_BONUSPAY.BONUSPAY_TYPE, KSCDT_SCH_BONUSPAY.FRAME_NO,"
				+ " KSCDT_SCH_BONUSPAY.PREMIUM_TIME, KSCDT_SCH_BONUSPAY.PREMIUM_TIME_WITHIN, KSCDT_SCH_BONUSPAY.PREMIUM_TIME_WITHOUT"
				+ " FROM KSCDT_SCH_BONUSPAY" 
				+ " WHERE KSCDT_SCH_BONUSPAY.SID IN " + listEmp + " AND KSCDT_SCH_BONUSPAY.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchBonusPay = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer frameNo = rs.getInt("FRAME_NO");
				Integer bonuspayType = rs.getInt("BONUSPAY_TYPE");
				Integer premiumTime = rs.getInt("PREMIUM_TIME");
				Integer premiumTimeWithIn = rs.getInt("PREMIUM_TIME_WITHIN");
				Integer premiumTimeWithOut = rs.getInt("PREMIUM_TIME_WITHOUT");

				return new KscdtSchBonusPay(new KscdtSchBonusPayPK(sid, ymd, bonuspayType, frameNo), cid, premiumTime,
						premiumTimeWithIn, premiumTimeWithOut);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchBonusPay>> mapPairBonusPay = listKscdtSchBonusPay.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairBonusPay;
	}

	// KSCDT_SCH_PREMIUM
	private Map<Pair<String, GeneralDate>, List<KscdtSchPremium>> getPremiums(String listEmp, DatePeriod period) {

		List<KscdtSchPremium> listKscdtSchPremium = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_PREMIUM.SID, KSCDT_SCH_PREMIUM.YMD, KSCDT_SCH_PREMIUM.CID,  "
				+ " KSCDT_SCH_PREMIUM.FRAME_NO, KSCDT_SCH_PREMIUM.PREMIUM_TIME, KSCDT_SCH_PREMIUM.PREMIUM_TIME_AMOUNT , KSCDT_SCH_PREMIUM.PREMIUM_TIME_UNIT_COST" 
				+ " FROM KSCDT_SCH_PREMIUM"
				+ " WHERE KSCDT_SCH_PREMIUM.SID IN " + listEmp + " AND KSCDT_SCH_PREMIUM.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchPremium = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer frameNo = rs.getInt("FRAME_NO");
				Integer premiumTime = rs.getInt("PREMIUM_TIME");
				Integer premiumTimeAmount = rs.getInt("PREMIUM_TIME_AMOUNT");
				Integer premiumTimeUnitCost = rs.getInt("PREMIUM_TIME_UNIT_COST");

				return new KscdtSchPremium(new KscdtSchPremiumPK(sid, ymd, frameNo), cid, premiumTime, premiumTimeAmount, premiumTimeUnitCost);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchPremium>> mapPairPremium = listKscdtSchPremium.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairPremium;
	}

	// KSCDT_SCH_SHORTTIME
	private Map<Pair<String, GeneralDate>, List<KscdtSchShortTime>> getShortTimes(String listEmp, DatePeriod period) {

		List<KscdtSchShortTime> listKscdtSchShortTime = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_SHORTTIME.SID, KSCDT_SCH_SHORTTIME.YMD, KSCDT_SCH_SHORTTIME.CID,  "
				+ " KSCDT_SCH_SHORTTIME.CHILD_CARE_ATR, "
				+ " KSCDT_SCH_SHORTTIME.COUNT, KSCDT_SCH_SHORTTIME.TOTAL_TIME, KSCDT_SCH_SHORTTIME.TOTAL_TIME_WITHIN, KSCDT_SCH_SHORTTIME.TOTAL_TIME_WITHOUT "
				+ " FROM KSCDT_SCH_SHORTTIME" 
				+ " WHERE KSCDT_SCH_SHORTTIME.SID IN " + listEmp + " AND KSCDT_SCH_SHORTTIME.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchShortTime = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer childCareAtr = rs.getInt("CHILD_CARE_ATR");
				Integer count = rs.getInt("COUNT");
				Integer totalTime = rs.getInt("TOTAL_TIME");
				Integer totalTimeWithIn = rs.getInt("TOTAL_TIME_WITHIN");
				Integer totalTimeWithOut = rs.getInt("TOTAL_TIME_WITHOUT");

				return new KscdtSchShortTime(new KscdtSchShortTimePK(sid, ymd, childCareAtr), cid, count, totalTime,
						totalTimeWithIn, totalTimeWithOut);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchShortTime>> mapPairShortTime = listKscdtSchShortTime.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairShortTime;
	}

	// KSCDT_SCH_COME_LATE
	private Map<Pair<String, GeneralDate>, List<KscdtSchComeLate>> getKscdtSchComeLates(String listEmp,
			DatePeriod period) {

		List<KscdtSchComeLate> listKscdtSchComeLate = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_COME_LATE.SID, KSCDT_SCH_COME_LATE.YMD, KSCDT_SCH_COME_LATE.CID,  "
				+ " KSCDT_SCH_COME_LATE.WORK_NO, "
				+ " KSCDT_SCH_COME_LATE.USE_HOURLY_HD_PAID, KSCDT_SCH_COME_LATE.USE_HOURLY_HD_COM, "
				+ " KSCDT_SCH_COME_LATE.USE_HOURLY_HD_60H, KSCDT_SCH_COME_LATE.USE_HOURLY_HD_SP_NO, "
				+ " KSCDT_SCH_COME_LATE.USE_HOURLY_HD_SP_TIME, KSCDT_SCH_COME_LATE.USE_HOURLY_HD_CHILDCARE, "
				+ " KSCDT_SCH_COME_LATE.USE_HOURLY_HD_NURSECARE" 
				+ " FROM KSCDT_SCH_COME_LATE"
				+ " WHERE KSCDT_SCH_COME_LATE.SID IN " + listEmp + " AND KSCDT_SCH_COME_LATE.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchComeLate = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer workNo = rs.getInt("WORK_NO");
				Integer useHourlyHdPaid = rs.getInt("USE_HOURLY_HD_PAID");
				Integer useHourlyHdCom = rs.getInt("USE_HOURLY_HD_COM");
				Integer useHourlyHd60h = rs.getInt("USE_HOURLY_HD_60H");
				Integer useHourlyHdSpNO = rs.getInt("USE_HOURLY_HD_SP_NO");
				Integer useHourlyHdSpTime = rs.getInt("USE_HOURLY_HD_SP_TIME");
				Integer useHourlyHdChildCare = rs.getInt("USE_HOURLY_HD_CHILDCARE");
				Integer useHourlyHdNurseCare = rs.getInt("USE_HOURLY_HD_NURSECARE");

				return new KscdtSchComeLate(new KscdtSchComeLatePK(sid, ymd, workNo), cid, useHourlyHdPaid,
						useHourlyHdCom, useHourlyHd60h, useHourlyHdSpNO, useHourlyHdSpTime, useHourlyHdChildCare,
						useHourlyHdNurseCare);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchComeLate>> mapPairComeLate = listKscdtSchComeLate.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairComeLate;
	}

	// KSCDT_SCH_GOING_OUT
	private Map<Pair<String, GeneralDate>, List<KscdtSchGoingOut>> getKscdtSchGoingOuts(String listEmp,
			DatePeriod period) {

		List<KscdtSchGoingOut> listKscdtSchGoingOut = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_GOING_OUT.SID, KSCDT_SCH_GOING_OUT.YMD, KSCDT_SCH_GOING_OUT.CID,  "
				+ " KSCDT_SCH_GOING_OUT.REASON_ATR,"
				+ " KSCDT_SCH_GOING_OUT.USE_HOURLY_HD_PAID, KSCDT_SCH_GOING_OUT.USE_HOURLY_HD_COM, "
				+ " KSCDT_SCH_GOING_OUT.USE_HOURLY_HD_60H, KSCDT_SCH_GOING_OUT.USE_HOURLY_HD_SP_NO, "
				+ " KSCDT_SCH_GOING_OUT.USE_HOURLY_HD_SP_TIME, KSCDT_SCH_GOING_OUT.USE_HOURLY_HD_CHILDCARE, "
				+ " KSCDT_SCH_GOING_OUT.USE_HOURLY_HD_NURSECARE" 
				+ " FROM KSCDT_SCH_GOING_OUT"
				+ " WHERE KSCDT_SCH_GOING_OUT.SID IN " + listEmp + " AND KSCDT_SCH_GOING_OUT.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchGoingOut = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer reasonAtr = rs.getInt("REASON_ATR");
				Integer useHourlyHdPaid = rs.getInt("USE_HOURLY_HD_PAID");
				Integer useHourlyHdCom = rs.getInt("USE_HOURLY_HD_COM");
				Integer useHourlyHd60h = rs.getInt("USE_HOURLY_HD_60H");
				Integer useHourlyHdSpNO = rs.getInt("USE_HOURLY_HD_SP_NO");
				Integer useHourlyHdSpTime = rs.getInt("USE_HOURLY_HD_SP_TIME");
				Integer useHourlyHdChildCare = rs.getInt("USE_HOURLY_HD_CHILDCARE");
				Integer useHourlyHdNurseCare = rs.getInt("USE_HOURLY_HD_NURSECARE");

				return new KscdtSchGoingOut(new KscdtSchGoingOutPK(sid, ymd, reasonAtr), cid, useHourlyHdPaid,
						useHourlyHdCom, useHourlyHd60h, useHourlyHdSpNO, useHourlyHdSpTime, useHourlyHdChildCare,
						useHourlyHdNurseCare);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchGoingOut>> mapPairGoingOut = listKscdtSchGoingOut.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairGoingOut;
	}

	// KSCDT_SCH_LEAVE_EARLY
	private Map<Pair<String, GeneralDate>, List<KscdtSchLeaveEarly>> getKscdtSchLeaveEarlys(String listEmp,
			DatePeriod period) {

		List<KscdtSchLeaveEarly> listKscdtSchLeaveEarly = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_LEAVE_EARLY.SID, KSCDT_SCH_LEAVE_EARLY.YMD, KSCDT_SCH_LEAVE_EARLY.CID,  "
				+ " KSCDT_SCH_LEAVE_EARLY.WORK_NO, "
				+ " KSCDT_SCH_LEAVE_EARLY.USE_HOURLY_HD_PAID, KSCDT_SCH_LEAVE_EARLY.USE_HOURLY_HD_COM, "
				+ " KSCDT_SCH_LEAVE_EARLY.USE_HOURLY_HD_60H, KSCDT_SCH_LEAVE_EARLY.USE_HOURLY_HD_SP_NO, "
				+ " KSCDT_SCH_LEAVE_EARLY.USE_HOURLY_HD_SP_TIME, KSCDT_SCH_LEAVE_EARLY.USE_HOURLY_HD_CHILDCARE, "
				+ " KSCDT_SCH_LEAVE_EARLY.USE_HOURLY_HD_NURSECARE" 
				+ " FROM KSCDT_SCH_LEAVE_EARLY"
				+ " WHERE KSCDT_SCH_LEAVE_EARLY.SID IN " + listEmp + " AND KSCDT_SCH_LEAVE_EARLY.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchLeaveEarly = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer workNo = rs.getInt("WORK_NO");
				Integer useHourlyHdPaid = rs.getInt("USE_HOURLY_HD_PAID");
				Integer useHourlyHdCom = rs.getInt("USE_HOURLY_HD_COM");
				Integer useHourlyHd60h = rs.getInt("USE_HOURLY_HD_60H");
				Integer useHourlyHdSpNO = rs.getInt("USE_HOURLY_HD_SP_NO");
				Integer useHourlyHdSpTime = rs.getInt("USE_HOURLY_HD_SP_TIME");
				Integer useHourlyHdChildCare = rs.getInt("USE_HOURLY_HD_CHILDCARE");
				Integer useHourlyHdNurseCare = rs.getInt("USE_HOURLY_HD_NURSECARE");

				return new KscdtSchLeaveEarly(new KscdtSchLeaveEarlyPK(sid, ymd, workNo), cid, useHourlyHdPaid,
						useHourlyHdCom, useHourlyHd60h, useHourlyHdSpNO, useHourlyHdSpTime, useHourlyHdChildCare,
						useHourlyHdNurseCare);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchLeaveEarly>> mapPairLeaveEarly = listKscdtSchLeaveEarly.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairLeaveEarly;
	}
	
	// KSCDT_SCH_TASK
	private Map<Pair<String, GeneralDate>, List<KscdtSchTask>> getKscdtSchTasks(String listEmp, DatePeriod period) {

		List<KscdtSchTask> listKscdtSchTask = new ArrayList<>();

		String QUERY = "SELECT KSCDT_SCH_TASK.SID, KSCDT_SCH_TASK.YMD, KSCDT_SCH_TASK.CID,  "
				+ " KSCDT_SCH_TASK.SERIAL_NO, KSCDT_SCH_TASK.TASK_CODE, KSCDT_SCH_TASK.START_CLOCK, "
				+ " KSCDT_SCH_TASK.END_CLOCK" 
				+ " FROM KSCDT_SCH_TASK" + " WHERE KSCDT_SCH_TASK.SID IN " + listEmp
				+ " AND KSCDT_SCH_TASK.YMD BETWEEN " + "'" + period.start() + "' AND '" + period.end() + "' ";

		try (PreparedStatement stmt = this.connection().prepareStatement(QUERY)) {
			listKscdtSchTask = new NtsResultSet(stmt.executeQuery()).getList(rs -> {
				String sid = rs.getString("SID");
				GeneralDate ymd = GeneralDate.fromString(rs.getString("YMD"), "yyyy-MM-dd");
				String cid = rs.getString("CID");
				Integer serialNo = rs.getInt("SERIAL_NO");
				String taskCode = rs.getString("TASK_CODE");
				Integer startClock = rs.getInt("START_CLOCK");
				Integer endClock = rs.getInt("END_CLOCK");

				return new KscdtSchTask(new KscdtSchTaskPK(sid, ymd, serialNo), taskCode, startClock, endClock, cid);
			});
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		Map<Pair<String, GeneralDate>, List<KscdtSchTask>> mapPairKscdtSchTask = listKscdtSchTask.stream()
				.collect(Collectors.groupingBy(x -> Pair.of(x.pk.sid, x.pk.ymd)));

		return mapPairKscdtSchTask;
	}

	private <V> List<V> removeInsertData(List<V> oldDatas, List<V> newDatas, BiFunction<V, V, Boolean> keyCheck) {
		oldDatas.forEach(x -> {
			if(!newDatas.stream().anyMatch(y -> keyCheck.apply(x, y))) {
				this.commandProxy().remove(x);
			}
		});
		return newDatas;
	}
}
