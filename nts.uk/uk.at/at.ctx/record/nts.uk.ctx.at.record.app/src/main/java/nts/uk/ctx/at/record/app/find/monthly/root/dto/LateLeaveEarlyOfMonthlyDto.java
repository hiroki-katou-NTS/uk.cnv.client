package nts.uk.ctx.at.record.app.find.monthly.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.monthly.verticaltotal.worktime.lateleaveearly.LateLeaveEarlyOfMonthly;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemLayout;

@Data
@NoArgsConstructor
@AllArgsConstructor
/** 月別実績の遅刻早退 */
public class LateLeaveEarlyOfMonthlyDto {

	@AttendanceItemLayout(jpPropertyName = "早退", layout = "A")
	/** 早退: 早退 */
	private CommonTimeCountDto leaveEarly;

	@AttendanceItemLayout(jpPropertyName = "遅刻", layout = "B")
	/** 遅刻: 遅刻 */
	private CommonTimeCountDto late;

	public static LateLeaveEarlyOfMonthlyDto from(LateLeaveEarlyOfMonthly domain) {
		LateLeaveEarlyOfMonthlyDto dto = new LateLeaveEarlyOfMonthlyDto();
		if(domain != null) {
			dto.setLate(CommonTimeCountDto.from(domain.getLate()));
			dto.setLeaveEarly(CommonTimeCountDto.from(domain.getLeaveEarly()));
		}
		return dto;
	}
	public LateLeaveEarlyOfMonthly toDomain() {
		return LateLeaveEarlyOfMonthly.of(leaveEarly == null ? null : leaveEarly.toLeaveEarly(),
										late == null ? null : late.toLate());
	}
}
