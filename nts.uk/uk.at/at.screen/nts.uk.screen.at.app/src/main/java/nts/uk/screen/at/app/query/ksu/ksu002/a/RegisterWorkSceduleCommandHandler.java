package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.RegisWorkScheduleCommandHandler;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.ResultRegisWorkSchedule;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.WorkInformationDto;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.WorkScheduleSaveCommand;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.RegisterWorkScheduleInputCommand;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * CM: 登録処理
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU002_個人スケジュール修正(個人別).A:メイン画面.メニュー別OCD.登録処理
 * @author chungnt
 *
 */

@Stateless
public class RegisterWorkSceduleCommandHandler extends CommandHandlerWithResult<RegisterWorkScheduleInputCommand, ResultRegisWorkSchedule> {

	@Inject
	private RegisWorkScheduleCommandHandler<TimeWithDayAttr> regisWorkSchedule;

	@Override
	protected ResultRegisWorkSchedule handle(CommandHandlerContext<RegisterWorkScheduleInputCommand> context) {
		RegisterWorkScheduleInputCommand param = context.getCommand();
		String sid = param.sid;

		List<WorkScheduleSaveCommand<TimeWithDayAttr>> commands = param.registerDates.stream().map(m -> {
			Map<Integer, TimeWithDayAttr> map = new HashMap<Integer, TimeWithDayAttr>();

			if (m.getStart() != null) {
				map.put(31, new TimeWithDayAttr(m.getStart()));
			}

			if (m.getEnd() != null) {
				map.put(34, new TimeWithDayAttr(m.getEnd()));
			}
			
			WorkInformationDto wif = new WorkInformationDto(m.getWorkTypeCode(), m.getWorkTimeCode());

			return new WorkScheduleSaveCommand<TimeWithDayAttr>(sid, m.getDate(), wif, map, new ArrayList<>(), false);
		}).collect(Collectors.toList());
		
		
		return this.regisWorkSchedule.handle(commands);
	}
}
