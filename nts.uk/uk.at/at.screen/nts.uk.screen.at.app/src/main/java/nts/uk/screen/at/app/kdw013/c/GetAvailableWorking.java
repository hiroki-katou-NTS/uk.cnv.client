package nts.uk.screen.at.app.kdw013.c;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.domainservice.GetWorkAvailableToEmployeesService;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskassign.taskassingemployee.TaskAssignEmployeeRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskassign.taskassingworkplace.NarrowingByWorkplaceRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskframe.TaskFrameUsageSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskassign.taskassignemployee.TaskAssignEmployee;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskassign.taskassignworkplace.NarrowingDownTaskByWorkplace;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.workflow.dom.adapter.bs.EmployeeAdapter;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.C:作業入力パネル.メニュー別OCD.利用可能作業を取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetAvailableWorking {

	@Inject
	private TaskFrameUsageSettingRepository taskFrameUsageSettingRepository;

	@Inject
	private TaskingRepository taskingRepo;

	@Inject
	private TaskAssignEmployeeRepository taskAssignEmployeeRepo;

	@Inject
	private EmployeeAdapter employeeAdapter;

	@Inject
	private NarrowingByWorkplaceRepository narrowRepo;

	/**
	 * 
	 * @param sId         社員ID
	 * @param refDate     基準日
	 * @param taskFrameNo 作業枠NO
	 * @param taskCode   上位枠作業コード
	 * @return
	 */
	public List<Task> get(String sId, GeneralDate refDate, TaskFrameNo taskFrameNo, Optional<TaskCode> taskCode) {
		RequireImpl require = new RequireImpl(taskingRepo, taskAssignEmployeeRepo, taskFrameUsageSettingRepository,
				employeeAdapter, narrowRepo);

		return GetWorkAvailableToEmployeesService.get(require, AppContexts.user().companyId(), sId, refDate,
				taskFrameNo, taskCode);
	}

	@AllArgsConstructor
	public class RequireImpl implements GetWorkAvailableToEmployeesService.Require {

		private TaskingRepository taskingRepo;

		private TaskAssignEmployeeRepository taskAssignEmployeeRepo;

		private TaskFrameUsageSettingRepository taskFrameUsageSettingRepository;

		private EmployeeAdapter employeeAdapter;

		private NarrowingByWorkplaceRepository narrowRepo;

		@Override
		public List<String> findWpkIdsBySid(String employeeID, GeneralDate date) {
			return employeeAdapter.findWpkIdsBySid(AppContexts.user().companyId(), employeeID, date);
		}

		@Override
		public Optional<NarrowingDownTaskByWorkplace> getNarrowingDownTaskByWorkplace(String workPlaceId,
				TaskFrameNo taskFrameNo) {
			return narrowRepo.getOptionalWork(workPlaceId, taskFrameNo);
		}

		@Override
		public TaskFrameUsageSetting getTask() {
			return taskFrameUsageSettingRepository.getWorkFrameUsageSetting(AppContexts.user().companyId());
		}

		@Override
		public List<Task> getTask(GeneralDate date, List<TaskFrameNo> taskFrameNo) {
			return taskingRepo.getListTask(AppContexts.user().companyId(), date, taskFrameNo);
		}

		@Override
		public List<Task> getListTask(GeneralDate referenceDate, TaskFrameNo taskFrameNo, List<TaskCode> codes) {
			return taskingRepo.getListTask(AppContexts.user().companyId(), referenceDate, taskFrameNo, codes);
		}

		@Override
		public Optional<Task> getOptionalTask(TaskFrameNo taskFrameNo, TaskCode code) {
			return taskingRepo.getOptionalTask(AppContexts.user().companyId(), taskFrameNo, code);
		}

		@Override
		public List<TaskAssignEmployee> getTaskAssignEmployee(String employeeId, TaskFrameNo taskFrameNo) {
			return taskAssignEmployeeRepo.get(employeeId, taskFrameNo.v());
		}
	}
}
