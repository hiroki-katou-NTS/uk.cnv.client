package nts.uk.screen.at.ws.kdw.kdw013;

import lombok.Getter;
import lombok.Setter;
import nts.uk.screen.at.app.kdw013.a.GetRefWorkplaceAndEmployeeDto;
import nts.uk.screen.at.app.kdw013.a.StartManHourInputResultDto;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class ProcessInitialStartDto {
	// 工数入力を起動する
	private StartManHourInputResultDto startManHourInputDto;

	// 参照可能職場・社員を取得する
	private GetRefWorkplaceAndEmployeeDto refWorkplaceAndEmployeeDto;
}
