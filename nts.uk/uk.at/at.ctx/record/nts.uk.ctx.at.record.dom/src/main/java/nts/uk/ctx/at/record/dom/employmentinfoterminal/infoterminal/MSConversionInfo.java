package nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.LeaveCategory;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;

/**
 * @author thanh_nx
 *
 *         ｍsの変換情報
 */
@AllArgsConstructor
@Getter
public class MSConversionInfo implements StampInfoConversion {

	// ｍsの変換情報
	private List<MSConversion> lstMSConversion;

	// [1] NRから変換する
	@Override
	public Optional<ChangeClockArt> convertFromNR(LeaveCategory leavCategory) {
		val stampClassifi = leavCategory.convertStampClassifi();
		// <>$打刻分類.isPresent()
		if (!stampClassifi.isPresent())
			return Optional.empty();
		return Optional.of(getStampOutput(stampClassifi.get()).getTimeChangeClassifi());
	}

	// [２] NRから外出理由を変換する
	public Optional<GoingOutReason> convertReason(LeaveCategory leavCategory) {
		val stampClassifi = leavCategory.convertStampClassifi();
		// <>$打刻分類.isPresent()
		if (!stampClassifi.isPresent())
			return Optional.empty();
		return getStampOutput(stampClassifi.get()).getOutReason();
	}

	// [pvt-1] 打刻分類から時刻変更区分と外出理由を取得する
	private StampTypeOutput getStampOutput(StampClassifi stampClassifi) {
		// ＄打刻先
		Optional<StampDestination> stampDes = this.getLstMSConversion().stream()
				.filter(x -> x.getStampClassifi() == stampClassifi).map(x -> x.getStampDestination()).findFirst();
		
		switch (stampDes.get()) {
		case ATTENDANCE:
			return new StampTypeOutput(ChangeClockArt.GOING_TO_WORK, Optional.empty());
		case START_IN:
			return new StampTypeOutput(ChangeClockArt.OVER_TIME, Optional.empty());
		case SUPPORT_ATTENDANCE:
			return new StampTypeOutput(ChangeClockArt.GOING_TO_WORK, Optional.empty());
		case LEAV:
			return new StampTypeOutput(ChangeClockArt.WORKING_OUT, Optional.empty());
		case START_OUT:
			return new StampTypeOutput(ChangeClockArt.BRARK, Optional.empty());
		case PRIVATE_OUT:
			return new StampTypeOutput(ChangeClockArt.GO_OUT, Optional.of(GoingOutReason.PRIVATE));
		case PUBLIC_OUT:
			return new StampTypeOutput(ChangeClockArt.GO_OUT, Optional.of(GoingOutReason.PUBLIC));
		case PAID_OUT:
			return new StampTypeOutput(ChangeClockArt.GO_OUT, Optional.of(GoingOutReason.COMPENSATION));
		case UNION_OUT:
			return new StampTypeOutput(ChangeClockArt.GO_OUT, Optional.of(GoingOutReason.UNION));
		case IN_SUPPORT_START:
			return new StampTypeOutput(ChangeClockArt.START_OF_SUPPORT, Optional.empty());
		case OUT_SUPPORT_END:
			return new StampTypeOutput(ChangeClockArt.END_OF_SUPPORT, Optional.empty());
		case GO_OUT:
			return new StampTypeOutput(ChangeClockArt.RETURN, Optional.empty());

		default:
			return null;
		}
	}

}
