package nts.uk.ctx.at.record.app.find.monthly.root.common;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.ItemConst;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.ReserveLeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.ReserveLeaveRemainingNumberInfo;

@Data
/** 積立年休残情報 */
@NoArgsConstructor
@AllArgsConstructor
public class RsvLeaveRemainingNumberInfoDto implements ItemConst {

	/** 合計残日数 */
	@AttendanceItemLayout(jpPropertyName = DAYS, layout = LAYOUT_A)
	private RsvLeaveRemainingNumberDto totalRemainingDays;

	/** 付与前 */
	@AttendanceItemLayout(jpPropertyName = BEFORE, layout = LAYOUT_B)
	private RsvLeaveRemainingNumberDto before;

	/** 付与後 */
	@AttendanceItemLayout(jpPropertyName = AFTER, layout = LAYOUT_C)
	private RsvLeaveRemainingNumberDto after;

	public static RsvLeaveRemainingNumberInfoDto from(ReserveLeaveRemainingNumberInfo domain) {

		return domain == null ? null : new RsvLeaveRemainingNumberInfoDto(
				RsvLeaveRemainingNumberDto.from(domain.getReserveLeaveWithMinus().getRemainingNumberInfo().getRemainingNumber()),
				RsvLeaveRemainingNumberDto.from(domain.getReserveLeaveWithMinus().getRemainingNumberInfo().getRemainingNumberBeforeGrant()),
				RsvLeaveRemainingNumberDto.from(domain.getReserveLeaveWithMinus().getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().orElse(null)));
	}

//	public static RsvLeaveRemainingNumberInfoDto from(ReserveLeaveRemainingNumber domain) {
//
//		return domain == null ? null : new RsvLeaveRemainingNumberInfoDto(
//				RsvLeaveRemainingNumberDto.from(domain.getTotalRemainingDays().v()),
//				RsvLeaveRemainingNumberDto.from(domain.getReserveLeaveWithMinus().getRemainingNumberInfo().getRemainingNumberBeforeGrant()),
//				RsvLeaveRemainingNumberDto.from(domain.getReserveLeaveWithMinus().getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().orElse(null)));
//
//
//
//	}

//	public ReserveLeaveRemainingNumberInfo toReserveDomain() {
//
//		return ReserveLeaveRemainingNumberInfo.of(
//				totalRemainingDays.toReserveDomain(),
//				before.toReserveDomain(),
//				after.toReserveDomain());
//	}

}
