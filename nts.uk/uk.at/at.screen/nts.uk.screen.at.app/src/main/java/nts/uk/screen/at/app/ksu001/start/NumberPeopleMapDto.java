package nts.uk.screen.at.app.ksu001.start;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class NumberPeopleMapDto {

	
	
	public String code;
	
	public String name;
	
	public BigDecimal value;
	
	public String rankCode;

	public NumberPeopleMapDto(String code, String name, BigDecimal value) {
		super();
		this.code = code;
		this.name = name;
		this.value = value;
	}
}
