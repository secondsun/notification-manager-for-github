package net.saga.github.notifications.manager;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import net.saga.github.notifications.manager.controller.MainController;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyphLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import java.util.concurrent.Executors;
import net.saga.github.notifications.service.persistence.DerbyBootStrap;
import net.saga.github.notifications.service.persistence.HibernateModule;
import static javafx.application.Application.launch;
import net.saga.github.notifications.manager.service.auth.AuthModule;
import net.saga.github.notifications.manager.service.net.AccountModule;
import net.saga.github.notifications.manager.service.net.RealtimePushModule;

public class MainApp extends Application {

    public static final EventBus BUS = new AsyncEventBus(Executors.newCachedThreadPool());
    public static final HibernateModule HIBERNATE = new HibernateModule();
    public static final AuthModule AUTH = new AuthModule(BUS);
    public static final AccountModule ACCOUNT = new AccountModule(AUTH, BUS);
    public static final RealtimePushModule NOTIFICATIONS = new RealtimePushModule(AUTH, HIBERNATE, BUS);
    
    @FXMLViewFlowContext
    private ViewFlowContext flowContext;
    
    private DerbyBootStrap bootStrap;

    @Override
    public void start(Stage stage) throws Exception {

        new Thread(() -> {
            try {
                SVGGlyphLoader.loadGlyphsFont(MainApp.class.getResourceAsStream("/font/icomoon.svg"), "icomoon.svg");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();

        Flow flow = new Flow(MainController.class);
        
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        JFXDecorator decorator = new JFXDecorator(stage, container.getView());
        //decorator.setCustomMaximize(true);
        Scene scene = new Scene(decorator, 800, 800);
        scene.getStylesheets().add("/styles/rootPanel.css");
        scene.getStylesheets().add("/styles/hd.css");

        stage.setMinWidth(700);
        stage.setMinHeight(800);
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        this.HIBERNATE.close();
        this.bootStrap.close();
    }

    @Override
    public void init() throws Exception {
        super.init();
        this.bootStrap = new DerbyBootStrap();
        bootStrap.startUp();
        
        HIBERNATE.open();
        
    }
    
   
    

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
