package nts.uk.ctx.at.schedule.dom.calendar;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UseSet {
	workingDay(0),
	nonWorkingDay_inlaw(1),
	nonWorkingDay_Outrage(2);
	
	public final int value;
	
}
