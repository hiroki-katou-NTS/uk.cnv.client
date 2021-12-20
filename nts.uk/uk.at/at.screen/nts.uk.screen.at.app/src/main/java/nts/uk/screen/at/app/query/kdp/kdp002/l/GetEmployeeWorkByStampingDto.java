package nts.uk.screen.at.app.query.kdp.kdp002.l;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author chungnt
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetEmployeeWorkByStampingDto {

	// List＜作業＞
	private List<TaskDto> task;

	// 作業枠利用設定(Option)
	private TaskFrameUsageSettingDto taskFrameUsageSetting;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TaskFrameUsageSettingDto {
	// 作業枠NO
	private List<TaskFrameSettingDto> taskFrameSetting;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TaskFrameSettingDto {
	// 作業枠NO
	private int taskFrameNo;

	// 作業枠名
	private String taskFrameName;

	// するしない区分
	private boolean useAtr;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TaskDto {

	// コード
	private String code;

	// 作業枠NO
	private int frameNo;

	// 外部連携情報
	private ExternalCooperationInfoDto cooperationInfo;

	// 子作業一覧
	private List<String> childTaskList;

	// 有効期限
	private GeneralDate startDate;

	// 有効期限
	private GeneralDate endDate;

	// 表示情報 : 作業表示情報
	private TaskDisplayInfoDto displayInfo;

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ExternalCooperationInfoDto {

	// 外部コード1
	private String externalCode1;

	// 外部コード2
	private String externalCode2;

	// 外部コード3
	private String externalCode3;

	// 外部コード4
	private String externalCode4;

	// 外部コード5
	private String externalCode5;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class TaskDisplayInfoDto {

	// 名称
	private String taskName;

	// 略名
	private String taskAbName;

	// 作業色
	private String color;

	// 備考
	private String taskNote;

}
