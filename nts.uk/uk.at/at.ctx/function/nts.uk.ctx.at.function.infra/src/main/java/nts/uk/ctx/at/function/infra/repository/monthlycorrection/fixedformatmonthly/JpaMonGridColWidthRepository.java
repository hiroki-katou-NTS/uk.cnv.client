package nts.uk.ctx.at.function.infra.repository.monthlycorrection.fixedformatmonthly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.ColumnWidtgByMonthly;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.ColumnWidtgByMonthlyRepository;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.ColumnWidthOfDisplayItem;
import nts.uk.ctx.at.function.infra.entity.monthlycorrection.fixedformatmonthly.KrcmtMonGridColWidth;
import nts.uk.ctx.at.function.infra.entity.monthlycorrection.fixedformatmonthly.KrcmtMonGridColWidthPk;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaMonGridColWidthRepository extends JpaRepository implements ColumnWidtgByMonthlyRepository {

	private static final String Get_COL_WIDTH_BY_CID = "SELECT c FROM KrcmtMonGridColWidth c "
			+ " WHERE c.monGridColWidthPk.cid = :companyID";

	private static final String Get_COL_WIDTH_BY_CID_AND_ATTDID = "SELECT c FROM KrcmtMonGridColWidth c "
			+ " WHERE c.monGridColWidthPk.cid = :companyID "
			+ " AND c.monGridColWidthPk.attendanceItemId IN :attendanceItemIds";

	@Override
	public Optional<ColumnWidtgByMonthly> getColumnWidtgByMonthly(String companyID) {
		List<KrcmtMonGridColWidth> lstData = this.queryProxy().query(Get_COL_WIDTH_BY_CID, KrcmtMonGridColWidth.class)
				.setParameter("companyID", companyID).getList();

		if (!lstData.isEmpty()) {
			List<ColumnWidthOfDisplayItem> listColumnWidthOfDisplayItem = lstData.stream()
					.map(row -> new ColumnWidthOfDisplayItem(row.monGridColWidthPk.attendanceItemId, row.columnWidth))
					.collect(Collectors.toList());

			return Optional.of(new ColumnWidtgByMonthly(companyID, listColumnWidthOfDisplayItem));

		}
		return Optional.empty();
	}

	@Override
	public void addColumnWidtgByMonthly(ColumnWidtgByMonthly columnWidtgByMonthly) {
	}

	@Override
	public void updateColumnWidtgByMonthly(Map<Integer, Integer> lstHeader) {
		if (!lstHeader.keySet().isEmpty()) {
			List<KrcmtMonGridColWidth> entity = new ArrayList<>();
			
			CollectionUtil.split(lstHeader, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subMap -> {
				entity.addAll(this.queryProxy()
					.query(Get_COL_WIDTH_BY_CID_AND_ATTDID, KrcmtMonGridColWidth.class)
					.setParameter("companyID", AppContexts.user().companyId())
					.setParameter("attendanceItemIds", subMap.keySet())
					.getList());
			});

			lstHeader.keySet().stream().forEach(id -> {
				Optional<KrcmtMonGridColWidth> flag = entity.stream()
						.filter(item -> item.monGridColWidthPk.attendanceItemId == id).findFirst();

				if (flag.isPresent()) {
					flag.get().columnWidth = lstHeader.get(id);
					this.commandProxy().update(flag.get());
				} else {
					KrcmtMonGridColWidth value = new KrcmtMonGridColWidth(
							new KrcmtMonGridColWidthPk(AppContexts.user().companyId(), id), lstHeader.get(id));
					this.commandProxy().insert(value);
				}
			});

//			if (entity.isEmpty()) {
//				List<KrcmtMonGridColWidth> entities = lstHeader.keySet().stream().map(item -> {
//					return new KrcmtMonGridColWidth(new KrcmtMonGridColWidthPk(AppContexts.user().companyId(), item),
//							lstHeader.get(item));
//				}).collect(Collectors.toList());
//
//				this.commandProxy().insertAll(entities);
//			} else {
//				for (KrcmtMonGridColWidth item : entity) {
//					item.columnWidth = lstHeader.get(item.monGridColWidthPk.attendanceItemId);
//				}
//				this.commandProxy().updateAll(entity);
//			}
		}
	}

	@Override
	public void deleteColumnWidtgByMonthly(String companyID) {
		// TODO Auto-generated method stub

	}

}
