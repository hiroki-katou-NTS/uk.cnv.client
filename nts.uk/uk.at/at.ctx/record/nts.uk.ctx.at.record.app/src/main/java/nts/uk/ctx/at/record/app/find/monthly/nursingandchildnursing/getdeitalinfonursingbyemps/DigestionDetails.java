package nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.getdeitalinfonursingbyemps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 消化詳細
 * 
 * @author tutk
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class DigestionDetails {
	/**
	 * 使用数
	 */
	private String numberOfUse;

	/**
	 * 消化日
	 */
	private String digestionDate;

	/**
	 * 消化状況
	 */
	private String digestionStatus;

	public DigestionDetails() {
		this.numberOfUse = "";
		this.digestionDate = "";
		this.digestionStatus = "";
	}

}
