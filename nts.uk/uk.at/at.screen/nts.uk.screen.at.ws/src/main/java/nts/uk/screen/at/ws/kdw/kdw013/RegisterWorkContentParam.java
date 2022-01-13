package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Getter
@Setter
public class RegisterWorkContentParam {

	/** 対象日 */
	private GeneralDate date;

	/** 対象者 */
	private String employeeId;
	
	/** 編集状態<Enum.日別勤怠の編集状態> */
	private int editStateSetting;
	
	/** List<年月日,List<作業詳細>> */
	private List<WorkDetailDto> workDetails;
	
}
