package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.clearreflectstatusperiod;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.getfirstreflect.GetFirstReflectFromListDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.getfirstreflect.GetFirstReflectOutput;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 期間で反映状態をクリアする
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.日別実績処理.作成処理.日別作成Mgrクラス.アルゴリズム.社員の日別実績を作成する.期間で反映状態をクリアする.期間で反映状態をクリアする
 * 
 * @author tutk
 *
 */
@Stateless
public class ClearReflectStatusPeriod {

	@Inject
	private GetFirstReflectFromListDaily getFirstReflectFromListDaily;

	@Inject
	private StampDakokuRepository stampDakokuRepository;

	@Inject
	private StampCardRepository stampCardRepository;

	public void clear(List<IntegrationOfDaily> listIntegrationOfDaily) {
		
		if(listIntegrationOfDaily.isEmpty()) {
			return;
		}
		// 最初の日別実績の反映範囲を取得する
		GetFirstReflectOutput getFirstReflect = getFirstReflectFromListDaily.get(listIntegrationOfDaily, true);
		if (!getFirstReflect.getStampReflectRangeOutput().isPresent()) {
			return;
		}

		// 最後の日別実績の一覧から一個目反映範囲を取得する
		GetFirstReflectOutput getLastReflect = getFirstReflectFromListDaily.get(listIntegrationOfDaily, false);
		if (!getLastReflect.getStampReflectRangeOutput().isPresent()) {
			return;
		}

		// ドメインモデル「打刻カード」を取得する
		List<StampCard> lstStampCard = stampCardRepository
				.getListStampCard(listIntegrationOfDaily.get(0).getEmployeeId());

		if (!lstStampCard.isEmpty()) {
			int timeStart = getFirstReflect.getStampReflectRangeOutput().get().getStampRange().getStart() != null
					? getFirstReflect.getStampReflectRangeOutput().get().getStampRange().getStart().v()
					: 0;
			int timeEnd = getLastReflect.getStampReflectRangeOutput().get().getStampRange().getEnd() != null
					? getLastReflect.getStampReflectRangeOutput().get().getStampRange().getEnd().v()
					: 0;
			GeneralDateTime start = GeneralDateTime.ymdhms(getFirstReflect.getYmd().get().year(),
					getFirstReflect.getYmd().get().month(), getFirstReflect.getYmd().get().day(), 0, 0, 0)
					.addMinutes(timeStart);

			GeneralDateTime end = GeneralDateTime.ymdhms(getLastReflect.getYmd().get().year(),
					getLastReflect.getYmd().get().month(), getLastReflect.getYmd().get().day(), 0, 0, 0)
					.addMinutes(timeEnd);
			// ドメインモデル「打刻カード」を取得する (Lấy từ domain)
			List<Stamp> listStamp = stampDakokuRepository.getByDateTimeperiod(
					lstStampCard.stream().map(c -> c.getStampNumber().v()).collect(Collectors.toList()),
					start, end);

			// 打刻の反映状態をクリアする
			for (Stamp s : listStamp) {
				s.getImprintReflectionStatus().clearReflect();
				stampDakokuRepository.update(s);
			}
		}
	}
}
