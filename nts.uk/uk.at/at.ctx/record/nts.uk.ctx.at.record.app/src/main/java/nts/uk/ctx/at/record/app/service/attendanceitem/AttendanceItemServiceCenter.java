package nts.uk.ctx.at.record.app.service.attendanceitem;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.shared.dom.attendance.util.AttendanceItemUtilRes;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemService;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.PerformanceAtr;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AttendanceItemServiceCenter implements AttendanceItemService {
	
	@Inject
	private OptionalItemRepository optionalMaster;

	@Override
	public List<ItemValue> getItemBy(AttendanceItemType type, ValueType... types) {
		List<ValueType> limitted = Arrays.asList(types);
		
		if(type == AttendanceItemType.MONTHLY_ITEM){
			return limitItemBy(type, AttendanceItemUtilRes.collect(new MonthlyRecordWorkDto(), AttendanceItemType.MONTHLY_ITEM), limitted);
		}
		
		return limitItemBy(type, AttendanceItemUtilRes.collect(new DailyRecordDto(), AttendanceItemType.DAILY_ITEM), limitted);
	}

	private Map<Integer, OptionalItem> getOptionalMaster(AttendanceItemType type) {
		return optionalMaster.findByPerformanceAtr(AppContexts.user().companyId(), 
				type == AttendanceItemType.MONTHLY_ITEM ? PerformanceAtr.MONTHLY_PERFORMANCE : PerformanceAtr.DAILY_PERFORMANCE)
				.stream().collect(Collectors.toMap(c -> c.getOptionalItemNo().v(), c -> c));
	}

	@Override
	public List<ItemValue> getTimeAndCountItem(AttendanceItemType type) {
		return getItemBy(type, ValueType.TIME, ValueType.COUNT, ValueType.COUNT_WITH_DECIMAL);
	}
	
	private List<ItemValue> limitItemBy(AttendanceItemType type, List<ItemValue> source, List<ValueType> constraints){
		Map<Integer, OptionalItem> optionalMaster = getOptionalMaster(type);
		Map<Integer, Integer> optionalItems = AttendanceItemIdContainer.mapOptionalItemIdsToNos(type);
		
		return source.stream().filter(c -> {
			if(!constraints.contains(c.type())){
				return false;
			}
			if(AttendanceItemIdContainer.isOptionalItem(c)){
				Integer itemNo = optionalItems.get(c.itemId());
				OptionalItem optionalItem = optionalMaster.get(itemNo);
				if(optionalItem == null){
					return false;
				}
				if(optionalItem.getOptionalItemAtr() == OptionalItemAtr.NUMBER){
					c.valueType(ValueType.COUNT_WITH_DECIMAL);
				}
			}
			
			
			return true;
		}).collect(Collectors.toList());
	}

}
