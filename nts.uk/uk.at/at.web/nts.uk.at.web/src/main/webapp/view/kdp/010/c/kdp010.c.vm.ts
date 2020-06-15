module nts.uk.at.view.kdp010.c {
    import getText = nts.uk.resource.getText;
    export module viewmodel {
        export class ScreenModel {
            correcValue: KnockoutObservable<number> = ko.observable(10);
            letterColors: KnockoutObservable<string> = ko.observable("#ffffff");
            backgroundColors: KnockoutObservable<string> = ko.observable("#0033cc");
            stampValue: KnockoutObservable<number> = ko.observable(3);
            highlightOption: KnockoutObservableArray<any> = ko.observableArray([
                { id: 1, name: getText("KDP010_241") },
                { id: 0, name: getText("KDP010_242") }
            ]);
            highlight: KnockoutObservable<number> = ko.observable(0);
            externalMapOption: KnockoutObservableArray<any> = ko.observableArray([
                { id: 1, name: getText("KDP010_187") },
                { id: 0, name: getText("KDP010_188") }
            ]);
            externalMap: KnockoutObservable<number> = ko.observable(0);
            constructor(){
                let self = this;
            }
         }   
    }
}