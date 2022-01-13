package nts.uk.screen.at.app.query.kdp.kdp002.a;

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
public class SettingsStampCommonDto {

	//応援利用
	public boolean supportUse;
	
	//臨時利用
	public boolean temporaryUse;
	
	//作業利用
	public boolean workUse;
	
	// 入退門管理追加
	// public boolean manageEntryExit;
	public boolean entranceExitUse;
}
