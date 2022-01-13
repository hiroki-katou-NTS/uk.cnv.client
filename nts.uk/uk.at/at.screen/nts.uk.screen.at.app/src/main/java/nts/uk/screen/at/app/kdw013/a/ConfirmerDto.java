package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDateTime;

/**
 * 確認者
 * @author tutt
 *
 */
@Data
@AllArgsConstructor
public class ConfirmerDto {
	
	/** 社員ID */
	private String confirmSID;
	
	/** 社員コード */
	private String confirmSCD;
	
	/** ビジネスネーム */
	private String businessName;

	/** 確認日時 */
	private GeneralDateTime confirmDateTime;
}
