package nts.uk.ctx.at.shared.dom.remainingnumber.common;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.LeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedNumber;

/**
 * 休暇残数シフトWORK
 * 
 * @author masaaki_jinno
 */
@Getter
public class RemNumShiftWork {

	/**
	 * 休暇付与残数データへの参照
	 */
	@Setter
	private LeaveGrantRemainingData refLeaveGrantRemainingData;

	/**
	 * コンストラクタ
	 * 
	 * @param aLeaveGrantRemainingData
	 */
	public RemNumShiftWork(LeaveGrantRemainingData aLeaveGrantRemainingData) {
		refLeaveGrantRemainingData = aLeaveGrantRemainingData;
	}

	/**
	 * 休暇残数を取得する
	 */
	public Optional<LeaveRemainingNumber> getLeaveRemainingNumber() {
		if (refLeaveGrantRemainingData.getDetails() == null) {
			return Optional.empty();
		}

		return Optional.of(refLeaveGrantRemainingData.getDetails().getRemainingNumber());
	}


	/**
	 * 休暇残数を全て消化する
	 */
	public void digestAll() {
		// 休暇残数をすべて消化する
		this.refLeaveGrantRemainingData.getDetails().digestAll();
	}
}
