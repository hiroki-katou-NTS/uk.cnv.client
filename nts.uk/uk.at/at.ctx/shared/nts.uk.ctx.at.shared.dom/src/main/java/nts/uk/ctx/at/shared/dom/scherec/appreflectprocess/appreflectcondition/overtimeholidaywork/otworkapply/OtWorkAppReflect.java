package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.AppOverTimeShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectWorkInformation;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.workchangeapp.ReflectWorkChangeApp.WorkInfoDto;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * refactor4 refactor 4
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.shared(勤務予定、勤務実績).申請反映処理.申請反映条件.残業・休日出勤.残業申請.残業申請の反映
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OtWorkAppReflect {
    /**
     * 事前
     */
    private BeforeOtWorkAppReflect before;

    /**
     * 事後
     */
    private AfterOtWorkAppReflect after;

	/**
	 * 実績の勤務情報へ反映する
	 */
	private NotUseAtr reflectActualWorkAtr;

    public static OtWorkAppReflect create(int reflectActualWorkAtr, int reflectWorkInfoAtr, int reflectActualOvertimeHourAtr, int reflectBeforeBreak, int workReflect, int reflectPaytime, int reflectDivergence, int reflectBreakOuting) {
        return new OtWorkAppReflect(
                BeforeOtWorkAppReflect.create(reflectWorkInfoAtr, reflectActualOvertimeHourAtr, reflectBeforeBreak),
                AfterOtWorkAppReflect.create(workReflect, reflectPaytime, reflectDivergence, reflectBreakOuting),
                EnumAdaptor.valueOf(reflectActualWorkAtr, NotUseAtr.class)
        );
    }
    
	/**
	 * @author thanh_nx
	 *
	 *         残業申請の反映（勤務実績）
	 */

	public List<Integer> process(RequireRC require, String cid, AppOverTimeShare overTimeApp,
			DailyRecordOfApplication dailyApp) {

		List<Integer> lstId = new ArrayList<Integer>();
		// [実績の勤務情報へ反映する]をチェック
		if (this.getReflectActualWorkAtr() == NotUseAtr.USE) {
			// [勤務情報]を勤務情報DTOへセット
			WorkInfoDto workInfoDto = overTimeApp.getWorkInfoOp().map(x -> {
				return new WorkInfoDto(Optional.ofNullable(x.getWorkTypeCode()), x.getWorkTimeCodeNotNull());
			}).orElse(new WorkInfoDto(Optional.empty(), Optional.empty()));

			// 勤務情報の反映
			lstId.addAll(ReflectWorkInformation.reflectInfo(require, cid, workInfoDto, dailyApp, Optional.of(true),
					Optional.of(true)));
		}

		// [input. 残業申請. 事前事後区分]をチェック
		if (overTimeApp.getPrePostAtr() == PrePostAtrShare.PREDICT) {
			// 事前残業申請の反映
			this.getBefore().processRC(require, cid, overTimeApp, dailyApp);
		}

		if (overTimeApp.getPrePostAtr() == PrePostAtrShare.POSTERIOR) {
			// 事後残業申請の反映
			this.getAfter().processAfter(require, cid, overTimeApp, dailyApp);
		}

		return lstId;
	}

	public static interface RequireRC extends ReflectWorkInformation.Require, BeforeOtWorkAppReflect.Require,
	AfterOtWorkAppReflect.RequireAfter {

	}

	public static interface RequireSC extends BeforeOtWorkAppReflect.Require {

	}
}
