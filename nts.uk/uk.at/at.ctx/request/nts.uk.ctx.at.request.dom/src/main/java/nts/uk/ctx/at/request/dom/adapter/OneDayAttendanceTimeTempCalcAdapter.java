package nts.uk.ctx.at.request.dom.adapter;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 1日分の勤怠時間を仮計算Adapter
 */
public interface OneDayAttendanceTimeTempCalcAdapter {
    /**
     * 計算する
     * @param params
     * @return
     */
    IntegrationOfDaily calculate(CalculationParams params);
}
