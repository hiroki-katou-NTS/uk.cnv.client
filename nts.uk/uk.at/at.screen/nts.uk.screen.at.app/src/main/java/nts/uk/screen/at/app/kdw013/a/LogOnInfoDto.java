package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.LogOnInfo;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogOnInfoDto {
	/** 勤務NO: 勤務NO */
	private Integer workNo;

	/** ログオフ: 勤怠打刻 */
	private Integer logOff;

	/** ログオン: 勤怠打刻 */
	private Integer logOn;

	public static LogOnInfoDto fromDomain(LogOnInfo domain) {

		return new LogOnInfoDto(domain.getWorkNo().v(), domain.getLogOff().map(x -> x.v()).orElse(null),
				domain.getLogOn().map(x -> x.v()).orElse(null));
	}
}
