package nts.uk.screen.at.app.kdw013.a;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class StartProcessDto {
	
	// 工数入力を起動する
	private StartManHourInputResultDto startManHourInputResultDto;
	
	// 参照可能職場・社員を取得する
	private GetRefWorkplaceAndEmployeeResultDto refWorkplaceAndEmployeeDto;
}
