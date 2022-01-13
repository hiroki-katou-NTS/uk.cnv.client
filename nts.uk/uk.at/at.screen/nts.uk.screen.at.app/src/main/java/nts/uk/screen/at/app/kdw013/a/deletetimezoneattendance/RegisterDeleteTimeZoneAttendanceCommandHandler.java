package nts.uk.screen.at.app.kdw013.a.deletetimezoneattendance;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.DeleteAttendancesByTimezone;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.DeleteAttendancesByTimezoneRepo;

/**
 * 
 * @author sonnlb
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力.App.時間帯別勤怠の削除を登録する.時間帯別勤怠の削除を登録する
 *
 */
@Stateless
public class RegisterDeleteTimeZoneAttendanceCommandHandler
		extends CommandHandler<RegisterDeleteTimeZoneAttendanceCommand> {
	
	@Inject
	private DeleteAttendancesByTimezoneRepo repo;

	@Override
	protected void handle(CommandHandlerContext<RegisterDeleteTimeZoneAttendanceCommand> context) {

		RegisterDeleteTimeZoneAttendanceCommand command = context.getCommand();

		// 1. 複数日を削除する(時間帯別勤怠の削除リスト.社員IDの1個目,時間帯別勤怠の削除リスト.年月日)
		this.repo.deleteDays(command.getEmployeeId(),
				command.getDeleteList().stream().map(d -> d.getDate()).collect(Collectors.toList()));

		// 2. create(時間帯別勤怠の削除リスト)

		List<DeleteAttendancesByTimezone> domains = command.toDomain();
		// 3. 登録する(時間帯別勤怠の削除リスト)
		
		domains.forEach(domain -> {
			this.repo.register(domain);
		});
		
		// persist
	}

}
