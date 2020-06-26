module nts.uk.at.view.kaf009_ref.a.viewmodel {
    import Application = nts.uk.at.view.kaf000_ref.shr.viewmodel.Application;
    import CommonProcess = nts.uk.at.view.kaf000_ref.shr.viewmodel.CommonProcess;
    import Model = nts.uk.at.view.kaf009_ref.shr.viewmodel.Model;
    
    @bean()
    class Kaf009AViewModel extends ko.ViewModel {
        
        application: KnockoutObservable<Application>;
        model: Model;
        commonSetting: any;
        appDispInfoStartupOutput: any;
        dataFetch: KnockoutObservable<ModelDto>;
    
        created(params: any) {
            const vm = this;
            vm.application = ko.observable(new Application("", 1, 2, ""));   
            vm.commonSetting = ko.observable(CommonProcess.initCommonSetting());
            vm.model = new Model(true, true, '01', 'ddd','30','33');
            vm.appDispInfoStartupOutput = ko.observable(CommonProcess.initCommonSetting());
            this.fetchData();
            
        }
    
        mounted() {
            
        }
        
        register() {
            const vm = this;
            console.log(ko.toJS(vm.application()));
            console.log(this.model);
//            vm.model(new Model(true, true, '01', 'hoang','30','33'));
        }
        
        fetchData() {
            this.dataFetch = ko.observable({
                workType: ko.observable({workType:'001', nameWorkType:'Work Type'}),
                workTime: ko.observable({workTime:'001', nameWorkTime:'Work Ttime'}),
                appDispInfoStartup: ko.observable(CommonProcess.initCommonSetting()),
                goBackReflect: ko.observable(null),
                lstWorkType: ko.observable(null),
                goBackApplication: ko.observable(new Application("", 1, 2, ""))
            })
        }
        
    }
    export class ModelDto {
        
        workType: KnockoutObservable<any>;
    
        workTime: KnockoutObservable<any>;
        
        appDispInfoStartup: KnockoutObservable<any>;
    
        goBackReflect: KnockoutObservable<any>;
    
        lstWorkType: KnockoutObservable<any>;
    
        goBackApplication: KnockoutObservable<any>;
    }
    
    const API = {
        startNew: "at/request/application/workchange/startNew"
    }
}