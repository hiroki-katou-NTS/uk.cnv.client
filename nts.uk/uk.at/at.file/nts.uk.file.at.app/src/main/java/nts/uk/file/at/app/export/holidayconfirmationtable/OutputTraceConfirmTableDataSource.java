package nts.uk.file.at.app.export.holidayconfirmationtable;


import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.function.app.query.holidayconfirmationtable.DisplayContentsOfSubLeaveConfirmationTable;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;

import java.util.List;

@AllArgsConstructor
@Getter
public class OutputTraceConfirmTableDataSource {
    private List<DisplayContentsOfSubLeaveConfirmationTable> contents;
    private CompanyBsImport companyInfo;
    private CreateTraceConfirmationTableFileQuery query;
    private boolean linking;
    private Integer mngUnit;
    private CompensatoryLeaveComSetting comSubstVacation;
}
