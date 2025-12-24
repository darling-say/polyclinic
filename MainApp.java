package com.polyclinic.registry;

import com.itextpdf.layout.element.Paragraph;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import java.awt.Desktop;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.util.StringConverter;
import java.awt.*;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainApp extends Application {
    static {
        // Для Mac: устанавливаем настройки для лучшего рендеринга
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.subpixeltext", "true");
    }
    @Override
    public void start(Stage stage) {
        AppLogger.info("Запуск JavaFX приложения 'Поликлиника - Система регистратуры'");
        System.out.println("Запускаем JavaFX приложение...");

        testClasses();
        testDatabaseConnection();

        // Основной контейнер с розовым градиентом
        VBox mainLayout = new VBox(30);
        mainLayout.setStyle("-fx-padding: 50; -fx-alignment: center; " +
                "-fx-background-color: linear-gradient(to bottom, #FFE4E1, #FFC0CB);");

        // Заголовок БЕЗ emoji
        Label title = new Label("Поликлиника - Система регистратуры");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 1, 1);");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        // Сетка для кнопок
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(20);
        buttonGrid.setVgap(20);
        buttonGrid.setAlignment(Pos.CENTER);

        // Стиль для кнопок (без emoji в тексте)
        String buttonStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 30; " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 2, 2); " +
                "-fx-min-width: 250px; -fx-font-family: 'System';";

        // БЕЛЫЕ КНОПКИ С ТЕНЬЮ:
        Button doctorsBtn = new Button("Список врачей");
        doctorsBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-width: 220px; -fx-padding: 15; " +
                "-fx-background-color: white; -fx-text-fill: #333; " +
                "-fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: #E0E0E0; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 2, 2); " +
                "-fx-cursor: hand;");

        Button patientsBtn = new Button("Список пациентов");
        patientsBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-width: 220px; -fx-padding: 15; " +
                "-fx-background-color: white; -fx-text-fill: #333; " +
                "-fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: #E0E0E0; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 2, 2); " +
                "-fx-cursor: hand;");

        Button appointmentsBtn = new Button("Запись на приём");
        appointmentsBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-width: 220px; -fx-padding: 15; " +
                "-fx-background-color: white; -fx-text-fill: #333; " +
                "-fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: #E0E0E0; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 2, 2); " +
                "-fx-cursor: hand;");

        Button certificatesBtn = new Button("Медицинские справки");
        certificatesBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-width: 220px; -fx-padding: 15; " +
                "-fx-background-color: white; -fx-text-fill: #333; " +
                "-fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: #E0E0E0; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 2, 2); " +
                "-fx-cursor: hand;");
        certificatesBtn.setOnAction(e -> showCertificatesManagement());

        Button statisticsBtn = new Button("Статистика заболеваний");
        statisticsBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-width: 220px; -fx-padding: 15; " +
                "-fx-background-color: white; -fx-text-fill: #333; " +
                "-fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: #E0E0E0; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 2, 2); " +
                "-fx-cursor: hand;");
        statisticsBtn.setOnAction(e -> showDiseaseStatistics());

        Button viewAppointmentsBtn = new Button("Просмотр записей");
        viewAppointmentsBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-pref-width: 220px; -fx-padding: 15; " +
                "-fx-background-color: white; -fx-text-fill: #333; " +
                "-fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: #E0E0E0; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 2, 2); " +
                "-fx-cursor: hand;");
        viewAppointmentsBtn.setOnAction(e -> showAllAppointments());

        // Распределяем кнопки в GridPane (2 строки, 2 колонки)
        buttonGrid.add(doctorsBtn, 0, 0);
        buttonGrid.add(patientsBtn, 1, 0);
        buttonGrid.add(appointmentsBtn, 0, 1);
        buttonGrid.add(viewAppointmentsBtn, 1, 1);
        buttonGrid.add(certificatesBtn, 0, 2);
        buttonGrid.add(statisticsBtn, 1, 2);

        // Статус с нормальным шрифтом
        Label status = new Label("Система готова к работе!\nБаза данных подключена успешно!");
        status.setStyle("-fx-font-size: 16px; -fx-text-fill: #2E8B57; -fx-font-weight: bold; " +
                "-fx-text-alignment: center; -fx-padding: 20; " +
                "-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 10;");
        status.setFont(Font.font("System", FontWeight.BOLD, 16));
        status.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        doctorsBtn.setOnAction(e -> showDoctorsList());
        patientsBtn.setOnAction(e -> showPatientsList());
        appointmentsBtn.setOnAction(e -> showAppointments());

        mainLayout.getChildren().addAll(title, buttonGrid, status);

        Scene scene = new Scene(mainLayout, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Поликлиника - Система регистратуры");
        stage.show();
    }
    private Button createStyledButton(String text, String color, String baseStyle) {
        Button button = new Button(text);
        button.setStyle(baseStyle + " -fx-background-color: " + color + "; -fx-text-fill: white;");
        return button;
    }

    //  метод для создания кнопки закрытия
    private Button createCloseButton(Stage stage) {
        Button closeBtn = new Button("Закрыть");
        closeBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #FFB6C1; " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10;");
        closeBtn.setOnAction(e -> stage.hide());
        return closeBtn;
    }

    // Универсальный метод для создания кнопки отмены
    private Button createCancelButton(Stage stage) {
        Button cancelBtn = new Button("Отмена");
        cancelBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #FFB6C1; " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10;");
        cancelBtn.setOnAction(e -> stage.hide());
        return cancelBtn;
    }

    private void setupAutoCapitalize(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String capitalized = newValue.substring(0, 1).toUpperCase() +
                        (newValue.length() > 1 ? newValue.substring(1).toLowerCase() : "");
                if (!newValue.equals(capitalized)) {
                    textField.setText(capitalized);
                }
            }
        });
    }

    @Override
    public void stop() {
        AppLogger.info("Завершение работы приложения 'Поликлиника - Система регистратуры'");
        AppLogger.close();
    }

    private void showPatientsList() {
        Stage patientsStage = new Stage();
        patientsStage.setTitle("Список пациентов");
        patientsStage.setMinWidth(900);
        patientsStage.setMinHeight(600);

        VBox mainLayout = new VBox(10);
        mainLayout.setStyle("-fx-padding: 20;");

        Label title = new Label("Список пациентов");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Создаем таблицу
        TableView<Patient> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white;");

        // Колонка Номер полиса
        TableColumn<Patient, Integer> policyColumn = new TableColumn<>("Номер полиса");
        policyColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPolicy()).asObject());
        policyColumn.setPrefWidth(100);
        policyColumn.setStyle("-fx-alignment: CENTER;");

        // Колонка Фамилия
        TableColumn<Patient, String> surnameColumn = new TableColumn<>("Фамилия");
        surnameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSurname()));
        surnameColumn.setPrefWidth(120);

        // Колонка Имя
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(100);

        // Колонка Отчество
        TableColumn<Patient, String> secondNameColumn = new TableColumn<>("Отчество");
        secondNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getSecondName() != null ? cellData.getValue().getSecondName() : ""
        ));
        secondNameColumn.setPrefWidth(120);

        // Колонка Телефон
        TableColumn<Patient, String> phoneColumn = new TableColumn<>("Телефон");
        phoneColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhone()));
        phoneColumn.setPrefWidth(120);

        // Колонка Адрес
        TableColumn<Patient, String> addressColumn = new TableColumn<>("Адрес");
        addressColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAddress()));
        addressColumn.setPrefWidth(200);

        // Колонка Дата рождения
        TableColumn<Patient, String> birthDateColumn = new TableColumn<>("Дата рождения");
        birthDateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateOfBirth();
            String dateStr = (date != null) ? date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "Не указана";
            return new javafx.beans.property.SimpleStringProperty(dateStr);
        });
        birthDateColumn.setPrefWidth(100);

        // Метод для обновления таблицы - делаем final
        final Runnable refreshTable = () -> {
            try {
                PatientDAO patientDAO = new PatientDAO();
                List<Patient> patients = patientDAO.getAllPatients();
                tableView.getItems().setAll(patients);
            } catch (PolyclinicException e) {
                System.err.println("Ошибка при загрузке пациентов: " + e.getMessage());
                e.printStackTrace();
                showError("Ошибка загрузки пациентов: " + e.getErrorCode().getDescription() +
                        "\n" + e.getAdditionalInfo());
            }
        };

        // Колонка действий - теперь можем использовать refreshTable
        TableColumn<Patient, Void> actionsColumn = new TableColumn<>("Действия");
        actionsColumn.setPrefWidth(150);
        actionsColumn.setCellFactory(param -> new TableCell<Patient, Void>() {
            private final Button editBtn = new Button("Редактировать");
            private final Button deleteBtn = new Button("Удалить");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-color: #98FB98;");
                deleteBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-color: #FFB6C1;");

                editBtn.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    showEditPatientForm(patient); // Старый метод
                    // Обновляем через 1 секунду
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(refreshTable);
                        }
                    }, 1000);
                });

                deleteBtn.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    deletePatient(patient.getPolicy(), refreshTable);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        tableView.getColumns().addAll(policyColumn, surnameColumn, nameColumn, secondNameColumn,
                phoneColumn, addressColumn, birthDateColumn, actionsColumn);

        // Первоначальная загрузка
        refreshTable.run();

        // Кнопки управления
        HBox buttonPanel = new HBox(15);
        buttonPanel.setAlignment(Pos.CENTER);


        Button addPatientBtn = new Button("Добавить пациента");
        addPatientBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #98FB98; " +
                "-fx-text-fill: #333; -fx-background-radius: 10; -fx-border-radius: 10;");
        addPatientBtn.setOnAction(e -> {
            showAddPatientForm();
            // Обновляем через 1 секунду
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(refreshTable);
                }
            }, 1000);
        });

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #FFB6C1; " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10;");
        refreshBtn.setOnAction(e -> refreshTable.run());

        Button exportCertificatePdfBtn = new Button("Справки пациента");
        exportCertificatePdfBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #ADD8E6; " +
                "-fx-text-fill: #333; -fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-cursor: hand;");
        exportCertificatePdfBtn.setOnAction(e -> {
            // Получаем выбранного пациента из таблицы
            Patient selectedPatient = tableView.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                // Показываем справки этого пациента
                showPatientCertificates(selectedPatient);
            } else {
                showError("Выберите пациента из таблицы!");
            }
        });

        Button closeBtn = createCloseButton(patientsStage);

        buttonPanel.getChildren().addAll(addPatientBtn, refreshBtn, closeBtn);

        mainLayout.getChildren().addAll(title, tableView, buttonPanel);

        Scene scene = new Scene(mainLayout, 900, 600);
        patientsStage.setScene(scene);
        patientsStage.show();
    }

    // Обновленный метод deletePatient с параметром для обновления таблицы
    private void deletePatient(int policyNumber, Runnable refreshCallback) {
        try {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Подтверждение удаления");
            confirmation.setHeaderText("Удаление пациента");
            confirmation.setContentText("Вы уверены, что хотите удалить пациента с полисом " + policyNumber + "?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                PatientDAO patientDAO = new PatientDAO();
                patientDAO.deletePatient(policyNumber);
                AppLogger.audit("Удаление пациента", "Система", "Удален пациент с полисом: " + policyNumber);
                AppLogger.info("Пациент успешно удален, полис: " + policyNumber);
                showSuccess("Пациент успешно удален");
                // Вызываем callback для обновления таблицы
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } else {
                AppLogger.info("Удаление пациента отменено пользователем, полис: " + policyNumber);
            }
        } catch (PolyclinicException e) {
            AppLogger.error("Ошибка при удалении пациента с полисом: " + policyNumber, e);
            System.err.println("Ошибка при удалении пациента: " + e.getMessage());
            e.printStackTrace();
            showError("Ошибка удаления пациента: " + e.getErrorCode().getDescription() +
                    "\n" + e.getAdditionalInfo());
        }
    }
    private void showDoctorsList() {
        Stage doctorsStage = new Stage();
        doctorsStage.setTitle("Список врачей");
        doctorsStage.setMinWidth(800);
        doctorsStage.setMinHeight(600);

        VBox mainLayout = new VBox(10);
        mainLayout.setStyle("-fx-padding: 20;");

        Label title = new Label("Список врачей");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Создаем таблицу
        TableView<Doctor> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white;");

        // ВОССТАНАВЛИВАЕМ КОЛОНКУ ID
        TableColumn<Doctor, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cellData.getValue().getId()).asObject()
        );
        idColumn.setPrefWidth(60);
        idColumn.setStyle("-fx-alignment: CENTER;");
        idColumn.setSortable(true); // Добавляем возможность сортировки по ID

        // Колонка Фамилия
        TableColumn<Doctor, String> surnameColumn = new TableColumn<>("Фамилия");
        surnameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSurname()
                )
        );
        surnameColumn.setPrefWidth(120);

        // Колонка Имя
        TableColumn<Doctor, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getName()
                )
        );
        nameColumn.setPrefWidth(100);

        // Колонка Отчество
        TableColumn<Doctor, String> secondNameColumn = new TableColumn<>("Отчество");
        secondNameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSecondName() != null ?
                                cellData.getValue().getSecondName() : ""
                )
        );
        secondNameColumn.setPrefWidth(120);

        // Колонка Специализация
        TableColumn<Doctor, String> specialtyColumn = new TableColumn<>("Специализация");
        specialtyColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSpecialty()
                )
        );
        specialtyColumn.setPrefWidth(150);

        // Метод для обновления таблицы
        final Runnable refreshDoctorsTable = () -> {
            try {
                DoctorDAO doctorDAO = new DoctorDAO();
                List<Doctor> doctors = doctorDAO.getAllDoctors();
                tableView.getItems().setAll(doctors);

                // Автоматически сортируем по ID (опционально)
                idColumn.setSortType(TableColumn.SortType.ASCENDING);
                tableView.getSortOrder().add(idColumn);
                tableView.sort();

            } catch (PolyclinicException e) {
                System.err.println("Ошибка при загрузке врачей: " + e.getMessage());
                e.printStackTrace();
                showError("Ошибка загрузки врачей: " + e.getErrorCode().getDescription() +
                        "\n" + e.getAdditionalInfo());
            }
        };

        // Колонка действий
        TableColumn<Doctor, Void> actionsColumn = new TableColumn<>("Действия");
        actionsColumn.setPrefWidth(200);
        actionsColumn.setCellFactory(param -> new TableCell<Doctor, Void>() {
            private final Button editBtn = new Button("Редактировать");
            private final Button scheduleBtn = new Button("Расписание");
            private final Button deleteBtn = new Button("Удалить");
            private final HBox buttons = new HBox(5, editBtn, scheduleBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 6; -fx-background-color: #98FB98;");
                scheduleBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 6; -fx-background-color: #ADD8E6;");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 6; -fx-background-color: #FFB6C1;");

                editBtn.setOnAction(event -> {
                    Doctor doctor = getTableView().getItems().get(getIndex());
                    showEditDoctorForm(doctor);
                });

                scheduleBtn.setOnAction(event -> {
                    Doctor doctor = getTableView().getItems().get(getIndex());
                    showDoctorSchedule(doctor.getId());
                });

                deleteBtn.setOnAction(event -> {
                    Doctor doctor = getTableView().getItems().get(getIndex());
                    deleteDoctor(doctor.getId());
                    refreshDoctorsTable.run();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        // ДОБАВЛЯЕМ КОЛОНКУ ID В НАЧАЛО
        tableView.getColumns().addAll(idColumn, surnameColumn, nameColumn,
                secondNameColumn, specialtyColumn, actionsColumn);

        // Первоначальная загрузка
        refreshDoctorsTable.run();

        // Кнопки управления
        HBox buttonPanel = new HBox(15);
        buttonPanel.setAlignment(Pos.CENTER);

        Button addDoctorBtn = new Button("Добавить врача");
        addDoctorBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #98FB98; " +
                "-fx-text-fill: #333; -fx-background-radius: 10; -fx-border-radius: 10;");
        addDoctorBtn.setOnAction(e -> {
            showAddDoctorForm();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(refreshDoctorsTable);
                }
            }, 1000);
        });

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #FFB6C1; " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10;");
        refreshBtn.setOnAction(e -> refreshDoctorsTable.run());

        Button closeBtn = createCloseButton(doctorsStage);

        // Добавляем кнопки в панель
        buttonPanel.getChildren().addAll(addDoctorBtn, refreshBtn, closeBtn);

        mainLayout.getChildren().addAll(title, tableView, buttonPanel);

        Scene scene = new Scene(mainLayout, 900, 600); // Увеличил ширину для ID
        doctorsStage.setScene(scene);
        doctorsStage.show();
    }

    private void showAddPatientForm() {
        Stage formStage = new Stage();
        formStage.setTitle("Добавить нового пациента");
        formStage.setMinWidth(450);
        formStage.setMinHeight(650);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 25; -fx-alignment: center; -fx-background-color: #FFE4E1;");

        Label title = new Label("Добавить нового пациента");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Карточка формы
        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-padding: 20; -fx-background-color: white; " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2;");

        // Функция для создания Label
        java.util.function.Function<String, Label> createFormLabel = (text) -> {
            Label label = new Label(text);
            label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #555;");
            return label;
        };

        // Основные данные
        Label personalInfoLabel = new Label("Личные данные:");
        personalInfoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #9C27B0;");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Иванов");
        surnameField.setStyle("-fx-pref-width: 300px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Иван");
        nameField.setStyle("-fx-pref-width: 300px;");

        TextField secondNameField = new TextField();
        secondNameField.setPromptText("Иванович (необязательно)");
        secondNameField.setStyle("-fx-pref-width: 300px;");

        TextField policyField = new TextField();
        policyField.setPromptText("123456 (6 цифр)");
        policyField.setStyle("-fx-pref-width: 300px;");

        TextField phoneField = new TextField();
        phoneField.setPromptText("+79991234567");
        phoneField.setStyle("-fx-pref-width: 300px;");

        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setPromptText("дд.мм.гггг");
        birthDatePicker.setStyle("-fx-pref-width: 300px;");

        // Адрес - разделенные поля
        Label addressLabel = new Label("Адрес:");
        addressLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #9C27B0;");

        // Улица
        HBox streetBox = new HBox(10);
        streetBox.setAlignment(Pos.CENTER_LEFT);
        Label streetLabel = createFormLabel.apply("Улица*:");
        TextField streetField = new TextField();
        streetField.setPromptText("Ленина (автоматически добавится 'ул.')");
        streetField.setStyle("-fx-pref-width: 250px;");
        streetField.setTextFormatter(ValidationUtils.createStreetFormatter());
        streetBox.getChildren().addAll(streetLabel, streetField);

        // Дом
        HBox houseBox = new HBox(10);
        houseBox.setAlignment(Pos.CENTER_LEFT);
        Label houseLabel = createFormLabel.apply("Дом*:");
        TextField houseField = new TextField();
        houseField.setPromptText("15");
        houseField.setStyle("-fx-pref-width: 80px;");
        houseBox.getChildren().addAll(houseLabel, houseField);
        houseField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9а-яА-Яa-zA-Z]*")) {
                return change;
            }
            return null;
        }));
        streetField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !streetField.getText().trim().isEmpty()) {
                String formatted = ValidationUtils.autoFormatStreet(streetField.getText());
                if (!formatted.equals(streetField.getText())) {
                    // Сохраняем позицию курсора
                    int caretPosition = streetField.getCaretPosition();
                    streetField.setText(formatted);

                    // Восстанавливаем позицию курсора
                    Platform.runLater(() -> {
                        streetField.positionCaret(Math.min(caretPosition, formatted.length()));
                    });
                }
            }
        });

        HBox apartmentBox = new HBox(10);
        apartmentBox.setAlignment(Pos.CENTER_LEFT);
        Label apartmentLabel = createFormLabel.apply("Квартира*:");
        TextField apartmentField = new TextField();
        apartmentField.setPromptText("23 (обязательно)");
        apartmentField.setStyle("-fx-pref-width: 100px;");
        apartmentField.setTextFormatter(ValidationUtils.createApartmentFormatter());
        apartmentBox.getChildren().addAll(apartmentLabel, apartmentField);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        // Валидация
        setupAutoCapitalize(surnameField);
        setupAutoCapitalize(nameField);
        setupAutoCapitalize(secondNameField);

        surnameField.setTextFormatter(ValidationUtils.createNameFormatter());
        nameField.setTextFormatter(ValidationUtils.createNameFormatter());
        policyField.setTextFormatter(ValidationUtils.createDigitsFormatter());
        phoneField.setTextFormatter(ValidationUtils.createPhoneFormatter());
        houseField.setTextFormatter(ValidationUtils.createDigitsFormatter());

        // Кнопки
        Button saveBtn = new Button("Сохранить пациента");
        saveBtn.setStyle("-fx-font-size: 14px; -fx-padding: 12 25; " +
                "-fx-background-color: #98FB98; -fx-text-fill: #333; " +
                "-fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: #7CFC00; -fx-border-width: 2; " +
                "-fx-cursor: hand;");

        Button cancelBtn = createCancelButton(formStage);

        HBox buttonBox = new HBox(15, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // Обработчик сохранения
        saveBtn.setOnAction(e -> {
            String surname = surnameField.getText().trim();
            String name = nameField.getText().trim();
            String secondName = secondNameField.getText().trim();
            String policyStr = policyField.getText().trim();
            String phone = phoneField.getText().trim();
            LocalDate birthDate = birthDatePicker.getValue();
            String street = streetField.getText().trim();
            String house = houseField.getText().trim();
            String apartment = apartmentField.getText().trim();

            // Собираем адрес с автоформатированием
            String address = "";
            if (!street.isEmpty() && !house.isEmpty() && !apartment.isEmpty()) {
                // Автоматически форматируем улицу
                String formattedStreet = ValidationUtils.autoFormatStreet(street);

                // Просто собираем адрес из отформатированных компонентов
                address = formattedStreet + ", д. " + house + ", кв. " + apartment;
            }

            // Валидация
            InputValidator.ValidationResult surnameValidation = InputValidator.validateName(surname, "Фамилия");
            InputValidator.ValidationResult nameValidation = InputValidator.validateName(name, "Имя");
            InputValidator.ValidationResult policyValidation = InputValidator.validatePolicyNumber(policyStr);
            InputValidator.ValidationResult phoneValidation = InputValidator.validatePhone(phone);
            InputValidator.ValidationResult secondNameValidation = InputValidator.validatePatronymic(secondName);

            // Валидация адреса
            if (street.isEmpty()) {
                statusLabel.setText("Укажите улицу!");
                statusLabel.setStyle("-fx-text-fill: red;");
                streetField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            } else {
                streetField.setStyle("");
            }

            if (house.isEmpty()) {
                statusLabel.setText("Укажите номер дома!");
                statusLabel.setStyle("-fx-text-fill: red;");
                houseField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            } else {
                houseField.setStyle("");
            }

            // Квартира обязательна
            if (apartment.isEmpty()) {
                statusLabel.setText("Укажите номер квартиры!");
                statusLabel.setStyle("-fx-text-fill: red;");
                apartmentField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            } else {
                apartmentField.setStyle("");
            }


            if (birthDate == null) {
                statusLabel.setText("Укажите дату рождения!");
                statusLabel.setStyle("-fx-text-fill: red;");
                birthDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            } else if (birthDate.isAfter(LocalDate.now())) {
                statusLabel.setText("Дата рождения не может быть в будущем!");
                statusLabel.setStyle("-fx-text-fill: red;");
                birthDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            } else if (birthDate.isBefore(LocalDate.now().minusYears(150))) {
                statusLabel.setText("Дата рождения слишком старая! Максимум 150 лет назад.");
                statusLabel.setStyle("-fx-text-fill: red;");
                birthDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            } else {
                birthDatePicker.setStyle("");
            }


            ValidationUtils.highlightInvalidField(surnameField, surnameValidation.isValid());
            ValidationUtils.highlightInvalidField(nameField, nameValidation.isValid());
            ValidationUtils.highlightInvalidField(policyField, policyValidation.isValid());
            ValidationUtils.highlightInvalidField(phoneField, phoneValidation.isValid());
            ValidationUtils.highlightInvalidField(secondNameField, secondNameValidation.isValid());


            if (!surnameValidation.isValid()) {
                statusLabel.setText(surnameValidation.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (!nameValidation.isValid()) {
                statusLabel.setText(nameValidation.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (!secondNameValidation.isValid()) {
                statusLabel.setText(secondNameValidation.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (!policyValidation.isValid()) {
                statusLabel.setText(policyValidation.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (!phoneValidation.isValid()) {
                statusLabel.setText(phoneValidation.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Преобразуем номер полиса в int для проверки
            int policy;
            try {
                policy = Integer.parseInt(policyStr);
            } catch (NumberFormatException ex) {
                statusLabel.setText("Номер полиса должен содержать только цифры!");
                statusLabel.setStyle("-fx-text-fill: red;");
                policyField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            }

            // Проверка уникальности телефона (только для добавления)
            try {
                PatientDAO patientDAO = new PatientDAO();
                if (patientDAO.phoneNumberExists(phone)) {
                    statusLabel.setText("Этот номер телефона уже зарегистрирован у другого пациента!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    phoneField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    return;
                }
            } catch (PolyclinicException ex) {
                System.err.println("Ошибка проверки телефона: " + ex.getMessage());
            }

            // Проверка уникальности номера полиса
            try {
                PatientDAO patientDAO = new PatientDAO();
                if (patientDAO.policyNumberExists(policy)) {
                    statusLabel.setText("Номер полиса занят!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    policyField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    return;
                }
            } catch (PolyclinicException ex) {
                System.err.println("Ошибка проверки полиса: " + ex.getMessage());
            }

            // Сохранение
            try {
                Patient newPatient = new Patient(name, surname, secondName, policy, phone, address, birthDate);
                PatientDAO patientDAO = new PatientDAO();

                if (patientDAO.addPatient(newPatient)) {
                    statusLabel.setText("Пациент успешно добавлен! Полис: " + String.format("%06d", policy));
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

                    // Логирование добавления пациента
                    AppLogger.audit("Добавление пациента",
                            "Система",
                            "Добавлен пациент: " + surname + " " + name +
                                    " (полис: " + String.format("%06d", policy) + ")");

                    AppLogger.info("Пациент успешно добавлен: " +
                            "ФИО=" + surname + " " + name + " " + (secondName != null ? secondName : "") +
                            ", Полис=" + policy +
                            ", Телефон=" + phone);

                    // Очистка полей
                    surnameField.clear();
                    nameField.clear();
                    secondNameField.clear();
                    policyField.clear();
                    phoneField.clear();
                    birthDatePicker.setValue(null);
                    streetField.clear();
                    houseField.clear();
                    apartmentField.clear();

                    // Автоматическое закрытие через 2 секунды
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(event -> formStage.close());
                    pause.play();

                } else {
                    statusLabel.setText("Ошибка при добавлении пациента!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (PolyclinicException ex) {
                System.err.println("Ошибка поликлиники: " + ex.getMessage());
                ex.printStackTrace();

                // Проверяем, является ли ошибка дублированием полиса или телефона
                String errorMessage = ex.getAdditionalInfo();
                if (errorMessage != null) {
                    if (errorMessage.contains("Номер полиса занят") ||
                            errorMessage.toLowerCase().contains("policy_number")) {
                        statusLabel.setText("Номер полиса занят!");
                        statusLabel.setStyle("-fx-text-fill: red;");
                        policyField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    } else if (errorMessage.contains("Номер телефона уже зарегистрирован") ||
                            errorMessage.toLowerCase().contains("phone")) {
                        statusLabel.setText("Этот номер телефона уже зарегистрирован у другого пациента!");
                        statusLabel.setStyle("-fx-text-fill: red;");
                        phoneField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    } else {
                        statusLabel.setText("Ошибка: " + ex.getErrorCode().getDescription() +
                                "\n" + ex.getAdditionalInfo());
                        statusLabel.setStyle("-fx-text-fill: red;");
                    }
                } else {
                    statusLabel.setText("Ошибка: " + ex.getErrorCode().getDescription() +
                            "\n" + ex.getAdditionalInfo());
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Номер полиса должен содержать только цифры!");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // Собираем форму
        formCard.getChildren().addAll(
                personalInfoLabel,
                createFormLabel.apply("Фамилия*:"), surnameField,
                createFormLabel.apply("Имя*:"), nameField,
                createFormLabel.apply("Отчество:"), secondNameField,
                createFormLabel.apply("Номер полиса* (6 цифр):"), policyField,
                createFormLabel.apply("Телефон*:"), phoneField,
                createFormLabel.apply("Дата рождения*:"), birthDatePicker,
                addressLabel,
                streetBox, houseBox, apartmentBox,
                statusLabel,
                buttonBox
        );

        layout.getChildren().addAll(title, formCard);

        Scene scene = new Scene(layout, 450, 700);
        formStage.setScene(scene);
        formStage.show();
    }


    private void showAppointments() {
        Stage appointmentsStage = new Stage();
        appointmentsStage.setTitle("Запись на приём");

        appointmentsStage.setMinWidth(550);
        appointmentsStage.setMinHeight(650);

        // Основной контейнер с прокруткой
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: #FFE4E1;");

        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #FFE4E1;");

        // Заголовок
        Label title = new Label("Запись на приём");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Информационное сообщение о расписании
        Label infoLabel = new Label("ПРИМЕЧАНИЕ: Врачи ведут прием только в дни, указанные в расписании.");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #FF4500; -fx-font-weight: bold; " +
                "-fx-padding: 5; -fx-background-color: #FFF0F5; -fx-background-radius: 5;");
        infoLabel.setWrapText(true);
        infoLabel.setMaxWidth(450);

        // Карточка формы
        VBox formCard = new VBox(20);
        formCard.setStyle("-fx-padding: 25; -fx-background-color: white; " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        java.util.function.Function<String, Label> createFormLabel = (text) -> {
            Label label = new Label(text);
            label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
            label.setFont(Font.font("System", FontWeight.BOLD, 14));
            return label;
        };

        // Создаем Label
        Label doctorLabel = createFormLabel.apply("Выберите врача:");
        Label patientLabel = createFormLabel.apply("Выберите пациента:");
        Label dateLabel = createFormLabel.apply("Выберите дату:");
        Label timeLabel = createFormLabel.apply("Выберите время:");
        Label notesLabel = createFormLabel.apply("Дополнительная информация (необязательно):");

        ComboBox<Doctor> doctorComboBox = new ComboBox<>();
        doctorComboBox.setPromptText("Выберите врача...");
        doctorComboBox.setStyle("-fx-pref-width: 300px; -fx-font-family: 'System';");

        // ComboBox для пациента
        ComboBox<Patient> patientComboBox = new ComboBox<>();
        patientComboBox.setPromptText("Выберите пациента...");
        patientComboBox.setStyle("-fx-pref-width: 300px; -fx-font-family: 'System';");

        // DatePicker
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Выберите дату...");
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                LocalDate maxDate = today.plusYears(1);

                // Деактивируем прошедшие даты и даты более чем через год
                setDisable(empty || date.isBefore(today) || date.isAfter(maxDate));

                if (date.isBefore(today)) {
                    setTooltip(new Tooltip("Нельзя выбрать прошедшую дату"));
                } else if (date.isAfter(maxDate)) {
                    setTooltip(new Tooltip("Запись возможна не более чем на год вперед"));
                }
            }
        });
        datePicker.setStyle("-fx-pref-width: 300px;");

        // ComboBox для времени
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.setPromptText("Выберите время...");
        timeComboBox.setStyle("-fx-pref-width: 300px;");
        timeComboBox.setDisable(true);

        // TextArea для заметок
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Опишите симптомы или другие детали...");
        notesArea.setStyle("-fx-pref-width: 300px; -fx-pref-height: 80px;");
        notesArea.setWrapText(true);

        // Статус
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-font-family: 'System';");

        // Кнопки
        Button bookButton = new Button("Записаться на приём");
        bookButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 25; " +
                "-fx-background-color: #98FB98; -fx-text-fill: #333; " +
                "-fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: #7CFC00; -fx-border-width: 2; " +
                "-fx-cursor: hand; -fx-font-family: 'System';");

        Button cancelButton = createCancelButton(appointmentsStage);

        HBox buttonBox = new HBox(15, bookButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);


        loadDoctorsForAppointment(doctorComboBox);
        loadPatientsForAppointment(patientComboBox);

        doctorComboBox.setOnAction(e -> {
            if (doctorComboBox.getValue() != null && datePicker.getValue() != null) {
                updateAvailableTimes(doctorComboBox.getValue().getId(),
                        datePicker.getValue(),
                        timeComboBox,
                        patientComboBox.getValue());
            }
        });

        datePicker.setOnAction(e -> {
            if (doctorComboBox.getValue() != null && datePicker.getValue() != null) {
                updateAvailableTimes(doctorComboBox.getValue().getId(),
                        datePicker.getValue(),
                        timeComboBox,
                        patientComboBox.getValue());
            }
        });

        patientComboBox.setOnAction(e -> {
            if (doctorComboBox.getValue() != null && datePicker.getValue() != null) {
                updateAvailableTimes(doctorComboBox.getValue().getId(),
                        datePicker.getValue(),
                        timeComboBox,
                        patientComboBox.getValue());
            }
        });
        bookButton.setOnAction(e -> {
            bookAppointment(doctorComboBox, patientComboBox, datePicker, timeComboBox, notesArea, statusLabel);
        });

        cancelButton.setOnAction(e -> appointmentsStage.hide());
        // Собираем форму
        formCard.getChildren().add(0, infoLabel);
        formCard.getChildren().addAll(
                doctorLabel, doctorComboBox,
                patientLabel, patientComboBox,
                dateLabel, datePicker,
                timeLabel, timeComboBox,
                notesLabel, notesArea,
                statusLabel,
                buttonBox
        );


        // Собираем основной layout
        mainLayout.getChildren().addAll(title, formCard);
        scrollPane.setContent(mainLayout);

        // Размеры
        scrollPane.setMinWidth(550);
        scrollPane.setMinHeight(650);

        Scene scene = new Scene(scrollPane, 550, 650);
        appointmentsStage.setScene(scene);
        appointmentsStage.show();
    }


    // Метод возвращает список дней недели, когда у врача есть расписание
    private List<String> getDaysWithSchedule(int doctorId) {
        List<String> days = new ArrayList<>();
        try {
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            List<Schedule> schedules = scheduleDAO.getScheduleByDoctor(doctorId);

            for (Schedule schedule : schedules) {
                String dayName = getDayName(schedule.getDayOfWeek().getValue());
                if (!days.contains(dayName)) {
                    days.add(dayName);
                }
            }

        } catch (PolyclinicException e) {
            System.err.println("Ошибка при получении дней расписания: " + e.getMessage());
        }
        return days;
    }

    // Метод для отображения дней с расписанием (можно добавить как подсказку)
    private void showScheduleInfo(int doctorId, ComboBox<Doctor> doctorComboBox) {
        List<String> availableDays = getDaysWithSchedule(doctorId);

        if (!availableDays.isEmpty()) {
            String info = "Врач ведет прием в дни: " + String.join(", ", availableDays);
            Tooltip tooltip = new Tooltip(info);
            doctorComboBox.setTooltip(tooltip);
        }
    }

    private void loadDoctorsForAppointment(ComboBox<Doctor> comboBox) {
        try {
            DoctorDAO doctorDAO = new DoctorDAO();
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            comboBox.getItems().clear();
            comboBox.getItems().addAll(doctors);

            comboBox.setConverter(new StringConverter<Doctor>() {
                @Override
                public String toString(Doctor doctor) {
                    if (doctor == null) return "";
                    String fullName = doctor.getSurname() + " " + doctor.getName();
                    if (doctor.getSecondName() != null && !doctor.getSecondName().trim().isEmpty()) {
                        fullName += " " + doctor.getSecondName();
                    }
                    return String.format("ID: %d | %s - %s",
                            doctor.getId(),
                            fullName,
                            doctor.getSpecialty());
                }

                @Override
                public Doctor fromString(String string) {
                    return null;
                }
            });

            System.out.println("Загружено врачей для записи: " + doctors.size());

        } catch (Exception e) {
            System.out.println("Ошибка загрузки врачей: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPatientsForAppointment(ComboBox<Patient> comboBox) {
        try {
            PatientDAO patientDAO = new PatientDAO();
            List<Patient> patients = patientDAO.getAllPatients();
            comboBox.getItems().clear();
            comboBox.getItems().addAll(patients);

            comboBox.setConverter(new StringConverter<Patient>() {
                @Override
                public String toString(Patient patient) {
                    if (patient == null) return "";
                    return patient.getSurname() + " " + patient.getName() +
                            " (Полис: " + String.format("%06d", patient.getPolicy()) + ")";
                }

                @Override
                public Patient fromString(String string) {
                    return null;
                }
            });

        } catch (Exception e) {
            System.out.println("Ошибка загрузки пациентов: " + e.getMessage());
        }
    }
    private void updateAvailableTimes(int doctorId, LocalDate date,
                                      ComboBox<String> timeComboBox,
                                      Patient selectedPatient) {
        try {
            // Проверяем, есть ли у врача расписание на этот день недели
            boolean hasScheduleForDay = hasScheduleForDay(doctorId, date);

            if (!hasScheduleForDay) {
                timeComboBox.getItems().clear();
                timeComboBox.setDisable(true);
                timeComboBox.setPromptText("выберите другой день, в этот день врач приём не ведет");
                return;
            }

            AppointmentDAO appointmentDAO = new AppointmentDAO();
            List<LocalTime> availableSlots = appointmentDAO.getAvailableTimeSlots(doctorId, date);

            // Фильтруем слоты, где пациент уже занят (если пациент выбран)
            if (selectedPatient != null) {
                List<LocalTime> filteredSlots = new ArrayList<>();

                for (LocalTime time : availableSlots) {
                    LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);

                    // Проверяем, занят ли пациент в это время
                    boolean isPatientBusy = isPatientBusyAtTime(selectedPatient.getPolicy(), appointmentDateTime);

                    if (!isPatientBusy) {
                        filteredSlots.add(time);
                    }
                }

                availableSlots = filteredSlots;
            }

            timeComboBox.getItems().clear();
            timeComboBox.setDisable(availableSlots.isEmpty());

            if (availableSlots.isEmpty()) {
                if (selectedPatient != null) {
                    timeComboBox.setPromptText("Нет свободных слотов или у пациента уже есть записи");
                } else {
                    timeComboBox.setPromptText("Нет доступных слотов на эту дату");
                }
            } else {
                timeComboBox.setPromptText("Выберите время...");

                // Фильтруем прошедшие временные слоты для сегодняшней даты
                List<LocalTime> filteredSlots = availableSlots;
                if (date.equals(LocalDate.now())) {
                    LocalTime now = LocalTime.now();
                    filteredSlots = availableSlots.stream()
                            .filter(time -> time.isAfter(now))
                            .collect(Collectors.toList());
                }

                for (LocalTime time : filteredSlots) {
                    timeComboBox.getItems().add(time.toString());
                }

                // Если после фильтрации слотов не осталось
                if (filteredSlots.isEmpty()) {
                    timeComboBox.setPromptText("Все слоты на сегодня уже прошли");
                    timeComboBox.setDisable(true);
                } else {
                    timeComboBox.getSelectionModel().selectFirst();
                }
            }

        } catch (PolyclinicException e) {
            System.err.println("Ошибка при обновлении времени: " + e.getMessage());
            timeComboBox.getItems().clear();
            timeComboBox.setPromptText("Ошибка загрузки времени");
            timeComboBox.setDisable(true);
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка при обновлении времени: " + e.getMessage());
            timeComboBox.getItems().clear();
            timeComboBox.setPromptText("Ошибка загрузки времени");
            timeComboBox.setDisable(true);
        }
    }
    // Проверяет, есть ли у пациента запись на указанное время
    private boolean hasPatientAppointmentConflict(int patientPolicy, LocalDateTime appointmentDateTime) {
        try {
            AppointmentDAO appointmentDAO = new AppointmentDAO();

            // Метод 1: Проверка точного совпадения времени
            String sql = "SELECT COUNT(*) FROM appointments WHERE patient_policy = ? AND appointment_datetime = ?";
            try (Connection conn = DatabaseManager.getConnectionStatic();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, patientPolicy);
                stmt.setObject(2, appointmentDateTime);

                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }

            // Метод 2: Проверка, что пациент не занят в соседние слоты (+- 30 минут)
            // (прием обычно длится 30 минут)
            LocalDateTime startTime = appointmentDateTime.minusMinutes(29);
            LocalDateTime endTime = appointmentDateTime.plusMinutes(30);

            String sql2 = """
            SELECT COUNT(*) FROM appointments 
            WHERE patient_policy = ? 
            AND appointment_datetime BETWEEN ? AND ?
        """;

            try (Connection conn = DatabaseManager.getConnectionStatic();
                 PreparedStatement stmt = conn.prepareStatement(sql2)) {

                stmt.setInt(1, patientPolicy);
                stmt.setObject(2, startTime);
                stmt.setObject(3, endTime);

                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            System.err.println("Ошибка при проверке конфликта пациента: " + e.getMessage());
            return false; // В случае ошибки считаем, что конфликта нет
        }
    }

    private void bookAppointment(ComboBox<Doctor> doctorComboBox, ComboBox<Patient> patientComboBox,
                                 DatePicker datePicker, ComboBox<String> timeComboBox,
                                 TextArea notesArea, Label statusLabel) {

        statusLabel.setText("");
        statusLabel.setStyle("");
        AppLogger.debug("Начало создания записи на приём");

        // 1. Проверка выбора врача
        if (doctorComboBox.getValue() == null) {
            statusLabel.setText("Выберите врача!");
            statusLabel.setStyle("-fx-text-fill: #FF4500;");
            return;
        }

        // 2. Проверка выбора пациента
        if (patientComboBox.getValue() == null) {
            statusLabel.setText("Выберите пациента!");
            statusLabel.setStyle("-fx-text-fill: #FF4500;");
            return;
        }

        // 3. Проверка выбора даты
        if (datePicker.getValue() == null) {
            statusLabel.setText("Выберите дату!");
            statusLabel.setStyle("-fx-text-fill: #FF4500;");
            return;
        }

        // 4. Проверка выбора времени
        if (timeComboBox.getValue() == null || timeComboBox.getValue().isEmpty()) {
            statusLabel.setText("Выберите время!");
            statusLabel.setStyle("-fx-text-fill: #FF4500;");
            return;
        }

        try {
            // Получаем выбранные значения
            Doctor doctor = doctorComboBox.getValue();
            Patient patient = patientComboBox.getValue();
            LocalDate selectedDate = datePicker.getValue();
            LocalTime selectedTime = LocalTime.parse(timeComboBox.getValue());
            LocalDateTime appointmentDateTime = LocalDateTime.of(selectedDate, selectedTime);
            AppLogger.audit("Создание записи",
                    "Система",
                    "Пациент: " + patient.getFullName() +
                            " (полис: " + patient.getPolicy() + "), " +
                            "Врач: " + doctor.getFullName() +
                            ", Дата: " + appointmentDateTime);

            // ПРОВЕРКА №1: Работает ли врач в этот день
            boolean hasScheduleForDay = hasScheduleForDay(doctor.getId(), selectedDate);
            if (!hasScheduleForDay) {
                statusLabel.setText("В этот день врач приём не ведет! Выберите другой день.");
                statusLabel.setStyle("-fx-text-fill: #FF4500;");
                return;
            }

            // ПРОВЕРКА №2: Нельзя записываться на прошедшие даты
            if (appointmentDateTime.isBefore(LocalDateTime.now())) {
                statusLabel.setText("Нельзя создать запись на прошедшую дату и время!");
                statusLabel.setStyle("-fx-text-fill: #FF4500;");
                return;
            }

            // ПРОВЕРКА №3: Запись не должна быть слишком далеко в будущем
            if (appointmentDateTime.isAfter(LocalDateTime.now().plusYears(1))) {
                statusLabel.setText("Запись возможна не более чем на год вперед!");
                statusLabel.setStyle("-fx-text-fill: #FF4500;");
                return;
            }

            // Все проверки пройдены - создаем запись
            Appointment appointment = new Appointment(
                    doctor.getId(),
                    patient.getPolicy(),
                    appointmentDateTime
            );
            appointment.setNotes(notesArea.getText());

            // Сохраняем в БД
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            if (appointmentDAO.createAppointment(appointment)) {
                // Успешная запись
                statusLabel.setText("Запись успешно создана! ID записи: " + appointment.getId());
                statusLabel.setStyle("-fx-text-fill: #2E8B57; -fx-font-weight: bold;");
                AppLogger.info("Запись успешно создана: ID=" + appointment.getId() +
                        ", Врач ID=" + doctor.getId() +
                        ", Пациент полис=" + patient.getPolicy());


                // Анимация успеха
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> {
                    // Очищаем форму
                    doctorComboBox.setValue(null);
                    patientComboBox.setValue(null);
                    datePicker.setValue(null);
                    timeComboBox.setValue(null);
                    notesArea.clear();
                });
                pause.play();

            } else {
                statusLabel.setText("Ошибка при создании записи!");
                statusLabel.setStyle("-fx-text-fill: #FF4500;");
                AppLogger.error("Ошибка создания записи (DAO вернул false)");
            }

        } catch (PolyclinicException ex) {
            AppLogger.error("Ошибка при создании записи: " + ex.getErrorCode().getDescription(), ex);
            // Обработка нашего кастомного исключения
            System.err.println("Ошибка поликлиники при создании записи: " + ex.getMessage());
            ex.printStackTrace();

            // Определяем, какое сообщение показать пользователю
            String userMessage;
            switch (ex.getErrorCode()) {
                case APPOINTMENT_CONFLICT:
                    userMessage = "Конфликт записи: " + ex.getAdditionalInfo();
                    break;
                case SCHEDULE_CONFLICT:
                    userMessage = "Проблема с расписанием: " + ex.getAdditionalInfo();
                    break;
                case DATABASE_ERROR:
                    userMessage = "Ошибка базы данных: " + ex.getAdditionalInfo();
                    break;
                case VALIDATION_ERROR:
                    userMessage = ex.getAdditionalInfo();
                    break;
                default:
                    userMessage = "Ошибка: " + ex.getErrorCode().getDescription();
            }

            statusLabel.setText(userMessage);
            statusLabel.setStyle("-fx-text-fill: #FF4500; -fx-font-size: 12px;");

        } catch (Exception e) {
            AppLogger.error("Неизвестная ошибка при создании записи", e);
            System.err.println("Неизвестная ошибка при создании записи: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Ошибка: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #FF4500;");
        }
    }
    // Проверяет, занят ли пациент в указанное время
    private boolean isPatientBusyAtTime(int patientPolicy, LocalDateTime appointmentDateTime) {
        try {
            Connection conn = DatabaseManager.getConnectionStatic();

            // Проверяем, есть ли у пациента запись на это время или в соседние слоты
            LocalDateTime startTime = appointmentDateTime.minusMinutes(29); // за 29 минут до
            LocalDateTime endTime = appointmentDateTime.plusMinutes(30);    // через 30 минут после

            String sql = """
            SELECT COUNT(*) FROM appointments 
            WHERE patient_policy = ? 
            AND appointment_datetime BETWEEN ? AND ?
        """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, patientPolicy);
                stmt.setObject(2, startTime);
                stmt.setObject(3, endTime);

                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    conn.close();
                    return true;
                }
            }

            conn.close();
            return false;

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке занятости пациента: " + e.getMessage());
            return false; // В случае ошибки считаем, что пациент не занят
        }
    }
    private void showError(String message) {
        Stage errorStage = new Stage();
        errorStage.setTitle("Ошибка");

        VBox layout = new VBox(20);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        Button okBtn = new Button("OK");
        okBtn.setOnAction(e -> errorStage.close());

        layout.getChildren().addAll(errorLabel, okBtn);

        Scene scene = new Scene(layout, 300, 150);
        errorStage.setScene(scene);
        errorStage.show();
    }

    // ФОРМА РЕДАКТИРОВАНИЯ ВРАЧА
    private void showEditDoctorForm(Doctor doctor) {
        Stage formStage = new Stage();
        formStage.setTitle("Редактировать врача: " + doctor.getFullName());
        formStage.setMinWidth(400);
        formStage.setMinHeight(500);
        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center;");

        Label title = new Label("Редактировать врача");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // ПОЛЯ С ПРЕДЗАПОЛНЕННЫМИ ДАННЫМИ
        TextField surnameField = new TextField(doctor.getSurname());
        surnameField.setPromptText("Фамилия*");
        surnameField.setStyle("-fx-pref-width: 250px;");

        TextField nameField = new TextField(doctor.getName());
        nameField.setPromptText("Имя*");
        nameField.setStyle("-fx-pref-width: 250px;");

        TextField secondNameField = new TextField(doctor.getSecondName());
        secondNameField.setPromptText("Отчество");
        secondNameField.setStyle("-fx-pref-width: 250px;");

        ComboBox<String> specialtyComboBox = new ComboBox<>();
        specialtyComboBox.setPromptText("Специализация*");
        specialtyComboBox.setStyle("-fx-pref-width: 250px;");
        specialtyComboBox.getItems().addAll(
                "Терапевт", "Хирург", "Педиатр", "Невролог", "Кардиолог",
                "Офтальмолог", "Отоларинголог", "Стоматолог", "Гинеколог",
                "Уролог", "Дерматолог", "Психиатр", "Ортопед", "Травматолог"
        );
        specialtyComboBox.setValue(doctor.getSpecialty());

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        // ВАЛИДАЦИЯ
        setupAutoCapitalize(surnameField);
        setupAutoCapitalize(nameField);
        setupAutoCapitalize(secondNameField);

        // Кнопка сохранения - МЯТНАЯ
        Button saveBtn = new Button("Сохранить изменения");
        saveBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #98FB98; " +
                "-fx-text-fill: #333; -fx-background-radius: 10; -fx-border-radius: 10;");

        // Кнопка отмены - РОЗОВАЯ
        Button cancelBtn = new Button("Отмена");
        cancelBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #FFB6C1; " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10;");

        saveBtn.setOnAction(e -> {
            String surname = surnameField.getText().trim();
            String name = nameField.getText().trim();
            String secondName = secondNameField.getText().trim();
            String specialty = specialtyComboBox.getValue();

            // ПОДСВЕТКА ОШИБОК ПРИ ВАЛИДАЦИИ
            boolean hasErrors = false;

            // ВАЛИДАЦИЯ фамилии
            InputValidator.ValidationResult surnameValidation = InputValidator.validateName(surname, "Фамилия");
            if (!surnameValidation.isValid()) {
                statusLabel.setText(surnameValidation.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                surnameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hasErrors = true;
            } else {
                surnameField.setStyle("");
            }

            // ВАЛИДАЦИЯ имени
            InputValidator.ValidationResult nameValidation = InputValidator.validateName(name, "Имя");
            if (!nameValidation.isValid()) {
                if (hasErrors) {
                    statusLabel.setText(statusLabel.getText() + "\n" + nameValidation.getMessage());
                } else {
                    statusLabel.setText(nameValidation.getMessage());
                }
                statusLabel.setStyle("-fx-text-fill: red;");
                nameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hasErrors = true;
            } else {
                nameField.setStyle("");
            }

            InputValidator.ValidationResult secondNameValidation = InputValidator.validatePatronymic(secondName);
            if (!secondNameValidation.isValid()) {
                if (hasErrors) {
                    statusLabel.setText(statusLabel.getText() + "\n" + secondNameValidation.getMessage());
                } else {
                    statusLabel.setText(secondNameValidation.getMessage());
                }
                statusLabel.setStyle("-fx-text-fill: red;");
                secondNameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hasErrors = true;
            } else {
                secondNameField.setStyle("");
            }

            // ВАЛИДАЦИЯ специализации
            if (specialty == null || specialty.trim().isEmpty()) {
                if (hasErrors) {
                    statusLabel.setText(statusLabel.getText() + "\nВыберите специализацию!");
                } else {
                    statusLabel.setText("Выберите специализацию!");
                }
                statusLabel.setStyle("-fx-text-fill: red;");
                specialtyComboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hasErrors = true;
            } else {
                specialtyComboBox.setStyle("");
            }

            // Если есть ошибки валидации - выходим
            if (hasErrors) {
                return;
            }

            try {
                // ОБНОВЛЯЕМ ВРАЧА
                doctor.setSurname(surname);
                doctor.setName(name);
                doctor.setSecondName(secondName);
                doctor.setSpecialty(specialty);

                DoctorDAO doctorDAO = new DoctorDAO();
                if (doctorDAO.updateDoctor(doctor)) {
                    statusLabel.setText("Данные врача успешно обновлены!");
                    statusLabel.setStyle("-fx-text-fill: green;");

                    // Закрываем через 1.5 секунды
                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(event -> formStage.close());
                    pause.play();
                } else {
                    statusLabel.setText("Ошибка при обновлении данных!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (PolyclinicException ex) {
                System.err.println("Ошибка поликлиники: " + ex.getMessage());
                ex.printStackTrace();
                statusLabel.setText("Ошибка: " + ex.getErrorCode().getDescription() +
                        "\n" + ex.getAdditionalInfo());
                statusLabel.setStyle("-fx-text-fill: red;");
            } catch (Exception ex) {
                // Обработка других исключений
                System.err.println("Неизвестная ошибка: " + ex.getMessage());
                ex.printStackTrace();
                statusLabel.setText("Неизвестная ошибка: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> formStage.close());

        layout.getChildren().addAll(
                title,
                new Label("Фамилия:"), surnameField,
                new Label("Имя:"), nameField,
                new Label("Отчество:"), secondNameField,
                new Label("Специализация:"), specialtyComboBox,
                statusLabel, saveBtn, cancelBtn
        );

        Scene scene = new Scene(layout, 400, 500);
        formStage.setScene(scene);
        formStage.show();
    }

    // УДАЛЕНИЕ ВРАЧА с обновлением списка
    private void deleteDoctor(int doctorId) {
        System.out.println("Удаление врача с ID: " + doctorId);
        AppLogger.debug("Начало удаления врача с ID: " + doctorId);

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение удаления");
        confirmation.setHeaderText("Удалить врача?");
        confirmation.setContentText("Это действие нельзя отменить.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    DoctorDAO doctorDAO = new DoctorDAO();
                    if (doctorDAO.deleteDoctor(doctorId)) {
                        AppLogger.audit("Удаление врача", "Система", "Удален врач с ID: " + doctorId);
                        AppLogger.info("Врач успешно удален, ID: " + doctorId);
                        System.out.println("Врач успешно удален");
                        showInfo("Врач успешно удален");
                    } else {
                        AppLogger.warning("Не удалось удалить врача с ID: " + doctorId);
                        showError("Не удалось удалить врача");
                    }
                } catch (PolyclinicException e) {
                    AppLogger.error("Ошибка поликлиники при удалении врача ID: " + doctorId, e);
                    System.err.println("Ошибка поликлиники: " + e.getMessage());
                    e.printStackTrace();
                    showError("Ошибка удаления: " + e.getErrorCode().getDescription() +
                            "\n" + e.getAdditionalInfo());
                }
            } else {
                AppLogger.info("Удаление врача отменено пользователем, ID: " + doctorId);
            }
        });
    }

    private void testClasses() {
        System.out.println("Тестирование классов");

        Doctor doctor = new Doctor("Анна", "Петрова", "Сергеевна", 1, "Терапевт");
        Patient patient = new Patient("Иван", "Иванов", "Иванович", 123456, "+79991234567", "ул. Ленина, 1", LocalDate.of(1985, 5, 15));

        System.out.println("Врач: " + doctor);
        System.out.println("Пациент: " + patient);
        System.out.println("Все классы работают корректно!");
    }


    private void testDatabaseConnection() {
        AppLogger.info("Тестирование подключения к базе данных");
        System.out.println("Тестирование подключения к базе данных");
        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.testConnection();
            dbManager.closeConnection();
            AppLogger.info("Подключение к базе данных успешно");
        } catch (Exception e) {
            AppLogger.error("Ошибка при тесте подключения к БД", e);
            System.out.println("Ошибка при тесте БД: " + e.getMessage());
        }
    }


    // ФОРМА РЕДАКТИРОВАНИЯ ПАЦИЕНТА (с раздельными полями адреса)
    private void showEditPatientForm(Patient patient) {
        Stage formStage = new Stage();
        formStage.setTitle("Редактировать пациента");
        formStage.setMinWidth(450);
        formStage.setMinHeight(650);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 25; -fx-alignment: center; -fx-background-color: #FFE4E1;");

        Label title = new Label("Редактировать пациента: " + patient.getFullName());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Карточка формы
        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-padding: 20; -fx-background-color: white; " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2;");

        // Функция для создания Label
        java.util.function.Function<String, Label> createFormLabel = (text) -> {
            Label label = new Label(text);
            label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #555;");
            return label;
        };

        // Основные данные
        Label personalInfoLabel = new Label("Личные данные:");
        personalInfoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #9C27B0;");

        TextField surnameField = new TextField(patient.getSurname());
        surnameField.setPromptText("Иванов");
        surnameField.setStyle("-fx-pref-width: 300px;");

        TextField nameField = new TextField(patient.getName());
        nameField.setPromptText("Иван");
        nameField.setStyle("-fx-pref-width: 300px;");

        TextField secondNameField = new TextField(patient.getSecondName() != null ? patient.getSecondName() : "");
        secondNameField.setPromptText("Иванович (необязательно)");
        secondNameField.setStyle("-fx-pref-width: 300px;");

        TextField policyField = new TextField(String.valueOf(patient.getPolicy()));
        policyField.setPromptText("123456 (6 цифр)");
        policyField.setStyle("-fx-pref-width: 300px;");
        policyField.setDisable(true);

        TextField phoneField = new TextField(patient.getPhone());
        phoneField.setPromptText("+79991234567");
        phoneField.setStyle("-fx-pref-width: 300px;");


        DatePicker birthDatePicker = new DatePicker(patient.getDateOfBirth());
        birthDatePicker.setPromptText("дд.мм.гггг");
        birthDatePicker.setStyle("-fx-pref-width: 300px;");

        // Адрес - разделенные поля
        Label addressLabel = new Label("Адрес:");
        addressLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #9C27B0;");

        // Парсим существующий адрес
        String currentAddress = patient.getAddress() != null ? patient.getAddress() : "";
        String street = "";
        String house = "";
        String apartment = "";

        if (!currentAddress.isEmpty()) {
            try {
                // Разделяем адрес на компоненты
                String[] parts = currentAddress.split(", ");

                for (String part : parts) {
                    part = part.trim();
                    // Ищем улицу
                    if (part.startsWith("ул.") || part.startsWith("пр.") || part.startsWith("пер.") ||
                            part.startsWith("бульвар") || part.startsWith("аллея") || part.startsWith("шоссе")) {
                        // Убираем префикс для отображения в поле ввода
                        street = part.replaceAll("^(ул\\.|пр\\.|пер\\.|бульвар|аллея|шоссе)\\s*", "");
                    }
                    // Ищем дом
                    else if (part.startsWith("д.")) {
                        house = part.replace("д.", "").trim();
                    }
                    // Ищем квартиру
                    else if (part.startsWith("кв.")) {
                        apartment = part.replace("кв.", "").trim();
                    }
                }

                // Если не нашли стандартные префиксы, пробуем другой подход
                if (street.isEmpty() && !currentAddress.isEmpty()) {
                    // Ищем первую часть до запятой
                    String firstPart = currentAddress.split(",")[0].trim();
                    // Убираем возможные префиксы
                    street = firstPart.replaceAll("^(ул\\.|пр\\.|пер\\.|бульвар|аллея|шоссе)\\s*", "");
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга адреса: " + e.getMessage());
                street = currentAddress;
            }
        }

        // Улица
        HBox streetBox = new HBox(10);
        streetBox.setAlignment(Pos.CENTER_LEFT);
        Label streetLabel = createFormLabel.apply("Улица*:");
        TextField streetField = new TextField(street);
        streetField.setPromptText("Ленина (автоматически добавится 'ул.')");
        streetField.setStyle("-fx-pref-width: 250px;");
        streetField.setTextFormatter(ValidationUtils.createStreetFormatter());
        streetBox.getChildren().addAll(streetLabel, streetField);

        // Дом
        HBox houseBox = new HBox(10);
        houseBox.setAlignment(Pos.CENTER_LEFT);
        Label houseLabel = createFormLabel.apply("Дом*:");
        TextField houseField = new TextField(house);
        houseField.setPromptText("15");
        houseField.setStyle("-fx-pref-width: 80px;");
        houseField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9а-яА-Яa-zA-Z]*")) {
                return change;
            }
            return null;
        }));
        houseBox.getChildren().addAll(houseLabel, houseField);

        // Квартира - ОБЯЗАТЕЛЬНАЯ
        HBox apartmentBox = new HBox(10);
        apartmentBox.setAlignment(Pos.CENTER_LEFT);
        Label apartmentLabel = createFormLabel.apply("Квартира*:");
        TextField apartmentField = new TextField(apartment);
        apartmentField.setPromptText("23 (обязательно)");
        apartmentField.setStyle("-fx-pref-width: 100px;");
        apartmentField.setTextFormatter(ValidationUtils.createApartmentFormatter());
        apartmentBox.getChildren().addAll(apartmentLabel, apartmentField);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        // Валидация
        setupAutoCapitalize(surnameField);
        setupAutoCapitalize(nameField);
        setupAutoCapitalize(secondNameField);
        setupAutoCapitalize(streetField);

        surnameField.setTextFormatter(ValidationUtils.createNameFormatter());
        nameField.setTextFormatter(ValidationUtils.createNameFormatter());
        phoneField.setTextFormatter(ValidationUtils.createPhoneFormatter());

        // Автоформатирование улицы при потере фокуса
        streetField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !streetField.getText().trim().isEmpty()) {
                String formatted = ValidationUtils.autoFormatStreet(streetField.getText());
                if (!formatted.equals(streetField.getText())) {
                    int caretPosition = streetField.getCaretPosition();
                    streetField.setText(formatted);
                    Platform.runLater(() -> {
                        streetField.positionCaret(Math.min(caretPosition, formatted.length()));
                    });
                }
            }
        });

        // Кнопки
        Button saveBtn = new Button("Сохранить изменения");
        saveBtn.setStyle("-fx-font-size: 14px; -fx-padding: 12 25; " +
                "-fx-background-color: #98FB98; -fx-text-fill: #333; " +
                "-fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: #7CFC00; -fx-border-width: 2; " +
                "-fx-cursor: hand;");

        Button cancelBtn = createCancelButton(formStage);

        HBox buttonBox = new HBox(15, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // Обработчик сохранения
        saveBtn.setOnAction(e -> {
            String surname = surnameField.getText().trim();
            String name = nameField.getText().trim();
            String secondName = secondNameField.getText().trim();
            String phone = phoneField.getText().trim();
            LocalDate birthDate = birthDatePicker.getValue();
            String streetInput = streetField.getText().trim();
            String houseInput = houseField.getText().trim();
            String apartmentInput = apartmentField.getText().trim();

            // Сбрасываем стили ошибок
            surnameField.setStyle("");
            nameField.setStyle("");
            phoneField.setStyle("");
            birthDatePicker.setStyle("");
            streetField.setStyle("");
            houseField.setStyle("");
            apartmentField.setStyle("");

            // ВАЛИДАЦИЯ ПОЛЕЙ
            InputValidator.ValidationResult secondNameValidation = InputValidator.validatePatronymic(secondName);
            InputValidator.ValidationResult surnameValidation = InputValidator.validateName(surname, "Фамилия");
            InputValidator.ValidationResult nameValidation = InputValidator.validateName(name, "Имя");
            InputValidator.ValidationResult phoneValidation = InputValidator.validatePhone(phone);
            InputValidator.ValidationResult streetValidation = ValidationUtils.validateStreet(streetInput);
            InputValidator.ValidationResult houseValidation = ValidationUtils.validateHouse(houseInput);
            InputValidator.ValidationResult apartmentValidation = ValidationUtils.validateApartment(apartmentInput);

            // Подсветка ошибок
            ValidationUtils.highlightInvalidField(secondNameField, secondNameValidation.isValid());
            ValidationUtils.highlightInvalidField(surnameField, surnameValidation.isValid());
            ValidationUtils.highlightInvalidField(nameField, nameValidation.isValid());
            ValidationUtils.highlightInvalidField(phoneField, phoneValidation.isValid());
            ValidationUtils.highlightInvalidField(streetField, streetValidation.isValid());
            ValidationUtils.highlightInvalidField(houseField, houseValidation.isValid());
            ValidationUtils.highlightInvalidField(apartmentField, apartmentValidation.isValid());

            // Проверка даты рождения
            if (birthDate == null) {
                statusLabel.setText("Укажите дату рождения!");
                statusLabel.setStyle("-fx-text-fill: red;");
                birthDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            } else if (birthDate.isAfter(LocalDate.now())) {
                statusLabel.setText("Дата рождения не может быть в будущем!");
                statusLabel.setStyle("-fx-text-fill: red;");
                birthDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            } else if (birthDate.isBefore(LocalDate.now().minusYears(150))) {
                statusLabel.setText("Дата рождения слишком старая! Максимум 150 лет назад.");
                statusLabel.setStyle("-fx-text-fill: red;");
                birthDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                return;
            } else {
                birthDatePicker.setStyle("");
            }

            // Собираем все ошибки
            StringBuilder errors = new StringBuilder();

            // Проверка обязательных полей
            if (!surnameValidation.isValid())
                errors.append(surnameValidation.getMessage()).append("\n");

            if (!nameValidation.isValid())
                errors.append(nameValidation.getMessage()).append("\n");

            if (!phoneValidation.isValid()) {
                errors.append(phoneValidation.getMessage()).append("\n");
            } else {
                // Проверка уникальности телефона ТОЛЬКО если телефон изменился
                if (!phone.equals(patient.getPhone())) {
                    try {
                        PatientDAO patientDAO = new PatientDAO();
                        if (patientDAO.phoneNumberExists(phone, patient.getPolicy())) {
                            errors.append("Этот номер телефона уже зарегистрирован у другого пациента!\n");
                            phoneField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                        }
                    } catch (PolyclinicException ex) {
                        System.err.println("Ошибка проверки телефона: " + ex.getMessage());
                        // Если не удалось проверить, продолжаем
                        errors.append("Не удалось проверить уникальность телефона. Пожалуйста, попробуйте еще раз.\n");
                    }
                }
            }
            if (!secondNameValidation.isValid()) {
                errors.append("Отчество: ").append(secondNameValidation.getMessage()).append("\n");
            }

            if (!streetValidation.isValid())
                errors.append("Улица: ").append(streetValidation.getMessage()).append("\n");

            if (!houseValidation.isValid())
                errors.append("Дом: ").append(houseValidation.getMessage()).append("\n");

            // Квартира обязательна
            if (apartmentInput.isEmpty()) {
                errors.append("Квартира: Укажите номер квартиры!\n");
            } else if (!apartmentValidation.isValid()) {
                errors.append("Квартира: ").append(apartmentValidation.getMessage()).append("\n");
            }

            if (errors.length() > 0) {
                statusLabel.setText(errors.toString().trim());
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            String address = "";
            if (!streetInput.isEmpty() && !houseInput.isEmpty() && !apartmentInput.isEmpty()) {
                // Автоматически форматируем улицу
                String formattedStreet = ValidationUtils.autoFormatStreet(streetInput);

                // Просто собираем адрес из отформатированных компонентов
                address = formattedStreet + ", д. " + houseInput + ", кв. " + apartmentInput;
            }

            // Обновляем данные пациента
            try {
                patient.setSurname(surname);
                patient.setName(name);
                patient.setSecondName(secondName.isEmpty() ? null : secondName);
                patient.setPhone(phone);
                patient.setDateOfBirth(birthDate);
                patient.setAddress(address);

                PatientDAO patientDAO = new PatientDAO();
                if (patientDAO.updatePatient(patient)) {
                    statusLabel.setText("Данные пациента успешно обновлены!");
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(event -> formStage.close());
                    pause.play();

                } else {
                    statusLabel.setText("Ошибка при обновлении данных пациента!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (PolyclinicException ex) {
                System.err.println("Ошибка поликлиники: " + ex.getMessage());
                ex.printStackTrace();
                statusLabel.setText("Ошибка: " + ex.getErrorCode().getDescription() +
                        "\n" + ex.getAdditionalInfo());
                statusLabel.setStyle("-fx-text-fill: red;");
            } catch (Exception ex) {
                System.err.println("Неизвестная ошибка: " + ex.getMessage());
                ex.printStackTrace();
                statusLabel.setText("Неизвестная ошибка: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });
        cancelBtn.setOnAction(e -> formStage.close());

        // Собираем форму
        formCard.getChildren().addAll(
                personalInfoLabel,
                createFormLabel.apply("Фамилия*:"), surnameField,
                createFormLabel.apply("Имя*:"), nameField,
                createFormLabel.apply("Отчество:"), secondNameField,
                createFormLabel.apply("Номер полиса (нельзя изменить):"), policyField,
                createFormLabel.apply("Телефон*:"), phoneField,
                createFormLabel.apply("Дата рождения*:"), birthDatePicker,
                addressLabel,
                streetBox, houseBox, apartmentBox,
                statusLabel,
                buttonBox
        );





        layout.getChildren().addAll(title, formCard);

        Scene scene = new Scene(layout, 450, 700);
        formStage.setScene(scene);
        formStage.show();
    }


    // Метод для показа информационного сообщения
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Показать расписание врача
    private void showDoctorSchedule(int doctorId) {
        Stage scheduleStage = new Stage();
        scheduleStage.setTitle("Расписание врача");
        scheduleStage.setMinWidth(500);
        scheduleStage.setMinHeight(500);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Получаем информацию о враче С ОБРАБОТКОЙ ИСКЛЮЧЕНИЙ
        try {
            DoctorDAO doctorDAO = new DoctorDAO();
            Doctor doctor = doctorDAO.getDoctorById(doctorId);

            if (doctor == null) {
                showError("Врач не найден!");
                return;
            }

            Label title = new Label("Расписание врача: " + doctor.getFullName());
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Label specialtyLabel = new Label("Специализация: " + doctor.getSpecialty());
            specialtyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

            // Контейнер для расписания
            VBox scheduleContainer = new VBox(10);
            scheduleContainer.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 5px;");

            // Метод для загрузки расписания
            Runnable loadSchedule = new Runnable() {
                @Override
                public void run() {
                    try {
                        loadAndDisplaySchedule(doctorId, scheduleContainer);
                    } catch (Exception e) {
                        Label errorLabel = new Label("Ошибка загрузки расписания: " + e.getMessage());
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                        scheduleContainer.getChildren().add(errorLabel);
                    }
                }
            };

            // Кнопки управления
            // Кнопка добавления нового расписания - МЯТНАЯ
            Button addScheduleBtn = new Button("Добавить время приёма");
            addScheduleBtn.setStyle("-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-color: #98FB98; " +
                    "-fx-text-fill: #333; -fx-background-radius: 8; -fx-border-radius: 8;");

            // Кнопка обновления - МЯТНАЯ
            Button refreshBtn = new Button("Обновить");
            refreshBtn.setStyle("-fx-font-size: 12px; -fx-padding: 8 15; -fx-background-color: #98FB98; " +
                    "-fx-text-fill: #333; -fx-background-radius: 8; -fx-border-radius: 8;");

            // Кнопка закрытия - РОЗОВАЯ
            Button closeBtn = createCloseButton(scheduleStage);

            // НАЗНАЧАЕМ ОБРАБОТЧИКИ ПОСЛЕ СОЗДАНИЯ loadSchedule
            addScheduleBtn.setOnAction(e -> {
                try {
                    showAddScheduleForm(doctorId, scheduleContainer);
                    // Обновляем через 1 секунду
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(loadSchedule);
                                }
                            },
                            1000
                    );
                } catch (Exception ex) {
                    showError("Ошибка открытия формы: " + ex.getMessage());
                }
            });

            refreshBtn.setOnAction(e -> {
                try {
                    loadSchedule.run();
                } catch (Exception ex) {
                    showError("Ошибка обновления: " + ex.getMessage());
                }
            });
            closeBtn.setOnAction(e -> scheduleStage.hide());

            // Первоначальная загрузка
            loadSchedule.run();

            // Панель кнопок
            HBox buttonPanel = new HBox(10, addScheduleBtn, refreshBtn, closeBtn);
            buttonPanel.setAlignment(Pos.CENTER);

            layout.getChildren().addAll(title, specialtyLabel, scheduleContainer, buttonPanel);

            Scene scene = new Scene(layout, 500, 500);
            scheduleStage.setScene(scene);
            scheduleStage.show();

        } catch (PolyclinicException ex) {
            // Обработка исключения при получении врача
            System.err.println("Ошибка поликлиники: " + ex.getMessage());
            ex.printStackTrace();
            showError("Ошибка: " + ex.getErrorCode().getDescription() +
                    "\n" + ex.getAdditionalInfo());
            scheduleStage.close();
            return;
        } catch (Exception ex) {
            System.err.println("Неизвестная ошибка: " + ex.getMessage());
            ex.printStackTrace();
            showError("Неизвестная ошибка: " + ex.getMessage());
            scheduleStage.close();
            return;
        }
    }


    //Загрузка и отображение расписания
    private void loadAndDisplaySchedule(int doctorId, VBox scheduleContainer) {
        scheduleContainer.getChildren().clear();

        // Получаем расписание с обработкой исключения
        List<Schedule> schedules;
        try {
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            schedules = scheduleDAO.getScheduleByDoctor(doctorId);
        } catch (PolyclinicException ex) {
            System.err.println("Ошибка при загрузке расписания: " + ex.getMessage());
            Label errorLabel = new Label("Ошибка загрузки расписания: " + ex.getErrorCode().getDescription());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            scheduleContainer.getChildren().add(errorLabel);
            return;
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка при загрузке расписания: " + e.getMessage());
            Label errorLabel = new Label("Ошибка загрузки расписания: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            scheduleContainer.getChildren().add(errorLabel);
            return;
        }

        // Если расписание пустое
        if (schedules.isEmpty()) {
            Label noScheduleLabel = new Label("У врача нет установленного расписания");
            noScheduleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray; -fx-padding: 10px;");
            scheduleContainer.getChildren().add(noScheduleLabel);
            return;
        }

        // Группируем по дням недели
        for (int day = 1; day <= 7; day++) {
            final int currentDay = day;
            List<Schedule> daySchedules = schedules.stream()
                    .filter(s -> s.getDayOfWeek().getValue() == currentDay)
                    .sorted(Comparator.comparing(Schedule::getStartTime))
                    .collect(Collectors.toList());

            if (!daySchedules.isEmpty()) {
                Label dayLabel = new Label(getDayName(currentDay));
                dayLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
                scheduleContainer.getChildren().add(dayLabel);

                for (Schedule schedule : daySchedules) {
                    HBox scheduleRow = new HBox(10);
                    scheduleRow.setAlignment(Pos.CENTER_LEFT);
                    scheduleRow.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 1px;");

                    Label timeLabel = new Label(String.format("%s - %s",
                            schedule.getStartTime().toString(),
                            schedule.getEndTime().toString()));
                    timeLabel.setStyle("-fx-font-size: 12px; -fx-min-width: 120px;");

                    Label roomLabel = new Label("Кабинет " + schedule.getRoomNumber());
                    roomLabel.setStyle("-fx-font-size: 12px; -fx-min-width: 80px;");

                    // КНОПКА РЕДАКТИРОВАНИЯ
                    Button editBtn = new Button("Редактировать");
                    editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 8; -fx-background-color: #98FB98; " +
                            "-fx-text-fill: #333; -fx-background-radius: 5; -fx-border-radius: 5; " +
                            "-fx-border-color: #7CFC00; -fx-border-width: 1;");
                    editBtn.setTooltip(new Tooltip("Редактировать время приёма"));

                    // КНОПКА УДАЛЕНИЯ
                    Button deleteBtn = new Button("Удалить");
                    deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 8; -fx-background-color: #FFB6C1; " +
                            "-fx-text-fill: #333; -fx-background-radius: 5; -fx-border-radius: 5; " +
                            "-fx-border-color: #FF69B4; -fx-border-width: 1;");
                    deleteBtn.setTooltip(new Tooltip("Удалить время приёма"));

                    // Сохраняем ID расписания
                    final int scheduleId = schedule.getId();

                    // ОБРАБОТЧИК ДЛЯ РЕДАКТИРОВАНИЯ
                    editBtn.setOnAction(event -> {
                        try {
                            ScheduleDAO editDAO = new ScheduleDAO();
                            Schedule currentSchedule = editDAO.getScheduleById(scheduleId);

                            if (currentSchedule != null) {
                                showEditScheduleForm(currentSchedule, doctorId, scheduleContainer);
                            } else {
                                showError("Расписание не найдено!");
                            }
                        } catch (PolyclinicException ex) {
                            System.err.println("Ошибка при получении расписания для редактирования: " + ex.getMessage());
                            showError("Ошибка загрузки расписания: " + ex.getErrorCode().getDescription());
                        } catch (Exception ex) {
                            System.err.println("Неизвестная ошибка при редактировании: " + ex.getMessage());
                            showError("Ошибка при открытии формы редактирования");
                        }
                    });

                    // ОБРАБОТЧИК ДЛЯ УДАЛЕНИЯ
                    deleteBtn.setOnAction(event -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Подтверждение удаления");
                        confirm.setHeaderText("Удалить время приёма?");
                        confirm.setContentText(String.format("%s: %s - %s (каб. %d)",
                                getDayName(schedule.getDayOfWeek().getValue()),
                                schedule.getStartTime(),
                                schedule.getEndTime(),
                                schedule.getRoomNumber()));

                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    ScheduleDAO deleteDAO = new ScheduleDAO();
                                    if (deleteDAO.deleteSchedule(scheduleId)) {
                                        // Перезагружаем расписание
                                        loadAndDisplaySchedule(doctorId, scheduleContainer);
                                        showInfo("Время приёма успешно удалено");
                                    }
                                } catch (PolyclinicException ex) {
                                    System.err.println("Ошибка при удалении расписания: " + ex.getMessage());
                                    ex.printStackTrace();
                                    showError("Ошибка удаления: " + ex.getErrorCode().getDescription() +
                                            "\n" + ex.getAdditionalInfo());
                                } catch (Exception ex) {
                                    System.err.println("Неизвестная ошибка при удалении: " + ex.getMessage());
                                    ex.printStackTrace();
                                    showError("Не удалось удалить время приёма: " + ex.getMessage());
                                }
                            }
                        });
                    });

                    scheduleRow.getChildren().addAll(timeLabel, roomLabel, editBtn, deleteBtn);
                    scheduleContainer.getChildren().add(scheduleRow);
                }

                // Пустая строка между днями
                Region spacer = new Region();
                spacer.setPrefHeight(5);
                scheduleContainer.getChildren().add(spacer);
            }
        }
    }

    // Получить название дня недели
    private String getDayName(int dayNumber) {
        switch (dayNumber) {
            case 1:
                return "Понедельник";
            case 2:
                return "Вторник";
            case 3:
                return "Среда";
            case 4:
                return "Четверг";
            case 5:
                return "Пятница";
            case 6:
                return "Суббота";
            case 7:
                return "Воскресенье";
            default:
                return "Неизвестный день";
        }
    }

    // Метод загрузки расписания
    private void loadDoctorSchedule(int doctorId, VBox container) {
        container.getChildren().clear();

        try {
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            List<Schedule> schedules = scheduleDAO.getScheduleByDoctor(doctorId);

            if (schedules.isEmpty()) {
                Label noScheduleLabel = new Label("У врача нет установленного расписания");
                noScheduleLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
                container.getChildren().add(noScheduleLabel);
                return;
            }

            for (Schedule schedule : schedules) {
                HBox scheduleRow = new HBox(10);
                scheduleRow.setAlignment(Pos.CENTER_LEFT);

                Label scheduleLabel = new Label(schedule.toString());
                scheduleLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 300px;");

                Button deleteBtn = new Button("Удалить");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    try {
                        scheduleDAO.deleteSchedule(schedule.getId());
                        loadDoctorSchedule(doctorId, container); // Перезагружаем список
                    } catch (PolyclinicException ex) {
                        System.err.println("Ошибка при удалении расписания: " + ex.getMessage());
                        showError("Ошибка удаления: " + ex.getErrorCode().getDescription());
                    }
                });

                scheduleRow.getChildren().addAll(scheduleLabel, deleteBtn);
                container.getChildren().add(scheduleRow);
            }

        } catch (PolyclinicException ex) {
            System.err.println("Ошибка при загрузке расписания: " + ex.getMessage());
            Label errorLabel = new Label("Ошибка загрузки: " + ex.getErrorCode().getDescription());
            errorLabel.setStyle("-fx-text-fill: red;");
            container.getChildren().add(errorLabel);
        } catch (Exception e) {
            Label errorLabel = new Label("Ошибка загрузки: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            container.getChildren().add(errorLabel);
        }
    }

    // Форма добавления расписания
    private void showAddScheduleForm(int doctorId, VBox scheduleContainer) {
        Stage formStage = new Stage();
        formStage.setTitle("Добавить время приёма");
        formStage.setMinWidth(400);
        formStage.setMinHeight(500);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Добавить новое время приёма");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // День недели
        ComboBox<String> dayComboBox = new ComboBox<>();
        dayComboBox.setPromptText("День недели");
        dayComboBox.getItems().addAll(
                "Понедельник", "Вторник", "Среда", "Четверг",
                "Пятница", "Суббота", "Воскресенье"
        );

        // Время начала
        ComboBox<String> startTimeComboBox = new ComboBox<>();
        startTimeComboBox.setPromptText("Начало приёма");
        for (int hour = 8; hour <= 18; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                startTimeComboBox.getItems().add(time);
            }
        }

        // Время окончания
        ComboBox<String> endTimeComboBox = new ComboBox<>();
        endTimeComboBox.setPromptText("Окончание приёма");
        for (int hour = 9; hour <= 20; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                endTimeComboBox.getItems().add(time);
            }
        }

        // Номер кабинета с ограничением от 1 до 100
        TextField roomField = new TextField();
        roomField.setPromptText("Номер кабинета (1-100)");
        // Ограничиваем ввод только цифрами и проверяем диапазон
        roomField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // Только цифры
                if (!newText.isEmpty()) {
                    try {
                        int room = Integer.parseInt(newText);
                        if (room < 1 || room > 100) {
                            roomField.setStyle("-fx-border-color: orange; -fx-border-width: 2px;");
                        } else {
                            roomField.setStyle("");
                        }
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return change;
            }
            return null;
        }));
        roomField.setMaxWidth(200);
        roomField.setPrefWidth(150);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        Button saveBtn = new Button("Сохранить");
        saveBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #98FB98; " +
                "-fx-text-fill: #333; -fx-background-radius: 10; -fx-border-radius: 10;");

        Button cancelBtn = new Button("Отмена");
        cancelBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #FFB6C1; " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10;");

        saveBtn.setOnAction(e -> {
            String dayStr = dayComboBox.getValue();
            String startTimeStr = startTimeComboBox.getValue();
            String endTimeStr = endTimeComboBox.getValue();
            String roomStr = roomField.getText().trim();

            // Валидация
            if (dayStr == null || startTimeStr == null || endTimeStr == null || roomStr.isEmpty()) {
                statusLabel.setText("Заполните все поля!");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            try {
                // Конвертация дня недели
                int dayNumber = getDayNumber(dayStr);
                LocalTime startTime = LocalTime.parse(startTimeStr);
                LocalTime endTime = LocalTime.parse(endTimeStr);
                int roomNumber = Integer.parseInt(roomStr);

                // 1. Проверка диапазона кабинета
                if (roomNumber < 1 || roomNumber > 100) {
                    statusLabel.setText("Номер кабинета должен быть от 1 до 100!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    roomField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    return;
                }

                // 2. Проверка времени
                if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                    statusLabel.setText("Время окончания должно быть позже начала!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

                // 3. Проверка конфликта у этого же врача
                ScheduleDAO scheduleDAO = new ScheduleDAO();
                boolean hasConflict = scheduleDAO.hasScheduleConflict(doctorId, DayOfWeek.of(dayNumber), startTime, endTime);

                if (hasConflict) {
                    statusLabel.setText("У врача уже есть приём в это время!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

                // 4. Проверка занятости кабинета другим врачом
                boolean isOccupied = scheduleDAO.isRoomOccupied(roomNumber, DayOfWeek.of(dayNumber), startTime, endTime);

                if (isOccupied) {
                    statusLabel.setText("Кабинет " + roomNumber + " занят другим врачом в это время!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    roomField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    return;
                }

                // Создание и сохранение расписания
                Schedule schedule = new Schedule(doctorId, DayOfWeek.of(dayNumber), startTime, endTime, roomNumber);

                if (scheduleDAO.addSchedule(schedule)) {
                    statusLabel.setText("Расписание успешно добавлено! ID: " + schedule.getId());
                    statusLabel.setStyle("-fx-text-fill: green;");

                    // Закрываем окно и обновляем список
                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(event -> {
                        formStage.close();
                        loadAndDisplaySchedule(doctorId, scheduleContainer);
                    });
                    pause.play();
                } else {
                    statusLabel.setText("Ошибка при добавлении расписания!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }

            } catch (PolyclinicException ex) {
                // Обработка нашего кастомного исключения
                System.err.println("Ошибка поликлиники: " + ex.getMessage());
                ex.printStackTrace();

                // Определяем, какое сообщение показать пользователю
                String userMessage;
                switch (ex.getErrorCode()) {
                    case SCHEDULE_CONFLICT:
                        userMessage = "Конфликт расписания: " + ex.getAdditionalInfo();
                        break;
                    case DATABASE_ERROR:
                        userMessage = "Ошибка базы данных: " + ex.getAdditionalInfo();
                        break;
                    default:
                        userMessage = "Ошибка: " + ex.getErrorCode().getDescription();
                }

                statusLabel.setText(userMessage);
                statusLabel.setStyle("-fx-text-fill: red;");

            } catch (NumberFormatException ex) {
                statusLabel.setText("Номер кабинета должен быть числом от 1 до 100!");
                statusLabel.setStyle("-fx-text-fill: red;");
                roomField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");

            } catch (Exception ex) {
                System.err.println("Исключение при добавлении расписания: " + ex.getMessage());
                ex.printStackTrace();
                statusLabel.setText("Ошибка в данных: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> formStage.close());

        layout.getChildren().addAll(
                title,
                new Label("День недели:"), dayComboBox,
                new Label("Время начала:"), startTimeComboBox,
                new Label("Время окончания:"), endTimeComboBox,
                new Label("Номер кабинета (1-100):"), roomField,
                statusLabel,
                new HBox(10, saveBtn, cancelBtn)
        );

        Scene scene = new Scene(layout, 400, 450);
        formStage.setScene(scene);
        formStage.show();
    }

    // Вспомогательный метод для конвертации дня недели
    private int getDayNumber(String dayName) {
        switch (dayName) {
            case "Понедельник":
                return 1;
            case "Вторник":
                return 2;
            case "Среда":
                return 3;
            case "Четверг":
                return 4;
            case "Пятница":
                return 5;
            case "Суббота":
                return 6;
            case "Воскресенье":
                return 7;
            default:
                return 1;
        }
    }

    // Метод для создания окон с правильными настройками
    private Stage createChildWindow(String title, double minWidth, double minHeight) {
        Stage stage = new Stage();
        stage.setTitle(title);

        stage.initModality(Modality.APPLICATION_MODAL); // Модальное окно
        stage.initStyle(StageStyle.UTILITY); // Простой стиль

        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.setOnCloseRequest(e -> {
            e.consume(); // Отменяем стандартное закрытие
            stage.hide(); // Скрываем окно
        });

        return stage;
    }

    // Форма редактирования расписания
    private void showEditScheduleForm(Schedule schedule, int doctorId, VBox scheduleContainer) {
        Stage formStage = new Stage();
        formStage.setTitle("Редактировать время приёма");
        formStage.setMinWidth(400);
        formStage.setMinHeight(500);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Редактировать время приёма");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Создаем экземпляр ScheduleDAO здесь
        ScheduleDAO scheduleDAO = new ScheduleDAO();

        // День недели (предзаполненный)
        ComboBox<String> dayComboBox = new ComboBox<>();
        dayComboBox.setPromptText("День недели");
        dayComboBox.getItems().addAll(
                "Понедельник", "Вторник", "Среда", "Четверг",
                "Пятница", "Суббота", "Воскресенье"
        );
        // Устанавливаем текущий день
        dayComboBox.setValue(getDayName(schedule.getDayOfWeek().getValue()));

        // Время начала (предзаполненное)
        ComboBox<String> startTimeComboBox = new ComboBox<>();
        startTimeComboBox.setPromptText("Начало приёма");
        for (int hour = 8; hour <= 18; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                startTimeComboBox.getItems().add(time);
            }
        }
        startTimeComboBox.setValue(schedule.getStartTime().toString());

        // Время окончания (предзаполненное)
        ComboBox<String> endTimeComboBox = new ComboBox<>();
        endTimeComboBox.setPromptText("Окончание приёма");
        for (int hour = 9; hour <= 20; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                endTimeComboBox.getItems().add(time);
            }
        }
        endTimeComboBox.setValue(schedule.getEndTime().toString());

        // Номер кабинета с ограничением от 1 до 100 (предзаполненный)
        TextField roomField = new TextField(String.valueOf(schedule.getRoomNumber()));
        roomField.setPromptText("Номер кабинета (1-100)");
        // Ограничиваем ввод только цифрами и проверяем диапазон
        roomField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // Только цифры
                if (!newText.isEmpty()) {
                    try {
                        int room = Integer.parseInt(newText);
                        if (room < 1 || room > 100) {
                            roomField.setStyle("-fx-border-color: orange; -fx-border-width: 2px;");
                        } else {
                            roomField.setStyle("");
                        }
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return change;
            }
            return null;
        }));
        roomField.setMaxWidth(200);
        roomField.setPrefWidth(150);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        Button saveBtn = new Button("Сохранить изменения");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button cancelBtn = new Button("Отмена");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        saveBtn.setOnAction(e -> {
            String dayStr = dayComboBox.getValue();
            String startTimeStr = startTimeComboBox.getValue();
            String endTimeStr = endTimeComboBox.getValue();
            String roomStr = roomField.getText().trim();

            // Валидация
            if (dayStr == null || startTimeStr == null || endTimeStr == null || roomStr.isEmpty()) {
                statusLabel.setText("Заполните все поля!");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            try {
                // Конвертация дня недели
                int dayNumber = getDayNumber(dayStr);
                LocalTime startTime = LocalTime.parse(startTimeStr);
                LocalTime endTime = LocalTime.parse(endTimeStr);
                int roomNumber = Integer.parseInt(roomStr);

                // Отладочный вывод
                System.out.println("\n=== Редактирование расписания ===");
                System.out.println("ID расписания: " + schedule.getId());
                System.out.println("Кабинет: " + roomNumber);
                System.out.println("День: " + dayNumber + " (" + dayStr + ")");
                System.out.println("Время: " + startTime + " - " + endTime);

                // 1. Проверка диапазона кабинета
                if (roomNumber < 1 || roomNumber > 100) {
                    statusLabel.setText("Номер кабинета должен быть от 1 до 100!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    roomField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    return;
                }

                // 2. Проверка времени
                if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                    statusLabel.setText("Время окончания должно быть позже начала!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

                // 3. Проверка конфликта у этого же врача (исключая текущее расписание)
                boolean hasConflict = scheduleDAO.hasScheduleConflict(doctorId, DayOfWeek.of(dayNumber),
                        startTime, endTime, schedule.getId());

                System.out.println("Конфликт у врача (исключая ID " + schedule.getId() + ")? " + hasConflict);

                if (hasConflict) {
                    statusLabel.setText("У врача уже есть другое расписание в это время!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

                // 4. Проверка занятости кабинета другим врачом (исключая текущее расписание)
                System.out.println("Проверка занятости кабинета " + roomNumber + "...");
                boolean isOccupied = scheduleDAO.isRoomOccupied(roomNumber, DayOfWeek.of(dayNumber),
                        startTime, endTime, schedule.getId());
                System.out.println("Кабинет занят другим врачом? " + isOccupied);

                if (isOccupied) {
                    statusLabel.setText("Кабинет " + roomNumber + " занят другим врачом в это время!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    roomField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    return;
                }

                schedule.setDayOfWeek(DayOfWeek.of(dayNumber));
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                schedule.setRoomNumber(roomNumber);

                if (scheduleDAO.updateSchedule(schedule)) {
                    System.out.println("Расписание успешно обновлено!");
                    statusLabel.setText("Расписание успешно обновлено!");
                    statusLabel.setStyle("-fx-text-fill: green;");

                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(event -> {
                        formStage.close();
                        loadDoctorSchedule(doctorId, scheduleContainer);
                    });
                    pause.play();
                } else {
                    System.out.println("Ошибка обновления расписания в БД!");
                    statusLabel.setText("Ошибка при обновлении расписания!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }

            } catch (Exception ex) {
                System.out.println("Исключение при редактировании расписания: " + ex.getMessage());
                ex.printStackTrace();
                statusLabel.setText("Ошибка в данных: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> formStage.close());

        layout.getChildren().addAll(
                title,
                new Label("День недели:"), dayComboBox,
                new Label("Время начала:"), startTimeComboBox,
                new Label("Время окончания:"), endTimeComboBox,
                new Label("Номер кабинета (1-100):"), roomField,
                statusLabel,
                new HBox(10, saveBtn, cancelBtn)
        );

        Scene scene = new Scene(layout, 400, 450);
        formStage.setScene(scene);
        formStage.show();
    }

    //метод для проверки конфликта с исключением
    private boolean hasScheduleConflictExcluding(int doctorId, int excludeScheduleId,
                                                 DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        try {
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            List<Schedule> allSchedules = scheduleDAO.getScheduleByDoctor(doctorId);

            // Проверяем конфликты, исключая текущее расписание
            for (Schedule s : allSchedules) {
                if (s.getId() == excludeScheduleId) {
                    continue;
                }

                if (s.getDayOfWeek() == dayOfWeek) {
                    boolean timeOverlap = !(endTime.isBefore(s.getStartTime()) ||
                            startTime.isAfter(s.getEndTime()));

                    if (timeOverlap) {
                        System.out.println("Конфликт с расписанием ID " + s.getId() +
                                ": " + s.getStartTime() + "-" + s.getEndTime());
                        return true;
                    }
                }
            }

            return false;

        } catch (PolyclinicException ex) {
            System.err.println("Ошибка при проверке конфликта расписания: " + ex.getMessage());
            return true;
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка при проверке конфликта: " + e.getMessage());
            return true;
        }
    }

    private void showAllAppointments() {
        Stage appointmentsStage = new Stage();
        appointmentsStage.setTitle("Все записи на приём");
        appointmentsStage.setMinWidth(900);
        appointmentsStage.setMinHeight(600);

        VBox mainLayout = new VBox(15);
        mainLayout.setStyle("-fx-padding: 20; -fx-background-color: #FFE4E1;");

        Label title = new Label("Все записи на приём");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Фильтры - делаем их final
        HBox filterBox = new HBox(15);
        filterBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10;");
        filterBox.setAlignment(Pos.CENTER_LEFT);

        final DatePicker dateFilter = new DatePicker(); // ДОБАВЛЯЕМ final
        dateFilter.setPromptText("Фильтр по дате");
        dateFilter.setPrefWidth(150);

        final ComboBox<Doctor> doctorFilter = new ComboBox<>(); // ДОБАВЛЯЕМ final
        doctorFilter.setPromptText("Фильтр по врачу");
        doctorFilter.setPrefWidth(200);

        final ComboBox<Patient> patientFilter = new ComboBox<>(); // ДОБАВЛЯЕМ final
        patientFilter.setPromptText("Фильтр по пациенту");
        patientFilter.setPrefWidth(200);

        // Загружаем данные в комбобоксы
        loadDoctorsForComboBox(doctorFilter);
        loadPatientsForComboBox(patientFilter);

        Button applyFilterBtn = new Button("Применить");
        applyFilterBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 15; -fx-background-color: #98FB98; " +
                "-fx-text-fill: #333; -fx-background-radius: 5; -fx-border-radius: 5;");

        Button clearFilterBtn = new Button("Сбросить");
        clearFilterBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5 15; -fx-background-color: #FFB6C1; " +
                "-fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5;");

        filterBox.getChildren().addAll(
                new Label("Фильтры:"), dateFilter, doctorFilter, patientFilter, applyFilterBtn, clearFilterBtn
        );

        // Таблица записей
        final TableView<Appointment> tableView = new TableView<>(); // ДОБАВЛЯЕМ final
        tableView.setStyle("-fx-background-color: white; -fx-table-cell-border-color: transparent;");

        // Колонки таблицы
        TableColumn<Appointment, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        idColumn.setPrefWidth(50);

        TableColumn<Appointment, String> doctorColumn = new TableColumn<>("Врач");
        doctorColumn.setCellValueFactory(cellData -> {
            try {
                DoctorDAO doctorDAO = new DoctorDAO();
                Doctor doctor = doctorDAO.getDoctorById(cellData.getValue().getDoctorId());
                return new javafx.beans.property.SimpleStringProperty(doctor != null ? doctor.getFullName() : "Неизвестно");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Ошибка");
            }
        });
        doctorColumn.setPrefWidth(150);

        TableColumn<Appointment, String> patientColumn = new TableColumn<>("Пациент");
        patientColumn.setCellValueFactory(cellData -> {
            try {
                PatientDAO patientDAO = new PatientDAO();
                Patient patient = patientDAO.getPatientByPolicy(cellData.getValue().getPatientPolicy());
                return new javafx.beans.property.SimpleStringProperty(patient != null ? patient.getFullName() : "Неизвестно");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Ошибка");
            }
        });
        patientColumn.setPrefWidth(150);

        TableColumn<Appointment, String> dateColumn = new TableColumn<>("Дата и время");
        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getAppointmentDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return new javafx.beans.property.SimpleStringProperty(dateTime.format(formatter));
        });
        dateColumn.setPrefWidth(150);

        TableColumn<Appointment, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(cellData -> {
            Appointment app = cellData.getValue();
            if (app.getAppointmentDateTime().isBefore(java.time.LocalDateTime.now())) {
                return new javafx.beans.property.SimpleStringProperty("Завершён");
            } else {
                return new javafx.beans.property.SimpleStringProperty("Предстоящий");
            }
        });
        statusColumn.setPrefWidth(100);

        // Колонка действий
        TableColumn<Appointment, Void> actionsColumn = new TableColumn<>("Действия");
        actionsColumn.setPrefWidth(200);
        actionsColumn.setCellFactory(param -> new TableCell<Appointment, Void>() {
            private final Button cancelBtn = new Button("Отменить");
            private final Button completeBtn = new Button("Завершить");
            private final Button deleteBtn = new Button("Удалить");
            private final HBox buttons = new HBox(5);

            {
                // Стили кнопок
                cancelBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 8; -fx-background-color: #FFB6C1; " +
                        "-fx-text-fill: #333; -fx-background-radius: 3;");
                completeBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 8; -fx-background-color: #98FB98; " +
                        "-fx-text-fill: #333; -fx-background-radius: 3;");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 8; -fx-background-color: #FF4500; " +
                        "-fx-text-fill: white; -fx-background-radius: 3;");

                buttons.getChildren().clear();
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Appointment appointment = getTableView().getItems().get(getIndex());

                    // Удаляем старые кнопки
                    buttons.getChildren().clear();

                    // Определяем, является ли запись предстоящей (в будущем)
                    boolean isFuture = appointment.getAppointmentDateTime().isAfter(java.time.LocalDateTime.now());

                    // Для предстоящих записей - только "Отменить"
                    if (isFuture) {
                        buttons.getChildren().add(cancelBtn);
                    }
                    // Для завершенных записей - только "Удалить"
                    // (и "Завершить", если еще не завершена, но это для записей в прошлом которые еще не отмечены как завершенные)
                    else {
                        // Проверяем, не завершена ли уже запись
                        boolean isCompleted = appointment.isCompleted();

                        if (!isCompleted) {
                            // Если запись в прошлом, но еще не завершена - можно завершить
                            buttons.getChildren().add(completeBtn);
                            buttons.getChildren().add(deleteBtn);
                        } else {
                            // Если уже завершена - только удалить
                            buttons.getChildren().add(deleteBtn);
                        }
                    }

                    // Обработчики событий
                    cancelBtn.setOnAction(event -> cancelAppointment(appointment, tableView,
                            dateFilter, doctorFilter, patientFilter));
                    completeBtn.setOnAction(event -> markAsComplete(appointment, tableView,
                            dateFilter, doctorFilter, patientFilter));
                    deleteBtn.setOnAction(event -> deleteAppointmentPermanently(appointment, tableView,
                            dateFilter, doctorFilter, patientFilter));

                    setGraphic(buttons);
                }
            }
        });

        tableView.getColumns().addAll(idColumn, doctorColumn, patientColumn, dateColumn, statusColumn, actionsColumn);

        // Кнопки управления
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #98FB98; " +
                "-fx-text-fill: #333; -fx-background-radius: 10; -fx-border-radius: 10;");

        Button closeBtn = createCloseButton(appointmentsStage);

        // Создаем метод для обновления таблицы
        final Runnable refreshTable = () -> loadAppointmentsToTable(tableView, dateFilter.getValue(),
                doctorFilter.getValue(), patientFilter.getValue());

        refreshBtn.setOnAction(e -> refreshTable.run());

        applyFilterBtn.setOnAction(e -> refreshTable.run());

        clearFilterBtn.setOnAction(e -> {
            dateFilter.setValue(null);
            doctorFilter.setValue(null);
            patientFilter.setValue(null);
            refreshTable.run();
        });

        buttonBox.getChildren().addAll(refreshBtn, closeBtn);

        // Загружаем данные
        loadAppointmentsToTable(tableView, null, null, null);

        // Контейнер для таблицы с прокруткой
        VBox tableContainer = new VBox();
        tableContainer.getChildren().add(tableView);
        tableContainer.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 10;");

        mainLayout.getChildren().addAll(title, filterBox, tableContainer, buttonBox);

        Scene scene = new Scene(mainLayout, 900, 600);
        appointmentsStage.setScene(scene);
        appointmentsStage.show();
    }

    private void loadAppointmentsToTable(TableView<Appointment> tableView, LocalDate dateFilter,
                                         Doctor doctorFilter, Patient patientFilter) {
        try {
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            List<Appointment> appointments = appointmentDAO.getAllAppointments();

            // Фильтрация
            List<Appointment> filteredAppointments = appointments.stream()
                    .filter(app -> {
                        if (dateFilter == null) return true;
                        return app.getAppointmentDateTime().toLocalDate().equals(dateFilter);
                    })
                    .filter(app -> {
                        if (doctorFilter == null) return true;
                        return app.getDoctorId() == doctorFilter.getId();
                    })
                    .filter(app -> {
                        if (patientFilter == null) return true;
                        return app.getPatientPolicy() == patientFilter.getPolicy();
                    })
                    .sorted(Comparator.comparing(Appointment::getAppointmentDateTime))
                    .collect(Collectors.toList());

            tableView.getItems().setAll(filteredAppointments);

            // Обновляем интерфейс
            Platform.runLater(() -> {
                tableView.refresh();
            });

        } catch (PolyclinicException e) {
            System.out.println("Ошибка загрузки записей: " + e.getMessage());
            showError("Ошибка загрузки: " + e.getErrorCode().getDescription() +
                    "\n" + e.getAdditionalInfo());
        } catch (Exception e) {
            System.out.println("Ошибка загрузки записей: " + e.getMessage());
            showError("Неизвестная ошибка загрузки: " + e.getMessage());
        }
    }

    private void cancelAppointment(Appointment appointment, TableView<Appointment> tableView,
                                   DatePicker dateFilter, ComboBox<Doctor> doctorFilter,
                                   ComboBox<Patient> patientFilter) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Отмена записи");
        confirm.setHeaderText("Отменить запись?");
        confirm.setContentText("Запись ID: " + appointment.getId() + " будет отменена.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    AppointmentDAO appointmentDAO = new AppointmentDAO();
                    if (appointmentDAO.deleteAppointment(appointment.getId())) {
                        showInfo("Запись успешно отменена!");

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Platform.runLater(() -> {
                                    loadAppointmentsToTable(tableView, dateFilter.getValue(),
                                            doctorFilter.getValue(), patientFilter.getValue());
                                });
                            }
                        }, 500);
                    }
                } catch (PolyclinicException e) {
                    System.err.println("Ошибка поликлиники: " + e.getMessage());
                    e.printStackTrace();
                    showError("Ошибка отмены: " + e.getErrorCode().getDescription() +
                            "\n" + e.getAdditionalInfo());
                }
            }
        });
    }

    // ввода диагноза
    private void markAsComplete(Appointment appointment, TableView<Appointment> tableView,
                                DatePicker dateFilter, ComboBox<Doctor> doctorFilter,
                                ComboBox<Patient> patientFilter) {
        Stage completeStage = new Stage();
        completeStage.setTitle("Завершение приёма #" + appointment.getId());
        completeStage.setMinWidth(500);
        completeStage.setMinHeight(500); // Увеличил высоту для лучшего отображения

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Завершить приём и выдать справку");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Информация о приеме
        try {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();

            Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
            Patient patient = patientDAO.getPatientByPolicy(appointment.getPatientPolicy());

            Label infoLabel = new Label("Пациент: " + patient.getFullName() +
                    "\nВрач: " + doctor.getFullName() +
                    "\nДата приема: " +
                    appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            infoLabel.setStyle("-fx-font-size: 14px;");
            layout.getChildren().add(infoLabel);
        } catch (Exception e) {
            // Просто продолжаем без информации
        }

        // Диагноз (обязательное поле)
        Label diagnosisLabel = new Label("Диагноз*:");
        diagnosisLabel.setStyle("-fx-font-weight: bold;");

        ComboBox<String> diagnosisComboBox = new ComboBox<>();
        diagnosisComboBox.setItems(getDiagnosesList());
        diagnosisComboBox.setEditable(true);
        diagnosisComboBox.setPromptText("Выберите или введите диагноз...");
        diagnosisComboBox.setPrefWidth(400);

        // Предзаполняем из существующих заметок, если есть
        if (appointment.getNotes() != null && appointment.getNotes().contains("ДИАГНОЗ:")) {
            String notes = appointment.getNotes();
            int start = notes.indexOf("ДИАГНОЗ:") + 8;
            int end = notes.indexOf("\n", start);
            if (end == -1) end = notes.length();
            String extractedDiagnosis = notes.substring(start, end).trim();
            diagnosisComboBox.setValue(extractedDiagnosis);
        }

        // Добавляем автодополнение
        diagnosisComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                String searchText = newVal.toLowerCase();
                List<String> filtered = getDiagnosesList().stream()
                        .filter(d -> d.toLowerCase().contains(searchText))
                        .collect(Collectors.toList());

                if (!filtered.isEmpty()) {
                    diagnosisComboBox.show();
                }
            }
        });

        // Рекомендации
        Label recommendationsLabel = new Label("Рекомендации:");
        recommendationsLabel.setStyle("-fx-font-weight: bold;");

        TextArea recommendationsArea = new TextArea();
        recommendationsArea.setPromptText("Введите рекомендации...");
        recommendationsArea.setPrefRowCount(3);
        recommendationsArea.setPrefWidth(400);

        // Период справки (по умолчанию 7 дней)
        Label periodLabel = new Label("Период болезни*:");
        periodLabel.setStyle("-fx-font-weight: bold;");

        LocalDate appointmentDate = appointment.getAppointmentDateTime().toLocalDate();
        DatePicker startDatePicker = new DatePicker(appointmentDate);
        DatePicker endDatePicker = new DatePicker(appointmentDate.plusDays(7));

        // Добавляем валидацию дат
        Label dateErrorLabel = new Label("");
        dateErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Валидация: дата окончания не может быть раньше даты начала
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && endDatePicker.getValue() != null) {
                if (newVal.isAfter(endDatePicker.getValue())) {
                    dateErrorLabel.setText("Дата начала не может быть позже даты окончания!");
                    endDatePicker.setValue(newVal);
                } else {
                    dateErrorLabel.setText("");
                }
            }
        });

        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && startDatePicker.getValue() != null) {
                if (newVal.isBefore(startDatePicker.getValue())) {
                    dateErrorLabel.setText("Дата окончания не может быть раньше даты начала!");
                    startDatePicker.setValue(newVal);
                } else {
                    dateErrorLabel.setText("");
                }
            }
        });

        // Валидация: дата начала не может быть в будущем
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isAfter(LocalDate.now())) {
                dateErrorLabel.setText("Дата начала не может быть в будущем!");
                startDatePicker.setValue(LocalDate.now());
            }
        });

        HBox periodBox = new HBox(10,
                new Label("С*:"), startDatePicker,
                new Label("По*:"), endDatePicker
        );

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        // Кнопки
        Button saveBtn = new Button("Завершить приём и выдать справку");
        saveBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #98FB98; " +
                "-fx-text-fill: #333; -fx-background-radius: 10; -fx-border-radius: 10;");

        Button cancelBtn = createCancelButton(completeStage);

        saveBtn.setOnAction(e -> {
            String diagnosis = diagnosisComboBox.getEditor().getText().trim();
            String recommendations = recommendationsArea.getText().trim();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            // Сбрасываем стили ошибок
            diagnosisComboBox.setStyle("");
            startDatePicker.setStyle("");
            endDatePicker.setStyle("");
            statusLabel.setText("");
            dateErrorLabel.setText("");

            // ВАЛИДАЦИЯ
            List<String> errors = new ArrayList<>();

            // 1. Проверка диагноза
            if (diagnosis.isEmpty()) {
                errors.add("Введите диагноз");
                diagnosisComboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }

            // 2. Проверка даты начала
            if (startDate == null) {
                errors.add("Выберите дату начала болезни");
                startDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            } else if (startDate.isAfter(LocalDate.now())) {
                errors.add("Дата начала болезни не может быть в будущем");
                startDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }

            // 3. Проверка даты окончания
            if (endDate == null) {
                errors.add("Выберите дату окончания болезни");
                endDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }

            // 4. Проверка корректности периода (только если обе даты не null)
            if (startDate != null && endDate != null) {
                if (endDate.isBefore(startDate)) {
                    errors.add("Дата окончания должна быть позже даты начала");
                    endDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }

                long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
                if (daysBetween > 365) {
                    errors.add("Период болезни не может превышать 1 год");
                    endDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }
                if (endDate.isAfter(LocalDate.now().plusYears(1))) {
                    errors.add("Дата окончания болезни не может быть более чем через год от сегодняшнего дня");
                    endDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }
            }

            if (!errors.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Исправьте ошибки:\n");
                for (String error : errors) {
                    errorMessage.append("• ").append(error).append("\n");
                }
                statusLabel.setText(errorMessage.toString());
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                return;
            }

            try {
                // 1. Обновляем запись на приём
                AppointmentDAO appointmentDAO = new AppointmentDAO();

                appointment.setCompleted(true);
                appointment.setStatus("завершён");

                String currentNotes = appointment.getNotes() != null ? appointment.getNotes() : "";
                String updatedNotes = currentNotes +
                        (currentNotes.isEmpty() ? "" : "\n\n") +
                        "ДИАГНОЗ: " + diagnosis +
                        (recommendations.isEmpty() ? "" : "\nРЕКОМЕНДАЦИИ: " + recommendations);

                appointment.setNotes(updatedNotes);

                boolean appointmentUpdated = appointmentDAO.updateAppointment(appointment);

                // 2. Создаём медицинскую справку
                MedicalCertificate certificate = new MedicalCertificate();
                certificate.setPatientPolicy(appointment.getPatientPolicy());
                certificate.setIssueDate(LocalDate.now());
                certificate.setStartDate(startDate);
                certificate.setEndDate(endDate);
                certificate.setDiagnosis(diagnosis);
                certificate.setRecommendations(recommendations);
                certificate.setDoctorId(appointment.getDoctorId());
                certificate.setStatus("active");

                MedicalCertificateDAO certificateDAO = new MedicalCertificateDAO();
                boolean certificateCreated = certificateDAO.addCertificate(certificate);

                if (appointmentUpdated && certificateCreated) {
                    statusLabel.setText("Приём завершён и справка выдана!");
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(event -> {
                        completeStage.close();
                        // Обновляем таблицу записей
                        Platform.runLater(() -> {
                            loadAppointmentsToTable(tableView, dateFilter.getValue(),
                                    doctorFilter.getValue(), patientFilter.getValue());
                        });
                    });
                    pause.play();

                } else {
                    statusLabel.setText("Ошибка при сохранении!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }

            } catch (PolyclinicException ex) {
                statusLabel.setText("Ошибка: " + ex.getErrorCode().getDescription());
                statusLabel.setStyle("-fx-text-fill: red;");
            } catch (Exception ex) {
                statusLabel.setText("Ошибка при сохранении: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> completeStage.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(
                title, new Separator(),
                diagnosisLabel, diagnosisComboBox,
                recommendationsLabel, recommendationsArea,
                periodLabel, periodBox,
                dateErrorLabel,
                statusLabel, buttonBox
        );

        Scene scene = new Scene(layout, 500, 550);
        completeStage.setScene(scene);
        completeStage.show();
    }

    private void deleteAppointmentPermanently(Appointment appointment, TableView<Appointment> tableView,
                                              DatePicker dateFilter, ComboBox<Doctor> doctorFilter,
                                              ComboBox<Patient> patientFilter) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Удаление записи");
        confirm.setHeaderText("Удалить запись навсегда?");
        confirm.setContentText("Запись ID: " + appointment.getId() +
                " на " + appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                " будет полностью удалена из базы данных.\nЭто действие нельзя отменить!");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    AppointmentDAO appointmentDAO = new AppointmentDAO();
                    if (appointmentDAO.deleteAppointment(appointment.getId())) {
                        showInfo("Запись успешно удалена!");

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Platform.runLater(() -> {
                                    loadAppointmentsToTable(tableView, dateFilter.getValue(),
                                            doctorFilter.getValue(), patientFilter.getValue());
                                });
                            }
                        }, 500);
                    } else {
                        showError("Не удалось удалить запись!");
                    }
                } catch (PolyclinicException e) {
                    System.err.println("Ошибка поликлиники: " + e.getMessage());
                    e.printStackTrace();
                    showError("Ошибка удаления: " + e.getErrorCode().getDescription() +
                            "\n" + e.getAdditionalInfo());
                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                    e.printStackTrace();
                    showError("Ошибка при удалении записи: " + e.getMessage());
                }
            }
        });
    }
    private void loadPatientsForComboBox(ComboBox<Patient> comboBox) {
        try {
            PatientDAO patientDAO = new PatientDAO();
            List<Patient> patients = patientDAO.getAllPatients();
            comboBox.getItems().clear();
            comboBox.getItems().addAll(patients);

            comboBox.setConverter(new StringConverter<Patient>() {
                @Override
                public String toString(Patient patient) {
                    if (patient == null) return "";
                    return patient.getSurname() + " " + patient.getName().charAt(0) + ".";
                }

                @Override
                public Patient fromString(String string) {
                    return null;
                }
            });

            System.out.println("Загружено пациентов: " + patients.size());

        } catch (Exception e) {
            System.out.println("Ошибка загрузки пациентов для фильтра: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadPatientsForCertificatesComboBox(ComboBox<Patient> comboBox) {
        try {
            PatientDAO patientDAO = new PatientDAO();
            List<Patient> patients = patientDAO.getAllPatients();
            comboBox.getItems().clear();
            comboBox.getItems().addAll(patients);

            comboBox.setConverter(new StringConverter<Patient>() {
                @Override
                public String toString(Patient patient) {
                    if (patient == null) return "";
                    String fullName = patient.getSurname() + " " + patient.getName();
                    if (patient.getSecondName() != null && !patient.getSecondName().trim().isEmpty()) {
                        fullName += " " + patient.getSecondName();
                    }
                    fullName += " (Полис: " + String.format("%06d", patient.getPolicy()) + ")";
                    return fullName;
                }

                @Override
                public Patient fromString(String string) {
                    return null;
                }
            });

        } catch (Exception e) {
            System.out.println("Ошибка загрузки пациентов для справок: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAppointmentDetails(Appointment appointment) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Детали записи #" + appointment.getId());
        detailsStage.setMinWidth(400);
        detailsStage.setMinHeight(300);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        try {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();

            Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
            Patient patient = patientDAO.getPatientByPolicy(appointment.getPatientPolicy());

            StringBuilder detailsText = new StringBuilder();
            detailsText.append("ID записи: ").append(appointment.getId()).append("\n\n");

            if (doctor != null) {
                detailsText.append("Врач: ").append(doctor.getFullName()).append("\n");
                detailsText.append("Специализация: ").append(doctor.getSpecialty()).append("\n\n");
            } else {
                detailsText.append("Врач: Неизвестно\n\n");
            }

            if (patient != null) {
                detailsText.append("Пациент: ").append(patient.getFullName()).append("\n");
                detailsText.append("Полис: ").append(String.format("%06d", patient.getPolicy())).append("\n\n");
            } else {
                detailsText.append("Пациент: Неизвестно\n\n");
            }

            detailsText.append("Дата и время: ").append(
                    appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            ).append("\n\n");

            detailsText.append("Статус: ").append(appointment.getStatus()).append("\n\n");

            if (appointment.getNotes() != null && !appointment.getNotes().isEmpty()) {
                detailsText.append("Примечания:\n").append(appointment.getNotes());
            } else {
                detailsText.append("Примечания: нет");
            }

            TextArea detailsArea = new TextArea(detailsText.toString());
            detailsArea.setEditable(false);
            detailsArea.setWrapText(true);
            detailsArea.setPrefRowCount(10);
            detailsArea.setStyle("-fx-font-size: 12px;");

            Button closeBtn = createCloseButton(detailsStage);

            layout.getChildren().addAll(detailsArea, closeBtn);

        } catch (Exception e) {
            Label error = new Label("Ошибка загрузки деталей: " + e.getMessage());
            error.setStyle("-fx-text-fill: red;");
            layout.getChildren().add(error);
        }

        Scene scene = new Scene(layout, 400, 300);
        detailsStage.setScene(scene);
        detailsStage.show();
    }
    private void showPatientCertificates(Patient patient) {
        Stage certificatesStage = new Stage();
        certificatesStage.setTitle("Справки о болезни: " + patient.getFullName());
        certificatesStage.setMinWidth(800);
        certificatesStage.setMinHeight(600);

        VBox mainLayout = new VBox(15);
        mainLayout.setStyle("-fx-padding: 20;");

        Label title = new Label("Медицинские справки пациента: " + patient.getFullName());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Таблица справок
        TableView<MedicalCertificate> tableView = new TableView<>();

        TableColumn<MedicalCertificate, String> diagnosisColumn = new TableColumn<>("Диагноз");
        diagnosisColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDiagnosis()));

        TableColumn<MedicalCertificate, String> periodColumn = new TableColumn<>("Период");
        periodColumn.setCellValueFactory(cellData -> {
            MedicalCertificate cert = cellData.getValue();
            String period = cert.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                    " - " + cert.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            return new SimpleStringProperty(period);
        });

        TableColumn<MedicalCertificate, String> doctorColumn = new TableColumn<>("Врач");
        doctorColumn.setCellValueFactory(cellData -> {
            try {
                DoctorDAO doctorDAO = new DoctorDAO();
                Doctor doctor = doctorDAO.getDoctorById(cellData.getValue().getDoctorId());
                return new SimpleStringProperty(doctor != null ? doctor.getFullName() : "Неизвестно");
            } catch (Exception e) {
                return new SimpleStringProperty("Ошибка");
            }
        });

        TableColumn<MedicalCertificate, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setPrefWidth(100);
        statusColumn.setCellFactory(column -> new TableCell<MedicalCertificate, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("active")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        setTooltip(new Tooltip("Справка действует"));
                    } else if (status.equals("closed")) {
                        setStyle("-fx-text-fill: gray;");
                        setTooltip(new Tooltip("Справка закрыта"));
                    }
                }
            }
        });
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        // Колонка действий с кнопками PDF и Удалить
        TableColumn<MedicalCertificate, Void> actionsColumn = new TableColumn<>("Действия");
        actionsColumn.setPrefWidth(150);
        actionsColumn.setCellFactory(param -> new TableCell<MedicalCertificate, Void>() {
            private final Button pdfBtn = new Button("PDF");
            private final Button deleteBtn = new Button("Удалить");
            private final HBox buttons = new HBox(5, pdfBtn, deleteBtn);

            {
                pdfBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-color: #DDA0DD;");
                deleteBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-color: #FFB6C1;");

                pdfBtn.setOnAction(event -> {
                    MedicalCertificate cert = getTableView().getItems().get(getIndex());
                    exportCertificateToPDF(cert, patient);
                });

                deleteBtn.setOnAction(event -> {
                    MedicalCertificate cert = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Подтверждение удаления");
                    confirm.setHeaderText("Удалить справку?");
                    confirm.setContentText("Вы уверены, что хотите удалить справку с диагнозом: \"" +
                            cert.getDiagnosis() + "\"?\n" +
                            "Период: " + cert.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                            " - " + cert.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            MedicalCertificateDAO certificateDAO = new MedicalCertificateDAO();
                            boolean deleted = certificateDAO.deleteCertificate(cert.getId());

                            if (deleted) {
                                // Обновляем таблицу для этого пациента
                                loadCertificatesForPatient(tableView, patient.getPolicy());
                                showInfo("Справка успешно удалена");
                            } else {
                                showError("Не удалось удалить справку");
                            }
                        } catch (PolyclinicException e) {
                            showError("Ошибка удаления справки: " + e.getMessage());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        // Добавляем все колонки
        tableView.getColumns().addAll(diagnosisColumn, periodColumn, doctorColumn, statusColumn, actionsColumn);

        // Кнопки
        Button addCertificateBtn = new Button("Выдать новую справку");
        addCertificateBtn.setOnAction(e -> showAddCertificateForm(patient, tableView, null));

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadCertificatesForPatient(tableView, patient.getPolicy()));

        Button closeBtn = createCloseButton(certificatesStage);

        HBox buttonBox = new HBox(10, addCertificateBtn, refreshBtn, closeBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // Загрузка данных - используем правильный метод
        loadCertificatesForPatient(tableView, patient.getPolicy());

        mainLayout.getChildren().addAll(title, tableView, buttonBox);

        Scene scene = new Scene(mainLayout, 800, 600);
        certificatesStage.setScene(scene);
        certificatesStage.show();
    }


    private void showCertificatesManagement() {
        Stage managementStage = new Stage();
        managementStage.setTitle("Управление медицинскими справками");
        managementStage.setMinWidth(800);
        managementStage.setMinHeight(600);

        VBox mainLayout = new VBox(15);
        mainLayout.setStyle("-fx-padding: 20; -fx-background-color: #FFE4E1;");

        Label title = new Label("Управление медицинскими справками");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Карточка выбора пациента
        VBox selectionCard = new VBox(15);
        selectionCard.setStyle("-fx-padding: 20; -fx-background-color: white; " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2;");

        Label selectLabel = new Label("Выберите пациента:");
        selectLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #555;");

        // ComboBox для выбора пациента
        ComboBox<Patient> patientComboBox = new ComboBox<>();
        patientComboBox.setPromptText("Выберите пациента...");
        patientComboBox.setPrefWidth(350);

        // Загружаем пациентов в ComboBox
        loadPatientsForCertificatesComboBox(patientComboBox);

        HBox selectionBox = new HBox(10, selectLabel, patientComboBox);
        selectionBox.setAlignment(Pos.CENTER_LEFT);

        selectionCard.getChildren().add(selectionBox);

        // Таблица справок
        TableView<MedicalCertificate> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white;");

        // Колонки таблицы
        TableColumn<MedicalCertificate, String> diagnosisColumn = new TableColumn<>("Диагноз");
        diagnosisColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDiagnosis()));
        diagnosisColumn.setPrefWidth(250);

        TableColumn<MedicalCertificate, String> periodColumn = new TableColumn<>("Период болезни");
        periodColumn.setCellValueFactory(cellData -> {
            MedicalCertificate cert = cellData.getValue();
            String period = cert.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                    " - " + cert.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            return new SimpleStringProperty(period);
        });
        periodColumn.setPrefWidth(150);

        TableColumn<MedicalCertificate, String> doctorColumn = new TableColumn<>("Врач");
        doctorColumn.setCellValueFactory(cellData -> {
            try {
                DoctorDAO doctorDAO = new DoctorDAO();
                Doctor doctor = doctorDAO.getDoctorById(cellData.getValue().getDoctorId());
                return new SimpleStringProperty(doctor != null ? doctor.getFullName() : "Неизвестно");
            } catch (Exception e) {
                return new SimpleStringProperty("Ошибка");
            }
        });
        doctorColumn.setPrefWidth(150);

        TableColumn<MedicalCertificate, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setPrefWidth(100);
        statusColumn.setCellFactory(column -> new TableCell<MedicalCertificate, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("active")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        setTooltip(new Tooltip("Справка действует"));
                    } else if (status.equals("closed")) {
                        setStyle("-fx-text-fill: gray;");
                        setTooltip(new Tooltip("Справка закрыта"));
                    }
                }
            }
        });
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        // Колонка действий с кнопками PDF и Удалить
        TableColumn<MedicalCertificate, Void> actionsColumn = new TableColumn<>("Действия");
        actionsColumn.setPrefWidth(150);
        actionsColumn.setCellFactory(param -> new TableCell<MedicalCertificate, Void>() {
            private final Button pdfBtn = new Button("PDF");
            private final Button deleteBtn = new Button("Удалить");
            private final HBox buttons = new HBox(5, pdfBtn, deleteBtn);

            {
                pdfBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 6; -fx-background-color: #DDA0DD;");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3 6; -fx-background-color: #FFB6C1;");

                pdfBtn.setOnAction(event -> {
                    MedicalCertificate cert = getTableView().getItems().get(getIndex());

                    // Получаем выбранного пациента из ComboBox
                    Patient selectedPatient = patientComboBox.getValue();

                    if (selectedPatient != null) {
                        // Теперь передаем оба параметра
                        exportCertificateToPDF(cert, selectedPatient);
                    } else {
                        showError("Выберите пациента!");
                    }
                });

                deleteBtn.setOnAction(event -> {
                    MedicalCertificate cert = getTableView().getItems().get(getIndex());

                    // Подтверждение удаления
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Подтверждение удаления");
                    confirm.setHeaderText("Удалить справку?");
                    confirm.setContentText("Вы уверены, что хотите удалить справку с диагнозом: \"" +
                            cert.getDiagnosis() + "\"?\n" +
                            "Период: " + cert.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                            " - " + cert.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            MedicalCertificateDAO certificateDAO = new MedicalCertificateDAO();
                            boolean deleted = certificateDAO.deleteCertificate(cert.getId());

                            if (deleted) {
                                // Обновляем таблицу
                                Patient selectedPatient = patientComboBox.getValue();
                                if (selectedPatient != null) {
                                    loadCertificatesForPatient(tableView, selectedPatient.getPolicy());
                                }
                                showInfo("Справка успешно удалена");
                            } else {
                                showError("Не удалось удалить справку");
                            }
                        } catch (PolyclinicException e) {
                            showError("Ошибка удаления справки: " + e.getMessage());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        // Добавляем ВСЕ колонки в таблицу
        tableView.getColumns().addAll(diagnosisColumn, periodColumn, doctorColumn, statusColumn, actionsColumn);

        // Кнопки управления
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button addCertificateBtn = new Button("Выдать новую справку");
        addCertificateBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; " +
                "-fx-background-color: #98FB98; -fx-text-fill: #333; " +
                "-fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: #7CFC00; -fx-border-width: 2; " +
                "-fx-cursor: hand;");
        addCertificateBtn.setDisable(true);

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; " +
                "-fx-background-color: #FFB6C1; -fx-text-fill: white; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;");

        Button closeBtn = createCloseButton(managementStage);

        buttonBox.getChildren().addAll(addCertificateBtn, refreshBtn, closeBtn);

        // Метод для загрузки справок пациента
        Runnable loadPatientCertificates = () -> {
            Patient selectedPatient = patientComboBox.getValue();
            if (selectedPatient != null) {
                try {
                    MedicalCertificateDAO certificateDAO = new MedicalCertificateDAO();
                    List<MedicalCertificate> certificates = certificateDAO.getCertificatesByPatient(selectedPatient.getPolicy());
                    tableView.getItems().setAll(certificates);

                    if (certificates.isEmpty()) {
                        tableView.setPlaceholder(new Label("У пациента нет медицинских справок"));
                    }
                } catch (PolyclinicException e) {
                    tableView.setPlaceholder(new Label("Ошибка загрузки справок: " + e.getMessage()));
                }
            } else {
                tableView.getItems().clear();
                tableView.setPlaceholder(new Label("Выберите пациента для просмотра справок"));
            }
        };

        // 1. При выборе пациента из ComboBox
        patientComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            addCertificateBtn.setDisable(!hasSelection);

            if (hasSelection) {
                loadPatientCertificates.run();
            } else {
                tableView.getItems().clear();
                tableView.setPlaceholder(new Label("Выберите пациента для просмотра справок"));
            }
        });

        // 2. Кнопка "Выдать новую справку"
        addCertificateBtn.setOnAction(e -> {
            Patient selectedPatient = patientComboBox.getValue();
            if (selectedPatient != null) {
                showAddCertificateForm(selectedPatient, tableView, patientComboBox);
            }
        });

        // 3. Кнопка "Обновить"
        refreshBtn.setOnAction(e -> loadPatientCertificates.run());

        // Собираем интерфейс
        VBox tableCard = new VBox(10);
        tableCard.setStyle("-fx-padding: 15; -fx-background-color: white; " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2;");
        tableCard.getChildren().add(tableView);

        mainLayout.getChildren().addAll(title, selectionCard, tableCard, buttonBox);

        // Устанавливаем начальный текст в таблице
        tableView.setPlaceholder(new Label("Выберите пациента для просмотра справок"));

        Scene scene = new Scene(mainLayout, 800, 600);
        managementStage.setScene(scene);
        managementStage.show();
    }
    private void showAddCertificateForm(Patient patient, TableView<MedicalCertificate> tableView,
                                        ComboBox<Patient> patientComboBox) {
        Stage formStage = new Stage();
        formStage.setTitle("Выдача справки пациенту: " + patient.getFullName());
        formStage.setMinWidth(500);
        formStage.setMinHeight(500);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #FFE4E1;");

        // Карточка формы
        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-padding: 20; -fx-background-color: white; " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2;");

        // Заголовок
        Label title = new Label("Выдача медицинской справки");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Информация о пациенте
        Label patientInfo = new Label("Пациент: " + patient.getFullName() +
                "\nПолис: " + String.format("%06d", patient.getPolicy()));
        patientInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Separator separator1 = new Separator();

        // ПЕРИОД БОЛЕЗНИ
        Label datesLabel = new Label("Период болезни:");
        datesLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusDays(7));

        // Подсказки для DatePicker
        Tooltip startTooltip = new Tooltip("Дата начала болезни");
        Tooltip endTooltip = new Tooltip("Дата окончания болезни\n(должна быть позже даты начала)");
        startDatePicker.setTooltip(startTooltip);
        endDatePicker.setTooltip(endTooltip);

        // Метка для отображения количества дней
        Label periodInfoLabel = new Label();
        periodInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Метод для обновления информации о периоде
        Runnable updatePeriodInfo = () -> {
            if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                LocalDate start = startDatePicker.getValue();
                LocalDate end = endDatePicker.getValue();

                if (end.isBefore(start)) {
                    periodInfoLabel.setText("⚠️ Дата окончания должна быть позже даты начала!");
                    periodInfoLabel.setStyle("-fx-text-fill: red;");
                } else {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(start, end);
                    periodInfoLabel.setText("Период: " + (days + 1) + " дней");
                    if (days > 30) {
                        periodInfoLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else {
                        periodInfoLabel.setStyle("-fx-text-fill: green;");
                    }
                }
            } else {
                periodInfoLabel.setText("");
            }
        };

        // Слушатели для валидации дат
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && endDatePicker.getValue() != null) {
                updatePeriodInfo.run();

                // Автоматически обновляем конечную дату, если она стала раньше начальной
                if (endDatePicker.getValue().isBefore(newVal)) {
                    endDatePicker.setValue(newVal.plusDays(1));
                }
            }
        });

        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && startDatePicker.getValue() != null) {
                updatePeriodInfo.run();
            }
        });

        // Инициализируем информацию о периоде
        updatePeriodInfo.run();

        HBox datesBox = new HBox(10,
                new Label("С:"), startDatePicker,
                new Label("По:"), endDatePicker
        );
        datesBox.setAlignment(Pos.CENTER_LEFT);

        Separator separator2 = new Separator();

        // ДИАГНОЗ
        Label diagnosisLabel = new Label("Диагноз*:");
        diagnosisLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

        ComboBox<String> diagnosisComboBox = new ComboBox<>();
        diagnosisComboBox.setItems(getDiagnosesList());
        diagnosisComboBox.setEditable(true); // Позволяет вводить свой вариант
        diagnosisComboBox.setPromptText("Выберите или введите диагноз...");
        diagnosisComboBox.setPrefWidth(400);

        // Добавляем автодополнение
        diagnosisComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                String searchText = newVal.toLowerCase();
                List<String> filtered = getDiagnosesList().stream()
                        .filter(d -> d.toLowerCase().contains(searchText))
                        .collect(Collectors.toList());

                if (!filtered.isEmpty()) {
                    diagnosisComboBox.show();
                }
            }
        });

        // Добавляем подсказку
        Tooltip diagnosisTooltip = new Tooltip("Выберите из списка или введите свой вариант");
        diagnosisComboBox.setTooltip(diagnosisTooltip);

        // РЕКОМЕНДАЦИИ
        Label recommendationsLabel = new Label("Рекомендации:");
        recommendationsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

        TextArea recommendationsArea = new TextArea();
        recommendationsArea.setPromptText("Введите рекомендации (необязательно)...");
        recommendationsArea.setPrefRowCount(3);
        recommendationsArea.setPrefWidth(400);
        recommendationsArea.setWrapText(true);

        Separator separator3 = new Separator();

        // ВРАЧ
        Label doctorLabel = new Label("Лечащий врач*:");
        doctorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

        ComboBox<Doctor> doctorComboBox = new ComboBox<>();
        doctorComboBox.setPromptText("Выберите врача...");
        doctorComboBox.setPrefWidth(300);
        loadDoctorsForComboBox(doctorComboBox);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        Separator separator4 = new Separator();

        // КНОПКИ
        Button saveBtn = new Button("Выдать справку");
        saveBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; " +
                "-fx-background-color: #98FB98; -fx-text-fill: #333; " +
                "-fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: #7CFC00; -fx-border-width: 2; " +
                "-fx-cursor: hand;");

        Button cancelBtn = createCancelButton(formStage);

        HBox buttonBox = new HBox(15, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        saveBtn.setOnAction(e -> {
            // Сбрасываем стили ошибок
            diagnosisComboBox.setStyle("");
            startDatePicker.setStyle("");
            endDatePicker.setStyle("");
            doctorComboBox.setStyle("");

            // Собираем ошибки
            List<String> errors = new ArrayList<>();

            // 1. Проверка диагноза
            String diagnosis = diagnosisComboBox.getEditor().getText().trim();
            if (diagnosis.isEmpty()) {
                errors.add("Введите диагноз");
                diagnosisComboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            } else {
                diagnosisComboBox.setStyle("");
            }

            // 2. Проверка врача
            if (doctorComboBox.getValue() == null) {
                errors.add("Выберите врача");
                doctorComboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }

            // 3. Проверка дат
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate == null) {
                errors.add("Выберите дату начала болезни");
                startDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }

            if (endDate == null) {
                errors.add("Выберите дату окончания болезни");
                endDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }

            // 4. Проверка корректности дат (только если обе даты не null)
            if (startDate != null && endDate != null) {
                if (endDate.isBefore(startDate)) {
                    errors.add("Дата окончания должна быть позже даты начала");
                    endDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }

                if (startDate.isAfter(LocalDate.now())) {
                    errors.add("Дата начала болезни не может быть в будущем");
                    startDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }

                long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
                if (daysBetween > 365) {
                    errors.add("Период болезни не может превышать 1 год");
                    endDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }

                // Проверка, что период не слишком далеко в прошлом (только если даты валидны)
                if (startDate.isBefore(LocalDate.now().minusYears(1))) {
                    errors.add("Дата начала болезни не может быть более года назад");
                    startDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }
            }

            if (startDate != null) {
                try {
                    AppointmentDAO appointmentDAO = new AppointmentDAO();
                    List<Appointment> patientAppointments = appointmentDAO.getAppointmentsByPatient(patient.getPolicy());

                    // Найти последний прием пациента
                    Optional<LocalDateTime> lastAppointment = patientAppointments.stream()
                            .map(Appointment::getAppointmentDateTime)
                            .max(LocalDateTime::compareTo);

                    if (lastAppointment.isPresent() && startDate.isBefore(lastAppointment.get().toLocalDate())) {
                        periodInfoLabel.setText("Внимание: справка выдается на период, когда пациент не был на приеме!");
                        periodInfoLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else {
                        periodInfoLabel.setText("");
                        periodInfoLabel.setStyle("");
                    }
                } catch (Exception ex) {
                    System.out.println("Не удалось проверить историю приемов: " + ex.getMessage());
                }
            }

            // Если есть ошибки - показываем их
            if (!errors.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Исправьте ошибки:\n");
                for (String error : errors) {
                    errorMessage.append("• ").append(error).append("\n");
                }
                statusLabel.setText(errorMessage.toString());
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                return;
            }
            try {
                MedicalCertificate certificate = new MedicalCertificate();
                certificate.setPatientPolicy(patient.getPolicy());
                certificate.setIssueDate(LocalDate.now());
                certificate.setStartDate(startDate);
                certificate.setEndDate(endDate);
                certificate.setDiagnosis(diagnosis);
                certificate.setRecommendations(recommendationsArea.getText().trim());
                certificate.setDoctorId(doctorComboBox.getValue().getId());
                certificate.setStatus("active");

                MedicalCertificateDAO certificateDAO = new MedicalCertificateDAO();
                if (certificateDAO.addCertificate(certificate)) {
                    statusLabel.setText("Справка успешно выдана!");
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    // Логирование выдачи справки
                    AppLogger.audit("Выдача медицинской справки",
                            "Система",
                            "Пациент: " + patient.getFullName() +
                                    " (полис: " + patient.getPolicy() + "), " +
                                    "Диагноз: " + diagnosis +
                                    ", Период: " + startDate + " - " + endDate);

                    AppLogger.info("Медицинская справка выдана: ID=" + certificate.getId() +
                            ", Пациент полис=" + patient.getPolicy() +
                            ", Диагноз=" + diagnosis);

                    // Обновляем таблицу
                    loadCertificatesForPatient(tableView, patient.getPolicy());

                    // Закрываем окно через 2 секунды
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(event -> formStage.close());
                    pause.play();
                } else {
                    statusLabel.setText("Ошибка при выдаче справки!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    AppLogger.error("Ошибка выдачи медицинской справки (DAO вернул false)");
                }
            } catch (PolyclinicException ex) {
                AppLogger.error("Ошибка выдачи справки: " + ex.getErrorCode().getDescription(), ex);
                statusLabel.setText("Ошибка: " + ex.getErrorCode().getDescription());
                statusLabel.setStyle("-fx-text-fill: red;");
            } catch (Exception ex) {
                AppLogger.error("Неизвестная ошибка при выдаче справки", ex);
                statusLabel.setText("Неизвестная ошибка: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> formStage.close());

        // Собираем форму
        formCard.getChildren().addAll(
                title,
                patientInfo,
                separator1,
                datesLabel,
                datesBox,
                periodInfoLabel,
                separator2,
                diagnosisLabel,
                diagnosisComboBox,
                recommendationsLabel,
                recommendationsArea,
                separator3,
                doctorLabel,
                doctorComboBox,
                separator4,
                statusLabel,
                buttonBox
        );

        layout.getChildren().addAll(formCard);

        Scene scene = new Scene(layout, 500, 600);
        formStage.setScene(scene);
        formStage.show();
    }

    private void loadCertificatesForPatient(TableView<MedicalCertificate> tableView, int patientPolicy) {
        try {
            MedicalCertificateDAO certificateDAO = new MedicalCertificateDAO();
            List<MedicalCertificate> certificates = certificateDAO.getCertificatesByPatient(patientPolicy);
            tableView.getItems().setAll(certificates);
            if (certificates.isEmpty()) {
                tableView.setPlaceholder(new Label("У пациента нет медицинских справок"));
            }
        } catch (PolyclinicException e) {
            System.err.println("Ошибка загрузки справок: " + e.getMessage());
            tableView.setPlaceholder(new Label("Ошибка загрузки справок: " + e.getMessage()));
        }
    }

    private void showCertificateDetails(MedicalCertificate certificate) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Детали справки #" + certificate.getId());
        detailsStage.setMinWidth(500);
        detailsStage.setMinHeight(400);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20;");

        try {
            PatientDAO patientDAO = new PatientDAO();
            DoctorDAO doctorDAO = new DoctorDAO();

            Patient patient = patientDAO.getPatientByPolicy(certificate.getPatientPolicy());
            Doctor doctor = doctorDAO.getDoctorById(certificate.getDoctorId());

            StringBuilder details = new StringBuilder();
            details.append("МЕДИЦИНСКАЯ СПРАВКА #").append(certificate.getId()).append("\n\n");
            details.append("Пациент: ").append(patient != null ? patient.getFullName() : "Неизвестно").append("\n");
            details.append("Номер полиса: ").append(String.format("%06d", certificate.getPatientPolicy())).append("\n");
            details.append("Дата выдачи: ").append(certificate.getIssueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append("\n");
            details.append("Период болезни: ").append(certificate.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .append(" - ").append(certificate.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append("\n");
            details.append("Лечащий врач: ").append(doctor != null ? doctor.getFullName() : "Неизвестно").append("\n");
            details.append("Специализация: ").append(doctor != null ? doctor.getSpecialty() : "Неизвестно").append("\n");
            details.append("Статус: ").append(certificate.getStatus()).append("\n\n");
            details.append("ДИАГНОЗ:\n").append(certificate.getDiagnosis()).append("\n\n");

            if (certificate.getRecommendations() != null && !certificate.getRecommendations().isEmpty()) {
                details.append("РЕКОМЕНДАЦИИ:\n").append(certificate.getRecommendations());
            }

            TextArea detailsArea = new TextArea(details.toString());
            detailsArea.setEditable(false);
            detailsArea.setWrapText(true);
            detailsArea.setPrefRowCount(15);
            detailsArea.setStyle("-fx-font-size: 12px;");

            Button closeBtn = createCloseButton(detailsStage);

            layout.getChildren().addAll(detailsArea, closeBtn);

        } catch (Exception e) {
            Label error = new Label("Ошибка загрузки деталей: " + e.getMessage());
            error.setStyle("-fx-text-fill: red;");
            layout.getChildren().add(error);
        }

        Scene scene = new Scene(layout, 500, 400);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    // Статистика заболеваний
    private void showDiseaseStatistics() {
        Stage statsStage = new Stage();
        statsStage.setTitle("Статистика заболеваний");
        statsStage.setMinWidth(600);
        statsStage.setMinHeight(400);

        VBox mainLayout = new VBox(15);
        mainLayout.setStyle("-fx-padding: 20;");

        Label title = new Label("Количество заболеваний по видам");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<DiseaseStatistics> tableView = new TableView<>();

        TableColumn<DiseaseStatistics, String> diseaseColumn = new TableColumn<>("Заболевание");
        diseaseColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDiseaseName()));
        diseaseColumn.setPrefWidth(300);

        TableColumn<DiseaseStatistics, Integer> countColumn = new TableColumn<>("Количество случаев");
        countColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCount()).asObject());
        countColumn.setPrefWidth(150);

        tableView.getColumns().addAll(diseaseColumn, countColumn);

        // Кнопка обновления
        Button refreshBtn = new Button("Обновить статистику");
        refreshBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #98FB98; " +
                "-fx-text-fill: #333; -fx-background-radius: 10; -fx-border-radius: 10;");

        refreshBtn.setOnAction(e -> loadStatistics(tableView));

        // Загрузка данных
        loadStatistics(tableView);

        Button closeBtn = createCloseButton(statsStage);

        // СОЗДАЕМ buttonBox и добавляем кнопки
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);


        mainLayout.getChildren().addAll(title, tableView, buttonBox);

        Scene scene = new Scene(mainLayout, 600, 400);
        statsStage.setScene(scene);
        statsStage.show();
    }
    private void loadStatistics(TableView<DiseaseStatistics> tableView) {
        try {
            MedicalCertificateDAO certificateDAO = new MedicalCertificateDAO();
            List<DiseaseStatistics> stats = certificateDAO.getDiseaseStatistics();
            tableView.getItems().setAll(stats);

            // Подсчитываем общее количество
            int total = stats.stream().mapToInt(DiseaseStatistics::getCount).sum();
            tableView.setPlaceholder(new Label("Всего заболеваний: " + total + "\n" +
                    "Количество различных диагнозов: " + stats.size()));

        } catch (PolyclinicException e) {
            tableView.setPlaceholder(new Label("Ошибка загрузки статистики: " + e.getMessage()));
        }
    }

    private void exportStatisticsToCSV(TableView<DiseaseStatistics> tableView) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить статистику как CSV");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV файлы", "*.csv")
            );
            fileChooser.setInitialFileName("статистика_заболеваний_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

            File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                    // Заголовок
                    writer.println("Заболевание;Количество случаев");

                    // Данные
                    for (DiseaseStatistics stat : tableView.getItems()) {
                        writer.println(stat.getDiseaseName() + ";" + stat.getCount());
                    }

                    showSuccess("Статистика успешно экспортирована в файл: " + file.getName());
                }
            }
        } catch (Exception e) {
            showError("Ошибка при экспорте: " + e.getMessage());
        }
    }

    private void createCertificateFromAppointment(Appointment appointment) {
        Stage certificateStage = new Stage();
        certificateStage.setTitle("Создание справки на основе приёма #" + appointment.getId());

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20;");

        try {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();

            Doctor doctor = doctorDAO.getDoctorById(appointment.getDoctorId());
            Patient patient = patientDAO.getPatientByPolicy(appointment.getPatientPolicy());

            Label infoLabel = new Label("Создание справки на основе приёма:\n" +
                    "Пациент: " + patient.getFullName() + "\n" +
                    "Врач: " + doctor.getFullName() + "\n" +
                    "Дата приёма: " +
                    appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            infoLabel.setStyle("-fx-font-size: 14px;");

            // Форма для диагноза (можно предзаполнить из заметок приема)
            TextArea diagnosisArea = new TextArea();
            diagnosisArea.setPromptText("Введите диагноз...");
            if (appointment.getNotes() != null && appointment.getNotes().contains("ДИАГНОЗ:")) {
                // Извлекаем диагноз из существующих заметок
                String notes = appointment.getNotes();
                int start = notes.indexOf("ДИАГНОЗ:") + 8;
                int end = notes.indexOf("\n", start);
                if (end == -1) end = notes.length();
                diagnosisArea.setText(notes.substring(start, end).trim());
            }

            // Кнопки
            Button createBtn = new Button("Создать справку");
            createBtn.setOnAction(e -> {
                if (diagnosisArea.getText().trim().isEmpty()) {
                    showError("Введите диагноз!");
                    return;
                }

                try {
                    MedicalCertificate certificate = new MedicalCertificate();
                    certificate.setPatientPolicy(patient.getPolicy());
                    certificate.setIssueDate(LocalDate.now());
                    certificate.setStartDate(appointment.getAppointmentDateTime().toLocalDate());
                    certificate.setEndDate(appointment.getAppointmentDateTime().toLocalDate().plusDays(7));
                    certificate.setDiagnosis(diagnosisArea.getText().trim());
                    certificate.setDoctorId(doctor.getId());
                    certificate.setStatus("active");

                    MedicalCertificateDAO certificateDAO = new MedicalCertificateDAO();
                    if (certificateDAO.addCertificate(certificate)) {
                        showSuccess("Справка успешно создана!");
                        certificateStage.close();
                    }
                } catch (PolyclinicException ex) {
                    showError("Ошибка: " + ex.getMessage());
                }
            });

            layout.getChildren().addAll(infoLabel, new Label("Диагноз:"), diagnosisArea, createBtn);

        } catch (Exception e) {
            layout.getChildren().add(new Label("Ошибка: " + e.getMessage()));
        }

        Scene scene = new Scene(layout, 400, 300);
        certificateStage.setScene(scene);
        certificateStage.show();
    }

    private void loadDoctorsForComboBox(ComboBox<Doctor> comboBox) {
        try {
            DoctorDAO doctorDAO = new DoctorDAO();
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            comboBox.getItems().clear();
            comboBox.getItems().addAll(doctors);

            comboBox.setConverter(new StringConverter<Doctor>() {
                @Override
                public String toString(Doctor doctor) {
                    if (doctor == null) return "";
                    return doctor.getSurname() + " " + doctor.getName() +
                            (doctor.getSecondName() != null ? " " + doctor.getSecondName() : "") +
                            " - " + doctor.getSpecialty();
                }

                @Override
                public Doctor fromString(String string) {
                    return null;
                }
            });

        } catch (Exception e) {
            System.out.println("Ошибка загрузки врачей: " + e.getMessage());
        }
    }

    private void showAddDoctorForm() {
        Stage formStage = new Stage();
        formStage.setTitle("Добавить нового врача");
        formStage.setMinWidth(400);
        formStage.setMinHeight(500);

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center;");

        Label title = new Label("Добавить нового врача");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Поля формы
        TextField surnameField = new TextField();
        surnameField.setPromptText("Фамилия*");
        surnameField.setStyle("-fx-pref-width: 250px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Имя*");
        nameField.setStyle("-fx-pref-width: 250px;");

        TextField secondNameField = new TextField();
        secondNameField.setPromptText("Отчество");
        secondNameField.setStyle("-fx-pref-width: 250px;");

        ComboBox<String> specialtyComboBox = new ComboBox<>();
        specialtyComboBox.setPromptText("Специализация*");
        specialtyComboBox.setStyle("-fx-pref-width: 250px;");
        specialtyComboBox.getItems().addAll(
                "Терапевт", "Хирург", "Педиатр", "Невролог", "Кардиолог",
                "Офтальмолог", "Отоларинголог", "Стоматолог", "Гинеколог",
                "Уролог", "Дерматолог", "Психиатр", "Ортопед", "Травматолог"
        );

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        // Валидация
        setupAutoCapitalize(surnameField);
        setupAutoCapitalize(nameField);
        setupAutoCapitalize(secondNameField);

        surnameField.setTextFormatter(ValidationUtils.createNameFormatter());
        nameField.setTextFormatter(ValidationUtils.createNameFormatter());

        // Кнопки
        Button saveBtn = new Button("Сохранить врача");
        saveBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #98FB98; " +
                "-fx-text-fill: #333; -fx-background-radius: 10; -fx-border-radius: 10;");

        Button cancelBtn = new Button("Отмена");
        cancelBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #FFB6C1; " +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10;");

        saveBtn.setOnAction(e -> {
            String surname = surnameField.getText().trim();
            String name = nameField.getText().trim();
            String secondName = secondNameField.getText().trim();
            String specialty = specialtyComboBox.getValue();

            // Валидация
            InputValidator.ValidationResult surnameValidation = InputValidator.validateName(surname, "Фамилия");
            InputValidator.ValidationResult nameValidation = InputValidator.validateName(name, "Имя");
            InputValidator.ValidationResult secondNameValidation = InputValidator.validatePatronymic(secondName);

            boolean hasErrors = false;

            if (!secondNameValidation.isValid()) {
                if (hasErrors) {
                    statusLabel.setText(statusLabel.getText() + "\n" + secondNameValidation.getMessage());
                } else {
                    statusLabel.setText(secondNameValidation.getMessage());
                }
                statusLabel.setStyle("-fx-text-fill: red;");
                secondNameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hasErrors = true;
            } else {
                secondNameField.setStyle("");
            }

            if (!surnameValidation.isValid()) {
                statusLabel.setText(surnameValidation.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                surnameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hasErrors = true;
            } else {
                surnameField.setStyle("");
            }

            if (!nameValidation.isValid()) {
                if (hasErrors) {
                    statusLabel.setText(statusLabel.getText() + "\n" + nameValidation.getMessage());
                } else {
                    statusLabel.setText(nameValidation.getMessage());
                }
                statusLabel.setStyle("-fx-text-fill: red;");
                nameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hasErrors = true;
            } else {
                nameField.setStyle("");
            }

            if (specialty == null || specialty.trim().isEmpty()) {
                if (hasErrors) {
                    statusLabel.setText(statusLabel.getText() + "\nВыберите специализацию!");
                } else {
                    statusLabel.setText("Выберите специализацию!");
                }
                statusLabel.setStyle("-fx-text-fill: red;");
                specialtyComboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hasErrors = true;
            } else {
                specialtyComboBox.setStyle("");
            }

            if (hasErrors) {
                return;
            }

            try {
                // Создаем нового врача
                Doctor newDoctor = new Doctor(name, surname,
                        secondName.isEmpty() ? null : secondName,
                        0, // ID будет сгенерирован базой данных
                        specialty);

                DoctorDAO doctorDAO = new DoctorDAO();
                if (doctorDAO.addDoctor(newDoctor)) {
                    statusLabel.setText("Врач успешно добавлен!");
                    statusLabel.setStyle("-fx-text-fill: green;");

                    // Очистка полей
                    surnameField.clear();
                    nameField.clear();
                    secondNameField.clear();
                    specialtyComboBox.setValue(null);

                    // Закрываем через 1.5 секунды
                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(event -> formStage.close());
                    pause.play();
                } else {
                    statusLabel.setText("Ошибка при добавлении врача!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (PolyclinicException ex) {
                System.err.println("Ошибка поликлиники: " + ex.getMessage());
                ex.printStackTrace();
                statusLabel.setText("Ошибка: " + ex.getErrorCode().getDescription() +
                        "\n" + ex.getAdditionalInfo());
                statusLabel.setStyle("-fx-text-fill: red;");
            } catch (Exception ex) {
                System.err.println("Неизвестная ошибка: " + ex.getMessage());
                ex.printStackTrace();
                statusLabel.setText("Неизвестная ошибка: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> formStage.close());

        layout.getChildren().addAll(
                title,
                new Label("Фамилия:"), surnameField,
                new Label("Имя:"), nameField,
                new Label("Отчество:"), secondNameField,
                new Label("Специализация:"), specialtyComboBox,
                statusLabel,
                new HBox(10, saveBtn, cancelBtn)
        );

        Scene scene = new Scene(layout, 400, 500);
        formStage.setScene(scene);
        formStage.show();
    }

    //проверка шрифтов
    private void checkAvailableFonts() {
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        System.out.println("Доступные шрифты:");
        for (String font : fontNames) {
            if (font.matches(".*[А-Яа-я].*")) {
                System.out.println("  - " + font + " (поддерживает кириллицу)");
            }
        }
    }
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успешно");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public class MedicalCertificatePDF {

        public static void createCertificate(MedicalCertificate certificate,
                                             Patient patient,
                                             Doctor doctor,
                                             String filePath) {
            try {
                // 1. НАСТРОЙКА ШРИФТОВ ДЛЯ macOS
                PdfFont font = null;

                // Список возможных путей к шрифтам на Mac
                String[] possibleFontPaths = {
                        "/System/Library/Fonts/Supplemental/Arial.ttf",      // Arial
                        "/System/Library/Fonts/Supplemental/Arial Unicode.ttf", // Arial Unicode
                        "/Library/Fonts/Arial.ttf",                          // Дополнительный Arial
                        "/System/Library/Fonts/Helvetica.ttc",               // Helvetica
                        "/System/Library/Fonts/Times New Roman.ttf",         // Times New Roman
                        "/Library/Fonts/Microsoft/Arial.ttf"                 // Microsoft Arial
                };

                // Пробуем найти доступный шрифт
                for (String fontPath : possibleFontPaths) {
                    File fontFile = new File(fontPath);
                    if (fontFile.exists()) {
                        System.out.println("Используем шрифт: " + fontPath);
                        try {
                            // Просто создаем шрифт с двумя параметрами
                            font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
                            break;
                        } catch (Exception e) {
                            System.out.println("Не удалось загрузить шрифт: " + fontPath);
                            continue;
                        }
                    }
                }

                // Если шрифты не найдены, используем встроенный
                if (font == null) {
                    System.out.println("Предупреждение: системные шрифты не найдены, использую стандартный");
                    font = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
                }

                // 2. СОЗДАЕМ PDF ДОКУМЕНТ
                com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(filePath);
                com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);

                document.setFont(font);

                com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph("МЕДИЦИНСКАЯ СПРАВКА")
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(16);
                document.add(title);

                document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

                document.add(new com.itextpdf.layout.element.Paragraph("Номер справки: МС-" + certificate.getId()).setFont(font));

                document.add(new com.itextpdf.layout.element.Paragraph("Дата выдачи: " +
                        certificate.getIssueDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                        .setFont(font));

                document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

                com.itextpdf.layout.element.Paragraph patientInfo = new com.itextpdf.layout.element.Paragraph("ПАЦИЕНТ:")
                        .setBold()
                        .setFontSize(12)
                        .setFont(font);
                document.add(patientInfo);

                // Таблица с данными пациента
                com.itextpdf.layout.element.Table patientTable = new com.itextpdf.layout.element.Table(2);

                patientTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph("ФИО:").setFont(font)));
                patientTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(patient.getFullName()).setFont(font)));

                patientTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph("Номер полиса:").setFont(font)));
                patientTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(String.format("%06d", patient.getPolicy())).setFont(font)));

                patientTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph("Дата рождения:").setFont(font)));
                patientTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(
                                patient.getDateOfBirth().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                                .setFont(font)));

                document.add(patientTable);
                document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

                // 7. МЕДИЦИНСКАЯ ИНФОРМАЦИЯ
                com.itextpdf.layout.element.Paragraph medicalInfo = new com.itextpdf.layout.element.Paragraph("МЕДИЦИНСКАЯ ИНФОРМАЦИЯ:")
                        .setBold()
                        .setFontSize(12)
                        .setFont(font);
                document.add(medicalInfo);

                document.add(new com.itextpdf.layout.element.Paragraph("Период болезни: с " +
                        certificate.getStartDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                        " по " +
                        certificate.getEndDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                        .setFont(font));

                document.add(new com.itextpdf.layout.element.Paragraph("Диагноз: " + certificate.getDiagnosis())
                        .setFont(font));

                if (certificate.getRecommendations() != null &&
                        !certificate.getRecommendations().isEmpty()) {
                    document.add(new com.itextpdf.layout.element.Paragraph("Рекомендации: " + certificate.getRecommendations())
                            .setFont(font));
                }

                document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));

                // 8. ВРАЧ
                com.itextpdf.layout.element.Paragraph doctorInfo = new com.itextpdf.layout.element.Paragraph("ЛЕЧАЩИЙ ВРАЧ:")
                        .setBold()
                        .setFontSize(12)
                        .setFont(font);
                document.add(doctorInfo);

                document.add(new com.itextpdf.layout.element.Paragraph("ФИО врача: " + doctor.getFullName())
                        .setFont(font));
                document.add(new com.itextpdf.layout.element.Paragraph("Специализация: " + doctor.getSpecialty())
                        .setFont(font));

                document.add(new com.itextpdf.layout.element.Paragraph(" ").setFont(font));
                document.add(new com.itextpdf.layout.element.Paragraph("___________________________").setFont(font));
                document.add(new com.itextpdf.layout.element.Paragraph("Подпись врача").setFont(font));

                // 9. ЗАКРЫВАЕМ ДОКУМЕНТ
                document.close();

                System.out.println("PDF успешно создан: " + filePath);

            } catch (Exception e) {
                e.printStackTrace();

                // Если не получилось с iText, создаем простой текстовый файл
                createFallbackTextFile(certificate, patient, doctor, filePath);
            }
        }

        // Резервный метод на случай ошибки
        private static void createFallbackTextFile(MedicalCertificate certificate,
                                                   Patient patient,
                                                   Doctor doctor,
                                                   String filePath) {
            try {
                String textContent =
                        "================ МЕДИЦИНСКАЯ СПРАВКА ================\n" +
                                "Номер справки: МС-" + certificate.getId() + "\n" +
                                "Дата выдачи: " + certificate.getIssueDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n\n" +
                                "---------------- ПАЦИЕНТ ----------------\n" +
                                "ФИО: " + patient.getFullName() + "\n" +
                                "Номер полиса: " + String.format("%06d", patient.getPolicy()) + "\n" +
                                "Дата рождения: " + patient.getDateOfBirth().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n\n" +
                                "-------------- МЕДИЦИНСКАЯ ИНФОРМАЦИЯ --------------\n" +
                                "Период болезни: с " + certificate.getStartDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                                " по " + certificate.getEndDate().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n" +
                                "Диагноз: " + certificate.getDiagnosis() + "\n" +
                                (certificate.getRecommendations() != null && !certificate.getRecommendations().isEmpty() ?
                                        "Рекомендации: " + certificate.getRecommendations() + "\n" : "") + "\n" +
                                "----------------- ВРАЧ ------------------\n" +
                                "ФИО врача: " + doctor.getFullName() + "\n" +
                                "Специализация: " + doctor.getSpecialty() + "\n\n" +
                                "___________________________\n" +
                                "Подпись врача\n\n" +
                                "=========================================";

                String txtFilePath = filePath.replace(".pdf", ".txt");
                java.nio.file.Files.write(
                        java.nio.file.Paths.get(txtFilePath),
                        textContent.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                );

                System.out.println("Создан резервный текстовый файл: " + txtFilePath);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private void exportCertificateToPDF(MedicalCertificate certificate, Patient patient) {
        try {
            // Получаем данные врача
            DoctorDAO doctorDAO = new DoctorDAO();
            Doctor doctor = doctorDAO.getDoctorById(certificate.getDoctorId());

            if (doctor == null) {
                showError("Не удалось получить данные врача!");
                return;
            }

            // Выбираем место для сохранения
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить медицинскую справку");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF файлы", "*.pdf")
            );
            fileChooser.setInitialFileName(
                    "Медицинская_справка_" + patient.getSurname() + "_" +
                            certificate.getIssueDate().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".pdf"
            );

            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                // Создаем PDF с помощью iText
                MedicalCertificatePDF.createCertificate(certificate, patient, doctor, file.getAbsolutePath());

                showSuccess("Медицинская справка успешно сохранена: " + file.getAbsolutePath());

                // Открываем файл
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка при создании PDF: " + e.getMessage());
        }
    }
    // Простой тестовый метод для проверки шрифтов на Mac
    public static void testMacFonts() {
        try {
            // Популярные шрифты на Mac (проверьте наличие)
            String[] testFonts = {
                    "/System/Library/Fonts/Supplemental/Arial.ttf",
                    "/Library/Fonts/Arial.ttf",
                    "/System/Library/Fonts/Helvetica.ttc",
                    "/System/Library/Fonts/Times New Roman.ttf"
            };

            for (String fontPath : testFonts) {
                File fontFile = new File(fontPath);
                System.out.println(fontPath + " существует: " + fontFile.exists());

                if (fontFile.exists()) {
                    System.out.println("Размер файла: " + fontFile.length() + " байт");
                }
            }

            // Также можно посмотреть все доступные шрифты
            System.out.println("\nПоиск шрифтов в /System/Library/Fonts/");
            File fontDir = new File("/System/Library/Fonts/");
            if (fontDir.exists()) {
                File[] files = fontDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttf") ||
                        name.toLowerCase().endsWith(".ttc"));
                if (files != null) {
                    for (File f : files) {
                        System.out.println("Найден: " + f.getName());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Метод проверяет, есть ли у врача расписание на указанный день недели
    private boolean hasScheduleForDay(int doctorId, LocalDate date) {
        try {
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            List<Schedule> schedules = scheduleDAO.getScheduleByDoctor(doctorId);

            if (schedules.isEmpty()) {
                return false;
            }

            // Получаем день недели из даты
            DayOfWeek selectedDay = date.getDayOfWeek();

            // Проверяем, есть ли у врача расписание на этот день
            for (Schedule schedule : schedules) {
                if (schedule.getDayOfWeek() == selectedDay) {
                    return true;
                }
            }

            return false;

        } catch (PolyclinicException e) {
            System.err.println("Ошибка при проверке расписания: " + e.getMessage());
            return false;
        }
    }

    private ObservableList<String> getDiagnosesList() {
        return FXCollections.observableArrayList(
                "Аллергический ринит",
                "Ангина",
                "Артериальная гипертензия",
                "Астма бронхиальная",
                "Атеросклероз",
                "Бронхит острый",
                "Бронхит хронический",
                "Гастрит",
                "Гайморит",
                "Геморрой",
                "Гипертоническая болезнь",
                "Грипп",
                "Дерматит",
                "Диабет сахарный 2 типа",
                "Колит",
                "Конъюнктивит",
                "Ларингит",
                "Мигрень",
                "Ожирение",
                "Остеохондроз",
                "Отит",
                "Панкреатит",
                "Пневмония",
                "Простатит",
                "Радикулит",
                "Ринит",
                "Сердечная недостаточность",
                "Тонзиллит",
                "Фарингит",
                "Цистит"
        );
    }
}
