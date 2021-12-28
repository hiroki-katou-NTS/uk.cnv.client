package nts.uk.ctx.at.record.app.command.kdw.kdw006.k;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeleteTheChoiceCommand {

	// 作業補足情報の選択肢コード：選択中の選択肢コード
	public String historyId;
	
	// 履歴ID：選択中の履歴ID
	public String choiceCode;

}
