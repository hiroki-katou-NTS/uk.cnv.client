package nts.uk.shr.com.validate.service;

import java.util.Optional;

import nts.uk.shr.com.validate.constraint.DataConstraint;
import nts.uk.shr.com.validate.constraint.implement.DateConstraint;
import nts.uk.shr.com.validate.constraint.implement.NumericConstraint;
import nts.uk.shr.com.validate.constraint.implement.StringConstraint;
import nts.uk.shr.com.validate.constraint.implement.TimeConstraint;
import nts.uk.shr.com.validate.constraint.implement.TimePointConstraint;
import nts.uk.shr.com.validate.validator.DateValidator;
import nts.uk.shr.com.validate.validator.NumericValidator;
import nts.uk.shr.com.validate.validator.StringValidator;
import nts.uk.shr.com.validate.validator.TimePointValidator;
import nts.uk.shr.com.validate.validator.TimeValidator;

public class CellValidateService {
	
	public static Optional<String> validateValue(DataConstraint constraint, String value) {
		
		switch (constraint.getConstraintType()) {
		case STRING:
			return StringValidator.validate((StringConstraint) constraint, value);
		case NUMERIC:
			return NumericValidator.validate((NumericConstraint) constraint, value);
		case DATE:
			return DateValidator.validate((DateConstraint) constraint, value);
		case TIME:
			return TimeValidator.validate((TimeConstraint) constraint, value);
		case TIMEPOINT:
			return TimePointValidator.validate((TimePointConstraint) constraint, value);
		}
		return Optional.empty();
	}

//	public static List<CellError> validateCells(List<DataConstraint> constraints, Map<Integer, NtsExcelRow> rows) {
//		Map<Integer, DataConstraint> constraintMap = constraints.stream()
//				.collect(Collectors.toMap(c -> c.getColumn(), c -> c));
//
//		List<CellError> errorList = new ArrayList<>();
//		
//		// for each row
//		rows.forEach((rowIndex, row) -> {
//			
//			// for each cell in the row
//			row.cells().forEach(cell -> {
//				
//				// validate value of the cell
//				DataConstraint constraint = constraintMap.get(cell.getColumn());
//				if (constraint != null) {
//					Optional<String> errorOpt = validateValue(constraint, cell);
//					if (errorOpt.isPresent()) {
//						errorList.add(new CellError(cell.getColumn(), cell.getRow(), errorOpt.get()));
//					}
//				}
//			});
//		});
//
//		return errorList;
//	}

	

}
