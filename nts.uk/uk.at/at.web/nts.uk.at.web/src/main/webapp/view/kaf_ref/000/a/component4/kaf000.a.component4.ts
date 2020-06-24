module nts.uk.at.view.kaf000_ref.a.component4.viewmodel {

    @component({
        name: 'kaf000-a-component4',
        template: '/nts.uk.at.web/view/kaf_ref/000/a/component4/index.html'
    })
    class Kaf000AComponent4ViewModel extends ko.ViewModel {
        prePostAtr: KnockoutObservable<number>;
        appType: number;
        appDate: KnockoutObservable<any>;
        appDispInfoStartupOutput: any;
        created(params: any) {
            const vm = this;
            vm.prePostAtr = params.application().prePostAtr;
            vm.appDate = params.application().appDate;
            vm.appDispInfoStartupOutput = params.appDispInfoStartupOutput;
            vm.appDispInfoStartupOutput.subscribe(value => {
                vm.prePostAtr(value.appDispInfoWithDateOutput.prePostAtr);
            });
        }
    
        mounted() {
            const vm = this;
        }
    }
}