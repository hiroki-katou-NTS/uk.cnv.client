/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.view.kmk013.k {
    
    interface IParams {
        date: Date;
    }

    @bean()
    export class ViewModel extends ko.ViewModel {

        public data: KnockoutObservable<Date | null> = ko.observable(new Date());

        created(param: IParams) {
            const vm = this;
            if (param) {
                vm.data(param.date)
            }
        }

        mounted() {
            const vm = this;
            $('.data-input').focus();
        }

        done() {
            const vm = this;
            vm.$window.close(ko.unwrap(vm.data));
        }

        cancel() {
            const vm = this;
            vm.$window.close();
        }
    }
}
