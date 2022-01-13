package nts.uk.screen.at.app.query.kdw.kdw003.g;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.operationsettings.TaskOperationSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskframe.TaskFrameUsageSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.shr.com.context.AppContexts;

/**
 * 作業項目情報を取得する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW003_日別実績の修正.G：初期作業選択設定.メニュー別OCD.作業項目情報を取得する
 * @author quytb
 *
 */

@Stateless
public class GetTaskItemInfoScreenQuery {
	@Inject
	TaskOperationSettingRepository taskOperationSettingRepository;
	
	@Inject
	TaskFrameUsageSettingRepository taskFrameUsageSettingRepository;
	
	@Inject
	TaskingRepository taskingRepository;

	public List<TaskItemDto> GetTaskItemInfo() {
		List<TaskItemDto> results = new ArrayList<TaskItemDto>();
		List<Integer> listFrameUseAtr = new ArrayList<Integer>();
		List<String> listFrameName = new ArrayList<String>();
		String companyId = AppContexts.user().companyId();
		Boolean hasTaskFrameSetting = true;
		/** 1.Get(ログイン会社 ID)*/
		Optional<TaskOperationSetting> optTaskOperation = taskOperationSettingRepository.getTasksOperationSetting(companyId);
		/** 2.[作業運用設定.isEmpty OR 作業運用設定.作業運用方法 <> 実績で利用]:<call>()*/
		if(!optTaskOperation.isPresent() || optTaskOperation.get().getTaskOperationMethod().value != 1) {
			throw new BusinessException("Msg_2185");
		}
		
		/** 3.Get(ログイン会社ID)*/
		TaskFrameUsageSetting taskFrameUsageSetting = taskFrameUsageSettingRepository.getWorkFrameUsageSetting(companyId);
		/** 4. [作業枠利用設定．枠設定．利用区分 == する　がない]:<call>()*/
		List<TaskFrameSetting> lstTaskFrameSettings = taskFrameUsageSetting.getFrameSettingList();

		for (int i = 0; i < lstTaskFrameSettings.size(); i++) {
			listFrameUseAtr.add(lstTaskFrameSettings.get(i).getUseAtr().value);
			listFrameName.add(lstTaskFrameSettings.get(i).getTaskFrameName().v());
			if (lstTaskFrameSettings.get(i).getUseAtr().value != 0) {
				hasTaskFrameSetting = false;
			}
		}

		if (hasTaskFrameSetting) {
			throw new BusinessException("Msg_1960");
		}
		
		/** 5.Get*(ログイン会社ID)*/
		List<Task> lstTask = taskingRepository.getListTask(companyId);
		
		/** 6.[作業.isEmpty]:<call>()*/
		if(lstTask.isEmpty()) {
			throw new BusinessException("Msg_1961");
		}
		
		results = lstTask.stream().map(task -> {
				return  TaskItemDto.builder().frameNo(task.getTaskFrameNo().v())
						.taskCode(task.getCode().v())
						.taskName(task.getDisplayInfo().getTaskName().v())
						.taskAbName(task.getDisplayInfo().getTaskAbName().v())
						.startDate(task.getExpirationDate().start().toString())
						.endDate(task.getExpirationDate().end().toString())
						.listFrameNoUseAtr(listFrameUseAtr)
						.listFrameName(listFrameName)
						.build();
		}).collect(Collectors.toList());	
	
		
//		for (int j = 0; j < results.size(); j++) {
//			results.get(j).setListFrameNoUseAtr(listFrameUseAtr);
//		}
		
		return results;
		
	}
}
