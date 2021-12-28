package nts.uk.screen.com.ws.cdl015;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.uk.screen.com.app.command.cdl015.RegisterEmotionalSettingCommand;
import nts.uk.screen.com.app.command.cdl015.RegisterEmotionalSettingCommandHandler;
import nts.uk.screen.com.app.find.cdl015.StartNikonikoSettingDialog;
import nts.uk.screen.com.app.find.cdl015.StartNikonikoSettingDialog.EmojiStateMngDto;

/**
 * 
 * @author tutt
 *
 */
@Path("screen/com/cdl015")
@Produces("application/json")
public class Cdl015WebService {

	@Inject
	private StartNikonikoSettingDialog start;

	@Inject
	private RegisterEmotionalSettingCommandHandler register;

	/**
	 * ニコニコ利用設定ダイアログを起動する
	 * 
	 * @return
	 */
	@POST
	@Path("start")
	public EmojiStateMngDto startNikonikoSet() {
		return start.startNikonikoSet();
	}

	/**
	 * 感情状態の利用設定を登録する
	 * @param command
	 */
	@POST
	@Path("register")
	public void registerEmojiStateMng(RegisterEmotionalSettingCommand command) {
		register.handle(command);
	}

}
