/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
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
package org.neo4j.kernel.impl.proc;

import org.junit.Test;

import org.neo4j.kernel.configuration.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.neo4j.helpers.collection.MapUtil.genericMap;

public class ProcedureAllowedConfigTest
{
    private static final String[] EMPTY = new String[]{};

    private static String[] arrayOf( String... values )
    {
        return values;
    }

    @Test
    public void shouldHaveEmptyDefaultConfigs()
    {
        Config config = Config.defaults();
        ProcedureAllowedConfig procConfig = new ProcedureAllowedConfig( config );
        assertThat( procConfig.rolesFor( "x" ), equalTo( EMPTY ) );
    }

    @Test
    public void shouldHaveConfigsWithDefaultProcedureAllowed()
    {
        Config config = Config.defaults()
                .with( genericMap( ProcedureAllowedConfig.PROC_ALLOWED_SETTING_DEFAULT_NAME, "role1" ) );
        ProcedureAllowedConfig procConfig = new ProcedureAllowedConfig( config );
        assertThat( procConfig.rolesFor( "x" ), equalTo( arrayOf( "role1" ) ) );
    }

    @Test
    public void shouldHaveConfigsWithExactMatchProcedureAllowed()
    {
        Config config = Config.defaults()
                .with( genericMap( ProcedureAllowedConfig.PROC_ALLOWED_SETTING_DEFAULT_NAME, "role1",
                        ProcedureAllowedConfig.PROC_ALLOWED_SETTING_ROLES, "xyz:anotherRole" ) );
        ProcedureAllowedConfig procConfig = new ProcedureAllowedConfig( config );
        assertThat( procConfig.rolesFor( "xyz" ), equalTo( arrayOf( "anotherRole" ) ) );
        assertThat( procConfig.rolesFor( "abc" ), equalTo( arrayOf( "role1" ) ) );
    }

    @Test
    public void shouldHaveConfigsWithWildcardProcedureAllowed()
    {
        Config config = Config.defaults()
                .with( genericMap( ProcedureAllowedConfig.PROC_ALLOWED_SETTING_DEFAULT_NAME, "role1",
                        ProcedureAllowedConfig.PROC_ALLOWED_SETTING_ROLES, "xyz*:anotherRole" ) );
        ProcedureAllowedConfig procConfig = new ProcedureAllowedConfig( config );
        assertThat( procConfig.rolesFor( "xyzabc" ), equalTo( arrayOf( "anotherRole" ) ) );
        assertThat( procConfig.rolesFor( "abcxyz" ), equalTo( arrayOf( "role1" ) ) );
    }

    @Test
    public void shouldHaveConfigsWithWildcardProcedureAllowedAndNoDefault()
    {
        Config config = Config.defaults()
                .with( genericMap( ProcedureAllowedConfig.PROC_ALLOWED_SETTING_ROLES, "xyz*:anotherRole" ) );
        ProcedureAllowedConfig procConfig = new ProcedureAllowedConfig( config );
        assertThat( procConfig.rolesFor( "xyzabc" ), equalTo( arrayOf( "anotherRole" ) ) );
        assertThat( procConfig.rolesFor( "abcxyz" ), equalTo( EMPTY ) );
    }

    @Test
    public void shouldHaveConfigsWithMultipleWildcardProcedureAllowedAndNoDefault()
    {
        Config config = Config.defaults()
                .with( genericMap(
                        ProcedureAllowedConfig.PROC_ALLOWED_SETTING_ROLES,
                        "apoc.convert.*:reader,apoc.load.json:writer,apoc.trigger.add:TriggerHappy"
                ) );
        ProcedureAllowedConfig procConfig = new ProcedureAllowedConfig( config );
        assertThat( procConfig.rolesFor( "xyz" ), equalTo( EMPTY ) );
        assertThat( procConfig.rolesFor( "apoc.convert.xml" ), equalTo( arrayOf( "reader" ) ) );
        assertThat( procConfig.rolesFor( "apoc.convert.json" ), equalTo( arrayOf( "reader" ) ) );
        assertThat( procConfig.rolesFor( "apoc.load.xml" ), equalTo( EMPTY ) );
        assertThat( procConfig.rolesFor( "apoc.load.json" ), equalTo( arrayOf( "writer" ) ) );
        assertThat( procConfig.rolesFor( "apoc.trigger.add" ), equalTo( arrayOf( "TriggerHappy" ) ) );
        assertThat( procConfig.rolesFor( "apoc.convert-json" ), equalTo( EMPTY ) );
        assertThat( procConfig.rolesFor( "apoc.load-xml" ), equalTo( EMPTY ) );
        assertThat( procConfig.rolesFor( "apoc.load-json" ), equalTo( EMPTY ) );
        assertThat( procConfig.rolesFor( "apoc.trigger-add" ), equalTo( EMPTY ) );
    }
}
