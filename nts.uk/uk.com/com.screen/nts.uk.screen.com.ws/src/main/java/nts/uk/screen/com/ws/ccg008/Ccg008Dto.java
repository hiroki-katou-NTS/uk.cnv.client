package nts.uk.screen.com.ws.ccg008;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;

@Data
@AllArgsConstructor
public class Ccg008Dto {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** 締めID */
	private int closureId;

	/** 表示年月 */
	private int currentOrNextMonth;
	
	private String startDate;
	
	private String endDate;
	
	private String processDate;

	/**
	 * ビルトインユーザ用のダミーデータ
	 * @return
	 */
	public static Ccg008Dto forBuiltInUser() {
		return new Ccg008Dto(1, GeneralDate.today().yearMonth().v(), "", "", "");
	}
}
