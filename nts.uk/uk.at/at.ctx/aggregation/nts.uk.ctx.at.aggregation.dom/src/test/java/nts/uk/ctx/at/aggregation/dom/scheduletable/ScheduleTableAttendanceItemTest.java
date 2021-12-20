package nts.uk.ctx.at.aggregation.dom.scheduletable;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.Test;

import lombok.val;

public class ScheduleTableAttendanceItemTest {
	
	@SuppressWarnings("serial")
	@Test
	public void testGetLaborCostTimeTypes() {
		
		val result = ScheduleTableAttendanceItem.getLaborCostTimeTypes();
		
		assertThat(result).containsExactlyInAnyOrderEntriesOf(
				
				new HashMap<ScheduleTableAttendanceItem, Integer>() {{
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_1, 1);
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_2, 2);
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_3, 3);
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_4, 4);
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_5, 5);
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_6, 6);
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_7, 7);
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_8, 8);
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_9, 9);
					put(ScheduleTableAttendanceItem.LABOR_COST_TIME_10, 10);
				}}
		);
	}

}
