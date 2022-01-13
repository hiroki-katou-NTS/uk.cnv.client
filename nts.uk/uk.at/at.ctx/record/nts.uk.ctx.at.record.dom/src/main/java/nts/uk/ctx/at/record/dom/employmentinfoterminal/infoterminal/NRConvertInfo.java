package nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.LeaveCategory;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author thanh_nx
 *
 *         NRLの変換情報
 */
@AllArgsConstructor
@Getter
public class NRConvertInfo implements StampInfoConversion {

	// 外出打刻の変換
	private final OutPlaceConvert outPlaceConvert;

	// 出退勤を入退門に変換
	private final NotUseAtr entranceExit;

	// [1] NRから変換する
	@Override
	public Optional<ChangeClockAtr> convertFromNR(LeaveCategory leavCategory) {

		switch (leavCategory) {
		case WORK:
		case WORK_HALF:
		case WORK_FLEX:
			if (entranceExit == NotUseAtr.USE)
				return Optional.of(ChangeClockAtr.OVER_TIME);
			return Optional.of(ChangeClockAtr.GOING_TO_WORK);

		case EARLY:
		case VACATION:
			return Optional.of(ChangeClockAtr.GOING_TO_WORK);

		case LEAVE:
		case LEAVE_HALF:
		case LEAVE_OVERTIME:
		case LEAVE_FLEX:
			if (entranceExit == NotUseAtr.USE)
				return Optional.of(ChangeClockAtr.BRARK);
			return Optional.of(ChangeClockAtr.WORKING_OUT);

		case GO_OUT:
			if (entranceExit == NotUseAtr.USE)
				return Optional.of(ChangeClockAtr.START_OF_SUPPORT);
			return Optional.of(ChangeClockAtr.GO_OUT);

		case RETURN:
			if (entranceExit == NotUseAtr.USE)
				return Optional.of(ChangeClockAtr.END_OF_SUPPORT);
			return Optional.of(ChangeClockAtr.RETURN);

		case WORK_TEMPORARY:
			return Optional.of(ChangeClockAtr.TEMPORARY_WORK);

		case RETURN_START:
			return Optional.of(ChangeClockAtr.START_OF_SUPPORT);

		case GO_EN:
			return Optional.of(ChangeClockAtr.END_OF_SUPPORT);

		case WORK_ENTRANCE:
		case WORK_HALF_ENTRANCE:
		case WORK_FLEX_ENTRANCE:
			return Optional.of(ChangeClockAtr.GOING_TO_WORK);

		case VACATION_ENTRANCE:
		case EARLY_ENTRANCE:
			return Optional.of(ChangeClockAtr.START_OF_SUPPORT);

		case TEMPORARY_ENTRANCE:
			return Optional.of(ChangeClockAtr.TEMPORARY_WORK);
			
		case RETIRED_TEMPORARY:
			return Optional.of(ChangeClockAtr.TEMPORARY_LEAVING);

		default:
			return Optional.empty();
		}
	}

	@Override
	public Optional<GoingOutReason> getOutReasonWhenReplace() {
		if (this.outPlaceConvert.getReplace() == NotUseAtr.USE) {
			return this.outPlaceConvert.getGoOutReason();
		}
		
		return Optional.empty();
	}
}
