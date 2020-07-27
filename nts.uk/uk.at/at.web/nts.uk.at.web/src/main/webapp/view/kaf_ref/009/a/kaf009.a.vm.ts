module nts.uk.at.view.kaf009_ref.a.viewmodel {
    import Application = nts.uk.at.view.kaf000_ref.shr.viewmodel.Application;
    import CommonProcess = nts.uk.at.view.kaf000_ref.shr.viewmodel.CommonProcess;
    import Model = nts.uk.at.view.kaf009_ref.shr.viewmodel.Model;

    @bean()
    class Kaf009AViewModel extends ko.ViewModel {
        
        application: KnockoutObservable<Application>;
        applicationTest: any = {
                version: 1,
                // appID: '939a963d-2923-4387-a067-4ca9ee8808zz',
                prePostAtr: 1,
                employeeID: '292ae91c-508c-4c6e-8fe8-3e72277dec16',
                appType: 2,
                appDate: moment(new Date()).format('YYYY/MM/DD'),
                enteredPerson: '1',
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
                },
                opStampRequestMode: 1,
                opReversionReason: '1',
                 opAppStartDate: '2020/08/07',
                 opAppEndDate: '2020/08/08',
                 opAppReason: 'jdjadja',
                 opAppStandardReasonCD: 1


            };
        model: Model;
        appDispInfoStartupOutput: any;
        dataFetch: KnockoutObservable<ModelDto> = ko.observable(null);
        mode: string = 'edit';

        created(params: any) {
            const vm = this;
            vm.application = ko.observable(new Application("", 1, [], 2, "", "", 0));
            vm.model = new Model(true, true, true, '001', 'WorkType', '001', 'WorkTime');
            vm.appDispInfoStartupOutput = ko.observable(CommonProcess.initCommonSetting());
            vm.application().appDate(moment(new Date()).format("YYYY/MM/DD"));
            

        }

        mounted() {
            const vm = this;
            vm.fetchData();
        }

        register() {
            const vm = this;
            console.log(vm.applicationTest);
            console.log(ko.toJS(vm.model));
            let model = ko.toJS(vm.model);
            let goBackApp = new GoBackApplication(
                    model.checkbox1 ? 1 : 0,
                    model.checkbox2 ? 1 : 0,                          
            ); 
            // is change can be null
            goBackApp.isChangedWork = model.checkbox3 ? 1 : 0 ;
            if (model.workTypeCode) {
               let dw = new DataWork(new InforType(model.workTypeCode, model.workTypeName));
               if (model.workTimeCode) {
                   dw.workTime = new InforWorkTime(model.workTimeCode, model.workTimeName);
               }
               goBackApp.dataWork = dw; 
                
            }
            console.log(goBackApp);
            
            let param = {
                companyId: this.$user.companyId,
                agentAtr: true,
                applicationDto: vm.applicationTest,
                goBackDirectlyDto: goBackApp,
                inforGoBackCommonDirectDto: ko.toJS(vm.dataFetch),
                mode : vm.mode == 'edit'
            };
            vm.$ajax(API.checkRegister,param)
                .done(res => {
                   console.log(res); 
                   let paramsRegister = {
                           companyId: vm.$user.companyId,
                           applicationDto: vm.applicationTest,
                           goBackDirectlyDto: goBackApp,
                           inforGoBackCommonDirectDto: ko.toJS(vm.dataFetch),
                           mode : vm.mode == 'edit'
                   }
                   if (_.isEmpty(res)) {
                       return vm.$ajax(API.register, paramsRegister)
                           .done(resRegister => {
                               console.log(resRegister);
                           })
                           .fail(errRegister => {
                               console.log(errRegister);
                           })
                   }
                })
                .fail(err => {
                    console.log(err);
                });
           


        }
        
        changeDate() {
            
        }

        fetchData() {
            const vm = this;
            vm.$blockui("show");
            let params = {
                ApplicantEmployeeID: null,
                ApplicantList: null
            }
            vm.$ajax(API.startNew, params)
                .done(res => {
                    console.log(res);
                    vm.dataFetch({
                        workType: ko.observable(res.workType),
                        workTime: ko.observable(res.workTime),
                        appDispInfoStartup: ko.observable(res.appDispInfoStartup),
                        goBackReflect: ko.observable(res.goBackReflect),
                        lstWorkType: ko.observable(res.lstWorkType),
                        goBackApplication: ko.observable(res.goBackApplication)
                    });
                    vm.appDispInfoStartupOutput(res.appDispInfoStartup);
                    vm.$blockui("hide");
                }).fail(err => {
                    vm.$blockui("hide");
                });
            
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
    export class InforType {
        workType: string;
        nameWorkType: string;
        constructor(code: string, name: string) {
            this.workType = code;
            this.nameWorkType = name;
        }
    }
    export class InforWorkTime {
        workTime: string;
        nameWorkTime: string;
        constructor(code: string, name: string) {
            this.workTime = code;
            this.nameWorkTime = name;
        }
    }
    export class DataWork {
        workType: InforType;
        workTime?: InforWorkTime;
        constructor(workType: InforType, workTime?: InforWorkTime) {
            this.workType = workType;
            this.workTime = workTime;
        }
    }
    export class GoBackReflect {
        companyId: string;
        reflectApplication: number;
    }
    export class ModelDto {

        workType: KnockoutObservable<any>;

        workTime: KnockoutObservable<any>;

        appDispInfoStartup: KnockoutObservable<any>;

        goBackReflect: KnockoutObservable<GoBackReflect> = ko.observable( null );

        lstWorkType: KnockoutObservable<any>;

        goBackApplication: KnockoutObservable<any>;
    }

    const API = {
        startNew: "at/request/application/gobackdirectly/getGoBackCommonSettingNew",
        checkRegister: "at/request/application/gobackdirectly/checkBeforeRegisterNew",
        register: "at/request/application/gobackdirectly/registerNewKAF009"
    }

    export class ApplicationStatus {
        //        反映しない
        public static DO_NOT_REFLECT: number = 0;
        //        反映する
        public static DO_REFLECT: number = 1;
        //      申請時に決める(初期値：反映しない)
        public static DO_NOT_REFLECT_1: number = 2;
        //        申請時に決める(初期値：反映する)
        public static DO_REFLECT_1: number = 3;
    }
    export class ParamBeforeRegister {
        companyId: string;
        agentAtr: boolean;
        applicationDto: any;
        goBackDirectlyDto: any;
        inforGoBackCommonDirectDto: any;

    }

}