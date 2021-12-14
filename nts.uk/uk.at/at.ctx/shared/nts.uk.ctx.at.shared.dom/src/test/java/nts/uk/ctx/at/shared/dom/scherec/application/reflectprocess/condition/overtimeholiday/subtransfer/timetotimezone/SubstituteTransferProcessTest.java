package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.overtimeholiday.subtransfer.timetotimezone;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import lombok.val;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.OvertimeHdHourTransfer;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubstituteTransferProcess;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.TransferResultFrame;

/**
 * @author thanh_nx
 *
 *         時間帯から時間の振替
 */
public class SubstituteTransferProcessTest {

	/*
	 * テストしたい内容
	 * 
	 * →時間帯から時間の振替
	 * 
	 * 準備するデータ
	 * 
	 * →input. 振替可能時間 <= input.最大時間とinput. 時間外労働時間.時間 <= input.振替可能時間
	 */
	@Test
	public void test1() {

		val timeAfterReflectApp = new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(444));

		TransferResultFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTimeZoneDetail", 700, 800, timeAfterReflectApp);
		assertThat(result.getTime().v()).isEqualTo(34);
		assertThat(result.getMaximumTime().getNo()).isEqualTo(1);
		assertThat(result.getMaximumTime().getTime().v()).isEqualTo(0);
		assertThat(result.getMaximumTime().getTransferTime().v()).isEqualTo(1110);

	}

	/*
	 * テストしたい内容
	 * 
	 * →時間帯から時間の振替
	 * 
	 * 準備するデータ
	 * 
	 * →input. 振替可能時間 <= input.最大時間とinput. 時間外労働時間.時間 >input.振替可能時間
	 */
	@Test
	public void test2() {

		val timeAfterReflectApp = new OvertimeHdHourTransfer(1, new AttendanceTime(900), new AttendanceTime(444));
		TransferResultFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTimeZoneDetail", 700, 800, timeAfterReflectApp);
		assertThat(result.getTime().v()).isEqualTo(0);
		assertThat(result.getMaximumTime().getNo()).isEqualTo(1);
		assertThat(result.getMaximumTime().getTime().v()).isEqualTo(200);
		assertThat(result.getMaximumTime().getTransferTime().v()).isEqualTo(1144);

	}

	/*
	 * テストしたい内容
	 * 
	 * →時間帯から時間の振替
	 * 
	 * 準備するデータ
	 * 
	 * →input. 振替可能時間 ＞ input.最大時間とinput. 時間外労働時間.時間 <= input.最大時間
	 */
	@Test
	public void test3() {

		val timeAfterReflectApp = new OvertimeHdHourTransfer(1, new AttendanceTime(600), new AttendanceTime(444));
		TransferResultFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTimeZoneDetail", 800, 700, timeAfterReflectApp);
		assertThat(result.getTime().v()).isEqualTo(200);
		assertThat(result.getMaximumTime().getNo()).isEqualTo(1);
		assertThat(result.getMaximumTime().getTime().v()).isEqualTo(0);
		assertThat(result.getMaximumTime().getTransferTime().v()).isEqualTo(1044);

	}

	/*
	 * テストしたい内容
	 * 
	 * →時間帯から時間の振替
	 * 
	 * 準備するデータ
	 * 
	 * →input. 振替可能時間 > input.最大時間とinput. 時間外労働時間.時間 >input.最大時間
	 */
	@Test
	public void test4() {

		val timeAfterReflectApp = new OvertimeHdHourTransfer(1, new AttendanceTime(900), new AttendanceTime(444));
		TransferResultFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTimeZoneDetail", 800, 700, timeAfterReflectApp);
		assertThat(result.getTime().v()).isEqualTo(100);
		assertThat(result.getMaximumTime().getNo()).isEqualTo(1);
		assertThat(result.getMaximumTime().getTime().v()).isEqualTo(200);
		assertThat(result.getMaximumTime().getTransferTime().v()).isEqualTo(1144);

	}
}
