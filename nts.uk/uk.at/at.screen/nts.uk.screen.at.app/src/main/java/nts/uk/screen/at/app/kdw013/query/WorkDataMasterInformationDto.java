package nts.uk.screen.at.app.kdw013.query;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.worklocation.WorkLocationDto;
import nts.uk.screen.at.app.kdw006.j.DailyAttendanceItemDto;

/**
 * @author thanhpv
 * @description output for 作業データマスタ情報を取得する
 */
@NoArgsConstructor
@Getter
public class WorkDataMasterInformationDto {

	//List<作業マスタ情報>
	public List<FrameNoVsTaskFrameNosDto> frameNoVsTaskFrameNos;
	
	//勤務場所一覧：List<勤務場所>
	public List<WorkLocationDto> workLocation;
	
	//List<作業補足情報の選択肢詳細>
	public List<TaskSupInfoChoicesDetailDto> taskSupInfoChoicesDetails;
	
	//List<工数実績項目>
	public List<ManHourRecordItemDto> manHourRecordItems;
	
	//List<日次の勤怠項目>
	public List<DailyAttendanceItemDto> attendanceItems;
	
	//List<工数実績項目と勤怠項目の紐付け>
	public List<ManHourRecordAndAttendanceItemLinkDto> manHourRecordAndAttendanceItemLink; 
	
	public WorkDataMasterInformationDto(List<FrameNoVsTaskFrameNosDto> frameNoVsTaskFrameNos,
			List<WorkLocationDto> workLocation, List<TaskSupInfoChoicesDetailDto> taskSupInfoChoicesDetails,
			List<ManHourRecordItemDto> manHourRecordItems, List<DailyAttendanceItemDto> attendanceItems,
			List<ManHourRecordAndAttendanceItemLinkDto> manHourRecordAndAttendanceItemLink) {
		super();
		this.frameNoVsTaskFrameNos = frameNoVsTaskFrameNos;
		this.workLocation = workLocation;
		this.taskSupInfoChoicesDetails = taskSupInfoChoicesDetails;
		this.manHourRecordItems = manHourRecordItems;
		this.attendanceItems = attendanceItems;
		this.manHourRecordAndAttendanceItemLink = manHourRecordAndAttendanceItemLink;
	}

}
