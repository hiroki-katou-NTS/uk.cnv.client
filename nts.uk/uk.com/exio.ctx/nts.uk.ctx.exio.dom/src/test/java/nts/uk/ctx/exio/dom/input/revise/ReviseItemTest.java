package nts.uk.ctx.exio.dom.input.revise;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import lombok.val;
import mockit.Expectations;
import mockit.Mocked;
import nts.gul.util.Either;
import nts.uk.ctx.exio.dom.input.DataItem;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.uk.ctx.exio.dom.input.errors.ItemError;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItem;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseValue;

public class ReviseItemTest {
	@Mocked ReviseValue reviseValue;
		
	static class Dummy{
		private static String COM_ID = "comcomcom";
		private static ExternalImportCode EXI_CODE = new ExternalImportCode("exiexiexi");
		private static ImportingDomainId DOMAIN_ID = ImportingDomainId.EMPLOYEE_BASIC;
		private static int EXI_ITEM_NO = 765;
//		private static ItemType ITEM_TYPE = ItemType.STRING;
		private static ReviseValue REVISE_VALUE;
//		private static ImportingDomainId EXI_GROUP = ImportingDomainId.TASK;
//		private static ImportingMode EXI_MODE = ImportingMode.INSERT_ONLY;
//		private static ExecutionContext EXE_CONTEXT = new ExecutionContext(COM_ID, EXI_CODE.v(), EXI_GROUP, EXI_MODE);
		private static Either<ErrorMessage, ?> REVISE_V_RESULT;
	}

//	@Test
//	public void NotCodeConvert() {
//		
//		val reviseItem = new ReviseItem(
//				Dummy.COM_ID, 
//				Dummy.EXI_CODE, 
//				Dummy.DOMAIN_ID,
//				Dummy.EXI_ITEM_NO, 
//				Dummy.REVISE_VALUE);
//		
//		String targetValue = "value";
//		
//		new Expectations() {{
//			reviseValue.revise(targetValue);
//			result = Dummy.REVISE_V_RESULT;
//		}};
//		
//		Either<ItemError, DataItem> result = reviseItem.revise(targetValue);
//		
//		result.ifLeft(err -> assertThat(err).isNotNull());
//		result.ifRight(v -> assertThat(true).isTrue());
//	}

}
