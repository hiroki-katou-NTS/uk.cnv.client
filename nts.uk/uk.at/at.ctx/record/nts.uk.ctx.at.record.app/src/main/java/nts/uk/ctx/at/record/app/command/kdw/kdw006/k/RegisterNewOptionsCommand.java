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
public class RegisterNewOptionsCommand {

	// 選択済みの履歴ID：履歴ID
	public String historyId;
	
	// 選択肢のコード：作業補足情報の選択肢コード
	public String choiceCode;
	
	// 選択肢の名称：作業補足情報の選択肢名称
	public String optionName;
	
	// <<Optional>>選択肢の外部コード：作業補足情報の外部コード
	public String eternalCodeOfChoice;
	
	/** 項目ID */
	public int itemId;

	
}
