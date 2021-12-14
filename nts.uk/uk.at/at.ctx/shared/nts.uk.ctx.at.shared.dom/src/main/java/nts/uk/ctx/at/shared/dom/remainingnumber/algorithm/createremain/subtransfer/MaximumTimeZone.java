package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;

/**
 * @author thanh_nx
 *
 *         最大時間帯
 */
@AllArgsConstructor
@Getter
@Setter
public class MaximumTimeZone {

	// Map<int,計算時間帯>
	 private List<Pair<OverTimeFrameNo, TimeSpanForDailyCalc>> timeSpan;

	public MaximumTimeZone() {
		this.timeSpan = new ArrayList<>();
	}

}
