/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.ui.sample.layout.a {

    @bean()
    export class ViewModel extends ko.ViewModel {
        text: KnockoutObservable<string> = ko.observable('Hello');

        listbox: KnockoutObservableArray<Item> = ko.observableArray([]);


        created() {
            const vm = this;
            const items: Item[] = [];

            for (var i = 1; i <= 100; i++) {
                const code = _.padStart(i.toString(), 3, '0');

                items.push({ code, name: `Name  ${code}`, abolition: false });
            }

            vm.listbox(items);
        }

        click() {
            const vm = this;

            vm.$dialog.error({ messageId: 'MsgB_1', messageParams: [vm.text()] });
        }
    }


    interface Item {
        code: string;
        name: string;
        abolition: boolean;
    }
}