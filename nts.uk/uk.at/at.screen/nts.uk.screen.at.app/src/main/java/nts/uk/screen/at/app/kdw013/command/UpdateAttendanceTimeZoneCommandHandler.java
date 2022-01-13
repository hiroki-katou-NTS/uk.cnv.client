package nts.uk.screen.at.app.kdw013.command;

import java.util.Arrays;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;

/**
 * 
 * @author sonnlb
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.App.応援作業別勤怠時間帯を更新する.応援作業別勤怠時間帯を更新する
 */
@Stateless
public class UpdateAttendanceTimeZoneCommandHandler extends CommandHandler<UpdateAttendanceTimeZoneCommand> {
	
	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenSheetRepo;

	@Inject
	private OuenWorkTimeOfDailyRepo ouenTimeRepo;

	/**
	 * 更新する
	 */
	@Override
	protected void handle(CommandHandlerContext<UpdateAttendanceTimeZoneCommand> context) {
		UpdateAttendanceTimeZoneCommand cmd = context.getCommand();
		
		// 1. Get(社員ID,年月日)
		OuenWorkTimeSheetOfDaily timeSheet =  this.ouenSheetRepo.find(cmd.getEmployeeId(), cmd.getRefDate());
		if (timeSheet != null) {
			// 2. 応援時間帯．応援勤務枠No == INPUT「日別勤怠の応援作業時間帯．応援勤務枠No」
			timeSheet.getOuenTimeSheet().forEach(sheet -> {
				cmd.getOuenSheet().stream().filter(inputSheet -> inputSheet.getWorkNo().equals(sheet.getWorkNo().v()))
						.findFirst().ifPresent(inputSheet -> {
							sheet.update(inputSheet);
						});
			});
			//3. persist
			this.ouenSheetRepo.persist(Arrays.asList(timeSheet));
		}
		
		// 4. Get(社員ID,年月日)
		this.ouenTimeRepo.find(cmd.getEmployeeId(), cmd.getRefDate()).ifPresent(x -> {
			// 5. 応援時間．応援勤務枠No == INPUT「日別勤怠の応援作業時間．応援勤務枠No」
			x.getOuenTimes().forEach(time -> {
				cmd.getOuenTimes().stream().filter(inputTime -> inputTime.getWorkNo().equals(time.getWorkNo().v()))
						.findFirst().ifPresent(inputTime -> {
							time.update(inputTime);
						});
			});
			// 6 . persist
			this.ouenTimeRepo.update(x);
		});
		
		
	}

}
