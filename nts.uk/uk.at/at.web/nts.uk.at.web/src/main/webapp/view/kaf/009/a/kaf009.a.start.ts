module nts.uk.at.view.kaf009.a {
    __viewContext.ready(function() {
        var screenModel = new nts.uk.at.view.kaf009.a.viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
        });
    });
}