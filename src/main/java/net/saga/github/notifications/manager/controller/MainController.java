package net.saga.github.notifications.manager.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javax.annotation.PostConstruct;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;

@io.datafx.controller.ViewController(value = "/fxml/Main.fxml", title = "Notification Manager for GitHub")
public class MainController implements Initializable {

    @FXML
    private StackPane root;

    @FXMLViewFlowContext
    private ViewFlowContext context;

    private FlowHandler flowHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @PostConstruct
    public void init() throws FlowException {

        // create the inner flow and content
        context = new ViewFlowContext();
        // set the default controller 
        Flow innerFlow = new Flow(LoadingController.class);

        flowHandler = innerFlow.createHandler(context);

        context.register(
                "ContentFlowHandler", flowHandler);
        context.register(
                "ContentFlow", innerFlow);
        context.register(
                "ContentPane", root);
        
        root.getChildren()
                .setAll(flowHandler.start(new AnimatedFlowContainer()));

    //        drawer.setContent(flowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT)));
    //        context.register("ContentPane", drawer.getContent().get(0));
    //
    //        // side controller will add links to the content flow
    //        Flow sideMenuFlow = new Flow(SideMenuController.class);
    //        sideMenuFlowHandler = sideMenuFlow.createHandler(context);
    //        drawer.setSidePane(sideMenuFlowHandler.start(new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT)));
    }

}
