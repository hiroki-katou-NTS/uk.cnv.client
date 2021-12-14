package nts.uk.screen.at.app.ktgwidget.ktg004;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetVacationLeftOutput {
    
    // 対象社員の残数情報
    RemainingNumberInforDto remainingNumberInforDto;
    
    // 休暇設定
    VacationSetting vacationSetting;
}
