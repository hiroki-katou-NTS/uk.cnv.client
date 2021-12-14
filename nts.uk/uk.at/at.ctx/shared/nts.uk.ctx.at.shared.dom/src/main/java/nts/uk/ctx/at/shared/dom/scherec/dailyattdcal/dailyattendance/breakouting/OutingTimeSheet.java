package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting;

import java.util.ArrayList;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionClassification;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.WorkingBreakTimeAtr;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author nampt
 * 外出時間帯
 *
 */
@AllArgsConstructor
@Getter
@Setter
public class OutingTimeSheet extends DomainObject {
	
	/*
	 * 外出枠NO
	 */
	private OutingFrameNo outingFrameNo;
	
	// 外出: 勤怠打刻
	private Optional<WorkStamp> goOut;
	
	/*
	 * 外出理由
	 */
	private GoingOutReason reasonForGoOut;
	
	// 戻り: 勤怠打刻
	private Optional<WorkStamp> comeBack;

	public void setProperty(OutingFrameNo outingFrameNo, Optional<WorkStamp> goOut,
			GoingOutReason reasonForGoOut, Optional<WorkStamp> comeBack) {
		this.outingFrameNo = outingFrameNo;
		this.goOut = goOut;
		this.reasonForGoOut = reasonForGoOut;
		this.comeBack = comeBack;
		
	}
	
	/**
	 * 自分自身を控除項目の時間帯に変換する
	 * @return 控除項目の時間帯
	 */
	public TimeSheetOfDeductionItem toTimeSheetOfDeductionItem() {
		return TimeSheetOfDeductionItem.createTimeSheetOfDeductionItem(new TimeSpanForDailyCalc(this.goOut.map(x-> x.getTimeDay()
																									.getTimeWithDay().orElse(TimeWithDayAttr.THE_PRESENT_DAY_0000))
																									.orElse(TimeWithDayAttr.THE_PRESENT_DAY_0000), 
																										this.comeBack.get().getTimeDay()
																											.getTimeWithDay().orElse(TimeWithDayAttr.THE_PRESENT_DAY_0000)),
																			  new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN),
																			  new ArrayList<>(),
																			  new ArrayList<>(),
																			  WorkingBreakTimeAtr.NOTWORKING,
																			  Finally.of(this.reasonForGoOut == null ? GoingOutReason.PRIVATE : this.reasonForGoOut),
																			  Finally.empty(),
																			  Optional.empty(),
																			  DeductionClassification.GO_OUT,
																			  Optional.empty(),
																			  false
																			  );
	}
	
	/**
	 * 計算可能な状態か判断する	
	 * @return 計算可能である
	 */
	public boolean isCalcState() {
		return isCalcGoOut() && isCalcComeBack();
	}
	
	/**
	 * 外出時刻が計算できる状態になっているか判定する
	 * (nullになっていないか)
	 * @return 計算可能である。
	 */
	private boolean isCalcGoOut() {
		if(this.getGoOut() != null && this.getGoOut().isPresent()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 戻り時刻が計算できる状態になっているか判定する
	 * (null になっていないか)
	 * @return　 計算可能である。
	 */
	private boolean isCalcComeBack() {
		if(this.getComeBack() != null && this.getComeBack().isPresent()) {
			return true;
		}
		return false;		
	}
	
	/**
	 * 	[2] 時間帯を返す
	 * @return
	 */
	public Optional<TimeSpanForCalc> getTimeZone(){
		
		if( this.isCalcState() ) {
			
			if(goOut.get().lessThan(comeBack.get())) {
				
				return Optional.of( new TimeSpanForCalc( 
								goOut.get().getTimeDay().getTimeWithDay().get() , 
								comeBack.get().getTimeDay().getTimeWithDay().get()));
			}  
		}
		
		return Optional.empty();
	}
	
	// NOとデフォルトを作成する
	public static OutingTimeSheet createDefaultWithNo(int no, GoingOutReason reason) {
		return new OutingTimeSheet(new OutingFrameNo(no), 
				Optional.of(WorkStamp.createDefault()), 
//				new AttendanceTime(0), 
//				new AttendanceTime(0), 
				reason,
				Optional.of(WorkStamp.createDefault()));
	}
	
	public boolean leakageCheck() {
		Optional<TimeWithDayAttr> goOutTimeAtr = getGoOutWithTimeDay();
		Optional<TimeWithDayAttr> goComeBack = getComeBackWithTimeDay();

		return (goOutTimeAtr.isPresent() && goComeBack.isPresent())
				|| (!goOutTimeAtr.isPresent() && !goComeBack.isPresent());

	}

	public Optional<TimeWithDayAttr> getGoOutWithTimeDay() {
		return this.getGoOut().flatMap(x -> x.getWithTimeDay());
	}

	public Optional<TimeWithDayAttr> getComeBackWithTimeDay() {
		return this.getComeBack().flatMap(x -> x.getWithTimeDay());
	}
}
