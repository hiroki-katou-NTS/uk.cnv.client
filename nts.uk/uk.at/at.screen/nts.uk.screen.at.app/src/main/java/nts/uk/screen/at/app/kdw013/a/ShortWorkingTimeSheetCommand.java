package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

@AllArgsConstructor
@Getter
public class ShortWorkingTimeSheetCommand {
	/** 短時間勤務枠NO: 短時間勤務枠NO */
	private int shortWorkTimeFrameNo;

	/** 育児介護区分: 育児介護区分 */
	private int childCareAttr;

	/** 開始: 時刻(日区分付き) */
	private int startTime;

	/** 終了: 時刻(日区分付き) */
	private int endTime;

	public ShortWorkingTimeSheet toDomain() {

		return new ShortWorkingTimeSheet(
				new ShortWorkTimFrameNo(this.getShortWorkTimeFrameNo()),
				EnumAdaptor.valueOf(this.getChildCareAttr(), ChildCareAtr.class),
				EnumAdaptor.valueOf(this.getStartTime(), TimeWithDayAttr.class),
				EnumAdaptor.valueOf(this.getEndTime(), TimeWithDayAttr.class));
	}
}
