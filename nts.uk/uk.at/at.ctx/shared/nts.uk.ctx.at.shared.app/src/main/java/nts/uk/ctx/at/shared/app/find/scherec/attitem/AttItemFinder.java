package nts.uk.ctx.at.shared.app.find.scherec.attitem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemOutput;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.DailyAttendanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.service.CompanyDailyItemService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.service.CompanyMonthlyItemService;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemUsageAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.PerformanceAtr;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AttItemFinder {

	@Inject
	private CompanyDailyItemService companyDailyItemService;

	@Inject
	private CompanyMonthlyItemService companyMonthlyItemService;
	
	@Inject
	private OptionalItemRepository optionalRepo;

	// to ver7
	public List<AttItemName> getDailyAttItemByIdAndAtr(AttItemParam param) {
		Optional<String> authorityId = Optional.empty();
		List<Integer> attendanceItemIds = Collections.emptyList();
		List<DailyAttendanceAtr> itemAtrs = Collections.emptyList();
		if (param != null) {
			authorityId = param.getRoleId() == null ? Optional.empty() : Optional.ofNullable(param.getRoleId());
			attendanceItemIds = param.getAttendanceItemIds() == null ? Collections.emptyList()
					: param.getAttendanceItemIds();
			itemAtrs = param.getItemAtrs() == null ? Collections.emptyList()
					: param.getItemAtrs().stream().map(x -> EnumAdaptor.valueOf(x, DailyAttendanceAtr.class))
							.collect(Collectors.toList());
		}
		// to ver7
		return this.companyDailyItemService.getDailyItems(AppContexts.user().companyId(), authorityId,
				attendanceItemIds, itemAtrs);
	}
	//get by authorityId (daily) 
		public List<AttItemName> getDailyAttItemById(String authorityId) {
			String companyId = AppContexts.user().companyId();
			return this.companyDailyItemService.getDailyItemsNew(companyId, Optional.ofNullable(authorityId));
		}

	// to ver7
	public List<AttItemName> getMonthlyAttItemByIdAndAtr(AttItemParam param) {
		Optional<String> authorityId = Optional.empty();
		List<Integer> attendanceItemIds = Collections.emptyList();
		List<MonthlyAttendanceItemAtr> itemAtrs = Collections.emptyList();
		if (param != null) {
			authorityId = param.getRoleId() == null ? Optional.empty() : Optional.ofNullable(param.getRoleId());
			attendanceItemIds = param.getAttendanceItemIds() == null ? Collections.emptyList()
					: param.getAttendanceItemIds();
			itemAtrs = param.getItemAtrs() == null ? Collections.emptyList()
					: param.getItemAtrs().stream().map(x -> EnumAdaptor.valueOf(x, MonthlyAttendanceItemAtr.class))
							.collect(Collectors.toList());
		}
		// to ver7
		return this.companyMonthlyItemService.getMonthlyItems(AppContexts.user().companyId(), authorityId,
				attendanceItemIds, itemAtrs);
	}
	//get by authorityId (monthly)
	public List<AttItemName> getMonthlyAttItemById(String authorityId) {
		String companyId = AppContexts.user().companyId();
		return this.companyMonthlyItemService.getMonthlyItemsNew(companyId, Optional.ofNullable(authorityId));
	}
	
	
	public List<AttItemName> getAttItemByIdAndAtr(AttItemParam param) {
		Optional<String> authorityId = Optional.empty();
		List<Integer> attendanceItemIds = Collections.emptyList();
		List<MonthlyAttendanceItemAtr> itemAtrs = Collections.emptyList();
		if (param != null) {
			authorityId = param.getRoleId() == null ? Optional.empty() : Optional.ofNullable(param.getRoleId());
			attendanceItemIds = param.getAttendanceItemIds() == null ? Collections.emptyList()
					: param.getAttendanceItemIds();
			itemAtrs = param.getItemAtrs() == null ? Collections.emptyList()
					: param.getItemAtrs().stream().map(x -> EnumAdaptor.valueOf(x, MonthlyAttendanceItemAtr.class))
							.collect(Collectors.toList());
		}
		return this.companyMonthlyItemService.getMonthlyItems(AppContexts.user().companyId(), authorityId,
				attendanceItemIds, itemAtrs);
	}
	
	/**
	 * 利用しない任意項目を外す
	 * 
	 * @param items
	 * @param dailyFlg
	 * @return list attandance items
	 */
	public List<AttItemOutput> removeUnusedItems(List<AttItemName> items, boolean dailyFlg) {
	    String companyId = AppContexts.user().companyId();
	    List<AttItemOutput> result = new ArrayList<AttItemOutput>();
	    
	    if (items.size() > 0) {
	        items.stream().filter(item -> {
	            Integer frameNo = item.getFrameCategory();
	            OptionalItem optionalItem = null;
	            // 「勤怠項目と枠の紐付け」の枠カテゴリを判別する
	            if (frameNo != null && frameNo == 8) {
	                // ドメインモデル「任意項目」を取得する
	                optionalItem = this.optionalRepo.find(companyId, item.getFrameNo());
	                // Input．「月次の勤怠項目」から利用しない勤怠項目を消す
	                // return false not add to list result
	                if (dailyFlg && optionalItem.getPerformanceAtr().equals(PerformanceAtr.MONTHLY_PERFORMANCE)) {
	                    return false;
	                }
	                if (optionalItem.getUsageAtr().equals(OptionalItemUsageAtr.NOT_USE)) {
	                    return false;
	                }
	                
	            }
	            AttItemOutput itemOutput = new AttItemOutput(item, optionalItem == null ? null : optionalItem.getOptionalItemAtr().value);
	            result.add(itemOutput);
	            
	            return true;
	        }).collect(Collectors.toList());
	    }
	    
	    // return result list attandance items
	    return result;
	}
}
