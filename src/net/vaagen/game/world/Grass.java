package net.vaagen.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.view.WorldRenderer;

import java.util.List;
import java.util.Random;

/**
 * Created by Magnus on 2/8/2016.
 */
public class Grass {

    private static TextureRegion[] grassTextures = new TextureRegion[16];

    private int id;

    Vector2 position = new Vector2();

    Vector2 topGrassPosition = new Vector2();
    Vector2 grassVelocity = new Vector2();
    Vector2 grassAcceleration = new Vector2();

    private float[] vertex;

    private World world;
    private Random random;
    private ShaderProgram shader;
    private Mesh mesh;

    // TODO : Make sure the grass blocks talk to each other and share some acceleration / velocity. At least the direction of the wind..
    // But only connect to those with the same id / same type (not sure yet)
    public Grass(Vector2 pos, int id) {
        this.position = pos;
        this.id = id;
        this.random = new Random();
        this.vertex = new float[24];
        grassAcceleration.set(-0.002F * (new Random().nextFloat() - 0.5F), 0);

        setupShader();
        mesh = new Mesh(false, vertex.length, 0, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"), VertexAttribute.TexCoords(0));
    }

    public static void loadTextures() {
        TextureRegion blocks = new TextureRegion(new Texture("images/grass.png"));
        TextureRegion[][] splitBlocks = blocks.split(16, 16);
        for (int x = 0; x < splitBlocks.length; x++) {
            for (int y = 0; y < splitBlocks[0].length; y++) {
                if (x * splitBlocks[0].length + y < grassTextures.length)
                    grassTextures[x * splitBlocks[0].length + y] = splitBlocks[x][y];
            }
        }
    }

    public void render(SpriteBatch spriteBatch) {
        drawGrass(spriteBatch);
    }

    public void update() {
        grassVelocity.add(grassAcceleration);
        topGrassPosition.add(grassVelocity);

        float range = 2F;
        List<Grass> grassList = world.getGrassInRangeWithId(getPosition().x, getPosition().y, range, id);
        float totalAccelerationSway = 0;
        for (Grass grass : grassList) {
            float dx = getPosition().x - grass.getPosition().x;
            float dy = getPosition().y - grass.getPosition().y;

            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            totalAccelerationSway += grass.getGrassVelocity().x * (distance * distance / range / range);
        }

        grassAcceleration.add(totalAccelerationSway / 1000, 0);

        // Calculate the acceleration of the grass
        float maxGrassPoint = 0.08F + random.nextFloat() * 0.05F;
        float accelerationAtTurn = 0.0002F * (random.nextFloat() * 0.5F + 0.5F);
        float maxVelocity = 0.003F;
        float recoveryAcceleration = 0.000001F;

        if (topGrassPosition.x < -maxGrassPoint)
            grassAcceleration.set(accelerationAtTurn, 0);
        else if (topGrassPosition.x > maxGrassPoint)
            grassAcceleration.set(-accelerationAtTurn, 0);

        if (grassVelocity.x < -maxVelocity && grassAcceleration.x < 0)
            grassAcceleration.set(recoveryAcceleration, 0);
        if (grassVelocity.x > maxVelocity && grassAcceleration.x > 0)
            grassAcceleration.set(-recoveryAcceleration, 0);
    }

    private static int currentVertex = 0;
    private void drawGrass(SpriteBatch spriteBatch) {
        vertex[currentVertex++] = 0 + getPosition().x;
        vertex[currentVertex++] = 0 + getPosition().y;
        vertex[currentVertex++] = 0;
        vertex[currentVertex++] = Color.toFloatBits(255, 1, 1, 255);
        vertex[currentVertex++] = grassTextures[id].getU();
        vertex[currentVertex++] = grassTextures[id].getV2();

        vertex[currentVertex++] = 1 + getPosition().x;
        vertex[currentVertex++] = 0 + getPosition().y;
        vertex[currentVertex++] = 0;
        vertex[currentVertex++] = Color.toFloatBits(255, 0, 0, 255);
        vertex[currentVertex++] = grassTextures[id].getU2();
        vertex[currentVertex++] = grassTextures[id].getV2();

        vertex[currentVertex++] = 1 + topGrassPosition.x  + getPosition().x;
        vertex[currentVertex++] = 1 + getPosition().y;
        vertex[currentVertex++] = 0;
        vertex[currentVertex++] = Color.toFloatBits(255, 0, 0, 255);
        vertex[currentVertex++] = grassTextures[id].getU2();
        vertex[currentVertex++] = grassTextures[id].getV();

        vertex[currentVertex++] = 0 + topGrassPosition.x + getPosition().x;
        vertex[currentVertex++] = 1 + getPosition().y;
        vertex[currentVertex++] = 0;
        vertex[currentVertex++] = Color.toFloatBits(255, 0, 0, 255);
        vertex[currentVertex++] = grassTextures[id].getU();
        vertex[currentVertex++] = grassTextures[id].getV();

        // Flush at the end
        flush(spriteBatch);
    }

    private void flush(SpriteBatch spriteBatch) {
        if (currentVertex == 0)
            return;

        mesh.setVertices(vertex);
        grassTextures[0].getTexture().bind(); // Doesn't matter what region you use, as you bind the entire texture
        shader.begin();
        shader.setUniformMatrix("u_worldView", spriteBatch.getProjectionMatrix());
        shader.setUniformi("u_texture", 0);
        mesh.render(shader, GL20.GL_TRIANGLE_FAN, 0, currentVertex / 6);
        shader.end();

        currentVertex = 0;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public Vector2 getGrassVelocity() {
        return grassVelocity;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    private void setupShader() {
        String vertexShader = "attribute vec4 a_position;    \n" + "attribute vec4 a_color;\n" + "attribute vec2 a_texCoord0;\n"
                + "uniform mat4 u_worldView;\n" + "varying vec4 v_color;" + "varying vec2 v_texCoords;"
                + "void main()                  \n" + "{                            \n" + "   v_color = vec4(1, 1, 1, 1); \n"
                + "   v_texCoords = a_texCoord0; \n" + "   gl_Position =  u_worldView * a_position;  \n"
                + "}                            \n";
        String fragmentShader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n" + "varying vec4 v_color;\n"
                + "varying vec2 v_texCoords;\n" + "uniform sampler2D u_texture;\n" + "void main()                                  \n"
                + "{                                            \n" + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n"
                + "}";

        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false) {
            Gdx.app.log("ShaderTest", shader.getLog());
            Gdx.app.exit();
        }
    }

}
