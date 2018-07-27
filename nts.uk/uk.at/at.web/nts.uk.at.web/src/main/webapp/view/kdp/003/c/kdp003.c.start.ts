module nts.uk.at.view.kdp003.c {
    __viewContext.ready(function() {
        let screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            $("#kdp003-grid").igGrid({
                columns: [
                    { headerText: nts.uk.resource.getText("KDP003_40"), key: "wkpCode", dataType: "string" },
                    { headerText: nts.uk.resource.getText("KDP003_41"), key: "wkpName", dataType: "string" },
                    { headerText: nts.uk.resource.getText("KDP003_42"), key: "empCode", dataType: "string" },
                    { headerText: nts.uk.resource.getText("KDP003_43"), key: "empName", dataType: "string" },
                    { headerText: nts.uk.resource.getText("KDP003_44"), key: "cardNo", dataType: "string" },
                    { headerText: nts.uk.resource.getText("KDP003_45"), key: "date", dataType: "string" },
                    { headerText: nts.uk.resource.getText("KDP003_46"), key: "time", dataType: "string" },
                    { headerText: nts.uk.resource.getText("KDP003_47"), key: "atdType", dataType: "string", hidden: !screenModel.hiddentOutputEmbossMethod() },
                    { headerText: nts.uk.resource.getText("KDP003_48"), key: "workTimeZone", dataType: "string", hidden: !screenModel.hiddentOutputWorkHours() },
                    { headerText: nts.uk.resource.getText("KDP003_49"), key: "installPlace", dataType: "string", hidden: !screenModel.hiddentOutputSetLocation() },
                    { headerText: nts.uk.resource.getText("KDP003_50"), key: "localInfor", dataType: "string", hidden: !screenModel.hiddentOutputPosInfor() },
                    { headerText: nts.uk.resource.getText("KDP003_51"), key: "otTime", dataType: "string", hidden: !screenModel.hiddentOutputOT() },
                    { headerText: nts.uk.resource.getText("KDP003_52"), key: "midnightTime", dataType: "string", hidden: !screenModel.hiddentOutputNightTime() },
                    { headerText: nts.uk.resource.getText("KDP003_53"), key: "supportCard", dataType: "string", hidden: !screenModel.hiddentOutputSupportCard() },
                ],
                features: [{
                    name: 'Selection',
                    mode: 'row',
                    multipleSelection: false,
                    activation: false,
                    rowVirtualization: true,
                    rowSelectionChanged: screenModel.selectionChanged.bind(screenModel)
                },
                    { name: 'Sorting', type: 'local' },
                    {
                        name: 'Paging',
                        pageSize: 20,
                        currentPageIndex: 0
                    },
                ],
                virtualization: true,
                virtualizationMode: 'continuous',
                width: "1450px",
                height: "550px",
                primaryKey: "wkpCode",
                dataSource: screenModel.dataSource
            });
            $("#kdp003-grid").closest('.ui-iggrid').addClass('nts-gridlist');
            $("#kdp003-grid").setupSearchScroll("igGrid", true);
            $("#kdp003-grid").igGrid("dataBind");

        });
    });
}
