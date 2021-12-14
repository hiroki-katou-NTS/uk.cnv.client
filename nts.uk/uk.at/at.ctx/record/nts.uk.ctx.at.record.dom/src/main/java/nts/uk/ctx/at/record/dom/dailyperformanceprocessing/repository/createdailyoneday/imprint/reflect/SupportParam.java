package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflect;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupportParam {
	// 時刻優先フラグ：Boolean
	private boolean TimePriorityFlag;
	// 開始区分
	private StartAtr startAtr;
	// 勤怠打刻：勤怠打刻 - 出退勤。出勤。打刻。時刻
	private WorkTimeInformation timeDay;
	// 場所コード＜Optional＞
	private Optional<WorkLocationCD> locationCode;
	// 職場ID＜Optional＞
	private Optional<String> workplaceId;
}
