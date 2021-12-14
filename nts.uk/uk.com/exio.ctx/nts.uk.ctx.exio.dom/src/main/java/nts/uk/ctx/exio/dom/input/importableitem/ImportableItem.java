package nts.uk.ctx.exio.dom.input.importableitem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import lombok.val;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.arc.primitive.DecimalPrimitiveValue;
import nts.arc.primitive.HalfIntegerPrimitiveValue;
import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.LongPrimitiveValue;
import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.TimeClockPrimitiveValue;
import nts.arc.primitive.TimeDurationPrimitiveValue;
import nts.gul.reflection.ClassReflection;
import nts.uk.ctx.exio.dom.input.DataItem;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.uk.ctx.exio.dom.input.errors.ItemError;
import nts.uk.ctx.exio.dom.input.util.Either;

/**
 * 受入可能項目
 */
@Getter
@AllArgsConstructor
@ToString
public class ImportableItem implements DomainAggregate{

	private ImportingDomainId domainId;
	private int itemNo;
	private String itemName;
	private ItemType itemType;
	private boolean required;
	private boolean isPrimaryKey;
	private Optional<DomainConstraint> domainConstraint;

	public Optional<ErrorMessage> validate(DataItem dataItem) {

		if(required && dataItem.isEmpty()) {
			return Optional.of(new ErrorMessage("必須項目ですが受入データがありません。"));
		}

		return domainConstraint
				.flatMap(c -> c.validate(dataItem.getValue()));
	}

	/**
	 * 型を変換する
	 * @param value
	 * @return
	 */
	public Either<ItemError, ?> parse(String value) {
		return itemType.parse(value)
				.mapLeft(errorMessage -> new ItemError(itemNo, errorMessage.getText()));
	}
	
	/**
	 * 自身の定義に不整合が無いか診断する
	 * @return
	 */
	public Either<String, Void> diagnose() {
		
		if (!domainConstraint.isPresent()) {
			if (itemType == ItemType.DATE) {
				return Either.rightVoid();
			}
			
			if (itemName.equals("社員コード")) {
				return Either.rightVoid();
			}
			
			if (domainId == ImportingDomainId.EMPLOYEE_BASIC && itemName.equals("パスワード")) {
				return Either.rightVoid();
			}
			
			return Either.left("ドメイン制約が設定されていない：" + this);
		}
		
		val constClass = domainConstraint.get().getConstraintClass();
		
		if (constClass.isEnum()) {
			if (itemType == ItemType.INT) {
				return Either.rightVoid();
			}
			
			return Either.left("FQNがEnumだが項目型が整数ではない：" + this);
		}
		
		if (!ValidDefine.isValid(constClass, itemType)) {
			return Either.left("FQNの型と項目型が一致しない：" + this);
		}
		
		return Either.rightVoid();
	}
	
	
	@Value
	private static class ValidDefine {
		Class<?> baseClass;
		ItemType itemType;
		
		static List<ValidDefine> VALID_PAIRS = Arrays.asList(
				new ValidDefine(TimeClockPrimitiveValue.class, ItemType.TIME_POINT),
				new ValidDefine(TimeDurationPrimitiveValue.class, ItemType.TIME_DURATION),
				new ValidDefine(IntegerPrimitiveValue.class, ItemType.INT),
				new ValidDefine(LongPrimitiveValue.class, ItemType.INT),
				new ValidDefine(DecimalPrimitiveValue.class, ItemType.REAL),
				new ValidDefine(HalfIntegerPrimitiveValue.class, ItemType.REAL),
				new ValidDefine(StringPrimitiveValue.class, ItemType.STRING)
				);
		
		static boolean isValid(Class<?> targetClass, ItemType targetItemType) {
			
			for (val defined : VALID_PAIRS) {
				if (ClassReflection.isSubclass(targetClass, defined.baseClass)) {
					if (targetItemType == defined.itemType) {
						return true;
					}
					
					return false;
				}
			}
			
			throw new RuntimeException("not supported: " + targetClass);
			
		}
	}
}
