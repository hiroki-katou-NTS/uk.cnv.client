package nts.uk.ctx.at.shared.app.find.worktime.worktimeset.dto;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeDto;
import nts.uk.ctx.at.shared.app.find.worktime.filtercriteria.WorkHoursFilterConditionDto;

/**
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL001_就業時間帯選択.A：就業時間帯選択.アルゴリズム.起動.就業時間帯リストの取得結果
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WorkTimeResultDto {
	// 全件チェック状態
    private Integer allCheckStatus;
    // 全件の就業時間帯
    private List<WorkTimeDto> allWorkHours;
    // 選択可能な就業時間帯
    private List<WorkTimeDto> availableWorkingHours;
    // 職場別の就業時間帯
    private List<WorkTimeDto> workingHoursByWorkplace;
    private String companyID;
    private Integer useATR;
    private List<WorkHoursFilterConditionDto> filterConditions;
}
