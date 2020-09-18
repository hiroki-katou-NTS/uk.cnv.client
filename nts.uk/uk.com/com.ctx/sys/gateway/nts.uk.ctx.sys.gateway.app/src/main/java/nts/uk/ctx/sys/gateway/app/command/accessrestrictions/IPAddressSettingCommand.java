package nts.uk.ctx.sys.gateway.app.command.accessrestrictions;

import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.gateway.dom.accessrestrictions.IPAddressSetting;

/**
 * @author thanhpv
 * @name IPアドレス設定
 */
@NoArgsConstructor
public class IPAddressSettingCommand {

	/** IPアドレス1 */
	public Integer ip1;

	/** IPアドレス2 */
	public Integer ip2;

	/** IPアドレス3 */
	public Integer ip3;

	/** IPアドレス4 */
	public Integer ip4;

	public IPAddressSetting toDomain() {
		return new IPAddressSetting(this.ip1, this.ip2, this.ip3, this.ip4);
	}
	
}
