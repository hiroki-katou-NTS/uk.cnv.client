module nts.uk.at.view.ksm001.e {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function(res) {
            __viewContext.bind(screenModel);
            $('#ckb-employment').focus();
        });
    });
}