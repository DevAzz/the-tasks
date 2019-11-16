package ru.devazz.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для анимации загрузки
 */
public class LoadAnimation {

	/** Число Пи */
	private final double PI = Math.PI;

	/** Массив углов для построения окружностей */
	private final double[] anglesArr = new double[] { -PI / 2, -PI / 3, -PI / 6, 0, PI / 6, PI / 3,
			PI / 2, (4 * PI) / 6, (5 * PI) / 6, PI, (-5 * PI) / 6, (-4 * PI) / 6 };

	/** Карта соответствия таймлайнов и кругов */
	private Map<Timeline, Circle> mapCircle = new HashMap<>();

	/**
	 * Конструктор
	 */
	public LoadAnimation() {
		super();
	}

	/**
	 * Конструктор
	 *
	 * @param centrX Цетр анимации по оси X
	 * @param centrY Центр анимации по оси Y
	 * @param radiusBig Радиус анимации
	 * @param radiusSmall Радиус окружностей, состовляющих анимацию
	 */
	public LoadAnimation(double centrX, double centrY, double radiusBig, double radiusSmall) {
		int delay = 100;
		for (double d : anglesArr) {
			Circle circle = new Circle(centrX + (radiusBig * Math.cos(d)),
					centrY + (radiusBig * Math.sin(d)), radiusSmall, Color.WHITE);
			circle.setVisible(false);
			Timeline timeline = new Timeline();
			timeline.getKeyFrames().add(
					new KeyFrame(Duration.millis(1200), new KeyValue(circle.opacityProperty(), 0)));
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.setDelay(Duration.millis(delay));
			timeline.jumpTo(Duration.millis(1200));

			mapCircle.put(timeline, circle);
			delay += 100;
		}
	}

	/**
	 * Запускает анимацию
	 */
	public void start() {
		for (Map.Entry<Timeline, Circle> entry : mapCircle.entrySet()) {
			entry.getValue().setVisible(true);
			entry.getValue().setOpacity(1);
			entry.getKey().jumpTo(Duration.millis(1200));
			entry.getKey().play();
		}

	}

	/**
	 * Останавливает анимацию
	 */
	public void stop() {
		for (Map.Entry<Timeline, Circle> entry : mapCircle.entrySet()) {
			entry.getValue().setVisible(false);
			entry.getValue().setOpacity(0);
			entry.getKey().stop();
		}
	}

	/**
	 * Возвращает фигуру
	 *
	 * @param centrX Цетр анимации по оси X
	 * @param centrY Центр анимации по оси Y
	 * @param radiusBig Радиус анимации
	 * @param radiusSmall Радиус окружностей, состовляющих анимацию
	 * @param aDelayIncrement начальное значение и инкремент задержки перед
	 *            следующим выполнением opacity
	 * @param aJumpTo длительность одного оборота
	 * @return фигура
	 */
	public Collection<Circle> getCircles(double centrX, double centrY, double radiusBig,
			double radiusSmall, int aDelayIncrement, double aJumpTo) {
		mapCircle.clear();
		int delay = aDelayIncrement;
		for (double d : anglesArr) {
			Circle circle = new Circle(centrX + (radiusBig * Math.cos(d)),
					centrY + (radiusBig * Math.sin(d)), radiusSmall, Color.WHITE);
			circle.setVisible(false);
			Timeline timeline = new Timeline();
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(aJumpTo),
					new KeyValue(circle.opacityProperty(), 0)));
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.setDelay(Duration.millis(delay));
			timeline.jumpTo(Duration.millis(aJumpTo));

			mapCircle.put(timeline, circle);
			delay += aDelayIncrement;
		}
		return mapCircle.values();
	}

	/**
	 * Возвращает фигуру
	 *
	 * @return фигура
	 */
	public Collection<Circle> getCircles() {
		return mapCircle.values();
	}
}
