package nts.uk.ctx.sys.gateway.app.command.accessrestrictions;

import java.util.Optional;

import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.gateway.dom.accessrestrictions.AllowedIPAddress;

/**
 * @author thanhpv 
 * @name: 許可IPアドレス
 */
@NoArgsConstructor
public class AllowedIPAddressCommand{

	/** 開始アドレス */
	public IPAddressSettingCommand startAddress;
	
	/** IPアドレスの登録形式 */
	public Integer ipInputType;

	/** 終了アドレス */
	public IPAddressSettingCommand endAddress; 
	
	/** 備考 */
	public String comment;

	public AllowedIPAddress toDomain() {
		return new AllowedIPAddress(
				this.startAddress.toDomain(),
				this.ipInputType,
				this.ipInputType == 0 ? Optional.empty()
						: Optional.of(this.startAddress.toDomain()),
				this.comment);
	}

} 