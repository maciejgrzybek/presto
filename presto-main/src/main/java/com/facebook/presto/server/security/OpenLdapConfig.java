/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.server.security;

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;

import javax.validation.constraints.NotNull;

public class OpenLdapConfig
{
    private String userBaseDistinguishedName;
    private String groupDistinguishedName;
    private String userObjectClass;

    @NotNull
    public String getUserBaseDistinguishedName()
    {
        return userBaseDistinguishedName;
    }

    @Config("authentication.ldap.user-base-dn")
    @ConfigDescription("Base distinguished name of the user")
    public OpenLdapConfig setUserBaseDistinguishedName(String userBaseDistinguishedName)
    {
        this.userBaseDistinguishedName = userBaseDistinguishedName;
        return this;
    }

    public String getGroupDistinguishedName()
    {
        return groupDistinguishedName;
    }

    @Config("authentication.ldap.group-dn")
    @ConfigDescription("Entire distinguished name of the group")
    public OpenLdapConfig setGroupDistinguishedName(String groupDistinguishedName)
    {
        this.groupDistinguishedName = groupDistinguishedName;
        return this;
    }

    public String getUserObjectClass()
    {
        return userObjectClass;
    }

    @Config("authentication.ldap.user-object-class")
    @ConfigDescription("LDAP object class the user implements")
    public OpenLdapConfig setUserObjectClass(String userObjectClass)
    {
        this.userObjectClass = userObjectClass;
        return this;
    }
}
