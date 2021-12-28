package nts.uk.ctx.at.shared.dom.attendance.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.val;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate.PropType;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

public final class AttendanceItemUtilRes {
	
	private AttendanceItemUtilRes () {}

	public static <T extends AttendanceItemDataGate> List<ItemValue> collect(T sources, AttendanceItemType type) {
		
		return collect(sources, new ArrayList<>(), type);
	}
	
	public static <T extends AttendanceItemDataGate> List<ItemValue> collect(T sources, 
			Collection<Integer> itemIds, AttendanceItemType type) {
		
		if (sources == null) {
			return new ArrayList<>();
		}
		
		List<ItemValue> result = new ArrayList<>();
		
		int layout = sources.isContainer() ? ItemConst.DEFAULT_IDX : ItemConst.DEFAULT_NEXT_IDX;
		
		Map<String, List<ItemValue>> items = AttendanceItemIdContainer.getIdMapStream(itemIds, type)
				.collect(Collectors.groupingBy(c -> getCurrentPath(layout, c.path(), 0)));
		
		collect(result, sources, items, layout, Optional.empty(), Optional.empty(), 0);
		
		return pathCorrect(type, result);
	}
	
	public static <T extends AttendanceItemDataGate> T merge(T sources, Collection<ItemValue> itemValues,
			AttendanceItemType type) {
		
		if (sources == null) {
			return sources;
		}
		
		itemValues = pathReverseCorrect(itemValues, type);
		
		int layout = sources.isContainer() ? ItemConst.DEFAULT_IDX : ItemConst.DEFAULT_NEXT_IDX;
		
		Map<String, List<ItemValue>> items = itemValues.stream().collect(Collectors.groupingBy(c -> {
											if (StringUtil.isNullOrEmpty(c.path(), false)) {
												c.withPath(AttendanceItemIdContainer.getPath(c.itemId(), type));
											}
											return getCurrentPath(layout, c.path(), 0);
										}));
		
		merge(sources, items, layout, Optional.empty(), Optional.empty(), 0);
		
		return sources;
	}
	
	private static <T extends AttendanceItemDataGate> void collect(List<ItemValue> result, T source, 
			Map<String, List<ItemValue>> items, int layout, 
			Optional<String> enumPlus, Optional<Integer> idxPlus, int idxLayout) {
		
		for (Entry<String, List<ItemValue>> i : items.entrySet()) {
			
			collectOneProp(result, source, layout, enumPlus, idxPlus, i, 0);
		}
	}

	private static void collectOneProp(List<ItemValue> result, AttendanceItemDataGate source, int layout,
			Optional<String> enumPlus, Optional<Integer> idxPlus, Entry<String, List<ItemValue>> i, int idxLayout) {
		String prop = i.getKey();
		PropType ct = source.typeOf(i.getKey());
		
		if (ct == PropType.VALUE) {

			source.valueOf(prop).ifPresent(v -> {
				ItemValue iv = i.getValue().get(0);
				iv.value(v.valueAsObjet()).valueType(v.type());
				result.add(iv);
			});
		} else {
			int nextLayout = layout + 1;
			
			if (ct == PropType.OBJECT) {
				AttendanceItemDataGate current = source.get(prop).orElse(null);

				if (current == null) {
					AttendanceItemDataGate emptyData = source.newInstanceOf(prop);
					
					if (emptyData == null) {
						/** マッピング不足かマッピング不正ののケース */
						return;
					}
					
					collect(result, emptyData, 
							groupNext(nextLayout, i.getValue(), idxLayout), 
							nextLayout, enumPlus, idxPlus, idxLayout);
					return;
				} 

				collect(result, current, 
						groupNext(nextLayout, i.getValue(), idxLayout), 
						nextLayout, enumPlus, idxPlus, idxLayout);
			} else {
				List<AttendanceItemDataGate> listV = source.gets(prop);
				
				Supplier<AttendanceItemDataGate> defaultGetter = () -> source.newInstanceOf(prop);

				switch (ct) {
				case IDX_LIST:
					processListIdx(result, listV, i.getValue(), nextLayout, enumPlus, idxLayout, defaultGetter);
					break;
				case ENUM_LIST:
					processListEnum(result, listV, i.getValue(), nextLayout, idxPlus, idxLayout, defaultGetter);
					break;
				case IDX_ENUM_LIST:
					processListAll(result, listV, i.getValue(), nextLayout, idxLayout, defaultGetter);
					break;
				case ENUM_HAVE_IDX:
					processEnumBeforeIdx(result, listV, i.getValue(), nextLayout, idxLayout, defaultGetter);
					break;
				case IDX_IN_ENUM:
					processIdxAfterEnum(result, listV, i.getValue(), nextLayout, idxLayout, defaultGetter);
					break;
				case IDX_IN_IDX:
					processListIdx(result, listV, i.getValue(), nextLayout, enumPlus, idxLayout + 1, defaultGetter);
					break;
				default:
					break;
				}
			}
		}
	}
	
