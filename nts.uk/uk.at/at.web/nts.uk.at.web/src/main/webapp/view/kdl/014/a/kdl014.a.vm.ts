module nts.uk.at.view.kdl014.a {
    import getText = nts.uk.resource.getText;
    import block = nts.uk.ui.block;
    import info = nts.uk.ui.dialog.info;
    import error = nts.uk.ui.dialog.error;
    import getShared = nts.uk.ui.windows.getShared;
    
    export class ScreenModel {
        empInfomationList = ko.observableArray([]);
        columns: any;
        currentCode = ko.observable();
        selectedItem = ko.observable( '' );
        listComponentOption: any;
        employeeInputList = ko.observableArray([]);
        employeeInfoList = ko.observableArray([]);
        dataServer: any[] = [];
        paramFromParent: ParamFromParent;
        display: boolean;
        height: number;
		empInfoCode = ko.observable();
		empInfoName = ko.observable();
		locationAdd : any = null;
		lstDataBase  = ko.observableArray([]);
		checkMobile : boolean = false;
        
        constructor(){
            let self = this;
            let tg: any[] = [];
            self.paramFromParent = getShared('KDL014-PARAM');
            if (self.paramFromParent.mode == 0 && self.paramFromParent.listEmp.length > 0) {
                let param = {
                    start: new Date(self.paramFromParent.startDate), 
                    end: new Date(self.paramFromParent.endDate), 
                    mode: self.paramFromParent.mode, 
                    listEmp: self.paramFromParent.listEmp.length > 0 ? [self.paramFromParent.listEmp[0]] : []
                };
                service.getEmployeeData(param).done(function(data) {
                    if (data) {
                        _.forEach(data, function(item) {
                            tg.push({ id: item.employeeId, code: item.employeeCode, businessName: item.businessName, workplaceName: item.affiliationName, depName: '' });
                        });
                        self.employeeInfoList(_.orderBy(tg, ['code'], ['asc']));
						if (self.employeeInfoList().length > 0){
							self.empInfoCode(self.employeeInfoList()[0].code);
							self.empInfoName(self.employeeInfoList()[0].businessName);
						}
                    }
                });
            }
            
            self.selectedItem.subscribe((newValue) => {
                $(document).ready(function() {
                    $('#btnClose').focus();
                });
               	self.filterGrid(newValue).done(() => {
					self.empInfomationList(_.orderBy(self.lstDataBase(), ['code', 'dateShow', 'time'], ['asc']));
					block.clear();
				});
            });
        }
        
        startPage(): JQueryPromise<any> {
            let self = this;
            let dfd = $.Deferred();
            block.grayout();

            if (self.paramFromParent) {
                let st = new Date(new Date(self.paramFromParent.startDate).setDate(new Date(new Date(self.paramFromParent.startDate)).getDate() - 1));
                let end = new Date(new Date(self.paramFromParent.endDate).setDate(new Date(new Date(self.paramFromParent.endDate)).getDate() + 2));
                
                let param = {
                  start: self.paramFromParent.startDate === self.paramFromParent.endDate ? st : new Date(self.paramFromParent.startDate),
                    end: self.paramFromParent.startDate === self.paramFromParent.endDate ? end : new Date(self.paramFromParent.endDate),
                    mode: self.paramFromParent.mode,
                    listEmp: self.paramFromParent.listEmp
                };
                service.getInfo(param).done(function(data) {
                    console.log(data);
                    self.dataServer = data.listEmps;
                    self.display = data.display;
					if (data != null && !_.isNil(data.checkMobile) && data.checkMobile){
						self.checkMobile = true;
					}

                    if (self.paramFromParent.mode == 0) {
                        self.selectedItem(self.employeeInfoList()[0].id);
                    } else {
                        self.filterGrid("").done(() => {
							self.empInfomationList(_.orderBy(self.lstDataBase(), ['code', 'dateShow', 'time'], ['asc']));
						});
                    }
                    self.bindingGrid();
                    $(document).ready(function() {
                        $('#btnClose').focus();
                    });
                }).fail(function(res) {
                    error({ messageId: res.messageId }).then(() => {
                        self.cancel_Dialog();
                    });
					block.clear();
                }).always(function() {
                    self.bindingGrid();
                    dfd.resolve();
                });
            } else {
                dfd.resolve();
                block.clear();
            } 
            return dfd.promise();
        }
        
        bindingGrid(){
            let self = this;
			let locationInfoHidden = true;
			if (self.display && self.checkMobile) {
				locationInfoHidden = false
			}
			
			
			let p = 0, q = 0, r = 0;
            if (self.paramFromParent.mode == 1) {
				
				if (locationInfoHidden == true && self.checkMobile) {
					r = 10;
					q = 15;
				}
				
				if (self.checkMobile == false) {
					q = 120;
					p = 18;
				}
				
				if (locationInfoHidden == false && self.checkMobile) {
					r = -25;
					q = -5
				}
                self.columns = ko.observableArray([
                    { headerText: getText("KDL014_11"), key: 'code', hidden: true },
                    { headerText: "<div style='text-align: center;'>"+getText("KDL014_12")+ "</div>" , key: 'name', width: 150 + p},
                    { headerText: "<div style='text-align: center;'>"+getText("KDL014_13")+ "</div>" , key: 'dateShow', width: 115 + p},
                    { headerText: "<div style='text-align: center;'>"+getText("KDL014_14")+ "</div>" , key: 'time', width: 80 + p},
                    { headerText: "<div style='text-align: center;'>"+getText("KDL014_15")+ "</div>" , key: 'stampAtr', width: 90 + p},
                    { headerText: "<div style='text-align: center;'>"+getText("KDL014_16")+ "</div>" , key: 'workLocationName', width: 130 + q },
					{ headerText: "<div style='text-align: center;'>"+getText("KDL014_26")+ "</div>" , key: 'locationAdd', width: 170 + r, hidden: !self.checkMobile},
                    { headerText: "<div style='text-align: center;'>"+getText("KDL014_17")+ "</div>" , key: 'locationInfo', width: 50, hidden: locationInfoHidden }
                ]);
            } else if (self.paramFromParent.mode == 0) {
				
				if (locationInfoHidden == true && self.checkMobile) {
					r = -20;
					q = 20;
					p = 10;
				}
				
				if (self.checkMobile == false) {
					p = 25;
					q = 200;
				}
				
				if (locationInfoHidden == false && self.checkMobile) {
					r = -20;
				}
                self.columns = ko.observableArray([
                    { headerText: getText("KDL014_11"), key: 'code', hidden: true },
                    { headerText: getText("KDL014_12"), key: 'name', hidden: true },
                    { headerText: "<div style='text-align: center;'>" + getText("KDL014_13") + "</div>", key: 'dateShow', width: 130 + p},
                    { headerText: "<div style='text-align: center;'>" + getText("KDL014_14") + "</div>", key: 'time', width: 90  + p},
                    { headerText: "<div style='text-align: center;'>" + getText("KDL014_15") + "</div>", key: 'stampAtr', width: 100  + p},
                    { headerText: "<div style='text-align: center;'>" + getText("KDL014_16") + "</div>", key: 'workLocationName', width: 155 + q},
					{ headerText: "<div style='text-align: center;'>" + getText("KDL014_26") + "</div>" , key: 'locationAdd', width: 250 + r , hidden: !self.checkMobile},
                    { headerText: "<div style='text-align: center;'>" + getText("KDL014_17") + "</div>", key: 'locationInfo', width: 50, hidden: locationInfoHidden }
                ]);
            }
            
        }

        filterGrid(id:string): JQueryPromise<any> {
            let self = this, tg : any = [], dfd = $.Deferred();
            self.empInfomationList([]);
			self.lstDataBase([]);
			
            for(let i = 0; i < self.dataServer.length; i++){
				let x = 0, y = 0;
	 			if (self.dataServer[i].locationInfo != null){
					x = self.dataServer[i].locationInfo.longitude;
					y = self.dataServer[i].locationInfo.latitude;
				}
				
				if (x == 0 && y == 0){
					self.dataServer[i].locationAdd = "";
	               	self.lstDataBase.push(new EmpInfomation(self.dataServer[i]));
				} else {
					self.getLocationAdd(self.dataServer[i], tg).done((a : any) => {
						self.lstDataBase.push(a);
						
						if (self.lstDataBase().length == self.dataServer.length){
						dfd.resolve();
				}
					});
					
				}
				
				if (self.lstDataBase().length == self.dataServer.length){
					//setTimeout(() => {
						dfd.resolve();
					//},self.dataServer.length * 100);
				}
					
            };
            return dfd.promise();
        }

		getLocationAdd(item : any, tg : any,): JQueryPromise<any>{
            let self = this,  dfd = $.Deferred();
			let x = 0, y = 0;
 			if (item.locationInfo != null){
				x = item.locationInfo.longitude;
				y = item.locationInfo.latitude;
			}
				$.ajax({
	                url: 'http://geoapi.heartrails.com/api/json',
	                data: { method: 'searchByGeoLocation', x: x, y : y}
	            }).done(function (data) {
	                if (data != null && !_.isNil(data.response.location) && data.response.location.length > 0) {
						self.locationAdd = data.response.location[0].prefecture + " " + data.response.location[0].city + " " + data.response.location[0].town; 
	                }
					
					if (data == null || (data != null && _.isNil(data.response.location)) || (data != null && !_.isNil(data.response.location) && data.response.location.length == 0)) {
						if (x == 0 && y == 0)
							self.locationAdd = "";
						else
							self.locationAdd = x + "、" + y;
					}
					item.locationAdd = self.locationAdd;
					dfd.resolve(new EmpInfomation(item));
	            });
			return dfd.promise();
        }
        
        cancel_Dialog(): any {
            nts.uk.ui.windows.close();
        }
        
    }
    
    class EmpInfomation {
        code: number;
        name: string;
        stampDateTime: string;
        stampMeans: string;
        stampAtr: string;
        workLocationName: string;
        locationInfo: string;
        time: string;
        date: string;
        color: number;
        dateShow: string;
		locationAdd : string;
        
        constructor(param: any) {
            let self = this;
            self.code = param.code;
            self.name = param.name;
            self.stampDateTime = new Date().toString();
            self.date = param.date;
            self.time = param.time;
            param.stampAtr = param.stampAtr.trim();

            if (param.stampAtr === '出勤' || param.stampAtr === '入門' || param.stampAtr === '応援開始' || param.stampAtr === '直行' || param.stampAtr === '早出'
                || param.stampAtr === '応援休出' || param.stampAtr === '応援早出' || param.stampAtr === '休出' || param.stampAtr === '応援出勤' || param.stampAtr === '臨時出勤') {

                self.stampAtr = `<div style="text-align: left; margin-left: 5px;">` + param.stampAtr + '</div>';

            } else if (param.stampAtr === '退勤' || param.stampAtr === '退門' || param.stampAtr === '応援終了' || param.stampAtr === '臨時退勤' || param.stampAtr === '直帰' || param.stampAtr === '退勤+残業') {
                self.stampAtr = `<div style="text-align: right; margin-right: 5px;">` + param.stampAtr + '</div>';

            } else {
                self.stampAtr = `<div style="text-align: center">` + param.stampAtr + '</div>';
            }
             
            self.workLocationName = param.workLocationName;
            
            if (_.includes([0,1,2,3,4,8], param.stampMeans)) {
                self.time = "<span>" + getText("KDP002_120") + "</span><span class='time'>" + param.time + "</span>";
            
                // 5:スマホ打刻
            } else if (param.stampMeans == 5) {
                self.time = "<span>" + getText("KDP002_121") + "</span><span class='time'>" + param.time + "</span>";

                // 6:タイムレコーダー打刻
            } else if (param.stampMeans == 6) {
                self.time = "<span>" + getText("KDP002_122") + "</span><span class='time'>" + param.time + "</span>";

            } else {
                self.time = "<span class='time'>" + param.time + "</span>";
            }
            
            let date = moment(param.stampDateTime).format("YYYY/MM/DD");
            
            if(new Date(date).getDay() == 6){
                self.dateShow = "<span class='color-schedule-saturday'>" + nts.uk.time.applyFormat("Short_YMDW", date) + "</span>";
            
            } else if (new Date(date).getDay() == 0) {
                self.dateShow = "<span class='color-schedule-sunday'>" + nts.uk.time.applyFormat("Short_YMDW", date) + "</span>";
            
            } else {
                self.dateShow = "<span>"+ nts.uk.time.applyFormat("Short_YMDW", date) + "</span>"            
            }

            if (param.locationInfo !== null) {
                self.locationInfo = '<a onClick = "gotoMap(' + param.locationInfo.latitude + ',' + param.locationInfo.longitude + ')"><img src="../img/Mapアイコン画像.png" height="20" width="20"/></a>';
            }

			self.locationAdd = param.locationAdd;
        }
        
    }
    
    class SystemType {
        static EMPLOYMENT = 1;
        static SALARY = 2;
        static PERSONNEL = 3;
        static ACCOUNTING = 4;
        static OH = 6;
    }
    
    interface ParamFromParent {
        startDate: string; //YYYY/MM/DD
        endDate: string; ////YYYY/MM/DD
        mode: number; //mode = 0 => Person, mode = 1 => Date
        listEmp: string[];//employeeId: 'ae7fe82e-a7bd-4ce3-adeb-5cd403a9d570'
    }

    interface EmployeeInfor {
        employeeId: string;
        employeeCode: string;
        employeeName: string;
        affiliationName: string;
    }
    
}

function gotoMap(latitude: string, longitude: string): any {
    window.open('https://www.google.co.jp/maps/place/' + latitude + ',' + longitude);
}

