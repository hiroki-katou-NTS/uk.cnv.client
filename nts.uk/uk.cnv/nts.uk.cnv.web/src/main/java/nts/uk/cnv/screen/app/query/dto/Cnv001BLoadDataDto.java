package nts.uk.cnv.screen.app.query.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Cnv001BLoadDataDto {

	List<String> selectedTables;

	List<String> unselectedTables;
}
