package nts.uk.ctx.at.request.app.find.overtime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OvertimeCheckResultDto {
	private int ErrorCode;
	private int AttendanceId;
	private int FrameNo;
	
}
