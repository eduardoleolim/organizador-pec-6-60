module org.eduardoleolim.organizadorpec660.core {
    requires kotlin.stdlib;
    requires ktorm.core;
    requires ktorm.support.sqlite;
    requires org.xerial.sqlitejdbc;

    opens database;

    exports org.eduardoleolim.organizadorpec660.core.auth.application;
    exports org.eduardoleolim.organizadorpec660.core.auth.application.authenticate;
    exports org.eduardoleolim.organizadorpec660.core.federalEntity.application;
    exports org.eduardoleolim.organizadorpec660.core.federalEntity.application.create;
    exports org.eduardoleolim.organizadorpec660.core.federalEntity.application.delete;
    exports org.eduardoleolim.organizadorpec660.core.federalEntity.application.search;
    exports org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchById;
    exports org.eduardoleolim.organizadorpec660.core.federalEntity.application.searchByTerm;
    exports org.eduardoleolim.organizadorpec660.core.federalEntity.application.update;
    exports org.eduardoleolim.organizadorpec660.core.instrumentType.application;
    exports org.eduardoleolim.organizadorpec660.core.instrumentType.application.create;
    exports org.eduardoleolim.organizadorpec660.core.instrumentType.application.delete;
    exports org.eduardoleolim.organizadorpec660.core.instrumentType.application.search;
    exports org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchById;
    exports org.eduardoleolim.organizadorpec660.core.instrumentType.application.searchByTerm;
    exports org.eduardoleolim.organizadorpec660.core.instrumentType.application.update;
    exports org.eduardoleolim.organizadorpec660.core.municipality.application;
    exports org.eduardoleolim.organizadorpec660.core.municipality.application.create;
    exports org.eduardoleolim.organizadorpec660.core.municipality.application.delete;
    exports org.eduardoleolim.organizadorpec660.core.municipality.application.search;
    exports org.eduardoleolim.organizadorpec660.core.municipality.application.searchById;
    exports org.eduardoleolim.organizadorpec660.core.municipality.application.searchByTerm;
    exports org.eduardoleolim.organizadorpec660.core.municipality.application.update;
    exports org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus;
    exports org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models;
    exports org.eduardoleolim.organizadorpec660.core.shared.domain;
    exports org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command;
    exports org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query;
    exports org.eduardoleolim.organizadorpec660.core.shared.domain.criteria;
}
