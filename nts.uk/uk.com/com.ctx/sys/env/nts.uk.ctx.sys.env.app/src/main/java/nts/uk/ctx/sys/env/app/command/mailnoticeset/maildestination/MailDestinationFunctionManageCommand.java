package nts.uk.ctx.sys.env.app.command.mailnoticeset.maildestination;

import lombok.Value;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.FunctionType;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManage;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Value
public class MailDestinationFunctionManageCommand {

	/**
	 * 機能ID
	 */
	private int functionId;

	/**
	 * 会社メールアドレスを利用する
	 */
	private int useCompanyMailAddress;

	/**
	 * 会社携帯メールアドレスを利用する
	 */
	private int useCompanyMobileMailAddress;

	/**
	 * 個人メールアドレスを利用する
	 */
	private int usePersonalMailAddress;

	/**
	 * 個人携帯メールアドレスを利用する
	 */
	private int usePersonalMobileMailAddress;

	public MailDestinationFunctionManage toDomain() {
		return new MailDestinationFunctionManage(EnumAdaptor.valueOf(this.functionId, FunctionType.class),
				NotUseAtr.valueOf(this.useCompanyMailAddress), NotUseAtr.valueOf(this.useCompanyMobileMailAddress),
				NotUseAtr.valueOf(this.usePersonalMailAddress), NotUseAtr.valueOf(this.usePersonalMobileMailAddress));
	}
}
