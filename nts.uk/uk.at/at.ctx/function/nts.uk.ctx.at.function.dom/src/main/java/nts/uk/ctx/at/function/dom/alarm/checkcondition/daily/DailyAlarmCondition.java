package nts.uk.ctx.at.function.dom.alarm.checkcondition.daily;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.ExtractionCondition;

/**
 * 日次のアラームチェック条件
 * 
 * @author tutk
 *
 */
@Getter
@NoArgsConstructor
public class DailyAlarmCondition extends ExtractionCondition {

	private String dailyAlarmConID;

	private ConExtractedDaily conExtractedDaily;

	private boolean addApplication;

	private List<String> extractConditionWorkRecord = new ArrayList<String>();

	private List<Integer> fixedExtractConditionWorkRecord = new ArrayList<Integer>();

	private List<String> errorAlarmCode = new ArrayList<String>();

	public DailyAlarmCondition(String dailyAlarmConID, int conExtractedDaily, boolean addApplication,
			List<String> errorAlarmWk, List<Integer> errorAlarmFixed, List<String> errorAlarmCode) {
		super();
		this.dailyAlarmConID = dailyAlarmConID;
		this.conExtractedDaily = EnumAdaptor.valueOf(conExtractedDaily, ConExtractedDaily.class);
		;
		this.addApplication = addApplication;
		this.extractConditionWorkRecord = errorAlarmWk;
		this.fixedExtractConditionWorkRecord = errorAlarmFixed;
		this.errorAlarmCode = errorAlarmCode;
	}

	@Override
	public void changeState(ExtractionCondition extractionCondition) {
		if (extractionCondition instanceof DailyAlarmCondition) {
			DailyAlarmCondition value = (DailyAlarmCondition) extractionCondition;
			this.addApplication = value.addApplication;
			this.conExtractedDaily = value.conExtractedDaily;
			this.errorAlarmCode = value.errorAlarmCode;
			this.extractConditionWorkRecord = value.extractConditionWorkRecord;
			this.fixedExtractConditionWorkRecord = value.fixedExtractConditionWorkRecord;
		}
	}

}
