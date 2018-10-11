package nts.uk.ctx.at.record.pubimp.remainnumber.reserveleave;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetAnnAndRsvRemNumWithinPeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AggrResultOfAnnAndRsvLeave;
import nts.uk.ctx.at.record.pub.remainnumber.reserveleave.GetRsvLeaNumCriteriaDate;
import nts.uk.ctx.at.record.pub.remainnumber.reserveleave.RsvLeaGrantRemainingExport;
import nts.uk.ctx.at.record.pub.remainnumber.reserveleave.RsvLeaNumByCriteriaDate;
import nts.uk.ctx.at.record.pub.remainnumber.reserveleave.TmpReserveLeaveMngExport;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.basicinfo.CalcNextAnnualLeaveGrantDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemainRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.daynumber.ReserveLeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.interim.TmpResereLeaveMngRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * 実装：基準日時点の積立年休残数を取得する
 * @author shuichi_ishida
 */
@Stateless
public class GetRsvLeaNumCriteriaDateImpl implements GetRsvLeaNumCriteriaDate {

	/** 社員に対応する締め開始日を取得する */
	@Inject
	private GetClosureStartForEmployee getClosureStartForEmployee;
	/** 次回年休付与を計算 */
	@Inject
	private CalcNextAnnualLeaveGrantDate calcNextAnnualLeaveGrantNum;
	/** 期間中の年休積休残数を取得 */
	@Inject
	private GetAnnAndRsvRemNumWithinPeriod getAnnAndRsvRemNumWithinPeriod;
	/** 暫定残数管理データ */
	@Inject
	private InterimRemainRepository interimRemainRepo;
	/** 暫定積立年休管理データ */
	@Inject
	private TmpResereLeaveMngRepository tmpReserveLeaveMng;
	
	/** 基準日時点の積立年休残数を取得する */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<RsvLeaNumByCriteriaDate> algorithm(String employeeId, GeneralDate criteria) {
		
		String companyId = AppContexts.user().companyId();
		
		//　社員に対応する締め開始日を取得する
		val closureStartOpt = this.getClosureStartForEmployee.algorithm(employeeId);
		if (!closureStartOpt.isPresent()) return Optional.empty();
		val closureStart = closureStartOpt.get();
		
		// 集計終了日　←　締め開始日+1年-1日
		GeneralDate aggrEnd = closureStart.addYears(1).addDays(-1);
		
		// 「次回年休付与を計算」を実行
		val nextAnnualLeaveGrants = this.calcNextAnnualLeaveGrantNum.algorithm(companyId, employeeId, Optional.empty());
		if (nextAnnualLeaveGrants.size() > 0){
			// 次回付与日前日　←　先頭の「次回年休付与」．付与年月日-1日
			GeneralDate prevNextGrant = nextAnnualLeaveGrants.get(0).getGrantDate().addDays(-1);
			if (prevNextGrant.before(aggrEnd)){
				// 集計終了日　←　次回付与日前日
				aggrEnd = prevNextGrant;
			}
		}
		
		// 期間中の年休積休残数を取得
		val aggrResult = this.getResult(companyId, employeeId, closureStart, aggrEnd, criteria);
		val aggrResultOfReserveOpt = aggrResult.getReserveLeave();
		if (!aggrResultOfReserveOpt.isPresent()) return Optional.empty();
		val aggrResultOfReserve = aggrResultOfReserveOpt.get();
		
		// 取得結果を出力用クラスに格納
		List<RsvLeaGrantRemainingExport> grantRemainingList = new ArrayList<>();
		for (val grantRemaining : aggrResultOfReserve.getAsOfPeriodEnd().getGrantRemainingList()){
			if (grantRemaining.getExpirationStatus() != LeaveExpirationStatus.AVAILABLE) continue;
			grantRemainingList.add(new RsvLeaGrantRemainingExport(
					grantRemaining.getGrantDate(),
					grantRemaining.getDeadline(),
					grantRemaining.getDetails().getGrantNumber(),
					grantRemaining.getDetails().getUsedNumber(),
					grantRemaining.getDetails().getRemainingNumber()));
		}
		
		// 「暫定積立年休管理データ」を取得する
		List<TmpReserveLeaveMngExport> tmpManageList = new ArrayList<>();
		val interimRemains = this.interimRemainRepo.getRemainBySidPriod(
				employeeId, new DatePeriod(GeneralDate.min(), criteria), RemainType.FUNDINGANNUAL);
		interimRemains.sort((a, b) -> a.getYmd().compareTo(b.getYmd()));
		for (val interimRemain : interimRemains){
			val tmpReserveLeaveMngOpt = this.tmpReserveLeaveMng.getById(interimRemain.getRemainManaID());
			if (!tmpReserveLeaveMngOpt.isPresent()) continue;
			val tmpReserveLeaveMng = tmpReserveLeaveMngOpt.get();
			
			// 取得結果を出力用クラスに格納
			tmpManageList.add(new TmpReserveLeaveMngExport(
					interimRemain.getYmd(),
					interimRemain.getCreatorAtr(),
					tmpReserveLeaveMng.getUseDays()));
		}
		
		// 積立年休付与日を出力用クラスに格納
		Optional<GeneralDate> grantDateOpt = Optional.empty();
		val asOfGrantOpt = aggrResultOfReserve.getAsOfGrant();
		if (asOfGrantOpt.isPresent()){
			val asOfGrant = asOfGrantOpt.get();
			if (asOfGrant.size() > 0){
				grantDateOpt = Optional.of(asOfGrant.get(0).getYmd());
			}
		}
		
		// 基準日時点積立年休残数．積立年休残日数　←　0
		double remainDays = 0.0;
		for (val grantRemaining : grantRemainingList){
			
			// 処理中の「積立年休付与残数データ．期限日」と「基準日」を比較
			if (grantRemaining.getDeadline().afterOrEquals(criteria)){
				// 積立年休残日数に加算
				remainDays += grantRemaining.getRemainingNumber().v();
			}
		}
		
		// 基準日時点の積立年休残数を返す
		return Optional.of(new RsvLeaNumByCriteriaDate(
				aggrResultOfReserve.getAsOfPeriodEnd(),
				grantRemainingList,
				tmpManageList,
				grantDateOpt,
				new ReserveLeaveRemainingDayNumber(remainDays)));
	}
	
	/**
	 * 期間中の年休積休残数を取得
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param closureStart 締め開始日
	 * @param aggrEnd 集計終了日
	 * @param criteria 基準日
	 * @return 年休積立年休の集計結果
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private AggrResultOfAnnAndRsvLeave getResult(
			String companyId, String employeeId, GeneralDate closureStart, GeneralDate aggrEnd, GeneralDate criteria){
		
		return this.getAnnAndRsvRemNumWithinPeriod.algorithm(
				companyId,
				employeeId,
				new DatePeriod(closureStart, aggrEnd),
				InterimRemainMngMode.OTHER,
				criteria,
				false,
				false,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of(true),
				Optional.empty(),
				Optional.empty());
	}
}
