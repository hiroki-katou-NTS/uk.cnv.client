module nts.uk.com.view.ccg031.b.viewmodel {
    import model = nts.uk.com.view.ccg.model;
    import util = nts.uk.util;
    import windows = nts.uk.ui.windows;
    import errors = nts.uk.ui.errors;
    import resource = nts.uk.resource;
    import block = nts.uk.ui.block;

    export class ScreenModel {
        // PGType
        pgType: number = 0;
        // Position
        positionRow: KnockoutObservable<number> = ko.observable(null);
        positionColumn: KnockoutObservable<number> = ko.observable(null);
        // TopPage Part Type
        listPartType: KnockoutObservableArray<any> = ko.observableArray([]);
        selectedPartType: KnockoutObservable<any> = ko.observable(null);
        //TopPage Part
        allPart: KnockoutObservableArray<model.TopPagePartDto> = ko.observableArray([]);
        listPart: KnockoutObservableArray<model.TopPagePartDto> = ko.observableArray([]);
        selectedPartID: KnockoutObservable<string> = ko.observable(null);
        selectedPart: KnockoutObservable<model.TopPagePartDto> = ko.observable(null);
        listPartColumn: any;
        // External Url
        isExternalUrl: KnockoutObservable<boolean> = ko.observable(false);
        urlWidth: KnockoutObservable<number> = ko.observable(null);
        urlHeight: KnockoutObservable<number> = ko.observable(null);
        url: KnockoutObservable<string> = ko.observable(null);
        // UI Binding
        instructionText: KnockoutObservable<string> = ko.observable('');
        constructor() {
            var self = this;
            // TopPage Part
            self.selectedPartType.subscribe((value) => {
                self.filterPartType(value);
            });
            self.selectedPartID.subscribe((value) => { self.changeSelectedPart(value); });
            self.listPartColumn = [
                { headerText: "ID", key: "topPagePartID", dataType: "string", hidden: true },
                { headerText: resource.getText("CCG031_27"), key: "code", dataType: "string", width: 50 },
                { headerText: resource.getText("CCG031_28"), key: "name", dataType: "string" },
            ];
        }

        /** Start Page */
        startPage(): JQueryPromise<any> {
            var self = this;
            var dfd = $.Deferred();
            block.invisible();
            // Shared
            self.pgType = windows.getShared("pgtype");
            self.positionRow(windows.getShared("size").row);
            self.positionColumn(windows.getShared("size").column);
            // Get Type and Part
            service.findAll(self.pgType).done((data: any) => {
                // Binding TopPage Part Type
                _.each(data.listTopPagePartType, (partType) => {
                    partType.localizedName = resource.getText(partType.localizedName);
                });
                self.listPartType(data.listTopPagePartType);
                // Binding TopPage Part
                self.allPart(data.listTopPagePart);
                // Default value
                self.selectedPartType(data.listTopPagePartType[0].value);
                self.selectFirstPart();
                dfd.resolve();
            }).fail((res) => {
                dfd.fail();
            }).always(() => {
                block.clear(); 
            });
            return dfd.promise();
        }

        /** Submit Dialog */
        submitDialog(): void {
            var self = this;
            if (self.isExternalUrl() === true)
            $(".nts-validate").trigger("validate");
            if (!$(".nts-validate").ntsError("hasError")) {
                var placement: model.Placement = self.buildReturnPlacement();
                windows.setShared("placement", placement, false);
                windows.close();
            }
        }

        /** Close Dialog */
        closeDialog(): void {
            windows.close();
        }

        /** Filter by Type */
        private filterPartType(partType: number): void {
            var isExternalUrl: boolean = (partType === 4);
            this.isExternalUrl(isExternalUrl);
            if (isExternalUrl !== true) {
                if (nts.uk.ui._viewModel)
                    $(".nts-validate").ntsError("clear");
                var listPart = _.chain(this.allPart()).filter(['type', partType]).sortBy("code").value();
                this.listPart(listPart);
                this.isExternalUrl(isExternalUrl);
                this.selectFirstPart();
            }
            // UI binding: Instruction Text
            if (partType === 0)
                this.instructionText(resource.getText("CCG031_17"));
            if (partType === 1)
                this.instructionText(resource.getText("CCG031_17"));
            if (partType === 2)
                this.instructionText(resource.getText("CCG031_18"));
            if (partType === 3)
                this.instructionText(resource.getText("CCG031_19"));
        }

        /** Change Selected Part */
        private changeSelectedPart(partID: string): void {
            if(!util.isNullOrUndefined(partID)){
                var selectedPart: model.TopPagePartDto = _.find(this.allPart(), ['topPagePartID', partID]);
                selectedPart.codeName = nts.uk.text.padLeft(selectedPart.code, '0', 4) + ' ' + selectedPart.name;
                this.selectedPart(selectedPart);
            } else {
                this.selectedPart(null);    
            }
        }

        /** Select first Part */
        private selectFirstPart(): void {
            var firstPart: model.TopPagePartDto = _.head(this.listPart());
            (firstPart !== undefined) ? this.selectedPartID(firstPart.topPagePartID) : this.selectedPartID(null);
        }

        /** Build a return Placement for LayoutSetting */
        private buildReturnPlacement(): model.Placement {
            var self = this;
            // Default is External Url
            var name: string = "";
            var width: number = self.urlWidth();
            var height: number = self.urlHeight();
            var topPagePartID: string = "";
            var topPagePartType: number = null;
            var url: string = self.url();

            // In case is TopPagePart
            if (self.selectedPartType() !== 4) {
                if (!util.isNullOrUndefined(self.selectedPart())) {
                    name = self.selectedPart().name;
                    width = self.selectedPart().width;
                    height = self.selectedPart().height;
                    topPagePartID = self.selectedPart().topPagePartID;
                    topPagePartType = self.selectedPart().type;
                    url = "";
                }
                else {
                    return null;
                }
            }

            var placement: model.Placement = new model.Placement(
                util.randomId(), name,
                self.positionRow(), self.positionColumn(),
                width, height, url, topPagePartID, topPagePartType);
            return placement;
        }

    }

}