/*!@license
* Infragistics.Web.ClientUI Grid localization resources 19.1.20
*
* Copyright (c) 2011-2019 Infragistics Inc.
*
* http://www.infragistics.com/
*
*/
(function(factory){if(typeof define==="function"&&define.amd){define(["jquery"],factory)}else{return factory(jQuery)}})(function($){$.ig=$.ig||{};$.ig.locale=$.ig.locale||{};$.ig.locale.bg=$.ig.locale.bg||{};$.ig.Grid=$.ig.Grid||{};$.ig.locale.bg.Grid={noSuchWidget:"{featureName} не е разпознато. Уверете се, че съществува такава функция, и че е правилно изписана.",autoGenerateColumnsNoRecords:"autoGenerateColumns е включено, но няма записи в източника на данни. Заредете източник на данни със записи за да може да бъдат определени колоните.",optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",optionChangeNotScrollingGrid:"{optionName} не може да бъде променяно след инициализация защото първоначално в мрежата не е възможно скролиране и има необходимост от пълно повторно рендиране. Тази опция трябва да бъде настроена по време на инициализацията.",widthChangeFromPixelsToPercentagesNotSupported:"Опцията width на мрежата на може да бъде сменяна динамично от пиксели в проценти.",widthChangeFromPercentagesToPixelsNotSupported:"Опцията width на мрежата на може да бъде сменяна динамично от проценти в пиксели.",noPrimaryKeyDefined:"Няма дефиниран primaryKey за мрежата. primaryKey трябва да бъде дефиниран за да може да бъдат използвани функционалности като GridEditing.",indexOutOfRange:"Индексът на ред, който сте въвели, е извън големината на колекцията. Индексът на ред трябва да бъде в границите между {0} и {max}.",noSuchColumnDefined:"Зададеният ключ за колоната не е валиден. Трябва да бъде подаден ключ, който съвпада с ключа на една от дефинираните колони на мрежата.",columnIndexOutOfRange:"Индексът на колоната, който сте въвели, е извън големината на колекцията. Индексът на колона трябва да бъде в границите между {0} и {max}.",recordNotFound:"Записът с id {id} не може да бъде намерен в колекцията от данни: Проверете ключа използван за търсенето и го променете ако е необходимо.",columnNotFound:"Не е намерена колона с ключ {key}. Проверете ключа използван за търсенето и го променете ако е необходимо.",colPrefix:"Колона ",columnVirtualizationRequiresWidth:"Виртуализация и columnVirtualization изискват ширината на мрежата или колоните ѝ да бъде настроена. Задайте стойност за ширината на мрежата, defaultColumnWidth или ширината на всяка колона.",virtualizationRequiresHeight:"Виртуализацията изисква височината на мрежата да бъде настроена. Трябва да бъде подадена стойност за височината на мрежата.",colVirtualizationDenied:'columnVirtualization изисква различна настройка на virtualizationMode. virtualizationMode трябва да бъде зададен като "fixed".',noColumnsButAutoGenerateTrue:"autoGenerateColumns не е включено и няма колони, които да са дефинирани за мрежата. Включете autoGenerateColumns или задайте колони ръчно.",expandTooltip:"Отвори реда",collapseTooltip:"Затвори реда",movingNotAllowedOrIncompatible:"Зададената колона не може да бъде премахната. Уверете се, че такава колона съществува, и че крайната ѝ позиция няма да наруши оформлението на колоните.",allColumnsHiddenOnInitialization:"Всички колони не могат да бъдат скрити по време на инициализация. Поне една колона трябва да бъде настроена като видима.",columnVirtualizationNotSupportedWithPercentageWidth:"Виртуализацията изисква конфигурация за ширината на колоната различна от '*'. Ширината на колоната трябва да бъде зададена като число в пиксели.",mixedWidthsNotSupported:"ColumnVirtualization изисква различна настройка на ширината на мрежата. Ширината на колоната трябва да бъде зададена като число в пиксели.",virtualizationNotSupportedWithAutoSizeCols:"Ширината на всички колони трябва да бъде зададена по един и същи начин. Задайте ширините на колоните като проценти или като числов пиксели.",multiRowLayoutColumnError:"Колона с ключ: {key1} не може да бъде добавена към multi-row-layout защото мястото ѝ в оформлението вече е заето от колона с ключ: {key2}.",multiRowLayoutNotComplete:"multi-row-layout не е завършен. Дефинирането на колона създава оформление, което има празни интервали и не може да бъде рендирано правилно.",multiRowLayoutMixedWidths:"В Multi-Row Layout не се поддържат смесени ширини (проценти и пиксели). Задайте всички ширини на колоните или в пиксели, или в проценти.",multiRowLayoutHidingNotSupported:"В Multi-Row Layout не се поддържат скрити колони. Моля премахнете скритите колони от дефинициите на колоните.",scrollableGridAreaNotVisible:"Фиксираните горен колонтитул и долен колонтитул са по-големи от наличната височина на мрежата. Зоната, която може да се превърта, не е видима. Задайте по-голяма височина на мрежата.",featureNotSupportedWithMRL:"{featureName} не се поддържа в Multi-Row Layout. Моля премахнете функцията от списъка с функции.",editorTypeCannotBeDetermined:"Обновяването нямаше достатъчно информация за да определи типа на редактора, който да използва за колона: "};$.ig.HierarchicalGrid=$.ig.HierarchicalGrid||{};$.ig.locale.bg.HierarchicalGrid={noPrimaryKey:"igHierarchicalGrid изисква primaryKey да бъде дефиниран. Опцията primaryKey на мрежата трябва да бъде настроена.",expandTooltip:"Отвори реда",collapseTooltip:"Затвори реда"};$.ig.GridFeatureChooser=$.ig.GridFeatureChooser||{};$.ig.locale.bg.GridFeatureChooser={featureChooserTooltip:"Избор на функции"};$.ig.GridFiltering=$.ig.GridFiltering||{};$.ig.locale.bg.GridFiltering={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",startsWithNullText:"Започва с...",endsWithNullText:"Завършва на...",containsNullText:"Съдържа...",doesNotContainNullText:"Не съдържа...",equalsNullText:"Равно на...",doesNotEqualNullText:"Не е равно на...",greaterThanNullText:"По-голямо от...",lessThanNullText:"По-малко от...",greaterThanOrEqualToNullText:"По-голямо или равно на...",lessThanOrEqualToNullText:"По-малко или равно на...",onNullText:"На...",notOnNullText:"Не на...",afterNullText:"След",beforeNullText:"Преди",emptyNullText:"Празно",notEmptyNullText:"Не е празно",nullNullText:"Null",notNullNullText:"Не е null",emptyLabel:"Празно",notEmptyLabel:"Не е празно",nullLabel:"Null",notNullLabel:"Не е null",startsWithLabel:"Започва с",endsWithLabel:"Завършва на",containsLabel:"Съдържа",doesNotContainLabel:"Не съдържа",equalsLabel:"Равно на",doesNotEqualLabel:"Не е равно на",greaterThanLabel:"По-голямо от",lessThanLabel:"По-малко от",greaterThanOrEqualToLabel:"По-голямо или равно на",lessThanOrEqualToLabel:"По-малко или равно на",trueLabel:"True",falseLabel:"False",afterLabel:"След",beforeLabel:"Преди",todayLabel:"Днес",yesterdayLabel:"Вчера",thisMonthLabel:"Този месец",lastMonthLabel:"Миналият месец",nextMonthLabel:"Следващият месец",thisYearLabel:"Тази година",lastYearLabel:"Миналата година",nextYearLabel:"Следващата година",atLabel:"At",atNullText:"At...",notAtLabel:"Not at",notAtNullText:"Not at...",atBeforeLabel:"At or before",atBeforeNullText:"At or before...",atAfterLabel:"At or after",atAfterNullText:"At or after...",clearLabel:"Изчисти филтърът",noFilterLabel:"Не",onLabel:"На",notOnLabel:"Не на",advancedButtonLabel:"Подробно",filterDialogCaptionLabel:"Подробен филтър",filterDialogConditionLabel1:"Покажи записите отговарящи на ",filterDialogConditionLabel2:" от следните критерий",filterDialogConditionDropDownLabel:"Условие за филтриране",filterDialogOkLabel:"Търси",filterDialogCancelLabel:"Отказ",filterDialogAnyLabel:"Който и да е",filterDialogAllLabel:"Всички",filterDialogAddLabel:"Прибави",filterDialogErrorLabel:"Беше достигнат максималния брой на поддържани филтри.",filterDialogCloseLabel:"Затваряне на диалога за филтриране",filterSummaryTitleLabel:"Резултати от търсенето",filterSummaryTemplate:"${matches} отговарящи записи",filterDialogClearAllLabel:"Изчисти всички филтри",tooltipTemplate:"${condition} филтър приложен",featureChooserText:"Скрий филтъра",featureChooserTextHide:"Покажи филтъра",featureChooserTextAdvancedFilter:"Подробен филтър",virtualizationSimpleFilteringNotAllowed:'ColumnVirtualization изисква различен тип на филтрацията. Настройте режима на филтриране на "advanced" или изключете advancedModeEditorsVisible.',multiRowLayoutSimpleFilteringNotAllowed:"Multi-row-layout изисква различен тип на филтриране. Задайте режима на филтриране на 'подробен'.",featureChooserNotReferenced:"Липсва референция към FeatureChooser. Реферирайте infragistics.ui.grid.featurechooser.js във Вашия проект, използвайте зареждаща служебна програма (loader) или един от комбинираните скриптови файлове.",conditionListLengthCannotBeZero:"Масивът conditionList в columnSettings е празен. Трябва да бъде подаден подходящ масив за conditionList.",conditionNotValidForColumnType:"Условието '{0}' не е подходящо за конкретната конфигурация. Трябва да бъде заменено с условие подходящо за колона тип {1}.",defaultConditionContainsInvalidCondition:"defaultExpression за колона '{0}' съдържа условие, което не е позволено. Трябва да бъде заменено с условие подходящо за колона тип {0}.",initialConditionIsNotInTheConditionsListArrayOrIsNotInTheDefaultConditions:"Началното условие за колона '{0}', зададено в columnSettings, не е от условията по подразбиране (или персонализираните условия), или не се намира в conditionList, зададен в columnSettings. Моля задайте валидно условие."};$.ig.GridGroupBy=$.ig.GridGroupBy||{};$.ig.locale.bg.GridGroupBy={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",emptyGroupByAreaContent:"Издърпайте колона до тук или {0} за да групирате",emptyGroupByAreaContentSelectColumns:"изберете колони",emptyGroupByAreaContentSelectColumnsCaption:"изберете колони",expandTooltip:"Отвори групирания ред",collapseTooltip:"Затвори групирания ред",removeButtonTooltip:"Премахни групирането по тази колона",modalDialogCaptionButtonDesc:"Сортиране във възходящ ред",modalDialogCaptionButtonAsc:"Сортиране във низходящ ред",modalDialogCaptionButtonUngroup:"Премахване на групирането",modalDialogGroupByButtonText:"Групирай",modalDialogCaptionText:"Прибави към групирането",modalDialogDropDownLabel:"Показване:",modalDialogClearAllButtonLabel:"изчисти всички",modalDialogRootLevelHierarchicalGrid:"Най-горно ниво",modalDialogDropDownButtonCaption:"Покажи/Скрий",modalDialogButtonApplyText:"Приложи",modalDialogButtonCancelText:"Отказ",fixedVirualizationNotSupported:'GroupBy изисква още една виртуализационна настройка. virtualizationMode трябва да бъде зададен като "continuous".',summaryRowTitle:"Групиране на сумарния ред",summaryIconTitle:"Обобщение за {0}: {1}"};$.ig.GridHiding=$.ig.GridHiding||{};$.ig.locale.bg.GridHiding={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",columnChooserDisplayText:"Избиране на колони",hiddenColumnIndicatorTooltipText:"Скрита колона/колони",columnHideText:"Скрий",columnChooserCaptionLabel:"Избиране на колони",columnChooserCheckboxesHeader:"покажи",columnChooserColumnsHeader:"колона",columnChooserCloseButtonTooltip:"Затвори",hideColumnIconTooltip:"Скрий",featureChooserNotReferenced:"Липсва референция към FeatureChooser. Реферирайте infragistics.ui.grid.featurechooser.js във Вашия проект или един от комбинираните скриптови файлове.",columnChooserShowText:"Покажи",columnChooserHideText:"Скрий",columnChooserResetButtonLabel:"върни към начално състояние",columnChooserButtonApplyText:"Приложи",columnChooserButtonCancelText:"Отказ"};$.ig.GridResizing=$.ig.GridResizing||{};$.ig.locale.bg.GridResizing={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",noSuchVisibleColumn:"Не съществува видима колона със зададения ключ. Методът showColumn() трябва да бъде използван върху колоната преди да се опитате да промените размерите ѝ.",resizingAndFixedVirtualizationNotSupported:'Оразмеряването на колоните изисква различни настройки на виртуализация. Използвайте rowVirtualization или настройте virtualizationMode на "continuous".'};$.ig.GridPaging=$.ig.GridPaging||{};$.ig.locale.bg.GridPaging={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",pageSizeDropDownLabel:"Покажи ",pageSizeDropDownTrailingLabel:"записи",nextPageLabelText:"следваща",prevPageLabelText:"предишна",firstPageLabelText:"",lastPageLabelText:"",currentPageDropDownLeadingLabel:"Страница",currentPageDropDownTrailingLabel:"от ${count}",currentPageDropDownTooltip:"Избиране на страница",pageSizeDropDownTooltip:"Избиране на брой записи на страница",pagerRecordsLabelTooltip:"Граници на показаните записи",prevPageTooltip:"Предишна страница",nextPageTooltip:"Следваща страница",firstPageTooltip:"Първа страница",lastPageTooltip:"Последна Страница",pageTooltipFormat:"страница ${index}",pagerRecordsLabelTemplate:"${startRecord} - ${endRecord} от ${recordCount} записа",invalidPageIndex:"Зададеният индекс на страница е невалиден. Задайте индекс на страница, който е по-голя или равен на 0 или по-малък от общия брой страници."};$.ig.GridSelection=$.ig.GridSelection||{};$.ig.locale.bg.GridSelection={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",persistenceImpossible:"Избора на състояние изисква различна конфигурация. Опцията primaryKey на мрежата трябва да бъде настроена."};$.ig.GridRowSelectors=$.ig.GridRowSelectors||{};$.ig.locale.bg.GridRowSelectors={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",selectionNotLoaded:"igGridSelection не е бил инициализиран. Селектирането трябва да бъде включено за мрежата.",columnVirtualizationEnabled:'RowSelectors изисква различна настройка на виртуализацията. Използвайте rowVirtualization и настройте virtualizationMode на "continuous".',selectedRecordsText:"Вие избрахте ${checked} записи.",deselectedRecordsText:"Вие отмаркирахте ${checked} записи.",selectAllText:"Избор на всички ${totalRecordsCount} записи",deselectAllText:"Отмаркиране на всички ${totalRecordsCount} записи",requireSelectionWithCheckboxes:"Необходима е селекция когато квадратчетата за отметка са включени."};$.ig.GridSorting=$.ig.GridSorting||{};$.ig.locale.bg.GridSorting={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",sortedColumnTooltip:"сортирано ${direction}",unsortedColumnTooltip:"Сортиране на колона",ascending:"възходящ ред",descending:"низходящ ред",modalDialogSortByButtonText:"сортирай по",modalDialogResetButton:"върни към начално състояние",modalDialogCaptionButtonDesc:"Натиснете, за да сортирате в низходящ ред",modalDialogCaptionButtonAsc:"Натиснете, за да сортирате във възходящ ред",modalDialogCaptionButtonUnsort:"Натиснете, за да премахнете сортирането",featureChooserText:"Сортиране",modalDialogCaptionText:"Сортиране по множество колони",modalDialogButtonApplyText:"Приложи",modalDialogButtonCancelText:"Отказ",sortingHiddenColumnNotSupport:"Зададената колона не може да бъде сортирана защото е скрита. Използвайте метода showColumn() преди да се опитате да я сортирате.",featureChooserSortAsc:"Сортиране във възходящ ред",featureChooserSortDesc:"Сортиране във низходящ ред"};$.ig.GridSummaries=$.ig.GridSummaries||{};$.ig.locale.bg.GridSummaries={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",featureChooserText:"Скрий обобщение",featureChooserTextHide:"Покажи обобщение",dialogButtonOKText:"OK",dialogButtonCancelText:"Отказ",emptyCellText:"",summariesHeaderButtonTooltip:"Покажи/Скрий обобщените стойности",defaultSummaryRowDisplayLabelCount:"Брой",defaultSummaryRowDisplayLabelMin:"Минимум",defaultSummaryRowDisplayLabelMax:"Максимум",defaultSummaryRowDisplayLabelSum:"Сума",defaultSummaryRowDisplayLabelAvg:"Осреднено",defaultSummaryRowDisplayLabelCustom:"",calculateSummaryColumnKeyNotSpecified:"Липсва ключа на колоната. Трябва да бъде подаден ключ на колона за да се изчислят обобщените стойности.",featureChooserNotReferenced:"Липсва референция към FeatureChooser. Реферирайте infragistics.ui.grid.featurechooser.js във Вашия проект или един от комбинираните скриптови файлове."};$.ig.GridUpdating=$.ig.GridUpdating||{};$.ig.locale.bg.GridUpdating={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",doneLabel:"Готово",doneTooltip:"Излез и запази промените",cancelLabel:"Отказ",cancelTooltip:"Излез без да запазваш промените",addRowLabel:"Пробави нов ред",addRowTooltip:"Започни добавянето на нови редове",deleteRowLabel:"Изтрий реда",deleteRowTooltip:"Изтрий реда",igTextEditorException:"В момента не е възможно актуализирането на низовите колони в мрежата. Първо трябва да бъде зареден ui.igTextEditor.",igNumericEditorException:"В момента не е възможно актуализирането на числовите колони в мрежата. Първо трябва да бъде зареден ui.igNumericEditor.",igCheckboxEditorException:"В момента не е възможно актуализирането на чекбокс колони в мрежата. Първо трябва да бъде зареден ui.igCheckboxEditor.",igCurrencyEditorException:"В момента не е възможно актуализирането на числовите колони в мрежата с валутен формат. Първо трябва да бъде зареден ui.igCurrencyEditor.",igPercentEditorException:"В момента не е възможно актуализирането на числовите колони в мрежата с валутен формат. Първо трябва да бъде зареден ui.igPercentEditor.",igDateEditorException:"В момента не е възможно актуализирането на дата колони в мрежата. Първо трябва да бъде зареден ui.igDateEditor.",igDatePickerException:"В момента не е възможно актуализирането на дата колони в мрежата. Първо трябва да бъде зареден ui.igDatePicker.",igTimePickerException:"В момента не е възможно актуализирането на дата колони в мрежата. Първо трябва да бъде зареден ui.igTimePicker.",igComboException:"В момента не е възможно използването на combo в мрежата. ui.igCombo трябва да бъде зареден първо.",igRatingException:"В момента не е възможно използването на igRating като редактор в мрежата. ui.igRating трябва да бъде зареден първо.",igValidatorException:"В момента не възможно поддържането на валидиране с опциите дефинирани в igGridUpdating. ui.igValidator трябва да бъде зареден първо.",noPrimaryKeyException:"Операциите за обновяване изискват различна конфигурация. Опцията primaryKey на мрежата трябва да бъде настроена.",hiddenColumnValidationException:"Не е възможно редактирането на ред със скрита колона и включена валидация. Използвайте метода showColumn() или изключете валидацията на колоната преди да се опитате да редактирате реда.",dataDirtyException:'Мрежата има неизпълнени транзакции, което може да доведе до рендирането на информацията ѝ. Включете опцията "autoCommit" на igGrid или обработете събитието "dataDirty", което връща false. Докато обработвате това събитие може по избор да извикате метода commit().',recordOrPropertyNotFoundException:"Зададеният запис или свойство не е намерено. Проверете критериите за търсене и ги променете ако е необходимо.",rowUpdatingNotSupportedWithColumnVirtualization:"Зададената конфигурация на редактиране чрез ред не работи с опцията за виртуализация на колони.",rowEditDialogCaptionLabel:"Редактирай данните в реда",excelNavigationNotSupportedWithCurrentEditMode:'ExcelNavigation изисква различна конфигурация. editMode трябва да бъде зададен като "cell" или "row".',columnNotFound:"Зададения ключ на колона не беше намерен във видимата колекция от колони или зададеният индекс е бил извън границите.",rowOrColumnSpecifiedOutOfView:"В момента не е възможна промяната на зададения ред или колона. Те трябва да бъдат видими на конкретната страница и във виртуализационната рамка.",editingInProgress:"В момента се променя ред или клетка. Не може да се започне нова процедура по промяна ако сегашната не е завършена.",undefinedCellValue:"Недефинирани не могат да бъдат зададени като стойност на клетка.",addChildTooltip:"Добави нов подред",multiRowGridNotSupportedWithCurrentEditMode:"Когато мрежата има включен multi-row-layout се поддържа единствено режим на редактиране на диалози.",virtualizationNotSupportedWithoutAutoCommit:" Не е поддържано активирането на Ъпдейтване и виртуализация докато autoCommit е настроено на false. Моля настройте опцията rautoCommit на мрежата на true."};$.ig.CellMerging=$.ig.CellMerging||{};$.ig.locale.bg.CellMerging={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",mergeStrategyNotAFunction:"Зададената mergeStrategy не е разпозната като предварително дефинирана стойност или функция с това име не е била намерена."};$.ig.ColumnMoving=$.ig.ColumnMoving||{};$.ig.locale.bg.ColumnMoving={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",movingDialogButtonApplyText:"Приложи",movingDialogButtonCancelText:"Отказ",movingDialogCaptionButtonDesc:"Премести надолу",movingDialogCaptionButtonAsc:"Премести нагоре",movingDialogCaptionText:"Премести колоните",movingDialogDisplayText:"Премести колоните",movingDialogDropTooltipText:"Премести тук",movingDialogCloseButtonTitle:"Затваряне на движещия се диалог",dropDownMoveLeftText:"Премести наляво",dropDownMoveRightText:"Премести надясно",dropDownMoveFirstText:"Премести в началото",dropDownMoveLastText:"Премести в края",featureChooserNotReferenced:"Липсва референция към FeatureChooser. Реферирайте infragistics.ui.grid.featurechooser.js във Вашия проект или един от комбинираните скриптови файлове.",movingToolTipMove:"Премести",featureChooserSubmenuText:"Премести",columnVirtualizationEnabled:"Преместването на колони изисква различни настройки на виртуализация. Използвайте rowVirtualization или настройте virtualizationMode на 'continuous'."};$.ig.ColumnFixing=$.ig.ColumnFixing||{};$.ig.locale.bg.ColumnFixing={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",headerFixButtonText:"Фиксирай колоната",headerUnfixButtonText:"Освободи колоната",featureChooserTextFixedColumn:"Фиксирай колоната",featureChooserTextUnfixedColumn:"Освободи колоната",groupByNotSupported:"ColumnFixing изисква различна конфигурация. Функцията GroupBy трябва да бъде изключена.",virtualizationNotSupported:"ColumnFixing изисква различни настройки на виртуализация. Вместо това трябва да се използва rowVirtualization.",columnVirtualizationNotSupported:"ColumnFixing изисква различни настройки на виртуализация. columnVirtualization трябва да се изключи.",columnMovingNotSupported:"ColumnFixing изисква различна конфигурация. ColumnMoving трябва да бъде изключено.",hidingNotSupported:"ColumnFixing изисква различна конфигурация. Функцията Hiding трябва да бъде изключена.",hierarchicalGridNotSupported:"igHierarchicalGrid не поддържа ColumnFixing. ColumnFixing трябва да бъде изключено.",responsiveNotSupported:"ColumnFixing изисква различна конфигурация. Функцията Responsive трябва да бъде изключена.",noGridWidthNotSupported:"Фиксирането на колони изисква различна конфигурация. Ширината на мрежата трябва да бъде зададена като проценти или като число в пиксели.",gridHeightInPercentageNotSupported:"Фиксирането на колони изисква различна конфигурация. Ширината на мрежата трябва да бъде зададена в пиксели.",defaultColumnWidthInPercentageNotSupported:"ColumnFixing изисква различна конфигурация. Стандартната ширина на колоната трябва да бъде зададена като число в пиксели.",columnsWidthShouldBeSetInPixels:"ColumnFixing изисква други настройки на ширината на колоната. Ширината на колоната с ключ {key} трябва да бъде зададена в пиксели.",unboundColumnsNotSupported:"ColumnFixing изисква различна конфигурация. UnboundColumns трябва да бъде изключено.",excelNavigationNotSupportedWithCurrentEditMode:"Режима Excel Navigation се поддържа само за режимите Cell Edit и Row Edit. За да избегнете тази грешка или изключете excelNavigationMode, или настройте editMode на клетки или редове.",initialFixingNotApplied:"Първоначалното фиксиране не може да бъде приложено за колoна с ключ: {0}. Причина: {1}",setOptionGridWidthException:"Невалидна стойност за опцията за размера на ширината на мрежата. Когато има фиксирани колони ширината на видимата зона на нефиксираните колони/нефиксираната колона трябва да бъде по-голяма или равна на стойността на minimalVisibleAreaWidth.",noneError:"Вие успешно конфигурирахте мрежата!",notValidIdentifierError:"Зададеният ключ за колоната не е валиден. Подайте ключ за колоната, който а съвпада с един от ключовете на дефинираната колона в мрежата.",fixingRefusedError:"Фиксирането на тази колона в момента не се поддържа. Освободете друга видима колона или първо използвайте метода showColumn() върху всяка една скрита свободна колона.",fixingRefusedMinVisibleAreaWidthError:"Тази колона неможе да бъде фиксирана. Ширината ѝ надхвърля наличното място за фиксиране на колона в мрежата.",alreadyHiddenError:"В момента не е възможно фиксирането/освобождаването на тази колона. Методът showColumn() трябва първо да бъде използван върху колоната.",alreadyUnfixedError:"Тази колона е вече свободна.",alreadyFixedError:"Тази колона е вече фиксирана.",unfixingRefusedError:"В момента не е възможно освобождаването на тази колона. Методът showColumn() трябва първо да бъде използван върху някоя скрита, фиксирана колоната.",targetNotFoundError:"Не е намерена целевата колона с ключ {key}. Проверете ключа използван за тъсенете и го променете ако е необходимо."};$.ig.GridAppendRowsOnDemand=$.ig.GridAppendRowsOnDemand||{};$.ig.locale.bg.GridAppendRowsOnDemand={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",loadMoreDataButtonText:"Зареди още данни",appendRowsOnDemandRequiresHeight:"AppendRowsOnDemand изисква различна конфигурация. Височината на мрежата трябва да бъде зададена.",groupByNotSupported:"AppendRowsOnDemand изисква различна конфигурация. GroupBy трябва да бъде изключено.",pagingNotSupported:"AppendRowsOnDemand изисква различна конфигурация. Paging трябва да бъде изключено.",cellMergingNotSupported:"AppendRowsOnDemand изисква различна конфигурация. CellMerging трябва да бъде изключено.",virtualizationNotSupported:"AppendRowsOnDemand изисква различна конфигурация. Virtualization трябва да бъде изключено."};$.ig.igGridResponsive=$.ig.igGridResponsive||{};$.ig.locale.bg.igGridResponsive={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",fixedVirualizationNotSupported:'Функцията Responsive изисква различна настройка на виртуализацията. virtualizationMode трябва да бъде зададен като "continuous".'};$.ig.igGridMultiColumnHeaders=$.ig.igGridMultiColumnHeaders||{};$.ig.locale.bg.igGridMultiColumnHeaders={optionChangeNotSupported:"{optionName} не може да бъде променяно след инициализиране. Трябва да бъде настроено по време на инициализацията.",multiColumnHeadersNotSupportedWithColumnVirtualization:"Multi-column headers изисква различна конфигурация. columnVirtualization трябва да бъде изключен.",cannotExpandMultiColumnHeader:"Multi-Column header exceeds the maximum allowed fixed area width and therefore cannot be expanded",atLeastOneColumnShouldBeShownWhenCollapseOrExpand:"Трябва да има поне 1 показана колона, когато multi-column header е collapsed или expanded.",collapsedColumnIconTooltip:"Разтвори",expandedColumnIconTooltip:"Свий"};$.ig.Grid.locale=$.ig.Grid.locale||$.ig.locale.bg.Grid;$.ig.GridFeatureChooser.locale=$.ig.GridFeatureChooser.locale||$.ig.locale.bg.GridFeatureChooser;$.ig.GridFiltering.locale=$.ig.GridFiltering.locale||$.ig.locale.bg.GridFiltering;$.ig.GridGroupBy.locale=$.ig.GridGroupBy.locale||$.ig.locale.bg.GridGroupBy;$.ig.GridHiding.locale=$.ig.GridHiding.locale||$.ig.locale.bg.GridHiding;$.ig.GridResizing.locale=$.ig.GridResizing.locale||$.ig.locale.bg.GridResizing;$.ig.GridPaging.locale=$.ig.GridPaging.locale||$.ig.locale.bg.GridPaging;$.ig.GridSelection.locale=$.ig.GridSelection.locale||$.ig.locale.bg.GridSelection;$.ig.GridRowSelectors.locale=$.ig.GridRowSelectors.locale||$.ig.locale.bg.GridRowSelectors;$.ig.GridSorting.locale=$.ig.GridSorting.locale||$.ig.locale.bg.GridSorting;$.ig.GridSummaries.locale=$.ig.GridSummaries.locale||$.ig.locale.bg.GridSummaries;$.ig.GridUpdating.locale=$.ig.GridUpdating.locale||$.ig.locale.bg.GridUpdating;$.ig.CellMerging.locale=$.ig.CellMerging.locale||$.ig.locale.bg.CellMerging;$.ig.ColumnMoving.locale=$.ig.ColumnMoving.locale||$.ig.locale.bg.ColumnMoving;$.ig.ColumnFixing.locale=$.ig.ColumnFixing.locale||$.ig.locale.bg.ColumnFixing;$.ig.GridAppendRowsOnDemand.locale=$.ig.GridAppendRowsOnDemand.locale||$.ig.locale.bg.GridAppendRowsOnDemand;$.ig.igGridResponsive.locale=$.ig.igGridResponsive.locale||$.ig.locale.bg.igGridResponsive;$.ig.igGridMultiColumnHeaders.locale=$.ig.igGridMultiColumnHeaders.locale||$.ig.locale.bg.igGridMultiColumnHeaders;$.ig.HierarchicalGrid.locale=$.ig.HierarchicalGrid.locale||$.ig.locale.bg.HierarchicalGrid;return $.ig.locale.bg});