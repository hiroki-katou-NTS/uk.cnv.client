package nts.uk.file.at.app.export.holidayconfirmationtable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 振休発生取得情報
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HolidayAcquisitionInfo {
    // 繰越数
    private double carryForwardNumber;

    // 発生数
    private double occurrencesNumber;

    // 使用数
    private double usedNumber;

    // 残数
    private double remainingNumber;

    // 未消化数
    private double undigestedNumber;

    // ER
    private boolean error;

    // 振休発生取得情報
    private List<OccurrenceAcquisitionDetail> occurrenceAcquisitionDetails;

    // 紐付け情報
    private List<LinkingInfo> linkingInfos;
}
