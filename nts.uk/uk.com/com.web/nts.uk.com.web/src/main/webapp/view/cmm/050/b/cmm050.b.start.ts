module nts.uk.com.view.cmm050.b {
  __viewContext.ready(function () {
    let screenModel = new viewmodel.ScreenModel();
    __viewContext.bind(screenModel);
    setTimeout(() => $("#B3_2").focus(), 500);
  });
}
