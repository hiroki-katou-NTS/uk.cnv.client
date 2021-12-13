package nts.uk.screen.at.app.ksu003.d.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeForm;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import java.util.List;

@AllArgsConstructor
@Getter
public class ScheFuncControlDto {
    // 時刻修正できる勤務形態
    private List<Integer> changeableWorks;

    // 実績表示できるか
    private boolean isDisplayActual;

    // 表示可能勤務種類制御
    private Integer displayWorkTypeControl;

    // 表示可能勤務種類リスト
    private List<String> displayableWorkTypeCodeList;
}
