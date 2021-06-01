package nts.uk.ctx.exio.dom.input.validation.condition.system;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  値のドメイン制約
 */
@AllArgsConstructor
@Getter
public class DomainConstraint {
	
	private CheckMethod checkMethod;
	private String fqn;

	public boolean validate(Object value) {
		switch(this.checkMethod) {
			case PRIMITIVE_VALUE:
				return ValidationPrimitiveValue.run(this.fqn, value);
			case ENUM:
				return ValidationEnum.run(this.fqn, value);
			default:
				throw new RuntimeException("チェック方法が定義されていません。:" + this.checkMethod);
		}
	}
}
