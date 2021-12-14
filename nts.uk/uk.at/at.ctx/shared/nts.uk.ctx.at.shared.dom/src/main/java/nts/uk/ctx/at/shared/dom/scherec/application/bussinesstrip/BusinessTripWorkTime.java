package nts.uk.ctx.at.shared.dom.scherec.application.bussinesstrip;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * @author thanh_nx
 *
 *         出張勤務時刻(反映用)
 */
@AllArgsConstructor
@Getter
public class BusinessTripWorkTime {

	// 勤務NO
	private WorkNo workNo;

	// 開始時刻
	private Optional<TimeWithDayAttr> startDate;

	// 終了時刻
	private Optional<TimeWithDayAttr> endDate;

}
