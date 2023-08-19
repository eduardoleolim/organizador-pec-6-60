package org.eduardoleolim.core.federalEntity.application

import org.eduardoleolim.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.shared.domain.bus.query.Response

class FederalEntityResponse(val id: String, val keyCode: String, val name: String) : Response {
    override fun toString(): String {
        return "FederalEntityResponse(id='$id', keyCode='$keyCode', name='$name')"
    }

    companion object {
        fun fromAggregate(federalEntity: FederalEntity): FederalEntityResponse {
            return FederalEntityResponse(federalEntity.id().toString(), federalEntity.keyCode(), federalEntity.name())
        }
    }
}
