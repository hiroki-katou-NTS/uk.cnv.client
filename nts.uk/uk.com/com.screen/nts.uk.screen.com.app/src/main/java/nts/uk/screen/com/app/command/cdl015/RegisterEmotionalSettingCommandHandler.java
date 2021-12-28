package nts.uk.screen.com.app.command.cdl015;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.health.dom.emoji.manage.EmojiStateMng;
import nts.uk.ctx.health.dom.emoji.manage.EmojiStateMngRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CDL_共通ダイアログ.CDL015_ニコニコ利用設定ダイアログ.メニュー別OCD.ニコニコ利用設定を登録する
 * 
 * @author tutt
 *
 */
@Stateless
@Transactional
public class RegisterEmotionalSettingCommandHandler extends CommandHandler<RegisterEmotionalSettingCommand> {

	@Inject
	private EmojiStateMngRepository emojiStateMngRepo;

	@Override
	protected void handle(CommandHandlerContext<RegisterEmotionalSettingCommand> context) {
		String loginCid = AppContexts.user().companyId();

		RegisterEmotionalSettingCommand command = context.getCommand();

		// 1. get(ログイン会社ID)
		Optional<EmojiStateMng> emoji = emojiStateMngRepo.getByCid(loginCid);

		if (!emoji.isPresent()) {

			// 感情状態管理 is empty
			emojiStateMngRepo.insert(EmojiStateMng.createFromMemento(command));
		} else {

			// 感情状態管理 is not empty
			EmojiStateMng updatedDomain = emoji.get();
			command.setEmojiStateSettings(updatedDomain.getEmojiStateSettings().stream()
					.map(m -> EmojiStateDetailCommand.fromDomain(m)).collect(Collectors.toList()));
			updatedDomain.getMemento(command);

			emojiStateMngRepo.update(updatedDomain);
		}

	}

}
