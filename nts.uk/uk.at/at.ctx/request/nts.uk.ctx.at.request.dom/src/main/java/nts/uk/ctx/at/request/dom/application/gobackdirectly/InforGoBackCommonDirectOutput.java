package nts.uk.ctx.at.request.dom.application.gobackdirectly;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.setting.request.gobackdirectlycommon.GoBackDirectlyCommonSetting;
import nts.uk.ctx.at.request.dom.setting.request.gobackdirectlycommon.GoBackReflect;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
@Data
@AllArgsConstructor
@NoArgsConstructor
//直行直帰申請起動時の表示情報
public class InforGoBackCommonDirectOutput {
//	勤務種類初期選択
	private InforWorkType workType;
//	就業時間帯初期選択
	private InforWorkTime workTime;
//	申請表示情報
	private AppDispInfoStartupOutput appDispInfoStartup;
//	直行直帰申請の反映
	private GoBackReflect goBackReflect;
//	勤務種類リスト
	private List<WorkType> lstWorkType;
//	直行直帰申請
	private Optional<GoBackAplication> goBackApplication;
}
