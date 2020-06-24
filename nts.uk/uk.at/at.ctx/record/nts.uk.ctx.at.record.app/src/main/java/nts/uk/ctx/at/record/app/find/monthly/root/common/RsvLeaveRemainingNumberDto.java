package nts.uk.ctx.at.record.app.find.monthly.root.common;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.monthly.vacation.annualleave.AnnualLeaveRemainingDetail;
import nts.uk.ctx.at.record.dom.monthly.vacation.annualleave.AnnualLeaveRemainingNumber;
import nts.uk.ctx.at.record.dom.monthly.vacation.reserveleave.ReserveLeaveRemainingNumber;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.attendance.util.ItemConst;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.attendance.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.daynumber.ReserveLeaveRemainingDayNumber;

@Data
/** 年休残数 */
@NoArgsConstructor
@AllArgsConstructor
public class RsvLeaveRemainingNumberDto implements ItemConst {

	/** 合計残日数 */
	@AttendanceItemValue(type = ValueType.DAYS)
	@AttendanceItemLayout(jpPropertyName = DAYS, layout = LAYOUT_A)
	private double totalRemainingDays;

	/** 付与前 */
	@AttendanceItemValue(type = ValueType.DAYS)
	@AttendanceItemLayout(jpPropertyName = BEFORE, layout = LAYOUT_B)
	private double before;
	
	/** 付与後 */
	@AttendanceItemValue(type = ValueType.DAYS)
	@AttendanceItemLayout(jpPropertyName = AFTER, layout = LAYOUT_B)
	private Double after;

	/** 明細 */
	// @AttendanceItemLayout(jpPropertyName = "明細", layout = "C", listMaxLength = ??)
	private List<CommonlLeaveRemainingDetailDto> details;

	public static RsvLeaveRemainingNumberDto from(ReserveLeaveRemainingNumber domain) {
		return domain == null ? null : new RsvLeaveRemainingNumberDto(
				domain.getTotalRemainingDays().v(),
				domain.getBeforeGrant().v(), 
				domain.getAfterGrant().map(c -> c.v()).orElse(null), 
				ConvertHelper.mapTo(domain.getDetails(), c -> CommonlLeaveRemainingDetailDto.from(c)));
	}
	
	public ReserveLeaveRemainingNumber toReserveDomain() {
		return ReserveLeaveRemainingNumber.of(
				new ReserveLeaveRemainingDayNumber(totalRemainingDays), 
				new ReserveLeaveRemainingDayNumber(before), 
				Optional.ofNullable(after == null ? null : new ReserveLeaveRemainingDayNumber(after)), 
				ConvertHelper.mapTo(details, c -> c == null ? null : c.toReserveDomain()));
	}
}
