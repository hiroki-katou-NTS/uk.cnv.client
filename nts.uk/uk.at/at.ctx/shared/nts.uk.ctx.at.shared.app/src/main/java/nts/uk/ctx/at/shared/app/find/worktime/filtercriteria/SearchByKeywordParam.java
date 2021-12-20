package nts.uk.ctx.at.shared.app.find.worktime.filtercriteria;

import java.util.List;

import lombok.Value;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeDto;

@Value
public class SearchByKeywordParam {

	/**
	 * 絞り込みキーワード
	 */
	private String keyword;
	
	/**
	 * 選択可能な「就業時間帯の設定」
	 */
	private List<WorkTimeDto> workTimes;
}
