package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;

/**
 * @author thanh_nx
 *
 *         申請反映実行情報
 */
@AllArgsConstructor
@Getter
public class AppReflectExecInfo {

	// 再反映
	private final boolean reReflect;

	// 実行ID
	private final String execId;

	// 反映時刻
	private final GeneralDateTime reflectionTime;

	// [1] 再反映により先に実行した反映前の勤怠を取得する
	public Optional<AttendanceBeforeApplicationReflect> acquireAttBeforeRereflect(Require require, String sid,
			GeneralDate date, ScheduleRecordClassifi classification, Integer itemId) {
		// $申請反映履歴
		List<ApplicationReflectHistory> lstHist = require.getHistWithSidDate(sid, date, classification);
		return lstHist.stream()
				.filter(x -> x.getAppExecInfo().isReReflect()
						&& x.getAppExecInfo().getReflectionTime().before(this.reflectionTime)
						&& x.getAppExecInfo().getExecId().equals(execId)
						&& x.getBeforeSpecifiAttReflected(itemId).isPresent())
				.sorted((a, b) -> a.getAppExecInfo().getReflectionTime()
						.compareTo(b.getAppExecInfo().getReflectionTime()))
				.findFirst().map(x -> x.getBeforeSpecifiAttReflected(itemId).get());
	}

	public static interface Require {
		// [R-1] 申請反映履歴を取得する
		// ApplicationReflectHistoryRepo
		public List<ApplicationReflectHistory> getHistWithSidDate(String sid, GeneralDate date,
				ScheduleRecordClassifi classification);
	}
}
