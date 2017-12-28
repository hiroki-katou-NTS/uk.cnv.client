package nts.uk.ctx.at.shared.app.find.ot.zerotime;

import lombok.Data;

/**
 * @author phongtq
 *  休日から休日への0時跨ぎ設定
 */

@Data
public class HdFromHdDto {
	/**変更前の休出枠NO*/
	private int overtimeFrameNo;
	
	/** 変更後の法定内休出NO*/
	private int legalHdNo;
	
	/** 変更後の法定外休出NO */
	private int nonLegalHdNo;
	
	/** 変更後の祝日休出NO */
	private int nonLegalPublicHdNo;
}
