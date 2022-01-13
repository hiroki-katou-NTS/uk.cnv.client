package nts.uk.ctx.exio.app.input.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.uk.ctx.exio.dom.input.csvimport.CsvRecord;
import nts.uk.ctx.exio.dom.input.csvimport.ExternalImportCsvFileInfo;
import nts.uk.ctx.exio.dom.input.setting.FromCsvBaseSettingToDomainRequire;

public class FromCsvBaseSettingToDomainRequireImpl  implements FromCsvBaseSettingToDomainRequire{
	private FileStorage fileStorage;

	public FromCsvBaseSettingToDomainRequireImpl(FileStorage fileStorage) {
		this.fileStorage = fileStorage;
	}
	
	@Override
	@SneakyThrows
	public Optional<List<String>> createBaseCsvInfo(ExternalImportCsvFileInfo fileInfo) {
		if (!fileInfo.getBaseCsvFileId().isPresent()) {
			return Optional.empty();
		}

		List<String> baseCsvColumns = new ArrayList<>();
		try (val inputStream = fileStorage.getStream(fileInfo.getBaseCsvFileId().get())
				.orElseThrow(() -> new RuntimeException("file not found: " + fileInfo.getBaseCsvFileId().get()))) {
			fileInfo.readBaseCsv(inputStream, r-> this.readBaseCsvRecord(r, fileInfo, baseCsvColumns));
		}
		
		return Optional.of(baseCsvColumns);
	}
	private void readBaseCsvRecord(CsvRecord r, ExternalImportCsvFileInfo fileInfo, List<String> columns) {
		if(r.getRowNo() > fileInfo.getItemNameRowNumber().v()) return;

		if(fileInfo.getImportStartRowNumber().v() <= fileInfo.getItemNameRowNumber().v()) {
			throw new RuntimeException("import start row number value  need setting more than value of header row number.");
		}
		
		if(r.getRowNo() == fileInfo.getItemNameRowNumber().v()) {
			for(int i=0; i<r.getRawItems().size(); i++) {
				String item = r.getRawItems().get(i);
				columns.add(item);
			}
		}
	}
	
	@Override
	@SneakyThrows
	public Map<Integer, List<String>> readBaseCsvWithFirstData(ExternalImportCsvFileInfo fileInfo) {
		Map<Integer, List<String>> result = new HashMap<>();
		
		if (!fileInfo.getBaseCsvFileId().isPresent()) return result;
		String fileId = fileInfo.getBaseCsvFileId().get();
		
		try (val inputStream = fileStorage.getStream(fileId)
				.orElseThrow(() -> new RuntimeException("file not found: " + fileId))) {
			fileInfo.readBaseCsv(inputStream, r-> this.readBaseCsvRecord(r, fileInfo, result));
		}
		
		return result;
	}

	private void readBaseCsvRecord(CsvRecord r, ExternalImportCsvFileInfo fileInfo, Map<Integer, List<String>> result) {
		if(r.getRowNo() > fileInfo.getImportStartRowNumber().v()) return;

		if(fileInfo.getImportStartRowNumber().v() <= fileInfo.getItemNameRowNumber().v()) {
			throw new RuntimeException("import start row number value  need setting more than value of header row number.");
		}
		
		if(r.getRowNo() == fileInfo.getItemNameRowNumber().v()) {
			for(int i=0; i<r.getRawItems().size(); i++) {
				String item = r.getRawItems().get(i);
				List<String> col = new ArrayList<>();
				col.add(item);
				result.put(i, col);
			}
		}
		
		if(r.getRowNo() == fileInfo.getImportStartRowNumber().v()) {
			for(int i=0; i<r.getRawItems().size(); i++) {
				String item = r.getRawItems().get(i);
				result.get(i).add(item);
			}
		}
	}
}