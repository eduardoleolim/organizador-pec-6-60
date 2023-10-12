module org.eduardoleolim.organizadorpec660.shared {
    requires kotlin.stdlib;

    exports org.eduardoleolim.organizadorpec660.shared.domain;
    exports org.eduardoleolim.organizadorpec660.shared.domain.bus.command;
    exports org.eduardoleolim.organizadorpec660.shared.domain.bus.query;
    exports org.eduardoleolim.organizadorpec660.shared.domain.criteria;
}
