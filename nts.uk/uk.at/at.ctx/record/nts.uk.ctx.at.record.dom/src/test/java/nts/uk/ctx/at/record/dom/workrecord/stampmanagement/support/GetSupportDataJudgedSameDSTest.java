/**
 * 
 */
package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflect.StartAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * @author laitv
 */

@RunWith(JMockit.class)
public class GetSupportDataJudgedSameDSTest {

	@Injectable
	private GetSupportDataJudgedSameDS.Required required;
	
	// required.getCriteriaSameStampOfSupport = null
	@Test
	public void case1() {
		List<OuenWorkTimeSheetOfDailyAttendance> supportDataList = new ArrayList<OuenWorkTimeSheetOfDailyAttendance>();
		TimeWithDayAttr targetSupportData = new TimeWithDayAttr(0);
		StartAtr isStart = StartAtr.START_OF_SUPPORT;
		new Expectations() {
			{
				required.getCriteriaSameStampOfSupport();
				result = null;
			}
		};
		Optional<OuenWorkTimeSheetOfDailyAttendance> rs = GetSupportDataJudgedSameDS.getSupportDataJudgedSame(required, supportDataList, targetSupportData, isStart);
		assertThat(rs).isEqualTo(Optional.empty());
	}
	
	// required.getCriteriaSameStampOfSupport != null
	// isStart = true
	// boolean check = jcSameStampOfSupport.checkStampRecognizedAsSame(standardStampStart, targetStampStart) ==> true
	@Test
	public void case2() {
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime1 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(5))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(60)))), Optional.empty());
		
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime2 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(10))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(60)))), Optional.empty());
		
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime3 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(1))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(60)))), Optional.empty());
		
		List<OuenWorkTimeSheetOfDailyAttendance> supportDataList = new ArrayList<OuenWorkTimeSheetOfDailyAttendance>();
		supportDataList.add(ouenWorkTime1);
		supportDataList.add(ouenWorkTime2);
		supportDataList.add(ouenWorkTime3);
		
		TimeWithDayAttr targetSupportData = new TimeWithDayAttr(20);
		
		StartAtr isStart = StartAtr.START_OF_SUPPORT;
		
		JudgmentCriteriaSameStampOfSupport support = new JudgmentCriteriaSameStampOfSupport("cid", new RangeRegardedSupportStamp(10), new MaximumNumberOfSupport(20));
		new Expectations() {
			{
				required.getCriteriaSameStampOfSupport();
				result = support;
			}
		};
		Optional<OuenWorkTimeSheetOfDailyAttendance> rs = GetSupportDataJudgedSameDS.getSupportDataJudgedSame(required, supportDataList, targetSupportData, isStart);
		assertThat(rs).isEqualTo(Optional.of(supportDataList.get(1)));
	}
	
	// required.getCriteriaSameStampOfSupport != null
	// isStart = true
	// boolean check = jcSameStampOfSupport.checkStampRecognizedAsSame(standardStampStart, targetStampStart) ==> false
	@Test
	public void case3() {
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime1 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(5))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(60)))), Optional.empty());
		
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime2 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(10))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(60)))), Optional.empty());
		
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime3 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(1))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(60)))), Optional.empty());
		
		List<OuenWorkTimeSheetOfDailyAttendance> supportDataList = new ArrayList<OuenWorkTimeSheetOfDailyAttendance>();
		supportDataList.add(ouenWorkTime1);
		supportDataList.add(ouenWorkTime2);
		supportDataList.add(ouenWorkTime3);
		
		TimeWithDayAttr targetSupportData = new TimeWithDayAttr(120);
		
		StartAtr isStart = StartAtr.START_OF_SUPPORT;
		
		JudgmentCriteriaSameStampOfSupport support = new JudgmentCriteriaSameStampOfSupport("cid", new RangeRegardedSupportStamp(10), new MaximumNumberOfSupport(20));
		new Expectations() {
			{
				required.getCriteriaSameStampOfSupport();
				result = support;
			}
		};
		Optional<OuenWorkTimeSheetOfDailyAttendance> rs = GetSupportDataJudgedSameDS.getSupportDataJudgedSame(required, supportDataList, targetSupportData, isStart);
		assertThat(rs).isEqualTo(Optional.empty());
	}

	// required.getCriteriaSameStampOfSupport != null
	// isStart = false
	// boolean check = jcSameStampOfSupport.checkStampRecognizedAsSame(standardStampStart, targetStampStart) ==> true
	@Test
	public void case4() {
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime1 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(5))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(30)))), Optional.empty());
		
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime2 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(10))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(30)))), Optional.empty());
		
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime3 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(1))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(20)))), Optional.empty());
		
		List<OuenWorkTimeSheetOfDailyAttendance> supportDataList = new ArrayList<OuenWorkTimeSheetOfDailyAttendance>();
		supportDataList.add(ouenWorkTime1);
		supportDataList.add(ouenWorkTime2);
		supportDataList.add(ouenWorkTime3);
		
		TimeWithDayAttr targetSupportData = new TimeWithDayAttr(10); 
		
		StartAtr isStart = StartAtr.END_OF_SUPPORT;

		JudgmentCriteriaSameStampOfSupport support = new JudgmentCriteriaSameStampOfSupport("cid", new RangeRegardedSupportStamp(10), new MaximumNumberOfSupport(20));
		new Expectations() {
			{
				required.getCriteriaSameStampOfSupport();
				result = support;
			}
		};
		Optional<OuenWorkTimeSheetOfDailyAttendance> rs = GetSupportDataJudgedSameDS.getSupportDataJudgedSame(required, supportDataList, targetSupportData, isStart);
		assertThat(rs).isEqualTo(Optional.of(supportDataList.get(2)));
	}
	
	// required.getCriteriaSameStampOfSupport != null
	// isStart = false
	// boolean check = jcSameStampOfSupport.checkStampRecognizedAsSame(standardStampStart, targetStampStart) ==> false
	@Test
	public void case5() {
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime1 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(5))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(30)))), Optional.empty());
		
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime2 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(10))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(40)))), Optional.empty());
		
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime3 = OuenWorkTimeSheetOfDailyAttendance.create(
				SupportFrameNo.of(1), null, TimeSheetOfAttendanceEachOuenSheet.create(
							new WorkNo(1), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(1))), 
							Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), new TimeWithDayAttr(50)))), Optional.empty());
		
		List<OuenWorkTimeSheetOfDailyAttendance> supportDataList = new ArrayList<OuenWorkTimeSheetOfDailyAttendance>();
		supportDataList.add(ouenWorkTime1);
		supportDataList.add(ouenWorkTime2);
		supportDataList.add(ouenWorkTime3);
		
		TimeWithDayAttr targetSupportData = new TimeWithDayAttr(10);
		
		StartAtr isStart = StartAtr.END_OF_SUPPORT;

		JudgmentCriteriaSameStampOfSupport support = new JudgmentCriteriaSameStampOfSupport("cid", new RangeRegardedSupportStamp(10), new MaximumNumberOfSupport(20));
		new Expectations() {
			{
				required.getCriteriaSameStampOfSupport();
				result = support;
			}
		};
		Optional<OuenWorkTimeSheetOfDailyAttendance> rs = GetSupportDataJudgedSameDS.getSupportDataJudgedSame(required,
				supportDataList, targetSupportData, isStart);
		assertThat(rs).isEqualTo(Optional.empty());
	} 
	
	
	/**
	 * required.getCriteriaSameStampOfSupport != null
	 * isStart = true
	 * start_ouenWorkTime1 = 15, start_targetSupportData = 20, RangeRegardedSupportStamp(応援打刻の同一とみなす範囲) = 10 => jcSameStampOfSupport.checkStampRecognizedAsSame(start_ouenWorkTime1, start_targetSupportData) ==> true
	 * start_ouenWorkTime2 = 5, start_targetSupportData = 20,  RangeRegardedSupportStamp(応援打刻の同一とみなす範囲) = 10 => jcSameStampOfSupport.checkStampRecognizedAsSame(start_ouenWorkTime2, start_targetSupportData) ==> false
	 * start_ouenWorkTime3 = 20, start_targetSupportData = 20, RangeRegardedSupportStamp(応援打刻の同一とみなす範囲) = 10 => jcSameStampOfSupport.checkStampRecognizedAsSame(start_ouenWorkTime3, start_targetSupportData) ==> true
	 * 
	 * => return ouenWorkTime1;
	 */
	@Test
	public void case6() {
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime1 = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1), null,
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(15))),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(60)))), Optional.empty());

		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime2 = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1), null,
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(5))),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(60)))), Optional.empty());

		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime3 = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1), null,
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(20))),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(60)))), Optional.empty());

		List<OuenWorkTimeSheetOfDailyAttendance> supportDataList = new ArrayList<OuenWorkTimeSheetOfDailyAttendance>();
		supportDataList.add(ouenWorkTime1);
		supportDataList.add(ouenWorkTime2);
		supportDataList.add(ouenWorkTime3);
		
		TimeWithDayAttr targetSupportData = new TimeWithDayAttr(20);

		StartAtr isStart = StartAtr.START_OF_SUPPORT;

		JudgmentCriteriaSameStampOfSupport support = new JudgmentCriteriaSameStampOfSupport("cid", new RangeRegardedSupportStamp(10), new MaximumNumberOfSupport(20));
		new Expectations() {
			{
				required.getCriteriaSameStampOfSupport();
				result = support;
			}
		};
		Optional<OuenWorkTimeSheetOfDailyAttendance> rs = GetSupportDataJudgedSameDS.getSupportDataJudgedSame(required,supportDataList, targetSupportData, isStart);
		assertThat(rs).isEqualTo(Optional.of(ouenWorkTime1));
	}
	
	/**
	 * required.getCriteriaSameStampOfSupport != null
	 * isStart = false
	 * end_ouenWorkTime1 = 15, end_targetSupportData = 20, RangeRegardedSupportStamp(応援打刻の同一とみなす範囲) = 10 => jcSameStampOfSupport.checkStampRecognizedAsSame(end_ouenWorkTime1, end_targetSupportData) ==> true
	 * end_ouenWorkTime2 = 5,  end_targetSupportData = 20, RangeRegardedSupportStamp(応援打刻の同一とみなす範囲) = 10 => jcSameStampOfSupport.checkStampRecognizedAsSame(end_ouenWorkTime2, end_targetSupportData) ==> false
	 * end_ouenWorkTime3 = 20, end_targetSupportData = 20, RangeRegardedSupportStamp(応援打刻の同一とみなす範囲) = 10 => jcSameStampOfSupport.checkStampRecognizedAsSame(end_ouenWorkTime3, end_targetSupportData) ==> true
	 * 
	 * => return ouenWorkTime1;
	 */
	@Test
	public void case7() {
		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime1 = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1), null,
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(15))),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(60)))), Optional.empty());

		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime2 = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1), null,
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(5))),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(60)))), Optional.empty());

		OuenWorkTimeSheetOfDailyAttendance ouenWorkTime3 = OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(1), null,
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(20))),
						Optional.of(new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(),new TimeWithDayAttr(60)))), Optional.empty());

		List<OuenWorkTimeSheetOfDailyAttendance> supportDataList = new ArrayList<OuenWorkTimeSheetOfDailyAttendance>();
		supportDataList.add(ouenWorkTime1);
		supportDataList.add(ouenWorkTime2);
		supportDataList.add(ouenWorkTime3);
		
		TimeWithDayAttr targetSupportData = new TimeWithDayAttr(70);

		StartAtr isStart = StartAtr.END_OF_SUPPORT;

		JudgmentCriteriaSameStampOfSupport support = new JudgmentCriteriaSameStampOfSupport("cid", new RangeRegardedSupportStamp(10), new MaximumNumberOfSupport(20));
		new Expectations() {
			{
				required.getCriteriaSameStampOfSupport();
				result = support;
			}
		};
		Optional<OuenWorkTimeSheetOfDailyAttendance> rs = GetSupportDataJudgedSameDS.getSupportDataJudgedSame(required,supportDataList, targetSupportData, isStart);
		assertThat(rs).isEqualTo(Optional.of(ouenWorkTime1));
	}
	

}
