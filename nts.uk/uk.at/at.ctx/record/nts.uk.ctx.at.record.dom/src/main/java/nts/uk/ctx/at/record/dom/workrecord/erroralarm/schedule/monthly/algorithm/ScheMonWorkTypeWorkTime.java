package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.algorithm;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;

/**
 * Output of 勤務種類コードを取得
 *
 */
@Data
@Builder
public class ScheMonWorkTypeWorkTime {
	/**
	 * 勤務種類コード
	 */
	private String workTypeCode;
	
	/**
	 * Optional＜就業時間帯コード＞　＃115443
	 */
	private Optional<String> workTimeCode;
}
