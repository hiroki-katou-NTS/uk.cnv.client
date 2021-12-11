package nts.uk.ctx.at.schedule.infra.repository.workschedules;

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

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.schedule.schedulemaster.requestperiodchange.AffInfoForWorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ConfirmedATR;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.EmployeeAndYmd;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchAtdLvwTime;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBasicInfo;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBasicInfoPK;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBonusPay;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchBreakTs;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchComeLate;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchEditState;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchGoingOut;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchGoingOutTs;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchLeaveEarly;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchPremium;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchShortTime;
import nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.KscdtSchShortTimeTs;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaWorkScheduleRepository extends JpaRepository implements WorkScheduleRepository {

	private static final String SELECT_BY_KEY = "SELECT c FROM KscdtSchBasicInfo c WHERE c.pk.sid = :employeeID AND c.pk.ymd = :ymd";

	private static final String SELECT_BY_LIST = "SELECT c FROM KscdtSchBasicInfo c WHERE c.pk.sid IN :sids AND (c.pk.ymd >= :startDate AND c.pk.ymd <= :endDate) ";

	private static final String SELECT_CHECK_UPDATE = "SELECT count (c) FROM KscdtSchBasicInfo c WHERE c.pk.sid = :employeeID AND c.pk.ymd = :ymd";

	private static final String WHERE_PK = "WHERE a.pk.sid = :sid AND a.pk.ymd >= :ymdStart AND a.pk.ymd <= :ymdEnd";

	private static final String DELETE_BY_LIST_DATE = "WHERE a.pk.sid = :sid AND a.pk.ymd IN :ymds";
	
	private static final String SELECT_CONFIRMATR = "SELECT DECISION_STATUS FROM KSCDT_SCH_BASIC_INFO WHERE SID = @employeeID AND YMD = @ymd";


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
	public List<WorkSchedule> getList(List<String> sids, DatePeriod period) {
		if (sids.isEmpty())
			return new ArrayList<>();

		List<WorkSchedule> result = this.queryProxy().query(SELECT_BY_LIST, KscdtSchBasicInfo.class)
				.setParameter("sids", sids).setParameter("startDate", period.start())
				.setParameter("endDate", period.end()).getList(c -> c.toDomain(c.pk.sid, c.pk.ymd));
		return result;
	}

	@Override
	public List<WorkSchedule> getListBySid(String sid, DatePeriod period) {

		List<WorkSchedule> result = this.queryProxy()
				.query("SELECT a FROM KscdtSchBasicInfo a " + WHERE_PK, KscdtSchBasicInfo.class)
				.setParameter("sid", sid)
				.setParameter("ymdStart", period.start())
				.setParameter("ymdEnd", period.end())
				.getList(c -> c.toDomain(c.pk.sid, c.pk.ymd));

		return result;
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

//			if (!oldData.get().kscdtSchTime.kscdtSchTask.isEmpty()) {
//				// get list insert and update data exist
//				List<KscdtSchTask> listInsert = new ArrayList<>();
//				for (KscdtSchTask task : newData.kscdtSchTime.kscdtSchTask) {
//					List<KscdtSchTask> checkLst = new ArrayList<>();
//					oldData.get().kscdtSchTime.kscdtSchTask.forEach(x -> {
//						if(task.pk.sid.equals(x.pk.sid)
//								&& task.pk.ymd.equals(x.pk.ymd)
//								&& task.pk.serialNo == x.pk.serialNo) {
//							x.taskCode = task.taskCode;
//							x.startClock = task.startClock;
//							x.endClock = task.endClock;
//							checkLst.add(x);
//						}
//					});
//					if(checkLst.isEmpty()) {
//						listInsert.add(task);
//					}
//				}

//				List<KscdtSchTask> listRemove = new ArrayList<>();
//				for (KscdtSchTask taskOld : oldData.get().kscdtSchTime.kscdtSchTask) {
//					boolean checkExist = false;
//					for (KscdtSchTask task : newData.kscdtSchTime.kscdtSchTask) {
//						if(task.pk.serialNo == taskOld.pk.serialNo
//								&& task.pk.sid.equals(taskOld.pk.sid)
//								&& task.pk.ymd.equals(taskOld.pk.ymd)
//								) {
//							checkExist = true;
//							break;
//						}
//					}
//					if(!checkExist) {
//						listRemove.add(taskOld);
//					}
//				}
//
//				//remove
//				String delete = "delete from KscdtSchTask o " + " where o.pk.sid = :sid "
//						+ " and o.pk.ymd = :ymd " + " and o.pk.serialNo = :serialNo";
//				for(KscdtSchTask sle : listRemove) {
//					this.getEntityManager().createQuery(delete).setParameter("sid", sle.pk.sid)
//					.setParameter("ymd", sle.pk.ymd)
//					.setParameter("serialNo", sle.pk.serialNo).executeUpdate();
//				}
//				//add
//				for(KscdtSchTask sle : listInsert) {
//					this.commandProxy().insert(sle);
//				}
//
//			} else {
//				oldData.get().kscdtSchTime.kscdtSchTask = newData.kscdtSchTime.kscdtSchTask;
//			}
//

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
				String delete = "delete from KscdtSchEditState o " + " where o.pk.sid = :sid "
						+ " and o.pk.ymd = :ymd " + " and o.pk.atdItemId = :atdItemId";
				for (KscdtSchEditState sle : listRemove) {
					this.getEntityManager().createQuery(delete).setParameter("sid", sle.pk.sid)
							.setParameter("ymd", sle.pk.ymd).setParameter("atdItemId", sle.pk.atdItemId).executeUpdate();
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

//	private static final String SELECT_MAX = "SELECT MAX(c.startDate) FROM KscdtSchBasicInfo c WHERE c.pk.sid IN :employeeIDs";
//	@Override
//	public Optional<GeneralDate> getMaxDate(List<String> employeeIDs, GeneralDate ymd) {
//		GeneralDate date = this.queryProxy().query(SELECT_MAX, GeneralDate.class)
//				.setParameter("employeeIDs", employeeIDs)
//				.getSingleOrNull();
//		return Optional.ofNullable(date);
//	}


	@Override
	public List<AffInfoForWorkSchedule> getAffiliationInfor(String sid, DatePeriod period) {
		List<WorkSchedule>  data = this.getListBySid(sid, period);
		List<AffInfoForWorkSchedule> result = data.stream().map(c->new AffInfoForWorkSchedule(c.getEmployeeID(), c.getYmd(), c.getAffInfo()) ).collect(Collectors.toList());
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
	
	private <V> List<V> removeInsertData(List<V> oldDatas, List<V> newDatas, BiFunction<V, V, Boolean> keyCheck) {
		oldDatas.forEach(x -> {
			if(!newDatas.stream().anyMatch(y -> keyCheck.apply(x, y))) {
				this.commandProxy().remove(x);
			}
		});
		return newDatas;
	}
}
