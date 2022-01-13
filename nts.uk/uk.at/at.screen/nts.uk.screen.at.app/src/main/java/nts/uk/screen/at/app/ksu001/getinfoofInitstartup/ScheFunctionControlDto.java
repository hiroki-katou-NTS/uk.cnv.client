package nts.uk.screen.at.app.ksu001.getinfoofInitstartup;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.schedule.dom.displaysetting.functioncontrol.ScheFunctionControl;
/**
 * スケジュール修正の機能制御
 * @author hoangnd
 *
 */

@AllArgsConstructor
@NoArgsConstructor
public class ScheFunctionControlDto {

	// 時刻修正できる勤務形態
	public List<Integer> changeableWorks;
	
	// 実績表示できるか
	public boolean isDisplayActual;
	
	// 表示可能勤務種類制御
	public int displayWorkTypeControl;
	
	// 表示可能勤務種類リスト
	public List<String> displayableWorkTypeCodeList;
	
	public static ScheFunctionControlDto fromDomain(ScheFunctionControl domain) {
		return new ScheFunctionControlDto(
				domain.getChangeableWorks()
					  .stream()
					  .map(x -> x.value)
					  .collect(Collectors.toList()),
				domain.isDisplayActual(),	  
				domain.getDisplayWorkTypeControl().value,
				domain.getDisplayableWorkTypeCodeList()
					  .stream()
					  .map(x -> x.v())
					  .collect(Collectors.toList())
				);
	}
}
