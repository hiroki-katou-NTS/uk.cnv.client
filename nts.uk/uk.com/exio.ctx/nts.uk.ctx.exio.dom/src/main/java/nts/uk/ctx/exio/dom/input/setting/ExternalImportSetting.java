package nts.uk.ctx.exio.dom.input.setting;

import java.io.InputStream;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.ImportingMode;
import nts.uk.ctx.exio.dom.input.csvimport.CsvRecord;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.setting.assembly.ExternalImportAssemblyMethod;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;
import nts.uk.ctx.exio.dom.input.setting.assembly.mapping.ImportingMapping;
import nts.uk.ctx.exio.dom.input.validation.ValidateData;

/**
 * 受入設定
 */
@Getter
@AllArgsConstructor
public class ExternalImportSetting implements DomainAggregate {

	/** 会社ID */
	private String companyId;

	/** 受入設定コード */
	private ExternalImportCode code;

	/** 受入設定名称 */
	@Setter
	private ExternalImportName name;

	/** 受入ドメインID */
	private ImportingDomainId externalImportDomainId;

	/** 受入モード */
	@Setter
	private ImportingMode importingMode;

	/** 組立方法 */
	private ExternalImportAssemblyMethod assembly;

	/**
	 * マッピングを更新する
	 * @param itemList
	 */

	public void merge(RequireMerge require, List<Integer> itemList) {
		
		val mappingRequire = new ImportingMapping.RequireMerge() {
			@Override
			public void deleteReviseItems(List<Integer> itemNos) {
				require.deleteReviseItems(code, itemNos);
			}
		};
		
		this.assembly.merge(mappingRequire, itemList);
	}
	
	public static interface RequireMerge {
		void deleteReviseItems(ExternalImportCode settingCode, List<Integer> itemNos);
	}

	/**
	 * ドメインが変更されたのでマッピングを作り直す
	 * @param domainId
	 * @param items
	 */
	public void changeDomain(RequireChangeDomain require, ImportingDomainId domainId, List<Integer> items) {
		externalImportDomainId = domainId;
		assembly = ExternalImportAssemblyMethod.create(assembly.getCsvFileInfo(), items);
		
		// 受入ドメインが変わるので既存の編集設定はすべて削除
		require.deleteReviseItems(code);
	}
	
	public static interface RequireChangeDomain {
		void deleteReviseItems(ExternalImportCode settingCode);
	}


	public void assemble(RequireAssemble require, ExecutionContext context, InputStream csvFileStream) {

		assembly.getCsvFileInfo().parse(
				csvFileStream,
				r -> processRecord(require, context, r));
	}

	/**
	 * 1レコード分の組み立て
	 * @param require
	 * @param context
	 * @param columnNames
	 * @param csvRecord
	 */
	private void processRecord(
			RequireAssemble require,
			ExecutionContext context,
			CsvRecord csvRecord) {

		val optRevisedData = assembly.assemble(require, context, csvRecord);
		if(!optRevisedData.isPresent()) {
			// データの組み立て結果が空の場合
			return;
		}

		val revisedData = optRevisedData.get();

		val errors = ValidateData.validate(require, context, revisedData);

		if(errors.isEmpty()) {
			require.save(context, revisedData);
		}
		else {
			errors.forEach(error ->{
				require.add(context, new ExternalImportError(revisedData.getRowNo(),
																						   error.getItemNo(),
																						   error.getMessage()));
			});
		}
	}

	public static interface RequireAssemble extends
			ExternalImportAssemblyMethod.Require,
			ValidateData.ValidateRequire {

		void save(ExecutionContext context, RevisedDataRecord revisedDataRecord);
	}
}
