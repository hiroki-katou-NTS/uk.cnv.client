package nts.uk.file.at.app.export.regisagreetime;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.MasterListData;
import nts.uk.shr.infra.file.report.masterlist.data.SheetData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;

@Stateless
@DomainID("RegisterTime")
public class RegisteTimeImpl implements MasterListData {
	
	@Inject
	private RegistTimeRepository registTimeRepository;
	
	/**
	 *  Sheet1
	 */
	@Override
	public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
		 List <MasterHeaderColumn> columns = new ArrayList<>();

	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_80, TextResource.localize("KMK008_80"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.HEADER_NONE1,"",
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.HEADER_NONE2, "",
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_81, TextResource.localize("KMK008_81"),
	                ColumnTextAlign.LEFT, "", true));
	        return columns;
	}
	
	
	@Override
	public List<MasterData> getMasterDatas(MasterListExportQuery query) {
		return registTimeRepository.getDataExportSheet1();
	}
	
	@Override
	public String mainSheetName() {
		return TextResource.localize("KMK008_70");
	}
	
	
	/**
	 *  Sheet 2
	 * @return
	 */
	public List<MasterHeaderColumn> getHeaderColumnsSheet2() {
		 List <MasterHeaderColumn> columns = new ArrayList<>();

	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_89, TextResource.localize("KMK008_89"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_90,TextResource.localize("KMK008_90"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_91, TextResource.localize("KMK008_91"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_92, TextResource.localize("KMK008_92"),
	                ColumnTextAlign.LEFT, "", true));
	        return columns;
	}
	
	public List<MasterData> getMasterDatasSheet2() {
		return registTimeRepository.getDataExportSheet2();
	}
	
	
	
	/**
	 *  sheet 3
	 * @return
	 */
	public List<MasterHeaderColumn> getHeaderColumnsSheet3() {
		 List <MasterHeaderColumn> columns = new ArrayList<>();

	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_100, TextResource.localize("KMK008_100"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_101,TextResource.localize("KMK008_101"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_89, TextResource.localize("KMK008_89"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_90, TextResource.localize("KMK008_90"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_91, TextResource.localize("KMK008_91"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_92, TextResource.localize("KMK008_92"),
	                ColumnTextAlign.LEFT, "", true));
	        return columns;
	}
	
	
	public List<MasterData> getMasterDatasSheet3() {
		return registTimeRepository.getDataExportSheet3();
	}
	
	
	/**
	 *  sheet 5
	 * @return
	 */
	public List<MasterHeaderColumn> getHeaderColumnsSheet5() {
		 List <MasterHeaderColumn> columns = new ArrayList<>();

	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_104, TextResource.localize("KMK008_104"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_105,TextResource.localize("KMK008_105"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_89, TextResource.localize("KMK008_89"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_90, TextResource.localize("KMK008_90"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_91, TextResource.localize("KMK008_91"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_92, TextResource.localize("KMK008_92"),
	                ColumnTextAlign.LEFT, "", true));
	        return columns;
	}
	
	public List<MasterData> getMasterDatasSheet5() {
		return registTimeRepository.getDataExportSheet5();
	}
	
	/**
	 *  Sheet 6
	 * @return
	 */
	public List<MasterHeaderColumn> getHeaderColumnsSheet6() {
		 List <MasterHeaderColumn> columns = new ArrayList<>();

	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_89, TextResource.localize("KMK008_89"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_90,TextResource.localize("KMK008_90"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_91, TextResource.localize("KMK008_91"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_92, TextResource.localize("KMK008_92"),
	                ColumnTextAlign.LEFT, "", true));
	        return columns;
	}
	
	public List<MasterData> getMasterDatasSheet6() {
		return registTimeRepository.getDataExportSheet6();
	}
	
	/**
	 *  sheet 7
	 * @return
	 */
	public List<MasterHeaderColumn> getHeaderColumnsSheet7() {
		 List <MasterHeaderColumn> columns = new ArrayList<>();

	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_100, TextResource.localize("KMK008_100"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_101,TextResource.localize("KMK008_101"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_89, TextResource.localize("KMK008_89"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_90, TextResource.localize("KMK008_90"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_91, TextResource.localize("KMK008_91"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_92, TextResource.localize("KMK008_92"),
	                ColumnTextAlign.LEFT, "", true));
	        return columns;
	}
	
	
	public List<MasterData> getMasterDatasSheet7() {
		return registTimeRepository.getDataExportSheet7();
	}
	
	/**
	 *  sheet 9
	 * @return
	 */
	public List<MasterHeaderColumn> getHeaderColumnsSheet9() {
		 List <MasterHeaderColumn> columns = new ArrayList<>();

	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_104, TextResource.localize("KMK008_104"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_105,TextResource.localize("KMK008_105"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_89, TextResource.localize("KMK008_89"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_90, TextResource.localize("KMK008_90"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_91, TextResource.localize("KMK008_91"),
	                ColumnTextAlign.LEFT, "", true));
	        columns.add(new MasterHeaderColumn(RegistTimeColumn.KMK008_92, TextResource.localize("KMK008_92"),
	                ColumnTextAlign.LEFT, "", true));
	        return columns;
	}
	
	public List<MasterData> getMasterDatasSheet9() {
		return registTimeRepository.getDataExportSheet9();
	}
	
	
	@Override
    public List<SheetData> extraSheets(MasterListExportQuery query) {
        List<SheetData> sheetDatas = new ArrayList<>();
        /**
         *  sheet 2
         */
        SheetData sheetData2 = SheetData.builder()
        		 .mainData(this.getMasterDatasSheet2())
                .mainDataColumns(this.getHeaderColumnsSheet2())
                .sheetName(TextResource.localize("KMK008_71"))
                .build();
        /**
         *  sheet 3
         */
        SheetData sheetData3 = SheetData.builder()
       		 .mainData(this.getMasterDatasSheet3())
               .mainDataColumns(this.getHeaderColumnsSheet3())
               .sheetName(TextResource.localize("KMK008_72"))
               .build();
        /**
         *  sheet 5
         */
        SheetData sheetData5 = SheetData.builder()
          		 .mainData(this.getMasterDatasSheet5())
                  .mainDataColumns(this.getHeaderColumnsSheet5())
                  .sheetName(TextResource.localize("KMK008_74"))
                  .build();
        
        /**
         *  sheet 6
         */
        SheetData sheetData6 = SheetData.builder()
          		 .mainData(this.getMasterDatasSheet6())
                  .mainDataColumns(this.getHeaderColumnsSheet6())
                  .sheetName(TextResource.localize("KMK008_75"))
                  .build();
        
        /**
         *  sheet 7
         */
        SheetData sheetData7 = SheetData.builder()
          		 .mainData(this.getMasterDatasSheet7())
                  .mainDataColumns(this.getHeaderColumnsSheet7())
                  .sheetName(TextResource.localize("KMK008_76"))
                  .build();
        
        /**
         *  sheet 9
         */
        SheetData sheetData9 = SheetData.builder()
          		 .mainData(this.getMasterDatasSheet9())
                  .mainDataColumns(this.getHeaderColumnsSheet9())
                  .sheetName(TextResource.localize("KMK008_78"))
                  .build();

        sheetDatas.add(sheetData2);
        sheetDatas.add(sheetData3);
        sheetDatas.add(sheetData5);
        sheetDatas.add(sheetData6);
        sheetDatas.add(sheetData7);
        sheetDatas.add(sheetData9);
        return sheetDatas;
    }
	
}
