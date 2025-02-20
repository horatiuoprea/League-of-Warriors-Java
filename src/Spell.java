import java.io.Serializable;

public abstract class Spell implements Visitor {

    protected int spell_damage, spell_mana, power;
    protected String spell_type;

    public Spell(int spell_damage, int spell_mana, String spell_type, int power) {
        this.spell_damage = spell_damage;
        this.spell_mana = spell_mana;
        this.spell_type = spell_type;
        this.power = power;
    }

    public Spell() {
        this(0, 0, null, 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tipul abilitatii: " + spell_type + "\n   ");
        sb.append("Damage-ul abilitatii: " + spell_damage + "\n   ");
        sb.append("Mana necesara: " + spell_mana + "\n" );
        return sb.toString();
    }

    @Override
    public void visit(Entity entity) {

        if ((this instanceof Fire && entity.fire_imune) ||
                (this instanceof Ice && entity.ice_imune) ||
                (this instanceof Earth && entity.earth_imune)) {
            if (entity instanceof Character) {
                System.out.println("Player-ul este imun la acest spell");
            }
            if (entity instanceof Enemy) {
                System.out.println("Inamicul este imun la acest spell");
            }
            return;
        }

        entity.receiveDamage(this.spell_damage + entity.getDamage());
    }
}

class Ice extends Spell {

    public Ice(int damage, int mana, String type, int power) {
        super(damage, mana, type, power);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

class Fire extends Spell {

    public Fire(int damage, int mana, String type, int power) {
        super(damage, mana, type, power);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

class Earth extends Spell {

    public Earth(int damage, int mana, String type, int power) {
        super(damage, mana, type, power);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}