/*
 * @(#)LocalDateTimeField.java 5/19/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 */

package jidefx.scene.control.field;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.Shape;
import javafx.util.Callback;
import jidefx.scene.control.decoration.Decorator;
import jidefx.scene.control.decoration.PredefinedDecorators;
import jidefx.scene.control.field.popup.LocalDateTimePopupContent;
import jidefx.scene.control.field.popup.PopupContent;
import jidefx.scene.control.field.verifier.PatternVerifierUtils;
import jidefx.utils.CommonUtils;
import jidefx.utils.PredefinedShapes;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

/**
 * A {@code PopupField} for {@link LocalDateTime}.
 */
public class LocalDateTimeField extends PopupField<LocalDateTime> {
    public LocalDateTimeField() {
        this(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance()).toPattern()); // TODO: find out the pattern for the DateTimeFormatter's FormatStyle.MEDIUM
    }

    public LocalDateTimeField(String pattern) {
        this(pattern, LocalDateTime.now());
    }

    public LocalDateTimeField(String pattern, LocalDateTime value) {
        setPattern(pattern);
        setValue(value);
    }

    private static final String STYLE_CLASS_DEFAULT = "local-date-field"; //NON-NLS

    @Override
    protected void initializeStyle() {
        super.initializeStyle();
        getStyleClass().addAll(STYLE_CLASS_DEFAULT);
    }

    @Override
    protected void initializeTextField() {
        super.initializeTextField();
        setPopupContentFactory(new Callback<LocalDateTime, PopupContent<LocalDateTime>>() {
            @Override
            public PopupContent<LocalDateTime> call(LocalDateTime param) {
                LocalDateTimePopupContent content = new LocalDateTimePopupContent();
                content.setValue(getValue());
                return content;
            }
        });
    }

    @Override
    protected void registerListeners() {
        super.registerListeners();
        patternProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                setDateTimeFormatter(DateTimeFormatter.ofPattern(getPattern()));
            }
        });
    }

    @Override
    protected void customizePopupContent(PopupContent<LocalDateTime> popupContent) {
        super.customizePopupContent(popupContent);
        popupContent.valueProperty().addListener(new ChangeListener<LocalDateTime>() {
            @Override
            public void changed(ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) {
                hide();
            }
        });
    }

    @Override
    protected boolean supportFromString() {
        return getDateTimeFormatter() != null || super.supportFromString();
    }

    @Override
    protected LocalDateTime fromString(String text) {
        DateTimeFormatter dateTimeFormatter = getDateTimeFormatter();
        if (dateTimeFormatter != null) {
            try {
                TemporalAccessor parse = dateTimeFormatter.parse(text);
                try {
                    return LocalDateTime.from(parse);
                }
                catch (DateTimeException e) {
                    CommonUtils.ignoreException(e);
                }
            }
            catch (DateTimeParseException e) {
                CommonUtils.ignoreException(e);
            }
        }
        return super.fromString(text);
    }

    @Override
    protected String toString(LocalDateTime value) {
        DateTimeFormatter formatter = getDateTimeFormatter();
        if (formatter != null) {
            try {
                return formatter.format(value);
            }
            catch (Exception e) {
                CommonUtils.ignoreException(e);
            }
        }
        return super.toString(value);
    }

    private ObjectProperty<DateTimeFormatter> _dateTimeFormatProperty;

    public ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty() {
        if (_dateTimeFormatProperty == null) {
            _dateTimeFormatProperty = new SimpleObjectProperty<DateTimeFormatter>(this, "dateTimeFormatter") { //NON-NLS
                @Override
                protected void invalidated() {
                    super.invalidated();
                    PatternVerifierUtils.initializePatternVerifiersForDateTimeFormatter(getPatternVerifiers());
                    setStringConverter(null);
                }
            };
        }
        return _dateTimeFormatProperty;
    }

    /**
     * Gets the DateTimeFormatter.
     *
     * @return the DateTimeFormatter.
     */
    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatterProperty().get();
    }

    /**
     * Sets the DateTimeFormatter that will format a LocalDateTime, LocalTime or LocalDateTimeTime.
     *
     * @param format the new DateTimeFormatter.
     */
    public void setDateTimeFormatter(DateTimeFormatter format) {
        dateTimeFormatterProperty().set(format);
    }

    @Override
    protected Decorator<Button> createPopupButtonDecorator() {
        return new PredefinedDecorators.AbstractButtonDecoratorSupplier() {
            @Override
            public Decorator<Button> get() {
                Shape calendarIcon = PredefinedShapes.getInstance().createCalendarIcon(15);
                Shape shape = PredefinedShapes.getInstance().createArrowedIcon(calendarIcon);
                return new Decorator<>(createButton(shape), Pos.CENTER_RIGHT, new Point2D(-70, 0), new Insets(0, 100, 0, 0));
            }
        }.get();
    }

    /**
     * A static method which will create a LocalDateTimeField for LocalDateTime.
     *
     * @param dateFormat   the format string as defined in {@link SimpleDateFormat}.
     * @param initialValue the initial value.
     * @return a LocalDateTimeField for the LocalDateTime.
     */
    public static LocalDateTimeField createLocalDateTimeField(String dateFormat, LocalDateTime initialValue) {
        LocalDateTimeField field = new LocalDateTimeField(dateFormat);
        field.setValue(initialValue);
        return field;
    }

    /**
     * A static method which will create a LocalDateTimeField for LocalDateTime.
     *
     * @param initialValue the initial value.
     * @return a LocalDateTimeField for the LocalDateTime.
     */
    public static LocalDateTimeField createLocalDateTimeField(LocalDateTime initialValue) {
        LocalDateTimeField field = new LocalDateTimeField();
        field.setValue(initialValue);
        return field;
    }

    /**
     * A static method which will create a LocalDateTimeField for LocalDateTime.
     *
     * @return a LocalDateTimeField for the LocalDateTime.
     */
    public static LocalDateTimeField createLocalDateTimeField() {
        return new LocalDateTimeField();
    }

}
