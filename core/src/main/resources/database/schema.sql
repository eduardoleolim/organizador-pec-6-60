-- Sqlite3 sqlite schema for the application

create TABLE IF NOT EXISTS federalEntity
(
    federalEntityId TEXT    NOT NULL,
    keyCode         TEXT    NOT NULL,
    name            TEXT    NOT NULL,
    createdAt       INTEGER NOT NULL,
    updatedAt       INTEGER,

    CONSTRAINT federalEntity_Pk PRIMARY KEY (federalEntityId),
    CONSTRAINT keyCode_Unq UNIQUE (keyCode)
);

create TABLE IF NOT EXISTS municipality
(
    municipalityId  TEXT    NOT NULL,
    keyCode         TEXT    NOT NULL,
    name            TEXT    NOT NULL,
    createdAt       INTEGER NOT NULL,
    updatedAt       INTEGER,
    federalEntityId TEXT    NOT NULL,

    CONSTRAINT municipality_Pk PRIMARY KEY (municipalityId),
    CONSTRAINT keyCode_federalEntityId_Unq UNIQUE (keyCode, federalEntityId),
    CONSTRAINT federalEntityId_Fk FOREIGN KEY (federalEntityId) REFERENCES federalEntity (federalEntityId)
);

create TABLE IF NOT EXISTS instrumentType
(
    instrumentTypeId TEXT    NOT NULL,
    name             TEXT    NOT NULL,
    createdAt        INTEGER NOT NULL,
    updatedAt        INTEGER,

    CONSTRAINT instrumentType_Pk PRIMARY KEY (instrumentTypeId)
);

create TABLE IF NOT EXISTS statisticType
(
    statisticTypeId TEXT    NOT NULL,
    keyCode         TEXT    NOT NULL,
    name            TEXT    NOT NULL,
    createdAt       INTEGER NOT NULL,
    updatedAt       INTEGER,

    CONSTRAINT statisticType_Pk PRIMARY KEY (statisticTypeId),
    CONSTRAINT keyCode_Unq UNIQUE (keyCode)
);

create TABLE IF NOT EXISTS statisticType_instrumentType
(
    statisticTypeId  TEXT NOT NULL,
    instrumentTypeId TEXT NOT NULL,

    CONSTRAINT statisticType_instrumentType_PK PRIMARY KEY (statisticTypeId, instrumentTypeId),
    CONSTRAINT idStatisticType_Fk FOREIGN KEY (statisticTypeId) REFERENCES statisticType (statisticTypeId),
    CONSTRAINT idInstrumentType_Fk FOREIGN KEY (instrumentTypeId) REFERENCES instrumentType (instrumentTypeId)
);

create TABLE IF NOT EXISTS instrument
(
    instrumentId     TEXT    NOT NULL,
    statisticYear    INTEGER NOT NULL,
    statisticMonth   INTEGER NOT NULL,
    consecutive      TEXT    NOT NULL,
    saved            INTEGER NOT NULL,
    buffer           BLOB    NOT NULL,
    createdAt        INTEGER NOT NULL,
    updatedAt        INTEGER,
    instrumentTypeId TEXT    NOT NULL,
    statisticTypeId  TEXT    NOT NULL,
    municipalityId   TEXT    NOT NULL,

    CONSTRAINT instrument_Pk PRIMARY KEY (instrumentId),
    CONSTRAINT instrument_Unq UNIQUE (statisticYear, statisticMonth, consecutive, instrumentTypeId, statisticTypeId,
                                      municipalityId),
    CONSTRAINT instrumentTypeId_Fk FOREIGN KEY (instrumentTypeId) REFERENCES instrumentType (instrumentTypeId),
    CONSTRAINT statisticTypeId_Fk FOREIGN KEY (statisticTypeId) REFERENCES statisticType (statisticTypeId),
    CONSTRAINT municipalityId_Fk FOREIGN KEY (municipalityId) REFERENCES municipality (municipalityId)
);

create TABLE IF NOT EXISTS role
(
    roleId TEXT NOT NULL,
    name   TEXT NOT NULL,

    CONSTRAINT roleId_Pk PRIMARY KEY (roleId)
);

create TABLE IF NOT EXISTS user
(
    userId    TEXT NOT NULL,
    firstname TEXT NOT NULL,
    lastname  TEXT,
    roleId    TEXT NOT NULL,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER,

    CONSTRAINT user_Pk PRIMARY KEY (userId),
    CONSTRAINT roleId_Fk FOREIGN KEY (roleId) REFERENCES role (roleId)
);

create TABLE IF NOT EXISTS credentials
(
    userId   TEXT NOT NULL,
    email    TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,

    CONSTRAINT password_Unq UNIQUE (email),
    CONSTRAINT username_Unq UNIQUE (username),
    CONSTRAINT userId_FK FOREIGN KEY (userId) REFERENCES user (userId)
);
