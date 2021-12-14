package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.task.tran.TransactionService;
import nts.uk.ctx.at.record.dom.daily.DailyRecordAdUpService;
import nts.uk.ctx.at.record.dom.functionalgorithm.errorforcreatedaily.ErrorHandlingCreateDailyResults;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.shr.com.context.AppContexts;

/**
 * 日別実績を登録する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績処理.作成処理.日別作成Mgrクラス.アルゴリズム.社員の日別実績を作成する.日別実績を登録する.日別実績を登録する
 * @author tutk
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterDailyAchievements {

	@Inject
	private ErrorHandlingCreateDailyResults errorHandlingCreateDailyResults;

	@Inject
	private RegisterDailyWork registerDailyWork;

	@Inject
	private StampDakokuRepository stampDakokuRepository;

	@Inject
	private DailyRecordAdUpService dailyRecordAdUpService;

	@Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	
	@Inject
	private TransactionService transactionService;

	public void register(List<IntegrationOfDaily> listIntegrationOfDaily, List<ErrorMessageInfo> listError,
			List<Stamp> listStamp, Optional<String> empCalAndSumExecLogId) {
		String companyId = AppContexts.user().companyId();
		
		val erByDate = listError.stream().collect(Collectors.groupingBy(ErrorMessageInfo::getProcessDate, Collectors.toList()));
		
		val stampByDate = listStamp.stream().filter(c -> c.getImprintReflectionStatus().getReflectedDate().isPresent())
				.collect(Collectors.groupingBy(c -> c.getImprintReflectionStatus().getReflectedDate().get(), Collectors.toList()));

		for (IntegrationOfDaily iod : listIntegrationOfDaily) {
			
			transactionService.execute(() -> {
				if (erByDate.containsKey(iod.getYmd())) {
					erByDate.get(iod.getYmd()).forEach(errorMessageInfo -> {
						
						// 日別実績の作成エラー処理
						errorHandlingCreateDailyResults.executeCreateError(companyId, errorMessageInfo.getEmployeeID(),
								errorMessageInfo.getProcessDate(), empCalAndSumExecLogId.orElse(null), 
								errorMessageInfo.getExecutionContent(), errorMessageInfo.getResourceID(), 
								errorMessageInfo.getMessageError());
					});
				}
				// ドメインモデル「打刻」を更新する (Update 「打刻」)
				if (stampByDate.containsKey(iod.getYmd())) {
					stampByDate.get(iod.getYmd()).forEach(stampItem -> {
						this.stampDakokuRepository.update(stampItem);
					});
				}
				
				// エラーで本人確認と上司承認を解除する
				dailyRecordAdUpService.removeConfirmApproval(Arrays.asList(iod));

				// 登録する (Đăng ký)
				registerDailyWork.register(iod, new ArrayList<>());
				
				// 暫定データの登録
				this.interimRemainDataMngRegisterDateChange.registerDateChange(companyId,
						iod.getEmployeeId(), Arrays.asList(iod.getYmd()));
			}); 
		}
	}
}
