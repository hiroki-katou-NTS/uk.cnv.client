package nts.uk.screen.at.app.query.kdp.kdp002.l;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

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
 * 打刻入力で社員利用可能作業を取得する
 * UKDesign.UniversalK.就業.KDP_打刻.KDP002_打刻入力(個人打刻).L:打刻作業選択.メニュー別OCD.打刻入力で社員利用可能作業を取得する
 * 
 * @author chungnt
 *
 */

@Stateless
public class GetEmployeeWorkByStamping {

	@Inject
	private TaskFrameUsageSettingRepository taskFrameRepo;

	@Inject
	private GetWorkAvailableToEmployeesService getWork;
	
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
	

	public GetEmployeeWorkByStampingDto getEmployeeWorkByStamping(GetEmployeeWorkByStampingInput param) {

		GetEmployeeWorkByStampingDto result = new GetEmployeeWorkByStampingDto();

		String cid = AppContexts.user().companyId();
		GeneralDate today = GeneralDate.today();

		Optional<TaskCode> taskCode = Optional.empty();

		if (param.getUpperFrameWorkCode() != null) {
			if (param.getUpperFrameWorkCode().equals("")) {
				taskCode = Optional.empty();
			}else {
				taskCode = Optional.of(new TaskCode(param.getUpperFrameWorkCode()));
			}
		}

		GetWorkAvailableToEmployeesServiceImp require = new GetWorkAvailableToEmployeesServiceImp();

		TaskFrameUsageSetting taskFrame = taskFrameRepo.getWorkFrameUsageSetting(cid);

		if (taskFrame != null) {
			if (!taskFrame.getFrameSettingList().isEmpty()) {
				result.setTaskFrameUsageSetting(new TaskFrameUsageSettingDto(
						taskFrame.getFrameSettingList()
						.stream()
						.map(m -> {
							return new TaskFrameSettingDto(
									m.getTaskFrameNo().v(),
									m.getTaskFrameName().v(),
									m.getUseAtr().value == 1 ? true : false); 
						}).collect(Collectors.toList())));
			}
		}

		List<Task> tasks = getWork.get(require, cid, param.getSid(), today, new TaskFrameNo(param.getWorkFrameNo()),
				taskCode);

		result.setTask(tasks.stream().map(m -> {
			return new TaskDto(m.getCode().v(), m.getTaskFrameNo().v(),
					new ExternalCooperationInfoDto(m.getCooperationInfo().getExternalCode1().map(a -> a.v()).orElse(""),
							m.getCooperationInfo().getExternalCode2().map(a -> a.v()).orElse(""),
							m.getCooperationInfo().getExternalCode3().map(a -> a.v()).orElse(""),
							m.getCooperationInfo().getExternalCode4().map(a -> a.v()).orElse(""),
							m.getCooperationInfo().getExternalCode5().map(a -> a.v()).orElse("")),
					m.getChildTaskList().stream().map(b -> b.v()).collect(Collectors.toList()),
					m.getExpirationDate().start(), m.getExpirationDate().end(),
					new TaskDisplayInfoDto(m.getDisplayInfo().getTaskName().v(), m.getDisplayInfo().getTaskAbName().v(),
							m.getDisplayInfo().getColor().map(c -> c.v()).orElse(""),
							m.getDisplayInfo().getTaskNote().map(d -> d.v()).orElse("")));
		}).collect(Collectors.toList()));

		return result;
	}

	private class GetWorkAvailableToEmployeesServiceImp implements GetWorkAvailableToEmployeesService.Require {

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
