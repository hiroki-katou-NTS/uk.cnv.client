package nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto.TaskPaletteDto;
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
 * 作業パレットを取得する
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務予定.勤務予定.作業.作業パレット.作業パレットを取得する
 * @author HieuLt
 *
 */
@Stateless
public class GetTaskPalletQuery {
		
		@Inject
		private TaskPaletteOrganizationRepository repo;
		
		@Inject
		private TaskingRepository taskingRepository;
		
	public TaskPaletteDto get(GeneralDate date , int page , int targetUnit , String organizationID){
		//取得する (対象日: 基準日, ページ: int, 対象組織の単位: 対象組織の単位, 組織ID: String): 作業パレット
		TargetOrgIdenInfor targetInfor = null;
		Optional<TaskPalette> taskPalette;
		TaskPaletteDto taskPaletteDto = null;
		//1: <call>
		if (targetUnit == TargetOrganizationUnit.WORKPLACE_GROUP.value) {
			targetInfor = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(organizationID);
		} else {
			targetInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(organizationID);
		}
		//2 : get(ページ、対象組織): Optional<組織の作業パレット>
		Optional<TaskPaletteOrganization> data = repo.getByPage(targetInfor, page);
		
		//3 : Optional<組織の作業パレット> isPresent :表示情報を取得する(Require, 年月日) : 作業パレット
		if (data.isPresent()) {
			TaskImpl require = new TaskImpl(taskingRepository);
			taskPalette = Optional.of(data.get().getDisplayInfo(require, date));
			//3.1 Create()
			taskPaletteDto = TaskPaletteDto.toDto(taskPalette.get());
		}
		return taskPaletteDto;
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
