package nts.uk.ctx.sys.assist.pub.command.autosetting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.system.ServerSystemProperties;
import nts.arc.task.AsyncTaskInfo;
import nts.arc.task.AsyncTaskInfoRepository;
import nts.arc.task.AsyncTaskStatus;
import nts.uk.ctx.sys.assist.app.command.autosetting.deletion.AutoDeletionPreparationCommandHandler;
import nts.uk.ctx.sys.assist.app.command.autosetting.storage.AutoStoragePreparationCommandHandler;
import nts.uk.ctx.sys.assist.app.command.deletedata.manualsetting.ManualSetDeletionService;
import nts.uk.ctx.sys.assist.app.command.resultofsaving.ResultOfSavingCommand;
import nts.uk.ctx.sys.assist.app.command.resultofsaving.ResultOfSavingHandler;
import nts.uk.ctx.sys.assist.dom.deletedata.EmployeeDeletion;
import nts.uk.ctx.sys.assist.dom.deletedata.EmployeesDeletionRepository;
import nts.uk.ctx.sys.assist.dom.deletedata.ManualSetDeletion;
import nts.uk.ctx.sys.assist.dom.deletedata.ManualSetDeletionRepository;
import nts.uk.ctx.sys.assist.dom.reference.record.EmpBasicInfoAdapter;
import nts.uk.ctx.sys.assist.dom.storage.BusinessName;
import nts.uk.ctx.sys.assist.dom.storage.EmployeeCode;
import nts.uk.ctx.sys.assist.dom.storage.ManualSetOfDataSave;
import nts.uk.ctx.sys.assist.dom.storage.ManualSetOfDataSaveHolder;
import nts.uk.ctx.sys.assist.dom.storage.ManualSetOfDataSaveRepository;
import nts.uk.ctx.sys.assist.dom.storage.ManualSetOfDataSaveService;
import nts.uk.ctx.sys.assist.dom.storage.ResultOfSaving;
import nts.uk.ctx.sys.assist.dom.storage.ResultOfSavingRepository;
import nts.uk.ctx.sys.assist.dom.storage.TargetCategoryRepository;
import nts.uk.ctx.sys.assist.dom.storage.TargetEmployees;
import nts.uk.ctx.sys.assist.dom.storage.TargetEmployeesRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.file.storage.stream.FileStoragePath;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class AutoExecutionPreparationPubImpl implements AutoExecutionPreparationPub {

	@Inject
	private AutoStoragePreparationCommandHandler autoStoragePreparationCommandHandler;

	@Inject
	private AutoDeletionPreparationCommandHandler autoDeletionPreparationCommandHandler;

	@Inject
	private ManualSetOfDataSaveRepository manualSetOfDataSaveRepository;

	@Inject
	private ResultOfSavingRepository resultOfSavingRepository;

	@Inject
	private TargetCategoryRepository targetCategoryRepository;

	@Inject
	private TargetEmployeesRepository targetEmployeesRepository;

	@Inject
	private ManualSetDeletionRepository manualSetDeletionRepository;

	@Inject
	private EmployeesDeletionRepository employeesDeletionRepository;

	@Inject
	private AsyncTaskInfoRepository asyncTaskInfoRepository;

	@Inject
	private EmpBasicInfoAdapter empBasicInfoAdapter;

	@Inject
	private ManualSetOfDataSaveService manualSetOfDataSaveService;

	@Inject
	private ManualSetDeletionService manualSetDeletionService;

	@Inject
	private ResultOfSavingHandler resultOfSavingHandler;

	@Override
	public AutoPrepareDataExport autoStoragePrepare(String patternCode) {
		ManualSetOfDataSave manualSet = this.autoStoragePreparationCommandHandler.handle(patternCode);
		// ドメインモデル「データ保存の対象社員」に保存する
		this.manualSetOfDataSaveRepository.addManualSetting(manualSet);
		this.targetCategoryRepository.add(manualSet.getCategory());
		return new AutoPrepareDataExport(manualSet.getStoreProcessingId(), manualSet.getDaySaveStartDate(),
				manualSet.getDaySaveEndDate());
	}

	@Override
	public Optional<String> updateTargetEmployee(String storeProcessId, String patternCode, List<String> empIds) {
		// 更新処理自動実行の実行対象社員リストを取得する
		List<TargetEmployees> targetEmployees = this.empBasicInfoAdapter
				.getEmpBasicInfo(AppContexts.user().companyId(), empIds).stream()
				.map(item -> new TargetEmployees(storeProcessId, item.getEmployeeId(),
						new BusinessName(item.getBusinessName()), new EmployeeCode(item.getEmployeeCode())))
				.collect(Collectors.toList());

		ManualSetOfDataSave manualSet = this.manualSetOfDataSaveRepository.getManualSetOfDataSaveById(storeProcessId)
				.orElse(null);
		String storeProcessingId = manualSet.getStoreProcessingId();
		manualSet.setCategory(this.targetCategoryRepository.getTargetCategoryListById(storeProcessingId));
		manualSet.setEmployees(targetEmployees);
		this.targetEmployeesRepository.addAll(targetEmployees);
		// アルゴリズム「サーバー手動保存処理」を実行する
		String taskId = this.manualSetOfDataSaveService.start(new ManualSetOfDataSaveHolder(manualSet, patternCode))
				.getTaskId();
		// Wait until export service is done
		AsyncTaskStatus taskStatus;
		do {
			taskStatus = this.asyncTaskInfoRepository.getStatus(taskId);
		} while (taskStatus.equals(AsyncTaskStatus.PENDING) || taskStatus.equals(AsyncTaskStatus.RUNNING));
		// Update file size if export is successful
		Optional<ResultOfSaving> optResultOfSaving = this.resultOfSavingRepository
				.getResultOfSavingById(storeProcessingId);
		optResultOfSaving.ifPresent(result -> {
			result.getFileId().ifPresent(fileId -> {
				this.resultOfSavingHandler.handle(new ResultOfSavingCommand(storeProcessingId, fileId));
			});
		});
		// Return whether errors have occured or not
		// Check runtime exception thrown from AsyncTask
		if (!taskStatus.equals(AsyncTaskStatus.COMPLETED)) {
			Optional<AsyncTaskInfo> optInfo = this.asyncTaskInfoRepository.find(taskId);
			if (optInfo.isPresent()) {
				return Optional.ofNullable(optInfo.get().getError().getMessage());
			}
		}
		return Optional.empty();
	}

	@Override
	public AutoPrepareDataExport autoDeletionPrepare(String patternCode) {
		ManualSetDeletion manualSet = this.autoDeletionPreparationCommandHandler.handle(patternCode);

		// ドメインモデル「データ削除の手動設定」を追加する
		this.manualSetDeletionRepository.addManualSetting(manualSet);
		return new AutoPrepareDataExport(manualSet.getDelId(), manualSet.getStartDateOfDaily().orElse(null),
				manualSet.getEndDateOfDaily().orElse(null));
	}

	@Override
	public Optional<String> updateEmployeeDeletion(String delId, List<String> empIds) {
		// 取得できた社員IDを社員ID（List）とする
		List<EmployeeDeletion> targetEmployees = this.empBasicInfoAdapter
				.getEmpBasicInfo(AppContexts.user().companyId(), empIds).stream()
				.map(item -> EmployeeDeletion.createFromJavatype(delId, item.getEmployeeId(), item.getEmployeeCode(),
						item.getBusinessName()))
				.collect(Collectors.toList());

		// ドメインモデル「データ削除の対象社員」を更新する
		employeesDeletionRepository.addAll(targetEmployees);
		ManualSetDeletion manualSet = this.manualSetDeletionRepository.getManualSetDeletionById(delId).orElse(null);
		// アルゴリズム「サーバ手動削除処理」を実行する
		String taskId = this.manualSetDeletionService.start(manualSet).getTaskId();
		// Wait until export service is done
		AsyncTaskStatus taskStatus;
		do {
			taskStatus = this.asyncTaskInfoRepository.getStatus(taskId);
		} while (taskStatus.equals(AsyncTaskStatus.PENDING) || taskStatus.equals(AsyncTaskStatus.RUNNING));
		// Return whether errors have occured or not
		// Check runtime exception thrown from AsyncTask
		if (!taskStatus.equals(AsyncTaskStatus.COMPLETED)) {
			Optional<AsyncTaskInfo> optInfo = this.asyncTaskInfoRepository.find(taskId);
			if (optInfo.isPresent()) {
				// Since ManualSetDeletionService will return an error in format of NoSuchFileException
				// even when deletion is successful
				// so check if this is truly error or not
				String storePath = String.format("%s\\%s", new FileStoragePath().getPathOfCurrentTenant().toString(), taskId);
				String msg = optInfo.get().getError().getMessage();
				if (!storePath.equals(msg)) {
					return Optional.of(msg);
				}
			}
		}
		return Optional.empty();
	}
}
