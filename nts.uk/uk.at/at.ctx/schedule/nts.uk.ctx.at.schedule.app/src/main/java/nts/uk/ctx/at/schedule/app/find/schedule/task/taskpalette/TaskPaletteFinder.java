package nts.uk.ctx.at.schedule.app.find.schedule.task.taskpalette;

import java.util.ArrayList;
import java.util.Arrays;
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
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.DisplayInfoOrganization;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.WorkplaceInfo;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupImport;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.AffWorkplaceAdapter;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.WorkplaceExportServiceAdapter;
import nts.uk.shr.com.context.AppContexts;

/**
 * 作業パレットを取得する 
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.勤務予定.作業.作業パレット.App.作業パレットを取得する
 * @author quytb
 *
 */

@Stateless
public class TaskPaletteFinder {
	
	@Inject
	private TaskingRepository taskingRepository;
	
	@Inject
	private TaskPaletteOrganizationRepository repository;
	
	@Inject
	private WorkplaceGroupAdapter groupAdapter;
	
	@Inject
	private WorkplaceExportServiceAdapter serviceAdapter;
	
	@Inject
	private AffWorkplaceAdapter wplAdapter; 
	
	public TaskPaletteOrgnizationDto findOne(Integer targetUnit, String targetId, Integer page, String referenceDate) {
		TargetOrgIdenInfor target = null;
		RequireTaskPalletteImpl require = new RequireTaskPalletteImpl(taskingRepository);
		if(targetUnit == 0) {
			target = TargetOrgIdenInfor.creatIdentifiWorkplace(targetId);	
		} else if(targetUnit == 1 ) {
			target = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(targetId);	
		}
		
		Optional<TaskPaletteOrganization> optional = repository.getByPage(target, page);
		if(optional.isPresent()) {
			TaskPalette taskPalette = optional.get().getDisplayInfo(require, GeneralDate.fromString(referenceDate, "yyyy/MM/dd"));
			return optional.isPresent()? TaskPaletteOrgnizationDto.setData(optional.get(), taskPalette): new TaskPaletteOrgnizationDto();
		} else {
			return new TaskPaletteOrgnizationDto();
		}		
	}
	
	public List<TaskPaletteDto> findTaskPalette(Integer targetUnit, String targetId, String referenceDate) {

		TargetOrgIdenInfor target = null;
		List<TaskPaletteOrganization> taskPaletteOrganizations = new ArrayList<TaskPaletteOrganization>();
		RequireImpl require = new RequireImpl(groupAdapter, serviceAdapter, wplAdapter);
		if(targetUnit == 0) {
			target = TargetOrgIdenInfor.creatIdentifiWorkplace(targetId);			
			taskPaletteOrganizations = repository.getAll(target);	
			
		} else if(targetUnit == 1 ) {
			target = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(targetId);			
			taskPaletteOrganizations = repository.getAll(target);
		}
		
		DisplayInfoOrganization data = target.getDisplayInfor(require, GeneralDate.fromString(referenceDate, "yyyy/MM/dd")); 
		
		
		if(taskPaletteOrganizations.size() > 0) {
			return taskPaletteOrganizations.stream().map(item -> TaskPaletteDto.setData(item, data.getDisplayName()))
					.collect(Collectors.toList());
		} else {
			TaskPaletteDto dto = new TaskPaletteDto();
			dto.setDisplayName(data.getDisplayName());
			return Arrays.asList(dto);
		}
		
	}
	
	@AllArgsConstructor
	private static class RequireImpl implements TargetOrgIdenInfor.Require {
		
		private WorkplaceGroupAdapter groupAdapter;		
	
		private WorkplaceExportServiceAdapter serviceAdapter;		
	
		private AffWorkplaceAdapter wplAdapter; 

		@Override
		public List<WorkplaceGroupImport> getSpecifyingWorkplaceGroupId(List<String> workplacegroupId) {
			return groupAdapter.getbySpecWorkplaceGroupID(workplacegroupId);
		}

		@Override
		public List<WorkplaceInfo> getWorkplaceInforFromWkpIds(List<String> listWorkplaceId, GeneralDate baseDate) {
			String companyId = AppContexts.user().companyId();
			List<WorkplaceInfo> workplaceInfos = serviceAdapter.getWorkplaceInforByWkpIds(companyId, listWorkplaceId, baseDate).stream()
			.map(mapper-> new WorkplaceInfo(mapper.getWorkplaceId(), Optional.ofNullable(mapper.getWorkplaceCode()), Optional.ofNullable(mapper.getWorkplaceName()), Optional.ofNullable(mapper.getWorkplaceExternalCode()), 
					Optional.ofNullable(mapper.getWorkplaceGenericName()), Optional.ofNullable(mapper.getWorkplaceDisplayName()), Optional.ofNullable(mapper.getHierarchyCode()))).collect(Collectors.toList());
			return workplaceInfos;
		}

		@Override
		public List<String> getWKPID(String WKPGRPID) {
			String CID = AppContexts.user().companyId();
			return wplAdapter.getWKPID(CID, WKPGRPID);
		}		
	}
	
	@AllArgsConstructor
	private static class RequireTaskPalletteImpl implements TaskPaletteOrganization.Require {
		
		private TaskingRepository taskingRepository;

		@Override
		public Optional<Task> getTask(TaskFrameNo taskFrameNo, TaskCode taskCode) {
			String companyId = AppContexts.user().companyId();
			return taskingRepository.getOptionalTask(companyId, taskFrameNo, taskCode);
		}
		
	}
}
