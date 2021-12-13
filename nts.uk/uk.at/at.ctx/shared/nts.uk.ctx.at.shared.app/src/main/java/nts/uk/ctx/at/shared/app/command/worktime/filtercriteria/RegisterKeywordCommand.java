package nts.uk.ctx.at.shared.app.command.worktime.filtercriteria;

import java.util.List;

import lombok.Value;

@Value
public class RegisterKeywordCommand {

	/**
	 * 絞り込みキーワード一覧
	 */
	private List<WorkHoursFilterConditionCommand> conditions;
}
