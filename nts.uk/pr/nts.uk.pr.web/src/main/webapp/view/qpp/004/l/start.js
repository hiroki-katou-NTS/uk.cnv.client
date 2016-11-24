__viewContext.ready(function () {
    var screenModel = new qpp004.l.viewmodel.ScreenModel();
    var data = nts.uk.ui.windows.getShared("data");
    this.bind(screenModel);
    screenModel.startPage(data).done(function () {
    });
    $("#timer").html(nts.uk.text.formatSeconds(0, "hh:mm:ss:ms"));
});
