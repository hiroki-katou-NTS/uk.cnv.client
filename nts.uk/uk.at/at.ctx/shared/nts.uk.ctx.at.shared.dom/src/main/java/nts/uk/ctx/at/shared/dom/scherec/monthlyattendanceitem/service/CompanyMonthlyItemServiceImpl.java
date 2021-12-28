package nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItem;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemAtr;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AtItemNameAdapter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemAuthority;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
//import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.TypeOfItemImport;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.DisplayAndInputMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.MonthlyItemControlByAuthRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.MonthlyItemControlByAuthority;

@Stateless
public class CompanyMonthlyItemServiceImpl implements CompanyMonthlyItemService {

	@Inject
	private MonthlyItemControlByAuthRepository monthlyItemControlByAuthRepository;

	@Inject
	private MonthlyAttendanceItemRepository monthlyAttendanceItemRepository;

	@Inject
	private AtItemNameAdapter atItemNameAdapter;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	// to ver7
	public List<AttItemName> getMonthlyItems(String cid, Optional<String> authorityId, List<Integer> attendanceItemIds,
			List<MonthlyAttendanceItemAtr> itemAtrs) {
		attendanceItemIds = attendanceItemIds == null ? Collections.emptyList() : attendanceItemIds;
		itemAtrs = itemAtrs == null ? Collections.emptyList() : itemAtrs;
		List<Integer> monthlyAttendanceItemIds = new ArrayList<>();
		Map<Integer, AttItemAuthority> authorityMap = new HashMap<Integer, AttItemAuthority>();
		// 	パラメータ「ロールID」をチェックする (Check the parameter "Roll ID")
		if (authorityId.isPresent()) {
			// 	ドメインモデル「権限別月次項目制御」を取得する
			Optional<MonthlyItemControlByAuthority> itemAuthority = monthlyItemControlByAuthRepository
					.getMonthlyAttdItemByAttItemId(cid, authorityId.get(), attendanceItemIds);
			if (itemAuthority.isPresent()) {
				monthlyAttendanceItemIds = itemAuthority.get().getListDisplayAndInputMonthly().stream()
						.map(x -> x.getItemMonthlyId()).collect(Collectors.toList());
				for (DisplayAndInputMonthly item : itemAuthority.get().getListDisplayAndInputMonthly()) {
					AttItemAuthority auth = new AttItemAuthority();
					auth.setToUse(item.isToUse());
					auth.setYouCanChangeIt(item.getInputControlMonthly().isYouCanChangeIt());
					auth.setCanBeChangedByOthers(item.getInputControlMonthly().isCanBeChangedByOthers());
					authorityMap.put(item.getItemMonthlyId(), auth);
				}
			}
		} else {
			monthlyAttendanceItemIds = attendanceItemIds;
		}
		// 	ドメインモデル「月次の勤怠項目」を取得する
		List<MonthlyAttendanceItem> monthlyItem = monthlyAttendanceItemRepository.findByAttendanceItemIdAndAtr(cid,
				monthlyAttendanceItemIds, itemAtrs.stream().map(x -> x.value).collect(Collectors.toList()));
		// 	取得した勤怠項目の件数をチェックする
		if (monthlyItem.isEmpty()) {
			return Collections.emptyList();
		}
		// 	勤怠項目に対応する名称を生成する
		// to ver7
		List<AttItemName> monthlyAttItem = atItemNameAdapter.getNameOfMonthlyAttendanceItem(monthlyItem);
		for (AttItemName att : monthlyAttItem) {
			int id = att.getAttendanceItemId();
			if (authorityMap.containsKey(id)) {
				att.setAuthority(authorityMap.get(id));
			}
		}
		return monthlyAttItem;
	}

	@Override
	public List<AttItemName> getMonthlyItemsNew(String cid, Optional<String> authorityId) {
		List<AttItemName> listAttItemName = new ArrayList<>();
		if (authorityId.isPresent()) {
			Optional<MonthlyItemControlByAuthority> itemAuthority = monthlyItemControlByAuthRepository
					.getMonthlyAttdItem(cid, authorityId.get());
			if (!itemAuthority.isPresent()) {
				return Collections.emptyList();
			}
			for(DisplayAndInputMonthly displayAndInputMonthly : itemAuthority.get().getListDisplayAndInputMonthly()) {
				AttItemName attItemName = new AttItemName();
				attItemName.setAttendanceItemId(displayAndInputMonthly.getItemMonthlyId());
				AttItemAuthority auth = new AttItemAuthority();
				auth.setToUse(displayAndInputMonthly.isToUse());
				auth.setYouCanChangeIt(displayAndInputMonthly.getInputControlMonthly().isYouCanChangeIt());
				auth.setCanBeChangedByOthers(displayAndInputMonthly.getInputControlMonthly().isCanBeChangedByOthers());
				attItemName.setAuthority(auth);
				
				listAttItemName.add(attItemName);
			}
			return listAttItemName;

		}
		return Collections.emptyList();
	}

}
