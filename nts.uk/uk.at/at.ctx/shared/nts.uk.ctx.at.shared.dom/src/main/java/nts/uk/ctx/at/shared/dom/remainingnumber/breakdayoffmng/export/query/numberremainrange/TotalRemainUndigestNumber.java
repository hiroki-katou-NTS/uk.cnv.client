package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.ManagementDataRemainUnit;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail.NumberConsecuVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.UnbalanceVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.SettingSubstituteHolidayProcess;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.SubstitutionHolidayOutput;

/**
 * @author ThanhNX
 *
 *         6.残数と未消化数を集計する
 */
public class TotalRemainUndigestNumber {

	private TotalRemainUndigestNumber() {
	};

	// 6.残数と未消化数を集計する
	public static RemainUndigestResult process(Require require, String companyId, String employeeID,
			GeneralDate baseDate, List<AccumulationAbsenceDetail> lstAccuAbsence, boolean isMode) {
		// 残日数 = 0、残時間数 = 0、未消化日数 = 0、未消化時間 = 0（初期化）
		double remainingDay = 0d, undigestDay = 0d;
		int remainingTime = 0, undigestTime = 0;

		// 10-2.代休の設定を取得する
		SubstitutionHolidayOutput substitutionHolidayOutput = SettingSubstituteHolidayProcess
				.getSettingForSubstituteHoliday(require, companyId, employeeID, baseDate);
		// 「休出代休明細」をループする
		for (AccumulationAbsenceDetail accuAbsence : lstAccuAbsence) {
			if (accuAbsence.getOccurrentClass() == OccurrenceDigClass.DIGESTION) {
				// 残日数 -= ループ中の「代休の未相殺」．未相殺日数、残時間 -= ループ中の「代休の未相殺」．未相殺時間
				remainingDay -= accuAbsence.getUnbalanceNumber().getDay().v();
				remainingTime -= accuAbsence.getUnbalanceNumber().getTime().isPresent()
						? accuAbsence.getUnbalanceNumber().getTime().get().v()
						: 0;
				continue;
			}

			if(substitutionHolidayOutput == null) {
				continue;
			}

			UnbalanceVacation vacation = (UnbalanceVacation) accuAbsence;
			// isMode = true => 月次
			if ((isMode && vacation.getDeadline().beforeOrEquals(baseDate))
					|| (!isMode && vacation.getDeadline().before(baseDate))) {
				// 未消化数を加算
				NumberConsecuVacation undigestDayTime = addUndigestNumber(accuAbsence,
						substitutionHolidayOutput.isTimeOfPeriodFlg());
				undigestTime += undigestDayTime.getTime().map(x -> x.v()).orElse(0);
				undigestDay += undigestDayTime.getDay().v();
			} else {
				remainingDay += accuAbsence.getUnbalanceNumber().getDay().v();
				remainingTime += accuAbsence.getUnbalanceNumber().getTime().isPresent()
						? accuAbsence.getUnbalanceNumber().getTime().get().v()
						: 0;
				if (!isMode && vacation.getDeadline().equals(baseDate)) {
					NumberConsecuVacation undigestDayTime = addUndigestNumber(accuAbsence,
							substitutionHolidayOutput.isTimeOfPeriodFlg());
					undigestTime += undigestDayTime.getTime().map(x -> x.v()).orElse(0);
					undigestDay += undigestDayTime.getDay().v();
				}
			}

		}
		return new RemainUndigestResult(remainingDay, remainingTime, undigestDay, undigestTime);
	}

	// 未消化数を加算
	private static NumberConsecuVacation addUndigestNumber(AccumulationAbsenceDetail accuAbsence,
			boolean timeOfPeriodFlg) {
		double undigestDay = 0d;
		int undigestTime = 0;
		// input.時間代休管理区分
		if (timeOfPeriodFlg) {
			// 未消化時間 += ループ中の「逐次発生の休暇明細」．未相殺．時間
			undigestTime = accuAbsence.getUnbalanceNumber().getTime().map(x -> x.v()).orElse(0);
		} else {
			// 未消化日数 += ループ中の「逐次発生の休暇明細」．未相殺．日数
			undigestDay = accuAbsence.getUnbalanceNumber().getDay().v();

			// ループ中の「逐次発生の休暇明細」．未相殺．日数をチェックする
			UnbalanceVacation vacation = (UnbalanceVacation) accuAbsence;
			if (accuAbsence.getUnbalanceNumber().getDay().v() == 1) {
				// 未消化時間 += ループ中の「逐次発生の休暇明細」．１日相当時間
				undigestTime = vacation.getTimeOneDay().v();
			} else {
				// 未消化時間 += ループ中の「逐次発生の休暇明細」．半日相当時間
				undigestTime = vacation.getTimeHalfDay().v();
			}

		}
		return new NumberConsecuVacation(new ManagementDataRemainUnit(undigestDay),
				Optional.of(new AttendanceTime(undigestTime)));
	}
	
	public static interface Require extends SettingSubstituteHolidayProcess.Require {

	}

	@Getter
	@AllArgsConstructor
	public static class RemainUndigestResult {

		/**
		 * 残日数
		 */
		private double remainingDay;
		/**
		 * 残時間数
		 */
		private int remainingTime;

		/**
		 * 未消化日数
		 */
		private double undigestDay;
		/**
		 * 未消化時間
		 */
		private int undigestTime;
	}
	
}
