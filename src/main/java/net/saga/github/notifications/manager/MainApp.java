package net.saga.github.notifications.manager;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyphLoader;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.container.DefaultFlowContainer;
import org.datafx.controller.flow.context.FXMLViewFlowContext;
import org.datafx.controller.flow.context.ViewFlowContext;

public class MainApp extends Application {

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

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

        Flow flow = new Flow(FXMLController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        JFXDecorator decorator = new JFXDecorator(stage, container.getView());
        decorator.setCustomMaximize(true);
        Scene scene = new Scene(decorator, 800, 800);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setMinWidth(700);
        stage.setMinHeight(800);
        stage.setScene(scene);
        stage.show();

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
