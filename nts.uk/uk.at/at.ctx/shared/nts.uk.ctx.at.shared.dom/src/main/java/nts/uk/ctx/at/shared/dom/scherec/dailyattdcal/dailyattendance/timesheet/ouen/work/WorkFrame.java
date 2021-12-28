package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work;

import lombok.Getter;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/** 作業枠 仮実装。ドメインが確定したら削除する。 */
@Getter
public class WorkFrame {
	/** 作業枠NO */
	private WorkFrameNo frameNo;
	/** 利用する */
	private NotUseAtr useAtr;
	/** 紐付けする */
	private NotUseAtr linkAtr;
	
	public WorkFrame(WorkFrameNo frameNo, NotUseAtr useAtr, NotUseAtr linkAtr) {
		this.frameNo = frameNo;
		this.useAtr = useAtr;
		this.linkAtr = linkAtr;
	}
}
