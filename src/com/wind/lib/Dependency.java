package com.wind.lib;

import com.wind.action.util.AssertionUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * maven依赖信息
 *
 * @author ：zhuYi
 * @date ：Created in 2022/9/3 17:29
 */

public class Dependency {
    String version;
    String artifactId;
    String groupId;
    String type;
    File pom, jar, aar;
    String mavenCoordinate;
    String key;
    String exclude;
    List<Configure> configureList;
    List<String> classList;

    public Dependency(File file) {
        version = file.getParentFile().getParentFile().getName();
        artifactId = file.getParentFile().getParentFile().getParentFile().getName();
        groupId = file.getParentFile().getParentFile().getParentFile().getParentFile().getName();
        mavenCoordinate = String.format("implementation '%s:%s:%s'", groupId, artifactId, version);
        key = artifactId + "-" + version;
        exclude = artifactId + ":" + version;
        setFile(file);
    }

    public List<String> getClassList() {
        return classList;
    }

    public void setClassList(List<String> classList) {
        this.classList = classList;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setFile(File file) {
        if (file.getName().endsWith(".pom")) {
            pom = file;
        }
        if (file.getName().endsWith(".jar")) {
            jar = file;
        }
        if (file.getName().endsWith(".aar")) {
            aar = file;
        }
    }

    public String getExclude() {
        return exclude;
    }

    public String getVersion() {
        return version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getKey() {
        return key;
    }

    public void setJar(File jar) {
        this.jar = jar;
    }

    //    implementation 'com.android.support:appcompat-v7:26.0.0'
    public String getMavenCoordinate() {
        return mavenCoordinate;
    }

    public List<Configure> getConfigureList() {
        return parsePom();
    }

    //判断是否是一个完整的依赖
    public boolean isValid() {
        if (AssertionUtil.notEmpty(type)) {
            if (type.equals("aar")) {
                return aar != null;
            }
            if (type.equals("jar")) {
                return jar != null;
            }
            return false;
        } else {
            return jar != null || aar != null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof File) {
            File file = (File) o;
            String version = file.getParentFile().getParentFile().getName();
            String artifactId = file.getParentFile().getParentFile().getParentFile().getName();
            String groupId = file.getParentFile().getParentFile().getParentFile().getParentFile().getName();
            String mavenCoordinate = String.format("implementation '%s:%s:%s'", groupId, artifactId, version);
            return this.mavenCoordinate.equals(mavenCoordinate);
        }
        return false;
    }

    public File getJarAar() {
        if (aar != null) {
            return aar;
        }
        return jar;
    }

    private List<Configure> parsePom() {
        if (AssertionUtil.notEmpty(configureList)) {
            return configureList;
        }
        if (pom == null) {
            return null;
        }
        try {
            //1.创建DocumentBuilderFactory对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.parse(pom.getAbsoluteFile());
            NodeList list = d.getElementsByTagName("dependency");
            NodeList packaging = d.getElementsByTagName("packaging");
            if (AssertionUtil.notEmpty(packaging)) {
                type = packaging.item(0).getTextContent();
            }
            return configureList = parse(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Configure> parse(NodeList list) {
        List<Configure> configureList = new ArrayList<>();
        if (AssertionUtil.notEmpty(list)) {
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                configureList.add(parseChild(node.getChildNodes()));
            }
        }
        return configureList;
    }

    private Configure parseChild(NodeList list) {
        if (AssertionUtil.notEmpty(list)) {
            String groupId = null, artifactId = null, version = null, type = null, scope = null;
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                String name = node.getNodeName();
                if (!name.contains("#text")) {
                    if (name.equals("groupId")) {
                        groupId = node.getTextContent();
                    }
                    if (name.equals("artifactId")) {
                        artifactId = node.getTextContent();
                    }
                    if (name.equals("version")) {
                        version = node.getTextContent().replace("]", "").replace("[", "");
                    }
                    if (name.equals("scope")) {
                        scope = node.getTextContent();
                    }
                    if (name.equals("type")) {
                        type = node.getTextContent();
                    }
                }
            }
            Configure configure = new Configure();
            configure.key = artifactId + "-" + version;
            configure.type = type;
            configure.scope = scope;
            configure.exclude = artifactId + ":" + version;
            configure.groupId = groupId;
            configure.artifactId = artifactId;
            configure.version=version;
            configure.mavenCoordinate = String.format("implementation '%s:%s:%s'", groupId, artifactId, version);
            return configure;
        }
        return null;
    }


    @Override
    public String toString() {
        return "Dependency{" +
                "version='" + version + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", type='" + type + '\'' +
                ", pom=" + pom +
                ", jar=" + jar +
                ", aar=" + aar +
                ", mavenCoordinate='" + mavenCoordinate + '\'' +
                ", configureList=" + configureList +
                '}';
    }

    public class Configure {
        String key;
        String type;
        String scope;
        String mavenCoordinate;
        String exclude;
        String groupId;
        String artifactId;
        String version;

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public String getKey() {
            return key;
        }

        public String getType() {
            return type == null ? "" : type;
        }

        public String getScope() {
            return scope == null ? "" : scope;
        }

        public String getExclude() {
            return exclude;
        }

        public String getVersion() {
            return version;
        }

        public String getMavenCoordinate() {
            return mavenCoordinate;
        }

        @Override
        public String toString() {
            return "Configure{" +
                    "key='" + key + '\'' +
                    ", type='" + type + '\'' +
                    ", scope='" + scope + '\'' +
                    ", mavenCoordinate='" + mavenCoordinate + '\'' +
                    '}';
        }
    }
}
