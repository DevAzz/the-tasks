package ru.devazz.utils;

import ru.sciencesquad.hqtasks.server.bean.ICommonService;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.siencesquad.hqtasks.ui.model.PresentationModel;
import ru.siencesquad.hqtasks.ui.view.*;
import ru.siencesquad.hqtasks.ui.view.dialogs.*;
import ru.siencesquad.hqtasks.ui.widgets.CustomTimeIntervalView;
import ru.siencesquad.hqtasks.ui.widgets.PageSettingsView;

import java.util.HashMap;
import java.util.Map;

/**
 * Перечисление представений
 */
public class ViewType {

	/** Экземпляр класса */
	private static ViewType instance;

	/** Карта типов представлений */
	private Map<Class<? extends AbstractView<? extends PresentationModel<? extends ICommonService, ? extends IEntity>>>, String> typeMap = new HashMap<>();

	/**
	 * Конструктор
	 */
	private ViewType() {
		typeMap.put(AuthView.class, "authView.fxml");
		typeMap.put(CurrentTaskView.class, "currentTaskView.fxml");
		typeMap.put(EventIndicatorView.class, "eventIndicatorView.fxml");
		typeMap.put(EventJournalView.class, "eventJournalView.fxml");
		typeMap.put(PositionBookView.class, "positionBookView.fxml");
		typeMap.put(RootView.class, "rootView.fxml");
		typeMap.put(SubInfoView.class, "subInfo.fxml");
		typeMap.put(SubordinationTreeView.class, "subordinationTree.fxml");
		typeMap.put(TaskPanelView.class, "taskPanelView.fxml");
		typeMap.put(ExtSearchResView.class, "extendedSearchResultsView.fxml");
		typeMap.put(ExtendedSearchView.class, "extendedSearchView.fxml");
		typeMap.put(WorkbenchView.class, "workbenchTasks.fxml");
		typeMap.put(TasksView.class, "tasksView.fxml");
		typeMap.put(SubFilterDialogView.class, "selectSubElsFilterDialog.fxml");
		typeMap.put(ReportView.class, "reportView.fxml");
		typeMap.put(PushUpMessageView.class, "pushUpMessage.fxml");
		typeMap.put(DefaultTasksDialog.class, "selectDefaultTasksDialog.fxml");
		typeMap.put(DecisionDialogView.class, "decisionDialogView.fxml");
		typeMap.put(TaskCompletionDialogView.class, "taskCompletionDialogView.fxml");
		typeMap.put(RegistryView.class, "registryView.fxml");
		typeMap.put(LegendOfIconsView.class, "legendOfIcons.fxml");
		typeMap.put(HelpView.class, "helpView.fxml");
		typeMap.put(RemoveTaskDialogView.class, "removeTaskDialogView.fxml");
		typeMap.put(PageSettingsView.class, "pageSettings.fxml");
		typeMap.put(TaskHistoryView.class, "historyView.fxml");
		typeMap.put(TaskHistoryEntryPanelView.class, "historyEntryView.fxml");
		typeMap.put(CustomTimeIntervalView.class, "customTimeIntervalView.fxml");
		typeMap.put(CycleTaskView.class, "cycleView.fxml");
		typeMap.put(SummaryView.class, "summaryView.fxml");
		typeMap.put(SummaryPanelView.class, "summaryPanelView.fxml");
		typeMap.put(SummaryTaskView.class, "summaryTaskView.fxml");
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
