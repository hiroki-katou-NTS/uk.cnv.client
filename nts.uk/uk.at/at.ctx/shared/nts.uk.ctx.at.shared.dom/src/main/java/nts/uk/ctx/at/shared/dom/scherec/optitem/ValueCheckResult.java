package nts.uk.ctx.at.shared.dom.scherec.optitem;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 入力値チェック結果
 * @author tutk
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValueCheckResult {
	/**
	 * チェック結果（正常、異常）
	 * true: 正常
	 * false: 異常
	 */
	private boolean checkResult;
	
	/**
	 * エラー内容
	 */
	private Optional<String> errorContent;
}
