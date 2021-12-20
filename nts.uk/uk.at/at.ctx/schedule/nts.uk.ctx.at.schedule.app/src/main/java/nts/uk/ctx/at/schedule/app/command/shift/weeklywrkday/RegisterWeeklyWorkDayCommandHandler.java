package nts.uk.ctx.at.schedule.app.command.shift.weeklywrkday;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.schedule.dom.shift.pattern.work.WeeklyWorkSettingRepository;
import nts.uk.ctx.at.schedule.dom.shift.weeklywrkday.WeeklyWorkDayPattern;
import nts.uk.ctx.at.schedule.dom.shift.weeklywrkday.WorkdayPatternItem;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.シフト管理.週間勤務設定.App.週間勤務設定を更新する.週間勤務設定を更新する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterWeeklyWorkDayCommandHandler extends CommandHandler<WeeklyWorkDayCommand> {

	@Inject
	private WeeklyWorkSettingRepository weeklyWorkSettingRepository;

	@Override
	protected void handle(CommandHandlerContext<WeeklyWorkDayCommand> context) {
		String cid = AppContexts.user().companyId();
		WeeklyWorkDayCommand command = context.getCommand();
		// 1. get(ログイン会社ID)
		WeeklyWorkDayPattern domain = this.weeklyWorkSettingRepository.getWeeklyWorkDayPatternByCompanyId(cid);
		// 2. set()
		List<WorkdayPatternItem> patternItems = command.getWorkdayPatternItemDtoList().stream()
				.map(WorkdayPatternItemCommand::toDomain).collect(Collectors.toList());
		WeeklyWorkDayPattern newDomain = WeeklyWorkDayPattern.weeklyWorkDayPattern(new CompanyId(cid), patternItems);
		// 3. persist()
		if (domain != null) {
			this.weeklyWorkSettingRepository.updateWeeklyWorkDayPattern(newDomain);
		} else {
			this.weeklyWorkSettingRepository.addWeeklyWorkDayPattern(newDomain);
		}
	}
}
