package nts.uk.shr.com.operation;

import java.util.Optional;

public interface SystemOperationSettingAdapter {

	public SystemOperationSetting getSetting();
	
	/**
	 * システム停止中の確認
	 * @return
	 * if stopping - return Optional with value
	 * else return empty optional
	 */
	public Optional<String> stopUseConfirm();
	/**
	 * システム利用停止の確認
	 */
	public SystemSuspendOut stopUseConfirm(String contractCD, String companyCD, int loginMethod, String programID, String screenID);
}
