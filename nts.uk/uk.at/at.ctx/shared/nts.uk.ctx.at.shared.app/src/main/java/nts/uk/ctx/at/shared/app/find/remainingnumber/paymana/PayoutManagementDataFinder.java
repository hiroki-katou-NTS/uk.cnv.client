package nts.uk.ctx.at.shared.app.find.remainingnumber.paymana;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class PayoutManagementDataFinder {
	
	@Inject
	private PayoutManagementDataRepository payoutManagementDataRepository;

//	@Inject
//	private PayoutSubofHDManaRepository payoutSubofHDManaRepository;
	@Inject
	private ClosureRepository closureRepo;
	@Inject
	private PayoutManagementDataRepository confirmRecMngRepo;

	
	/**
	 * ドメイン「振休管理データ」より紐付け対象となるデータを取得する
	 * Ｆ．振休管理データの紐付設定（振出選択）画面表示処理
	 * @param sid
	 * @return List<PayoutManagementDataDto>
	 */
//	public List<PayoutManagementDataDto> getBySidDatePeriod(String sid, String subOfHDID){
//		List<PayoutManagementData> listPayout = payoutManagementDataRepository.getBySidDatePeriod(sid,subOfHDID,DigestionAtr.UNUSED.value);
//		
//		List<PayoutSubofHDManagement> listPayoutSub = payoutSubofHDManaRepository.getBySubId(subOfHDID);
//		Map<String, UsedDays> listPaySubId = listPayoutSub.stream().collect(
//                Collectors.toMap(PayoutSubofHDManagement::getPayoutId, PayoutSubofHDManagement::getUsedDays));
////		List<String> listPaySubId = listPayoutSub.stream().map(i->i.getPayoutId()).collect(Collectors.toList());
//		return listPayout.stream().map(i->{
//			PayoutManagementDataDto payout = PayoutManagementDataDto.createFromDomain(i);
//			if (listPaySubId.containsKey(i.getPayoutId())){
//				payout.setLinked(true);
//				payout.setunUsedDays(listPaySubId.get(i.getPayoutId()).v());
//			}
//			return payout;
//			
//		}).collect(Collectors.toList());
//	}
	
	public List<PayoutManagementDataDto> getBysiDRemCod(String empId, int state) {
		String cid = AppContexts.user().companyId();

		return payoutManagementDataRepository.getSidWithCod(cid, empId, state).stream().map(item -> PayoutManagementDataDto.createFromDomain(item))
				.collect(Collectors.toList());
	}
	
	/**
	 * 空いてる振出管理データを取得する
	 * @param sid 社員ID
	 */
	public List<PayoutManagementDataDto> findPayoutManaDataBySid(String sid, int closureId) {
		String cid = AppContexts.user().companyId();
		Optional<Closure> optClosure = closureRepo.findById(AppContexts.user().companyId(), closureId);
		Closure closure = optClosure.get();

		// Get Processing Ym 処理年月
		YearMonth processingYm = closure.getClosureMonth().getProcessingYm();

		DatePeriod closurePeriod = ClosureService.getClosurePeriod(closureId, processingYm, optClosure);
		// ドメインモデル「振出管理データ」を取得
		List<PayoutManagementData> lstRecconfirm  = this.confirmRecMngRepo.getByIdAndUnUse(cid, sid, closurePeriod.start(), 0.5);
		// 取得したList＜振出管理データ＞を返す
		return lstRecconfirm.stream().map(domain -> PayoutManagementDataDto.createFromDomain(domain))
				.collect(Collectors.toList());
	}
}
