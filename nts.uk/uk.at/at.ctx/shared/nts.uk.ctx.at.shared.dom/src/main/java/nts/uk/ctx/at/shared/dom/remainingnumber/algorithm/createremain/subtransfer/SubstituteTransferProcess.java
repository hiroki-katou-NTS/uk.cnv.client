package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.GetSubHolOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSet;

/**
 * @author thanh_nx
 *
 *         代休振替処理
 */
public class SubstituteTransferProcess {
	public static List<OvertimeHdHourTransfer> process(Require require, String cid, Optional<String> workTimeCode,
			CompensatoryOccurrenceDivision originAtr, MaximumTimeZone maxTimeZone, List<OvertimeHdHourTransfer> maxTime,
			List<OvertimeHdHourTransfer> timeAfterReflectApp) {

		//代休設定を取得する
		Optional<SubHolTransferSet> subSet = GetSubHolOccurrenceSetting.process(require, cid, workTimeCode, originAtr);
		if(!subSet.isPresent()) return timeAfterReflectApp;
		
		// 振替可能時間を算出する
		int tranferableTime = timeAfterReflectApp.stream().collect(Collectors.summingInt(x -> x.getTime().v()));
		
		tranferableTime = subSet.get().getTransferTime(new AttendanceTime(tranferableTime)).v();

		// 時間帯から振替時間を振り替える
		TransferResultAllFrame transferTimeResult = processTransferFromTransTimeZone(new AttendanceTime(tranferableTime), maxTimeZone,
				maxTime, timeAfterReflectApp);

		// 時間帯から時間を振り替える
		TransferResultAllFrame transferTimeAfterResult = processTransferFromTimeZone(new AttendanceTime(transferTimeResult.getTimeRemain().v()),
				maxTimeZone, maxTime, transferTimeResult.getTimeAfterTransfer());

		// 未割当の時間を算出
		int unallocatedTime = transferTimeAfterResult.getTimeAfterTransfer().stream()
				.collect(Collectors.summingInt(x -> x.getTime().v()));

		if (unallocatedTime <= 0) {
			return transferTimeAfterResult.getTimeAfterTransfer();
		}

		transferTimeResult.getTimeAfterTransfer().sort((x, y) -> {
			val maxTimeX = maxTimeZone.getTimeSpan().stream().filter(z -> x.getNo() == z.getLeft().v()).findFirst();
			val maxTimeY = maxTimeZone.getTimeSpan().stream().filter(z -> y.getNo() == z.getLeft().v()).findFirst();
			return maxTimeX.map(z -> maxTimeZone.getTimeSpan().indexOf(maxTimeX.get())).orElse(Integer.MAX_VALUE)
					- maxTimeY.map(z -> maxTimeZone.getTimeSpan().indexOf(maxTimeY.get())).orElse(Integer.MAX_VALUE);
		});
		// 時間から振り替える
		return processTimeToTimeTransferAll(transferTimeAfterResult.getTimeRemain().v(), transferTimeResult.getTimeAfterTransfer());
	}

	// 時間帯から振替時間を振り替える
	private static TransferResultAllFrame processTransferFromTransTimeZone(AttendanceTime tranferableTime, MaximumTimeZone maxTimeZone,
			List<OvertimeHdHourTransfer> maxTime, List<OvertimeHdHourTransfer> timeAfterReflectApp) {

		// input. 最大時間帯（List）でループ
		AtomicInteger index = new AtomicInteger(-1);
		for (Pair<OverTimeFrameNo, TimeSpanForDailyCalc> maxTimeZoneItem : maxTimeZone.getTimeSpan()) {
			index.getAndIncrement();
			// 振替可能時間をチェック
			if (tranferableTime.v() <= 0)

				return new TransferResultAllFrame(tranferableTime, timeAfterReflectApp);
			// 処理中の最大時間帯に該当する枠NOの振替時間を取得
			OvertimeHdHourTransfer maxTimeItem = maxTime.stream().filter(x -> x.getNo() == index.get()).findFirst()
					.orElse(null);
			if (maxTimeItem == null || maxTimeItem.getTransferTime().v() <= 0)
				continue;

			// [input.振替をした後の時間（List）]をチェック
			OvertimeHdHourTransfer timeAfterReflectAppItem = timeAfterReflectApp.stream()
					.filter(x -> x.getNo() == maxTimeZoneItem.getKey().v()).findFirst().orElse(null);
			if (timeAfterReflectAppItem == null || timeAfterReflectAppItem.getTime().v() <= 0)
				continue;

			// 時間の振替
			TransferResultFrame tranferDetail = processTimeZoneDetail(tranferableTime.v(),
					maxTimeItem.getTransferTime().v(), timeAfterReflectAppItem);
			tranferableTime = tranferDetail.getTime();
		}

		return new TransferResultAllFrame(tranferableTime, timeAfterReflectApp);
	}

