package nts.uk.ctx.at.record.infra.entity.daily.ouen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet.FullTimeNightShiftAttr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@NoArgsConstructor
@Table(name = "KRCDT_DAY_TIME_SUP")
public class KrcdtDayOuenTime extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 主キー */
	@EmbeddedId
	public KrcdtDayOuenTimePK pk;

	/** 総労働時間 */
	@Column(name = "TOTAL_TIME")
	public int totalTime;

	/** 所定内時間 */
	@Column(name = "WITHIN_TIME")
	public int withinTime;

	/** 休憩時間 */
	@Column(name = "BREAK_TIME")
	public int breakTime;

	/** 単価 */
	@Column(name = "PRICE_UNIT")
	public int priceUnit;

	/** 金額 */
	@Column(name = "AMOUNT")
	public int amount;

	/** 総移動時間 */
	@Column(name = "MOVE_TOTAL_TIME")
	public int moveTotalTime;

	/** 所定内移動時間 */
	@Column(name = "MOVE_WITHIN_TIME")
	public int moveWithinTime;

	/** 移動の休憩時間 */
	@Column(name = "MOVE_BREAK_TIME")
	public int moveBreakTime;

	/** 割増時間1 */
	@Column(name = "PREMIUM_TIME1")
	public int premiumTime1;

	/** 割増時間2 */
	@Column(name = "PREMIUM_TIME2")
	public int premiumTime2;

	/** 割増時間3 */
	@Column(name = "PREMIUM_TIME3")
	public int premiumTime3;

	/** 割増時間4 */
	@Column(name = "PREMIUM_TIME4")
	public int premiumTime4;

	/** 割増時間5 */
	@Column(name = "PREMIUM_TIME5")
	public int premiumTime5;

	/** 割増時間6 */
	@Column(name = "PREMIUM_TIME6")
	public int premiumTime6;

	/** 割増時間7 */
	@Column(name = "PREMIUM_TIME7")
	public int premiumTime7;

	/** 割増時間8 */
	@Column(name = "PREMIUM_TIME8")
	public int premiumTime8;

	/** 割増時間9 */
	@Column(name = "PREMIUM_TIME9")
	public int premiumTime9;

	/** 割増時間10 */
	@Column(name = "PREMIUM_TIME10")
	public int premiumTime10;

	/** 移動の割増時間1 */
	@Column(name = "MOVE_PREMIUM_TIME1")
	public int movePremiumTime1;

	/** 移動の割増時間2 */
	@Column(name = "MOVE_PREMIUM_TIME2")
	public int movePremiumTime2;

	/** 移動の割増時間3 */
	@Column(name = "MOVE_PREMIUM_TIME3")
	public int movePremiumTime3;

	/** 移動の割増時間4 */
	@Column(name = "MOVE_PREMIUM_TIME4")
	public int movePremiumTime4;

	/** 移動の割増時間5 */
	@Column(name = "MOVE_PREMIUM_TIME5")
	public int movePremiumTime5;

	/** 移動の割増時間6 */
	@Column(name = "MOVE_PREMIUM_TIME6")
	public int movePremiumTime6;

	/** 移動の割増時間7 */
	@Column(name = "MOVE_PREMIUM_TIME7")
	public int movePremiumTime7;

	/** 移動の割増時間8 */
	@Column(name = "MOVE_PREMIUM_TIME8")
	public int movePremiumTime8;

	/** 移動の割増時間9 */
	@Column(name = "MOVE_PREMIUM_TIME9")
	public int movePremiumTime9;

	/** 移動の割増時間10 */
	@Column(name = "MOVE_PREMIUM_TIME10")
	public int movePremiumTime10;

	/** 常勤勤務時間 */
	@Column(name = "NORMAL_WORK_TIME")
	public int normalWorkTime;

	/** 常勤控除時間 */
	@Column(name = "NORMAL_DEDUCTION_TIME")
	public int normalDeductionTime;

	/** 常勤休憩時間 */
	@Column(name = "NORMAL_BREAK_TIME")
	public int normalBreakTime;

	/** 夜勤勤務時間 */
	@Column(name = "NIGHT_WORK_TIME")
	public int nightWorkTime;

	/** 夜勤控除時間 */
	@Column(name = "NIGHT_DEDUCTION_TIME")
	public int nightDeductionTime;

	/** 夜勤休憩時間 */
	@Column(name = "NIGHT_BREAK_TIME")
	public int nightBreakTime;

	@Override
	protected Object getKey() {
		return pk;
	}
	
	public static List<KrcdtDayOuenTime> convert(OuenWorkTimeOfDaily domain) {
		
		List<KrcdtDayOuenTime> rs = new ArrayList<>();

		for (OuenWorkTimeOfDailyAttendance oTimeOfDaily : domain.getOuenTimes()) {
			KrcdtDayOuenTime entity = new KrcdtDayOuenTime();
			entity.pk = new KrcdtDayOuenTimePK(domain.getEmpId(), domain.getYmd(), oTimeOfDaily.getWorkNo());

			entity.amount = oTimeOfDaily.getAmount().v();
			entity.priceUnit = oTimeOfDaily.getPriceUnit().v();
			entity.totalTime = oTimeOfDaily.getWorkTime().getTotalTime().valueAsMinutes();
			entity.breakTime = oTimeOfDaily.getWorkTime().getBreakTime().valueAsMinutes();
			entity.withinTime = oTimeOfDaily.getWorkTime().getWithinTime().valueAsMinutes();
			entity.premiumTime1 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 1);
			entity.premiumTime2 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 2);
			entity.premiumTime3 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 3);
			entity.premiumTime4 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 4);
			entity.premiumTime5 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 5);
			entity.premiumTime6 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 6);
			entity.premiumTime7 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 7);
			entity.premiumTime8 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 8);
			entity.premiumTime9 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 9);
			entity.premiumTime10 = getPremiumTime(oTimeOfDaily.getWorkTime().getPremiumTime(), 10);
			entity.moveTotalTime = oTimeOfDaily.getMoveTime().getTotalMoveTime().valueAsMinutes();
			entity.moveBreakTime = oTimeOfDaily.getMoveTime().getBreakTime().valueAsMinutes();
			entity.moveWithinTime = oTimeOfDaily.getMoveTime().getWithinMoveTime().valueAsMinutes();
			entity.movePremiumTime1 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 1);
			entity.movePremiumTime2 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 2);
			entity.movePremiumTime3 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 3);
			entity.movePremiumTime4 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 4);
			entity.movePremiumTime5 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 5);
			entity.movePremiumTime6 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 6);
			entity.movePremiumTime7 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 7);
			entity.movePremiumTime8 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 8);
			entity.movePremiumTime9 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 9);
			entity.movePremiumTime10 = getPremiumTime(oTimeOfDaily.getMoveTime().getPremiumTime(), 10);

			getMedicalTime(oTimeOfDaily.getWorkTime().getMedicalTime(), FullTimeNightShiftAttr.DAY_SHIFT)
					.ifPresent(m -> {

						entity.normalWorkTime = m.getWorkTime().valueAsMinutes();
						entity.normalBreakTime = m.getBreakTime().valueAsMinutes();
						entity.normalDeductionTime = m.getDeductionTime().valueAsMinutes();
					});
			getMedicalTime(oTimeOfDaily.getWorkTime().getMedicalTime(), FullTimeNightShiftAttr.NIGHT_SHIFT)
					.ifPresent(m -> {

						entity.nightWorkTime = m.getWorkTime().valueAsMinutes();
						entity.nightBreakTime = m.getBreakTime().valueAsMinutes();
						entity.nightDeductionTime = m.getDeductionTime().valueAsMinutes();
					});
			rs.add(entity);
		}
		return rs;
	}
	
	private static Optional<MedicalCareTimeEachTimeSheet> getMedicalTime(
			List<MedicalCareTimeEachTimeSheet> times, FullTimeNightShiftAttr attr) {
		
		return times.stream().filter(c -> c.getAttr() == attr)
				.findFirst();
	}
	
	private static int getPremiumTime(List<PremiumTime> times, int no) {
		
		return times.stream().filter(c -> c.getPremiumTimeNo() == no)
				.findFirst().map(c -> c.getPremitumTime().v()).orElse(0);
	}
}
