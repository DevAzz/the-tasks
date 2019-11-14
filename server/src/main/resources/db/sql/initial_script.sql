CREATE SCHEMA IF NOT EXISTS `tasksdb` DEFAULT CHARACTER SET utf8 ;
DROP TABLE IF EXISTS `tasksdb`.`default_tasks`;

CREATE TABLE IF NOT EXISTS `tasksdb`.`default_tasks` (
  `id` BIGINT(20) NOT NULL,
  `name` TEXT NOT NULL,
  `note` TEXT NULL DEFAULT NULL,
  `start_time` VARCHAR(5) NOT NULL,
  `end_time` VARCHAR(5) NOT NULL,
  `subordination_id` BIGINT(20) NOT NULL,
  `author_id` BIGINT(20) NOT NULL,
  `next_day` INT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksdb`.`events`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksdb`.`events` ;

CREATE TABLE IF NOT EXISTS `tasksdb`.`events` (
  `id` BIGINT(20) NOT NULL,
  `name` TEXT NULL DEFAULT NULL,
  `date` DATETIME NULL DEFAULT NULL,
  `event_type` VARCHAR(45) NULL DEFAULT NULL,
  `author_id` BIGINT(20) NULL DEFAULT NULL,
  `tasks_task_id` BIGINT(20) NULL DEFAULT NULL,
  `executor_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idevents_UNIQUE` (`id` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksdb`.`help`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksdb`.`help` ;

CREATE TABLE IF NOT EXISTS `tasksdb`.`help` (
  `id` BIGINT(20) NOT NULL,
  `help_item_name` VARCHAR(45) NOT NULL,
  `help_item_text` TEXT NULL DEFAULT NULL,
  `role` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksdb`.`role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksdb`.`role` ;

CREATE TABLE IF NOT EXISTS `tasksdb`.`role` (
  `id` BIGINT(20) NOT NULL,
  `name_role` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksdb`.`subordination_element`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksdb`.`subordination_element` ;

CREATE TABLE IF NOT EXISTS `tasksdb`.`subordination_element` (
  `id` BIGINT(20) NOT NULL DEFAULT '-1',
  `name` VARCHAR(45) NULL DEFAULT NULL,
  `role_id` BIGINT(20) NULL DEFAULT NULL,
  `rootElement` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksdb`.`task_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksdb`.`task_history` ;

CREATE TABLE IF NOT EXISTS `tasksdb`.`task_history` (
  `id` BIGINT(20) NOT NULL,
  `task_id` BIGINT(20) NULL DEFAULT NULL,
  `type` VARCHAR(100) NULL DEFAULT NULL,
  `title` VARCHAR(100) NULL DEFAULT NULL,
  `text` TEXT NULL DEFAULT NULL,
  `actor_id` BIGINT(20) NULL DEFAULT NULL,
  `date` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksdb`.`tasks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksdb`.`tasks` ;

CREATE TABLE IF NOT EXISTS `tasksdb`.`tasks` (
  `id` BIGINT(20) NOT NULL,
  `name` TEXT NULL DEFAULT NULL,
  `note` TEXT NULL DEFAULT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `status` VARCHAR(45) NULL DEFAULT NULL,
  `priority` VARCHAR(45) NULL DEFAULT NULL,
  `start_date` DATETIME NULL DEFAULT NULL,
  `end_date` DATETIME NULL DEFAULT NULL,
  `author_id` BIGINT(20) NOT NULL,
  `executor_id` BIGINT(20) NULL DEFAULT NULL,
  `document` LONGBLOB NULL DEFAULT NULL,
  `documenName` VARCHAR(300) NULL DEFAULT NULL,
  `task_type` VARCHAR(45) NOT NULL,
  `cycle_type` VARCHAR(45) NULL DEFAULT NULL,
  `cycle_time` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_task_subordination_element1_idx` (`author_id` ASC),
  CONSTRAINT `fk_task_subordination_element_author`
    FOREIGN KEY (`author_id`)
    REFERENCES `tasksdb`.`subordination_element` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksdb`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksdb`.`user` ;

CREATE TABLE IF NOT EXISTS `tasksdb`.`user` (
  `iduser` BIGINT(20) NOT NULL,
  `login` VARCHAR(45) NULL DEFAULT NULL,
  `password` VARCHAR(45) NULL DEFAULT NULL,
  `idrole` BIGINT(20) NOT NULL,
  `sub_element_suid` BIGINT(20) NOT NULL,
  `name` VARCHAR(300) NULL DEFAULT NULL,
  `military_rank` VARCHAR(300) NULL DEFAULT NULL,
  `position` VARCHAR(300) NULL DEFAULT NULL,
  `image` LONGBLOB NULL DEFAULT NULL,
  `online` INT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`iduser`),
  INDEX `fk_user_role_idx` (`idrole` ASC),
  INDEX `fk_user_subordination_element1_idx` (`sub_element_suid` ASC),
  CONSTRAINT `fk_user_role`
    FOREIGN KEY (`idrole`)
    REFERENCES `tasksdb`.`role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_subordination_element1`
    FOREIGN KEY (`sub_element_suid`)
    REFERENCES `tasksdb`.`subordination_element` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
