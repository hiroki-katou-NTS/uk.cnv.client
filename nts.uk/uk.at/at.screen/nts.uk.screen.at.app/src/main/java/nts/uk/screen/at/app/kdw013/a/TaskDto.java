package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.screen.at.app.kdw013.c.TaskDisplayInfoDto;
import nts.uk.screen.at.app.kmt009.ExternalCooperationInfoDto;

/**
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@Getter
public class TaskDto {
	// コード
    private String code;

    // 作業枠NO
    private Integer taskFrameNo;

    // 外部連携情報
    private ExternalCooperationInfoDto cooperationInfo;

    // 子作業一覧
    private List<String> childTaskList;

    // 有効期限
    private GeneralDate expirationStartDate;

    private GeneralDate expirationEndDate;

    // 表示情報 : 作業表示情報
    private TaskDisplayInfoDto displayInfo;
    
    public static TaskDto toDto(Task task) {
    	return new TaskDto(task.getCode().v(), task.getTaskFrameNo().v(),
				new ExternalCooperationInfoDto(task.getCooperationInfo().getExternalCode1().map(a -> a.toString()).orElse(null),
						task.getCooperationInfo().getExternalCode2().map(a -> a.v()).orElse(null),
						task.getCooperationInfo().getExternalCode3().map(a -> a.v()).orElse(null),
						task.getCooperationInfo().getExternalCode4().map(a -> a.v()).orElse(null),
						task.getCooperationInfo().getExternalCode5().map(a -> a.v()).orElse(null)),
				task.getChildTaskList().stream().map(n -> n.v()).collect(Collectors.toList()),
				task.getExpirationDate().start(), task.getExpirationDate().end(),
				new TaskDisplayInfoDto(task.getDisplayInfo().getTaskName().v(), task.getDisplayInfo().getTaskAbName().v(),
						task.getDisplayInfo().getColor().map(a -> a.v()).orElse(null),
						task.getDisplayInfo().getTaskNote().map(a -> a.v()).orElse(null)));
    }
    
}
