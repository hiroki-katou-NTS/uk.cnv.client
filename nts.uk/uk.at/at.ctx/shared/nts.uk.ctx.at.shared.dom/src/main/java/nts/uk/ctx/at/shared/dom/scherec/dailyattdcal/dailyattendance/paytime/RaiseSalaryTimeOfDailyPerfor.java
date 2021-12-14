package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.BonusPayAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BPTimeItemSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;

/**
 * 
 * @author nampt
 * 日別実績の加給時間
 *
 */
@Getter
@AllArgsConstructor
public class RaiseSalaryTimeOfDailyPerfor {
	
	//加給時間
	private List<BonusPayTime> raisingSalaryTimes;
	
	//特定日加給時間
	private List<BonusPayTime> autoCalRaisingSalarySettings;
	
	/**
	 * 日別実績の加給時間
	 * @param oneDayRange 1日の計算範囲
	 * @param bpTimeItemSets 加給自動計算設定
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 日別実績の加給時間
	 */
	public static RaiseSalaryTimeOfDailyPerfor calcBonusPayTime(
			CalculationRangeOfOneDay oneDayRange,
			List<BPTimeItemSetting> bpTimeItemSets,
			CalAttrOfDailyAttd calcAtrOfDaily) {
		if(oneDayRange == null) return new RaiseSalaryTimeOfDailyPerfor(Collections.emptyList(), Collections.emptyList());
		val bonusPay = oneDayRange.calcBonusPayTime(bpTimeItemSets, calcAtrOfDaily);
		val specBonusPay = oneDayRange.calcSpecBonusPayTime(bpTimeItemSets, calcAtrOfDaily);
		
		return new RaiseSalaryTimeOfDailyPerfor(bonusPay, specBonusPay);
	}
	
	/**
	 * 加給・特定日加給の上限値制御指示
	 * @param upperTime 上限値
	 */
	public void controlUpperTimeForSalaryTime(AttendanceTime upperTime) {
		this.controlUpperTime(this.raisingSalaryTimes,upperTime);
		this.controlUpperTime(this.autoCalRaisingSalarySettings,upperTime);
	}

	/**
	 * 上限制御処理
	 * @param bonusPayTimeList 上限制御をする対象
	 * @param upperTime 上限時間
	 */
	private void controlUpperTime(List<BonusPayTime> bonusPayTimeList, AttendanceTime upperTime) {
		//上限制御
		bonusPayTimeList.forEach(tc -> {
			tc.controlUpperTime(upperTime);
		});
	}

	public void replaceValueByPCLogInfo(RaiseSalaryTimeOfDailyPerfor rsTimeOfDaily) {
		//加給の入替
		replaceBpTimeByPcInfo(rsTimeOfDaily.getRaisingSalaryTimes());
		//特定日加給の入替
		replaceSpecBpTimeByPcInfo(rsTimeOfDaily.getAutoCalRaisingSalarySettings());
	}

	private void replaceSpecBpTimeByPcInfo(List<BonusPayTime> specRsTimes) {
		specRsTimes.forEach(tc ->{
			val getItemByBpNo = this.autoCalRaisingSalarySettings.stream().filter(ts -> ts.getBonusPayTimeItemNo() == tc.getBonusPayTimeItemNo()).findFirst();
			if(getItemByBpNo.isPresent()) {
				this.autoCalRaisingSalarySettings.forEach(tt ->{
					if(tt.getBonusPayTimeItemNo() == tc.getBonusPayTimeItemNo()) {
						tt.replaceValueByPClogInfo(tc.getBonusPayTime(),
												   tc.getWithinBonusPay().getCalcTime(),
												   tc.getExcessBonusPayTime().getCalcTime());
					}
				});
			}
			else {
				this.autoCalRaisingSalarySettings.add(tc);
			}
		});
	}

	private void replaceBpTimeByPcInfo(List<BonusPayTime> rSTimes) {
		rSTimes.forEach(tc ->{
			val getItemByBpNo = this.raisingSalaryTimes.stream().filter(ts -> ts.getBonusPayTimeItemNo() == tc.getBonusPayTimeItemNo()).findFirst();
			if(getItemByBpNo.isPresent()) {
				this.raisingSalaryTimes.forEach(tt ->{
					if(tt.getBonusPayTimeItemNo() == tc.getBonusPayTimeItemNo()) {
						tt.replaceValueByPClogInfo(tc.getBonusPayTime(),
												   tc.getWithinBonusPay().getCalcTime(),
												   tc.getExcessBonusPayTime().getCalcTime());
					}
				});
			}
			else {
				this.raisingSalaryTimes.add(tc);
			}
		});
	}
	
	public List<BonusPayTime> summary(BonusPayAtr atr ){
		List<BonusPayTime> bpList = atr.isBonusPay()?this.getRaisingSalaryTimes():this.getAutoCalRaisingSalarySettings();
		List<BonusPayTime> returnList = new ArrayList<>();
		for(int i = 1 ; i<=10 ; i++) {
			returnList.add(recreate(i,bpList));
		}
		return returnList;
	}

	private BonusPayTime recreate(int number, List<BonusPayTime> bpList) {
			int bpTime = bpList.stream().filter(tc -> tc.getBonusPayTimeItemNo() == number).map(ts -> ts.getBonusPayTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc));
			int bpWithinTime = bpList.stream().filter(tc -> tc.getBonusPayTimeItemNo() == number).map(ts -> ts.getWithinBonusPay().getTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc));
			int bpWithinCalcTime = bpList.stream().filter(tc -> tc.getBonusPayTimeItemNo() == number).map(ts -> ts.getWithinBonusPay().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc));
			int bpExcessTime =  bpList.stream().filter(tc -> tc.getBonusPayTimeItemNo() == number).map(ts -> ts.getExcessBonusPayTime().getTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc));
			int bpExcessCalcTime = bpList.stream().filter(tc -> tc.getBonusPayTimeItemNo() == number).map(ts -> ts.getExcessBonusPayTime().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc));
			return new BonusPayTime(number, 
								    new AttendanceTime(bpTime),
								    TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(bpWithinTime), new AttendanceTime( bpWithinCalcTime)),
								    TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(bpExcessTime),new AttendanceTime( bpExcessCalcTime)));
	}
}
