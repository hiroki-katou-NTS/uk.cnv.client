package nts.uk.screen.com.app.find.cdl015;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.health.dom.emoji.manage.EmojiStateMng;
import nts.uk.ctx.health.dom.emoji.manage.EmojiStateMngRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CDL_共通ダイアログ.CDL015_ニコニコ利用設定ダイアログ.メニュー別OCD.感情筐体管理を取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetEmotionalMngtScreenQuery {

	@Inject
	private EmojiStateMngRepository emojiRepo;

	public EmojiStateMng getEmotionalMngtS(String loginCid) {

		// get(ログイン会社ID): Optional<感情状態管理>
		Optional<EmojiStateMng> emoji = emojiRepo.getByCid(loginCid);

		if (!emoji.isPresent()) {
			return null;
		}

		return emoji.get();
	}

}
