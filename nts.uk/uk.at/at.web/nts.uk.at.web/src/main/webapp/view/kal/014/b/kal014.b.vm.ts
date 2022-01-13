
module nts.uk.at.kal014.b {

    import common=nts.uk.at.kal014.common;

    @bean()
    export class Kal014BViewModel extends ko.ViewModel {
        modalDTO: ModalDto = new ModalDto();
        isStartDateEnable: KnockoutObservable<boolean>;
        isEndDateEnable: KnockoutObservable<boolean>;
        strComboMonth: KnockoutObservableArray<any>;
        endComboMonth: KnockoutObservableArray<any>;
        workPalceCategory: any;

        constructor(params: any) {
            super();
            const vm = this;
            let strMonth = 0, endMonth = 0;
            vm.workPalceCategory = common.WORKPLACE_CATAGORY;

            if (params.alarmCategory === vm.workPalceCategory.MASTER_CHECK_BASIC
                || params.alarmCategory === vm.workPalceCategory.MASTER_CHECK_WORKPLACE){
                strMonth = params.listExtractionMonthly.strMonth;
                endMonth = params.listExtractionMonthly.endMonth;
            } else {
                strMonth = params.singleMonth.monthNo;
            }
            vm.modalDTO.categoryId(params.alarmCategory);
            vm.modalDTO.categoryName(params.alarmCtgName);
            vm.modalDTO.startMonth(strMonth);
            vm.modalDTO.endMonth(endMonth);
            vm.isStartDateEnable = ko.observable(vm.checkStartDateISEnable());
            vm.isEndDateEnable = ko.observable(vm.checkEndDateISEnable());
            vm.strComboMonth = ko.observableArray(params.alarmCategory === vm.workPalceCategory.MONTHLY
                ? __viewContext.enums.MonthlySingleMonthSpecifiedMonth
                : __viewContext.enums.MonthlyClosingMonthSpecifiedMonth
            );
            vm.endComboMonth = ko.observableArray(__viewContext.enums.MonthlyClosingMonthSpecifiedMonth);
        }

        created(params: any) {
            const vm = this;
        }

        mounted(){
            $('#combo-box').focus();
        }
        /**
         * This function is responsible to check start month disable enable check
         *
         * @return boolean
         **/
        checkStartDateISEnable(): boolean {
            const vm = this;
            return (vm.modalDTO.categoryId() === vm.workPalceCategory.MASTER_CHECK_BASIC
                || vm.modalDTO.categoryId() === vm.workPalceCategory.MASTER_CHECK_WORKPLACE
                || vm.modalDTO.categoryId() === vm.workPalceCategory.MONTHLY);
        }

        /**
         * This function is responsible to check end month disable enable check
         *
         * @return boolean
         **/
        checkEndDateISEnable(): boolean {
            const vm = this;
            return (vm.modalDTO.categoryId() === vm.workPalceCategory.MASTER_CHECK_BASIC
                || vm.modalDTO.categoryId() === vm.workPalceCategory.MASTER_CHECK_WORKPLACE);
        }

        /**
         * This function is responsible to close the modal
         *
         * @return type void         *
         * */
        cancel_Dialog(): any {
            const vm = this;
            vm.$window.close({
            });
        }

        /**
         * This function is responsible to perform modal data action
         *
         * @return type void         *
         * */
        decide(): void{
           const vm = this;
           if (vm.checkPeriod()) {
                let shareData: Result  = {
                    alarmCategory: vm.modalDTO.categoryId(),
                    strMonth: vm.modalDTO.startMonth(),
                    endMonth: vm.modalDTO.endMonth()
                }

                vm.$window.close({
                    shareData
                });
            }
        }

        /**
         * This function is responsible to error validation check
         *
         * @return type void               *
         * */
        checkPeriod(): boolean {
            var vm = this;
            if ((vm.modalDTO.categoryId() === vm.workPalceCategory.MASTER_CHECK_BASIC
                    || vm.modalDTO.categoryId() === vm.workPalceCategory.MASTER_CHECK_WORKPLACE)
                && vm.modalDTO.startMonth() > vm.modalDTO.endMonth()) {
                vm.$dialog.error({messageId: "Msg_812"}).then(() => {
                    return false;
                });
            } else if (vm.modalDTO.categoryId() === vm.workPalceCategory.MONTHLY && vm.modalDTO.startMonth() < vm.modalDTO.endMonth()) {
                vm.$dialog.error({messageId: "Msg_812"}).then(() => {
                    return false;
                });
            } else {
                return true;
            }
        }
    }
    interface Result {
        alarmCategory: number;
        strMonth: number;
        endMonth: number;
    }

    class ModalDto {
        categoryId: KnockoutObservable<any>;
        categoryName: KnockoutObservable<string>;
        extractionPeriod: KnockoutObservable<string>;
        startMonth: KnockoutObservable<number>;
        endMonth: KnockoutObservable<number>;

        constructor() {
            this.categoryId = ko.observable('');
            this.categoryName = ko.observable('');
            this.startMonth = ko.observable(0);
            this.endMonth = ko.observable(0);
            this.extractionPeriod = ko.observable('');
        }
    }
}