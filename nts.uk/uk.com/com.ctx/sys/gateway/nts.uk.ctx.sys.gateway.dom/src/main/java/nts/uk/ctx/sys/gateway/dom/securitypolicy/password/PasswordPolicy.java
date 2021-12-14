package nts.uk.ctx.sys.gateway.dom.securitypolicy.password;

import static java.util.stream.Collectors.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.val;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUser;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.PasswordState;
import nts.uk.ctx.sys.gateway.dom.loginold.ContractCode;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.password.complexity.PasswordComplexityRequirement;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.password.complexity.PasswordMinimumLength;
import nts.uk.ctx.sys.gateway.dom.securitypolicy.password.validate.ValidationResultOnLogin;

@Getter
/**
 * パスワードポリシー
 */
public class PasswordPolicy extends AggregateRoot {
	
	// テナントコード
	private final ContractCode contractCode;
	
	// 利用する
	private boolean isUse;
	
	// 複雑さ
	private PasswordComplexityRequirement complexityRequirement;
	
	// 変更履歴回数
	private PasswordHistoryCount historyCount;
	
	// 有効期限
	private PasswordValidityPeriod validityPeriod;
	
	// 期限切れ逼迫通知
	private NotificationPasswordChange notificationPasswordChange;
	
	// 初回ログイン時パスワード変更
	private boolean initialPasswordChange;
	
	// ログイン時にポリシーチェック実施
	private boolean loginCheck;

	public PasswordPolicy(
			ContractCode contractCode,
			NotificationPasswordChange notificationPasswordChange,
			boolean loginCheck,
			boolean initialPasswordChange,
			boolean isUse,
			PasswordHistoryCount historyCount,
			PasswordValidityPeriod validityPeriod,
			PasswordComplexityRequirement complexityRequirement) {
		super();
		this.contractCode = contractCode;
		this.notificationPasswordChange = notificationPasswordChange;
		this.loginCheck = loginCheck;
		this.initialPasswordChange = initialPasswordChange;
		this.isUse = isUse;
		this.historyCount = historyCount;
		this.validityPeriod = validityPeriod;
		this.complexityRequirement = complexityRequirement;
	}
	public static PasswordPolicy createNotUse(String tenantCode) {
		return new PasswordPolicy(
				new ContractCode(tenantCode), 
				new NotificationPasswordChange(new BigDecimal(0)), 
				false, 
				false, 
				false, 
				new PasswordHistoryCount(new BigDecimal(0)), 
				new PasswordValidityPeriod(new BigDecimal(0)), 
				PasswordComplexityRequirement.createNotCheck());
	}
	
	public static PasswordPolicy createFromJavaType(String contractCode, int notificationPasswordChange,
			boolean loginCheck, boolean initialPasswordChange, boolean isUse, int historyCount, 
			int validityPeriod, PasswordComplexityRequirement complexityRequirement) {
		return new PasswordPolicy(new ContractCode(contractCode),
				new NotificationPasswordChange(new BigDecimal(notificationPasswordChange)), loginCheck,
				initialPasswordChange, isUse, new PasswordHistoryCount(new BigDecimal(historyCount)),
				new PasswordValidityPeriod(new BigDecimal(validityPeriod)),
				complexityRequirement);

	}

	/**
	 * ログイン時にポリシー違反してないか
	 */
	public ValidationResultOnLogin violatedOnLogin(LoginPasswordOfUser changeLog, String passwordPlainText) {
		
		// ポリシー利用しない
		if (!isUse) {
			return ValidationResultOnLogin.ok();
		}
		
		PasswordState passwordStatus = changeLog.getPasswordState();

		// パスワードリセット
		if (passwordStatus.equals(PasswordState.RESET)) {
			return ValidationResultOnLogin.reset();
		}
		
		// 初期パスワード
		if (initialPasswordChange && passwordStatus.equals(PasswordState.INITIAL)) {
			return ValidationResultOnLogin.initial();
		}
		
		// パスワードをチェック
		if (loginCheck) {
			val violations = validate(changeLog, passwordPlainText);
			if (!violations.isEmpty()) {
				return ValidationResultOnLogin.complexityError(violations.stream().map(v -> v.getErrorMessageId()).collect(toList()));
			}
		}
		
		// 有効期限をチェック
		if (!validityPeriod.isUnlimited()) {
			int remainingDays = calculateRemainingDays(changeLog);
			
			// 有効期限切れ
			if (remainingDays < 0) {
				return ValidationResultOnLogin.expired();
			}
			
			// 期限切れが近い
			if (notificationPasswordChange.needsNotify(remainingDays)) {
				return ValidationResultOnLogin.expiresSoon(remainingDays);
			}
		}
		
		// 問題なし
		return ValidationResultOnLogin.ok();
	}
	
	/**
	 * パスワードの状態がポリシーに適合しているか検証する
	 * @param changeLog パスワードの履歴（currentPasswordPlainTextを含むこと）
	 * @param currentPasswordPlainText 現在のパスワードの平文
	 * @return
	 */
	public List<ViolationInfo> validate(LoginPasswordOfUser changeLog, String currentPasswordPlainText) {

		if (!isUse) {
			return Collections.emptyList();
		}
		
		List<ViolationInfo> errors = new ArrayList<>();
		
		errors.addAll(complexityRequirement.validatePassword(currentPasswordPlainText));
		
		if (duplicatesLatestPassword(changeLog)) {
			errors.add(new ViolationInfo("Msg_1187", historyCount.v().intValue()));
		}
		
		return errors;
	}
	
	/**
	 * 最新のパスワードと重複があるかチェック
	 * @param changeLog
	 * @return
	 */
	private boolean duplicatesLatestPassword(LoginPasswordOfUser changeLog) {
		
		// 最新のものとの重複チェックなので、+1が必要
		val logs = changeLog.getLatestPasswords(historyCount.v().intValue() + 1);
		
		if (logs.size() <= 1) {
			return false;
		}
		
		val latest = logs.get(0);
		val olds = logs.subList(1, Math.max(historyCount.v().intValue(), logs.size()));
		
		return olds.stream().anyMatch(o -> o.getHashedPassword().equals(latest.getHashedPassword()));
	}
	
	/**
	 * 有効期限が切れるまでの残日数を求める
	 * @return 有効期限までの日数
	 */
	private int calculateRemainingDays(LoginPasswordOfUser changeLog) {
		// 前回変更してからの日数
		int ageInDays = changeLog.getLatestPassword().get().ageInDays();
		// 有効日数から上の日数を引く
		return validityPeriod.v().intValue() - ageInDays;
	}
}
