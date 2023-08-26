package render;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.ArrayList;

public class Drawing {

    private final int height = 1000;
    private final int width = 1000;
    private SubScene scene;
    private final Group group = new Group();
    private RenderableObject selectedObject;
    private ArrayList<RenderableObject> shapes = new ArrayList<RenderableObject>();
    private PerspectiveCamera camera = new PerspectiveCamera(true);
    private double startX = 0, startY = 0, startAngleX = 0, startAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0) , angleY = new SimpleDoubleProperty(0);
    private boolean rightClick = false;
    private ToggleButton rotateButton,moveButton,selectButton;
    private static Drawing instance;

    public static Drawing getInstance(){
        if(instance == null)
            new Drawing();
        return instance;
    }

    private Drawing(){
        instance = this;
    }

    private void setupCameraControl() {
        Rotate xAxisRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate yAxisRotate = new Rotate(0, Rotate.Y_AXIS);
        xAxisRotate.angleProperty().bind(angleX);
        yAxisRotate.angleProperty().bind(angleY);
        group.getTransforms().addAll(
                xAxisRotate,
                yAxisRotate
        );
        scene.setOnMousePressed(clicked -> {
            //Checks if the users last mouse click was the right one
            if(clicked.getButton() == MouseButton.SECONDARY) {
                //stores the starting point of drag
                startX = clicked.getSceneX();
                startY = clicked.getSceneY();
                //stores the angle of the camera before drag
                startAngleX = angleX.get();
                startAngleY = angleY.get();
                //sets it true that the user last mouse click was the right one
                rightClick = true;
            }else {
                rightClick = false;
            }
        });
        scene.setOnMouseDragged(dragged -> {
            //Checks if the users last mouse click was the right one
            if(rightClick) {
                //Sets the cameras new angle
                angleX.set(startAngleX - (startY - dragged.getSceneY()));
                angleY.set(startAngleY + startX - dragged.getSceneX());
            }
        });
    }

    public void createSphere(){
        Sphere3D sphere= new Sphere3D(50);
        shapes.add(sphere);
        group.getChildren().add(sphere);
        sphere.setOnMouseClicked(clicked -> {
            selectedObject =sphere;
            if(selectButton.isSelected())
                ConfigBox.generateBox();
        });
    }

    public Box3D createBox(double d,double h,double w){
        Box3D box= new Box3D(d,h,w);
        shapes.add(box);
        group.getChildren().add(box);
        selectedObject = box;
        box.setOnMouseClicked(clicked -> {
            if(clicked.getButton() == MouseButton.PRIMARY) {
                selectedObject = box;
                if(selectButton.isSelected())
                    ConfigBox.generateBox();
            }
        });
        return box;
    }

    public VBox generateButtons(){
        ButtonBar buttonBar = new ButtonBar();
        rotateButton = new ToggleButton("Rotate");
        rotateButton.setPrefSize(80,20);
        moveButton = new ToggleButton("Move");
        moveButton.setPrefSize(80,20);
        selectButton = new ToggleButton("Select");
        selectButton.setPrefSize(80,20);
        ToggleGroup toggleGroup = new ToggleGroup();
        rotateButton.setToggleGroup(toggleGroup);
        moveButton.setToggleGroup(toggleGroup);
        selectButton.setToggleGroup(toggleGroup);
        Button resetCameraButton = new Button("Reset Camera");
        resetCameraButton.setOnAction(clicked ->{
            angleX.set(0);
            angleY.set(0);
        });
        resetCameraButton.setPrefSize(110,20);
        ButtonBar.setButtonData(rotateButton, ButtonBar.ButtonData.APPLY);
        ButtonBar.setButtonData(moveButton, ButtonBar.ButtonData.APPLY);
        ButtonBar.setButtonData(selectButton, ButtonBar.ButtonData.APPLY);
        ButtonBar.setButtonData(resetCameraButton, ButtonBar.ButtonData.APPLY);
        buttonBar.getButtons().addAll(rotateButton,selectButton,moveButton,resetCameraButton);
        VBox vBox = new VBox(buttonBar);
        vBox.setStyle("-fx-background-color: GREY");
        return vBox;
    }

    public boolean rotateSelected(){
        return rotateButton.isSelected();
    }

    public boolean moveSelected(){
        return moveButton.isSelected();
    }

    public Scene generateScene(){
        camera.setTranslateZ(-400);
        camera.setNearClip(1);
        camera.setFarClip(10000);
        Box3D box1 = createBox(70,70,40);
        Box3D box2 = createBox(30,20,60);
        PhongMaterial cobble = new PhongMaterial();
        //cobble.setDiffuseMap(new Image(getClass().getResourceAsStream("cobble.png")));
        scene = new SubScene(group,width,height,true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);
        scene.setFill(Color.GREY);
        setupCameraControl();
        VBox vBox = generateButtons();
        vBox.getChildren().add(scene);
        vBox.setAlignment(Pos.TOP_LEFT);
        Scene mainScene = new Scene(vBox);
        mainScene.setFill(Color.GREY);
        return mainScene;
    }

    public void scroll(double movement){
        group.translateZProperty().set(group.getTranslateZ() + movement);
    }

    public void rotateX(double angle){
        selectedObject.setRotationX(angle);
    }

    public void rotateY(double angle){
        selectedObject.setRotationY(angle);
    }

    public void rotateZ(double angle){
        selectedObject.setRotationZ(angle);
    }

    public void setX(double offset){
        selectedObject.setX(offset);
    }

    public void setY(double offset){
        selectedObject.setY(offset);
    }

    public void setZ(double offset){
        selectedObject.setZ(offset);
    }

    public RenderableObject getSelectedObject() {
        return selectedObject;
    }
}