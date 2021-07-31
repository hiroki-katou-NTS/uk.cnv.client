package nts.uk.ctx.exio.app.input.prepare;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.diagnose.stopwatch.embed.EmbedStopwatch;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.bs.employee.dom.classification.affiliate.AffClassHistory;
import nts.uk.ctx.bs.employee.dom.classification.affiliate.AffClassHistoryRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.dom.employment.history.EmploymentHistory;
import nts.uk.ctx.bs.employee.dom.employment.history.EmploymentHistoryRepository;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistory;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryRepository;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.PrepareImporting;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizedDataRecord;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizedDataRecordRepository;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataId;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataRepository;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToChange;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToDelete;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.ExternalImportExistingRepository;
import nts.uk.ctx.exio.dom.input.group.ImportingGroup;
import nts.uk.ctx.exio.dom.input.group.ImportingGroupId;
import nts.uk.ctx.exio.dom.input.group.ImportingGroupRepository;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMetaRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecordRepository;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItem;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItemRepository;
import nts.uk.ctx.exio.dom.input.validation.user.ImportingUserCondition;
import nts.uk.ctx.exio.dom.input.validation.user.ImportingUserConditionRepository;
import nts.uk.ctx.exio.dom.input.workspace.ExternalImportWorkspaceRepository;
import nts.uk.ctx.exio.dom.input.workspace.group.GroupWorkspace;
import nts.uk.ctx.exio.dom.input.workspace.group.GroupWorkspaceRepository;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExternalImportPrepareRequire {
	
	public Require create(String companyId) {
		
		return EmbedStopwatch.embed(new RequireImpl(companyId));
	}
	
	public static interface Require extends
			PrepareImporting.Require,
			ExternalImportWorkspaceRepository.Require {
		
		Optional<ExternalImportSetting> getExternalImportSetting(String companyId, ExternalImportCode settingCode);
	}
	
	@Inject
	private ImportingUserConditionRepository importingUserConditionRepo;
	
	@Inject
	private ImportableItemsRepository importableItemsRepo;
	
	@Inject
	private ImportingGroupRepository importingGroupRepo;
	
	@Inject
	private GroupWorkspaceRepository groupWorkspaceRepo;
	
	@Inject
	private ExternalImportWorkspaceRepository workspaceRepo;
	
	@Inject
	private RevisedDataRecordRepository revisedDataRecordRepo;
	
	@Inject
	private CanonicalizedDataRecordRepository canonicalizedDataRecordRepo;
	
	@Inject
	private ExternalImportExistingRepository existingRepo;
	
	@Inject
	private ImportingDataMetaRepository metaRepo;
	
	@Inject
	private DomainDataRepository domainDataRepo;
	@Inject
	private EmployeeDataMngInfoRepository employeeDataMngInfoRepo;
	
	@Inject
	private EmploymentHistoryRepository employmentHistoryRepo;
	
	@Inject
	private TaskingRepository taskingRepo;
	
	@Inject
	private WorkInformationRepository workInformationRepo;
	
	@Inject
	private ExternalImportSettingRepository settingRepo;
	
	@Inject
	private ReviseItemRepository reviseItemRepo;
	
	@Inject
	private AffClassHistoryRepository affClassHistoryRepo;
	
	@Inject
	private AffJobTitleHistoryRepository affJobTitleHistoryRepo;
	
	public class RequireImpl implements Require {
		
		private final String companyId ;
		
		public RequireImpl(String companyId) {
			this.companyId = companyId;
		}
		
		
		/***** 外部受入関連 *****/
		
		@Override
		public Optional<ExternalImportSetting> getExternalImportSetting(String companyId, ExternalImportCode settingCode) {
			return settingRepo.get(companyId, settingCode);
		}
		
		@Override
		public ImportingGroup getImportingGroup(ImportingGroupId groupId) {
			return importingGroupRepo.find(groupId);
		}
		
		@Override
		public GroupWorkspace getGroupWorkspace(ImportingGroupId groupId) {
			return groupWorkspaceRepo.get(groupId);
		}
		
		@Override
		public Optional<ReviseItem> getReviseItem(String companyId, ExternalImportCode importCode, int importItemNumber) {
			return reviseItemRepo.get(companyId, importCode, importItemNumber);
		}
		
		@Override
		public ImportableItem getImportableItem(ImportingGroupId groupId, int itemNo) {
			return importableItemsRepo.get(groupId, itemNo)
					.orElseThrow(() -> new RuntimeException("not found: " + groupId + ", " + itemNo));
		}
		
		@Override
		public Optional<ImportingUserCondition> getImportingUserCondition(String settingCode,
				int itemNo) {
			return importingUserConditionRepo.get(companyId, settingCode, itemNo);
		}
		
		
		/***** Workspace *****/
		
		@Override
		public void setupWorkspace(ExecutionContext context) {
			workspaceRepo.setup(this, context);
			existingRepo.setup(context);
			metaRepo.setup(context);
		}
		
		@Override
		public void save(ExecutionContext context, AnyRecordToDelete toDelete) {
			existingRepo.save(context, toDelete);
		}
		
		@Override
		public void save(ExecutionContext context, AnyRecordToChange recordToChange) {
			existingRepo.save(context, recordToChange);
		}
		
		@Override
		public void save(ExecutionContext context, RevisedDataRecord revisedDataRecord) {
			revisedDataRecordRepo.save(this, context, revisedDataRecord);
		}
		
		@Override
		public void save(ExecutionContext context, CanonicalizedDataRecord canonicalizedDataRecord) {
			canonicalizedDataRecordRepo.save(this, context, canonicalizedDataRecord);
		}
		
		@Override
		public int getMaxRowNumberOfRevisedData(ExecutionContext context) {
			return revisedDataRecordRepo.getMaxRowNumber(this, context);
		}
		
		@Override
		public List<String> getStringsOfRevisedData(ExecutionContext context, int itemNo) {
			return revisedDataRecordRepo.getStrings(this, context, itemNo);
		}
		
		@Override
		public Optional<RevisedDataRecord> getRevisedDataRecordByRowNo(ExecutionContext context, int rowNo) {
			return revisedDataRecordRepo.findByRowNo(this, context, rowNo);
		}
		
		@Override
		public List<RevisedDataRecord> getRevisedDataRecordWhere(
				ExecutionContext context, int itemNoCondition, String conditionString) {
			return revisedDataRecordRepo.findByCriteria(this, context, itemNoCondition, conditionString);
		}
		
		@Override
		public void save(ImportingDataMeta meta) {
			metaRepo.save(meta);
		}
		
		
		/***** domains for canonicalization *****/
		
		@Override
		public Optional<EmployeeDataMngInfo> getEmployeeDataMngInfoByEmployeeCode(String employeeCode) {
			return employeeDataMngInfoRepo.findByScdNotDel(employeeCode, companyId);
		}
		
		@Override
		public Optional<EmploymentHistory> getEmploymentHistory(String employeeId) {
			return employmentHistoryRepo.getByEmployeeIdDesc(companyId, employeeId);
		}
		
		public Optional<Task> getTask(String companyId, int taskFrameNo, String taskCode) {
			return taskingRepo.getOptionalTask(companyId, new TaskFrameNo(taskFrameNo), new TaskCode(taskCode));
		}
		
		@Override
		public Optional<WorkInfoOfDailyPerformance> getWorkInfoOfDailyPerformance(String employeeId, GeneralDate date) {
			return workInformationRepo.find(employeeId, date);
		}

		@Override
		public boolean existsDomainData(DomainDataId id) {
			return domainDataRepo.exists(id);
		}

		@Override
		public Optional<AffClassHistory> getAffClassHistory(String employeeId) {
			return affClassHistoryRepo.getByEmployeeId(companyId, employeeId);
		}

		@Override
		public Optional<AffJobTitleHistory> getAffJobTitleHistory(String employeeId) {
			return affJobTitleHistoryRepo.getListBySid(companyId, employeeId);
		}

	}
}
