package com.polyclinic.registry;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    private DatabaseManager dbManager;

    public ScheduleDAO() {
        this.dbManager = new DatabaseManager();
    }

    // Получить расписание врача
    public List<Schedule> getScheduleByDoctor(int doctorId) throws PolyclinicException {
        List<Schedule> schedules = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT * FROM schedules WHERE doctor_id = ? AND is_active = TRUE ORDER BY day_of_week, start_time";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, doctorId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Schedule schedule = new Schedule();
                schedule.setId(resultSet.getInt("id"));
                schedule.setDoctorId(resultSet.getInt("doctor_id"));

                int dayValue = resultSet.getInt("day_of_week");
                schedule.setDayOfWeek(DayOfWeek.of(dayValue));

                schedule.setStartTime(resultSet.getTime("start_time").toLocalTime());
                schedule.setEndTime(resultSet.getTime("end_time").toLocalTime());
                schedule.setRoomNumber(resultSet.getInt("room_number"));
                schedule.setActive(resultSet.getBoolean("is_active"));

                schedules.add(schedule);
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при загрузке расписания врача ID " + doctorId + ": " + e.getMessage(),
                    e
            );
        }

        return schedules;
    }

    // Добавить запись в расписание
    public boolean addSchedule(Schedule schedule) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "INSERT INTO schedules (doctor_id, day_of_week, start_time, end_time, room_number, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, schedule.getDoctorId());
            statement.setInt(2, schedule.getDayOfWeek().getValue());
            statement.setTime(3, Time.valueOf(schedule.getStartTime()));
            statement.setTime(4, Time.valueOf(schedule.getEndTime()));
            statement.setInt(5, schedule.getRoomNumber());
            statement.setBoolean(6, schedule.isActive());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                // Получаем сгенерированный ID
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    schedule.setId(generatedKeys.getInt(1));
                }
                return true;
            }

            return false;

        } catch (SQLException e) {
            // Проверяем, не дублируется ли запись
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("unique constraint")) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.SCHEDULE_CONFLICT,
                        "У врача уже есть расписание в это время",
                        e
                );
            }

            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при добавлении расписания: " + e.getMessage(),
                    e
            );
        }
    }

    // Удалить запись из расписания
    public boolean deleteSchedule(int scheduleId) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "DELETE FROM schedules WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, scheduleId);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted == 0) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.INVALID_DATA,
                        "Расписание с ID " + scheduleId + " не найдено"
                );
            }

            return true;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при удалении расписания: " + e.getMessage(),
                    e
            );
        }
    }

    // ========== МЕТОД С 4 ПАРАМЕТРАМИ (для добавления) ==========

    // Проверить конфликт расписания (обычная проверка) - 4 параметра
    public boolean hasScheduleConflict(int doctorId, DayOfWeek dayOfWeek,
                                       LocalTime startTime, LocalTime endTime) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT COUNT(*) FROM schedules WHERE doctor_id = ? AND day_of_week = ? " +
                    "AND is_active = TRUE " +
                    "AND NOT (end_time <= ? OR start_time >= ?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, doctorId);
            statement.setInt(2, dayOfWeek.getValue());
            statement.setTime(3, Time.valueOf(startTime));
            statement.setTime(4, Time.valueOf(endTime));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке конфликта расписания: " + e.getMessage(),
                    e
            );
        }
    }

    // Проверить занятость кабинета (обычная проверка) - 4 параметра
    public boolean isRoomOccupied(int roomNumber, DayOfWeek dayOfWeek,
                                  LocalTime startTime, LocalTime endTime) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT COUNT(*) FROM schedules WHERE room_number = ? AND day_of_week = ? " +
                    "AND NOT (end_time <= ? OR start_time >= ?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, roomNumber);
            statement.setInt(2, dayOfWeek.getValue());
            statement.setTime(3, Time.valueOf(startTime));
            statement.setTime(4, Time.valueOf(endTime));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке занятости кабинета: " + e.getMessage(),
                    e
            );
        }
    }

    // ========== МЕТОДЫ С 5 ПАРАМЕТРАМИ (для редактирования) ==========

    // Проверить конфликт расписания (исключая определенное расписание) - 5 параметров
    public boolean hasScheduleConflict(int doctorId, DayOfWeek dayOfWeek,
                                       LocalTime startTime, LocalTime endTime, int excludeScheduleId) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT COUNT(*) FROM schedules WHERE doctor_id = ? AND day_of_week = ? " +
                    "AND id != ? " +  // Исключаем текущую запись
                    "AND is_active = TRUE " +
                    "AND NOT (end_time <= ? OR start_time >= ?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, doctorId);
            statement.setInt(2, dayOfWeek.getValue());
            statement.setInt(3, excludeScheduleId);
            statement.setTime(4, Time.valueOf(startTime));
            statement.setTime(5, Time.valueOf(endTime));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке конфликта расписания: " + e.getMessage(),
                    e
            );
        }
    }

    // Проверить занятость кабинета другим врачом, исключая определенное расписание
    public boolean isRoomOccupied(int roomNumber, DayOfWeek dayOfWeek,
                                  LocalTime startTime, LocalTime endTime, int excludeScheduleId) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT COUNT(*) FROM schedules WHERE room_number = ? AND day_of_week = ? " +
                    "AND id != ? " +  // Исключаем текущую запись
                    "AND NOT (end_time <= ? OR start_time >= ?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, roomNumber);
            statement.setInt(2, dayOfWeek.getValue());
            statement.setInt(3, excludeScheduleId);
            statement.setTime(4, Time.valueOf(startTime));
            statement.setTime(5, Time.valueOf(endTime));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке занятости кабинета: " + e.getMessage(),
                    e
            );
        }
    }

    // Обновить расписание
    public boolean updateSchedule(Schedule schedule) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "UPDATE schedules SET day_of_week = ?, start_time = ?, end_time = ?, room_number = ? WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, schedule.getDayOfWeek().getValue());
            statement.setTime(2, Time.valueOf(schedule.getStartTime()));
            statement.setTime(3, Time.valueOf(schedule.getEndTime()));
            statement.setInt(4, schedule.getRoomNumber());
            statement.setInt(5, schedule.getId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.INVALID_DATA,
                        "Расписание с ID " + schedule.getId() + " не найдено для обновления"
                );
            }

            return true;

        } catch (SQLException e) {
            // Проверяем, не дублируется ли запись
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("unique constraint")) {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.SCHEDULE_CONFLICT,
                        "Конфликт расписания при обновлении",
                        e
                );
            }

            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при обновлении расписания: " + e.getMessage(),
                    e
            );
        }
    }

    // Получить расписание по ID
    public Schedule getScheduleById(int scheduleId) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT * FROM schedules WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, scheduleId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Schedule schedule = new Schedule();
                schedule.setId(resultSet.getInt("id"));
                schedule.setDoctorId(resultSet.getInt("doctor_id"));
                schedule.setDayOfWeek(DayOfWeek.of(resultSet.getInt("day_of_week")));
                schedule.setStartTime(resultSet.getTime("start_time").toLocalTime());
                schedule.setEndTime(resultSet.getTime("end_time").toLocalTime());
                schedule.setRoomNumber(resultSet.getInt("room_number"));
                schedule.setActive(resultSet.getBoolean("is_active"));
                return schedule;
            } else {
                throw new PolyclinicException(
                        PolyclinicException.ErrorCode.INVALID_DATA,
                        "Расписание с ID " + scheduleId + " не найдено"
                );
            }

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при получении расписания по ID: " + e.getMessage(),
                    e
            );
        }
    }

    // Проверить существование врача в расписании
    public boolean doctorHasSchedule(int doctorId) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "SELECT COUNT(*) FROM schedules WHERE doctor_id = ? AND is_active = TRUE";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, doctorId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при проверке расписания врача: " + e.getMessage(),
                    e
            );
        }
    }

    // Удалить все расписание врача
    public boolean deleteAllScheduleByDoctor(int doctorId) throws PolyclinicException {
        try {
            Connection connection = dbManager.getConnection();
            String sql = "DELETE FROM schedules WHERE doctor_id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, doctorId);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            throw new PolyclinicException(
                    PolyclinicException.ErrorCode.DATABASE_ERROR,
                    "Ошибка при удалении расписания врача: " + e.getMessage(),
                    e
            );
        }
    }
}