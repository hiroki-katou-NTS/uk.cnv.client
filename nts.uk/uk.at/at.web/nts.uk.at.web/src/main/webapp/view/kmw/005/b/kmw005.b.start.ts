module nts.uk.at.view.kmw005.b {
    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            $("#selUseClassification").focus();
        }); 
    });
}