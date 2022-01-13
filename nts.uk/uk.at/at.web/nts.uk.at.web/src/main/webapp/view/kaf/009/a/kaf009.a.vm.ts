module nts.uk.at.view.kaf009_ref.a.viewmodel {
    import Application = nts.uk.at.view.kaf000.shr.viewmodel.Application;
    import CommonProcess = nts.uk.at.view.kaf000.shr.viewmodel.CommonProcess;
    import Model = nts.uk.at.view.kaf009_ref.shr.viewmodel.Model;
    import AppType = nts.uk.at.view.kaf000.shr.viewmodel.model.AppType;
    import Kaf000AViewModel = nts.uk.at.view.kaf000.a.viewmodel.Kaf000AViewModel;
	import AppInitParam = nts.uk.at.view.kaf000.shr.viewmodel.AppInitParam;

    @bean()
    class Kaf009AViewModel extends Kaf000AViewModel {

		appType: KnockoutObservable<number> = ko.observable(AppType.GO_RETURN_DIRECTLY_APPLICATION);
		isAgentMode : KnockoutObservable<boolean> = ko.observable(false);
        application: KnockoutObservable<Application>;
        applicationTest: any = {
                employeeID: this.$user.employeeId,
                appType: '4',
                appDate: moment(new Date()).format('YYYY/MM/DD'),
                enteredPerson: this.$user.employeeId,
                inputDate: moment(new Date()).format('YYYY/MM/DD HH:mm:ss'),
                reflectionStatus: {
                    listReflectionStatusOfDay: [{
                        actualReflectStatus: 1,
                        scheReflectStatus: 1,
                        targetDate: '2020/01/07',
                        opUpdateStatusAppReflect: {
                            opActualReflectDateTime: '2020/01/07 20:11:11',
                            opScheReflectDateTime: '2020/01/07 20:11:11',
                            opReasonActualCantReflect: 1,
                            opReasonScheCantReflect: 0

                        },
                        opUpdateStatusAppCancel: {
                            opActualReflectDateTime: '2020/01/07 20:11:11',
                            opScheReflectDateTime: '2020/01/07 20:11:11',
                            opReasonActualCantReflect: 1,
                            opReasonScheCantReflect: 0
                        }
                    }]
                }



            };
        model: Model;
        dataFetch: KnockoutObservable<ModelDto> = ko.observable(null);
        mode: KnockoutObservable<String> = ko.observable('edit');
        isSendMail: KnockoutObservable<boolean>;
		isFromOther: boolean = false;

        created(params: AppInitParam) {
            const vm = this;
            vm.$blockui("show");
			if(nts.uk.request.location.current.isFromMenu) {
				sessionStorage.removeItem('nts.uk.request.STORAGE_KEY_TRANSFER_DATA');	
			} else {
				if(!_.isNil(__viewContext.transferred.value)) {
					vm.isFromOther = true;
					params = __viewContext.transferred.value;
				}
			}
			
			let empLst: Array<string> = [],
				dateLst: Array<string> = [],
				screenCode: number = null;
            vm.isSendMail = ko.observable(false);
            vm.application = ko.observable(new Application(vm.appType()));
            vm.model = new Model(true, true, true, '', '', '', '');
			if (!_.isEmpty(params)) {
				if (!nts.uk.util.isNullOrUndefined(params.screenCode)) {
					screenCode = params.screenCode;
				}
				if (!_.isEmpty(params.employeeIds)) {
					empLst = params.employeeIds;
				}
				if (!_.isEmpty(params.baseDate)) {
					let paramDate = moment(params.baseDate).format('YYYY/MM/DD');
					dateLst = [paramDate];
					vm.application().appDate(paramDate);
					vm.application().opAppStartDate(paramDate);
                    vm.application().opAppEndDate(paramDate);
				}
				if (params.isAgentMode) {
					vm.isAgentMode(params.isAgentMode);
				}
			}
			let paramKAF000 = {
				empLst, 
				dateLst, 
				appType: vm.appType(),
				screenCode
			};
            vm.loadData(paramKAF000)
            .then((loadDataFlag: any) => {
                vm.application().appDate.subscribe(value => {
                    console.log(value);
                    if (value) {
                        vm.changeDate();
                    }
                });
                if(loadDataFlag) {
					vm.application().employeeIDLst(empLst);
					let command = {} as ParamStart;
					command.appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput);
                    command.sids = _.isEmpty(empLst) ? [vm.$user.employeeId] : empLst;
					command.dates = _.isEmpty(dateLst) ? [] : dateLst;

                    return vm.$ajax(API.startNew, command);
                }
            }).then((res: any) => {
                if(res) {
                    vm.dataFetch({
                        workType: ko.observable(res.workType),
                        workTime: ko.observable(res.workTime),
                        appDispInfoStartup: ko.observable(res.appDispInfoStartup),
                        goBackReflect: ko.observable(res.goBackReflect),
                        lstWorkType: ko.observable(res.lstWorkType),
                        goBackApplication: ko.observable(res.goBackApplication),
                        isChangeDate: false
                    });
					if (!_.isEmpty(params)) {
						if (!_.isEmpty(params.baseDate)) {
							vm.changeDate();
						}
					}
                }
            }).fail((failData: any) => {
                let param;
                if (failData.message) {
                    param = {message: failData.message, messageParams: failData.parameterIds};
                } else {
                    param = {messageId: failData.messageId, messageParams: failData.parameterIds}
                }
                vm.$dialog.error(param);

            }).always(() => vm.$blockui("hide"));




        }

        mounted() {
            const vm = this;
        }



        public handleConfirmMessage(listMes: any, res: any) {
            let vm = this;
            if (!_.isEmpty(listMes)) {
                let item = listMes.shift();
                return vm.$dialog.confirm({ messageId: item.msgID, messageParams: item.paramLst }).then((value) => {
                    if (value == 'yes') {
                        if (_.isEmpty(listMes)) {
                            return vm.registerData(res);
                        } else {
                            return vm.handleConfirmMessage(listMes, res);
                        }

                    }
                });
            }
        }

        registerData(goBackApp) {
            let vm = this;
            let paramsRegister = {
                    companyId: vm.$user.companyId,
                    applicationDto: vm.applicationTest,
                    goBackDirectlyDto: goBackApp,
                    inforGoBackCommonDirectDto: ko.toJS(vm.dataFetch),
                    mode : vm.mode == 'edit'
            }

            return vm.$ajax(API.register, paramsRegister);
                
        }

        register() {
            const vm = this;
            let application = ko.toJS(vm.application);
            vm.applicationTest.appID = application.appID;
            vm.applicationTest.appDate = application.appDate;
            vm.applicationTest.appType = application.appType;
            vm.applicationTest.prePostAtr = application.prePostAtr;
            vm.applicationTest.opAppStartDate = application.opAppStartDate;
            vm.applicationTest.opAppEndDate = application.opAppEndDate;
            vm.applicationTest.opAppReason = application.opAppReason;
            vm.applicationTest.opAppStandardReasonCD = application.opAppStandardReasonCD;
            vm.applicationTest.opReversionReason = application.opReversionReason;
			vm.applicationTest.employeeID = application.employeeIDLst[0];
            if (vm.model) {
				/*
				
					let isCondition1 = 
						!_.isNil(vm.model.checkbox3())
						&& _.isNil(vm.model.workTypeCode())
					 	&& (vm.dataFetch().goBackReflect().reflectApplication === ApplicationStatus.DO_REFLECT_1 || vm.dataFetch().goBackReflect().reflectApplication === ApplicationStatus.DO_NOT_REFLECT_1);
					
					
					let isCondition2 = 
							_.isNil(vm.model.checkbox3())
							&& _.isNil(vm.model.workTypeCode()) 
							&& vm.dataFetch().goBackReflect().reflectApplication === ApplicationStatus.DO_REFLECT;
							
				
				 */
				const isDisplayMsg2150 = 
					((!_.isNil(vm.model.checkbox3()) ? vm.model.checkbox3() : false) // ②「勤務を変更する」がチェックしている
					|| vm.dataFetch().goBackReflect().reflectApplication === ApplicationStatus.DO_REFLECT) // ①直行直帰申請の反映.勤務情報を反映する　＝　反映する
                	&& _.isNil(vm.model.workTypeCode())

				if (isDisplayMsg2150) {
					
					vm.$dialog.error({messageId: 'Msg_2150'});
					
                    return vm.$validate().then(() => false);
                } 
            }
            let model = ko.toJS( vm.model );
            let goBackApp = new GoBackApplication(
                model.checkbox1 ? 1 : 0,
                model.checkbox2 ? 1 : 0,
            );
            // is change can be null
//            if (!_.isNull(model.checkbox3)) {
                if (!_.isNull(model.checkbox3)) {
                    goBackApp.isChangedWork = model.checkbox3 ? 1 : 0;                    
                }
                if (vm.mode && vm.model.checkbox3() || vm.dataFetch().goBackReflect().reflectApplication == 1) {
                    if (!_.isEmpty(vm.model.workTypeCode())) {
                        let dw = new DataWork( model.workTypeCode );
                        if ( model.workTimeCode ) {
                            dw.workTime = model.workTimeCode
                        }
                        goBackApp.dataWork = dw;
                        
                    }
                    
                }
//            }

            let param = {
                companyId: this.$user.companyId,
                agentAtr: true,
                applicationDto: vm.applicationTest,
                goBackDirectlyDto: goBackApp,
                inforGoBackCommonDirectDto: ko.toJS( vm.dataFetch ),
                mode: true
            };
            vm.$blockui( "show" );
            vm.$validate('.nts-input', '#kaf000-a-component3-prePost', '#kaf000-a-component5-comboReason')
                .then( isValid => {
                    if ( isValid ) {
                        return true;
                    }
                } )
                .then( result => {
                    if (!result) return;
                    return vm.$ajax(API.checkRegister, param); 
                }).then( res => {
                    if (res == undefined) return;
                    if (_.isEmpty( res )) {
                        return vm.registerData(goBackApp);
                    }else {
                        let listTemp = _.clone(res);
                        vm.handleConfirmMessage(listTemp, goBackApp);
                    }
                }).done(result => {
                    if (result != undefined) {
                        vm.$dialog.info( { messageId: "Msg_15" } ).then(() => {
							nts.uk.request.ajax("at", API.reflectApp, result.reflectAppIdLst);
                       		CommonProcess.handleAfterRegister(result, vm.isSendMail(), vm, false, vm.appDispInfoStartupOutput().appDispInfoNoDateOutput.employeeInfoLst);
                        });                
                    }
                })
                .fail( err => {
                    let param;
                    if (err.message && err.messageId) {
                        param = {messageId: err.messageId, messageParams: err.parameterIds};
                    } else {

                        if (err.message) {
                            param = {message: err.message, messageParams: err.parameterIds};
                        } else {
                            param = {messageId: err.messageId, messageParams: err.parameterIds};
                        }
                    }
                    vm.$dialog.error(param);
                })
                .always(() => vm.$blockui("hide"));





        }

        changeDate() {
            const vm = this;
			vm.$blockui( "show" );
            if(!_.isNull(ko.toJS(vm.dataFetch))) {
                vm.dataFetch().isChangeDate = true;
            }
			let companyId = vm.$user.companyId;
			let appDates = [];
			appDates.push(ko.toJS(vm.application().appDate));
			let employeeIds = ko.toJS(vm.application().employeeIDLst);
            let dataClone = _.clone(vm.dataFetch());
			let inforGoBackCommonDirectDto = ko.toJS(vm.dataFetch); 
			inforGoBackCommonDirectDto.appDispInfoStartup = ko.toJS(vm.appDispInfoStartupOutput);
			let commandChangeDate = {companyId, appDates, employeeIds, inforGoBackCommonDirectDto};
        
            
            if (!_.isNull(dataClone)) {
				vm.$ajax(API.changeDate, commandChangeDate)
				.done(res => {
					if (res) {
						dataClone.lstWorkType(res.lstWorkType);
						dataClone.workType(res.workType);
						dataClone.workTime(res.workTime);
	                	dataClone.appDispInfoStartup = vm.appDispInfoStartupOutput;
						vm.dataFetch(dataClone);						
					}
				}).fail(res => {
					let param;
                    if (res.message) {
                        param = {message: res.message, messageParams: res.parameterIds};
                    } else {
                        param = {messageId: res.messageId, messageParams: res.parameterIds}
                    }
                    vm.$dialog.error(param);
				}).always(res => {
					vm.$blockui( "hide" );
				});
                
                return;
            }
            vm.$blockui( "show" );
            let ApplicantEmployeeID: null,
            ApplicantList: null,
            appDispInfoStartupOutput = ko.toJS(vm.appDispInfoStartupOutput),
            command = { ApplicantEmployeeID, ApplicantList, appDispInfoStartupOutput };
            vm.$ajax(API.startNew, command)
                .done(res => {
                    if (res) {
                        vm.dataFetch({
                            workType: ko.observable(res.workType),
                            workTime: ko.observable(res.workTime),
                            appDispInfoStartup: ko.observable(res.appDispInfoStartup),
                            goBackReflect: ko.observable(res.goBackReflect),
                            lstWorkType: ko.observable(res.lstWorkType),
                            goBackApplication: ko.observable(res.goBackApplication),
                            isChangeDate: false
                        });
                    }
                })
                .fail(res => {

                    let param;
                    if (res.message) {
                        param = {message: res.message, messageParams: res.parameterIds};
                    } else {
                        param = {messageId: res.messageId, messageParams: res.parameterIds}
                    }
                    vm.$dialog.error(param);
                })
                .always(() => vm.$blockui( "hide" ));

        }



    }
    export class GoBackApplication {
        straightDistinction: number;
        straightLine: number;
        isChangedWork?: number;
        dataWork?: DataWork;
        constructor(straightDistinction: number, straightLine: number, isChangedWork?: number, dataWork?: DataWork) {
            this.straightDistinction = straightDistinction;
            this.straightLine = straightLine;
            this.isChangedWork = isChangedWork;
            this.dataWork = dataWork;
        }
    }


    export class DataWork {
        workType: string;
        workTime?: string;
        constructor(workType: string, workTime?: string) {
            this.workType = workType;
            this.workTime = workTime;
        }
    }
    export class GoBackReflect {
        companyId: string;
        reflectApplication: ApplicationStatus;
    }
	
	
	
    export class ModelDto {

        workType: KnockoutObservable<any>;

        workTime: KnockoutObservable<any>;

        appDispInfoStartup: KnockoutObservable<any>;

        goBackReflect: KnockoutObservable<GoBackReflect> = ko.observable( null );

        lstWorkType: KnockoutObservable<any>;

        goBackApplication: KnockoutObservable<any>;

        isChangeDate: boolean = false;
    }

    const API = {
        startNew: "at/request/application/gobackdirectly/getGoBackCommonSettingNew",
        checkRegister: "at/request/application/gobackdirectly/checkBeforeRegisterNew",
        register: "at/request/application/gobackdirectly/registerNewKAF009",
        changeDate: "at/request/application/gobackdirectly/getAppDataByDate",
		reflectApp: "at/request/application/reflect-app"
    }

    export class ApplicationStatus {
        // 反映しない
        public static DO_NOT_REFLECT: number = 0;
        // 反映する
        public static DO_REFLECT: number = 1;
        // 申請時に決める(初期値：反映しない)
        public static DO_NOT_REFLECT_1: number = 2;
        // 申請時に決める(初期値：反映する)
        public static DO_REFLECT_1: number = 3;
    }
    export class ParamBeforeRegister {
        companyId: string;
        agentAtr: boolean;
        applicationDto: any;
        goBackDirectlyDto: any;
        inforGoBackCommonDirectDto: any;

    }
	export interface ParamStart {
		// ・会社ID
		// companyId: string;
		// ・申請者リスト
		sids: Array<string>;
		// ・申請対象日リスト
		dates: Array<string>;
		// ・申請表示情報　
		appDispInfoStartupOutput: any;
	}

}