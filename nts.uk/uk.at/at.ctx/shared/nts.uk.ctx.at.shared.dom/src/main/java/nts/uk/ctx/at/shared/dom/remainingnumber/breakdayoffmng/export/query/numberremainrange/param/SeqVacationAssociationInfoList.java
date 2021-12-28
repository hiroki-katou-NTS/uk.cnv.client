package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.daynumber.ReserveLeaveRemainingDayNumber;

/**
 * @author thanh_nx
 *
 *         逐次休暇の紐付け情報一覧
 */
@AllArgsConstructor
@Getter
public class SeqVacationAssociationInfoList {

	// 一覧
	private List<SeqVacationAssociationInfo> seqVacInfoList;

	// [1]発生と使用の紐付け状態の整合を取る
	public SeqVacationAssociationInfoList matchAssocStateOccAndDigest(VacationDetails occr, VacationDetails digest) {

		// $発生の検索一覧 = 逐次発生の休暇明細一覧#作成する(発生一覧)
		VacationDetails occrSearchList = new VacationDetails(occr.getLstAcctAbsenDetail().stream().map(x -> x.clone()).collect(Collectors.toList()));

		// $使用の検索一覧 = 逐次発生の休暇明細一覧#作成する(使用一覧)
		VacationDetails digestSearchList = new VacationDetails(digest.getLstAcctAbsenDetail().stream().map(x -> x.clone()).collect(Collectors.toList()));

		SeqVacationAssociationInfoList associationLst = new SeqVacationAssociationInfoList(new ArrayList<>());

		for (SeqVacationAssociationInfo seqVac : this.seqVacInfoList) {
			// if $発生数 = 0.5
			val numberOccCorresDay = occrSearchList.getNumberOccCorrespDay(seqVac.getOutbreakDay());
			if (numberOccCorresDay == 0.5) {
				occrSearchList.getLstAcctAbsenDetail().removeIf(x -> x.getDateOccur().getDayoffDate().isPresent()
						&& x.getDateOccur().getDayoffDate().get().equals(seqVac.getOutbreakDay()));
			}
			val conStateOccUse = seqVac
					.consistentStateOccurrenceandUse(new ReserveLeaveRemainingDayNumber(numberOccCorresDay));
			// if not $紐付け情報.isPresent()
			if (!conStateOccUse.isPresent()) {
				continue;
			}

			// $使用数
			val numberDigetCorresDay = digestSearchList.getNumberDigestCorrespDay(seqVac.getDateOfUse());
			if (numberDigetCorresDay == 0.5) {
				digestSearchList.getLstAcctAbsenDetail().removeIf(x -> x.getDateOccur().getDayoffDate().isPresent()
						&& x.getDateOccur().getDayoffDate().get().equals(seqVac.getDateOfUse()));
			}

			val conStateDigestUse = conStateOccUse.get()
					.consistentStateOccurrenceandUse(new ReserveLeaveRemainingDayNumber(numberDigetCorresDay));
			// if not $紐付け情報.isPresent()
			if (!conStateDigestUse.isPresent()) {
				continue;
			}
			associationLst.addData(conStateDigestUse.get());
		}

		return associationLst;
	}

	// [2]追加する
	public SeqVacationAssociationInfoList addData(SeqVacationAssociationInfo seqVacAssoci) {
		this.seqVacInfoList.add(seqVacAssoci);
		return this;
	}

	//[3] 指定した休出と紐づく紐付け情報を取得する
	public List<SeqVacationAssociationInfo> getWithOccrDay(GeneralDate date){
		return this.seqVacInfoList.stream().filter(x -> x.getOutbreakDay().equals(date)).collect(Collectors.toList());
	}
}
