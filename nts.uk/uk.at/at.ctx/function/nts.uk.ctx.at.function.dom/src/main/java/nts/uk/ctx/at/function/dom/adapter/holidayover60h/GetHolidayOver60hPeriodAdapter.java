package nts.uk.ctx.at.function.dom.adapter.holidayover60h;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.holidayover60h.interim.TmpHolidayOver60hMng;

import java.util.List;
import java.util.Optional;

public interface GetHolidayOver60hPeriodAdapter {
    /**
     * [RQ677]期間中の60H超休残数を取得する
     * @param companyId 会社ID
     * @param employeeId 社員ID
     * @param aggrPeriod 集計期間
     * @param mode 実績のみ参照区分
     * @param criteriaDate 基準日
     * @param isOverWriteOpt 上書きフラグ
     * @param forOverWriteList 上書き用の暫定60H超休管理データ
     * @param prevHolidayOver60h 前回の60H超休の集計結果
     * @return 60H超休の集計結果
     */
    AggrResultOfHolidayOver60hImport algorithm(
            String companyId,
            String employeeId,
            DatePeriod aggrPeriod,
            InterimRemainMngMode mode,
            GeneralDate criteriaDate,
            Optional<Boolean> isOverWriteOpt,
            Optional<List<TmpHolidayOver60hMng>> forOverWriteList,
            Optional<AggrResultOfHolidayOver60hImport> prevHolidayOver60h);
}
