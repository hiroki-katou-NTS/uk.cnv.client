package nts.uk.ctx.at.record.app.find.dailyperformanceprocessing.creationprocess;

import lombok.Value;
import nts.arc.time.GeneralDate;

@Value
public class CreateFutureDayCheckParam {

	/**
	 * 対象期間終了日
	 */
	private GeneralDate endDate;
}
