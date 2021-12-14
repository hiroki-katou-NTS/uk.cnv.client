package nts.uk.ctx.at.request.app.find.application.appabsence.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class ChangeWorkTimeParam {

    // 社員ID
    String sId;
    
    // 年月日
    String date;
    
    // 勤務種類コード
    String workTypeCd;
    
    // 就業時間帯コード
    String workTimeCd;
    
    // 休暇申請起動時の表示情報
    AppAbsenceStartInfoDto appAbsenceStartInfo;
}
