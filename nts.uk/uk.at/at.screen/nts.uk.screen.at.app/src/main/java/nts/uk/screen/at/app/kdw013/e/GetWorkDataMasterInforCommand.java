package nts.uk.screen.at.app.kdw013.e;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class GetWorkDataMasterInforCommand {
	
	/** 社員ID*/
	public String sId;
	
	/** 基準日*/
	public GeneralDate refDate;
	
	/** 作業コード1*/
	public String taskCode1;
	
	/** 作業コード2*/
	public String taskCode2;
	
	/** 作業コード3*/
	public String taskCode3;
	
	/** 作業コード4*/
	public String taskCode4;
	
	/** 作業コード5*/
	public String taskCode5;
	
}
