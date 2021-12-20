package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

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
public class RegisterWorkContentCommand {
	
	/** 対象者 */
	private String employeeId;
	
	/** 編集状態<Enum.日別勤怠の編集状態> */
	private int editStateSetting;
	
	/** List<年月日,List<作業詳細>> */
	private List<WorkDetailCommand> workDetails;
	
	/** 確認モード */
	private int mode;
	
	/** 変更対象日 */
	private GeneralDate changedDate;
	
}
