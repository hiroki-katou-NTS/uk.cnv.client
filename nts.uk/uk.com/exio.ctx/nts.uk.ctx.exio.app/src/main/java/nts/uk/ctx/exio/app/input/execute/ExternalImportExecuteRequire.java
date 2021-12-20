package nts.uk.ctx.exio.app.input.execute;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import nts.arc.diagnose.stopwatch.embed.EmbedStopwatch;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.cnv.core.dom.conversionsql.ConversionSQL;
import nts.uk.cnv.core.dom.conversiontable.ConversionCodeType;
import nts.uk.cnv.core.dom.conversiontable.ConversionSource;
import nts.uk.cnv.core.dom.conversiontable.ConversionTable;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceConfigurationRepository;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformationRepository;
import nts.uk.ctx.bs.person.dom.person.info.PersonRepository;
import nts.uk.ctx.exio.dom.input.ExecuteImporting;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataId;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataRepository;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToChange;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToDelete;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.ExternalImportExistingRepository;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalizedDataRecordRepository;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomain;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainRepository;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrorsRepository;
import nts.uk.ctx.exio.dom.input.manage.ExternalImportCurrentState;
import nts.uk.ctx.exio.dom.input.manage.ExternalImportCurrentStateRepository;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMetaRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.dom.input.transfer.ConversionTableRepository;
import nts.uk.ctx.exio.dom.input.transfer.TransferCanonicalDataRepository;
import nts.uk.ctx.exio.dom.input.workspace.ExternalImportWorkspaceRepository;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspace;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspaceRepository;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUserRepository;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;
import nts.uk.shr.com.history.DateHistoryItem;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExternalImportExecuteRequire {

	public Require create(String companyId) {
		return EmbedStopwatch.embed(new RequireImpl(companyId));
	}
	
	public static interface Require extends
			ExternalImportCurrentState.Require,
			ExecuteImporting.Require,
			ExternalImportWorkspaceRepository.Require {
		
		ExternalImportSetting getExternalImportSetting(ExternalImportCode settingCode);
		
		ExternalImportCurrentState getExternalImportCurrentState();
	}
	
	@Inject
	private ExternalImportCurrentStateRepository currentStateRepo;
	
	@Inject
	private ExternalImportErrorsRepository errorsRepo;
	
	@Inject
	private ExternalImportSettingRepository settingRepo;
	
	@Inject
	private ImportingDomainRepository importingDomainRepo;
	
	@Inject
	private ConversionTableRepository conversionTableRepo;
	
	@Inject
	private TransferCanonicalDataRepository transferCanonicalDataRepo;
	
	@Inject
	private DomainWorkspaceRepository domainWorkspaceRepo;
	
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
	private PersonRepository personRepo;
	
	@Inject
	private UserRepository userRepo;
	
	@Inject
	private LoginPasswordOfUserRepository loginPasswordRepo;
	
	@Inject
	private SubstitutionOfHDManaDataRepository substitutionOfHdManaDataRepo;
	
	@Inject
	private PayoutManagementDataRepository payoutManagementDataRepo;
	
	@Inject
	private ComDayOffManaDataRepository comDayOffManaDataRepo;
	
	@Inject
	private LeaveManaDataRepository leaveManaDataRepo;
	
	@Inject
	private WorkplaceConfigurationRepository wkpConfigRepo;

	@Inject
	private StampCardRepository stampCardRepo;
	
	@Inject
	private WorkplaceInformationRepository wkpInfoRepo;
	
	
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public class RequireImpl implements Require {
		
		@SuppressWarnings("unused")
		private final String companyId;

		@Override
		public ExternalImportCurrentState getExternalImportCurrentState() {
			return currentStateRepo.find(companyId);
		}

		@Override
		public void update(ExternalImportCurrentState currentState) {
			currentStateRepo.save(currentState);
		}

		@Override
		public void add(ExternalImportError error) {
			errorsRepo.add(companyId, error);
		}
		
		@Override
		public ExternalImportSetting getExternalImportSetting(ExternalImportCode settingCode) {
			return settingRepo.get(Optional.empty(), companyId, settingCode)
					.orElseThrow(() -> new RuntimeException("not found: " + companyId + ", " + settingCode));
		}

		@Override
		public ImportingDomain getImportingDomain(ImportingDomainId domainId) {
			return importingDomainRepo.find(domainId);
		}
		
		@Override
		public DomainWorkspace getDomainWorkspace(ImportingDomainId domainId) {
			return domainWorkspaceRepo.get(domainId);
		}

		@Override
		public List<String> getAllEmployeeIdsOfCanonicalizedData(ExecutionContext context) {
			return canonicalizedDataRecordRepo.getAllEmployeeIds(this, context);
		}

		@Override
		public List<AnyRecordToChange> getAllAnyRecordToChange(ExecutionContext context) {
			return existingRepo.findAllChanges(context);
		}

		@Override
		public List<AnyRecordToDelete> getAllAnyRecordToDelete(ExecutionContext context) {
			return existingRepo.findAllDeletes(context);
		}

		@Override
		public List<AnyRecordToChange> getAnyRecordToChangeWhere(ExecutionContext context, int keyItemNo, String keyValue) {
			return existingRepo.findAllChangesWhere(context, keyItemNo, keyValue);
		}

		@Override
		public List<AnyRecordToDelete> getAnyRecordToDeleteWhere(ExecutionContext context, int keyItemNo, String keyValue) {
			return existingRepo.findAllDeletesWhere(context, keyItemNo, keyValue);
		}

		@Override
		public ConversionSource getConversionSource(String domainName) {
			return conversionTableRepo.getSource(domainName);
		}
		
		@Override
		public List<ConversionTable> getConversionTable(ConversionSource source, String domainName, ConversionCodeType cct) {
			return conversionTableRepo.get(domainName, source, cct);
		}

		@Override
		public int execute(List<ConversionSQL> conversionSqls) {
			return transferCanonicalDataRepo.execute(conversionSqls);
		}

		@Override
		public ImportingDataMeta getImportingDataMeta(ExecutionContext context) {
			return metaRepo.find(context);
		}

		@Override
		public void deleteDomainData(DomainDataId id) {
			domainDataRepo.delete(id);
		}

		@Override
		public void delete(DomainDataId id) {
			domainDataRepo.delete(id);			
		}

		@Override
		public void update(DomainDataId id, DatePeriod period) {
			domainDataRepo.update(id, period);
		}

		@Override
		public void deleteEmployeeDataMngInfo(String employeeId, String personId) {
			employeeDataMngInfoRepo.remove(employeeId, personId);
		}

		@Override
		public void deletePerson(String personId) {
			personRepo.delete(personId);
		}

		@Override
		public void deleteUser(String userId) {
			userRepo.delete(userId);
		}

		@Override
		public void deleteLoginPasswordOfUser(String userId) {
			loginPasswordRepo.delete(userId);
		}

		@Override
		public void deleteAllSubstitutionOfHDManagementData(String employeeId) {
			substitutionOfHdManaDataRepo.deleteAllByEmployeeId(employeeId);
		}

		@Override
		public void deleteAllPayoutManagementData(String employeeId) {
			payoutManagementDataRepo.deleteAllByEmployeeId(employeeId);
			
		}

		@Override
		public void deleteAllCompensatoryDayOffManaData(String employeeId) {
			comDayOffManaDataRepo.deleteAllByEmployeeId(employeeId);
		}

		@Override
		public void deleteAllLeaveManagementData(String employeeId) {
			leaveManaDataRepo.deleteAllByEmployeeId(employeeId);
		}

		@Override
		public void updateWorkplaceConfigurationHistoryItem(String companyId, DateHistoryItem historyItem) {
			wkpConfigRepo.updateHistoryItem(companyId, historyItem);
		}

		public void deleteStampCardById(String stampCardId) {
			stampCardRepo.delete(stampCardId);
		}

		@Override
		public void deleteStampCardByTenant(String tenantCode) {
			stampCardRepo.deleteByTenantCode(tenantCode);
		}

		@Override
		public void deleteWorkplaceConfigurationHistoryItem(String companyId, String historyId) {
			wkpConfigRepo.deleteWorkplaceConfig(companyId, historyId);
		}

		@Override
		public void deleteWorkplaceInformation(String companyId, String historyId) {
			wkpInfoRepo.deleteWorkplaceInforOfHistory(companyId, historyId);
		}
	}
}
