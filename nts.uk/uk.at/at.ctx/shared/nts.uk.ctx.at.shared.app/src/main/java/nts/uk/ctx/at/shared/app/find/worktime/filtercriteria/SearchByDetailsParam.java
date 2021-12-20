package nts.uk.ctx.at.shared.app.find.worktime.filtercriteria;

import java.util.List;

import lombok.Value;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeDto;

@Value
public class SearchByDetailsParam {

	/**
	 * 開始時刻
	 */
	private Integer startTime;
	
	/**
	 * 終了時刻
	 */
	private Integer endTime;
	
	/**
	 * コード名称
	 */
	private String searchQuery;
	
	/**
	 * 選択可能な「就業時間帯の設定」 
	 */
	private List<WorkTimeDto> workTimes;
}
