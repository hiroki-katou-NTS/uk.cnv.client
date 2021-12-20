package nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Value;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.app.find.workrule.shiftmaster.TargetOrgIdenInforDto;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.screen.at.app.kmt009.ExternalCooperationInfoDto;
import nts.uk.screen.at.app.kmt009.TaskDisplayInfoDto;

@Value
@Getter
public class TaskData {

	// コード
	public String code;

	// 作業枠NO
	public int taskFrameNo;
	// 外部連携情報
	public ExternalCooperationInfoDto externalCooperationInfoDto;
	// 子作業一覧
	public List<String> childTaskList;
	// 有効期限
	public DatePeriod expirationDate;
	// 表示情報 : 作業表示情報
	public TaskDisplayInfoDto taskDisplayInfoDto;

	public TaskData(String code, int taskFrameNo, ExternalCooperationInfoDto externalCooperationInfoDto,
			List<String> childTaskList, DatePeriod expirationDate, TaskDisplayInfoDto taskDisplayInfoDto) {
		super();
		this.code = code;
		this.taskFrameNo = taskFrameNo;
		this.externalCooperationInfoDto = externalCooperationInfoDto;
		this.childTaskList = childTaskList;
		this.expirationDate = expirationDate;
		this.taskDisplayInfoDto = taskDisplayInfoDto;
	}

	public static TaskData toDto(Task domain) {
		TaskDisplayInfoDto taskDisplayInfoDto = new TaskDisplayInfoDto(domain.getDisplayInfo().getTaskName().v(),
				domain.getDisplayInfo().getTaskAbName().v(),
				domain.getDisplayInfo().getColor().isPresent() ? domain.getDisplayInfo().getColor().get().v() : "",
				domain.getDisplayInfo().getTaskNote().isPresent() ? domain.getDisplayInfo().getTaskNote().get().v()
						: "");
		ExternalCooperationInfoDto externalCooperationInfoDto = new ExternalCooperationInfoDto(
				domain.getCooperationInfo().getExternalCode1().isPresent()
						? domain.getCooperationInfo().getExternalCode1().get().v() : "",
				domain.getCooperationInfo().getExternalCode2().isPresent()
						? domain.getCooperationInfo().getExternalCode2().get().v() : "",
				domain.getCooperationInfo().getExternalCode3().isPresent()
						? domain.getCooperationInfo().getExternalCode3().get().v() : "",
				domain.getCooperationInfo().getExternalCode4().isPresent()
						? domain.getCooperationInfo().getExternalCode4().get().v() : "",
				domain.getCooperationInfo().getExternalCode5().isPresent()
						? domain.getCooperationInfo().getExternalCode5().get().v() : "");
		List<String> childTaskListDto = domain.getChildTaskList().stream().map(c -> c.toString())
				.collect(Collectors.toList());
		TaskData data = new TaskData(domain.getCode().v(), domain.getTaskFrameNo().v(), externalCooperationInfoDto,
				childTaskListDto, domain.getExpirationDate(), taskDisplayInfoDto);
		return data;
	}

}
