package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;

import lombok.Data;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameSetting;

/**
 * 
 * @author tutt
 *
 */
@Data
@AllArgsConstructor
public class TaskFrameSettingDto {
	private int frameNo;

	private String frameName;

	private int useAtr;

	public static TaskFrameSettingDto toDto(TaskFrameSetting setting) {
		return new TaskFrameSettingDto(setting.getTaskFrameNo().v(), setting.getTaskFrameName().v(),
				setting.getUseAtr().value);
	}
}
