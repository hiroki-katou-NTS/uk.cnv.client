package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.overtimeholiday.subtransfer.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.OvertimeHdHourTransfer;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubstituteTransferProcess;

/**
 * @author thanh_nx
 *
 *          時間から振り替える
 */
@RunWith(JMockit.class)
public class SubstituteTransferProcessTest {

	/*
	 * テストしたい内容
	 * 
	 * →振替可能時間= 0の場合、時間外労働時間（振替用）値のままです
	 * 
	 * 準備するデータ
	 * 
	 * →未割り当ての時間を算出<=0
	 * 
	 */
	@Test
	public void test() {

		List<OvertimeHdHourTransfer> timeAfterReflectApp = Arrays
				.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(999)));

		List<OvertimeHdHourTransfer> result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTimeToTimeTransferAll", 0, timeAfterReflectApp);

		assertThat(result).extracting(x -> x.getNo(), x -> x.getTransferTime().v(), //
				x -> x.getTime().v())//
				.contains(Tuple.tuple(1, 999, 666));
	}

	/*
	 * テストしたい内容
	 * 
	 * →振替可能時間> 0の場合、時間外労働時間（振替用）値を更新する
	 * 
	 * 準備するデータ
	 * 
	 * →未割り当ての時間を算出<=0
	 * 
	 */
	@Test
	public void test2() {
		List<OvertimeHdHourTransfer> timeAfterReflectApp = Arrays
				.asList(new OvertimeHdHourTransfer(1, new AttendanceTime(666), new AttendanceTime(444)));

		List<OvertimeHdHourTransfer> result = NtsAssert.Invoke.staticMethod(SubstituteTransferProcess.class,
				"processTimeToTimeTransferAll", 111, timeAfterReflectApp);

		assertThat(result).extracting(x -> x.getNo(), x -> x.getTransferTime().v(), //
				x -> x.getTime().v())//
				.contains(Tuple.tuple(1, 555, 555));// 666-111, 666-111
	}

}
