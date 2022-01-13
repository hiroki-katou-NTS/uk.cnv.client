package nts.uk.screen.at.app.kdw013.query;

import lombok.Data;

@Data
public class WorkCodeFrameNoParamDto {
	//応援勤務枠No
	public Integer frameNo;
	//Optional<作業コード1>
	public String workCode1; 
	//Optional<作業コード2>
	public String workCode2;
	//Optional<作業コード3>
	public String workCode3;
	//Optional<作業コード4>
	public String workCode4;
	//Optional<作業コード5>
	public String workCode5;
}
