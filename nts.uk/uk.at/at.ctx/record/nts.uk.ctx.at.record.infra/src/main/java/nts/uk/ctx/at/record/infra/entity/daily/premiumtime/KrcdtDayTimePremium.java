package nts.uk.ctx.at.record.infra.entity.daily.premiumtime;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import nts.arc.time.GeneralDate;
import nts.gul.reflection.FieldReflection;
//import nts.uk.ctx.at.record.infra.entity.daily.actualworktime.KrcdtDayAttendanceTime;
import nts.uk.ctx.at.record.infra.entity.daily.time.KrcdtDayTimeAtd;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "KRCDT_DAY_TIME_PREMIUM")
public class KrcdtDayTimePremium extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/* 主キー */
	@EmbeddedId
	public KrcdtDayPremiumTimePK krcdtDayPremiumTimePK;
	
	/* 割増時間1 */
	@Column(name = "PREMIUM_TIME1")
	public int premiumTime1;
	/* 割増時間2 */
	@Column(name = "PREMIUM_TIME2")
	public int premiumTime2;
	/* 割増時間3 */
	@Column(name = "PREMIUM_TIME3")
	public int premiumTime3;
	/* 割増時間4 */
	@Column(name = "PREMIUM_TIME4")
	public int premiumTime4;
	/* 割増時間5 */
	@Column(name = "PREMIUM_TIME5")
	public int premiumTime5;
	/* 割増時間6 */
	@Column(name = "PREMIUM_TIME6")
	public int premiumTime6;
	/* 割増時間7 */
	@Column(name = "PREMIUM_TIME7")
	public int premiumTime7;
	/* 割増時間8 */
	@Column(name = "PREMIUM_TIME8")
	public int premiumTime8;
	/* 割増時間9 */
	@Column(name = "PREMIUM_TIME9")
	public int premiumTime9;
	/* 割増時間10 */
	@Column(name = "PREMIUM_TIME10")
	public int premiumTime10;
	/* 割増金額1 */
	@Column(name = "PREMIUM_AMOUNT1")
	public int premiumAmount1;
	/* 割増金額2 */
	@Column(name = "PREMIUM_AMOUNT2")
	public int premiumAmount2;
	/* 割増金額3 */
	@Column(name = "PREMIUM_AMOUNT3")
	public int premiumAmount3;
	/* 割増金額4 */
	@Column(name = "PREMIUM_AMOUNT4")
	public int premiumAmount4;
	/* 割増金額5 */
	@Column(name = "PREMIUM_AMOUNT5")
	public int premiumAmount5;
	/* 割増金額6 */
	@Column(name = "PREMIUM_AMOUNT6")
	public int premiumAmount6;
	/* 割増金額7 */
	@Column(name = "PREMIUM_AMOUNT7")
	public int premiumAmount7;
	/* 割増金額8 */
	@Column(name = "PREMIUM_AMOUNT8")
	public int premiumAmount8;
	/* 割増金額9 */
	@Column(name = "PREMIUM_AMOUNT9")
	public int premiumAmount9;
	/* 割増金額10 */
	@Column(name = "PREMIUM_AMOUNT10")
	public int premiumAmount10;
	/* 割増時間合計 */
	@Column(name = "PREMIUM_TIME_TOTAL")
	public int premiumTimeTotal;
	/* 割増金額合計 */
	@Column(name = "PREMIUM_AMOUNT_TOTAL")
	public int premiumAmountTotal;
	
	@OneToOne(mappedBy="krcdtDayPremiumTime", fetch = FetchType.LAZY)
	//public KrcdtDayAttendanceTime krcdtDayAttendanceTime;
	public KrcdtDayTimeAtd krcdtDayTime;
	
	@Override
	protected Object getKey() {
		return this.krcdtDayPremiumTimePK;
	}
	
	public PremiumTimeOfDailyPerformance toDomain() {	
		List<PremiumTime> premiumTimeList = new ArrayList<>();
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(1), new AttendanceTime(this.premiumTime1), new AttendanceAmountDaily(this.premiumAmount1)));
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(2), new AttendanceTime(this.premiumTime2), new AttendanceAmountDaily(this.premiumAmount2)));
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(3), new AttendanceTime(this.premiumTime3), new AttendanceAmountDaily(this.premiumAmount3)));
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(4), new AttendanceTime(this.premiumTime4), new AttendanceAmountDaily(this.premiumAmount4)));
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(5), new AttendanceTime(this.premiumTime5), new AttendanceAmountDaily(this.premiumAmount5)));
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(6), new AttendanceTime(this.premiumTime6), new AttendanceAmountDaily(this.premiumAmount6)));
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(7), new AttendanceTime(this.premiumTime7), new AttendanceAmountDaily(this.premiumAmount7)));
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(8), new AttendanceTime(this.premiumTime8), new AttendanceAmountDaily(this.premiumAmount8)));
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(9), new AttendanceTime(this.premiumTime9), new AttendanceAmountDaily(this.premiumAmount9)));
		premiumTimeList.add(new PremiumTime(ExtraTimeItemNo.valueOf(10), new AttendanceTime(this.premiumTime10), new AttendanceAmountDaily(this.premiumAmount10)));
		return new PremiumTimeOfDailyPerformance(premiumTimeList, new AttendanceAmountDaily(premiumAmountTotal), new AttendanceTime(premiumTimeTotal));
	}
	
	public void setData(PremiumTimeOfDailyPerformance domain) {
		if(domain == null || domain.getPremiumTimes() == null || domain.getPremiumTimes().isEmpty() )
			return;
		//値のセットループ
		for(int loopNumber = 1 ; loopNumber <= 10 ; loopNumber++) {
			Optional<PremiumTime> premiumTime = domain.getPremiumTime(ExtraTimeItemNo.valueOf(loopNumber));
			//自分自身の値セット先(フィールド)取得 [割増時間]
			Field field = FieldReflection.getField(this.getClass(), "premiumTime" + loopNumber);		
			if(premiumTime.isPresent()&&premiumTime.get().getPremitumTime()!=null) {
				//値セット
				FieldReflection.setField(field, this, premiumTime.get().getPremitumTime().valueAsMinutes());
			}else {
				//値セット
				FieldReflection.setField(field, this, 0);
			}
			//自分自身の値セット先(フィールド)取得 [割増金額]
			Field amount = FieldReflection.getField(this.getClass(), "premiumAmount" + loopNumber);
			if(premiumTime.isPresent()&&premiumTime.get().getPremiumAmount()!=null) {
				//値セット
				FieldReflection.setField(amount, this, premiumTime.get().getPremiumAmount().v());
			}else {
				//値セット
				FieldReflection.setField(amount, this, 0);
			}
		}
		Field timeTotal = FieldReflection.getField(this.getClass(), "premiumTimeTotal");
		FieldReflection.setField(timeTotal, this, domain.getTotalWorkingTime().valueAsMinutes());
		Field amountTotal = FieldReflection.getField(this.getClass(), "premiumAmountTotal");
		FieldReflection.setField(amountTotal, this, domain.getTotalAmount().v());
	}
	
	public static KrcdtDayTimePremium totoEntity(String employeeId,GeneralDate targetDate,PremiumTimeOfDailyPerformance domain) {
		KrcdtDayTimePremium entity = new KrcdtDayTimePremium();
		/*主キーセット*/
		entity.krcdtDayPremiumTimePK = new KrcdtDayPremiumTimePK(employeeId, targetDate);
		/*データセット*/
		entity.setData(domain);
		return entity;
	}
	
	
}
