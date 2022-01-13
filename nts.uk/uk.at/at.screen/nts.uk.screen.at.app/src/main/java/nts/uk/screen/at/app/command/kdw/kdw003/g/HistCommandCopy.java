package nts.uk.screen.at.app.command.kdw.kdw003.g;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistCommandCopy {
	private String empIdSource;
	private List<String> empIdDes;
}
