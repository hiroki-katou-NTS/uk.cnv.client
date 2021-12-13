package nts.uk.file.at.app.export.holidayconfirmationtable;

import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffAtWorkplaceImport;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeBasicInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceConfigInfoAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.config.info.WorkplaceInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.ManagermentAtr;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureIdPresentClosingPeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureIdPresentClosingPeriods;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsAdapter;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 振休確認表を作成する
 */

@Stateless
public class HolidayConfirmationTableExportService extends ExportService<Kdr004ExportQuery> {
    @Inject
    private GetClosureIdPresentClosingPeriods getClosureIdPresentClosingPeriods;

    @Inject
    private EmpEmployeeAdapter empEmployeeAdapter;

    @Inject
    private CompanyBsAdapter companyBsAdapter;

    @Inject
    private AffWorkplaceAdapter affWorkplaceAdapter;

    @Inject
    private WorkplaceConfigInfoAdapter workplaceConfigInfoAdapter;

    @Inject
    private ComSubstVacationRepository comSubstVacationRepo;

    @Inject
    private CreateHolidayConfirmationTableContentsQuery contentsQuery;

    @Inject
    private HolidayConfirmationTableGenerator generator;

    @Override
    protected void handle(ExportServiceContext<Kdr004ExportQuery> exportServiceContext) {
        String companyId = AppContexts.user().companyId();
        Kdr004ExportQuery query = exportServiceContext.getQuery();

        // 全ての締めの処理年月と締め期間を取得する
        List<ClosureIdPresentClosingPeriod> closingPeriods = getClosureIdPresentClosingPeriods.get(companyId);

        // 締めID=1の期間を抽出
        ClosureIdPresentClosingPeriod closure1 = closingPeriods.stream().filter(x -> x.getClosureId().equals(1)).findFirst().get();

        // [No.600]社員ID（List）から社員コードと表示名を取得（削除社員考慮）
        YearMonth processingYm = closure1.getCurrentClosingPeriod().getProcessingYm();
        DatePeriod datePeriod = new DatePeriod(processingYm.firstGeneralDate(), processingYm.lastGeneralDate());
        List<EmployeeBasicInfoImport> lstEmployeeInfo = empEmployeeAdapter.getEmpInfoLstBySids(query.getEmployeeIds(), datePeriod, true, true);

        // 会社を取得する
        CompanyBsImport companyInfo = companyBsAdapter.getCompanyByCid(companyId);

        // 社員ID（List）と基準日から所属職場IDを取得
        List<AffAtWorkplaceImport> lstAffAtWorkplaceImport = affWorkplaceAdapter.findBySIdAndBaseDate(
                lstEmployeeInfo.stream().map(EmployeeBasicInfoImport::getSid).collect(Collectors.toList()),
                closure1.getCurrentClosingPeriod().getClosureEndDate()
        );

        // [No.560]職場IDから職場の情報をすべて取得する
        List<WorkplaceInfor> lstWorkplaceInfo = workplaceConfigInfoAdapter.getWorkplaceInforByWkpIds(
                companyId,
                lstAffAtWorkplaceImport.stream().map(AffAtWorkplaceImport::getWorkplaceId).distinct().collect(Collectors.toList()),
                closure1.getCurrentClosingPeriod().getClosureEndDate()
        );

        Optional<ComSubstVacation> comSubstVacation = comSubstVacationRepo.findById(companyId);

        // 振休確認表の表示内容を作成する
        int mngUnit = 1; // 管理単位(1:日数管理/2:時間管理) ===> only 1 for now
        boolean linkingMng, holidayMng;
        if (comSubstVacation.isPresent()) {
            if (comSubstVacation.get().getManageDistinct() == ManageDistinct.NO) {
//                mngUnit = 1;
                linkingMng = false;
                holidayMng = false;
            } else {
                holidayMng = true;
                if (comSubstVacation.get().getLinkingManagementATR() == ManageDistinct.YES) {
//                    mngUnit = 1;
                    linkingMng = true;
                } else {
//                    mngUnit = 1;
                    linkingMng = false;
                }
            }
        } else {
//            mngUnit = 1;
            linkingMng = false;
            holidayMng = false;
        }
        List<HolidayConfirmationTableContent> contents = contentsQuery.create(
                closure1.getCurrentClosingPeriod().getClosureEndDate(),
                lstEmployeeInfo,
                mngUnit,
                linkingMng,
                query.isHaveMoreHolidayThanDrawOut(),
                query.isHaveMoreDrawOutThanHoliday(),
                lstAffAtWorkplaceImport,
                lstWorkplaceInfo
        );

        Kdr004DataSource dataSource = new Kdr004DataSource(contents, companyInfo, comSubstVacation, holidayMng, linkingMng, query.getHowToPrintDate(), query.getPageBreak());
        generator.generate(exportServiceContext.getGeneratorContext(), dataSource);
    }
}
