/**
 * The marshalling API.  Marshalling is done by use of {@link org.jboss.marshalling.Marshaller Marshaller} and {@link org.jboss.marshalling.Unmarshaller Unmarshaller} instances.  These
 * instances are acquired from a {@link org.jboss.marshalling.MarshallerFactory MarshallerFactory} using a {@link MarshallingConfiguration Configuration} to configure them.  The
 * default implementation is the River protocol, usable by way of the {@link org.jboss.marshalling.river} package.
 */
package org.jboss.marshalling;

/*
 * #%L
 * JBoss Marshalling API (Patch for JBossAS 7.1.1.Final)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2014 Leadware
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
