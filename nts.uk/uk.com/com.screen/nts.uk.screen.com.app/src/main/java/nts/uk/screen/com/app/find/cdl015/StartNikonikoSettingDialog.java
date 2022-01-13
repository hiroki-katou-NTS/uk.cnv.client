package nts.uk.screen.com.app.find.cdl015;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.health.dom.emoji.manage.EmojiStateMng;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CDL_共通ダイアログ.CDL015_ニコニコ利用設定ダイアログ.メニュー別OCD.ニコニコ利用設定ダイアログを起動する
 * @author tutt
 *
 */
@Stateless
public class StartNikonikoSettingDialog {
	
	@Inject
	private GetEmotionalMngtScreenQuery query;
	
	public EmojiStateMngDto startNikonikoSet() {
		
		String loginCid = AppContexts.user().companyId();
		
		EmojiStateMng emojiStateMng = query.getEmotionalMngtS(loginCid);
		
		if (emojiStateMng == null) {
			return new EmojiStateMngDto();
		}
		
		return new EmojiStateMngDto(emojiStateMng.getManageEmojiState().value);
	}
	
	@AllArgsConstructor
	@Getter
	@NoArgsConstructor
	public class EmojiStateMngDto {
		
		private Integer manageEmojiState;
	}

}
