package nts.uk.ctx.at.function.app.query.holidayconfirmationtable;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * 代休発生取得情報
 */
@AllArgsConstructor
@Getter
@Setter
public class SubstituteHolidayOccurrenceInfo {
    private boolean er;
    // 使用数: 代休使用数合計
    private TotalNumberOfSubstituteHolidaysUsed numOfUse;
    // 未消化数 : 代休未消化数
    private NumberOfUndigestedSubstitutes undeterminedNumber;
    // 残数 : 代休残数
    private NumberOfSubstituteHoliday numberOfRemaining;
    //発生取得明細 : 発生取得明細
    private List<OccurrenceAcquisitionDetails> occurrenceAcquisitionDetailsList;
    //発生数 : 代休発生数合計
    private TotalNumberOfSubstituteHolidays totalNumberOfSubstituteHolidays;
    //紐付け情報 : 紐付け情報
    private List<LinkingInformation> listTyingInformation;
    //繰越数: 代休繰越数
    private NumberOfSubstituteHolidayCarriedForward numberCarriedForward;
}
