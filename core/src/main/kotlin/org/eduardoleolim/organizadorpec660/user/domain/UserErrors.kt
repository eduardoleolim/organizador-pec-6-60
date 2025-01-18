/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.user.domain

import java.util.*

sealed class UserError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidUserIdError(val id: String, override val cause: Throwable?) :
    UserError("The id <$id> is not a valid user id", cause)

class InvalidUserUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    UserError("The updatedAt <$updatedAt> is not a valid user update date", null)

class UserNotFoundError(val id: String) :
    UserError("The user with id <$id> was not found", null)
