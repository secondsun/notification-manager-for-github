package net.saga.github.notifications.manager;

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
import static javafx.application.Application.launch;
import net.saga.github.notifications.manager.persistence.DerbyBootStrap;

public class MainApp extends Application {

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
        decorator.setCustomMaximize(true);
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
        this.bootStrap.close();
    }

    @Override
    public void init() throws Exception {
        super.init();
        this.bootStrap = new DerbyBootStrap();
        bootStrap.startUp();
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
