package nts.uk.screen.com.app.command.cmm029;

import java.util.List;

import lombok.Value;
import nts.uk.screen.com.app.find.cmm029.DisplayDataDto;

@Value
public class RegisterFunctionSettingCommand {

	/**
	 * <<List>> 機能の選択の表示データDTO
	 */
	private List<DisplayDataDto> datas;
}
