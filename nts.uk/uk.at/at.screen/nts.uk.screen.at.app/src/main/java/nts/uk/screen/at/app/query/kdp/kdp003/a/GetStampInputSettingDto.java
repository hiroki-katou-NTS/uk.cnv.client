package nts.uk.screen.at.app.query.kdp.kdp003.a;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.sys.portal.app.query.notice.MessageNoticeDto;

/**
 * 打刻入力(共有)でお知らせメッセージを取得する
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@Data
public class GetStampInputSettingDto {
	
	//Map<お知らせメッセージ>(List)
	private List<MessageNoticeDto> messageNotices;
	
	//会社単位の利用停止の設定
	
	//テナント単位の利用停止の設定
}
