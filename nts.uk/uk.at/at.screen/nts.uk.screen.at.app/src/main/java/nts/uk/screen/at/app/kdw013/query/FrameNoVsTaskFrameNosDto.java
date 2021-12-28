package nts.uk.screen.at.app.kdw013.query;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.kdw013.a.TaskDto;
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class FrameNoVsTaskFrameNosDto {
	//応援勤務枠No
	public Integer frameNo;
	//作業1一覧：List<作業>
	public List<TaskDto> taskFrameNo1;
	
	//作業2一覧：List<作業>
	public List<TaskDto> taskFrameNo2;
	
	//作業3一覧：List<作業>
	public List<TaskDto> taskFrameNo3;
	
	//作業4一覧：List<作業>
	public List<TaskDto> taskFrameNo4;
	
	//作業5一覧：List<作業>
	public List<TaskDto> taskFrameNo5;
}
