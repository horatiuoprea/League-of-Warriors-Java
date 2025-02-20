import java.util.*;

interface Battle {
    public void receiveDamage(int damage);

    public int getDamage();
}

interface Element <T extends Entity> {
    void accept(Visitor<T> visitor);
}

interface Visitor <T extends Entity> {
    void visit(T entity);
}

public abstract class Entity implements Battle, Element {

    protected int current_health, max_health;
    protected int current_mana, max_mana;
    protected boolean fire_imune, ice_imune, earth_imune;
    protected ArrayList<Spell> spell_list;

    public Entity(int current_health, int max_health, int current_mana, int max_mana, boolean fire_imune,
            boolean ice_imune, boolean earth_imune, ArrayList<Spell> spell_list) {
        this.current_health = current_health;
        this.max_health = max_health;
        this.current_mana = current_mana;
        this.max_mana = max_mana;
        this.fire_imune = fire_imune;
        this.ice_imune = ice_imune;
        this.earth_imune = earth_imune;
        this.spell_list = spell_list;
        this.spell_list.add(new Ice(30, 20, "Ice", 1));
        this.spell_list.add(new Fire(40, 25, "Fire", 1));
        this.spell_list.add(new Earth(25, 15, "Earth", 1));
    }

    public Entity() {
        this(0, 0, 0, 0, false, false, false, null);
    }

    public void regenerate_health(int health) {
        if (current_health + health > max_health)
            current_health = max_health;
        else {
            current_health = current_health + health;
        }
    }

    public void regenerate_mana(int mana) {
        if (current_mana + mana > max_mana)
            current_mana = max_mana;
        else {
            current_mana = current_mana + mana;
        }
    }

    public void normal_attack() {
        Random random = new Random();
        current_health = Math.max(current_health - 10 - random.nextInt(15), 0);
    }

    public void use_ability(Spell spell, Entity entity) {

        if (current_mana < spell.spell_mana) {
            System.out.println("Nu ai suficienta mana pentru acest spell");
            entity.normal_attack();
            return;
        }

        if ((spell instanceof Fire && entity.fire_imune) ||
                (spell instanceof Ice && entity.ice_imune) ||
                (spell instanceof Earth && entity.earth_imune)) {
            if (entity instanceof Character) {
                System.out.println("Player-ul este imun la acest spell");
            }
            if (entity instanceof Enemy) {
                System.out.println("Inamicul este imun la acest spell");
            }
            return;
        }

        current_mana -= spell.spell_mana;
        entity.receiveDamage(spell.spell_damage + getDamage());
    }

}

abstract class Character extends Entity {

    protected String character_name, main_atribute;
    protected int character_exp;
    protected int character_level;
    protected int character_strength, character_charisma, character_dexterity;
    protected int kill_count;

    public Character(int current_health, int max_health, int current_mana, int max_mana, boolean fire_imune,
            boolean ice_imune, boolean earth_imune, ArrayList<Spell> spell_list, String character_name,
            int character_exp, int character_level, int character_strength, int character_charisma,
            int character_dexterity, int kill_count,  String main_atribute) {
        super(current_health, max_health, current_mana, max_mana, fire_imune, ice_imune, earth_imune, spell_list);
        this.character_name = character_name;
        this.character_exp = character_exp;
        this.character_level = character_level;
        this.character_strength = character_strength;
        this.character_charisma = character_charisma;
        this.character_dexterity = character_dexterity;
        this.main_atribute = main_atribute;
        this.kill_count = kill_count;
    }

    public Character() {
        this(0, 0, 0, 0, false, false, false, null, null, 0, 0, 0, 0, 0, 0, null);
    }

    public void receiveDamage(int damage) {
        if (main_atribute.equals("Strength")) {
            if (character_charisma + character_dexterity > character_strength) {
                current_health = Math.max((int) (current_health - damage / 2), 0);
            } else {
                current_health = Math.max(current_health - damage, 0);
            }
        }

        if (main_atribute.equals("Dexterity")) {
            if (character_charisma + character_strength > character_dexterity) {
                current_health = Math.max((int) (current_health - damage / 2), 0);
            } else {
                current_health = Math.max(current_health - damage, 0);
            }
        }

        if (main_atribute.equals("Charisma")) {
            if (character_strength + character_dexterity > character_charisma) {
                current_health = Math.max((int) (current_health - damage / 2), 0);
            } else {
                current_health = Math.max(current_health - damage, 0);
            }
        }
    }

    public int getDamage() {
        int base_damage = (character_charisma + character_dexterity + character_strength)/3;
        if (Math.random() < 0.5) {
            if (main_atribute.equals("Strength")) {
                return 2 * character_strength;
            } else if (main_atribute.equals("Dexterity")) {
                return 2 * character_dexterity;
            } else if (main_atribute.equals("Charisma")) {
                return 2 * character_charisma;
            }
        }
        return base_damage;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

class Warrior extends Character {

    public Warrior(String character_name, int character_exp, int character_level) {
        super(150, 150, 60, 60, true, false, false, new ArrayList<Spell>(), character_name, character_exp,
                character_level, 50, 10, 15, 0,"Strength");
    }

}

class Rogue extends Character {

    public Rogue(String character_name, int character_exp, int character_level) {
        super(75, 75, 70, 70, false, false, true, new ArrayList<Spell>(), character_name, character_exp,
                character_level, 25, 25, 45, 0,"Dexterity");
    }

}

class Mage extends Character {

    public Mage(String character_name, int character_exp, int character_level) {
        super(90, 90, 100, 100, false, true, false, new ArrayList<Spell>(), character_name, character_exp,
                character_level, 20, 35, 20, 0,"Charisma");
    }

}

class Enemy extends Entity {

    public Enemy(int current_health, int max_health, int current_mana, int max_mana, boolean fire_imune,
            boolean ice_imune, boolean earth_imune, ArrayList<Spell> spell_list) {
        super(current_health, max_health, current_mana, max_mana, fire_imune, ice_imune, earth_imune, spell_list);
    }

    public void receiveDamage(int damage) {
        if (Math.random() < 0.5) {
            damage /= 2;
        }
        current_health = Math.max(current_health - damage, 0);
    }

    public int getDamage() {
        return (int) (Math.random() * 30 + 10);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}