	private static <T extends AttendanceItemDataGate> void merge(T source, 
			Map<String, List<ItemValue>> items, int layout, 
			Optional<String> enumPlus, Optional<Integer> idxPlus, int idxLayout) {
		
		for (Entry<String, List<ItemValue>> i : items.entrySet()) {
			
			internalMerge(source, layout, enumPlus, idxPlus, i, 0);
		}
	}

	private static <T extends AttendanceItemDataGate> void internalMerge(T source, int layout,
			Optional<String> enumPlus, Optional<Integer> idxPlus, Entry<String, List<ItemValue>> i, int idxLayout) {
		String prop = i.getKey();
		PropType ct = source.typeOf(i.getKey());
		
		if (ct == PropType.VALUE) {
			ItemValue iv = i.getValue().get(0);
			if (iv.type() == null) {
				iv.valueType(source.valueOf(prop).orElseGet(ItemValue::builder).type());
			}
			source.set(prop, iv);
			
			source.exsistData();
			
		} else {
			int nextLayout = layout + 1;
			
			if (ct == PropType.OBJECT) {
				AttendanceItemDataGate v = source.get(prop).orElseGet(() -> source.newInstanceOf(prop));
				
				if (v != null) {
					merge(v, groupNext(nextLayout, i.getValue(), idxLayout), nextLayout, enumPlus, idxPlus, idxLayout);
					
					source.set(prop, v);
					
					source.exsistData();
				}
			} else {
				List<AttendanceItemDataGate> listV = source.gets(prop);
				if (listV == null) {
					listV = new ArrayList<>();
				}
				
				Supplier<AttendanceItemDataGate> defaultGetter = () -> source.newInstanceOf(prop);
				switch (ct) {
				case IDX_LIST:
					mergeListIdx(defaultGetter, listV, i.getValue(), nextLayout, enumPlus, idxLayout);
					break;
				case ENUM_LIST:
					mergeListEnum(defaultGetter, listV, i.getValue(), nextLayout, idxPlus, idxLayout);
					break;
				case IDX_ENUM_LIST:
					mergeListAll(defaultGetter, listV, i.getValue(), nextLayout, idxLayout);
					break;
				case ENUM_HAVE_IDX:
					mergeEnumBeforeIdx(defaultGetter, listV, i.getValue(), nextLayout, idxLayout);
					break;
				case IDX_IN_ENUM:
					mergeIdxAfterEnum(defaultGetter, listV, i.getValue(), nextLayout, idxLayout);
					break;
				case IDX_IN_IDX:
					mergeListIdx(defaultGetter, listV, i.getValue(), nextLayout, enumPlus, idxLayout + 1);
					break;
				default:
					break;
				}
				
				source.set(prop, listV);
				
				source.exsistData();
			}
		}
		
		enumPlus.ifPresent(source::setEnum);
		idxPlus.ifPresent(source::setNo);
	}
	
	private static void processListIdx(List<ItemValue> result, List<AttendanceItemDataGate> listV, 
			List<ItemValue> items, int layout, Optional<String> enumPlus, int idxLayout, Supplier<AttendanceItemDataGate> defaultGetter) {
		
		for (Entry<Integer, List<ItemValue>> g : groupIdx(items, idxLayout).entrySet()) {
			int idx = g.getKey();
			
			internalProcess(result, listV, layout, g.getValue(), 
					enumPlus, Optional.of(idx), l -> l.isNo(idx), idxLayout, defaultGetter);
		}
	} 
	
	private static void processListEnum(List<ItemValue> result, List<AttendanceItemDataGate> listV, 
			List<ItemValue> items, int layout, Optional<Integer> idxPlus, int idxLayout, Supplier<AttendanceItemDataGate> defaultGetter) {
		
		for (Entry<String, List<ItemValue>> g : groupEnum(items).entrySet()) {
			String enumT = getEnum(g.getKey());

			internalProcess(result, listV, layout, g.getValue(), 
					Optional.of(enumT), idxPlus, l -> l.isEnum(enumT), idxLayout, defaultGetter);
		}
	} 
	
