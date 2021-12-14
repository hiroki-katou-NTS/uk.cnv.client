package nts.uk.ctx.at.record.infra.repository.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.record.infra.entity.daily.ouen.KrcdtDayOuenTime;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.dailyattdcal.premiumitem.PriceUnit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet.FullTimeNightShiftAttr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenAttendanceTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenMovementTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;

@Stateless
public class OuenWorkTimeOfDailyRepoImpl extends JpaRepository implements OuenWorkTimeOfDailyRepo {

	@Override
	public OuenWorkTimeOfDaily find(String empId, GeneralDate ymd) {
		List<KrcdtDayOuenTime> entitis = queryProxy().query("SELECT o FROM KrcdtDayOuenTime o WHERE o.pk.sid = :sid AND o.pk.ymd = :ymd", 
									KrcdtDayOuenTime.class)
				.setParameter("sid", empId).setParameter("ymd", ymd)
				.getList();
		if(entitis.isEmpty())
			return null;
		
		OuenWorkTimeOfDaily rs = toDomain(entitis);
		
		return rs;
	}

	@Override
	public void update(List<OuenWorkTimeOfDaily> domain) {
		domain.stream().map(c -> KrcdtDayOuenTime.convert(c)).forEach(e -> {
			commandProxy().update(e);
		});
	}

	@Override
	public void insert(List<OuenWorkTimeOfDaily> domain) {
		domain.stream().map(c -> KrcdtDayOuenTime.convert(c)).forEach(e -> {
			commandProxy().insert(e);
		});
	}

	@Override
	public void delete(List<OuenWorkTimeOfDaily> domain) {
		domain.stream().map(c -> KrcdtDayOuenTime.convert(c)).forEach(e -> {
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
			
			return OuenWorkTimeOfDailyAttendance.create(ots.pk.ouenNo, 
							OuenAttendanceTimeEachTimeSheet.create(
									new AttendanceTime(ots.totalTime), 
									new AttendanceTime(ots.breakTime), 
									new AttendanceTime(ots.withinTime),
									medicalTimes, 
									premiumTime(ots.premiumTime1, ots.premiumTime2, ots.premiumTime3, ots.premiumTime4, ots.premiumTime5, 
											ots.premiumTime6, ots.premiumTime7, ots.premiumTime8, ots.premiumTime9, ots.premiumTime10)),
							OuenMovementTimeEachTimeSheet.create(
									new AttendanceTime(ots.moveTotalTime), 
									new AttendanceTime(ots.moveBreakTime), 
									new AttendanceTime(ots.moveWithinTime), 
									premiumTime(ots.movePremiumTime1, ots.movePremiumTime2, ots.movePremiumTime3, ots.movePremiumTime4, ots.movePremiumTime5, 
											    ots.movePremiumTime6, ots.movePremiumTime7, ots.movePremiumTime8, ots.movePremiumTime9, ots.movePremiumTime10)), 
							new AttendanceAmountDaily(ots.amount), 
							new PriceUnit(ots.priceUnit));
		}).collect(Collectors.toList());
		
		return OuenWorkTimeOfDaily.create(es.get(0).pk.sid, es.get(0).pk.ymd, ouenTimes);
	}
	
	public List<PremiumTime> premiumTime(int no1, int no2, int no3, 
			int no4, int no5, int no6, int no7, 
			int no8, int no9, int no10){
		
		List<PremiumTime> premiumTimes = new ArrayList<>();
		
		premiumTimes.add(new PremiumTime(1, new AttendanceTime(no1)));
		premiumTimes.add(new PremiumTime(2, new AttendanceTime(no2)));
		premiumTimes.add(new PremiumTime(3, new AttendanceTime(no3)));
		premiumTimes.add(new PremiumTime(4, new AttendanceTime(no4)));
		premiumTimes.add(new PremiumTime(5, new AttendanceTime(no5)));
		premiumTimes.add(new PremiumTime(6, new AttendanceTime(no6)));
		premiumTimes.add(new PremiumTime(7, new AttendanceTime(no7)));
		premiumTimes.add(new PremiumTime(8, new AttendanceTime(no8)));
		premiumTimes.add(new PremiumTime(9, new AttendanceTime(no9)));
		premiumTimes.add(new PremiumTime(10, new AttendanceTime(no10)));
		
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

}
