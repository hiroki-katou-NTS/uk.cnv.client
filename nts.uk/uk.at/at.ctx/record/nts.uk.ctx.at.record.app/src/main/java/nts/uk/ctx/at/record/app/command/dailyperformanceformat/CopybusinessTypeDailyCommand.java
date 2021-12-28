package nts.uk.ctx.at.record.app.command.dailyperformanceformat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * @author dungbn
 *
 */
@AllArgsConstructor
@Data
public class CopybusinessTypeDailyCommand {
	
	private String businessTypeCode;
	
	private List<String> listBusinessTypeCode;
}
