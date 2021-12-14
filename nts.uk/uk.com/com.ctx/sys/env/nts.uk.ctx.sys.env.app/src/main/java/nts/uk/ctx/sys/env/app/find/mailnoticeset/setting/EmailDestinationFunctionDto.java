package nts.uk.ctx.sys.env.app.find.mailnoticeset.setting;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto メール送信先機能
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDestinationFunctionDto {
	/**
	 * メール分類
	 */
	private Integer emailClassification;

	/**
	 * 機能ID
	 */
	private List<Integer> functionIds;
}
