package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.overtimeholiday.subtransfer.timezone;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.MaximumTimeZone;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.OvertimeHdHourTransfer;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubstituteTransferProcess;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.TransferResultAllFrame;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * @author thanh_nx
 *
 *         時間帯から振替時間を振り替える
 */
public class SubstituteTransferProcessTest {

	/*
	 * テストしたい内容
	 * 
	 * →振替可能時間<=0時に処理を停止し、「残りの振替可能時間振替をした後の時間（List）」戻る値
	 * 
	 * 準備するデータ
	 * 
	 * →振替可能時間<=0
	 */

	@Test
	public void test1() {

		MaximumTimeZone maxTimeZone = new MaximumTimeZone();
		maxTimeZone.getTimeSpan().add(Pair.of(new OverTimeFrameNo(1), new TimeSpanForDailyCalc(new TimeWithDayAttr(600), new TimeWithDayAttr(800))));// 最大時間帯(List）：最大時間帯

		List<OvertimeHdHourTransfer> timeAfterReflectApp = Arrays
				.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(444)));// 最大の時間(List)：時間外労働時間（振替用）

		List<OvertimeHdHourTransfer> maxTime = Arrays.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(444)));// 振替をした後の時間(List）：時間外労働時間（振替用）

		TransferResultAllFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTransferFromTransTimeZone", new AttendanceTime(0), maxTimeZone, maxTime, timeAfterReflectApp);

		assertThat(result.getTimeRemain().v()).isEqualTo(0);
		assertThat(result.getTimeAfterTransfer())
				.extracting(x -> x.getNo(), x -> x.getTime().v(), x -> x.getTransferTime().v())
				.contains(Tuple.tuple(1, 666, 444));

	}

	/*
	 * テストしたい内容
	 * 
	 * →処理中の最大時間帯に該当する枠NOの振替時間をチェクする場合は＜＝0、処理を停止し、「残りの振替可能時間振替をした後の時間（List）」戻る値
	 * 
	 * 準備するデータ
	 * 
	 * →最大の時間.振替時間<=0
	 */

	@Test
	public void test2() {
		MaximumTimeZone maxTimeZone = new MaximumTimeZone();
		maxTimeZone.getTimeSpan().add(Pair.of(new OverTimeFrameNo(1), new TimeSpanForDailyCalc(new TimeWithDayAttr(0), new TimeWithDayAttr(900))));// 最大時間帯(List）：最大時間帯
		maxTimeZone.getTimeSpan().add(Pair.of(new OverTimeFrameNo(2),  new TimeSpanForDailyCalc(new TimeWithDayAttr(900), new TimeWithDayAttr(1440))));

		List<OvertimeHdHourTransfer> timeAfterReflectApp = Arrays
				.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(360), new AttendanceTime(0)),
						 new OvertimeHdHourTransfer(2, new AttendanceTime(480), new AttendanceTime(0)));// 最大の時間(List)：時間外労働時間（振替用）

		List<OvertimeHdHourTransfer> maxTime = Arrays.asList(new OvertimeHdHourTransfer(0, new AttendanceTime(780), new AttendanceTime(120)),
																						  new OvertimeHdHourTransfer(1, new AttendanceTime(180), new AttendanceTime(360)));// 振替をした後の時間(List）：時間外労働時間（振替用）

		TransferResultAllFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTransferFromTransTimeZone", new AttendanceTime(480), maxTimeZone, maxTime, timeAfterReflectApp);

		assertThat(result.getTimeRemain().v()).isEqualTo(0);
		assertThat(result.getTimeAfterTransfer())
				.extracting(x -> x.getNo(), x -> x.getTime().v(), x -> x.getTransferTime().v())
				.contains(Tuple.tuple(1, 240, 120), Tuple.tuple(2, 120, 360));
	}

	/*
	 * テストしたい内容
	 * 
	 * →[input.振替をした後の時間（List）]を取得をチェクする場合は＜＝0、処理を停止し、「残りの振替可能時間振替をした後の時間（List）」戻る値
	 * 
	 * 準備するデータ
	 * 
	 * →最大の時間.振替時間<=0
	 */

	@Test
	public void test3() {
		MaximumTimeZone maxTimeZone = new MaximumTimeZone();
		maxTimeZone.getTimeSpan().add(Pair.of(new OverTimeFrameNo(1), new TimeSpanForDailyCalc(new TimeWithDayAttr(600), new TimeWithDayAttr(800))));// 最大時間帯(List）：最大時間帯

		List<OvertimeHdHourTransfer> timeAfterReflectApp = Arrays
				.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(0), new AttendanceTime(444)));// 最大の時間(List)：時間外労働時間（振替用）

		List<OvertimeHdHourTransfer> maxTime = Arrays.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(444)));// 振替をした後の時間(List）：時間外労働時間（振替用）

		TransferResultAllFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTransferFromTransTimeZone", new AttendanceTime(100), maxTimeZone, maxTime, timeAfterReflectApp);

		assertThat(result.getTimeRemain().v()).isEqualTo(100);
		assertThat(result.getTimeAfterTransfer())
				.extracting(x -> x.getNo(), x -> x.getTime().v(), x -> x.getTransferTime().v())
				.contains(Tuple.tuple(1, 0, 444));
	}

	/*
	 * テストしたい内容
	 * 
	 * →[input.振替をした後の時間（List）]を取得をチェクする場合は>0、時間の振替ができます
	 * 
	 * 準備するデータ
	 * 
	 * →最大の時間.振替時間＞0
	 */

	@Test
	public void test4() {
		MaximumTimeZone maxTimeZone = new MaximumTimeZone();
		maxTimeZone.getTimeSpan().add(Pair.of(new OverTimeFrameNo(1), new TimeSpanForDailyCalc(new TimeWithDayAttr(600), new TimeWithDayAttr(800))));// 最大時間帯(List）：最大時間帯

		List<OvertimeHdHourTransfer> timeAfterReflectApp = Arrays
				.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(444)));// 最大の時間(List)：時間外労働時間（振替用）

		List<OvertimeHdHourTransfer> maxTime = Arrays.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(444)));// 振替をした後の時間(List）：時間外労働時間（振替用）

		TransferResultAllFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTransferFromTransTimeZone", new AttendanceTime(100), maxTimeZone, maxTime, timeAfterReflectApp);

		assertThat(result.getTimeRemain().v()).isEqualTo(100);
		assertThat(result.getTimeAfterTransfer())
				.extracting(x -> x.getNo(), x -> x.getTime().v(), x -> x.getTransferTime().v())
				.contains(Tuple.tuple(1, 666, 444));// transfer += tranferableTime, time 
	}
}
