package nts.uk.ctx.exio.dom.exo.exoutsummaryservice;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import com.aspose.cells.AutoFitterOptions;
import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Encoding;
import com.aspose.cells.Font;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.TxtSaveOptions;
import com.aspose.cells.TxtValueQuoteType;

import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.uk.ctx.exio.dom.exo.condset.Delimiter;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

@Stateless
public class FileGenerator extends AsposeCellsReportGenerator {
	private static final String REPORT_ID = "SCV_GENERATOR";

	private static final int START_COLUMN = 0;

	private static final String FONT_FAMILY = "ＭＳ ゴシック";

	private static final int FONT_SIZE = 10;

	private static final int STANDARD_WIDTH = 100;

	private static final int STANDARD_HEIGHT = 25;

	public void generate(FileGeneratorContext generatorContext, FileData dataSource, String condSetName,
			boolean drawHeader, Delimiter delimiter) {
		val reportContext = this.createEmptyContext(REPORT_ID);

		val workbook = reportContext.getWorkbook();
		val sheet = workbook.getWorksheets().get(0);
		val cells = sheet.getCells();

		val headers = dataSource.getHeaders();
		List<Map<String, Object>> datas = dataSource.getDatas();
		
		int headerRowStart = 0;
		int dataRowStart = 0;

		if (!headers.isEmpty()) {
			this.setCommonStyle(cells);
			
			if(!StringUtils.isEmpty(condSetName)) {
				this.drawCondSetName(cells, condSetName, headerRowStart);
				headerRowStart++;
				dataRowStart++;
			}
			
			if(drawHeader) {
				this.drawHeader(cells, headers, headerRowStart);
				dataRowStart++;
			}
			
			this.drawTableBody(cells, headers, datas, dataRowStart);

			try {
				AutoFitterOptions options = new AutoFitterOptions();
				options.setAutoFitMergedCells(true);
				options.setOnlyAuto(true);
				sheet.autoFitColumns(options);
				sheet.autoFitRows(options);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		reportContext.processDesigner();

		TxtSaveOptions opts = new TxtSaveOptions();
		opts.setSeparatorString(delimiter.delimiter);
		opts.setQuoteType(TxtValueQuoteType.NEVER);
		opts.setEncoding(Encoding.getUTF8());
		
		reportContext.saveWithOtherOption(this.createNewFile(generatorContext, dataSource.getFileName()), opts);
	}

	private void setCommonStyle(Cells cells) {

		cells.setStandardWidthPixels(STANDARD_WIDTH);
		cells.setStandardHeightPixels(STANDARD_HEIGHT);
	}
	
	private void drawCondSetName(Cells cells, String condSetName, int headerRowStart) {
		Cell dataCell = cells.get(headerRowStart, START_COLUMN);
		dataCell.setValue(condSetName);
		Style style = this.getCellStyleNoBorder(dataCell.getStyle());
		dataCell.setStyle(style);
	}

	private void drawHeader(Cells cells, Map<String,String> headers, int headerRowStart) {

		int j = 0;
		for ( val item:  headers.entrySet()) {
			Cell header = cells.get(headerRowStart, START_COLUMN + j);
			header.setValue(item.getValue());
			Style style = this.getCellStyleNoBorder(header.getStyle());
			header.setStyle(style);
			j++;
		}
	}
	
	private void drawTableBody(Cells cells, Map<String,String> headers, List<Map<String, Object>> datas, int dataRowStart) {

		for (int i = 0; i < datas.size(); i++) {
			Map<String, Object> data = datas.get(i);
			int j = 0;
			for ( val item:  headers.entrySet()) {
				Cell dataCell = cells.get(dataRowStart + i, START_COLUMN + j);
				dataCell.setValue(data.get(item.getKey()));
				Style style = this.getCellStyleNoBorder(dataCell.getStyle());
				dataCell.setStyle(style);
				j++;
			}
		}
	}

	private Style getCellStyleNoBorder(Style s) {
		Style style = new Style();
		style.copy(s);
		style.setHorizontalAlignment(TextAlignmentType.LEFT);
		style.setVerticalAlignment(TextAlignmentType.CENTER);

		this.setFontStyle(style);

		return style;
	}

	private void setFontStyle(Style style) {

		Font font = style.getFont();
		font.setSize(FONT_SIZE);
		font.setName(FONT_FAMILY);
	}
}
