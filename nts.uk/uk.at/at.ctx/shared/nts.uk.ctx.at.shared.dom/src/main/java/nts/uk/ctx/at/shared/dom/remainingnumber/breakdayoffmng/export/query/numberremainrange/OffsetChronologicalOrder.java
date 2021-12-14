package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.UnbalanceCompensation;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.DayOffError;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.LeaveOccurrDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SeqVacationAssociationInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.UnbalanceVacation;

/**
 * @author ThanhNX
 *
 *         時系列順で相殺する
 */
public class OffsetChronologicalOrder {

	private OffsetChronologicalOrder() {
	};

	// 時系列順で相殺する
	public static Pair<Optional<DayOffError>, List<SeqVacationAssociationInfo>> process(String employeeId,
			boolean managerTimeCate, List<AccumulationAbsenceDetail> lstAccAbse,
			TypeOffsetJudgment typeJudgment) {
		Optional<DayOffError> error = Optional.empty();
		List<SeqVacationAssociationInfo> lstSeqVacation = new ArrayList<>();
		// INPUT．「逐次発生の休暇明細」．発生消化区分により、発生と消化で別ける

		List<AccumulationAbsenceDetail> lstAccdigest = lstAccAbse.stream()
				.filter(x -> x.getOccurrentClass() == OccurrenceDigClass.DIGESTION).collect(Collectors.toList());

		List<AccumulationAbsenceDetail> lstAccOccur = lstAccAbse.stream()
				.filter(x -> x.getOccurrentClass() == OccurrenceDigClass.OCCURRENCE).collect(Collectors.toList());

		// 「逐次発生の休暇明細」(消化)でループする
		for (AccumulationAbsenceDetail accAbsence : lstAccdigest) {

//			if (!accAbsence.getDateOccur().getDayoffDate().isPresent())
//				continue;

			// 逐次発生の休暇明細（消化）.年月日が期間に含まれる逐次発生の休暇設定を取得
//			TimeLapseVacationSetting timeLapSet = lstTimeLap.stream()
//					.filter(x -> accAbsence.getDateOccur().getDayoffDate().get().afterOrEquals(x.getPeriod().start())
//							&& accAbsence.getDateOccur().getDayoffDate().get().beforeOrEquals(x.getPeriod().end()))
//					.findFirst().orElse(null);
//			if (timeLapSet == null)
//				continue;
			// ループ中の「逐次発生の休暇明細」（消化）．未相殺数をチェックする
			if (checkUnbalNum(managerTimeCate, accAbsence))
				continue;

			// 逐次発生の休暇明細（消化）.年月日が期間に含まれる逐次発生の休暇設定を取得
			// 「逐次発生の休暇明細」(発生)でループする
			for (AccumulationAbsenceDetail occur : lstAccOccur) {
				Pair<OffsetJudgment, Optional<SeqVacationAssociationInfo>> offsetJudgment = offsetJudgment(managerTimeCate,
						accAbsence, occur, typeJudgment);
				if (offsetJudgment.getRight().isPresent())
					lstSeqVacation.add(offsetJudgment.getRight().get());
				if (offsetJudgment.getLeft() == OffsetJudgment.ERROR) {
					error = Optional.of(DayOffError.PREFETCH_ERROR);
					break;
				}
				else {
					// 「逐次発生の休暇明細」（消化）.未相殺数 > 0
					if (checkUnbalNum(managerTimeCate, accAbsence)) {
						break;
					} else {
						continue;
					}
				}
			}

			// 相殺できる「発生数」があるかチェックする
			if (!lstAccOccur.stream().filter(x -> !x.getNumberOccurren().allFieldZero()).findAny().isPresent()) {
				//全ての「逐次発生の休暇明細」(発生)．「発生数」が0の場合、相殺できる発生がないとする。
				break;
			}

		}
		return Pair.of(error, lstSeqVacation);

	}

	// 相殺判定
	private static Pair<OffsetJudgment, Optional<SeqVacationAssociationInfo>> offsetJudgment(
			boolean managerTimeCate, AccumulationAbsenceDetail accdigest,
			AccumulationAbsenceDetail occur, TypeOffsetJudgment typeJudgment) {

		// 期限切れかをチェックする
		Optional<LeaveOccurrDetail> occurrDetail = occurrDetail(occur, typeJudgment);
		if (!occurrDetail.isPresent() || !accdigest.getDateOccur().getDayoffDate().isPresent()
				|| occurrDetail.get().getDeadline().before(accdigest.getDateOccur().getDayoffDate().get())) {
			return Pair.of(OffsetJudgment.SUCCESS, Optional.empty());
		}

		// 逐次発生の休暇明細（発生）.休暇数をチェックする
		if (checkUnbalNum(managerTimeCate, occur)) {
			return Pair.of(OffsetJudgment.SUCCESS, Optional.empty());

		}

		Optional<SeqVacationAssociationInfo> seqVacation = Optional.empty();
		if (occur.getDateOccur().getDayoffDate().isPresent() && accdigest.getDateOccur().getDayoffDate().isPresent()) {

		// 紐づけ登録処理
		seqVacation = TypeRegistrationProcess.process(
				occur.getDateOccur().getDayoffDate().get(), accdigest.getDateOccur().getDayoffDate().get(),
				accdigest.getUnbalanceNumber().getDay(), typeJudgment);
		
		}

		// 未相殺数を更新 in process 振休
		UpdateUnbalancedNumber.updateUnbalanced(managerTimeCate, accdigest, occur, typeJudgment);

		return Pair.of(OffsetJudgment.SUCCESS, seqVacation);

	}

	// 期限切れかをチェックする
	private static Optional<LeaveOccurrDetail> occurrDetail(AccumulationAbsenceDetail occur,
			TypeOffsetJudgment typeJudgment) {
		if (typeJudgment == TypeOffsetJudgment.ABSENCE) {
			return Optional.of((UnbalanceCompensation) occur);
		} else {
			return Optional.of((UnbalanceVacation) occur);
		}

	}

	private static boolean checkUnbalNum(boolean managerTimeCate, AccumulationAbsenceDetail accAbsence) {

		// 逐次発生休暇設定.時間管理区分 = true
		// 逐次発生の休暇明細（消化）.未相殺数.時間 ＞０
		// 逐次発生休暇設定.時間管理区分 = false
		// 逐次発生の休暇明細（消化）.未相殺数.日数 ＞０
		if (!managerTimeCate && accAbsence.getUnbalanceNumber().getDay().v() <= 0) {
			return true;
		} else if (managerTimeCate
				&& (!accAbsence.getUnbalanceNumber().getTime().isPresent()
						|| accAbsence.getUnbalanceNumber().getTime().get().v() <= 0)) {
			return true;
		}
		return false;

	}
}
