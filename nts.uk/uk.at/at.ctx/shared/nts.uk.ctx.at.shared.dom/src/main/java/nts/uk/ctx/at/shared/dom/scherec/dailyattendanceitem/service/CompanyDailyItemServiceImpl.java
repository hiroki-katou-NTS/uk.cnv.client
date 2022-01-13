package nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItemAuthority;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DisplayAndInputControl;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AtItemNameAdapter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemAuthority;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
//import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.TypeOfItemImport;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.DailyAttendanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.DailyAttdItemAuthRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.DailyAttendanceItemRepository;
import nts.uk.shr.com.context.AppContexts;
//import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.DisplayAndInputMonthly;
//import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.MonthlyItemControlByAuthority;

@Stateless
public class CompanyDailyItemServiceImpl implements CompanyDailyItemService {

	@Inject
	private DailyAttdItemAuthRepository dailyAttdItemAuthRepository;

	@Inject
	private DailyAttendanceItemRepository dailyAttendanceItemRepository;

	@Inject
	private AtItemNameAdapter atItemNameAdapter;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<AttItemName> getDailyItems(String cid, Optional<String> authorityId,
			List<Integer> attendanceItemIds, List<DailyAttendanceAtr> itemAtrs) {
		attendanceItemIds = attendanceItemIds == null ? Collections.emptyList() : attendanceItemIds;
		itemAtrs = itemAtrs == null ? Collections.emptyList() : itemAtrs;
		List<Integer> dailyAttendanceItemIds = new ArrayList<>();
		Map<Integer, AttItemAuthority> authorityMap = new HashMap<Integer, AttItemAuthority>();
		//	パラメータ「ロールID」をチェックする (Check the parameter "Roll ID")
		if (authorityId.isPresent()) {
			//	ドメインモデル「権限別日次項目制御」を取得する
			Optional<DailyAttendanceItemAuthority> itemAuthority = dailyAttdItemAuthRepository
					.getDailyAttdItemByAttItemId(cid, authorityId.get(), attendanceItemIds);
			if (itemAuthority.isPresent()) {
				dailyAttendanceItemIds = itemAuthority.get().getListDisplayAndInputControl().stream()
						.map(x -> x.getItemDailyID()).collect(Collectors.toList());
				for (DisplayAndInputControl item : itemAuthority.get().getListDisplayAndInputControl()) {
					AttItemAuthority auth = new AttItemAuthority();
					auth.setToUse(item.isToUse());
					auth.setYouCanChangeIt(item.getInputControl().isYouCanChangeIt());
					auth.setCanBeChangedByOthers(item.getInputControl().isCanBeChangedByOthers());
					authorityMap.put(item.getItemDailyID(), auth);
				}
			}
		} else {
			dailyAttendanceItemIds = attendanceItemIds;
		}
		//	ドメインモデル「日次の勤怠項目」を取得する
		List<DailyAttendanceItem> dailyItem = dailyAttendanceItemRepository.findByAttendanceItemIdAndAtr(cid,
				dailyAttendanceItemIds, itemAtrs.stream().map(x -> x.value).collect(Collectors.toList()));
		// 	取得した勤怠項目の件数をチェックする
		if (dailyItem.isEmpty()) {
			return Collections.emptyList();
		}
		// 	勤怠項目に対応する名称を生成する
		// to ver7
		List<AttItemName> dailyAttItem = atItemNameAdapter.getNameOfDailyAttendanceItem(dailyItem);
		for (AttItemName att : dailyAttItem) {
			int id = att.getAttendanceItemId();
			if (authorityMap.containsKey(id)) {
				att.setAuthority(authorityMap.get(id));
			}
		}
		return dailyAttItem;
	}

	@Override
	public List<AttItemName> getDailyItemsNew(String cid, Optional<String> authorityId) {
		List<AttItemName> listAttItemName = new ArrayList<>();
		if (authorityId.isPresent()) {
			Optional<DailyAttendanceItemAuthority> itemAuthority = dailyAttdItemAuthRepository
					.getDailyAttdItem(cid, authorityId.get());
			if (!itemAuthority.isPresent()) {
				return Collections.emptyList();
			}
			for(DisplayAndInputControl displayAndInputDaily : itemAuthority.get().getListDisplayAndInputControl()) {
				AttItemName attItemName = new AttItemName();
				attItemName.setAttendanceItemId(displayAndInputDaily.getItemDailyID());
				AttItemAuthority auth = new AttItemAuthority();
				auth.setToUse(displayAndInputDaily.isToUse());
				auth.setYouCanChangeIt(displayAndInputDaily.getInputControl().isYouCanChangeIt());
				auth.setCanBeChangedByOthers(displayAndInputDaily.getInputControl().isCanBeChangedByOthers());
				attItemName.setAuthority(auth);
				
				listAttItemName.add(attItemName);
			}
			return listAttItemName;

		}
		return Collections.emptyList();
	}

	@Override
	public List<DailyItemDto> findByAttendanceItems(String companyId, List<Integer> attendanceItems) {
		
		String authorityId = AppContexts.user().roles().forAttendance();
		
		// 日次の勤怠項目を取得する Nhận daily Attendance items
		List<DailyAttendanceItem> dailyAttendanceItems = this.dailyAttendanceItemRepository
				.findByADailyAttendanceItems(attendanceItems, companyId);
		
		// 0件の場合
		if (dailyAttendanceItems.isEmpty()) {
			// 終了状態：取得失敗 (Trạng thái kết thúc : Acquisition failure)
			return new ArrayList<>();
		}
		
		// アルゴリズム「会社の日次を取得する」を実行する.collect(Collectors.toList());
		List<AttItemName> attItemNames = this.getDailyItems(companyId, Optional.of(authorityId), attendanceItems, null);

		// 取得したドメインモデル「日次の勤怠項目」（日次勤怠項目の属性、日次の勤怠項目に関連するマスタの種類、表示番号）と取得したList＜勤怠項目ID、名称＞を結合する
		List<DailyItemDto> result = dailyAttendanceItems.stream()
								.map(t -> {
									Optional<AttItemName> attItemName = attItemNames.stream()
											.filter(item -> item.getAttendanceItemId() == t.getAttendanceItemId()).findFirst();
									return attItemName.isPresent() 
											? DailyItemDto.builder()
													.displayNumber(t.getDisplayNumber())
													.masterType(t.getMasterType().map(r -> r.value).orElse(null))
													.attribute(t.getDailyAttendanceAtr().value)
													.timeId(attItemName.map(at -> at.getAttendanceItemId()).orElse(null))
													.name(attItemName.map(at -> at.getAttendanceItemName()).orElse(null))
													.build()
											: null;
								})
								.filter(Objects::nonNull)
								.collect(Collectors.toList());

		// List＜勤怠項目ID、名称、属性、マスタの種類。表示番号＞を渡す Trả về List <...>
		return result;
	}

}
