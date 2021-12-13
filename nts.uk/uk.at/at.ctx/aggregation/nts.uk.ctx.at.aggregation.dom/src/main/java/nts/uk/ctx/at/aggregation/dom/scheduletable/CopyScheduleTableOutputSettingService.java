package nts.uk.ctx.at.aggregation.dom.scheduletable;

import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;

/**
 * スケジュール表の出力設定を複製する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール表.スケジュール表の出力設定を複製する
 * @author dan_pv
 *
 */
public class CopyScheduleTableOutputSettingService {
	
	/**
	 * 複製する
	 * @param require
	 * @param copySource 複製元
	 * @param newCode コード
	 * @param newName 名称
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public static AtomTask copy(Require require,
			ScheduleTableOutputSetting copySource,
			OutputSettingCode newCode,
			OutputSettingName newName
			) {
		
		if ( require.isScheduleTableOutputSettingRegistered(newCode)) {
			throw new BusinessException("Msg_3");
		}
		
		ScheduleTableOutputSetting newDomain = copySource.clone(newCode, newName);
		
		return AtomTask.of( () -> require.insertScheduleTableOutputSetting(newDomain));
		
	}
	
	public static interface Require {
		
		/**
		 * 指定のコートが既に保存されるか
		 * @param code 出力設定コード
		 * @return
		 */
		boolean isScheduleTableOutputSettingRegistered(OutputSettingCode code);
		
		/**
		 * スケジュール表の出力設定を追加する
		 * @param domain スケジュール表の出力設定
		 */
		void insertScheduleTableOutputSetting(ScheduleTableOutputSetting domain);
	}

}
