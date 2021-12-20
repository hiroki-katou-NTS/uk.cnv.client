package nts.uk.screen.at.app.ksu001.getinfoofInitstartup;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.schedule.dom.displaysetting.functioncontrol.ScheFunctionCtrlByWorkplace;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheFunctionCtrlByWorkplaceDto {

	/**
	 * 使用する表示期間
	 */
	public List<Integer> useDisplayPeriod;
	
	/**
	 * 使用する表示形式
	 */
	public List<Integer> useDisplayFormat;
	
	/**
	 * 起動できる画面
	 */
	public List<Integer> pageCanBeStarted;
	
	/**
	 * 完了利用する区分
	 */
	public int useCompletionAtr;
	
	/**
	 * 完了方法制御.完了実行方法
	 */
	public Integer completionExecutionMethod;
	
	// 完了方法制御.完了方法制御
	public List<Integer> completionMethodControl;
	
	// 完了方法制御.アラームチェックコードリスト
	public List<String> alarmCheckCodeList;
	
	public static ScheFunctionCtrlByWorkplaceDto fromDomain(ScheFunctionCtrlByWorkplace domain) {
		
		return ScheFunctionCtrlByWorkplaceDto.builder()
					.useDisplayPeriod(domain.getUseDisplayPeriod()
											.stream()
											.map(x -> x.value)
											.collect(Collectors.toList()))
					.useDisplayFormat(domain.getUseDisplayFormat()
											.stream()
											.map(x -> x.value)
											.collect(Collectors.toList()))
					.pageCanBeStarted(domain.getPageCanBeStarted()
											.stream()
											.map(x -> x.value)
											.collect(Collectors.toList()))
					.useCompletionAtr(domain.getUseCompletionAtr().value)
					.completionExecutionMethod(domain.getCompletionMethodControl()
													 .map(x -> x.getCompletionExecutionMethod().value)
													 .orElse(null))
					.completionMethodControl(domain.getCompletionMethodControl()
													 .map(x -> x.getCompletionMethodControl()
															 	.stream()
															 	.map(y -> y.value)
															 	.collect(Collectors.toList())
															 )
													 .orElse(Collections.emptyList()))
					.alarmCheckCodeList(domain.getCompletionMethodControl()
											  .map(x -> x.getAlarmCheckCodeList())
											  .orElse(Collections.emptyList()))	
					.build();
	}
	
}
