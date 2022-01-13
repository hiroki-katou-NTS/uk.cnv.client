package nts.uk.ctx.at.record.dom.jobmanagement.usagesetting;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationMethod;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationSetting;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力の利用設定
 * AR: 工数入力の利用設定
 * @author tutt
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class ManHrInputUsageSetting extends AggregateRoot {
	
	/** 会社ID */
	private String cid;
	
	/** 使用区分*/
	private NotUseAtr usrAtr;
	
	/** [1] 作業実績の補正処理を行っても良いか判断する */
	public boolean decideCanCorrectTaskRecord(Require require) {
	
		/** $作業運用設定 = require.作業運用設定を取得する(@会社ID)	 */
		val taskOpSet = require.taskOperationSetting(this.cid);
		
		/** return $.作業運用方法==実績で利用 and @使用区分 = しない */
		return taskOpSet.map(c -> c.getTaskOperationMethod() == TaskOperationMethod.USED_IN_ACHIEVENTS 
									&& this.usrAtr == NotUseAtr.NOT_USE)
				.orElse(false);
	}
	
	public static interface Require {

		Optional<TaskOperationSetting> taskOperationSetting(String cid);
	}
}
