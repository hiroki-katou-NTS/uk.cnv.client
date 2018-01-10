package nts.uk.ctx.at.function.dom.alarm.checkcondition.daily;


import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.ExtractionCondition;

/**
 * 日次のアラームチェック条件
 * @author tutk
 *
 */
@Getter
@NoArgsConstructor
public class DailyAlarmCondition extends ExtractionCondition  {

	private String dailyAlarmConID;
	
	private ConExtractedDaily conExtractedDaily;
	
	private boolean addApplication;
	
	private String errorAlarmCode;

	public DailyAlarmCondition(String dailyAlarmConID, ConExtractedDaily conExtractedDaily, boolean addApplication,
			String errorAlarmCode) {
		super();
		this.dailyAlarmConID = dailyAlarmConID;
		this.conExtractedDaily = conExtractedDaily;
		this.addApplication = addApplication;
		this.errorAlarmCode = errorAlarmCode;
	}

}
