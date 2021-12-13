module nts.uk.at.view.ksu003.a.model {
	import duration = nts.uk.time.minutesBased.duration; // convert time 
	import formatById = nts.uk.time.format.byId;
	import getText = nts.uk.resource.getText;

	export function convertTimeToChart(startTime: any, endTime: any) {
		let convertTime = null;

		startTime = startTime != null ? formatById("Clock_Short_HM", startTime) : "0:00";
		endTime = endTime != null ? formatById("Clock_Short_HM", endTime) : "0:00";
		startTime = convertTimePixel(startTime);
		endTime = convertTimePixel(endTime);
		convertTime = {
			startTime: startTime,
			endTime: endTime
		}
		return convertTime;
	}

	export function convertTimePixel(timeStart: any) {
		let time = 0;
		if (timeStart != null) {
			// convert string to pixel
			time = ((duration.parseString(timeStart).toValue()) / 5);
		}
		return time;
	}

	/** COLOR-ZONE */
	/*A4_color*/
	export function setColorEmployee(needSchedule: number, cheering: number) {
		if (needSchedule == 0) return '#ddddd2';
		switch (cheering) {
			case model.SupportAtr.ALL_DAY_SUPPORTER:
				return '#c3d69b';
			case model.SupportAtr.ALL_DAY_RESPONDENT:
				return '#fedfe6';
			case model.SupportAtr.TIMEZONE_SUPPORTER:
				return '#ebf1de';
			case model.SupportAtr.TIMEZONE_RESPONDENT:
				return '#ffccff';
		}
		return "";
	}

	export function checkColorA6(workInfo: any) {
		let workTypeColor = "";
		if (workInfo === model.EditStateSetting.HAND_CORRECTION_MYSELF) {
			workTypeColor = "#94b7fe"
		} else if (workInfo === model.EditStateSetting.HAND_CORRECTION_OTHER) {
			workTypeColor = "#cee6ff"
		} else if (workInfo === model.EditStateSetting.REFLECT_APPLICATION) {
			workTypeColor = "#bfea60"
		}
		return workTypeColor;
	}

	/** A6_color① - chua xong o phan login va xin bang don */
	export function setColorWorkingInfo(empId: string, isConfirmed: number, workScheduleDto: any, isNeedWorkSchedule: any) {
		let self = this, workTypeColor = "", workTimeColor = "", startTime1Color = "", endTime1Color = "",
			startTime2Color = "", endTime2Color = "", breakTimeColor = "", workingInfoColor = "";

		if (isConfirmed == 0 && isNeedWorkSchedule == 0) {
			workingInfoColor = '#ddddd2';
		}
		if (isConfirmed == 1) {
			workingInfoColor = '#eccefb';
		}
		if (workScheduleDto != null) {
			if (workScheduleDto.workTypeStatus != null) workTypeColor = checkColorA6(workScheduleDto.workTypeStatus);
			if (workScheduleDto.workTimeStatus != null) workTimeColor = checkColorA6(workScheduleDto.workTimeStatus);
			if (workScheduleDto.startTime1Status != null) startTime1Color = checkColorA6(workScheduleDto.startTime1Status);
			if (workScheduleDto.endTime1Status != null) endTime1Color = checkColorA6(workScheduleDto.endTime1Status);
			if (workScheduleDto.startTime2Status != null) startTime2Color = checkColorA6(workScheduleDto.startTime2Status);
			if (workScheduleDto.endTime2Status != null) endTime2Color = checkColorA6(workScheduleDto.endTime2Status);
			if (workScheduleDto.breakTimeStatus != null) breakTimeColor = checkColorA6(workScheduleDto.breakTimeStatus);
		}
		return ({
			workTypeColor: workTypeColor,
			workTimeColor: workTimeColor,
			startTime1Color: startTime1Color,
			endTime1Color: endTime1Color,
			startTime2Color: startTime2Color,
			endTime2Color: endTime2Color,
			breakTimeColor: breakTimeColor,
			workingInfoColor: workingInfoColor

		});
	}

	// Kiểm tra giới hạn của thời gian 
	export function checkLimitTime(time: any, timeRangeLimit: any, index: number, dispStartHours: any) {
		let self = this;
		let timeLimit = null, limitStartMin = 0, limitStartMax = timeRangeLimit, limitEndMin = 0, limitEndMax = timeRangeLimit;
		let dispStart = (dispStartHours * 60) / 5;
		if (time[index].startTimeRange != null) {
			if (index > 0) {
				limitStartMin = time[index].endTime;
			}
			limitStartMin = checkRangeLimitTime(time, timeRangeLimit, 1, 0, dispStartHours) ? ((time[index].startTimeRange.startTime.time) / 5) : dispStartHours;
			limitStartMax = checkRangeLimitTime(time, timeRangeLimit, 2, 0, dispStartHours) ? ((time[index].startTimeRange.endTime.time) / 5) : timeRangeLimit;
			if (time[index].startTimeRange.startTime.time <= dispStartHours) {
				limitStartMin = dispStartHours;
			}
			if (time[index].startTimeRange.endTime.time <= dispStartHours) {
				limitStartMax = dispStartHours;
			}
		} else if (time[0].startTimeRange != null) {
			limitStartMin = ((time[0].startTimeRange.startTime.time) / 5)
		}

		if (time[index].endTimeRange != null) {
			limitEndMin = self.checkRangeLimitTime(time, timeRangeLimit, 3, 0, dispStartHours) ? ((time[index].endTimeRange.startTime.time) / 5) : timeRangeLimit;
			limitEndMax = self.checkRangeLimitTime(time, timeRangeLimit, 4, 0, dispStartHours) ? ((time[index].endTimeRange.endTime.time) / 5) : timeRangeLimit;
			if (time[index].endTimeRange.startTime.time <= dispStartHours) {
				limitEndMin = dispStartHours;
			}
			if (time[index].endTimeRange.endTime.time <= dispStartHours) {
				limitEndMax = dispStartHours;
			}
		}

		timeLimit = {
			limitStartMin: limitStartMin < dispStart ? dispStart : limitStartMin,
			limitStartMax: limitStartMax < dispStart ? dispStart : limitStartMax,
			limitEndMin: limitEndMin < dispStart ? dispStart : limitEndMin,
			limitEndMax: limitEndMax < dispStart ? dispStart : limitEndMax
		}
		return timeLimit;
	}

	// Kiểm tra giới hạn hiển thị của thanh chart
	export function checkRangeLimitTime(flex: any, timeRangeLimit: number, type: number, index: number, dispStartHours: any) {
		let check = false, self = this;
		if (flex.length > 0) {
			if (type == 1 && flex[index].startTimeRange != null && ((flex[index].startTimeRange.startTime.time - dispStartHours * 60) / 5) <= timeRangeLimit) check = true;
			if (type == 2 && flex[index].startTimeRange != null && ((flex[index].startTimeRange.endTime.time - dispStartHours * 60) / 5) < timeRangeLimit) check = true;
			if (type == 3 && flex[index].endTimeRange != null && ((flex[index].endTimeRange.startTime.time - dispStartHours * 60) / 5) <= timeRangeLimit) check = true;
			if (type == 4 && flex[index].endTimeRange != null && ((flex[index].endTimeRange.endTime.time - dispStartHours * 60) / 5) < timeRangeLimit) check = true;
		}
		return check;
	}

	export function convertTime(timeStart: any, timeEnd: any, timeStart2: any, timeEnd2: any) {
		if (timeStart != "") timeStart = (duration.parseString(timeStart).toValue());
		if (timeEnd != "") timeEnd = (duration.parseString(timeEnd).toValue());
		if (timeStart2 != "") timeStart2 = (duration.parseString(timeStart2).toValue());
		if (timeEnd2 != "") timeEnd2 = (duration.parseString(timeEnd2).toValue());

		let timeConvert = {
			start: timeStart != "" ? timeStart : "",
			end: timeEnd != "" ? timeEnd : "",
			start2: timeStart2 != "" ? timeStart2 : "",
			end2: timeEnd2 != "" ? timeEnd2 : ""
		}
		return timeConvert;
	}

	export function checkTimeOfChart(time: any, timeRangeLimit: any, dispStartHours: any) {
		// check start time
		let self = this;
		//if (time > timeRangeLimit) time = timeRangeLimit;
		if (time < ((dispStartHours * 60) / 5)) time = ((dispStartHours * 60) / 5);
		return time;
	}
	
	export function checkTimeChart(time: any, timeRangeLimit: any, dispStartHours: any) {
		// check start time
		let self = this;
		if (time > timeRangeLimit) time = timeRangeLimit;
		if (time < ((dispStartHours * 60) / 5)) time = ((dispStartHours * 60) / 5);
		return time;
	}
	
	export function checkTimeChartChang(time: any, timeRangeLimit: any, dispStartHours: any) {
		// check start time
		if (time < ((dispStartHours * 60) / 5)) time = ((dispStartHours * 60) / 5);
		return time;
	}
	
	export function checkRangeBreakTime(lstHoliday: any[], breakTime: any, index: any) {
		let rangeBreak: any = { start: 0, end: 9999 };
		lstHoliday = _.filter(lstHoliday, x => { return x.index === index });
		for (let i = 0; i < lstHoliday.length; i++) {
			if (lstHoliday[i].start > breakTime.end) {
				rangeBreak.end = lstHoliday[i].start;
			}
			if (lstHoliday[i].end < breakTime.start) {
				rangeBreak.start = lstHoliday[i].end;
			}
		}
		return rangeBreak;
	}

	export function calcTimeDuplicate(timeStart: any, timeEnd: any, startTime: any, endTime: any, type: number) {
		let duplicateTime: any = [];
		duplicateTime = ({
			startTime: 0,
			endTime: 0
		});
		if (_.inRange(timeStart, startTime, endTime) || _.inRange(timeEnd, startTime, endTime)) {
			if (timeStart <= startTime && timeEnd <= endTime) {
				duplicateTime = ({
					startTime: startTime,
					endTime: timeEnd
				});
			}

			if (timeStart >= startTime && timeEnd >= endTime) {
				duplicateTime = ({
					startTime: timeStart,
					endTime: endTime
				});
			}

			if (timeStart >= startTime && timeEnd <= endTime) {
				duplicateTime = ({
					startTime: timeStart,
					endTime: timeEnd
				});
			}
		}
		return duplicateTime;
	}

	export function calcTotalTime(workScheduleDto: model.EmployeeWorkScheduleDto, whenCall?: any) {
		let totalTime: any = null;
		if (!_.isNil(workScheduleDto)) {
			if (workScheduleDto.endTime2 != null && workScheduleDto.startTime2 != null && workScheduleDto.endTime2 != 0 && workScheduleDto.startTime2 != 0)
				totalTime = (workScheduleDto.endTime1 - workScheduleDto.startTime1) + (workScheduleDto.endTime2 - workScheduleDto.startTime2);
			else if (workScheduleDto.endTime1 != null && workScheduleDto.startTime1 != null && workScheduleDto.endTime1 != 0 && workScheduleDto.startTime1 != 0)
				totalTime = (workScheduleDto.endTime1 - workScheduleDto.startTime1);
			if (_.isNil(whenCall))
				totalTime = totalTime != null ? formatById("Clock_Short_HM", totalTime) : "";
		}
		return totalTime;
	}

	export function calcAllTime(schedule: any, lstTime: any, timeRangeLimit: any, dispStart: any, dispStartHours: any) {
		// Tính tổng thời gian làm việc
		timeRangeLimit = timeRangeLimit;
		let targetInfor = nts.uk.ui.windows.getShared("targetInforKsu003"); // thay tạm cho __viewContext.viewModel.viewmodelA.dataScreen003A().targetInfor sau khi merge 5_3 thì xóa đi
		let totalTimeAll = 0, totalTimeWork = 0,
			start1 = (schedule.workScheduleDto != null && schedule.workScheduleDto.startTime1 != null && schedule.workScheduleDto.startTime1 != 0) ? (model.checkTimeChart(schedule.workScheduleDto.startTime1, timeRangeLimit * 5 + dispStart *5, dispStartHours)) : 0,
			end1 = (schedule.workScheduleDto != null && schedule.workScheduleDto.endTime1 != null && schedule.workScheduleDto.endTime1 != 0) ? (model.checkTimeChart(schedule.workScheduleDto.endTime1, timeRangeLimit * 5 + dispStart *5, dispStartHours)) : 0,
			start2 = (schedule.workScheduleDto != null && schedule.workScheduleDto.startTime2 != null && schedule.workScheduleDto.startTime2 != 0) ? (model.checkTimeChart(schedule.workScheduleDto.startTime2, timeRangeLimit * 5 + dispStart *5, dispStartHours)) : 0,
			end2 = (schedule.workScheduleDto != null && schedule.workScheduleDto.endTime2 != null && schedule.workScheduleDto.endTime2 != 0) ? (model.checkTimeChart(schedule.workScheduleDto.endTime2, timeRangeLimit * 5 + dispStart *5, dispStartHours)) : 0;

		lstTime = _.sortBy(lstTime, [function(o: any) { return o.end; }]).reverse();
		lstTime = _.uniqWith(lstTime, function(arrVal: any, othVal: any) {
			return (arrVal.start === othVal.start);
		});

		lstTime = _.sortBy(lstTime, [function(o: any) { return o.start; }]);
		lstTime = _.uniqWith(lstTime, function(arrVal: any, othVal: any) {
			return (arrVal.end === othVal.end);
		});

		lstTime.forEach((total: any) => {
			totalTimeAll += (total.end * 5) - (total.start * 5)
		});
		start1 = start1 <= dispStart * 5 ? dispStart * 5 : start1;

		if (start2 != null && start2 != 0)
			start2 = start2 <= dispStart * 5 ? dispStart * 5 : start2;

		if (start2 != 0 && end2 != 0 && __viewContext.viewModel.viewmodelA.dataScreen003A().targetInfor == 1)
			totalTimeWork = ((end2) - (start2)) + ((end1) - (start1));
		else if (end1 != 0)
			totalTimeWork = ((end1) - (start1));
		totalTimeWork = totalTimeWork - totalTimeAll;

		let totalTimeWorks = totalTimeWork != 0 ? formatById("Clock_Short_HM", totalTimeWork) : "";

		return totalTimeWorks;
	}

	export function getCss(index: number, targetInfor: number) {
		let cssWorkType: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(1)",
			cssWorkTypeName: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(2)",
			cssWorkTime: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(3)",
			cssWorkTName: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(4)",
			cssStartTime1: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(5)",
			cssEndTime1: string = "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(6)",
			cssStartTime2: string = targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(7)" : "",
			cssEndTime2: string = targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(8)" : "",
			cssbreakTime: string = targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(10)" :
				"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(8)",
			cssTotalTime: string = targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(9)" :
				"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index + 2).toString() + ")" + " > td:nth-child(7)";
		let css = {
			cssWorkType: cssWorkType,
			cssWorkTypeName: cssWorkTypeName,
			cssWorkTime: cssWorkTime,
			cssWorkTName: cssWorkTName,
			cssStartTime1: cssStartTime1,
			cssEndTime1: cssEndTime1,
			cssStartTime2: cssStartTime2,
			cssEndTime2: cssEndTime2,
			cssbreakTime: cssbreakTime,
			cssTotalTime: cssTotalTime
		};
		return css;
	}

	export function setDisableCell(type: any, empId: any, worktypeCode: string, worktypeName: string, worktimeCode: string, worktimeName: string, startTime1: string, endTime1: string,
		startTime2: string, endTime2: string) {
		if (worktypeCode != "") $("#extable-ksu003").exTable(type, "middle", empId, "worktypeCode");
		if (worktypeName != "") $("#extable-ksu003").exTable(type, "middle", empId, "worktypeName");
		if (worktimeCode != "") $("#extable-ksu003").exTable(type, "middle", empId, "worktimeCode");
		if (worktimeName != "") $("#extable-ksu003").exTable(type, "middle", empId, "worktimeName");
		if (startTime1 != "") $("#extable-ksu003").exTable(type, "middle", empId, "startTime1");
		if (endTime1 != "") $("#extable-ksu003").exTable(type, "middle", empId, "endTime1");
		if (startTime2 != "") $("#extable-ksu003").exTable(type, "middle", empId, "startTime2");
		if (endTime2 != "") $("#extable-ksu003").exTable(type, "middle", empId, "endTime2");
	}

	export function setDisBackCell(type: any, css: any, worktypeCode: string, worktypeName: string, worktimeCode: string, worktimeName: string, startTime1: string, endTime1: string, startTime2: string, endTime2: string) {
		if (worktypeCode != "") $(css.cssWorkType).css("background-color", type);
		if (worktypeName != "") $(css.cssWorkTypeName).css("background-color", type);
		if (worktimeCode != "") $(css.cssWorkTime).css("background-color", type);
		if (worktimeName != "") $(css.cssWorkTName).css("background-color", type);
		if (startTime1 != "") $(css.cssStartTime1).css("background-color", type);
		if (endTime1 != "") $(css.cssEndTime1).css("background-color", type);
		if (startTime2 != "") $(css.cssStartTime2).css("background-color", type);
		if (endTime2 != "") $(css.cssEndTime2).css("background-color", type);
	}

	export function setCellValue(empId: any) {
		$("#extable-ksu003").exTable("cellValue", "middle", empId, "totalTime", "");
		$("#extable-ksu003").exTable("cellValue", "middle", empId, "startTime1", "");
		$("#extable-ksu003").exTable("cellValue", "middle", empId, "startTime2", "");
		$("#extable-ksu003").exTable("cellValue", "middle", empId, "endTime1", "");
		$("#extable-ksu003").exTable("cellValue", "middle", empId, "endTime2", "");
	}

	// setting show hide for middle
	export function showHide(showA9: any, indexBtnToLeft: any, targetInfor: any) {
		let margin = $(".ex-header-leftmost").width() - 12 + $(".ex-header-middle").width();
		let x = $('.ex-header-leftmost').width() + $('.ex-header-middle').width() + $('.ex-header-detail').width();
		$("#contents-area").css({ 'overflow-y': 'hidden' });
		if (indexBtnToLeft() % 2 == 0) {
			if (!showA9) 
			$("#extable-ksu003").exTable("showMiddle");
			
			$(".toLeft").css('margin-left', margin + 'px');
			$("#setting-time-grid").css("margin-left", x + 16 + 'px');
		} else {
			margin = $(".ex-header-leftmost").width() - 12	
			if (showA9) {
				$("#extable-ksu003").exTable("hideMiddle");
			}
			$(".toLeft").css("margin-left", margin + 'px');
			
			x = $('.ex-header-leftmost').width() + $('.ex-header-detail').width();
				$("#setting-time-grid").css("margin-left", x + 16 + 'px');
		}
	}

	export function buidDataReg(cellsGroup: any, targetInfor: any, employeeInfo: any, employeeIdLogin: any, colorBreak45: any, index045: any) {
		let dataReg: any = [];
		let groupData = _.groupBy(cellsGroup, 'rowIndex');
		let isBreakByHand = false;
		if (cellsGroup.length > 0) {
			_.forEach(groupData, function(cells: any) {
				if (cells.length > 0) {
					let cssbreakTime: string = targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (cells[0].rowIndex + 2).toString() + ")" + " > td:nth-child(10)" :
						"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (cells[0].rowIndex + 2).toString() + ")" + " > td:nth-child(8)";
					if (!_.isEqual($(cssbreakTime).css("background-color"), "rgb(255, 255, 255)") && !_.isEqual($(cssbreakTime).css("background-color"), "rgba(0, 0, 0, 0)")) {
						isBreakByHand = true;
						if (employeeInfo[cells[0].rowIndex].empId === employeeIdLogin) {
							if (_.isEqual($(cssbreakTime).css("background-color"), "rgb(206, 230, 255)")) isBreakByHand = false;
						} else {
							if (_.isEqual($(cssbreakTime).css("background-color"), "rgb(148, 183, 254)")) isBreakByHand = false;
						}
						if (colorBreak45 == true && index045 != -1) isBreakByHand = true;
					}
					
					if (employeeInfo[cells[0].rowIndex].workScheduleDto != null){
						let dataCell: any = {
							sid: employeeInfo[cells[0].rowIndex].empId,
							ymd: employeeInfo[cells[0].rowIndex].workInfoDto.date,
							workTypeCd: employeeInfo[cells[0].rowIndex].workScheduleDto.workTypeCode,
							workTimeCd: employeeInfo[cells[0].rowIndex].workScheduleDto.workTimeCode,
							startTime: employeeInfo[cells[0].rowIndex].workScheduleDto.startTime1,
							endTime: employeeInfo[cells[0].rowIndex].workScheduleDto.endTime1,
							startTime2: employeeInfo[cells[0].rowIndex].workScheduleDto.startTime2,
							endTime2: employeeInfo[cells[0].rowIndex].workScheduleDto.endTime2,
							listBreakTime: employeeInfo[cells[0].rowIndex].workScheduleDto.listBreakTimeZoneDto,
							directAtr: employeeInfo[cells[0].rowIndex].workInfoDto.directAtr,
							bounceAtr: employeeInfo[cells[0].rowIndex].workInfoDto.bounceAtr,
							isBreakByHand: isBreakByHand
						}
						dataReg.push(dataCell);
					}
					
				}
			});
		} else {
			let cssbreakTime: string = targetInfor == 1 ? "#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index045 + 2).toString() + ")" + " > td:nth-child(10)" :
				"#extable-ksu003 > .ex-body-middle > table > tbody tr:nth-child" + "(" + (index045 + 2).toString() + ")" + " > td:nth-child(8)";
			if (!_.isEqual($(cssbreakTime).css("background-color"), "rgb(255, 255, 255)") && !_.isEqual($(cssbreakTime).css("background-color"), "rgba(0, 0, 0, 0)")) {
				isBreakByHand = true;

				if (employeeInfo[index045].empId === employeeIdLogin) {
					if (_.isEqual($(cssbreakTime).css("background-color"), "rgb(206, 230, 255)")) isBreakByHand = false;
				} else {
					if (_.isEqual($(cssbreakTime).css("background-color"), "rgb(148, 183, 254)")) isBreakByHand = false;
				}
				if (colorBreak45 == true && index045 != -1) isBreakByHand = true;
			}
			let dataCell: any = null;
			if (index045 != -1) {
				dataCell = {
					sid: employeeInfo[index045].empId,
					ymd: employeeInfo[index045].workInfoDto.date,
					workTypeCd: employeeInfo[index045].workScheduleDto.workTypeCode,
					workTimeCd: employeeInfo[index045].workScheduleDto.workTimeCode,
					startTime: employeeInfo[index045].workScheduleDto.startTime1,
					endTime: employeeInfo[index045].workScheduleDto.endTime1,
					startTime2: employeeInfo[index045].workScheduleDto.startTime2 == 0 ? null : employeeInfo[index045].workScheduleDto.startTime2 ,
					endTime2: employeeInfo[index045].workScheduleDto.endTime2 == 0 ? null : employeeInfo[index045].workScheduleDto.endTime2,
					listBreakTime: employeeInfo[index045].workScheduleDto.listBreakTimeZoneDto,
					directAtr: employeeInfo[index045].workInfoDto.directAtr,
					bounceAtr: employeeInfo[index045].workInfoDto.bounceAtr,
					isBreakByHand: isBreakByHand
				}
			}
			dataReg.push(dataCell);
		}
		console.log(dataReg);
		return dataReg;
	}
	
	export function createColumnMid(){
		let middleColumns : any = [];
		if (__viewContext.viewModel.viewmodelA.dataScreen003A().targetInfor == 1) {
				middleColumns = [
					{
						headerText: getText('KSU003_23'), group: [
							{ headerText: "", key: "worktypeCode", width: "40px", handlerType: "input", dataType: "text", primitiveValue: "WorkTypeCode", required: true, textFormat: { length: 3, padSide: "left", padChar: '0' } },
							{
								headerText: "", key: "worktypeName", width: "38px", control: "link", primitiveValue: "WorkTypeName", css: { whiteSpace: "pre" }, handler: function(e: any) {
									__viewContext.viewModel.viewmodelA.openKdl003Dialog(e.worktypeCode, e.worktimeCode, e.empId, "WorkTypeName");
								}
							}]
					},
					{
						headerText: getText('KSU003_25'), group: [
							{ headerText: "", key: "worktimeCode", width: "40px", handlerType: "input", dataType: "text", primitiveValue: "WorkTimeCode", required: true, textFormat: { length: 3, padSide: "left", padChar: '0' } },
							{
								headerText: "", key: "worktimeName", width: "38px", control: "link", primitiveValue: "WorkTimeName", css: { whiteSpace: "pre" }, handler: function(e: any) {
									__viewContext.viewModel.viewmodelA.openKdl003Dialog(e.worktypeCode, e.worktimeCode, e.empId, "WorkTimeName");
								}
							}]
					},
					{
						headerText: getText('KSU003_27'), group: [
							{ headerText: "", key: "startTime1", width: "41px", handlerType: "input", dataType: "duration", primitiveValue: "TimeWithDayAttr", required: true }]
					},
					{
						headerText: getText('KSU003_28'), group: [
							{ headerText: "", key: "endTime1", width: "41px", handlerType: "input", dataType: "duration", primitiveValue: "TimeWithDayAttr", required: true }]
					},
					{
						headerText: getText('KSU003_29'), group: [
							{ headerText: "", key: "startTime2", width: "41px", handlerType: "input", dataType: "duration", primitiveValue: "TimeWithDayAttr", required: true }]
					},
					{
						headerText: getText('KSU003_30'), group: [
							{ headerText: "", key: "endTime2", width: "41px", handlerType: "input", dataType: "duration", primitiveValue: "TimeWithDayAttr", required: true }]
					},
					{
						headerText: getText('KSU003_31'), group: [
							{ headerText: "", key: "totalTime", width: "40px", dataType: "duration", primitiveValue: "TimeWithDayAttr" }]
					},
					{
						headerText: getText('KSU003_32'), group: [
							{ headerText: "", key: "breaktime", width: "39px", dataType: "duration", primitiveValue: "TimeWithDayAttr" }]
					}
				];
			} else {
				middleColumns = [
					{
						headerText: getText('KSU003_23'), group: [
							{ headerText: "", key: "worktypeCode", width: "40px", handlerType: "input", dataType: "text", primitiveValue: "WorkTypeCode", required: true, textFormat: { length: 3, padSide: "left", padChar: '0' } },
							{
								headerText: "", key: "worktypeName", width: "38px", control: "link", primitiveValue: "WorkTypeName", css: { whiteSpace: "pre" }, handler: function(e: any) {
									__viewContext.viewModel.viewmodelA.openKdl003Dialog(e.worktypeCode, e.worktimeCode, e.empId, "WorkTypeName");
								}
							}]
					},
					{
						headerText: getText('KSU003_25'), group: [
							{ headerText: "", key: "worktimeCode", width: "40px", handlerType: "input", dataType: "text", primitiveValue: "WorkTimeCode", required: true, textFormat: { length: 3, padSide: "left", padChar: '0' } },
							{
								headerText: "", key: "worktimeName", width: "38px", control: "link", primitiveValue: "WorkTimeName", css: { whiteSpace: "pre" }, handler: function(e: any) {
									__viewContext.viewModel.viewmodelA.openKdl003Dialog(e.worktypeCode, e.worktimeCode, e.empId, "WorkTimeName");
								}
							}]
					},
					{
						headerText: getText('KSU003_27'), group: [
							{ headerText: "", key: "startTime1", width: "41px", handlerType: "input", dataType: "duration", primitiveValue: "TimeWithDayAttr", required: true }]
					},
					{
						headerText: getText('KSU003_28'), group: [
							{ headerText: "", key: "endTime1", width: "41px", handlerType: "input", dataType: "duration", primitiveValue: "TimeWithDayAttr", required: true }]
					},

					{
						headerText: getText('KSU003_31'), group: [
							{ headerText: "", key: "totalTime", width: "39px", dataType: "duration", primitiveValue: "TimeWithDayAttr" }]
					},
					{
						headerText: getText('KSU003_32'), group: [
							{ headerText: "", key: "breaktime", width: "39px", dataType: "duration", primitiveValue: "TimeWithDayAttr" }]
					}
				];
			}
			
			return middleColumns;
	}

	export function addColumn(index: any, y: any, detailColumns: any, detailHeaderDs: any, detailHeaders: any, width: any) {
		detailColumns[index] = { key: (y).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
		detailColumns[index += 1] = { key: (y += 1).toString() + "_", width: width, headerText: "", visible: true };
	}

	export function removeError(cssWorkType: any, cssWorkTime: any, cssWorkTypeName: any, cssWorkTName: any, cssStartTime1: any, cssEndTime1: any, cssStartTime2: any, cssEndTime2: any, type: number) {
		if (type == 1) {
			$(cssWorkType).removeClass("x-error");
			$(cssWorkTime).removeClass("x-error");
			$(cssWorkTypeName).removeClass("x-error");
			$(cssWorkTName).removeClass("x-error");
		}
		$(cssStartTime1).removeClass("x-error");
		$(cssEndTime1).removeClass("x-error");
		$(cssStartTime2).removeClass("x-error");
		$(cssEndTime2).removeClass("x-error");
	}

	export class DataScreenA {
		startDate: string; // 開始日		
		endDate: string; // 終了日
		/** 基準の組織 */
		unit: number;
		id: string;
		name: string;
		timeCanEdit: string; //いつから編集可能か
		/** 複数回勤務管理 */
		targetInfor: number;//対象情報 : 複数回勤務 (1 :true,0:false)
		canModified: number;//修正可能 CanModified
		scheCorrection: Array<number>;//スケジュール修正の機能制御  WorkTimeForm
		employeeInfo: Array<DisplayWorkInfoByDateDto>;
		constructor(
			startDate: string,
			endDate: string,
			unit: number,
			id: string,
			name: string,
			timeCanEdit: string,
			targetInfor: number,
			canModified: number,
			scheCorrection: Array<number>,
			employeeInfo: Array<DisplayWorkInfoByDateDto>) {
			let self = this;
			self.startDate = startDate;
			self.endDate = endDate;
			self.unit = unit;
			self.id = id;
			self.name = name;
			self.timeCanEdit = timeCanEdit;
			self.targetInfor = targetInfor;
			self.canModified = canModified;
			self.scheCorrection = scheCorrection;
			self.employeeInfo = employeeInfo;
		}
	}

	export class A6Data {
		startTime1: KnockoutObservable<number>; //開始時刻１
		endTime1: KnockoutObservable<number>; //終了時刻１
		startTime2: KnockoutObservable<number>; //開始時刻2
		endTime2: KnockoutObservable<number>; //終了時刻2
		listBreakTimeZoneDto: KnockoutObservableArray<TimeSpanForCalcDto> //List<休憩時間帯>
		workTypeCode: KnockoutObservable<string>;
		workTimeCode: KnockoutObservable<string>;//社員コード
		workTimeName: KnockoutObservable<string>;//社員名称   
		constructor(
			startTime1: number,
			endTime1: number,
			startTime2: number,
			endTime2: number,
			listBreakTimeZoneDto: Array<TimeSpanForCalcDto>,
			workTypeCode: string,
			workTimeCode: string,
			workTimeName: string) {
			let self = this;
			self.startTime1 = ko.observable(startTime1);
			self.endTime1 = ko.observable(endTime1);
			self.startTime2 = ko.observable(startTime2);
			self.endTime2 = ko.observable(endTime2);
			self.listBreakTimeZoneDto = ko.observableArray(listBreakTimeZoneDto);
			self.workTypeCode = ko.observable(workTypeCode);
			self.workTimeCode = ko.observable(workTimeCode);
			self.workTimeName = ko.observable(workTimeName);
		}
	}

	export interface A6 {
		startTime1: number; //開始時刻１
		endTime1: number; //終了時刻１
		startTime2: number; //開始時刻2
		endTime2: number; //終了時刻2
		listBreakTimeZoneDto: Array<TimeSpanForCalcDto> //List<休憩時間帯>
		workTypeCode: string;//勤務種類コード
		workTimeCode: string;//就業時間帯コード
		workTimeName: string; //就業時間帯名称
	}

	export class EmployeeInfor {
		employeeId: string;
		employeeCode: string;//社員コード
		employeeName: string;//社員名称   
		employeeWorkInfoDto: EmployeeWorkInfoDto;//対象社員の社員勤務情報dto
		employeeWorkScheduleDto: EmployeeWorkScheduleDto;//対象社員の社員勤務予定dto
		fixedWorkInforDto: FixedWorkInforDto;//対象社員の勤務固定情報dto
		constructor(
			employeeId: string,
			employeeCode: string,
			employeeName: string,
			employeeWorkInfoDto: EmployeeWorkInfoDto,
			employeeWorkScheduleDto: EmployeeWorkScheduleDto,
			fixedWorkInforDto: FixedWorkInforDto) {
			let self = this;
			self.employeeId = employeeId;
			self.employeeCode = employeeCode;
			self.employeeName = employeeName;
			self.employeeWorkInfoDto = employeeWorkInfoDto;
			self.employeeWorkScheduleDto = employeeWorkScheduleDto;
			self.fixedWorkInforDto = fixedWorkInforDto;
		}
	}

	export class DisplayWorkInfoByDateDto {
		empId: string;
		workInfoDto: EmployeeWorkInfoDto; /** 社員勤務情報　dto */
		workScheduleDto: EmployeeWorkScheduleDto; /** 社員勤務予定　dto */
		fixedWorkInforDto: FixedWorkInforDto; /** 勤務固定情報　dto */
		empTaskInfoDto : any;
		constructor(empId: string,
			workInfoDto: EmployeeWorkInfoDto,
			workScheduleDto: EmployeeWorkScheduleDto,
			fixedWorkInforDto: FixedWorkInforDto, 
			empTaskInfoDto : any) {
			this.empId = empId;
			this.workInfoDto = workInfoDto;
			this.workScheduleDto = workScheduleDto;
			this.fixedWorkInforDto = fixedWorkInforDto;
			this.empTaskInfoDto = empTaskInfoDto;
		}
	}


	export class InforScreenADto {
		detailContentDs: any;
		daySelect: string;
		// 対象期間
		startDate: string;
		endDate: string;
		// 基準の組織
		unit: number;
		workplaceId: string;
		workplaceGroupId: string;
		workplaceName: string;
		// 社員情報
		listEmp: Array<EmployeeInfo>;
		// いつから編集可能か
		dayEdit: string;
		constructor(
			detailContentDs: any,
			daySelect: string,
			startDate: string,
			endDate: string,
			unit: number,
			workplaceId: string,
			workplaceGroupId: string,
			workplaceName: string,
			// 社員情報
			listEmp: Array<EmployeeInfo>,
			// いつから編集可能か
			dayEdit: string) {
			let self = this;
			self.detailContentDs = detailContentDs;
			self.daySelect = daySelect;
			self.startDate = startDate;
			self.endDate = endDate;
			self.unit = unit;
			self.workplaceId = workplaceId;
			self.workplaceGroupId = workplaceGroupId;
			self.workplaceName = workplaceName;
			self.listEmp = listEmp;
			self.dayEdit = dayEdit;
		}
	}

	export class EmployeeInfo {
		id: string;
		code: string;//社員コード
		name: string;//社員名称   
		constructor(
			id: string,
			code: string,
			name: string) {
			let self = this;
			self.id = id;
			self.code = code;
			self.name = name;
		}
	}

	/**
	* 社員情報
	*/
	export class DataFrom045 {
		workScheduleDto: EmployeeWorkScheduleDto;//対象社員の社員勤務予定dto
		fixedWorkInforDto: FixedWorkInforDto;//対象社員の勤務固定情報dto
		workInfoDto: EmployeeWorkInfoDto
		constructor(
			workScheduleDto: EmployeeWorkScheduleDto,
			fixedWorkInforDto: FixedWorkInforDto,
			workInfoDto: EmployeeWorkInfoDto) {
			let self = this;
			self.workScheduleDto = workScheduleDto;
			self.fixedWorkInforDto = fixedWorkInforDto;
			self.workInfoDto = workInfoDto
		}
	}


	export class ParamKsu003 {
		employeeInfo: EmployeeInfor;//社員情報
		targetInfor: number;//対象情報 : 複数回勤務 (1 :true,0:false)
		canModified: number;//修正可能 CanModified
		scheCorrection: Array<number>;//スケジュール修正の機能制御  WorkTimeForm
		unit: number;
		targetId: string;
		workplaceName: string;
		constructor(employeeInfo: EmployeeInfor,
			targetInfor: number,
			canModified: number,
			scheCorrection: Array<number>,
			unit: number,
			targetId: string,
			workplaceName: string) {
			let self = this;
			self.employeeInfo = employeeInfo;
			self.targetInfor = targetInfor;
			self.canModified = canModified;
			self.scheCorrection = scheCorrection;
			self.unit = unit;
			self.targetId = targetId;
			self.workplaceName = workplaceName;

		}
	}

	/**
	 * 社員情報
	 */
	export class EmployeeInformation {
		employeeCode: string;//社員コード
		employeeName: string;//社員名称   
		employeeWorkInfoDto: EmployeeWorkInfoDto;//対象社員の社員勤務情報dto
		employeeWorkScheduleDto: EmployeeWorkScheduleDto;//対象社員の社員勤務予定dto
		fixedWorkInforDto: FixedWorkInforDto;//対象社員の勤務固定情報dto
		constructor(employeeCode: string,
			employeeName: string,
			employeeWorkInfoDto: EmployeeWorkInfoDto,
			employeeWorkScheduleDto: EmployeeWorkScheduleDto,
			fixedWorkInforDto: FixedWorkInforDto) {
			let self = this;
			self.employeeCode = employeeCode;
			self.employeeName = employeeName;
			self.employeeWorkInfoDto = employeeWorkInfoDto;
			self.employeeWorkScheduleDto = employeeWorkScheduleDto;
			self.fixedWorkInforDto = fixedWorkInforDto;
		}
	}

	// interface : 対象社員の社員勤務情報dto - 社員勤務情報dto
	export interface IEmployeeWorkInfoDto {
		isCheering: number; //応援か : SupportCategory
		isConfirmed: number; //確定済みか 0:false or 1:true
		bounceAtr: number; //直帰区分 0:false or 1:true
		directAtr: number; //直行区分 0:false or 1:true
		isNeedWorkSchedule: number;//勤務予定が必要か 0:false or 1:true
		employeeId: string;//社員ID
		date: string;//年月日
		//optional.empty;
		supportTimeZone: TimeZoneDto;//応援時間帯
		wkpNameSupport: string;//応援先の職場名称
		listTimeVacationAndType: Array<TimeVacationAndType> //Map<時間休暇種類, 時間休暇>
		shiftCode: string;//シフトコード
		shiftName: string;//シフト名称
		shortTime: Array<TimeShortDto>;//List<育児介護短時間帯>
	}

	//対象社員の社員勤務情報dto - 社員勤務情報dto
	export class EmployeeWorkInfoDto {
		isCheering: number; //応援か : SupportAtr
		isConfirmed: number; //確定済みか 0:false or 1:true
		bounceAtr: number; //直帰区分 0:false or 1:true
		directAtr: number; //直行区分 0:false or 1:true
		isNeedWorkSchedule: number;//勤務予定が必要か 0:false or 1:true
		employeeId: string;//社員ID
		date: string;//年月日
		//optional.empty;
		supportTimeZone: TimeZoneDto;//応援時間帯
		wkpNameSupport: string;//応援先の職場名称
		listTimeVacationAndType: Array<TimeVacationAndType> //Map<時間休暇種類, 時間休暇>
		shiftCode: string;//シフトコード
		shiftName: string;//シフト名称
		shortTime: Array<TimeShortDto>;//List<育児介護短時間帯>
		constructor(param: IEmployeeWorkInfoDto) {
			let self = this;
			self.isCheering = param.isCheering;
			self.isConfirmed = param.isConfirmed;
			self.bounceAtr = param.bounceAtr;
			self.directAtr = param.directAtr;
			self.isNeedWorkSchedule = param.isNeedWorkSchedule;
			self.employeeId = param.employeeId;
			self.date = param.date;
			self.supportTimeZone = param.supportTimeZone;
			self.wkpNameSupport = param.wkpNameSupport;
			self.listTimeVacationAndType = param.listTimeVacationAndType;
			self.shiftCode = param.shiftCode;
			self.shiftName = param.shiftName;
			self.shortTime = param.shortTime;
		}
	}

	//時間休暇
	export class TimeVacationDto {
		timeZone: Array<TimeSpanForCalcDto>; //時間帯リスト
		usageTime: DailyAttdTimeVacationDto;//使用時間
		constructor(timeZone: Array<TimeSpanForCalcDto>,
			usageTime: DailyAttdTimeVacationDto) {
			this.timeZone = timeZone;
			this.usageTime = usageTime;
		}
	}

	//時間帯(実装コードなし/使用不可)
	export class TimeZoneDto {
		startTime: TimeOfDayDto;//開始時刻 
		endTime: TimeOfDayDto;//終了時刻
		constructor(startTime: TimeOfDayDto,
			endTime: TimeOfDayDto) {
			this.startTime = startTime;
			this.endTime = endTime;
		}
	}

	export class TimeShortDto {
		startTime: number;//開始時刻 
		endTime: number;//終了時刻
		dayDivision: number;
		workNo: number;
		constructor(startTime: number,
			endTime: number,
			dayDivision: number,
			workNo: number) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.dayDivision = dayDivision;
			this.workNo = workNo;
		}
	}

	//時刻(日区分付き)
	export class TimeOfDayDto {
		dayDivision: number;//日区分   : DayDivision
		time: number;//時刻
		constructor(dayDivision: number,
			time: number) {
			this.dayDivision = dayDivision;
			this.time = time;
		}
	}
	//日別勤怠の時間休暇使用時間
	export class DailyAttdTimeVacationDto {
		timeAbbyakLeave: number; //時間年休使用時間
		timeOff: number;//時間代休使用時間
		excessPaidHoliday: number;//超過有休使用時間
		specialHoliday: number;//特別休暇使用時間
		frameNO: number;//特別休暇枠NO
		childNursingLeave: number;//子の看護休暇使用時間
		nursingCareLeave: number;//介護休暇使用時間
		constructor(timeAbbyakLeave: number,
			timeOff: number,
			excessPaidHoliday: number,
			specialHoliday: number,
			frameNO: number,
			childNursingLeave: number,
			nursingCareLeave: number) {
			this.timeAbbyakLeave = timeAbbyakLeave;
			this.timeOff = timeOff;
			this.excessPaidHoliday = excessPaidHoliday;
			this.specialHoliday = specialHoliday;
			this.frameNO = frameNO;
			this.childNursingLeave = childNursingLeave;
			this.nursingCareLeave = nursingCareLeave;
		}
	}

	//Map<時間休暇種類, 時間休暇>
	export class TimeVacationAndType {
		typeVacation: number; //時間休暇種類 : TimeVacationType
		timeVacation: TimeVacationDto; //時間休暇 
		constructor(typeVacation: number,
			timeVacation: TimeVacationDto) {
			this.typeVacation = typeVacation;
			this.timeVacation = timeVacation;
		}
	}

	// interface : 対象社員の社員勤務予定dto - 社員勤務予定dto
	export interface IEmployeeWorkScheduleDto {
		startTime1: number; //開始時刻１
		startTime1Status: number; //開始時刻１編集状態 : EditStateSetting
		endTime1: number; //終了時刻１
		endTime1Status: number; //終了時刻１編集状態 : EditStateSetting
		startTime2: number; //開始時刻2
		startTime2Status: number; //開始時刻2編集状態 : EditStateSetting
		endTime2: number; //終了時刻2
		endTime2Status: number; //終了時刻2編集状態 : EditStateSetting
		listBreakTimeZoneDto: Array<TimeSpanForCalcDto> //List<休憩時間帯>
		workTypeCode: string;//勤務種類コード
		breakTimeStatus: number;//休憩時間帯編集状態 : EditStateSetting
		workTypeStatus: number;//勤務種類編集状態 : EditStateSetting
		workTimeCode: string;//就業時間帯コード
		workTimeStatus: number;//就業時間帯編集状態 : EditStateSetting
	}

	// 対象社員の社員勤務予定dto - 社員勤務予定dto
	export class EmployeeWorkScheduleDto {
		startTime1: number; //開始時刻１
		startTime1Status: number; //開始時刻１編集状態 : EditStateSetting
		endTime1: number; //終了時刻１
		endTime1Status: number; //終了時刻１編集状態 : EditStateSetting
		startTime2: number; //開始時刻2
		startTime2Status: number; //開始時刻2編集状態 : EditStateSetting
		endTime2: number; //終了時刻2
		endTime2Status: number; //終了時刻2編集状態 : EditStateSetting
		listBreakTimeZoneDto: Array<TimeSpanForCalcDto> //List<休憩時間帯> (EA : List＜休憩時間帯＞＝勤務予定．休憩時間帯) 
		workTypeCode: string;//勤務種類コード
		breakTimeStatus: number;//休憩時間帯編集状態 : EditStateSetting
		workTypeStatus: number;//勤務種類編集状態 : EditStateSetting
		workTimeCode: string;//就業時間帯コード
		workTimeStatus: number;//就業時間帯編集状態 : EditStateSetting
		constructor(param: IEmployeeWorkScheduleDto) {
			let self = this;
			self.startTime1 = param.startTime1;
			self.startTime1Status = param.startTime1Status;
			self.endTime1 = param.endTime1;
			self.endTime1Status = param.endTime1Status;
			self.startTime2 = param.startTime2;
			self.startTime2Status = param.startTime2Status;
			self.endTime2 = param.endTime2;
			self.endTime2Status = param.endTime2Status;
			self.listBreakTimeZoneDto = param.listBreakTimeZoneDto;
			self.workTypeCode = param.workTypeCode;
			self.breakTimeStatus = param.breakTimeStatus;
			self.workTypeStatus = param.workTypeStatus;
			self.workTimeCode = param.workTimeCode;
			self.workTimeStatus = param.workTimeStatus;
		}
	}

	/**
	 * 日別勤怠の休憩時間帯   (EA : List＜休憩時間帯＞＝勤務予定．休憩時間帯) 
	 */
	export class TimeSpanForCalcDto {
		start: number; //開始 - 勤怠打刻(実打刻付き)
		end: number; //終了 - 勤怠打刻(実打刻付き)
		constructor(start: number,
			end: number) {
			this.start = start;
			this.end = end;

		}
	}


	/**
	 * 時間帯
	 */
	export class BreakTimeZoneDto {
		breakFrameNo: number;//休憩枠NO
		startTime: number; //開始 - 勤怠打刻(実打刻付き)
		endTime: number; //終了 - 勤怠打刻(実打刻付き)
		breakTime: number;//休憩時間: 勤怠時間 
		constructor(breakFrameNo: number,
			startTime: number,
			endTime: number,
			breakTime: number) {
			this.breakFrameNo = breakFrameNo;
			this.startTime = startTime;
			this.endTime = endTime;
			this.breakTime = breakTime;
		}
	}

	// interface : 対象社員の勤務固定情報dto - 勤務固定情報dto
	export interface IFixedWorkInforDto {
		workTimeName: string; //就業時間帯名称
		coreStartTime: number; //コア開始時刻
		coreEndTime: number; //コア終了時刻
		overtimeHours: Array<ChangeableWorkTime>; //List<残業時間帯>
		startTimeRange1: TimeZoneDto; //日付開始時刻範囲時間帯1
		endTimeRange1: TimeZoneDto;//日付終了時刻範囲時間帯1
		workTypeName: string;//勤務種類名称
		startTimeRange2: TimeZoneDto; //日付開始時刻範囲時間帯2
		endTimeRange2: TimeZoneDto;//日付終了時刻範囲時間帯2
		fixBreakTime: any; //休憩時間帯を固定にする (0:false 1:true)
		workType: number;//勤務タイプ : WorkTimeForm
		isHoliday: boolean;// 休日か : SetupType
		isNeedWorkTime: string;// 就業時間帯が不要 : 必須任意不要区分
	}

	export class FixedWork {
		empId: string;
		fixedWorkInforDto: FixedWorkInforDto;
		constructor(empId: string,
			fixedWorkInforDto: FixedWorkInforDto) {
			this.empId = empId;
			this.fixedWorkInforDto = fixedWorkInforDto;

		}
	}

	// 対象社員の勤務固定情報dto - 勤務固定情報dto
	export class FixedWorkInforDto {
		workTimeName: string; //就業時間帯名称
		coreStartTime: number; //コア開始時刻
		coreEndTime: number; //コア終了時刻
		overtimeHours: Array<ChangeableWorkTime>; //List<残業時間帯>
		startTimeRange1: TimeZoneDto; //日付開始時刻範囲時間帯1
		endTimeRange1: TimeZoneDto;//日付終了時刻範囲時間帯1
		workTypeName: string;//勤務種類名称
		startTimeRange2: TimeZoneDto; //日付開始時刻範囲時間帯2
		endTimeRange2: TimeZoneDto;//日付終了時刻範囲時間帯2
		fixBreakTime: any; //休憩時間帯を固定にする (0:false 1:true)
		workType: number;//勤務タイプ : WorkTimeForm
		isHoliday: boolean; // 休日か : SetupType
		isNeedWorkTime: string; // 就業時間帯が不要 : 必須任意不要区分
		constructor(param: IFixedWorkInforDto) {
			let self = this;
			self.workTimeName = param.workTimeName;
			self.coreStartTime = param.coreStartTime;
			self.coreEndTime = param.coreEndTime;
			self.overtimeHours = param.overtimeHours;
			self.startTimeRange1 = param.startTimeRange1;
			self.endTimeRange1 = param.endTimeRange1;
			self.workTypeName = param.workTypeName;
			self.startTimeRange2 = param.startTimeRange2;
			self.endTimeRange1 = param.endTimeRange1;
			self.fixBreakTime = param.fixBreakTime;
			self.workType = param.workType;
			self.isHoliday = param.isHoliday;
			self.isNeedWorkTime = param.isNeedWorkTime;
		}
	}

	/**
	 * 勤務NOごとの変更可能な勤務時間帯
	 */
	export class ChangeableWorkTime {
		workNo: number;//勤務NO
		startTime: number;//開始時刻の変更可能な時間帯
		endTime: number;//終了時刻の変更可能な時間帯
		constructor(startTime: number,
			endTime: number) {
			this.startTime = startTime;
			this.endTime = endTime;
		}
	}

	export class GetInfoInitStartKsu003Dto {
		byDateDto: DisplaySettingByDateDto = new DisplaySettingByDateDto(0, 0, 0); //スケジュール修正日付別の表示設定
		displayInforOrganization: DisplayInfoOrganizationDto = new DisplayInfoOrganizationDto("", "", "", "", ""); // 組織の表示情報
		manageMultiDto: WorkManageMultiDto = new WorkManageMultiDto("", 0); // 複数回勤務管理
		functionControlDto: ScheFunctionControlDto = new ScheFunctionControlDto([], 0,0, []); // スケジュール修正の機能制御 
		taskOperationMethod: number;
		constructor(byDateDto: DisplaySettingByDateDto,
			displayInforOrganization: DisplayInfoOrganizationDto,
			manageMultiDto: WorkManageMultiDto,
			functionControlDto: ScheFunctionControlDto,
			taskOperationMethod: number) {
			this.byDateDto = byDateDto;
			this.displayInforOrganization = displayInforOrganization;
			this.manageMultiDto = manageMultiDto;
			this.functionControlDto = functionControlDto;
			this.taskOperationMethod = taskOperationMethod;
		}
	}
	// スケジュール修正日付別の表示設定
	export class DisplaySettingByDateDto {
		dispRange: number; /** 表示範囲 */
		dispStart: number; /** 開始時刻 */
		initDispStart: number;  /** スケジュール修正日付別の表示設定 */
		constructor(dispRange: number,
			dispStart: number,
			initDispStart: number) {
			this.dispRange = dispRange;
			this.dispStart = dispStart;
			this.initDispStart = initDispStart;
		}
	}

	// 組織の表示情報
	export class DisplayInfoOrganizationDto {
		designation: string; /** 呼称 **/
		code: string; /** コード **/
		name: string;  /** 名称 **/
		displayName: string; /** 表示名 **/
		genericTerm: string;  /** 呼称 **/
		constructor(designation: string,
			code: string,
			name: string,
			displayName: string,
			genericTerm: string) {
			this.designation = designation;
			this.code = code;
			this.name = name;
			this.displayName = displayName;
			this.genericTerm = genericTerm;
		}
	}

	// 複数回勤務管理
	export class WorkManageMultiDto {
		companyID: string; /** 会社ID */
		useATR: number; /** 使用区分 */
		constructor(companyID: string,
			useATR: number) {
			this.companyID = companyID;
			this.useATR = useATR;
		}
	}

	// スケジュール修正の機能制御 
	export class ScheFunctionControlDto {
		changeableWorks: Array<number>; /** 時刻修正できる勤務形態 */
		useATR: number; /** 実績表示できるか */
		displayWorkTypeControl: number; /** 表示可能勤務種類制御 */
		displayableWorkTypeCodeList: Array<string>; /** 表示可能勤務種類リスト */
		constructor(changeableWorks: Array<number>,
			useATR: number, displayWorkTypeControl: number, 
			displayableWorkTypeCodeList: Array<string>) {
			this.changeableWorks = changeableWorks;
			this.useATR = useATR;
			this.displayWorkTypeControl = displayWorkTypeControl;
			this.displayableWorkTypeCodeList = displayableWorkTypeCodeList;
		}
	}



	/**
	* 時間休暇種類
	*/
	export enum TimeVacationType {
		/**
		 * 出勤前
		 */
		ATWORK = 0,
		/**
		 * 退勤後
		 */
		OFFWORK = 1,
		/**
		 * 出勤前2
		 */
		ATWORK2 = 2,
		/**
		 * 退勤後2
		 */
		OFFWORK2 = 3,
		/**
		 * 私用外出
		 */
		PRIVATE = 4,
		/**
		 * 組合外出
		 */
		UNION = 5
	}

	/**
	 * 日区分
	 */
	export enum DayDivision {
		/**
		* 当日
		*/
		THIS_DAY = 0,
		/**
		 * 翌日
		 */
		NEXT_DAY = 1,
		/**
		 * 翌々日
		 */
		TW0_DAY_LATE = 2,
		/**
		 * 前日
		 */
		DAY_BEFORE = 3
	}

	/**
	 * 修正可能
	 */
	export enum CanModified {
		/**
		* 参照
		*/
		REFERENCE = 0,
		/**
		 * 修正
		 */
		FIX = 1
	}

	export interface IFixedFlowFlexTime {
		timeNo: number;
		empId: string;
		color: string;
		// 社員勤務情報　dto
		isCheering: number; //応援か : SupportAtr

		// 勤務固定情報　dto
		workType: number;//勤務タイプ : WorkTimeForm

		// 社員勤務予定　dto
		startTime: number; //開始時刻１
		endTime: number; //終了時刻１

		// 勤務固定情報　dto
		startTimeRange: model.TimeZoneDto; //日付開始時刻範囲時間帯1
		endTimeRange: model.TimeZoneDto;//日付終了時刻範囲時間帯1

	}

	export interface IBreakTime {
		empId: string;
		color: string;
		lstBreakTime: Array<model.TimeSpanForCalcDto>;
		fixBreakTime: number;
	}

	export interface IOverTime {
		empId: string;
		color: string;
		lstOverTime: Array<ChangeableWorkTime>;

	}

	export interface ICoreTime {
		empId: string;
		color: string;
		workType: number;//勤務タイプ : WorkTimeForm
		coreStartTime: number; //コア開始時刻
		coreEndTime: number; //コア終了時刻
	}

	export interface ILocalStore {
		startTimeSort ?: string;
		showWplName ?: boolean;
		operationUnit ?: string;
		displayFormat ?: number;
		showHide ?: number;
		lstEmpIdSort ?: Array<any>;
		workSelection ?: number;
		work1Selection ?: string;
		pageNo ?: number;
		workPalletDetails ?: any; 
	}

	export interface IHolidayTime {
		empId: string;
		color: string;
		listTimeVacationAndType: Array<TimeVacationAndType>;
	}

	export interface IShortTime {
		empId: string;
		color: string;
		listShortTime: Array<TimeShortDto>;
	}

	export class CellColor {
		columnKey: any;
		rowId: any;
		innerIdx: any;
		clazz: any;
		constructor(columnKey: any, rowId: any, clazz: any, innerIdx?: any) {
			this.columnKey = columnKey;
			this.rowId = rowId;
			this.innerIdx = innerIdx;
			this.clazz = clazz;
		}
	}

	export enum SupportAtr {
		NOT_CHEERING = 1,
		TIMEZONE_SUPPORTER = 2,
		TIMEZONE_RESPONDENT = 3,
		ALL_DAY_SUPPORTER = 4,
		ALL_DAY_RESPONDENT = 5
	}

	export enum SetupType {
		REQUIRED = "REQUIRED",
		OPTIONAL = "OPTIONAL",
		NOT_REQUIRED = "NOT_REQUIRED"
	}

	export enum EditStateSetting {
		HAND_CORRECTION_MYSELF = 0,
		HAND_CORRECTION_OTHER = 1,
		REFLECT_APPLICATION = 2,
		IMPRINT = 3
	}

	export enum WorkTimeForm {
		FIXED = 0,
		FLEX = 1,
		FLOW = 2,
		TIMEDIFFERENCE = 3
	}
	
	export class ItemModel {
		code: string;
		name: string;
		constructor(code: string, name: string) {
			this.code = code;
			this.name = name;
		}
	}
	
	export class RangeModel {
		name : string;
		code: number;
		constructor(name : string,code: number) {
			this.name = name;
			this.code = code;
		}
	}

	export interface ITimeGantChart {
		empId: string,
		typeOfTime: string,
		gantCharts: number,
		gcFixedWorkTime: Array<model.IFixedFlowFlexTime>,
		gcBreakTime: Array<model.IBreakTime>,
		gcOverTime: Array<model.IOverTime>,
		gcSupportTime: any,
		gcFlowTime: Array<model.IFixedFlowFlexTime>,
		gcFlexTime: Array<model.IFixedFlowFlexTime>,
		gcCoreTime: Array<model.ICoreTime>,
		gcHolidayTime: Array<model.IHolidayTime>,
		gcShortTime: Array<model.IShortTime>;
		gcTaskTime : any;
	};

    export interface IEmpidName {
        empId: string,
        name: string,
        code: string
    }

    export interface IError {
        sid: string,
        scd: string,
        empName: string,
        date: string,
        attendanceItemId: string,
        errorMessage: string,
    }

    export class InforError {
        sid: string;
        scd: string;
        empName: string;
        date: string;
        attendanceItemId: string;
        errorMessage: string;
        constructor(param: IError) {
            let self = this;
            self.sid = param.sid;
            self.scd = param.scd;
            self.empName = param.empName;
            self.date = param.date;
            self.attendanceItemId = param.attendanceItemId;
            self.errorMessage = param.errorMessage;
        }
    }

	/*service.sortEmployee(param)
	.done((data: Array<model.DisplayWorkInfoByDateDto>) => {
		self.lstEmpId = self.lstEmpId.sort(function(a: any, b: any) {
			return _.findIndex(data, x => { return x == a.empId }) - _.findIndex(data, x => { return x == b.empId });
		});
		self.localStore().lstEmpIdSort = self.lstEmpId;
		characteristics.save(self.KEY, self.localStore());
		self.destroyAndCreateGrid(self.lstEmpId, 1);
	}).fail(function(error) {
		errorDialog({ messageId: error.messageId });
	}).always(function() {
	});*/

}