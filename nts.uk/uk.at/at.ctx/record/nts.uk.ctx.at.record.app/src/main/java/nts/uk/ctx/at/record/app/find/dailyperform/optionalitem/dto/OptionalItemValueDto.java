package nts.uk.ctx.at.record.app.find.dailyperform.optionalitem.dto;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemAmount;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemNo;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemTime;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemTimes;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValue;
import nts.uk.ctx.at.record.dom.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemValue;

@AllArgsConstructor
@NoArgsConstructor
public class OptionalItemValueDto {

	/** 任意項目: 回数, 時間, 金額*/
	@AttendanceItemValue
	@AttendanceItemLayout(layout = "A", jpPropertyName = "値")
	private String value;

	@Getter
	private int itemNo;
	
	private boolean isTimeItem;
	
	private boolean isTimesItem;
	
	private boolean isAmountItem;
	
	public static OptionalItemValueDto from(AnyItemValue c) {
		if(c != null) {
			boolean isTimes = c.getTimes().isPresent();
			boolean isAmount = c.getAmount().isPresent();
			boolean isTime = c.getTime().isPresent();
			String value = isAmount ? c.getAmount().get().v().toString()
					: isTime ? String.valueOf(c.getTime().get().valueAsMinutes())
							: String.valueOf(c.getTimes().get().v());
			return new OptionalItemValueDto(value, c.getItemNo().v(), isTime, isTimes, isAmount);
		}
		return null;
	}
	
	public AnyItemValue toDomain() {
		return new AnyItemValue(new AnyItemNo(this.itemNo), 
						this.isTimesItem ? Optional.of(new AnyItemTimes(Integer.valueOf(this.value))) : Optional.empty(),
						this.isAmountItem ? Optional.of(new AnyItemAmount(new BigDecimal(this.value))) : Optional.empty(),
						this.isTimeItem ? Optional.of(new AnyItemTime(Integer.valueOf(this.value))) : Optional.empty());
	}
	
	public static OptionalItemValueDto from(AnyItemOfMonthly c) {
		if(c != null) {
			boolean isTimes = c.getTimes().v().doubleValue() > 0;
			boolean isAmount = c.getAmount().v() > 0;
			boolean isTime = c.getTime().valueAsMinutes() > 0;
			String value = isTimes ? c.getTimes().v().toString()
					: isAmount ? String.valueOf(c.getAmount().v())
							: String.valueOf(c.getTime().valueAsMinutes());
			return new OptionalItemValueDto(value, c.getAnyItemId(), isTime, isTimes, isAmount);
		}
		return null;
	}
	
	public Integer getDailyTime(){
		if(isTimeItem){
			return Integer.parseInt(value);
		}
		return null;
	}
	
	public Integer getDailyTimes(){
		if(isTimesItem){
			return Integer.parseInt(value);
		}
		return null;
	}
	
	public BigDecimal getDailyAmount(){
		if(isAmountItem){
			return new BigDecimal(value);
		}
		return null;
	}
	
	public int getMonthlyTime(){
		if(isTimeItem){
			return Integer.parseInt(value);
		}
		return 0;
	}
	
	public double getMonthlyTimes(){
		if(isTimesItem){
			return Double.parseDouble(value);
		}
		return 0d;
	}
	
	public int getMonthlyAmount(){
		if(isAmountItem){
			return Integer.parseInt(value);
		}
		return 0;
	}
}
