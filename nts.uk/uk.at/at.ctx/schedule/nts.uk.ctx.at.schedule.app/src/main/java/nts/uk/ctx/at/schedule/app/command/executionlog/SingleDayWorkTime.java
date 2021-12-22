package nts.uk.ctx.at.schedule.app.command.executionlog;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SingleDayWorkTime {
	
	EXIST(0),
	
	EMPTY(1),
	
	ERROR(2);
	
	public final int value;

}