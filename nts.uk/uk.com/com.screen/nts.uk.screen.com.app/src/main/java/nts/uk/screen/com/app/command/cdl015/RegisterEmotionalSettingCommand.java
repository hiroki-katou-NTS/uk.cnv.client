package nts.uk.screen.com.app.command.cdl015;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.health.dom.emoji.manage.EmojiStateDetail;
import nts.uk.ctx.health.dom.emoji.manage.EmojiStateMng;

/**
 * 
 * @author tutt
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterEmotionalSettingCommand implements EmojiStateMng.MementoGetter, EmojiStateMng.MementoSetter {

	// 会社ID
	private String cid;

	// 感情状態を管理する
	private int manageEmojiState;

	// 感情状態詳細
	private List<EmojiStateDetailCommand> emojiStateSettings;

	public EmojiStateMng toDomain() {
		return EmojiStateMng.createFromMemento(this);
	}

	@Override
	public void setCid(String cid) {
		this.cid = cid;
	}

	@Override
	public void setManageEmojiState(Integer manageEmojiState) {
		this.manageEmojiState = manageEmojiState;
	}

	@Override
	public void setEmojiStateSetting(List<EmojiStateDetail> emojiStateSettings) {
		this.emojiStateSettings = emojiStateSettings.stream().map(m -> EmojiStateDetailCommand.fromDomain(m))
				.collect(Collectors.toList());
	}

	@Override
	public String getCid() {
		return this.cid;
	}

	@Override
	public Integer getManageEmojiState() {
		return this.manageEmojiState;
	}

	@Override
	public List<EmojiStateDetail> getEmojiStateSettings() {
		return this.emojiStateSettings.stream().map(m -> m.toDomain()).collect(Collectors.toList());
	}
}
