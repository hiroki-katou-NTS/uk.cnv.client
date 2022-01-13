package nts.uk.screen.com.app.command.cdl015;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.health.dom.emoji.manage.EmijiName;
import nts.uk.ctx.health.dom.emoji.manage.EmojiStateDetail;
import nts.uk.ctx.health.dom.emoji.manage.EmojiType;

/**
 * 
 * @author tutt
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmojiStateDetailCommand {
	// 感情名称
	private String emijiName;

	// 感情種類
	private int emojiType;

	public static EmojiStateDetailCommand fromDomain(EmojiStateDetail emojiStateDetail) {
		return new EmojiStateDetailCommand(emojiStateDetail.getEmijiName().v(), emojiStateDetail.getEmojiType().value);
	}

	public EmojiStateDetail toDomain() {
		return EmojiStateDetail.builder().emijiName(new EmijiName(this.emijiName))
				.emojiType(EmojiType.of(this.emojiType)).build();

	}
}
