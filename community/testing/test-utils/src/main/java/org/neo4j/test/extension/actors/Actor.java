/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
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
package org.neo4j.test.extension.actors;

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Actor
{
    <T> Future<T> submit( Callable<T> callable );

    <T> Future<T> submit( Runnable runnable, T result );

    Future<Void> submit( Runnable runnable );

    void untilWaiting() throws InterruptedException;

    void untilWaitingIn( Executable constructorOrMethod ) throws InterruptedException;

    void untilWaitingIn( String methodName ) throws InterruptedException;

    void untilThreadState( Thread.State... states );

    void interrupt();
}