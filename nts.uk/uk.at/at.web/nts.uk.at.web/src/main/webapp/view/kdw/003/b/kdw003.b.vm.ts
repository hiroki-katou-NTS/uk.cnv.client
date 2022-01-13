module nts.uk.at.view.kdw003.b {
    export module viewmodel {
        import getText = nts.uk.resource.getText;
        import block = nts.uk.ui.block;
        import jump = window.parent.nts.uk.request.jump;
        let __viewContext: any = window["__viewContext"] || {};

        export class ScreenModel {
            lstError: KnockoutObservableArray<any> = ko.observableArray([]);
            listExportDto: KnockoutObservableArray<ExportDto> = ko.observableArray([]);
            textApp: KnockoutObservable<any> = ko.observable(nts.uk.resource.getText('KDW003_63'));
            isDisableExportCSV: KnockoutObservable<boolean> = ko.observable(false);
            messageAlert: KnockoutObservable<any> =  ko.observable(nts.uk.resource.getText('KDW003_63'));
            showMessage: KnockoutObservable<any> = ko.observable(false);
            errName: KnockoutObservable<any> = ko.observable('');
			diplayFormat: KnockoutObservable<any> = ko.observable('');
            constructor() {
                
            }

            exportCSV(): void {
                let self = this;
                block.invisible();
                service.exportCsv(self.listExportDto()).always(() => {
                    block.clear();
                });
            }

            closeDialog(): void {
                nts.uk.ui.windows.close();
            }

            startPage(param, errorValidate, messageRefer): JQueryPromise<any> {
                let self = this,
                    dfd = $.Deferred();
				self.diplayFormat(nts.uk.ui.windows.getShared('displayFormat'));
                if (!_.isEmpty(messageRefer)) {
                    self.showMessage(true);
                    self.messageAlert(nts.uk.resource.getMessage(messageRefer));

//                    let windowSize = nts.uk.ui.windows.getSelf();
//                    windowSize.$dialog.dialog('option', {
//                        width : 900,
//                        height : 530
//                    });
                    let data = [];
                     let i: number = 0;
                    _.each(errorValidate, value => {
                        data.push({
							idRandom: nts.uk.util.randomId(),
                            id: i++,
                            date: value.date,
                            employeeCode: value.employeeCode,
                            employeeName: value.employeeName,
                            message: value.message,
                            itemName: value.itemName,
                            errorCode: ""
                        });
                    });

                    let dataTemp = _.uniqWith(data, function(x, y) {
                        if (_.isEmpty(x.date)) return x.employeeCode === y.employeeCode && x.message === y.message;
                        else return false;
                    });
                    let arr: any[] = _.orderBy(dataTemp, ['employeeCode', 'date', 'errorCode'], ['asc', 'asc', 'asc']);
                    
                    self.lstError(arr);
                    self.listExportDto(arr.map((d) => { return new ExportDto(d); }));
                    if (self.lstError().length == 0) self.isDisableExportCSV(true);
                    self.loadGridSimple();
                    dfd.resolve();
                } else {
                    self.showMessage(false);
                    service.getErrorRefer(param).done((data) => {
                        let arrErrorCode: string[] = [];
                        let i: number = data.length;
                        _.each(errorValidate, value => {
                            data.push({ 
                                id: i++, 
                                date: value.date, 
                                employeeCode: value.employeeCode, 
                                employeeName: value.employeeName, 
                                message: value.message, 
                                itemName: value.itemName, 
                                errorCode: "" });
                        });
                        let dataTemp = _.uniqWith(data, function(x, y) {
                            if (_.isEmpty(x.date)) return x.employeeCode === y.employeeCode && x.message === y.message;
                            else return false;
                        });
                        let arr: any[] = _.orderBy(dataTemp, ['employeeCode', 'date', 'errorCode'], ['asc', 'asc', 'asc']);
    
                        _.each(data, (dt: any) => {
                            arrErrorCode.push(dt.errorCode);
                        });
    
                        arrErrorCode = _.uniq(arrErrorCode);
    
                        if (arrErrorCode.length != 0) {
                            service.getErrAndAppTypeCd(arrErrorCode).done((data1) => {
                                let app = [];
                                let appTypeEnum = __viewContext.enums.ApplicationType;  
                                _.each(data1.mapErrCdAppTypeCd, e => {
                                    _.each(e, f => {
                                        app = _.filter(appTypeEnum, function(o) { return o.value == f.value; });
                                        f.fieldName =  app.length > 0 ? app[0].name : f.fieldName;
                                    });
                                });
                                self.lstError(arr.map((d) => { 
                                    return new ErrorReferModel(d, data1.employeeIdLogin, !!data1.mapErrCdAppTypeCd[d.errorCode] ? data1.mapErrCdAppTypeCd[d.errorCode] : []); 
                                }));
                                self.listExportDto(arr.map((d) => { return new ExportDto(d); }));
                                if (self.lstError().length == 0) self.isDisableExportCSV(true);
    
                                self.loadGridNormal();
    
                                dfd.resolve();
                            }).fail(() => {
                                dfd.reject();
                            });
                        } else {
                            if (self.lstError().length == 0) self.isDisableExportCSV(true);
                            self.loadGridNormal();
                            dfd.resolve();
                        }
    
                    }).fail(() => {
                        dfd.reject();
                    });
                   }
                return dfd.promise();
            }

            loadGridSimple() {
                let self = this,
					employeeCode_width = 120,
					employeeName_width = 150,
					date_width = 95,
					message_width = 860 - 120;
				if(self.diplayFormat() != 0) {
					message_width = message_width - employeeCode_width - employeeName_width;
				}
				if(self.diplayFormat() != 1) {
					message_width = message_width - date_width;
				}
                $("#grid").igGrid({
                    primaryKey: "idRandom",
                    height: 400,
                    dataSource: self.lstError(),
                    autoGenerateColumns: false,
                    alternateRowStyles: false,
                    dataSourceType: "json",
                    autoCommit: true,
                    columns: [
						{ key: "idRandom", width: "130px", hidden: true, dataType: "string" },	
                        { key: "id", width: "130px", hidden: true, dataType: "number" },
                        { key: "employeeCode", width: employeeCode_width, headerText: getText('KDW003_32'), dataType: "string", hidden:self.diplayFormat() == 0 ? true : false },
                        { key: "employeeName", width: employeeName_width, headerText: getText('KDW003_33'), dataType: "string", hidden:self.diplayFormat() == 0 ? true : false },
                        { key: "date", width: date_width, headerText: getText('KDW003_34'), dataType: "string",hidden:self.diplayFormat() == 1 ? true : false },
                        { key: "message", width: message_width, headerText: getText('KDW003_36'), dataType: "string" },
                        { key: "itemName", width: "120px", headerText: getText('KDW003_37'), dataType: "string" }
                    ],
                    features: [
                        {
                            name: "Updating",
                            showDoneCancelButtons: false,
                            enableAddRow: false,
                            enableDeleteRow: false,
                            editMode: 'cell',
                            columnSettings: [
                                { columnKey: "employeeCode", readOnly: true },
                                { columnKey: "employeeName", readOnly: true },
                                { columnKey: "date", readOnly: true },
                                { columnKey: "message", readOnly: true },
                                { columnKey: "itemName", readOnly: true }
                            ]
                        },
                        {
                            name: "Selection",
                            mode: "row",
                            multipleSelection: false,
                            touchDragSelect: false,
                            multipleCellSelectOnClick: false
                        },
                        {
                            name: 'Paging',
                            type: "local",
                            pageSize: 10
                        }
                    ]
                });
            }

         	loadGridNormal(){
                        let self = this,
							employeeCode_width = 120,
							employeeName_width = 150,
							date_width = 95,
							message_width = 1220 - 45 - 60 - 120 - 170 - 55;
						if(self.diplayFormat() != 0) {
							message_width = message_width - employeeCode_width - employeeName_width;
						}
						if(self.diplayFormat() != 1) {
							message_width = message_width - date_width;
						}
        
                        $("#grid").igGrid({
                            primaryKey: "id",
                            height: 400,
                            dataSource: self.lstError(),
                            autoGenerateColumns: false,
                            alternateRowStyles: false,
                            dataSourceType: "json",
                            autoCommit: true,
                            columns: [
                                { key: "id", width: "130px", hidden: true, dataType: "number" },
                                { key: "stateBtn", width: "130px", hidden: true, dataType: "string" },
                                { key: "employeeCode", width: employeeCode_width, headerText: getText('KDW003_32'), dataType: "string",hidden:self.diplayFormat() == 0 ? true : false },
                                { key: "employeeName", width: employeeName_width, headerText: getText('KDW003_33'), dataType: "string",hidden:self.diplayFormat() == 0 ? true : false },
                                { key: "date", width: date_width, headerText: getText('KDW003_34'), dataType: "string", hidden:self.diplayFormat() == 1 ? true : false  },
                                { key: "errorCode", width: "45px", headerText: "コード", dataType: "string" },
                                { key: "errorAlarmAtr", width: "60px", headerText: getText('KDW003_129'), dataType: "string" },
                                { key: "message", width: message_width, headerText: getText('KDW003_36'), dataType: "string" },
                                { key: "itemName", width: "120px", headerText: getText('KDW003_37'), dataType: "string" },
                                { key: "submitedName", width: "170px", headerText: getText('KDW003_62'), dataType: "string" },
                                {
                                    key: "application", width: "55px", headerText: getText('KDW003_63'), dataType: "string", unbound: true,
                                    template: "<input type= \"button\"  onclick = \"nts.uk.at.view.kdw003.b.viewmodel.redirectApplication(${id}) \" value= \" " + getText('KDW003_63') + " \" ${stateBtn} />"
                                }
                            ],
                            features: [
                                {
                                    name: "Updating",
                                    showDoneCancelButtons: false,
                                    enableAddRow: false,
                                    enableDeleteRow: false,
                                    editMode: 'cell',
                                    columnSettings: [
                                        { columnKey: "employeeCode", readOnly: true },
                                        { columnKey: "employeeName", readOnly: true },
                                        { columnKey: "date", readOnly: true },
                                        { columnKey: "errorCode", readOnly: true },
                                        { columnKey: "message", readOnly: true },
                                        { columnKey: "itemName", readOnly: true },
                                        { columnKey: "submitedName", readOnly: true },
                                        { columnKey: "application", readOnly: true }
                                    ]
                                },
                                {
                                    name: "Selection",
                                    mode: "row",
                                    multipleSelection: false,
                                    touchDragSelect: false,
                                    multipleCellSelectOnClick: false
                                },
                                {
                                    name: 'Paging',
                                    type: "local",
                                    pageSize: 10
                                }
                            ]
                        });
                    }
        }
        export function redirectApplication(id) {
            let dataSource = $("#grid").igGrid("option", "dataSource");
            let rowSelect = _.find(dataSource, (value) => {
                return value.id == id;
            });

            let dataShare: any = {
                date: rowSelect.dateDetail.format("YYYY/MM/DD"),
                listValue: rowSelect.arrAppTypeCode
            }

            nts.uk.ui.windows.setShared("shareToKdw003e", dataShare);
            nts.uk.ui.windows.sub.modal("/view/kdw/003/e/index.xhtml").onClosed(() => {
                let screen = nts.uk.ui.windows.getShared("shareToKdw003a");
                if (screen == undefined) screen = 1905;
                let transfer = {
                    baseDate: dataShare.date,
                    uiType: 1,
                    employeeIDs: [],
                    stampRequestMode: 1,
                    screenMode: 0,
					screenCode: 0
                };
				
				var vmNew = new ko.ViewModel();

                switch (screen) {
                    case 0:
                        //KAF005-残業申請（早出）
                        vmNew.$jump.blank("/view/kaf/005/a/index.xhtml?overworkatr=0", transfer);
                        break;

                    case 1:
                        //KAF005-残業申請（通常） 
                        vmNew.$jump.blank("/view/kaf/005/a/index.xhtml?overworkatr=1", transfer);
                        break;

                    case 2:
                        //KAF005-残業申請（早出・通常）
                        vmNew.$jump.blank("/view/kaf/005/a/index.xhtml?overworkatr=2", transfer);
                        break;

                    case 3:
                        //KAF006-休暇申請
                        vmNew.$jump.blank("/view/kaf/006/a/index.xhtml", transfer);
                        break;

                    case 4:
                        //KAF007-勤務変更申請
                        vmNew.$jump.blank("/view/kaf/007/a/index.xhtml", transfer);
                        break;

                    case 5:
                        //KAF008-出張申請 
                        vmNew.$jump.blank("/view/kaf/008/a/index.xhtml", transfer);
                        break;

                    case 6:
                        //KAF009-直行直帰申請
                        vmNew.$jump.blank("/view/kaf/009/a/index.xhtml", transfer);
                        break;

                    case 7:
                        //KAF010-休出時間申請
                        transfer.uiType = 0;
						vmNew.$jump.blank("/view/kaf/010/a/index.xhtml", transfer);
                        break;

                    case 8:
                        //KAF002-打刻申請（外出許可）
                        transfer.stampRequestMode = 0;
                         transfer.screenMode = 1;
                        jump("/view/kaf/002/a/index.xhtml", transfer);
                        break;

                    case 9:
                        //KAF002-打刻申請（出退勤打刻漏れ）
                        transfer.stampRequestMode = 1;
                         transfer.screenMode = 1;
                        jump("/view/kaf/002/a/index.xhtml", transfer);
                        break;

                    case 10:
                        //KAF002-打刻申請（打刻取消）
                        transfer.stampRequestMode = 2;
                        transfer.screenMode = 1;
                        jump("/view/kaf/002/a/index.xhtml", transfer);
                        break;

                    case 11:
                        //KAF002-打刻申請（レコーダイメージ）
                        transfer.stampRequestMode = 3;
                         transfer.screenMode = 1;
                        jump("/view/kaf/002/a/index.xhtml", transfer);
                        break;

                    case 12:
                        //KAF002-打刻申請（その他）
                        transfer.stampRequestMode = 4;
                         transfer.screenMode = 1;
                        jump("/view/kaf/002/a/index.xhtml", transfer);
						break;
					case 13:
                        //任意申請
                        vmNew.$jump.blank("/view/kaf/020/a/index.xhtml", transfer);
                        break;

                    case 14:
                        //申請一覧
                        vmNew.$jump.blank("at", "/view/cmm/045/a/index.xhtml?a=0");
                        break;
                    default:
                        break;
                }
            });
            //alert(rowSelect.dateDetail); 
        }

        interface IErrorReferModel {
            id: string;
            employeeId: string;
            employeeCode: string;
            employeeName: string;
            date: string;
            errorCode: string;
            message: string;
            itemId: number;
            itemName: string;
            boldAtr: boolean;
            messageColor: string;
            submitedName: string;
            errorAlarmAtr: number;
            
        }

        class ErrorReferModel {
            id: string;
            employeeId: string;
            employeeCode: string;
            employeeName: string;
            date: string;
            errorCode: string;
            message: string;
            itemId: number;
            itemName: string;
            boldAtr: boolean;
            messageColor: string;
            submitedName: string;
            dateDetail: string;
            employeeIdLogin: string;
            arrAppTypeCode: any[];
            stateBtn: string;

            constructor(model: IErrorReferModel, employeeIdLogin: string, arrAppTypeCode: any[]) {
                let self = this;
                self.id = model.id;
                self.employeeId = model.employeeId;
                self.employeeCode = model.employeeCode;
                self.employeeName = model.employeeName;
                self.submitedName = model.submitedName;
                if (model.date != "" && model.date != null) {
                    if (moment(model.date, "YYYY/MM/DD").day() == 6) {
                        self.date = '<span style="color: #4F81BD">' + moment(model.date, 'YYYY/MM/DD').format('YYYY/MM/DD(ddd)') + '</span>';
                    } else if (moment(model.date, "YYYY/MM/DD").day() == 0) {
                        self.date = '<span style="color: #e51010">' + moment(model.date, 'YYYY/MM/DD').format('YYYY/MM/DD(ddd)') + '</span>';
                    } else {
                        self.date = '<span>' + moment(model.date, 'YYYY/MM/DD').format('YYYY/MM/DD(ddd)') + '</span>';
                    }
                }else{
                     self.date = "";
                }
                self.errorCode = model.errorCode;
                self.message = model.boldAtr ? '<span style="font-weight: bold;color: ' + model.messageColor + ';">' + model.message + '</span>' : '<span style="color: ' + model.messageColor + ';">' + model.message + '</span>';
                self.itemId = model.itemId;
                self.itemName = model.itemName;
                self.boldAtr = model.boldAtr;
                self.messageColor = model.messageColor;
                self.dateDetail = moment(model.date, "YYYY/MM/DD");
                self.employeeIdLogin = employeeIdLogin;
                self.arrAppTypeCode = arrAppTypeCode;
                self.stateBtn = ((model.employeeId == employeeIdLogin) && (arrAppTypeCode.length > 0)) ? "" : "hidden";
                  if(model.errorAlarmAtr == 0){
                    self.errorAlarmAtr = nts.uk.resource.getText('KDW003_130');
                    }
                      if(model.errorAlarmAtr == 1){
                    self.errorAlarmAtr = nts.uk.resource.getText('KDW003_131');
                    }
                      if(model.errorAlarmAtr == 2){
                    self.errorAlarmAtr = '';
                    } 
            }
        }

        class ExportDto {
            employeeCode: string;
            employeeName: string;
            date: string;
            errorCode: string;
            message: string;
            itemName: string;
            submitedName: string;
            errorAlarmAtr: String;
            

            constructor(model: IErrorReferModel) {
                let self = this;
                self.employeeCode = model.employeeCode;
                self.employeeName = model.employeeName;
                self.submitedName = model.submitedName;
                if (model.date != "") {
                    if (moment(model.date, "YYYY/MM/DD").day() == 6) {
                        self.date = moment(model.date, 'YYYY/MM/DD').format('YYYY/MM/DD(ddd)');
                    } else if (moment(model.date, "YYYY/MM/DD").day() == 0) {
                        self.date = moment(model.date, 'YYYY/MM/DD').format('YYYY/MM/DD(ddd)');
                    } else {
                        self.date = moment(model.date, 'YYYY/MM/DD').format('YYYY/MM/DD(ddd)');
                    }
                } else {
                    self.date = "";
                }
                self.errorCode = model.errorCode;
                self.message = model.message;
                self.itemName = model.itemName;
                if(model.errorAlarmAtr == 0){
                    self.errorAlarmAtr = nts.uk.resource.getText('KDW003_130');
                    }
                      if(model.errorAlarmAtr == 1){
                    self.errorAlarmAtr = nts.uk.resource.getText('KDW003_131');
                    }
                      if(model.errorAlarmAtr == 2){
                    self.errorAlarmAtr = '';
                    }
                
            }
        }
    }
}