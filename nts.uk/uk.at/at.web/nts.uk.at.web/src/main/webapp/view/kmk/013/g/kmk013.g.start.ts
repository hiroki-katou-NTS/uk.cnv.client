module nts.uk.at.view.kmk013.g {
    __viewContext.ready(function() {
        var screenModel = new g.viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            setTimeout(() => $("#G3_2").focus(), 500);
        });
    });
}
