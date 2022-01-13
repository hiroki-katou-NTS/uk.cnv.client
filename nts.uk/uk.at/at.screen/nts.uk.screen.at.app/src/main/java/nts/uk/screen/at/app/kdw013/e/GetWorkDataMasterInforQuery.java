package nts.uk.screen.at.app.kdw013.e;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.screen.at.app.kdw013.a.TaskDto;
import nts.uk.screen.at.app.kdw013.c.GetAvailableWorking;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.E：時刻なし作業内容.メニュー別OCD.作業データマスタ情報を取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetWorkDataMasterInforQuery {

	@Inject
	private GetAvailableWorking getAvailableWorking;

	@Inject
	private TaskingRepository taskingRepository;

	/**
	 * 作業データマスタ情報を取得する
	 * 
	 * @param command
	 * @return
	 */
	public GetWorkDataMasterInforDto get(GetWorkDataMasterInforCommand command) {
		String cId = AppContexts.user().companyId();

		// 1. <call>(社員ID,基準日,作業枠NO,上位枠作業コード)
		// 作業枠NOを1～5をループする
		// 利用可能作業を取得する

		List<TaskDto> taskFrameNo1 = getAvailableWorking
				.get(command.getSId(), command.getRefDate(), new TaskFrameNo(1), Optional.empty()).stream()
				.map(TaskDto::toDto).collect(Collectors.toList());

		List<TaskDto> taskFrameNo2 = getAvailableWorking.get(command.getSId(), command.getRefDate(), new TaskFrameNo(2),
				Optional.ofNullable(command.getTaskCode1()).map(c -> Optional.of(new TaskCode(c)))
						.orElse(Optional.empty()))
				.stream().map(TaskDto::toDto).collect(Collectors.toList());

		List<TaskDto> taskFrameNo3 = getAvailableWorking
				.get(command.getSId(), command.getRefDate(), new TaskFrameNo(3),
						Optional.ofNullable(command.getTaskCode2()).map(c -> Optional.of(new TaskCode(c)))
								.orElse(Optional.empty()))
				.stream().map(c -> TaskDto.toDto(c)).collect(Collectors.toList());

		List<TaskDto> taskFrameNo4 = getAvailableWorking
				.get(command.getSId(), command.getRefDate(), new TaskFrameNo(4),
						Optional.ofNullable(command.getTaskCode3()).map(c -> Optional.of(new TaskCode(c)))
								.orElse(Optional.empty()))
				.stream().map(c -> TaskDto.toDto(c)).collect(Collectors.toList());

		List<TaskDto> taskFrameNo5 = getAvailableWorking
				.get(command.getSId(), command.getRefDate(), new TaskFrameNo(5),
						Optional.ofNullable(command.getTaskCode4()).map(c -> Optional.of(new TaskCode(c)))
								.orElse(Optional.empty()))
				.stream().map(c -> TaskDto.toDto(c)).collect(Collectors.toList());

		// 2. [取得したList<作業>にINPUT「作業コード」が含まれてない]:get(会社ID, 作業枠NO, 作業コード):Optional<作業>
		// 指定された作業情報を取得する
		if (Optional.ofNullable(command.getTaskCode1()).isPresent() && !taskFrameNo1.stream()
				.filter(c -> c.getCode().equals(command.getTaskCode1())).findAny().isPresent()) {
			taskingRepository.getOptionalTask(cId, new TaskFrameNo(1), new TaskCode(command.getTaskCode1()))
					.ifPresent(c -> taskFrameNo1.add(TaskDto.toDto(c)));
		}

		if (Optional.ofNullable(command.getTaskCode2()).isPresent() && !taskFrameNo2.stream()
				.filter(c -> c.getCode().equals(command.getTaskCode2())).findAny().isPresent()) {
			taskingRepository.getOptionalTask(cId, new TaskFrameNo(2), new TaskCode(command.getTaskCode2()))
					.ifPresent(c -> taskFrameNo2.add(TaskDto.toDto(c)));
		}

		if (Optional.ofNullable(command.getTaskCode3()).isPresent() && !taskFrameNo3.stream()
				.filter(c -> c.getCode().equals(command.getTaskCode3())).findAny().isPresent()) {
			taskingRepository.getOptionalTask(cId, new TaskFrameNo(3), new TaskCode(command.getTaskCode3()))
					.ifPresent(c -> taskFrameNo3.add(TaskDto.toDto(c)));
		}

		if (Optional.ofNullable(command.getTaskCode4()).isPresent() && !taskFrameNo4.stream()
				.filter(c -> c.getCode().equals(command.getTaskCode4())).findAny().isPresent()) {
			taskingRepository.getOptionalTask(cId, new TaskFrameNo(4), new TaskCode(command.getTaskCode4()))
					.ifPresent(c -> taskFrameNo4.add(TaskDto.toDto(c)));
		}

		if (Optional.ofNullable(command.getTaskCode5()).isPresent() && !taskFrameNo5.stream()
				.filter(c -> c.getCode().equals(command.getTaskCode5())).findAny().isPresent()) {
			taskingRepository.getOptionalTask(cId, new TaskFrameNo(5), new TaskCode(command.getTaskCode5()))
					.ifPresent(c -> taskFrameNo5.add(TaskDto.toDto(c)));
		}

		return new GetWorkDataMasterInforDto(taskFrameNo1, taskFrameNo2, taskFrameNo3, taskFrameNo4, taskFrameNo5);

	}
}
