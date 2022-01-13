package nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.attendance.AttendanceName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.DailyAttendanceItemRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author xuannt
 * 
 *
 */
@Stateless
public class UpdateDailyAttendanceItemCommandHandler extends CommandHandler<UpdateDailyAttendanceItemCommand> {

	@Inject
	DailyAttendanceItemRepository dailyAttendanceItemRepository;
	@Override
	protected void handle(CommandHandlerContext<UpdateDailyAttendanceItemCommand> context) {
		String companyId = AppContexts.user().companyId();
		UpdateDailyAttendanceItemCommand command = context.getCommand();
		int attendanceItemId = command.getAttendanceItemId();
		String displayName  = command.getDisplayName();
		int nameLineFeedPosition = command.getNameLineFeedPosition();
		
		Optional<DailyAttendanceItem> data = this.dailyAttendanceItemRepository
											 .getDailyAttendanceItem(companyId,attendanceItemId);
		if(!data.isPresent())
			return;
		DailyAttendanceItem dailyAttendanceItem = data.get();
		this.dailyAttendanceItemRepository.update(new DailyAttendanceItem(
												  dailyAttendanceItem.getCompanyId(),
												  dailyAttendanceItem.getAttendanceItemId(),
												  dailyAttendanceItem.getAttendanceName(),
												  dailyAttendanceItem.getDisplayNumber(),
												  dailyAttendanceItem.getUserCanUpdateAtr(),
												  dailyAttendanceItem.getDailyAttendanceAtr(),
												  nameLineFeedPosition,
												  dailyAttendanceItem.getMasterType(),
												  dailyAttendanceItem.getPrimitiveValue(),
												  displayName == null ? dailyAttendanceItem.getDisplayName() : Optional.ofNullable(new AttendanceName(displayName))));	
	}
	
	
}
