package nts.uk.file.com.app.newlayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import nts.gul.collection.CollectionUtil;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterCellStyle;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.MasterListData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;

@Stateless
@DomainID(value = "NewLayout")
public class NewLayoutExportImpl implements MasterListData{
	
	public static String value1= "value1";
	public static String value2= "value2";
	
	@Inject
	private NewLayoutExportRepository newLayoutExportRepository;

	@Override
	public List<MasterData> getMasterDatas(MasterListExportQuery query) {
		
		String companyId = AppContexts.user().companyId();
		String contractCode = AppContexts.user().contractCode();

		
		List<MasterData> datas = new ArrayList<>();
		List<NewLayoutExportData> listNewLayout = newLayoutExportRepository.getAllMaintenanceLayout(companyId, contractCode);

		if (CollectionUtil.isEmpty(listNewLayout)) {
			return null;
		} else {
			for (int i = 0; i < listNewLayout.size(); i++) {
				Map<String, Object> data = new HashMap<>();
				putEmptyData(data);
				String cateName = listNewLayout.get(i).getCategoryName();
				String itemName = listNewLayout.get(i).getItemName();

				if (cateName == null) {
					data.put(value1, "----------");
					data.put(value2, "----------");
				} else {
					if (i == 0) {
						data.put(value1, cateName);
						data.put(value2, itemName);
					} else {
						if (listNewLayout.get(i).getCategoryName().equals(listNewLayout.get(i - 1).getCategoryName())) {
							data.put(value1, "");
							data.put(value2, itemName);
						} else {
							data.put(value1, cateName);
							data.put(value2, itemName);
						}
					}
				}

				MasterData masterData = new MasterData(data, null, "");
				masterData.cellAt(value1).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
				masterData.cellAt(value2).setStyle(MasterCellStyle.build().horizontalAlign(ColumnTextAlign.LEFT));
				datas.add(masterData);
			}
		}
		return datas;
	}
	
	@Override
	public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
		
		List<MasterHeaderColumn> columns = new ArrayList<>();
		
		columns.add(new MasterHeaderColumn(value1, TextResource.localize("CPS007_22"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(value2, TextResource.localize("CPS007_23"),
				ColumnTextAlign.LEFT, "", true));
		
		return columns;
	}
	
	private void putEmptyData (Map<String, Object> data){
		data.put(value1, "");
		data.put(value2, "");
	}
	
	@Override
	public String mainSheetName() {
		return TextResource.localize("CPS007_25");
	}
}