	private static void processListAll(List<ItemValue> result, List<AttendanceItemDataGate> listV, 
			List<ItemValue> items, int layout, int idxLayout, Supplier<AttendanceItemDataGate> defaultGetter) {
		
		for (Entry<String, List<ItemValue>> g : groupEnum(items).entrySet()) {
			int idx = getIdx(g.getKey(), idxLayout);
			String enumT = getEnum(g.getKey());
			
			internalProcess(result, listV, layout, g.getValue(), 
					Optional.of(enumT), Optional.of(idx),
					l -> l.isEnum(enumT) && l.isNo(idx), idxLayout, defaultGetter);
		}
	} 
	
	private static void processEnumBeforeIdx(List<ItemValue> result, List<AttendanceItemDataGate> listV, 
			List<ItemValue> items, int layout, int idxLayout, Supplier<AttendanceItemDataGate> defaultGetter) {
		
		for (Entry<String, List<ItemValue>> g : groupEnum(items).entrySet()) {
			int idx = getIdx(g.getKey(), idxLayout);
			String enumT = getEnum(g.getKey());
			
			internalProcess(result, listV, layout, g.getValue(), 
					Optional.of(enumT), Optional.of(idx), 
					l -> l.isEnum(enumT), idxLayout, defaultGetter);
		}
	}
	
	private static void processIdxAfterEnum(List<ItemValue> result, List<AttendanceItemDataGate> listV, 
			List<ItemValue> items, int layout, int idxLayout, Supplier<AttendanceItemDataGate> defaultGetter) {
		
		for (Entry<String, List<ItemValue>> g : groupEnum(items).entrySet()) {
			int idx = getIdx(g.getKey(), idxLayout);
			String enumT = getEnum(g.getKey());
			
			internalProcess(result, listV, layout, g.getValue(), 
					Optional.of(enumT), Optional.of(idx),
					l -> l.isNo(idx), idxLayout, defaultGetter);
		}
	}

	private static void internalProcess(List<ItemValue> result, List<AttendanceItemDataGate> listV, 
			int layout, List<ItemValue> groupItems, 
			Optional<String> enumT, Optional<Integer> idx,
			Predicate<AttendanceItemDataGate> checker, int idxLayout,
			Supplier<AttendanceItemDataGate> defaultGetter) {
		AttendanceItemDataGate current = listV == null ? defaultGetter.get() : listV.stream().filter(checker).findFirst().orElse(null);
		
		if (current == null) { 
			current = defaultGetter.get();
			collect(result, current, groupNext(layout, groupItems, idxLayout), layout, enumT, idx, idxLayout);
			return;
		} 
		collect(result, current, groupNext(layout, groupItems, idxLayout), layout, enumT, idx, idxLayout);
	} 
	
	private static void mergeListIdx(Supplier<AttendanceItemDataGate> defaultGetter,
			List<AttendanceItemDataGate> listV, List<ItemValue> items, 
			int layout, Optional<String> enumPlus, int idxLayout) {
		
		for (Entry<Integer, List<ItemValue>> g : groupIdx(items, idxLayout).entrySet()) {
			int idx = g.getKey();
			
			internalMergeList(defaultGetter, layout, listV, g.getValue(), 
								enumPlus, Optional.of(idx), v -> v.isNo(idx), idxLayout);
		}
	} 
	
	private static void mergeListEnum(Supplier<AttendanceItemDataGate> defaultGetter, List<AttendanceItemDataGate> listV, 
			List<ItemValue> items, int layout, Optional<Integer> idxPlus, int idxLayout) {
		
		for (Entry<String, List<ItemValue>> g : groupEnum(items).entrySet()) {
			String enumT = getEnum(g.getKey());
			
			internalMergeList(defaultGetter, layout, listV, g.getValue(), 
								Optional.of(enumT), idxPlus, v -> v.isEnum(enumT), idxLayout);
		}
	} 
	
	private static void mergeListAll(Supplier<AttendanceItemDataGate> defaultGetter, List<AttendanceItemDataGate> listV, 
			List<ItemValue> items, int layout, int idxLayout) {

		for (Entry<String, List<ItemValue>> g : groupEnum(items).entrySet()) {
			int idx = getIdx(g.getKey(), idxLayout);
			String enumT = getEnum(g.getKey());
			
			internalMergeList(defaultGetter, layout, listV, g.getValue(), 
								Optional.of(enumT), Optional.of(idx), 
								v -> v.isNo(idx) && v.isEnum(enumT), idxLayout);
		}
	} 
	
