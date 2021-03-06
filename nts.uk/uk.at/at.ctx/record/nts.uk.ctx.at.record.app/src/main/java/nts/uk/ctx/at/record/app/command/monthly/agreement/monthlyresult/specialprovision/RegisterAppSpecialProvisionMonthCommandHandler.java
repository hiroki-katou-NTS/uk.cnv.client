package nts.uk.ctx.at.record.app.command.monthly.agreement.monthlyresult.specialprovision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.adapter.classification.affiliate.AffClassificationSidImport;
import nts.uk.ctx.at.record.dom.adapter.employment.SyEmploymentImport;
import nts.uk.ctx.at.record.dom.adapter.personempbasic.EmployeeInfor;
import nts.uk.ctx.at.record.dom.adapter.personempbasic.PersonEmpBasicInfoAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.SWkpHistRcImported;
import nts.uk.ctx.at.record.dom.adapter.workplace.SyWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.monthly.agreement.approver.AppCreationResult;
import nts.uk.ctx.at.record.dom.monthly.agreement.approver.Approver36AgrByCompany;
import nts.uk.ctx.at.record.dom.monthly.agreement.approver.Approver36AgrByCompanyRepo;
import nts.uk.ctx.at.record.dom.monthly.agreement.approver.Approver36AgrByWorkplace;
import nts.uk.ctx.at.record.dom.monthly.agreement.approver.Approver36AgrByWorkplaceRepo;
import nts.uk.ctx.at.record.dom.monthly.agreement.approver.OneMonthAppCreate;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.AggregateAgreementTimeByYM;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.AggregateAgreementTimeByYear;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.AgreementExcessInfo;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.GetExcessTimesYear;
import nts.uk.ctx.at.record.dom.monthly.agreement.monthlyresult.approveregister.UnitOfApprover;
import nts.uk.ctx.at.record.dom.monthly.agreement.monthlyresult.approveregister.UnitOfApproverRepo;
import nts.uk.ctx.at.record.dom.monthly.agreement.monthlyresult.specialprovision.CheckErrorApplicationMonthService;
import nts.uk.ctx.at.record.dom.monthly.agreement.monthlyresult.specialprovision.SpecialProvisionsOfAgreement;
import nts.uk.ctx.at.record.dom.monthly.agreement.monthlyresult.specialprovision.SpecialProvisionsOfAgreementRepo;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.standardtime.repository.AgreementOperationSettingRepository;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreMaxAverageTimeMulti;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeYear;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfClassification;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfCompany;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfEmployment;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.AgreementTimeOfWorkPlace;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.enums.LaborSystemtAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.setting.AgreementOperationSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.setting.AgreementUnitSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.BasicAgreementSettingForCalc;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.shr.com.context.AppContexts;

