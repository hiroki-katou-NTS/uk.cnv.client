package nts.uk.screen.at.app.kdw006.j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AtItemNameAdapter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.DailyAttendanceItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.service.CompanyDailyItemService;
import nts.uk.shr.com.context.AppContexts;

/**
 * ScreenQuery: 画面利用内容を取得する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW006_前準備.J：工数入力画面フォーマット設定.メニュー別OCD.画面利用内容を取得する
 * 
 * @author chungnt
 *
 */

@Stateless
public class GetScreenUsageDetails {

	@Inject
	private GetDisplayFormat getDisplayFormat;

	@Inject
	private DailyAttendanceItemRepository dailyAttendanceItemRepository;

	@Inject
	private AcquireManHourRecordItems acquireManHourRecordItems;

	@Inject
	private AtItemNameAdapter atItemNameAdapter;
	
	@Inject
	private CompanyDailyItemService companyDailyItemService;

	public GetScreenUsageDetailsDto get() {
		GetScreenUsageDetailsDto result = new GetScreenUsageDetailsDto();
		List<DailyAttendanceItemKDW006Dto> dailyItem = new ArrayList<>();
		
		String companyId = AppContexts.user().companyId();
		List<AttItemName> data = new ArrayList<>();

		// Get Data
		GetDisplayFormatDto getDisplayFormatDto = this.getDisplayFormat.get();
		List<DailyAttendanceItem> attendents = this.dailyAttendanceItemRepository
				.getListById(AppContexts.user().companyId(), getAttendanceId());
		List<AcquireManHourRecordItemsDto> hourRecordItemsDtos = this.acquireManHourRecordItems.get();
		List<AcquireManHourRecordItemsDto> hourRecordItemsDtoOuts = new ArrayList<>();
		
		data = companyDailyItemService.getDailyItems(companyId, Optional.empty(), getAttendanceId() ,null);

		// Mapping Dto
		result.setManHourInputDisplayFormat(getDisplayFormatDto);
		dailyItem = data.stream().map(m -> {
			return new DailyAttendanceItemKDW006Dto(m.getAttendanceItemId(), m.getAttendanceItemName(), m.getAttendanceItemDisplayNumber());
		}).collect(Collectors.toList());

		hourRecordItemsDtos.forEach(f -> {
			if (f.getUseAtr() == 1) {
				hourRecordItemsDtoOuts.add(f);
			}
		});

		// 勤怠項目に対応する名称を生成する
//		List<AttItemName> dailyAttItem = atItemNameAdapter.getNameOfDailyAttendanceItem(attendents);

//		dailyItem.stream().forEach(f -> {
//			Optional<AttItemName> exist = dailyAttItem.stream()
//					.filter(a -> a.getAttendanceItemId() == f.getAttendanceItemId()).findFirst();
//			if (exist.isPresent()) {
//				f.changeName(exist.get().getAttendanceItemName(), exist.get().getOldName());
//			}
//		});

		result.setDailyAttendanceItem(dailyItem);
		result.setManHourRecordItem(hourRecordItemsDtoOuts);

		return result;
	}

	private List<Integer> getAttendanceId() {
		Integer[] attendanceId = { 216, 221, 226, 231, 236, 241, 246, 251, 256, 261, 266, 271, 276, 281, 286, 291, 296,
				301, 306, 311, 532, 535, 559, 592, 598, 604, 610, 641, 642, 643, 644, 645, 646, 647, 648, 649, 650, 651,
				652, 653, 654, 655, 656, 657, 658, 659, 660, 661, 662, 663, 664, 665, 666, 667, 668, 669, 670, 671, 672,
				673, 674, 675, 676, 677, 678, 679, 680, 681, 682, 683, 684, 685, 686, 687, 688, 689, 690, 691, 692, 693, 
				694, 695, 696, 697, 698, 699, 700, 701, 702, 703, 704, 705, 706, 707, 708, 709, 710, 711, 712, 713, 714, 
				715, 716, 717, 718, 719, 720, 721, 722, 723, 724, 725, 726, 727, 728, 729, 730, 731, 732, 733, 734, 735, 
				736, 737, 738, 739, 740};

		List<Integer> result = Arrays.asList(attendanceId);
		return result;
	}
}
