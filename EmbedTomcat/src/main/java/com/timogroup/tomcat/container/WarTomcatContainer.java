package com.timogroup.tomcat.container;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;

import java.io.File;

/**
 * Created by TimoRD on 2016/7/6.
 */
public class WarTomcatContainer extends AbstractTomcatContainer {

    private File war;

    public WarTomcatContainer(File war) {
        this.war = war;
    }

    @Override
    protected Tomcat createTomcat() {
        String dir = String.format("%s.%d", getDisplayName(), getPort());
        File baseDir = new File(war.getParent(), dir);

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        tomcat.setPort(getPort());
        Connector connector = new Connector(Http11NioProtocol);
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        protocol.setMaxThreads(getMaxThreads());
        protocol.setMaxConnections(getMaxConnections());
        protocol.setConnectionTimeout(getConnectionTimeout());
        connector.setPort(getPort());
        connector.setURIEncoding(getEncoding());
        tomcat.setConnector(connector);
        tomcat.getService().addConnector(connector);

        return tomcat;
    }

    @Override
    protected void onStart(Tomcat tomcat) {
        if (!isWar(war.getAbsolutePath())) {
            throw new RuntimeException("not found war");
        }

        try {
            tomcat.addWebapp("/", war.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getParent(String file) {
        int index = file.lastIndexOf(File.separator);
        if (-1 == index) {
            throw new RuntimeException("index: -1");
        }

        String parent = file.substring(0, index);
        return parent;
    }

    private boolean isWar(String war) {
        if (war == null || war.length() == 0) {
            return false;
        }
        int index = war.lastIndexOf(".war");
        if (-1 == index) {
            return false;
        }

        return true;
    }

    @Override
    protected void showBanner() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("**********************************" + System.lineSeparator());
        buffer.append("*                                *" + System.lineSeparator());
        buffer.append("*       WarTomcatContainer       *" + System.lineSeparator());
        buffer.append("*                                *" + System.lineSeparator());
        buffer.append("**********************************" + System.lineSeparator());
        buffer.append(String.format("DisplayName: %s", getDisplayName()) + System.lineSeparator());
        buffer.append(String.format("Port: %d", getPort()) + System.lineSeparator());
        buffer.append(String.format("War: %s", war.getAbsolutePath()) + System.lineSeparator());
        System.out.println(buffer.toString());
    }
}
