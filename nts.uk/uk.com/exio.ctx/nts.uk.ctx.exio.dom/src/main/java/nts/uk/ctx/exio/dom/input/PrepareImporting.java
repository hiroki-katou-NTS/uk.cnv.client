package nts.uk.ctx.exio.dom.input;

import java.io.InputStream;

import lombok.val;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizeRevisedData;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportErrorsRequire;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.meta.ImportingDataMeta;
import nts.uk.ctx.exio.dom.input.setting.DomainImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;

/**
 * 受入処理を準備する
 */
public class PrepareImporting {

	public static void prepare(
			Require require,
			ExternalImportSetting externalImportSetting,
			DomainImportSetting setting,
			InputStream csvFileStream) {

			val context = setting.executionContext(externalImportSetting.getCompanyId(), externalImportSetting.getCode());

			require.setupWorkspaceForEachDomain(context);//削除も一緒に
			
			// 受入データの組み立て
			setting.assemble(require, context, externalImportSetting.getCsvFileInfo(), csvFileStream);
			
			// 編集済みデータの正準化
			val meta = ImportingDataMeta.create(require, context, setting.getAssembly().getAllItemNo());
			CanonicalizeRevisedData.canonicalize(require, context, meta);
	}
	
	public static interface Require extends
			ExternalImportErrorsRequire,
			ExternalImportSetting.RequireAssemble,
			CanonicalizeRevisedData.Require {
		
		void setupWorkspace();

		void setupWorkspaceForEachDomain(ExecutionContext context);
		
		ImportableItem getImportableItem(ImportingDomainId domainId, int itemNo);
	}
}
