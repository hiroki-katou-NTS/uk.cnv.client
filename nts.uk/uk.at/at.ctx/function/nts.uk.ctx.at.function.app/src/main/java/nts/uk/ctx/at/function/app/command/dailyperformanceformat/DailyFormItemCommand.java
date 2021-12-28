package nts.uk.ctx.at.function.app.command.dailyperformanceformat;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFomatDaily;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyFormItemCommand {

	private int attendanceItemId;
	
	private int displayOrder;
	
	private BigDecimal columnWidth;
	
	public static DailyFormItemCommand toDto(AuthorityFomatDaily authorityFomatDaily) {
		return new DailyFormItemCommand(
				authorityFomatDaily.getAttendanceItemId(),
				authorityFomatDaily.getDisplayOrder(),
				authorityFomatDaily.getColumnWidth());
	}
}
