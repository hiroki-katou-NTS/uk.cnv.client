package nts.uk.file.at.infra.vacation.set.specialleave;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.LeaveCountedAsWorkDaysType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCount;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCountRepository;
import nts.uk.file.at.app.export.vacation.set.EmployeeSystemImpl;
import nts.uk.file.at.app.export.vacation.set.specialleave.SpecialLeaveSettingRepository;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;

@Stateless
public class JpaSpecialLeaveRepository implements SpecialLeaveSettingRepository {

	private static final int LEAVE_TYPE = 3;

	@Inject
	private WorkDaysNumberOnLeaveCountRepository repository;

	@Override
	public List<MasterData> getWorkDaysNumberOnLeaveCount(String cid) {
		WorkDaysNumberOnLeaveCount domain = this.repository.findByCid(cid);
		Optional<LeaveCountedAsWorkDaysType> optLeave = domain.getCountedLeaveList().stream()
				.filter(data -> data.value == LEAVE_TYPE).findAny();
		String isCounting = optLeave.isPresent() ? "管理する" : "管理しない";
		Map<String, MasterCellData> datas = new HashMap<>();
		datas.put(EmployeeSystemImpl.KMF001_166,
				MasterCellData.builder().columnId(EmployeeSystemImpl.KMF001_166)
						.value(TextResource.localize("KMF001_350"))
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		datas.put(EmployeeSystemImpl.KMF001_B01,
				MasterCellData.builder().columnId(EmployeeSystemImpl.KMF001_B01)
						.value(TextResource.localize("KMF001_351"))
						.style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		datas.put(EmployeeSystemImpl.KMF001_167, MasterCellData.builder().columnId(EmployeeSystemImpl.KMF001_167)
				.value(isCounting).style(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT)).build());
		return Arrays.asList(MasterData.builder().rowData(datas).build());
	}

}
