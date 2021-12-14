package nts.uk.screen.at.app.ktgwidget.ktg004;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author anhnm
 * 休暇設定
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VacationSetting {

    // 60H超休残数管理する
    boolean holiday60HManage;
    
    // 介護残数管理する
    boolean nursingManage;
    
    // 公休残数管理する
    boolean publicHolidayManage;
    
    // 子の看護残数管理する
    boolean childCaremanage;
    
    // 振休残数管理する
    boolean accomoManage;
    
    // 積立年休残数管理する
    boolean accumAnnualManage;
    
    // 代休残数管理する
    boolean substituteManage;
    
    // 代休時間残数管理する
    boolean substituteTimeManage;
    
    // 年休残数管理する
    boolean annualManage;
}