	private static void mergeEnumBeforeIdx(Supplier<AttendanceItemDataGate> defaultGetter, List<AttendanceItemDataGate> listV, 
			List<ItemValue> items, int layout, int idxLayout) {
		
		for (Entry<String, List<ItemValue>> g : groupEnum(items).entrySet()) {
			int idx = getIdx(g.getKey(), idxLayout);
			String enumT = getEnum(g.getKey());
			
			internalMergeList(defaultGetter, layout, listV, g.getValue(), 
								Optional.of(enumT), Optional.of(idx), 
								v -> v.isEnum(enumT), idxLayout);
		}
	} 
	
	private static void mergeIdxAfterEnum(Supplier<AttendanceItemDataGate> defaultGetter, List<AttendanceItemDataGate> listV, 
			List<ItemValue> items, int layout, int idxLayout) {
		
		for (Entry<String, List<ItemValue>> g : groupEnum(items).entrySet()) {
			int idx = getIdx(g.getKey(), idxLayout);
			String enumT = getEnum(g.getKey());
			
			internalMergeList(defaultGetter, layout, listV, g.getValue(), 
								Optional.of(enumT), Optional.of(idx), 
								v -> v.isNo(idx), idxLayout);
		}
	} 
	
	private static void internalMergeList(Supplier<AttendanceItemDataGate> defaultGetter, 
			int layout, List<AttendanceItemDataGate> listV, List<ItemValue> values,
			Optional<String> enumPlus, Optional<Integer> idxPlus, 
			Predicate<AttendanceItemDataGate> checker, int idxLayout) {
		
		AttendanceItemDataGate val = listV.stream().filter(checker::test)
				.findFirst().orElseGet(defaultGetter::get);

		if (val != null) {
			merge(val, groupNext(layout, values, idxLayout), layout, enumPlus, idxPlus, idxLayout);
			
			listV.removeIf(checker::test);
			
			listV.add(val);
			
			listV.sort((v1, v2) -> v1.getNo() - v2.getNo());
		}
	}

	private static Map<String, List<ItemValue>> groupNext(int layout, List<ItemValue> items, int idxLayout) {
		return items.stream().collect(Collectors.groupingBy(c -> getCurrentPath(layout, c.path(), idxLayout), 
																Collectors.toList()));
	}
	
	private static Map<Integer, List<ItemValue>> groupIdx(List<ItemValue> items, int idxLayout) {
		return items.stream().collect(Collectors.groupingBy(c -> getIdx(c.path(), idxLayout), Collectors.toList()));
	}

	public static int getIdx(String text, int idxLayout) {
		if (StringUtil.isNullOrEmpty(text, false)) return -1;
		
		String noEnumTextPath = text.split(ItemConst.DEFAULT_ENUM_SEPERATOR)[0];
		
		String[] layouts = noEnumTextPath.split(ItemConst.DEFAULT_IDX_SEPERATOR);
		
		if (idxLayout == 0) {
			
			return getFirstIdx(layouts[0]);
		}
		
		if (idxLayout >= layouts.length) {
			return -1;
		}
		
		return Integer.parseInt(layouts[idxLayout]);
	}

	private static int getFirstIdx(String text) {
		char char1 = text.charAt(text.length() - 1);
		char char2 = text.length() < 2 ? 'a' : text.charAt(text.length() - 2);
		char char3 = text.length() < 3 ? 'a' : text.charAt(text.length() - 3);
		
		if (char3 >= '0' && char3 <= '9') {
			return Character.getNumericValue(char3) * 100 
					+ Character.getNumericValue(char2) * 10 
					+ Character.getNumericValue(char1);
		} else if (char2 >= '0' && char2 <= '9'){
			return Character.getNumericValue(char2) * 10 
					+ Character.getNumericValue(char1);
		} else if (char1 >= '0' && char1 <= '9'){
			return Character.getNumericValue(char1);
		}
		
		return -1;
	}
	
	private static Map<String, List<ItemValue>> groupEnum(List<ItemValue> items) {
		return items.stream().collect(Collectors.groupingBy(c -> {
			String[] parts = c.path().split(Pattern.quote(ItemConst.DEFAULT_ENUM_SEPERATOR));
			if(parts.length>=2)
				return parts[parts.length - 1];
			return "";
		}));
	}
	
