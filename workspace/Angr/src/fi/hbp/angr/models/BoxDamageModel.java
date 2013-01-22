package fi.hbp.angr.models;


public class BoxDamageModel extends DamageModel {
    public BoxDamageModel() {
        resetHealth();
    }

    @Override
    public void hit(float force) {
        if (force > 1)
            this.health -= force / 100f;
    }

    @Override
    public int getPoints() {
        return 100;
    }

    @Override
    public boolean isEnemy() {
        return false;
    }
}
