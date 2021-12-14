package nts.uk.ctx.at.request.dom.application.common.service.other.output;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Time36UpperLimitCheckResult {
	/**
	 * 上限エラーフラグ
	 */
	private List<Time36ErrorOutput> errorFlg;

}
