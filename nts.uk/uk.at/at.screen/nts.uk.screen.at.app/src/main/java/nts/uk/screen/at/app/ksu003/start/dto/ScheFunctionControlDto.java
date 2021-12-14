package nts.uk.screen.at.app.ksu003.start.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Value;
import nts.uk.ctx.at.schedule.dom.displaysetting.functioncontrol.ScheFunctionControl;

@Value
public class ScheFunctionControlDto {
	/** 時刻修正できる勤務形態 */
	private List<Integer> changeableWorks;

	/** 実績表示できるか */
	private boolean isDisplayActual;
	
	public static ScheFunctionControlDto convert(Optional<ScheFunctionControl> scheFunc) {
		List<Integer> changeableWorks = new ArrayList<>();
		if(scheFunc.isPresent()) {
			changeableWorks = scheFunc.get().getChangeableWorks().stream().map(x-> x.value).collect(Collectors.toList());
			return new ScheFunctionControlDto(changeableWorks, scheFunc.get().isDisplayActual());
		}
		return null;
	}
}
