package nts.uk.ctx.at.record.infra.entity.daily.ouen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet.FullTimeNightShiftAttr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;
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

	/** 金額 */
	@Column(name = "AMOUNT")
	public int amount;

	/** 総移動時間 */
	@Column(name = "MOVE_TOTAL_TIME")
	public int moveTotalTime;

	/** 所定内移動時間 */
	@Column(name = "MOVE_WITHIN_TIME")
	public int moveWithinTime;

	/** 移動休憩時間 */
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
	
	/** 割増金額1 */
	@Column(name = "PREMIUM_AMOUNT1")
	public int premiumAmount1;

	/** 割増金額2 */
	@Column(name = "PREMIUM_AMOUNT2")
	public int premiumAmount2;

	/** 割増金額3 */
	@Column(name = "PREMIUM_AMOUNT3")
	public int premiumAmount3;

	/** 割増金額4 */
	@Column(name = "PREMIUM_AMOUNT4")
	public int premiumAmount4;

	/** 割増金額5 */
	@Column(name = "PREMIUM_AMOUNT5")
	public int premiumAmount5;

	/** 割増金額6 */
	@Column(name = "PREMIUM_AMOUNT6")
	public int premiumAmount6;

	/** 割増金額7 */
	@Column(name = "PREMIUM_AMOUNT7")
	public int premiumAmount7;

	/** 割増金額8 */
	@Column(name = "PREMIUM_AMOUNT8")
	public int premiumAmount8;

	/** 割増金額9 */
	@Column(name = "PREMIUM_AMOUNT9")
	public int premiumAmount9;

	/** 割増金額10 */
	@Column(name = "PREMIUM_AMOUNT10")
	public int premiumAmount10;

	/** 移動割増時間1 */
	@Column(name = "MOVE_PREMIUM_TIME1")
	public int movePremiumTime1;

	/** 移動割増時間2 */
	@Column(name = "MOVE_PREMIUM_TIME2")
	public int movePremiumTime2;

	/** 移動割増時間3 */
	@Column(name = "MOVE_PREMIUM_TIME3")
	public int movePremiumTime3;

	/** 移動割増時間4 */
	@Column(name = "MOVE_PREMIUM_TIME4")
	public int movePremiumTime4;

	/** 移動割増時間5 */
	@Column(name = "MOVE_PREMIUM_TIME5")
	public int movePremiumTime5;

	/** 移動の割増時間6 */
	@Column(name = "MOVE_PREMIUM_TIME6")
	public int movePremiumTime6;

	/** 移動割増時間7 */
	@Column(name = "MOVE_PREMIUM_TIME7")
	public int movePremiumTime7;

	/** 移動割増時間8 */
	@Column(name = "MOVE_PREMIUM_TIME8")
	public int movePremiumTime8;

	/** 移動割増時間9 */
	@Column(name = "MOVE_PREMIUM_TIME9")
	public int movePremiumTime9;

	/** 移動割増時間10 */
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

	/** 所定内時間金額 */
	@Column(name = "WITHIN_AMOUNT")
	public int withinAmount;

	/** 割増時間合計 */
	@Column(name = "PREMIUM_TIME_TOTAL")
	public int premiumTimeTotal;

	/** 割増金額合計 */
	@Column(name = "PREMIUM_AMOUNT_TOTAL")
	public int premiumAmountTotal;

	@Override
	protected Object getKey() {
		return pk;
	}
	
	public static List<KrcdtDayOuenTime> convert(OuenWorkTimeOfDaily domain) {
		
		List<KrcdtDayOuenTime> rs = new ArrayList<>();

		for (OuenWorkTimeOfDailyAttendance oTimeOfDaily : domain.getOuenTimes()) {
			KrcdtDayOuenTime entity = new KrcdtDayOuenTime();
			entity.pk = new KrcdtDayOuenTimePK(domain.getEmpId(), domain.getYmd(), oTimeOfDaily.getWorkNo().v());

			entity.amount = oTimeOfDaily.getAmount().v();
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
			entity.premiumAmount1 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 1);
			entity.premiumAmount2 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 2);
			entity.premiumAmount3 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 3);
			entity.premiumAmount4 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 4);
			entity.premiumAmount5 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 5);
			entity.premiumAmount6 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 6);
			entity.premiumAmount7 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 7);
			entity.premiumAmount8 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 8);
			entity.premiumAmount9 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 9);
			entity.premiumAmount10 = getPremiumAmount(oTimeOfDaily.getWorkTime().getPremiumTime(), 10);
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
			entity.withinAmount = oTimeOfDaily.getWorkTime().getWithinAmount().v();
			entity.premiumTimeTotal = oTimeOfDaily.getWorkTime().getPremiumTime().getTotalWorkingTime().valueAsMinutes();
			entity.premiumAmountTotal = oTimeOfDaily.getWorkTime().getPremiumTime().getTotalAmount().v();

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
	
	private static int getPremiumTime(PremiumTimeOfDailyPerformance time, int no) {
		
		return time.getPremiumTimes().stream().filter(c -> c.getPremiumTimeNo().value == no)
				.findFirst().map(c -> c.getPremitumTime().v()).orElse(0);
	}

	private static int getPremiumAmount(PremiumTimeOfDailyPerformance time, int no) {
		return time.getPremiumTimes().stream().filter(c -> c.getPremiumTimeNo().value == no)
				.findFirst().map(c -> c.getPremiumAmount().v()).orElse(0);
	}

	public void update(KrcdtDayOuenTime entity) {
		this.amount = entity.amount;
		this.totalTime = entity.totalTime;
		this.breakTime = entity.breakTime;
		this.withinTime = entity.withinTime;
		this.premiumTime1 = entity.premiumTime1;
		this.premiumTime2 = entity.premiumTime2;
		this.premiumTime3 = entity.premiumTime3;
		this.premiumTime4 = entity.premiumTime4;
		this.premiumTime5 = entity.premiumTime5;
		this.premiumTime6 = entity.premiumTime6;
		this.premiumTime7 = entity.premiumTime7;
		this.premiumTime8 = entity.premiumTime8;
		this.premiumTime9 = entity.premiumTime9;
		this.premiumTime10 = entity.premiumTime10;
		this.premiumAmount1 = entity.premiumAmount1;
		this.premiumAmount2 = entity.premiumAmount2;
		this.premiumAmount3 = entity.premiumAmount3;
		this.premiumAmount4 = entity.premiumAmount4;
		this.premiumAmount5 = entity.premiumAmount5;
		this.premiumAmount6 = entity.premiumAmount6;
		this.premiumAmount7 = entity.premiumAmount7;
		this.premiumAmount8 = entity.premiumAmount8;
		this.premiumAmount9 = entity.premiumAmount9;
		this.premiumAmount10 = entity.premiumAmount10;
		this.moveTotalTime = entity.moveTotalTime;
		this.moveBreakTime = entity.moveBreakTime;
		this.moveWithinTime = entity.moveWithinTime;
		this.movePremiumTime1 = entity.movePremiumTime1;
		this.movePremiumTime2 = entity.movePremiumTime2;
		this.movePremiumTime3 = entity.movePremiumTime3;
		this.movePremiumTime4 = entity.movePremiumTime4;
		this.movePremiumTime5 = entity.movePremiumTime5;
		this.movePremiumTime6 = entity.movePremiumTime6;
		this.movePremiumTime7 = entity.movePremiumTime7;
		this.movePremiumTime8 = entity.movePremiumTime8;
		this.movePremiumTime9 = entity.movePremiumTime9;
		this.movePremiumTime10 = entity.movePremiumTime10;
		this.normalWorkTime = entity.normalWorkTime;
		this.normalBreakTime = entity.normalBreakTime;
		this.normalDeductionTime = entity.normalDeductionTime;
		this.nightWorkTime = entity.nightWorkTime;
		this.nightBreakTime = entity.nightBreakTime;
		this.nightDeductionTime = entity.nightDeductionTime;
		this.withinAmount = entity.withinAmount;
		this.premiumTimeTotal = entity.premiumTimeTotal;
		this.premiumAmountTotal = entity.premiumAmountTotal;
	}

}
