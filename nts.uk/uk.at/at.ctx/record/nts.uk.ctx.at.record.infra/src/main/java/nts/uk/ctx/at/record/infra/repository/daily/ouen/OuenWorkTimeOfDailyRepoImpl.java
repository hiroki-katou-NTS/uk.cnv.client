package nts.uk.ctx.at.record.infra.repository.daily.ouen;

import java.sql.Date;
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
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.record.infra.entity.daily.ouen.KrcdtDayOuenTime;
import nts.uk.ctx.at.record.infra.entity.daily.ouen.KrcdtDayOuenTimePK;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenAttendanceTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenMovementTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet.FullTimeNightShiftAttr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;

@Stateless
public class OuenWorkTimeOfDailyRepoImpl extends JpaRepository implements OuenWorkTimeOfDailyRepo {

	@Override
	public Optional<OuenWorkTimeOfDaily> find(String empId, GeneralDate ymd) {
		List<KrcdtDayOuenTime> entitis = queryProxy().query("SELECT o FROM KrcdtDayOuenTime o WHERE o.pk.sid = :sid AND o.pk.ymd = :ymd", 
																KrcdtDayOuenTime.class)
				.setParameter("sid", empId).setParameter("ymd", ymd)
				.getList();
		if(entitis.isEmpty())
			return Optional.empty();
		
		OuenWorkTimeOfDaily rs = toDomain(entitis);
		
		return Optional.of(rs);
	}

