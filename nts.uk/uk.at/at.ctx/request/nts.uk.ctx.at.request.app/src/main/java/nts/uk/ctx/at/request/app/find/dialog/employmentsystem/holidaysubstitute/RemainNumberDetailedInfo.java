package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
/**
 * 残数詳細情報
 * @author phongtq
 *
 */
@Data
public class RemainNumberDetailedInfo {
	// 期限日
	private String deadline;
	
	// 期限日状況
	private String dueDateStatus;
	
	// 消化状況
	private String digestionStatus;
	
	// 発生数
	private String numberOccurrences;
	
	// 発生日
	private String accrualDate;
	
	// 発生日状況
	private String occurrenceDateStatus;
	
	// 消化一覧
	private List<DigestionItem> listDigestion = new ArrayList<>();

	public RemainNumberDetailedInfo(String deadline, String dueDateStatus, String digestionStatus,
			String numberOccurrences, String accrualDate, String occurrenceDateStatus,
			List<DigestionItem> listDigestion) {
		super();
		this.deadline = deadline;
		this.dueDateStatus = dueDateStatus;
		this.digestionStatus = digestionStatus;
		this.numberOccurrences = numberOccurrences;
		this.accrualDate = accrualDate;
		this.occurrenceDateStatus = occurrenceDateStatus;
		this.listDigestion = listDigestion;
	}

	
}
