module nts.uk.at.view.kal003.a.tab {
    import windows = nts.uk.ui.windows;
    import getText = nts.uk.resource.getText;
    import block = nts.uk.ui.block;
    import model = nts.uk.at.view.kal003.share.model;
    import shareutils = nts.uk.at.view.kal003.share.kal003utils;

    export class AgreementErrorTab {
        listAgreementError: KnockoutObservableArray<model.AgreeConditionErrorDto> = ko.observableArray([]);
        category: KnockoutObservable<number>;
        constructor(category: number, listAgreementError?: Array<model.AgreeConditionErrorDto>) {
            let self = this;
            self.category = ko.observable(category);
            if (listAgreementError) {
                self.listAgreementError(listAgreementError);
            }

            $("#fixed-table-agreement-error").ntsFixedTable({ height: 416, width: 220 });
        }

    }
}//end tab



