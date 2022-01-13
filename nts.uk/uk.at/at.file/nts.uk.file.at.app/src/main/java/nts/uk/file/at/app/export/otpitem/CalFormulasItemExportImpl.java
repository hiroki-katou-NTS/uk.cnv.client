package nts.uk.file.at.app.export.otpitem;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.MasterListData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListMode;

@Stateless
@DomainID(value = "CalFormulasItem")
public class CalFormulasItemExportImpl implements MasterListData {

	@Inject
	private CalFormulasItemRepository calFormulasItemRepository;

	@Override
	public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
		List<MasterHeaderColumn> columns = new ArrayList<>();
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_76, TextResource.localize("KMK002_76"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_77, TextResource.localize("KMK002_77"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_110, TextResource.localize("KMK002_110"),
		        ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_78, TextResource.localize("KMK002_78"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_79, TextResource.localize("KMK002_79"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_132, TextResource.localize("KMK002_132"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_80, TextResource.localize("KMK002_80"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_81, TextResource.localize("KMK002_81"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_82, TextResource.localize("KMK002_82"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_83, TextResource.localize("KMK002_83"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_84, TextResource.localize("KMK002_84"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_85, TextResource.localize("KMK002_85"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_111, TextResource.localize("KMK002_111"),
		        ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_86, TextResource.localize("KMK002_86"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_87, TextResource.localize("KMK002_87"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_112, TextResource.localize("KMK002_112"),
		        ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_170, TextResource.localize("KMK002_170"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_113, TextResource.localize("KMK002_113"),
		        ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_114, TextResource.localize("KMK002_114"),
		        ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_88, TextResource.localize("KMK002_88"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_89, TextResource.localize("KMK002_89"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_90, TextResource.localize("KMK002_90"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_91, TextResource.localize("KMK002_91"),
				ColumnTextAlign.LEFT, "", true));
		//update v1.1
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_97, TextResource.localize("KMK002_97"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_92, TextResource.localize("KMK002_92"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_93, TextResource.localize("KMK002_93"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_94, TextResource.localize("KMK002_94"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_95, TextResource.localize("KMK002_95"),
				ColumnTextAlign.LEFT, "", true));
		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_96, TextResource.localize("KMK002_96"),
				ColumnTextAlign.LEFT, "", true));
//		columns.add(new MasterHeaderColumn(CalFormulasItemColumn.KMK002_100, TextResource.localize("KMK002_100"),
//				ColumnTextAlign.LEFT, "", true));
		
		return columns;
	}

	@Override
	public List<MasterData> getMasterDatas(MasterListExportQuery query) {
		String companyId = AppContexts.user().companyId();
		List<MasterData> datas = calFormulasItemRepository.getDataTableExport(companyId, query.getLanguageId());
		return datas;
	}

	@Override
	public MasterListMode mainSheetMode(){
		return MasterListMode.NONE;
	}
}