	// 時間帯から時間を振り替える
	private static TransferResultAllFrame  processTransferFromTimeZone(AttendanceTime tranferableTime, MaximumTimeZone maxTimeZone,
			List<OvertimeHdHourTransfer> maxTime, List<OvertimeHdHourTransfer> timeAfterReflectApp) {

		AtomicInteger index = new AtomicInteger(-1);
		// input. 最大時間帯（List）でループ
		for (Pair<OverTimeFrameNo, TimeSpanForDailyCalc> maxTimeZoneItem : maxTimeZone.getTimeSpan()) {
			index.getAndIncrement();
			// 振替可能時間をチェック
			if (tranferableTime.v() <= 0)

				return new TransferResultAllFrame(tranferableTime, timeAfterReflectApp);
			// 処理中の最大時間帯に該当する枠NOの振替時間を取得
			OvertimeHdHourTransfer maxTimeItem = maxTime.stream().filter(x -> x.getNo() ==  index.get()).findFirst()
					.orElse(null);
			if (maxTimeItem == null || maxTimeItem.getTime().v() <= 0)
				continue;

			// [input.振替をした後の時間（List）]をチェック
			OvertimeHdHourTransfer timeAfterReflectAppItem = timeAfterReflectApp.stream()
					.filter(x -> x.getNo() == maxTimeZoneItem.getKey().v()).findFirst().orElse(null);
			if (timeAfterReflectAppItem == null || timeAfterReflectAppItem.getTime().v() <= 0)
				continue;

			// 時間の振替
			TransferResultFrame tranferDetail = processTimeZoneDetail(tranferableTime.v(),
					maxTimeItem.getTransferTime().v(), timeAfterReflectAppItem);
			tranferableTime = tranferDetail.getTime();
		}

		return new TransferResultAllFrame(tranferableTime, timeAfterReflectApp);
	}

	// 時間帯から時間の振替
	private static TransferResultFrame processTimeZoneDetail(int tranferableTime, int maxTime,
			OvertimeHdHourTransfer timeAfterReflectApp) {

		// [input.振替可能時間] と [input.最大時間]を比較

		int transferredTime = 0;
		if (tranferableTime <= maxTime) {
			// [時間外労働時間.時間]と[振替可能時間]を比較
			if (timeAfterReflectApp.getTime().v() <= tranferableTime) {

				/// [時間外労働時間.時間]を振替時間に加算
				timeAfterReflectApp.setTransferTime(new AttendanceTime(
						timeAfterReflectApp.getTransferTime().v() + timeAfterReflectApp.getTime().valueAsMinutes()));
				transferredTime = timeAfterReflectApp.getTime().valueAsMinutes();
			} else {

				/// [振替可能時間]を振替時間に加算
				timeAfterReflectApp.setTransferTime(
						new AttendanceTime(timeAfterReflectApp.getTransferTime().v() + tranferableTime));
				transferredTime = tranferableTime;
			}
		} else {
			// [時間外労働時間.時間]と[最大時間]を比較
			if (timeAfterReflectApp.getTime().v() <= maxTime) {

				/// [時間外労働時間.時間]を振替時間に加算
				timeAfterReflectApp.setTransferTime(new AttendanceTime(
						timeAfterReflectApp.getTransferTime().v() + timeAfterReflectApp.getTime().valueAsMinutes()));
				transferredTime = timeAfterReflectApp.getTime().valueAsMinutes();
			} else {

				/// [最大時間]を振替時間に加算
				timeAfterReflectApp
						.setTransferTime(new AttendanceTime(timeAfterReflectApp.getTransferTime().v() + maxTime));
				transferredTime = maxTime;
			}

		}

		// 振り替えた時間を[時間外労働時間.時間]から減算する
		timeAfterReflectApp.setTime(new AttendanceTime(timeAfterReflectApp.getTime().v() - transferredTime));

		// 振り替えた時間を振替可能時間から減算する
		Integer remainTransfer = tranferableTime - transferredTime;

		return new TransferResultFrame(new AttendanceTime(remainTransfer), timeAfterReflectApp);
	}

	/**
	 *
	 * 時間から振り替える
	 */
	private static List<OvertimeHdHourTransfer> processTimeToTimeTransferAll(int tranferableTime,
			List<OvertimeHdHourTransfer> timeAfterReflectApp) {

		// input.振替をした後の時間（List）でループ
		for (OvertimeHdHourTransfer detail : timeAfterReflectApp) {
			// 振替可能時間をチェック
			if (tranferableTime <= 0) {
				return timeAfterReflectApp;
			}

			// 時間から振替時間を割り振る
			val result = processTimeToTimeTransfer(tranferableTime, detail);
			tranferableTime = result.getTime().v();
		}

		return timeAfterReflectApp;
	}

	/**
	 *
	 * 時間から時間の振替
	 */
	private static TransferResultFrame processTimeToTimeTransfer(int tranferableTime,
			OvertimeHdHourTransfer timeReflectApp) {

		int transferredTime = 0;
		// [時間外労働時間.時間]と[振替可能時間]を比較
		if (timeReflectApp.getTime().v() <= tranferableTime) {
			// [時間外労働時間.時間]を振替時間に加算
			timeReflectApp.setTransferTime(
					new AttendanceTime(timeReflectApp.getTransferTime().v() + timeReflectApp.getTime().v()));
			transferredTime = timeReflectApp.getTime().v();
		} else {
			// [振替可能時間]を振替時間に加算
			timeReflectApp.setTransferTime(new AttendanceTime(timeReflectApp.getTransferTime().v() + tranferableTime));
			transferredTime = tranferableTime;
		}

		// 振り替えた時間を[時間外労働時間.時間]から減算する
		timeReflectApp.setTime(new AttendanceTime(timeReflectApp.getTime().v() - transferredTime));

		tranferableTime -= transferredTime;
		// 振り替えた時間を振替可能時間から減算する

		return new TransferResultFrame(new AttendanceTime(tranferableTime), timeReflectApp);
	}
	
	public static interface Require extends GetSubHolOccurrenceSetting.Require{
		
	}
}
