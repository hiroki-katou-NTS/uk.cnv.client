package nts.uk.file.at.app.export.holidayconfirmationtable;

import lombok.val;
import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.app.query.holidayconfirmationtable.CreateDisplayContentOfTheSubstituteLeaveQuery;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.dto.EmployeeInfor;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffAtWorkplaceImport;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeBasicInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceConfigInfoAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceInfor;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureIdPresentClosingPeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureIdPresentClosingPeriods;
import nts.uk.ctx.bs.company.dom.company.CompanyRepository;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.SysAuthWorkplaceAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class OutputTraceConfirmationTableService extends ExportService<CreateTraceConfirmationTableFileQuery> {
    @Inject
    OutputTraceConfirmTableReportGenerator reportGenerator;

    @Inject
    private GetClosureIdPresentClosingPeriods getClosureIdPresentClosingPeriods;

    @Inject
    private EmpEmployeeAdapter empEmployeeAdapter;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private AffWorkplaceAdapter affWorkplaceAdapter;

    @Inject
    private SysAuthWorkplaceAdapter sysAuthWorkplaceAdapter;

    @Inject
    private WorkplaceConfigInfoAdapter workplaceConfigInfoAdapter;

    @Inject
    private CompensLeaveComSetRepository compensLeaveComSetRepo;

    @Inject
    private CreateDisplayContentOfTheSubstituteLeaveQuery leaveQuery;

    @Inject
    private CompanyBsAdapter companyBsAdapter;


    @Override
    protected void handle(ExportServiceContext<CreateTraceConfirmationTableFileQuery> exportServiceContext) {
        val query = exportServiceContext.getQuery();
        val lstEmpIds = query.getListEmployeeId();
        val cid = AppContexts.user().companyId();
        // 1-- ???= call ????????????????????????????????????????????????????????????
        List<ClosureIdPresentClosingPeriod> closingPeriods = getClosureIdPresentClosingPeriods.get(cid);
        // 1.1-???
        Optional<ClosureIdPresentClosingPeriod> employeeBasicInfoImport =
                closingPeriods.stream().filter(x -> x.getClosureId().equals(1)).findFirst();
        DatePeriod datePeriod = null;
        GeneralDate endDate = null;
        GeneralDate startDate = null;
        if (employeeBasicInfoImport.isPresent()) {
            YearMonth processingYm = employeeBasicInfoImport.get()
                    .getCurrentClosingPeriod().getProcessingYm();
            startDate = processingYm.firstGeneralDate();
            endDate = processingYm.lastGeneralDate();

            datePeriod = new DatePeriod(startDate, endDate);
        }

        // 2--???= Call [No.600]??????ID???List?????????????????????????????????????????????????????????????????????
        List<EmployeeBasicInfoImport> lstEmployeeInfo = empEmployeeAdapter.getEmpInfoLstBySids(lstEmpIds, datePeriod, true, false);
        GeneralDate referenceDate = endDate;
        val listemployees = lstEmployeeInfo.stream()
                .map(EmployeeBasicInfoImport::getSid).collect(Collectors.toList());
        // 3--???= Call ?????????????????????
        CompanyBsImport companyInfo = companyBsAdapter.getCompanyByCid(cid);

        // 4--???= Call ??????ID???List?????????????????????????????????ID?????????
        List<AffAtWorkplaceImport> lstAffAtWorkplaceImport = affWorkplaceAdapter
                .findBySIdAndBaseDate(listemployees, referenceDate);

        List<EmployeeInfor> employeeInfoList = new ArrayList<>();
        lstEmployeeInfo.forEach(e -> {
            val wpl = lstAffAtWorkplaceImport.stream().filter(i -> i.getEmployeeId().equals(e.getSid())).findFirst();
            employeeInfoList.add(new EmployeeInfor(
                    e.getSid(),
                    e.getEmployeeCode(),
                    e.getEmployeeName(),
                    wpl.isPresent() ? wpl.get().getWorkplaceId() : null
            ));
        });
        //4.1-???
        // [No.560]??????ID?????????????????????????????????????????????
        List<WorkplaceInfor> lstWorkplaceInfo = workplaceConfigInfoAdapter.getWorkplaceInforByWkpIds(
                cid,
                lstAffAtWorkplaceImport.stream().map(AffAtWorkplaceImport::getWorkplaceId).distinct().collect(Collectors.toList()),
                referenceDate
        );

        // 5 - ??? get ??????????????????
        CompensatoryLeaveComSetting comSubstVacation = compensLeaveComSetRepo.find(cid);

        Integer mngUnit = null; // ????????????(1:????????????/2:????????????)
        boolean linkingMng = false;
        ManageDistinct isManaged = null;
        if (comSubstVacation != null) {
            isManaged = comSubstVacation.getIsManaged();
            if (comSubstVacation.getIsManaged() == ManageDistinct.NO) {
                mngUnit = 1;
                linkingMng = false;
            } else if (comSubstVacation.getIsManaged() == ManageDistinct.YES
                    && comSubstVacation.getCompensatoryDigestiveTimeUnit().getIsManageByTime() == ManageDistinct.NO
                    && comSubstVacation.getLinkingManagementATR() == ManageDistinct.YES) {
                mngUnit = 1;
                linkingMng = true;
            } else if (comSubstVacation.getIsManaged() == ManageDistinct.YES
                    && comSubstVacation.getCompensatoryDigestiveTimeUnit().getIsManageByTime() == ManageDistinct.NO
                    && comSubstVacation.getLinkingManagementATR() == ManageDistinct.NO) {
                mngUnit = 1;
                linkingMng = false;

            } else if (comSubstVacation.getIsManaged() == ManageDistinct.YES
                    && comSubstVacation.getCompensatoryDigestiveTimeUnit().getIsManageByTime() == ManageDistinct.YES) {
                mngUnit = 2;
                linkingMng = false;
            }
        }

        val listDetail = leaveQuery.getDisplayContent(
                referenceDate,
                employeeInfoList,
                isManaged,
                mngUnit,
                linkingMng,
                query.isMoreSubstituteHolidaysThanHolidays(),
                query.isMoreHolidaysThanSubstituteHolidays(),
                lstWorkplaceInfo);
        val dataSource = new OutputTraceConfirmTableDataSource(listDetail, companyInfo,query,linkingMng, mngUnit,comSubstVacation);
        // 5-- ??? get ??????????????????
        reportGenerator.generate(exportServiceContext.getGeneratorContext(), dataSource);
    }
}
