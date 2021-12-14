package nts.uk.screen.com.app.find.user.information.setting;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.env.dom.mailnoticeset.FunctionId;
import nts.uk.ctx.sys.env.dom.mailnoticeset.FunctionName;
import nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionSetMemento;
import nts.uk.ctx.sys.env.dom.mailnoticeset.SortOrder;

@Data
@NoArgsConstructor
public class MailFunctionDto implements MailFunctionSetMemento {
	/** The function id. */
	//機能ID
	private Integer functionId;
	
	/** The function name. */
	//機能名
	private String functionName;
	
	/** The propriety send mail setting atr. */
	//メール送信設定可否区分
	private boolean proprietySendMailSettingAtr;
	
	/** The sort order. */
	//並び順
	private Integer sortOrder;

	@Override
	public void setFunctionId(FunctionId functionId) {
		this.functionId = functionId.v();
	}

	@Override
	public void setFunctionName(FunctionName functionName) {
		this.functionName = functionName.v();
	}

	@Override
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder.v();
	}
}
