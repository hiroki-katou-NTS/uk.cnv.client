package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.breakouting.reflectgoingoutandreturn.reflecttimeofday;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.breakouting.reflectgoingoutandreturn.TimeFrame;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.breakouting.reflectgoingoutandreturn.reflecttimeofday.imprint.ImprintReflectTimeOfDay;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.breakouting.reflectgoingoutandreturn.reflecttimeofday.preparetimeframe.PrepareTimeFrame;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.reflectattdclock.AttendanceAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;

/**
 * 時間帯反映する (new_2020)
 * 
 * @author tutk
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ReflectTimeOfDay {
	@Inject
	private PrepareTimeFrame prepareTimeFrame;
	
	@Inject
	private ImprintReflectTimeOfDay imprintReflectTimeOfDay;
	public void reflectTimeOfDay(String employeeId, GeneralDate ymd, Stamp stamp, int maxNumberOfUses,
			List<TimeFrame> listTimeFrame, AttendanceAtr attendanceAtr,WorkTimeCode workTimeCode) {
		//時間帯の枠を用意する
		prepareTimeFrame.prepareTimeFrame(maxNumberOfUses, listTimeFrame, attendanceAtr);
		listTimeFrame = listTimeFrame.stream().sorted(Comparator.comparing(TimeFrame::getFrameNo)).collect(Collectors.toList());
		for(int i = 0;i<listTimeFrame.size();i++) {
			TimeFrame timeFrame = listTimeFrame.get(i);
			Optional<TimeFrame> timeFrameNext = Optional.empty();
			if((i+1) < listTimeFrame.size()) {
				timeFrameNext = Optional.of(listTimeFrame.get(i+1));
			}
			//戻りOR終了の場合 (臨時退勤)
			if(stamp.getType().getChangeClockArt() == ChangeClockArt.RETURN || 
			   stamp.getType().getChangeClockArt() == ChangeClockArt.TEMPORARY_LEAVING ) {
				if(!listTimeFrame.get(i).getEnd().isPresent()
						|| !timeFrame.getEnd().isPresent()
						|| !timeFrame.getEnd().get().getStamp().isPresent()
						|| timeFrame.getEnd().get().getStamp().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.AUTOMATIC_SET
						|| !timeFrame.getEnd().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
						|| timeFrame.getEnd().get().getStamp().get().getTimeDay().getTimeWithDay().get().valueAsMinutes() 
									== stamp.getStampDateTime().clockHourMinute().valueAsMinutes()
						) {
					//打刻反映する
					TimeActualStamp actualStamp = imprintReflectTimeOfDay.imprint(false, timeFrame, timeFrameNext, stamp, timeFrame.getEnd(),workTimeCode,ymd);
					listTimeFrame.get(i).setEnd(Optional.ofNullable(actualStamp));
					break;
				}
			//外出OR開始の場合(臨時出勤 )
			}else if(stamp.getType().getChangeClockArt() == ChangeClockArt.GO_OUT || 
					   stamp.getType().getChangeClockArt() == ChangeClockArt.TEMPORARY_WORK ) {
				if(!listTimeFrame.get(i).getStart().isPresent() 
						|| !timeFrame.getStart().isPresent()
						|| !timeFrame.getStart().get().getStamp().isPresent()
						|| timeFrame.getStart().get().getStamp().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.AUTOMATIC_SET
						|| !timeFrame.getStart().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
						|| timeFrame.getStart().get().getStamp().get().getTimeDay().getTimeWithDay().get().valueAsMinutes() 
									== stamp.getStampDateTime().clockHourMinute().valueAsMinutes()
						) {
					//打刻反映する
					TimeActualStamp actualStamp = imprintReflectTimeOfDay.imprint(true, timeFrame, timeFrameNext, stamp, timeFrame.getStart(),workTimeCode,ymd);
					listTimeFrame.get(i).setStart(Optional.ofNullable(actualStamp));
					break;
				}
			}
		}
		//空っぽの時間帯を削除する
		listTimeFrame = listTimeFrame.stream().filter(x -> x.getStart().isPresent() || x.getEnd().isPresent())
				.collect(Collectors.toList());
		//枠NOに番号付けする
		for(int i =1;i<=listTimeFrame.size();i++) {
			listTimeFrame.get(i-1).setFrameNo(i);
		}
	}
}
