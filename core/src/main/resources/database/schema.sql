-- Sqlite3 sqlite schema for the application

create table if not exists federalEntity
(
    federalEntityId text    not null,
    keyCode         text    not null,
    name            text    not null,
    createdAt       integer not null,
    updatedAt       integer,

    constraint federalEntity_Pk primary key (federalEntityId),
    constraint keyCode_Unq unique (keyCode)
);

create table if not exists municipality
(
    municipalityId  text    not null,
    keyCode         text    not null,
    name            text    not null,
    createdAt       integer not null,
    updatedAt       integer,
    federalEntityId text    not null,

    constraint municipality_Pk primary key (municipalityId),
    constraint keyCode_federalEntityId_Unq unique (keyCode, federalEntityId),
    constraint federalEntityId_Fk foreign key (federalEntityId) references federalEntity (federalEntityId)
);

create table if not exists statisticType
(
    statisticTypeId text    not null,
    keyCode         text    not null,
    name            text    not null,
    createdAt       integer not null,
    updatedAt       integer,

    constraint statisticType_Pk primary key (statisticTypeId),
    constraint keyCode_Unq unique (keyCode)
);

create table if not exists agency
(
    agencyId        text    not null,
    name            text    not null,
    consecutive     integer not null,
    municipalityId  text    not null,
    createdAt       integer not null,
    updatedAt       integer,

    constraint agencyId_Pk primary key (agencyId),
    constraint municipalityId_Fk foreign key (municipalityId) references municipality (municipalityId),
    constraint consecutive_Unq unique (consecutive, municipalityId)
);

create table if not exists statisticType_agency
(
    agencyId        text not null,
    statisticTypeId text not null,

    constraint statisticType_agency_Unq unique (agencyId, statisticTypeId),
    constraint agencyId_Fk foreign key (agencyId) references agency (agencyId),
    constraint statisticTypeId_Fk foreign key (statisticTypeId) references statisticType (statisticTypeId)
);

create table if not exists instrumentFile
(
    instrumentFileId text    not null,
    content          blob    not null,

    constraint instrumentFile_Pk primary key (instrumentFileId)
);

create table if not exists instrument
(
    instrumentId     text    not null,
    statisticYear    integer not null,
    statisticMonth   integer not null,
    consecutive      text    not null,
    saved            integer not null,
    createdAt        integer not null,
    updatedAt        integer,
    statisticTypeId  text    not null,
    municipalityId   text    not null,
    instrumentFileId text    not null,

    constraint instrument_Pk primary key (instrumentId),
    constraint instrument_Unq unique (statisticYear, statisticMonth, consecutive, statisticTypeId, municipalityId),
    constraint statisticTypeId_Fk foreign key (statisticTypeId) references statisticType (statisticTypeId),
    constraint municipalityId_Fk foreign key (municipalityId) references municipality (municipalityId),
    constraint instrumentFileId_Fk foreign key (instrumentFileId) references instrumentFile (instrumentFileId)
);

create table if not exists role
(
    roleId text not null,
    name   text not null,

    constraint roleId_Pk primary key (roleId)
);

create table if not exists user
(
    userId    text not null,
    firstname text not null,
    lastname  text,
    roleId    text not null,
    createdAt integer not null,
    updatedAt integer,

    constraint user_Pk primary key (userId),
    constraint roleId_Fk foreign key (roleId) references role (roleId)
);

create table if not exists credentials
(
    userId   text not null,
    email    text not null,
    username text not null,
    password text not null,

    constraint password_Unq unique (email),
    constraint username_Unq unique (username),
    constraint userId_FK foreign key (userId) references user (userId)
);
