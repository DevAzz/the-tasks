package ru.devazz.utils;

import ru.devazz.model.PresentationModel;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.view.*;
import ru.devazz.view.dialogs.*;
import ru.devazz.widgets.CustomTimeIntervalView;
import ru.devazz.widgets.PageSettingsView;

import java.util.HashMap;
import java.util.Map;

/**
 * Перечисление представений
 */
public class ViewType {

	/** Экземпляр класса */
	private static ViewType instance;

	/** Карта типов представлений */
	private Map<Class<? extends AbstractView<? extends PresentationModel<? extends ICommonService, ? extends IEntity>>>, String>
			typeMap = new HashMap<>();

	/**
	 * Конструктор
	 */
	private ViewType() {
		typeMap.put(AuthView.class, "/view/authView.fxml");
		typeMap.put(CurrentTaskView.class, "/view/currentTaskView.fxml");
		typeMap.put(EventIndicatorView.class, "/view/eventIndicatorView.fxml");
		typeMap.put(EventJournalView.class, "/view/eventJournalView.fxml");
		typeMap.put(PositionBookView.class, "/view/positionBookView.fxml");
		typeMap.put(RootView.class, "/view/rootView.fxml");
		typeMap.put(SubInfoView.class, "/view/subInfo.fxml");
		typeMap.put(SubordinationTreeView.class, "/view/subordinationTree.fxml");
		typeMap.put(TaskPanelView.class, "/view/taskPanelView.fxml");
		typeMap.put(ExtSearchResView.class, "/view/extendedSearchResultsView.fxml");
		typeMap.put(ExtendedSearchView.class, "/view/extendedSearchView.fxml");
		typeMap.put(WorkbenchView.class, "/view/workbenchTasks.fxml");
		typeMap.put(TasksView.class, "/view/tasksView.fxml");
		typeMap.put(SubFilterDialogView.class, "/view/dialogs/selectSubElsFilterDialog.fxml");
		typeMap.put(ReportView.class, "/view/reportView.fxml");
		typeMap.put(PushUpMessageView.class, "/view/dialogs/pushUpMessage.fxml");
		typeMap.put(DefaultTasksDialog.class, "/view/dialogs/selectDefaultTasksDialog.fxml");
		typeMap.put(DecisionDialogView.class, "/view/dialogs/decisionDialogView.fxml");
		typeMap.put(TaskCompletionDialogView.class, "/view/dialogs/taskCompletionDialogView.fxml");
		typeMap.put(RegistryView.class, "/view/registryView.fxml");
		typeMap.put(LegendOfIconsView.class, "/view/legendOfIcons.fxml");
		typeMap.put(HelpView.class, "/view/helpView.fxml");
		typeMap.put(RemoveTaskDialogView.class, "/view/dialogs/removeTaskDialogView.fxml");
		typeMap.put(PageSettingsView.class, "/view/widgets/pageSettings.fxml");
		typeMap.put(TaskHistoryView.class, "/view/historyView.fxml");
		typeMap.put(TaskHistoryEntryPanelView.class, "/view/historyEntryView.fxml");
		typeMap.put(CustomTimeIntervalView.class, "/view/widgets/customTimeIntervalView.fxml");
		typeMap.put(CycleTaskView.class, "/view/cycleView.fxml");
		typeMap.put(SummaryView.class, "/view/summaryView.fxml");
		typeMap.put(SummaryPanelView.class, "/view/summaryPanelView.fxml");
		typeMap.put(SummaryTaskView.class, "/view/summaryTaskView.fxml");
	}

	/**
	 * Возвращает {@link #instance}
	 *
	 * @return {@link #instance}
	 */
	public static ViewType getInstance() {
		if (null == instance) {
			instance = new ViewType();
		}
		return instance;
	}

	/**
	 * Возвращает путь до FXML файла описания представления
	 *
	 * @param aType тип представления
	 * @return путь до FXML файла описания представления
	 */
	public <T> String getFilePath(Class<T> aType) {
		return typeMap.get(aType);

	}

}
