/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.at.kmr001.c {

    const API = {
        SETTING: 'at/record/stamp/management/personal/startPage',
        HIGHTLIGHT: 'at/record/stamp/management/personal/stamp/getHighlightSetting',
        GET_ALL : 'ctx/at/schedule/shift/pattern/daily/getall'
    };

    const PATH = {
        REDIRECT: '/view/ccg/008/a/index.xhtml',
        KMR001_D: '/view/kmr/001/d/index.xhtml'
    }

    @bean()
    export class Kmr001CVmViewModel extends ko.ViewModel {

        items: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        currentCode: KnockoutObservable<any> = ko.observable();
        currentCodeList: KnockoutObservableArray<any> = ko.observableArray([]);

        //C6_2
        lunchBoxTextbox: KnockoutObservable<string> = ko.observable("");
        //C7_2
        amount1Textbox: KnockoutObservable<string> = ko.observable("");
        //C8_2
        amount2Textbox: KnockoutObservable<string> = ko.observable("");
        //C9_2
        unitTextbox: KnockoutObservable<string> = ko.observable("");
        //C10_2
        maxNumberOfReservationsTxtbox: KnockoutObservable<string> = ko.observable("");

        constructor() {
            super();
            var vm = this;
            for(let i = 1; i < 41; ++i){
                vm.items.push(new ItemModel(i, 'data ' + i));
            }
        }

        deselectAll() {
            this.currentCode(null);
            this.currentCodeList.removeAll();
        }

        removeItem() {
            this.items.shift();
        }

        created() {
            const vm = this;
            _.extend(window, { vm });
        }

        getListWorkingCycle() {
            let vm = this;
            vm.$ajax(API.GET_ALL).done((dataRes: Array<ReservedItemDto>) => {
                if (dataRes === undefined || dataRes.length == 0) {
                    vm.items([]);
                    // vm.switchNewMode();
                } else {
                    vm.items(dataRes);
                }
            });
        }

        openConfigHisDialog() {
            let vm = this;
            vm.$blockui('invisible');
            vm.$window.modal('at', PATH.KMR001_D, {});
            vm.$blockui('clear');
            //block.invisible();
            //block.invisible();
            //setShared('KMR001_C_PARAMS', { });
            // modal(PATH.KMR001_D).onClosed(function() {
            //     let params = getShared('KMR001_C_PARAMS');
            // });
            //block.clear();
        }
    }

    class ItemModel {
        id: number;
        name: string;

        constructor(id: number, name: string) {
            this.id = id;
            this.name = name;
        }
    }

    interface ReservedItemDto {
        id: number;
        name: string;
    }

    class BentoMenuSetting{

    }

}

