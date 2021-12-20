package nts.uk.ctx.at.shared.pubimp.monthesultsvacationinfo.nursing.monthlyremainingnumberdata;

import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.shared.pub.monthesultsvacationinfo.nursing.monthlyremainingnumberdata.ChildNursingVacationMonRemainNumberDataPub;

import javax.inject.Inject;

public class ChildNursingVacationMonRemainNumberDataPubImpl implements ChildNursingVacationMonRemainNumberDataPub {
    @Override
    public Object getNursingOfEmployeeMonthlyProbatedChildren(String sid, YearMonthPeriod yearMonthPeriod) {
        //年月期間．開始年月から終了年月まで1か月ずつループ
        for (int i = 0; i < yearMonthPeriod.yearMonthsBetween().size() ; i++) {
            // ドメインモデル「子の看護休暇月別残数データ」を取

        }

        return null;
    }
}
