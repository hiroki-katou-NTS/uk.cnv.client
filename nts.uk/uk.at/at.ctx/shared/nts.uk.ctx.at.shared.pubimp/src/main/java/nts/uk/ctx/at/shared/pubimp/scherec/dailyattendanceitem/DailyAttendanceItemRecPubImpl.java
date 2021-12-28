package nts.uk.ctx.at.shared.pubimp.scherec.dailyattendanceitem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.repository.DailyAttendanceItemRepository;
import nts.uk.ctx.at.shared.pub.scherec.dailyattendanceitem.DailyAttendanceItemRecPub;
import nts.uk.ctx.at.shared.pub.scherec.dailyattendanceitem.DailyAttendanceItemRecPubExport;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DailyAttendanceItemRecPubImpl implements DailyAttendanceItemRecPub {

	@Inject
	private DailyAttendanceItemRepository dailyAttendanceItemRepository;

	@Override
	public List<DailyAttendanceItemRecPubExport> getDailyAttendanceItem(String companyId,
			List<Integer> dailyAttendanceItemIds) {
		List<DailyAttendanceItem> dailyAttendanceItemList = this.dailyAttendanceItemRepository.getListById(companyId,
				dailyAttendanceItemIds);

		List<DailyAttendanceItemRecPubExport> attendanceItemPubDtos = dailyAttendanceItemList.stream().map(f -> {
			return new DailyAttendanceItemRecPubExport(f.getCompanyId(), f.getAttendanceItemId(),
					f.getAttendanceName().v(), f.getDisplayNumber(), f.getUserCanUpdateAtr().value,
					f.getDailyAttendanceAtr().value, f.getNameLineFeedPosition(), f.getDisplayName().isPresent() ? f.getDisplayName().get().v() : "");
		}).collect(Collectors.toList());

		return attendanceItemPubDtos;
	}

	@Override
	public List<DailyAttendanceItemRecPubExport> getDailyAttendanceItemList(String companyId) {
		List<DailyAttendanceItem> attendanceItems = this.dailyAttendanceItemRepository.getList(companyId);
		
		List<DailyAttendanceItemRecPubExport> dailyAttendanceItemRecPubDtos = attendanceItems.stream().map(f -> {
			return new DailyAttendanceItemRecPubExport(f.getCompanyId(), f.getAttendanceItemId(), f.getAttendanceName().v(), f.getDisplayNumber(), f.getUserCanUpdateAtr().value, f.getDailyAttendanceAtr().value, f.getNameLineFeedPosition(), f.getDisplayName().isPresent() ? f.getDisplayName().get().v() : "");
		}).collect(Collectors.toList());
		return dailyAttendanceItemRecPubDtos;
	}

	@Override
	public List<DailyAttendanceItemRecPubExport> getDailyAttendanceItemList(String companyId, List<Integer> dailyAttendanceAtrs) {
		if (CollectionUtil.isEmpty(dailyAttendanceAtrs)) {
			return Collections.emptyList();
		}
		
		return this.dailyAttendanceItemRepository.findByAtr(companyId, dailyAttendanceAtrs)
				.stream().map(x -> DailyAttendanceItemRecPubExport.builder()
							.attendanceItemId(x.getAttendanceItemId())
							.attendanceName(x.getAttendanceName().v())
							.dailyAttendanceAtr(x.getDailyAttendanceAtr().value)
							.displayNumber(x.getDisplayNumber())
							.nameLineFeedPosition(x.getNameLineFeedPosition())
							.userCanUpdateAtr(x.getUserCanUpdateAtr().value)
							.companyId(companyId).build()).collect(Collectors.toList());
	}
}
