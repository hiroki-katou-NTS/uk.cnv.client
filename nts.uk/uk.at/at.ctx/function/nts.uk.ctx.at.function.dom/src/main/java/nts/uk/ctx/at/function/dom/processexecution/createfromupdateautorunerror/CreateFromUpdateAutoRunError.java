package nts.uk.ctx.at.function.dom.processexecution.createfromupdateautorunerror;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.DeleteInfoAlarmImport;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.TopPageAlarmImport;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogManage;

/*
 * 更新処理自動実行エラーからトップページアラームを作成する
 * 
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.更新処理自動実行.DomainService.更新処理自動実行エラーからトップページアラームを作成する.更新処理自動実行エラーからトップページアラームを作成する
 */
public class CreateFromUpdateAutoRunError {
	
	private CreateFromUpdateAutoRunError() {}
	
	public static AtomTask create(Require rq, String cid) {
		
		List<ProcessExecutionLogManage> listAutorun = rq.getAutorunItemsWithErrors(cid);
		List<String> sids = rq.getListEmpID(cid, GeneralDate.today());

		if (listAutorun.isEmpty()) { // 解消済み
			
			DeleteInfoAlarmImport delInfo = DeleteInfoAlarmImport.builder()
					.alarmClassification(1) // 更新処理自動実行業務エラー
					.sids(sids)
					.displayAtr(1) // 上長
					.patternCode(Optional.empty())
					.build();
			
			return AtomTask.of(() -> rq.createAlarmData(cid, Collections.emptyList(), Optional.ofNullable(delInfo)));
		}
		
		// アラームがある
		List<ProcessExecutionLogManage> removedOptionals = listAutorun.stream()
				.filter(item -> item.getLastEndExecDateTime().isPresent())
				.collect(Collectors.toList());
		
		Optional<ProcessExecutionLogManage> lastItem;
		
		if (removedOptionals.isEmpty()) {
			lastItem = listAutorun.stream().findFirst();
		} else {
			lastItem = removedOptionals.stream()
					.sorted(Comparator.comparing(
							ProcessExecutionLogManage::getLastEndExecDateTime, (s1, s2) -> {
					            return s2.get().compareTo(s1.get());
					        }))
					.findFirst();
		}
		
		List<TopPageAlarmImport> alarmInfos = sids.stream()
				.map(sid -> TopPageAlarmImport.builder()
						.alarmClassification(1) // 更新処理自動実行業務エラー
						.occurrenceDateTime(lastItem.map(i -> i.getLastEndExecDateTime().orElse(null)).orElse(null))
						.displaySId(sid)
						.displayAtr(1) // 上長
						.subEmployeeIds(Collections.emptyList()) //#116503
						.noErrEmployeeIds(Collections.emptyList())
						.patternCode(Optional.empty())
						.patternName(Optional.empty())
						.linkUrl(Optional.empty())
						.displayMessage(Optional.empty())
						.build())
				.collect(Collectors.toList());
		
		return AtomTask.of(() -> rq.createAlarmData(cid, alarmInfos, Optional.empty()));
	}

	public interface Require {
		/**
		 * [R-1] エラーがある自動実行項目を取得する
		 * 
		 * エラーがある自動実行項目を取得する(会社ID)
		 * 
		 * @param 会社ID companyId
		 * @return List<ProcessExecutionLogManage>
		 */
		public List<ProcessExecutionLogManage> getAutorunItemsWithErrors(String companyId);

		/**
		 * [R-2] 就業担当者Listを取得する
		 * 
		 * 就業担当者(社員IDList)を取得するAdapter(会社ID, 基準日)
		 * 
		 * @param 会社ID companyID
		 * @param 基準日  referenceDate
		 * @return
		 */
		public List<String> getListEmpID(String companyID, GeneralDate referenceDate);

		/**
		 * [R-3] トップページアラームデータを作成する
		 * 
		 * トップページアラームデータを作成するAdapter(会社ID,トップアラームパラメータ、削除の情報)
		 * 
		 * @param companyId  会社ID
		 * @param alarmInfos List トップアラームパラメータ
		 * @param delInfo    削除の情報
		 */
		public void createAlarmData(String companyId, List<TopPageAlarmImport> alarmInfos,
				Optional<DeleteInfoAlarmImport> delInfoOpt);
	}
}
