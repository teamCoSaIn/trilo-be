DROP TABLE IF EXISTS `trilo_db_test`.`schedules`;
DROP TABLE IF EXISTS `trilo_db_test`.`days`;
DROP TABLE IF EXISTS `trilo_db_test`.`trip`;
DROP TABLE IF EXISTS `trilo_db_test`.`users`;

CREATE TABLE IF NOT EXISTS `trilo_db_test`.`users` (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    nick_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    auth_provider VARCHAR(20) NOT NULL,
    user_role VARCHAR(255) NOT NULL,
    my_page_image_file_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS `trilo_db_test`.`trip` (
    trip_id BIGINT NOT NULL AUTO_INCREMENT,
    tripper_id BIGINT NOT NULL,
    trip_title VARCHAR(20) NOT NULL,
    trip_status VARCHAR(20) NOT NULL,
    start_date DATE,
    end_date DATE,
    trip_image_file_name VARCHAR(255),
    PRIMARY KEY (trip_id)
);

CREATE TABLE IF NOT EXISTS `trilo_db_test`.`days` (
    day_id BIGINT NOT NULL AUTO_INCREMENT,
    trip_id BIGINT NOT NULL,
    trip_date DATE NOT NULL,
    day_color VARCHAR(20) NOT NULL,
    PRIMARY KEY (day_id)
);

CREATE TABLE IF NOT EXISTS `trilo_db_test`.`schedules` (
    schedule_id BIGINT NOT NULL AUTO_INCREMENT,
    trip_id BIGINT NOT NULL,
    day_id BIGINT,
    schedule_index BIGINT NOT NULL,
    schedule_title VARCHAR(20) NOT NULL,
    schedule_content TEXT NOT NULL,
    place_id VARCHAR(255),
    place_name VARCHAR(255),
    place_latitude FLOAT(53) NOT NULL,
    place_longitude FLOAT(53) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    PRIMARY KEY (schedule_id)
);

ALTER TABLE `trilo_db_test`.`trip`
ADD FOREIGN KEY (tripper_id) REFERENCES `trilo_db_test`.`users` (user_id);

ALTER TABLE `trilo_db_test`.`days`
ADD FOREIGN KEY (trip_id) REFERENCES `trilo_db_test`.`trip` (trip_id);

ALTER TABLE `trilo_db_test`.`schedules`
ADD FOREIGN KEY (trip_id) REFERENCES `trilo_db_test`.`trip` (trip_id);

ALTER TABLE `trilo_db_test`.`schedules`
ADD FOREIGN KEY (day_id) REFERENCES `trilo_db_test`.`days` (day_id);
