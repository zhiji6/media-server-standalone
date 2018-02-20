/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2018, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.restcomm.media.server.standalone.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.media.server.standalone.bootstrap.ioc.AsrModule;
import org.restcomm.media.server.standalone.bootstrap.ioc.CoreModule;
import org.restcomm.media.server.standalone.bootstrap.ioc.MediaModule;
import org.restcomm.media.server.standalone.bootstrap.ioc.MgcpModule;
import org.restcomm.media.server.standalone.configuration.loader.ConfigurationLoader;
import org.restcomm.media.server.standalone.configuration.loader.XmlConfigurationLoader;
import org.restcomm.media.server.standalone.configuration.MediaServerConfiguration;
import org.restcomm.media.core.resource.vad.VoiceActivityDetectorProvider;
import org.restcomm.media.plugin.vad.spring.NoiseThresholdDetectorSpringProvider;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.guice.module.SpringModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.restcomm.media.spi.MediaServer;

/**
 * Bootstrapper that reads from a configuration file and initializes the Media Server.
 *
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 */
public class GuiceBootstrapper implements Bootstrapper {

    @Configuration
    public static class SpringBootConfig {
        @Bean
        public VoiceActivityDetectorProvider voiceActivityDetectorProvider() {
            return new NoiseThresholdDetectorSpringProvider(15);
        }
    }

    private static final Logger log = LogManager.getLogger(GuiceBootstrapper.class);
    private final String filepath;
    private final ConfigurationLoader configurationLoader;
    private MediaServer mediaServer;

    public GuiceBootstrapper(String filepath) {
        this.filepath = filepath;
        this.configurationLoader = new XmlConfigurationLoader();
    }

    public void deploy() throws Exception {
        MediaServerConfiguration conf = configurationLoader.load(this.filepath);
        Injector injector = Guice.createInjector(new CoreModule(conf), new MediaModule(), new MgcpModule(), new AsrModule(),
            new SpringModule(new AnnotationConfigApplicationContext(SpringBootConfig.class)));
        this.mediaServer = injector.getInstance(StandaloneMediaServer.class);
        this.mediaServer.start();
    }

    public void undeploy() {
        if (mediaServer != null) {
            this.mediaServer.stop();
        }
    }

}
