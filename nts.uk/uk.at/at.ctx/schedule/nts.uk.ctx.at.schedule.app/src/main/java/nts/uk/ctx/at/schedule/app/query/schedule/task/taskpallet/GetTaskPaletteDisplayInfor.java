package nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto.TaskPaletteDto;
import nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto.TaskPaletteOrganizationDto;
import nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto.WorkPaletteDisplayInforDto;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPalette;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganization;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganizationRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務予定.勤務予定.作業.作業パレット.App.作業パレット表示情報を取得する
 * 作業パレット表示情報を取得する
 * @author HieuLt
 *
 */
@Stateless
public class GetTaskPaletteDisplayInfor {
	
	@Inject
	private TaskPaletteOrganizationRepository repo;
	
	@Inject
	private TaskingRepository taskingRepository;
	
public WorkPaletteDisplayInforDto get( GeneralDate date , int page , int targetUnit , String organizationID){
		//取得する (対象組織の単位: 対象組織の単位, 組織ID: String, 対象日: 年月日, ページ: int): List＜組織の作業パレット＞，作業パレット
		TargetOrgIdenInfor targetOrgIdenInfor = null;
		Optional<TaskPalette> taskPalette;
		TaskPaletteDto taskPaletteDto = null;
		//1: <call>() 
		if (targetUnit == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(organizationID);
		} else {
			targetOrgIdenInfor = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(organizationID);
		}
		//2: 作業パレットをすべて取得する(対象組織): List<組織の作業パレット>
		List<TaskPaletteOrganization> lstTaskPaletteOrg = repo.getAll(targetOrgIdenInfor);
		List<TaskPaletteOrganizationDto> dtos = lstTaskPaletteOrg.stream().map(mapper -> {
			TaskPaletteOrganizationDto dto = TaskPaletteOrganizationDto.toDto(mapper);
			return dto;
		}).collect(Collectors.toList());
		//3: 取得する(ページ，対象組織)Optional＜組織の作業パレット＞
		Optional<TaskPaletteOrganization> taskPaletteOrg = repo.getByPage(targetOrgIdenInfor, page);
		//4:Optional<組織の作業パレット> isPresent: 表示情報を取得する(Require, 年月日) : 作業パレット
		if (taskPaletteOrg.isPresent()) {
			TaskImpl require = new TaskImpl(taskingRepository);
			//4.1: create()
			taskPalette = Optional.of(taskPaletteOrg.get().getDisplayInfo(require, date));
			taskPaletteDto = TaskPaletteDto.toDto(taskPalette.get());
		}
		WorkPaletteDisplayInforDto result = new WorkPaletteDisplayInforDto(dtos, taskPaletteDto);
		return result;
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
