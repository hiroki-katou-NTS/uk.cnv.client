package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.divergencetimeofdaily.DivergenceTime;
import nts.uk.ctx.at.shared.dom.attendanceitem.util.annotation.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.attendanceitem.util.annotation.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.attendanceitem.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

/** 乖離時間 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DivergenceTimeDto {

	/** 乖離時間: 勤怠時間 */
	@AttendanceItemLayout(layout = "A", jpPropertyName = "乖離時間")
	@AttendanceItemValue(type = ValueType.INTEGER)
	private Integer divergenceTime;

	/** 控除時間: 勤怠時間 */
	@AttendanceItemLayout(layout = "B", jpPropertyName = "控除時間")
	@AttendanceItemValue(type = ValueType.INTEGER)
	private Integer deductionTime;

	/** 乖離理由コード: 乖離理由コード */
	@AttendanceItemLayout(layout = "C", jpPropertyName = "乖離理由コード")
	@AttendanceItemValue
	private String divergenceReasonCode;

	/** 乖離理由: 乖離理由 */
	@AttendanceItemLayout(layout = "D", jpPropertyName = "乖離理由")
	@AttendanceItemValue
	private String divergenceReason;

	/** 控除後乖離時間: 勤怠時間 */
	@AttendanceItemLayout(layout = "E", jpPropertyName = "控除後乖離時間")
	@AttendanceItemValue(type = ValueType.INTEGER)
	private Integer divergenceTimeAfterDeduction;

	/** 乖離時間NO: 乖離時間NO */
	private Integer divergenceTimeNo;
	
	public static DivergenceTimeDto fromDivergenceTime(DivergenceTime domain){
		return domain == null ? null : new DivergenceTimeDto(
				getAttendanceTime(domain.getDivTime()), 
				getAttendanceTime(domain.getDeductionTime()),
				domain.getDivResonCode() == null ? null : domain.getDivResonCode().v(), 
				domain.getDivReason() == null ? null : domain.getDivReason().v(), 
				getAttendanceTime(domain.getDivTimeAfterDeduction()), 
				domain.getDivTimeId());
	}

	private static int getAttendanceTime(AttendanceTime domain) {
		return domain == null ? null : domain.valueAsMinutes();
	}
}
