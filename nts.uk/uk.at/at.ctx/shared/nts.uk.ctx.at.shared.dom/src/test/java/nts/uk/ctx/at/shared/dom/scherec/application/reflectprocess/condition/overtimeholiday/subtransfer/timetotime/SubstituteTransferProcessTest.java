package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.overtimeholiday.subtransfer.timetotime;

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
 *         時間から時間の振替
 */
public class SubstituteTransferProcessTest {

	/*
	 * テストしたい内容
	 * 
	 * →「時間外労働時間.時間<= input. 振替可能時間」のとき時間外労働時間.振替時間 += 時間外労働時間.時間
	 * 
	 * 準備するデータ
	 * 
	 * →「時間外労働時間.時間<= input. 振替可能時間」
	 */
	@Test
	public void test1() {
		val timeReflectApp = new OvertimeHdHourTransfer(1, new AttendanceTime(10), // 時間
				new AttendanceTime(9));// 振替時間

		TransferResultFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTimeToTimeTransfer", 11, timeReflectApp);
		
		assertThat(result.getTime().v()).isEqualTo(1);

		assertThat(result.getMaximumTime().getTime().v()).isEqualTo(0);

		assertThat(result.getMaximumTime().getTransferTime().v()).isEqualTo(19);
	}

	/*
	 * テストしたい内容
	 * 
	 * →「時間外労働時間.時間＞ input. 振替可能時間」のとき時間外労働時間.振替時間 += 振替可能時間
	 * 
	 * 準備するデータ
	 * 
	 * →「時間外労働時間.時間>input. 振替可能時間」
	 */
	@Test
	public void test2() {
		val timeReflectApp = new OvertimeHdHourTransfer(1, new AttendanceTime(10), // 時間
				new AttendanceTime(9));// 振替時間

		TransferResultFrame result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTimeToTimeTransfer", 8, timeReflectApp);

		assertThat(result.getTime().v()).isEqualTo(0);

		assertThat(result.getMaximumTime().getTime().v()).isEqualTo(2);

		assertThat(result.getMaximumTime().getTransferTime().v()).isEqualTo(17);
	}

}
