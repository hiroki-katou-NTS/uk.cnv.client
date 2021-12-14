/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.app.command.login.password;

import java.util.Optional;

import lombok.Setter;
import lombok.val;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.password.validate.ValidationResultOnLogin.Status;

/**
 * The Class CheckChangePassDto.
 */
public class CheckChangePassDto {

	/** The show change pass. */
	public boolean showChangePass;
	
	/** The msg error id. */
	public String msgErrorId;

	/** The show contract. */
	public boolean showContract;
	
	/** param for SystemSuspend */
	@Setter
	public String successMsg;
	/**変更理由*/
	public String changePassReason;
	/**残り何日*/
	public int spanDays;
	
	public CheckChangePassDto() {
		super();
	}

	/**
	 * Instantiates a new check change pass dto.
	 *
	 * @param showChangePass the show change pass
	 */
	public CheckChangePassDto(boolean showChangePass, String msgErrorId, boolean showContract) {
		super();
		this.showChangePass = showChangePass;
		this.msgErrorId = msgErrorId;
		this.showContract = showContract;
	}
	public CheckChangePassDto(String changePassReason) {
		super();
		this.showChangePass = true;
		this.changePassReason = changePassReason;
	}
	public CheckChangePassDto(String msgErrorId, int spanDays) {
		super();
		this.msgErrorId = msgErrorId;
		this.spanDays = spanDays;
	}
	
	// テナント認証に失敗
	public static CheckChangePassDto failedToAuthTenant() {
		return new CheckChangePassDto(false, null, true);
	}
	
	// 社員の識別に失敗
	public static CheckChangePassDto failedToIdentificate() {
		return new CheckChangePassDto(false, "Msg_301", false);
	}
	
	// パスワード認証による認証に失敗
	public static CheckChangePassDto failedToAuthPassword() {
		return new CheckChangePassDto(false, "Msg_302", false);
	}
	
	// パスワード認証による認証に成功
	public static CheckChangePassDto successToAuthPassword(AuthenticationResult authen, Optional<String> msg) {
		val passwordValidation = authen.getPasswordValidation().get();
		// パスワードの変更が必要な場合
		if(passwordValidation.getStatus().isNeedToChangePassword()) {
			return new CheckChangePassDto(passwordValidation.getStatus().getMessageId());
		}
		// パスワードの期限が迫っている場合
		if(passwordValidation.getStatus() == Status.EXPIRES_SOON) {
			return new CheckChangePassDto(passwordValidation.getStatus().getMessageId(), passwordValidation.getRemainingDays().get());
		}
		
		// システム利用停止中の警告がある場合
		if(msg.isPresent()) {
			val dto = new CheckChangePassDto(false, null, false);
			dto.setSuccessMsg(msg.get());
			return dto;
		}
		
		// 特に問題なし
		return new CheckChangePassDto(false, null, false);
	}
}
