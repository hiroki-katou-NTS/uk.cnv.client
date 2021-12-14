package nts.uk.ctx.at.function.dom.processexecution.createfromupdateexcecerror;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.DeleteInfoAlarmImport;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.TopPageAlarmImport;
import nts.uk.ctx.at.function.dom.processexecution.ExecutionCode;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.CurrentExecutionStatus;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogManage;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.ExecutionTaskSetting;

/*
 * 更新処理自動実行異常からトップページアラームを作成する
 * 
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.更新処理自動実行.DomainService.更新処理自動実行異常からトップページアラームを作成する.更新処理自動実行異常からトップページアラームを作成する
 */
public class CreateFromUpdateExecError {
	
	private CreateFromUpdateExecError() {}

	public static void create(Require rq, String cid) {

		List<ProcessExecutionLogManage> processExecutionLogManageList = rq.getUpdateExecItemsWithErrors(cid);

		List<ExecutionTaskSetting> executionTaskSettingList = rq.getByCid(cid);

		Map<ExecutionCode, ExecutionTaskSetting> executionTaskSettingMap = executionTaskSettingList.stream()
				.collect(Collectors.toMap(ExecutionTaskSetting::getExecItemCd, Function.identity()));

		List<String> sids = rq.getListEmpID(cid, GeneralDate.today());

		List<ProcessExecutionLogManage> errorList = processExecutionLogManageList.stream().map(item -> {

			boolean alarmFlag = false;

			if (item.getCurrentStatus().isPresent()) {
				if (item.getCurrentStatus().get() == CurrentExecutionStatus.RUNNING
						&& item.getLastExecDateTime().isPresent()) {
					// 過去の実行平均時間を超過しているか
					alarmFlag = rq.isPassAverageExecTimeExceeded(cid, item.getExecItemCd(),	item.getLastExecDateTime().get());
				}

				if (item.getCurrentStatus().get() == CurrentExecutionStatus.WAITING && executionTaskSettingMap.get(item.getExecItemCd()) != null) {
					// 次回実行日時作成処理
					GeneralDateTime nextTime = executionTaskSettingMap.get(item.getExecItemCd()) != null
							? rq.processNextExecDateTimeCreation(executionTaskSettingMap.get(item.getExecItemCd()))
							: null; //#115526
					
					if (nextTime != null && nextTime.before(GeneralDateTime.now())) { 
						alarmFlag = true;
					}
				}
				
				//Loopしている項目はエラーがある
				if (alarmFlag) {
					List<TopPageAlarmImport> alarmInfos = sids.stream()
							.map(sid -> TopPageAlarmImport.builder()
									.alarmClassification(2) // 更新処理自動実行動作異常
									.occurrenceDateTime(GeneralDateTime.now())
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
					rq.createAlarmData(cid, alarmInfos, Optional.empty());
					return item;
				}
			}
			return null;
		})
		.filter(i -> i != null)
		.collect(Collectors.toList());

		// すべて項目はエラーがない → 削除
		if(errorList.isEmpty()) {
			DeleteInfoAlarmImport delInfo = DeleteInfoAlarmImport.builder()
					.alarmClassification(2) // 更新処理自動実行動作異常
					.sids(sids)
					.displayAtr(1) // 上長
					.patternCode(Optional.empty())
					.build();

			rq.createAlarmData(cid, Collections.emptyList(), Optional.ofNullable(delInfo));
		}
	}

	public interface Require {
		/**
		 * [R-1] 更新処理自動実行管理を取得
		 * 
		 * 更新処理自動実行管理Repository．取得する(会社ID)
		 * 
		 * @param 会社ID companyId
		 * @return List<ProcessExecutionLogManage>
		 */
		public List<ProcessExecutionLogManage> getUpdateExecItemsWithErrors(String companyId);

		/**
		 * [R-2] 実行タスク設定を取得
		 * 
		 * 実行タスク設定Repository．取得する(会社ID)
		 * 
		 * @param 会社ID cid
		 * @return List<ExecutionTaskSetting>
		 */
		public List<ExecutionTaskSetting> getByCid(String cid);

		/**
		 * [R-3] 過去の実行平均時間を超過しているか
		 * 
		 * アルゴリズム．過去の実行平均時間を超過しているか(会社ID,更新処理自動実行,実行開始日時)
		 * 
		 * @param companyId
		 * @param updateProcessAutoExec
		 * @param execStartDateTime
		 * @return
		 */
		public boolean isPassAverageExecTimeExceeded(String companyId, ExecutionCode execItemCode,
				GeneralDateTime execStartDateTime);

		/**
		 * [R-4] 次回実行日時作成処理
		 * 
		 * アルゴリズム．次回実行日時作成処理(スケジュールID,1日の繰り返しスケジュールID)
		 * 
		 * @param execTaskSet
		 * @return
		 */
		public GeneralDateTime processNextExecDateTimeCreation(ExecutionTaskSetting execTaskSet);

		/**
		 * [R-5] 就業担当者Listを取得する
		 * 
		 * 就業担当者(社員IDList)を取得するAdapter(会社ID, 基準日)
		 * 
		 * @param 会社ID companyID
		 * @param 基準日  referenceDate
		 * @return
		 */
		public List<String> getListEmpID(String companyID, GeneralDate referenceDate);

		/**
		 * [R-6] トップページアラームデータを作成する
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
