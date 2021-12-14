package nts.uk.ctx.sys.env.app.find.mailnoticeset.setting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.env.dom.mailnoticeset.FunctionId;
import nts.uk.ctx.sys.env.dom.mailnoticeset.FunctionName;
import nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionSetMemento;
import nts.uk.ctx.sys.env.dom.mailnoticeset.SortOrder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailFunctionDto implements MailFunctionSetMemento {
	/** The function id. */
	// 機能ID
	private FunctionId functionId;

	/** The function name. */
	// 機能名
	private FunctionName functionName;

	/** The propriety send mail setting atr. */
	// メール送信設定可否区分
	private boolean proprietySendMailSettingAtr;

	/** The sort order. */
	// 並び順
	private SortOrder sortOrder;
}