	private static String getCurrentPath(int layout, String path, int idxLayout) {
		String prop = getAfter(path, ItemConst.DEFAULT_SEPERATOR, layout);
		prop = getAfter(prop, ItemConst.DEFAULT_ENUM_SEPERATOR, 0);
		
		if (idxLayout > 0) {
			prop = getAfter(prop, ItemConst.DEFAULT_IDX_SEPERATOR, 0);
		}
		
		if (prop.contains(ItemConst.DEFAULT_IDX_SEPERATOR)) {
			prop = prop.split(ItemConst.DEFAULT_IDX_SEPERATOR)[0];
		}
		
		int idx = getIdx(prop, 0);
		if (idx > 0) {
			return getEnum(prop);
		}
		return prop;
	}
	
	// chua lay duoc dung layout?
	private static String getAfter(String path, String sepe, int layout) {
	    int pos = 0, count = 0, end;
	    if (layout < 0) {
	    	return path;
	    }
	    if (layout == 0) {
	    	if ((end = path.indexOf(sepe, pos)) >= 0) {
		    	return path.substring(pos, end); 
	    	}
	    	return path;
	    }
	    while ((end = path.indexOf(sepe, pos)) >= 0) {
	        if (count == layout) {
	        	return path.substring(pos, end); 
	        }
	        count++;
	        pos = end + 1;
	    }
	    if (count == layout) {
        	return path.substring(pos); 
        }
	    return path;
	}

	private static String getEnum(String key) {
		String[] parts = key.split(ItemConst.DEFAULT_ENUM_SEPERATOR);
		
		if (parts.length == 0) {
			return "";
		}
		return parts[parts.length - 1].replaceAll(ItemConst.DEFAULT_NUMBER_REGEX, "");
	} 

	private static List<ItemValue> pathCorrect(AttendanceItemType type, Collection<ItemValue> items) {
		if (type != AttendanceItemType.WEEKLY_ITEM) {
			return new ArrayList<>(items);
		}
		
		return changePathToWeekly(items);
	}
	

	private static Collection<ItemValue> pathReverseCorrect(Collection<ItemValue> itemValues, AttendanceItemType type) {
		if(type == AttendanceItemType.WEEKLY_ITEM) {

			return changePathToMonthly(itemValues);
		}
		
		return itemValues;
	}
	
	private static List<ItemValue> changePathToWeekly(Collection<ItemValue> items) {
		return items.stream().map(i -> {
			
			val clone = i.clone();
			
			if (clone.path() == null) {
				
				return clone;
			}

			if (clone.path().startsWith(ItemConst.WEEKLY_ATTENDANCE_TIME_EXESS)) {
				return clone;
			}
			
			if (clone.path().startsWith(ItemConst.MONTHLY_ATTENDANCE_TIME_NAME)) {
				clone.withPath(clone.path().replaceFirst(Pattern.quote(ItemConst.MONTHLY_ATTENDANCE_TIME_NAME), ItemConst.WEEKLY_ATTENDANCE_TIME_NAME));
			} else if (clone.path().startsWith(ItemConst.MONTHLY_OPTIONAL_ITEM_NAME)) {
				clone.withPath(clone.path().replaceFirst(Pattern.quote(ItemConst.MONTHLY_OPTIONAL_ITEM_NAME), ItemConst.WEEKLY_ATTENDANCE_TIME_NAME));
			} 
			
			return clone;
		}).collect(Collectors.toList());
	}

	private static Collection<ItemValue> changePathToMonthly(Collection<ItemValue> items) {
		
		return items.stream().map(i -> {

			val clone = i.clone();
			
			if (clone.path() == null) {
				return clone;
			}
			
			if (clone.path().startsWith(ItemConst.WEEKLY_ATTENDANCE_TIME_EXESS)) {
				return clone;
			}
			
			if (clone.path().startsWith(WEEK_OPT)) {
				clone.withPath(clone.path().replaceFirst(Pattern.quote(ItemConst.WEEKLY_ATTENDANCE_TIME_NAME), ItemConst.MONTHLY_OPTIONAL_ITEM_NAME));
			} else if (clone.path().startsWith(ItemConst.WEEKLY_ATTENDANCE_TIME_NAME)) {
				clone.withPath(clone.path().replaceFirst(Pattern.quote(ItemConst.WEEKLY_ATTENDANCE_TIME_NAME), ItemConst.MONTHLY_ATTENDANCE_TIME_NAME));
			}  
			
			return clone;
		}).collect(Collectors.toList());
	}
	
	private static String WEEK_OPT = AttendanceItemIdContainer.join(ItemConst.WEEKLY_ATTENDANCE_TIME_NAME, ItemConst.OPTIONAL_ITEM_VALUE);
	
}
