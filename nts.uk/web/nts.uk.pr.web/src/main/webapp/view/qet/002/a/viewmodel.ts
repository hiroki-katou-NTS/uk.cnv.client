module qet002.a.viewmodel {
    export class ScreenModel {
        targetYear: KnockoutObservable<number>;
        isLowerLimit: KnockoutObservable<boolean>;
        lowerLimitValue: KnockoutObservable<number>;
        isUpperLimit: KnockoutObservable<boolean>;
        upperLimitValue: KnockoutObservable<number>;
        
        constructor() {
            this.targetYear = ko.observable(2016);
            this.isLowerLimit = ko.observable(true);
            this.isUpperLimit = ko.observable(true);
            this.lowerLimitValue = ko.observable(null);
            this.upperLimitValue = ko.observable(null);
        }
        
        public start(): JQueryPromise<any>{
            var dfd = $.Deferred<any>();
            dfd.resolve();
            return dfd.promise();
        }
        
    }
}