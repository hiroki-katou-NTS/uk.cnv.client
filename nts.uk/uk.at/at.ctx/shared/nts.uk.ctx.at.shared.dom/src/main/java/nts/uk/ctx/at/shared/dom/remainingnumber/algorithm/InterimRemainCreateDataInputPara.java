package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.calendar.period.DatePeriod;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class InterimRemainCreateDataInputPara {
	/**会社ID	 */
	private String cid;
	/**	社員ID */
	private String sid;
	/**期間	 */
	private DatePeriod dateData;
	/**	残数作成元情報(実績)(List)*/
	private List<RecordRemainCreateInfor> recordData;
	/**	残数作成元の勤務予定(List)  */
	private List<ScheRemainCreateInfor> scheData;
	/**	残数作成元の申請(List) */
	private List<AppRemainCreateInfor> appData;
	//呼び出し元機能（予定／実績／申請）
	private CallFunction callFunction = CallFunction.APPLICATION;
	
	public InterimRemainCreateDataInputPara(String cid, String sid, DatePeriod dateData,
			List<RecordRemainCreateInfor> recordData, List<ScheRemainCreateInfor> scheData,
			List<AppRemainCreateInfor> appData) {
		super();
		this.cid = cid;
		this.sid = sid;
		this.dateData = dateData;
		this.recordData = recordData;
		this.scheData = scheData;
		this.appData = appData;
	}
}
