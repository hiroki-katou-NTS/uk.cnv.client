package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheFunctionControlDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheModifyAuthCtrlByPersonDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScheModifyAuthCtrlCommonDto;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IndividualDisplayControlDto {

	/**スケジュール修正共通の権限制御*/
	public List<ScheModifyAuthCtrlCommonDto> scheModifyAuthCtrlCommon;
	
	/**スケジュール修正個人別の権限制御*/
	public List<ScheModifyAuthCtrlByPersonDto> scheModifyAuthCtrlByPerson;
	
	/**スケジュール修正の機能制御*/
	public ScheFunctionControlDto scheFunctionControl;
	
}
