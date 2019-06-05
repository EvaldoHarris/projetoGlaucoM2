package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class HelloPicking extends SimpleApplication {

    public static void main(String[] args) {
        HelloPicking app = new HelloPicking();
        app.start();
    }
    private Node shootables;
    private Node shootables2;
    private Node shootables3;
    private Node shootables4;
    private Node shootables5;
    private Geometry mark;
    boolean dragon = true;

    @Override
    public void simpleInitApp() {
        initCrossHairs(); // um "+" no meio da tela para ajudar a mirar
        initKeys();       // carregar mapeamentos de teclas personalizados
        initMark();       // uma esfera vermelha para marcar o hit

        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);
        shootables = new Node("Shootables");
        shootables2 = new Node("Shootables2");
        shootables3 = new Node("Shootables3");
        shootables4 = new Node("Shootables4");
        shootables5 = new Node("Shootables4");
        

        shootables.attachChild(makeCube("a Dragon", -20f, 0f, -100f));
        rootNode.attachChild(shootables);
        shootables2.attachChild(makeCube("the Sheriff", 0f, 1f, -100f));
        rootNode.attachChild(shootables2);
        shootables3.attachChild(makeCube("a tin can", 1f, -2f, -100f));
        rootNode.attachChild(shootables3);
        shootables4.attachChild(makeCube("the Deputy", 1f, 0f, -100f));
        rootNode.attachChild(shootables4);
        shootables.detachChild(makeFloor());
        shootables5.attachChild(makeCharacter());
       // rootNode.attachChild(shootables5);
        shootables2.detachChild(makeFloor());
        shootables3.detachChild(makeFloor());
        shootables4.detachChild(makeFloor());

    }

    void deleta(String hit) {
        System.out.println("deleta()");
        if (hit == "Oto-geom-1") {
            shootables5.detachChild(makeCharacter());
            rootNode.detachChild(shootables5);
        }
        if (hit == "a Dragon") {
            shootables.detachChild(makeCube("a Dragon", -2f, 0f, 1f));
            rootNode.detachChild(shootables);
        }
        if (hit == "the Sheriff") {
            shootables2.detachChild(makeCube("the Sheriff", 0f, 1f, -2f));
            rootNode.detachChild(shootables2);
        }
        if (hit == "a tin can") {
            shootables3.detachChild(makeCube("a tin can", 1f, -2f, 0f));
            rootNode.detachChild(shootables3);
        }
        if (hit == "the Deputy") {
            shootables4.detachChild(makeCube("the Deputy", 1f, 0f, -4f));
            rootNode.detachChild(shootables4);

        }
        //shootables.detachChild(makeFloor());
        //shootables.detachChild(makeCharacter());

    }

    /**
     * Declarando a ação "Disparar" e mapeando seus gatilhos.
     */
    private void initKeys() {
        inputManager.addMapping("Shoot",
                new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
        inputManager.addListener(actionListener, "Shoot");
    }
    /**
     * Definindo a ação "Atirar": Determine o que foi atingido e como responder.
     */
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Shoot") && !keyPressed) {
                // 1. Redefinir lista de resultados.
                CollisionResults results = new CollisionResults();
                // 2. Aponte o raio da localização do came para a direção da came.
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                // 3. Aponte o raio do bloqueio do came para a direção da câmera.
                shootables.collideWith(ray, results);
                shootables2.collideWith(ray, results);
                shootables3.collideWith(ray, results);
                shootables4.collideWith(ray, results);
                // 4. Imprimir os resultados
                System.out.println("----- Collisions? " + results.size() + "-----");
                for (int i = 0; i < results.size(); i++) {
                    // Para cada acerto, sabemos a distância, o ponto de impacto, o nome da geometria.
                    float dist = results.getCollision(i).getDistance();
                    Vector3f pt = results.getCollision(i).getContactPoint();
                    String hit = results.getCollision(i).getGeometry().getName();
                    System.out.println("---------------------------------------------------------" + i);
                    System.out.println("  ACERTOU " + hit + " at " + pt + ", " + dist + " wu away.");
                    deleta(hit);

                }
                // 5. Use os resultados (marcamos o objeto hit)
                if (results.size() > 0) {
                    //O ponto de colisão mais próximo é o que realmente foi atingido:
                    CollisionResult closest = results.getClosestCollision();
                    // Vamos interagir - marcamos o hit com um ponto vermelho.
                    mark.setLocalTranslation(closest.getContactPoint());
                    rootNode.attachChild(mark);

                    //
                } else {
                    // Sem hits? Em seguida, remova a marca vermelha.
                    rootNode.detachChild(mark);
                }
            }
        }
    };

    /**
     * Um objeto de cubo para a prática de destino
     */
    protected Geometry makeCube(String name, float x, float y, float z) {
        //criar aqui os inimigos
        Box box = new Box(1, 1, 1);
        Geometry cube = new Geometry(name, box);
        cube.setLocalTranslation(x, y, z);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        return cube;
    }

    /**
     * Um piso para mostrar que o "tiro" pode passar por vários objetos.
     */
    protected Geometry makeFloor() {
        Box box = new Box(40, .2f, 40);
        Geometry floor = new Geometry("the Floor", box);
        floor.setLocalTranslation(0, -4, -5);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        floor.setMaterial(mat1);
        return floor;
    }

    /**
     * Uma bola vermelha que marca o último ponto que foi "atingido" pelo
     * "tiro".
     */
    protected void initMark() {
        Sphere sphere = new Sphere(30, 30, 0.05f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }

    /**
     * Um sinal de mais centrado para ajudar o jogador a mirar.
     */
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    protected Spatial makeCharacter() {
        // load a character from jme3test-test-data
        Spatial golem = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        golem.scale(0.5f);
        golem.setLocalTranslation(-1.0f, -1.5f, -0.6f);

        //Devemos adicionar uma luz para tornar o modelo visível
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        golem.addLight(sun);
        return golem;
    }
}
