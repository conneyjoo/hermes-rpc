package com.xhtech.hermes.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import sun.misc.Launcher;

import java.io.File;
import java.io.FileReader;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Manifestor {

    private static final Logger logger = LoggerFactory.getLogger(Manifestor.class);

    private String version;

    public Manifestor() {
    }

    public void load() {
        Manifest manifest;
        Model model;

        if ((manifest = getManifest()) != null) {
            new ManifestSetup(manifest).setup(this);
        } else if ((model = getPom()) != null) {
            new PomSetup(model).setup(this);
        }
    }

    private Manifest getManifest() {
        JarFile jarfile = null;

        try {
            jarfile = new JarFile(getAppHome());
            return jarfile.getManifest();
        } catch (Exception e) {
            logger.info("read version of manifest failed");
        } finally {
            IOUtils.closeQuietly(jarfile);
        }

        return null;
    }

    private Model getPom() {
        File file = new File(getCodeSourcePath(), "pom.xml");
        FileReader reader = null;

        try {
            reader = new FileReader(file);
            return new MavenXpp3Reader().read(reader);
        } catch (Exception e) {
            logger.info("read version of pom failed");
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return null;
    }

    private String getAppHome() {
        return new ApplicationHome().getSource().getPath();
    }

    private String getLuanchPath() {
        return Launcher.getLauncher().getClassLoader().getResource("").getPath();
    }

    private String getCodeSourcePath() {
        return StringUtils.substringBefore(getLuanchPath(), "/target/classes");
    }

    public static Logger getLogger() {
        return logger;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    interface Setup {

        void setup(Manifestor manifestor);
    }

    class ManifestSetup implements Setup {

        private Manifest manifest;

        public ManifestSetup(Manifest manifest) {
            this.manifest = manifest;
        }

        @Override
        public void setup(Manifestor manifestor) {
            manifestor.version = manifest.getMainAttributes().getValue("Implementation-Version");
        }
    }

    class PomSetup implements Setup {

        private Model model;

        public PomSetup(Model model) {
            this.model = model;
        }

        @Override
        public void setup(Manifestor manifestor) {
            manifestor.version = model.getVersion();
        }
    }
}