/**
 * 36??????????????????????????????????????????????????????1?????????
 *
 * @author Le Huu Dat
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterAppSpecialProvisionMonthCommandHandler
        extends CommandHandlerWithResult<List<RegisterAppSpecialProvisionMonthCommand>, List<ErrorResultDto>> {

    @Inject
    private RecordDomRequireService requireService;
    @Inject
    private SpecialProvisionsOfAgreementRepo specialProvisionsOfAgreementRepo;
    @Inject
    private Approver36AgrByCompanyRepo approver36AgrByCompanyRepo;
    @Inject
    private UnitOfApproverRepo unitOfApproverRepo;
    @Inject
    private SyWorkplaceAdapter syWorkplaceAdapter;
    @Inject
    private Approver36AgrByWorkplaceRepo approver36AgrByWorkplaceRepo;
    @Inject
    private AffWorkplaceAdapter affWorkplaceAdapter;
    @Inject
    private PersonEmpBasicInfoAdapter personEmpBasicInfoAdapter;
    @Inject
    private AgreementOperationSettingRepository agreementOperationSettingRepository;

    @Override
    protected List<ErrorResultDto> handle(CommandHandlerContext<List<RegisterAppSpecialProvisionMonthCommand>> context) {
        String cid = AppContexts.user().companyId();
        String sid = AppContexts.user().employeeId();
        RequireImpl require = new RequireImpl(cid, requireService.createRequire(),
                specialProvisionsOfAgreementRepo, approver36AgrByCompanyRepo,
                unitOfApproverRepo, syWorkplaceAdapter, approver36AgrByWorkplaceRepo,
                affWorkplaceAdapter,agreementOperationSettingRepository);
        List<RegisterAppSpecialProvisionMonthCommand> commands = context.getCommand();
        List<ErrorResultDto> errorResults = new ArrayList<>();
        List<AppCreationResult> results = new ArrayList<>();
        for (RegisterAppSpecialProvisionMonthCommand command : commands) {
            // 1???????????????????????????
            AppCreationResult result = OneMonthAppCreate.create(require, cid, sid, command.getContent().toMonthlyAppContent(),
                    command.getScreenInfo().toScreenDisplayInfo());
            results.add(result);
        }

        // get errors
        for (AppCreationResult result : results){
            List<ExcessErrorContentDto> errors = result.getErrorInfo().stream().map(ExcessErrorContentDto::new).collect(Collectors.toList());
            if (!CollectionUtil.isEmpty(errors)) {
                errorResults.add(new ErrorResultDto(result.getEmpId(),
                        null, null, errors));
            }
        }

        // execute
        if (CollectionUtil.isEmpty(errorResults)) {
            for (AppCreationResult result : results){
                if (result.getAtomTask().isPresent()) {
                    transaction.execute(result.getAtomTask().get());
                }
            }
        }

        // ??????ID???????????????????????????????????????
        List<String> employeeIds = errorResults.stream().map(ErrorResultDto::getEmployeeId)
                .distinct().collect(Collectors.toList());
        Map<String, EmployeeInfor> empInfo = personEmpBasicInfoAdapter.getPerEmpBasicInfo(employeeIds)
                .stream().collect(Collectors.toMap(EmployeeInfor::getEmployeeId, c -> c));
        errorResults.forEach(x -> x.mappingEmpInfo(empInfo));

        return errorResults;
    }


    @AllArgsConstructor
    private class RequireImpl implements OneMonthAppCreate.Require {

        private String companyId;
        private RecordDomRequireService.Require require;
        private SpecialProvisionsOfAgreementRepo specialProvisionsOfAgreementRepo;
        private Approver36AgrByCompanyRepo approver36AgrByCompanyRepo;
        private UnitOfApproverRepo unitOfApproverRepo;
        private SyWorkplaceAdapter syWorkplaceAdapter;
        private Approver36AgrByWorkplaceRepo approver36AgrByWorkplaceRepo;
        private AffWorkplaceAdapter affWorkplaceAdapter;
        private AgreementOperationSettingRepository agreementOperationSettingRepository;


        @Override
        public void addApp(SpecialProvisionsOfAgreement app) {
            specialProvisionsOfAgreementRepo.insert(app);
        }

        @Override
        public Optional<Approver36AgrByCompany> getApproverHistoryItem(GeneralDate baseDate) {
            return approver36AgrByCompanyRepo.getByCompanyIdAndDate(companyId, baseDate);
        }

        @Override
        public UnitOfApprover getUsageSetting() {
            return unitOfApproverRepo.getByCompanyId(companyId);
        }

        @Override
        public Optional<SWkpHistRcImported> getYourWorkplace(String employeeId, GeneralDate baseDate) {
            return syWorkplaceAdapter.findBySid(employeeId, baseDate);
        }

        @Override
        public Optional<Approver36AgrByWorkplace> getApproveHistoryItem(String workplaceId, GeneralDate baseDate) {
            return approver36AgrByWorkplaceRepo.getByWorkplaceIdAndDate(workplaceId, baseDate);
        }

        @Override
        public List<String> getUpperWorkplace(String workplaceID, GeneralDate date) {
            return affWorkplaceAdapter.getUpperWorkplace(companyId, workplaceID, date);
        }

        @Override
        public Optional<AgreementUnitSetting> agreementUnitSetting(String companyId) {
            return require.agreementUnitSetting(companyId);
        }

        @Override
        public Optional<AffClassificationSidImport> affEmployeeClassification(String companyId, String employeeId, GeneralDate baseDate) {
            return this.require.affEmployeeClassification(companyId, employeeId, baseDate);
        }

        @Override
        public Optional<AgreementTimeOfClassification> agreementTimeOfClassification(String companyId, LaborSystemtAtr laborSystemAtr, String classificationCode) {
            return this.require.agreementTimeOfClassification(companyId, laborSystemAtr, classificationCode);

        }

        @Override
        public List<String> getCanUseWorkplaceForEmp(String companyId, String employeeId, GeneralDate baseDate) {
            return this.require.getCanUseWorkplaceForEmp(companyId, employeeId, baseDate);
        }

        @Override
        public Optional<AgreementTimeOfWorkPlace> agreementTimeOfWorkPlace(String workplaceId, LaborSystemtAtr laborSystemAtr) {
            return this.require.agreementTimeOfWorkPlace(workplaceId, laborSystemAtr);
        }

        @Override
        public Optional<SyEmploymentImport> employment(String companyId, String employeeId, GeneralDate baseDate) {
            return this.require.employment(companyId, employeeId, baseDate);
        }

        @Override
        public Optional<AgreementTimeOfEmployment> agreementTimeOfEmployment(String companyId, String employmentCategoryCode, LaborSystemtAtr laborSystemAtr) {
            return this.require.agreementTimeOfEmployment(companyId, employmentCategoryCode, laborSystemAtr);
        }

        @Override
        public Optional<AgreementTimeOfCompany> agreementTimeOfCompany(String companyId, LaborSystemtAtr laborSystemAtr) {
            return this.require.agreementTimeOfCompany(companyId, laborSystemAtr);
        }


        @Override
        public AgreMaxAverageTimeMulti getMaxAverageMulti(String sid, GeneralDate baseDate, YearMonth ym, Map<YearMonth, AttendanceTimeMonth> agreementTimes) {
            return AggregateAgreementTimeByYM.aggregate(this.require, sid, baseDate, ym, agreementTimes);
        }

        @Override
        public AgreementTimeYear timeYear(String sid, GeneralDate baseDate, nts.arc.time.calendar.Year year, Map<YearMonth, AttendanceTimeMonth> agreementTimes) {
            return AggregateAgreementTimeByYear.aggregate(createRequireY(), sid, baseDate, year, agreementTimes);
        }

        @Override
        public AgreementExcessInfo algorithm(CheckErrorApplicationMonthService.Require require, String employeeId, nts.arc.time.calendar.Year year) {
            return GetExcessTimesYear.get(this.require, employeeId, year);
        }

        @Override
        public Optional<AgreementOperationSetting> agreementOperationSetting(String cid) {
            return this.require.agreementOperationSetting(cid);
        }

        @Override
        public List<AgreementTimeOfManagePeriod> agreementTimeOfManagePeriod(List<String> sids, List<YearMonth> yearMonths) {
            return this.require.agreementTimeOfManagePeriod(sids, yearMonths);
        }

        @Override
        public Optional<WorkingConditionItem> workingConditionItem(String employeeId, GeneralDate baseDate) {
            return this.require.workingConditionItem(employeeId, baseDate);
        }

        private AggregateAgreementTimeByYear.RequireM1 createRequireY() {

            return new AggregateAgreementTimeByYear.RequireM1() {

                @Override
                public Optional<AgreementOperationSetting> agreementOperationSetting(String cid) {
                    return require.agreementOperationSetting(cid);
                }

                @Override
                public BasicAgreementSettingForCalc basicAgreementSetting(String cid, String sid, GeneralDate baseDate, nts.arc.time.calendar.Year year) {
                    return require.basicAgreementSetting(cid, sid, baseDate, year);
                }

                @Override
                public Optional<AgreementTimeOfManagePeriod> agreementTimeOfManagePeriod(String sid, YearMonth ym) {
                    return require.agreementTimeOfManagePeriod(sid, ym);
                }
            };
        }

        @Override
        public Optional<AgreementOperationSetting> find() {
            return agreementOperationSettingRepository.find(AppContexts.user().companyId());
        }

		@Override
		public List<WorkingConditionItem> workingConditionItemClones(List<String> employeeId, GeneralDate baseDate) {
			return this.require.workingConditionItemClones(employeeId, baseDate);
		}
    }
}
