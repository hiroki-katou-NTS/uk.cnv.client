package nts.uk.file.at.app.export.outputworkstatustable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.dto.EmployeeInfor;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureDateDto;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class OutputFileWorkStatusFileQuery {
    //「１」ExcelPdf区分
    private int mode;
    //「２」対象年月
    private int targetDate;
    //「３」List<抽出社員>
    private List<String> lstEmpIds;
    // 「４」定型自由区分
    private int standardFreeClassification;

    //「５」項目選択の設定ID
    private String settingId;

    //「６」ゼロ表示区分
    private boolean isZeroDisplay;

    //「７」改ページ指定選択肢
    private boolean pageBreak;

    // 締め日
    private int closureId;

}
