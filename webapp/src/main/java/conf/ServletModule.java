package conf;

import ninja.servlet.NinjaServletDispatcher;
import websocket.WSServlet;

public class ServletModule extends com.google.inject.servlet.ServletModule {

    @Override
    protected void configureServlets() {

        /*bind(WSServlet.class).asEagerSingleton();
        serve("/ws*//*").with(WSServlet.class);*/

        bind(NinjaServletDispatcher.class).asEagerSingleton();
        serve("/*").with(NinjaServletDispatcher.class);
    }
}
