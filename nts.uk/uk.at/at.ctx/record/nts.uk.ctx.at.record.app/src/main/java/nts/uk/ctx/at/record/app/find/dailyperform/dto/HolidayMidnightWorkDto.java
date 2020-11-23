package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayMidnightWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;

/** 休出深夜 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayMidnightWorkDto implements ItemConst {

	 /** 休出深夜時間: 休出深夜時間 */
//	 @AttendanceItemLayout(layout="A", jpPropertyName="休出深夜時間")
//	 private HolidayWorkMidNightTimeDto holidayWorkMidNightTime;

	/** 法定内: 計算付き時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = LEGAL)
	private CalcAttachTimeDto withinPrescribedHolidayWork;

	/** 法定外: 計算付き時間 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = ILLEGAL)
	private CalcAttachTimeDto excessOfStatutoryHolidayWork;

	/** 祝日: 計算付き時間 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = PUBLIC_HOLIDAY)
	private CalcAttachTimeDto publicHolidayWork;
	
	public static HolidayMidnightWorkDto fromHolidayMidnightWork(HolidayMidnightWork domain){
		return domain == null ? null : new HolidayMidnightWorkDto(getWorkTime(
				domain.getHolidayWorkMidNightTime(), StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork), 
				getWorkTime(domain.getHolidayWorkMidNightTime(), StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork), 
				getWorkTime(domain.getHolidayWorkMidNightTime(), StaturoryAtrOfHolidayWork.PublicHolidayWork));
	}
	
	@Override
	public HolidayMidnightWorkDto clone() {
		return new HolidayMidnightWorkDto(withinPrescribedHolidayWork == null ? null : withinPrescribedHolidayWork.clone(), 
						excessOfStatutoryHolidayWork == null ? null : excessOfStatutoryHolidayWork.clone(),
						publicHolidayWork == null ? null : publicHolidayWork.clone());
	}
	
	private static CalcAttachTimeDto getWorkTime(List<HolidayWorkMidNightTime> source, StaturoryAtrOfHolidayWork type){
		return source.stream().filter(c -> c.getStatutoryAtr() == type).findFirst().map(c -> 
														CalcAttachTimeDto.toTimeWithCal(c.getTime())).orElse(null);
	}
	
	public HolidayMidnightWork toDomain() {
		return withinPrescribedHolidayWork == null ? createDefaul() : new HolidayMidnightWork(Arrays.asList(
				newMidNightTime(withinPrescribedHolidayWork, StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork),
				newMidNightTime(excessOfStatutoryHolidayWork, StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork),
				newMidNightTime(publicHolidayWork, StaturoryAtrOfHolidayWork.PublicHolidayWork)));
	}

	public HolidayWorkMidNightTime newMidNightTime(CalcAttachTimeDto time, StaturoryAtrOfHolidayWork attr) {
		return new HolidayWorkMidNightTime(time == null ? TimeDivergenceWithCalculation.emptyTime() 
																: time.createTimeDivWithCalc(), attr);
	}
	
	public static HolidayMidnightWork createDefaul(){
		return new HolidayMidnightWork(Arrays.asList(
				 createHolidateMidTime(StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork),
				 createHolidateMidTime(StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork),
				 createHolidateMidTime(StaturoryAtrOfHolidayWork.PublicHolidayWork)));
	}

	private static HolidayWorkMidNightTime createHolidateMidTime(StaturoryAtrOfHolidayWork statutoryAtr) {
		return new HolidayWorkMidNightTime(TimeDivergenceWithCalculation.defaultValue(), statutoryAtr);
	}
}
