package nts.uk.screen.at.app.ksu003.getworkselectioninfor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPalette;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganization;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganizationRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto.TaskPaletteDto;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto.TaskPaletteOrganizationDto;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto.WorkPaletteDisplayInforDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * 使用できる作業マスタを取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).A：個人スケジュール修正(日付別).メニュー別OCD.
 * 
 * @author HieuLt
 *
 */
@Stateless
public class GetWorkPaletteDisplay {

	@Inject
	private TaskPaletteOrganizationRepository repo;

	@Inject
	private TaskingRepository taskingRepository;

	public WorkPaletteDisplayInforDto getWorkPaletteDisplayInfo(int targetUnit, String organizationID,
			GeneralDate targetDate, int page) {

		// 1.1[対象組織の単位==職場]: 職場を指定して識別情報を作成する(職場ID):対象組織識別情報
		// 1.2[対象組織の単位==職場グループ]: 職場グループを指定して識別情報を作成する(職場グループID):対象組織識別情報
		TargetOrgIdenInfor targetOrgIdenInfor = null;
		TaskPaletteDto taskPaletteDto = null;

		Optional<TaskPalette> taskPalette;
		if (targetUnit == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(organizationID);
		} else {
			targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(organizationID);
		}
		// 2 作業パレットをすべて取得する(対象組織):List<組織の作業パレット>
		List<TaskPaletteOrganization> lstTaskPaletteOrganization = repo.getAll(targetOrgIdenInfor);

		// Convert Dto List<TaskPaletteOrganization> ->
		List<TaskPaletteOrganizationDto> dtos = lstTaskPaletteOrganization.stream().map(mapper -> {
			TaskPaletteOrganizationDto dto = TaskPaletteOrganizationDto.toDto(mapper);
			return dto;
		}).collect(Collectors.toList());

		// 3 取得する(ページ，対象組織):Optional＜組織の作業パレット＞
		Optional<TaskPaletteOrganization> otpTaskPaletteOrganization = repo.getByPage(targetOrgIdenInfor, page);
		// 4 [Optional<組織の作業パレット> isPresent]: 表示情報を取得する(Require, 年月日) : 作業パレット
		if (otpTaskPaletteOrganization.isPresent()) {
			TaskImpl require = new TaskImpl(taskingRepository);
			taskPalette = Optional.of(otpTaskPaletteOrganization.get().getDisplayInfo(require, targetDate));
			taskPaletteDto = TaskPaletteDto.toDto(taskPalette.get());
		}
		WorkPaletteDisplayInforDto data = new WorkPaletteDisplayInforDto(dtos, taskPaletteDto);
		return data;
	}

	@AllArgsConstructor
	public static class TaskImpl implements TaskPaletteOrganization.Require {

		@Inject
		
		private TaskingRepository taskingRepository;

		@Override
		public Optional<Task> getTask(TaskFrameNo taskFrameNo, TaskCode taskCode) {
			String cid = AppContexts.user().companyId();
			Optional<Task> data = taskingRepository.getOptionalTask(cid, taskFrameNo, taskCode);
			return data;
		}
	}
}
