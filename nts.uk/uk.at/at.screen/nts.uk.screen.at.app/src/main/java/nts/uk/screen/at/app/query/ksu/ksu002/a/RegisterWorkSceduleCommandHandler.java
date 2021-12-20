package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.task.AsyncTaskInfo;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExecutionInfor;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.RegisWorkScheduleCommandHandler;
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
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterWorkSceduleCommandHandler<T> {

	@Inject
	private RegisWorkScheduleCommandHandler<TimeWithDayAttr> regisWorkSchedule;

	public ExecutionInfor handle(RegisterWorkScheduleInputCommand param) {
		String sid = param.sid;
		String executeId = IdentifierUtil.randomUniqueId();

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
		
		AsyncTaskInfo taskInfor = regisWorkSchedule.handle(commands);
		return ExecutionInfor.builder()
                .taskInfor(taskInfor)
                .executeId(executeId)
                .build();
	}
}
