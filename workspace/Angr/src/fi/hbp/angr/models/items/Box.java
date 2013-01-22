package fi.hbp.angr.models.items;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import fi.hbp.angr.AssetContainer;
import fi.hbp.angr.G;
import fi.hbp.angr.ItemDestruction;
import fi.hbp.angr.models.BoxDamageModel;
import fi.hbp.angr.models.DamageModel;
import fi.hbp.angr.models.Destructible;

public class Box extends Actor implements Destructible {
    private static final String MODEL_NAME = "box";
    private static final String TEXTURE_PATH = "data/" + MODEL_NAME + ".png";
    private ItemDestruction des;
    private Body body;
    private Vector2 modelOrigin;
    private Sprite sprite;
    private DamageModel damageModel = new BoxDamageModel();
    private BitmapFont font = new BitmapFont();

    public static void preload() {
        G.getAssetManager().load(TEXTURE_PATH, Texture.class);
    }

    public static void initAssets(AssetContainer as, BodyEditorLoader bel) {
        as.texture = G.getAssetManager().get(
                bel.getImagePath(MODEL_NAME),
                Texture.class);

        as.bd = new BodyDef();
        as.bd.type = BodyType.DynamicBody;
        as.bd.active = true;
        as.bd.position.set(0, 0);

        as.fd = new FixtureDef();
        as.fd.density = 3.0f;
        as.fd.friction = 0.3f;
        as.fd.restitution = 0.3f;

        //as.fd.filter.categoryBits = CollisionFilterMasks.GRENADE;
        //as.fd.filter.maskBits = CollisionFilterMasks.ENEMY | CollisionFilterMasks.WALL | CollisionFilterMasks.GRENADE;
    }

    public Box(World world, ItemDestruction des, BodyEditorLoader bel, AssetContainer as, float x, float y, float angle) {
        this.des = des;

        as.bd.position.set(new Vector2(x * G.WORLD_TO_BOX, y * G.WORLD_TO_BOX));
        body = world.createBody(as.bd);
        body.setUserData(this);
        sprite = new Sprite(as.texture);

        bel.attachFixture(body,
                          MODEL_NAME,
                          as.fd,
                          sprite.getWidth() * G.WORLD_TO_BOX);
        modelOrigin = bel.getOrigin(MODEL_NAME, sprite.getWidth()).cpy();
        sprite.setOrigin(modelOrigin.x, modelOrigin.y);
        sprite.setPosition(x, y);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));

        font.setScale(4);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        Vector2 pos = body.getPosition();
        sprite.setPosition(pos.x * G.BOX_TO_WORLD - modelOrigin.x,
                           pos.y * G.BOX_TO_WORLD - modelOrigin.y);
        sprite.setOrigin(modelOrigin.x, modelOrigin.y);
        sprite.setRotation((float)(body.getAngle() * MathUtils.radiansToDegrees));
        sprite.draw(batch, parentAlpha);

        /* Debug print healt status */
        font.draw(batch, this.getDatamageModel().toString(), pos.x * G.BOX_TO_WORLD - modelOrigin.x, pos.y * G.BOX_TO_WORLD - modelOrigin.y);

        /* Push this item to the stack of destroyed items if health < 0 */
        if (damageModel.getHealth() < 0)
            des.add(this);
    }

    @Override
    public DamageModel getDatamageModel() {
        return damageModel;
    }

    @Override
    public Body getBody() {
        return body;
    }
}