package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.aggframe.AggregateFrameTargetWork;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.aggframe.OuenAggregateFrameSetOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen.aggframe.OuenAggregateFrameSetOfMonthly.AggregateFrameUnit;

@Getter
/** 月別実績の応援時間 */
public class OuenTimeOfMonthly implements DomainObject {

	/** 集計枠集計明細: 作業集計枠明細 */
	private List<OuenWorkAggregateFrameDetail> frames;
	
	/** その他集計明細: 応援作業集計明細  */
	private OuenWorkAggregateDetail other;

	private OuenTimeOfMonthly(OuenWorkAggregateDetail other) {
		super();
		this.frames = new ArrayList<>();
		this.other = other;
	}
	
	public void add(OuenWorkAggregateFrameDetail frameDetail) {
		this.frames.add(frameDetail);
	}
	
	public static OuenTimeOfMonthly empty() {

		return new OuenTimeOfMonthly(OuenWorkAggregateDetail.empty());
	}
	
	public static OuenTimeOfMonthly create(OuenWorkAggregateDetail other) {

		return new OuenTimeOfMonthly(other);
	}
	
	/** 計算準備する */
	public static OuenTimeOfMonthly prepare(Optional<OuenAggregateFrameSetOfMonthly> aggregateFrameSet) {
		
		val ouen = OuenTimeOfMonthly.create(OuenWorkAggregateDetail.empty());
		
		aggregateFrameSet.ifPresent(afs -> {
			afs.getFrames().stream().forEach(af -> {
				ouen.add(OuenWorkAggregateFrameDetail.create(af.getFrameNo(),
														OuenWorkAggregateDetail.empty()));
			});
		});
		
		return ouen;
	}
	
	/** 応援作業時間を集計する */
	public void aggregate(RequireM1 require, List<OuenWorkTimeOfDailyAttendance> ouenTimes,
			List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheets, 
			Optional<OuenAggregateFrameSetOfMonthly> aggreFrameSet) {
		
		ouenTimes.stream().forEach(ot -> {
			
			val ouenTimeSheet = ouenTimeSheets.stream()
					.filter(ots -> ots.getWorkNo() == ot.getWorkNo()).findFirst();
			
			if(ouenTimeSheet.isPresent()) {
				aggregateOneOuen(require, ot, ouenTimeSheet.get(), aggreFrameSet);
			}
		});
	}
	
	/** 1個の応援時間を集計する */
	private void aggregateOneOuen(RequireM1 require, OuenWorkTimeOfDailyAttendance ouenTime, 
			OuenWorkTimeSheetOfDailyAttendance ouenTimeSheet, 
			Optional<OuenAggregateFrameSetOfMonthly> aggreFrameSet) {
		
		aggreFrameSet.ifPresent(afs -> {
			/** Requireで「作業階層設定」を取得する */
			val workLayerUsage = require.isUseWorkLayer(afs.getCompanyId());
			
			/** 作業階層ベースに集計すべきかを確認する */
			if (afs.getUnit() == AggregateFrameUnit.WORK && workLayerUsage) {
				
				aggregateOnWorkLayer(ouenTime, ouenTimeSheet, afs);
				return;
			} 
			
			aggregateSimple(ouenTime, ouenTimeSheet, afs);
		});
	}
	
	/** 日の応援時間を作業階層ベースに集計する */
	private void aggregateOnWorkLayer(OuenWorkTimeOfDailyAttendance ouenTime, 
			OuenWorkTimeSheetOfDailyAttendance ouenTimeSheet, 
			OuenAggregateFrameSetOfMonthly aggreFrameSet) {
		
		List<WorkLayerEachFrame> layers = new ArrayList<>();
		
		ouenTimeSheet.getWorkContent().getWork().ifPresent(work -> {
		
			aggreFrameSet.getFrames().stream().forEach(frame -> {
					
				val layer = ((AggregateFrameTargetWork) frame.getTarget()).sameWorkLayerNumber(work);
				
				if (layer > 0) {
					layers.add(new WorkLayerEachFrame(frame.getFrameNo(), layer));
				}
			});
		});
		
		if (layers.isEmpty()) {
			/** その他集計明細に加算する */
			this.other.plus(ouenTime);
		} else {
			/** 集計枠集計明細に加算する */
			layers.sort((l1, l2) -> Integer.compare(l1.layer, l2.layer));
			
			this.frames.stream().filter(f -> f.getFrameNo() == layers.get(0).frameNo)
						.findFirst().ifPresent(f -> {
				
				f.getDetail().plus(ouenTime);
			});
		}
	}
	
	/** 日の応援時間を簡単モード集計する */
	private void aggregateSimple(OuenWorkTimeOfDailyAttendance ouenTime, 
			OuenWorkTimeSheetOfDailyAttendance ouenTimeSheet, 
			OuenAggregateFrameSetOfMonthly aggreFrameSet) {
		
		for (val frame : aggreFrameSet.getFrames()) {
			
			if (frame.getTarget().canAggregate(ouenTimeSheet)) {
				
				/** 集計枠集計明細に加算する */
				this.frames.stream().filter(f -> f.getFrameNo() == frame.getFrameNo())
							.findFirst().ifPresent(f -> {
					
					f.getDetail().plus(ouenTime);
				});
				return;
			}
		}
		
		/** その他集計明細に加算する */
		this.other.plus(ouenTime);
	}
	
	public static interface RequireM1 {
		
		boolean isUseWorkLayer(String companyId);
	}
	
	/** 枠別作業階層情報 */
	@AllArgsConstructor
	private static class WorkLayerEachFrame {
		
		int frameNo;
		
		int layer;
	} 
}
