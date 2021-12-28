package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.MngDataStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.AccumulationAbsenceDetailComparator;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;

/**
 * @author ThanhNX
 *
 *         逐次発生の休暇明細一覧
 */
@AllArgsConstructor
@Getter
public class VacationDetails {

	// 休暇リスト
	private List<AccumulationAbsenceDetail> lstAcctAbsenDetail;

	// 日付不明ではない発生の一覧を取得
	public List<AccumulationAbsenceDetail> getOccurrenceNotDateUnknown() {
		return lstAcctAbsenDetail.stream().filter(
				x -> !x.getDateOccur().isUnknownDate() && x.getOccurrentClass() == OccurrenceDigClass.OCCURRENCE)
				.collect(Collectors.toList());

	}

	// 日付不明ではない消化の一覧を取得
	public List<AccumulationAbsenceDetail> getDigestNotDateUnknown() {
		return lstAcctAbsenDetail.stream()
				.filter(x -> !x.getDateOccur().isUnknownDate() && x.getOccurrentClass() == OccurrenceDigClass.DIGESTION)
				.collect(Collectors.toList());

	}

	// [3] 追加する
	public void addDetail(AccumulationAbsenceDetail detail) {
		this.lstAcctAbsenDetail.add(detail);
	}

	// [4]追加する
	public void addDetail(List<AccumulationAbsenceDetail> details) {
		this.lstAcctAbsenDetail.addAll(details);
	}

	// [5] 該当日の発生数を取得する
	public double getNumberOccCorrespDay(GeneralDate correspDay) {
		return getVacStateForAppDay(correspDay).map(x -> x.getNumberOccurren().getDay().v()).orElse(0.0);
	}

	// [6] 該当日の使用数を取得する
	public double getNumberDigestCorrespDay(GeneralDate correspDay) {
		return getVacStateForAppDay(correspDay).map(x -> x.getNumberOccurren().getDay().v()).orElse(0.0);
	}

	//[7] 指定した期間内に未消化となる情報を取得する
	public List<AccumulationAbsenceDetail> getUndigestInfoInPeriod(DatePeriod dateperiod) {
		return getOccurrenceNotDateUnknown().stream()
				.filter(x -> (((LeaveOccurrDetail) x).getDigestionCate() == DigestionAtr.UNUSED
						|| ((LeaveOccurrDetail) x).judgeDigestiveStatus(GeneralDate.today()) == DigestionAtr.UNUSED) //
						&& dateperiod.contains(((LeaveOccurrDetail) x).getDeadline()))
				.collect(Collectors.toList());
	}
	
	//[8] 時系列にソートする
	public List<AccumulationAbsenceDetail> sortAccAbsDetailASC(){
		return this.lstAcctAbsenDetail.stream().sorted(new AccumulationAbsenceDetailComparator())
				.collect(Collectors.toList());
	}
	
	// [9] 未相殺の合計を取得する
	public UnoffsetNumSeqVacation getTotalUnoffset() {
		double daySum = this.lstAcctAbsenDetail.stream().mapToDouble(x -> x.getUnbalanceNumber().getDay().v()).sum();
		int timeSum = this.lstAcctAbsenDetail.stream()
				.mapToInt(x -> x.getUnbalanceNumber().getTime().map(y -> y.v()).orElse(0)).sum();
		return new UnoffsetNumSeqVacation(new LeaveRemainingDayNumber(daySum), new LeaveRemainingTime(timeSum));
	}

	// [10] 発生の確定データを取得する
	public List<AccumulationAbsenceDetail> getOccrFixed() {
		return this.getOccurrenceNotDateUnknown().stream().filter(x -> x.getDataAtr() == MngDataStatus.CONFIRMED)
				.collect(Collectors.toList());
	}
	
	// [11] 指定した日付の消化暫定データを取得する
	public Optional<AccumulationAbsenceDetail> getDigestTempWithDate(GeneralDate date) {
		return this.getDigestNotDateUnknown().stream().filter(
				x -> x.getDateOccur().getDayoffDate().get().equals(date) && x.getDataAtr() != MngDataStatus.CONFIRMED)
				.findFirst();
	}
	
	// [1] 該当する日の休暇明細を取得
	private Optional<AccumulationAbsenceDetail> getVacStateForAppDay(GeneralDate correspDay) {

		return this.lstAcctAbsenDetail.stream().filter(x -> x.getDateOccur().getDayoffDate().isPresent()
				&& x.getDateOccur().getDayoffDate().get().equals(correspDay)).findFirst();
	}
}
