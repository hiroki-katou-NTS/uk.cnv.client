package nts.uk.screen.at.app.kdw006.j;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLink;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLinkRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * Query: 勤怠項目IDに対応する工数実績項目を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.App.勤怠項目IDに対応する工数実績項目を取得する
 * 
 * @author chungnt
 *
 */

@Stateless
public class LinkingManHour {

	@Inject
	private ManHourRecordAndAttendanceItemLinkRepository repo;
	
	public LinkingManHourDto get(int attendanceItemId) {
		Optional<ManHourRecordAndAttendanceItemLink> domianOpt = this.repo.get(AppContexts.user().companyId(), attendanceItemId);
		
		return domianOpt.map(m -> new LinkingManHourDto(m.getFrameNo().v(), m.getItemId(), m.getAttendanceItemId())).orElse(new LinkingManHourDto());
	}
	
}
