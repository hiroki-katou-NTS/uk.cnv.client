package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.vacationdetail;

import java.util.stream.Collectors;

import lombok.val;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SeqVacationAssociationInfoList;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.VacationDetails;

/**
 * @author thanh_nx
 *
 *         休出振出管理データを補正する
 */
public class CorrectDaikyuFurikyuFixed {

	//補正する
	public static void correct(VacationDetails vacationDetail, SeqVacationAssociationInfoList seqVacInfoList) {
		
		//$確定データ一覧
		vacationDetail.getOccrFixed().forEach(x -> {
			//$紐付け一覧 
			val linkData = seqVacInfoList.getWithOccrDay(x.getDateOccur().getDayoffDate().get());
			if(!linkData.isEmpty()) {
				//$代休一覧
				val daikyuLinkDetail = linkData.stream()
						.filter(data -> vacationDetail.getDigestTempWithDate(data.getDateOfUse()).isPresent())
						.collect(Collectors.toList());
				//$使用日数合
				double sumUsedDay = daikyuLinkDetail.stream().mapToDouble(y -> y.getDayNumberUsed().v()).sum();
				//$未相殺数
				x.updateUsedDay(sumUsedDay);
			}
		});
	
	}
}
