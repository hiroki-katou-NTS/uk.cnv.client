package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.ManHrInputDisplayFormat;
import nts.uk.ctx.at.record.dom.jobmanagement.usagesetting.ManHrInputUsageSetting;
import nts.uk.ctx.at.shared.app.query.task.GetTaskOperationSettingQuery;
import nts.uk.ctx.at.shared.dom.personallaborcondition.UseAtr;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationMethod;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskframe.TaskFrameUsageSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.screen.at.app.kdw006.i.GetManHrInputUsageSetting;
import nts.uk.screen.at.app.kdw013.query.GetManHrInputDisplayFormat;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.工数入力を起動する
 * 
 * @author tutt
 * <<ScreenQuery>>
 */
@Stateless
public class StartManHourInputScreenQuery {

	@Inject
	private GetTaskOperationSettingQuery getTaskOperationSettingQuery;

	@Inject
	private GetManHrInputUsageSetting getManHrInputUsageSetting;
	
	@Inject
	private TaskFrameUsageSettingRepository taskFrameUsageSettingRepository;
	
	@Inject
	private TaskingRepository taskingRepository;
	
	@Inject
	private GetManHrInputDisplayFormat getManHrInputDisplayFormat;

	public StartManHourInput startManHourInput() {
		String cid = AppContexts.user().companyId();
		// 1. 取得する(会社ID)

		Optional<TaskOperationSetting> opSettingOpt = this.getTaskOperationSettingQuery.getTasksOperationSetting(cid);

		// 2. 作業運用設定.isEmpty OR 作業運用設定.運用方法 = 利用しない

		if (!opSettingOpt.isPresent()
				|| opSettingOpt.get().getTaskOperationMethod().equals(TaskOperationMethod.DO_NOT_USE)) {
			throw new BusinessException("Msg_2122");
		}

		// 3. 作業運用設定.運用方法 = 予定で利用

		if (opSettingOpt.get().getTaskOperationMethod().equals(TaskOperationMethod.USE_ON_SCHEDULE)) {
			throw new BusinessException("Msg_2253");
		}

		// 4. 取得する()

		Optional<ManHrInputUsageSetting> manHrSettingOpt = this.getManHrInputUsageSetting.get();

		// 5. 工数入力の利用設定.isEmpty OR 工数入力の利用設定.使用区分 = 使用しない

		if (!manHrSettingOpt.isPresent() || manHrSettingOpt.get().getUsrAtr().equals(NotUseAtr.NOT_USE)) {
			throw new BusinessException("Msg_2243");
		}
		
		// 6. get(ログイン会社ID)
		
		TaskFrameUsageSetting taskFrameUsageSetting = this.taskFrameUsageSettingRepository.getWorkFrameUsageSetting(cid);
		
		// 7. 「作業枠利用設定．枠設定．利用区分」 = する　がない
		
		if (taskFrameUsageSetting == null || taskFrameUsageSetting.getFrameSettingList().stream()
				.filter(x -> x.getUseAtr().equals(UseAtr.USE))
				.collect(Collectors.toList()).isEmpty()) {
			throw new BusinessException("Msg_1960");
		}
		
		// 8. get(ログイン会社ID)
		
		List<Task> tasks = this.taskingRepository.getListTask(cid);

		// 9.作業.isEmpty
		if (tasks.isEmpty()) {
			throw new BusinessException("Msg_1961");
		}
		
		// 10. 取得する() ログイン契約コード
		
		Optional<ManHrInputDisplayFormat> manHrInputDisplayFormat = this.getManHrInputDisplayFormat.get();

		return new StartManHourInput(taskFrameUsageSetting, tasks, manHrInputDisplayFormat);
	}

}