	@Override
	public List<OuenWorkTimeOfDaily> find(List<String> employeeIds, DatePeriod datePeriod) {
		List<OuenWorkTimeOfDaily> ouens = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, empIds -> {
			ouens.addAll(internalQuery(datePeriod, empIds));
		});
		return ouens;
	}

	@Override
	public void update(List<OuenWorkTimeOfDaily> domain) {
		domain.stream().map(c -> KrcdtDayOuenTime.convert(c)).forEach(lstE -> {
			lstE.forEach(e -> {
				this.queryProxy().find(e.pk, KrcdtDayOuenTime.class).ifPresent(entity -> {
					entity.update(e);
					commandProxy().update(entity);
				});
			});

		});
	}
	
	@SneakyThrows
	private List<OuenWorkTimeOfDaily> internalQuery(DatePeriod datePeriod, List<String> subList) {
		String subIn = NtsStatement.In.createParamsString(subList);

		Map<String, Map<GeneralDate, List<OuenWorkTimeOfDailyAttendance>>> scheTimes = new HashMap<>();
		try (val stmt = this.connection().prepareStatement("SELECT * FROM KRCDT_DAY_TIME_SUP WHERE YMD >= ? AND YMD <= ? AND SID IN (" + subIn + ")")){
			stmt.setDate(1, Date.valueOf(datePeriod.start().localDate()));
			stmt.setDate(2, Date.valueOf(datePeriod.end().localDate()));
			for (int i = 0; i < subList.size(); i++) {
				stmt.setString(i + 3, subList.get(i));
			}
			new NtsResultSet(stmt.executeQuery()).getList(c -> {
				String sid = c.getString("SID");
				GeneralDate ymd = c.getGeneralDate("YMD");
				if(!scheTimes.containsKey(sid)){
					scheTimes.put(sid, new HashMap<>());
				}
				if(!scheTimes.get(sid).containsKey(ymd)) {
					scheTimes.get(sid).put(ymd, new ArrayList<>());
				}
				getCurrent(scheTimes, sid, ymd).add(toDomain(c));
				return null;
			});
			return scheTimes.keySet().stream()
					.map(emp -> scheTimes.get(emp).entrySet().stream()
								.map(set -> OuenWorkTimeOfDaily.create(emp, set.getKey(), set.getValue()))
								.collect(Collectors.toList()))
					.flatMap(List::stream)
					.collect(Collectors.toList());
		}
	}
	
	private <T> List<T> getCurrent(Map<String, Map<GeneralDate, List<T>>> scheTimes,
			String sid, GeneralDate ymd) {
		if(scheTimes.containsKey(sid)){
			if(scheTimes.get(sid).containsKey(ymd)){
				return scheTimes.get(sid).get(ymd);
			}
		}
		return new ArrayList<>();
	}

	private OuenWorkTimeOfDailyAttendance toDomain(NtsResultRecord r) {
		
		List<MedicalCareTimeEachTimeSheet> medicalTimes = new ArrayList<>();
		medicalTimes.add(MedicalCareTimeEachTimeSheet.create(
				FullTimeNightShiftAttr.DAY_SHIFT,
				new AttendanceTime(r.getInt("NORMAL_WORK_TIME")), 
				new AttendanceTime(r.getInt("NORMAL_BREAK_TIME")), 
				new AttendanceTime(r.getInt("NORMAL_DEDUCTION_TIME"))));
		medicalTimes.add(MedicalCareTimeEachTimeSheet.create(
				FullTimeNightShiftAttr.NIGHT_SHIFT,
				new AttendanceTime(r.getInt("NIGHT_WORK_TIME")), 
				new AttendanceTime(r.getInt("NIGHT_BREAK_TIME")), 
				new AttendanceTime(r.getInt("NIGHT_DEDUCTION_TIME"))));
		
		return OuenWorkTimeOfDailyAttendance.create(new SupportFrameNo(r.getInt("SUP_NO")), 
				OuenAttendanceTimeEachTimeSheet.create(
						new AttendanceTime(r.getInt("TOTAL_TIME")), 
						new AttendanceTime(r.getInt("BREAK_TIME")), 
						new AttendanceTime(r.getInt("WITHIN_TIME")),
						new AttendanceAmountDaily(r.getInt("WITHIN_AMOUNT")),
						medicalTimes, 
						new PremiumTimeOfDailyPerformance(
								premiumTime(r.getInt("PREMIUM_TIME1"), r.getInt("PREMIUM_TIME2"), r.getInt("PREMIUM_TIME3"), r.getInt("PREMIUM_TIME4"), r.getInt("PREMIUM_TIME5"), 
										r.getInt("PREMIUM_TIME6"), r.getInt("PREMIUM_TIME7"), r.getInt("PREMIUM_TIME8"), r.getInt("PREMIUM_TIME9"), r.getInt("PREMIUM_TIME10"),
										r.getInt("PREMIUM_AMOUNT1"), r.getInt("PREMIUM_AMOUNT2"), r.getInt("PREMIUM_AMOUNT3"), r.getInt("PREMIUM_AMOUNT4"), r.getInt("PREMIUM_AMOUNT5"),
										r.getInt("PREMIUM_AMOUNT6"), r.getInt("PREMIUM_AMOUNT7"), r.getInt("PREMIUM_AMOUNT8"), r.getInt("PREMIUM_AMOUNT9"), r.getInt("PREMIUM_AMOUNT10")),
								new AttendanceAmountDaily(r.getInt("PREMIUM_AMOUNT_TOTAL")),
								new AttendanceTime(r.getInt("PREMIUM_TIME_TOTAL")))),
				OuenMovementTimeEachTimeSheet.create(
						new AttendanceTime(r.getInt("MOVE_TOTAL_TIME")), 
						new AttendanceTime(r.getInt("MOVE_BREAK_TIME")), 
						new AttendanceTime(r.getInt("MOVE_WITHIN_TIME")), 
						new PremiumTimeOfDailyPerformance(
							premiumTime(r.getInt("MOVE_PREMIUM_TIME1"), r.getInt("MOVE_PREMIUM_TIME2"), r.getInt("MOVE_PREMIUM_TIME3"), r.getInt("MOVE_PREMIUM_TIME4"), r.getInt("MOVE_PREMIUM_TIME5"), 
									r.getInt("MOVE_PREMIUM_TIME6"), r.getInt("MOVE_PREMIUM_TIME7"), r.getInt("MOVE_PREMIUM_TIME8"), r.getInt("MOVE_PREMIUM_TIME9"), r.getInt("MOVE_PREMIUM_TIME10"),
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
							AttendanceAmountDaily.ZERO,
							AttendanceTime.ZERO)),
				new AttendanceAmountDaily(r.getInt("AMOUNT")));
	}

	@Override
	public void update(OuenWorkTimeOfDaily domain) {
		KrcdtDayOuenTime.convert(domain).forEach(e -> {
			commandProxy().update(e);
		});
	}


	@Override
	public void insert(List<OuenWorkTimeOfDaily> domain) {
		domain.stream().map(c -> KrcdtDayOuenTime.convert(c)).forEach(lstE -> {
			commandProxy().insertAll(lstE);
		});
	}

	@Override
	public void insert(OuenWorkTimeOfDaily domain) {
		KrcdtDayOuenTime.convert(domain).forEach(e -> {
			commandProxy().insert(e);
		});
	}

	@Override
	public void delete(List<OuenWorkTimeOfDaily> domain) {
		domain.stream().map(c -> KrcdtDayOuenTime.convert(c)).forEach(lstE -> {
			lstE.forEach(entity -> {
				this.queryProxy().find(entity.pk, KrcdtDayOuenTime.class).ifPresent(e -> {
					this.commandProxy().remove(e);
				});
			});
		});
	}

	@Override
	public void delete(OuenWorkTimeOfDaily domain) {
		KrcdtDayOuenTime.convert(domain).forEach(e -> {
			commandProxy().remove(e);
		});
	}
	
	public OuenWorkTimeOfDaily toDomain(List<KrcdtDayOuenTime> es) {
		
		if (es.isEmpty()) {
			return null;
		}
		List<OuenWorkTimeOfDailyAttendance> ouenTimes = es.stream().map(ots -> {
			
			List<MedicalCareTimeEachTimeSheet> medicalTimes = new ArrayList<>();
			
			medicalTimes.add(MedicalCareTimeEachTimeSheet.create(
					FullTimeNightShiftAttr.DAY_SHIFT,
					new AttendanceTime(ots.normalWorkTime), 
					new AttendanceTime(ots.normalBreakTime),
					new AttendanceTime(ots.normalDeductionTime)));
			medicalTimes.add(MedicalCareTimeEachTimeSheet.create(
					FullTimeNightShiftAttr.NIGHT_SHIFT,
					new AttendanceTime(ots.nightWorkTime), 
					new AttendanceTime(ots.nightBreakTime),
					new AttendanceTime(ots.nightDeductionTime)));
			
			return OuenWorkTimeOfDailyAttendance.create(SupportFrameNo.of(ots.pk.ouenNo), 
							OuenAttendanceTimeEachTimeSheet.create(
									new AttendanceTime(ots.totalTime), 
									new AttendanceTime(ots.breakTime), 
									new AttendanceTime(ots.withinTime),
									new AttendanceAmountDaily(ots.withinAmount),
									medicalTimes,
									new PremiumTimeOfDailyPerformance(
										premiumTime(ots.premiumTime1, ots.premiumTime2, ots.premiumTime3, ots.premiumTime4, ots.premiumTime5,
												ots.premiumTime6, ots.premiumTime7, ots.premiumTime8, ots.premiumTime9, ots.premiumTime10,
												ots.premiumAmount1, ots.premiumAmount2, ots.premiumAmount3, ots.premiumAmount4, ots.premiumAmount5,
												ots.premiumAmount6, ots.premiumAmount7, ots.premiumAmount8, ots.premiumAmount9, ots.premiumAmount10),
										new AttendanceAmountDaily(ots.premiumAmountTotal),
										new AttendanceTime(ots.premiumTimeTotal))),
							OuenMovementTimeEachTimeSheet.create(
									new AttendanceTime(ots.moveTotalTime), 
									new AttendanceTime(ots.moveBreakTime), 
									new AttendanceTime(ots.moveWithinTime), 
									new PremiumTimeOfDailyPerformance(
										premiumTime(ots.movePremiumTime1, ots.movePremiumTime2, ots.movePremiumTime3, ots.movePremiumTime4, ots.movePremiumTime5,
												ots.movePremiumTime6, ots.movePremiumTime7, ots.movePremiumTime8, ots.movePremiumTime9, ots.movePremiumTime10,
												0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
										AttendanceAmountDaily.ZERO,
										AttendanceTime.ZERO)),
							new AttendanceAmountDaily(ots.amount));
		}).collect(Collectors.toList());
		
		return OuenWorkTimeOfDaily.create(es.get(0).pk.sid, es.get(0).pk.ymd, ouenTimes);
	}
	
	public List<PremiumTime> premiumTime(int no1, int no2, int no3, 
			int no4, int no5, int no6, int no7, 
			int no8, int no9, int no10,
			int amount1, int amount2, int amount3, int amount4,
			int amount5, int amount6, int amount7, int amount8,
			int amount9, int amount10){
		
		List<PremiumTime> premiumTimes = new ArrayList<>();
		
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(1), new AttendanceTime(no1), new AttendanceAmountDaily(amount1)));
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(2), new AttendanceTime(no2), new AttendanceAmountDaily(amount2)));
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(3), new AttendanceTime(no3), new AttendanceAmountDaily(amount3)));
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(4), new AttendanceTime(no4), new AttendanceAmountDaily(amount4)));
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(5), new AttendanceTime(no5), new AttendanceAmountDaily(amount5)));
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(6), new AttendanceTime(no6), new AttendanceAmountDaily(amount6)));
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(7), new AttendanceTime(no7), new AttendanceAmountDaily(amount7)));
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(8), new AttendanceTime(no8), new AttendanceAmountDaily(amount8)));
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(9), new AttendanceTime(no9), new AttendanceAmountDaily(amount9)));
		premiumTimes.add(new PremiumTime(ExtraTimeItemNo.valueOf(10), new AttendanceTime(no10), new AttendanceAmountDaily(amount10)));
		
		return premiumTimes;
	}

	@Override
	public void remove(String sid, GeneralDate ymd) {
		String delete = "delete from KrcdtDayOuenTime o " + " where o.pk.sid = :sid "
				+ " and o.pk.ymd = :ymd ";
		this.getEntityManager().createQuery(delete).setParameter("sid", sid)
												   .setParameter("ymd", ymd)
												   .executeUpdate();
	}

	@Override
	public void removePK(String employeeId, GeneralDate today, int i) {
		this.commandProxy().remove(KrcdtDayOuenTime.class, new KrcdtDayOuenTimePK(employeeId, today, i));
	}

}
