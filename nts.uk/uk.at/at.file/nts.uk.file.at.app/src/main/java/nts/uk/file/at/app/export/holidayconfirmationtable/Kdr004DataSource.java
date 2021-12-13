package nts.uk.file.at.app.export.holidayconfirmationtable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Kdr004DataSource {
    private List<HolidayConfirmationTableContent> contents;
    private CompanyBsImport companyInfo;
    private Optional<ComSubstVacation> comSubstVacationSetting;
    private boolean holidayMng;
    private boolean linking;
    private int howToPrintDate;
    private int pageBreak;
}
