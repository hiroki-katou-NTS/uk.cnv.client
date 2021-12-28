package nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.attendance.AttendanceName;
import nts.uk.ctx.at.shared.dom.attendance.UseSetting;
import nts.uk.ctx.at.shared.dom.monthlyattditem.DisplayMonthResultsMethod;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItem;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemAtr;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemGetMemento;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.PrimitiveValueOfAttendanceItem;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author xuannt
 *
 */
@Stateless
public class UpdateMonthlyAttendanceItemCommandHandler extends CommandHandler<UpdateMonthlyAttendanceItemCommand> {

	@Inject
	MonthlyAttendanceItemRepository monthlyAttendanceItemRepository;
	
	@Override
	protected void handle(CommandHandlerContext<UpdateMonthlyAttendanceItemCommand> context) {
		String companyId = AppContexts.user().companyId();
		UpdateMonthlyAttendanceItemCommand command = context.getCommand();
		int attendanceItemId = command.getAttendanceItemId();
		String displayName  = command.getDisplayName();		
		int nameLineFeedPosition = command.getNameLineFeedPosition();
		
		Optional<MonthlyAttendanceItem> data = this.monthlyAttendanceItemRepository
												    .findByAttendanceItemId(companyId, attendanceItemId);
		if(!data.isPresent())
			return;
		MonthlyAttendanceItem monthlyAttendanceItem = data.get();
		
		this.monthlyAttendanceItemRepository.update(new MonthlyAttendanceItem(new MonthlyAttendanceItemGetMemento() {
			
			@Override
			public UseSetting getUserCanUpdateAtr() {
				return monthlyAttendanceItem.getUserCanUpdateAtr();
			}
			
			@Override
			public DisplayMonthResultsMethod getTwoMonthlyDisplay() {
				return monthlyAttendanceItem.getTwoMonthlyDisplay();
			}
			
			@Override
			public Optional<PrimitiveValueOfAttendanceItem> getPrimitiveValue() {
				return monthlyAttendanceItem.getPrimitiveValue();
			}
			
			@Override
			public int getNameLineFeedPosition() {
				return nameLineFeedPosition;
			}
			
			@Override
			public MonthlyAttendanceItemAtr getMonthlyAttendanceAtr() {
				return monthlyAttendanceItem.getMonthlyAttendanceAtr();
			}
			
			@Override
			public int getDisplayNumber() {
				return monthlyAttendanceItem.getDisplayNumber();
			}
			
			@Override
			public Optional<AttendanceName> getDisplayName() {
				return (null == displayName) ? monthlyAttendanceItem.getDisplayName() : ("".equals(displayName.trim()) ? Optional.empty() : Optional.ofNullable(new AttendanceName(displayName)));
			}
			
			@Override
			public String getCompanyId() {
				return monthlyAttendanceItem.getCompanyId();
			}
			
			@Override
			public AttendanceName getAttendanceName() {
				return monthlyAttendanceItem.getAttendanceName();
			}
			
			@Override
			public int getAttendanceItemId() {
				return monthlyAttendanceItem.getAttendanceItemId();
			}
		}));
	}

}
