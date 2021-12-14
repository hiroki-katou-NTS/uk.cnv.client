package nts.uk.file.at.infra.attendancerecord.generator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Value
public class ColumnsToMerge {
	
	private List<Indexes> indexes;
	
	/**
	 * Create data from length of merged columns, start from column index = 0
	 * @param mergeLengths
	 */
	public ColumnsToMerge(int... mergeLengths) {
		AtomicInteger index = new AtomicInteger(0);
		this.indexes = Arrays.stream(mergeLengths)
				.mapToObj(mergeLength -> new Indexes(index.getAndAdd(mergeLength), mergeLength))
				.collect(Collectors.toList());
	}

	@Getter
	@AllArgsConstructor
	public class Indexes {
		// Start columns index
		private int startColumn;
		
		// Number of columns to merge
		private int numberToMerge;
	}
}
