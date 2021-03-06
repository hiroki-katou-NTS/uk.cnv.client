module nts.uk.at.view.ktg004.a.viewmodel {
    import block = nts.uk.ui.block;
	import ajax = nts.uk.request.ajax;
    import getText = nts.uk.resource.getText;
	import windows = nts.uk.ui.windows;

    export var STORAGE_KEY_TRANSFER_DATA = "nts.uk.request.STORAGE_KEY_TRANSFER_DATA";

	const KTG004_API = {
		GET_DATA: 'screen/at/ktg004/getData'
	};
	
    export class ScreenModel {
        name = ko.observable(''); 
		selectedSwitch = ko.observable(1);
		itemsSetting: KnockoutObservableArray<any> = ko.observableArray([]);
		attendanceInfor = new AttendanceInfor();
		remainingNumberInfor = new RemainingNumberInfor();
		vacationSetting = new VacationSetting();
		detailedWorkStatusSettings = ko.observable(false);
		specialHolidaysRemainings: KnockoutObservableArray<SpecialHolidaysRemainings> = ko.observableArray([]);

		show: KnockoutObservable<boolean> = ko.observable(false);

        public startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
			var cacheCcg008 = windows.getShared("cache");
            if (!cacheCcg008 || !cacheCcg008.currentOrNextMonth) {
                self.selectedSwitch(1);
            }
            else {
                self.selectedSwitch(cacheCcg008.currentOrNextMonth);
            }
			block.grayout();
            ajax("at", KTG004_API.GET_DATA, {topPageYearMonthEnum: self.selectedSwitch()}).done(function(data: any){
				self.name(data.name || "");
				self.detailedWorkStatusSettings(data.detailedWorkStatusSettings);
				self.itemsSetting(data.itemsSetting);
				self.attendanceInfor.update(data.attendanceInfor);
				self.remainingNumberInfor.update(data.remainingNumberInfor);
				self.vacationSetting = data.vacationSetting;
				let tg: SpecialHolidaysRemainings[] = [];
				_.forEach(data.remainingNumberInfor.specialHolidaysRemainings, function(c){
					tg.push(new SpecialHolidaysRemainings(c));
				});
				self.specialHolidaysRemainings(tg);
				let show = _.filter(data.itemsSetting, { 'displayType': true });
				if(show && show.length > 14){
					$("#scrollTable").addClass("scroll");
					$(".widget-table td").each(function() {
						this.setAttribute("style", "width:262px");
					});
				}
				if(data.name == null){
					$('#setting').css("top", "-7px");
				}else{
					$('#setting').css("top", "6px");
				}
				$("#contents").css("display", "");

				self.show(true);
				dfd.resolve();
            }).always(() => {
				block.clear();
			});
            return dfd.promise();
        }
        
		public setting() {
			let self = this;
			nts.uk.ui.windows.sub.modal('at', '/view/ktg/004/b/index.xhtml').onClosed(() => {
				let data = nts.uk.ui.windows.getShared("KTG004B");
				if(data){
					self.startPage();
				}
			});
		}
		
		public openKDW003() {
			window.top.location = window.location.origin + '/nts.uk.at.web/view/kdw/003/a/index.xhtml';
		}
    }

	class AttendanceInfor {
		//???????????????????????????
		flexCarryOverTime = ko.observable(getText('KTG004_4', ['00:00']));
		//?????????????????????
		flexTime = ko.observable('00:00');
		//????????????
		holidayTime = ko.observable('00:00');
		//????????????
		overTime = ko.observable('00:00');
		//???????????????????????????
		nigthTime = ko.observable('00:00');
		//????????????/????????????
		lateEarly = ko.observable(getText('KTG004_8', ['0','0']));
		//??????????????????????????????
		dailyErrors = ko.observable(false);
		
		constructor(){}
				
		update(param: any){
			if(param){
				let self = this;
				self.flexCarryOverTime(getText('KTG004_4', [self.convertToTime(param.flexCarryOverTime)]));
				self.flexTime(self.convertToTime(param.flexTime));
				self.holidayTime(self.convertToTime(param.holidayTime));
				self.overTime(self.convertToTime(param.overTime));
				self.nigthTime(self.convertToTime(param.nigthTime));
				self.lateEarly(getText('KTG004_8', [param.late, param.early]));
				self.dailyErrors(param.dailyErrors);	
			}
		}
		
		convertToTime(data:any):string{
			if(data == null || data == undefined || data == 0 || data == '0'){
				return '00:00';
			}else{
				return (data < 0 ? '-': '') + moment(Math.floor(data / (data < 0 ? -60: 60)) + ":" + Math.abs(data % 60),'hh:mm').format('H:mm');
			}
		}
	}
	
	class RemainingNumberInfor {
		//????????????
		numberOfAnnualLeaveRemain = ko.observable(getText('KTG004_15', [0]));
		//?????????????????????
		numberAccumulatedAnnualLeave = ko.observable(getText('KTG004_15', [0]));
		//????????????
		numberOfSubstituteHoliday = ko.observable(getText('KTG004_15', [0]));
		//???????????????
		remainingHolidays = ko.observable(getText('KTG004_15', [0]));
		//??????????????????
		nursingRemainingNumberOfChildren = ko.observable(getText('KTG004_15', [0]));
		//????????????
		longTermCareRemainingNumber = ko.observable(getText('KTG004_15', [0]));
		
		constructor(){}
				
		update(param: any){
			if(param){
				const self = this;
				
				self.numberOfAnnualLeaveRemain(
					param.numberOfAnnualLeaveRemain.time == ZERO_TIME
					?
					getText('KTG004_15', [param.numberOfAnnualLeaveRemain.day])
					:
					getText('KTG004_28', [param.numberOfAnnualLeaveRemain.day, param.numberOfAnnualLeaveRemain.time])
					);
					
				self.numberAccumulatedAnnualLeave(
					getText('KTG004_15', [param.numberAccumulatedAnnualLeave])
					);
					
				self.numberOfSubstituteHoliday(
					// param.numberOfSubstituteHoliday.time == ZERO_TIME
					// ?
					// getText('KTG004_15', [param.numberOfSubstituteHoliday.day])
					// :
					// getText('KTG004_28', [param.numberOfSubstituteHoliday.day, param.numberOfSubstituteHoliday.time])
					// );
					this.getRemainSubHoliday(param.numberOfSubstituteHoliday.day, param.numberOfSubstituteHoliday.time, param.numberOfSubstituteHoliday.subHolidayTimeManage)
				);
				self.remainingHolidays(getText('KTG004_15', [param.remainingHolidays]));
				
				self.nursingRemainingNumberOfChildren(
					param.nursingRemainingNumberOfChildren.time = ZERO_TIME
					?
					getText('KTG004_15', [param.nursingRemainingNumberOfChildren.day])
					:
					getText('KTG004_29', [param.nursingRemainingNumberOfChildren.day, param.nursingRemainingNumberOfChildren.time])
					);
				
				
				self.longTermCareRemainingNumber(
					param.longTermCareRemainingNumber.time = ZERO_TIME
					?
					getText('KTG004_15', [param.longTermCareRemainingNumber.day])
					:
					getText('KTG004_28', [param.longTermCareRemainingNumber.day, param.longTermCareRemainingNumber.time])
					);
					
				
			}
		}

		public getRemainSubHoliday(day: any, time: any, manage: any) {
            let output = "";
            let timeString = nts.uk.time.format.byId("Time_Short_HM", time);
            let dayString = getText('KTG004_15', [day.toString()]);
            if (time) {
                output = timeString;
            } else if (day) {
                output = dayString;
            } else {
                if (manage) {
                    output = timeString; 
                } else {
                    output = dayString;
                }
            }

			alert(output);
            return output;
        }
	}
	class SpecialHolidaysRemainings {
		//?????????????????????
		code: number;
		//??????????????????
		name: string;
		//????????????
		specialResidualNumber: string;
		constructor(param: any){
			if(param){
				let self = this;
				self.code = param.code;
				self.name = param. name;
				self.specialResidualNumber = 
						param.specialResidualNumber.time == '0:00'
						?
						getText('KTG004_15', [param.specialResidualNumber.day])
						:
						getText('KTG004_28', [param.specialResidualNumber.day, param.specialResidualNumber.time])
			}
		}
				
	}
	class VacationSetting {
		// 60H????????????????????????
		holiday60HManage: boolean;
    
		// ????????????????????????
		nursingManage: boolean;
		
		// ????????????????????????
		publicHolidayManage: boolean;
		
		// ??????????????????????????????
		childCaremanage: boolean;
		
		// ????????????????????????
		accomoManage: boolean;
		
		// ??????????????????????????????
		accumAnnualManage: boolean;
		
		// ????????????????????????
		substituteManage: boolean;
		
		// ??????????????????????????????
		substituteTimeManage: boolean;
		
		// ????????????????????????
		annualManage: boolean;
	}
	
	const ZERO_TIME = "0:00";
	
}
module nts.uk.at.view.ktg004.a {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
        });
    });
}
