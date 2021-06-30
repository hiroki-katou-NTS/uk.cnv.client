﻿package nts.uk.ctx.exio.app.input.prepare;

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
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.dom.employment.history.EmploymentHistory;
import nts.uk.ctx.bs.employee.dom.employment.history.EmploymentHistoryRepository;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.PrepareImporting;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizedDataRecord;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToChange;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToDelete;
import nts.uk.ctx.exio.dom.input.canonicalize.groups.GroupCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.groups.GroupCanonicalizationRepository;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.importableitem.group.ImportingGroup;
import nts.uk.ctx.exio.dom.input.importableitem.group.ImportingGroupId;
import nts.uk.ctx.exio.dom.input.revise.ReviseItem;
import nts.uk.ctx.exio.dom.input.revise.reviseddata.RevisedDataRecord;
import nts.uk.ctx.exio.dom.input.revise.type.codeconvert.CodeConvertCode;
import nts.uk.ctx.exio.dom.input.revise.type.codeconvert.ExternalImportCodeConvert;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.dom.input.setting.assembly.ExternalImportAssemblyMethod;
import nts.uk.ctx.exio.dom.input.validation.ImportingUserConditionRepository;
import nts.uk.ctx.exio.dom.input.validation.condition.ImportingUserCondition;
import nts.uk.ctx.exio.dom.input.workspace.ExternalImportWorkspaceRepository;
import nts.uk.ctx.exio.dom.input.workspace.GroupWorkspace;

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
	private GroupCanonicalizationRepository groupCanonicalizationRepo;
	
	@Inject
	private ExternalImportWorkspaceRepository workspaceRepo;
	
	@Inject
	private EmployeeDataMngInfoRepository employeeDataMngInfoRepo;
	
	@Inject
	private EmploymentHistoryRepository employmentHistoryRepo;
	
	@Inject
	private TaskingRepository taskingRepo;
	
	@Inject
	private WorkInformationRepository workInformationRepo;
	
	public class RequireImpl implements Require {

		private final String companyId ;
		
		public RequireImpl(String companyId) {
			this.companyId = companyId;
		}
		
		
		/***** 外部受入関連 *****/

		@Override
		public Optional<ExternalImportSetting> getExternalImportSetting(String companyId,
				ExternalImportCode settingCode) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<ExternalImportAssemblyMethod> getAssemblyMethod(String companyId,
				ExternalImportCode settingCode) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ImportingGroup getImportingGroup(ImportingGroupId groupId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public GroupWorkspace getGroupWorkspace(ImportingGroupId groupId) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Optional<ReviseItem> getRevise(ExecutionContext context, int importItemNumber) {
			// TODO 1発目では対象外のため取得できない体で実装
			return Optional.empty();
		}

		@Override
		public ExternalImportCodeConvert getCodeConvert(CodeConvertCode code) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public ImportableItem getImportableItem(ImportingGroupId groupId, int itemNo) {
			return importableItemsRepo.find(groupId, itemNo).get();
		}

		@Override
		public List<ImportingUserCondition> getImportingUserCondition(String settingCode,
				List<Integer> itemNo) {
			return importingUserConditionRepo.get(companyId, settingCode, itemNo);
		}

		@Override
		public GroupCanonicalization getGroupCanonicalization(ImportingGroupId groupId) {
			return groupCanonicalizationRepo.find(groupId);
		}
		
		
		/***** Workspace *****/
		
		@Override
		public void setupWorkspace(ExecutionContext context) {
			workspaceRepo.createWorkspace(this, context);
		}

		@Override
		public void save(ExecutionContext context, AnyRecordToDelete toDelete) {
			workspaceRepo.save(context, toDelete);
		}

		@Override
		public void save(ExecutionContext context, AnyRecordToChange recordToChange) {
			workspaceRepo.save(context, recordToChange);
		}
		
		@Override
		public void save(ExecutionContext context, RevisedDataRecord revisedDataRecord) {
			
			workspaceRepo.save(this, context, revisedDataRecord);
		}
		
		@Override
		public void save(ExecutionContext context, CanonicalizedDataRecord canonicalizedDataRecord) {
			workspaceRepo.save(this, context, canonicalizedDataRecord);
		}

		@Override
		public int getMaxRowNumberOfRevisedData(ExecutionContext context) {
			return workspaceRepo.getMaxRowNumberOfRevisedData(context);
		}

		@Override
		public List<String> getStringsOfRevisedData(ExecutionContext context, int itemNo) {
			return workspaceRepo.getStringsOfRevisedData(context, itemNo);
		}

		@Override
		public Optional<RevisedDataRecord> getRevisedDataRecordByRowNo(ExecutionContext context, int rowNo) {
			return workspaceRepo.findRevisedByRowNo(context, rowNo);
		}

		@Override
		public List<RevisedDataRecord> getRevisedDataRecordWhere(
				ExecutionContext context, int itemNoCondition, String conditionString) {
			return workspaceRepo.findRevisedWhere(context, itemNoCondition, conditionString);
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

		@Override
		public Optional<Task> getTask(String companyId, int taskFrameNo, String taskCode) {
			return taskingRepo.getOptionalTask(companyId, new TaskFrameNo(taskFrameNo), new TaskCode(taskCode));
		}

		@Override
		public Optional<WorkInfoOfDailyPerformance> getWorkInfoOfDailyPerformance(String employeeId, GeneralDate date) {
			return workInformationRepo.find(employeeId, date);
		}


	}
}
